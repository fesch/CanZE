/*
 * This class manages all know fields.
 * Actually only the simple fields from the free CAN stream are handled.
 */
package lu.fisch.canze.actors;

import android.os.Environment;

import lu.fisch.canze.activities.MainActivity;
import lu.fisch.canze.interfaces.MessageListener;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author robertfisch test
 */
public class Fields implements MessageListener {

    private static final int FIELD_ID           = 0;
    private static final int FIELD_FROM         = 1;    
    private static final int FIELD_TO           = 2;    
    private static final int FIELD_DIVIDER      = 3;
    private static final int FIELD_MULTIPLIER   = 4;    
    private static final int FIELD_OFFSET       = 5;    
    private static final int FIELD_DECIMALS     = 6;    
    private static final int FIELD_FORMAT       = 7;
    private static final int FIELD_UNIT         = 8;
    private static final int FIELD_REQUEST_ID   = 9;
    private static final int FIELD_RESPONSE_ID  = 10;
    private static final int FIELD_DESCRIPTION  = 11;
    private static final int FIELD_CAR          = 12;
    private static final int FIELD_SKIPS        = 13;
    private static final int FIELD_FREQ         = 14;

    public static final int CAR_ANY             = 0;
    public static final int CAR_FLUENCE         = 1;
    public static final int CAR_ZOE             = 2;
    public static final int CAR_KANGOO          = 3;
    public static final int CAR_TWIZY           = 4;    // you'll never know ;-)
    public static final int CAR_X10             = 5;

    public static final int TOAST_NONE          = 0;
    public static final int TOAST_DEVICE        = 1;
    public static final int TOAST_ALL           = 2;

    private final ArrayList<Field> fields = new ArrayList<>();
    private final HashMap<String, Field> fieldsBySid = new HashMap<>();

    private static Fields instance = null;

    private int car = CAR_ANY;

    private Fields() {
        fillStatic();
    }

    public static Fields getInstance()
    {
        if(instance==null) instance=new Fields();
        return instance;
    }

