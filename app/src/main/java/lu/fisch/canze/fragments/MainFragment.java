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
import android.widget.TextView;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import lu.fisch.canze.R;
import lu.fisch.canze.activities.BatteryActivity;
import lu.fisch.canze.activities.BrakingActivity;
import lu.fisch.canze.activities.ChargingActivity;
import lu.fisch.canze.activities.ConsumptionActivity;
import lu.fisch.canze.activities.DrivingActivity;
import lu.fisch.canze.activities.HeatmapBatcompActivity;
import lu.fisch.canze.activities.HeatmapCellvoltageActivity;
import lu.fisch.canze.activities.MainActivity;
import lu.fisch.canze.activities.TyresActivity;


public class MainFragment extends Fragment {

    static boolean firstRun = true;
    static String msg = "";

    public MainFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        activateButton(view, R.id.buttonBattery,            BatteryActivity.class);
        activateButton(view, R.id.buttonChargingActivity,   ChargingActivity.class);

        activateButton(view, R.id.buttonTyres,              TyresActivity.class);
        activateButton(view, R.id.buttonDrivingActivity,    DrivingActivity.class);

        activateButton(view, R.id.buttonConsumption,        ConsumptionActivity.class);
        activateButton(view, R.id.buttonBraking,            BrakingActivity.class);

        activateButton(view, R.id.buttonHeatmapCellvoltage, HeatmapCellvoltageActivity.class);
        activateButton(view, R.id.buttonHeatmapBatcomp,     HeatmapBatcompActivity.class);

        getNews(view);

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
                MainFragment.this.startActivityForResult(intent, MainActivity.LEAVE_BLUETOOTH_ON);
            }
        });
    }

    private void getNews (final View view) {

        if (firstRun) {
            (new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        URL url = new URL("https://raw.githubusercontent.com/fesch/CanZE/Development/NEWS.txt");
                        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                        try {
                            urlConnection.setConnectTimeout(10000);
                            InputStream ips = urlConnection.getInputStream();
                            BufferedInputStream in = new BufferedInputStream(ips);
                            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                            StringBuilder stringBuilder = new StringBuilder(200);
                            while ((msg = reader.readLine()) != null) {
                                // MainActivity.debug("ELM327Http: httpGet append " + st);
                                stringBuilder.append(msg);
                            }
                            msg = stringBuilder.toString();
                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {
                            urlConnection.disconnect();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    displayNews(view);
                }
            })).start();
            firstRun = false;
        } else {
            displayNews(view);
        }
    }

    private void displayNews (final View view) {
        if (!"".equals(msg)) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    TextView tv = view.findViewById(R.id.textNews);
                    tv.setText(msg);
                    tv.setVisibility(View.VISIBLE);
                }
            });
        }

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        MainActivity.getInstance().onActivityResult(requestCode,resultCode,data);
    }
}
