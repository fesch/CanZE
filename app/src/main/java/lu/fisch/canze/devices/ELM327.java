package lu.fisch.canze.devices;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;

import lu.fisch.canze.MainActivity;
import lu.fisch.canze.actors.Field;
import lu.fisch.canze.actors.Message;
import lu.fisch.canze.actors.Utils;

/**
 * Created by robertfisch on 07.09.2015.
 * Main loop fir ELM
 */
public class ELM327 extends Device {

    // *** needed by the "decoder" part of this device
    private String buffer = "";
    private final String SEPARATOR = "\r\n";

    // *** needed by the "reader" part of this device

    // define the timeout we may wait to get an answer
    private int DEFAULT_TIMEOUT = 500;
    private int TIMEOUT = 500;
    // define End Of Message for this type of reader
    private static final char EOM1 = '\r';
    private static final char EOM2 = '>';
    private static final char EOM3 = '?';

    private int timeoutLogLevel = 1; // 0 = none, 1=only ELM issues, 2=elm and car issues

    /**
     * the index of the actual field to request
     */
    private int fieldIndex = 0;
    private int lastId = 0;

    boolean sumTingWong = false; // yes I know, the fake news name of Asiana flight 214 777 captain that crashed in SF

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
            // post a task to the UI thread
            Runnable r = new Runnable() {
                @Override
                public void run() {
                    // atz / atws (reset)
                    // continue only if we got an answer.
                    if (initELM(0)) {

                        setPollerActive(true);
                        while (isPollerActive()) {
                            // if the no field is to be queried, sleep for a while
                            if(fields.size()==0)
                            {
                                if(connectedBluetoothThread!=null)
                                    try{
                                        Thread.sleep(5000);
                                    }
                                    catch (Exception e) {}
                            }
                            // query a field
                            else {
                                queryNextFilter();
                            }
                        }
                        MainActivity.debug("ELM: poller is done!");
                        pollerThread=null;
                    } else {
                        MainActivity.debug("ELM: no answer ...");
                        if (timeoutLogLevel >= 1) MainActivity.toast("Retrying ...");
                        if(connectedBluetoothThread!=null) {
                            // retry
                            pollerThread = new Thread(this);
                            pollerThread.start();
                        }
                    }
                }
            };
            pollerThread = new Thread(r);
            pollerThread.start();
        }
    }

    @Override
    public void join() throws InterruptedException {
        if(pollerThread!=null)
            pollerThread.join();
    }

    @Override
    public void registerFilter(int frameId) {
        // not needed for this device
    }

    @Override
    public void unregisterFilter(int frameId) {
        // not needed for this device
    }

    @Override
    protected ArrayList<Message> processData(int[] input) {
        ArrayList<Message> result = new ArrayList<>();

        // add to buffer as characters
        for (int anInput : input) {
            buffer += (char) anInput;
        }

        //MainActivity.debug("Buffer: "+buffer);

        // split by <new line>
        String[] messages = buffer.split(SEPARATOR);
        // let assume the last message is fine
        int last = messages.length;
        // but if it is not, do not consider it
        if (!buffer.endsWith(SEPARATOR)) last--;

        // process each message
        for (int i = 0; i < last; i++) {
            // decode into a frame
            Message message = lineToMessage(messages[i].trim());
            // store if valid
            if (message != null)
                result.add(message);
        }
        // adapt the buffer
        if (!buffer.endsWith(SEPARATOR))
            // retain the last uncompleted message
            buffer = messages[messages.length - 1];
        else
            // empty the entire buffer
            buffer = "";
        // we are done

        return result;
    }

    private boolean initELM (int toughness, int retries) {
        boolean ret;
        if (ret = initELM(toughness)) return ret;
        while (retries-- > 0) {
            flushWithTimeout(500);
            if (ret = initELM(toughness)) return ret;
        }
        if (timeoutLogLevel >= 1) MainActivity.toast("Hard reset failed");

        // -- give up and restart BT
        // stop BT
        MainActivity.getInstance().stopBluetooth();
        // restart BT
        MainActivity.getInstance().reloadBluetooth();

        return false;
    }


    private boolean initELM (int toughness) {

        String response;
        int elmVersion = 0;

        // ensure the decoder (processData) is reset
        buffer = "";

        // ensure the dongle header field is set again
        lastId = 0;

        // ensure any running operation is stopped
        // sending a return might restart the last command. Bad plan.
        sendNoWait("x");
        // discard everything that still comes in
        flushWithTimeout(200);
        // if a command was running, it is interrupted now and the ELM is waiting for a command. However, if there was no command running, the x
        // in the buffer will screw up the next command. There are two possibilities: Sending a Backspace and hope for the best, or sending x <CR>
        // and being sure the ELM will report an unknow command (prompt a ? mark), as it will be processing either x <CR> or xx <CR>. We choose the latter
        // discard the ? anser
        sendNoWait("x\r");
        flushWithTimeout(500);

        if (toughness == 0 ) {
            // the default 500mS should be enough to answer, however, the answer contains various <cr>'s, so we need to set untilEmpty to true
            response = sendAndWaitForAnswer("atws", 0, true, -1 , true);
        }
        else if (toughness == 1) {
            response = sendAndWaitForAnswer("atws", 0, true, -1 , true);
        }
        else {
            // not used
            response = sendAndWaitForAnswer("atd", 0, true, -1 , true);
        }

        if (response.trim().equals("")) {
            if (timeoutLogLevel >= 1) MainActivity.toast("ELM is not responding");
            return false;
        }

        // only do version control at a full reset
        if (toughness <= 1) {
            if (response.toUpperCase().contains("V1.4")) {
                elmVersion = 14;
            } else if (response.toUpperCase().contains("V1.5")) {
                elmVersion = 15;
            } else if (response.toUpperCase().contains("V2.")) {
                elmVersion = 20;
            } else if (response.toUpperCase().contains("INNOCAR")) {
                elmVersion = 8015;
            } else {
                if (timeoutLogLevel >= 1) MainActivity.toast("Unrecognized ELM version response [" + response.replace("\r", "<cr>").replace(" ", "<sp>") + "]");
                return false;
            }
        }

        // at this point, echo is still on (except when atd was issued), so we still need to absorb the echoed command
        // ate0 (no echo)
        if (!initCommandExpectOk("ate0", true)) return false;

        // at this point, echo is finally off so we can safely check for OK messages. If the app starts responding with toasts showing the responses
        // in brackets equal to the commands, somehow the echo was not executed. so maybe we need to check for that specific condition in the next
        // command.

        // ats0 (no spaces)
        if (!initCommandExpectOk("ats0")) return false;

        // atsp6 (CAN 500K 11 bit)
        if (!initCommandExpectOk("atsp6")) return false;

        // atat1 (auto timing)
        if (!initCommandExpectOk("atat1")) return false;

        // atcaf0 (no formatting)
        if (!initCommandExpectOk("atcaf0")) return false;

        // atfcsh79b        Set flow control response ID to 79b (the LBC) This is needed to set the flow control response, but that one is remembered :-)
        if (!initCommandExpectOk("atfcsh77b")) return false;

        // atfcsd300020     Set the flow control response data to 300010 (flow control, clear to send,
        //                  all frames, 16 ms wait between frames. Note that it is not possible to let
        //                  the ELM request each frame as the Altered Flow Control only responds to a
        //                  First Frame (not a Next Frame)
        if (!initCommandExpectOk("atfcsd300010")) return false;

        // atfcsm1          Set flow control mode 1 (ID and data suplied)
        if (!initCommandExpectOk("atfcsm1")) return false;

        if (toughness == 0 ) {
            switch (elmVersion) {
                case 14:
                    if (timeoutLogLevel >= 1) MainActivity.toast("ELM ready, version 1.4, should work");
                    break;
                case 15:
                    if (timeoutLogLevel >= 1) MainActivity.toast("ELM is now ready");
                    break;
                case 20:
                    if (timeoutLogLevel >= 1) MainActivity.toast("ELM ready, version 2.x, will probably not work, please report if it does");
                    break;
                case 8015:
                    if (timeoutLogLevel >= 1) MainActivity.toast("ELM ready, version innocar, should work");
                    break;

                // default should never be reached!!
                default:
                    if (timeoutLogLevel >= 1) MainActivity.toast("ELM ready, unknown version, will probably not work, please report if it does");
                    break;
            }
        }

        return true;
    }

    void flushWithTimeout (int timeout) {
        flushWithTimeout(timeout, '\0');
    }

    void flushWithTimeout (int timeout, char eom) {
        // empty incoming buffer
        // just make sure there is no previous response
        try {
            // fast track.....
            if (timeout == 0) {
                if (connectedBluetoothThread != null && connectedBluetoothThread.available() > 0) {
                    connectedBluetoothThread.read();
                }
            } else {
                long end = Calendar.getInstance().getTimeInMillis() + timeout;
                while (Calendar.getInstance().getTimeInMillis() < end) {
                    // read a byte
                    if (connectedBluetoothThread == null) return;
                    if (connectedBluetoothThread.available() > 0) {
                        // absorb the characters
                        while (connectedBluetoothThread.available() > 0) {
                            int c = connectedBluetoothThread.read();
                            if (c == (int)eom) return;

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
    }

    private boolean initCommandExpectOk (String command) {
        return initCommandExpectOk(command, false);
    }

    private boolean initCommandExpectOk (String command, boolean untilEmpty) {
        String response = "";
        for (int i = 2; i > 0; i--) {
            if (untilEmpty) {
                response = sendAndWaitForAnswer(command, 40, true, -1, true);
            } else {
                response = sendAndWaitForAnswer(command, 0);
            }
            if (response.toUpperCase().contains("OK")) return true;
        }

        if (timeoutLogLevel >= 2 || (timeoutLogLevel >= 1 && !command.startsWith("atma") && command.startsWith("at"))) {
            MainActivity.toast("Err " + command + " [" + response.replace("\r", "<cr>").replace(" ", "<sp>") + "]");
        }
        return false;
    }

    /**
     * Creates a message based on the data of a line
     * @param text what came back from the ELM
     * @return Message
     */
    private Message lineToMessage(String text) {
        // split up the fields
        String[] pieces = text.split(",");
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
                return null;
            }
        }
        else if(pieces.length==3) {
            try {
                // get the id
                int id = Integer.parseInt(pieces[0], 16);
                // get the data
                int[] data = Utils.toIntArray(pieces[1].trim());
                // get the reply-ID
                Message f = new Message(id,data);
                //MainActivity.debug("THIRD: "+pieces[2].trim());
                f.setResponseId(pieces[2].trim());
                return f;
            }
            catch(Exception e)
            {
                //MainActivity.debug("BAD: "+text);
                return null;
            }
        }
        return null;
    }

    private void sendNoWait(String command) {
        if(connectedBluetoothThread==null) return;
        if(command!=null) {
            connectedBluetoothThread.write(command);
        }
    }

    // send a command and wait for an answer
    private String sendAndWaitForAnswer(String command, int waitMillis) {
        return sendAndWaitForAnswer(command,waitMillis,false,-1, true);
    }

    private String sendAndWaitForAnswer(String command, int waitMillis, int answerLinesCount) {
        return sendAndWaitForAnswer(command,waitMillis,false,answerLinesCount, true);
    }

    private String sendAndWaitForAnswer(String command, int waitMillis, boolean untilEmpty) {
        return sendAndWaitForAnswer(command,waitMillis,untilEmpty,-1, true);
    }

    private String sendAndWaitForAnswer(String command, int waitMillis, boolean untilEmpty, int answerLinesCount) {
        return sendAndWaitForAnswer(command,waitMillis,untilEmpty,answerLinesCount, true);
    }

    // send a command and wait for an answer
    private String sendAndWaitForAnswer(String command, int waitMillis, boolean untilEmpty, int answerLinesCount, boolean addReturn)
    {
        if(connectedBluetoothThread==null) return "";

        if(command!=null) {
            flushWithTimeout (10);
            // send the command
            //connectedBluetoothThread.write(command + "\r\n");
            connectedBluetoothThread.write(command + (addReturn? "\r" : ""));
        }

        //MainActivity.debug("Send > "+command);
        // wait if needed (JM: tbh, I think waiting here is never needed. Any waiting should be handled in the wait for an answer timeout. But that's me.
        if(waitMillis>0) {
            try {
                Thread.sleep(waitMillis);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        // init the buffer
        boolean stop = false;
        String readBuffer = "";
        // wait for answer
        long end = Calendar.getInstance().getTimeInMillis() + TIMEOUT;
        boolean timedOut = false;
        while(!stop && !timedOut)
        {
            //MainActivity.debug("Delta = "+(Calendar.getInstance().getTimeInMillis()-start));
            try {
                // read a byte
                if(connectedBluetoothThread!=null && connectedBluetoothThread.available()>0) {
                    //MainActivity.debug("Reading ...");
                    int data = connectedBluetoothThread.read();
                    //MainActivity.debug("... done");
                    // if it is a real one
                    if (data != -1) {
                        // we might be JUST approaching the TIMEOUT, so give it a chance to get to the EOM,
                        end = end + 2;
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
                                    stop = true; // so quit
                                }
                                else // the number of lines is NOT in
                                {
                                    end = Calendar.getInstance().getTimeInMillis() + TIMEOUT; // so restart the timeout
                                }
                            }
                            else { // if (untilEmpty) {
                                stop=(connectedBluetoothThread.available()==0);
                                // a problem here is that we assume the next character is already available, which might not be the case, so adding.....
                                if (stop) {
                                    // wait a fraction
                                    try {
                                        Thread.sleep(5);
                                    } catch (InterruptedException e) {
                                    }
                                    stop=(connectedBluetoothThread.available()==0);
                                }
                            }
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

        // set the flag that a timeout has occurred. sumTingWong can be inspected anywhere, but we reset the device after a full filter has been run
        if (timedOut) {
            if (timeoutLogLevel >= 2 || (timeoutLogLevel >= 1 && !command.startsWith("atma") && command.startsWith("at"))) {
                MainActivity.toast("Timeout on [" + command + "][" + readBuffer.replace("\r", "<cr>").replace(" ", "<sp>") + "]");
            }
            sumTingWong |= true;
            return ("");
        }

        //MainActivity.debug("ALC: "+answerLinesCount+" && Stop: "+stop+" && Delta: "+(Calendar.getInstance().getTimeInMillis()-start));
        //MainActivity.debug("Recv < "+readBuffer);
        return readBuffer;
    }

    private int getRequestId(int responseId)
    {                     //from        // to
        if     (responseId==0x7ec) return 0x7e4;  // EVC / SCH
        else if(responseId==0x7da) return 0x7ca;  // TCU
        else if(responseId==0x7bb) return 0x79b;  // LBC
        else if(responseId==0x77e) return 0x75a;  // PEB
        else if(responseId==0x772) return 0x752;  // Airbag
        else if(responseId==0x76d) return 0x74d;  // USM / UDP
        else if(responseId==0x763) return 0x743;  // CLUSTER / instrument panel
        else if(responseId==0x762) return 0x742;  // PAS
        else if(responseId==0x760) return 0x740;  // ABS
        else if(responseId==0x7bc) return 0x79c;  // UBP
        else if(responseId==0x765) return 0x745;  // BCM
        else if(responseId==0x764) return 0x744;  // CLIM
        else if(responseId==0x76e) return 0x74e;  // UPA
        else if(responseId==0x793) return 0x792;  // BCB
        else if(responseId==0x7b6) return 0x796;  // LBC2
        else if(responseId==0x722) return 0x702;  // LINSCH
        else return -1;
    }

    private String getRequestHexId(int responseId)
    {
        return Integer.toHexString(getRequestId(responseId));
    }

    // query the device for the next filter
    private void queryNextFilter()
    {
        //MainActivity.debug("queryNextFilter: "+fieldIndex);
        try {
            if(fields.size()>0) {
                //MainActivity.debug("Index: "+fieldIndex);

                // get field
                Field field;
                boolean runFilter;

                synchronized (fields) {
                    field = fields.get(fieldIndex);
                }

                MainActivity.debug("queryNextFilter: "+fieldIndex+" --> "+field.getSID()+" \tSkipsCount = "+field.getSkipsCount());

                // only run the filter if the skipsCount is down to zero
                runFilter = (field.getSkipsCount() == 0);
                if (runFilter)
                    // reset it to its initial value
                    field.resetSkipsCount();
                else
                    // decrement the skipsCount
                    field.decSkipCount();

                if (runFilter) {
                    String data = "";
                    if (field.isIsoTp()) {
                        data = requestIsoTpFrame(field);
                        if (!sumTingWong && !data.equals("")) {
                            data = field.getHexId() + "," + data.trim() + "," + field.getResponseId() + SEPARATOR;
                            process(Utils.toIntArray(data.getBytes()));
                        }

                    } else {
                        data = requestFreeFrame(field);
                        if (!sumTingWong && !data.equals("")) {
                            data = field.getHexId() + "," + data.trim() + SEPARATOR;
                            process(Utils.toIntArray(data.getBytes()));
                        }
                    }

                    // reset the ELM if a timeout occurred somewhere running this filter
                    // ... but only if we are not asked to stop!
                    if (sumTingWong && connectedBluetoothThread!=null) {
                        //MainActivity.toast("... in command " + emlFilter + ", resetting ELM");
                        initELM(1, 2);
                        sumTingWong = false;
                    }
                }

                // move on to the next field
                synchronized (fields) {
                    if(fields.size()==0)
                        fieldIndex=0;
                    else
                        fieldIndex = (fieldIndex + 1) % fields.size();
                }
            }

        }
        catch (Exception e)
        {
            e.printStackTrace();
            // move on to the next field
            synchronized (fields) {
                if(fields.size()==0)
                    fieldIndex=0;
                else
                    fieldIndex = (fieldIndex + 1) % fields.size();
            }
        }
    }

    @Override
    public void clearFields() {
        super.clearFields();
        fieldIndex=0;
    }

    @Override
    public String requestFreeFrame(Field field) {

        String hexData = "";

        // EML needs the filter to be 3 symbols and contains the from CAN id of the ECU
        String emlFilter = field.getHexId() + "";
        while (emlFilter.length() < 3) emlFilter = "0" + emlFilter;

        if (!initCommandExpectOk("atcra" + emlFilter)) sumTingWong |= true;

        // avoid starting an ATMA id the ATCRA failed
        if (!sumTingWong) {
            //sendAndWaitForAnswer("atcra" + emlFilter, 400);
            // atma     (wait for one answer line)
            TIMEOUT = field.getFrequency() + 50;
            if (TIMEOUT < 70) TIMEOUT = 70;
            hexData = sendAndWaitForAnswer("atma", 20);
            // the dongle starts babbling now. sendAndWaitForAnswer should stop at the first full line
            // ensure any running operation is stopped
            // sending a return might restart the last command. Bad plan.
            sendNoWait("x");
            // let it settle down, the ELM should indicate STOPPED then prompt >
            flushWithTimeout(100, '>');
            TIMEOUT = DEFAULT_TIMEOUT;
        }
        // atar     (clear filter)
        // AM has suggested the atar might not be neccesary as it might only influence cra filters and they are always set
        // however, make sure proper flushing is done
        // if cra does influence ISO-TP requests, an small optimization might be to only sending an atar when switching from free
        // frames to isotp frames.
        if (!initCommandExpectOk("atar")) sumTingWong |= true;
        return hexData;
    }

    @Override
    public String requestIsoTpFrame(Field field) {

        String hexData = "";

        // PERFORMANCE ENHANCEMENT II: lastId contains the CAN id of the previous ISO-TP command. If the current ID is the same, no need to re-address that ECU
        if (lastId != field.getId()) {
            lastId = field.getId();

            // request contains the to CAN id of the ECU
            String request = getRequestHexId(field.getId());

            // Set header
            if (!initCommandExpectOk("atsh" + request)) sumTingWong |= true;
            // Set flow control response ID
            if (!initCommandExpectOk("atfcsh" + request)) sumTingWong |= true;

        }

        // 022104           ISO-TP single frame - length 2 - payload 2104, which means PID 21 (??), id 04 (see first tab).
        String pre = "0" + field.getRequestId().length() / 2;
        //MainActivity.debug("R: "+request+" - C: "+pre+field.getRequestId());

        // get 0x1 frame. No delays, and no waiting until done.
        String line0x1 = sendAndWaitForAnswer(pre + field.getRequestId(), 0, false);

        if (!sumTingWong) {
            // process first line (SINGLE or FIRST frame)
            line0x1 = line0x1.trim();
            // clean-up if there is mess around
            if (line0x1.startsWith(">")) line0x1 = line0x1.substring(1);
            sumTingWong |= line0x1.isEmpty();
        }
        if (!sumTingWong) {
            // get type (first nibble)
            String type = line0x1.substring(0, 1);
            //MainActivity.debug("Type: "+type);

            switch (type) {
                case "0": // SINGLE frame
                    // remove 2 nibbles (type + length)
                    hexData = line0x1.substring(2);
                    break;
                case "1": // FIRST frame
                    // remove first nibble (type)
                    line0x1 = line0x1.substring(1);
                    //MainActivity.debug("HEX-length = " + line0x1.substring(0, 3));
                    // get the number of payload bytes
                    int count = Integer.valueOf(line0x1.substring(0, 3), 16);
                    //MainActivity.debug("DEC-length = " + count);
                    // remove 3 nibbles (number of payload bytes)
                    line0x1 = line0x1.substring(3);
                    // remove response
                    //line0x1 = line0x1.substring(field.getRequestId().length());
                    // store the reminding bytes
                    hexData = line0x1;
                    // decrease count
                    count -= line0x1.length() / 2;
                    // each of the 0x2 frames has a payload of 7 bytes
                    int framesToReceive = (int) Math.ceil(count / 7.);
                    //MainActivity.debug("framesToReceive = " + framesToReceive);

                    // get remaining 0x2 (NEXT) frames
                    String lines0x2 = sendAndWaitForAnswer(null, 0, framesToReceive);

                    // PERFORMANE ENHACMENT
                    // atfcsm0          Reset flow control mode to 0 (default)
                    // sendAndWaitForAnswer("atfcsm0", 50);

                    //MainActivity.debug("Got:\n"+lines0x2);

                    // split into lines
                    String[] hexDataLines = lines0x2.split(String.valueOf(EOM1));

                    for (int i = 0; i < hexDataLines.length; i++) {
                        String line = hexDataLines[i];
                        //MainActivity.debug("Line "+(i+1)+": " + line);
                        if (!line.isEmpty() && line.length() > 2) {
                            // cut off the first byte (type + sequence)
                            // adding sequence checking would be wise to detect collisions
                            line = line.substring(2);
                            hexData += line;
                        }
                    }
                    break;
                default:  // a NEXT, FLOWCONTROL should not be received. Neither should any other string (such as NO DATA)
                    sumTingWong = true;
                    break;
            }

        }
        // better here is to wait for a >
        flushWithTimeout(400, '>');
        return hexData;
    }
}
