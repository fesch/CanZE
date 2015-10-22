package lu.fisch.canze._old.readers;

import lu.fisch.canze._old.actors.Stack;


import java.io.IOException;

import lu.fisch.canze.exeptions.NoDecoderException;
import lu.fisch.canze.activities.MainActivity;

/**
 * Created by robertfisch on 29.08.2015.
 */
public class ElmReader extends DataReader {

    // the index of the actual filter
    private int filterIndex = 0;

    // create a new reader
    public ElmReader(Stack stack) {
        super(stack);
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

    // initialise the connection
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

    // query the device for the next filter
    private void queryNextFilter()
    {
        try {
            if(fields.size()>0) {
                // get filter ID
                String filter = fields.get(filterIndex).getHexId();
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

                    // send it to the stack
                    try {
                        stack.process(data);
                    } catch (NoDecoderException e) {
                        e.printStackTrace();
                    }
                }
                else // should not happen as the bus is faster than this ...
                {
                    String data = filter + "," + hexDataLines[0].trim() +"\r\n";

                    // send it to the stack
                    try {
                        stack.process(data);
                    } catch (NoDecoderException e) {
                        e.printStackTrace();
                    }
                }
                filterIndex = (filterIndex + 1) % fields.size();
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

    // register a filter
    @Override
    public void registerFilter(String filter) {
        // do nothing special here
    }

}
