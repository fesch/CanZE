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
