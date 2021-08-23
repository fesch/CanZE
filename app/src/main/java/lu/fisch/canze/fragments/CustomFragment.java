package lu.fisch.canze.fragments;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import lu.fisch.canze.R;
import lu.fisch.canze.activities.MainActivity;
import lu.fisch.canze.classes.Activity;
import lu.fisch.canze.classes.ActivityRegistry;


public class CustomFragment extends Fragment {

    public CustomFragment() {
        // Required empty public constructor
    }

    public static final int BUTTONCOUNT = 14;

    private View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_custom, container, false);
        this.view=view;
        loadButtons();
        return view;
    }

    @Override
    public void onResume()
    {
        super.onResume();
        loadButtons();
    }

    public void loadButtons()
    {
        ActivityRegistry registry = ActivityRegistry.getInstance();

        for(int i=0; i<BUTTONCOUNT; i++)
        {
            int buttonId = getResources().getIdentifier("buttonC" + i, "id",MainActivity.getInstance().getApplicationContext().getPackageName());
            Button button = view.findViewById(buttonId);
            if(i<registry.selectedSize())
            {
                Activity a = registry.selectedGet(i);
                button.setText(a.getTitle());

                //int drawableId = getResources().getIdentifier(a.getDrawable(), "id",MainActivity.getInstance().getApplicationContext().getPackageName());
                Drawable icon=this.getResources().getDrawable(a.getDrawable()); //drawableId);
                button.setCompoundDrawablesWithIntrinsicBounds(icon,null,null,null);
                activateButton(view,buttonId,a.getClassOf());
                button.setVisibility(View.VISIBLE);
            }
            // hide button
            else button.setVisibility(View.INVISIBLE);
        }

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
                CustomFragment.this.startActivityForResult(intent, MainActivity.LEAVE_BLUETOOTH_ON);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        MainActivity.getInstance().onActivityResult(requestCode, resultCode, data);
    }
}
