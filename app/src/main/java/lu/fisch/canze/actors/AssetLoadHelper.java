package lu.fisch.canze.actors;


import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.content.Context;
import android.content.res.AssetManager;

import lu.fisch.canze.activities.MainActivity;

public class AssetLoadHelper {

    // calling getAssets() from outside an Activity, source:
    // http://stackoverflow.com/questions/11199900/using-getassets-outside-an-activity

    private Context myContext = null; //<-- declare a Context reference
    private AssetManager myAssetManager = null; //<-- declare a Context reference

    public AssetLoadHelper() {
        super(); // silences "This class should provide a default constructor" warning
        // src: http://stackoverflow.com/questions/24817662/error-this-class-should-provide-a-default-constructor-a-public-constructor-wit
    }

    public AssetLoadHelper (Context context) {
        myContext = context; //<-- fill it with the Context you are passed
    }

    public AssetLoadHelper (AssetManager assetManager) {
        myAssetManager = assetManager; //<-- fill it with the Context you are passed
    }

    public BufferedReader getBufferedReaderFromAsset (String asset) {
        try {
            // call getAssets() on the private Context myContext
            InputStream inputStream;
            if (myContext != null) {
                AssetManager assetmanager = myContext.getAssets();
                inputStream = assetmanager.open(asset);
            } else {
                inputStream = myAssetManager.open(asset);
            }

            // BufferedReader handles encoding, default is UTF-8, which is what we use
            //BufferedReader bufferedReader = new BufferedReader (new InputStreamReader (inputStream));
            //return bufferedReader;
            return new BufferedReader (new InputStreamReader (inputStream));

        } catch (FileNotFoundException e) {
            // do nothing, so return null
        } catch (IOException | NullPointerException e) {
            MainActivity.logExceptionToCrashlytics(e);
        } // Catching null is bad practive, but I have seen one thrown by myContext.getAssets();

        return null;
    }
}
