/*
 * This class represents the CAN frame stack. For multi-frames it stores them
 * until they are complete, then it pushes them over to the listeners.
 */
package lu.fisch.can;

import android.util.Log;

import lu.fisch.can.interfaces.StackListener;
import java.util.ArrayList;
import java.util.HashMap;
import lu.fisch.can.exeptions.NoDecoderException;
import lu.fisch.can.decoders.BOB;
import lu.fisch.can.decoders.CRDT;
import lu.fisch.can.decoders.Decoder;
import lu.fisch.canze.MainActivity;

/**
 *
 * @author robertfisch
 */
public class Stack {
    
    private final static int[] MULTI_FRAME_IDS = {0x7bb};
    
    private final ArrayList<StackListener> stackListeners = new ArrayList<>();
    
    private final HashMap<Integer,MultiFrame> frameStack = new HashMap<>();
    
    private Decoder decoder = null;

    private int invalid = 0;
    
    /**
     * Detects the decoder to used depending on the first line that is being processed.
     * If a decoder is being detected, the internal attribute is being set too.
     * @param line      the data
     * @return          the decoder to be used
     * @throws NoDecoderException 
     */
    private Decoder detectDecoder(String line) throws NoDecoderException
    {
        if(decoder!=null) return decoder;
        // CRDT is space delimited and has at least 4 fields
        String[] pieces = line.split(" ");
        if(pieces.length>3) decoder=new CRDT();
        else {
            // Bob uses comma separation and has 2 fields
            pieces = line.split(",");
            if (pieces.length == 2) decoder = new BOB();
        }
        if (decoder == null) throw new NoDecoderException();
        return decoder;
    }

    private Decoder detectDecoder(int[] data) throws NoDecoderException
    {
        if(decoder!=null) return decoder;

        String dataString = "";
        for(int i=0; i<data.length; i++)
            dataString += (char) data[i];

        decoder = detectDecoder(dataString);

        return decoder;
    }

    /**
     * Test if a frame is a multi-frame
     * @param frame     the frame to be tested
     * @return          true if the passed frame is a multi-frame, false otherwise
     */
    private boolean isMultiFrame(Frame frame)
    {
        for(int i=0; i<MULTI_FRAME_IDS.length; i++)
            if(MULTI_FRAME_IDS[i]==frame.getId())
                return true;
        return false;
    }
    
    private Frame process(Frame frame) {
        if(isMultiFrame(frame))
        {
            MultiFrame multiFrame = new MultiFrame(frame);

            if(multiFrame.getType()==0x01)
            {
                // add the frame to the stack,
                // possiblity overwrite a non complete frame with the same ID
                frameStack.put(multiFrame.getId(), multiFrame);
                // only return the frame if if is complete ...
                if(multiFrame.isComplete())
                    return multiFrame;
                // otherwise wait for the missing parts
                else
                    return null;
            }
            else if(multiFrame.getType()==0x02)
            {
                // discard the frame if no 0x01 frame has been received yet
                if(!frameStack.containsKey(multiFrame.getId())) return null;
                /// get the frame
                MultiFrame firstFrame = frameStack.get(multiFrame.getId());
                // check the sequence
                if(firstFrame.getSequence()+1==multiFrame.getSequence())
                {
                    // add the frame
                    firstFrame.addFrameData(frame);
                }
                
                // only return the frame if if is complete ...
                if(firstFrame.isComplete())
                    return firstFrame;
                // otherwise wait for the missing parts
                else
                    return null;
            }
            else 
            {
                //System.err.println(multiFrame);
                throw new UnsupportedOperationException("Not supported yet.");
            } 
        }
        else return frame;
    }
    
    public void process(String line) throws NoDecoderException
    {
        //Log.d(MainActivity.TAG,"Process line: "+line);

        // decode the frame
        Frame frame = null;

        try {
            frame = detectDecoder(line).decodeFrame(line);
        }
        catch(Exception e)
        {
            Log.d(MainActivity.TAG,"Problematic line = "+line);
            //e.printStackTrace();
        }
        // process and possibly discard or retain the frame
        if(frame!=null)
            frame = process(frame);
        else {
            invalid++;
            //Log.w(MainActivity.TAG,"Invalid framecount = "+invalid);
        }

        if(frame!=null)
            notifyStackListeners(frame);
    }

    public void process(int[] data) throws NoDecoderException
    {
        // decodeFrame the frame
        ArrayList<Frame> frames = detectDecoder(data).process(data);

        //MainActivity.debug("Frames = "+frames.size());

        for(int i=0; i<frames.size(); i++)
        {
            Frame frame = frames.get(i);

            // process and possibly discard or retain the frame
            frame = process(frame);

            // notify the listeners about if the precessed frame is OK
            if(frame!=null)
                notifyStackListeners(frame);
        }
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
    }      
    
}


