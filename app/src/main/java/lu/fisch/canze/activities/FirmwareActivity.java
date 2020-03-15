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

        int index = 0;
        for (Ecu ecu : Ecus.getInstance().getAllEcus()) {
            // ensure we are only selecting true (as in physical boxes) and reachable (as in, i.e. skipping R-LINK) ECU's
            if (ecu.getFromId() > 0 && (ecu.getFromId() < 0x800 || ecu.getFromId() >= 0x900)) {
                TextView tv;
                tv = findViewById(getResources().getIdentifier("lEcu" + index, "id", getPackageName()));
                if (tv != null) {
                    final Ecu thisEcu = ecu;
                    tv.setText(ecu.getMnemonic() + " (" + ecu.getName() + ")");
                    tv.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            showSelected (v); // clear out previous values an the selection bar, and set the new bar
                            showDetails(thisEcu); // get the new values and populate
                        }
                    });
                    index++;
                } else {
                    MainActivity.toast(MainActivity.TOAST_NONE, MainActivity.getStringSingle(R.string.format_NoView), "lEcu", Integer.toString (index));
                }
            }
        }

        TextView textView = findViewById(R.id.link);
        textView.setText(Html.fromHtml(MainActivity.getStringSingle(R.string.help_Ecus)));
        textView.setMovementMethod(LinkMovementMethod.getInstance());

        setDoRestartQueueOnResume(false);
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (MainActivity.device != null) {
                    // stop the poller thread
                    // note tht for the ZE50, the gateway is now not triggered
                    MainActivity.device.stopAndJoin();
                }
            }
        }).start();
    }

    private void showSelected (View v) {
        View tv;
        int bgColor = 0xfff3f3f3;
        TypedValue a = new TypedValue();
        getTheme().resolveAttribute(android.R.attr.windowBackground, a, true);
        if (a.type >= TypedValue.TYPE_FIRST_COLOR_INT && a.type <= TypedValue.TYPE_LAST_COLOR_INT) {
            bgColor = a.data;
        }

        // deselect bg color ecu list
        for (int index = 0; (tv = findViewById(getResources().getIdentifier("lEcu" + index, "id", getPackageName())))!= null ; index++) {
            tv.setBackgroundColor(bgColor);
        }
/*
        for (Ecu ecu : Ecus.getInstance().getAllEcus()) {
            if (ecu.getFromId() > 0 && ecu.getFromId() < 0x800) {
                tv = findViewById(getResources().getIdentifier("lEcu" + Integer.toHexString(ecu.getFromId()).toLowerCase(), "id", getPackageName()));
                if (tv != null) {
                    tv.setBackgroundColor(bgColor);
                }
            }
        }
 */
        // set bg selected ecu
        v.setBackgroundColor(0xff808080); // selected color

        // clear out old values
        setSoftwareValue(R.id.textDiagVersion, null, "");
        setSoftwareValue(R.id.textSupplier, null, "");
        setSoftwareValue(R.id.textSoft, null, "");
        setSoftwareValue(R.id.textVersion, null, "");
    }


    private void showDetails(final Ecu ecu) {

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

        setSoftwareValue(R.id.textDiagVersion, null, null);
        setSoftwareValue(R.id.textSupplier, null, null);
        setSoftwareValue(R.id.textSoft, null, null);
        setSoftwareValue(R.id.textVersion, null, null);

        queryThread = new StoppableThread(new Runnable() {
            @Override
            public void run() {

                Frame frame;

                if (MainActivity.isZOEZE50()) {
                    frame = queryFrame(0x18daf1d2, "5003"); // open the gateway, as the poller is stopped
                    if (frame != null) {
                        frame = queryFrame(ecu.getFromId(), ecu.getStartDiag()); // open the ecu, as the poller is stopped (now evc only)
                    }
                }

                // query the Frame
                frame = queryFrame(ecu.getFromId(), "6180");
                if (frame == null) return;
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

    private Frame queryFrame (int fromId, String responseId) {
        Frame frame = Frames.getInstance().getById(fromId, responseId);
        if (frame == null) {
            MainActivity.getInstance().dropDebugMessage("Frame for this ECU not found");
            return null;
        }
        MainActivity.getInstance().dropDebugMessage(frame.getFromIdHex() + "." + frame.getResponseId());
        Message message = MainActivity.device.requestFrame(frame); //  field.getFrame());
        if (message.isError()) {
            MainActivity.getInstance().dropDebugMessage(message.getError());
            return null;
        }
        message.onMessageCompleteEvent(); // set the value of all fields in the frame of this message
        return frame;
    };


    private void setSoftwareValue(final int id, final Field field, final String label) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                TextView tv = findViewById(id);
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

    protected void initListeners () {
        MainActivity.getInstance().setDebugListener(this);
    }
    @Override
    public void onFieldUpdateEvent(final Field field) {}
}
