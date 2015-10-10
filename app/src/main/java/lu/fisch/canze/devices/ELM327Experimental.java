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
public class ELM327Experimental extends Device {

    // *** needed by the "decoder" part of this device
    private String buffer = "";
    private final String SEPARATOR = "\r\n";

    private static final int TIMEOUT = 500;

    /**
     * the index of the actual field to request
     */
    private int fieldIndex = 0;
    private int lastId = 0;

    private Thread pollerThread;

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
                    // atz (reset)
                    // continue only if we got an answer.
                    if (restoreOrder(0)) {

                        while (connectedBluetoothThread!=null) {
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
                        MainActivity.toast("No answer from ELM ... retrying ...");
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

    private boolean restoreOrder(int toughness) {

        // ensure the decoder (processData) is reset
        buffer = "";

        // ensure the dongle header are set again
        lastId = 0;

        String response;

        // ensure any running command is interrupted.
        // the x should kill any running command. However, if there is no command, the x will screw up the reset
        sendSerial("x");
        flushSerial(100);
        // so at this point, either the ELM is waiting for a commmand, when it was interrupted, or it has x in
        // the command buffer, therefor, we send another x then cr, to ensure a known state, which is ?
        sendSerial("x\r");
        getSerialCommandLine('?', 50);

        // now we are in a known state, being waiting for a command
        switch (toughness) {
            case 0:
                //response = sendSerialCommandElm("atz", 1); // we don't need the LED test
                response = sendSerialCommandElm("atws", 1);
                break;
            case 1:
                response = sendSerialCommandElm("atws", 1);
                break;
            default:
                response = sendSerialCommandElm("atd", 1);

        }

        if (response.trim().equals("")) {
            MainActivity.toast("ELM is not responding, sorry");
            return false;
        }

        // only do version control at a full reset
        if (toughness <= 1 && !response.toUpperCase().contains("V1.4") && !response.toUpperCase().contains("V1.5")) {
            MainActivity.toast("ELM is not a version 1.4 or 1.5 [" + response + "]");
            return false;
        }

        // ate0 (no echo)
        response = sendSerialCommandElm("ate0", 0);
        if (!response.toUpperCase().contains("OK")) {
            MainActivity.toast("Err e0 [" + response + "]");
            return false;
        }

        // ats0 (no spaces)
        response = sendSerialCommandElm("ats0", 0);
        if (!response.toUpperCase().contains("OK")) {
            MainActivity.toast("Err s0 [" + response + "]");
            return false;
        }

        // atsp6 (CAN 500K 11 bit)
        response = sendSerialCommandElm("atsp6", 0);
        if (!response.toUpperCase().contains("OK")) {
            MainActivity.toast("Err sp6 [" + response + "]");
            return false;
        }

        // atat1 (auto timing)
        response = sendSerialCommandElm("atat1", 0);
        if (!response.toUpperCase().contains("OK")) {
            MainActivity.toast("Err at1 [" + response + "]");
            return false;
        }

        // atcaf0 (no formatting)
        response = sendSerialCommandElm("atcaf0", 0);
        if (!response.toUpperCase().contains("OK")) {
            MainActivity.toast("Err caf0 [" + response + "]");
            return false;
        }

        // PERFORMANCE ENHACMENT
        // atfcsh79b        Set flow control response ID to 79b (the LBC) This is needed to set the flow control response, but that one is remembered :-)
        response = sendSerialCommandElm("atfcsh77b", 0);
        if (!response.toUpperCase().contains("OK")) {
            MainActivity.toast("Err fcsh77b [" + response + "]");
            return false;
        }

        // atfcsd300020     Set the flow control response data to 300020 (flow control, clear to send,
        //                  all frames, 16 ms wait between frames. Note that it is not possible to let
        //                  the ELM request each frame as the Altered Flow Control only responds to a
        //                  First Frame (not a Next Frame)
        response = sendSerialCommandElm("atfcsd300010", 0);
        if (!response.toUpperCase().contains("OK")) {
            MainActivity.toast("Err fcsd300010 [" + response + "]");
            return false;
        }

        // atfcsm1          Set flow control mode 1 (ID and data suplied)
        response = sendSerialCommandElm("atfcsm1", 0);
        if (!response.toUpperCase().contains("OK")) {
            MainActivity.toast("Err fcsm1 [" + response + "]");
            return false;
        }

        if (toughness == 0 ) {
            MainActivity.toast("ELM is now ready ...");
        }

        return true;
    }

    // RAW SERIAL BLUETOOTH METHODS ====================================================================================

    private void sendSerial (String command) {
        if (connectedBluetoothThread == null) return;
        connectedBluetoothThread.write (command);
    }


    private void flushSerial (int timeout) {
        if(connectedBluetoothThread == null) return;
        try {
            // raw implementation of timeout. Better would be to use serialReady and restart the timeout after echt character.
            // then again, this timeout is in reality only used by restoreOrder, so we're fine.
            if (timeout != 0) {
                Thread.sleep(timeout);
            }
            while (connectedBluetoothThread.available() > 0) {
                connectedBluetoothThread.read();
            }
        } catch (IOException e) {
            // ignore
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    private boolean serialReady (int msecs) throws IOException {
        if (connectedBluetoothThread == null) return false;
        try {
            if (connectedBluetoothThread.available() > 0) return true;
            long end = Calendar.getInstance().getTimeInMillis() + msecs;
            while (Calendar.getInstance().getTimeInMillis() < end) {
                if (connectedBluetoothThread.available() > 0) return true;
                Thread.sleep(2);
            }
        } catch (IOException | InterruptedException e) {
            // ignore
        }
        return false;
    }


    private String getSerialCommandLine (char stopChar, int timeout) {
        if (connectedBluetoothThread == null) return "";
        String readBuffer ="";
        try {
            while (serialReady(timeout)) {
                char c = (char )connectedBluetoothThread.read();
                if (c == stopChar) {
                    //MainActivity.toast (readBuffer);
                    return readBuffer;
                }
                switch (c) {
                    case '\0':
                    case '\r':
                    case '\n':
                    case '>':
                        break;
                    default:
                        readBuffer += c;
                        break;
                }
            }
        } catch (IOException e) {
            // ignore
        }
        return readBuffer;
    }

    // ELM SERIAL BLUETOOTH METHODS ====================================================================================

    // send a command to control the ELM and wait for an answer
    private String sendSerialCommandElm(String command, int retries) {
        String result;
        do {
            flushSerial(0);
            sendSerial(command + "\r");
            result = getSerialCommandLine('>', TIMEOUT);
            if (!result.equals("")) return result;
        } while (retries-- >= 0);
        return ("");
    }

    // open as passive monitor for a given frame ID, and stop as soon as one line is in
    private String sendSerialMonitor (String id, int timeout) {
        String result;

        if (sendSerialCommandElm("atcra" + ("000" + id).substring(id.length()), 0).toUpperCase().contains("OK")) {
            flushSerial(400);
            sendSerial("atma\r");
            result = getSerialCommandLine('\r', timeout);
            sendSerial("x");
            getSerialCommandLine('>', TIMEOUT);
            if (!sendSerialCommandElm("atar", 0).toUpperCase().contains("OK")) {
                if (!sendSerialCommandElm("atar", 0).toUpperCase().contains("OK")) {
                    MainActivity.toast("Error on command ar");
                }
            }
            return result;
        }
        return "";
    }

    // send an ISO-TP frame to the car on the given CANbus ID, and wait for the answer. Notice the ISO-TP headers are stripped and multi-frame messages are assembled.
    // for now, we assume the frame to be send is always less than 7 bytes, so on sending, we only support SINGLE
    String sendSerialCommandIsoTp (String id, String command, boolean checkUDP, boolean fast) {
        String result;
        String finalResult;
        int length;
        int runLength;
        int sequence;

        // check if the command fits in a SINGLE ISO-TP frame to avoid messing with the ECU
        if (command.length() > 7) return ("");

        // if the last command was on the same ID, we can skip seting up the ELM for those headers. This is only for performace improvement
        if (!fast) {
            sendSerialCommandElm("atsh" + ("000" + id).substring(id.length()), 0);
            //sendSerialCommandElm("atfcsd300010", 0);
            sendSerialCommandElm("atfcsh" + ("000" + id).substring(id.length()), 0);
            // since we always use fcsm1, the next line may be removed. Experimentation needed as setting fcsh might reset the fc mode
            // sendSerialCommandElm("atfcsm1", 0);
        }

        // send the command as an ISO-TP SINGLE frame
        sendSerial("0" + (command.length() / 2) + command + "\r");

        //get the first reponse
        result = getSerialCommandLine('\r', TIMEOUT);
        if (result.toUpperCase().contains("NO DATA") || result.toUpperCase().contains("CAN ERROR")) {
            MainActivity.toast("iso " + id + "," + command + ":" + result);
            restoreOrder(2);
            return "";
        }

        // handle response type
        switch (result.substring(0, 1)) {
            case "0": //ISO-TP SINGLE
                length = Integer.valueOf(result.substring(1, 2), 16);
                if (length > 7) {
                    MainActivity.toast("iso0 " + id + "," + command + ":" + result);
                    restoreOrder(2);
                    return ("");
                }
                // simply remove the header byte and trim the result to the given length (raw CAN frame can be padded)
                return result.substring(2, 2 + (length * 2));
            case "1": // ISO-TP FIRST
                length = Integer.valueOf(result.substring(1, 4), 16);
                // remove the header
                finalResult = result.substring(4);
                // we already have 6 data bytes
                runLength = length - 6;
                sequence = 1;
                // receive the remaining NEXT frames
                while (runLength > 0) {
                    result = getSerialCommandLine('\r', TIMEOUT); // should be NEXT
                    // check for proper sequencing
                    if (Integer.valueOf(result.substring(0, 2), 16) != (0x20 + sequence)) {
                        MainActivity.toast("iso seq " + id + "," + command + ":" + result);
                        restoreOrder(2);
                        return ("");
                    }
                    // remove the header of the NEXT frame
                    finalResult += result.substring(2);
                    sequence = (sequence + 1) & 0xf;
                    //and assume a full frame of data. No need to check length as the last frame may be padded anyway
                    runLength -= 7;
                }
                // now trim the final result
                finalResult = finalResult.substring(0, length * 2);
                //MainActivity.toast("mframe " + length + "-" + finalResult);
                //try {Thread.sleep(3000);} catch (Exception e) {}
                return finalResult;
            default:
                // MainActivity.toast("iso x " + id + "," + command + ":" + result);
                // try {Thread.sleep(3000);} catch (Exception e) {}
                // restoreOrder(2);
                break;
        }
        return "";
    }

    private int getRequestId(int responseId)
    {          //from  to
        switch (responseId) {
            case 0x7ec:
                return 0x7e4;  // EVC / SCH
            case 0x7cd:
                return 0x7ca;  // TCU
            case 0x7bb:
                return 0x79b;  // LBC
            case 0x77e:
                return 0x75a;  // PEB
            case 0x772:
                return 0x752;  // Airbag
            case 0x76d:
                return 0x74d;  // USM / UDP
            case 0x763:
                return 0x743;  // CLUSTER / instrument panel
            case 0x762:
                return 0x742;  // PAS
            case 0x760:
                return 0x740;  // ABS
            case 0x7bc:
                return 0x79c;  // UBP
            case 0x765:
                return 0x745;  // BCM
            case 0x764:
                return 0x744;  // CLIM
            case 0x76e:
                return 0x74e;  // UPA
            case 0x793:
                return 0x792;  // BCB
            case 0x7b6:
                return 0x796;  // LBC2
            case 0x722:
                return 0x702;  // LINSCH
            default:
                return -1;
        }
    }

    private String getRequestHexId(int responseId)
    {
        return Integer.toHexString(getRequestId(responseId));
    }

    public void join() throws InterruptedException {

    }

    // query the device for the next filter
    private void queryNextFilter()
    {
        if(fields.size() == 0) return;

        try {
            // get field
            Field field;
            String result;

            synchronized (fields) {
                field = fields.get(fieldIndex);
            }

            // only run the filter if the skipsCount is down to zero
            if (field.getSkipsCount() == 0) {
                // reset skipCount to its initial value
                field.resetSkipsCount();

                // get filter ID
                String filter = field.getHexId();

                if (field.isIsoTp()) {

                    result = sendSerialCommandIsoTp(getRequestHexId(field.getId()), field.getRequestId(), false, lastId == field.getId());
                    // MainActivity.toast("iso " + getRequestHexId(field.getId()) + "," + field.getRequestId() + ":" + result);
                    // Thread.sleep(1000);
                    lastId = field.getId();

                    if(!result.isEmpty())
                    {
                        result = field.getHexId()+","+result+","+field.getResponseId()+SEPARATOR;
                        MainActivity.debug("Result = "+result);
                        // process data
                        process(Utils.toIntArray(result.getBytes()));
                    }

                } else {

                    result = sendSerialMonitor(field.getHexId(), field.getFrequency()+20);
                    lastId = 0;
                    // MainActivity.toast("atma " + field.getHexId() + ":" + result);
                    // Thread.sleep(1000);

                    if(!result.isEmpty())
                    {
                        result = field.getHexId()+","+result+SEPARATOR;
                        MainActivity.debug("Result = "+result);
                        // process data
                        process(Utils.toIntArray(result.getBytes()));
                    }
                }

            } else {
                // decrement the skipsCount
                field.decSkipCount();
            }

            // move on to the next field
            synchronized (fields) {
                if(fields.size()==0)
                    fieldIndex=0;
                else
                    fieldIndex = (fieldIndex + 1) % fields.size();
            }
        }
        catch (Exception e)
        {
            // e.printStackTrace();
            // ignore
        }
    }

    @Override
    public void clearFields() {
        super.clearFields();
        fieldIndex = 0;
    }
}
