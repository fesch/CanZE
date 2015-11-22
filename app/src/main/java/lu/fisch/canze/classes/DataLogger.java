package lu.fisch.canze.classes;

import android.os.Environment;
import android.os.Handler;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import static lu.fisch.canze.activities.MainActivity.debug;


/**
 * Created by Chris Mattheis on 03/11/15.
 * don't use yet - still work in progress
 */
public class DataLogger {

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

    private File logFile = null;
    private boolean activated = false;

    private long z = 2;

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
    // Checks if external storage is available for read and write

    public DataLogger() {

        debug("DataLogger: constructor called");

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
        debug ( "DataLogger: activate req " + state );

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
        debug ( "DataLogger: activate return " + result );
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
            String exportdataFileName = file_path + "data" + sdf.format(Calendar.getInstance().getTime()) + ".log";

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

    private long intervall = 5000;

    private Handler handler = new Handler();


    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            // write data to file
            String data = "Zeile";
            // String dataWithNewLine= sdf.format(Calendar.getInstance()) + data + System.getProperty("line.separator");
            String dataWithNewLine=  data + System.getProperty("line.separator");

            // if(!isCreated()) createNewLog();

            // try {
            //    BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(logFile, true));
            //    bufferedWriter.append(data);
            //    bufferedWriter.close();
            //}
            //catch (IOException e) {
            //    e.printStackTrace();
            //}
            log ( dataWithNewLine );
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
        debug( "DataLogger - log: " + text );

        try {
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(logFile, true));
            bufferedWriter.append(text+"\n");
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
        return createNewLog();
    }

    public boolean stop() {
        boolean result = false;

        // flush and close logfile
        // stop timer
        logFile = null;
        handler.removeCallbacks(runnable);
        debug("DataLogger: stop - and logFile = null");
        return result;
    }

    public void onCreate() {
        // start timer in 400ms
        boolean result = start();
        // handler.postDelayed(runnable, 400 );
    }

    public void onDestroy() {
        handler.removeCallbacks(runnable);
        boolean result = stop();
    }

    public void onPause() {
        onDestroy();
    }

    public void onResume() {
        onCreate();
    }

    // only for test and trace purposes - delete later on
    public void add14() {
        z += 14;
        debug( "DataLogger: " + z );
    }
}

