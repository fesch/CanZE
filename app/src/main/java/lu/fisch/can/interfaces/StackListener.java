/*
 * This class represents a stack listener.
 */
package lu.fisch.can.interfaces;

import lu.fisch.can.Frame;

/**
 *
 * @author robertfisch
 */
public interface StackListener {
    public void onFrameCompleteEvent(Frame frame);
}
