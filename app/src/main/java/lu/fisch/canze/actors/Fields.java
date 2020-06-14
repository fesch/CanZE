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

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import lu.fisch.canze.R;
import lu.fisch.canze.activities.MainActivity;
import lu.fisch.canze.classes.FieldLogger;
import lu.fisch.canze.database.CanzeDataSource;
import lu.fisch.canze.interfaces.VirtualFieldAction;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

/**
 * @author robertfisch test
 */
public class Fields {

    private static final int FIELD_SID = 0; // to be stated in HEX, no leading 0x
    private static final int FIELD_ID = 1; // to be stated in HEX, no leading 0x
    private static final int FIELD_FROM = 2; // decimal
    private static final int FIELD_TO = 3; // decimal
    private static final int FIELD_RESOLUTION = 4; // double
    private static final int FIELD_OFFSET = 5; // double
    private static final int FIELD_DECIMALS = 6; // decimal
    private static final int FIELD_UNIT = 7;
    private static final int FIELD_REQUEST_ID = 8; // to be stated in HEX, no leading 0x
    private static final int FIELD_RESPONSE_ID = 9; // to be stated in HEX, no leading 0x
    private static final int FIELD_OPTIONS = 10; // to be stated in HEX, no leading 0x
    private static final int FIELD_NAME = 11; // can be displayed/saved. Now only used for Diag ISO-TP
    private static final int FIELD_LIST = 12; // same

    private final ArrayList<Field> fields = new ArrayList<>();
    private final HashMap<String, Field> fieldsBySid = new HashMap<>();

    private static Fields instance = null;
    private double runningUsage = 0;
    private double realRangeReference = Double.NaN;
    private double realRangeReference2 = Double.NaN;
    private static long start = Calendar.getInstance().getTimeInMillis();

    private LocationManager locationManager;
    private LocationListener locationListener;

    //private int car = CAR_ANY;

    private Fields() {
        // the will be called by load(), and only after we know (or have changed) the car
        //fillStatic();
        //addVirtualFields();
    }

    public static boolean initialised() {
        return (instance == null);
    }

    public static Fields getInstance() {
        if (instance == null) instance = new Fields();
        return instance;
    }


    private void addVirtualFields() {
        addVirtualFieldUsage();
        addVirtualFieldUsageLpf();
        addVirtualFieldFrictionTorque();
        //addVirtualFieldFrictionPower();
        addVirtualFieldElecBrakeTorque();
        addVirtualFieldTotalPositiveTorque();
        addVirtualFieldTotalNegativeTorque();
        addVirtualFieldDcPowerIn();
        addVirtualFieldDcPowerOut();
        addVirtualFieldHeaterSetpoint();
        addVirtualFieldRealRange();
        addVirtualFieldRealDelta();
        addVirtualFieldRealDeltaNoReset();
        addVirtualFieldPilotAmp();
    }

    private void addVirtualField(String id) {
        switch (id) {
            case "6100": addVirtualFieldUsage();                break;
            case "6104": addVirtualFieldUsageLpf();             break;
            case "6101": addVirtualFieldFrictionTorque();       break;
            case "610a": addVirtualFieldElecBrakeTorque();      break;
            case "610b": addVirtualFieldTotalPositiveTorque();  break;
            case "610c": addVirtualFieldTotalNegativeTorque();  break;
            case "6103": addVirtualFieldDcPowerIn();            break;
            case "6109": addVirtualFieldDcPowerOut();           break;
            case "6105": addVirtualFieldHeaterSetpoint();       break;
            case "6106": addVirtualFieldRealRange();            break;
            case "6107": addVirtualFieldRealDelta();            break;
            case "6108": addVirtualFieldRealDeltaNoReset();     break;
            case "610d": addVirtualFieldPilotAmp();             break;
            case "610e": addVirtualFieldGps();                  break;
        }
    }

