/*
    CanZE
    Take a closer look at your ZE car

    Copyright (C) 2015 - The CanZE Team
    http://canze.fisch.lu

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or any
    later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/


/*
 * This class manages all know fields.
 * Actually only the simple fields from the free CAN stream are handled.
 */
package lu.fisch.canze.actors;

import lu.fisch.canze.activities.MainActivity;
import lu.fisch.canze.database.CanzeDataSource;
import lu.fisch.canze.interfaces.VirtualFieldAction;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

/**
 *
 * @author robertfisch test
 */
public class Fields {

    private static final int FIELD_SID          = 0; // to be stated in HEX, no leading 0x
    private static final int FIELD_ID           = 1; // to be stated in HEX, no leading 0x
    private static final int FIELD_FROM         = 2; // decimal
    private static final int FIELD_TO           = 3; // decimal
    private static final int FIELD_RESOLUTION   = 4; // double
    private static final int FIELD_OFFSET       = 5; // double
    private static final int FIELD_DECIMALS     = 6; // decimal
    private static final int FIELD_UNIT         = 7;
    private static final int FIELD_REQUEST_ID   = 8; // to be stated in HEX, no leading 0x
    private static final int FIELD_RESPONSE_ID  = 9; // to be stated in HEX, no leading 0x
    private static final int FIELD_OPTIONS      = 10; // to be stated in HEX, no leading 0x
    private static final int FIELD_NAME         = 11; // can be displayed/saved. Now only used for Diag ISO-TP
    private static final int FIELD_LIST         = 12; // same

    public static final int TOAST_NONE          = 0;
    public static final int TOAST_DEVICE        = 1;
    public static final int TOAST_ALL           = 2;

    private final ArrayList<Field> fields = new ArrayList<>();
    private final HashMap<String, Field> fieldsBySid = new HashMap<>();

    private static Fields instance = null;
    private double runningUsage = 0;
    private double realRangeReference = Double.NaN;
    private static long start = Calendar.getInstance().getTimeInMillis();

    //private int car = CAR_ANY;

    private Fields() {
        // the will be called by load(), and only after we know (or have changed) the car
        //fillStatic();
        //addVirtualFields();
    }

    public static boolean initialised()
    {
        return (instance==null);
    }

    public static Fields getInstance()
    {
        if(instance==null) instance=new Fields();
        return instance;
    }

    private void addVirtualFields() {
        addVirtualFieldUsage();
        addVirtualFieldUsageLpf();
        addVirtualFieldFrictionTorque();
        addVirtualFieldFrictionPower();
        addVirtualFieldDcPower();
        addVirtualFieldHeaterSetpoint();
        addVirtualFieldRealRange();
        addVirtualFieldRealDelta();
        addVirtualFieldRealDeltaNoReset();
    }


    private void addVirtualFieldUsage() {

        // It would be easier use SID_Consumption = "1fd.48" (dash kWh) instead of V*A

        final String SID_EVC_TractionBatteryVoltage = "7ec.623203.24";  // unit = V
        final String SID_EVC_TractionBatteryCurrent = "7ec.623204.24";  // unit = A
        final String SID_RealSpeed = "5d7.0";                           // unit = km/h

        addVirtualFieldCommon ("6100", "kWh/100km", SID_EVC_TractionBatteryVoltage+";"+SID_EVC_TractionBatteryCurrent+";"+SID_RealSpeed, new VirtualFieldAction() {
            @Override
            public double updateValue(HashMap<String, Field> dependantFields) {
                // get real speed
                double realSpeed = dependantFields.get(SID_RealSpeed).getValue();
                if (realSpeed < 0 || realSpeed > 150) return Double.NaN;
                if (realSpeed < 5) return 0;
                // get voltage
                double dcVolt = dependantFields.get(SID_EVC_TractionBatteryVoltage).getValue();
                // get current
                double dcCur = dependantFields.get(SID_EVC_TractionBatteryCurrent).getValue();
                if (dcVolt < 300 || dcVolt > 450 || dcCur < -200 || dcCur > 100) return Double.NaN;
                // power in kW
                double dcPwr = dcVolt * dcCur / 1000.0;
                double usage = -(Math.round(1000.0 * dcPwr / realSpeed) / 10.0);
                if (usage < -150) return -150; else if (usage > 150) return 150; else return usage;
            }
        });
    }

