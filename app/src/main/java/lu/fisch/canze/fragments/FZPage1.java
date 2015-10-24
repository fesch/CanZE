package lu.fisch.canze.fragments;

/**
 * Created by alexandre.moleiro on 15-10-2015.
 */

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import lu.fisch.canze.R;

public class FZPage1 extends Fragment {


    public FZPage1(){

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {



        View v = inflater.inflate(R.layout.fz_section1, null);


        return       v;


    }

    @Override
    public void onResume() {
        super.onResume();

    }


    public void actpag1(long[] array1) {
        if (array1[0]!=-100) {
            TextView view = (TextView) getView().findViewById(R.id.socx475_1);
            double temp = ((double) array1[0]) /100.0;
            view.setText("SOC:" + String.format("%3.2f", temp) + "%");
        }


    }


}







