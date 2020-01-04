package lu.fisch.canze.classes;

import java.util.ArrayList;

import lu.fisch.canze.R;
import lu.fisch.canze.activities.BatteryActivity;
import lu.fisch.canze.activities.BrakingActivity;
import lu.fisch.canze.activities.ChargingActivity;
import lu.fisch.canze.activities.ClimaTechActivity;
import lu.fisch.canze.activities.ConsumptionActivity;
import lu.fisch.canze.activities.DrivingActivity;
import lu.fisch.canze.activities.SpeedcontrolActivity;

public class ActivityRegistry {

    private ArrayList<Activity> activities = new ArrayList<>();

    private static ActivityRegistry registry = new ActivityRegistry();

    private ActivityRegistry()
    {
        activities.add(new Activity("CONSUMPTION","@drawable/button_consumption",ChargingActivity.class));

        /*
        activateButton(view, R.id.buttonChargingActivity, ChargingActivity.class);

        activateButton(view, R.id.buttonBattery, BatteryActivity.class);
        activateButton(view, R.id.buttonDrivingActivity, DrivingActivity.class);

        activateButton(view, R.id.buttonClimaTech, ClimaTechActivity.class);
        activateButton(view, R.id.buttonBraking, BrakingActivity.class);

        activateButton(view, R.id.buttonSpeed, SpeedcontrolActivity.class);
        */
    }

    public static ActivityRegistry getInstance()
    {
        return registry;
    }

    public Activity get(int index)
    {
        return activities.get(index);
    }

    public int size()
    {
        return activities.size();
    }

}
