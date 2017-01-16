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
import lu.fisch.canze.actors.Ecu;
import lu.fisch.canze.actors.Ecus;
import lu.fisch.canze.actors.Frame;
import lu.fisch.canze.actors.Message;
import lu.fisch.canze.bluetooth.BluetoothManager;

/**
 * Created by robertfisch on 07.09.2015.
 * Main loop fir ELM
 */
public class ELM327 extends Device {

    // *** needed by the "decoder" part of this device
    //private String buffer = "";
    //private final String SEPARATOR = "\r\n";


    // define the Timeout we may wait to get an answer
    private static int DEFAULT_TIMEOUT = 500;
    private static int MINIMUM_TIMEOUT = 100;
    private int generalTimeout = 500;
    // define End Of Message for this type of reader
    private static final char EOM1 = '\r';
    private static final char EOM2 = '>';
    private static final char EOM3 = '?';

    private boolean deviceIsInitialized = false;

    /**
     * the index of the actual field to request
     */
    private int lastId = 0;

    private boolean lastCommandWasFreeFrame = false;

    @Override
    public void registerFilter(int frameId) {
        // not needed for this device
    }

    @Override
    public void unregisterFilter(int frameId) {
        // not needed for this device
    }


    protected boolean initDevice (int toughness, int retries) {
        if (initDevice(toughness)) return true;
        while (retries-- > 0) {
            MainActivity.debug("ELM327: flushWithTimeout");
            flushWithTimeout(500);
            MainActivity.debug("ELM327: initDevice("+toughness+"), "+retries+" retries left");
            if (initDevice(toughness)) return true;
        }
        MainActivity.toast(MainActivity.TOAST_ELM, "Hard reset failed, restarting Bluetooth ...");
        MainActivity.debug("ELM327: Hard reset failed, restarting Bluetooth ...");

        ///----- WE ARE HERE INSIDE THE POLLER THREAD, SO
        ///----- JOINING CAN'T WORK!

        // ... but we don't want the next request to happen,
        // so we need to stop the poller here anyway, but
        // DO NOT JOIN IT!
        setPollerActive(false);

        (new Thread(new Runnable() {
            @Override
            public void run() {
                // -- give up and restart BT
                // stop BT without resetting the registered fields
                MainActivity.debug("ELM327: stopBluetooth (via MainActivity)");
                MainActivity.getInstance().stopBluetooth(false);
                // restart BT without reloading all settings
                MainActivity.debug("ELM327: reloadBluetooth (via MainActivity)");
                MainActivity.getInstance().reloadBluetooth(false);
            }
        })).start();

        return false;
    }


