package lu.fisch.canze.classes;

import android.content.res.Resources;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import lu.fisch.canze.R;
import lu.fisch.canze.activities.MainActivity;
import lu.fisch.canze.actors.Field;
import lu.fisch.canze.interfaces.FieldListener;

import static lu.fisch.canze.activities.MainActivity.debug;


/**
 * Created by Chris Mattheis on 03/11/15.
 * don't use yet - still work in progress
 */
public class DataLogger  implements FieldListener {

    /* ****************************
     * Singleton stuff
     * ****************************/

    private static DataLogger dataLogger = null;

    public static DataLogger getInstance() {
        if(dataLogger ==null) dataLogger =new DataLogger();
        return dataLogger;
    }

    /* ****************************
     * Datalogger stuff
     * ****************************/

    // -------- Data Definitions copied from Driving Activity -- start ---
    // for ISO-TP optimization to work, group all identical CAN ID's together when calling addListener
    // free data
    public static final String SID_Consumption                          = "1fd.48"; //EVC
    public static final String SID_Pedal                                = "186.40"; //EVC
    public static final String SID_MeanEffectiveTorque                  = "186.16"; //EVC
    public static final String SID_RealSpeed                            = "5d7.0";  //ESC-ABS
    public static final String SID_SoC                                  = "654.25"; //EVC
    public static final String SID_RangeEstimate                        = "654.42"; //EVC
    public static final String SID_DriverBrakeWheel_Torque_Request      = "130.44"; //UBP braking wheel torque the driver wants
    public static final String SID_ElecBrakeWheelsTorqueApplied         = "1f8.28"; //UBP 10ms

    // ISO-TP data
//  public static final String SID_EVC_SoC                              = "7ec.622002.24"; //  (EVC)
//  public static final String SID_EVC_RealSpeed                        = "7ec.622003.24"; //  (EVC)
    public static final String SID_EVC_Odometer                         = "7ec.622006.24"; //  (EVC)
    //  public static final String SID_EVC_Pedal                            = "7ec.62202e.24"; //  (EVC)
    public static final String SID_EVC_TractionBatteryVoltage           = "7ec.623203.24"; //  (EVC)
    public static final String SID_EVC_TractionBatteryCurrent           = "7ec.623204.24"; //  (EVC)
    public static final String SID_MaxCharge                            = "7bb.6101.336";

    private double dcVolt                           = 0; // holds the DC voltage, so we can calculate the power when the amps come in
    private int    odo                              = 0;
    private double realSpeed                        = 0;
    private double dcPwr                            = 0;

    private String var_SoC;
    private String var_Pedal;
    private String var_MeanEffectiveTorque;
    private String var_Odometer;
    private String var_realSpeed;
    private String var_Consumption;
    private String var_dcVolt;
    private String var_dcPwr;
    private String var_rangeInBat;

    private ArrayList<Field> subscribedFields;
    // -------- Data Definitions copied from Driving Activity -- end ---

    private File logFile = null;
    private boolean activated = false;

    private long z = 2;

    SimpleDateFormat sdf = new SimpleDateFormat(MainActivity.getStringSingle(R.string.format_YMDHMS), Locale.getDefault());
    // SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss", Locale.getDefault());

    // Checks if external storage is available for read and write

    public DataLogger() {

        debug("DataLogger: constructor called");

    }

