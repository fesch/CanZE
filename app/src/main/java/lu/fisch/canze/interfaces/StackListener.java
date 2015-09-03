/*
 * This class represents a stack listener.
 */
package lu.fisch.canze.interfaces;

import lu.fisch.canze.actors.Frame;

/**
 *
 * @author robertfisch
 */
public interface StackListener {
    public void onFrameCompleteEvent(Frame frame);
}
