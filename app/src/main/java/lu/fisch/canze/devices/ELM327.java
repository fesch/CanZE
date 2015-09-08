package lu.fisch.canze.devices;

import java.io.IOException;
import java.util.ArrayList;

import lu.fisch.canze.MainActivity;
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

    // send a command and wait for the answer
    private String sendAndWaitForAnswer(String command, int waitMillis)
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
    }

    // query the device for the next filter
    private void queryNextFilter()
    {
        try {
            if(fields.size()>0) {
                // get filter ID
                String filter = fields.get(fieldIndex).getHexId();
                // atcra186 (substitute 186 by the hex code of the id)
                sendAndWaitForAnswer("atcra" + filter,400);
                // atma     (wait for one answer line)
                String hexData = sendAndWaitForAnswer("atma",100);
                // atar     (stop output)
                sendAndWaitForAnswer("atar", 10);
                // atar     (clear filter)
                sendAndWaitForAnswer("atar",10);

                // the result may contain multiple lines
                String[] hexDataLines = hexData.split("\r");

                //MainActivity.debug("ELM: lines = "+hexDataLines.length);

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

                    // process data
                    process(Utils.toIntArray(data.getBytes()));
                }
                fieldIndex = (fieldIndex + 1) % fields.size();
            }
            else
            {
                //MainActivity.debug("ELM: no filters set ...");
            }
        }
        catch (Exception e)
        {
            // ignore
        }

    }
}
