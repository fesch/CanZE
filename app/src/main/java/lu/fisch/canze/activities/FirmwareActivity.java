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
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;

import java.util.Locale;

import lu.fisch.canze.R;
import lu.fisch.canze.actors.Ecu;
import lu.fisch.canze.actors.Ecus;
import lu.fisch.canze.actors.Field;
import lu.fisch.canze.actors.Frame;
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
                            showSelected (v);
                            showDetails(thisEcu);
                        }
                    });
                } else {
                    MainActivity.toast(MainActivity.getStringSingle(R.string.format_NoView), "lEcu", Integer.toHexString (ecu.getFromId()).toLowerCase());
                }
            }
        }

        TextView textView = (TextView) findViewById(R.id.link);
        textView.setText(Html.fromHtml(MainActivity.getStringSingle(R.string.help_Ecus)));
        textView.setMovementMethod(LinkMovementMethod.getInstance());

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

    void showSelected (View v) {
        View tv;
        int bgColor = 0xfff3f3f3;
        TypedValue a = new TypedValue();
        getTheme().resolveAttribute(android.R.attr.windowBackground, a, true);
        if (a.type >= TypedValue.TYPE_FIRST_COLOR_INT && a.type <= TypedValue.TYPE_LAST_COLOR_INT) {
            bgColor = a.data;
        }
        for (Ecu ecu : Ecus.getInstance().getAllEcus()) {
            if (ecu.getFromId() > 0 && ecu.getFromId() < 0x800) {
                tv = findViewById(getResources().getIdentifier("lEcu" + Integer.toHexString(ecu.getFromId()).toLowerCase(), "id", getPackageName()));
                if (tv != null) {
                    tv.setBackgroundColor(bgColor);
                }
            }
        }
        v.setBackgroundColor(0xff808080); // selected color
        setSoftwareValue(R.id.textDiagVersion, null, "");
        setSoftwareValue(R.id.textSupplier, null, "");
        setSoftwareValue(R.id.textSoft, null, "");
        setSoftwareValue(R.id.textVersion, null, "");
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
                Frame frame = Frames.getInstance().getById(ecu.getFromId(), "6180");
                Message message = MainActivity.device.requestFrame(frame); //  field.getFrame());
                if (message.isError()) {
                    MainActivity.getInstance().dropDebugMessage(message.getError());
                    return;
                }

                message.onMessageCompleteEvent(); // set the value of all fields in the frame of this message
                for (Field field : frame.getAllFields()) {
                    switch (field.getFrom()) {
                        case 56:
                            setSoftwareValue(R.id.textDiagVersion, field, "DiagVersion: ");
                            break;
                        case 64:
                            setSoftwareValue(R.id.textSupplier, field, "Supplier: ");
                            break;
                        case 128:
                            setSoftwareValue(R.id.textSoft, field, "Soft: ");
                            break;
                        case 144:
                            setSoftwareValue(R.id.textVersion, field, "Version: ");
                            break;
                    }
                }

            }
        });
        queryThread.start();
    }

    void setSoftwareValue(final int id, final Field field, final String label) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                TextView tv = (TextView) findViewById(id);
                if (tv != null) {
                    if (field == null) {
                        tv.setText("");
                    } else if (field.isString()) {
                        tv.setText(label + field.getStringValue());
                    } else if ((field.getTo() - field.getFrom()) < 8) {
                        tv.setText(label + String.format(Locale.getDefault(), "%02X", (int)field.getValue()));
                    } else {
                        tv.setText(label + String.format(Locale.getDefault(), "%04X", (int)field.getValue()));
                    }
                }
            }
        });
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
