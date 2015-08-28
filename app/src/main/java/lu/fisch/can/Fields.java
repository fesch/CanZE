/*
 * This class manages all know fields.
 * Actually only the simple fields from the free CAN stream are handled.
 */
package lu.fisch.can;

import lu.fisch.can.interfaces.StackListener;
import lu.fisch.canze.MainActivity;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 *
 * @author robertfisch
 */
public class Fields implements StackListener {
    
    private static final int FIELD_ID           = 0;    
    private static final int FIELD_FROM         = 1;    
    private static final int FIELD_TO           = 2;    
    private static final int FIELD_DEVIDER      = 3;    
    private static final int FIELD_MULTIPLIER   = 4;    
    private static final int FIELD_OFFSET       = 5;    
    private static final int FIELD_DECIMALS     = 6;    
    private static final int FIELD_FORMAT       = 7;    
    private static final int FIELD_UNIT         = 8;    
    
    private final ArrayList<Field> fields = new ArrayList<>();

    public void fillStatic()
    {
        String fieldDef = // startBit, endBit, divider, multiplier, offset, decimals, format
                 "0x0c6, 0, 15, 1, 1, 0x8000, 0, Steering pos:%5ld, \n"
                +"0x0c6, 16, 29, 1, 1, 0x2000, 0, Steering ac: %5ld, \n"
                +"0x12e, 0, 7, 1, 1, 198, 0, Accel F/R: %4ld, \n"
                +"0x12e, 8, 23, 1, 1, 0x8000, 0, Incl: %4ld, \n"
                +"0x12e, 24, 35, 1, 1, 0x800, 0, Accel L/R: %4ld, \n"
                +"0x17e, 50, 51, 1, 1, 0, 0, Gear: %4ld, \n"
                +"0x186, 0, 11, 4, 1, 10, 10, Speed(c): %3ld.%02ld, km/h\n"
                +"0x186, 16, 27, 1, 1, 0, 0, Accel(a): %4ld, \n"
                +"0x186, 28, 39, 1, 1, 0, 0, Accel(b): %4ld, \n"
                +"0x186, 40, 49, 1, 1, 0, 0, Pedal: %4ld, \n"
                +"0x18a, 16, 25, 1, 1, 0, 0, Pedal cc: %4ld, \n"
                +"0x18a, 27, 27, 1, 1, 0, 0, Pedal (further): %4ld, \n"
                +"0x18a, 31, 38, 1, 1, 0, 0, Regen?: %4ld, \n"
                +"0x1f6, 20, 20, 1, 5, 0, 10, Break pedal: %4ld, \n"
                +"0x1fd, 0, 7, 1, 5, 0, 10, Amp 12V: %2ld.%01ld, A\n"
                +"0x1fd, 48, 55, 1, 1, 0x50, 0, KwDash: %4ld, kW\n"
                +"0x29a, 0, 15, 1, 1, 0, 0, Speed FR: %5ld, \n"
                +"0x29a, 16, 31, 1, 1, 0, 0, Speed FL: %5ld, \n"
                +"0x29a, 32, 47, 1, 1, 0, 100, Speed F100: %3ld.%02ld, \n"
                +"0x29c, 0, 15, 1, 1, 0, 0, Speed RR: %5ld, \n"
                +"0x29c, 16, 31, 1, 1, 0, 0, Speed RL: %5ld, \n"
                +"0x29c, 48, 63, 1, 1, 0, 100, Speed R100: %3ld.%02ld, \n"
                +"0x352, 24, 31, 1, 1, 0, 0, Break force %5ld, \n"
                +"0x35c, 4, 15, 1, 1, 0, 0, Key-Start: %5ld, \n"
                +"0x35c, 16, 39, 1, 1, 0, 0, Minutes: %7ld, min\n"
                +"0x3f7, 2, 3, 1, 1, 0, 0, Gear??: %4ld, \n"
                +"0x42a, 16, 23, 1, 1, 0, 0, Temp set: %5ld, \n"
                +"0x42e, 0, 12, 49, 1, 0, 0, SOC(a): %5ld, %\n"
                +"0x42e, 24, 36, 16, 100, 0, 100, Batt V: %3ld.%02ld, V\n"
                +"0x42e, 38, 43, 1, 1, 0, 1, Current: %3ld, A\n"
                +"0x42e, 56, 63, 1, 1, 0, 10, EOL kWh: %3ld.%01ld, kWh\n"
                +"0x4f8, 0, 1, 1, -1, -2, 0, Start: %4ld, \n"
                +"0x4f8, 4, 5, 1, -1, -2, 0, Park.break: %4ld, \n"
                +"0x534, 32, 40, 1, 1, 40, 0, Temp out: %4ld, C\n"
                +"0x5d7, 0, 15, 1, 1, 0, 100, Speed(a): %3ld.%02ld, \n"
                +"0x5d7, 16, 43, 1, 1, 0, 100, Odo: %5ld.%02ld, km\n"
                +"0x5de, 1, 1, 1, 1, 0, 0, Right: %5ld, \n"
                +"0x5de, 2, 2, 1, 1, 0, 0, Left: %5ld, \n"
                +"0x5de, 5, 5, 1, 1, 0, 0, Park light: %5ld, \n"
                +"0x5de, 6, 6, 1, 1, 0, 0, Head light: %5ld, \n"
                +"0x5de, 7, 7, 1, 1, 0, 0, Beam light: %5ld, \n"
                +"0x5de, 12, 12, 1, 1, 0, 0, FL door open: %1ld, \n"
                +"0x5de, 14, 14, 1, 1, 0, 0, FR door open: %1ld, \n"
                +"0x5de, 17, 17, 1, 1, 0, 0, RL door open: %1ld, \n"
                +"0x5de, 19, 19, 1, 1, 0, 0, RR door open: %1ld, \n"
                +"0x5de, 59, 59, 1, 1, 0, 0, Hatch door open: %1ld, \n"
                +"0x5ee, 0, 0, 1, 1, 0, 0, Park light: %5ld, \n"
                +"0x5ee, 1, 1, 1, 1, 0, 0, Head light: %5ld, \n"
                +"0x5ee, 2, 2, 1, 1, 0, 0, Beam light: %5ld, \n"
                +"0x5ee, 0, 4, 1, 1, 0, 0, Headlights %4ld, \n"
                +"0x5ee, 16, 19, 1, 1, 0, 0, Door locks %4ld, \n"
                +"0x5ee, 20, 24, 1, 1, 0, 0, Flashers %4ld, \n"
                +"0x5ee, 24, 27, 1, 1, 0, 0, Doors %4ld, \n"
                +"0x646, 8, 15, 1, 1, 0, 10, avg trB cons %2ld.%01ld, kWh/100km\n"
                +"0x646, 16, 32, 1, 1, 0, 10, trB dist %5ld.%01ld, km\n"
                +"0x646, 33, 47, 1, 1, 0, 10, trB cons %5ld.%01ld, kWh\n"
                +"0x646, 48, 59, 1, 1, 0, 10, avg trB spd %5ld.%01ld, km/h\n"
                +"0x653, 9, 9, 1, 1, 0, 0, dr seatbelt %4ld, \n"
                +"0x654, 24, 31, 1, 1, 0, 0, SOC(b): %4ld, \n"
                +"0x654, 32, 41, 1, 1, 0, 0, Time to full %4ld, min\n"
                +"0x654, 42, 51, 1, 1, 0, 0, Km avail: %4ld, km\n"
                +"0x654, 52, 61, 1, 1, 0, 10, kw/100Km %2ld.%01ld, \n"
                +"0x658, 0, 31, 1, 1, 0, 0, S# batt:%10ld, \n"
                +"0x658, 32, 39, 1, 1, 0, 0, Bat health %4ld, %\n"
                +"0x65b, 25, 26, 1, 1, 0, 0, ECO Mode: %1ld, \n"
                +"0x65b, 41, 42, 1, 1, 0, 0, battery flap: %1ld, \n"
                +"0x66a, 5, 7, 1, 1, 0, 0, CruisC mode %4ld, \n"
                +"0x66a, 8, 15, 1, 1, 0, 0, CruisC spd %4ld, km/h\n"
                +"0x66a, 42, 42, 1, 1, 0, 0, CruisC ? %4ld, \n"
                +"0x699, 2, 3, 1, 1, 0, 0, Clima rear dfr %4ld, \n"
                +"0x699, 4, 4, 1, -1, -1, 0, Clima auto %4ld, \n"
                +"0x699, 5, 5, 1, 1, 0, 0, Clima Maxdfr %4ld, \n"
                +"0x699, 6, 6, 1, 1, 0, 0, Clima autofan %4ld, \n"
                +"0x699, 10, 14, 2, 1, 0, 0, Clima temp %4ld, \n"
                +"0x699, 16, 16, 1, 1, 0, 0, Clima wshld %4ld, \n"
                +"0x699, 18, 18, 1, 1, 0, 0, Clima face %4ld, \n"
                +"0x699, 19, 19, 1, 1, 0, 0, Clima feet %4ld, \n"
                +"0x699, 20, 21, 1, 1, 0, 0, Clima recycling %4ld, \n"
                +"0x699, 22, 23, 1, 1, 0, 0, ECO mode %4ld, \n"
                +"0x699, 24, 27, 1, 1, 0, 0, Clima fan %4ld, \n"
                +"0x699, 28, 31, 1, 1, 0, 0, Clima chg %4ld, \n"
                +"0x699, 52, 53, 1, 1, 0, 0, Clima AUTO %4ld, \n"
                +"0x699, 54, 55, 1, 1, 0, 0, Clima AC %4ld, \n"
                +"0x699, 56, 56, 1, 1, 0, 0, Clima fan %4ld, \n"
                +"0x69f, 0, 31, 1, 1, 0, 0, S# car: %10ld, \n"
                +"0x6f8, 16, 23, 16, 100, 0, 100, Bat12: %2ld.%02ld, V\n";
        String[] lines = fieldDef.split("\n");
        for(int i=0; i<lines.length; i++)
        {
            String line = lines[i];
            //Get all tokens available in line
            String[] tokens = line.split(",");
            if (tokens.length > 0) {
                //Create a new field object and fill his  data
                Field field = new Field(
                        Integer.parseInt(tokens[FIELD_ID].trim().replace("0x", ""), 16),
                        Integer.parseInt(tokens[FIELD_FROM].trim()),
                        Integer.parseInt(tokens[FIELD_TO].trim()),
                        Integer.parseInt(tokens[FIELD_DEVIDER].trim()),
                        Integer.parseInt(tokens[FIELD_MULTIPLIER].trim()),
                        (
                                tokens[FIELD_OFFSET].trim().contains("0x")
                                        ?
                                        Integer.parseInt(tokens[FIELD_OFFSET].trim().replace("0x", ""), 16)
                                        :
                                        Integer.parseInt(tokens[FIELD_OFFSET].trim())
                        ),
                        Integer.parseInt(tokens[FIELD_DECIMALS].trim()),
                        tokens[FIELD_FORMAT],
                        tokens[FIELD_UNIT]
                );
                // add the fieled to the list of available fields
                fields.add(field);
            }
        }
    }

