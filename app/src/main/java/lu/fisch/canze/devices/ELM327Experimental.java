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

        String response = "";

        // sendSerialCommandElm("x", 2); // kill any running command (specifically ATMA), ignore response

        switch (toughness) {
            case 0:
                //response = sendSerialCommandElm("atz", 1);
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
        if (toughness <= 1 && !response.contains("v1.4") && !response.contains("v1.5")) {
            MainActivity.toast("ELM is not a version 1.4 or 1.5 [" + response + "]");
            return false;
        }



        // ate0 (no echo)
        if (!sendSerialCommandElm("ate0", 0).contains("OK")) {
            MainActivity.toast("Error on command e0");
            return false;
        }

        // ats0 (no spaces)
        if (!sendSerialCommandElm("ats0", 0).contains("OK")) {
            MainActivity.toast("Error on command s0");
            return false;
        }

        // atsp6 (CAN 500K 11 bit)
        if (!sendSerialCommandElm("atsp6", 0).contains("OK")) {
            MainActivity.toast("Error on command sp6");
            return false;
        }

        // atat1 (auto timing)
        if (!sendSerialCommandElm("atat1", 0).contains("OK")) {
            MainActivity.toast("Error on command at1");
            return false;
        }

        // atcaf0 (no formatting)
        if (!sendSerialCommandElm("atcaf0", 0).contains("OK")) {
            MainActivity.toast("Error on command caf0");
            return false;
        }

        // PERFORMANCE ENHACMENT
        // atfcsh79b        Set flow control response ID to 79b (the LBC) This is needed to set the flow control response, but that one is remembered :-)
        if (!sendSerialCommandElm("atfcsh77b", 0).contains("OK")) {
            MainActivity.toast("Error on command fcsh77b");
            return false;
        }

        // atfcsd300020     Set the flow control response data to 300020 (flow control, clear to send,
        //                  all frames, 16 ms wait between frames. Note that it is not possible to let
        //                  the ELM request each frame as the Altered Flow Control only responds to a
        //                  First Frame (not a Next Frame)
        if (!sendSerialCommandElm("atfcsd300010", 0).contains("OK")) {
            MainActivity.toast("Error on command fcsd300010");
            return false;
        }

        // atfcsm1          Set flow control mode 1 (ID and data suplied)
        if (!sendSerialCommandElm("atfcsm1", 0).contains("OK")) {
            MainActivity.toast("Error on command atfcsm1");
            return false;
        }

        if (toughness == 0 ) {
            MainActivity.toast("ELM is now ready ...");
        }

        return true;
    }


    private void sendSerial (String command) {
        if (connectedBluetoothThread == null) return;
        connectedBluetoothThread.write (command);
    }

    private void flushSerial () {
        if(connectedBluetoothThread == null) return;
        try {
            while (connectedBluetoothThread.available() > 0) {
                connectedBluetoothThread.read();
            }
        } catch (IOException e) {
            // ignore
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
        } catch (IOException e) {
            // ignore
        } catch (InterruptedException e) {
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

    // send a command to control the ELM and wait for an answer
    private String sendSerialCommandElm(String command, int retries) {
        do {
            flushSerial();
            sendSerial(command + "\r");
            return getSerialCommandLine('>', TIMEOUT);
        } while (retries-- >= 0);
    }

    // open as passive monitor for a given frame ID, and stop as soon as one line is in
    private String sendSerialMonitor (String id, int timeout) {
        String result = "";
        if (sendSerialCommandElm("atcra" + ("000" + id).substring(id.length()), 0).contains("OK")) {
            try {
                Thread.sleep(400);
                flushSerial();
                sendSerial("atma\r");
                result = getSerialCommandLine('\r', timeout);
                sendSerial("x");
                getSerialCommandLine('>', TIMEOUT);
                if (!sendSerialCommandElm("atar", 0).contains("OK")) {
                    if (!sendSerialCommandElm("atar", 0).contains("OK")) {
                        MainActivity.toast("Error on command ar");
                    }
                }
                return result;
            } catch (InterruptedException e) {
                // ignore
            }
        }
        return "";
    }

    String sendSerialCommandIsoTp (String id, String command, boolean checkUDP, boolean fast) {
        String result = "";
        if (!fast) {
            sendSerialCommandElm("atsh" + ("000" + id).substring(id.length()), 0);
            //sendSerialCommandElm("atfcsd300010", 0);
            sendSerialCommandElm("atfcsh" + ("000" + id).substring(id.length()), 0);
            sendSerialCommandElm("atfcsm1", 0);
        }
        sendSerial("0" + (command.length() / 2) + command + "\r");
        result = getSerialCommandLine('\r', TIMEOUT);
        if (result.contains("NO DATA") || result.contains("CAN ERROR")) {
            MainActivity.toast("ELM error:" + result);
            restoreOrder(2);
            result = "";
        }





        return result;
    }

    private int getRequestId(int responseId)
    {                     //from        // to
        if     (responseId==0x7ec) return 0x7e4;  // EVC / SCH
        else if(responseId==0x7cd) return 0x7ca;  // TCU
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

                    result = sendSerialCommandIsoTp (getRequestHexId(field.getId()), field.getRequestId(), false, lastId == field.getId());
                    //MainActivity.toast("iso " + getRequestHexId(field.getId()) + "," + field.getRequestId() + ":" + result);
                    //Thread.sleep(1000);
                    lastId = field.getId();

                    // process data
                    process(Utils.toIntArray(result.getBytes()));

                } else {

                    result = sendSerialMonitor(field.getHexId(), field.getFrequency()+20);
                    //MainActivity.toast("atma " + field.getHexId() + ":" + result);
                    //Thread.sleep(1000);
                    // process data
                    process(Utils.toIntArray(result.getBytes()));
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
