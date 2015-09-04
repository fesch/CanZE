package lu.fisch.canze.readers;


import java.util.ArrayList;

import lu.fisch.canze.actors.Stack;
import lu.fisch.canze.bluetooth.ConnectedBluetoothThread;

/**
 * Created by robertfisch on 27.08.2015.
 */
public abstract class DataReader {
    // the list of filters, e.g. a list with the hex-values of the ID of the frames we need to request
    protected ArrayList<String> filters = new ArrayList<>();

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
        for(int i=0; i<filters.size(); i++)
        {
            registerFilter(filters.get(i));
        }
    }

    // clean filters
    public void clearFilters()
    {
        filters.clear();
    }

    // add a given filter
    public void addFilter(String filter)
    {
        if(!filters.contains(filter)) {
            filters.add(filter);
            registerFilter(filter);
        }
    }

    // remove a given filter
    public void removeFilter(String filter)
    {
        filters.remove(filter);
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
        clearFilters();
        // register all filters
        registerFilters();
    }


}
