/*
    CanZE
    Take a closer look at your ZE car

    Copyright (C) 2015 - The CanZE Team
    http://canze.fisch.lu

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or any
    later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

package lu.fisch.canze.activities;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import lu.fisch.canze.R;
import lu.fisch.canze.actors.Ecu;
import lu.fisch.canze.actors.Ecus;
import lu.fisch.canze.actors.Field;
import lu.fisch.canze.actors.Frames;
import lu.fisch.canze.actors.Message;
import lu.fisch.canze.actors.StoppableThread;
import lu.fisch.canze.interfaces.DebugListener;
import lu.fisch.canze.interfaces.FieldListener;

// Jeroen

public class FirmwareActivity extends CanzeActivity implements FieldListener, DebugListener {

    private StoppableThread queryThread;

    @SuppressLint("StringFormatMatches")

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_firmware);

        for (Ecu ecu : Ecus.getInstance().getAllEcus()) {
            // ensure we are only selecting true (as in physical boxes) and reachable (as in, i.e. skipping R-LINK) ECU's
            if (ecu.getFromId() > 0 && ecu.getFromId() < 0x800) {
                TextView tv;
                tv = (TextView) findViewById(getResources().getIdentifier("lEcu" + Integer.toHexString (ecu.getFromId()).toLowerCase(), "id", getPackageName()));
                if (tv != null) {
                    final Ecu thisEcu = ecu;
                    tv.setText(ecu.getMnemonic() + " (" + ecu.getName() + ")");
                    tv.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            showDetails(thisEcu);
                        }
                    });
                } else {
                    MainActivity.toast(getString(R.string.format_NoView), "lEcu", Integer.toHexString (ecu.getFromId()).toLowerCase());
                }
            }
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                if (MainActivity.device != null) {
                    // stop the poller thread
                    MainActivity.device.stopAndJoin();
                }
            }
        }).start();
    }


    void showDetails(final Ecu ecu) {

        // try to stop previous thread
        if(queryThread!=null) {
            if (queryThread.isAlive()) {
                queryThread.tryToStop();
                try {
                    queryThread.join();
                } catch (Exception e) {
                    MainActivity.debug(e.getMessage());
                }
            }
        }

        queryThread = new StoppableThread(new Runnable() {
            @Override
            public void run() {

                // query the Frame
                Message message = MainActivity.device.requestFrame(Frames.getInstance().getById(ecu.getFromId(), "6180")); //  field.getFrame());
                if (message.isError()) {
                    MainActivity.getInstance().dropDebugMessage(message.getError());
                    return;
                }

                String backRes = message.getData();
                // check the response
                if (!backRes.startsWith("59")) {
                    MainActivity.getInstance().dropDebugMessage(getString(R.string.message_UnexpectedResult) + backRes + "]");
                    return;
                }



                final Context context = FirmwareActivity.this;
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                LayoutInflater inflater = getLayoutInflater();
                // we allow this SuppressLint as this is a pop up Dialog
                @SuppressLint("InflateParams")
                final View softwareView = inflater.inflate(R.layout.alert_software, null);

                // set dialog message
                alertDialogBuilder
                        .setView(softwareView)
                        .setTitle(R.string.prompt_Distance)
                        .setMessage(getString(R.string.title_activity_firmware))
                        .setCancelable(true);

                setValueInAlert(R.id.AlertDiagVersion, ecu);
                setValueInAlert(R.id.AlertSupplier, ecu);
                setValueInAlert(R.id.AlertSoft, ecu);
                setValueInAlert(R.id.AlertVersion, ecu);

                // create alert dialog
                AlertDialog alertDialog = alertDialogBuilder.create();

                //InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                ///imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);

                // show it
                alertDialog.show();

            }
        });




    }

    void setValueInAlert (int id, Ecu ecu) {
        TextView tv = (TextView) findViewById(id);
        tv.setText("TEST1!");
    }

    // UI elements
    @Override
    protected void onDestroy() {

        // stop the query thread if still running
        if(queryThread!=null)
            if(queryThread.isAlive()) {
                queryThread.tryToStop();
                try {
                    queryThread.join();
                }
                catch(Exception e)
                {
                    MainActivity.debug(e.getMessage());
                }
            }

        // restart the poller
        if (MainActivity.device != null) {
            MainActivity.device.initConnection();
            // register application wide fields
            MainActivity.getInstance().registerApplicationFields();
        }

        super.onDestroy();
    }


    protected void initListeners() {}

    @Override
    public void onFieldUpdateEvent(final Field field) {}
}
