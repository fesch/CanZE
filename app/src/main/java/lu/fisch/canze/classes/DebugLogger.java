package lu.fisch.canze.classes;

import android.os.Environment;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import lu.fisch.canze.R;
import lu.fisch.canze.activities.MainActivity;

import static lu.fisch.canze.activities.MainActivity.TAG;
import static lu.fisch.canze.activities.MainActivity.debug;

/**
 * Created by Chris Mattheis on 03/11/15.
 * don't use yet - still work in progress
 */
public class DebugLogger {

    /* ****************************
     * Singleton stuff
     * ****************************/

    private final static DebugLogger instance = new DebugLogger();

    private DebugLogger() {
    }

    public static DebugLogger getInstance() {
        return instance;
    }

    /* ****************************
     * Datalogger stuff
     * ****************************/

    private SimpleDateFormat sdf = new SimpleDateFormat(MainActivity.getStringSingle(R.string.format_YMDHMS), Locale.US);
    private boolean firstLog = true;
    private File logFile = null;

    private boolean isCreated() {
        return (logFile != null);
    }

    private boolean createNewLog() {

        if (!MainActivity.storageIsAvailable) {
            Log.d(TAG, "AllDataActivity.createDump: SDcard not available");
            return false;
        }
        if (!MainActivity.getInstance().isExternalStorageWritable()) {
            Log.d(TAG, "DiagDump: SDcard not writeable");
            return false;
        }
        String file_path = MainActivity.getInstance().getExternalFolder();

        File dir = new File(file_path);
        if (!dir.exists()) {
            if (!dir.mkdirs()) {
                Log.d(TAG, "DiagDump: can't create directory:" + file_path);
                logFile = null;
                return false;
            }
        }

        String exportdataFileName = file_path + "debug-" + sdf.format(Calendar.getInstance().getTime()) + ".log";

        logFile = new File(exportdataFileName);
        if (!logFile.exists()) {
            try {
                if (!logFile.createNewFile()) {
                    Log.d(TAG, "DiagDump: can't create file:" + exportdataFileName);
                    logFile = null;
                    return false;
                }
            } catch (IOException e) {
                e.printStackTrace();
                logFile = null;
                return false;
            }
        }

        // this should not happen, but it did 30-12-2020 Android 6.0.1 App 1.54
        if (logFile == null) return false;

        try {
            //BufferedWriter for performance, true to set append to file flag
            FileWriter fileWriter = new FileWriter(logFile, true);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

            // header
            bufferedWriter.append("Datetime,Message");
            bufferedWriter.close();

            sdf = new SimpleDateFormat(MainActivity.getStringSingle(R.string.format_YMDHMSs), Locale.US);

            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        logFile = null;
        return false;
    }

    /**
     * Appends a line of text to the log file
     *
     * @param text the text line. A CR will be added automatically
     */
    public void log(String text) {
        if (logFile == null && firstLog) createNewLog();
        firstLog = false;

        if (logFile != null) {
            try {
                BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(logFile, true));
                bufferedWriter.append(sdf.format(Calendar.getInstance().getTime()) + "," + text + "\n");
                bufferedWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
                logFile = null;
            }
        }
    }

}
