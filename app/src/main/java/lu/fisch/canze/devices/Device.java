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
import lu.fisch.canze.actors.Fields;
import lu.fisch.canze.actors.Frame;
import lu.fisch.canze.actors.Message;
import lu.fisch.canze.actors.VirtualField;
import lu.fisch.canze.bluetooth.BluetoothManager;
import lu.fisch.canze.database.CanzeDataSource;

/**
 * This class defines an abstract device. It has to manage the device related
 * decoding of the incoming data as well as the data flow to the device or
 * whatever is needed to "talk" to it.
 * <p>
 * Created by robertfisch on 07.09.2015.
 */

public abstract class Device {

    public static final int INTERVAL_ASAP = 0; // follows frame rate
    public static final int INTERVAL_ASAPFAST = -1; // truly as fast as possible
    public static final int INTERVAL_ONCE = -2; // one shot


    protected static final int TOUGHNESS_HARD = 0;    // hardest reset possible (ie atz)
    protected static final int TOUGHNESS_MEDIUM = 1;    // medium reset (i.e. atws)
    protected static final int TOUGHNESS_SOFT = 2;    // softest reset (i.e atd for ELM)
    protected static final int TOUGHNESS_NONE = 100;  // just clear error status

    private final double minIntervalMultiplicator = 1.3;
    private final double maxIntervalMultiplicator = 2.5;
    double intervalMultiplicator = minIntervalMultiplicator;
    private boolean deviceIsInitialized = false; // if true initConnection will only start a new pollerthread

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
    public void initConnection() {
        MainActivity.debug("Device.initConnection: start");

        if (BluetoothManager.getInstance().isConnected()) {
            MainActivity.debug("Device.initConnection: BT is connected");
            // make sure we only have one poller task
            if (pollerThread == null) {
                MainActivity.debug("Device.initConnection: starting new poller");
                // post a task to the UI thread
                setPollerActive(true);

                Runnable r = new Runnable() {
                    @Override
                    public void run() {
                        // if the device has been initialised and we got an answer
                        // TOUGHNESS_NONE does basically nothing
                        if (initDevice(deviceIsInitialized ? TOUGHNESS_NONE : TOUGHNESS_HARD)) {
                            deviceIsInitialized = true;
                            while (isPollerActive()) {
                                // MainActivity.debug("Device: inside poller thread");
                                if (applicationFields.size() + activityFieldsScheduled.size() + activityFieldsAsFastAsPossible.size() == 0
                                        || !BluetoothManager.getInstance().isConnected()) {
                                    // MainActivity.debug("Device.poller: no work");
                                    try {
                                        if (isPollerActive())
                                            Thread.sleep(1000);
                                        else return;
                                    } catch (Exception e) {
                                        // ignore a sleep exception
                                    }
                                }
                                // query a field
                                else {
                                    if (isPollerActive()) {
                                        MainActivity.debug("Device.poller: Doing next query");
                                        queryNextFilter();
                                    } else return;
                                }
                            }
                            // dereference the poller thread (it i stopped now anyway!)
                            MainActivity.debug("Device.poller stopped");
                            pollerThread = null;
                        } else {
                            MainActivity.debug("Device.poller: initDevice failed");
                            deviceIsInitialized = false;
                            // first check if we have not yet been killed!
                            if (isPollerActive()) {
                                MainActivity.debug("Device.poller: restarting Bluetooth");
                                // drop the BT connexion and try again
                                (new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        // stop the BT but don't reset the device registered fields
                                        MainActivity.getInstance().stopBluetooth(false);
                                        // reload the BT with filter registration
                                        MainActivity.getInstance().reloadBluetooth(false);
                                        //BluetoothManager.getInstance().connect();
                                        pollerThread = null; // but we are still quitting the poller thread
                                    }
                                })).start();
                            }
                        }
                    }
                };
                pollerThread = new Thread(r);
                // start the thread
                pollerThread.start();
            } // never mind, the BT is active, and the poller thread is running. Nothing to do
        } else {
            MainActivity.debug("Device.initConnection: BT is not connected");
            if (pollerThread != null && pollerThread.isAlive()) {
                MainActivity.debug("Device.initConnection: stopping poller");
                setPollerActive(false);
                try {
                    pollerThread.join();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }


    public Message injectRequest(String sid) {
        Field field = Fields.getInstance().getBySID(sid);
        if (field == null) return null;
        return injectRequest(field.getFrame());
    }

    // stop the poller and request a frame (new!)
    public Message injectRequest(Frame frame) {
        if (frame == null) return null;
        // stop the poller and wait for it to become inactive
        stopAndJoin();

        Message message = requestFrame(frame);

        // restart the poller
        initConnection();

        // return the captured message
        return message;
    }

    // stop the poller and request multiple frames (new!)
    public Message injectRequests(Frame[] frames) {
        return injectRequests(frames, false, false);
    }


    // stop the poller and request multiple frames (new!)
    // this variant will be very useful for ie LoadAllData
    public Message injectRequests(Frame[] frames, boolean stopOnError, boolean callOnMessageComplete) {
        // stop the poller and wait for it to become inactive
        stopAndJoin();

        Message message = null;
        for (Frame frame : frames) {
            if (frame != null) {
                message = requestFrame(frame);
                if (stopOnError && (message == null || message.isError())) break;
                if (message != null && callOnMessageComplete) {
                    if (!message.isError()) {
                        message.onMessageCompleteEvent();
                    } else {
                        message.onMessageIncompleteEvent();
                    }
                }
            }
        }

        // restart the poller
        initConnection();

        // return the last captured message
        return message;
    }

    // query the device for the next filter
    private void queryNextFilter() {
        if (applicationFields.size() + activityFieldsScheduled.size() + activityFieldsAsFastAsPossible.size() > 0) {
            try {

                Field field = getNextField();

                if (field == null) {
                    // MainActivity.debug("Device: got no next field --> sleeping");
                    // no next field ---> sleep
                    try {
                        Thread.sleep(200);
                    } catch (Exception e) {
                        // ignore a sleep exception
                    }
                } else {
                    // long start = Calendar.getInstance().getTimeInMillis();
                    // MainActivity.debug("Device: queryNextFilter: " + field.getSID());
                    MainActivity.getInstance().dropDebugMessage(field.getSID());

                    // get the data
                    Message message = requestFrame(field.getFrame());

                    // test if we got something
                    if (!message.isError()) {
                        MainActivity.getInstance().appendDebugMessage("ok");
                        // trigger the compete event of the message. It will update all fields linked to it's corresponding frame
                        message.onMessageCompleteEvent();
                        if (field.getInterval() == INTERVAL_ONCE) {
                            removeActivityField(field);
                        }
                    } else {
                        if (message.isError7f()) {
                            message.onMessageIncompleteEvent();
                            return;
                        }
                        // one plain retry
                        MainActivity.getInstance().appendDebugMessage("...");
                        message = requestFrame(field.getFrame());
                        if (!message.isError()) {
                            MainActivity.getInstance().appendDebugMessage("ok");
                            message.onMessageCompleteEvent();
                            if (field.getInterval() == INTERVAL_ONCE) {
                                removeActivityField(field);
                            }
                        } else {
                            if (message.isError7f()) {
                                message.onMessageIncompleteEvent();
                                return;
                            }
                            MainActivity.getInstance().appendDebugMessage("fail");
                            // failed after single retry. Mark underlying fields as updated to avoid
                            // queue clogging. The frame will have to get back to the end of the queue
                            message.onMessageIncompleteEvent();
                            // reset if something went wrong ...
                            // ... but only if we are not asked to stop!
                            if (BluetoothManager.getInstance().isConnected()) {
                                MainActivity.debug("Device.queryNextFilter: Re-initializing");
                                deviceIsInitialized = false; // force a true device init
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

    private Field getNextField() {
        long referenceTime = Calendar.getInstance().getTimeInMillis();

        synchronized (fields) {
            if (applicationFields.size() > 0) {
                // get the first field (the one with the smallest lastRequest time
                Field field = Collections.min(applicationFields, new Comparator<Field>() {
                    @Override
                    public int compare(Field lhs, Field rhs) {
                        return (int) (lhs.getLastRequest() + lhs.getInterval() - (rhs.getLastRequest() + rhs.getInterval()));
                    }
                });

                // return it's index in the global registered field array
                if (field.isDue(referenceTime)) {
                    //MainActivity.debug(Calendar.getInstance().getTimeInMillis()/1000.+" > Chosing: "+field.getSID());
                    MainActivity.debug("Device.getNextField (" + pollerThread.getId() + "): applicationFields, " + field.getSID());
                    return field;
                }
            }

            // take the next custom field
            if (activityFieldsScheduled.size() > 0) {
                // get the first field (the one with the smallest lastRequest time
                Field field = Collections.min(activityFieldsScheduled, new Comparator<Field>() {
                    @Override
                    public int compare(Field lhs, Field rhs) {
                        return (int) (lhs.getLastRequest() + lhs.getInterval() - (rhs.getLastRequest() + rhs.getInterval()));
                    }
                });

                // return it's index in the global registered field array
                if (field.isDue(referenceTime)) {
                    //MainActivity.debug(Calendar.getInstance().getTimeInMillis()/1000.+" > Chosing: "+field.getSID());
                    MainActivity.debug("Device.getNextField (" + pollerThread.getId() + "): activityFieldsScheduled, " + field.getSID());
                    return field;
                }
            }

            if (activityFieldsAsFastAsPossible.size() > 0) {
                activityFieldIndex = (activityFieldIndex + 1) % activityFieldsAsFastAsPossible.size();
                Field field = activityFieldsAsFastAsPossible.get(activityFieldIndex);
                MainActivity.debug("Device.getNextField (" + pollerThread.getId() + "): activityFieldsAsFastAsPossible, " + field.getSID());
                return field;
            }

            MainActivity.debug("Device.getNextField (" + pollerThread.getId() + "): empty:" + applicationFields.size() + " / " + activityFieldsScheduled.size() + " / " + activityFieldsAsFastAsPossible.size());

            return null;
        }
    }

    public void join() throws InterruptedException {
        if (pollerThread != null)
            pollerThread.join();
    }


    /* ----------------------------------------------------------------
     * Methods (that will be inherited by any "real" device)
     \ -------------------------------------------------------------- */

    /**
     * This method clears the list of monitored fields,
     * but only the custom ones ...
     */
    public void clearFields() {
        MainActivity.debug("Device.clearFields: start");
        synchronized (fields) {
            activityFieldsScheduled.clear();
            activityFieldsAsFastAsPossible.clear();
            fields.clear();
            fields.addAll(applicationFields);
        }
    }

    /**
     * A CAN message will trigger updates for all connected fields, meaning
     * any field with the same ID and the same responseID will be updated.
     * For this reason we don't need to query these fields multiple times
     * in one turn.
     *
     * @param _field the field to be tested
     * @return boolean  true if field's frame is already monitored
     */
    private boolean containsField(Field _field) {
        for (int i = 0; i < fields.size(); i++) {
            Field field = fields.get(i);
            if (field.getId() == _field.getId() && field.getResponseId().equals(_field.getResponseId()))
                return true;
        }
        return false;
    }

    private boolean containsApplicationField(Field _field) {
        for (int i = 0; i < applicationFields.size(); i++) {
            Field field = applicationFields.get(i);
            if (field.getId() == _field.getId() && field.getResponseId().equals(_field.getResponseId()))
                return true;
        }
        return false;
    }

    private boolean containsActivityFieldScheduled(Field _field) {
        for (int i = 0; i < activityFieldsScheduled.size(); i++) {
            Field field = activityFieldsScheduled.get(i);
            if (field.getId() == _field.getId() && field.getResponseId().equals(_field.getResponseId()))
                return true;
        }
        return false;
    }

    private boolean containsActivityFieldAsFastAsPossible(Field _field) {
        for (int i = 0; i < activityFieldsAsFastAsPossible.size(); i++) {
            Field field = activityFieldsAsFastAsPossible.get(i);
            if (field.getId() == _field.getId() && field.getResponseId().equals(_field.getResponseId()))
                return true;
        }
        return false;
    }

    /**
     * Method to add a field to the list of monitored field.
     * The field is also immediately registered onto the device.
     *
     * @param field the field to be added
     */
    private void addActivityField(final Field field) {
        // ass already present listeners are no being re-registered, do this always
        // register it to be saved to the database
        field.addListener(CanzeDataSource.getInstance());

        if (!field.isVirtual()) {

            synchronized (fields) {

                if (!containsField(field)) {
                    // add it to the lists
                    fields.add(field);
                    activityFieldsAsFastAsPossible.add(field);
                    // if the scheduled list constains the same frame id,
                    // it can be removed there
                    if (containsActivityFieldScheduled(field))
                        activityFieldsScheduled.remove(field);
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
        else {
            VirtualField virtualField = (VirtualField) field;
            for (Field realField : virtualField.getFields()) {
                addActivityField(realField);
            }
        }
    }

    /*
    JM: I made addActivity without interval private, as to change the behavior with interval 0, which
    is used throughout the code to mean "we don't care, as fast as possible"
     */

    public void addActivityField(final Field field, int interval) {
        /*
        JM changed behavior
        interval > 0 is interval
        interval == INTERVAL_ASAP (0) is frame repeat interval (as given in the frame assets, especially relevant for freeframes)
        interval == INTERVAL_ASAPFAST (-1) is ASAP (note: old behavior is ASAP for ANYTHING negative, but I am pretty sure only -1
                was ever used). This should be used sparingly, if at all. The only reason I can think of is for a fast ISO-TP field
                which has no known interval rate
        interval == INTERVAL_ONCE) (-2) is add, but remove after one successful execution (in the poller thread). This is very
                useful for i.e. tester present messages
        */

        if (field.isSelfPropelled()) { //if self propelled, do not add to queue
            return;
        } else if (interval > INTERVAL_ASAP) { // interval is a ms value. Continue
            // Nothing
        } else if (interval == INTERVAL_ASAP) { // if INTERVAL_ASAP, get from the frame definition
            interval = field.getFrame().getInterval();
        } else if (interval == INTERVAL_ASAPFAST) { // if INTERVAL_ASAP, add to the ASAP queue
            addActivityField(field);
            return;
        } else if (interval == INTERVAL_ONCE) { // if INTERVAL_ONCE. Continue
            // Nothing. The INTERVAL_ONCE will be picked up by the poller
        } else { // abort
            return;
        }

        // ass already present listeners are no being re-registered, do this always
        // register it to be saved to the database
        field.addListener(CanzeDataSource.getInstance());

        if (!field.isVirtual()) {
            synchronized (fields) {

                if (!containsField(field)) {
                    // add it to the lists
                    fields.add(field);
                    activityFieldsScheduled.add(field);
                    // set the fields query interval
                    field.setInterval(interval);
                }
                if (!containsActivityFieldScheduled(field)) {
                    // only add it this field id is not yet on the list of the
                    // request as fast as possible list.
                    if (!containsActivityFieldAsFastAsPossible(field))
                        activityFieldsScheduled.add(field);
                    // the fields interval will be ignored as the one from the
                    // applicationFields has priority
                } else {
                    // the smallest interval is the one to take
                    if (interval < field.getInterval())
                        field.setInterval(interval);
                }
            }
        }
        // register real fields on which a virtual field may depend
        else {
            VirtualField virtualField = (VirtualField) field;
            for (Field realField : virtualField.getFields()) {
                // increase interval
                addActivityField(realField, interval * virtualField.getFields().size());
            }
        }
    }

    public void removeActivityField(final Field field) {
        synchronized (fields) {
            // only remove from the custom fields
            if (activityFieldsScheduled.remove(field)) {
                //field.setInterval(Integer.MAX_VALUE);
                //return; /*
                // remove it from the database if it is not on the other list
                if (!containsApplicationField(field) && !containsActivityFieldAsFastAsPossible(field)) {
                    fields.remove(field);
                    field.setInterval(Integer.MAX_VALUE);
                    // un-register it ...
                    field.removeListener(CanzeDataSource.getInstance());
                }
            }
        }
    }

    public void addApplicationField(final Field field, int interval) {
        // as already present listeners are not being re-registered, do this always
        // register it to be saved to the database
        field.addListener(CanzeDataSource.getInstance());

        if (!field.isVirtual()) {
            synchronized (fields) {
                if (!containsField(field)) {
                    // set the fields query interval
                    field.setInterval(interval);
                    // add it to the two lists
                    fields.add(field);
                    applicationFields.add(field);
                }
            }
        }
        // register real fields on which a virtual field may depend
        else {
            VirtualField virtualField = (VirtualField) field;
            for (Field realField : virtualField.getFields()) {
                // increase interval
                addApplicationField(realField, interval * virtualField.getFields().size());
            }
        }

    }

    public void removeApplicationField(final Field field) {
        synchronized (fields) {
            // only remove from the custom fields
            if (applicationFields.remove(field)) {
                // remove it from the database if it is not on the other list
                if (!containsActivityFieldScheduled(field) && !containsActivityFieldAsFastAsPossible(field)) {
                    fields.remove(field);
                    field.setInterval(Integer.MAX_VALUE);
                    // un-register it ...
                    field.removeListener(CanzeDataSource.getInstance());
                }
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

        if (reset) {
            // clean all filters (just to make sure)
            clearFields();
            MainActivity.debug("Device.init: done, reset");
        } else
            MainActivity.debug("Device.init: done, noreset");
    }

    /**
     * Stop the poller thread and wait for it to be finished
     */
    public void stopAndJoin() {
        MainActivity.debug("Device.stopAndJoin: start");
        setPollerActive(false);
        try {
            if (pollerThread != null && pollerThread.isAlive()) {
                MainActivity.debug("Device.stopAndJoin: poller informed. Joining thread");
                if (pollerThread != null)
                    pollerThread.join();
                pollerThread = null;
            } else MainActivity.debug("Device.stopAndJoin: pollerThread is null");
        } catch (Exception e) {
            e.printStackTrace();
        }
        MainActivity.debug("Device.stopAndJoin: poller stopped");
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
     *
     * @param frame the field to be requested
     * @return Message  containing the response or an error
     */
    public Message requestFrame(Frame frame) {
        Message msg;

        if (frame == null) return null;

        if (frame.isIsoTp()) {
            msg = requestIsoTpFrame(frame);
        } else {
            if (MainActivity.altFieldsMode) MainActivity.toast(MainActivity.TOAST_NONE, "Free frame in ISOTP mode:" + frame.getRID()); // MainActivity.debug("********* free frame in alt mode ********: " + frame.getRID());
            msg = requestFreeFrame(frame);
        }

        if (msg.isError()) {
            MainActivity.debug("Device.requestframe: " + frame.getRID() + " returned error " + msg.getError());
            // when the answer is empty, the timeout is to low --> increase it!
            if (intervalMultiplicator < maxIntervalMultiplicator) {
                intervalMultiplicator += 0.1;
                MainActivity.debug("Device.requestframe: intervalMultiplicator+ = " + intervalMultiplicator);
            }
        } else {
            MainActivity.debug("Device.requestframe: request for " + frame.getRID() + " returned data " + msg.getData());
            // theory: when the answer is good, we might recover slowly --> decrease it!
            // jm: but never below 1 ----> 2015-12-14 changed 10 1.3
            if (intervalMultiplicator > minIntervalMultiplicator) {
                intervalMultiplicator -= 0.01;
                MainActivity.debug("Device.requestframe: intervalMultiplicator- = " + intervalMultiplicator);
            }
        }

        return msg;
    }

    /**
     * Request a free-frame type field from the device
     *
     * @param frame The frame requested
     * @return Message
     */
    public abstract Message requestFreeFrame(Frame frame);

    /**
     * Request an ISO-TP frame type from the device
     *
     * @param frame The frame requested
     * @return Message
     */
    public abstract Message requestIsoTpFrame(Frame frame);

    public abstract boolean initDevice(int toughness);

    protected abstract boolean initDevice(int toughness, int retries);

    public String getLastInitProblem() {
        return lastInitProblem;
    }
}
