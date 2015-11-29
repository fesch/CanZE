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

import java.util.ArrayList;

import lu.fisch.canze.R;
import lu.fisch.canze.actors.Field;
import lu.fisch.canze.interfaces.FieldListener;

// If you want to monitor changes, you must add a FieldListener to the fields.
// For the simple activity, the easiest way is to implement it in the actitviy itself.
public class TyresActivity extends CanzeActivity implements FieldListener {

    public static final String SID_TyreSpdPresMisadaption       = "673.0";
    public static final String SID_TyreFLState                  = "673.11";
    public static final String SID_TyreFLPressure               = "673.40";
    public static final String SID_TyreFRState                  = "673.8";
    public static final String SID_TyreFRPressure               = "673.32";
    public static final String SID_TyreRLState                  = "673.5";
    public static final String SID_TyreRLPressure               = "673.24";
    public static final String SID_TyreRRState                  = "673.2";
    public static final String SID_TyreRRPressure               = "673.16";

    public static final String val_TyreSpdPresMisadaption  [] = {"OK", "Not OK"};
    public static final String val_TyreState               [] = {"OK", "No info", "-", "-", "-", "Flat", "Under infl."};
    public static final String val_Unavailable                = "Unavailable";

    private ArrayList<Field> subscribedFields;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tyres);

        initListeners();

    }

    private void addListener(String sid) {
        Field field;
        field = MainActivity.fields.getBySID(sid);
        if (field != null) {
            field.addListener(this);
            MainActivity.device.addActivityField(field);
            subscribedFields.add(field);
        }
        else
        {
            MainActivity.toast("sid " + sid + " does not exist in class Fields");
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // free up the listeners again
        for(Field field : subscribedFields)
        {
            field.removeListener(this);
        }
        subscribedFields.clear();
        MainActivity.device.clearFields();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // initialise the widgets
        initListeners();
    }

    private void initListeners() {

        subscribedFields = new ArrayList<>();

        addListener(SID_TyreSpdPresMisadaption);
        addListener(SID_TyreFLState);
        addListener(SID_TyreFLPressure);
        addListener(SID_TyreFRState);
        addListener(SID_TyreFRPressure);
        addListener(SID_TyreRLState);
        addListener(SID_TyreRLPressure);
        addListener(SID_TyreRRState);
        addListener(SID_TyreRRPressure);
    }

    // This is the event fired as soon as this the registered fields are
    // getting updated by the corresponding reader class.
    @Override
    public void onFieldUpdateEvent(final Field field) {
        // the update has to be done in a separate thread
        // otherwise the UI will not be repainted
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String fieldId = field.getSID();
                TextView tv = null;
                String value = "";
                int intValue = (int) field.getValue();
                int color = 0xffc0c0c0;

                // get the text field
                switch (fieldId) {

                    case SID_TyreSpdPresMisadaption:
                        tv = (TextView) findViewById(R.id.text_TyreSpdPresMisadaption);
                        color = 0xfff3f3f3; // default background holo_light
                        value = val_TyreSpdPresMisadaption[intValue];
                        break;
                    case SID_TyreFLState:
                        tv = (TextView) findViewById(R.id.text_TyreFLState);
                        if (intValue > 1) color = 0xffffc0c0;
                        value = val_TyreState[intValue];
                        break;
                    case SID_TyreFLPressure:
                        tv = (TextView) findViewById(R.id.text_TyreFLPressure);
                        value = intValue == 0xff ? val_Unavailable : "" + intValue;
                        break;
                    case SID_TyreFRState:
                        tv = (TextView) findViewById(R.id.text_TyreFRState);
                        if (intValue > 1) color = 0xffffc0c0;
                        value = val_TyreState[intValue];
                        break;
                    case SID_TyreFRPressure:
                        tv = (TextView) findViewById(R.id.text_TyreFRPressure);
                        value = intValue == 0xff ? val_Unavailable : "" + intValue;
                        break;
                    case SID_TyreRLState:
                        tv = (TextView) findViewById(R.id.text_TyreRLState);
                        if (intValue > 1) color = 0xffffc0c0;
                        value = val_TyreState[intValue];
                        break;
                    case SID_TyreRLPressure:
                        tv = (TextView) findViewById(R.id.text_TyreRLPressure);
                        value = intValue == 0xff ? val_Unavailable : "" + intValue;
                        break;
                    case SID_TyreRRState:
                        tv = (TextView) findViewById(R.id.text_TyreRRState);
                        if (intValue > 1) color = 0xffffc0c0;
                        value = val_TyreState[intValue];
                        break;
                    case SID_TyreRRPressure:
                        tv = (TextView) findViewById(R.id.text_TyreRRPressure);
                        value = intValue == 0xff ? val_Unavailable : "" + intValue;
                        break;
                }
                // set regular new content, all exeptions handled above
                if (tv != null) {
                    tv.setText(value);
                    tv.setBackgroundColor(color);
                }

                tv = (TextView) findViewById(R.id.textDebug);
                tv.setText(fieldId);
            }
        });

    }
}