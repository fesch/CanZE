/*
 * The interface any decoder class has to implement.
 */
package lu.fisch.can.decoders;

import lu.fisch.can.Frame;

/**
 *
 * @author robertfisch
 */
public interface Decoder {
    public Frame decode(String text);
}
