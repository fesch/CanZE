/*
 * This class represents the CAN frame stack. For multi-frames it stores them
 * until they are complete, then it pushes them over to the listeners.
 */
package lu.fisch.canze._old.actors;

import android.util.Log;

import lu.fisch.canze._old.decoders.GVRET;
import lu.fisch.canze.actors.Message;
import lu.fisch.canze.interfaces.MessageListener;
import java.util.ArrayList;

import lu.fisch.canze.exeptions.NoDecoderException;
import lu.fisch.canze._old.decoders.BOB;
import lu.fisch.canze._old.decoders.CRDT;
import lu.fisch.canze._old.decoders.Decoder;
import lu.fisch.canze.activities.MainActivity;

/**
 *
 * @author robertfisch
 */
public class Stack {
    
    private final ArrayList<MessageListener> messageListeners = new ArrayList<>();
    
    private Decoder decoder = new CRDT();

    private int invalid = 0;
    
    public void process(String line) throws NoDecoderException
    {
        //Log.d(MainActivity.TAG,"Process line: "+line);

        // decode the frame
        Message message = null;

        try {
            message = decoder.decodeFrame(line);
        }
        catch(Exception e)
        {
            Log.d(MainActivity.TAG,"Problematic line = "+line);
            //e.printStackTrace();
        }


        if(message !=null)
            notifyStackListeners(message);
    }

    public void process(int[] data) throws NoDecoderException
    {
        // decodeFrame the frame
        ArrayList<Message> messages = decoder.process(data);

        //MainActivity.debug("Frames = "+frames.size());

        for(int i=0; i< messages.size(); i++)
        {
            Message message = messages.get(i);

            // notify the listeners about if the precessed frame is OK
            if(message !=null)
                notifyStackListeners(message);
        }
    }

    public void setDataFormat(String dataFormat) {
        if(dataFormat.equals("crdt")) decoder=new CRDT();
        else if(dataFormat.equals("bob")) decoder=new BOB();
        else if(dataFormat.equals("gvret")) decoder=new GVRET();
        else decoder=null;
    }


    /* --------------------------------
     * Listeners management
     \ ------------------------------ */
    
    public void addListener(MessageListener messageListener)
    {
        if(!messageListeners.contains(messageListener))
            messageListeners.add(messageListener);
    }
    
    public void removeSackListener(MessageListener messageListener)
    {
        messageListeners.remove(messageListener);
    }
    
    /**
     * Notify all listeners synchronously
     * @param message     the frame to send
     */
    private void notifyStackListeners(Message message)
    {
        notifyStackListeners(message,false);
    }

    /**
     * Notify all listeners
     * @param message     the frame to send
     * @param async     true for asynchronous notifications (one thread per listener)
     */
    private void notifyStackListeners(Message message, boolean async)
    {
        //Log.d(MainActivity.TAG,"Listeners: "+stackListeners.size());
        if(async == false) {
            for(int i=0; i< messageListeners.size(); i++) {
                messageListeners.get(i).onMessageCompleteEvent(message.clone());
            }
        } else {
            // clone the frame to make sure modifications will 
            final Message clone = message.clone();
            for(int i=0; i< messageListeners.size(); i++) {
                final int index = i;
                (new Thread(new Runnable() {

                    @Override
                    public void run() {
                        messageListeners.get(index).onMessageCompleteEvent(clone.clone());
                    }
                })).start();
            }
        }
    }

    /* --------------------------------
     * Tests ...
     \ ------------------------------ */
    
    public static void main(String[] args)
    {
        Stack s = new Stack();
        
        Message f;
        /*
        f=s.process(new Frame(0x7bb, 0, new int[] {0x10,0x1D,0x61,0xF3,0xF1,0x95,0x21,0xD5}));
        System.out.println(f);

        f=s.process(new Frame(0x7bb, 0, new int[] {0x21,0xAA,0x9E,0xF0,0xF0,0xF0,0xF0,0xF1}));
        System.out.println(f);

        f=s.process(new Frame(0x7bb, 0, new int[] {0x22,0xBB,0xF1,0x95,0xF0,0xF0,0xF0,0xF1}));
        System.out.println(f);

        f=s.process(new Frame(0x7bb, 0, new int[] {0x23,0xCC,0xF7,0xD0,0xF5,0x22,0x41,0xF0}));
        System.out.println(f);

        f=s.process(new Frame(0x7bb, 0, new int[] {0x24,0xDD,0xF7,0x00,0xF5,0x22,0x41,0xF0}));
        System.out.println(f);
        */
    }


}


