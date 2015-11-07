package lu.fisch.canze.classes;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.os.Handler;
import android.os.Environment;

/**
 * Created by Chris Mattheis on 03/11/15.
 * don't use yet - still work in progress
 */
public class DataLogger {

    private File logFile;
    private FileWriter fr = null;
    private BufferedWriter br = null;


    private long intervall = 5000;

    private Handler handler = new Handler();

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            // write data to file
            String data = "Zeile";
            String dataWithNewLine=data+System.getProperty("line.separator");

            try {
                fr.write (dataWithNewLine);
            }
            catch (IOException e) {
                e.printStackTrace();
            }

            handler.postDelayed(this, intervall);
        }
    };


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

        // File logFile = new File(exportdataFileName);
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
            // BufferedWriter buf = new BufferedWriter(new FileWriter(logFile, true));
            fr = new FileWriter(logFile, true);
            br = new BufferedWriter(fr);
            // set global static BufferedWriter dataexportStream later
            if (true) {
                br.append("this is just a test if stream is writeable");
                br.newLine();
                br.close();
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
        try {
            br.close();
            fr.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    public void onCreate() {
        handler.postDelayed(runnable, intervall);
    }

    public void onDestroy() {
        handler.removeCallbacks(runnable);

    }

    public void onPause() {
        handler.removeCallbacks(runnable);

    }

    public void onResume() {

    }
}

