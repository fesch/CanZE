package lu.fisch.canze.classes;

import android.os.Environment;

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
public class DebugLogger {

    /* ****************************
     * Singleton stuff
     * ****************************/

    private static DebugLogger debugLogger = null;

    private DebugLogger() {}

    public static DebugLogger getInstance() {
        if(debugLogger ==null) debugLogger =new DebugLogger();
        return debugLogger;
    }

    /* ****************************
     * Datalogger stuff
     * ****************************/

    private File logFile = null;

    public boolean isCreated()
    {
        return (logFile!=null);
    }

    public boolean createNewLog() {
        boolean result = false;

        debug ("create new data logfile");

        // ensure that there is a CanZE Folder in SDcard
        String file_path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/CanZE/";

        debug ("file_path:" + file_path);

        File dir = new File(file_path);
        if (!dir.exists()) {
            dir.mkdirs();
            debug ("SDcard dir CanZE created");
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
        String exportdataFileName = file_path + sdf.format(Calendar.getInstance().getTime()) + ".log";

        logFile = new File(exportdataFileName);
        if (!logFile.exists()) {
            try {
                logFile.createNewFile();
            }
            catch (IOException e) {
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
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * Appends a line of text to the log file
     * @param text  the text line. A CR will be added automatically
     */
    public void log(String text)
    {
        try {
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(logFile, true));
            bufferedWriter.append(text+"\n");
            bufferedWriter.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

}

