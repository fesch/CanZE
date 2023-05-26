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
import androidx.fragment.app.FragmentActivity;

import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import lu.fisch.canze.BuildConfig;
import lu.fisch.canze.R;
import lu.fisch.canze.activities.BatteryActivity;
import lu.fisch.canze.activities.BrakingActivity;
import lu.fisch.canze.activities.ChargingActivity;
import lu.fisch.canze.activities.ClimaTechActivity;
import lu.fisch.canze.activities.ConsumptionActivity;
import lu.fisch.canze.activities.DrivingActivity;
import lu.fisch.canze.activities.MainActivity;
import lu.fisch.canze.activities.SpeedcontrolActivity;


public class MainFragment extends Fragment {

    private static boolean firstRun = true;
    private static String msg = "";
    private static boolean isHtml = false;

    public MainFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        // For a bundle, the app might have been installed by users of rooted devices without the
        // proper "sub-APKs", which will make it crash on not found resources when inflating the
        // screen. We try to catch that here without using any resource.
        try {
            View view = inflater.inflate(R.layout.fragment_main, container, false);

            activateButton(view, R.id.buttonConsumption, ConsumptionActivity.class);
            activateButton(view, R.id.buttonChargingActivity, ChargingActivity.class);

            activateButton(view, R.id.buttonBattery, BatteryActivity.class);
            activateButton(view, R.id.buttonDrivingActivity, DrivingActivity.class);

            activateButton(view, R.id.buttonClimaTech, ClimaTechActivity.class);
            activateButton(view, R.id.buttonBraking, BrakingActivity.class);

            activateButton(view, R.id.buttonSpeed, SpeedcontrolActivity.class);

            getNews(view);
            return view;

        } catch (Exception e) {
            MainActivity.toast(MainActivity.TOAST_NONE, "Please install from the play store");
            MainActivity.getInstance().finish();
            System.exit(0);
        }

        return null;
    }

    private void activateButton(View view, int buttonId, final Class<?> activityClass) {
        Button button = view.findViewById(buttonId);
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
                MainFragment.this.startActivityForResult(intent, MainActivity.LEAVE_BLUETOOTH_ON);
            }
        });
    }

    private void getNews(final View view) {

        if (firstRun) {
            (new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        URL url = new URL("https://raw.githubusercontent.com/fesch/CanZE/Development/NEWS.json");
                        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                        if (urlConnection == null) return;
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

                            JsonElement jsonElement = JsonParser.parseString(stringBuilder.toString());
                            msg = "";
                            int version = jsonElement.getAsJsonObject().get("version").getAsInt();
                            msg = jsonElement.getAsJsonObject().get("news").getAsString();
                            isHtml = msg.contains("<");
                            if (version > BuildConfig.VERSION_CODE) {
                                if (isHtml) {
                                    msg = "<p>Please upgrade CanZE</p>" + msg;
                                } else {
                                    msg = "Please upgrade CanZE\n\n" + msg;
                                }
                            }
                        } catch (Exception e) {
                            // ignore offline situation
                        } finally {
                            urlConnection.disconnect();
                        }
                    } catch (Exception e) {
                        // ignore offline situation
                    }
                    displayNews(view);
                }
            })).start();
            firstRun = false;
        } else {
            displayNews(view);
        }
    }

    private void displayNews(final View view) {
        if (msg == null || "".equals(msg)) return;
        FragmentActivity fa = getActivity();
        if (fa == null) return;
        fa.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                TextView tv = view.findViewById(R.id.textNews);
                tv.setVisibility(View.VISIBLE);
                if (isHtml) {
                    tv.setText(Html.fromHtml(msg));
                    tv.setMovementMethod(LinkMovementMethod.getInstance());
                } else {
                    tv.setText(msg);
                }
            }
        });
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        MainActivity.getInstance().onActivityResult(requestCode, resultCode, data);
    }
}
