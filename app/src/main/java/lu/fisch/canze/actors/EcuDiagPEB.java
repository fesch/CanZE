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

    // static final public String fieldsString () {
    void load () {

        String fieldDef = // ID (hex), startBit, endBit, resolution, offset (aplied BEFORE resolution multiplication), decimals, unit, requestID (hex string), responseID (hex string),
                // options (hex, see MainActivity for definitions), optional name, optional list
                ""

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
                        +"77e,24,31,1,0,0,,22F180,62F180,ff,bootSoftwareIdentification\n" //
                        +"77e,31,31,1,0,0,,223001,623001,ff,VerlogStatus,0:Vehicle Not Locked;1:Vehicle Locked\n" //
                        +"77e,24,31,1,0,0,,223002,623002,ff,Error angle\n" //
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
                        +"77e,160,167,1,0,0,,2180,6180,ff,CalibrationNumber\n" //
                        +"77e,56,63,1,0,0,,2180,6180,ff,DiagnosticIdentificationCode\n" //
                        +"77e,144,151,1,0,0,,2180,6180,ff,EditionNumber\n" //
                        +"77e,184,191,1,0,0,,2180,6180,ff,HardwareNumber.BasicPartList,1:291A5\n" //
                        +"77e,88,95,1,0,0,,2180,6180,ff,HardwareNumber.LowerPart\n" //
                        +"77e,200,207,1,0,0,,2180,6180,ff,ManufacturerIdentificationCode,136:5DIGITS R2;255:Renault R3;0:Renault R1;128:Nissan N1;129:Nissan N2;130:Nissan N3\n" //
                        +"77e,176,183,1,0,0,,2180,6180,ff,PartNumber.BasicPartList,1:291A5\n" //
                        +"77e,16,23,1,0,0,,2180,6180,ff,PartNumber.LowerPart\n" //
                        +"77e,128,135,1,0,0,,2180,6180,ff,SoftwareNumber\n" //
                        +"77e,64,71,1,0,0,,2180,6180,ff,SupplierNumber.ITG\n" //
                        +"77e,16,23,1,0,0,,2184,6184,ff,REF SUPPLIER\n" //
                        +"77e,40,47,1,0,0,,2184,6184,ff,REFHW (to be coded on 10 bytes)\n" //
                        +"77e,120,127,1,0,0,,2184,6184,ff,DATE(YEAR + Day Number)\n" //
                        +"77e,152,159,1,0,0,,2181,6181,ff,Vin CRC\n" //
                        +"77e,16,23,1,0,0,,2181,6181,ff,Vin In Ascii\n" //
                        +"77e,24,31,1,0,0,,22F18E,62F18E,ff,VehicleManufacturerKitAssemblyPartNumber\n" //
                        +"77e,24,39,1,0,0,°C,223017,623017,ff,DCDC Trafo Temp\n" //
                        +"77e,16,23,1,0,0,,21B7,61B7,ff,Configuration of Electrical Vehicle Networks\n" //
                        +"77e,16,23,1,0,0,,21B8,61B8,ff,List of Electrical Vehicle ECUs with After-sales diagnostic\n" //
                        +"77e,24,31,1,0,0,,223037,623037,ff,R20 Rotor resistor value\n" //
                        +"77e,31,31,1,0,0,,223039,623039,ff,Effective Charge feedback to EVC,0:Not in charge;1:In charge\n" //
                        +"77e,31,31,1,0,0,,22303A,62303A,ff,Distribution type from JB,0:Default;1:Simultaneous\n" //
                        +"77e,24,31,1,0,0,,22303C,62303C,ff,CPLD DCDC\n" //
                        +"77e,24,31,1,0,0,,22303D,62303D,ff,CPLD INVERTER\n" //
                        +"77e,30,31,1,0,0,,22303B,62303B,ff,Failure class of the charge,0:No default;1:Downgraded mode;2:SW temporary shutdown;3:SW definitive shutdown\n" //
                        +"77e,24,31,1,0,0,,223040,623040,ff,Raw debug data encoding the cause of SPT fail\n" //
                        +"77e,24,31,1,0,0,,22303F,62303F,ff,ID of last detected FAM raising the DTC\n" //
                        +"77e,24,31,1,0,0,,22303E,62303E,ff,ID of first detected FAM raising the DTC\n" //
                        +"77e,24,31,1,0,0,,223038,623038,ff,Charge request from EVC to PEB,0:No charge requested;1:Charge Requested\n" //
                ;

        Frames.getInstance().load ("77E,0,0,PEB\n");
        Fields.getInstance().load (fieldDef);
    }
}
