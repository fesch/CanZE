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

package lu.fisch.canze.fragments;


import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import lu.fisch.canze.R;
import lu.fisch.canze.activities.AllDataActivity;
import lu.fisch.canze.activities.AuxBattTechActivity;
import lu.fisch.canze.activities.ChargingGraphActivity;
import lu.fisch.canze.activities.ChargingHistActivity;
import lu.fisch.canze.activities.ChargingTechActivity;
import lu.fisch.canze.activities.DtcActivity;
import lu.fisch.canze.activities.ElmTestActivity;
import lu.fisch.canze.activities.FirmwareActivity;
import lu.fisch.canze.activities.HeatmapBatcompActivity;
import lu.fisch.canze.activities.HeatmapCellvoltageActivity;
import lu.fisch.canze.activities.LeakCurrentsActivity;
import lu.fisch.canze.activities.MainActivity;
import lu.fisch.canze.activities.PredictionActivity;
import lu.fisch.canze.activities.RangeActivity;
import lu.fisch.canze.activities.TiresActivity;

/**
 * A simple {@link Fragment} subclass.
 */
public class TechnicalFragment extends Fragment {


    public TechnicalFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_technical, container, false);

        activateButton(view, R.id.buttonChargingTech, ChargingTechActivity.class);
        activateButton(view, R.id.buttonDtc, DtcActivity.class);

        activateButton(view, R.id.buttonChargingGraphs, ChargingGraphActivity.class);
        activateButton(view, R.id.buttonFirmware, FirmwareActivity.class);

        activateButton(view, R.id.buttonChargingPrediction, PredictionActivity.class);
        activateButton(view, R.id.buttonElmTest, ElmTestActivity.class);

        activateButton(view, R.id.buttonChargingHistory, ChargingHistActivity.class);
        activateButton(view, R.id.buttonAuxBatt, AuxBattTechActivity.class);

        activateButton(view, R.id.buttonLeakCurrents, LeakCurrentsActivity.class);
        activateButton(view, R.id.buttonTires, TiresActivity.class);

        activateButton(view, R.id.buttonHeatmapCellvoltage, HeatmapCellvoltageActivity.class);
        activateButton(view, R.id.buttonHeatmapBatcomp, HeatmapBatcompActivity.class);

        activateButton(view, R.id.buttonRange, RangeActivity.class);
        activateButton(view, R.id.buttonAllData, AllDataActivity.class);

        return view;
    }

    private void activateButton(View view, int buttonId, final Class<?> activityClass) {
        Button button = view.findViewById(buttonId);
        if (MainActivity.isPh2() && buttonId == R.id.buttonTires) {
            button.setVisibility(View.INVISIBLE);
            button.setEnabled(false);
        } else {
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!MainActivity.isSafe()) return;
                    if (MainActivity.device == null) {
                        MainActivity.toast(MainActivity.TOAST_NONE, R.string.toast_AdjustSettings);
                        return;
                    }
                    MainActivity.getInstance().leaveBluetoothOn = true;
                    Intent intent = new Intent(MainActivity.getInstance(), activityClass);
                    TechnicalFragment.this.startActivityForResult(intent, MainActivity.LEAVE_BLUETOOTH_ON);
                }
            });
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        MainActivity.getInstance().onActivityResult(requestCode, resultCode, data);
    }
}
