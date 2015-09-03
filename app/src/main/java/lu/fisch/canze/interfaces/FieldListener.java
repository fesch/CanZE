/*
 * This class represents a stack listener.
 */
package lu.fisch.canze.interfaces;

import lu.fisch.canze.actors.Field;

/**
 *
 * @author robertfisch
 */
public interface FieldListener {
    public void onFieldUpdateEvent(Field field);
}
