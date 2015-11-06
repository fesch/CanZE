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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;

import lu.fisch.canze.activities.MainActivity;
import lu.fisch.canze.actors.Field;
import lu.fisch.canze.actors.Message;
import lu.fisch.canze.actors.Utils;
import lu.fisch.canze.bluetooth.BluetoothManager;

/**
 * Created by robertfisch on 07.09.2015.
 */
public class BobDue extends Device {

    // *** needed by the "reader" part
    //private String buffer = "";
    //private final String separator = "\n";


    // define the timeout we may wait to get an answer
    private static final int TIMEOUT = 250;
    // define End Of Message for this type of reader
    private static final char EOM = '\n';
    // the actual filter
    //private int fieldIndex = 0;
    // the thread that polls the data to the stack

    public void join() throws InterruptedException {
        pollerThread.join();
    }

    @Override
    public void registerFilter(int frameId) {
        String filter = Integer.toHexString(frameId);
        if(BluetoothManager.getInstance().isConnected())
            BluetoothManager.getInstance().write("f" + filter + "\n");
        else
            MainActivity.debug("BobDue.registerFilter " + filter + " failed because connectedBluetoothThread is NULL");
    }

    @Override
    public void unregisterFilter(int frameId) {
        String filter = Integer.toHexString(frameId);
        if(BluetoothManager.getInstance().isConnected())
            BluetoothManager.getInstance().write("r" + filter + "\n");
        else
            MainActivity.debug("BobDue.unregisterFilter " + filter + " failed because connectedBluetoothThread is NULL");
    }

    /*
    @Override
    protected ArrayList<Message> processData(String inputString) {
        ArrayList<Message> result = new ArrayList<>();

        // add to buffer as characters
        buffer+=inputString;
        //MainActivity.debug("Buffer = "+buffer);

        // split by <new line>
        String[] messages = buffer.split(separator);
        // let assume the last message is fine
        int last = messages.length;
        // but if it is not, do not consider it
        if (!buffer.endsWith(separator)) last--;

        // process each message
        for (int i = 0; i < last; i++) {
            // decode into a frame
            //MainActivity.debug("Decoding: "+messages[i].trim());
            Message message = decodeFrame(messages[i].trim());
            // store if valid
            if (message != null)
                result.add(message);
        }
        // adapt the buffer
        if (!buffer.endsWith(separator))
            // retain the last uncompleted message
            buffer = messages[messages.length - 1];
        else
            // empty the entire buffer
            buffer = "";
        // we are done

        return result;
    }*/

    protected Message processData(String text) {
        // split up the fields
        String[] pieces = text.split(",");
        //MainActivity.debug("Pieces = "+pieces);
        //MainActivity.debug("Size = "+pieces.length);
        if(pieces.length==2) {
            try {
                // get the id
                int id = Integer.parseInt(pieces[0], 16);
                // get the data
                int[] data = Utils.toIntArray(pieces[1].trim());
                // create and return new frame
                return new Message(id, data);
            }
            catch(Exception e)
            {
                //MainActivity.debug("BAD: "+text);
                return null;
            }
        }
        else if(pieces.length>=3) {
            try {
                // get the id
                int id = Integer.parseInt(pieces[0], 16);
                // get the data
                int[] data = Utils.toIntArray(pieces[1].trim());
                // get the reply-ID
                Message f = new Message(id,data);
                //MainActivity.debug("ID = "+id+" / Data = "+data);
                //MainActivity.debug("THIRD: "+pieces[2].trim());
                f.setResponseId(pieces[2].trim());
                return f;
                /*
                // get checksum
                int chk = Integer.parseInt(pieces[2].trim(), 16);
                int check = 0;
                for(int i=0; i<data.length; i++)
                    check ^= data[i];
                // validate the checksum
                if(chk==check)
                    // create and return new frame
                    return new Frame(id, data);
                */
            }
            catch(Exception e)
            {
                //MainActivity.debug("BAD: "+text);
                return null;
            }
        }
        //MainActivity.debug("BAD: "+text);
        return null;
    }




    // send a command and wait for an answer
    private String sendAndWaitForAnswer(String command, int waitMillis)
    {
        // empty incoming buffer
        // just make sure there is no previous response
        try {
            while(BluetoothManager.getInstance().available()>0)
            {
                BluetoothManager.getInstance().read();
            }
        } catch (IOException e) {
            // ignore
        }
        // send the command
        if(command!=null)
            BluetoothManager.getInstance().write(command + "\r\n");
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
                if(BluetoothManager.getInstance().available()>0) {
                    //MainActivity.debug("Reading ...");
                    int data = BluetoothManager.getInstance().read();
                    //MainActivity.debug("... done");
                    // if it is a real one
                    if (data != -1) {
                        // convert it to a character
                        char ch = (char) data;
                        // add it to the readBuffer
                        readBuffer += ch;
                        // stop if we reached the end or if no more data is available
                        if (ch == EOM || BluetoothManager.getInstance().available() <= 0) stop = true;
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
    public void clearFields() {
        super.clearFields();
        fieldIndex=0;
    }

    @Override
    public String requestFreeFrame(Field field) {
        // send the command and wait fir an answer, no delay
        return sendAndWaitForAnswer("g" + field.getHexId(), 0);
    }

    @Override
    public String requestIsoTpFrame(Field field) {
        // build the command string to send to the remote device
        String command = "i" + field.getHexId() + "," + field.getRequestId() + "," + field.getResponseId();
        // send and wait fir an answer, no delay
        return sendAndWaitForAnswer(command, 0);
    }

    @Override
    public boolean initDevice(int toughness) { return true; }

    @Override
    protected boolean initDevice(int toughness, int retries) {
        return true;
    }
}
