package lu.fisch.canze.readers;


import java.util.ArrayList;

import lu.fisch.canze.MainActivity;
import lu.fisch.canze.actors.Field;
import lu.fisch.canze.actors.Stack;
import lu.fisch.canze.bluetooth.ConnectedBluetoothThread;

/**
 * Created by robertfisch on 27.08.2015.
 */
public abstract class DataReader {
    // the list of filters, e.g. a list with the hex-values of the ID of the frames we need to request
    protected ArrayList<Field> fields = new ArrayList<>();

    // the thread in charge of reading from the Bluetooth
    protected ConnectedBluetoothThread connectedBluetoothThread = null;

    // the stack that will process the incoming data
    protected Stack stack = null;

    // default constructor
    public DataReader(Stack stack)
    {
        this.stack=stack;
    }

    // initialise the connection
    // (specific to each reader)
    public abstract void initConnection();

    // method to register a filter
    // (specific to each reader)
    public abstract void registerFilter(String filter);

    public void registerFilters()
    {
        synchronized (fields) {
            for (int i = 0; i < fields.size(); i++) {
                registerFilter(fields.get(i).getHexId());
            }
        }
    }

    // clean filters
    public void clearFields()
    {
        synchronized (fields) {
            fields.clear();
        }
    }

    // add a given filter
    public void addField(Field field)
    {
        synchronized (fields) {
            if (!fields.contains(field)) {
                fields.add(field);
                registerFilter(field.getHexId());
            }
            //MainActivity.debug("Filters = "+filters.toString());
        }
    }

    // remove a given filter
    public void removeField(Field field)
    {
        synchronized (fields) {
            fields.remove(field);
        }
    }

    // get the Bluetooth thread
    public ConnectedBluetoothThread getConnectedBluetoothThread() {
        return connectedBluetoothThread;
    }

    // set the Bluetooth thread
    public void setConnectedBluetoothThread(ConnectedBluetoothThread connectedBluetoothThread) {
        this.connectedBluetoothThread = connectedBluetoothThread;
        // init the connection
        initConnection();
        // clean all filters
        clearFields();
        // register all filters
        registerFilters();
    }


}
