/*
 * Decodes a frame in GVRET (protocol, not the obsolete logging) format.
 */
package lu.fisch.can.decoders;

import java.util.ArrayList;

import lu.fisch.can.Frame;

/**
 *
 * @author robertfisch
 */
public class GVRET implements Decoder {


    public Frame decodeFrame(int[] bin) {
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

    private int[] buffer = new int[0];

    @Override
    public Frame decodeFrame(String line) {
        // not possible for this decoder
        return null;
    }

    @Override
    public ArrayList<Frame> process(int[] input) {
        ArrayList<Frame> result = new ArrayList<>();

        // add bytes to buffer
        int[] newBuffer = new int[buffer.length+input.length];
        for(int i=0; i<buffer.length; i++) newBuffer[i]=buffer[i];
        for(int i=0; i<input.length; i++) newBuffer[buffer.length+i]=input[i];
        buffer=newBuffer;

        // TODO: find frames ...

        return result;
    }
    
    /* --------------------------------
     * Tests ...
     \ ------------------------------ */

    public static void main(String[] args)
    {
        Frame f = (new GVRET()).decodeFrame(new int[]{
                0xf1, 0,                         // receive frame
                0x02, 0x01, 0x00, 0x00,          // timestamp
                0xbb, 0x07, 0x00, 0x00,          // id
                0x08,                            // length & bus
                0x10, 0x66, 0x61, 0x67,          // data
                0xf0, 0xf0, 0xf0, 0xf0});
        System.out.println(f);
    }

}
