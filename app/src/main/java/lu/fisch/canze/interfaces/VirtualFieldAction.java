package lu.fisch.canze.interfaces;

import java.util.ArrayList;
import java.util.HashMap;

import lu.fisch.canze.actors.Field;

/**
 * Created by robertfisch on 15.11.2015.
 */
public interface VirtualFieldAction {
    double updateValue(HashMap<String,Field> dependantFields);
}
