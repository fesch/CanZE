/*
 * Decodes a frame in GVRET (protocol, not the obsolete logging) format.
 */
package lu.fisch.can.decoders;

import lu.fisch.can.Frame;
import lu.fisch.can.MultiFrame;
import static lu.fisch.can.decoders.Utils.toByteArray;

/**
 *
 * @author robertfisch
 */
public class GVRET implements Decoder {


    public Frame decode(int[] bin) {
        // split up the text
        if(bin[0] == 0xf1 && bin[1] == 0x00 && bin[10] > 0) {
            // get the timestamp
            long timestamp = bin[2] + bin[3] * 0x100 + bin[4] * 0x10000 + bin[5] * 0x1000000;
            // get the id
            int id =  bin[6] + bin[7] * 0x100; // ignore 29 bit, not relevant for the Zoe / Fluence anyway
            int length = bin[10] & 0x0f;
            int[] data = new int [length];
            for (int i = 0; i < length; i++)
                data[i] += bin[i+10];
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
        Frame f = (new GVRET()).decode(new int[]{
            0xf1, 0,                         // receive frame
            0x02, 0x01, 0x00, 0x00,          // timestamp
            0xbb, 0x07, 0x00, 0x00,          // id
            0x08,                            // length & bus
            0x10, 0x66, 0x61, 0x67,          // data
            0xf0, 0xf0, 0xf0, 0xf0});
        System.out.println(f);
    }

    @Override
    public Frame decode(String text) {
        return null;
    }
}
