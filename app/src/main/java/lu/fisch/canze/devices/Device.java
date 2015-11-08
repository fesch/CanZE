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
import lu.fisch.canze.actors.Message;
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
     * Some fields will be custom, activity based
     */
    protected ArrayList<Field> activityFieldsScheduled = new ArrayList<>();
    protected ArrayList<Field> activityFieldsAsFastAsPossible = new ArrayList<>();
    /**
     * Some other fields will have to be queried anyway,
     * such as e.g. the speed --> safe mode driving
     */
    protected ArrayList<Field> applicationFields = new ArrayList<>();

    /**
     * The index of the actual field to query.
     * Loops over ther "fields" array
     */
    protected int fieldIndex = 0;

    protected int activityFieldIndex = 0;

    protected boolean pollerActive = false;
    protected Thread pollerThread;

    /**
     * someThingWrong will be set when something goes wrong, usually a timeout.
     * most command routines just won't run when someThingWrong is set
     * someThingWrong can be reset only by calling initElm, but with toughness 100 this is the only thing it does :-)
     */
    boolean someThingWrong = false;

    /**
     * lastInitProblem should be filled with a descriptive problem description by the initDevice implementation. In normal operation we don't care
     * because a device either initializes or not, but for testing a new device this can be very helpful.
     */
    protected String lastInitProblem = "";

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
                        if(initDevice(0)) {
                            while (isPollerActive()) {
                                //MainActivity.debug("Device: inside poller thread");
                                if (fields.size() == 0 || !BluetoothManager.getInstance().isConnected()) {
                                    //MainActivity.debug("Device: sleeping");
                                    try {
                                        Thread.sleep(5000);
                                    } catch (Exception e) {
                                        // ignore a sleep exception
                                    }
                                }
                                // query a field
                                else {
                                    //MainActivity.debug("Device: Doing next query ...");
                                    queryNextFilter();
                                }
                            }
                            // dereference the poller thread (it i stopped now anyway!)
                            MainActivity.debug("Device: Poller is done");
                            pollerThread = null;
                        }
                        else
                        {
                            MainActivity.debug("Device: no answer from device");

                            // drop the BT connexion and try again
                            (new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    // stop the BT but don't reset the device registered fields
                                    //MainActivity.getInstance().stopBluetooth(false);
                                    // reload the BT with filter registration
                                    //MainActivity.getInstance().reloadBluetooth(false);
                                    BluetoothManager.getInstance().connect();
                                }
                            })).start();
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
    protected void queryNextFilter()
    {
        if (fields.size() > 0)
        {
            try {

                Field field;

                if(fieldIndex <0) {
                    MainActivity.debug("Device: fieldIndex < 0, sleeping");
                    // no next field ---> sleep
                    try {
                        Thread.sleep(100);
                    } catch(Exception e) {
                        // ignore a sleep exception
                    }
                    // try to get the next field
                    fieldIndex = getNextIndex();
                    return;
                }

                // get field
                synchronized (fields) {
                    field = fields.get(fieldIndex);
                }

                MainActivity.debug("Device: queryNextFilter: " + fieldIndex + " --> " + field.getSID()); //" + " \tSkipsCount = " + field.getSkipsCount());
                long start = Calendar.getInstance().getTimeInMillis();

                // if we got the field
                if (field != null) {
                    /*
                    // only run the filter if the skipsCount is down to zero
                    boolean runFilter = (field.getSkipsCount() == 0);
                    if (runFilter)
                        // reset it to its initial value
                        field.resetSkipsCount();
                    else
                        // decrement the skipsCount
                        field.decSkipCount();

                    // get this field
                    if (runFilter) {
                    */
                        // get the data
                        Message message = requestField(field);
                        // test if we got something
                        if(message!=null && !someThingWrong) {
                            Fields.getInstance().onMessageCompleteEvent(message);
                        }

                        // reset if something went wrong ...
                        // ... but only if we are not asked to stop!
                        if (someThingWrong && BluetoothManager.getInstance().isConnected()) {
                            initDevice(1, 2);
                        }
                    //}

                    // goto next filter
                    /*synchronized (fields) {
                        if (fields.size() == 0)
                            fieldIndex = 0;
                        else
                            fieldIndex = (fieldIndex + 1) % fields.size();
                    }*/

                    MainActivity.debug("Device: Request took "+(Calendar.getInstance().getTimeInMillis()-start)/1000.+"s -( "+
                            field.getSID()+" )-> "+field.getPrintValue());

                    // determine the next field to query
                    fieldIndex = getNextIndex();
                }
                else
                    MainActivity.debug("Device: failed to get the field!");
            }
            // if any error occures, reset the fieldIndex
            catch (Exception e) {
                e.printStackTrace();
                fieldIndex = getNextIndex();
            }
        }
    }

    private int getNextIndex()
    {
        long referenceTime = Calendar.getInstance().getTimeInMillis();

        synchronized (fields) {
            if(applicationFields.size()>0) {
                // sort the applicationFields
                Collections.sort(applicationFields, new Comparator<Field>() {
                    @Override
                    public int compare(Field lhs, Field rhs) {
                    return (int) (lhs.getLastRequest()+lhs.getInterval() - (rhs.getLastRequest()+rhs.getInterval()));
                    }
                });

                /*MainActivity.debug("-1-");
                for(int i=0; i<applicationFields.size(); i++)
                {
                    MainActivity.debug(
                            (applicationFields.get(i).getLastRequest()+applicationFields.get(i).getInterval()-referenceTime)+
                                    " (" + applicationFields.get(i).getInterval() + ")> "+
                                    applicationFields.get(i).getSID());
                }/**/

                // get the first field (the one with the smallest lastRequest time
                Field field = applicationFields.get(0);
                // return it's index in the global registered field array
                if(field.isDue(referenceTime)) {
                    //MainActivity.debug(Calendar.getInstance().getTimeInMillis()/1000.+" > Chosing: "+field.getSID());
                    return fields.indexOf(field);
                }
            }
            // take the next costum field
            if(activityFieldsScheduled.size()>0)
            {
                // sort the activityFields
                Collections.sort(activityFieldsScheduled, new Comparator<Field>() {
                    @Override
                    public int compare(Field lhs, Field rhs) {
                    return (int) (lhs.getLastRequest()+lhs.getInterval() - (rhs.getLastRequest()+rhs.getInterval()));
                    }
                });

                /*MainActivity.debug("-2-");
                for(int i=0; i< activityFieldsScheduled.size(); i++)
                {
                    Field field = activityFieldsScheduled.get(i);
                    MainActivity.debug(
                            applicationFields.contains(field) + " | " + (field.getLastRequest() + field.getInterval() - referenceTime) +
                                    " (" + field.getInterval() + ")> " +
                                    field.getSID());
                }/**/

                // get the first field (the one with the smallest lastRequest time
                Field field = activityFieldsScheduled.get(0);
                // return it's index in the global registered field array
                if(field.isDue(referenceTime)) {
                    //MainActivity.debug(Calendar.getInstance().getTimeInMillis()/1000.+" > Chosing: "+field.getSID());
                    return fields.indexOf(field);
                }
            }
            if(activityFieldsAsFastAsPossible.size()>0)
            {

                /*MainActivity.debug("-3-");
                for(int i=0; i< activityFieldsAsFastAsPossible.size(); i++)
                {
                    Field field = activityFieldsAsFastAsPossible.get(i);
                    MainActivity.debug(
                            applicationFields.contains(field)+" | "+(field.getLastRequest()+field.getInterval()-referenceTime)+
                                    " (" + field.getInterval() + ")> "+
                                    field.getSID());
                }/**/

                activityFieldIndex = (activityFieldIndex + 1) % activityFieldsAsFastAsPossible.size();
                return fields.indexOf(activityFieldsAsFastAsPossible.get(activityFieldIndex));
            }

            MainActivity.debug("Device: applicationFields & customActivityFields empty? "
                    + applicationFields.size() + " / " + activityFieldsScheduled.size()+ " / " + activityFieldsAsFastAsPossible.size());

            return -1;
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

    //protected abstract Message processData(String inputString);
    /*
    protected Message processData(String text) {
        // split up the fields
        String[] pieces = text.trim().split(",");
        if(pieces.length==2) {
            try {
                // get the id
                int id = Integer.parseInt(pieces[0], 16);
                // create and return new frame
                return new Message(id, pieces[1].trim());
            }
            catch(Exception e)
            {
                return null;
            }
        }
        else if(pieces.length>=3) {
            try {
                // get the id
                int id = Integer.parseInt(pieces[0], 16);
                // get the reply-ID
                Message f = new Message(id,pieces[1].trim());
                f.setResponseId(pieces[2].trim());
                return f;

                // get checksum
             //   int chk = Integer.parseInt(pieces[2].trim(), 16);
             //   int check = 0;
             //   for(int i=0; i<data.length; i++)
             //       check ^= data[i];
                // validate the checksum
             //   if(chk==check)
                    // create and return new frame
             //       return new Frame(id, data);
            }
            catch(Exception e)
            {
                return null;
            }
        }
        return null;
    }
    */

    /*
    protected boolean notifyFields(String text) {
        // split up the fields
        String[] pieces = text.trim().split(",");
        if(pieces.length==2) {
            try {
                Fields.getInstance().onMessageCompleteEvent(Integer.parseInt(pieces[0], 16),pieces[1].trim(),null);
                return true;
            }
            catch(Exception e)
            {
                return false;
            }
        }
        else if(pieces.length>=3) {
            try {
                Fields.getInstance().onMessageCompleteEvent(Integer.parseInt(pieces[0], 16), pieces[1].trim(), pieces[2].trim());
                return true;
            }
            catch(Exception e)
            {
                return false;
            }
        }
        return false;
    }
    */

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

        synchronized (fields) {

            if (!containsField(field)) {
                // add it to the lists
                fields.add(field);
                activityFieldsAsFastAsPossible.add(field);
                // if the scheduled list constains the same frame id,
                // it can be removed there
                if(containsActivityFieldScheduled(field))
                    activityFieldsScheduled.remove(field);
                // launch the field registration asynchronously
                (new Thread(new Runnable() {
                    @Override
                    public void run() {
                        registerFilter(field.getId());
                    }
                })).start();
            }
            if(!containsActivityFieldAsFastAsPossible(field))
            {
                activityFieldsAsFastAsPossible.add(field);
                // if the scheduled list constains the same frame id,
                // it can be removed there
                if(containsActivityFieldScheduled(field))
                    activityFieldsScheduled.remove(field);
            }
        }
    }

    public void addActivityField(final Field field, int interval)
    {
        // ass already present listeners are no being re-registered, do this always
        // register it to be saved to the database
        field.addListener(CanzeDataSource.getInstance());

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
            if(!containsActivityFieldScheduled(field))
            {
                // only add it this field id is not yet on the list of the
                // request as fast as possible list.
                if(!containsActivityFieldAsFastAsPossible(field))
                    activityFieldsScheduled.add(field);
                // the fields interval will be ignored as the one from the
                // applicationFields has priority
            }
            else
            {
                // the smallest intervall is the one to take
                if(interval<field.getInterval())
                    field.setInterval(interval);
            }
        }
    }

    public void addApplicationField(final Field field, int interval)
    {
        // ass already present listeners are no being re-registered, do this always
        // register it to be saved to the database
        field.addListener(CanzeDataSource.getInstance());

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

    /**
     * This method removes a field from the list of monitored fields
     * and unregisters the corresponding filter.
     * @param field
     */
    public void removeActivityField(final Field field)
    {
        synchronized (fields) {
            // only remove from the custom fields
            if(activityFieldsScheduled.remove(field))
            {
                // remove it from the database if it is not on the other list
                if(!containsApplicationField(field)) {
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
            if(pollerThread!=null) {
                MainActivity.debug("Device: joining thread");
                pollerThread.join();
                pollerThread=null;
            }
            else MainActivity.debug("Device: >>>>>>> pollerThread is NULL!!!");
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        MainActivity.debug("Device: poller stopped");
    }

    public boolean isPollerActive() {
        return pollerActive;
    }

    public void setPollerActive(boolean pollerActive) {
        this.pollerActive = pollerActive;
    }

    /**
     * Request a field from the device depending on the
     * type of field.
     * @param field     the field to be requested
     * @return
     */
    public Message requestField(Field field)
    {
        Message msg = null;
        if(field.isIsoTp()) msg=requestIsoTpFrame(field);
        else msg=requestFreeFrame(field);

        if(msg==null || msg.getData().isEmpty()) MainActivity.debug("Device: request for "+field.getSID()+" is empty ...");

        return msg;
    }

    /**
     * Request a free-frame type field from the device
     * @param field
     * @return
     */
    public abstract Message requestFreeFrame(Field field);

    /**
     * Request an ISO-TP frame type from the device
     * @param field
     * @return
     */
    public abstract Message requestIsoTpFrame(Field field);

    public abstract boolean initDevice(int toughness);

    protected abstract boolean initDevice (int toughness, int retries);

    public String getLastInitProblem () {
        return lastInitProblem;
    }
}
