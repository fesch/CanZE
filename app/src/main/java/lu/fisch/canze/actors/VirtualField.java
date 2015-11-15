package lu.fisch.canze.actors;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import lu.fisch.canze.interfaces.FieldListener;
import lu.fisch.canze.interfaces.VirtualFieldAction;

/**
 * Created by robertfisch on 15.11.2015.
 */
public class VirtualField extends Field implements FieldListener {

    // the list of fields this field depends on
    protected HashMap<String,Field> dependantFields = new HashMap<>();

    // the method to be executed for the calculation of this field
    protected VirtualFieldAction virtualFieldAction = null;


    public VirtualField(int id, HashMap<String,Field> dependantFields, String unit, VirtualFieldAction virtualFieldAction)
    {
        // virtual frame added in the initialization block
        super(Frames.getInstance().createVirtualIfNotExists(id), 0, 0, 1, 1, 0, unit, "", "", 0);

        // regiester dependant listeners
        for (Field field : dependantFields.values()) {
            field.addListener(this);
        }

        this.dependantFields    = dependantFields;
        this.virtualFieldAction = virtualFieldAction;
        this.virtual            = true;
    }

    @Override
    public void onFieldUpdateEvent(Field field) {
        if(virtualFieldAction!=null) {
            setValue(virtualFieldAction.updateValue(dependantFields));
        }
    }

    public Collection<Field> getFields()
    {
        return dependantFields.values();
    }
}
