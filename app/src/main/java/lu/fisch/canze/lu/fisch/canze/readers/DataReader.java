package lu.fisch.canze.lu.fisch.canze.readers;


import android.os.Handler;

import java.util.ArrayList;

import lu.fisch.can.Stack;
import lu.fisch.canze.ConnectedBluetoothThread;

/**
 * Created by robertfisch on 27.08.2015.
 */
public abstract class DataReader extends Handler{

    protected ArrayList<String> filters = new ArrayList<>();
    protected ConnectedBluetoothThread connectedBluetoothThread = null;
    protected Stack stack = null;

    public DataReader(Stack stack)
    {
        this.stack=stack;
    }

    public abstract void registerFilter(String filter);

    public void registerFilters()
    {
        for(int i=0; i<filters.size(); i++)
        {
            registerFilter(filters.get(i));
        }
    }

    public void clearFilters()
    {
        filters.clear();
    }

    public void addFilter(String filter)
    {
        filters.add(filter);
    }

    public void removeFilter(String filter)
    {
        filters.remove(filter);
    }

    public ConnectedBluetoothThread getConnectedBluetoothThread() {
        return connectedBluetoothThread;
    }

    public void setConnectedBluetoothThread(ConnectedBluetoothThread connectedBluetoothThread) {
        this.connectedBluetoothThread = connectedBluetoothThread;
        initConnection();
        clearFilters();
    }

    public abstract void initConnection();

}
