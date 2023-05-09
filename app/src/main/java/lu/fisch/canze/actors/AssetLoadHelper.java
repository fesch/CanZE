package lu.fisch.canze.actors;


import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.content.Context;
import android.content.res.AssetManager;

import lu.fisch.canze.activities.MainActivity;
import lu.fisch.canze.classes.Crashlytics;

public class AssetLoadHelper {

    public static BufferedReader getBufferedReaderFromAsset (String asset) {
        try {
            Context myContext = MainActivity.getInstance().getApplicationContext();
            // call getAssets() on the private Context myContext
            if (myContext == null) return null;
            AssetManager assetmanager = myContext.getAssets();
            if (assetmanager == null) return null;
            InputStream inputStream = assetmanager.open(asset);

            // BufferedReader handles encoding, default is UTF-8, which is what we use
            InputStreamReader inputStreamReader = new InputStreamReader (inputStream);
            return new BufferedReader (inputStreamReader);

        } catch (FileNotFoundException e) {
            // do nothing, so return null
        } catch (IOException | NullPointerException e) {
            Crashlytics.logString("loading asset:[" + asset + "]");
            Crashlytics.logException(e);
        } // Catching null is bad practice, but I have seen one thrown by myContext.getAssets();

        return null;
    }
}