    public void readFromFile(String filename) throws FileNotFoundException, IOException
    {
        BufferedReader fileReader = new BufferedReader(new FileReader(filename));
        String line;
        //Read the file line by line starting from the second line
        while ((line = fileReader.readLine()) != null) 
        {
            //Get all tokens available in line
            String[] tokens = line.split(",");
            if (tokens.length > 0) 
            {
                //Create a new field object and fill his  data
                Field field = new Field(
                        Integer.parseInt(tokens[FIELD_ID].trim().replace("0x", ""),16),
                        Integer.parseInt(tokens[FIELD_FROM].trim()),
                        Integer.parseInt(tokens[FIELD_TO].trim()),
                        Integer.parseInt(tokens[FIELD_DEVIDER].trim()),
                        Integer.parseInt(tokens[FIELD_MULTIPLIER].trim()),
                        (
                            tokens[FIELD_OFFSET].trim().contains("0x")
                            ?
                            Integer.parseInt(tokens[FIELD_OFFSET].trim().replace("0x", ""),16)
                            :
                            Integer.parseInt(tokens[FIELD_OFFSET].trim())
                        ),
                        Integer.parseInt(tokens[FIELD_DECIMALS].trim()),
                        tokens[FIELD_FORMAT],
                        tokens[FIELD_UNIT]
                );
                // add the filed to the list of available fields
                fields.add(field);
            }
        }
    }

