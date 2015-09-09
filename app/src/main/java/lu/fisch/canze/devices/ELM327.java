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
                    if (!sendAndWaitForAnswer("atz", 1000).trim().equals("")) {
                        // ate0 (no echo)
                        sendAndWaitForAnswer("ate0", 1000);
                        // ats0 (no spaces)
                        sendAndWaitForAnswer("ats0", 1000);
                        // atsp6 (CAN 500K 11 bit)
                        sendAndWaitForAnswer("atsp6", 500);
                        // atat1 (auto timing)
                        sendAndWaitForAnswer("atat1", 500);
                        // atdp
                        sendAndWaitForAnswer("atdp", 500);
                        // atcaf0 (no formatting)
                        sendAndWaitForAnswer("atcaf0", 500);

                        MainActivity.debug("ELM: initialised ...");

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
                        Thread t = new Thread(this);
                        t.start();
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

        MainActivity.debug("Buffer: "+buffer);

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
        return sendAndWaitForAnswer(command,waitMillis,false);
    }

    // send a command and wait for an answer
    private String sendAndWaitForAnswer(String command, int waitMillis, boolean untilEmpty)
    {
        // empty incoming buffer
        // just make sure there is no previous response
        try {
            while(connectedBluetoothThread.available()>0)
            {
                connectedBluetoothThread.read();
            }
        } catch (IOException e) {
            // ignore
        }
        // send the command
        if(command!=null)
            connectedBluetoothThread.write(command + "\r\n");
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
                if(connectedBluetoothThread.available()>0) {
                    //MainActivity.debug("Reading ...");
                    int data = connectedBluetoothThread.read();
                    //MainActivity.debug("... done");
                    // if it is a real one
                    if (data != -1) {
                        // convert it to a character
                        char ch = (char) data;
                        // add it to the readBuffer
                        readBuffer += ch;
                        // stop if we reached the end or if no more data is available
                        if (ch == EOM || connectedBluetoothThread.available() <= 0) stop = true;
                        if (ch == EOM && untilEmpty)
                        {
                            try {
                                Thread.sleep(30);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        // if we should read until the end and there is still data on the line,
                        // do not stop and reset timeout!
                        if(stop==true && connectedBluetoothThread.available()>0 && untilEmpty)
                        {
                            stop=false;
                            start = Calendar.getInstance().getTimeInMillis();
                        }
                    }
                }
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
        //MainActivity.debug("Stop: "+stop+" && Delta: "+(Calendar.getInstance().getTimeInMillis()-start));
        //MainActivity.debug("Recv < "+readBuffer);
        return readBuffer;
    }

    private int getRequestId(int responseId)
    {                     //from        // to
        if     (responseId==0x7ec) return 0x7e4;  // EVC
        else if(responseId==0x7cd) return 0x7ca;  // TCU
        else if(responseId==0x7bb) return 0x79b;  // LBC
        else if(responseId==0x77e) return 0x75a;  // PEB
        else if(responseId==0x772) return 0x752;  // Airbag
        else if(responseId==0x76d) return 0x74d;  // UDP
        else if(responseId==0x763) return 0x743;  // instrument panel
        else if(responseId==0x762) return 0x742;  // PAS
        else if(responseId==0x760) return 0x740;  // ABS
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
                synchronized (fields) {
                    field = fields.get(fieldIndex);
                }
                // get filter ID
                String filter = field.getHexId();

                // EML needs the filter to be 3 symbols!
                String emlFilter = filter+"";
                while(emlFilter.length()<3) emlFilter="0"+emlFilter;


                if (field.isIsoTp()) {
                    String request = getRequestHexId(field.getId());

                    //MainActivity.debug("ELM: ask for "+request+","+field.getRequestId());

                    // atsh7e4          Set header to hex 79b (the LBC)
                    sendAndWaitForAnswer("atsh" + request,50);
                    // atfcsh79b        Set flow control response ID to 79b (the LBC)
                    sendAndWaitForAnswer("atfcsh" + request,50);
                    // atfcsd300020     Set the flow control response data to 300020 (flow control, clear to send,
                    //                  all frames, 32 ms wait between frames. Note that it is not possible to let
                    //                  the ELM request each frame as the Altered Flow Control only responds to a
                    //                  First Frame (not a Next Frame)
                    sendAndWaitForAnswer("atfcsd300030",50);
                    // atfcsm1          Set flow control mode 1 (ID and data suplied)
                    sendAndWaitForAnswer("atfcsm1", 50);
                    // 022104           ISO-TP single frame - length 2 - payload 2104, which means PID 21 (??), id 04 (see first tab).
                    String pre="0"+field.getRequestId().length()/2;
                    MainActivity.debug("R: "+request+" - F: "+emlFilter+" - C: "+pre+field.getRequestId());
                    String hexData = sendAndWaitForAnswer(pre+field.getRequestId(),400,true);
                    // atfcsm0          Reset flow control mode to 0 (default)
                    sendAndWaitForAnswer("atfcsm0", 50);

                    //MainActivity.debug("Got:\n"+hexData);

                    // split into lines
                    String[] hexDataLines = hexData.split(String.valueOf(EOM));
                    String finalData = "";

                    for(int i=0; i<hexDataLines.length; i++)
                    {
                        // get the line
                        String line = hexDataLines[i].trim();
                        if(line.startsWith(">")) line=line.substring(1);
                        if(!line.isEmpty()) {
                            MainActivity.debug("Line: " + line);
                            // first line
                            if (i == 0) {
                                // cut off the two first bytes (type + frame length)
                                line = line.substring(4);
                                // cut of the reply code
                                line = line.substring(field.getRequestId().length());
                                finalData = line;
                            }
                            // second line
                            else {
                                // cut off the first byte (type + sequence)
                                line = line.substring(2);
                                finalData += line;
                            }
                        }
                    }

                    MainActivity.debug("ELM: received " + emlFilter+","+finalData.trim());

                    String data = filter + "," + finalData.trim() + SEPARATOR;

                    // process data
                    process(Utils.toIntArray(data.getBytes()));


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
                }
                else
                {

                    //MainActivity.debug("ELM: ask for "+filter);

                    // atcra186 (substitute 186 by the hex code of the id)
                    sendAndWaitForAnswer("atcra" + emlFilter,400);
                    // atma     (wait for one answer line)
                    String hexData = sendAndWaitForAnswer("atma",80);
                    // the first line may miss the first some bytes, so read a second one
                    //hexData = sendAndWaitForAnswer(null,0);
                    // atar     (stop output)
                    sendAndWaitForAnswer("atar", 0);
                    // atar     (clear filter)
                    sendAndWaitForAnswer("atar",0);

                    // the result may contain multiple lines
                    //String[] hexDataLines = hexData.split(String.valueOf(EOM));
                    //MainActivity.debug("ELM: lines = "+hexDataLines.length);

                    String data = filter + "," + hexData.trim() + SEPARATOR;

                    MainActivity.debug("ELM: received " + emlFilter+","+hexData.trim());

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

                synchronized (fields) {
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
}
