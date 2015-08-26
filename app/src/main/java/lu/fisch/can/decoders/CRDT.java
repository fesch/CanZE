/*
 * Decodes a frame in CRDT format.
 */
package lu.fisch.can.decoders;

import lu.fisch.can.Frame;
import lu.fisch.can.MultiFrame;
import static lu.fisch.can.decoders.Utils.toByteArray;

/**
 *
 * @author robertfisch
 */
public class CRDT implements Decoder {

    @Override
    public Frame decode(String text) {
        // split up the text
        String[] fields = text.split(" ");
        if(fields.length>=4) {
            // get the timestamp
            long timestamp = Long.parseLong(fields[0]);
            // get the id
            int id = Integer.parseInt(fields[2], 16);
            // get the data (concatenate it)
            String hexData = "";
            for (int i = 3; i < fields.length; i++)
                hexData += fields[i];
            int[] data = toByteArray(hexData.trim());
            // create & return a new frame
            return new Frame(id, timestamp, data);
        }
        return null;
    }
    
    /* --------------------------------
     * Tests ...
     \ ------------------------------ */

    public static void main(String[] args)
    {
        Frame f = (new CRDT()).decode("0 R11 7bb 10 66 61 67 f0 f0 f0 f0");
        System.out.println(f);
    }
    
}
