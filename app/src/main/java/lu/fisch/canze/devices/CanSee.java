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
public class CanSee extends Device {

    // *** needed by the "reader" part
    //private String buffer = "";
    //private final String separator = "\n";

    // If there are more than WRONG_THRESHOLD empyy answer strings (meaning either just
    // EOM as answer or no answer in TIMEOUT milliseconds, Bluetooth will be restarted
    private static final int WRONG_THRESHOLD = 20;
    private int wrongCount = 0;

    // define the timeout we may wait to get an answer
    private static final int TIMEOUT_FREE = 250;
    private static final int TIMEOUT_ISO = 1000;
    // define End Of Message for this type of reader
    private static final char EOM = '\n';
    // the actual filter
    //private int fieldIndex = 0;
    // the thread that polls the data to the stack

    public void join() throws InterruptedException {
        pollerThread.join();
    }

    // send a command and wait for an answer
    private String sendAndWaitForAnswer(String command, int waitMillis, int timeout) {
        // empty incoming buffer. This is neccesary to ensure things get not horribly out of sync
        try {
            while (BluetoothManager.getInstance().available() > 0) {
                BluetoothManager.getInstance().read();
            }
        } catch (IOException e) {
            // ignore
        }
        // send the command
        if (command != null)
            // prefix fir EOM to make sure the previous command is done!
            //BluetoothManager.getInstance().write("\r\n"+command + "\r\n");
            BluetoothManager.getInstance().write(command + "\n");
        //MainActivity.debug("Send > "+command);
        // wait if needed
        if (waitMillis > 0)
            try {
                Thread.sleep(waitMillis);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        // init the buffer
        boolean stop = false;
        StringBuffer readBuffer = new StringBuffer();
        // wait for answer
        long start = Calendar.getInstance().getTimeInMillis();
        long runtime = 0;
        while (!stop && runtime < timeout) {
            //MainActivity.debug("Delta = "+(Calendar.getInstance().getTimeInMillis()-start));
            try {
                if (BluetoothManager.getInstance().available() > 0) { // read a byte
                    int data = BluetoothManager.getInstance().read();
                    //MainActivity.debug("Received byte:" + Integer.toHexString(data));
                    if (data != -1) {              // if it is a real one
                        char ch = (char) data;     // convert it to a character
                        if (ch == EOM) {           // stop if we reached the end or if no more data is available
                            stop = true;
                        } else {
                            // add it to the readBufferr
                            readBuffer.append(ch);
                        }
                    }
                } else {
                    //stop = true;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            runtime = Calendar.getInstance().getTimeInMillis() - start;
        }
        //MainActivity.debug("Recv < " + readBuffer + ", runtime:" + runtime);
        return readBuffer.toString().replaceAll("\\s", ""); // all whitespace is removed
    }

    @Override
    public void clearFields() {
        super.clearFields();
        //fieldIndex =0;
    }

    @Override
    public Message requestFreeFrame(Frame frame) {
        // build the command string to send to the remote device
        String command = "g" + frame.getHexId();
        return responseToMessage(frame, command, TIMEOUT_FREE);
    }

    @Override
    public Message requestIsoTpFrame(Frame frame) {
        // build the command string to send to the remote device
        String command = "i" + frame.getHexId() + "," + frame.getRequestId() + "," + frame.getResponseId();
        return responseToMessage(frame, command, TIMEOUT_ISO);
    }

    private Message responseToMessage(Frame frame, String command, int timeout) {
        //MainActivity.debug("CanSee.rtm.send [" + command + "]");
        String text = sendAndWaitForAnswer(command, 0, timeout);    // send and wait for an answer, no delay
        if (text.length() == 0) {
            //wrongCount++;
            if (wrongCount > WRONG_THRESHOLD) {
                wrongCount = 0;
                (new Thread(new Runnable() {
                    @Override
                    public void run() {
                        MainActivity.getInstance().stopBluetooth(false);
                        MainActivity.getInstance().reloadBluetooth(false);
                    }
                })).start();
            }
            return new Message(frame, "-E-CanSee.rtm.empty", true);
        }

        // split up the fields
        String[] pieces = text.trim().split(",");
        if (pieces.length < 2) {
            MainActivity.debug("CanSee.rtm.nocomma [" + text + "]");
            return new Message(frame, "-E-CanSee.rtm.nocomma:" + text, true);
        }

        int id;
        try {
            id = Integer.parseInt(pieces[0].trim(), 16);
        } catch (NumberFormatException e) {
            MainActivity.debug("CanSee.rtm.Nan [" + text + "]");
            return new Message(frame, "-E-CanSee.rtm.Nan:" + text, true);
        }

/*        if (frame.getId() < 0x700) {
            if (id != frame.getId()) {
                MainActivity.debug("CanSee.rtm.difffree [" + text + "]");
                return new Message(frame, "-E-CanSee.rtm.difffree", true);
            }
        } else {
            if (id < 0x700 || id > 0x7ff) { // crude but I don't know the exact behavior of CanSee here
                MainActivity.debug("CanSee.rtm.diffiso [" + text + "]");
                return new Message(frame, "CanSee.rtm.diffiso", true);
            }
        } */
        if (id != frame.getId()) {
            MainActivity.debug("CanSee.rtm.diffid [" + text + "]");
            return new Message(frame, "-E-CanSee.rtm.diffid:" + text, true);
        }

        // we could add answer length but that is not in the frame definition now
        //  everything is fine so
        wrongCount = 0;
        return new Message(frame, pieces[1].trim().toLowerCase(), false);
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