    private void addVirtualFieldFrictionTorque() {
        final String SID_DriverBrakeWheel_Torque_Request        = "130.44"; //UBP braking wheel torque the driver wants
        final String SID_ElecBrakeWheelsTorqueApplied           = "1f8.28"; //10ms

        addVirtualFieldCommon ("6101", "Nm", SID_DriverBrakeWheel_Torque_Request+";"+SID_ElecBrakeWheelsTorqueApplied, new VirtualFieldAction() {
            @Override
            public double updateValue(HashMap<String,Field> dependantFields) {

                return dependantFields.get(SID_DriverBrakeWheel_Torque_Request).getValue() - dependantFields.get(SID_ElecBrakeWheelsTorqueApplied).getValue();
            }
        });
    }

    private void addVirtualFieldFrictionPower() {
        final String SID_DriverBrakeWheel_Torque_Request        = "130.44"; //UBP braking wheel torque the driver wants
        final String SID_ElecBrakeWheelsTorqueApplied           = "1f8.28"; //10ms
        final String SID_ElecEngineRPM                          = "1f8.40"; //10ms

        addVirtualFieldCommon ("6102", "kW", SID_DriverBrakeWheel_Torque_Request+";"+SID_ElecBrakeWheelsTorqueApplied+";"+SID_ElecEngineRPM, new VirtualFieldAction() {
            @Override
            public double updateValue(HashMap<String,Field> dependantFields) {

                return (dependantFields.get(SID_DriverBrakeWheel_Torque_Request).getValue() - dependantFields.get(SID_ElecBrakeWheelsTorqueApplied).getValue()) * dependantFields.get(SID_ElecEngineRPM).getValue() / MainActivity.reduction;
            }
        });
    }

    private void addVirtualFieldDcPower() {
        final String SID_TractionBatteryVoltage             = "7ec.623203.24";
        final String SID_TractionBatteryCurrent             = "7ec.623204.24";

        addVirtualFieldCommon ("6103", "kW", SID_TractionBatteryVoltage+";"+SID_TractionBatteryCurrent, new VirtualFieldAction() {
            @Override
            public double updateValue(HashMap<String,Field> dependantFields) {

                return dependantFields.get(SID_TractionBatteryVoltage).getValue() * dependantFields.get(SID_TractionBatteryCurrent).getValue() / 1000;
            }
        });

    }

    private void addVirtualFieldUsageLpf() {

        // It would be easier use SID_Consumption = "1fd.48" (dash kWh) instead of V*A
        // need to use real timer. Now the averaging is dependant on dongle speed

        final String SID_VirtualUsage               = "800.6100.24";

        addVirtualFieldCommon ("6104", "kWh/100km", SID_VirtualUsage, new VirtualFieldAction() {
            @Override
            public double updateValue(HashMap<String, Field> dependantFields) {
                double value = dependantFields.get(SID_VirtualUsage).getValue();
                if (!Double.isNaN(value)) {
                    long now = Calendar.getInstance().getTimeInMillis();
                    long since = now - start;
                    if (since > 1000) since = 1000; // use a maximim of 1 second
                    start = now;

                    double factor = since * 0.00005; // 0.05 per second
                    runningUsage = runningUsage * (1 - factor) + value * factor;
                }
                return runningUsage;
            }
        });
    }

    private void addVirtualFieldHeaterSetpoint() {
        final String SID_VirtualUsage               = "699.8";

        addVirtualFieldCommon ("6105", "°C", SID_VirtualUsage, new VirtualFieldAction() {
            @Override
            public double updateValue(HashMap<String, Field> dependantFields) {
                double value = dependantFields.get(SID_VirtualUsage).getValue();
                if (value == 0) {
                    return Double.NaN;
                } else if (value == 4) {
                    return -10.0;
                } else if (value == 5) {
                    return 40.0;
                }
                return value;
            }
        });
    }

    private void addVirtualFieldRealRange() {
        final String SID_EVC_Odometer                         = "7ec.622006.24"; //  (EVC)
        final String SID_RangeEstimate                        = "654.42"; //  (EVC)

        if(Double.isNaN(realRangeReference))
            realRangeReference = CanzeDataSource.getInstance().getLast(SID_RangeEstimate);

        addVirtualFieldCommon ("6106", "km", SID_EVC_Odometer + ";" + SID_RangeEstimate, new VirtualFieldAction() {
            @Override
            public double updateValue(HashMap<String, Field> dependantFields) {
                double odo = dependantFields.get(SID_EVC_Odometer).getValue();
                double gom = dependantFields.get(SID_RangeEstimate).getValue();

                // timestamp of last inserted dot in MILLISECONDS
                long lastInsertedTime = CanzeDataSource.getInstance().getLastTime(SID_RangeEstimate);
                if (    // timeout of 15 minutes
                        (Calendar.getInstance().getTimeInMillis() - lastInsertedTime > 15*60*1000)
                                ||
                                Double.isNaN(realRangeReference)
                )
                {

                    if (!Double.isNaN(gom) && !Double.isNaN(odo)) {
                        realRangeReference = odo + gom;
                    }
                }

                if (Double.isNaN(realRangeReference)) {
                    return Double.NaN;
                }
                /*
                double delta = realRangeReference - odo - gom;
                if (delta > 12.0 || delta < -12.0) {
                    realRangeReference = odo + gom;
                } */

                return realRangeReference - odo;
            }
        });
    }


