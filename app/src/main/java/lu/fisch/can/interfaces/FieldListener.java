/*
 * This class represents a stack listener.
 */
package lu.fisch.can.interfaces;

import lu.fisch.can.Field;

/**
 *
 * @author robertfisch
 */
public interface FieldListener {
    public void onFieldUpdateEvent(Field field);
}
