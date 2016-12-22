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

package lu.fisch.canze.actors;

/**
 * Created by jeroen on 4-12-16.
 */

public class EcuDiagEPS {

    // static final public String fieldsString () {
    void load () {

        String fieldDef = // ID (hex), startBit, endBit, resolution, offset (aplied BEFORE resolution multiplication), decimals, unit, requestID (hex string), responseID (hex string),
                // options (hex, see MainActivity for definitions), optional name, optional list
                ""

                        +"762,16,23,1,0,0,,2192,6192,ff,APV.ECU production date\n" //
                        +"762,32,39,1,0,0,,2182,6182,ff,BUS OFF parameter\n" //
                        +"762,40,47,1,0,0,,2182,6182,ff,Mute Module parameter\n" //
                        +"762,16,23,1,0,0,,2182,6182,ff,MUX.messaging reference\n" //
                        +"762,56,63,1,0,0,,2182,6182,ff,MUX.Producer ABS/ESP absent parameter\n" //
                        +"762,184,191,1,0,0,,2182,6182,ff,MUX.Producer DAE absent parameter\n" //
                        +"762,48,55,1,0,0,,2182,6182,ff,MUX.Producer ECM Absent parameter\n" //
                        +"762,64,71,1,0,0,,2182,6182,ff,MUX.Producer TdB absent parameter\n" //
                        +"762,72,79,1,0,0,,2182,6182,ff,MUX.Producer UCH absent parameter\n" //
                        +"762,192,199,1,0,0,,2182,6182,ff,MUX.Producers 19 -> 25 absent parameter\n" //
                        +"762,80,87,1,0,0,,2182,6182,ff,MUX.Producers 5 -> 17 absent parameter\n" //
                        +"762,16,23,1,0,0,,2184,6184,ff,APV.Traceability Information\n" //
                        +"762,152,159,1,0,0,,2181,6181,ff,APV.CRC VIN\n" //
                        +"762,16,23,1,0,0,,2181,6181,ff,APV.VIN\n" //
                        +"762,16,23,1,0,0,,1904000000FF,5904000000FF,ff,DTCRecord\n" //
                        +"762,40,47,1,0,0,,1904000000FF,5904000000FF,ff,StatusOfDTC\n" //
                        +"762,16,23,1,0,0,,1906000000FF,5906000000FF,ff,DTCRecord\n" //
                        +"762,40,47,1,0,0,,1906000000FF,5906000000FF,ff,StatusOfDTC\n" //
                        +"762,56,79,1,0,0,km,1906000000FF,5906000000FF,ff,DTCExtendedData.Mileage\n" //
                        +"762,48,55,1,0,0,,1906000000FF,5906000000FF,ff,DTCExtendedDataRecordNumber,128:Mileage;129:AgingCounter;130:DTCOccurrenceCounter\n" //
                        +"762,16,23,1,0,0,,190600000080,590600000080,ff,DTCRecord\n" //
                        +"762,40,47,1,0,0,,190600000080,590600000080,ff,StatusOfDTC\n" //
                        +"762,56,79,1,0,0,km,190600000080,590600000080,ff,DTCExtendedData.Mileage\n" //
                        +"762,48,55,1,0,0,,190600000080,590600000080,ff,DTCExtendedDataRecordNumber,128:Mileage;129:AgingCounter;130:DTCOccurrenceCounter\n" //
                        +"762,48,48,1,0,0,,1902FF,5902FF,ff,DTCStatus.warningIndicatorRequested,0:No;1:Yes\n" //
                        +"762,55,55,1,0,0,,1902FF,5902FF,ff,DTCStatus.testFailed,0:No;1:Yes\n" //
                        +"762,54,54,1,0,0,,1902FF,5902FF,ff,DTCStatus.testFailedThisMonitoringCycle,0:No;1:Yes\n" //
                        +"762,53,53,1,0,0,,1902FF,5902FF,ff,DTCStatus.pendingDTC,0:No;1:Yes\n" //
                        +"762,52,52,1,0,0,,1902FF,5902FF,ff,DTCStatus.confirmedDTC,0:No;1:Yes\n" //
                        +"762,51,51,1,0,0,,1902FF,5902FF,ff,DTCStatus.testNotCompletedSinceLastClear,0:No;1:Yes\n" //
                        +"762,50,50,1,0,0,,1902FF,5902FF,ff,DTCStatus.testFailedSinceLastClear,0:No;1:Yes\n" //
                        +"762,49,49,1,0,0,,1902FF,5902FF,ff,DTCStatus.testNotCompletedThisMonitoringCycle,0:No;1:Yes\n" //
                        +"762,40,47,1,0,0,,1902FF,5902FF,ff,DTCFailureType,0:Device and failure type ODB codding;1:General Electrical Failure;2:General signal failure;3:FM (Frequency Modulated) / PWM (Pulse Width Modulated) Failures;4:System Internal Failures;5:System Programming Failures;6:Algorithm Based Failures;7:Mechanical Failures;8:Bus Signal / Message Failures;9:Component Failures;17:circuit short to ground;18:circuit short to battery;19:circuit open;20:circuit short to ground or open;21:circuit short to battery or open;22:circuit voltage below threshold;23:circuit voltage above threshold;24:circuit current below threshold;25:circuit current above threshold;26:circuit resistance below threshold;27:circuit resistance above threshold;28:circuit voltage out of range;29:circuit current out of range;30:circuit resistance out of range;31:circuit intermittent;33:signal amplitude < minimum;34:signal amplitude > maximum;35:signal stuck low;36:signal stuck high;37:signal shape / waveform failure;38:signal rate of change below threshold;39:signal rate of change above threshold;40:signal bias level out of range / zero adjustment failure;41:signal signal invalid;47:signal erratic;49:no signal;50:signal low time < minimum;51:signal low time > maximum;52:signal high time < minimum;53:signal high time > maximum;54:signal frequency too low;55:signal frequency too high;56:signal frequency incorrect;57:incorrect has too few pulses;58:incorrect has too many pulses;65:general checksum failure;66:general memory failure;67:special memory failure;68:data memory failure;69:program memory failure;70:calibration / parameter memory failure;71:watchdog / safety µC failure;72:supervision software failure;73:internal electronic failure;74:incorrect component installed;75:over temperature;81:not programmed;82:not activated;83:deactivated;84:missing calibration;85:not configured;97:signal calculation failure;98:signal compare failure;99:circuit / component protection time-out;100:signal plausibility failure;101:signal has too few transitions / events;102:signal has too many transitions / events;103:signal incorrect after event;104:event information;113:actuator stuck;114:actuator stuck open;115:actuator stuck closed;116:actuator slipping;117:emergency position not reachable;118:wrong mounting position;119:commanded position not reachable;120:alignment or adjustment incorrect;121:mechanical linkage failure;122:fluid leak or seal failure;123:low fluid level;129:invalid serial data received;130:alive / sequence counter incorrect / not updated;131:value of signal protection calculation incorrect;132:signal below allowable range;133:signal above allowable range;134:signal invalid;135:missing message;136:bus off;143:erratic;145:parametric;146:performance or incorrect operation;147:no operation;148:unexpected operation;149:incorrect assembly;150:component internal failure;151:Component or system operation obstructed or blocked;152:component or system over temperature\n" //
                        +"762,24,39,1,0,0,,1902FF,5902FF,ff,DTCDeviceAndFailureTypeOBD\n" //
                        +"762,44,47,1,0,0,,1902FF,5902FF,ff,DTCFailureType.ManufacturerOrSupplier\n" //
                        +"762,16,23,1,0,0,,1902FF,5902FF,ff,DTCStatusAvailabilityMask\n" //
                        +"762,8,23,24414,0,0,%,2303FF8CC00003,6303FF8CC00003,ff,NSK.usm_FadeGain\n" //
                        +"762,8,23,24414,0,0,%,2303FF8CC80003,6303FF8CC80003,ff,NSK.ssm_CurGain\n" //
                        +"762,8,23,3418,0,0,Nm,2303FF8C6C0003,6303FF8C6C0003,ff,NSK.ssm_ExtendTrq\n" //
                        +"762,160,167,1,0,0,,2180,6180,ff,CalibrationNumber\n" //
                        +"762,56,63,1,0,0,,2180,6180,ff,DiagnosticIdentificationCode\n" //
                        +"762,128,135,1,0,0,,2180,6180,ff,SoftwareNumber\n" //
                        +"762,64,71,1,0,0,,2180,6180,ff,SupplierNumber.ITG\n" //
                        +"762,144,151,1,0,0,,2180,6180,ff,EditionNumber\n" //
                        +"762,176,183,1,0,0,,2180,6180,ff,PartNumber.BasicPartList,0:N/A;2:28504\n" //
                        +"762,184,191,1,0,0,,2180,6180,ff,HardwareNumber.BasicPartList,0:HARDW;1:28500\n" //
                        +"762,192,199,1,0,0,,2180,6180,ff,ApprovalNumber.BasicPartList,0:N/A\n" //
                        +"762,16,23,1,0,0,,2180,6180,ff,PartNumber.LowerPart\n" //
                        +"762,88,95,1,0,0,,2180,6180,ff,HardwareNumber.LowerPart\n" //
                        +"762,200,207,1,0,0,,2180,6180,ff,ManufacturerIdentificationCode,0:Renault R1;128:Nissan N1;129:Nissan N2;130:Nissan N3;136:5DIGITS;255:Renault R3\n" //
                        +"762,8,15,2,0,0,kph,2303FF94E70003,6303FF94E70003,ff,NSK.b_speed\n" //
                        +"762,24,31,1,0,0,,220139,620139,ff,DID - Angle sensor index status,0:Turn not detected;1:Turn detected\n" //
                        +"762,24,31,1,0,0,,220102,620102,ff,DID - Angle sensor internal status.DID - SWA status.Internal status\n" //
                        +"762,29,29,1,0,0,,220102,620102,ff,DID - Angle sensor internal status.DID - SWA status.Calibration status,0:Not calibrated;1:Calibrated\n" //
                        +"762,30,30,1,0,0,,220102,620102,ff,DID - Angle sensor internal status.DID - SWA status.Failure,0:No failure;1:Failure\n" //
                        +"762,24,31,1,0,0,,22015B,62015B,ff,DID - Angle sensor mode,0:Sensorless;1:Real sensor\n" //
                        +"762,24,31,1,0,0,%,220115,620115,ff,DID - Assistance level\n" //
                        +"762,24,39,1,0,0,Nm,22011F,62011F,ff,DID - Assistance torque demand\n" //
                        +"762,24,31,1,0,0,%,220160,620160,ff,DID - Derating assistance level\n" //
                        +"762,24,31,1,0,0,,22015E,62015E,ff,DID - Derating Status,0:No derating;1:Derating active\n" //
                        +"762,24,31,1,0,0,,220500,620500,ff,DID - DiagMuxON,0:Disabled;1:Enabled\n" //
                        +"762,24,31,1,0,0,,221024,621024,ff,DID - Tuning number\n" //
                        +"762,24,31,1,0,0,,220167,620167,ff,DID - WheelSpeedSensorsCfg\n" //
                        +"762,24,31,1,0,0,,220168,620168,ff,DID - WheelSpeedIndexMapSelector,0:Default;1:MAP 1;2:MAP 2;3:MAP 3;4:MAP 4;5:MAP 5;6:MAP 6;7:MAP 7;8:MAP 8;9:MAP 9;10:MAP 10;11:MAP 11;12:MAP 12\n" //
                        +"762,24,31,1,0,0,,220162,620162,ff,DID - Tuning Number (selected)\n" //
                        +"762,24,31,1,0,0,,22015C,62015C,ff,DID - Tuning version number\n" //
                        +"762,24,39,1,0,0,km/h,220204,620204,ff,DID - Vehicle speed (CAN)\n" //
                        +"762,24,31,1,0,0,km/h,220116,620116,ff,DID - Vehicle speed (filtered)\n" //
                        +"762,24,31,1,0,0,,220119,620119,ff,DID - System mode,1:System stop;2:Power-up tests;3:Ramp-up Normal operation;4:Very soft ramp-down;5:Soft ramp-down;6:Wait for shutdown\n" //
                        +"762,24,39,1,0,0,V,22012F,62012F,ff,DID - Supply voltage\n" //
                        +"762,24,31,1,0,0,,220163,620163,ff,DID - Supplier fault code\n" //
                        +"762,24,39,1,0,0,°/s,220101,620101,ff,DID - Steering velocity\n" //
                        +"762,24,31,1,0,0,°,220103,620103,ff,DID - Steering angle offset\n" //
                        +"762,24,39,1,0,0,°,220104,620104,ff,DID - Steering angle\n" //
                        +"762,24,31,2,0,0,°C,22012D,62012D,ff,DID - Motor temperature\n" //
                        +"762,24,31,1,0,0,A,220113,620113,ff,DID - Motor current demand\n" //
                        +"762,24,31,1,0,0,A,22012C,62012C,ff,DID - Motor current\n" //
                        +"762,24,31,1,0,0,,220161,620161,ff,DID - Internal fault store (Supplier)\n" //
                        +"762,24,31,1,0,0,,220131,620131,ff,DID - Ignition status,0:OFF;1:ON\n" //
                        +"762,24,31,1,0,0,,22011E,62011E,ff,DID - IGN counter\n" //
                        +"762,24,31,1,0,0,,22011A,62011A,ff,DID - Fault Indicator,0:No failure;1:Minor failure;2:Major failure\n" //
                        +"762,24,31,1,0,0,,220501,620501,ff,DID - Engine Status (CAN),0:Stopped;1:Reserved;2:Running;3:Driven\n" //
                        +"762,24,31,2,0,0,°C,22012E,62012E,ff,DID - ECU temperature\n" //
                        +"762,24,39,1,0,0,Nm,220112,620112,ff,DID - Driver Torque\n" //
                        +"762,24,31,1,0,0,,220164,620164,ff,DID - Dongle state,1:Operational learnt;2:Operational blank;3:Not operational\n" //
                        +"762,24,31,1,0,0,,220165,620165,ff,DID - Dongle pairing counter\n" //
                        +"762,24,31,1,0,0,,220166,620166,ff,DID - Dongle error,0:No error;1:EEPROM problem on Dongle_ID;2:EEPROM problem on Dongle_pairing_counter;3:Motor disconnected from ECU\n" //
                        +"762,24,31,1,0,0,,22012A,62012A,ff,DID - RES On_Off,0:Disable (default);1:Enable\n" //
                        +"762,24,31,1,0,0,,22F187,62F187,ff,DID - Spare part number\n" //
                        +"762,8,23,1,0,0,°,2303FF8B9C0002,6303FF8B9C0002,ff,NSK.angle_estimation_wodb1\n" //
                        +"762,24,31,1,0,0,,22015D,62015D,ff,TuningParameter\n" //
                ;

        Frames.getInstance().load ("762,0,0,EPS\n");
        Fields.getInstance().load (fieldDef);
    }
}
