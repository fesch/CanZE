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

public class EcuDiagUBP {

    void load () {

        String fieldDef1 =
                ""
                        + "7bc,0,7,1,0,0,,14ffff,54,ff\n" // Reset DTC
                        + "7bc,0,23,1,0,0,,1902ff,5902ff,ff\n" // Query DTC

                        +"7bc,16,23,1,0,0,,2184,6184,ff,ECU traceability.calendarweek\n" //
                        +"7bc,24,31,1,0,0,,2184,6184,ff,ECU traceability.year\n" //
                        +"7bc,32,34,1,0,0,,2184,6184,ff,ECU traceability.plant,0:Blaichach;3:Moulins\n" //
                        +"7bc,35,37,1,0,0,,2184,6184,ff,ECU traceability.line,0:Line n°1;1:Line n°2;2:Line n°3;3:Line n°4;4:Line n°5;5:Line n°6;6:Line n°7;7:Line n°8\n" //
                        +"7bc,38,39,1,0,0,,2184,6184,ff,ECU traceability.shift,0:Shift n°1;1:Shift n°2;2:Shift n°3;3:Shift n°4\n" //
                        +"7bc,40,42,1,0,0,,2184,6184,ff,ECU traceability.day,0:forbidden - interdit;1:monday - lundi;2:tuesday - mardi;3:wednesday - mercredi;4:thursday - jeudi;5:friday - vendredi;6:saturday - samedi;7:sunday - dimanche\n" //
                        +"7bc,43,55,1,0,0,,2184,6184,ff,ECU traceability.counter\n" //
                        +"7bc,16,55,1,0,0,,21F0,61F0,2ff,PartNumber.LowerPart\n" //
                        +"7bc,56,63,1,0,0,,21F0,61F0,ff,DiagnosticIdentificationCode\n" //
                        +"7bc,64,87,1,0,0,,21F0,61F0,2ff,SupplierNumber.ITG\n" //
                        +"7bc,88,127,1,0,0,,21F0,61F0,2ff,HardwareNumber.LowerPart\n" //
                        +"7bc,128,143,1,0,0,,21F0,61F0,ff,SoftwareNumber\n" //
                        +"7bc,144,159,1,0,0,,21F0,61F0,ff,EditionNumber\n" //
                        +"7bc,160,175,1,0,0,,21F0,61F0,ff,CalibrationNumber\n" //
                        +"7bc,176,183,1,0,0,,21F0,61F0,ff,PartNumber.BasicPartList,0:N/A;1:47210;2:47208\n" //
                        +"7bc,184,191,1,0,0,,21F0,61F0,ff,HardwareNumber.BasicPartList,0:HARDW;1:47213\n" //
                        +"7bc,192,199,1,0,0,,21F0,61F0,ff,ApprovalNumber.BasicPartList,0:N/A\n" //
                        +"7bc,200,207,1,0,0,,21F0,61F0,ff,ManufacturerIdentificationCode\n" //
                        +"7bc,16,55,1,0,0,,21F1,61F1,2ff,ApprovalNumber.LowerPart\n" //
                        +"7bc,56,95,1,0,0,,21F1,61F1,2ff,Programming site reference\n" //
                        +"7bc,96,135,1,0,0,,21F1,61F1,2ff,Programming tool reference\n" //
                        +"7bc,136,143,1,0,0,,21F1,61F1,ff,Number of reprogrammings\n" //
                        +"7bc,144,167,1,0,0,,21F1,61F1,ff,Date of reprogramming\n" //
                        +"7bc,168,183,1,0,0,,21F1,61F1,ff,Time of reprogramming\n" //
                        +"7bc,184,191,1,0,0,,21F1,61F1,ff,Record marking\n" //
                        +"7bc,192,207,1,0,0,,21F1,61F1,ff,CRC of log record\n" //
                        +"7bc,16,55,1,0,0,,21FE,61FE,2ff,PartNumber.LowerPart\n" //
                        +"7bc,56,63,1,0,0,,21FE,61FE,ff,DiagnosticIdentificationCode\n" //
                        +"7bc,64,87,1,0,0,,21FE,61FE,2ff,SupplierNumber.ITG\n" //
                        +"7bc,88,127,1,0,0,,21FE,61FE,2ff,HardwareNumber.LowerPart\n" //
                        +"7bc,128,143,1,0,0,,21FE,61FE,ff,SoftwareNumber\n" //
                        +"7bc,144,159,1,0,0,,21FE,61FE,ff,EditionNumber\n" //
                        +"7bc,160,175,1,0,0,,21FE,61FE,ff,CalibrationNumber\n" //
                        +"7bc,176,183,1,0,0,,21FE,61FE,ff,PartNumber.BasicPartList,0:N/A;1:47210;2:47208\n" //
                        +"7bc,184,191,1,0,0,,21FE,61FE,ff,HardwareNumber.BasicPartList,0:HARDW;1:47213\n" //
                        +"7bc,192,199,1,0,0,,21FE,61FE,ff,ApprovalNumber.BasicPartList,0:N/A\n" //
                        +"7bc,200,207,1,0,0,,21FE,61FE,ff,ManufacturerIdentificationCode\n" //
                        +"7bc,16,55,1,0,0,,21FF,61FF,2ff,ApprovalNumber.LowerPart\n" //
                        +"7bc,56,95,1,0,0,,21FF,61FF,2ff,Programming site reference\n" //
                        +"7bc,96,135,1,0,0,,21FF,61FF,2ff,Programming tool reference\n" //
                        +"7bc,136,143,1,0,0,,21FF,61FF,ff,Number of reprogrammings\n" //
                        +"7bc,144,167,1,0,0,,21FF,61FF,ff,Date of reprogramming\n" //
                        +"7bc,168,183,1,0,0,,21FF,61FF,ff,Time of reprogramming\n" //
                        +"7bc,184,191,1,0,0,,21FF,61FF,ff,Record marking\n" //
                        +"7bc,192,207,1,0,0,,21FF,61FF,ff,CRC of log record\n" //
                        +"7bc,24,31,.1,0,0,V,22012F,62012F,ff,Supply voltage\n" //
                        +"7bc,24,39,.01,0,0,km/h,224B00,624B00,ff,WheelSpeed FL\n" //
                        +"7bc,24,39,.01,0,0,km/h,224B01,624B01,ff,WheelSpeed FR\n" //
                        +"7bc,24,39,.01,0,0,km/h,224B02,624B02,ff,WheelSpeed RL\n" //
                        +"7bc,24,39,.01,0,0,km/h,224B03,624B03,ff,WheelSpeed RR\n" //
                        +"7bc,24,39,.01,0,0,km/h,224B04,624B04,ff,Vehicle speed\n" //
                        +"7bc,24,31,1,0,0,,224B70,624B70,ff,Filling status,0:To be Defined\n" //
                        +"7bc,24,31,.2,127,0,°,224B36,624B36,ff,Steering wheel angle offset\n" //
                        +"7bc,24,39,1,0,0,,224B3D,624B3D,ff,Supplier fault code\n" //
                        +"7bc,24,31,1,0,0,bar,224B73,624B73,ff,Master cylinder pressure\n" //
                        +"7bc,24,31,1,0,0,,224B18,624B18,ff,After sales date.Day\n" //
                        +"7bc,32,39,1,0,0,,224B18,624B18,ff,After sales date.Month\n" //
                        +"7bc,40,47,1,0,0,,224B18,624B18,ff,After sales date.Year\n" //
                        +"7bc,24,31,1,0,0,,224B1A,624B1A,ff,Vehicle status\n" //
                        +"7bc,24,39,1,0,0,mm,224B24,624B24,ff,Tires real circumference\n" //
                        +"7bc,24,31,10,-1,0,s,224B25,624B25,ff,Powerlatch duration\n" //
                        +"7bc,24,31,.2,127,0,Bar,224B74,624B74,ff,Master cylinder pressure offset\n" //
                        +"7bc,24,39,.1,32767,0,°,220100,620100,ff,Steering wheel angle\n" //
                        +"7bc,24,39,.1,32767,0,°/s,224B42,624B42,ff,Steering wheel speed\n" //
                        +"7bc,24,39,1,0,0,,224B44,624B44,ff,List of supplier internal faults\n" //
                        +"7bc,24,103,1,0,0,,22F18E,62F18E,2ff,Vehicle manufacturer spare part number\n" //
                        +"7bc,16,151,1,0,0,,2181,6181,2ff,Vehicle identification number\n" //
                        +"7bc,152,167,1,0,0,,2181,6181,ff,Vehicle identification number CRC\n" //
                        +"7bc,28,55,10,0,0,m,224B9B,624B9B,ff,Vehicle odometer.Odometer\n" //
                        +"7bc,24,27,1,0,0,,224B9B,624B9B,ff,Vehicle odometer.Reserved,0:reserved\n" //
                        +"7bc,24,31,10,-1,0,s,224B9A,624B9A,ff,Brake Powerlatch duration\n" //
                        +"7bc,24,31,1,0,0,bar,224B75,624B75,ff,Accumulator pressure\n" //
                        +"7bc,24,31,1,0,0,bar,224B71,624B71,ff,Sense piston pressure\n" //
                        +"7bc,24,31,.2,127,0,bar,224B72,624B72,ff,Sense piston pressure offset\n" //
                        +"7bc,24,31,1,0,0,,224B80,624B80,ff,HBB function inhibition state,0:Activated by diag;170:Deactivated by diag;255:Not implemented\n" //
                        +"7bc,24,31,1,0,0,,224B81,624B81,ff,EB function inhibition state,0:Activated by diag;170:Deactivated by diag;255:Not implemented\n" //
                        +"7bc,24,31,1,0,0,mm,224B77,624B77,ff,Brake pedal position 1\n" //
                        +"7bc,24,31,.2,77,0,mm,224B78,624B78,ff,Brake pedal position 1 offset\n" //
                        +"7bc,24,31,1,0,0,mm,224B79,624B79,ff,Brake pedal position 2\n" //
                        +"7bc,24,31,.2,77,0,mm,224B7A,624B7A,ff,Brake pedal position 2 offset\n" //
                        +"7bc,29,29,1,0,0,,224B90,624B90,ff,UBP Function status.PUMP function.activation status,0:not active;1:active\n" //
                        +"7bc,28,28,1,0,0,,224B90,624B90,ff,UBP Function status.PUMP function.failure status,0:not operational;1:operational\n" //
                        +"7bc,27,27,1,0,0,,224B90,624B90,ff,UBP Function status.EB function.activation status,0:not active;1:active\n" //
                        +"7bc,26,26,1,0,0,,224B90,624B90,ff,UBP Function status.EB function.failure status,0:not operational;1:operational\n" //
                        +"7bc,25,25,1,0,0,,224B90,624B90,ff,UBP Function status.HBB function.activation status,0:not active;1:active\n" //
                        +"7bc,24,24,1,0,0,,224B90,624B90,ff,UBP Function status.HBB function.failure status,0:not operational;1:operational\n" //
                        +"7bc,30,30,1,0,0,,224B90,624B90,ff,UBP Function status.Mode roller bench function.activation status,0:not active;1:active\n" //
                        +"7bc,33,33,1,0,0,,224B90,624B90,ff,UBP Function status.HBA function.activation status,0:not active;1:active\n" //
                        +"7bc,31,31,1,0,0,,224B90,624B90,ff,UBP Function status.Advanced diagnosis function.activation status,0:not active;1:active\n" //
                        +"7bc,32,32,1,0,0,,224B90,624B90,ff,UBP Function status.HBA function.failure status,0:not operational;1:operational\n" //
                        +"7bc,34,39,1,0,0,,224B90,624B90,ff,UBP Function status.reserved,0:reserved\n" //
                        +"7bc,28,39,1,4094,0,N·m,224B7B,624B7B,ff,Electric brake wheels torque applied\n" //
                        +"7bc,24,31,1,0,0,,224B84,624B84,ff,PUMP function inhibition state,0:Activated by diag;170:Deactivated by diag;255:Not implemented\n" //
                        +"7bc,24,39,.01,0,0,km/h,224B8A,624B8A,ff,Vehicle speed Delayed\n" //
                        +"7bc,24,31,1,0,0,bar,224B95,624B95,ff,Sense piston pressure Delayed\n" //
                        +"7bc,24,31,128,0,0,bar,224B96,624B96,ff,Master cylinder pressure Delayed\n" //
                        +"7bc,24,31,1,0,0,bar,224B97,624B97,ff,Accumulator pressure Delayed\n" //
                        +"7bc,24,31,1,0,0,mm,224B98,624B98,ff,Brake pedal position 1 Delayed\n" //
                        +"7bc,24,31,1,0,0,mm,224B99,624B99,ff,Brake pedal position 2 Delayed\n" //
                        +"7bc,28,39,1,4094,0,N·m,224B7C,624B7C,ff,Electric brake wheels torque request\n" //
                        +"7bc,28,39,1,4094,0,N·m,224B9D,624B9D,ff,Electric brake wheels torque request Delayed\n" //
                        +"7bc,28,39,1,4094,0,N·m,224B7D,624B7D,ff,Total Hydraulic brake wheels torque request\n" //
                        +"7bc,28,39,1,4094,0,N·m,224B9E,624B9E,ff,Total Hydraulic brake wheels torque request Delayed\n" //
                        +"7bc,28,39,1,4094,0,N·m,224B7E,624B7E,ff,Driver brake wheels torque request\n" //
                        +"7bc,28,39,1,4094,0,N·m,224B9C,624B9C,ff,Electric brake wheels torque applied Delayed\n" //
                        +"7bc,28,39,1,4094,0,N·m,224B9F,624B9F,ff,Driver brake wheels torque request Delayed\n" //
                        +"7bc,24,47,1,0,0,W/h,224B92,624B92,ff,Gain Elec kWh per km on gaz pedal released.ElecCurrent\n" //
                        +"7bc,48,63,1,0,0,km,224B92,624B92,ff,Gain Elec kWh per km on gaz pedal released.OdometerCounter\n" //
                        +"7bc,64,87,1,0,0,W/h,224B92,624B92,ff,Gain Elec kWh per km on gaz pedal released.ElecN\n" //
                        +"7bc,88,111,1,0,0,km,224B92,624B92,ff,Gain Elec kWh per km on gaz pedal released.OdometerN\n" //
                        +"7bc,112,135,1,0,0,W/h,224B92,624B92,ff,Gain Elec kWh per km on gaz pedal released.ElecN1\n" //
                        +"7bc,136,159,1,0,0,km,224B92,624B92,ff,Gain Elec kWh per km on gaz pedal released.OdometerN1\n" //
                        +"7bc,160,183,1,0,0,W/h,224B92,624B92,ff,Gain Elec kWh per km on gaz pedal released.ElecN2\n" //
                        +"7bc,184,207,1,0,0,km,224B92,624B92,ff,Gain Elec kWh per km on gaz pedal released.OdometerN2\n" //
                        +"7bc,24,47,1,0,0,W/h,224B91,624B91,ff,Gain Elec kWh per km on brake pedal pressed.ElecCurrent\n" //
                        +"7bc,48,63,1,0,0,km,224B91,624B91,ff,Gain Elec kWh per km on brake pedal pressed.OdometerCounter\n" //
                        +"7bc,64,87,1,0,0,W/h,224B91,624B91,ff,Gain Elec kWh per km on brake pedal pressed.ElecN\n" //
                        +"7bc,88,111,1,0,0,km,224B91,624B91,ff,Gain Elec kWh per km on brake pedal pressed.OdometerN\n" //
                        +"7bc,112,135,1,0,0,W/h,224B91,624B91,ff,Gain Elec kWh per km on brake pedal pressed.ElecN1\n" //
                        +"7bc,136,159,1,0,0,km,224B91,624B91,ff,Gain Elec kWh per km on brake pedal pressed.OdometerN1\n" //
                        +"7bc,160,183,1,0,0,W/h,224B91,624B91,ff,Gain Elec kWh per km on brake pedal pressed.ElecN2\n" //
                        +"7bc,184,207,1,0,0,km,224B91,624B91,ff,Gain Elec kWh per km on brake pedal pressed.OdometerN2\n" //
                        +"7bc,24,31,1,0,0,,224B85,624B85,ff,Mode roller bench function activation state,0:Deactivated by diag;170:Activated by diag\n" //
                        +"7bc,28,55,10,0,0,m,224B09,624B09,ff,Odometer of the first occurence.Odometer\n" //
                        +"7bc,24,27,1,0,0,,224B09,624B09,ff,Odometer of the first occurence.Reserved,0:reserved\n" //
                        +"7bc,24,31,.2,127,0,bar,224B76,624B76,ff,Accumulator pressure offset\n" //
                        +"7bc,24,31,1,0,0,,224B7F,624B7F,ff,HBA function inhibition state,0:Activated by diag;170:Deactivated by diag;255:Not implemented\n" //
                        +"7bc,24,31,1,0,0,,224B86,624B86,ff,Advanced diagnosis function activation state,0:Deactivated by diag;170:Activated by diag\n" //
                        +"7bc,24,727,1,0,0,,224B82,624B82,ff,Advanced diagnostic datas memorized buffer1\n" //
                        +"7bc,24,727,1,0,0,,224B8F,624B8F,ff,Advanced diagnostic datas memorized buffer10\n" //
                        +"7bc,24,727,1,0,0,,224B83,624B83,ff,Advanced diagnostic datas memorized buffer2\n" //
                        +"7bc,24,727,1,0,0,,224B87,624B87,ff,Advanced diagnostic datas memorized buffer3\n" //
                        +"7bc,24,727,1,0,0,,224B88,624B88,ff,Advanced diagnostic datas memorized buffer4\n" //
                        +"7bc,24,727,1,0,0,,224B89,624B89,ff,Advanced diagnostic datas memorized buffer5\n" //
                        +"7bc,24,727,1,0,0,,224B8B,624B8B,ff,Advanced diagnostic datas memorized buffer6\n" //
                        +"7bc,24,727,1,0,0,,224B8C,624B8C,ff,Advanced diagnostic datas memorized buffer7\n" //
                        +"7bc,24,727,1,0,0,,224B8D,624B8D,ff,Advanced diagnostic datas memorized buffer8\n" //
                        +"7bc,24,727,1,0,0,,224B8E,624B8E,ff,Advanced diagnostic datas memorized buffer9\n" //
                        +"7bc,16,55,1,0,0,,2180,6180,2ff,PartNumber.LowerPart\n" //
                        +"7bc,56,63,1,0,0,,2180,6180,ff,DiagnosticIdentificationCode\n" //
                        +"7bc,64,87,1,0,0,,2180,6180,2ff,SupplierNumber.ITG\n" //
                        +"7bc,88,127,1,0,0,,2180,6180,2ff,HardwareNumber.LowerPart\n" //
                        +"7bc,128,143,1,0,0,,2180,6180,ff,SoftwareNumber\n" //
                        +"7bc,144,159,1,0,0,,2180,6180,ff,EditionNumber\n" //
                        +"7bc,160,175,1,0,0,,2180,6180,ff,CalibrationNumber\n" //
                        +"7bc,176,183,1,0,0,,2180,6180,ff,PartNumber.BasicPartList,0:N/A;1:47210;2:47208\n" //
                        +"7bc,184,191,1,0,0,,2180,6180,ff,HardwareNumber.BasicPartList,0:HARDW;1:47213\n" //
                        +"7bc,192,199,1,0,0,,2180,6180,ff,ApprovalNumber.BasicPartList,0:N/A\n" //
                        +"7bc,200,207,1,0,0,,2180,6180,ff,ManufacturerIdentificationCode\n" //
                        +"7bc,24,39,1,0,0,min,224B93,624B93,ff,Running Pump Cumulated\n" //

                ;

        String dtcDef =
                ""

                        +"C140,Lost Communication with BCM\n" //
                        +"C422,Invalid Data Received From BCM\n" //
                        +"F003,Supply voltage\n" //
                        +"4051,Steering wheel angle sensor\n" //
                        +"5154,Invalid wheel circumference parameter\n" //
                        +"5177,Important function deactivated\n" //
                        +"C001,Vehicle CAN - no transmission\n" //
                        +"C073,Vehicle CAN - Busoff\n" //
                        +"C131,Lost Communication with EPS\n" //
                        +"C420,Invalid Data Received From EPS\n" //
                        +"C416,Invalid Data Received From ESC\n" //
                        +"C122,Lost Communication with ESC\n" //
                        +"5084,UBP EV inlet 1 switched BIVs\n" //
                        +"5085,UBP EV inlet 2 controlled BIVc1\n" //
                        +"5086,UBP EV inlet 3 controlled BIVc2\n" //
                        +"5087,UBP EV outlet 1 switched BOVs\n" //
                        +"5088,UBP EV outlet 2 controlled BOVc1\n" //
                        +"5089,UBP EV outlet 3 controlled BOVc2\n" //
                        +"508A,UBP Pressure sensor Sense piston PsSP\n" //
                        +"508B,UBP Pressure sensor Master Cylinder PsBP\n" //
                        +"508C,UBP Pressure sensor Accumulator PsAcc\n" //
                        +"508D,UBP Pump motor control\n" //
                        +"5050,UBP Internal failure\n" //
                        +"5081,UBP EV Simulation1 SSV1\n" //
                        +"5082,UBP EV Simulation2 SSV2\n" //
                        +"5083,UBP EV by-pass BSV\n" //
                        +"5151,Invalid UBP calibration parameter\n" //
                        +"5182,EVC signal plausibility\n" //
                        +"C100,Lost Communication with EVC\n" //
                        +"C401,Invalid Data Received From EVC\n" //
                        +"5181,ESC signal plausibility\n" //
                        +"4042,UBP Brake pedal position sensor\n" //
                        +"508E,UBP EV offset learning calibration parameter\n" //

                ;

        String testDef =
                ""

                        +"00,Device and failure type ODB codding\n" //
                        +"01,General Electrical Failure\n" //
                        +"02,General signal failure\n" //
                        +"03,FM (Freq Mod) / PWM (Pulse Width Mod) Failures\n" //
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
                        +"97,Component or operation obstructed or blocked\n" //
                        +"98,component or system over temperature\n" //

                ;

        Frames.getInstance().load("7BC,0,0,UBP\n");
        Fields.getInstance().load(fieldDef1);
        Dtcs.getInstance().load(dtcDef, testDef);
    }
}