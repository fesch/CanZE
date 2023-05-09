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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import lu.fisch.canze.BuildConfig;
import lu.fisch.canze.R;
import lu.fisch.canze.activities.DashActivity;
import lu.fisch.canze.activities.FieldTestActivity;
import lu.fisch.canze.activities.MainActivity;
import lu.fisch.canze.activities.ResearchActivity;
import lu.fisch.canze.activities.TwingoTestActivity;
import lu.fisch.canze.activities.TwizyTestActivity;

/**
 * A simple {@link Fragment} subclass.
 */
public class ExperimentalFragment extends Fragment {

    public ExperimentalFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_experimental, container, false);

        activateButton(view, R.id.buttonDash, DashActivity.class);
        activateButton(view, R.id.buttonResearch, ResearchActivity.class);

        activateButton(view, R.id.buttonFieldTest, FieldTestActivity.class, true);
        activateButton(view, R.id.buttonTwingoTest, TwingoTestActivity.class, false);

        activateButton(view, R.id.buttonTwizyTest, TwizyTestActivity.class, false);

        return view;
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
                ExperimentalFragment.this.startActivityForResult(intent, MainActivity.LEAVE_BLUETOOTH_ON);
            }
        });
    }

    private void activateButton(View view, int buttonId, final Class<?> activityClass, boolean onlyDebug) {
        if (BuildConfig.BRANCH.equals("master") & onlyDebug) {
            // if on master and onlyDebug is true, hide button
            Button button = view.findViewById(buttonId);
            button.setVisibility(View.INVISIBLE);
        } else {
            activateButton(view, buttonId, activityClass);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        MainActivity.getInstance().onActivityResult(requestCode, resultCode, data);
    }
}
