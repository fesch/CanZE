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

import lu.fisch.canze.MainActivity;
import lu.fisch.canze.actors.Stack;
import lu.fisch.canze.exeptions.NoDecoderException;

/**
 * Created by robertfisch on 27.08.2015.
 */
public class DueReader extends DataReader {
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
        // send the command
        if(command!=null)
            connectedBluetoothThread.write(command + "\r\n");
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
        // wait for
        while(!stop)
        {
            try {
                // read a byte
                int data = connectedBluetoothThread.read();
                // if it is a real one
                if(data!=-1)
                {
                    // convert it to a character
                    char ch = (char) data;
                    // add it to the readBuffer
                    readBuffer+=ch;
                    // stop if we reached the end or if no more data is available
                    if(ch==EOM || connectedBluetoothThread.available()<=0) stop=true;
                }
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
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
        if(filters.size()>0) {
            try {
                // get filter ID
                String filter = filters.get(filterIndex);
                // request the response from the device
                //MainActivity.debug("Requesting: " + filter);
                String hexData = sendAndWaitForAnswer("g" + filter, 0);
                // send it to the stack
                try {
                    stack.process(hexData);
                } catch (NoDecoderException e) {
                    e.printStackTrace();
                }
                // goto next filter
                filterIndex = (filterIndex + 1) % filters.size();
            }
            catch (Exception e)
            {
                // ignore
            }
        }
        else
        {
            // ignore
        }
    }

    // clean all filters
    @Override
    public void clearFilters() {
        super.clearFilters();
        if(connectedBluetoothThread!=null)
            connectedBluetoothThread.write("c\n");
    }

    // remove a filter
    @Override
    public void removeFilter(String filter) {
        super.removeFilter(filter);
        if(connectedBluetoothThread!=null)
            connectedBluetoothThread.write("r" + filter + "\n");
    }

    // register a filter
    public void registerFilter(String filter)
    {
        if(connectedBluetoothThread!=null)
            connectedBluetoothThread.write("f" + filter + "\n");
    }


}
