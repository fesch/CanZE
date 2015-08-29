package lu.fisch.canze.lu.fisch.canze.readers;

import android.os.Handler;
import android.os.Message;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import lu.fisch.can.Stack;
import lu.fisch.can.exeptions.NoDecoderException;
import lu.fisch.canze.DrawThread;
import lu.fisch.canze.MainActivity;

/**
 * Created by robertfisch on 29.08.2015.
 */
public class ElmReader extends DataReader {

    private String buffer = "";
    private int filterIndex = 0;

    public ElmReader(Stack stack) {
        super(stack);
    }

    /**
     *
     * @param command       the command to send
     * @return
     */
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

    @Override
    public void initConnection() {
        // stop the reading thread because for this reader
        // we need to actively poll everthing out of the ELM
        connectedBluetoothThread.cleanStop();
        try {
            connectedBluetoothThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

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

                    while(true)
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
                }
                else
                {
                    MainActivity.debug("ELM: no answer ...");
                }
            }
        };
        Thread t = new Thread(r);
        t.start();
    }

    private void queryNextFilter()
    {
        if(filters.size()>0) {
            // get filter ID
            String filter = filters.get(filterIndex);
            // atcra186 (substitute 186 by the hex code of the id)
            sendAndWaitForAnswer("atcra" + filter,500);
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

                MainActivity.debug("ELM: received message " + data);

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
            filterIndex = (filterIndex + 1) % filters.size();
        }
        else
        {
            MainActivity.debug("ELM: no filters set ...");
        }
    }

    @Override
    public void registerFilter(String filter) {
        // do nothing special here
    }

    @Override
    public void handleMessage(android.os.Message msg) {
        switch (msg.what) {
            // if recieve a massage
            case MainActivity.RECIEVE_MESSAGE:
                //debug("Got message ...");
                // get the raw data
                byte[] readBuf = (byte[]) msg.obj;
                // create string from bytes array
                String strIncom = new String(readBuf, 0, msg.arg1);
                //MainActivity.debug("Got message: "+strIncom);
                // add it to the buffer
                buffer += strIncom;
                // split up the buffer using either space separator
                String[] lines = {};
                if(buffer.contains("|"))
                {
                    lines=buffer.split("\\|");
                    // when the last symbol in the buffer if the separator, it will be cut of,
                    // but as we will not process the last message and save it for later usage,
                    // we need to add it again, if needed.
                    if(lines.length>0 && buffer.endsWith("|"))
                        lines[lines.length-1]+="|";
                }
                else if(buffer.contains("\r\n"))
                {
                    lines=buffer.split("\r\n");
                    // when the last symbol in the buffer if the separator, it will be cut of,
                    // but as we will not process the last message and save it for later usage,
                    // we need to add it again, if needed.
                    if(lines.length>0 && buffer.endsWith("\r\n"))
                        lines[lines.length-1]+="\r\n";
                }
                // now process each message, except the last one (mostly empty anyway)
                for(int i=0; i<lines.length-1; i++)
                {
                    // stats
                            /*
                            if(count==0) start= Calendar.getInstance().getTimeInMillis();
                            count++;
                            double freg = (double) count/((Calendar.getInstance().getTimeInMillis()-start)/1000);
                            txtFreq.setText("Message rate: "+freg);
                            txtArduino.setText("Data from Arduino: " + lines[i]);
                            */

                    try
                    {
                        // process the message
                        stack.process(lines[i].trim());
                    }
                    catch (NoDecoderException e)
                    {
                        e.printStackTrace();
                    }
                }

                // if there were some lines
                if(lines.length>0)
                    // set the buffer to te last message (mostly empty anyway)
                    buffer = lines[lines.length-1];
                else
                    buffer="";
        }
    }

}
