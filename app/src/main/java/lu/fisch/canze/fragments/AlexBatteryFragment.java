package lu.fisch.canze.fragments;

/**
 * Created by alexandre.moleiro on 15-10-2015.
 */

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import lu.fisch.canze.R;

public class AlexBatteryFragment extends Fragment {


    public AlexBatteryFragment(){

    }





    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.alex_battery_section, null);
        return       v;
    }



    //comentario
    @Override
    public void onResume() {
        super.onResume();

    }





}