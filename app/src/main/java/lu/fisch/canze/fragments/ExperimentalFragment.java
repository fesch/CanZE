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
import lu.fisch.canze.activities.ChargingHistActivity;
import lu.fisch.canze.activities.FluenceKangooTempsActivity;
import lu.fisch.canze.activities.LeafSpyActivity;
import lu.fisch.canze.activities.MainActivity;

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

        button = (Button) view.findViewById(R.id.buttonChargingHistory);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!MainActivity.isSafe()) return;
                if(MainActivity.device==null) {MainActivity.toast("You first need to adjust the settings ..."); return;}
                MainActivity.getInstance().leaveBluetoothOn=true;
                Intent intent = new Intent(MainActivity.getInstance(), ChargingHistActivity.class);
                ExperimentalFragment.this.startActivityForResult(intent,MainActivity.LEAVE_BLUETOOTH_ON);
            }
        });


        button = (Button) view.findViewById(R.id.buttonLeafSpy);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!MainActivity.isSafe()) return;
                if(MainActivity.device==null) {MainActivity.toast("You first need to adjust the settings ..."); return;}
                MainActivity.getInstance().leaveBluetoothOn=true;
                Intent intent = new Intent(MainActivity.getInstance(), LeafSpyActivity.class);
                ExperimentalFragment.this.startActivityForResult(intent,MainActivity.LEAVE_BLUETOOTH_ON);
            }
        });


        button = (Button) view.findViewById(R.id.buttonFluenceKangooTemps);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!MainActivity.isSafe()) return;
                if(MainActivity.device==null) {MainActivity.toast("You first need to adjust the settings ..."); return;}
                MainActivity.getInstance().leaveBluetoothOn=true;
                Intent intent = new Intent(MainActivity.getInstance(), FluenceKangooTempsActivity.class);
                ExperimentalFragment.this.startActivityForResult(intent, MainActivity.LEAVE_BLUETOOTH_ON);
            }
        });

        // activateButton(view, R.id.buttonLeafSpy,                LeafSpyActivity.class);
        // activateButton(view, R.id.buttonFluenceKangooTemps,     FluenceKangooTempsActivity.class);

        return view;
    }

    private void activateButton (View view, int buttonId, final Class<?> activityClass) {
        Button button = (Button) view.findViewById(buttonId);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!MainActivity.isSafe()) return;
                if(MainActivity.device==null) {MainActivity.toast("You first need to adjust the settings ..."); return;}
                MainActivity.getInstance().leaveBluetoothOn = true;
                Intent intent = new Intent(MainActivity.getInstance(), activityClass);
                ExperimentalFragment.this.startActivityForResult(intent, MainActivity.LEAVE_BLUETOOTH_ON);
            }
        });
    }





    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        MainActivity.getInstance().onActivityResult(requestCode,resultCode,data);
    }
}
