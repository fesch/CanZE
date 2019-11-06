package lu.fisch.canze.classes;

import android.os.Environment;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import lu.fisch.canze.R;
import lu.fisch.canze.activities.MainActivity;

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

    private boolean isExternalStorageWritable() {
        String SDstate = Environment.getExternalStorageState();
        return (Environment.MEDIA_MOUNTED.equals(SDstate));
    }

    private SimpleDateFormat sdf = new SimpleDateFormat(MainActivity.getStringSingle(R.string.format_YMDHMS), Locale.US);
    private boolean firstLog = true;
    private File logFile = null;

    private boolean isCreated() {
        return (logFile != null);
    }

    private boolean createNewLog() {

        if (!MainActivity.storageIsAvailable) {
            debug("AllDataActivity.createDump: SDcard not available");
            return false;
        }
        if (!isExternalStorageWritable()) {
            debug("DiagDump: SDcard not writeable");
            return false;
        }
        String file_path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/CanZE/";

        File dir = new File(file_path);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        String exportdataFileName = file_path + "debug-" + sdf.format(Calendar.getInstance().getTime()) + ".log";

        logFile = new File(exportdataFileName);
        if (!logFile.exists()) {
            try {
                logFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                logFile = null;
                return false;
            }
        }

        try {
            //BufferedWriter for performance, true to set append to file flag
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(logFile, true));

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
