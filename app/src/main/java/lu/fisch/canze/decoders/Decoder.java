/*
 * The interface any decoder class has to implement.
 */
package lu.fisch.canze.decoders;

import java.util.ArrayList;

import lu.fisch.canze.actors.Frame;

/**
 *
 * @author robertfisch
 */
public interface Decoder {
    public Frame decodeFrame(String line);
    public ArrayList<Frame> process(int[] input);
}
