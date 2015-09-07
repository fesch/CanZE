/*
 * Decodes a frame in CRDT format.
 */
package lu.fisch.canze._old.decoders;

import java.util.ArrayList;

import lu.fisch.canze.actors.Message;
import lu.fisch.canze.actors.Utils;

/**
 *
 * @author robertfisch
 */
public class CRDT implements Decoder {

    private String buffer = "";
    private final String separator = "\n";

    @Override
    public Message decodeFrame(String text) {
        // split up the text
        String[] fields = text.split(" ");
        if(fields.length>=3) {
            // get the timestamp
            long timestamp = Long.parseLong(fields[0]);
            // get the id
            int id = Integer.parseInt(fields[2], 16);
            // get the data (concatenate it)
            String hexData = "";
            for (int i = 3; i < fields.length; i++)
                hexData += fields[i];
            int[] data = Utils.toIntArray(hexData.trim());
            // create & return a new frame
            return new Message(id, timestamp, data);
        }
        return null;
    }

    @Override
    public ArrayList<Message> process(int[] input) {
        ArrayList<Message> result = new ArrayList<>();

        // add to buffer as characters
        for(int i=0; i<input.length; i++)
            buffer+= (char) input[i];

        // split by <new line>
        String[] messages = buffer.split(separator);
        // let assume the last message is fine
        int last = messages.length;
        // but if it is not, do not consider it
        if(!buffer.endsWith(separator)) last--;
        // process each message
        for(int i=0; i<last; i++)
        {
            // decode into a frame
            Message message = decodeFrame(messages[i].trim());
            // store if valid
            if(message !=null)
                result.add(message);
        }
        // adapt the buffer
        if(!buffer.endsWith(separator))
            // retain the last uncompleted message
            buffer=messages[messages.length-1];
        else
            // empty the entire buffer
            buffer="";
        // we are done
        return result;
    }
    
    /* --------------------------------
     * Tests ...
     \ ------------------------------ */

    public static void main(String[] args)
    {
        Message f = (new CRDT()).decodeFrame("0 R11 7bb 10 66 61 67 f0 f0 f0 f0");
        System.out.println(f);
    }
    
}