    public void selfPropel(String id, boolean startStop) {
        switch (id) {
            case "610e": virtualFieldPropelGps (startStop);     break;
        }
    }







    private void addVirtualFieldUsage() {

        // It would be easier use SID_Consumption = "1fd.48" (dash kWh) instead of V*A

        final String SID_EVC_TractionBatteryVoltage = "7ec.623203.24";  // unit = V
        final String SID_EVC_TractionBatteryCurrent = "7ec.623204.24";  // unit = A
        final String SID_RealSpeed = "5d7.0";                           // unit = km/h

        addVirtualFieldCommon("6100", "kWh/100km", SID_EVC_TractionBatteryVoltage + ";" + SID_EVC_TractionBatteryCurrent + ";" + SID_RealSpeed, new VirtualFieldAction() {
            @Override
            public double updateValue(HashMap<String, Field> dependantFields) {
                // get real speed
                Field privateField;
                if ((privateField = dependantFields.get(SID_RealSpeed)) == null) return Double.NaN;
                double realSpeed = privateField.getValue();
                if (realSpeed < 0 || realSpeed > 150) return Double.NaN;
                if (realSpeed < 5) return 0;
                // get voltage
                if ((privateField = dependantFields.get(SID_EVC_TractionBatteryVoltage)) == null) return Double.NaN;
                double dcVolt = privateField.getValue();
                // get current
                if ((privateField = dependantFields.get(SID_EVC_TractionBatteryCurrent)) == null) return Double.NaN;
                double dcCur = privateField.getValue();
                if (dcVolt < 300 || dcVolt > 450 || dcCur < -200 || dcCur > 100) return Double.NaN;
                // power in kW
                double dcPwr = dcVolt * dcCur / 1000.0;
                double usage = -(Math.round(1000.0 * dcPwr / realSpeed) / 10.0);
                if (usage < -150) return -150;
                else if (usage > 150) return 150;
                else return usage;
            }
        });
    }

    private void addVirtualFieldFrictionTorque() {
        final String SID_DriverBrakeWheel_Torque_Request = "130.44"; //UBP braking wheel torque the driver wants
        final String SID_ElecBrakeWheelsTorqueApplied = "1f8.28"; //10ms
        final String SID_Coasting_Torque = "18a.27";
        final String SID_HydraulicTorqueRequest = "7bc.624b7d.28"; // Total Hydraulic brake wheels torque request

        if (MainActivity.altFieldsMode) {
            addVirtualFieldCommon("6101", "Nm", SID_HydraulicTorqueRequest, new VirtualFieldAction() {
                @Override
                public double updateValue(HashMap<String, Field> dependantFields) {
                    Field privateField;
                    if ((privateField = dependantFields.get(SID_HydraulicTorqueRequest)) == null)
                        return Double.NaN;
                    return -privateField.getValue();
                }
            });

        } else {
            addVirtualFieldCommon("6101", "Nm", SID_DriverBrakeWheel_Torque_Request + ";" + SID_ElecBrakeWheelsTorqueApplied + ";" + SID_Coasting_Torque, new VirtualFieldAction() {
                @Override
                public double updateValue(HashMap<String, Field> dependantFields) {
                    Field privateField;
                    if ((privateField = dependantFields.get(SID_DriverBrakeWheel_Torque_Request)) == null)
                        return Double.NaN;
                    double requestedTorque = privateField.getValue();
                    if ((privateField = dependantFields.get(SID_ElecBrakeWheelsTorqueApplied)) == null)
                        return Double.NaN;
                    double electricTorque = privateField.getValue();
                    if ((privateField = dependantFields.get(SID_Coasting_Torque)) == null)
                        return Double.NaN;
                    double coastingTorque = privateField.getValue();

                    return requestedTorque - electricTorque - coastingTorque;
                }
            });
        }
    }

