/**
 * A very basic reader that does not send data to the bluetooth server
 * but only receives data.
 */

package lu.fisch.canze.lu.fisch.canze.readers;

import android.os.Handler;

import lu.fisch.can.Stack;
import lu.fisch.can.exeptions.NoDecoderException;
import lu.fisch.canze.ConnectedBluetoothThread;
import lu.fisch.canze.MainActivity;

/**
 * Created by robertfisch on 27.08.2015.
 */
public class DueReader extends DataReader {

    private String buffer = "";

    public DueReader(Stack stack)
    {
        super(stack);
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

    @Override
    public void clearFilters() {
        super.clearFilters();
        connectedBluetoothThread.write("c\n");
    }

    @Override
    public void addFilter(String filter) {
        super.addFilter(filter);
        connectedBluetoothThread.write("f" + filter + "\n");
    }

    @Override
    public void removeFilter(String filter) {
        super.removeFilter(filter);
        connectedBluetoothThread.write("r"+filter+"\n");
    }


}
