/*
 * This class represents the CAN frame stack. For multi-frames it stores them
 * until they are complete, then it pushes them over to the listeners.
 */
package lu.fisch.canze.actors;

import android.util.Log;

import lu.fisch.canze.decoders.GVRET;
import lu.fisch.canze.interfaces.StackListener;
import java.util.ArrayList;
import java.util.HashMap;
import lu.fisch.canze.exeptions.NoDecoderException;
import lu.fisch.canze.decoders.BOB;
import lu.fisch.canze.decoders.CRDT;
import lu.fisch.canze.decoders.Decoder;
import lu.fisch.canze.MainActivity;

/**
 *
 * @author robertfisch
 */
public class Stack {
    
    private final ArrayList<StackListener> stackListeners = new ArrayList<>();
    
    private Decoder decoder = new CRDT();

    private int invalid = 0;
    
    public void process(String line) throws NoDecoderException
    {
        //Log.d(MainActivity.TAG,"Process line: "+line);

        // decode the frame
        Frame frame = null;

        try {
            frame = decoder.decodeFrame(line);
        }
        catch(Exception e)
        {
            Log.d(MainActivity.TAG,"Problematic line = "+line);
            //e.printStackTrace();
        }


        if(frame!=null)
            notifyStackListeners(frame);
    }

    public void process(int[] data) throws NoDecoderException
    {
        // decodeFrame the frame
        ArrayList<Frame> frames = decoder.process(data);

        //MainActivity.debug("Frames = "+frames.size());

        for(int i=0; i<frames.size(); i++)
        {
            Frame frame = frames.get(i);

            // notify the listeners about if the precessed frame is OK
            if(frame!=null)
                notifyStackListeners(frame);
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
    
    public void addListener(StackListener stackListener)
    {
        if(!stackListeners.contains(stackListener))
            stackListeners.add(stackListener);
    }
    
    public void removeSackListener(StackListener stackListener)
    {
        stackListeners.remove(stackListener);
    }
    
    /**
     * Notify all listeners synchronously
     * @param frame     the frame to send 
     */
    private void notifyStackListeners(Frame frame)
    {
        notifyStackListeners(frame,false);
    }

    /**
     * Notify all listeners
     * @param frame     the frame to send        
     * @param async     true for asynchronous notifications (one thread per listener)
     */
    private void notifyStackListeners(Frame frame, boolean async)
    {
        //Log.d(MainActivity.TAG,"Listeners: "+stackListeners.size());
        if(async == false) {
            for(int i=0; i<stackListeners.size(); i++) {
                stackListeners.get(i).onFrameCompleteEvent(frame.clone());
            }
        } else {
            // clone the frame to make sure modifications will 
            final Frame clone = frame.clone();
            for(int i=0; i<stackListeners.size(); i++) {
                final int index = i;
                (new Thread(new Runnable() {

                    @Override
                    public void run() {
                        stackListeners.get(index).onFrameCompleteEvent(clone.clone());                   
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
        
        Frame f;
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


