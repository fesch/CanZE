package lu.fisch.canze.classes;

import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import lu.fisch.canze.R;
import lu.fisch.canze.activities.AllDataActivity;
import lu.fisch.canze.activities.AuxBattTechActivity;
import lu.fisch.canze.activities.BatteryActivity;
import lu.fisch.canze.activities.BrakingActivity;
import lu.fisch.canze.activities.ChargingActivity;
import lu.fisch.canze.activities.ChargingGraphActivity;
import lu.fisch.canze.activities.ChargingHistActivity;
import lu.fisch.canze.activities.ChargingTechActivity;
import lu.fisch.canze.activities.ClimaTechActivity;
import lu.fisch.canze.activities.ConsumptionActivity;
import lu.fisch.canze.activities.DashActivity;
import lu.fisch.canze.activities.DrivingActivity;
import lu.fisch.canze.activities.DtcActivity;
import lu.fisch.canze.activities.ElmTestActivity;
import lu.fisch.canze.activities.FieldTestActivity;
import lu.fisch.canze.activities.FirmwareActivity;
import lu.fisch.canze.activities.HeatmapBatcompActivity;
import lu.fisch.canze.activities.HeatmapCellvoltageActivity;
import lu.fisch.canze.activities.LeakCurrentsActivity;
import lu.fisch.canze.activities.MainActivity;
import lu.fisch.canze.activities.PredictionActivity;
import lu.fisch.canze.activities.RangeActivity;
import lu.fisch.canze.activities.ResearchActivity;
import lu.fisch.canze.activities.SpeedcontrolActivity;
import lu.fisch.canze.activities.TiresActivity;
import lu.fisch.canze.fragments.CustomFragment;

public class ActivityRegistry {

    private ArrayList<Activity> activities = new ArrayList<>();
    private ArrayList<Activity> selected = new ArrayList<>();

    private static ActivityRegistry registry = null;

    public void loadSelection()
    {
        selected.clear();
        SharedPreferences settings = MainActivity.getInstance().getSharedPreferences(MainActivity.PREFERENCES_FILE, 0);
        SharedPreferences.Editor editor = settings.edit();
        for(int i = 0; i< CustomFragment.BUTTONCOUNT; i++)
        {
            int act = settings.getInt("buttonC"+i,-1);
            if(act!=-1)
            {
                selected.add(activities.get(act));
            }
        }
    }



