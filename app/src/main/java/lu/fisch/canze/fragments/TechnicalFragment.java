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
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import lu.fisch.canze.R;
import lu.fisch.canze.activities.ChargingGraphActivity;
import lu.fisch.canze.activities.ChargingTechActivity;
import lu.fisch.canze.activities.ClimaTechActivity;
import lu.fisch.canze.activities.DtcActivity;
import lu.fisch.canze.activities.ElmTestActivity;
import lu.fisch.canze.activities.FirmwareActivity;
import lu.fisch.canze.activities.MainActivity;
import lu.fisch.canze.activities.PredictionActivity;
import lu.fisch.canze.activities.RangeActivity;

/**
 * A simple {@link Fragment} subclass.
 */
public class TechnicalFragment extends Fragment {


    public TechnicalFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_technical, container, false);

        Button button;

        button = (Button) view.findViewById(R.id.buttonChargingTech);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!MainActivity.isSafe()) return;
                if(MainActivity.device==null) {MainActivity.toast("You first need to adjust the settings ..."); return;}
                MainActivity.getInstance().leaveBluetoothOn=true;
                Intent intent = new Intent(MainActivity.getInstance(), ChargingTechActivity.class);
                TechnicalFragment.this.startActivityForResult(intent, MainActivity.LEAVE_BLUETOOTH_ON);
            }
        });

        button = (Button) view.findViewById(R.id.buttonChargingGraphs);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!MainActivity.isSafe()) return;
                if(MainActivity.device==null) {MainActivity.toast("You first need to adjust the settings ..."); return;}
                MainActivity.getInstance().leaveBluetoothOn=true;
                Intent intent = new Intent(MainActivity.getInstance(), ChargingGraphActivity.class);
                TechnicalFragment.this.startActivityForResult(intent, MainActivity.LEAVE_BLUETOOTH_ON);
            }
        });

        button = (Button) view.findViewById(R.id.buttonChargingPrediction);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!MainActivity.isSafe()) return;
                if(MainActivity.device==null) {MainActivity.toast("You first need to adjust the settings ..."); return;}
                MainActivity.getInstance().leaveBluetoothOn=true;
                Intent intent = new Intent(MainActivity.getInstance(), PredictionActivity.class);
                TechnicalFragment.this.startActivityForResult(intent, MainActivity.LEAVE_BLUETOOTH_ON);
            }
        });

        button = (Button) view.findViewById(R.id.buttonDtc);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!MainActivity.isSafe()) return;
                if(MainActivity.device==null) {MainActivity.toast("You first need to adjust the settings ..."); return;}
                MainActivity.getInstance().leaveBluetoothOn=true;
                Intent intent = new Intent(MainActivity.getInstance(), DtcActivity.class);
                TechnicalFragment.this.startActivityForResult(intent, MainActivity.LEAVE_BLUETOOTH_ON);
            }
        });

        button = (Button) view.findViewById(R.id.buttonFirmware);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!MainActivity.isSafe()) return;
                if(MainActivity.device==null) {MainActivity.toast("You first need to adjust the settings ..."); return;}
                MainActivity.getInstance().leaveBluetoothOn=true;
                Intent intent = new Intent(MainActivity.getInstance(), FirmwareActivity.class);
                TechnicalFragment.this.startActivityForResult(intent,MainActivity.LEAVE_BLUETOOTH_ON);
            }
        });

        button = (Button) view.findViewById(R.id.buttonElmTest);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!MainActivity.isSafe()) return;
                if(MainActivity.device==null) {MainActivity.toast("You first need to adjust the settings ..."); return;}
                MainActivity.getInstance().leaveBluetoothOn=true;
                Intent intent = new Intent(MainActivity.getInstance(), ElmTestActivity.class);
                TechnicalFragment.this.startActivityForResult(intent,MainActivity.LEAVE_BLUETOOTH_ON);
            }
        });

        button = (Button) view.findViewById(R.id.buttonRange);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!MainActivity.isSafe()) return;
                if(MainActivity.device==null) {MainActivity.toast("You first need to adjust the settings ..."); return;}
                MainActivity.getInstance().leaveBluetoothOn=true;
                Intent intent = new Intent(MainActivity.getInstance(), RangeActivity.class);
                TechnicalFragment.this.startActivityForResult(intent,MainActivity.LEAVE_BLUETOOTH_ON);
            }
        });

        button = (Button) view.findViewById(R.id.buttonClimaTech);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!MainActivity.isSafe()) return;
                if(MainActivity.device==null) {MainActivity.toast("You first need to adjust the settings ..."); return;}
                MainActivity.getInstance().leaveBluetoothOn=true;
                Intent intent = new Intent(MainActivity.getInstance(), ClimaTechActivity.class);
                TechnicalFragment.this.startActivityForResult(intent,MainActivity.LEAVE_BLUETOOTH_ON);
            }
        });

        return view;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        MainActivity.getInstance().onActivityResult(requestCode,resultCode,data);
    }
}
