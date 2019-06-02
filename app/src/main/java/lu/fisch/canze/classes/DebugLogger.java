package lu.fisch.canze.classes;

import android.Manifest;
import android.content.res.Resources;
import android.os.Environment;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import android.content.Context;

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

    private static DebugLogger instance = new DebugLogger();

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

    private boolean firstLog = true;
    private File logFile = null;

    private boolean isCreated() {
        return (logFile != null);
    }

    private void createNewLog() {

        if (!MainActivity.storageIsAvailable) {
            debug("AllDataActivity.createDump: SDcard not available");
            return;
        }
        //debug(this.getClass().getSimpleName()+": create new debug logfile");
        if (!isExternalStorageWritable()) {
            debug("DiagDump: SDcard not writeable");
            return;
        }
        // ensure that there is a CanZE Folder in SDcard
        String file_path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/CanZE/";

        //debug(this.getClass().getSimpleName()+": file_path:" + file_path);

        File dir = new File(file_path);
        if (!dir.exists()) {
            dir.mkdirs();
            //debug(this.getClass().getSimpleName()+": SDcard dir CanZE created");
        }

        SimpleDateFormat sdf = new SimpleDateFormat(MainActivity.getStringSingle(R.string.format_YMDHMSs), Locale.getDefault());
        // SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault());
        String exportdataFileName = file_path + "debug" + sdf.format(Calendar.getInstance().getTime()) + ".log";

        logFile = new File(exportdataFileName);
        if (!logFile.exists()) {
            try {
                logFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                logFile = null;
                return;
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
            return;
        } catch (IOException e) {
            e.printStackTrace();
            logFile = null;
        }
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
                bufferedWriter.append(text);
                bufferedWriter.append("\n");
                bufferedWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
                logFile = null;
            }
        }
    }

}
