/**
 * This reader is polling data from the DUE using the following command set:
 *
 *  fID  register a filter. ID is its hex value
 *  rID  remove a filter, ID is its hex filter
 *  c    clear all filters
 *  gID  get the data of a frame, ID is its hex filter
 */

package lu.fisch.canze.readers;

import java.io.IOException;
import java.util.Calendar;

import lu.fisch.canze.MainActivity;
import lu.fisch.canze.actors.Field;
import lu.fisch.canze.actors.Stack;
import lu.fisch.canze.exeptions.NoDecoderException;

/**
 * Created by robertfisch on 27.08.2015.
 */
public class DueReader extends DataReader {
    // define the timeout we may wait to get an answer
    private static final int TIMEOUT = 500;
    // define End Of Message for this type of reader
    private static final char EOM = '\n';
    // the actual filter
    private int filterIndex = 0;
    // the thread that polls the data to the stack
    private Thread poller = null;
    private boolean pollerRunning = true;

    // create a new reader
    public DueReader(Stack stack) {
        super(stack);
    }

    // send a command and wait for an answer
    private String sendAndWaitForAnswer(String command, int waitMillis)
    {
        // empty incoming buffer
        // just make sure there is no previous response
        try {
            while(connectedBluetoothThread.available()>0)
            {
                connectedBluetoothThread.read();
            }
        } catch (IOException e) {
            // ignore
        }
        // send the command
        if(command!=null)
            connectedBluetoothThread.write(command + "\r\n");
        //MainActivity.debug("Send > "+command);
        // wait if needed
        if(waitMillis>0)
            try {
                Thread.sleep(waitMillis);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        // init the buffer
        boolean stop = false;
        String readBuffer = "";
        // wait for answer
        long start = Calendar.getInstance().getTimeInMillis();
        while(!stop && Calendar.getInstance().getTimeInMillis()-start<TIMEOUT)
        {
            //MainActivity.debug("Delta = "+(Calendar.getInstance().getTimeInMillis()-start));
            try {
                // read a byte
                if(connectedBluetoothThread.available()>0) {
                    //MainActivity.debug("Reading ...");
                    int data = connectedBluetoothThread.read();
                    //MainActivity.debug("... done");
                    // if it is a real one
                    if (data != -1) {
                        // convert it to a character
                        char ch = (char) data;
                        // add it to the readBuffer
                        readBuffer += ch;
                        // stop if we reached the end or if no more data is available
                        if (ch == EOM || connectedBluetoothThread.available() <= 0) stop = true;
                    }
                }
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
        //MainActivity.debug("Recv < "+readBuffer);
        return readBuffer;
    }

    @Override
    public void initConnection() {
        // if the reading thread is running: stop it, because we don't need it
        if(connectedBluetoothThread!=null && connectedBluetoothThread.isAlive()) {
            connectedBluetoothThread.cleanStop();
            try {
                connectedBluetoothThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        if(connectedBluetoothThread!=null) {
            // make sure we only have one poller task
            if (poller == null) {
                // post a task to the UI thread
                pollerRunning=true;
                Runnable r = new Runnable() {
                    @Override
                    public void run() {
                        while (pollerRunning)
                            queryNextFilter();
                    }
                };
                poller = new Thread(r);
                poller.start();
            }
        }
        else
        {
            if(poller!=null && poller.isAlive())
            {
                pollerRunning=false;
                try {
                    poller.join();
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
            if (fields.size() > 0) {
                try {
                    // get field
                    Field field;

                    synchronized (fields) {
                        field = fields.get(filterIndex);
                    }

                    if(field!=null) {
                        // get field ID
                        String filter = field.getHexId();

                        if (field.isIsoTp()) {
                            String hexData = sendAndWaitForAnswer("i" + filter + "," + field.getRequestId() + "," + field.getResponseId(), 0);
                            // send it to the stack
                            try {
                                stack.process(hexData);
                            } catch (NoDecoderException e) {
                                e.printStackTrace();
                            }
                        } else {
                            // request the response from the device
                            //MainActivity.debug("Requesting: " + filter);
                            String hexData = sendAndWaitForAnswer("g" + filter, 0);
                            // send it to the stack
                            try {
                                stack.process(hexData);
                            } catch (NoDecoderException e) {
                                e.printStackTrace();
                            }
                        }
                        // goto next filter
                        synchronized (fields) {
                            filterIndex = (filterIndex + 1) % fields.size();
                        }
                    }
                } catch (Exception e) {
                    filterIndex=0;
                }
            } else {
                // ignore
            }
    }

    // clean all filters
    @Override
    public void clearFields() {
        super.clearFields();
        if(connectedBluetoothThread!=null)
            connectedBluetoothThread.write("c\n");
    }

    // remove a filter
    @Override
    public void removeField(Field field) {
        super.removeField(field);
        if(connectedBluetoothThread!=null)
            connectedBluetoothThread.write("r" + field.getHexId() + "\n");
    }

    // register a filter
    public void registerFilter(String filter)
    {
        //MainActivity.debug("registerFilter "+filter);
        if(connectedBluetoothThread!=null)
            connectedBluetoothThread.write("f" + filter + "\n");
        else
            MainActivity.debug("registerFilter "+filter+" failed because connectedBluetoothThread is NULL");
    }


}