    // milliSeconds == 0 --> get current time
    // milliSeconds > 0 --> use the time given as parameter
    private String getDateString(long milliSeconds, String dateFormat)
    {
        // Create a DateFormatter object for displaying date in specified format.
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);
        // Create a calendar object that will convert the date and time value in milliseconds to date.
        Calendar calendar = Calendar.getInstance();
        if ( milliSeconds > 0 ) {
            calendar.setTimeInMillis(milliSeconds);
        }
        return formatter.format(calendar.getTime());
    }

    public boolean isExternalStorageWritable() {
        String SDstate = Environment.getExternalStorageState();
        return ( Environment.MEDIA_MOUNTED.equals(SDstate));
    }

    public boolean isCreated()
    {
        return (logFile!=null);
    }

    public boolean activate ( boolean state ) {
        boolean result = state;
        debug ( "DataLogger: activate > request = " + state );

        if ( activated != state) {
            if (state) { // now need to activate, open file, start timer
                result = start();
                activated = result; // only true in case of no errors
                // debug("DataLogger: start");
            } else { // now need to de-activate, close file, stop timer
                result = stop();
                activated = false; // always false
                // debug("DataLogger: stop ");
            }
        }
        debug ( "DataLogger: activate > return " + result );
        return result;
    }


    public boolean createNewLog() {
        boolean result = false;

        // ensure that there is a CanZE Folder in SDcard
        if ( ! isExternalStorageWritable()) {
            debug ( "DataLogger: SDcard not writeable");
            return false;
        }
        else {
            String file_path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/CanZE/";
            File dir = new File(file_path);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            debug("DataLogger: file_path:" + file_path);

            // SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
            String exportdataFileName = file_path + "data-" + sdf.format(Calendar.getInstance().getTime()) + ".log";

            logFile = new File(exportdataFileName);
            if (!logFile.exists()) {
                try {
                    logFile.createNewFile();
                    debug("DataLogger: NewFile:" +  exportdataFileName );
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            try {
                //BufferedWriter for performance, true to set append to file flag
                BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(logFile, true));
                // set global static BufferedWriter dataexportStream later
                //if (true) {
                //    bufferedWriter.append("this is just a test if stream is writeable");
                //    bufferedWriter.newLine();
                //    bufferedWriter.close();
                //}
                bufferedWriter.close();
                result = true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    // private long intervall = 5000;
    private int intervall = 5000;

    private Handler handler = new Handler();


    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            // write data to file
            // Long tsLong = System.currentTimeMillis(); // Method 1
            Long tsLong = new Date().getTime(); // Method 2

            String DateString = getDateString( 0 , MainActivity.getStringSingle(R.string.format_YMDHMS));
            // String DateString = getDateString( 0 , "yyyy-MM-dd-HH-mm-ss");
            tsLong >>= 8;
            String timestamp = tsLong.toString();

            // String dataWithNewLine= sdf.format(Calendar.getInstance()) + data + System.getProperty("line.separator");

            // if(!isCreated()) createNewLog();

            // try {
            //    BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(logFile, true));
            //    bufferedWriter.append(data);
            //    bufferedWriter.close();
            //}
            //catch (IOException e) {
            //    e.printStackTrace();
            //}

            if ( realSpeed + dcPwr > 0 ) { // only log while driving or charging
                String dataWithNewLine = timestamp
                        + ";" + DateString
                        + ";" + String.format ("%.3f", realSpeed )
                        + ";" + String.format ("%.3f", dcPwr )
                        + ";" + var_SoC
                        + ";" + var_dcVolt
                        + ";" + var_dcPwr
                        + ";" + var_Pedal
                        + ";" + var_MeanEffectiveTorque
                        + ";" + var_Odometer
                        + ";" + var_realSpeed
                        + ";" + var_Consumption
                        + ";" + var_rangeInBat;
                log(dataWithNewLine);
            }
            handler.postDelayed(this, intervall);
        }
    };

    /**
     * Appends a line of text to the log file
     * @param text  the text line. A CR will be added automatically
     */
    public void log(String text)
    {
        if(!isCreated()) createNewLog();
        debug("DataLogger - log: " + text);

        try {
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(logFile, true));
            bufferedWriter.append(text+ System.getProperty("line.separator"));
            bufferedWriter.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean start() {
        boolean result = false;

        // open logfile
        // start timer
        debug("DataLogger: start");
        handler.postDelayed(runnable, 400);
        initListeners();
        return createNewLog();
    }

    public boolean stop() {
        boolean result = false;

        // flush and close logfile
        // stop timer
        debug("DataLogger: stop");
        logFile = null;
        handler.removeCallbacks(runnable);

        // free up the listeners again
        if (subscribedFields != null) {
            for (Field field : subscribedFields) {
                field.removeListener(this);
            }
            subscribedFields.clear();
        }
        debug("DataLogger: stop - and logFile = null");
        return result;
    }

    public void destroy() {
        handler.removeCallbacks(runnable);
        boolean result = stop();
    }

    private void addListener(String sid, int intervalMs) {
        Field field;
        field = MainActivity.fields.getBySID(sid);
        if (field != null) {
            field.addListener(this);
            // MainActivity.device.addActivityField(field, intervalMs);
            MainActivity.device.addApplicationField(field, intervalMs);
            subscribedFields.add(field);
        }
        else
        {
            MainActivity.toast("sid " + sid + " does not exist in class Fields");
        }
    }

    private void initListeners() {

        subscribedFields = new ArrayList<>();

        debug("DataLogger: initListeners");

        // Make sure to add ISO-TP listeners grouped by ID

        addListener(SID_Consumption, intervall ); // 2000
        addListener(SID_Pedal, intervall );       // 2000
        addListener(SID_MeanEffectiveTorque, intervall ); // 2000
        addListener(SID_DriverBrakeWheel_Torque_Request, intervall ); // 2000
        addListener(SID_ElecBrakeWheelsTorqueApplied, intervall); // 2000
        addListener(SID_RealSpeed, intervall); // 2000
        addListener(SID_SoC, intervall); // 3600
        addListener(SID_RangeEstimate, intervall); // 3600

        //addListener(SID_EVC_SoC);
        addListener(SID_EVC_Odometer, intervall );  // 6000
        addListener(SID_EVC_TractionBatteryVoltage, intervall ); // 5000
        addListener(SID_EVC_TractionBatteryCurrent, intervall ); // 2000
        //addListener(SID_PEB_Torque);
    }


    // This is the event fired as soon as this the registered fields are
    // getting updated by the corresponding reader class.
    @Override
    public void onFieldUpdateEvent(final Field field) {
        String fieldId = field.getSID();
        double fieldValue;

        // Long tsLong = System.currentTimeMillis()/1000;
        // String timestamp = tsLong.toString();

        // String timestamp = "timestamp"; // sdf.format(sdf.format(Calendar.getInstance().getTime()));
        // System.getProperty("line.separator");

        // log ( timestamp + ";" + fieldId + ";" + field.getPrintValue() );
        // get the text field
        switch (fieldId) {
            case SID_SoC:
//                  case SID_EVC_SoC:
                var_SoC = field.getPrintValue();
                // log ( "...SID_SoC: " + fieldValue );
                break;
            case SID_Pedal:
//                  case SID_EVC_Pedal:
                var_Pedal =  field.getPrintValue();
                // pb.setProgress((int) field.getValue());
                break;
            case SID_MeanEffectiveTorque:
                var_MeanEffectiveTorque = field.getPrintValue();
                // pb.setProgress((int) field.getValue());
                break;
            case SID_EVC_Odometer:
                odo = (int ) field.getValue();
                //odo = (int) Utils.kmOrMiles(field.getValue());
                var_Odometer = "" + odo;
                break;
            case SID_RealSpeed:
//                  case SID_EVC_RealSpeed:
                //realSpeed = (Math.round(Utils.kmOrMiles(field.getValue()) * 10.0) / 10.0);
                realSpeed = (Math.round(field.getValue() * 10.0) / 10.0);
                var_realSpeed = "" + realSpeed;
                break;
            //case SID_PEB_Torque:
            //    tv = (TextView) findViewById(R.id.textTorque);
            //    break;
            case SID_EVC_TractionBatteryVoltage: // DC volts
                // save DC voltage for DC power purposes
                dcVolt = field.getValue();
                var_dcVolt = field.getPrintValue();
                break;
            case SID_EVC_TractionBatteryCurrent: // DC amps
                // calculate DC power
                dcPwr = Math.round(dcVolt * field.getValue() / 100.0) / 10.0;
                var_dcPwr = field.getPrintValue();
                break;
            case SID_Consumption:
                dcPwr = field.getValue();
                if (realSpeed > 5) {
                    var_Consumption = "" + (Math.round(1000.0 * dcPwr / realSpeed) / 10.0);
                } else {
                    var_Consumption = "-";
                }
                break;
            case SID_RangeEstimate:
                //int rangeInBat = (int) Utils.kmOrMiles(field.getValue());
                var_rangeInBat = "" + (int) field.getValue();
                break;
            case SID_DriverBrakeWheel_Torque_Request:
                // driverBrakeWheel_Torque_Request = field.getValue();
                break;
            case SID_ElecBrakeWheelsTorqueApplied:
                // double frictionBrakeTorque = driverBrakeWheel_Torque_Request - field.getValue();
                // a fair full red bar is estimated @ 1000 Nm
                // pb = (ProgressBar) findViewById(R.id.FrictionBreaking);
                // pb.setProgress((int) (frictionBrakeTorque * realSpeed));
                break;
        }

    }

}

