package lu.fisch.canze.devices;

import java.util.ArrayList;

import lu.fisch.canze.MainActivity;
import lu.fisch.canze.actors.Field;
import lu.fisch.canze.actors.Fields;
import lu.fisch.canze.actors.Message;
import lu.fisch.canze.bluetooth.ConnectedBluetoothThread;

/**
 * This class defines an abstract device. It has to manage the device related
 * decoding of the incoming data as well as the data flow to the device or
 * whatever is needed to "talk" to it.
 *
 * Created by robertfisch on 07.09.2015.
 */

public abstract class Device {

    /* ----------------------------------------------------------------
     * Attributes
     \ -------------------------------------------------------------- */

    /**
     * A device will "monitor" or "request" a given number of fields from
     * the connected CAN-device, so this is the list of all fields that
     * have to be read and updated.
     */
    protected ArrayList<Field> fields = new ArrayList<>();

    /**
     * The connected Bluetooth thread is being used to read and write
     * to the Bluetooth serial connection. It's actually just a wrapper
     * class around some streams.
     */
    protected ConnectedBluetoothThread connectedBluetoothThread = null;



    /* ----------------------------------------------------------------
     * Abstract methods (to be implemented in each "real" device)
     \ -------------------------------------------------------------- */

    /**
     * A device may need some initialisation before data can be requested.
     */
    public abstract void initConnection();

    /**
     * Ass the CAN bus sends a lot of free frames, the device may want
     * to apply a filter. This method should thus register or apply a
     * given filter to the hardware.
     * @param frameId   the ID of the frame to filter for
     */
    public abstract void registerFilter(int frameId);

    /**
     * Method to unregister a filter.
     * @param frameId   the ID of the frame to no longer filter on
     */
    public abstract void unregisterFilter(int frameId);

    /**
     * This method will process the passed (binary) data and then
     * return based on that and on what the internal buffer still
     * may hold a list of complete messages.
     * @param input
     * @return
     */
    protected abstract ArrayList<Message> processData(int[] input);

    public abstract void join() throws InterruptedException;


    /* ----------------------------------------------------------------
     * Methods (that will be inherited by any "real" device)
     \ -------------------------------------------------------------- */

    /**
     * This method will process the passed (binary) data and notify
     * the fields (=singleton) about the incoming messages. The
     * listeners of the fields will then pass this information, via their
     * own listeners to the GUI or whoever needs to know about the changes.
     * @param input
     */
    public void process(final int[] input)
    {
        /*(new Thread(new Runnable() {
            @Override
            public void run() {
                ArrayList<Message> messages = processData(input);
                for(int i=0; i<messages.size(); i++)
                {
                    Fields.getInstance().onMessageCompleteEvent(messages.get(i));
                }
            }
        })).start();
        /**/
        ArrayList<Message> messages = processData(input);
        for(int i=0; i<messages.size(); i++)
        {
            Fields.getInstance().onMessageCompleteEvent(messages.get(i));
        }
        /**/
    }

    /**
     * This method registers the IDs of all monitored fields.
     */
    public void registerFilters()
    {
        // another thread my also access the list of monitored fields,
        // so we need to "protect" it against simultaneous changes.
        synchronized (fields) {
            for (int i = 0; i < fields.size(); i++) {
                registerFilter(fields.get(i).getId());
            }
        }
    }

    /**
     * This method unregisters all filters from the remote device
     */
    public void unregisterFilters()
    {
        synchronized (fields) {
            for (int i = 0; i < fields.size(); i++) {
                unregisterFilter(fields.get(i).getId());
            }
        }
    }

    /**
     * This method clears the list of monitored fields.
     */
    public void clearFields()
    {
        synchronized (fields) {
            fields.clear();
            //MainActivity.debug("cleared");
            // launch the filter clearing asynchronously
            (new Thread(new Runnable() {
                @Override
                public void run() {
                    unregisterFilters();
                }
            })).start();
        }
    }

    /**
     * A CAN message will trigger updates for all connected fields, meaning
     * any field with the same ID and the same responseID will be updated.
     * For this reason we don't need to query these fields multiple times
     * in one turn.
     * @param _field    the field to be tested
     * @return
     */
    private boolean containsField(Field _field)
    {
        for(int i=0; i<fields.size(); i++)
        {
            Field field = fields.get(i);
            if(field.getId()==_field.getId() && field.getResponseId().equals(_field.getResponseId()))
                return true;
        }
        return false;
    }

    /**
     * Method to add a field to the list of monitored field.
     * The field is also immediately registered onto the device.
     * @param field the field to be added
     */
    public void addField(final Field field)
    {
        synchronized (fields) {
            if (!containsField(field)) {
                //MainActivity.debug("reg: "+field.getSID());
                fields.add(field);
                // launch the field registration asynchronously
                (new Thread(new Runnable() {
                    @Override
                    public void run() {
                        registerFilter(field.getId());
                    }
                })).start();
            }
        }
    }

    /**
     * This method removes a field from the list of monitored fields
     * and unregisters the corresponding filter.
     * @param field
     */
    public void removeField(final Field field)
    {
        synchronized (fields) {
            if(fields.remove(field))
            {
                // launch the field registration asynchronously
                (new Thread(new Runnable() {
                    @Override
                    public void run() {
                        unregisterFilter(field.getId());
                    }
                })).start();
            }
        }
    }

    /* ----------------------------------------------------------------
     * Methods (that will be inherited by any "real" device)
     \ -------------------------------------------------------------- */

    public ConnectedBluetoothThread getConnectedBluetoothThread()
    {
        return connectedBluetoothThread;
    }

    public void setConnectedBluetoothThread(ConnectedBluetoothThread connectedBluetoothThread) {
        setConnectedBluetoothThread(connectedBluetoothThread,true);
    }

    public void setConnectedBluetoothThread(ConnectedBluetoothThread connectedBluetoothThread, boolean reset) {
        if(connectedBluetoothThread==null)
            MainActivity.debug("Device: nulling out connectedBluetoothThread");
        this.connectedBluetoothThread = connectedBluetoothThread;
        if(connectedBluetoothThread!=null)
        {
            // init the connection
            initConnection();

            if(reset) {
                // clean all filters (just to make sure)
                clearFields();
                // register all filters (if there are any)
                registerFilters();
            }
        }
    }
}
