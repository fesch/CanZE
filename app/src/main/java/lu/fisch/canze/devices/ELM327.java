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

import lu.fisch.canze.R;
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

    // Implementation of requestFreeFrame and requestIsotpFrame for the eLM327 style KONNWEI and
    // comparable drivers. Note that all ISOTP handling is done in this driver, as well as the
    // low level serial command interface.

    // Note that the output of the driver is an object of the type Message

    // This is a fairly complex driver dealing with subtle intricacies and it's better not touched
    // without extensive testing afterwards. Any assumpion may safely be considered wrong ;-)

    // define the Timeout we may wait to get an answer
    private static int DEFAULT_TIMEOUT = 500;
    private static int MINIMUM_TIMEOUT = 100;
    private int generalTimeout = 500;
    // define End Of Message for this type of reader
    private static final char EOM1 = '\r';
    private static final char EOM2 = '>';
    private static final char EOM3 = '?';

    private boolean deviceIsInitialized = false;

    private int lastId = 0; // used to skip sending unneeded commands
    private boolean lastCommandWasFreeFrame = false; // ditto


    protected boolean initDevice(int toughness, int retries) {
        if (initDevice(toughness)) return true;
        while (retries-- > 0) {
            MainActivity.debug("ELM327: flushWithTimeout");
            flushWithTimeout(500);
            MainActivity.debug("ELM327: initDevice(" + toughness + "), " + retries + " retries left");
            if (initDevice(toughness)) return true;
        }
        MainActivity.toast(MainActivity.TOAST_ELM, R.string.message_HardResetFailed);
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

        MainActivity.debug("ELM327: initDevice (" + toughness + ")");

        String response;
        int elmVersion = 0;

        lastInitProblem = "";

        // ensure the dongle header field is set again
        lastId = 0;

        // extremely soft, just clear the global error condition
        if (toughness == TOUGHNESS_NONE) {
            deviceIsInitialized = true;
            return deviceIsInitialized;
        }

        killCurrentOperation();

        if (toughness == TOUGHNESS_HARD || toughness == TOUGHNESS_MEDIUM) {
            // the default 500mS should be enough to answer, however, the answer contains various <cr>'s, so we need to set untilEmpty to true
            response = sendAndWaitForAnswer("atws", 0, true, -1, true);
        } else { // TOUGHNESS_WEAK
            response = sendAndWaitForAnswer("atd", 0, true, -1, true);
            MainActivity.debug("ELM327: version = " + response);
        }
        MainActivity.debug("ELM327: version: [" + response + "]");

        response = response.trim();
        if (response.equals("")) {
            lastInitProblem = "ELM is not responding (toughness = " + toughness + ")";
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


        deviceIsInitialized = false;


        // ***** CanZE version *******************************************************************
        // ate0         (no echo. At this point, echo is still on (except when atd was issued), so
        //              we still need to absorb the echoed command. After this point, echo is
        //              finally off so we can safely check for OK messages. If the app starts
        //              responding with toasts showing the responses in brackets equal to the
        //              commands, somehow the echo was not executed. so maybe we need to check for
        //              that specific condition in the next command.)
        // ats0         (no spaces)
        // atsp6        (CAN 500K 11 bit)
        // atat1        (auto timing)
        // atcaf0       (no formatting)
        // atfcsh77b    (flow control response ID to 77b. This is needed to be able to set the flow
        //               control response. Any ID would be fine
        // atfcsd300010 (the flow control response data to 300010 (flow control, clear to send,
        //               all frames, 16 ms wait between frames. Note that it is not possible to let
        //               the ELM request each frame as the Altered Flow Control only responds to a
        //               FRST, not a NEXT)
        // atfcsm1 (flow control mode 1: ID and data suplied)

        //String[] commands = "ate0;ats0;atsp6;atat1;atcaf0;atfcsh77b;atfcsd300010;atfcsm1".split(";");

        // ***** DDT4ALL version *****************************************************************
        // ate1         (echo on) ==> we don't do this
        // ats0         (no spaces)
        // ath0         (headers off = default)
        // atl0         (Linefeeds off)
        // atal         (allow long messages)
        // atcaf0       (no formatting)
        //              Here they stop initialization and do the remainder per ECU.
        //              We do ATSH and ATFCSH too per request (since we constantly switch ECU's,
        //              (we can optimize that, remember last ECU), however they also do flow control
        //              initialisation and bus speed, per ECU, which we prefer to do here.
        //              We also skip ATCRA (see comments at freeframe)
        // atfcsh77b    (flow control response ID to 77b. This is needed to be able to set the flow
        //               control response. Any ID would be fine
        // atfcsd300000 (the flow control response data to 300010 (flow control, clear to send,
        //               all frames, no wait between frames. Note that it is not possible to let
        //               the ELM request each frame as the Altered Flow Control only responds to a
        //               FRST, not a NEXT)
        // atsp6        (CAN 500K 11 bit) ==> might need to change that if we want to support
        //               pin re-assignment to ie the MM bus

        String[] commands = "ate0;ats0;ath0;atl0;atal;atcaf0;atfcsh77b;atfcsd300000;atfcsm1;atsp6".split(";");

        boolean first = true;
        for (String command : commands) {
            if (!initCommandExpectOk(command, first)) {
                lastInitProblem = command + " command problem";
                return deviceIsInitialized;
            }
            first = false;
        }

        if (toughness == TOUGHNESS_HARD) {
            switch (elmVersion) {
                case 13:
                    MainActivity.toast(MainActivity.TOAST_ELM, R.string.message_ELM13Ready);
                    break;
                case 14:
                    MainActivity.toast(MainActivity.TOAST_ELM, R.string.message_ELM13Ready);
                    break;
                case 15:
                    MainActivity.toast(MainActivity.TOAST_ELM, R.string.message_ELMReady);
                    break;
                case 20:
                    lastInitProblem = MainActivity.getStringSingle(R.string.message_ELM20Ready);
                    MainActivity.toast(MainActivity.TOAST_ELM, lastInitProblem);
                    break;
                case 8015:
                    MainActivity.toast(MainActivity.TOAST_ELM, R.string.message_ELM8015Ready);
                    break;

                // default should never be reached!!
                default:
                    lastInitProblem = MainActivity.getStringSingle(R.string.message_ELMUnknown);
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
        flushWithTimeoutCore(200, '\0');
        // if a command was running, it is interrupted now and the ELM is waiting for a command.
        // However, if there was no command running, the x in the buffer will screw up the next
        // command. There are two possibilities: Sending a Backspace and hope for the best, or
        // sending x <CR> and being sure the ELM will report an unknown command (prompt a ? mark),
        // as it will be processing either x <CR> or xx <CR>. We choose the latter and discard
        // the ? anser
        sendNoWait("x\r");
        if (!flushWithTimeoutCore(500, '\0')) {
            MainActivity.debug("ELM327: KillCurrentOperation unable to flush after x");
        }
    }

    private void flushWithTimeout(int timeout) {
        flushWithTimeout(timeout, '\0');
    }

    private void flushWithTimeout(int timeout, char eom) {
        if (flushWithTimeoutCore(timeout, eom)) return;
        killCurrentOperation();
    }

    private boolean flushWithTimeoutCore(int timeout, char eom) {
        // empty incoming buffer
        // just make sure there is no previous response
        // the ELM might be in a mode where it is spewing out data, and that might put this
        // method in an endless loop. If there are more than 100 character flushed, return false
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
                            if (c == (int) eom) return true;
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

    private boolean initCommandExpectOk(String command) {
        return initCommandExpectOk(command, false, true);
    }

    private boolean initCommandExpectOk(String command, boolean untilEmpty) {
        return initCommandExpectOk(command, untilEmpty, true);
    }

    private boolean initCommandExpectOk(String command, boolean untilEmpty, boolean addReturn) {
        String response = "";
        for (int i = 2; i > 0; i--) {
            if (untilEmpty) {
                response = sendAndWaitForAnswer(command, 40, true, -1, addReturn); // wait 40 ms for untilempty
            } else {
                response = sendAndWaitForAnswer(command, 0, false, -1, addReturn); // // just one line
            }
            if (response.toUpperCase().contains("OK")) return true; // we're done if we got an OK
            if (MainActivity.altFieldsMode) untilEmpty = true; // crappy dongles answer with things like CR > LF [space]
        }

        // we've tried and tried and failed here
        /* if (timeoutLogLevel >= 2 || (timeoutLogLevel >= 1 && !command.startsWith("atma") && command.startsWith("at"))) {
            MainActivity.toast("Err " + command + " [" + response.replace("\r", "<cr>").replace(" ", "<sp>") + "]");
        } */
        MainActivity.toast(MainActivity.TOAST_ELM, "Error [" + command + "] [" + response.replace("\r", "<cr>").replace(" ", "<sp>") + "]");
        MainActivity.debug("ELM327.initCommandExpectOk c:" + command + ", untilempty:" + untilEmpty + " res:" + response);

        return false;
    }

    private void sendNoWait(String command) {
        if (!BluetoothManager.getInstance().isConnected()) return;
        if (command != null) {
            BluetoothManager.getInstance().write(command);
        }
    }

    private String sendAndWaitForAnswer(String command, int waitMillis) {
        return sendAndWaitForAnswer(command, waitMillis, false, -1, true);
    }

    private String sendAndWaitForAnswer(String command, int waitMillis, int answerLinesCount) {
        return sendAndWaitForAnswer(command, waitMillis, false, answerLinesCount, true);
    }

    private String sendAndWaitForAnswer(String command, int waitMillis, boolean untilEmpty) {
        return sendAndWaitForAnswer(command, waitMillis, untilEmpty, -1, true);
    }

    private String sendAndWaitForAnswer(String command, int waitMillis, boolean untilEmpty, int answerLinesCount, boolean addReturn) {

        int maxUntilEmptyCounter = 10;
        int maxLengthCounter = 500; // char = nibble, so 2000 bits

        if (!BluetoothManager.getInstance().isConnected()) return "";

        if (command != null) {
            flushWithTimeout(10, '>');
            // send the command
            BluetoothManager.getInstance().write(command + (addReturn ? "\r" : ""));
        }

        MainActivity.debug("Send > "+command);
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
        StringBuilder readBuffer = new StringBuilder();
        // wait for answer
        long end = Calendar.getInstance().getTimeInMillis() + generalTimeout;
        boolean timedOut = false;
        while (!stop && !timedOut) {
            //MainActivity.debug("Delta = "+(Calendar.getInstance().getTimeInMillis()-start));
            try {
                // read a byte
                if (BluetoothManager.getInstance().isConnected() && BluetoothManager.getInstance().available() > 0) {
                    //MainActivity.debug("Reading ...");
                    int data = BluetoothManager.getInstance().read();
                    //MainActivity.debug("... done");
                    // if it is a real one
                    if (data != -1) {
                        // we might be JUST approaching the generalTimeout, so give it a chance to get to the EOM,
                        // end = end + 2;
                        // convert it to a character
                        char ch = (char) data;
                        if (ch == '\n') ch = '\r';
                        // add it to the readBuffer
                        readBuffer.append(ch);
                        // if we reach the end of a line
                        if (ch == EOM1 || ch == EOM2 || ch == EOM3) {
                            //MainActivity.debug("ALC: "+answerLinesCount+")\n"+readBuffer);
                            // decrease awaiting answer lines
                            answerLinesCount--;
                            // if we not asked to keep on and we got enough lines, stop
                            if (!untilEmpty) {
                                if (answerLinesCount <= 0) { // the number of lines is in
                                    //MainActivity.debug("ELM327: sendAndWaitForAnswer > stop on decimal char [" + data + "]");
                                    stop = true; // so quit
                                } else // the number of lines is NOT in
                                {
                                    end = Calendar.getInstance().getTimeInMillis() + generalTimeout; // so restart the timeout
                                }
                            } else { // if (untilEmpty) {
                                stop = (BluetoothManager.getInstance().available() == 0);
                                // a problem here is that we assume the next character is already available, which might not be the case, so adding.....
                                if (stop) {
                                    // wait a fraction
                                    try {
                                        Thread.sleep(50);
                                    } catch (InterruptedException e) {
                                        // do nothing
                                    }
                                    stop = (BluetoothManager.getInstance().available() == 0);
                                } else {
                                    if (--maxUntilEmptyCounter <= 0)
                                        timedOut = true; // well, this is a timed"In", as in, too many lines
                                }
                            }
                        } else {
                            if (--maxLengthCounter <= 0)
                                timedOut = true; // well, this is a timed"In", as in, too many lines
                        }

                    }
                } else {
                    // let the system breath if there was no data
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        // do nothing
                    }
                }

                if (Calendar.getInstance().getTimeInMillis() > end) {
                    timedOut = true;
                    // MainActivity.toast("Sum Ting Wong on command " + command);
                }

            } catch (IOException e) {
                // ignore: e.printStackTrace();
            }
        }

        // set the flag that a timeout has occurred. someThingWrong can be inspected anywhere, but we reset the device after a full filter has been run
        if (timedOut) {
            /* if (timeoutLogLevel >= 2 || (timeoutLogLevel >= 1 && (command==null || (!command.startsWith("atma") && command.startsWith("at"))))) {
                MainActivity.toast("Timeout on [" + command + "][" + readBuffer.replace("\r", "<cr>").replace(" ", "<sp>") + "]");
            } */
            MainActivity.toast(MainActivity.TOAST_ELM, "Timeout on [" + command + "] [" + readBuffer.toString().replace("\r", "<cr>").replace(" ", "<sp>") + "]");
            MainActivity.debug("ELM327: sendAndWaitForAnswer > timed out on [" + command + "] [" + readBuffer.toString().replace("\r", "<cr>").replace(" ", "<sp>") + "]");
            return ("");
        }

        //MainActivity.debug("ALC: "+answerLinesCount+" && Stop: "+stop+" && Delta: "+(Calendar.getInstance().getTimeInMillis()-start));
        MainActivity.debug("Recv < "+readBuffer);
        return readBuffer.toString();
    }

    @Override
    public void clearFields() {
        super.clearFields();
        //fieldIndex=0;
    }

    @Override
    public Message requestFreeFrame(Frame frame) {

        if (!deviceIsInitialized) {
            return new Message(frame, "-E-Re-initialisation needed", true);
        }

        String hexData;

        // ensure the ATCRA filter is reset in the next NON free frame request
        lastCommandWasFreeFrame = true;

        // EML needs the filter to be 3 hex symbols and contains the from CAN id of the ECU.
        // getFromIdHex returns 3 chars for 11 bit id, and 8 bits for a 29 bit id
        String emlFilter = frame.getFromIdHex();

        MainActivity.debug("ELM327: requestFreeFrame: atcra" + emlFilter);
        if (!initCommandExpectOk("atcra" + emlFilter))
            return new Message(frame, "-E-Problem sending atcra command", true);

        //sendAndWaitForAnswer("atcra" + emlFilter, 400);
        // atma     (wait for one answer line)
        generalTimeout = (int) (frame.getInterval() * intervalMultiplicator + 50);
        if (generalTimeout < MINIMUM_TIMEOUT) generalTimeout = MINIMUM_TIMEOUT;
        MainActivity.debug("ELM327: requestFreeFrame > TIMEOUT = " + generalTimeout);

        // 10 ms plus repeat time timeout, do not wait until empty, do not count lines, add \r to command
        hexData = sendAndWaitForAnswer("atma", frame.getInterval() + 10);

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
        if (hexData.equals(""))
            return new Message(frame, "-E-data empty", true);
        else
            return new Message(frame, hexData, false);
    }

    @Override
    public Message requestIsoTpFrame(Frame frame) {

        if (!deviceIsInitialized) {
            return new Message(frame, "-E-Re-initialisation needed", true);
        }

        String hexData;
        int len;

        // PERFORMANCE ENHANCEMENT: only send ATAR if coming from a free frame
        if (lastCommandWasFreeFrame) {
            // atar     (clear filter set by free frame capture method)
            if (!initCommandExpectOk("atar")) {
                return new Message(frame, "-E-Problem sending atar command", true);
            }
            lastCommandWasFreeFrame = false;
        }

        // PERFORMANCE ENHANCEMENT II: lastId contains the CAN id of the previous ISO-TP command. If
        // the current ID is the same, no need to re-address that ECU. Even if different ECU but
        // same id length, no change 11/29 bit mode needed either.
        // (re)enabled after 1.54 release
        // lastId = 0;
        if (lastId != frame.getFromId()) {
            // IIa: 11/29 bit mode change only if needed
            if (frame.isExtended()) {
                if (lastId < 0x1000) {
                    // switch to 29 bit
                    if (!initCommandExpectOk("atsp7",true))
                        return new Message(frame, "-E-Problem sending atsp7 command", true);
                    // set prio using AT CP
                    if (!initCommandExpectOk("atcp" + frame.getToIdHexMSB(), true))
                        return new Message(frame, "-E-Problem sending atcp command", true);
                }
            } else {
                if (lastId >= 0x1000 || lastId == 0) { // 0 check if optimization II is disabled
                    // switch to 11 bit
                    if (!initCommandExpectOk("atsp6", true))
                        return new Message(frame, "-E-Problem sending atsp6 command", true);
                }
            }

            // change ECU address
            // Set header
            if (!initCommandExpectOk("atsh" + frame.getToIdHexLSB()))
                return new Message(frame, "-E-Problem sending atsh command", true);
            // Set filter
            if (!initCommandExpectOk("atcra" + frame.getFromIdHex()))
                return new Message(frame, "-E-Problem sending atcra command", true);
            // Set flow control response ID
            if (!initCommandExpectOk("atfcsh" + frame.getToIdHex()))
                return new Message(frame, "-E-Problem sending atfcsh command", true);

            lastId = frame.getFromId();
        }

        // ISOTP outgoing starts here
        int outgoingLength = frame.getRequestId().length();
        String elmResponse = "";
        if (outgoingLength <= 14) {
            // SINGLE transfers up to 7 bytes. If we ever implement extended addressing (which is
            // not the same as 29 bits mode) this driver considers this simply data
            // 022104           ISO-TP single frame - length 2 - payload 2104, which means PID 21 (??), id 04 (see first tab).
            String elmCommand = "0" + (outgoingLength / 2) + frame.getRequestId();
            // send SING frame.
            elmResponse = sendAndWaitForAnswer(elmCommand, 0, false).replace("\r", "");
        } else {
            int startIndex = 0;
            int endIndex = 12;
            // send FRST frame.
            String elmCommand = String.format("1%03X", outgoingLength / 2) + frame.getRequestId().substring(startIndex, endIndex);
            //flushWithTimeout(500, '>');
            String elmFlowResponse = sendAndWaitForAnswer(elmCommand, 0, false).replace("\r", "");
            startIndex = endIndex;
            if (startIndex > outgoingLength) startIndex = outgoingLength;
            endIndex += 14;
            if (endIndex > outgoingLength) endIndex = outgoingLength;
            int next = 1;
            while (startIndex < outgoingLength) {
                // prepare NEXT frame.
                elmCommand = String.format("2%01X", next) + frame.getRequestId().substring(startIndex, endIndex);
                // for the moment we ignore block size, just 1 or all. Also ignore delay
                if (elmFlowResponse.startsWith("3000")) {
                    // The receiving ECU expects all data to be sent without further flow control,
                    // the ELM still answers with at least a \n after each sent frame.
                    // Since there are no further flow control frames, we just pretent the answer
                    // of each frame is the actual answer and won't change the FlowResponse
                    //flushWithTimeout(500, '>');
                    elmResponse = sendAndWaitForAnswer(elmCommand, 0, false).replace("\r", "");
                } else if (elmFlowResponse.startsWith("30")) {
                    // The receiving ECU expects the next frame of data to be sent, and it will
                    // respond with the next flow control command, or the actual answer. We just
                    // pretent the answer of the frame is both the actual answer as wel as the next
                    // FlowResponse
                    //flushWithTimeout(500, '>');
                    elmFlowResponse = sendAndWaitForAnswer(elmCommand, 0, false).replace("\r", "");
                    elmResponse = elmFlowResponse;
                } else {
                    return new Message(frame, "-E-ISOTP tx flow Error:" + elmFlowResponse, true);
                }
                startIndex = endIndex;
                if (startIndex > outgoingLength) startIndex = outgoingLength;
                endIndex += 14;
                if (endIndex > outgoingLength) endIndex = outgoingLength;
                if (next == 15) next = 0;
                else next++;
            }
        }

        // ISOTP receiver starts here
        // clean-up if there is mess around
        elmResponse = elmResponse.trim();
        if (elmResponse.startsWith(">")) elmResponse = elmResponse.substring(1);

        // quit on error conditions
        if (elmResponse.compareTo("CAN ERROR") == 0) {
            return new Message(frame, "-E-Can Error", true);
        } else if (elmResponse.compareTo("?") == 0) {
            return new Message(frame, "-E-Unknown command", true);
        } else if (elmResponse.compareTo("") == 0) {
            return new Message(frame, "-E-Empty result", true);
        }

        // get type (first nibble of first line)
        switch (elmResponse.substring(0, 1)) {
            case "0": // SINGLE frame
                try {
                    len = Integer.parseInt(elmResponse.substring(1, 2), 16);
                    // remove 2 nibbles (type + length)
                    hexData = elmResponse.substring(2);
                    // and we're done
                } catch (StringIndexOutOfBoundsException e) {
                    return new Message(frame, "-E-ISOTP rx unexpected length of SING frame:" + elmResponse, true);
                } catch (NumberFormatException e) {
                    return new Message(frame, "-E-ISOTP rx uninterpretable length of SING frame:" + elmResponse, true);
                }
                break;
            case "1": // FIRST frame
                try {
                    len = Integer.parseInt(elmResponse.substring(1, 4), 16);
                    // remove 4 nibbles (type + length)
                    hexData = elmResponse.substring(4);
                } catch (StringIndexOutOfBoundsException e) {
                    return new Message(frame, "-E-ISOTP rx unexpected length of FRST frame:" + elmResponse, true);
                } catch (NumberFormatException e) {
                    return new Message(frame, "-E-ISOTP rx uninterpretable length of FRST frame:" + elmResponse, true);
                }
                // calculate the # of frames to come. 6 byte are in and each of the 0x2 frames has a payload of 7 bytes
                int framesToReceive = len / 7; // read this as ((len - 6 [remaining characters]) + 6 [offset to / 7, so 0->0, 1-7->7, etc]) / 7
                // get all remaining 0x2 (NEXT) frames
                String lines0x1 = sendAndWaitForAnswer(null, 0, framesToReceive);
                // split into lines with hex data
                String[] hexDataLines = lines0x1.split("[\\r]+");
                int next = 1;
                for (String hexDataLine : hexDataLines) {
                    // ignore empty lines
                    hexDataLine = hexDataLine.trim();
                    if (hexDataLine.length() > 2) {
                        // check the proper sequence
                        if (hexDataLine.startsWith(String.format("2%01X", next))) {
                            // cut off the first byte (type + sequence) and add to the result
                            hexData += hexDataLine.substring(2);
                        } else {
                            return new Message(frame, "-E-ISOTP rx out of sequence:" + hexDataLine, true);
                        }
                        if (next == 15) next = 0;
                        else next++;
                    }
                }
                break;
            default:  // a NEXT, FLOWCONTROL should not be received. Neither should any other string (such as NO DATA)
                flushWithTimeout(400, '>');
                return new Message(frame, "-E-ISOTP rx unexpected 1st nibble of 1st frame:" + elmResponse, true);
        }


        // There was spurious error here, that immediately sending another command STOPPED the still not entirely finished ISO-TP command.
        // It was probably still sending "OK>" or just ">". So, the next command files and if it was i.e. an atcra f a free frame capture,
        // the following ATMA immediately overwhelmed the ELM as no filter was set.
        // As a solution, added this wait for a > after an ISO-TP command.

        flushWithTimeout(400, '>');
        len *= 2;

        // Having less data than specified in length is actually an error, but at least we do not need so substr it
        // if there is more data than specified in length, that is OK (filler bytes in the last frame), so cut those away
        hexData = (hexData.length() <= len) ? hexData.trim().toLowerCase() : hexData.substring(0, len).trim().toLowerCase();

        if (hexData.equals(""))
            return new Message(frame, "-E-ISOTP rx data empty", true);
        else
            return new Message(frame, hexData.toLowerCase(), false);
    }
}