    private ActivityRegistry()
    {
        /*// main
        activities.add(new Activity("CONSUMPTION", @drawable/button_consumption",ConsumptionActivity.class));
        activities.add(new Activity("CHARGING","@drawable/button_charge",ChargingActivity.class));
        activities.add(new Activity("BATTERY","@drawable/button_battery",BatteryActivity.class));
        activities.add(new Activity("DRIVING","@drawable/button_drive",DrivingActivity.class));
        activities.add(new Activity("CLIMATE","@drawable/button_climate",ClimaTechActivity.class));
        activities.add(new Activity("BRAKING","@drawable/button_brake",BrakingActivity.class));
        activities.add(new Activity("AVG SPEED","@drawable/button_speedcam",SpeedcontrolActivity.class));
        // technical
        activities.add(new Activity("CHARGING TECH","@drawable/button_charge", ChargingTechActivity.class));
        activities.add(new Activity("CHARGING GRAPHS","@drawable/button_charging_graphs", ChargingGraphActivity.class));
        activities.add(new Activity("CHARGING PREDICTION","@drawable/button_prediction", PredictionActivity.class));
        activities.add(new Activity("CHARGING HISTORY","@drawable/button_charginghist", ChargingHistActivity.class));
        activities.add(new Activity("LEAK CURRENTS","@drawable/button_leak", LeakCurrentsActivity.class));
        activities.add(new Activity("VOLTAGE HEATMAP","@drawable/button_lightning", HeatmapCellvoltageActivity.class));
        activities.add(new Activity("RANGE","@drawable/button_range", RangeActivity.class));
        activities.add(new Activity("DTC READOUT","@drawable/button_attention", DtcActivity.class));
        activities.add(new Activity("FIRMWARE","@drawable/button_firmware", FirmwareActivity.class));
        activities.add(new Activity("ELM TESTING","@drawable/button_elm327", ElmTestActivity.class));
        activities.add(new Activity("12 VOLT BATT","@drawable/button_auxbat", AuxBattTechActivity.class));
        activities.add(new Activity("TIRES","@drawable/button_tire", TiresActivity.class));
        activities.add(new Activity("TEMPERATURE HEATMAP","@drawable/button_batterytemp", HeatmapBatcompActivity.class));
        activities.add(new Activity("ALL DATA","@drawable/button_alldata", AllDataActivity.class));
        // experimental
        activities.add(new Activity("DASH","@drawable/button_dash", DashActivity.class));
        activities.add(new Activity("RESEARCH","@drawable/button_microscope", ResearchActivity.class));
        activities.add(new Activity("FIELD TEST","@drawable/button_test", FieldTestActivity.class));*/

        // main
        activities.add(new Activity(0, R.string.button_Consumption, R.drawable.button_consumption, ConsumptionActivity.class));
        activities.add(new Activity(1, R.string.button_Charging, R.drawable.button_charge, ChargingActivity.class));
        activities.add(new Activity(2, R.string.button_Battery, R.drawable.button_battery, BatteryActivity.class));
        activities.add(new Activity(3, R.string.button_Driving, R.drawable.button_drive, DrivingActivity.class));
        activities.add(new Activity(4, R.string.button_Climate, R.drawable.button_climate, ClimaTechActivity.class));
        activities.add(new Activity(5, R.string.button_Braking, R.drawable.button_brake, BrakingActivity.class));
        activities.add(new Activity(6, R.string.button_speedcontrol, R.drawable.button_speedcam, SpeedcontrolActivity.class));
        // technical
        activities.add(new Activity(7, R.string.button_ChargingTech, R.drawable.button_charge, ChargingTechActivity.class));
        activities.add(new Activity(8, R.string.button_DtcReadout, R.drawable.button_attention, DtcActivity.class));
        activities.add(new Activity(9, R.string.button_ChargingGraphs, R.drawable.button_charging_graphs, ChargingGraphActivity.class));
        activities.add(new Activity(10, R.string.button_Firmware, R.drawable.button_firmware, FirmwareActivity.class));
        activities.add(new Activity(11, R.string.button_ChargingPrediction, R.drawable.button_prediction, PredictionActivity.class));
        activities.add(new Activity(12, R.string.button_ElmTesting, R.drawable.button_elm327, ElmTestActivity.class));
        activities.add(new Activity(13, R.string.button_chargingHistory, R.drawable.button_charginghist, ChargingHistActivity.class));
        activities.add(new Activity(14, R.string.button_AuxBatt, R.drawable.button_auxbat, AuxBattTechActivity.class));
        activities.add(new Activity(15, R.string.button_LeakCurrents, R.drawable.button_leak, LeakCurrentsActivity.class));
        activities.add(new Activity(16, R.string.button_Tires, R.drawable.button_tire, TiresActivity.class));
        activities.add(new Activity(17, R.string.button_HeatmapVoltage, R.drawable.button_lightning, HeatmapCellvoltageActivity.class));
        activities.add(new Activity(18, R.string.button_HeatmapTemperature, R.drawable.button_batterytemp, HeatmapBatcompActivity.class));
        activities.add(new Activity(19, R.string.button_Range, R.drawable.button_range, RangeActivity.class));
        activities.add(new Activity(20, R.string.button_AllData, R.drawable.button_alldata, AllDataActivity.class));
        // experimental
        activities.add(new Activity(21, R.string.button_Dash, R.drawable.button_dash, DashActivity.class));
        activities.add(new Activity(22, R.string.button_Research, R.drawable.button_microscope, ResearchActivity.class));
        activities.add(new Activity(23, R.string.button_FieldTest, R.drawable.button_test, FieldTestActivity.class));

        // sort by title
        Collections.sort(activities, new Comparator<Activity>() {
            @Override
            public int compare(Activity o1, Activity o2) {
                return o1.getTitle().compareTo(o2.getTitle());
            }
        });

        loadSelection();
    }

    public static ActivityRegistry getInstance()
    {
        if(registry==null) registry = new ActivityRegistry();
        return registry;
    }

    public Activity get(int index)
    {
        return activities.get(index);
    }

    public Activity getById (int id) {
        for (int i = 0; i < activities.size(); i++) {
            Activity a = activities.get (i);
            if (a.getId() == id) return a;
        }
        return null;
    }

    public Activity getByTitle (String title) {
        for (int i = 0; i < activities.size(); i++) {
            Activity a = activities.get (i);
            if (a.getTitle().equals(title)) return a;
        }
        return null;
    }

    public int size()
    {
        return activities.size();
    }

    public Activity selectedGet(int index)
    {
        return selected.get(index);
    }

    public int selectedSize()
    {
        return selected.size();
    }

    public ArrayList<String> getActivities()
    {
        ArrayList<String> result = new ArrayList<>();
        for (int i = 0; i < activities.size(); i++) {
            result.add(activities.get(i).getTitle());
        }
        return result;
    }

    public ArrayList<String> getSelected()
    {
        ArrayList<String> result = new ArrayList<>();
        for (int i = 0; i < selected.size(); i++) {
            result.add(selected.get(i).getTitle());
        }
        return result;
    }

    public void addToSelected(int index)
    {
        selected.add(activities.get(index));
    }

    public void removeFromSelected(int index)
    {
        selected.remove(index);
    }

    public int getPos(Activity a)
    {
        return activities.indexOf(a);
    }

    public boolean moveSelectedUp(int index)
    {
        if(index>0) {
            Activity a = selected.get(index);
            selected.remove(index);
            selected.add(index - 1, a);
            return true;
        }
        return false;
    }

    public boolean moveSelectedDown(int index)
    {
        if(index<selected.size()-1) {
            Activity a = selected.get(index);
            selected.remove(index);
            selected.add(index + 1, a);
            return true;
        }
        return false;
    }


}
