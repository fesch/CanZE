package lu.fisch.canze.classes;

import android.os.Environment;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by Chris Mattheis on 03/11/15.
 * don't use yet - still work in progress
 */
public class DataLogger {

    public DataLogger () {}

    public boolean start() {
        boolean result = false;

        // ensure that there is a CanZE Folder in SDcard
        String file_path = Environment.getExternalStorageDirectory().getAbsolutePath() + "CanZE";
        File dir = new File(file_path);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HHmmss");
        String exportdataFileName = file_path + sdf.format(cal.getTime());

        File logFile = new File(exportdataFileName);
        if (!logFile.exists()) {
            try {
                logFile.createNewFile();
            }
            catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        try {
            //BufferedWriter for performance, true to set append to file flag
            BufferedWriter buf = new BufferedWriter(new FileWriter(logFile, true));
            // set global static BufferedWriter dataexportStream later
            if (true) {
                buf.append("this is just a test if stream is writeable");
                buf.newLine();
                buf.close();
            }
            result = true;
        }
        catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return result;
    }
    public boolean stop() {
        boolean result = false;
        return result;
    }
}