    private void fillStatic()
    {
        String fieldDef = // startBit, endBit, divider, multiplier, offset, decimals, format, requestID, responseID
                ""
/* old
                        +"0x0c6, 0, 15, 1, 1, 0x8000, 0, Steering pos:%5ld, , , , Steering Position, 0, 0\n"
                        +"0x0c6, 16, 29, 1, 1, 0x2000, 0, Steering ac: %5ld, , , , Steering Acceleration, 0, 0\n"
                        +"0x12e, 0, 7, 1, 1, 198, 0, Accel F/R: %4ld, , , , , 0, 0\n"
                        +"0x12e, 8, 23, 1, 1, 0x8000, 0, Incl: %4ld, , , , , 0, 0\n"
                        +"0x12e, 24, 35, 1, 1, 0x800, 0, Accel L/R: %4ld, , , , , 0, 0\n"
                        +"0x17e, 50, 51, 1, 1, 0, 0, Gear: %4ld, , , , Gear, 0, 0\n"
                        +"0x186, 0, 11, 4, 1, 0, 10, Speed(c): %3ld.%02ld, km/h, , , Speed, 0, 0\n"
                        +"0x186, 16, 27, 1, 1, 800, 0, Accel(a): %4ld, , , , Acceleration, 0, 0\n"
                        +"0x186, 28, 39, 1, 1, 800, 0, Accel(b): %4ld, , , , Acceleration, 0, 0\n"
                        +"0x186, 40, 49, 1, 1, 0, 0, Pedal: %4ld, , , , Throttle, 0, 0\n"
                        +"0x18a, 16, 25, 1, 1, 0, 0, Pedal cc: %4ld, , , , Throttle, 0, 0\n"
                        +"0x18a, 27, 27, 1, 1, 0, 0, Pedal (further): %4ld, , , , Throttle Pushthrough, 0, 0\n"
                        +"0x18a, 31, 38, 1, 1, 0, 0, Regen?: %4ld, , , , , 0, 0\n"
                        +"0x1f6, 20, 20, 1, 5, 0, 10, Break pedal: %4ld, , , , Break Pedal, 0, 0\n"
                        +"0x1fd, 0, 7, 1, 5, 0, 10, Amp 12V: %2ld.%01ld, A, , , 12V Battery Current, 0, 0\n"
                        +"0x1fd, 48, 55, 1, 1, 0x50, 0, KwDash: %4ld, kW, , , Consumption, 0, 0\n"
                        +"0x218, 0, 15, 1, 1, 0, 0, , , , , , 0, 0\n"
                        +"0x29a, 0, 15, 1, 1, 0, 0, Speed FR: %5ld, , , , Speed Front Right, 0, 0\n"
                        +"0x29a, 16, 31, 1, 1, 0, 0, Speed FL: %5ld, , , , Speed Front Left, 0, 0\n"
                        +"0x29a, 32, 47, 1, 1, 0, 100, , , , , , 0, 0\n"
                        +"0x29c, 0, 15, 1, 1, 0, 0, Speed RR: %5ld, , , , Speed Rear Right, 0, 0\n"
                        +"0x29c, 16, 31, 1, 1, 0, 0, Speed RL: %5ld, , , , Speed Rear Left, 0, 0\n"
                        +"0x29c, 48, 63, 1, 1, 0, 100, Speed R100: %3ld.%02ld, , , , , 0, 0\n"
                        +"0x352, 24, 31, 1, 1, 0, 0, Brake force %5ld, , , , Break Force, 0, 0\n"
                        +"0x35c, 4, 15, 1, 1, 0, 0, Key-Start: %5ld, , , , , 0, 0\n"
                        +"0x35c, 16, 39, 1, 1, 0, 0, Minutes: %7ld, min, , , , 0, 0\n"
                        +"0x3f7, 2, 3, 1, 1, 0, 0, Gear??: %4ld, , , , , 2, 0\n"
                        +"0x42a, 16, 23, 1, 1, 0, 0, Temp set: %5ld, , , , , 1, 0\n"
                        +"0x42e, 0, 12, 49, 1, 0, 0, SOC(a): %5ld, %, , , State of Charge, 0, 0\n"
                        +"0x42e, 24, 35, 16, 100, 0, 100, AC V: %3ld.%02ld, V, , , , 2, 0\n"
                        +"0x42e, 25, 35, 2, 100, 0, 100, DC V: %3ld.%02ld, V, , , , 1, 0\n"
                        +"0x42e, 38, 43, 1, 1, 0, 1, AC pilot current: %3ld, A, , , Charing Pilot Current, 0, 0\n"
                        +"0x42e, 56, 63, 1, 1, 0, 10, EOC kWh: %3ld.%01ld, kWh, , , Remaining Capacity to Charge, 0, 0\n"
                        +"0x439, 0, 15, 1, 1, 0, 0, Accel? %5ld, ?, , , , 0, 0\n"
                        +"0x4f8, 0, 1, 1, -1, -2, 0, Start: %4ld, , , , , 0, 0\n"
                        +"0x4f8, 4, 5, 1, -1, -2, 0, Park.break: %4ld, , , , Parking Break, 0, 0\n"
                        +"0x4f8, 24, 39, 1, 1, 0, 100, Speed(d): %3ld.%02ld, , , , Speed, 2, 0\n"
                        +"0x534, 32, 40, 1, 1, 40, 0, Temp out: %4ld, C, , , , 0, 0\n"
                        +"0x5d7, 0, 15, 1, 1, 0, 100, Speed(a): %3ld.%02ld, km/h, , , Speed, 0, 0\n"
                        +"0x5d7, 16, 43, 1, 1, 0, 100, Odo: %5ld.%02ld, km, , , Odometer, 0, 0\n"
                        +"0x5de, 1, 1, 1, 1, 0, 0, Right: %5ld, , , , Right Indicator, 0, 0\n"
                        +"0x5de, 2, 2, 1, 1, 0, 0, Left: %5ld, , , , Left Indicator, 0, 0\n"
                        +"0x5de, 5, 5, 1, 1, 0, 0, Park light: %5ld, , , , Park Light, 0, 0\n"
                        +"0x5de, 6, 6, 1, 1, 0, 0, Head light: %5ld, , , , Head Light, 0, 0\n"
                        +"0x5de, 7, 7, 1, 1, 0, 0, Beam light: %5ld, , , , Beam Light, 0, 0\n"
                        +"0x5de, 12, 12, 1, 1, 0, 0, FL door open: %1ld, , , , Door Front Left, 0, 0\n"
                        +"0x5de, 14, 14, 1, 1, 0, 0, FR door open: %1ld, , , , Dort Front Right, 0, 0\n"
                        +"0x5de, 17, 17, 1, 1, 0, 0, RL door open: %1ld, , , , Door Rear Left, 0, 0\n"
                        +"0x5de, 19, 19, 1, 1, 0, 0, RR door open: %1ld, , , , Door Rear Right, 0, 0\n"
                        +"0x5de, 59, 59, 1, 1, 0, 0, Hatch door open: %1ld, , , , Door Hatch, 0, 0\n"
                        +"0x5ee, 0, 0, 1, 1, 0, 0, Park light: %5ld, , , , Park Light, 0, 0\n"
                        +"0x5ee, 1, 1, 1, 1, 0, 0, Head light: %5ld, , , , Head Light, 0, 0\n"
                        +"0x5ee, 2, 2, 1, 1, 0, 0, Beam light: %5ld, , , , Beam Light, 0, 0\n"
                        +"0x5ee, 0, 4, 1, 1, 0, 0, Headlights %4ld, , , , Head Light, 0, 0\n"
                        +"0x5ee, 16, 19, 1, 1, 0, 0, Door locks %4ld, , , , Door Locks, 0, 0\n"
                        +"0x5ee, 20, 24, 1, 1, 0, 0, Flashers %4ld, , , , Indicators, 0, 0\n"
                        +"0x5ee, 24, 27, 1, 1, 0, 0, Doors %4ld, , , , Doors, 0, 0\n"
                        +"0x646, 8, 15, 1, 1, 0, 10, avg trB cons %2ld.%01ld, kWh/100km, , , , 0, 0\n"
                        +"0x646, 16, 32, 1, 1, 0, 10, trB dist %5ld.%01ld, km, , , , 0, 0\n"
                        +"0x646, 33, 47, 1, 1, 0, 10, trB cons %5ld.%01ld, kWh, , , , 0, 0\n"
                        +"0x646, 48, 59, 1, 1, 0, 10, avg trB spd %5ld.%01ld, km/h, , , , 0, 0\n"
                        +"0x653, 9, 9, 1, 1, 0, 0, dr seatbelt %4ld, , , , , 0, 0\n"
                        +"0x654, 3, 3, 1, 1, 0, 0, Plugin state, , , , Plugin State, 0, 0\n"
                        +"0x654, 24, 31, 1, 1, 0, 0, SOC(b): %4ld, , , , State of Charge, 0, 0\n"
                        +"0x654, 32, 41, 1, 1, 0, 0, Time to full %4ld, min, , , Time to Full, 0, 0\n"
                        +"0x654, 42, 51, 1, 1, 0, 0, Km avail: %4ld, km, , , Available Distance, 0, 0\n"
                        +"0x654, 52, 61, 1, 1, 0, 10, kw/100km %2ld.%01ld, , , , , 0, 0\n"
                        +"0x658, 0, 31, 1, 1, 0, 0, S# batt:%10ld, , , , Battery Serial N°, 0, 0\n"
                        +"0x658, 32, 39, 1, 1, 0, 0, Bat health %4ld, %, , , Battery Health, 0, 0\n"
                        +"0x65b, 25, 26, 1, 1, 0, 0, ECO Mode: %1ld, , , , Economy Mode, 0, 0\n"
                        +"0x65b, 41, 42, 1, 1, 0, 0, battery flap: %1ld, , , , Battery Flap, 0, 0\n"
                        +"0x66a, 5, 7, 1, 1, 0, 0, CruisC mode %4ld, , , , Cruise Control Mode, 0, 0\n"
                        +"0x66a, 8, 15, 1, 1, 0, 0, CruisC spd %4ld, km/h, , , Cruise Control Speed, 0, 0\n"
                        +"0x66a, 42, 42, 1, 1, 0, 0, CruisC ? %4ld, , , , , 0, 0\n"
                        +"0x68b, 0, 3, 1, 1, 0, 0, # presses MM %2ld, , , , , 0, 0\n"
                        +"0x699, 2, 3, 1, 1, 0, 0, Clima rear dfr %4ld, , , , , 2, 0\n"
                        +"0x699, 4, 4, 1, -1, -1, 0, Clima auto %4ld, , , , , 2, 0\n"
                        +"0x699, 5, 5, 1, 1, 0, 0, Clima Maxdfr %4ld, , , , , 2, 0\n"
                        +"0x699, 6, 6, 1, 1, 0, 0, Clima autofan %4ld, , , , , 2, 0\n"
                        +"0x699, 10, 14, 2, 1, 0, 0, Clima temp %4ld, C, , , , 2, 0\n"
                        +"0x699, 16, 16, 1, 1, 0, 0, Clima wshld %4ld, , , , , 2, 0\n"
                        +"0x699, 18, 18, 1, 1, 0, 0, Clima face %4ld, , , , , 2, 0\n"
                        +"0x699, 19, 19, 1, 1, 0, 0, Clima feet %4ld, , , , , 2, 0\n"
                        +"0x699, 20, 21, 1, 1, 0, 0, Clima recycling %4ld, , , , , 2, 0\n"
                        +"0x699, 22, 23, 1, 1, 0, 0, ECO mode %4ld, , , , , 2, 0\n"
                        +"0x699, 24, 27, 1, 1, 0, 0, Clima fan %4ld, , , , , 2, 0\n"
                        +"0x699, 28, 31, 1, 1, 0, 0, Clima chg %4ld, , , , , 2, 0\n"
                        +"0x699, 52, 53, 1, 1, 0, 0, Clima AUTO %4ld, , , , , 2, 0\n"
                        +"0x699, 54, 55, 1, 1, 0, 0, Clima AC %4ld, , , , , 2, 0\n"
                        +"0x699, 56, 56, 1, 1, 0, 0, Clima fan %4ld, , , , , 2, 0\n"
                        +"0x69f, 0, 31, 1, 1, 0, 0, S# car: %10ld, , , , Car Serial N°, 0, 0\n"
                        +"0x6f8, 16, 23, 16, 100, 0, 100, Bat12: %2ld.%02ld, V, , , 12V Battery Voltage, 0, 0\n"
                        +"0x760, 24, 31, 1, 1, 0, 0, Mas cyl pr %3ld, bar, 0x224b0e, 0x624b0e, , 2, 0\n"
                        +"0x762, 24, 39, 256, 100, 100, 0, Bat12: %2ld.%02ld, V, 0x22012f, 0x62012f, 12V Battery Voltage, 0, 0\n"
                        +"0x763, 24, 31, 1, 1, 0, 0, Parking brake %1ld, , 0x222001, 0x622001, Parking Break, 0, 0\n"
                        +"0x763, 3, 3, 1, 1, 0, 0, vol+ %1ld, , 0x2220f0, 0x6220f0, << UDS replies start at bit 24 - need to recheck, 0, 0\n"
                        +"0x763, 4, 4, 1, 1, 0, 0, vol- %1ld, , 0x2220f0, 0x6220f0, , 0, 0\n"
                        +"0x763, 2, 2, 1, 1, 0, 0, mute %1ld, , 0x2220f0, 0x6220f0, , 0, 0\n"
                        +"0x763, 5, 5, 1, 1, 0, 0, media %1ld, , 0x2220f0, 0x6220f0, , 0, 0\n"
                        +"0x763, 6, 6, 1, 1, 0, 0, radio %1ld, , 0x2220f0, 0x6220f0, , 0, 0\n"
                        +"0x77e, 24, 31, 1, 1, 0, 0, dcdc state %2ld, , 0x22300f, 0x62300f, , 0, 0\n"
                        +"0x77e, 24, 31, 32, 1000, 0, 1000, Batt volt %3ld.%03ld, V, 0x22300e, 0x62300e, Battery Voltage, 0, 0\n"
                        +"0x77e, 24, 31, 32, 1, 0, 0, Trq req. %5ld, Nm, 0x223024, 0x623024, , 0, 0\n"
                        +"0x77e, 24, 31, 32, 1, 0, 0, Trq app. %5ld, Nm, 0x223025, 0x623025, , 0, 0\n"
                        +"0x77e, 24, 31, 64, 100, 0, 100, Inv temp %2ld.%02ld, C, 0x22302b, 0x62302b, , 0, 0\n"
                        +"0x77e, 24, 31, 16, 100, 0, 100, Current %3ld.%02ld, A, 0x22301d, 0x62301d, , 0, 0\n"
                        +"0x7bb, 336, 351, 1, 1, 0, 100, Max batt in pw:%2ld.%02ld, kW, 0x2101, 0x6101, , 2, 0\n"
                        +"0x7bb, 192, 207, 1, 1, 0, 100, Max batt in pw:%2ld.%02ld, kW, 0x2101, 0x6101, , 1, 0\n"
                        +"0x7bb, 208, 223, 1, 1, 0, 100, Max batt out pw:%2ld.%02ld, kW, 0x2101, 0x6101, , 1, 0\n"
                        +"0x7bb, 348, 367, 10000, 1, 0, 10000, Bat Ah %2ld.%04ld, Ah, 0x2101, 0x6101, , 1, 0\n"
                        +"0x7bb, 316, 335, 10000, 1, 0, 10000, SOC(real) %2ld.%04ld, %, 0x2101, 0x6101, Real State of Charge, 1, 0\n"
                        +"0x7bb, 16, 31, 1, 1, 0, 0, Raw t c01: %4ld, unknown, 0x2104, 0x6104, , 2, 0\n"
                        +"0x7bb, 32, 39, 1, 1, 40, 0, T 1: %4ld, C, 0x2104, 0x6104, Cell 1 Temperature, 2, 0\n"
                        +"0x7bb, 40, 55, 1, 1, 0, 0, Raw t c02: %4ld, unknown, 0x2104, 0x6104, , 2, 0\n"
                        +"0x7bb, 56, 63, 1, 1, 40, 0, T 2: %4ld, C, 0x2104, 0x6104, Cell 2 Temperature, 2, 0\n"
                        +"0x7bb, 64, 79, 1, 1, 0, 0, Raw t c03: %4ld, unknown, 0x2104, 0x6104, , 2, 0\n"
                        +"0x7bb, 80, 87, 1, 1, 40, 0, T 3: %4ld, C, 0x2104, 0x6104, Cell 3 Temperature, 2, 0\n"
                        +"0x7bb, 88, 103, 1, 1, 0, 0, Raw t c04: %4ld, unknown, 0x2104, 0x6104, , 2, 0\n"
                        +"0x7bb, 104, 111, 1, 1, 40, 0, T 4: %4ld, C, 0x2104, 0x6104, Cell 4 Temperature, 2, 0\n"
                        +"0x7bb, 112, 127, 1, 1, 0, 0, Raw t c05: %4ld, unknown, 0x2104, 0x6104, , 2, 0\n"
                        +"0x7bb, 128, 135, 1, 1, 40, 0, T 5: %4ld, C, 0x2104, 0x6104, Cell 5 Temperature, 2, 0\n"
                        +"0x7bb, 136, 151, 1, 1, 0, 0, Raw t c06: %4ld, unknown, 0x2104, 0x6104, , 2, 0\n"
                        +"0x7bb, 152, 159, 1, 1, 40, 0, T 6: %4ld, C, 0x2104, 0x6104, Cell 6 Temperature, 2, 0\n"
                        +"0x7bb, 160, 175, 1, 1, 0, 0, Raw t c07: %4ld, unknown, 0x2104, 0x6104, , 2, 0\n"
                        +"0x7bb, 176, 183, 1, 1, 40, 0, T 7: %4ld, C, 0x2104, 0x6104, Cell 7 Temperature, 2, 0\n"
                        +"0x7bb, 184, 199, 1, 1, 0, 0, Raw t c08: %4ld, unknown, 0x2104, 0x6104, , 2, 0\n"
                        +"0x7bb, 200, 207, 1, 1, 40, 0, T 8: %4ld, C, 0x2104, 0x6104, Cell 8 Temperature, 2, 0\n"
                        +"0x7bb, 208, 223, 1, 1, 0, 0, Raw t c09: %4ld, unknown, 0x2104, 0x6104, , 2, 0\n"
                        +"0x7bb, 224, 231, 1, 1, 40, 0, T 9: %4ld, C, 0x2104, 0x6104, Cell 9 Temperature, 2, 0\n"
                        +"0x7bb, 232, 247, 1, 1, 0, 0, Raw t c10: %4ld, unknown, 0x2104, 0x6104, , 2, 0\n"
                        +"0x7bb, 248, 255, 1, 1, 40, 0, T 10: %4ld, C, 0x2104, 0x6104, Cell 10 Temperature, 2, 0\n"
                        +"0x7bb, 256, 271, 1, 1, 0, 0, Raw t c11: %4ld, unknown, 0x2104, 0x6104, , 2, 0\n"
                        +"0x7bb, 272, 279, 1, 1, 40, 0, T 11: %4ld, C, 0x2104, 0x6104, Cell 11 Temperature, 2, 0\n"
                        +"0x7bb, 280, 295, 1, 1, 0, 0, Raw t c12: %4ld, unknown, 0x2104, 0x6104, , 2, 0\n"
                        +"0x7bb, 296, 303, 1, 1, 40, 0, T 12: %4ld, C, 0x2104, 0x6104, Cell 12 Temperature, 2, 0\n"
                        +"0x7bb, 16, 31, 1, 1, 0, 0, Raw t c01: %4ld, unknown, 0x2104, 0x6104, , 1, 0\n"
                        +"0x7bb, 32, 39, 1, 1, 0, 0, T 1: %4ld, C, 0x2104, 0x6104, Cell 1 Temperature, 1, 0\n"
                        +"0x7bb, 40, 55, 1, 1, 0, 0, Raw t c02: %4ld, unknown, 0x2104, 0x6104, , 1, 0\n"
                        +"0x7bb, 56, 63, 1, 1, 0, 0, T 2: %4ld, C, 0x2104, 0x6104, Cell 2 Temperature, 1, 0\n"
                        +"0x7bb, 64, 79, 1, 1, 0, 0, Raw t c03: %4ld, unknown, 0x2104, 0x6104, , 1, 0\n"
                        +"0x7bb, 80, 87, 1, 1, 0, 0, T 3: %4ld, C, 0x2104, 0x6104, Cell 3 Temperature, 1, 0\n"
                        +"0x7bb, 88, 103, 1, 1, 0, 0, Raw t c04: %4ld, unknown, 0x2104, 0x6104, , 1, 0\n"
                        +"0x7bb, 104, 111, 1, 1, 0, 0, T 4: %4ld, C, 0x2104, 0x6104, Cell 4 Temperature, 1, 0\n"
                        +"0x7bb, 16, 31, 1, 1, 0, 1000, Cell 01 %1ld.%03ld, V, 0x2141, 0x6141, , 0, 0\n"
                        +"0x7bb, 32, 47, 1, 1, 0, 1000, Cell 02 %1ld.%03ld, V, 0x2141, 0x6141, , 0, 0\n"
                        +"0x7bb, 48, 63, 1, 1, 0, 1000, Cell 03 %1ld.%03ld, V, 0x2141, 0x6141, , 0, 0\n"
                        +"0x7bb, 64, 79, 1, 1, 0, 1000, Cell 04 %1ld.%03ld, V, 0x2141, 0x6141, , 0, 0\n"
                        +"0x7bb, 80, 95, 1, 1, 0, 1000, Cell 05 %1ld.%03ld, V, 0x2141, 0x6141, , 0, 0\n"
                        +"0x7bb, 96, 111, 1, 1, 0, 1000, Cell 06 %1ld.%03ld, V, 0x2141, 0x6141, , 0, 0\n"
                        +"0x7bb, 112, 127, 1, 1, 0, 1000, Cell 07 %1ld.%03ld, V, 0x2141, 0x6141, , 0, 0\n"
                        +"0x7bb, 128, 143, 1, 1, 0, 1000, Cell 08 %1ld.%03ld, V, 0x2141, 0x6141, , 0, 0\n"
                        +"0x7bb, 144, 159, 1, 1, 0, 1000, Cell 09 %1ld.%03ld, V, 0x2141, 0x6141, , 0, 0\n"
                        +"0x7bb, 160, 175, 1, 1, 0, 1000, Cell 10 %1ld.%03ld, V, 0x2141, 0x6141, , 0, 0\n"
                        +"0x7bb, 176, 191, 1, 1, 0, 1000, Cell 11 %1ld.%03ld, V, 0x2141, 0x6141, , 0, 0\n"
                        +"0x7bb, 192, 207, 1, 1, 0, 1000, Cell 12 %1ld.%03ld, V, 0x2141, 0x6141, , 0, 0\n"
                        +"0x7bb, 208, 223, 1, 1, 0, 1000, Cell 13 %1ld.%03ld, V, 0x2141, 0x6141, , 0, 0\n"
                        +"0x7bb, 224, 239, 1, 1, 0, 1000, Cell 14 %1ld.%03ld, V, 0x2141, 0x6141, , 0, 0\n"
                        +"0x7bb, 240, 255, 1, 1, 0, 1000, Cell 15 %1ld.%03ld, V, 0x2141, 0x6141, , 0, 0\n"
                        +"0x7bb, 256, 271, 1, 1, 0, 1000, Cell 16 %1ld.%03ld, V, 0x2141, 0x6141, , 0, 0\n"
                        +"0x7bb, 272, 287, 1, 1, 0, 1000, Cell 17 %1ld.%03ld, V, 0x2141, 0x6141, , 0, 0\n"
                        +"0x7bb, 288, 303, 1, 1, 0, 1000, Cell 18 %1ld.%03ld, V, 0x2141, 0x6141, , 0, 0\n"
                        +"0x7bb, 304, 319, 1, 1, 0, 1000, Cell 19 %1ld.%03ld, V, 0x2141, 0x6141, , 0, 0\n"
                        +"0x7bb, 320, 335, 1, 1, 0, 1000, Cell 20 %1ld.%03ld, V, 0x2141, 0x6141, , 0, 0\n"
                        +"0x7bb, 336, 351, 1, 1, 0, 1000, Cell 21 %1ld.%03ld, V, 0x2141, 0x6141, , 0, 0\n"
                        +"0x7bb, 352, 367, 1, 1, 0, 1000, Cell 22 %1ld.%03ld, V, 0x2141, 0x6141, , 0, 0\n"
                        +"0x7bb, 368, 383, 1, 1, 0, 1000, Cell 23 %1ld.%03ld, V, 0x2141, 0x6141, , 0, 0\n"
                        +"0x7bb, 384, 399, 1, 1, 0, 1000, Cell 24 %1ld.%03ld, V, 0x2141, 0x6141, , 0, 0\n"
                        +"0x7bb, 400, 415, 1, 1, 0, 1000, Cell 25 %1ld.%03ld, V, 0x2141, 0x6141, , 0, 0\n"
                        +"0x7bb, 416, 431, 1, 1, 0, 1000, Cell 26 %1ld.%03ld, V, 0x2141, 0x6141, , 0, 0\n"
                        +"0x7bb, 432, 447, 1, 1, 0, 1000, Cell 27 %1ld.%03ld, V, 0x2141, 0x6141, , 0, 0\n"
                        +"0x7bb, 448, 463, 1, 1, 0, 1000, Cell 28 %1ld.%03ld, V, 0x2141, 0x6141, , 0, 0\n"
                        +"0x7bb, 464, 479, 1, 1, 0, 1000, Cell 29 %1ld.%03ld, V, 0x2141, 0x6141, , 0, 0\n"
                        +"0x7bb, 480, 495, 1, 1, 0, 1000, Cell 30 %1ld.%03ld, V, 0x2141, 0x6141, , 0, 0\n"
                        +"0x7bb, 496, 511, 1, 1, 0, 1000, Cell 31 %1ld.%03ld, V, 0x2141, 0x6141, , 0, 0\n"
                        +"0x7bb, 512, 527, 1, 1, 0, 1000, Cell 32 %1ld.%03ld, V, 0x2141, 0x6141, , 0, 0\n"
                        +"0x7bb, 528, 543, 1, 1, 0, 1000, Cell 33 %1ld.%03ld, V, 0x2141, 0x6141, , 0, 0\n"
                        +"0x7bb, 544, 559, 1, 1, 0, 1000, Cell 34 %1ld.%03ld, V, 0x2141, 0x6141, , 0, 0\n"
                        +"0x7bb, 560, 575, 1, 1, 0, 1000, Cell 35 %1ld.%03ld, V, 0x2141, 0x6141, , 0, 0\n"
                        +"0x7bb, 576, 591, 1, 1, 0, 1000, Cell 36 %1ld.%03ld, V, 0x2141, 0x6141, , 0, 0\n"
                        +"0x7bb, 592, 607, 1, 1, 0, 1000, Cell 37 %1ld.%03ld, V, 0x2141, 0x6141, , 0, 0\n"
                        +"0x7bb, 608, 623, 1, 1, 0, 1000, Cell 38 %1ld.%03ld, V, 0x2141, 0x6141, , 0, 0\n"
                        +"0x7bb, 624, 639, 1, 1, 0, 1000, Cell 39 %1ld.%03ld, V, 0x2141, 0x6141, , 0, 0\n"
                        +"0x7bb, 640, 655, 1, 1, 0, 1000, Cell 40 %1ld.%03ld, V, 0x2141, 0x6141, , 0, 0\n"
                        +"0x7bb, 656, 671, 1, 1, 0, 1000, Cell 41 %1ld.%03ld, V, 0x2141, 0x6141, , 0, 0\n"
                        +"0x7bb, 672, 687, 1, 1, 0, 1000, Cell 42 %1ld.%03ld, V, 0x2141, 0x6141, , 0, 0\n"
                        +"0x7bb, 688, 703, 1, 1, 0, 1000, Cell 43 %1ld.%03ld, V, 0x2141, 0x6141, , 0, 0\n"
                        +"0x7bb, 704, 719, 1, 1, 0, 1000, Cell 44 %1ld.%03ld, V, 0x2141, 0x6141, , 0, 0\n"
                        +"0x7bb, 720, 735, 1, 1, 0, 1000, Cell 45 %1ld.%03ld, V, 0x2141, 0x6141, , 0, 0\n"
                        +"0x7bb, 736, 751, 1, 1, 0, 1000, Cell 46 %1ld.%03ld, V, 0x2141, 0x6141, , 0, 0\n"
                        +"0x7bb, 752, 767, 1, 1, 0, 1000, Cell 47 %1ld.%03ld, V, 0x2141, 0x6141, , 0, 0\n"
                        +"0x7bb, 768, 783, 1, 1, 0, 1000, Cell 48 %1ld.%03ld, V, 0x2141, 0x6141, , 0, 0\n"
                        +"0x7bb, 784, 799, 1, 1, 0, 1000, Cell 49 %1ld.%03ld, V, 0x2141, 0x6141, , 0, 0\n"
                        +"0x7bb, 800, 815, 1, 1, 0, 1000, Cell 50 %1ld.%03ld, V, 0x2141, 0x6141, , 0, 0\n"
                        +"0x7bb, 816, 831, 1, 1, 0, 1000, Cell 51 %1ld.%03ld, V, 0x2141, 0x6141, , 0, 0\n"
                        +"0x7bb, 832, 847, 1, 1, 0, 1000, Cell 52 %1ld.%03ld, V, 0x2141, 0x6141, , 0, 0\n"
                        +"0x7bb, 848, 863, 1, 1, 0, 1000, Cell 53 %1ld.%03ld, V, 0x2141, 0x6141, , 0, 0\n"
                        +"0x7bb, 864, 879, 1, 1, 0, 1000, Cell 54 %1ld.%03ld, V, 0x2141, 0x6141, , 0, 0\n"
                        +"0x7bb, 880, 895, 1, 1, 0, 1000, Cell 55 %1ld.%03ld, V, 0x2141, 0x6141, , 0, 0\n"
                        +"0x7bb, 896, 911, 1, 1, 0, 1000, Cell 56 %1ld.%03ld, V, 0x2141, 0x6141, , 0, 0\n"
                        +"0x7bb, 912, 927, 1, 1, 0, 1000, Cell 57 %1ld.%03ld, V, 0x2141, 0x6141, , 0, 0\n"
                        +"0x7bb, 928, 943, 1, 1, 0, 1000, Cell 58 %1ld.%03ld, V, 0x2141, 0x6141, , 0, 0\n"
                        +"0x7bb, 944, 959, 1, 1, 0, 1000, Cell 59 %1ld.%03ld, V, 0x2141, 0x6141, , 0, 0\n"
                        +"0x7bb, 960, 975, 1, 1, 0, 1000, Cell 60 %1ld.%03ld, V, 0x2141, 0x6141, , 0, 0\n"
                        +"0x7bb, 976, 991, 1, 1, 0, 1000, Cell 61 %1ld.%03ld, V, 0x2141, 0x6141, , 0, 0\n"
                        +"0x7bb, 992, 1007, 1, 1, 0, 1000, Cell 62 %1ld.%03ld, V, 0x2141, 0x6141, , 0, 0\n"
                        +"0x7bb, 16, 31, 1, 1, 0, 1000, Cell 63 %1ld.%03ld, V, 0x2142, 0x6142, , 0, 0\n"
                        +"0x7bb, 32, 47, 1, 1, 0, 1000, Cell 64 %1ld.%03ld, V, 0x2142, 0x6142, , 0, 0\n"
                        +"0x7bb, 48, 63, 1, 1, 0, 1000, Cell 65 %1ld.%03ld, V, 0x2142, 0x6142, , 0, 0\n"
                        +"0x7bb, 64, 79, 1, 1, 0, 1000, Cell 66 %1ld.%03ld, V, 0x2142, 0x6142, , 0, 0\n"
                        +"0x7bb, 80, 95, 1, 1, 0, 1000, Cell 67 %1ld.%03ld, V, 0x2142, 0x6142, , 0, 0\n"
                        +"0x7bb, 96, 111, 1, 1, 0, 1000, Cell 68 %1ld.%03ld, V, 0x2142, 0x6142, , 0, 0\n"
                        +"0x7bb, 112, 127, 1, 1, 0, 1000, Cell 69 %1ld.%03ld, V, 0x2142, 0x6142, , 0, 0\n"
                        +"0x7bb, 128, 143, 1, 1, 0, 1000, Cell 70 %1ld.%03ld, V, 0x2142, 0x6142, , 0, 0\n"
                        +"0x7bb, 144, 159, 1, 1, 0, 1000, Cell 71 %1ld.%03ld, V, 0x2142, 0x6142, , 0, 0\n"
                        +"0x7bb, 160, 175, 1, 1, 0, 1000, Cell 72 %1ld.%03ld, V, 0x2142, 0x6142, , 0, 0\n"
                        +"0x7bb, 176, 191, 1, 1, 0, 1000, Cell 73 %1ld.%03ld, V, 0x2142, 0x6142, , 0, 0\n"
                        +"0x7bb, 192, 207, 1, 1, 0, 1000, Cell 74 %1ld.%03ld, V, 0x2142, 0x6142, , 0, 0\n"
                        +"0x7bb, 208, 223, 1, 1, 0, 1000, Cell 75 %1ld.%03ld, V, 0x2142, 0x6142, , 0, 0\n"
                        +"0x7bb, 224, 239, 1, 1, 0, 1000, Cell 76 %1ld.%03ld, V, 0x2142, 0x6142, , 0, 0\n"
                        +"0x7bb, 240, 255, 1, 1, 0, 1000, Cell 77 %1ld.%03ld, V, 0x2142, 0x6142, , 0, 0\n"
                        +"0x7bb, 256, 271, 1, 1, 0, 1000, Cell 78 %1ld.%03ld, V, 0x2142, 0x6142, , 0, 0\n"
                        +"0x7bb, 272, 287, 1, 1, 0, 1000, Cell 79 %1ld.%03ld, V, 0x2142, 0x6142, , 0, 0\n"
                        +"0x7bb, 288, 303, 1, 1, 0, 1000, Cell 80 %1ld.%03ld, V, 0x2142, 0x6142, , 0, 0\n"
                        +"0x7bb, 304, 319, 1, 1, 0, 1000, Cell 81 %1ld.%03ld, V, 0x2142, 0x6142, , 0, 0\n"
                        +"0x7bb, 320, 335, 1, 1, 0, 1000, Cell 82 %1ld.%03ld, V, 0x2142, 0x6142, , 0, 0\n"
                        +"0x7bb, 336, 351, 1, 1, 0, 1000, Cell 83 %1ld.%03ld, V, 0x2142, 0x6142, , 0, 0\n"
                        +"0x7bb, 352, 367, 1, 1, 0, 1000, Cell 84 %1ld.%03ld, V, 0x2142, 0x6142, , 0, 0\n"
                        +"0x7bb, 368, 383, 1, 1, 0, 1000, Cell 85 %1ld.%03ld, V, 0x2142, 0x6142, , 0, 0\n"
                        +"0x7bb, 384, 399, 1, 1, 0, 1000, Cell 86 %1ld.%03ld, V, 0x2142, 0x6142, , 0, 0\n"
                        +"0x7bb, 400, 415, 1, 1, 0, 1000, Cell 87 %1ld.%03ld, V, 0x2142, 0x6142, , 0, 0\n"
                        +"0x7bb, 416, 431, 1, 1, 0, 1000, Cell 88 %1ld.%03ld, V, 0x2142, 0x6142, , 0, 0\n"
                        +"0x7bb, 432, 447, 1, 1, 0, 1000, Cell 89 %1ld.%03ld, V, 0x2142, 0x6142, , 0, 0\n"
                        +"0x7bb, 448, 463, 1, 1, 0, 1000, Cell 90 %1ld.%03ld, V, 0x2142, 0x6142, , 0, 0\n"
                        +"0x7bb, 464, 479, 1, 1, 0, 1000, Cell 91 %1ld.%03ld, V, 0x2142, 0x6142, , 0, 0\n"
                        +"0x7bb, 480, 495, 1, 1, 0, 1000, Cell 92 %1ld.%03ld, V, 0x2142, 0x6142, , 0, 0\n"
                        +"0x7bb, 496, 511, 1, 1, 0, 1000, Cell 93 %1ld.%03ld, V, 0x2142, 0x6142, , 0, 0\n"
                        +"0x7bb, 512, 527, 1, 1, 0, 1000, Cell 94 %1ld.%03ld, V, 0x2142, 0x6142, , 0, 0\n"
                        +"0x7bb, 528, 543, 1, 1, 0, 1000, Cell 95 %1ld.%03ld, V, 0x2142, 0x6142, , 0, 0\n"
                        +"0x7bb, 544, 559, 1, 1, 0, 1000, Cell 96 %1ld.%03ld, V, 0x2142, 0x6142, , 0, 0\n"
                        +"0x7bb, 60, 79, 10000, 1, 0, 10000, Bat Ah %2ld.%04ld, Ah, 0x2161, 0x6161, , 1, 0\n"
                        +"0x7bb, 80, 87, 2, 1, 0, 10, Bat SOH%3ld.%01ld, %, 0x2161, 0x6161, , 1, 0\n"
                        +"0x7bb, 104, 119, 1, 1, 0, 0, Bat km %5ld, km?, 0x2161, 0x6161, , 1, 0\n"
                        +"0x7bb, 136, 151, 1, 1, 0, 0, Bat kWh %5ld, kWh, 0x2161, 0x6161, , 1, 0\n"
                        +"0x7ec, 32, 39, 1, 30, 0, 100, Mx ch pwr, kW, 0x223444, 0x623444, , 2, 0 \n"
                        +"0x7ec, 24, 39, 1, 2, 0, 100, SOC, %, 0x222002, 0x622002, , 2, 0\n"
                        +"0x7ec, 24, 39, 48, 100, 0, 100, SOC, %, 0x222002, 0x622002, , 1, 0\n"
                        +"0x7ec, 24, 39, 1, 1, 0, 100, Speed, km/h, 0x222003, 0x622003, , 1, 0\n"
                        +"0x7ec, 24, 39, 1, 50, 0, 100, Motor Volt, V, 0x222004, 0x622004, , 0, 0\n"
                        +"0x7ec, 24, 39, 1, 1, 0, 100, 12V Volt, V, 0x222005, 0x622005, , 0, 0\n"
                        +"0x7ec, 24, 47, 1, 1, 0, 0, Odometer, km, 0x222006, 0x622006, , 0, 0\n"
                        +"0x7ec, 24, 39, 1, 1, 0, 0, Pedal, , 0x22202e, 0x62202e, , 0, 0\n"
                        +"0x7ec, 24, 31, 1, 1, 0, 0, Steering wheel CC/SL buttons, , 0x22204b, 0x62204b, , 1, 0\n"
                        +"0x7ec, 24, 39, 1, 50, 0, 100, Volt, V, 0x223203, 0x623203, , 0, 0\n"
                        +"0x7ec, 24, 39, 1, 25, 0x8000, 100, Amp, A, 0x223204, 0x623204, , 0, 0\n"
                        +"0x7ec, 24, 31, 1, 1, 0, 0, Bat Health, %, 0x223206, 0x623206, , 0, 0\n"
                        +"0x7ec, 24, 31, 1, 1, 40, 0, Ext temp, C, 0x2233b1, 0x6233b1, , 0, 0\n"
*/
                        // 26/09/15
                        +"0x0c6, 0, 15, 1, 1, 0x8000, 10, Steering pos:%5ld, °, , , Steering Position, 0, 0, 10\n"
                        +"0x0c6, 16, 31, 1, 1, 0x8000, 10, Steering ac: %5ld, °/s, , , Steering Acceleration, 0, 0, 10\n"
                        +"0x0c6, 32, 47, 1, 1, 0x8000, 10, Steering offset %5ld, °, , , SteeringWheelAngle_Offset, 0, 0, 10\n"
                        +"0x0c6, 48, 50, 1, 1, 0, 0, SwaStatus %1ld, , , , SwaSensorInternalStatus, 0, 0, 10\n"
                        +"0x0c6, 51, 54, 1, 1, 0, 0, SwaClock %2ld, , , , SwaClock, 0, 0, 10\n"
                        +"0x0c6, 56, 53, 1, 1, 0, 0, , , , , SwaChecksum, 0, 0, 10\n"
                        +"0x12e, 0, 7, 1, 1, 198, 0, Accel F/R: %4ld, , , , LongitudinalAccelerationProc, 0, 0, 10\n"
                        +"0x12e, 24, 35, 1, 1, 2047, 10, Yaw rate: %4ld, deg/s, , , Yaw rate, 0, 0, 10\n"
                        +"0x12e, 8, 23, 1, 1, 0x8000, 0, Accel L/R: %4ld, , , , TransversalAcceleration, 0, 0, 10\n"
                        +"0x130, 11, 12, 1, 1, 0, 0, HBB_Malfunction %1ld, , , , HBB_Malfunction, 0, 0, 10\n"
                        +"0x130, 16, 17, 1, 1, 0, 0, EB_Malfunction %ld, , , , EB_Malfunction, 0, 0, 10\n"
                        +"0x130, 18, 19, 1, 1, 0, 0, EB_inProgress %1ld, , , , EB_inProgress, 0, 0, 10\n"
                        +"0x130, 20, 31, 1, -1, 4049, 0, ElBrkWhTqReq %4d , Nm, , , ElecBrakeWheelsTorqueRequest, 0, 0, 10\n"
                        +"0x130, 32, 38, 1, 1, 0, 0, BrakePedalDriverWill %3ld, %, , , BrakePedalDriverWill, 0, 0, 10\n"
                        +"0x130, 40, 41, 1, 1, 0, 0, HBA_ActivationRequest %1ld, , , , HBA_ActivationRequest, 0, 0, 10\n"
                        +"0x130, 42, 43, 1, 1, 0, 0, PressureBuildUp %1ld, , , , PressureBuildUp, 0, 0, 10\n"
                        +"0x130, 44, 55, 1, -1, 4049, 0, DriverBrakeWheelTq_Req %4ld, Nm, , , DriverBrakeWheelTq_Req , 0, 0, 10\n"
                        +"0x130, 56, 63, 1, 1, 0, 0, CheckSum_UBP %3ld, , , , CheckSum_UBP, 0, 0, 10\n"
                        +"0x130, 8, 10, 1, 1, 0, 0, UBP_Clock %1ld, , , , UBP_Clock, 0, 0, 10\n"
                        +"0x17e, 40, 41, 1, 1, 0, 0, , , , , CrankingAuthorisation_AT, 0, 0, 10\n"
                        +"0x17e, 48, 51, 1, 1, 0, 0, Gear: %4ld, , , , GearLeverPosition, 0, 0, 10\n"
                        +"0x186, 0, 15, 10, 125, 0, 10, Engine RPM: %3ld.%02ld, Tr/min, , , Speed, 0, 0, 10\n"
                        +"0x186, 16, 27, 2, 1, 800, 0, MeanEffectiveTorque: %4ld, Nm, , , MeanEffectiveTorque, 0, 0, 10\n"
                        +"0x186, 28, 39, 1, 1, 800, 0, Accel(b): %4ld, , , , RequestedTorqueAfterProc, 0, 0, 10\n"
                        +"0x186, 40, 49, 10, 1, 0, 0, Pedal: %4ld, %, , , Throttle, 0, 0, 10\n"
                        +"0x18a, 0, 11, 1, 1, 0, 0, RawEngineTorque: %4ld, %, , , RawEngineTorque_WithoutTMReq, 0, 0, 10\n"
                        +"0x18a, 12, 12, 1, 1, 0, 0, torq ack %1ld, , , , AT_TorqueAcknowledgement, 0, 0, 10\n"
                        +"0x18a, 13, 14, 1, 1, 0, 0, cc status %1ld, , , , CruiseControlStatus_forTM, 0, 0, 10\n"
                        +"0x18a, 16, 25, 800, 100, 0, 0, PowerTrainSetPoint: %4ld, %, , , Throttle, 0, 0, 10\n"
                        +"0x18a, 26, 26, 1, 1, 0, 0, Kickdown: %1ld, , , , KickDownActivated, 0, 0, 10\n"
                        +"0x18a, 27, 38, 1, 5, 800, 10, FrictionTorque: %4ld, Nm, , , , 0, 0, 10\n"
                        +"0x1f6, 20, 20, 1, 5, 0, 10, Break pedal: %4ld, , , , Break Pedal, 0, 0, 10\n"
                        +"0x1fd, 0, 7, 1, 5, 0, 10, Amp 12V: %2ld.%01ld, A, , , 12V Battery Current, 0, 0, 100\n"
                        +"0x1fd, 48, 55, 1, 1, 0x50, 0, KwDash: %4ld, kW, , , Consumption, 0, 0, 100\n"
                        +"0x218, 0, 15, 1, 1, 0, 0, , , , , , 0, 0, 20\n"
                        +"0x29a, 0, 15, 1, 1, 0, 0, Speed FR: %5ld, , , , Speed Front Right, 0, 0, 20\n"
                        +"0x29a, 16, 31, 1, 1, 0, 0, Speed FL: %5ld, , , , Speed Front Left, 0, 0, 20\n"
                        +"0x29a, 32, 47, 1, 1, 0, 100, , , , , , 0, 0, 20\n"
                        +"0x29c, 0, 15, 1, 1, 0, 0, Speed RR: %5ld, , , , Speed Rear Right, 0, 0, 20\n"
                        +"0x29c, 16, 31, 1, 1, 0, 0, Speed RL: %5ld, , , , Speed Rear Left, 0, 0, 20\n"
                        +"0x29c, 48, 63, 1, 1, 0, 100, Speed R100: %3ld.%02ld, , , , , 0, 0, 20\n"
                        +"0x352, 0, 1, 1, 1, 0, 0, ABS warn req %1ld, , , , ABS Warning Request, 0, 0, 40\n"
                        +"0x352, 2, 3, 1, 1, 0, 0, StopLReq %1ld, , , , ESP_StopLampRequest, 0, 0, 40\n"
                        +"0x352, 24, 31, 2, 1, 0, 0, Brake pressure %3ld, bar, , , Brake pressure, 0, 0, 40\n"
                        +"0x35c, 16, 39, 1, 1, 0, 0, Minutes: %7ld, min, , , , 0, 0, 100\n"
                        +"0x35c, 4, 15, 1, 1, 0, 0, Key-Start: %5ld, , , , , 0, 0, 100\n"
                        +"0x3f7, 2, 3, 1, 1, 0, 0, Gear??: %4ld, , , , , 2, 0, 60\n"
                        +"0x427, 40, 47, 10, 3, 0, 0, AvCharPwr: %5ld, kW, , , , 1, 0, 100\n"
                        +"0x427, 49, 57, 1, 1, 0, 10, AvEnergy: %2ld.%1ld, kWh, , , , 1, 0, 100\n"
                        +"0x42a, 16, 23, 1, 1, 0, 0, Temp set: %5ld, , , , , 1, 0, 100\n"
                        +"0x42e, 0, 12, 1, 2, 0, 100, SOC(a): %3ld.%02ld, %, , , State of Charge, 2, 0, 100\n"
                        +"0x42e, 0, 12, 475, 1000, 0, 100, SOC(a): %3ld.%02ld, %, , , State of Charge, 1, 0, 100\n"
                        +"0x42e, 18, 19, 1, 1, 0, 0, , , , , HVBatLevel2Failure, 0, 0, 100\n"
                        +"0x42e, 20, 24, 1, 5, 0, 0, EngineFanSpeed %3ld, %, , , EngineFanSpeed, 0, 0, 100\n"
                        +"0x42e, 25, 34, 2, 1, 0, 0, DC V: %3ld.%02ld, V, , , HVNetworkVoltage, 0, 0, 100\n"
                        +"0x42e, 38, 43, 1, 1, 0, 1, AC pilot current: %3ld, A, , , Charging Pilot Current, 0, 0, 100\n"
                        +"0x42e, 44, 50, 1, 1, 40, 0, HVBatteryTemp, C, , , HVBatteryTemp, 0, 0, 100\n"
                        +"0x42e, 56, 63, 10, 3, 0, 1, ChargingPower, kW, , , ChargingPower, 0, 0, 100\n"
                        +"0x439, 0, 15, 1, 1, 0, 0, Accel? %5ld, ?, , , , 0, 0, 1000\n"
                        +"0x4f8, 0, 1, 1, -1, -2, 0, Start: %4ld, , , , , 0, 0, 100\n"
                        +"0x4f8, 24, 39, 1, 1, 0, 100, Speed(d): %3ld.%02ld, , , , Speed, 2, 0, 100\n"
                        +"0x4f8, 4, 5, 1, -1, -2, 0, Park.break: %4ld, , , , Parking Break, 0, 0, 100\n"
                        +"0x534, 32, 40, 1, 1, 40, 0, Temp out: %4ld, C, , , , 0, 0, 100\n"
                        +"0x5d7, 0, 15, 1, 1, 0, 100, Speed(a): %3ld.%02ld, km/h, , , Speed, 0, 0, 100\n"
                        +"0x5d7, 16, 43, 1, 1, 0, 100, Odo: %5ld.%02ld, km, , , Odometer, 0, 0, 100\n"
                        +"0x5d7, 44, 45, 1, 1, 0, 0, WheelsLockingState %1ld, ?, , , WheelsLockingState, 0, 0, 100\n"
                        +"0x5d7, 48, 49, 1, 1, 0, 0, VehicleSpeedSign %1ld, ?, , , VehicleSpeedSign, 0, 0, 100\n"
                        +"0x5d7, 50, 54, 1, 4, 0, 100, Fine dist: %5ld.%02ld, cm, , , , 0, 0, 100\n"
                        +"0x5de, 1, 1, 1, 1, 0, 0, Right: %5ld, , , , Right Indicator, 0, 0, 100\n"
                        +"0x5de, 12, 12, 1, 1, 0, 0, FL door open: %1ld, , , , Door Front Left, 0, 0, 100\n"
                        +"0x5de, 14, 14, 1, 1, 0, 0, FR door open: %1ld, , , , Dort Front Right, 0, 0, 100\n"
                        +"0x5de, 17, 17, 1, 1, 0, 0, RL door open: %1ld, , , , Door Rear Left, 0, 0, 100\n"
                        +"0x5de, 19, 19, 1, 1, 0, 0, RR door open: %1ld, , , , Door Rear Right, 0, 0, 100\n"
                        +"0x5de, 2, 2, 1, 1, 0, 0, Left: %5ld, , , , Left Indicator, 0, 0, 100\n"
                        +"0x5de, 5, 5, 1, 1, 0, 0, Park light: %5ld, , , , Park Light, 0, 0, 100\n"
                        +"0x5de, 59, 59, 1, 1, 0, 0, Hatch door open: %1ld, , , , Door Hatch, 0, 0, 100\n"
                        +"0x5de, 6, 6, 1, 1, 0, 0, Head light: %5ld, , , , Head Light, 0, 0, 100\n"
                        +"0x5de, 7, 7, 1, 1, 0, 0, Beam light: %5ld, , , , Beam Light, 0, 0, 100\n"
                        +"0x5ee, 0, 0, 1, 1, 0, 0, Park light: %5ld, , , , Park Light, 0, 0, 100\n"
                        +"0x5ee, 1, 1, 1, 1, 0, 0, Head light: %5ld, , , , Head Light, 0, 0, 100\n"
                        +"0x5ee, 16, 19, 1, 1, 0, 0, Door locks %4ld, , , , Door Locks, 0, 0, 100\n"
                        +"0x5ee, 2, 2, 1, 1, 0, 0, Beam light: %5ld, , , , Beam Light, 0, 0, 100\n"
                        +"0x5ee, 20, 24, 1, 1, 0, 0, Flashers %4ld, , , , Indicators, 0, 0, 100\n"
                        +"0x5ee, 24, 27, 1, 1, 0, 0, Doors %4ld, , , , Doors, 0, 0, 100\n"
                        +"0x646, 16, 32, 1, 1, 0, 10, trB dist %5ld.%01ld, km, , , , 0, 0, 500\n"
                        +"0x646, 33, 47, 1, 1, 0, 10, trB cons %5ld.%01ld, kWh, , , , 0, 0, 500\n"
                        +"0x646, 48, 59, 1, 1, 0, 10, avg trB spd %5ld.%01ld, km/h, , , , 0, 0, 500\n"
                        +"0x646, 8, 15, 1, 1, 0, 10, avg trB cons %2ld.%01ld, kWh/100km, , , , 0, 0, 500\n"
                        +"0x653, 9, 9, 1, 1, 0, 0, dr seatbelt %4ld, , , , , 0, 0, 100\n"
                        +"0x654, 24, 31, 1, 1, 0, 0, SOC(b): %4ld, , , , State of Charge, 0, 0, 500\n"
                        +"0x654, 2, 2, 1, 1, 0, 0, Plugin state, , , , Plugin State, 0, 0, 500\n"
                        +"0x654, 32, 41, 1, 1, 0, 0, Time to full %4ld, min, , , Time to Full, 0, 0, 500\n"
                        +"0x654, 42, 51, 1, 1, 0, 0, Km avail: %4ld, km, , , Available Distance, 0, 0, 500\n"
                        +"0x654, 52, 61, 1, 1, 0, 10, kw/100km %2ld.%01ld, , , , , 0, 0, 500\n"
                        +"0x658, 0, 31, 1, 1, 0, 0, S# batt:%10ld, , , , Battery Serial N°, 0, 0, 3000\n"
                        +"0x658, 32, 39, 1, 1, 0, 0, Bat health %4ld, %, , , Battery Health, 0, 0, 3000\n"
                        +"0x65b, 25, 26, 1, 1, 0, 0, ECO Mode: %1ld, , , , Economy Mode, 0, 0, 100\n"
                        +"0x65b, 41, 43, 1, 1, 0, 0, Charging Stat: %1ld, , , , Charging Stat, 0, 0, 100\n"
                        +"0x66a, 42, 42, 1, 1, 0, 0, CruisC ? %4ld, , , , , 0, 0, 100\n"
                        +"0x66a, 5, 7, 1, 1, 0, 0, CruisC mode %4ld, , , , Cruise Control Mode, 0, 0, 100\n"
                        +"0x66a, 8, 15, 1, 1, 0, 0, CruisC spd %4ld, km/h, , , Cruise Control Speed, 0, 0, 100\n"
                        +"0x68b, 0, 3, 1, 1, 0, 0, # presses MM %2ld, , , , , 0, 0, 100\n"
                        +"0x699, 10, 14, 2, 1, 0, 0, Clima temp %4ld, C, , , , 2, 0, 1000\n"
                        +"0x699, 16, 16, 1, 1, 0, 0, Clima wshld %4ld, , , , , 2, 0, 1000\n"
                        +"0x699, 18, 18, 1, 1, 0, 0, Clima face %4ld, , , , , 2, 0, 1000\n"
                        +"0x699, 19, 19, 1, 1, 0, 0, Clima feet %4ld, , , , , 2, 0, 1000\n"
                        +"0x699, 2, 3, 1, 1, 0, 0, Clima rear dfr %4ld, , , , , 2, 0, 1000\n"
                        +"0x699, 20, 21, 1, 1, 0, 0, Clima recycling %4ld, , , , , 2, 0, 1000\n"
                        +"0x699, 22, 23, 1, 1, 0, 0, ECO mode %4ld, , , , , 2, 0, 1000\n"
                        +"0x699, 24, 27, 1, 1, 0, 0, Clima fan %4ld, , , , , 2, 0, 1000\n"
                        +"0x699, 28, 31, 1, 1, 0, 0, Clima chg %4ld, , , , , 2, 0, 1000\n"
                        +"0x699, 4, 4, 1, -1, -1, 0, Clima auto %4ld, , , , , 2, 0, 1000\n"
                        +"0x699, 5, 5, 1, 1, 0, 0, Clima Maxdfr %4ld, , , , , 2, 0, 1000\n"
                        +"0x699, 52, 53, 1, 1, 0, 0, Clima AUTO %4ld, , , , , 2, 0, 1000\n"
                        +"0x699, 54, 55, 1, 1, 0, 0, Clima AC %4ld, , , , , 2, 0, 1000\n"
                        +"0x699, 56, 56, 1, 1, 0, 0, Clima fan %4ld, , , , , 2, 0, 1000\n"
                        +"0x699, 6, 6, 1, 1, 0, 0, Clima autofan %4ld, , , , , 2, 0, 1000\n"
                        +"0x69f, 0, 31, 1, 1, 0, 0, S# car: %10ld, , , , Car Serial N°, 0, 0, 1000\n"
                        +"0x6f8, 16, 23, 16, 100, 0, 100, Bat12: %2ld.%02ld, V, , , 12V Battery Voltage, 0, 0, 100\n"
                        +"0x760, 24, 31, 1, 1, 0, 0, Mas cyl pr %3ld, bar, 0x224b0e, 0x624b0e, , 2, 0, 0\n"
                        +"0x762, 24, 39, 256, 100, 100, 0, Bat12: %2ld.%02ld, V, 0x22012f, 0x62012f, 12V Battery Voltage, 0, 0, 0\n"
                        +"0x763, 2, 2, 1, 1, 0, 0, mute %1ld, , 0x2220f0, 0x6220f0, , 0, 0, 0\n"
                        +"0x763, 24, 31, 1, 1, 0, 0, Parking brake %1ld, , 0x222001, 0x622001, Parking Break, 0, 0, 0\n"
                        +"0x763, 3, 3, 1, 1, 0, 0, vol+ %1ld, , 0x2220f0, 0x6220f0, << UDS replies start at bit 24 - need to recheck, 0, 0, 0\n"
                        +"0x763, 4, 4, 1, 1, 0, 0, vol- %1ld, , 0x2220f0, 0x6220f0, , 0, 0, 0\n"
                        +"0x763, 5, 5, 1, 1, 0, 0, media %1ld, , 0x2220f0, 0x6220f0, , 0, 0, 0\n"
                        +"0x763, 6, 6, 1, 1, 0, 0, radio %1ld, , 0x2220f0, 0x6220f0, , 0, 0, 0\n"
                        +"0x77e, 24, 31, 1, 1, 0, 0, dcdc state %2ld, , 0x22300f, 0x62300f, , 0, 0, 0\n"
                        +"0x77e, 24, 31, 16, 100, 0, 100, Current %3ld.%02ld, A, 0x22301d, 0x62301d, , 0, 0, 0\n"
                        +"0x77e, 24, 31, 32, 1, 0, 0, Trq app. %5ld, Nm, 0x223025, 0x623025, , 0, 0, 0\n"
                        +"0x77e, 24, 31, 32, 1, 0, 0, Trq req. %5ld, Nm, 0x223024, 0x623024, , 0, 0, 0\n"
                        +"0x77e, 24, 31, 32, 1000, 0, 1000, Batt volt %3ld.%03ld, V, 0x22300e, 0x62300e, Battery Voltage, 0, 0, 0\n"
                        +"0x77e, 24, 31, 64, 100, 0, 100, Inv temp %2ld.%02ld, C, 0x22302b, 0x62302b, , 0, 0, 0\n"
                        +"0x7bb, 104, 111, 1, 1, 0, 0, T 4: %4ld, C, 0x2104, 0x6104, Cell 4 Temperature, 1, 10, 0\n"
                        +"0x7bb, 104, 111, 1, 1, 40, 0, T 4: %4ld, C, 0x2104, 0x6104, Cell 4 Temperature, 2, 10, 0\n"
                        +"0x7bb, 104, 119, 1, 1, 0, 0, Bat km %5ld, km?, 0x2161, 0x6161, , 1, 0, 0\n"
                        +"0x7bb, 112, 127, 1, 1, 0, 0, Raw t c05: %4ld, unknown, 0x2104, 0x6104, , 2, 10, 0\n"
                        +"0x7bb, 112, 127, 1, 1, 0, 1000, Cell 07 %1ld.%03ld, V, 0x2141, 0x6141, , 0, 0, 0\n"
                        +"0x7bb, 112, 127, 1, 1, 0, 1000, Cell 69 %1ld.%03ld, V, 0x2142, 0x6142, , 0, 0, 0\n"
                        +"0x7bb, 128, 135, 1, 1, 40, 0, T 5: %4ld, C, 0x2104, 0x6104, Cell 5 Temperature, 2, 10, 0\n"
                        +"0x7bb, 128, 143, 1, 1, 0, 1000, Cell 08 %1ld.%03ld, V, 0x2141, 0x6141, , 0, 0, 0\n"
                        +"0x7bb, 128, 143, 1, 1, 0, 1000, Cell 70 %1ld.%03ld, V, 0x2142, 0x6142, , 0, 0, 0\n"
                        +"0x7bb, 136, 151, 1, 1, 0, 0, Bat kWh %5ld, kWh, 0x2161, 0x6161, , 1, 0, 0\n"
                        +"0x7bb, 136, 151, 1, 1, 0, 0, Raw t c06: %4ld, unknown, 0x2104, 0x6104, , 2, 10, 0\n"
                        +"0x7bb, 144, 159, 1, 1, 0, 1000, Cell 09 %1ld.%03ld, V, 0x2141, 0x6141, , 0, 0, 0\n"
                        +"0x7bb, 144, 159, 1, 1, 0, 1000, Cell 71 %1ld.%03ld, V, 0x2142, 0x6142, , 0, 0, 0\n"
                        +"0x7bb, 152, 159, 1, 1, 40, 0, T 6: %4ld, C, 0x2104, 0x6104, Cell 6 Temperature, 2, 10, 0\n"
                        +"0x7bb, 16, 31, 1, 1, 0, 0, Raw t c01: %4ld, unknown, 0x2104, 0x6104, , 1, 10, 0\n"
                        +"0x7bb, 16, 31, 1, 1, 0, 0, Raw t c01: %4ld, unknown, 0x2104, 0x6104, , 2, 10, 0\n"
                        +"0x7bb, 16, 31, 1, 1, 0, 1000, Cell 01 %1ld.%03ld, V, 0x2141, 0x6141, , 0, 0, 0\n"
                        +"0x7bb, 16, 31, 1, 1, 0, 1000, Cell 63 %1ld.%03ld, V, 0x2142, 0x6142, , 0, 0, 0\n"
                        +"0x7bb, 160, 175, 1, 1, 0, 0, Raw t c07: %4ld, unknown, 0x2104, 0x6104, , 2, 10, 0\n"
                        +"0x7bb, 160, 175, 1, 1, 0, 1000, Cell 10 %1ld.%03ld, V, 0x2141, 0x6141, , 0, 0, 0\n"
                        +"0x7bb, 160, 175, 1, 1, 0, 1000, Cell 72 %1ld.%03ld, V, 0x2142, 0x6142, , 0, 0, 0\n"
                        +"0x7bb, 176, 183, 1, 1, 40, 0, T 7: %4ld, C, 0x2104, 0x6104, Cell 7 Temperature, 2, 10, 0\n"
                        +"0x7bb, 176, 191, 1, 1, 0, 1000, Cell 11 %1ld.%03ld, V, 0x2141, 0x6141, , 0, 0, 0\n"
                        +"0x7bb, 176, 191, 1, 1, 0, 1000, Cell 73 %1ld.%03ld, V, 0x2142, 0x6142, , 0, 0, 0\n"
                        +"0x7bb, 184, 199, 1, 1, 0, 0, Raw t c08: %4ld, unknown, 0x2104, 0x6104, , 2, 10, 0\n"
                        +"0x7bb, 192, 207, 1, 1, 0, 100, Max batt in pw:%2ld.%02ld, kW, 0x2101, 0x6101, , 1, 0, 0\n"
                        +"0x7bb, 192, 207, 1, 1, 0, 1000, Cell 12 %1ld.%03ld, V, 0x2141, 0x6141, , 0, 0, 0\n"
                        +"0x7bb, 192, 207, 1, 1, 0, 1000, Cell 74 %1ld.%03ld, V, 0x2142, 0x6142, , 0, 0, 0\n"
                        +"0x7bb, 200, 207, 1, 1, 40, 0, T 8: %4ld, C, 0x2104, 0x6104, Cell 8 Temperature, 2, 10, 0\n"
                        +"0x7bb, 208, 223, 1, 1, 0, 0, Raw t c09: %4ld, unknown, 0x2104, 0x6104, , 2, 10, 0\n"
                        +"0x7bb, 208, 223, 1, 1, 0, 100, Max batt out pw:%2ld.%02ld, kW, 0x2101, 0x6101, , 1, 0, 0\n"
                        +"0x7bb, 208, 223, 1, 1, 0, 1000, Cell 13 %1ld.%03ld, V, 0x2141, 0x6141, , 0, 0, 0\n"
                        +"0x7bb, 208, 223, 1, 1, 0, 1000, Cell 75 %1ld.%03ld, V, 0x2142, 0x6142, , 0, 0, 0\n"
                        +"0x7bb, 224, 231, 1, 1, 40, 0, T 9: %4ld, C, 0x2104, 0x6104, Cell 9 Temperature, 2, 10, 0\n"
                        +"0x7bb, 224, 239, 1, 1, 0, 1000, Cell 14 %1ld.%03ld, V, 0x2141, 0x6141, , 0, 0, 0\n"
                        +"0x7bb, 224, 239, 1, 1, 0, 1000, Cell 76 %1ld.%03ld, V, 0x2142, 0x6142, , 0, 0, 0\n"
                        +"0x7bb, 232, 247, 1, 1, 0, 0, Raw t c10: %4ld, unknown, 0x2104, 0x6104, , 2, 10, 0\n"
                        +"0x7bb, 240, 255, 1, 1, 0, 1000, Cell 15 %1ld.%03ld, V, 0x2141, 0x6141, , 0, 0, 0\n"
                        +"0x7bb, 240, 255, 1, 1, 0, 1000, Cell 77 %1ld.%03ld, V, 0x2142, 0x6142, , 0, 0, 0\n"
                        +"0x7bb, 248, 255, 1, 1, 40, 0, T 10: %4ld, C, 0x2104, 0x6104, Cell 10 Temperature, 2, 10, 0\n"
                        +"0x7bb, 256, 271, 1, 1, 0, 0, Raw t c11: %4ld, unknown, 0x2104, 0x6104, , 2, 10, 0\n"
                        +"0x7bb, 256, 271, 1, 1, 0, 1000, Cell 16 %1ld.%03ld, V, 0x2141, 0x6141, , 0, 0, 0\n"
                        +"0x7bb, 256, 271, 1, 1, 0, 1000, Cell 78 %1ld.%03ld, V, 0x2142, 0x6142, , 0, 0, 0\n"
                        +"0x7bb, 272, 279, 1, 1, 40, 0, T 11: %4ld, C, 0x2104, 0x6104, Cell 11 Temperature, 2, 10, 0\n"
                        +"0x7bb, 272, 287, 1, 1, 0, 1000, Cell 17 %1ld.%03ld, V, 0x2141, 0x6141, , 0, 0, 0\n"
                        +"0x7bb, 272, 287, 1, 1, 0, 1000, Cell 79 %1ld.%03ld, V, 0x2142, 0x6142, , 0, 0, 0\n"
                        +"0x7bb, 280, 295, 1, 1, 0, 0, Raw t c12: %4ld, unknown, 0x2104, 0x6104, , 2, 10, 0\n"
                        +"0x7bb, 288, 303, 1, 1, 0, 1000, Cell 18 %1ld.%03ld, V, 0x2141, 0x6141, , 0, 0, 0\n"
                        +"0x7bb, 288, 303, 1, 1, 0, 1000, Cell 80 %1ld.%03ld, V, 0x2142, 0x6142, , 0, 0, 0\n"
                        +"0x7bb, 296, 303, 1, 1, 40, 0, T 12: %4ld, C, 0x2104, 0x6104, Cell 12 Temperature, 2, 10, 0\n"
                        +"0x7bb, 304, 319, 1, 1, 0, 1000, Cell 19 %1ld.%03ld, V, 0x2141, 0x6141, , 0, 0, 0\n"
                        +"0x7bb, 304, 319, 1, 1, 0, 1000, Cell 81 %1ld.%03ld, V, 0x2142, 0x6142, , 0, 0, 0\n"
                        +"0x7bb, 316, 335, 10000, 1, 0, 10000, SOC(real) %2ld.%04ld, %, 0x2101, 0x6101, Real State of Charge, 1, 0, 0\n"
                        +"0x7bb, 32, 39, 1, 1, 0, 0, T 1: %4ld, C, 0x2104, 0x6104, Cell 1 Temperature, 1, 10, 0\n"
                        +"0x7bb, 32, 39, 1, 1, 40, 0, T 1: %4ld, C, 0x2104, 0x6104, Cell 1 Temperature, 2, 10, 0\n"
                        +"0x7bb, 32, 47, 1, 1, 0, 1000, Cell 02 %1ld.%03ld, V, 0x2141, 0x6141, , 0, 0, 0\n"
                        +"0x7bb, 32, 47, 1, 1, 0, 1000, Cell 64 %1ld.%03ld, V, 0x2142, 0x6142, , 0, 0, 0\n"
                        +"0x7bb, 320, 335, 1, 1, 0, 1000, Cell 20 %1ld.%03ld, V, 0x2141, 0x6141, , 0, 0, 0\n"
                        +"0x7bb, 320, 335, 1, 1, 0, 1000, Cell 82 %1ld.%03ld, V, 0x2142, 0x6142, , 0, 0, 0\n"
                        +"0x7bb, 336, 351, 1, 1, 0, 100, Max batt in pw:%2ld.%02ld, kW, 0x2101, 0x6101, , 2, 0, 0\n"
                        +"0x7bb, 336, 351, 1, 1, 0, 1000, Cell 21 %1ld.%03ld, V, 0x2141, 0x6141, , 0, 0, 0\n"
                        +"0x7bb, 336, 351, 1, 1, 0, 1000, Cell 83 %1ld.%03ld, V, 0x2142, 0x6142, , 0, 0, 0\n"
                        +"0x7bb, 348, 367, 10000, 1, 0, 10000, Bat Ah %2ld.%04ld, Ah, 0x2101, 0x6101, , 1, 0, 0\n"
                        +"0x7bb, 352, 367, 1, 1, 0, 1000, Cell 22 %1ld.%03ld, V, 0x2141, 0x6141, , 0, 0, 0\n"
                        +"0x7bb, 352, 367, 1, 1, 0, 1000, Cell 84 %1ld.%03ld, V, 0x2142, 0x6142, , 0, 0, 0\n"
                        +"0x7bb, 368, 383, 1, 1, 0, 1000, Cell 23 %1ld.%03ld, V, 0x2141, 0x6141, , 0, 0, 0\n"
                        +"0x7bb, 368, 383, 1, 1, 0, 1000, Cell 85 %1ld.%03ld, V, 0x2142, 0x6142, , 0, 0, 0\n"
                        +"0x7bb, 384, 399, 1, 1, 0, 1000, Cell 24 %1ld.%03ld, V, 0x2141, 0x6141, , 0, 0, 0\n"
                        +"0x7bb, 384, 399, 1, 1, 0, 1000, Cell 86 %1ld.%03ld, V, 0x2142, 0x6142, , 0, 0, 0\n"
                        +"0x7bb, 40, 55, 1, 1, 0, 0, Raw t c02: %4ld, unknown, 0x2104, 0x6104, , 1, 10, 0\n"
                        +"0x7bb, 40, 55, 1, 1, 0, 0, Raw t c02: %4ld, unknown, 0x2104, 0x6104, , 2, 10, 0\n"
                        +"0x7bb, 400, 415, 1, 1, 0, 1000, Cell 25 %1ld.%03ld, V, 0x2141, 0x6141, , 0, 0, 0\n"
                        +"0x7bb, 400, 415, 1, 1, 0, 1000, Cell 87 %1ld.%03ld, V, 0x2142, 0x6142, , 0, 0, 0\n"
                        +"0x7bb, 416, 431, 1, 1, 0, 1000, Cell 26 %1ld.%03ld, V, 0x2141, 0x6141, , 0, 0, 0\n"
                        +"0x7bb, 416, 431, 1, 1, 0, 1000, Cell 88 %1ld.%03ld, V, 0x2142, 0x6142, , 0, 0, 0\n"
                        +"0x7bb, 432, 447, 1, 1, 0, 1000, Cell 27 %1ld.%03ld, V, 0x2141, 0x6141, , 0, 0, 0\n"
                        +"0x7bb, 432, 447, 1, 1, 0, 1000, Cell 89 %1ld.%03ld, V, 0x2142, 0x6142, , 0, 0, 0\n"
                        +"0x7bb, 448, 463, 1, 1, 0, 1000, Cell 28 %1ld.%03ld, V, 0x2141, 0x6141, , 0, 0, 0\n"
                        +"0x7bb, 448, 463, 1, 1, 0, 1000, Cell 90 %1ld.%03ld, V, 0x2142, 0x6142, , 0, 0, 0\n"
                        +"0x7bb, 464, 479, 1, 1, 0, 1000, Cell 29 %1ld.%03ld, V, 0x2141, 0x6141, , 0, 0, 0\n"
                        +"0x7bb, 464, 479, 1, 1, 0, 1000, Cell 91 %1ld.%03ld, V, 0x2142, 0x6142, , 0, 0, 0\n"
                        +"0x7bb, 48, 63, 1, 1, 0, 1000, Cell 03 %1ld.%03ld, V, 0x2141, 0x6141, , 0, 0, 0\n"
                        +"0x7bb, 48, 63, 1, 1, 0, 1000, Cell 65 %1ld.%03ld, V, 0x2142, 0x6142, , 0, 0, 0\n"
                        +"0x7bb, 480, 495, 1, 1, 0, 1000, Cell 30 %1ld.%03ld, V, 0x2141, 0x6141, , 0, 0, 0\n"
                        +"0x7bb, 480, 495, 1, 1, 0, 1000, Cell 92 %1ld.%03ld, V, 0x2142, 0x6142, , 0, 0, 0\n"
                        +"0x7bb, 496, 511, 1, 1, 0, 1000, Cell 31 %1ld.%03ld, V, 0x2141, 0x6141, , 0, 0, 0\n"
                        +"0x7bb, 496, 511, 1, 1, 0, 1000, Cell 93 %1ld.%03ld, V, 0x2142, 0x6142, , 0, 0, 0\n"
                        +"0x7bb, 512, 527, 1, 1, 0, 1000, Cell 32 %1ld.%03ld, V, 0x2141, 0x6141, , 0, 0, 0\n"
                        +"0x7bb, 512, 527, 1, 1, 0, 1000, Cell 94 %1ld.%03ld, V, 0x2142, 0x6142, , 0, 0, 0\n"
                        +"0x7bb, 528, 543, 1, 1, 0, 1000, Cell 33 %1ld.%03ld, V, 0x2141, 0x6141, , 0, 0, 0\n"
                        +"0x7bb, 528, 543, 1, 1, 0, 1000, Cell 95 %1ld.%03ld, V, 0x2142, 0x6142, , 0, 0, 0\n"
                        +"0x7bb, 544, 559, 1, 1, 0, 1000, Cell 34 %1ld.%03ld, V, 0x2141, 0x6141, , 0, 0, 0\n"
                        +"0x7bb, 544, 559, 1, 1, 0, 1000, Cell 96 %1ld.%03ld, V, 0x2142, 0x6142, , 0, 0, 0\n"
                        +"0x7bb, 56, 63, 1, 1, 0, 0, T 2: %4ld, C, 0x2104, 0x6104, Cell 2 Temperature, 1, 10, 0\n"
                        +"0x7bb, 56, 63, 1, 1, 40, 0, T 2: %4ld, C, 0x2104, 0x6104, Cell 2 Temperature, 2, 10, 0\n"
                        +"0x7bb, 560, 575, 1, 1, 0, 1000, Cell 35 %1ld.%03ld, V, 0x2141, 0x6141, , 0, 0, 0\n"
                        +"0x7bb, 576, 591, 1, 1, 0, 1000, Cell 36 %1ld.%03ld, V, 0x2141, 0x6141, , 0, 0, 0\n"
                        +"0x7bb, 592, 607, 1, 1, 0, 1000, Cell 37 %1ld.%03ld, V, 0x2141, 0x6141, , 0, 0, 0\n"
                        +"0x7bb, 60, 79, 10000, 1, 0, 10000, Bat Ah %2ld.%04ld, Ah, 0x2161, 0x6161, , 1, 0, 0\n"
                        +"0x7bb, 608, 623, 1, 1, 0, 1000, Cell 38 %1ld.%03ld, V, 0x2141, 0x6141, , 0, 0, 0\n"
                        +"0x7bb, 624, 639, 1, 1, 0, 1000, Cell 39 %1ld.%03ld, V, 0x2141, 0x6141, , 0, 0, 0\n"
                        +"0x7bb, 64, 79, 1, 1, 0, 0, Raw t c03: %4ld, unknown, 0x2104, 0x6104, , 1, 10, 0\n"
                        +"0x7bb, 64, 79, 1, 1, 0, 0, Raw t c03: %4ld, unknown, 0x2104, 0x6104, , 2, 10, 0\n"
                        +"0x7bb, 64, 79, 1, 1, 0, 1000, Cell 04 %1ld.%03ld, V, 0x2141, 0x6141, , 0, 0, 0\n"
                        +"0x7bb, 64, 79, 1, 1, 0, 1000, Cell 66 %1ld.%03ld, V, 0x2142, 0x6142, , 0, 0, 0\n"
                        +"0x7bb, 640, 655, 1, 1, 0, 1000, Cell 40 %1ld.%03ld, V, 0x2141, 0x6141, , 0, 0, 0\n"
                        +"0x7bb, 656, 671, 1, 1, 0, 1000, Cell 41 %1ld.%03ld, V, 0x2141, 0x6141, , 0, 0, 0\n"
                        +"0x7bb, 672, 687, 1, 1, 0, 1000, Cell 42 %1ld.%03ld, V, 0x2141, 0x6141, , 0, 0, 0\n"
                        +"0x7bb, 688, 703, 1, 1, 0, 1000, Cell 43 %1ld.%03ld, V, 0x2141, 0x6141, , 0, 0, 0\n"
                        +"0x7bb, 704, 719, 1, 1, 0, 1000, Cell 44 %1ld.%03ld, V, 0x2141, 0x6141, , 0, 0, 0\n"
                        +"0x7bb, 720, 735, 1, 1, 0, 1000, Cell 45 %1ld.%03ld, V, 0x2141, 0x6141, , 0, 0, 0\n"
                        +"0x7bb, 736, 751, 1, 1, 0, 1000, Cell 46 %1ld.%03ld, V, 0x2141, 0x6141, , 0, 0, 0\n"
                        +"0x7bb, 752, 767, 1, 1, 0, 1000, Cell 47 %1ld.%03ld, V, 0x2141, 0x6141, , 0, 0, 0\n"
                        +"0x7bb, 768, 783, 1, 1, 0, 1000, Cell 48 %1ld.%03ld, V, 0x2141, 0x6141, , 0, 0, 0\n"
                        +"0x7bb, 784, 799, 1, 1, 0, 1000, Cell 49 %1ld.%03ld, V, 0x2141, 0x6141, , 0, 0, 0\n"
                        +"0x7bb, 80, 87, 1, 1, 0, 0, T 3: %4ld, C, 0x2104, 0x6104, Cell 3 Temperature, 1, 10, 0\n"
                        +"0x7bb, 80, 87, 1, 1, 40, 0, T 3: %4ld, C, 0x2104, 0x6104, Cell 3 Temperature, 2, 10, 0\n"
                        +"0x7bb, 80, 87, 2, 1, 0, 10, Bat SOH%3ld.%01ld, %, 0x2161, 0x6161, , 1, 0, 0\n"
                        +"0x7bb, 80, 95, 1, 1, 0, 1000, Cell 05 %1ld.%03ld, V, 0x2141, 0x6141, , 0, 0, 0\n"
                        +"0x7bb, 80, 95, 1, 1, 0, 1000, Cell 67 %1ld.%03ld, V, 0x2142, 0x6142, , 0, 0, 0\n"
                        +"0x7bb, 800, 815, 1, 1, 0, 1000, Cell 50 %1ld.%03ld, V, 0x2141, 0x6141, , 0, 0, 0\n"
                        +"0x7bb, 816, 831, 1, 1, 0, 1000, Cell 51 %1ld.%03ld, V, 0x2141, 0x6141, , 0, 0, 0\n"
                        +"0x7bb, 832, 847, 1, 1, 0, 1000, Cell 52 %1ld.%03ld, V, 0x2141, 0x6141, , 0, 0, 0\n"
                        +"0x7bb, 848, 863, 1, 1, 0, 1000, Cell 53 %1ld.%03ld, V, 0x2141, 0x6141, , 0, 0, 0\n"
                        +"0x7bb, 864, 879, 1, 1, 0, 1000, Cell 54 %1ld.%03ld, V, 0x2141, 0x6141, , 0, 0, 0\n"
                        +"0x7bb, 88, 103, 1, 1, 0, 0, Raw t c04: %4ld, unknown, 0x2104, 0x6104, , 1, 10, 0\n"
                        +"0x7bb, 88, 103, 1, 1, 0, 0, Raw t c04: %4ld, unknown, 0x2104, 0x6104, , 2, 10, 0\n"
                        +"0x7bb, 880, 895, 1, 1, 0, 1000, Cell 55 %1ld.%03ld, V, 0x2141, 0x6141, , 0, 0, 0\n"
                        +"0x7bb, 896, 911, 1, 1, 0, 1000, Cell 56 %1ld.%03ld, V, 0x2141, 0x6141, , 0, 0, 0\n"
                        +"0x7bb, 912, 927, 1, 1, 0, 1000, Cell 57 %1ld.%03ld, V, 0x2141, 0x6141, , 0, 0, 0\n"
                        +"0x7bb, 928, 943, 1, 1, 0, 1000, Cell 58 %1ld.%03ld, V, 0x2141, 0x6141, , 0, 0, 0\n"
                        +"0x7bb, 944, 959, 1, 1, 0, 1000, Cell 59 %1ld.%03ld, V, 0x2141, 0x6141, , 0, 0, 0\n"
                        +"0x7bb, 96, 111, 1, 1, 0, 1000, Cell 06 %1ld.%03ld, V, 0x2141, 0x6141, , 0, 0, 0\n"
                        +"0x7bb, 96, 111, 1, 1, 0, 1000, Cell 68 %1ld.%03ld, V, 0x2142, 0x6142, , 0, 0, 0\n"
                        +"0x7bb, 960, 975, 1, 1, 0, 1000, Cell 60 %1ld.%03ld, V, 0x2141, 0x6141, , 0, 0, 0\n"
                        +"0x7bb, 976, 991, 1, 1, 0, 1000, Cell 61 %1ld.%03ld, V, 0x2141, 0x6141, , 0, 0, 0\n"
                        +"0x7bb, 992, 1007, 1, 1, 0, 1000, Cell 62 %1ld.%03ld, V, 0x2141, 0x6141, , 0, 0, 0\n"
                        +"0x7ec, 24, 31, 1, 1, 0, 0, Bat Health, %, 0x223206, 0x623206, , 0, 0, 0\n"
                        +"0x7ec, 24, 31, 1, 1, 0, 0, Steering wheel CC/SL buttons, , 0x22204b, 0x62204b, , 1, 0, 0\n"
                        +"0x7ec, 24, 31, 1, 1, 40, 0, Ext temp, C, 0x2233b1, 0x6233b1, , 0, 0, 0\n"
                        +"0x7ec, 24, 39, 1, 1, 0, 0, Pedal, , 0x22202e, 0x62202e, , 0, 0, 0\n"
                        +"0x7ec, 24, 39, 1, 1, 0, 100, 12V Volt, V, 0x222005, 0x622005, , 0, 0, 0\n"
                        +"0x7ec, 24, 39, 1, 1, 0, 100, Speed, km/h, 0x222003, 0x622003, , 1, 0, 0\n"
                        +"0x7ec, 24, 39, 1, 2, 0, 100, SOC, %, 0x222002, 0x622002, , 2, 0, 0\n"
                        +"0x7ec, 24, 39, 1, 25, 0x8000, 100, Amp, A, 0x223204, 0x623204, , 0, 0, 0\n"
                        +"0x7ec, 24, 39, 1, 50, 0, 100, Motor Volt, V, 0x222004, 0x622004, , 0, 0, 0\n"
                        +"0x7ec, 24, 39, 1, 50, 0, 100, Volt, V, 0x223203, 0x623203, , 0, 0, 0\n"
                        +"0x7ec, 24, 39, 48, 100, 0, 100, SOC, %, 0x222002, 0x622002, , 1, 0, 0\n"
                        +"0x7ec, 24, 47, 1, 1, 0, 0, Odometer, km, 0x222006, 0x622006, , 0, 0, 0\n"
                        +"0x7ec, 128, 143, 1, 1, 0, 0, SCH SW version, , 0x2180, 0x6180, , 0, 0, 0\n"
                        +"0x7da, 128, 143, 1, 1, 0, 0, TCU SW version, , 0x2180, 0x6180, , 0, 0, 0\n"
                        +"0x7bb, 128, 143, 1, 1, 0, 0, LBC SW version, , 0x2180, 0x6180, , 0, 0, 0\n"
                        +"0x77e, 128, 143, 1, 1, 0, 0, PEBSW version, , 0x2180, 0x6180, , 0, 0, 0\n"
                        +"0x772, 128, 143, 1, 1, 0, 0, AIBAG SW version, , 0x2180, 0x6180, , 0, 0, 0\n"
                        +"0x76d, 128, 143, 1, 1, 0, 0, USM SW version, , 0x2180, 0x6180, , 0, 0, 0\n"

                        +"0x763, 128, 143, 1, 1, 0, 0, CLUSTER SW ver, , 0x2180, 0x6180, , 0, 0, 0\n"
                        +"0x763, 112, 127, 1, 1, 0, 0, CLUSTER PG ver, , 0x2180, 0x6180, , 0, 0, 0\n"
                        +"0x763,   0,   7, 1, 1, 0, 0, CLUSTER reset,  , 0x14ffff, 0x54, , 0, 0, 0\n"
                        +"0x763,   0,   0, 1, 1, 0, 0, CLUSTER DTC,    , 0x19023b, 0x5902ff, , 0, 0, 0\n"

                        +"0x762, 128, 143, 1, 1, 0, 0, EPS SW version, , 0x2180, 0x6180, , 0, 0, 0\n"
                        +"0x760, 128, 143, 1, 1, 0, 0, ABS SW version, , 0x2180, 0x6180, , 0, 0, 0\n"
                        +"0x7bc, 128, 143, 1, 1, 0, 0, UBP SW version, , 0x2180, 0x6180, , 0, 0, 0\n"
                        +"0x765, 128, 143, 1, 1, 0, 0, BCM SW version, , 0x2180, 0x6180, , 0, 0, 0\n"

                        +"0x764, 128, 143, 1, 1, 0, 0, CLIM SW version, , 0x2180, 0x6180, , 0, 0, 0\n"
                        +"0x764, 112, 127, 1, 1, 0, 0, CLIM PG version, , 0x2180, 0x6180, , 0, 0, 0\n"
                        +"0x764,   0,   7, 1, 1, 0, 0, CLIM reset,      , 0x14ffff, 0x54, , 0, 0, 0\n"
                        +"0x764,   0,   0, 1, 1, 0, 0, CLIM DTC,        , 0x19023b, 0x5902ff, , 0, 0, 0\n"

                        +"0x76e, 128, 143, 1, 1, 0, 0, UPA SW version,  , 0x2180, 0x6180, , 0, 0, 0\n"

                        +"0x793, 128, 143, 1, 1, 0, 0, BCB SW version, , 0x2180, 0x6180, , 0, 0, 0\n"
                        +"0x793,   0,   0, 1, 1, 0, 0, BCB Reset,      , 0x14ffffff, 0x54, , 0, 0, 0\n"
                        +"0x793,   0,   0, 1, 1, 0, 0, BCB DTC,        , 0x19023b, 0x5902ff, , 0, 0, 0\n"

                        +"0x7b6, 128, 143, 1, 1, 0, 0, LBC2 SW version, , 0x2180, 0x6180, , 0, 0, 0\n"

                        +"0x722, 128, 143, 1, 1, 0, 0, LINSCH SW version, , 0x2180, 0x6180, , 0, 0, 0\n"


                ;

        try {
            fieldDef += readFromLocalFile();
        }
        catch(Exception e)
        {
            // ignore
        }

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
                        Integer.parseInt(tokens[FIELD_DIVIDER].trim()),
                        Integer.parseInt(tokens[FIELD_MULTIPLIER].trim()),
                        (
                                tokens[FIELD_OFFSET].trim().contains("0x")
                                        ?
                                        Integer.parseInt(tokens[FIELD_OFFSET].trim().replace("0x", ""), 16)
                                        :
                                        Double.parseDouble(tokens[FIELD_OFFSET].trim())
                        ),
                        Integer.parseInt(tokens[FIELD_DECIMALS].trim()),
                        tokens[FIELD_FORMAT],
                        tokens[FIELD_UNIT].trim(),
                        tokens[FIELD_REQUEST_ID].trim().replace("0x", ""),
                        tokens[FIELD_RESPONSE_ID].trim().replace("0x", ""),
                        tokens[FIELD_DESCRIPTION],
                        Integer.parseInt(tokens[FIELD_CAR].trim()),
                        Integer.parseInt(tokens[FIELD_SKIPS].trim()),
                        Integer.parseInt(tokens[FIELD_FREQ].trim())
                );
                // add the field to the list of available fields
                add(field);
            }
        }
    }

    private String readFromLocalFile()
    {
        //*Don't* hardcode "/sdcard"
        File sdcard = Environment.getExternalStorageDirectory();
        MainActivity.debug("SD: "+sdcard.getAbsolutePath());

        //Get the text file
        File file = new File(sdcard,"fields.csv");

        //Read text from file
        StringBuilder text = new StringBuilder();

        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;

            while ((line = br.readLine()) != null) {
                text.append(line);
                text.append('\n');
            }
            br.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
       return text.toString();
    }

    private void readFromFile(String filename) throws FileNotFoundException, IOException
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
                Field field = new Field(
                        Integer.parseInt(tokens[FIELD_ID].trim().replace("0x", ""), 16),
                        Integer.parseInt(tokens[FIELD_FROM].trim()),
                        Integer.parseInt(tokens[FIELD_TO].trim()),
                        Integer.parseInt(tokens[FIELD_DIVIDER].trim()),
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
                        tokens[FIELD_UNIT],
                        tokens[FIELD_REQUEST_ID].trim().replace("0x", ""),
                        tokens[FIELD_RESPONSE_ID].trim().replace("0x", ""),
                        tokens[FIELD_DESCRIPTION].trim(),
                        Integer.parseInt(tokens[FIELD_CAR].trim()),
                        Integer.parseInt(tokens[FIELD_SKIPS].trim()),
                        Integer.parseInt(tokens[FIELD_FREQ].trim())
                );
                // add the field to the list of available fields
                add(field);
            }
        }
    }

    public Field getBySID(String sid) {
        sid=sid.toLowerCase();

        // first let's try to get the field that is bound to the selected car
        Field tryField = fieldsBySid.get(getCar()+"."+sid);
        if(tryField!=null) return tryField;

        // if none is found, try the other one, starting with 0 = CAR_ANY
        for(int i=0; i<5; i++) {
            tryField = fieldsBySid.get(i + "." + sid);
            if (tryField != null) return tryField;
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
    public void onMessageCompleteEvent(Message message) {
        //MainActivity.debug(message.toString());
        //if(message.getResponseId()!=null)
        //  MainActivity.debug("Frame.rID = "+message.getResponseId());
        for(int i=0; i< fields.size(); i++)
        {
            Field field = fields.get(i);
            /*if(field.getId()== message.getId())
            {
                if(message.getResponseId()!=null)
                    MainActivity.debug(message.getResponseId()+" -> Field: "+field.getFormat());
            }*/
            if(field.getId()== message.getId() &&
                    (
                            message.getResponseId()==null
                            ||
                            message.getResponseId().trim().equals(field.getResponseId().trim())
                            ))
            {
                //MainActivity.debug("Field.rID = "+field.getResponseId());
                //if(message.getResponseId()!=null)
                //    MainActivity.debug("Field: "+field.getFormat());
                String binString = message.getAsBinaryString();
                if(binString.length()>= field.getTo()) {
                    // parseInt --> signed, so the first bit is "cut-off"!
                    try {
                        int val = Integer.parseInt("0" + binString.substring(field.getFrom(), field.getTo() + 1), 2);
                        //MainActivity.debug("Fields: onMessageCompleteEvent > "+field.getSID()+" = "+val);
                        field.setValue(val);
                    } catch (Exception e)
                    {
                        // ignore
                    }
                }
            }
        }
    }

    public void add(Field field) {
        fields.add(field);
        fieldsBySid.put(field.getCar()+"."+field.getSID(),field);
    }

    public int getCar() {
        return car;
    }

    public void setCar(int car) {
        this.car = car;
    }

    public void notifyAllFieldListeners()
    {
        for(int i=0; i< fields.size(); i++) {
            fields.get(i).notifyFieldListeners();
        }
    }

    public void clearAllFields()
    {
        for(int i=0; i< fields.size(); i++) {
            fields.get(i).setValue(0);
        }
    }

    /* --------------------------------
     * Tests ...
     \ ------------------------------ */
    
    public static void main(String[] args)
    {
        
    }
    
}
