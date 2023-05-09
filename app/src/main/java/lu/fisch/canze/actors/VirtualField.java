package lu.fisch.canze.actors;

import java.util.Collection;
import java.util.HashMap;

import lu.fisch.canze.interfaces.FieldListener;
import lu.fisch.canze.interfaces.VirtualFieldAction;

/**
 * Created by robertfisch on 15.11.2015.
 */
public class VirtualField extends Field implements FieldListener {

    // the list of fields this field depends on
    private final HashMap<String,Field> dependantFields;

    // the method to be executed for the calculation of this field
    private final VirtualFieldAction virtualFieldAction;


    public VirtualField(String responseId, HashMap<String,Field> dependantFields, int decimals, String unit, VirtualFieldAction virtualFieldAction)
    {
        // virtual frame added in the initialization block
        // super(Frames.getInstance().createVirtualIfNotExists(id), 0, 0, 1, 1, 0, unit, "", "", 0);
        // We're creating a new Field, frame 800, bit position 24-31, resolution 1, decimals 0, offset 0, given unit, empty requestId, given responseId, generic car
        super("", Frames.getInstance().getById(0x800), (short)24, (short)31, 1, 0, 0, unit, responseId, (short)0, null, null);

        // register dependant listeners
        for (Field field : dependantFields.values()) {
            if(field!=null)
                field.addListener(this);
        }

        this.dependantFields    = dependantFields;
        this.virtualFieldAction = virtualFieldAction;
        this.virtual            = true;
        this.setUnit(unit);
        this.setDecimals(decimals);
    }

    @Override
    public void onFieldUpdateEvent(Field field) {
        if(virtualFieldAction!=null) {
            setValue(virtualFieldAction.updateValue(dependantFields));
        }
    }

    @Override
    public void removeListener(FieldListener fieldListener)
    {
        // remove our listener
        super.removeListener(fieldListener);

        // remove listeners to dependant listeners
        for (Field field : dependantFields.values()) {
            fieldListeners.remove(this);
        }
    }

    public Collection<Field> getFields()
    {
        return dependantFields.values();
    }
}
