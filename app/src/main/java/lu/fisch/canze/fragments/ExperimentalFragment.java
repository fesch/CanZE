package lu.fisch.canze.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


import lu.fisch.canze.R;
import lu.fisch.canze.activities.AlexandreActivity;
import lu.fisch.canze.activities.BatteryTempActivity;
import lu.fisch.canze.activities.DtcActivity;
import lu.fisch.canze.activities.ElmTestActivity;
import lu.fisch.canze.activities.HarmActivity;
import lu.fisch.canze.activities.LeafSpyActivity;
import lu.fisch.canze.activities.MainActivity;
import lu.fisch.canze.activities.StatsActivity;
import lu.fisch.canze.activities.TachoActivity;
import lu.fisch.canze.activities.TemperatureActivity;

/**
 * A simple {@link Fragment} subclass.
 */
public class ExperimentalFragment extends Fragment {


    public ExperimentalFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_experimental, container, false);

        Button button;

        button = (Button) view.findViewById(R.id.buttonTacho);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!MainActivity.getInstance().isSafe()) return;
                if(MainActivity.device==null) {MainActivity.toast("You first need to adjust the settings ..."); return;}
                MainActivity.getInstance().leaveBluetoothOn=true;
                Intent intent = new Intent(MainActivity.getInstance(), TachoActivity.class);
                ExperimentalFragment.this.startActivityForResult(intent,MainActivity.LEAVE_BLUETOOTH_ON);
            }
        });



        button = (Button) view.findViewById(R.id.buttonBatTemp);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!MainActivity.getInstance().isSafe()) return;
                if(MainActivity.device==null) {MainActivity.toast("You first need to adjust the settings ..."); return;}
                MainActivity.getInstance().leaveBluetoothOn=true;
                Intent intent = new Intent(MainActivity.getInstance(), BatteryTempActivity.class);
                ExperimentalFragment.this.startActivityForResult(intent,MainActivity.LEAVE_BLUETOOTH_ON);
            }
        });

        button = (Button) view.findViewById(R.id.buttonTemperature);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!MainActivity.getInstance().isSafe()) return;
                if(MainActivity.device==null) {MainActivity.toast("You first need to adjust the settings ..."); return;}
                MainActivity.getInstance().leaveBluetoothOn=true;
                Intent intent = new Intent(MainActivity.getInstance(), TemperatureActivity.class);
                ExperimentalFragment.this.startActivityForResult(intent,MainActivity.LEAVE_BLUETOOTH_ON);
            }
        });



        button = (Button) view.findViewById(R.id.buttonLeafSpy);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!MainActivity.getInstance().isSafe()) return;
                if(MainActivity.device==null) {MainActivity.toast("You first need to adjust the settings ..."); return;}
                MainActivity.getInstance().leaveBluetoothOn=true;
                Intent intent = new Intent(MainActivity.getInstance(), LeafSpyActivity.class);
                ExperimentalFragment.this.startActivityForResult(intent,MainActivity.LEAVE_BLUETOOTH_ON);
            }
        });



        button = (Button) view.findViewById(R.id.buttonStats);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!MainActivity.getInstance().isSafe()) return;
                if(MainActivity.device==null) {MainActivity.toast("You first need to adjust the settings ..."); return;}
                MainActivity.getInstance().leaveBluetoothOn=true;
                Intent intent = new Intent(MainActivity.getInstance(), StatsActivity.class);
                ExperimentalFragment.this.startActivityForResult(intent,MainActivity.LEAVE_BLUETOOTH_ON);
            }
        });

        button = (Button) view.findViewById(R.id.buttonPgHo);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!MainActivity.getInstance().isSafe()) return;
                if(MainActivity.device==null) {MainActivity.toast("You first need to adjust the settings ..."); return;}
                MainActivity.getInstance().leaveBluetoothOn=true;
                Intent intent = new Intent(MainActivity.getInstance(), HarmActivity.class);
                ExperimentalFragment.this.startActivityForResult(intent,MainActivity.LEAVE_BLUETOOTH_ON);
            }
        });

        button = (Button) view.findViewById(R.id.buttonPgJm);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!MainActivity.getInstance().isSafe()) return;
                if(MainActivity.device==null) {MainActivity.toast("You first need to adjust the settings ..."); return;}
                MainActivity.getInstance().leaveBluetoothOn=true;
                Intent intent = new Intent(MainActivity.getInstance(), ElmTestActivity.class);
                ExperimentalFragment.this.startActivityForResult(intent,MainActivity.LEAVE_BLUETOOTH_ON);
            }
        });

        button = (Button) view.findViewById(R.id.buttonPgAm);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!MainActivity.getInstance().isSafe()) return;
                if(MainActivity.device==null) {MainActivity.toast("You first need to adjust the settings ..."); return;}
                MainActivity.getInstance().leaveBluetoothOn=true;
                Intent intent = new Intent(MainActivity.getInstance(), AlexandreActivity.class);
                ExperimentalFragment.this.startActivityForResult(intent, MainActivity.LEAVE_BLUETOOTH_ON);
            }
        });

        button = (Button) view.findViewById(R.id.buttonDtc);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!MainActivity.getInstance().isSafe()) return;
                if(MainActivity.device==null) {MainActivity.toast("You first need to adjust the settings ..."); return;}
                MainActivity.getInstance().leaveBluetoothOn=true;
                Intent intent = new Intent(MainActivity.getInstance(), DtcActivity.class);
                ExperimentalFragment.this.startActivityForResult(intent, MainActivity.LEAVE_BLUETOOTH_ON);
            }
        });

        return view;
    }


}