    private void addVirtualFieldElecBrakeTorque() {
        final String SID_ElecBrakeWheelsTorqueApplied = "1f8.28"; //10ms
        final String SID_Coasting_Torque = "18a.27";
        final String SID_PEBTorque = "77e.623025.24";

        if (MainActivity.altFieldsMode) {
            addVirtualFieldCommon("610a", "Nm", SID_PEBTorque, new VirtualFieldAction() {
                @Override
                public double updateValue(HashMap<String, Field> dependantFields) {
                    Field privateField;
                    if ((privateField = dependantFields.get(SID_PEBTorque)) == null)
                        return Double.NaN;
                    double electricTorque = privateField.getValue() * MainActivity.reduction;
                    return electricTorque <= 0 ? -electricTorque : 0;
                }
            });

        } else {
            addVirtualFieldCommon("610a", "Nm", SID_ElecBrakeWheelsTorqueApplied + ";" + SID_Coasting_Torque, new VirtualFieldAction() {
                @Override
                public double updateValue(HashMap<String, Field> dependantFields) {
                    Field privateField;
                    if ((privateField = dependantFields.get(SID_ElecBrakeWheelsTorqueApplied)) == null)
                        return Double.NaN;
                    double electricTorque = privateField.getValue();
                    if ((privateField = dependantFields.get(SID_Coasting_Torque)) == null)
                        return Double.NaN;
                    return electricTorque + (privateField.getValue() * MainActivity.reduction);
                }
            });
        }
    }


    private void addVirtualFieldTotalPositiveTorque() {
        final String SID_MeanEffectiveTorque = "186.16";
        final String SID_PEBTorque = "77e.623025.24";

        if (MainActivity.altFieldsMode) {
            addVirtualFieldCommon("610b", "Nm", SID_PEBTorque, new VirtualFieldAction() {
                @Override
                public double updateValue(HashMap<String, Field> dependantFields) {
                    Field privateField;
                    if ((privateField = dependantFields.get(SID_PEBTorque)) == null)
                        return Double.NaN;
                    double pebTorque = privateField.getValue();
                    return pebTorque >= 0 ? pebTorque * MainActivity.reduction : 0;
                }
            });

        } else {
            addVirtualFieldCommon("610b", "Nm", SID_MeanEffectiveTorque, new VirtualFieldAction() {
                @Override
                public double updateValue(HashMap<String, Field> dependantFields) {
                    Field privateField;
                    if ((privateField = dependantFields.get(SID_MeanEffectiveTorque)) == null)
                        return Double.NaN;
                    return privateField.getValue() * MainActivity.reduction;
                }
            });
        }
    }

