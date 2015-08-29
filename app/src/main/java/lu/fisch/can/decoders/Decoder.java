/*
 * The interface any decoder class has to implement.
 */
package lu.fisch.can.decoders;

import java.util.ArrayList;

import lu.fisch.can.Frame;

/**
 *
 * @author robertfisch
 */
public interface Decoder {
    public Frame decodeFrame(String line);
    public ArrayList<Frame> process(int[] input);
}