    public boolean initDevice(int toughness) {

        MainActivity.debug("ELM327: initDevice ("+toughness+")");

        String response;
        int elmVersion = 0;

        lastInitProblem = "";

        // ensure the dongle header field is set again
        lastId = 0;

        // extremely soft, just clear the global error condition
        if (toughness == TOUGHNESS_NONE){
            deviceIsInitialized = true;
            return deviceIsInitialized;
        }

        killCurrentOperation ();

        if (toughness == TOUGHNESS_HARD ) {
            // the default 500mS should be enough to answer, however, the answer contains various <cr>'s, so we need to set untilEmpty to true
            response = sendAndWaitForAnswer("atws", 0, true, -1 , true);
            MainActivity.debug("ELM327: version = "+response);
        }
        else if (toughness == TOUGHNESS_MEDIUM) {
            response = sendAndWaitForAnswer("atws", 0, true, -1 , true);
            MainActivity.debug("ELM327: version = "+response);
        }
        else { // TOUGHNESS_WEAK
            // not used
            response = sendAndWaitForAnswer("atd", 0, true, -1 , true);
            MainActivity.debug("ELM327: version = "+response);
        }

        if (response.trim().equals("")) {
            lastInitProblem = "ELM is not responding (toughness = "+toughness+")";
            MainActivity.toast(MainActivity.TOAST_ELM, lastInitProblem);
            return false;
        }

        // only do version control at a full reset
        if (toughness <= TOUGHNESS_MEDIUM) {
            if (response.toUpperCase().contains("V1.3")) {
                elmVersion = 13;
            } else if (response.toUpperCase().contains("V1.4")) {
                elmVersion = 14;
            } else if (response.toUpperCase().contains("V1.5")) {
                elmVersion = 15;
            } else if (response.toUpperCase().contains("V2.")) {
                elmVersion = 20;
            } else if (response.toUpperCase().contains("INNOCAR")) {
                elmVersion = 8015;
            } else {
                lastInitProblem = "Unrecognized ELM version response [" + response.replace("\r", "<cr>").replace(" ", "<sp>") + "]";
                MainActivity.toast(MainActivity.TOAST_ELM, lastInitProblem);
                return false;
            }
        }

        // at this point, echo is still on (except when atd was issued), so we still need to absorb the echoed command
        // ate0 (no echo)
        if (!initCommandExpectOk("ate0", true)) {
            lastInitProblem = "ATE0 command problem";
            deviceIsInitialized = false;
            return deviceIsInitialized;
        }

        // at this point, echo is finally off so we can safely check for OK messages. If the app starts responding with toasts showing the responses
        // in brackets equal to the commands, somehow the echo was not executed. so maybe we need to check for that specific condition in the next
        // command.

        // ats0 (no spaces)
        if (!initCommandExpectOk("ats0")) {
            lastInitProblem = "ATS0 command problem";
            deviceIsInitialized = false;
            return deviceIsInitialized;
        }

        // atsp6 (CAN 500K 11 bit)
        if (!initCommandExpectOk("atsp6")) {
            lastInitProblem = "ATSP6 command problem";
            deviceIsInitialized = false;
            return deviceIsInitialized;
        }

        // atat1 (auto timing)
        if (!initCommandExpectOk("atat1")) {
            lastInitProblem = "ATAT1 command problem";
            return false;
        }

        // atcaf0 (no formatting)
        if (!initCommandExpectOk("atcaf0")) {
            lastInitProblem = "ATCAF0 command problem";
            deviceIsInitialized = false;
            return deviceIsInitialized;
        }

        // atfcsh77b        Set flow control response ID to 77b. This is needed to set the flow control response, but that one is remembered :-)
        if (!initCommandExpectOk("atfcsh77b")) {
            lastInitProblem = "ATFCSH77B command problem";
            deviceIsInitialized = false;
            return deviceIsInitialized;
        }

        // atfcsd300020     Set the flow control response data to 300010 (flow control, clear to send,
        //                  all frames, 16 ms wait between frames. Note that it is not possible to let
        //                  the ELM request each frame as the Altered Flow Control only responds to a
        //                  First Frame (not a Next Frame)
        if (!initCommandExpectOk("atfcsd300010")) {
            lastInitProblem = "ATFCSD300010 command problem";
            deviceIsInitialized = false;
            return deviceIsInitialized;
        }

        // atfcsm1          Set flow control mode 1 (ID and data suplied)
        if (!initCommandExpectOk("atfcsm1")) {
            lastInitProblem = "ATFCSM1 command problem";
            deviceIsInitialized = false;
            return deviceIsInitialized;
        }

        if (toughness == TOUGHNESS_HARD ) {
            switch (elmVersion) {
                case 13:
                   MainActivity.toast(MainActivity.TOAST_ELM, "ELM ready, version 1.3, should work");
                    break;
                case 14:
                    MainActivity.toast(MainActivity.TOAST_ELM, "ELM ready, version 1.4, should work");
                    break;
                case 15:
                    MainActivity.toast(MainActivity.TOAST_ELM, "ELM is now ready");
                    break;
                case 20:
                    lastInitProblem = "ELM ready, version 2.x, will probably not work, please report if it does";
                    MainActivity.toast(MainActivity.TOAST_ELM, lastInitProblem);
                    break;
                case 8015:
                    MainActivity.toast(MainActivity.TOAST_ELM, "ELM ready, version innocar, should work");
                    break;

                // default should never be reached!!
                default:
                    lastInitProblem = "ELM ready, unknown version, will probably not work, please report if it does";
                    MainActivity.toast(MainActivity.TOAST_ELM, lastInitProblem);
                    break;
            }
        }

        deviceIsInitialized = true;
        return deviceIsInitialized;
    }

