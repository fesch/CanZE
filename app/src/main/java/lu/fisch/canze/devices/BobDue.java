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
import java.util.Calendar;

import lu.fisch.canze.activities.MainActivity;
import lu.fisch.canze.actors.Field;
import lu.fisch.canze.actors.Frame;
import lu.fisch.canze.actors.Message;
import lu.fisch.canze.bluetooth.BluetoothManager;

/**
 * Created by robertfisch on 07.09.2015.
 */
public class BobDue extends Device {

    // *** needed by the "reader" part
    //private String buffer = "";
    //private final String separator = "\n";

    private static final int WRONG_THRESHOLD = 20;

    // define the timeout we may wait to get an answer
    private static final int TIMEOUT = 500;
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
            // prefix fir EOM to make sure the previous command is done!
            BluetoothManager.getInstance().write("\r\n"+command + "\r\n");
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
                        if ((ch == EOM || BluetoothManager.getInstance().available() <= 0) &&
                                !readBuffer.trim().isEmpty())  stop = true;
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

    private int wrongCount = 0;

    @Override
    public void clearFields() {
        super.clearFields();
        //fieldIndex =0;
    }

    @Override
    public Message requestFreeFrame(Frame frame) {
        // send the command and wait fir an answer, no delay
        String data = sendAndWaitForAnswer("g" + frame.getHexId(), 0);
        // handle empty answer
        if(data.trim().isEmpty()) wrongCount++;
        if(wrongCount>WRONG_THRESHOLD) {
            wrongCount=0;
            (new Thread(new Runnable() {
                @Override
                public void run() {
                    MainActivity.getInstance().stopBluetooth(false);
                    MainActivity.getInstance().reloadBluetooth(false);
                }
            })).start();
        }
        return responseToMessage(frame,data);
    }

    @Override
    public Message requestIsoTpFrame(Frame frame) {
        // build the command string to send to the remote device
        String command = "i" + frame.getHexId() + "," + frame.getRequestId() + "," + frame.getResponseId();
        String data = sendAndWaitForAnswer(command, 0);
        // handle empty answer
        if(data.trim().isEmpty()) wrongCount++;
        if(wrongCount>WRONG_THRESHOLD) {
            wrongCount=0;
            (new Thread(new Runnable() {
                @Override
                public void run() {
                    MainActivity.getInstance().stopBluetooth(false);
                    MainActivity.getInstance().reloadBluetooth(false);
                }
            })).start();
        }
        // send and wait for an answer, no delay
        return responseToMessage(frame,data);
    }

    private Message responseToMessage(Frame frame, String text)
    {
        // split up the fields
        String[] pieces = text.trim().split(",");
        if(pieces.length>1)
            return new Message(frame, pieces[1].trim(), false);
        else
        {
            MainActivity.debug("BobDue: Got > "+text.trim());
            return new Message(frame, "-E-Unexpected result", true);
        }
    }

    @Override
    public boolean initDevice(int toughness) {
        lastInitProblem = "";
        return true;
    }

    @Override
    protected boolean initDevice(int toughness, int retries) {
        return true;
    }
}
