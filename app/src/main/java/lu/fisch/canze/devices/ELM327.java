package lu.fisch.canze.devices;

import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;

import lu.fisch.canze.MainActivity;
import lu.fisch.canze.actors.Field;
import lu.fisch.canze.actors.Message;
import lu.fisch.canze.actors.Utils;

/**
 * Created by robertfisch on 07.09.2015.
 */
public class ELM327 extends Device {

    // *** needed by the "decoder" part of this device
    private String buffer = "";
    private final String SEPARATOR = "\r\n";

    // *** needed by the "reader" part of this device

    // define the timeout we may wait to get an answer
    private static final int TIMEOUT = 500;
    // define End Of Message for this type of reader
    private static final char EOM = '\r';

    /**
     * the index of the actual field to request
     */
    private int fieldIndex = 0;

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
                    // atz (reset)
                    // continue only if we got an answer.
                    if (initELM(0)) {

                        while (true)
                            queryNextFilter();

                        // now start the query'ing timer
                    /*
                    Timer timer = new Timer();
                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            queryNextFilter();
                        }
                    }, 1, 1);
                    /**/
                    } else {
                        MainActivity.debug("ELM: no answer ...");
                        MainActivity.toast("No answer from ELM ... retrying ...");
                        if(connectedBluetoothThread!=null) {
                            Thread t = new Thread(this);
                            t.start();
                        }
                    }
                }
            };
            Thread t = new Thread(r);
            t.start();
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
        for (int i = 0; i < input.length; i++) {
            buffer += (char) input[i];
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

    private boolean initELM (int toughness) {

        if (toughness == 0 ) {
            if (sendAndWaitForAnswer("atz", 1000).trim().equals("")) {
                return false;
            }
        }
        else if (toughness == 1) {
            if (sendAndWaitForAnswer("atws", 1000).trim().equals("")) {
                return false;
            }
        }
        else {
            if (sendAndWaitForAnswer("atd", 100).trim().equals("")) {
                return false;
            }
            return true;
        }
        // ate0 (no echo)
        sendAndWaitForAnswer("ate0", 100);
        // ats0 (no spaces)
        sendAndWaitForAnswer("ats0", 100);
        // atsp6 (CAN 500K 11 bit)
        sendAndWaitForAnswer("atsp6", 100);
        // atat1 (auto timing)
        sendAndWaitForAnswer("atat1", 100);
        // atdp ==> not needed
        //sendAndWaitForAnswer("atdp", 100);
        // atcaf0 (no formatting)
        sendAndWaitForAnswer("atcaf0", 100);

        // PERFORMANE ENHACMENT
        // atfcsh79b        Set flow control response ID to 79b (the LBC)
        sendAndWaitForAnswer("atfcsh77b", 50);
        // atfcsd300020     Set the flow control response data to 300020 (flow control, clear to send,
        //                  all frames, 32 ms wait between frames. Note that it is not possible to let
        //                  the ELM request each frame as the Altered Flow Control only responds to a
        //                  First Frame (not a Next Frame)
        sendAndWaitForAnswer("atfcsd300000", 50);
        // atfcsm1          Set flow control mode 1 (ID and data suplied)
        sendAndWaitForAnswer("atfcsm1", 50);


        MainActivity.debug("ELM: initialised ...");
        if (toughness == 0 ) {
            MainActivity.toast("ELM is now ready ...");
        }

        return true;
    }

    /**
     * Creates a message based on the data of a line
     * @param text
     * @return
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
/*
    // send a command and wait for the answer
    private String sendAndWaitForAnswer2(String command, int waitMillis)
    {
        //MainActivity.debug("ELM: > " + command);

        if(command!=null)
            connectedBluetoothThread.write(command + "\r\n");

        if(waitMillis>0)
            try {
                Thread.sleep(waitMillis);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        byte[] bytesBuffer = new byte[256];     // buffer store for the stream
        String stringBuffer = "";               // buffer store for the stream as string
        int bytes;                              // bytes returned from read()
        boolean loop = true;
        //while (!stringBuffer.endsWith("\n"))
        try {
            while(connectedBluetoothThread.available()>0)
            {
                try {
                    // get number of bytes and message in "buffer"
                    bytes = connectedBluetoothThread.read(bytesBuffer);
                    // put it into the string buffer
                    stringBuffer += new String(bytesBuffer, 0, bytes);
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        //MainActivity.debug("ELM: < " + stringBuffer.trim());

        return stringBuffer;
    }*/

    // send a command and wait for an answer
    private String sendAndWaitForAnswer(String command, int waitMillis) {
        return sendAndWaitForAnswer(command,waitMillis,false,-1);
    }

    private String sendAndWaitForAnswer(String command, int waitMillis, int answerLinesCount) {
        return sendAndWaitForAnswer(command,waitMillis,false,answerLinesCount);
    }

    private String sendAndWaitForAnswer(String command, int waitMillis, boolean untilEmpty) {
        return sendAndWaitForAnswer(command,waitMillis,untilEmpty,-1);
    }

    // send a command and wait for an answer
    private String sendAndWaitForAnswer(String command, int waitMillis, boolean untilEmpty, int answerLinesCount)
    {
        if(connectedBluetoothThread==null) return "";

        boolean hooLeeFuk = false; // the fake news name of Asiana flight 214 777 First Officer that crashed in SF

        if(command!=null) {
            // empty incoming buffer
            // just make sure there is no previous response
            try {
                while (connectedBluetoothThread.available() > 0) {
                    connectedBluetoothThread.read();
                }
            } catch (IOException e) {
                // ignore
            }
            // send the command
            connectedBluetoothThread.write(command + "\r\n");
        }

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
        hooLeeFuk = false;
        while(!stop && !hooLeeFuk)
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
                        // convert it to a character
                        char ch = (char) data;
                        // add it to the readBuffer
                        readBuffer += ch;
                        // if we reach the end of a line
                        if (ch == EOM)
                        {
                            //MainActivity.debug("ALC: "+answerLinesCount+")\n"+readBuffer);
                            // decrease awaiting answer lines
                            answerLinesCount--;
                            // if we not asked to keep on and we got enough lines, stop
                            if(!untilEmpty){
                                if(answerLinesCount<=0) {
                                    stop = true;
                                }
                                else
                                {
                                    start = Calendar.getInstance().getTimeInMillis();
                                }
                            }
                            else if (untilEmpty) {
                                stop=(connectedBluetoothThread.available()==0);
                            }
                            else
                            {
                                start = Calendar.getInstance().getTimeInMillis();
                            }
                        }
                    }
                }

                if (Calendar.getInstance().getTimeInMillis() - start >= TIMEOUT) {
                    hooLeeFuk = true;
                    // MainActivity.toast("Sum Ting Wong on command " + command);
                }

            }
            catch (IOException e)
            {
                // ignore: e.printStackTrace();
            }
        }
        
        // set the flag that a timeout has occurred. sumTingWong can be inspected anywhere, but we reset the device after a full filter has been run
        if (hooLeeFuk) sumTingWong |= true;
        
        //MainActivity.debug("ALC: "+answerLinesCount+" && Stop: "+stop+" && Delta: "+(Calendar.getInstance().getTimeInMillis()-start));
        //MainActivity.debug("Recv < "+readBuffer);
        return readBuffer;
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

    // query the device for the next filter
    private void queryNextFilter()
    {
        try {
            if(fields.size()>0) {
                //MainActivity.debug("Index: "+fieldIndex);

                // get field
                Field field;
                boolean runFilter;

                synchronized (fields) {
                    field = fields.get(fieldIndex);

                    // only run the filter if the skipsCount is down to zero
                    runFilter = (field.getSkipsCount() == 0);
                    if (runFilter)
                        // reset it to its initial value
                        field.resetSkipsCount();
                    else
                        // decrement the skipsCount
                        field.decSkipCount();
                }

                if (runFilter) {

                    // get filter ID
                    String filter = field.getHexId();

                    // EML needs the filter to be 3 symbols!
                    String emlFilter = filter + "";
                    while (emlFilter.length() < 3) emlFilter = "0" + emlFilter;


                    if (field.isIsoTp()) {
                        String request = getRequestHexId(field.getId());

                        //MainActivity.debug("ELM: ask for "+request+","+field.getRequestId());

                        // atsh7e4          Set header to hex 79b (the LBC)
                        sendAndWaitForAnswer("atsh" + request, 50);
                        // atfcsh79b        Set flow control response ID to 79b (the LBC)
                        sendAndWaitForAnswer("atfcsh" + request, 50);
                        // atfcsd300020     Set the flow control response data to 300020 (flow control, clear to send,
                        //                  all frames, 32 ms wait between frames. Note that it is not possible to let
                        //                  the ELM request each frame as the Altered Flow Control only responds to a
                        //                  First Frame (not a Next Frame)

// PERFORMANE ENHACMENT
//                        sendAndWaitForAnswer("atfcsd300000", 50);
                        // atfcsm1          Set flow control mode 1 (ID and data suplied)
//                        sendAndWaitForAnswer("atfcsm1", 50);
                        // 022104           ISO-TP single frame - length 2 - payload 2104, which means PID 21 (??), id 04 (see first tab).
                        String pre = "0" + field.getRequestId().length() / 2;
                        //MainActivity.debug("R: "+request+" - C: "+pre+field.getRequestId());

                        // get 0x1 frame
                        String line0x1 = sendAndWaitForAnswer(pre + field.getRequestId(), 400, false);

                        // process first line (0x1 frame)
                        line0x1 = line0x1.trim();
                        //MainActivity.debug("Line: "+line0x1);
                        // clean-up if there is mess around
                        if (line0x1.startsWith(">")) line0x1 = line0x1.substring(1);
                        if (!line0x1.isEmpty()) {
                            // get type (first nibble)
                            String type = line0x1.substring(0, 1);
                            //MainActivity.debug("Type: "+type);

                            String finalData = "";
                            if (type.equals("0")) {
                                // remove 2 nibbles (type + length)
                                line0x1 = line0x1.substring(2); // was listed as 4
                                // remove response
                                //line0x1 = line0x1.substring(field.getRequestId().length());
                                finalData = line0x1;
                            } else {
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
                                finalData = line0x1;
                                // decrease count
                                count -= line0x1.length() / 2;
                                // each of the 0x2 frames has a payload of 7 bytes
                                int framesToReceive = (int) Math.ceil(count / 7.);
                                //MainActivity.debug("framesToReceive = " + framesToReceive);

                                // get remaining 0x2 frames
                                String lines0x2 = sendAndWaitForAnswer(null, 0, framesToReceive);

// PERFORMANE ENHACMENT
                                // atfcsm0          Reset flow control mode to 0 (default)
//                                sendAndWaitForAnswer("atfcsm0", 50);

                                //MainActivity.debug("Got:\n"+hexData);

                                // split into lines
                                String[] hexDataLines = lines0x2.split(String.valueOf(EOM));

                                for (int i = 0; i < hexDataLines.length; i++) {
                                    String line = hexDataLines[i];
                                    //MainActivity.debug("Line "+(i+1)+": " + line);
                                    if (!line.isEmpty() && line.length() > 2) {
                                        // cut off the first byte (type + sequence)
                                        // adding sequence checking would be wise to detect collisions
                                        line = line.substring(2);
                                        finalData += line;
                                    }
                                }
                            }

                            //MainActivity.debug("ELM: received " + emlFilter+","+finalData.trim());

                            String data = filter + "," + finalData.trim() + "," + field.getResponseId() + SEPARATOR;

                            // process data
                            process(Utils.toIntArray(data.getBytes()));
                        }

                        /*

                            atsh79b         > OK
                            atfcsh79b       > OK
                            atfcsd300020    > OK
                            atfcsm1         > OK
                            022141          > 107E61410F6F0F6D
                            atfcsm0         > OK


                            atsh7e4	        >OK	    Set header to hex 79b (the LBC)
                            atfcsh79b	    >OK	    Set flow control response ID to 79b (the LBC)
                            atfcsd300020	>OK	    Set the flow control response data to 300020 (flow control, clear to send, all frames, 32 ms wait between frames. Note that it is not possible to let the ELM request each frame as the Altered Flow Control only responds to a First Frame (not a Next Frame)
                            atfcsm1	        >OK	    Set flow control mode 1 (ID and data suplied)
                            022104	        104D610407D04207
                                            21D74207DA4207D9
                                            22.......
                                            ...........
                                            2B43000000000000

                                            ISO-TP single frame - length 2 - payload 2104, which means PID 21 (??), id 04 (see first tab).
                                            The answer: ISO-TP first frame - length 0x4D (67) - payload 610407D04207, which means answer to PID 21 (??), id 04.
                                            ISO-TP next frame - sequence number 1 - payload D74207DA4207D9
                                            ISO-TP next frame - sequence number 2 - .......
                                            .........
                                            ISO-TP next frame - sequence number b - payload 43 (remainder is padding beyond the length given in the first frame)"

                            atfcsm0	        >OK	Reset flow control mode to 0 (default)

                         */
                    } else {

                        //MainActivity.debug("ELM: ask for "+filter);

                        // atcra186 (substitute 186 by the hex code of the id)
                        sendAndWaitForAnswer("atcra" + emlFilter, 400);
                        // atma     (wait for one answer line)
                        String hexData = sendAndWaitForAnswer("atma", 1500); // was 80. This is way too short for an atma, SOme frames come in only once per second
                        // the first line may miss the first some bytes, so read a second one
                        //hexData = sendAndWaitForAnswer(null,0);
                        // atar     (stop output)
                        sendAndWaitForAnswer("atar", 0);
                        // atar     (clear filter)
                        sendAndWaitForAnswer("atar", 0);

                        // the result may contain multiple lines
                        //String[] hexDataLines = hexData.split(String.valueOf(EOM));
                        //MainActivity.debug("ELM: lines = "+hexDataLines.length);

                        String data = filter + "," + hexData.trim() + SEPARATOR;

                        //MainActivity.debug("ELM: received " + emlFilter+","+hexData.trim());

                        // process data
                        process(Utils.toIntArray(data.getBytes()));

                        /*
                        if(hexDataLines.length>1) {
                            // take the line in the middle
                            String data = hexDataLines[hexDataLines.length/2].trim();
                            // format the line (Bob's condensed: <ID>,<data>)
                            data = filter + "," + data +"\r\n";

                            MainActivity.debug("ELM: received " + data);

                            // Send to message queue Handler
                            //connectedBluetoothThread.getHandler().obtainMessage(MainActivity.RECIEVE_MESSAGE, data.length(), -1, hexData.getBytes()).sendToTarget();

                            // process data
                            process(Utils.toIntArray(data.getBytes()));
                        }
                        else // should not happen as the bus is faster than this ...
                        {
                            String data = filter + "," + hexDataLines[0].trim() +"\r\n";
                            MainActivity.debug("ELM: received " + data);

                            // process data
                            process(Utils.toIntArray(data.getBytes()));
                        }*/
                    }

                    // reset the ELM if a timeout occurred somewhere tunning this filter
                    if (sumTingWong) {
                        //MainActivity.toast("... in command " + emlFilter + ", resetting ELM");
                        initELM(1);
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
            else
            {
                //MainActivity.debug("ELM: no filters set ...");
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            // ignore
        }
    }

    @Override
    public void clearFields() {
        super.clearFields();
        fieldIndex=0;
    }
}
