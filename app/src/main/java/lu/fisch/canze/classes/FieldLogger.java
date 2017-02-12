package lu.fisch.canze.classes;

import android.content.res.Resources;
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
public class FieldLogger {

    /* ****************************
     * Singleton stuff
     * ****************************/

    private static FieldLogger instance = new FieldLogger();

    private FieldLogger() {}

    public static FieldLogger getInstance() {
        return instance;
    }

    /* ****************************
     * Datalogger stuff
     * ****************************/

    private File logFile = null;


    private SimpleDateFormat sdf = new SimpleDateFormat(MainActivity.getStringSingle(R.string.format_YMDHMS), Locale.getDefault());

    private boolean isCreated()
    {
        return (logFile!=null);
    }

    private boolean createNewLog() {
        boolean result = false;

        //debug(this.getClass().getSimpleName()+": create new field logfile");

        // ensure that there is a CanZE Folder in SDcard
        String file_path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/CanZE/";

        //debug(this.getClass().getSimpleName()+": file_path:" + file_path);

        File dir = new File(file_path);
        if (!dir.exists()) {
            dir.mkdirs();
            //debug(this.getClass().getSimpleName()+": SDcard dir CanZE created");
        }

        String exportdataFileName = file_path + "field-" + sdf.format(Calendar.getInstance().getTime()) + ".log";

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

            log("time,SID,value");
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
        if(logFile==null) createNewLog();

        try {
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(logFile, true));
            bufferedWriter.append(sdf.format(Calendar.getInstance().getTime())+","+text+"\n");
            bufferedWriter.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

}

