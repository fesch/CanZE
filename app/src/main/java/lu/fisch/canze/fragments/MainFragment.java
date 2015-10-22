package lu.fisch.canze.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import lu.fisch.canze.R;
import lu.fisch.canze.activities.*;


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

                if(!MainActivity.getInstance().isSafe()) return;
                MainActivity.getInstance().leaveBluetoothOn=true;
                Intent intent = new Intent(MainActivity.getInstance(), BatteryActivity.class);
                MainFragment.this.startActivityForResult(intent,MainActivity.LEAVE_BLUETOOTH_ON);
            }
        });

        button = (Button) view.findViewById(R.id.buttonChargingActivity);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!MainActivity.getInstance().isSafe()) return;
                MainActivity.getInstance().leaveBluetoothOn=true;
                Intent intent = new Intent(MainActivity.getInstance(), ChargingActivity.class);
                MainFragment.this.startActivityForResult(intent,MainActivity.LEAVE_BLUETOOTH_ON);
            }
        });

        button = (Button) view.findViewById(R.id.buttonFirmware);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!MainActivity.getInstance().isSafe()) return;
                MainActivity.getInstance().leaveBluetoothOn=true;
                Intent intent = new Intent(MainActivity.getInstance(), FirmwareActivity.class);
                MainFragment.this.startActivityForResult(intent,MainActivity.LEAVE_BLUETOOTH_ON);
            }
        });

        button = (Button) view.findViewById(R.id.buttonDrivingActivity);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!MainActivity.getInstance().isSafe()) return;
                MainActivity.getInstance().leaveBluetoothOn=true;
                Intent intent = new Intent(MainActivity.getInstance(), DrivingActivity.class);
                MainFragment.this.startActivityForResult(intent,MainActivity.LEAVE_BLUETOOTH_ON);
            }
        });

        button = (Button) view.findViewById(R.id.buttonConsumption);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!MainActivity.getInstance().isSafe()) return;
                MainActivity.getInstance().leaveBluetoothOn=true;
                Intent intent = new Intent(MainActivity.getInstance(), ConsumptionActivity.class);
                MainFragment.this.startActivityForResult(intent,MainActivity.LEAVE_BLUETOOTH_ON);
            }
        });

        button = (Button) view.findViewById(R.id.buttonBraking);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!MainActivity.getInstance().isSafe()) return;
                MainActivity.getInstance().leaveBluetoothOn=true;
                Intent intent = new Intent(MainActivity.getInstance(), BrakingActivity.class);
                MainFragment.this.startActivityForResult(intent,MainActivity.LEAVE_BLUETOOTH_ON);
            }
        });

        return view;
    }


}
