/*
 * This class represents a multi-frame. Basically it is the same a normal
 * frame but it holds informations for assembling. 
 *
 * Multi-frames are supposed to be incoming _IN ORDER_.
 */
package lu.fisch.can;

import lu.fisch.can.decoders.Utils;

/**
 *
 * @author robertfisch
 */
public class MultiFrame extends Frame {
    
    private int type = 0;
    private int length = 0;
    private int sequence = 0;
    
    /**
     * Create a multi-frame from a standard frame
     * @param frame 
     */
    public MultiFrame(Frame frame)
    {
        super(frame.getId(),frame.getTimestamp(),frame.getData());
        // extract frame type
        type = Utils.getFirstNibble(data[0]);
        if(type==0x01)
        {
            // extract length
            length = Utils.getLastNibble(data[0]);
            length = length << 8;
            length += data[1];
        
            // only the 6 last bytes are considered as payload
            int[] newData = new int[6];
            for(int i=2; i<data.length; i++)
                newData[i-2]=data[i];
            data = newData;
        }
        else if(type==0x02)
        {
            sequence = Utils.getLastNibble(data[0]);

            // only the 7 last bytes are considered as payload
            int[] newData = new int[data.length-1];
            for(int i=1; i<data.length; i++)
                newData[i-1]=data[i];
            data = newData;
        }
    }

    private MultiFrame(int id, long timestamp, int[] data) {
        super(id, timestamp, data);
    }

    /**
     * Clones this multi-frame
     * @return      the cloned multi-frame
     */
    @Override
    public MultiFrame clone()
    {
        MultiFrame multiFrame = new MultiFrame(getId(),getTimestamp(),getData().clone());
        multiFrame.length = this.length;
        multiFrame.sequence = this.sequence;
        multiFrame.type = this.type;
        return multiFrame;
    }
    
    @Override
    public boolean isMultiFrame()
    {
        return true;
    }
    
    /** 
     * The frame is supposed to be complete if the received data is
     * at least as long as the announced length.
     * @return      true if the multi-frame is complete
     */
    public boolean isComplete()
    {
        return getData().length>=length;
    }

    
    public void addFrameData(Frame frame) {
        int min = Math.min(length,data.length+frame.getData().length-1);
        int[] newData = new int[min];
        for(int i=0; i<data.length; i++)
            newData[i]=data[i];
        // the first byte (type+sequence) has to be removed
        for(int i=1; i<frame.getData().length; i++)
            // ignore anything beyond the indicated frame length
            if(i+data.length-1<min)
                newData[i+data.length-1]=frame.getData()[i];
        data = newData;
        // increment the frame data
        sequence++;
    }
    
    
    
    /* --------------------------------
     * Getters & setters
     \ ------------------------------ */

    public int getType() {
        return type;
    }

    public int getLength() {
        return length;
    }

    public int getSequence() {
        return sequence;
    }
    
    @Override
    public String toString() {
        return super.toString()+"\nLength: "+length+" ("+(data.length)+")";
    }
    
    
    
    /* --------------------------------
     * Tests ...
     \ ------------------------------ */
    
    public static void main(String[] args)
    {
        // create a frame
        MultiFrame mf = new MultiFrame(new Frame(0x7bb, 0, new int[] {0x10,0x66,0x61,0x67,0xf0,0xf0,0xf0,0xf0}));
        System.out.println(mf);
        // add a second frame
        mf.addFrameData(new Frame(0x7bb,0,new int[] {0x21,0xf0,0x12,0x34,0x45,0xf0,0xf0,0xf0}));
        System.out.println(mf);
    }    
}
