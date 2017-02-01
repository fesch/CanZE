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
import lu.fisch.canze.activities.MainActivity;

// If you want to monitor changes, you must add a FieldListener to the fields.
// For the simple activity, the easiest way is to implement it in the actitviy itself.
public class BcbActivity extends CanzeActivity implements FieldListener, DebugListener {

    public static final String SID_Phase1currentRMS             = "793.222001.24";
    public static final String SID_MainsCurrentType             = "793.225017.29";
    public static final String SID_PhaseCurrent1                = "793.225029.24";
    public static final String SID_PhaseCurrent2                = "793.22502A.24";
    public static final String SID_PhaseCurrent3                = "793.22502B.25";
    public static final String SID_PhaseVoltage1                = "793.22502C.24";
    public static final String SID_PhaseVoltage2                = "793.22502D.24";
    public static final String SID_PhaseVoltage3                = "793.22502E.24";
    public static final String SID_InterPhaseVoltage12          = "793.225012.24";
    public static final String SID_InterPhaseVoltage23          = "793.225013.24";
    public static final String SID_InterPhaseVoltage31          = "793.225014.24";
    public static final String SID_MainsActivePower             = "793.22504a.29";
    public static final String SID_GroundResistance             = "793.225062.29";
    public static final String SID_CompletionStatus             = "793.225064.29";

    final String mains_Current_Type [] = MainActivity.getInstance().getStringList(R.array.list_MainsCurrentType);
    final String completion_Status  [] = MainActivity.getInstance().getStringList(R.array.list_CompletionStatus);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bcb);
    }

    protected void initListeners() {
        MainActivity.getInstance().setDebugListener(this);
        addField(SID_Phase1currentRMS);
        addField(SID_MainsCurrentType);
        addField(SID_PhaseCurrent1);
        addField(SID_PhaseCurrent2);
        addField(SID_PhaseCurrent3);
        addField(SID_PhaseVoltage1);
        addField(SID_PhaseVoltage2);
        addField(SID_PhaseVoltage3);
        addField(SID_InterPhaseVoltage12);
        addField(SID_InterPhaseVoltage23);
        addField(SID_InterPhaseVoltage31);
        addField(SID_MainsActivePower);
        addField(SID_GroundResistance);
        addField(SID_CompletionStatus);
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

                // get the text field
                switch (fieldId) {

                    case SID_Phase1currentRMS:
                        tv = (TextView) findViewById(R.id.textPhase1currentRMS);
                        break;
                    case SID_MainsCurrentType:
                        tv = (TextView) findViewById(R.id.textMainsCurrentType);
                        tv.setText(mains_Current_Type[(int) field.getValue()]);
                        tv = null;
                        break;
                    case SID_PhaseCurrent1:
                        tv = (TextView) findViewById(R.id.textPhaseCurrent1);
                        break;
                    case SID_PhaseCurrent2:
                        tv = (TextView) findViewById(R.id.textPhaseCurrent2);
                        break;
                    case SID_PhaseCurrent3:
                        tv = (TextView) findViewById(R.id.textPhaseCurrent3);
                        break;
                    case SID_PhaseVoltage1:
                        tv = (TextView) findViewById(R.id.textPhaseVoltage1);
                        break;
                    case SID_PhaseVoltage2:
                        tv = (TextView) findViewById(R.id.textPhaseVoltage2);
                        break;
                    case SID_PhaseVoltage3:
                        tv = (TextView) findViewById(R.id.textPhaseVoltage3);
                        break;
                    case SID_InterPhaseVoltage12:
                        tv = (TextView) findViewById(R.id.textInterPhaseVoltage12);
                        break;
                    case SID_InterPhaseVoltage23:
                        tv = (TextView) findViewById(R.id.textInterPhaseVoltage23);
                        break;
                    case SID_InterPhaseVoltage31:
                        tv = (TextView) findViewById(R.id.textInterPhaseVoltage31);
                        break;
                    case SID_MainsActivePower:
                        tv = (TextView) findViewById(R.id.textMainsActivePower);
                        break;
                    case SID_GroundResistance:
                        tv = (TextView) findViewById(R.id.textGroundResistance);
                        break;
                    case SID_CompletionStatus:
                        tv = (TextView) findViewById(R.id.textCompletionStatus);
                        tv.setText(completion_Status[(int) field.getValue()]);
                        tv = null;
                        break;
                }
                // set regular new content, all exeptions handled above
                if (tv != null) {
                    tv.setText(String.format(Locale.getDefault(), "%.1f", field.getValue()));
                }
            }
        });

    }
}