    private void addVirtualFieldTotalNegativeTorque() {
        final String SID_DriverBrakeWheel_Torque_Request = "130.44"; //UBP braking wheel torque the driver wants
        final String SID_HydraulicTorqueRequest = "7bc.624b7d.28"; // Total Hydraulic brake wheels torque request
        final String SID_PEBTorque = "77e.623025.24";

        if (MainActivity.altFieldsMode) {
            addVirtualFieldCommon("610c", "Nm", SID_PEBTorque + ";" + SID_HydraulicTorqueRequest, new VirtualFieldAction() {
                @Override
                public double updateValue(HashMap<String, Field> dependantFields) {
                    Field privateField;
                    if ((privateField = dependantFields.get(SID_HydraulicTorqueRequest)) == null)
                        return Double.NaN;
                    double hydraulicTorqueRequest = privateField.getValue();
                    if ((privateField = dependantFields.get(SID_PEBTorque)) == null)
                        return Double.NaN;
                    double pebTorque = privateField.getValue();
                    return pebTorque <= 0 ? -hydraulicTorqueRequest - pebTorque * MainActivity.reduction : -hydraulicTorqueRequest;
                }
            });

        } else {
            addVirtualFieldCommon("610c", "Nm", SID_DriverBrakeWheel_Torque_Request, new VirtualFieldAction() {
                @Override
                public double updateValue(HashMap<String, Field> dependantFields) {
                    Field privateField;
                    if ((privateField = dependantFields.get(SID_DriverBrakeWheel_Torque_Request)) == null)
                        return Double.NaN;
                    return privateField.getValue();
                }
            });
        }
    }
/*
    private void addVirtualFieldFrictionPower() {
        final String SID_DriverBrakeWheel_Torque_Request = "130.44"; //UBP braking wheel torque the driver wants
        final String SID_ElecBrakeWheelsTorqueApplied = "1f8.28"; //10ms
        final String SID_ElecEngineRPM = "1f8.40"; //10ms

        addVirtualFieldCommon("6102", "kW", SID_DriverBrakeWheel_Torque_Request + ";" + SID_ElecBrakeWheelsTorqueApplied + ";" + SID_ElecEngineRPM, new VirtualFieldAction() {
            @Override
            public double updateValue(HashMap<String, Field> dependantFields) {
                Field privateField;
                if ((privateField = dependantFields.get(SID_DriverBrakeWheel_Torque_Request)) == null) return Double.NaN;
                double torque = privateField.getValue();
                if ((privateField = dependantFields.get(SID_ElecBrakeWheelsTorqueApplied)) == null) return Double.NaN;
                torque -= privateField.getValue();
                if ((privateField = dependantFields.get(SID_ElecEngineRPM)) == null) return Double.NaN;
                return (torque * privateField.getValue() / MainActivity.reduction);
                //return (dependantFields.get(SID_DriverBrakeWheel_Torque_Request).getValue() - dependantFields.get(SID_ElecBrakeWheelsTorqueApplied).getValue()) * dependantFields.get(SID_ElecEngineRPM).getValue() / MainActivity.reduction;
            }
        });
    }
*/
    private void addVirtualFieldDcPowerIn() {
        // positive = charging, negative = discharging. Unusable for consumption graphs
        final String SID_TractionBatteryVoltage = "7ec.623203.24";
        final String SID_TractionBatteryCurrent = "7ec.623204.24";

        addVirtualFieldCommon("6103", "kW", SID_TractionBatteryVoltage + ";" + SID_TractionBatteryCurrent, new VirtualFieldAction() {
            @Override
            public double updateValue(HashMap<String, Field> dependantFields) {
                Field privateField;
                if ((privateField = dependantFields.get(SID_TractionBatteryVoltage)) == null) return Double.NaN;
                double voltage = privateField.getValue();
                if ((privateField = dependantFields.get(SID_TractionBatteryCurrent)) == null) return Double.NaN;
                return (voltage * privateField.getValue() / 1000.0);
                //return dependantFields.get(SID_TractionBatteryVoltage).getValue() * dependantFields.get(SID_TractionBatteryCurrent).getValue() / 1000;
            }
        });
    }

    private void addVirtualFieldDcPowerOut() {
        // positive = discharging, negative = charging. Unusable for harging graphs
        final String SID_TractionBatteryVoltage = "7ec.623203.24";
        final String SID_TractionBatteryCurrent = "7ec.623204.24";

        addVirtualFieldCommon("6109", "kW", SID_TractionBatteryVoltage + ";" + SID_TractionBatteryCurrent, new VirtualFieldAction() {
            @Override
            public double updateValue(HashMap<String, Field> dependantFields) {
                Field privateField;
                if ((privateField = dependantFields.get(SID_TractionBatteryVoltage)) == null) return Double.NaN;
                double voltage = privateField.getValue();
                if ((privateField = dependantFields.get(SID_TractionBatteryCurrent)) == null) return Double.NaN;
                return (voltage * privateField.getValue() / -1000.0);
                //return dependantFields.get(SID_TractionBatteryVoltage).getValue() * dependantFields.get(SID_TractionBatteryCurrent).getValue() / 1000;
            }
        });
    }

