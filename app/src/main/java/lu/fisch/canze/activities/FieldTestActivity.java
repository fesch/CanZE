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
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.Locale;

import lu.fisch.canze.R;
import lu.fisch.canze.actors.Field;
import lu.fisch.canze.interfaces.DebugListener;
import lu.fisch.canze.interfaces.FieldListener;

public class FieldTestActivity extends CanzeActivity implements FieldListener, DebugListener {


    // ISO-TP data
    private static final String SID_PEB_ElecTorqueRequest = "77e.623024.24";
    private static final String SID_PEB_Torque = "77e.623025.24";
    private static final String SID_PEB_ElecMachineMaxMotorTorque = "77e.623026.24";
    private static final String SID_PEB_ElecMachineMaxGenTorque = "77e.623027.24";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fieldtest);
    }

    protected void initListeners() {
        MainActivity.getInstance().setDebugListener(this);
        addField(SID_PEB_ElecTorqueRequest);
        addField(SID_PEB_Torque);
        addField(SID_PEB_ElecMachineMaxMotorTorque);
        addField(SID_PEB_ElecMachineMaxGenTorque);
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
                double val = field.getValue();
                TextView tv = null;
                ProgressBar pb = null;

                // get the text field
                switch (fieldId) {
                    case SID_PEB_ElecTorqueRequest:
                        tv = findViewById(R.id.tv_test_ElecTorqueRequest);
                        pb = findViewById(R.id.pb_test_ElecTorqueRequest);
                        break;
                    case SID_PEB_Torque:
                        tv = findViewById(R.id.tv_test_Torque);
                        pb = findViewById(R.id.pb_test_Torque);
                        break;
                    case SID_PEB_ElecMachineMaxMotorTorque:
                        tv = findViewById(R.id.tv_test_ElecMachineMaxMotorTorque);
                        pb = findViewById(R.id.pb_test_ElecMachineMaxMotorTorque);
                        break;
                    case SID_PEB_ElecMachineMaxGenTorque:
                        tv = findViewById(R.id.tv_test_ElecMachineMaxGenTorque);
                        pb = findViewById(R.id.pb_test_ElecMachineMaxGenTorque);
                        break;
                }
                // set regular new content, all exceptions handled above
                if (tv != null) {
                    tv.setText(field.getName() + ":" + String.format(Locale.getDefault(), "%.1f", val));
                }
                if (pb != null) {
                    pb.setProgress((int)(val / MainActivity.reduction));
                }


            }
        });

    }

}