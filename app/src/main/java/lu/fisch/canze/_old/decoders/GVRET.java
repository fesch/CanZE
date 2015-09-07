/*
 * Decodes a frame in GVRET (protocol, not the obsolete logging) format.
 */
package lu.fisch.canze._old.decoders;

import java.util.ArrayList;

import lu.fisch.canze.actors.Message;

/**
 *
 * @author robertfisch
 */
public class GVRET implements Decoder {

    private static final int IDLE             = 0;
    private static final int GET_COMMAND      = 1;
    private static final int BUILD_CAN_FRAME  = 2;


    private int rxState = IDLE;
    private int rxStep;
    private long timestamp;
    private int id;
    private int length;
    private int[] dataBuf = new int[8];

    public Message decodeFrame(int[] bin) {
        // split up the text
        if(bin[0] == 0xf1 && bin[1] == 0x00 && bin[10] > 0) {
            // get the timestamp
            long timestamp = bin[2] + bin[3] * 0x100 + bin[4] * 0x10000 + bin[5] * 0x1000000;
            // get the id
            int id =  bin[6] + bin[7] * 0x100; // ignore 29 bit, not relevant for the Zoe / Fluence anyway
            int length = bin[10] & 0x0f;
            int[] data = new int [length];
            for (int i = 0; i < length; i++)
                data[i] += bin[i+10];
            // create & return a new frame
            return new Message(id, timestamp, data);
        }
        return null;
    }

    @Override
    public Message decodeFrame(String line) {
        // not possible for this decoder
        return null;
    }

    @Override
    public ArrayList<Message> process(int[] input) {
        ArrayList<Message> result = new ArrayList<>();

        for(int i=0; i<input.length; i++)
        {
            Message f = receiveCode(input[i]);
            if(f!=null) result.add(f);
        }

        return result;
    }

    public Message receiveCode (int code) {
        int dataidx;

        switch (rxState) {
            case IDLE:
                if (code == 0xf1) rxState = GET_COMMAND;
                break;
            case GET_COMMAND:
                switch (code) {
                    case 0: //receiving a can frame
                        rxState = BUILD_CAN_FRAME;
                        rxStep = 0;
                        break;
                    default:
                        rxState = IDLE;
                        rxStep = 0;
                        break;
                }
                break;

            case BUILD_CAN_FRAME:
                switch (rxStep) {
                    case 0:
                        timestamp = code;
                        break;
                    case 1:
                        timestamp = timestamp + (code << 8);
                        break;
                    case 2:
                        timestamp = timestamp + (code << 16);
                        break;
                    case 3:
                        timestamp = timestamp + (code << 24);
                        break;
                    case 4:
                        id = code;
                        break;
                    case 5:
                        id = id + (code << 8);
                        break;
                    case 6:
                        id = id + (code << 16);
                        break;
                    case 7:
                        id = id + (code << 24);
                        break;
                    case 8:
                        length = code & 0x07;
                        break;
                    default:
                        if (rxStep < length + 9) {
                            dataBuf[rxStep - 9] = code;
                        } else {
                            int[] data = new int[length];
                            for (int i = 0; i < length; i++)
                                data[i] += dataBuf[i];
                            rxState = IDLE;
                            rxStep = 0;
                            return new Message(id, timestamp, data);
                        }
                        break;
                }
                rxStep++;
                break;

            default:
                break;

        }
        return null;
    }
    
    /* --------------------------------
     * Tests ...
     \ ------------------------------ */

    public static void main(String[] args)
    {
        Message f = (new GVRET()).decodeFrame(new int[]{
                0xf1, 0,                         // receive frame
                0x02, 0x01, 0x00, 0x00,          // timestamp
                0xbb, 0x07, 0x00, 0x00,          // id
                0x08,                            // length & bus
                0x10, 0x66, 0x61, 0x67,          // data
                0xf0, 0xf0, 0xf0, 0xf0});
        System.out.println(f);
    }

}