    private void addVirtualFieldRealDelta() {
        final String SID_EVC_Odometer                         = "7ec.622006.24"; //  (EVC)
        final String SID_RangeEstimate                        = "654.42"; //  (EVC)

        // get last value for realRange from internal database
        //MainActivity.debug("realRange 1: "+realRangeReference);
        if(Double.isNaN(realRangeReference)) {
            realRangeReference = CanzeDataSource.getInstance().getLast(SID_RangeEstimate);
            //MainActivity.debug("realRange >> getLast");
        }
        //MainActivity.debug("realRange 2: "+realRangeReference);

        addVirtualFieldCommon ("6107", "km", SID_EVC_Odometer + ";" + SID_RangeEstimate, new VirtualFieldAction() {
            @Override
            public double updateValue(HashMap<String, Field> dependantFields) {
                double odo = dependantFields.get(SID_EVC_Odometer).getValue();
                double gom = dependantFields.get(SID_RangeEstimate).getValue();

                //MainActivity.debug("realRange ODO: "+odo);
                //MainActivity.debug("realRange GOM: "+gom);

                // timestamp of last inserted dot in MILLISECONDS
                long lastInsertedTime = CanzeDataSource.getInstance().getLastTime(SID_RangeEstimate);
                if (    // timeout of 15 minutes
                        (Calendar.getInstance().getTimeInMillis() - lastInsertedTime > 15*60*1000)
                                ||
                                Double.isNaN(realRangeReference)
                )
                {
                    if (!Double.isNaN(gom) && !Double.isNaN(odo)) {
                        realRangeReference = odo + gom;
                    }
                }
                if (Double.isNaN(realRangeReference)) {
                    return Double.NaN;
                }
                double delta = realRangeReference - odo - gom;
                if (delta > 12.0 || delta < -12.0) {
                    realRangeReference = odo + gom;
                    delta = 0.0;
                }
                return delta;
            }
        });
    }

    private void addVirtualFieldRealDeltaNoReset() {
        final String SID_EVC_Odometer                         = "7ec.622006.24"; //  (EVC)
        final String SID_RangeEstimate                        = "654.42"; //  (EVC)

        // get last value for realRange from internal database
        //MainActivity.debug("realRange 1: "+realRangeReference);
        if(Double.isNaN(realRangeReference)) {
            realRangeReference = CanzeDataSource.getInstance().getLast(SID_RangeEstimate);
            //MainActivity.debug("realRange >> getLast");
        }
        //MainActivity.debug("realRange 2: "+realRangeReference);

        addVirtualFieldCommon ("6108", "km", SID_EVC_Odometer + ";" + SID_RangeEstimate, new VirtualFieldAction() {
            @Override
            public double updateValue(HashMap<String, Field> dependantFields) {
                double odo = dependantFields.get(SID_EVC_Odometer).getValue();
                double gom = dependantFields.get(SID_RangeEstimate).getValue();

                //MainActivity.debug("realRange ODO: "+odo);
                //MainActivity.debug("realRange GOM: "+gom);

                // timestamp of last inserted dot in MILLISECONDS
                long lastInsertedTime = CanzeDataSource.getInstance().getLastTime(SID_RangeEstimate);
                if (    // timeout of 15 minutes
                        (Calendar.getInstance().getTimeInMillis() - lastInsertedTime > 15*60*1000)
                                ||
                                Double.isNaN(realRangeReference)
                )
                {
                    if (!Double.isNaN(gom) && !Double.isNaN(odo)) {
                        realRangeReference = odo + gom;
                    }
                }
                if (Double.isNaN(realRangeReference)) {
                    return Double.NaN;
                }
                return realRangeReference - odo - gom;
            }
        });
    }

    private void addVirtualFieldCommon (String virtualId, String unit, String dependantIds, VirtualFieldAction virtualFieldAction) {
        // create a list of field this new virtual field will depend on
        HashMap<String, Field> dependantFields = new HashMap<>();
        boolean allOk = true;
        for (String idStr: dependantIds.split(";")){
            Field field = getBySID(idStr);
            if (field != null) {
                dependantFields.put(idStr, field);
            } else {
                allOk = false;
            }
        }
        if (allOk) {
            VirtualField virtualField = new VirtualField(virtualId, dependantFields, unit, virtualFieldAction);
            // add it to the list of fields
            add(virtualField);

        }
    }

