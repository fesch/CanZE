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
import lu.fisch.canze.actors.Frames;
import lu.fisch.canze.interfaces.DebugListener;
import lu.fisch.canze.interfaces.FieldListener;
import lu.fisch.canze.activities.MainActivity;

// If you want to monitor changes, you must add a FieldListener to the fields.
// For the simple activity, the easiest way is to implement it in the actitviy itself.
public class BcbActivity extends CanzeActivity implements FieldListener, DebugListener {

    public static final String SID_TesterInit                   = "793.50c0.0";
    public static final String SID_TesterAwake                  = "793.7e01.0";

    public static final String SID_MainsCurrentType             = "793.625017.29";
    public static final String SID_Phase1currentRMS             = "793.622001.24";
    public static final String SID_Phase2CurrentRMS             = "793.62503a.24"; // Raw <= this seems to be instant DC coupled value
    public static final String SID_Phase3CurrentRMS             = "793.62503b.24";
    public static final String SID_PhaseVoltage1                = "793.62502c.24"; // Raw
    public static final String SID_PhaseVoltage2                = "793.62502d.24";
    public static final String SID_PhaseVoltage3                = "793.62502e.24";
    public static final String SID_InterPhaseVoltage12          = "793.62503f.24"; // Measured
    public static final String SID_InterPhaseVoltage23          = "793.625041.24";
    public static final String SID_InterPhaseVoltage31          = "793.625042.24";
    public static final String SID_MainsActivePower             = "793.62504a.24";
    public static final String SID_GroundResistance             = "793.625062.24";
    public static final String SID_SupervisorState              = "793.625063.24";
    public static final String SID_CompletionStatus             = "793.625064.24";

    final String mains_Current_Type [] = MainActivity.getStringList(R.array.list_MainsCurrentType);
    final String supervisor_State   [] = MainActivity.getStringList(R.array.list_SupervisorState);
    final String completion_Status  [] = MainActivity.getStringList(R.array.list_CompletionStatus);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bcb);
    }

    protected void initListeners() {
        MainActivity.getInstance().setDebugListener(this);
        MainActivity.device.injectRequest(MainActivity.fields.getBySID(SID_TesterInit).getFrame());
        addField(SID_TesterAwake, 1500);
        addField(SID_MainsCurrentType);
        addField(SID_Phase1currentRMS);
        addField(SID_Phase2CurrentRMS);
        addField(SID_Phase3CurrentRMS);
        addField(SID_PhaseVoltage1);
        addField(SID_PhaseVoltage2);
        addField(SID_PhaseVoltage3);
        addField(SID_InterPhaseVoltage12);
        addField(SID_InterPhaseVoltage23);
        addField(SID_InterPhaseVoltage31);
        addField(SID_MainsActivePower);
        addField(SID_GroundResistance);
        addField(SID_SupervisorState);
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
                int value;

                // get the text field
                switch (fieldId) {

                    case SID_MainsCurrentType:
                        tv = findViewById(R.id.textMainsCurrentType);
                        value = (int) field.getValue();
                        if (tv != null && mains_Current_Type != null && value >= 0 && value < mains_Current_Type.length)
                            tv.setText(mains_Current_Type[value]);
                        tv = null;
                        break;
                    case SID_Phase1currentRMS:
                        tv = findViewById(R.id.textPhase1CurrentRMS);
                        break;
                    case SID_Phase2CurrentRMS:
                        tv = findViewById(R.id.textPhase2CurrentRMS);
                        break;
                    case SID_Phase3CurrentRMS:
                        tv = findViewById(R.id.textPhase3CurrentRMS);
                        break;
                    case SID_PhaseVoltage1:
                        tv = findViewById(R.id.textPhaseVoltage1);
                        break;
                    case SID_PhaseVoltage2:
                        tv = findViewById(R.id.textPhaseVoltage2);
                        break;
                    case SID_PhaseVoltage3:
                        tv = findViewById(R.id.textPhaseVoltage3);
                        break;
                    case SID_InterPhaseVoltage12:
                        tv = findViewById(R.id.textInterPhaseVoltage12);
                        break;
                    case SID_InterPhaseVoltage23:
                        tv = findViewById(R.id.textInterPhaseVoltage23);
                        break;
                    case SID_InterPhaseVoltage31:
                        tv = findViewById(R.id.textInterPhaseVoltage31);
                        break;
                    case SID_MainsActivePower:
                        tv = findViewById(R.id.textMainsActivePower);
                        break;
                    case SID_GroundResistance:
                        tv = findViewById(R.id.textGroundResistance);
                        break;
                    case SID_SupervisorState:
                        tv = findViewById(R.id.textSupervisorState);
                        value = (int) field.getValue();
                        if (tv != null && supervisor_State != null && value >= 0 && value < supervisor_State.length)
                            tv.setText(supervisor_State[value]);
                        tv = null;
                        break;
                    case SID_CompletionStatus:
                        tv = findViewById(R.id.textCompletionStatus);
                        value = (int) field.getValue();
                        if (tv != null && completion_Status != null && value >= 0 && value < completion_Status.length)
                            tv.setText(completion_Status[value]);
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