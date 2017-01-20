/*
    CanZE
    Take a closer look at your ZE car

    Copyright (C) 2015 - The CanZE Team
    http://canze.fisch.lu

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or any
    later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

package lu.fisch.canze.devices;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;

import lu.fisch.canze.activities.MainActivity;
import lu.fisch.canze.actors.Field;
import lu.fisch.canze.actors.Frame;
import lu.fisch.canze.actors.Message;
import lu.fisch.canze.actors.VirtualField;
import lu.fisch.canze.bluetooth.BluetoothManager;
import lu.fisch.canze.database.CanzeDataSource;

/**
 * This class defines an abstract device. It has to manage the device related
 * decoding of the incoming data as well as the data flow to the device or
 * whatever is needed to "talk" to it.
 *
 * Created by robertfisch on 07.09.2015.
 */

public abstract class Device {

    public static final int TOUGHNESS_HARD              = 0;    // hardest reset possible (ie atz)
    public static final int TOUGHNESS_MEDIUM            = 1;    // medium reset (i.e. atws)
    public static final int TOUGHNESS_SOFT              = 2;    // softest reset (i.e atd for ELM)
    public static final int TOUGHNESS_NONE              = 100;  // just clear error status

    private final double minIntervalMultiplicator       = 1.3;
    private final double maxIntervalMultiplicator       = 2.5;
    double intervalMultiplicator                        = minIntervalMultiplicator;

    /* ----------------------------------------------------------------
     * Attributes
     \ -------------------------------------------------------------- */

    /**
     * A device will "monitor" or "request" a given number of fields from
     * the connected CAN-device, so this is the list of all fields that
     * have to be read and updated.
     */
    protected final ArrayList<Field> fields = new ArrayList<>();
    /**
     * Some fields will be custom, activity based
     */
    private ArrayList<Field> activityFieldsScheduled = new ArrayList<>();
    private ArrayList<Field> activityFieldsAsFastAsPossible = new ArrayList<>();
    /**
     * Some other fields will have to be queried anyway,
     * such as e.g. the speed --> safe mode driving
     */
    private ArrayList<Field> applicationFields = new ArrayList<>();

    /**
     * The index of the actual field to query.
     * Loops over ther "fields" array
     */
    //protected int fieldIndex = 0;

    private int activityFieldIndex = 0;

    private boolean pollerActive = false;
    Thread pollerThread;

    /**
     * lastInitProblem should be filled with a descriptive problem description by the initDevice implementation. In normal operation we don't care
     * because a device either initializes or not, but for testing a new device this can be very helpful.
     */
    String lastInitProblem = "";

    /* ----------------------------------------------------------------
     * Abstract methods (to be implemented in each "real" device)
     \ -------------------------------------------------------------- */