    private void fillOneLine (String line) {
        if (line.contains ("#")) line = line.substring(0, line.indexOf('#'));
        String[] tokens = line.split(",");
        if (tokens.length > FIELD_OPTIONS) {
            int frameId = Integer.parseInt(tokens[FIELD_ID].trim(), 16);
            Frame frame = Frames.getInstance().getById(frameId);
            if (frame == null) {
                MainActivity.debug("frame does not exist:" + tokens[FIELD_ID].trim());
            } else {
                short options = Short.parseShort(tokens[FIELD_OPTIONS].trim(), 16);
                // ensure this field matches the selected car
                if ((options & MainActivity.car) != 0) {
                    //Create a new field object and fill his  data
                    MainActivity.debug(tokens[FIELD_SID] + " " + tokens[FIELD_ID] + "." + tokens[FIELD_FROM] + "." + tokens[FIELD_RESPONSE_ID]);
                    try {
                        Field field = new Field(
                                tokens[FIELD_SID].trim(),
                                frame,
                                Short.parseShort(tokens[FIELD_FROM].trim()),
                                Short.parseShort(tokens[FIELD_TO].trim()),
                                Double.parseDouble(tokens[FIELD_RESOLUTION].trim()),
                                Integer.parseInt(tokens[FIELD_DECIMALS].trim()),
                                Integer.parseInt(tokens[FIELD_OFFSET].trim()),
                                tokens[FIELD_UNIT].trim(),
                                tokens[FIELD_RESPONSE_ID].trim(),
                                options,
                                (tokens.length > FIELD_NAME) ? tokens[FIELD_NAME] : "",
                                (tokens.length > FIELD_LIST) ? tokens[FIELD_LIST] : ""
                        );

                        // we are maintaining a list of all fields in a frame so we can very
                        // quickly update all fields when a message (=frame data) comes in
                        // note that for free frames a frame is identified by it's ID and itś definition
                        // is entirely given
                        // for an ISOTP frame (diagnostics) frame, the frame is just a skeleton and
                        // the definition is entirely dependant on the responseID. Therefor, when an
                        // ISOTP field is defined, new frames are created dynamically
                        if (field.isIsoTp()) {
                            Frame subFrame = Frames.getInstance().getById(frameId, field.getResponseId());
                            if (subFrame == null) {
                                subFrame = new Frame(frame.getId(),frame.getInterval(),frame.getSendingEcu(),field.getResponseId(),frame);
                                Frames.getInstance().add (subFrame);
                            }
                            subFrame.addField(field);
                            field.setFrame(subFrame);
                        } else {
                            frame.addField(field);
                        }

                        // add the field to the list of available fields
                        add(field);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }

    }

    private void fillFromAsset (String assetName) {
        //Read text from asset
        AssetLoadHelper assetLoadHelper = new AssetLoadHelper(MainActivity.getInstance());

        BufferedReader bufferedReader = assetLoadHelper.getBufferedReaderFromAsset(assetName);
        if (bufferedReader == null) {
            MainActivity.toast(-100, "Can't access asset " + assetName);
            return;
        }
        try {
            String line;
            while ((line = bufferedReader.readLine()) != null)
                fillOneLine(line);
            bufferedReader.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void load ()
    {
        load ("");
    }


    public void load (String assetName)
    {
        fields.clear();
        fieldsBySid.clear();
        if (assetName.equals("")) {
            fillFromAsset("_Fields.csv");
            addVirtualFields();
        } else {
            fillFromAsset(assetName);
        }
        MainActivity.getInstance().registerApplicationFields(); // this registers i.e. speed for save driving mode
    }

    public Field getBySID(String sid) {
        return fieldsBySid.get(sid.toLowerCase());
    }

    public int size() {
        return fields.size();
    }

    public Field get(int index) {
        if(index < 0 || index>=fields.size()) return null;
        return fields.get(index);
    }

    public Object[] toArray() {
        return fields.toArray();
    }


    public void add(Field field) {
        fields.add(field);
        fieldsBySid.put(field.getSID(),field);
        //fieldsBySid.put(field.getCar()+"."+field.getSID(),field);
    }

    public void clearAllFields()
    {
        for(int i=0; i< fields.size(); i++) {
            fields.get(i).setValue(0);
        }
    }

    public ArrayList<Field> getAllFields () {
        return fields;
    }

}
