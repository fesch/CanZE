/*
 * This class represents a stack listener.
 */
package lu.fisch.canze.interfaces;

import lu.fisch.canze.actors.Message;

/**
 *
 * @author robertfisch
 */
public interface MessageListener {
    public void onMessageCompleteEvent(Message message);
}