    public Field getBySID(String sid)
    {
        for(int i=0; i<fields.size(); i++)
            if(fields.get(i).getSID().equals(sid))
                return fields.get(i);
        return null;
    }
    
    public Field getFieldByFormat(String formatStartsWith)
    {
        for(int i=0; i<fields.size(); i++)
        {
            Field field = fields.get(i);
            if(field.getFormat().startsWith(formatStartsWith))
                return field;
        }
        return null;
    }

    public int size() {
        return fields.size();
    }

    public Field get(int index) {
        return fields.get(index);
    }

    public Object[] toArray() {
        return fields.toArray();
    }
    
    @Override
    public void onFrameCompleteEvent(Frame frame) {
        for(int i=0; i<fields.size(); i++)
        {
            Field field = fields.get(i);
            if(field.getId()==frame.getId())
            {
                String binString = frame.getAsBinaryString();
                if(binString.length()>=field.getTo()) {
                    // parseInt --> signed, so the first bit is "cut-off"!
                    try {
                        field.setValue(Integer.parseInt("0" + binString.substring(field.getFrom(), field.getTo() + 1), 2));
                    } catch (Exception e)
                    {
                        // ignore
                    }
                }
            }
        }
    }

    
    /* --------------------------------
     * Tests ...
     \ ------------------------------ */
    
    public static void main(String[] args)
    {
        
    }
    
}