    private void killCurrentOperation() {
        // ensure any running operation is stopped
        // sending a return might restart the last command. Bad plan.
        sendNoWait("x");
        // discard everything that still comes in
        flushWithTimeoutCore (200, '\0');
        // if a command was running, it is interrupted now and the ELM is waiting for a command. However, if there was no command running, the x
        // in the buffer will screw up the next command. There are two possibilities: Sending a Backspace and hope for the best, or sending x <CR>
        // and being sure the ELM will report an unknow command (prompt a ? mark), as it will be processing either x <CR> or xx <CR>. We choose the latter
        // discard the ? anser
        sendNoWait("x\r");
        if (!flushWithTimeoutCore (500, '\0')) {
            MainActivity.debug("ELM327: KillCurrentOperation unable to flush after x");
        }
    }

    private void flushWithTimeout(int timeout) {
        flushWithTimeout(timeout, '\0');
    }

    private void flushWithTimeout(int timeout, char eom) {
        if (flushWithTimeoutCore(timeout, eom)) return;
        killCurrentOperation ();
    }

    private boolean flushWithTimeoutCore(int timeout, char eom) {
        // empty incoming buffer
        // just make sure there is no previous response
        // the ELM might be in a mode where it is spewing out data, and that might put this
        // mothod in an endless loop. If there are more than 200 character to flushed, return false
        // this should normally be followed by a failure and thus device re-initialisation

        int count = 100;

        try {
            // fast track, don't use expensive calendar.....
            if (timeout == 0) {
                while (BluetoothManager.getInstance().isConnected() && BluetoothManager.getInstance().available() > 0) {
                    BluetoothManager.getInstance().read();
                    if (count-- == 0) return false;
                }
            } else {
                long end = Calendar.getInstance().getTimeInMillis() + timeout;
                while (Calendar.getInstance().getTimeInMillis() < end) {
                    // read a byte
                    if (!BluetoothManager.getInstance().isConnected()) return false;
                    if (BluetoothManager.getInstance().available() > 0) {
                        // absorb the characters
                        while (BluetoothManager.getInstance().available() > 0) {
                            int c = BluetoothManager.getInstance().read();
                            if (c == (int)eom) return true;
                            if (count-- == 0) return false;

                        }
                        // restart the timer
                        end = Calendar.getInstance().getTimeInMillis() + timeout;
                    } else {
                        // let the system breath if there was no data
                        Thread.sleep(5);
                    }
                }
            }
        } catch (IOException | InterruptedException e) {
            // ignore
        }
        return true;
    }

    private boolean initCommandExpectOk (String command) {
        return initCommandExpectOk(command, false, true);
    }

    private boolean initCommandExpectOk (String command, boolean untilEmpty) {
        return initCommandExpectOk(command, untilEmpty, true);
    }

    private boolean initCommandExpectOk (String command, boolean untilEmpty, boolean addReturn) {
        MainActivity.debug("ELM327: initCommandExpectOk [" + command + "] untilempty [" + untilEmpty + "]");
        String response = "";
        for (int i = 2; i > 0; i--) {
            if (untilEmpty) {
                response = sendAndWaitForAnswer(command, 40, true, -1, addReturn); // wait 40 ms for untilempty
            } else {
                response = sendAndWaitForAnswer(command, 0, false, -1, addReturn); // // just one line
            }
            if (response.toUpperCase().contains("OK")) return true; // we're done if we got an OK
        }

        // we've tried and tried and failed here
        /* if (timeoutLogLevel >= 2 || (timeoutLogLevel >= 1 && !command.startsWith("atma") && command.startsWith("at"))) {
            MainActivity.toast("Err " + command + " [" + response.replace("\r", "<cr>").replace(" ", "<sp>") + "]");
        } */
        MainActivity.toast(MainActivity.TOAST_ELM, "Error [" + command + "] [" + response.replace("\r", "<cr>").replace(" ", "<sp>") + "]");
        MainActivity.debug("ELM327: initCommandExpectOk > Response was > " + response);

        return false;
    }

    private void sendNoWait(String command) {
        if(!BluetoothManager.getInstance().isConnected()) return;
        if(command!=null) {
            BluetoothManager.getInstance().write(command);
        }
    }

    private String sendAndWaitForAnswer(String command, int waitMillis) {
        return sendAndWaitForAnswer(command,waitMillis,false,-1, true);
    }