    private void addVirtualFieldUsageLpf() {
        // It would be easier use SID_Consumption = "1fd.48" (dash kWh) instead of V*A
        // need to use real timer. Now the averaging is dependant on dongle speed
        final String SID_VirtualUsage = "800.6100.24";

        addVirtualFieldCommon("6104", "kWh/100km", SID_VirtualUsage, new VirtualFieldAction() {
            @Override
            public double updateValue(HashMap<String, Field> dependantFields) {
                Field privateField;
                if ((privateField = dependantFields.get(SID_VirtualUsage)) == null) return Double.NaN;
                double value = privateField.getValue();
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
        final String SID_HeaterSetpoint = "699.8";
        final String SID_OH_ClimTempDisplay = "764.6145.29";

        if (MainActivity.altFieldsMode) {
            addVirtualFieldCommon("6105", "°C", SID_OH_ClimTempDisplay, new VirtualFieldAction() {
                @Override
                public double updateValue(HashMap<String, Field> dependantFields) {
                    Field privateField;
                    if ((privateField = dependantFields.get(SID_OH_ClimTempDisplay)) == null) return Double.NaN;
                    double value = privateField.getValue() / 2;
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

        } else {
            addVirtualFieldCommon("6105", "°C", SID_HeaterSetpoint, new VirtualFieldAction() {
                @Override
                public double updateValue(HashMap<String, Field> dependantFields) {
                    Field privateField;
                    if ((privateField = dependantFields.get(SID_HeaterSetpoint)) == null) return Double.NaN;
                    double value = privateField.getValue();
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
    }

    private void addVirtualFieldRealRange() {
        final String SID_EVC_Odometer = "7ec.622006.24"; //  (EVC)
        final String SID_RangeEstimate = "654.42"; //  (EVC)

        if (Double.isNaN(realRangeReference))
            realRangeReference = CanzeDataSource.getInstance().getLast(SID_RangeEstimate);

        addVirtualFieldCommon("6106", "km", SID_EVC_Odometer + ";" + SID_RangeEstimate, new VirtualFieldAction() {
            @Override
            public double updateValue(HashMap<String, Field> dependantFields) {
                Field privateField;
                if ((privateField = dependantFields.get(SID_EVC_Odometer)) == null) return Double.NaN;
                double odo = privateField.getValue();
                if ((privateField = dependantFields.get(SID_RangeEstimate)) == null) return Double.NaN;
                double gom = privateField.getValue();

                // timestamp of last inserted dot in MILLISECONDS
                long lastInsertedTime = CanzeDataSource.getInstance().getLastTime(SID_RangeEstimate);
                if (    // timeout of 15 minutes
                        (Calendar.getInstance().getTimeInMillis() - lastInsertedTime > 15 * 60 * 1000)
                                ||
                                Double.isNaN(realRangeReference)
                ) {

                    if (!Double.isNaN(gom) && !Double.isNaN(odo)) {
                        realRangeReference = odo + gom;
                        realRangeReference2 = odo + gom;
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
        final String SID_EVC_Odometer = "7ec.622006.24"; //  (EVC)
        final String SID_RangeEstimate = "654.42"; //  (EVC)

        // get last value for realRange from internal database
        //MainActivity.debug("realRange 1: "+realRangeReference);
        if (Double.isNaN(realRangeReference)) {
            realRangeReference = CanzeDataSource.getInstance().getLast(SID_RangeEstimate);
            //MainActivity.debug("realRange >> getLast");
        }
        //MainActivity.debug("realRange 2: "+realRangeReference);

        addVirtualFieldCommon("6107", "km", SID_EVC_Odometer + ";" + SID_RangeEstimate, new VirtualFieldAction() {
            @Override
            public double updateValue(HashMap<String, Field> dependantFields) {
                Field privateField;
                if ((privateField = dependantFields.get(SID_EVC_Odometer)) == null) return Double.NaN;
                double odo = privateField.getValue();
                if ((privateField = dependantFields.get(SID_RangeEstimate)) == null) return Double.NaN;
                double gom = privateField.getValue();

                //MainActivity.debug("realRange ODO: "+odo);
                //MainActivity.debug("realRange GOM: "+gom);

                // timestamp of last inserted dot in MILLISECONDS
                long lastInsertedTime = CanzeDataSource.getInstance().getLastTime(SID_RangeEstimate);
                if (    // timeout of 15 minutes
                        (Calendar.getInstance().getTimeInMillis() - lastInsertedTime > 15 * 60 * 1000)
                                ||
                                Double.isNaN(realRangeReference)
                ) {
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
        final String SID_EVC_Odometer = "7ec.622006.24"; //  (EVC)
        final String SID_RangeEstimate = "654.42"; //  (EVC)

        // get last value for realRange from internal database
        //MainActivity.debug("realRange 1: "+realRangeReference);
        if (Double.isNaN(realRangeReference2)) {
            realRangeReference2 = CanzeDataSource.getInstance().getLast(SID_RangeEstimate);
            //MainActivity.debug("realRange >> getLast");
        }
        //MainActivity.debug("realRange 2: "+realRangeReference);

        addVirtualFieldCommon("6108", "km", SID_EVC_Odometer + ";" + SID_RangeEstimate, new VirtualFieldAction() {
            @Override
            public double updateValue(HashMap<String, Field> dependantFields) {
                Field privateField;
                if ((privateField = dependantFields.get(SID_EVC_Odometer)) == null) return Double.NaN;
                double odo = privateField.getValue();
                if ((privateField = dependantFields.get(SID_RangeEstimate)) == null) return Double.NaN;
                double gom = privateField.getValue();

                //MainActivity.debug("realRange ODO: "+odo);
                //MainActivity.debug("realRange GOM: "+gom);

                // timestamp of last inserted dot in MILLISECONDS
                long lastInsertedTime = CanzeDataSource.getInstance().getLastTime(SID_RangeEstimate);
                if (    // timeout of 15 minutes
                        (Calendar.getInstance().getTimeInMillis() - lastInsertedTime > 15 * 60 * 1000)
                                ||
                                Double.isNaN(realRangeReference2)
                ) {
                    if (!Double.isNaN(gom) && !Double.isNaN(odo)) {
                        realRangeReference2 = odo + gom;
                    }
                }
                if (Double.isNaN(realRangeReference2)) {
                    return Double.NaN;
                }
                double delta = realRangeReference2 - odo - gom;
                if (delta > 500.0 || delta < -500.0) {
                    realRangeReference2 = odo + gom;
                    delta = 0.0;
                }
                return delta;
            }
        });
    }


    private void addVirtualFieldPilotAmp() {
        final String SID_ACPilot = "42e.38";
        final String SID_ACPilotDutyCycle = "793.625026.24";

        if (MainActivity.altFieldsMode) {
            addVirtualFieldCommon("610d", "A", SID_ACPilotDutyCycle, new VirtualFieldAction() {
                @Override
                public double updateValue(HashMap<String, Field> dependantFields) {
                    Field privateField;
                    if ((privateField = dependantFields.get(SID_ACPilotDutyCycle)) == null)
                        return Double.NaN;
                    double dutyCycle = privateField.getValue();
                    return dutyCycle < 80.0 ? dutyCycle * 0.6 : (dutyCycle - 64.0) * 2.5;
                }
            });

        } else {
            addVirtualFieldCommon("610d", "A", SID_ACPilot, new VirtualFieldAction() {
                @Override
                public double updateValue(HashMap<String, Field> dependantFields) {
                    Field privateField;
                    if ((privateField = dependantFields.get(SID_ACPilot)) == null)
                        return Double.NaN;
                    return privateField.getValue();
                }
            });
        }
    }


    private class MyLocationListener implements LocationListener {
        /*---------- Listener class to get coordinates ------------- */
        // https://stackoverflow.com/questions/1513485/how-do-i-get-the-current-gps-location-programmatically-in-android

        @Override
        public void onLocationChanged(Location loc) {
            Field gpsField = getBySID("800.610e.24");
            if (gpsField != null) {
                gpsField.setValue(String.format(Locale.US, "%.6f/%.6f/%.1f", loc.getLatitude(), loc.getLongitude(), loc.getAltitude()));
                if (MainActivity.fieldLogMode)
                    FieldLogger.getInstance().log(gpsField.getDebugValue());
            }
        }

        @Override
        public void onProviderDisabled(String provider) {}

        @Override
        public void onProviderEnabled(String provider) {}

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {}
    }
    private void addVirtualFieldGps() {
        if (locationManager == null) {
            locationManager = (LocationManager) MainActivity.getInstance().getBaseContext().getSystemService(Context.LOCATION_SERVICE);
            locationListener = new MyLocationListener();
        }
        Field gpsField = getBySID("800.610e.24");
        if (gpsField == null) {
            Frame frame = Frames.getInstance().getById(0x800);
            gpsField = new Field("", frame, (short) 24, (short) 31, 1, 0, 0, "coord", "610e", (short) 0xaff, "GPS", "");
            add(gpsField);
        }
    }
    private void virtualFieldPropelGps (boolean startStop) {
        if (locationManager == null) return;
        // yes I know, this is the brute force approach...
        try {
            if (startStop) {
                if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ) {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 10, locationListener);
                } else {
                    MainActivity.toast(MainActivity.TOAST_NONE, "Can't start location. Please switch on location services");
                }
            } else {
                locationManager.removeUpdates (locationListener);
            }
        } catch (SecurityException e) {
            MainActivity.toast(MainActivity.TOAST_NONE, "Can't start location. Please give CanZE location permission");
        }
    }

    private void addVirtualFieldCommon(String virtualId, String unit, String dependantSids, VirtualFieldAction virtualFieldAction) {
        // create a list of field this new virtual field will depend on
        HashMap<String, Field> dependantFields = new HashMap<>();
        boolean allOk = true;
        for (String sid : dependantSids.split(";")) {
            Field field = getBySID(sid);
            if (field != null) {
                dependantFields.put(sid, field);
            } else {
                MainActivity.toast(MainActivity.TOAST_NONE,  String.format(Locale.getDefault(),MainActivity.getStringSingle(R.string.format_NoSid), "Fields", sid));
                allOk = false;
            }
        }
        if (allOk) {
            VirtualField virtualField = new VirtualField(virtualId, dependantFields, unit, virtualFieldAction);
            // a virtualfield is always ISO-TP, so we need to create a subframe for it
            Frame frame = Frames.getInstance().getById(0x800);
            Frame subFrame = Frames.getInstance().getById(0x800, virtualField.getResponseId());
            if (subFrame == null) {
                subFrame = new Frame(frame.getFromId(), frame.getInterval(), frame.getSendingEcu(), virtualField.getResponseId(), frame);
                Frames.getInstance().add(subFrame);
            }
            subFrame.addField(virtualField);
            virtualField.setFrame(subFrame);
            // add it to the list of fields
            add(virtualField);
        }
    }

    private void fillOneLine(String line) {
        if (line.contains("#")) line = line.substring(0, line.indexOf('#'));
        String[] tokens = line.split(",");
        if (tokens.length > FIELD_OPTIONS) {
            int frameId = Integer.parseInt(tokens[FIELD_ID].trim(), 16);
            Frame frame = Frames.getInstance().getById(frameId);
            if (frame == null) {
                MainActivity.debug("frame does not exist:" + tokens[FIELD_ID].trim());
            } else {
                if (frameId < 0x800 || frameId > 0x8ff) {
                    short options = Short.parseShort(tokens[FIELD_OPTIONS].trim(), 16);
                    // ensure this field matches the selected car
                    if ((options & MainActivity.car) != 0 && (!tokens[FIELD_RESPONSE_ID].trim().startsWith("7") || tokens[FIELD_RESPONSE_ID].trim().toLowerCase().startsWith("7e"))) {
                        //Create a new field object and fill his  data
                        //MainActivity.debug(tokens[FIELD_SID] + " " + tokens[FIELD_ID] + "." + tokens[FIELD_FROM] + "." + tokens[FIELD_RESPONSE_ID]);
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
                                    subFrame = new Frame(frame.getFromId(), frame.getInterval(), frame.getSendingEcu(), field.getResponseId(), frame);
                                    Frames.getInstance().add(subFrame);
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
                } else {
                    addVirtualField(tokens[FIELD_RESPONSE_ID].trim());
                }
            }
        }

    }

    private void fillFromFile(String filename) {
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(filename));
            // removed checking for non null bufferedReader, as file not found will thow an Exception
            //if (bufferedReader == null) {
            //    MainActivity.toast(MainActivity.TOAST_NONE, "Can't access file " + filename);
            //    return;
            //}
            String line;
            while ((line = bufferedReader.readLine()) != null)
                fillOneLine(line);
            bufferedReader.close();
        } catch (IOException e ) {
            MainActivity.toast(MainActivity.TOAST_NONE, "Can't access file " + filename);
        }
    }

    private void fillFromAsset(String assetName) {
        //Read text from asset
        AssetLoadHelper assetLoadHelper = new AssetLoadHelper(MainActivity.getInstance());

        BufferedReader bufferedReader = assetLoadHelper.getBufferedReaderFromAsset(assetName);
        if (bufferedReader == null) {
            MainActivity.toast(MainActivity.TOAST_NONE, "Can't access asset " + assetName);
            return;
        }
        try {
            String line;
            while ((line = bufferedReader.readLine()) != null)
                fillOneLine(line);
            bufferedReader.close();
        } catch (IOException e) {
            MainActivity.logExceptionToCrashlytics(e);
        }
    }

    public void load() {
        load("");
    }


    public void load(String assetName) {
        fields.clear();
        fieldsBySid.clear();

        // if settings were not done, no fields would be loaded from file, and then the system will
        // complain because it can't get register the virtual fields because it cannot find the
        // required dependantfields.
        if (MainActivity.device == null) return;

        if (assetName.equals("")) {
            fillFromAsset(getDefaultAssetName());
            addVirtualFields();
        } else if (assetName.startsWith("/")) {
            fillFromFile(assetName);
        } else {
            fillFromAsset(assetName);
            //if (assetName.startsWith("VFC")) {
            //    addVirtualFields();
            //}
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
        if (index < 0 || index >= fields.size()) return null;
        // avoid a rare outofbounds crash when MainActivity.onCreate is reloading values from the
        // SQL while another thread is loading the fields.
        try {
            return fields.get(index);
        } catch (IndexOutOfBoundsException e) {
            return null;
        }
    }

    public Object[] toArray() {
        return fields.toArray();
    }


    public void add(Field field) {
        fields.add(field);
        fieldsBySid.put(field.getSID(), field);
        //fieldsBySid.put(field.getCar()+"."+field.getSID(),field);
    }

    public void clearAllFields() {
        for (int i = 0; i < fields.size(); i++) {
            Field f = fields.get(i);
            if(f!=null)
                fields.get(i).setValue(0);
        }
    }

    public ArrayList<Field> getAllFields() {
        return fields;
    }

    private String getDefaultAssetName() {
        // note - we might ditch non-alt mode. I doubt if it's worth the effort for CanSee dongle only
        if (MainActivity.isZOE()) {
            if (MainActivity.altFieldsMode) {
                return "_FieldsAlt.csv";
            } else {
                return "_Fields.csv";
            }
        } else if (MainActivity.isZOEZE50())
            return "_FieldsZE50.csv";
        return "_Fields.csv";
    }

}
