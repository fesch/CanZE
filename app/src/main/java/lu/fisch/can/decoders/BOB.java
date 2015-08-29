/*
 * Decodes a frame in the condensed format Bob uses.
 */
package lu.fisch.can.decoders;

import android.util.Log;

import lu.fisch.can.Frame;
import lu.fisch.can.MultiFrame;
import lu.fisch.canze.MainActivity;

import static lu.fisch.can.decoders.Utils.toByteArray;

/**
 *
 * @author robertfisch
 */
public class BOB implements Decoder {

    @Override
    public Frame decode(String text) {
        // split up the fields
        String[] pieces = text.split(",");
        if(pieces.length>=2) {
            try {
                // get the id
                int id = Integer.parseInt(pieces[0], 16);
                // get the data
                int[] data = toByteArray(pieces[1].trim());
                // create and return new frame
                Frame frame = new Frame(id, data);

                if(pieces.length>=3)
                    frame.setRate(Double.valueOf(pieces[2]));

                return frame;
            }
            catch(Exception e)
            {
                //e.printStackTrace();
                //Log.d(MainActivity.TAG,"Problematic line while decoding: "+text);
            }
        }
        return null;
    }
    
    /* --------------------------------
     * Tests ...
     \ ------------------------------ */

    public static void main(String[] args)
    {
        Frame f = (new BOB()).decode("7bb,10666167f0f0f0f0");
        System.out.println(f);
    }
    
}