    private String sendAndWaitForAnswer(String command, int waitMillis, int answerLinesCount) {
        return sendAndWaitForAnswer(command,waitMillis,false,answerLinesCount, true);
    }

    private String sendAndWaitForAnswer(String command, int waitMillis, boolean untilEmpty) {
        return sendAndWaitForAnswer(command,waitMillis,untilEmpty,-1, true);
    }

    private String sendAndWaitForAnswer(String command, int waitMillis, boolean untilEmpty, int answerLinesCount, boolean addReturn)
    {

        int maxUntilEmptyCounter = 10;
        int maxLengthCounter = 500; // char = nibble, so 2000 bits

        if(!BluetoothManager.getInstance().isConnected()) return "";

        if(command!=null) {
            flushWithTimeout (10);
            // send the command
            //connectedBluetoothThread.write(command + "\r\n");
            BluetoothManager.getInstance().write(command + (addReturn ? "\r" : ""));
        }

        //MainActivity.debug("Send > "+command);
        // wait if needed (JM: tbh, I think waiting here is never needed. Any waiting should be handled in the wait for an answer timeout. But that's me.
/*        if(waitMillis>0) {
            try {
                Thread.sleep(waitMillis);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } */
        // init the buffer
        boolean stop = false;
        String readBuffer = "";
        // wait for answer
        long end = Calendar.getInstance().getTimeInMillis() + generalTimeout;
        boolean timedOut = false;
        while(!stop && !timedOut)
        {
            //MainActivity.debug("Delta = "+(Calendar.getInstance().getTimeInMillis()-start));
            try {
                // read a byte
                if(BluetoothManager.getInstance().isConnected() && BluetoothManager.getInstance().available()>0) {
                    //MainActivity.debug("Reading ...");
                    int data = BluetoothManager.getInstance().read();
                    //MainActivity.debug("... done");
                    // if it is a real one
                    if (data != -1) {
                        // we might be JUST approaching the generalTimeout, so give it a chance to get to the EOM,
                        // end = end + 2;
                        // convert it to a character
                        char ch = (char) data;
                        // add it to the readBuffer
                        readBuffer += ch;
                        // if we reach the end of a line
                        if (ch == EOM1 || ch == EOM2 || ch == EOM3)
                        {
                            //MainActivity.debug("ALC: "+answerLinesCount+")\n"+readBuffer);
                            // decrease awaiting answer lines
                            answerLinesCount--;
                            // if we not asked to keep on and we got enough lines, stop
                            if(!untilEmpty){
                                if(answerLinesCount<=0) { // the number of lines is in
                                    MainActivity.debug("ELM327: sendAndWaitForAnswer > stop on decimal char [" + data + "]");
                                    stop = true; // so quit
                                }
                                else // the number of lines is NOT in
                                {
                                    end = Calendar.getInstance().getTimeInMillis() + generalTimeout; // so restart the timeout
                                }
                            }
                            else { // if (untilEmpty) {
                                stop=(BluetoothManager.getInstance().available()==0);
                                // a problem here is that we assume the next character is already available, which might not be the case, so adding.....
                                if (stop) {
                                    // wait a fraction
                                    try {
                                        Thread.sleep(50);
                                    } catch (InterruptedException e) {
                                        // do nothing
                                    }
                                    stop=(BluetoothManager.getInstance().available()==0);
                                } else {
                                    if (--maxUntilEmptyCounter <= 0) timedOut = true; // well, this is a timed"In", as in, too many lines
                                }
                            }
                        } else {
                            if (--maxLengthCounter <= 0) timedOut = true; // well, this is a timed"In", as in, too many lines
                        }

                    }
                }
                else
                {
                    // let the system breath if there was no data
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                if (Calendar.getInstance().getTimeInMillis() > end) {
                    timedOut = true;
                    // MainActivity.toast("Sum Ting Wong on command " + command);
                }

            }
            catch (IOException e)
            {
                // ignore: e.printStackTrace();
            }
        }

        // set the flag that a timeout has occurred. someThingWrong can be inspected anywhere, but we reset the device after a full filter has been run
        if (timedOut) {
            /* if (timeoutLogLevel >= 2 || (timeoutLogLevel >= 1 && (command==null || (!command.startsWith("atma") && command.startsWith("at"))))) {
                MainActivity.toast("Timeout on [" + command + "][" + readBuffer.replace("\r", "<cr>").replace(" ", "<sp>") + "]");
            } */
            MainActivity.toast(MainActivity.TOAST_ELM, "Timeout on [" + command + "] [" + readBuffer.replace("\r", "<cr>").replace(" ", "<sp>") + "]");
            MainActivity.debug("ELM327: sendAndWaitForAnswer > timed out on [" + command + "] [" + readBuffer.replace("\r", "<cr>").replace(" ", "<sp>") + "]");
            return ("");
        }

        //MainActivity.debug("ALC: "+answerLinesCount+" && Stop: "+stop+" && Delta: "+(Calendar.getInstance().getTimeInMillis()-start));
        //MainActivity.debug("Recv < "+readBuffer);
        return readBuffer;
    }

    private int getToId(int fromId)
    {
        Ecu ecu = Ecus.getInstance().getByFromId(fromId);
        return ecu != null ? ecu.getToId() : 0;
    }

    private String getToIdHex(int fromId)
    {
        return Integer.toHexString(getToId(fromId));
    }

    @Override
    public void clearFields() {
        super.clearFields();
        //fieldIndex=0;
    }

    @Override
    public Message requestFreeFrame(Frame frame) {

        if (!deviceIsInitialized) {return new Message(frame, "-E-Re-initialisation needed", true); }

        String hexData;

        // ensure the ATCRA filter is reset in the next NON free frame request
        lastCommandWasFreeFrame = true;

        // EML needs the filter to be 3 symbols and contains the from CAN id of the ECU
        String emlFilter = frame.getHexId() + "";
        while (emlFilter.length() < 3) emlFilter = "0" + emlFilter;

        MainActivity.debug("ELM327: requestFreeFrame: atcra" + emlFilter);
        if (!initCommandExpectOk("atcra" + emlFilter)) return new Message(frame, "-E-Problem sending atcra command", true);

        //sendAndWaitForAnswer("atcra" + emlFilter, 400);
        // atma     (wait for one answer line)
        generalTimeout = (int) (frame.getInterval() * intervalMultiplicator + 50);
        if (generalTimeout < MINIMUM_TIMEOUT) generalTimeout = MINIMUM_TIMEOUT;
        MainActivity.debug("ELM327: requestFreeFrame > TIMEOUT = "+ generalTimeout);

        hexData = sendAndWaitForAnswer("atma", 20);

        MainActivity.debug("ELM327: requestFreeFrame > hexData = [" + hexData + "]");
        // the dongle starts babbling now. sendAndWaitForAnswer should stop at the first full line
        // ensure any running operation is stopped
        // sending a return might restart the last command. Bad plan.
        sendNoWait("x");
        // let it settle down, the ELM should indicate STOPPED then prompt >
        flushWithTimeout(100, '>');
        generalTimeout = DEFAULT_TIMEOUT;

        // atar     (clear filter)
        // AM has suggested the atar might not be neccesary as it might only influence cra filters and they are always set
        // however, make sure proper flushing is done
        // if cra does influence ISO-TP requests, an small optimization might be to only sending an atar when switching from free
        // frames to isotp frames.
        // if (!initCommandExpectOk("atar")) someThingWrong |= true;

        hexData = hexData.trim();
        if(hexData.equals(""))
            return new Message(frame, "-E-data empty", true);
        else
            return new Message(frame, hexData, false);
    }

    @Override
    public Message requestIsoTpFrame(Frame frame) {

        if (!deviceIsInitialized) {return new Message(frame, "-E-Re-initialisation needed", true); }

        String hexData;
        int len = 0;

        // PERFORMANCE ENHANCEMENT: only send ATAR if coming from a free frame
        if (lastCommandWasFreeFrame) {
            // atar     (clear filter set by free frame capture method)
            if (!initCommandExpectOk("atar")){
                return new Message(frame, "-E-Problem sending atar command", true);
            }
            lastCommandWasFreeFrame = false;
        }

        // PERFORMANCE ENHANCEMENT II: lastId contains the CAN id of the previous ISO-TP command. If the current ID is the same, no need to re-address that ECU
        lastId = 0;
        if (lastId != frame.getId()) {
            lastId = frame.getId();

            // request contains the to CAN id of the ECU. Note that we store the FromId in a frame
            String toIdHex = getToIdHex(frame.getId());

            // Set header
            if (!initCommandExpectOk("atsh" + toIdHex)) return new Message(frame, "-E-Problem sending atsh command", true);
            // Set flow control response ID
            if (!initCommandExpectOk("atfcsh" + toIdHex)) return new Message(frame, "-E-Problem sending atfcsh command", true);

        }

        // 022104           ISO-TP single frame - length 2 - payload 2104, which means PID 21 (??), id 04 (see first tab).
        String elmCommand = "0" + (frame.getRequestId().length() / 2) + frame.getRequestId();
        //MainActivity.debug("R: "+request+" - C: "+pre+field.getToId());

        // get 0x1 frame. No delays, and no waiting until done.
        String elmResponse = sendAndWaitForAnswer(elmCommand, 0, false).replace("\r", "");

        if (elmResponse.compareTo("CAN ERROR") == 0) {
            return new Message(frame, "-E-Can Error", true);
        }
        if (elmResponse.compareTo("?") == 0) {
            return new Message(frame, "-E-Unknown command", true);
        }
        if (elmResponse.compareTo("") == 0) {
            return new Message(frame, "-E-Empty result", true);
        }

        // process first line (SINGLE or FIRST frame)
        elmResponse = elmResponse.trim();
        // clean-up if there is mess around
        if (elmResponse.startsWith(">")) elmResponse = elmResponse.substring(1);

        if (elmResponse.isEmpty()) {
            return new Message(frame, "-E-unexpected ISO-TP 1st line empty", true);
        }

        // get type (first nibble)
        switch (elmResponse.substring(0, 1)) {
            case "0": // SINGLE frame
                len = Integer.parseInt(elmResponse.substring(1, 2), 16);
                // remove 2 nibbles (type + length)
                hexData = elmResponse.substring(2);
                break;
            case "1": // FIRST frame
                len = Integer.parseInt(elmResponse.substring(1, 4), 16);
                // remove 4 nibbles (type + length)
                hexData = elmResponse.substring(4);
                // calculate the # of frames to come. 6 byte are in and each of the 0x2 frames has a payload of 7 bytes
                int framesToReceive = len / 7; // read this as ((len - 6 [remaining characters]) + 6 [offset to / 7, so 0->0, 1-7->7, etc]) / 7
                // get remaining 0x2 (NEXT) frames
                String lines0x1 = sendAndWaitForAnswer(null, 0, framesToReceive);
                // split into lines
                //String[] hexDataLines = lines0x1.split(String.valueOf(EOM1));
                String[] hexDataLines = lines0x1.replaceAll("\n", "\r").split("[\\r]+");
                for (String hexDataLine : hexDataLines) {
                    elmResponse = hexDataLine;
                    //MainActivity.debug("Line "+(i+1)+": " + line);
                    if (!elmResponse.isEmpty() && elmResponse.length() > 2) {
                        // cut off the first byte (type + sequence)
                        // adding sequence checking would be wise to detect collisions
                        hexData += elmResponse.substring(2);
                    }
                }
                break;
            default:  // a NEXT, FLOWCONTROL should not be received. Neither should any other string (such as NO DATA)
                flushWithTimeout(400, '>');
                return new Message(frame, "-E-unexpected ISO-TP 1st nibble of first frame:" + elmResponse, true);
        }


        // There was spurious error here, that immediately sending another command STOPPED the still not entirely finished ISO-TP command.
        // It was probably still sending "OK>" or just ">". So, the next command files and if it was i.e. an atcra f a free frame capture,
        // the following ATMA immediately overwhelmed the ELM as no filter was set.
        // As a solution, added this wait for a > after an ISO-TP command.

        flushWithTimeout(400, '>');
        len *= 2;

        // Having less data than specified in length is actually an error, but at least we do not need so substr it
        // if there is more data than specified in length, that is OK (filler bytes in the last frame), so cut those away
        hexData = (hexData.length() <= len) ? hexData.trim() : hexData.substring(0, len).trim();

        if (hexData.equals(""))
            return new Message(frame, "-E-data empty", true);
        else
            return new Message(frame, hexData, false);
    }
}
