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

        String fieldDef1 =
                ""
                        +"762,0,7,1,0,0,,14ffff,54,ff\n" // Reset DTC
                        +"762,0,23,1,0,0,,1902ff,5902ff,ff\n" // Query DTC

                        +"762,16,47,1,0,0,,2192,6192,ff,APV.ECU production date\n" //
                        +"762,32,39,1,0,0,,2182,6182,ff,BUS OFF parameter\n" //
                        +"762,40,47,1,0,0,,2182,6182,ff,Mute Module parameter\n" //
                        +"762,16,31,1,0,0,,2182,6182,ff,MUX.messaging reference\n" //
                        +"762,56,63,1,0,0,,2182,6182,ff,MUX.Producer ABS/ESP absent parameter\n" //
                        +"762,184,191,1,0,0,,2182,6182,ff,MUX.Producer DAE absent parameter\n" //
                        +"762,48,55,1,0,0,,2182,6182,ff,MUX.Producer ECM Absent parameter\n" //
                        +"762,64,71,1,0,0,,2182,6182,ff,MUX.Producer TdB absent parameter\n" //
                        +"762,72,79,1,0,0,,2182,6182,ff,MUX.Producer UCH absent parameter\n" //
                        +"762,192,247,1,0,0,,2182,6182,ff,MUX.Producers 19 -> 25 absent parameter\n" //
                        +"762,80,183,1,0,0,,2182,6182,ff,MUX.Producers 5 -> 17 absent parameter\n" //
                        +"762,16,175,1,0,0,,2184,6184,2ff,APV.Traceability Information\n" //
                        +"762,152,167,1,0,0,,2181,6181,ff,APV.CRC VIN\n" //
                        +"762,16,151,1,0,0,,2181,6181,2ff,APV.VIN\n" //
                        +"762,8,23,24414,0,0,%,2303FF8CC00003,6303FF8CC00003,ff,NSK.usm_FadeGain\n" //
                        +"762,8,23,24414,0,0,%,2303FF8CC80003,6303FF8CC80003,ff,NSK.ssm_CurGain\n" //
                        +"762,8,23,3418,0,0,Nm,2303FF8C6C0003,6303FF8C6C0003,ff,NSK.ssm_ExtendTrq\n" //
                        +"762,160,175,1,0,0,,2180,6180,ff,CalibrationNumber\n" //
                        +"762,56,63,1,0,0,,2180,6180,ff,DiagnosticIdentificationCode\n" //
                        +"762,128,143,1,0,0,,2180,6180,ff,SoftwareNumber\n" //
                        +"762,64,87,1,0,0,,2180,6180,2ff,SupplierNumber.ITG\n" //
                        +"762,144,159,1,0,0,,2180,6180,ff,EditionNumber\n" //
                        +"762,176,183,1,0,0,,2180,6180,ff,PartNumber.BasicPartList,0:N/A;2:28504\n" //
                        +"762,184,191,1,0,0,,2180,6180,ff,HardwareNumber.BasicPartList,0:HARDW;1:28500\n" //
                        +"762,192,199,1,0,0,,2180,6180,ff,ApprovalNumber.BasicPartList,0:N/A\n" //
                        +"762,16,55,1,0,0,,2180,6180,2ff,PartNumber.LowerPart\n" //
                        +"762,88,127,1,0,0,,2180,6180,2ff,HardwareNumber.LowerPart\n" //
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
                        +"762,24,119,1,0,0,,22015C,62015C,ff,DID - Tuning version number\n" //
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
                        +"762,24,103,1,0,0,,220161,620161,ff,DID - Internal fault store (Supplier)\n" //
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
                        +"762,24,103,1,0,0,,22F187,62F187,2ff,DID - Spare part number\n" //
                        +"762,8,23,1,0,0,°,2303FF8B9C0002,6303FF8B9C0002,ff,NSK.angle_estimation_wodb1\n" //
                        +"762,24,11975,1,0,0,,22015D,62015D,ff,TuningParameter\n" //

                ;

        String dtcDef =
                ""

                        +"D000,CAN bus fault (EPS mute)\n" //
                        +"C100,CAN Engine Status fault\n" //
                        +"5605,EPS fault - Angle sensor\n" //
                        +"5608,EPS fault - ECU\n" //
                        +"5606,EPS fault - Motor\n" //
                        +"5604,EPS fault - Torque Sensor\n" //
                        +"5614,EPS Thermal Protection\n" //
                        +"5613,IGNITION signal fault\n" //
                        +"5612,No Angle calibration\n" //
                        +"F003,Battery voltage fault\n" //
                        +"C129,CAN ABS Absent fault\n" //
                        +"C418,CAN ABS Invalid data fault\n" //
                        +"D001,CAN wheelspeed fault\n" //
                        +"5603,EPS fault - Adaptative straight ahead offset\n" //

                ;

        String testDef =
                ""

                        +"00,Device and failure type ODB codding\n" //
                        +"01,General Electrical Failure\n" //
                        +"02,General signal failure\n" //
                        +"03,FM (Frequency Modulated) / PWM (Pulse Width Modulated) Failures\n" //
                        +"04,System Internal Failures\n" //
                        +"05,System Programming Failures\n" //
                        +"06,Algorithm Based Failures\n" //
                        +"07,Mechanical Failures\n" //
                        +"08,Bus Signal / Message Failures\n" //
                        +"09,Component Failures\n" //
                        +"11,circuit short to ground\n" //
                        +"12,circuit short to battery\n" //
                        +"13,circuit open\n" //
                        +"14,circuit short to ground or open\n" //
                        +"15,circuit short to battery or open\n" //
                        +"16,circuit voltage below threshold\n" //
                        +"17,circuit voltage above threshold\n" //
                        +"18,circuit current below threshold\n" //
                        +"19,circuit current above threshold\n" //
                        +"1A,circuit resistance below threshold\n" //
                        +"1B,circuit resistance above threshold\n" //
                        +"1C,circuit voltage out of range\n" //
                        +"1D,circuit current out of range\n" //
                        +"1E,circuit resistance out of range\n" //
                        +"1F,circuit intermittent\n" //
                        +"21,signal amplitude < minimum\n" //
                        +"22,signal amplitude > maximum\n" //
                        +"23,signal stuck low\n" //
                        +"24,signal stuck high\n" //
                        +"25,signal shape / waveform failure\n" //
                        +"26,signal rate of change below threshold\n" //
                        +"27,signal rate of change above threshold\n" //
                        +"28,signal bias level out of range / zero adjustment failure\n" //
                        +"29,signal signal invalid\n" //
                        +"2F,signal erratic\n" //
                        +"31,no signal\n" //
                        +"32,signal low time < minimum\n" //
                        +"33,signal low time > maximum\n" //
                        +"34,signal high time < minimum\n" //
                        +"35,signal high time > maximum\n" //
                        +"36,signal frequency too low\n" //
                        +"37,signal frequency too high\n" //
                        +"38,signal frequency incorrect\n" //
                        +"39,incorrect has too few pulses\n" //
                        +"3A,incorrect has too many pulses\n" //
                        +"41,general checksum failure\n" //
                        +"42,general memory failure\n" //
                        +"43,special memory failure\n" //
                        +"44,data memory failure\n" //
                        +"45,program memory failure\n" //
                        +"46,calibration / parameter memory failure\n" //
                        +"47,watchdog / safety µC failure\n" //
                        +"48,supervision software failure\n" //
                        +"49,internal electronic failure\n" //
                        +"4A,incorrect component installed\n" //
                        +"4B,over temperature\n" //
                        +"51,not programmed\n" //
                        +"52,not activated\n" //
                        +"53,deactivated\n" //
                        +"54,missing calibration\n" //
                        +"55,not configured\n" //
                        +"61,signal calculation failure\n" //
                        +"62,signal compare failure\n" //
                        +"63,circuit / component protection time-out\n" //
                        +"64,signal plausibility failure\n" //
                        +"65,signal has too few transitions / events\n" //
                        +"66,signal has too many transitions / events\n" //
                        +"67,signal incorrect after event\n" //
                        +"68,event information\n" //
                        +"71,actuator stuck\n" //
                        +"72,actuator stuck open\n" //
                        +"73,actuator stuck closed\n" //
                        +"74,actuator slipping\n" //
                        +"75,emergency position not reachable\n" //
                        +"76,wrong mounting position\n" //
                        +"77,commanded position not reachable\n" //
                        +"78,alignment or adjustment incorrect\n" //
                        +"79,mechanical linkage failure\n" //
                        +"7A,fluid leak or seal failure\n" //
                        +"7B,low fluid level\n" //
                        +"81,invalid serial data received\n" //
                        +"82,alive / sequence counter incorrect / not updated\n" //
                        +"83,value of signal protection calculation incorrect\n" //
                        +"84,signal below allowable range\n" //
                        +"85,signal above allowable range\n" //
                        +"86,signal invalid\n" //
                        +"87,missing message\n" //
                        +"88,bus off\n" //
                        +"8F,erratic\n" //
                        +"91,parametric\n" //
                        +"92,performance or incorrect operation\n" //
                        +"93,no operation\n" //
                        +"94,unexpected operation\n" //
                        +"95,incorrect assembly\n" //
                        +"96,component internal failure\n" //
                        +"97,Component or system operation obstructed or blocked\n" //
                        +"98,component or system over temperature\n" //

                ;

        Frames.getInstance().load("762,0,0,EPS\n");
        Fields.getInstance().load(fieldDef1);
        Dtcs.getInstance().load(dtcDef, testDef);
    }
}