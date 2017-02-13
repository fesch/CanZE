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

import android.os.Bundle;
import android.widget.TextView;

import java.util.Locale;

import lu.fisch.canze.R;
import lu.fisch.canze.actors.Field;
import lu.fisch.canze.interfaces.DebugListener;
import lu.fisch.canze.interfaces.FieldListener;

// If you want to monitor changes, you must add a FieldListener to the fields.
// For the simple activity, the easiest way is to implement it in the actitviy itself.
public class ChargingHistActivity extends CanzeActivity implements FieldListener, DebugListener {

    public static final String SID_Preamble_KM                      = "7ec.6233d4."; // 240 - 24
    public static final String SID_Preamble_END                     = "7ec.6233d5."; //  96 -  8
    public static final String SID_Preamble_TYP                     = "7ec.6233d6."; //  96 -  8
    public static final String SID_Preamble_SOC                     = "7ec.6233d7."; // 168 - 16
    public static final String SID_Preamble_TMP                     = "7ec.6233d8."; //  96 -  8
    public static final String SID_Preamble_DUR                     = "7ec.6233d9."; // 168 - 16

    final String charging_HistEnd [] = MainActivity.getStringList(R.array.list_ChargingHistEnd);
    final String charging_HistTyp [] = MainActivity.getStringList(R.array.list_ChargingHistTyp);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_charginghist);
    }

    protected void initListeners() {
        String sid;
        MainActivity.getInstance().setDebugListener(this);
        for (int i = 0; i < 10; i++) {
            sid = SID_Preamble_KM  + (240 - i * 24);
            addField(sid);
            sid = SID_Preamble_END + ( 96 - i *  8);
            addField(sid);
            sid = SID_Preamble_TYP + ( 96 - i *  8);
            addField(sid);
            sid = SID_Preamble_SOC + (168 - i * 16);
            addField(sid);
            sid = SID_Preamble_TMP + ( 96 - i *  8);
            addField(sid);
            sid = SID_Preamble_DUR + (168 - i * 16);
            addField(sid);
        }
    }

    // This event is fired as soon as any registered field is set through its setValue() method
    // by the Message.onMessageCompleteEvent event.

    @Override
    public void onFieldUpdateEvent(final Field field) {
        // the update has to be done in a separate thread
        // otherwise the UI will not be repainted
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String sidPreamble = field.getSID().substring(0,11); // 7e4.2233D4.
                String startBit    = field.getSID().substring(11); // 240 = 0 etc
                double val         = field.getValue();
                TextView tv        = null;

                // get the text column, select the row through the bit position
                switch (sidPreamble) {
                    case SID_Preamble_KM:
                        tv = (TextView) findViewById(getResources().getIdentifier("textKM"  + ((264 - Integer.parseInt(startBit)) / 24), "id", getPackageName()));
                        break;
                    case SID_Preamble_END:
                        tv = (TextView) findViewById(getResources().getIdentifier("textEND" + ((104 - Integer.parseInt(startBit)) /  8), "id", getPackageName()));
                        if (tv != null) tv.setText(Double.isNaN(val) ? "" : charging_HistEnd[(int) field.getValue()]);
                        tv = null;
                        break;
                    case SID_Preamble_TYP:
                        tv = (TextView) findViewById(getResources().getIdentifier("textTYP" + ((104 - Integer.parseInt(startBit)) /  8), "id", getPackageName()));
                        if (tv != null) tv.setText(Double.isNaN(val) ? "" : charging_HistTyp[(int) field.getValue()]);
                        tv = null;
                        break;
                    case SID_Preamble_SOC:
                        tv = (TextView) findViewById(getResources().getIdentifier("textSOC" + ((184 - Integer.parseInt(startBit)) / 16), "id", getPackageName()));
                        break;
                    case SID_Preamble_TMP:
                        tv = (TextView) findViewById(getResources().getIdentifier("textTMP" + ((104 - Integer.parseInt(startBit)) /  8), "id", getPackageName()));
                        break;
                    case SID_Preamble_DUR:
                        tv = (TextView) findViewById(getResources().getIdentifier("textDUR" + ((184 - Integer.parseInt(startBit)) / 16), "id", getPackageName()));
                        break;
                }
                // set regular new content, all exeptions handled above
                if (tv != null) {
                    tv.setText(Double.isNaN(val) ? "" : String.format(Locale.getDefault(), "%.0f", val));
                }
            }
        });

    }
}