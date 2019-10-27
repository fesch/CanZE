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
    private static final String SID_1 = "77e.623025.24"; // PEBTorque ==>>> total torque, positive or negative, on the motor axle
    private static final String SID_2 = "7ec.622247.24"; // Minimum effective torque that can be requested to the electrical motor (EM) ==> this is the blue bar, negative value
    //private static final String SID_2 = "7ec.622243.24"; // Final effective torque request to the electric motor (EM) ==> Same but only braking, becomes -2048 when not braking/coasting
    private static final String SID_3 = "7ec.622241.24"; // MSR torque setting sent to torque arbitration
    //private static final String SID_3 = "7ec.622246.24"; // Electrical motor (EM) maximum effective torque available ==> not usable
    //private static final String SID_4 = "7ec.622245.24"; // Limited electrical motor (EM) effective torque setpoint
    private static final String SID_4 = "7bc.624b7d.28"; // Total Hydraulic brake wheels torque request

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fieldtest);
    }

    protected void initListeners() {
        MainActivity.getInstance().setDebugListener(this);
        addField(SID_1);
        addField(SID_2);
        addField(SID_3);
        addField(SID_4);
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
                    case SID_1:
                        tv = findViewById(R.id.tv_test_1);
                        pb = findViewById(R.id.pb_test_1);
                        val *= MainActivity.reduction;
                        break;
                    case SID_2:
                        tv = findViewById(R.id.tv_test_2);
                        pb = findViewById(R.id.pb_test_2);
                        val *= MainActivity.reduction;
                        break;
                    case SID_3:
                        tv = findViewById(R.id.tv_test_3);
                        pb = findViewById(R.id.pb_test_3);
                        val *= MainActivity.reduction;
                        break;
                    case SID_4:
                        tv = findViewById(R.id.tv_test_4);
                        pb = findViewById(R.id.pb_test_4);
                        break;
                }
                // set regular new content, all exceptions handled above
                if (tv != null) {
                    tv.setText(field.getName() + ":" + String.format(Locale.getDefault(), "%.1f", val));
                }
                if (pb != null) {
                    pb.setProgress((int)(val));
                }


            }
        });

    }

}