/*
 * The interface any decoder class has to implement.
 */
package lu.fisch.canze._old.decoders;

import java.util.ArrayList;

import lu.fisch.canze.actors.Message;

/**
 *
 * @author robertfisch
 */
public interface Decoder {
    public Message decodeFrame(String line);
    public ArrayList<Message> process(int[] input);
}