    /**
     * A device may need some initialisation before data can be requested.
     */
    public void initConnection()
    {
        MainActivity.debug("Device: initConnection");

        if(BluetoothManager.getInstance().isConnected()) {
            MainActivity.debug("Device: BT connected");
            // make sure we only have one poller task
            if (pollerThread == null) {
                MainActivity.debug("Device: pollerThread == null");
                // post a task to the UI thread
                setPollerActive(true);

                Runnable r = new Runnable() {
                    @Override
                    public void run() {
                        // if the device has been initialised and we got an answer
                        if(initDevice(TOUGHNESS_HARD)) {
                            while (isPollerActive()) {
                                MainActivity.debug("Device: inside poller thread");
                                if (applicationFields.size()+activityFieldsScheduled.size()+activityFieldsAsFastAsPossible.size() == 0
                                        || !BluetoothManager.getInstance().isConnected()) {
                                    MainActivity.debug("Device: sleeping");
                                    try {
                                        if(isPollerActive())
                                            Thread.sleep(5000);
                                        else return;
                                    } catch (Exception e) {
                                        // ignore a sleep exception
                                    }
                                }
                                // query a field
                                else {
                                    if(isPollerActive())
                                    {
                                        MainActivity.debug("Device: Doing next query ...");
                                        queryNextFilter();
                                    }
                                    else return;
                                }
                            }
                            // dereference the poller thread (it i stopped now anyway!)
                            MainActivity.debug("Device: Poller is done");
                            pollerThread = null;
                        }
                        else
                        {
                            MainActivity.debug("Device: no answer from device");

                            // first check if we have not yet been killed!
                            if(isPollerActive()) {
                                MainActivity.debug("Device: --- init failed ---");
                                // drop the BT connexion and try again
                                (new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        // stop the BT but don't reset the device registered fields
                                        MainActivity.getInstance().stopBluetooth(false);
                                        // reload the BT with filter registration
                                        MainActivity.getInstance().reloadBluetooth(false);
                                        //BluetoothManager.getInstance().connect();
                                    }
                                })).start();
                            }
                        }
                    }
                };
                pollerThread = new Thread(r);
                // start the thread
                pollerThread.start();
            }
        }
        else
        {
            MainActivity.debug("Device: BT not connected");
            if(pollerThread!=null && pollerThread.isAlive())
            {
                setPollerActive(false);
                try {
                    MainActivity.debug("Device: joining pollerThread");
                    pollerThread.join();
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }
    }

    // query the device for the next filter
    private void queryNextFilter()
    {
        if (applicationFields.size()+activityFieldsScheduled.size()+activityFieldsAsFastAsPossible.size() > 0)
        {
            try {

                Field field = getNextField();

                if(field == null) {
                    MainActivity.debug("Device: got no next field --> sleeping");
                    // no next field ---> sleep
                    try {
                        Thread.sleep(200);
                    } catch(Exception e) {
                        // ignore a sleep exception
                    }
                    return;
                }
                else
                {
                    // long start = Calendar.getInstance().getTimeInMillis();
                    MainActivity.debug("Device: queryNextFilter: " + field.getSID());
                    MainActivity.getInstance().dropDebugMessage(field.getSID());

                    // get the data
                    Message message = requestFrame(field.getFrame());

                    // test if we got something
                    if(!message.isError()) {
                        //Fields.getInstance().onMessageCompleteEvent(message);
                        message.onMessageCompleteEvent();
                    } else {
                        // one plain retry
                        message = requestFrame(field.getFrame());
                        if(!message.isError()) {
                            message.onMessageCompleteEvent();
                        } else {
                            // failed after single retry
                            // mark underlying fields as uodated to avoid queue clogging
                            // the will have to get backk to the end of the queue
                            message.onMessageIncompleteEvent();
                            // reset if something went wrong ...
                            // ... but only if we are not asked to stop!
                            if (BluetoothManager.getInstance().isConnected()) {
                                MainActivity.debug("Device: something went wrong!");
                                // we don't want to continue, so we need to stop the poller right now!
                                // TODO but are we? I don't believe this comment is correct is it?
                                initDevice(TOUGHNESS_MEDIUM, 2); // toughness = 1, retries = 2
                            }
                        }
                    }
                }
            }
            // if any error occures, reset the fieldIndex
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private Field getNextField()
    {
        long referenceTime = Calendar.getInstance().getTimeInMillis();

        synchronized (fields) {
            if(applicationFields.size()>0) {
                /*
                // sort the applicationFields
                Collections.sort(applicationFields, new Comparator<Field>() {
                    @Override
                    public int compare(Field lhs, Field rhs) {
                        return (int) (lhs.getLastRequest()+lhs.getInterval() - (rhs.getLastRequest()+rhs.getInterval()));
                    }
                });
                // get the first field (the one with the smallest lastRequest time
                Field field = applicationFields.get(0); */

                // get the first field (the one with the smallest lastRequest time
                Field field = Collections.min(applicationFields, new Comparator<Field>() {
                    @Override
                    public int compare(Field lhs, Field rhs) {
                        return (int) (lhs.getLastRequest()+lhs.getInterval() - (rhs.getLastRequest()+rhs.getInterval()));
                    }
                });

                // return it's index in the global registered field array
                if(field.isDue(referenceTime)) {
                    //MainActivity.debug(Calendar.getInstance().getTimeInMillis()/1000.+" > Chosing: "+field.getSID());
                    MainActivity.debug("Device: getNextField > applicationFields");
                    return field;
                }
            }
            // take the next costum field
            if(activityFieldsScheduled.size()>0)
            {
                /*
                // sort the activityFields
                Collections.sort(activityFieldsScheduled, new Comparator<Field>() {
                    @Override
                    public int compare(Field lhs, Field rhs) {
                        return (int) (lhs.getLastRequest()+lhs.getInterval() - (rhs.getLastRequest()+rhs.getInterval()));
                    }
                });

                // get the first field (the one with the smallest lastRequest time
                Field field = activityFieldsScheduled.get(0); */

                // get the first field (the one with the smallest lastRequest time
                Field field = Collections.min(activityFieldsScheduled, new Comparator<Field>() {
                    @Override
                    public int compare(Field lhs, Field rhs) {
                        return (int) (lhs.getLastRequest()+lhs.getInterval() - (rhs.getLastRequest()+rhs.getInterval()));
                    }
                });

                // return it's index in the global registered field array
                if(field.isDue(referenceTime)) {
                    //MainActivity.debug(Calendar.getInstance().getTimeInMillis()/1000.+" > Chosing: "+field.getSID());
                    MainActivity.debug("Device: getNextField > activityFieldsScheduled");
                    return field;
                }
            }
            if(activityFieldsAsFastAsPossible.size()>0)
            {
                activityFieldIndex = (activityFieldIndex + 1) % activityFieldsAsFastAsPossible.size();
                MainActivity.debug("Device: getNextField > activityFieldsAsFastAsPossible");
                return activityFieldsAsFastAsPossible.get(activityFieldIndex);
            }

            MainActivity.debug("Device: applicationFields & customActivityFields empty? "
                    + applicationFields.size() + " / " + activityFieldsScheduled.size()+ " / " + activityFieldsAsFastAsPossible.size());

            return null;
        }
    }

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


    public void join() throws InterruptedException{
        if(pollerThread!=null)
            pollerThread.join();
    }


    /* ----------------------------------------------------------------
     * Methods (that will be inherited by any "real" device)
     \ -------------------------------------------------------------- */

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
     * This method clears the list of monitored fields,
     * but only the custom ones ...
     */
    public void clearFields()
    {
        MainActivity.debug("Device: clearFields");
        synchronized (fields) {
            activityFieldsScheduled.clear();
            activityFieldsAsFastAsPossible.clear();
            fields.clear();
            fields.addAll(applicationFields);
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
     * @return boolean  true if field's frame is already monitored
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

    private boolean containsApplicationField(Field _field)
    {
        for(int i=0; i< applicationFields.size(); i++)
        {
            Field field = applicationFields.get(i);
            if(field.getId()==_field.getId() && field.getResponseId().equals(_field.getResponseId()))
                return true;
        }
        return false;
    }

    private boolean containsActivityFieldScheduled(Field _field)
    {
        for(int i=0; i< activityFieldsScheduled.size(); i++)
        {
            Field field = activityFieldsScheduled.get(i);
            if(field.getId()==_field.getId() && field.getResponseId().equals(_field.getResponseId()))
                return true;
        }
        return false;
    }

    private boolean containsActivityFieldAsFastAsPossible(Field _field)
    {
        for(int i=0; i< activityFieldsAsFastAsPossible.size(); i++)
        {
            Field field = activityFieldsAsFastAsPossible.get(i);
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
    public void addActivityField(final Field field) {
        // ass already present listeners are no being re-registered, do this always
        // register it to be saved to the database
        field.addListener(CanzeDataSource.getInstance());

        if(!field.isVirtual()) {

            synchronized (fields) {

                if (!containsField(field)) {
                    // add it to the lists
                    fields.add(field);
                    activityFieldsAsFastAsPossible.add(field);
                    // if the scheduled list constains the same frame id,
                    // it can be removed there
                    if (containsActivityFieldScheduled(field))
                        activityFieldsScheduled.remove(field);
                    // launch the field registration asynchronously
                    (new Thread(new Runnable() {
                        @Override
                        public void run() {
                            registerFilter(field.getId());
                        }
                    })).start();
                }
                if (!containsActivityFieldAsFastAsPossible(field)) {
                    activityFieldsAsFastAsPossible.add(field);
                    // if the scheduled list constains the same frame id,
                    // it can be removed there
                    if (containsActivityFieldScheduled(field))
                        activityFieldsScheduled.remove(field);
                }
            }
        }
        // register real fields on which a virtual field may depend
        else
        {
            VirtualField virtualField = (VirtualField) field;
            for (Field realField : virtualField.getFields())
            {
                addActivityField(realField);
            }
        }
    }

    public void addActivityField(final Field field, int interval)
    {
        // if the interval is 0 or below, the field should be
        // added to the list of "as fast as possible" fields.
        if (interval <=0 )
        {
            addActivityField(field);
            return;
        }

        // ass already present listeners are no being re-registered, do this always
        // register it to be saved to the database
        field.addListener(CanzeDataSource.getInstance());

        if(!field.isVirtual()) {
            synchronized (fields) {

                if (!containsField(field)) {
                    // add it to the lists
                    fields.add(field);
                    activityFieldsScheduled.add(field);
                    // set the fields query interval
                    field.setInterval(interval);
                    // launch the field registration asynchronously
                    (new Thread(new Runnable() {
                        @Override
                        public void run() {
                            registerFilter(field.getId());
                        }
                    })).start();
                }
                if (!containsActivityFieldScheduled(field)) {
                    // only add it this field id is not yet on the list of the
                    // request as fast as possible list.
                    if (!containsActivityFieldAsFastAsPossible(field))
                        activityFieldsScheduled.add(field);
                    // the fields interval will be ignored as the one from the
                    // applicationFields has priority
                } else {
                    // the smallest intervall is the one to take
                    if (interval < field.getInterval())
                        field.setInterval(interval);
                }
            }
        }
        // register real fields on which a virtual field may depend
        else
        {
            VirtualField virtualField = (VirtualField) field;
            for (Field realField : virtualField.getFields())
            {
                // increase interval
                addActivityField(realField, interval * virtualField.getFields().size());
            }
        }
    }

    public void addApplicationField(final Field field, int interval)
    {
        // ass already present listeners are no being re-registered, do this always
        // register it to be saved to the database
        field.addListener(CanzeDataSource.getInstance());

        if(!field.isVirtual()) {
            synchronized (fields) {
                if (!containsField(field)) {
                    // set the fields query interval
                    field.setInterval(interval);
                    // add it to the two lists
                    fields.add(field);
                    applicationFields.add(field);
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
        // register real fields on which a virtual field may depend
        else
        {
            VirtualField virtualField = (VirtualField) field;
            for (Field realField : virtualField.getFields())
            {
                // increase interval
                addApplicationField(realField,interval*virtualField.getFields().size());
            }
        }

    }

    public void removeApplicationField(final Field field)
    {
        synchronized (fields) {
            // only remove from the custom fields
            if(applicationFields.remove(field))
            {
                // remove it from the database if it is not on the other list
                if(!containsActivityFieldScheduled(field)) {
                    fields.remove(field);
                    field.setInterval(Integer.MAX_VALUE);
                    // un-register it ...
                    field.removeListener(CanzeDataSource.getInstance());
                }

                // launch the field registration asynchronously
                (new Thread(new Runnable() {
                    @Override
                    public void run() {
                        unregisterFilter(field.getId());
                    }
                })).start();
            }
        }

        // remove depenand fields
        // ATTENTION; remove the field, despite if it is used by some other VF or not!
        //if(field.isVirtual())
        //{
        // may break something, so please do it manually if really needed!
        //}
    }

    /* ----------------------------------------------------------------
     * Methods (that will be inherited by any "real" device)
     \ -------------------------------------------------------------- */


    public void init(boolean reset) {
        // init the connection
        initConnection();

        if(reset) {
            MainActivity.debug("Device: init with reset");
            // clean all filters (just to make sure)
            clearFields();
            // register all filters (if there are any)
            registerFilters();
        }
        else
            MainActivity.debug("Device: init");
    }

    /**
     * Stop the poller thread and wait for it to be finished
     */
    public void stopAndJoin()
    {
        MainActivity.debug("Device: stopping poller");
        setPollerActive(false);
        MainActivity.debug("Device: waiting for poller to be stopped");
        try {
            if(pollerThread!=null && pollerThread.isAlive()) {
                MainActivity.debug("Device: joining thread");
                if(pollerThread!=null)
                    pollerThread.join();
                pollerThread=null;
            }
            else MainActivity.debug("Device: >>>>>>> pollerThread is NULL!!!");
            MainActivity.debug("Device: pollerThread joined");
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        MainActivity.debug("Device: poller stopped");
    }

    private boolean isPollerActive() {
        return pollerActive;
    }

    void setPollerActive(boolean pollerActive) {
        this.pollerActive = pollerActive;
    }

    /**
     * Request a field from the device depending on the
     * type of field.
     * @param frame     the field to be requested
     * @return Message  containing the response or an error
     */
    public Message requestFrame(Frame frame)
    {
        Message msg;

        if (frame.isIsoTp())
            msg = requestIsoTpFrame(frame);
        else
            msg = requestFreeFrame(frame);

        if (msg.isError()) {
            MainActivity.debug("Device: request for " + frame.getRID() + " returned error " + msg.getError());
            // theory: when the answer is empty, the timeout is to low --> increase it!
            // jm: but never beyond 2
            if (intervalMultiplicator < maxIntervalMultiplicator) intervalMultiplicator += 0.1;
            MainActivity.debug("Device: intervalMultiplicator = " + intervalMultiplicator);
        } else {
            // theory: when the answer is good, we might recover slowly --> decrease it!
            // jm: but never below 1 ----> 2015-12-14 changed 10 1.3
            if (intervalMultiplicator > minIntervalMultiplicator) intervalMultiplicator -= 0.01;
            MainActivity.debug("Device: intervalMultiplicator = " + intervalMultiplicator);
        }

        return msg;
    }

    /**
     * Request a free-frame type field from the device
     * @param frame         The frame requested
     * @return Message
     */
    public abstract Message requestFreeFrame(Frame frame);

    /**
     * Request an ISO-TP frame type from the device
     * @param frame         The frame requested
     * @return Message
     */
    public abstract Message requestIsoTpFrame(Frame frame);

    public abstract boolean initDevice(int toughness);

    protected abstract boolean initDevice (int toughness, int retries);

    public String getLastInitProblem () {
        return lastInitProblem;
    }
}
