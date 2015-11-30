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
import lu.fisch.canze.activities.BatteryActivity;
import lu.fisch.canze.activities.BrakingActivity;
import lu.fisch.canze.activities.ChargingActivity;
import lu.fisch.canze.activities.ConsumptionActivity;
import lu.fisch.canze.activities.DrivingActivity;
import lu.fisch.canze.activities.FirmwareActivity;
import lu.fisch.canze.activities.HeatmapBatcompActivity;
import lu.fisch.canze.activities.HeatmapCellvoltageActivity;
import lu.fisch.canze.activities.MainActivity;
import lu.fisch.canze.activities.TyresActivity;


public class MainFragment extends Fragment {


    public MainFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        Button button;

        button = (Button) view.findViewById(R.id.buttonBattery);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!MainActivity.isSafe()) return;
                if(MainActivity.device==null) {MainActivity.toast("You first need to adjust the settings ..."); return;}
                MainActivity.getInstance().leaveBluetoothOn=true;
                Intent intent = new Intent(MainActivity.getInstance(), BatteryActivity.class);
                MainFragment.this.startActivityForResult(intent,MainActivity.LEAVE_BLUETOOTH_ON);
            }
        });

        button = (Button) view.findViewById(R.id.buttonChargingActivity);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!MainActivity.isSafe()) return;
                if(MainActivity.device==null) {MainActivity.toast("You first need to adjust the settings ..."); return;}
                MainActivity.getInstance().leaveBluetoothOn=true;
                Intent intent = new Intent(MainActivity.getInstance(), ChargingActivity.class);
                MainFragment.this.startActivityForResult(intent,MainActivity.LEAVE_BLUETOOTH_ON);
            }
        });

        button = (Button) view.findViewById(R.id.buttonDrivingActivity);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!MainActivity.isSafe()) return;
                if(MainActivity.device==null) {MainActivity.toast("You first need to adjust the settings ..."); return;}
                MainActivity.getInstance().leaveBluetoothOn=true;
                Intent intent = new Intent(MainActivity.getInstance(), DrivingActivity.class);
                MainFragment.this.startActivityForResult(intent,MainActivity.LEAVE_BLUETOOTH_ON);
            }
        });

        button = (Button) view.findViewById(R.id.buttonConsumption);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!MainActivity.isSafe()) return;
                if(MainActivity.device==null) {MainActivity.toast("You first need to adjust the settings ..."); return;}
                MainActivity.getInstance().leaveBluetoothOn=true;
                Intent intent = new Intent(MainActivity.getInstance(), ConsumptionActivity.class);
                MainFragment.this.startActivityForResult(intent,MainActivity.LEAVE_BLUETOOTH_ON);
            }
        });

        button = (Button) view.findViewById(R.id.buttonBraking);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!MainActivity.isSafe()) return;
                if(MainActivity.device==null) {MainActivity.toast("You first need to adjust the settings ..."); return;}
                MainActivity.getInstance().leaveBluetoothOn=true;
                Intent intent = new Intent(MainActivity.getInstance(), BrakingActivity.class);
                MainFragment.this.startActivityForResult(intent,MainActivity.LEAVE_BLUETOOTH_ON);
            }
        });

        button = (Button) view.findViewById(R.id.buttonHeatmapBatcomp);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!MainActivity.isSafe()) return;
                if(MainActivity.device==null) {MainActivity.toast("You first need to adjust the settings ..."); return;}
                MainActivity.getInstance().leaveBluetoothOn=true;
                Intent intent = new Intent(MainActivity.getInstance(), HeatmapBatcompActivity.class);
                MainFragment.this.startActivityForResult(intent, MainActivity.LEAVE_BLUETOOTH_ON);
            }
        });

        button = (Button) view.findViewById(R.id.buttonHeatmapCellvoltage);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!MainActivity.isSafe()) return;
                if(MainActivity.device==null) {MainActivity.toast("You first need to adjust the settings ..."); return;}
                MainActivity.getInstance().leaveBluetoothOn=true;
                Intent intent = new Intent(MainActivity.getInstance(), HeatmapCellvoltageActivity.class);
                MainFragment.this.startActivityForResult(intent, MainActivity.LEAVE_BLUETOOTH_ON);
            }
        });

        button = (Button) view.findViewById(R.id.buttonTyres);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!MainActivity.isSafe()) return;
                if(MainActivity.device==null) {MainActivity.toast("You first need to adjust the settings ..."); return;}
                MainActivity.getInstance().leaveBluetoothOn=true;
                Intent intent = new Intent(MainActivity.getInstance(), TyresActivity.class);
                MainFragment.this.startActivityForResult(intent, MainActivity.LEAVE_BLUETOOTH_ON);
            }
        });

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        MainActivity.getInstance().onActivityResult(requestCode,resultCode,data);
    }
}
