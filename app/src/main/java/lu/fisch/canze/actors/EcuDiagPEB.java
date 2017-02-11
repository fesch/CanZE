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

public class EcuDiagPEB {

    void load () {

        String fieldDef1 =
                ""
                        +"77e,0,7,1,0,0,,14ffff,54,ff\n" // Reset DTC
                        +"77e,0,23,1,0,0,,1902ff,5902ff,ff\n" // Query DTC

                        +"77e,28,51,1,0,0,km,223008,623008,ff,DistanceTotalizer\n" //
                        +"77e,24,39,1,0,0,V,223009,623009,ff,BatteryVoltage\n" //
                        +"77e,24,39,1,0,0,V,22300A,62300A,ff,Batt_voltage_mes\n" //
                        +"77e,31,31,1,0,0,,22300B,62300B,ff,APCStatus,0:Ignition is OFF;1:Ignition is ON\n" //
                        +"77e,31,31,1,0,0,,22300C,62300C,ff,Volt_APCPEB_veh_status,0:Ignition is OFF;1:Ignition is ON\n" //
                        +"77e,24,39,1,0,0,V,22300D,62300D,ff,Onboard Power Supply\n" //
                        +"77e,24,39,1,0,0,V,22300E,62300E,ff,DCHighVoltage\n" //
                        +"77e,30,31,1,0,0,,22300F,62300F,ff,DCDCState,0:Starting mode  or Power off;1:Standby(ready to power = preload);2:Power On ;3:Unavailable value\n" //
                        +"77e,31,31,1,0,0,,223010,623010,ff,DCDCActivation,0:DCDC Off;1:DCDC On\n" //
                        +"77e,24,39,1,0,0,V,223011,623011,ff,DCDCVoltageRegulation\n" //
                        +"77e,24,39,1,0,0,V,223012,623012,ff,DCDCLowVoltage\n" //
                        +"77e,24,39,1,0,0,A,223013,623013,ff,DCDCCurrentOutput\n" //
                        +"77e,24,39,1,0,0,A,223015,623015,ff,DCDCCurrentInput\n" //
                        +"77e,24,31,1,0,0,%,223019,623019,ff,DCDCTemp\n" //
                        +"77e,29,31,1,0,0,,22301A,62301A,ff,DCDCDefault,0:No failure;1:Downgraded mode;2:SW temporary Shutdown;3:SW definitive Shutdown\n" //
                        +"77e,31,31,1,0,0,,22301B,62301B,ff,PEBDCDCServLampRequest,0:No security barrier lost;1:Security barrier lost\n" //
                        +"77e,31,31,1,0,0,,22301C,62301C,ff,InverterActivation,0:Inverter Off;1:Inverter On\n" //
                        +"77e,24,39,1,0,0,A,22301D,62301D,ff,PowerTrainCurrent\n" //
                        +"77e,24,39,1,0,0,,22301E,62301E,ff,Current Ph U\n" //
                        +"77e,24,39,1,0,0,,22301F,62301F,ff,Current Ph V\n" //
                        +"77e,24,39,1,0,0,,223021,623021,ff,Current Ph W\n" //
                        +"77e,24,39,1,0,0,A,223022,623022,ff,Current Excit Pos\n" //
                        +"77e,24,39,1,0,0,A,223023,623023,ff,Current Excit Neg\n" //
                        +"77e,24,39,1,0,0,Nm,223024,623024,ff,ElecTorqueRequest\n" //
                        +"77e,24,39,1,0,0,Nm,223025,623025,ff,PEBTorque\n" //
                        +"77e,24,39,1,0,0,Nm,223026,623026,ff,ElecMachineMaxMotorTorque\n" //
                        +"77e,24,39,1,0,0,Nm,223027,623027,ff,ElecMachineMaxGenTorque\n" //
                        +"77e,24,39,1,0,0,rpm,223028,623028,ff,ElecMachineSpeed\n" //
                        +"77e,24,31,1,0,0,%,223029,623029,ff,InverterTemp\n" //
                        +"77e,24,31,1,0,0,%,22302A,62302A,ff,ElecMachineTemp\n" //
                        +"77e,24,39,1,0,0,°C,22302B,62302B,ff,InverterTempOrder\n" //
                        +"77e,24,39,1,0,0,°C,223036,623036,ff,Excitation temperature\n" //
                        +"77e,24,39,1,0,0,°C,22302C,62302C,ff,Stator temperature\n" //
                        +"77e,24,39,1,0,0,°C,223035,623035,ff,Rotor temperature\n" //
                        +"77e,31,31,1,0,0,,22302D,62302D,ff,B_PebDischarge_sch_req,0:Discharge not requested;1:Discharge requested\n" //
                        +"77e,24,31,1,0,0,,22302E,62302E,ff,PEBActiveDischarge,0:Active Discharge Not In Progress;1:Active Discharge In Progress;255:Unavailable Value\n" //
                        +"77e,31,31,1,0,0,,22302F,62302F,ff,ElecMAchineWorkingMode,0:Motor mode;1:Generator mode\n" //
                        +"77e,30,31,1,0,0,,223030,623030,ff,ElecMachineDefault,0:No default;1:Downgraded mode;2:Temporary fault;3:Permanent fault\n" //
                        +"77e,31,31,1,0,0,,223031,623031,ff,PEBMotorServLampRequest,0:No security barrier lost;1:Security barrier lost\n" //
                        +"77e,31,31,1,0,0,,223032,623032,ff,PEBMotorStopLampRequest,0:No security barrier lost;1:Security barrier lost\n" //
                        +"77e,24,39,1,0,0,V,223034,623034,ff,Rotor PositionSensor Supply\n" //
                        +"77e,24,63,1,0,0,,22F180,62F180,ff,bootSoftwareIdentification\n" //
                        +"77e,31,31,1,0,0,,223001,623001,ff,VerlogStatus,0:Vehicle Not Locked;1:Vehicle Locked\n" //
                        +"77e,24,47,1,0,0,,223002,623002,ff,Error angle\n" //
                        +"77e,31,31,1,0,0,,223003,623003,ff,PEBExcitationInterlockState,0:Interlock Open;1:Interlock Closed\n" //
                        +"77e,31,31,1,0,0,,223007,623007,ff,InterlockBatt,0:Interlock Open;1:Interlock Closed\n" //
                        +"77e,31,31,1,0,0,,222070,622070,ff,Immobilizer - diagnosis availability,0:unavailable;1:available\n" //
                        +"77e,24,31,1,0,0,-,222071,622071,ff,Immobilizer - Byte 1 used to allow diagnosis\n" //
                        +"77e,24,31,1,0,0,-,222072,622072,ff,Immobilizer - Byte 2 used to allow diagnosis\n" //
                        +"77e,24,31,1,0,0,-,222073,622073,ff,Immobilizer - Byte 3 used to allow diagnosis\n" //
                        +"77e,31,31,1,0,0,,222074,622074,ff,Immobilizer - engine not running due to ECM,0:no;1:yes\n" //
                        +"77e,31,31,1,0,0,,222075,622075,ff,Immobilizer - engine not running due to BCM in secure mode,0:no;1:yes\n" //
                        +"77e,31,31,1,0,0,,222076,622076,ff,Immobilizer - engine not running due to no BCM authorization,0:no;1:yes\n" //
                        +"77e,31,31,1,0,0,,222077,622077,ff,Immobilizer - engine not running due to a CAN network problem with the BCM,0:no;1:yes\n" //
                        +"77e,31,31,1,0,0,,223004,623004,ff,PEBSpiderBoxInterlockState,0:Interlock Open;1:Interlock Closed\n" //
                        +"77e,31,31,1,0,0,,223006,623006,ff,Interlock EM,0:Interlock Open;1:Interlock Closed\n" //
                        +"77e,24,39,1,0,0,,223016,623016,ff,DCDC_PEB_Driver_board_mes\n" //
                        +"77e,24,39,1,0,0,,223018,623018,ff,DCDCTempOrder\n" //
                        +"77e,192,199,1,0,0,,2180,6180,ff,ApprovalNumber.BasicPartList\n" //
                        +"77e,160,175,1,0,0,,2180,6180,ff,CalibrationNumber\n" //
                        +"77e,56,63,1,0,0,,2180,6180,ff,DiagnosticIdentificationCode\n" //
                        +"77e,144,159,1,0,0,,2180,6180,ff,EditionNumber\n" //
                        +"77e,184,191,1,0,0,,2180,6180,ff,HardwareNumber.BasicPartList,1:291A5\n" //
                        +"77e,88,127,1,0,0,,2180,6180,2ff,HardwareNumber.LowerPart\n" //
                        +"77e,200,207,1,0,0,,2180,6180,ff,ManufacturerIdentificationCode,136:5DIGITS R2;255:Renault R3;0:Renault R1;128:Nissan N1;129:Nissan N2;130:Nissan N3\n" //
                        +"77e,176,183,1,0,0,,2180,6180,ff,PartNumber.BasicPartList,1:291A5\n" //
                        +"77e,16,55,1,0,0,,2180,6180,2ff,PartNumber.LowerPart\n" //
                        +"77e,128,143,1,0,0,,2180,6180,ff,SoftwareNumber\n" //
                        +"77e,64,87,1,0,0,,2180,6180,2ff,SupplierNumber.ITG\n" //
                        +"77e,16,39,1,0,0,,2184,6184,2ff,REF SUPPLIER\n" //
                        +"77e,40,119,1,0,0,,2184,6184,2ff,REFHW (to be coded on 10 bytes)\n" //
                        +"77e,120,175,1,0,0,,2184,6184,2ff,DATE(YEAR + Day Number)\n" //
                        +"77e,152,167,1,0,0,,2181,6181,ff,Vin CRC\n" //
                        +"77e,16,151,1,0,0,,2181,6181,2ff,Vin In Ascii\n" //
                        +"77e,24,103,1,0,0,,22F18E,62F18E,2ff,VehicleManufacturerKitAssemblyPartNumber\n" //
                        +"77e,24,39,1,0,0,°C,223017,623017,ff,DCDC Trafo Temp\n" //
                        +"77e,16,103,1,0,0,,21B7,61B7,ff,Configuration of Electrical Vehicle Networks\n" //
                        +"77e,16,103,1,0,0,,21B8,61B8,ff,List of Electrical Vehicle ECUs with After-sales diagnostic\n" //
                        +"77e,24,47,1,0,0,,223037,623037,ff,R20 Rotor resistor value\n" //
                        +"77e,31,31,1,0,0,,223039,623039,ff,Effective Charge feedback to EVC,0:Not in charge;1:In charge\n" //
                        +"77e,31,31,1,0,0,,22303A,62303A,ff,Distribution type from JB,0:Default;1:Simultaneous\n" //
                        +"77e,24,39,1,0,0,,22303C,62303C,ff,CPLD DCDC\n" //
                        +"77e,24,39,1,0,0,,22303D,62303D,ff,CPLD INVERTER\n" //
                        +"77e,30,31,1,0,0,,22303B,62303B,ff,Failure class of the charge,0:No default;1:Downgraded mode;2:SW temporary shutdown;3:SW definitive shutdown\n" //
                        +"77e,24,103,1,0,0,,223040,623040,ff,Raw debug data encoding the cause of SPT fail\n" //
                        +"77e,24,39,1,0,0,,22303F,62303F,ff,ID of last detected FAM raising the DTC\n" //
                        +"77e,24,39,1,0,0,,22303E,62303E,ff,ID of first detected FAM raising the DTC\n" //
                        +"77e,24,31,1,0,0,,223038,623038,ff,Charge request from EVC to PEB,0:No charge requested;1:Charge Requested\n" //

                ;

        String dtcDef =
                ""

                        +"1000,PEB Ctrl Board\n" //
                        +"1001,PEB Ctrl Board ADC\n" //
                        +"1002,PEB Ctrl Board ATIC81\n" //
                        +"1003,PEB Ctrl Board microprocessor\n" //
                        +"1004,Internal voltage sensor on HV side\n" //
                        +"1006,Interlock electrical circuit\n" //
                        +"1007,PEB HV DC link discharge failure\n" //
                        +"1009,14V supply wire (KL30)\n" //
                        +"100A,DCDC internal Power module Temp sensor\n" //
                        +"100B,DCDC internal Driver Board Temp sensor\n" //
                        +"100C,DCDC current sensor LV side\n" //
                        +"100D,DCDC Sigma delta voltage sensor 14V side\n" //
                        +"100E,DCDC internal MOS+drivers\n" //
                        +"100F,DCDC internal FPGA_CPLD component\n" //
                        +"1010,14V_DCDC wire\n" //
                        +"1011,HV_DCDC wire\n" //
                        +"1012,DCDC Temp\n" //
                        +"1013,INV internal temp sensor\n" //
                        +"1014,INV phase U internal current sensor\n" //
                        +"1015,INV phase V internal current sensor\n" //
                        +"1016,INV phase W internal current sensor\n" //
                        +"1018,INV internal FPGA_CPLD component\n" //
                        +"1019,HV_Inverter wire\n" //
                        +"101A,INV_EM Iphase\n" //
                        +"101B,INV_EM Phase U wire\n" //
                        +"101C,INV_EM Phase V wire\n" //
                        +"101D,INV_EM Phase W wire\n" //
                        +"101E,INV+EXCIT Temp\n" //
                        +"101F,EXCIT Temp sensor\n" //
                        +"1020,EXCIT +current  sensor\n" //
                        +"1021,EXCIT -current  sensor\n" //
                        +"1023,EM Iexcit signal\n" //
                        +"1024,APC by wire\n" //
                        +"1025,EM_SIN signal\n" //
                        +"1026,EM_COS signal\n" //
                        +"1027,EM_SIN_COS signals\n" //
                        +"1028,EM position sensor Supply signal\n" //
                        +"1029,Excitation current sensors\n" //
                        +"102A,EM_TEMP signal\n" //
                        +"102B,EMR Interlock electrical circuit\n" //
                        +"102C,EM Temp\n" //
                        +"102E,Spiderbox Interlock electrical circuit\n" //
                        +"102F,Excitation Interlock electrical circuit\n" //
                        +"1031,Switch power test procedure\n" //
                        +"1032,Software parametrization\n" //
                        +"1033,DCDC Trafo temp sensor\n" //
                        +"1034,DCDC Power Supply\n" //
                        +"1036,Program flow\n" //
                        +"1037,DCDC Failure Class C\n" //
                        +"1038,Inverter+ Excitation Failure Class C\n" //
                        +"1039,PEB Failure Class C\n" //
                        +"103A,Inverter Power Supply\n" //
                        +"103E,DCDC ADC voltage sensor 14V side\n" //
                        +"1040,DCDC voltage sensors 14V side\n" //
                        +"1041,Writing of rotor resistance\n" //
                        +"1042,Writing of the position sensor\n" //
                        +"1043,OV flags Data Received by CAN\n" //
                        +"1044,Torque request deviation\n" //
                        +"D000,CAN communication\n" //
                        +"D001,Communication with EVC\n" //
                        +"D002,Torque request from EVC\n" //
                        +"D004,Communication with LBC\n" //
                        +"D003,14V Data from USM\n" //
                        +"1045,Incoherent requests from EVC\n" //
                        +"D005,Communication in charge mode\n" //
                        +"1046,EM positive overspeed\n" //
                        +"1047,EM negative overspeed\n" //
                        +"1048,Wrong direction of the wheels\n" //
                        +"1049,PWM signal\n" //
                        +"1050,CPLD reprogramming\n" //
                        +"1052,PEB Failure Class B with safety alert\n" //

                ;

        String testDef =
                ""

                        +"00,Device and failure type ODB codding\n" //
                        +"01,General Electrical Failure\n" //
                        +"02,General signal failure\n" //
                        +"03,FM / PWM Failures\n" //
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
                        +"28,signal bias level / zero adjustment failure\n" //
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
                        +"97,Component or system operation obstructed/blocked\n" //
                        +"98,component or system over temperature\n" //

                ;

        Frames.getInstance().load("77E,0,0,PEB\n");
        Fields.getInstance().load(fieldDef1);
        Dtcs.getInstance().load(dtcDef, testDef);
    }
}