/*
 * Decodes a frame in the condensed format Bob uses.
 */
package lu.fisch.canze.decoders;

import java.util.ArrayList;

import lu.fisch.canze.actors.Frame;
import lu.fisch.canze.MainActivity;

/**
 *
 * @author robertfisch
 */
public class BOB implements Decoder {

    private String buffer = "";
    private final String separator2 = "\\|"; // \r\n
    private final String separator = "\r\n";

    @Override
    public Frame decodeFrame(String text) {
        // split up the fields
        String[] pieces = text.split(",");
        if(pieces.length==2) {
            try {
                // get the id
                int id = Integer.parseInt(pieces[0], 16);
                // get the data
                int[] data = Utils.toIntArray(pieces[1].trim());
                // create and return new frame
                return new Frame(id, data);
            }
            catch(Exception e)
            {
                MainActivity.debug("BAD: "+text);
                return null;
            }
        }
        else if(pieces.length==3) {
            try {
                // get the id
                int id = Integer.parseInt(pieces[0], 16);
                // get the data
                int[] data = Utils.toIntArray(pieces[1].trim());
                // get checksum
                int chk = Integer.parseInt(pieces[2].trim(), 16);
                int check = 0;
                for(int i=0; i<data.length; i++)
                    check ^= data[i];
                // validate the checksum
                if(chk==check)
                    // create and return new frame
                    return new Frame(id, data);
            }
            catch(Exception e)
            {
                MainActivity.debug("BAD: "+text);
                return null;
            }
        }
        MainActivity.debug("BAD: "+text);
        return null;
    }

    @Override
    public ArrayList<Frame> process(int[] input) {
        ArrayList<Frame> result = new ArrayList<>();

        // add to buffer as characters
        for (int i = 0; i < input.length; i++) {
            buffer += (char) input[i];
        }

        // split by <new line>
        String[] messages = buffer.split(separator);
        // let assume the last message is fine
        int last = messages.length;
        // but if it is not, do not consider it
        if (!buffer.endsWith(separator)) last--;

        // process each message
        for (int i = 0; i < last; i++) {
            // decode into a frame
            Frame frame = decodeFrame(messages[i].trim());
            // store if valid
            if (frame != null)
                result.add(frame);
        }
        // adapt the buffer
        if (!buffer.endsWith(separator))
            // retain the last uncompleted message
            buffer = messages[messages.length - 1];
        else
            // empty the entire buffer
            buffer = "";
        // we are done

        return result;
    }
    
    /* --------------------------------
     * Tests ...
     \ ------------------------------ */

    public static void main(String[] args)
    {
        Frame f = (new BOB()).decodeFrame("7bb,10666167f0f0f0f0");
        System.out.println(f);
    }
    
}
