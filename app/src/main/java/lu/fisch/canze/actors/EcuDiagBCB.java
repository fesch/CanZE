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
 * Contains the BCB/JB@ Dianostics definitions
 */

public class EcuDiagBCB {

    void load () {

        String fieldDef1 =
                ""
                        +"793,0,23,1,0,0,,1902ff,5902ff,ff\n" // Query DTC BCB
                        +"793,0,23,1,0,0,,1902af,5902af,ff\n" // Query DTC JB2

                        +"793,24,31,.08,0,0,V,223101,623101,ff,Batt_voltage_mes\n" //
                        +"793,31,39,1,0,0,V,223102,623102,ff,BCBHighVoltage\n" //
                        +"793,24,31,1,0,0,%,223103,623103,ff,BCBTemp\n" //
                        +"793,24,31,1,40,0,°C,223104,623104,ff,BCBWaterTemp\n" //
                        +"793,27,31,1,0,0,,223106,623106,ff,DomesticNetworkType,0:Unvailable Value;1:FRANCE;2:JP 1;3:JP 2;4:JP 3;5:JP 4;6:USA\n" //
                        +"793,31,31,1,0,0,,223107,623107,ff,DID3107.ILOCK_CLIMA,0:Open;1:Close\n" //
                        +"793,30,30,1,0,0,,223107,623107,ff,DID3107.ILOCK_PTC,0:Open;1:Close\n" //
                        +"793,29,29,1,0,0,,223107,623107,ff,DID3107.ILOCK_MAINS_LEFT,0:Open;1:Close\n" //
                        +"793,28,28,1,0,0,,223107,623107,ff,DID3107.ILOCK_MAINS_RIGHT,0:Open;1:Close\n" //
                        +"793,27,27,1,0,0,,223107,623107,ff,DID3107.ILOCK_PEB,0:Open;1:Close\n" //
                        +"793,26,26,1,0,0,,223107,623107,ff,DID3107.ILOCK_BAT,0:Open;1:Close\n" //
                        +"793,25,25,1,0,0,,223107,623107,ff,DID3107.ED_Cover_SW,0:Open;1:Close\n" //
                        +"793,24,24,1,0,0,,223107,623107,ff,DID3107.Interlock_sec_connector_sts,0:Close;1:Open\n" //
                        +"793,29,31,1,0,0,,223108,623108,ff,DID3108.BCBLoadState,0:Unvailable Value;1:SlowCharge and Diag State;2:Quick Charge and Diag State;3:Nissan Quick Charge and Diag State;4:Diagnosis State;5:No State;6:Not used\n" //
                        +"793,39,39,1,0,0,,223108,623108,ff,DID3108.BCBPerformance,0:Normal Performance;1:derating mode active\n" //
                        +"793,38,38,1,0,0,,223108,623108,ff,DID3108.Ctrl_Relay_DCDC_P,0:Relay is Open;1:Relay is Closed\n" //
                        +"793,37,37,1,0,0,,223108,623108,ff,DID3108.Wake_Up_System,0:Wake_Up is not requested;1:Wake Up is being requested\n" //
                        +"793,25,27,1,0,0,,223108,623108,ff,DID3108.BCBFaultClass,0:No default;1:Class A;2:Class B;3:Class C;4:Class D;5:Class E;6:Not used\n" //
                        +"793,35,35,1,0,0,,223108,623108,ff,DID3108.SwitchingBoxRelay1State,0:Relay Open;1:Relay Closed\n" //
                        +"793,36,36,1,0,0,,223108,623108,ff,DID3108.BCBPowerPlantState,0:Power Plant Off;1:Power Plant On\n" //
                        +"793,34,34,1,0,0,,223108,623108,ff,DID3108.SwitchingBoxRelay2State,0:Relay Open;1:Relay Closed\n" //
                        +"793,28,28,1,0,0,,22310A,62310A,ff,DID310A.DomesticNetworkState,0:Domestic Network is not present;1:Domestic Network is present\n" //
                        +"793,29,31,1,0,0,,22310A,62310A,ff,DID310A.ChargingPlugConnectionState 1,0:DL_OPEN_LOAD;1:DL_NOT_CONNECTED;2:DL_CONNECTED_NOT_LOCKED;3:DL_CONNECTED_AND_LOCKED;4:DL_SCTOGND_UNDERVOLTAGE\n" //
                        +"793,25,27,1,0,0,,22310A,62310A,ff,DID310A.ChargingPlugConnectionState 2,0:DL_OPEN_LOAD;1:DL_NOT_CONNECTED;2:DL_CONNECTED_NOT_LOCKED;3:DL_CONNECTED_AND_LOCKED;4:DL_SCTOGND_UNDERVOLTAGE\n" //
                        +"793,24,24,1,0,0,,22310A,62310A,ff,DID310A.Earth plug connection,0:Ground Connection failure;1:Ground is correct\n" //
                        +"793,24,31,1,-40,0,,22310B,62310B,ff,Mains Voltage\n" //
                        +"793,25,31,.1,0,0,,22310D,62310D,ff,SlowChargingMaxPower\n" //
                        +"793,24,31,1,0,0,,22310E,62310E,ff,ChargerCurrent\n" //
                        +"793,25,31,1,-32,0,,22310F,62310F,ff,ChargerMaxCurrent\n" //
                        +"793,31,39,1,0,0,,223110,623110,ff,ChargerVoltage\n" //
                        +"793,24,31,1,-240,0,,223111,623111,ff,ChargerMaxVoltage\n" //
                        +"793,24,31,1,0,0,,223112,623112,ff,ChargerCurrentSlope\n" //
                        +"793,26,31,1,0,0,,223113,623113,ff,ChargerMaxPower\n" //
                        +"793,26,31,1,0,0,,223114,623114,ff,ChargerMinCurrent\n" //
                        +"793,24,31,1,-240,0,,223115,623115,ff,ChargerMinVoltage\n" //
                        +"793,31,31,1,0,0,,223119,623119,ff,DID3119.QuickChargeStopRequest,0:Quick Charge Stop Not Requested;1:Quick Charge Stop  Requested\n" //
                        +"793,30,30,1,0,0,,223119,623119,ff,DID3119.QuickChargeEmergencyStop,0:Quick Charge Emergency Stop Not Requested;1:Quick Charge Emergency Stop Requested\n" //
                        +"793,29,29,1,0,0,,223119,623119,ff,DID3119.KLineState,0:K Line Not present;1:K Line present\n" //
                        +"793,28,28,1,0,0,,223119,623119,ff,DID3119.QuickChargeSessionStopRequest,0:Quick Charge Session Stop Not Requested;1:Quick Charge Session Stop Requested\n" //
                        +"793,30,31,1,0,0,,223120,623120,ff,DID3120.QuickChargeLimitsACK,0:unavailable value;1:Charger has received Security Limits;2:Charger has not received Security Limits;3:reserved\n" //
                        +"793,28,29,1,0,0,,223120,623120,ff,DID3120.QuickChargeStopACK,0:unavailable value;1:Charger has received Stop Request;2:Charger has not received Stop Request;3:reserved\n" //
                        +"793,26,27,1,0,0,,223120,623120,ff,DID3120.QuickChargeStartACK,0:unavailable value;1:Charger has received Start Request;2:Charger has not received Start Request;3:reserved\n" //
                        +"793,24,25,1,0,0,,223120,623120,ff,DID3120.QuickChargeTempStopACK,0:unavailable value;1:Charger has received Temp Stop Request;2:Charger has not received Temp Stop Request;3:reserved\n" //
                        +"793,30,31,1,0,0,,22312D,62312D,ff,InterlockBatt,0:Unvailable Value;1:Open;2:Closed;3:Not used\n" //
                        +"793,28,39,10,2000,0,rpm,22312E,62312E,ff,ElecMachineSpeed\n" //
                        +"793,24,47,1,0,0,,223130,623130,ff,DistanceTotalizer\n" //
                        +"793,29,31,1,0,0,,223132,623132,ff,BCBState,0:Unavailable Value;1:Slow Charge, Zero Amp, Diag;2:Quick Charge, Diag state;3:Nissan Quick Charge, Diag state;4:Diagnosis state;5:No state;6:not used;7:Not Used\n" //
                        +"793,24,31,.1,0,0,,223133,623133,ff,SlowChargingCurrent\n" //
                        +"793,24,31,1,0,0,,223134,623134,ff,QuickChargeCurrent\n" //
                        +"793,24,31,1,0,0,,223135,623135,ff,QuickChargeCurrentLimit\n" //
                        +"793,24,31,1,-240,0,,223136,623136,ff,QuickChargeVoltageLimit\n" //
                        +"793,31,31,1,0,0,,223137,623137,ff,DID3137.QuickChargeStartRequest,0:Quick Charge start Not Requested;1:Quick Charge start Requested\n" //
                        +"793,30,30,1,0,0,,223137,623137,ff,DID3137.QuickChargeSessionStopRequest,0:Quick Charge Session Stop Not Requested;1:Quick Charge Session Stop Requested\n" //
                        +"793,29,29,1,0,0,,223137,623137,ff,DID3137.QuickChargeTempStopRequest,0:QCharge Temporary Stop Not Requested;1:QCharge Temporary Stop Requested\n" //
                        +"793,31,31,1,0,0,0,22FFF0,62FFF0,ff,DIDFFF0.Derating_Mode_Active\n" //
                        +"793,30,30,1,0,0,0,22FFF0,62FFF0,ff,DIDFFF0.PFC_On_Off_Status\n" //
                        +"793,29,29,1,0,0,0,22FFF0,62FFF0,ff,DIDFFF0.Input_Overcurrent_Alarm\n" //
                        +"793,28,28,1,0,0,0,22FFF0,62FFF0,ff,DIDFFF0.Input_PFC_Undervoltage_Alarm\n" //
                        +"793,27,27,1,0,0,0,22FFF0,62FFF0,ff,DIDFFF0.Input_Overvoltage_Alarm\n" //
                        +"793,26,26,1,0,0,0,22FFF0,62FFF0,ff,DIDFFF0.Output_PFC_Overvoltage_Alarm\n" //
                        +"793,25,25,1,0,0,0,22FFF0,62FFF0,ff,DIDFFF0.Cooling_Overtermperature_Alarm\n" //
                        +"793,24,24,1,0,0,0,22FFF0,62FFF0,ff,DIDFFF0.DCDC_On_Off_Status\n" //
                        +"793,39,39,1,0,0,0,22FFF0,62FFF0,ff,DIDFFF0.Output_Overcurrent_Alarm\n" //
                        +"793,38,38,1,0,0,0,22FFF0,62FFF0,ff,DIDFFF0.Input_DCDC_Undervoltage_Alarm\n" //
                        +"793,37,37,1,0,0,0,22FFF0,62FFF0,ff,DIDFFF0.Output_DCDC_Overvoltage_Alarm\n" //
                        +"793,36,36,1,0,0,0,22FFF0,62FFF0,ff,DIDFFF0.Primary_Hardware_Alarm\n" //
                        +"793,35,35,1,0,0,0,22FFF0,62FFF0,ff,DIDFFF0.Secondary_Hardware_Alarm\n" //
                        +"793,34,34,1,0,0,0,22FFF0,62FFF0,ff,DIDFFF0.Output_Short_Circuit_Alarm\n" //
                        +"793,33,33,1,0,0,0,22FFF0,62FFF0,ff,DIDFFF0.SCI_Error\n" //
                        +"793,32,32,1,0,0,0,22FFF0,62FFF0,ff,DIDFFF0.Interlock_sec_connector_sts\n" //
                        +"793,40,47,1,40,0,-40,22FFF0,62FFF0,ff,DIDFFF0.Final_Ambient_temperature\n" //
                        +"793,54,55,1,0,0,0,22FFF0,62FFF0,ff,DIDFFF0.Mains_Frequency\n" //
                        +"793,48,53,.5,0,0,A,22FFF0,62FFF0,ff,DIDFFF0.DCDC_Output_Current\n" //
                        +"793,56,63,1,-40,0,V,22FFF0,62FFF0,ff,DIDFFF0.Mains_Voltage\n" //
                        +"793,64,72,1,0,0,V,22FFF0,62FFF0,ff,DIDFFF0.DCDC_output_voltage\n" //
                        +"793,73,81,1,0,0,0,22FFF0,62FFF0,ff,DIDFFF0.HV_Bus_meas_voltage\n" //
                        +"793,85,85,1,0,0,0,22FFF0,62FFF0,ff,DIDFFF0.Status_FCHG_N_Relay\n" //
                        +"793,84,84,1,0,0,0,22FFF0,62FFF0,ff,DIDFFF0.Status_FCHG_P_Relay\n" //
                        +"793,88,95,.5,0,0,A,22FFF0,62FFF0,ff,DIDFFF0.Input_current\n" //
                        +"793,31,31,1,0,0,0,22FFF1,62FFF1,ff,DIDFFF1.Charger_On_Off\n" //
                        +"793,27,30,1,-10,0,A,22FFF1,62FFF1,ff,DIDFFF1.Input_Current_Limit\n" //
                        +"793,26,26,1,0,0,0,22FFF1,62FFF1,ff,DIDFFF1.FCHG_Vdrop_Relay_P\n" //
                        +"793,25,25,1,0,0,0,22FFF1,62FFF1,ff,DIDFFF1.FCHG_Vdrop_Relay_N\n" //
                        +"793,24,24,1,0,0,0,22FFF1,62FFF1,ff,DIDFFF1.PFC_Alarm_Reset\n" //
                        +"793,35,39,.5,0,0,A,22FFF1,62FFF1,ff,DIDFFF1.Set_Output_Max_Current\n" //
                        +"793,40,47,1,40,0,°C,22FFF1,62FFF1,ff,DIDFFF1.Ambient_Temp\n" //
                        +"793,48,55,1,40,0,°C,22FFF1,62FFF1,ff,DIDFFF1.Cooling_Temp\n" //
                        +"793,24,63,1,0,0,,22F180,62F180,ff,bootSoftwareIdentification\n" //
                        +"793,24,31,.1,0,0,A,223105,623105,ff,BCBSlowChargeInputCurrent\n" //
                        +"793,24,31,.1,0,0,A,223109,623109,ff,EVSECurrentLimit\n" //
                        +"793,30,30,1,0,0,,22310C,62310C,ff,DID310C.Current_Ilimit,0:Not Pushed;1:Pushed\n" //
                        +"793,31,31,1,0,0,,22310C,62310C,ff,DID310C.O_LED_Ilimit,0:Off ;1:ON\n" //
                        +"793,29,29,1,0,0,,22310C,62310C,ff,DID310C.CTRL_ToCharge_J1772,0:Open;1:Closed\n" //
                        +"793,24,31,1,0,0,,223118,623118,ff,FastChargerReference\n" //
                        +"793,24,39,1,0,0,,22312F,62312F,ff,Quick drop counter\n" //
                        +"793,24,31,100,0,0,%,223139,623139,ff,EPWM_Pilot\n" //
                        +"793,24,31,.5,0,0,A,22313A,62313A,ff,DCDC_Output_Current\n" //
                        +"793,192,199,1,0,0,,2180,6180,ff,ApprovalNumber.BasicPartList,0:N/A;1:8200x\n" //
                        +"793,160,175,1,0,0,,2180,6180,ff,CalibrationNumber\n" //
                        +"793,56,63,1,0,0,,2180,6180,ff,DiagnosticIdentificationCode\n" //
                        +"793,144,159,1,0,0,,2180,6180,ff,EditionNumber\n" //
                        +"793,184,191,1,0,0,,2180,6180,ff,HardwareNumber.BasicPartList,0:N/A;1:29609\n" //
                        +"793,88,127,1,0,0,,2180,6180,2ff,HardwareNumber.LowerPart\n" //
                        +"793,200,207,1,0,0,,2180,6180,ff,ManufacturerIdentificationCode,0:Renault R1;128:Nissan N1;129:Nissan N2;130:Nissan N3;136:5DIGITS;255:Renault R3\n" //
                        +"793,176,183,1,0,0,,2180,6180,ff,PartNumber.BasicPartList,0:N/A;2:29609\n" //
                        +"793,16,55,1,0,0,,2180,6180,2ff,PartNumber.LowerPart\n" //
                        +"793,128,143,1,0,0,,2180,6180,ff,SoftwareNumber\n" //
                        +"793,64,87,1,0,0,,2180,6180,2ff,SupplierNumber.ITG\n" //
                        +"793,152,167,1,0,0,,2181,6181,ff,Vin CRC\n" //
                        +"793,16,151,1,0,0,,2181,6181,2ff,Vin In Ascii\n" //
                        +"793,148,155,1,0,0,,2184,6184,2ff,PaddingDatas\n" //
                        +"793,136,143,1,0,0,,2184,6184,2ff,Siemens serial number Byte 0 to be coded in ASCII\n" //
                        +"793,128,135,1,0,0,,2184,6184,2ff,Siemens serial number Byte 1 to be coded in ASCII\n" //
                        +"793,56,63,1,0,0,,2184,6184,2ff,Siemens serial number Byte 10 to be coded in ASCII\n" //
                        +"793,48,55,1,0,0,,2184,6184,2ff,Siemens serial number Byte 11 to be coded in ASCII\n" //
                        +"793,40,47,1,0,0,,2184,6184,2ff,Siemens serial number Byte 12 to be coded in ASCII\n" //
                        +"793,32,39,1,0,0,,2184,6184,2ff,Siemens serial number Byte 13 to be coded in ASCII\n" //
                        +"793,24,31,1,0,0,,2184,6184,2ff,Siemens serial number Byte 14 to be coded in ASCII\n" //
                        +"793,16,23,1,0,0,,2184,6184,2ff,Siemens serial number Byte 15 to be coded in ASCII\n" //
                        +"793,120,127,1,0,0,,2184,6184,2ff,Siemens serial number Byte 2 to be coded in ASCII\n" //
                        +"793,112,119,1,0,0,,2184,6184,2ff,Siemens serial number Byte 3 to be coded in ASCII\n" //
                        +"793,104,111,1,0,0,,2184,6184,2ff,Siemens serial number Byte 4 to be coded in ASCII\n" //
                        +"793,96,103,1,0,0,,2184,6184,2ff,Siemens serial number Byte 5 to be coded in ASCII\n" //
                        +"793,88,95,1,0,0,,2184,6184,2ff,Siemens serial number Byte 6 to be coded in ASCII\n" //
                        +"793,80,87,1,0,0,,2184,6184,2ff,Siemens serial number Byte 7 to be coded in ASCII\n" //
                        +"793,72,79,1,0,0,,2184,6184,2ff,Siemens serial number Byte 8 to be coded in ASCII\n" //
                        +"793,64,71,1,0,0,,2184,6184,2ff,Siemens serial number Byte 9 to be coded in ASCII\n" //
                        +"793,24,39,1,0,0,Hz,223138,623138,ff,EPWM_Pilot_Frequency\n" //
                        +"793,24,103,1,0,0,,22F18E,62F18E,2ff,VehicleManufacturerKitAssemblyPartNumber\n" //
                        +"793,31,31,1,0,0,,22FFF5,62FFF5,ff,DIDFFF5.Download_enable\n" //
                        +"793,30,30,1,0,0,,22FFF5,62FFF5,ff,DIDFFF5.Coupler_unlocked\n" //
                        +"793,29,29,1,0,0,,22FFF5,62FFF5,ff,DIDFFF5.DCDC_Alarm\n" //
                        +"793,32,39,1,0,0,Hz,22FFF5,62FFF5,ff,DIDFFF5.MainsFrequency\n" //
                        +"793,40,47,1,0,0,A,22FFF5,62FFF5,ff,DIDFFF5.Input_Current_offset\n" //
                        +"793,48,55,1,0,0,A,22FFF5,62FFF5,ff,DIDFFF5.Set_Output_Max_Current\n" //
                        +"793,56,65,.0049,0,0,V,22FFF5,62FFF5,ff,DIDFFF5.NTC4_PCB\n" //
                        +"793,72,81,.0049,0,0,V,22FFF5,62FFF5,ff,DIDFFF5.NTC_PFC\n" //
                        +"793,88,97,.5,0,0,V,22FFF5,62FFF5,ff,DIDFFF5.Set_Output_Voltage\n" //
                        +"793,104,113,.5,0,0,V,22FFF5,62FFF5,ff,DIDFFF5.DC_Bus_Voltage\n" //
                        +"793,120,127,1,0,0,V,22FFF5,62FFF5,ff,DIDFFF5.5V_Supply_Pri\n" //
                        +"793,128,135,1,0,0,V,22FFF5,62FFF5,ff,DIDFFF5.12V_Supply_Pri\n" //
                        +"793,30,39,.5,0,0,V,22FFF4,62FFF4,ff,DIDFFF4.HV Battery_monitoring\n" //
                        +"793,25,25,1,0,0,,22FFF4,62FFF4,ff,DIDFFF4.Output_DCDC_Overvoltage_Alarm\n" //
                        +"793,24,24,1,0,0,,22FFF4,62FFF4,ff,DIDFFF4.Output_Overcurrent_Alarm\n" //
                        +"793,27,27,1,0,0,,22FFF4,62FFF4,ff,DIDFFF4.Overlock_Status\n" //
                        +"793,26,26,1,0,0,,22FFF4,62FFF4,ff,DIDFFF4.Secondary_Hardware_Alarm\n" //
                        +"793,40,47,.1,0,0,dV,22FFF4,62FFF4,ff,DIDFFF4.12V_Supply\n" //
                        +"793,48,55,.1,0,0,dV,22FFF4,62FFF4,ff,DIDFFF4.5V_Supply\n" //
                        +"793,81,90,.0049,0,0,Vdc,22FFF4,62FFF4,ff,DIDFFF4.Feedback_current_consign\n" //
                        +"793,97,106,.0049,0,0,Vdc,22FFF4,62FFF4,ff,DIDFFF4.Feedback_voltage_consign\n" //
                        +"793,56,63,1,0,0,dAdc,22FFF4,62FFF4,ff,DIDFFF4.Output_Current_DCDC\n" //
                        +"793,65,74,.5,0,0,Vdc,22FFF4,62FFF4,ff,DIDFFF4.Output_Voltage_DCDC\n" //
                        +"793,40,55,1,0,0,,22FFF2,62FFF2,ff,DIDFFF2.14VBatteryVoltage\n" //
                        +"793,56,71,1,0,0,,22FFF2,62FFF2,ff,DIDFFF2.14VBatteryVoltageSwitched\n" //
                        +"793,72,87,1,0,0,,22FFF2,62FFF2,ff,DIDFFF2.PowerPlantCurrentConsumption\n" //
                        +"793,88,103,1,0,0,,22FFF2,62FFF2,ff,DIDFFF2.NTC1_PCB\n" //
                        +"793,104,119,1,0,0,,22FFF2,62FFF2,ff,DIDFFF2.NTC2_PCB\n" //
                        +"793,120,135,1,0,0,,22FFF2,62FFF2,ff,DIDFFF2.NTC3_PCB\n" //
                        +"793,136,151,1,0,0,,22FFF2,62FFF2,ff,DIDFFF2.PILOT_J1772\n" //
                        +"793,152,167,1,0,0,,22FFF2,62FFF2,ff,DIDFFF2.NTC_Coolplate\n" //
                        +"793,168,183,1,0,0,,22FFF2,62FFF2,ff,DIDFFF2.VCC\n" //
                        +"793,184,199,1,0,0,,22FFF2,62FFF2,ff,DIDFFF2.VCC_SW\n" //
                        +"793,200,215,1,0,0,,22FFF2,62FFF2,ff,DIDFFF2.Ea_NCH_DET_LOGIC\n" //
                        +"793,216,231,1,0,0,,22FFF2,62FFF2,ff,DIDFFF2.Ea_RSA_NCH_DET_LOGIC\n" //
                        +"793,232,247,1,0,0,,22FFF2,62FFF2,ff,DIDFFF2.EDIAG_LED_Ilimit\n" //
                        +"793,31,31,1,0,0,,22FFF3,62FFF3,ff,DIDFFF3.CTRL_14V_Switched\n" //
                        +"793,30,30,1,0,0,,22FFF3,62FFF3,ff,DIDFFF3.CTRL_CAN1_STB\n" //
                        +"793,29,29,1,0,0,,22FFF3,62FFF3,ff,DIDFFF3.CTRL_5V_SW_Enable\n" //
                        +"793,28,28,1,0,0,,22FFF3,62FFF3,ff,DIDFFF3.CTRL_EN_PWSupply\n" //
                        +"793,27,27,1,0,0,,22FFF3,62FFF3,ff,DIDFFF3.Download_CTRL\n" //
                        +"793,26,26,1,0,0,,22FFF3,62FFF3,ff,DIDFFF3.CTRL_RELAY_SWBOX_L1\n" //
                        +"793,25,25,1,0,0,,22FFF3,62FFF3,ff,DIDFFF3.CTRL_RELAY_SWBOX_L2\n" //
                        +"793,24,24,1,0,0,,22FFF3,62FFF3,ff,DIDFFF3.CTRL_RELAY_DCDC_P\n" //
                        +"793,39,39,1,0,0,,22FFF3,62FFF3,ff,DIDFFF3.EN_INH_KLine\n" //
                        +"793,37,37,1,0,0,,22FFF3,62FFF3,ff,DIDFFF3.CTRL_WAKEUP_SYS\n" //
                        +"793,35,35,1,0,0,,22FFF3,62FFF3,ff,DIDFFF3.DIAG_OT_Relay_SWBox_L1\n" //
                        +"793,34,34,1,0,0,,22FFF3,62FFF3,ff,DIDFFF3.DIAG_OT_Relay_SWBox_L2\n" //
                        +"793,33,33,1,0,0,,22FFF3,62FFF3,ff,DIDFFF3.DIAG_OT_Relay_DCDC_P\n" //
                        +"793,24,31,1,0,0,,22FF01,62FF01,ff,DIDFF01.AlarmIndex\n" //
                        +"793,32,39,1,0,0,,22FF01,62FF01,ff,DIDFF01.AlarmCode0\n" //
                        +"793,40,47,1,0,0,,22FF01,62FF01,ff,DIDFF01.AlarmCode1\n" //
                        +"793,48,55,1,0,0,,22FF01,62FF01,ff,DIDFF01.AlarmCode2\n" //
                        +"793,56,63,1,0,0,,22FF01,62FF01,ff,DIDFF01.AlarmCode3\n" //
                        +"793,64,71,1,0,0,,22FF01,62FF01,ff,DIDFF01.AlarmCode4\n" //
                        +"793,72,79,1,0,0,,22FF01,62FF01,ff,DIDFF01.AlarmCode5\n" //
                        +"793,80,87,1,0,0,,22FF01,62FF01,ff,DIDFF01.AlarmCode6\n" //
                        +"793,88,95,1,0,0,,22FF01,62FF01,ff,DIDFF01.AlarmCode7\n" //
                        +"793,96,103,1,0,0,,22FF01,62FF01,ff,DIDFF01.AlarmCode8\n" //
                        +"793,104,111,1,0,0,,22FF01,62FF01,ff,DIDFF01.AlarmCode9\n" //
                        +"793,24,24,1,0,0,,22FFF8,62FFF8,ff,DIDFFF8.Sup_SFY_Tx_Disp\n" //
                        +"793,25,25,1,0,0,,22FFF8,62FFF8,ff,DIDFFF8.Sup_SFY_Tx_RecMn\n" //
                        +"793,26,26,1,0,0,,22FFF8,62FFF8,ff,DIDFFF8.Sup_SFY_Tx_PSupp\n" //
                        +"793,27,27,1,0,0,,22FFF8,62FFF8,ff,DIDFFF8.Sup_SFY_Tx_RAM_Ch\n" //
                        +"793,32,32,1,0,0,,22FFF8,62FFF8,ff,DIDFFF8.PPP_SFY_Rx_Dispat\n" //
                        +"793,33,33,1,0,0,,22FFF8,62FFF8,ff,DIDFFF8.PPP_SFY_Rx_RecM\n" //
                        +"793,34,34,1,0,0,,22FFF8,62FFF8,ff,DIDFFF8.PPP_SFY_Rx_PSupp\n" //
                        +"793,35,35,1,0,0,,22FFF8,62FFF8,ff,DIDFFF8.PPP_SFY_Rx_PSecMn\n" //
                        +"793,36,36,1,0,0,,22FFF8,62FFF8,ff,DIDFFF8.SFY_Rx_RAM_Ch\n" //

                ;

        String fieldDef2 =
                ""

                        +"793,29,31,1,0,0,,222003,622003,ff,Junction box 2 state,0:Init;1:Charge;4:Wait;5:Power Off/Sleep\n" //
                        +"793,24,31,1,40,0,°C,222005,622005,ff,Cooling temperature from BI\n" //
                        +"793,24,47,1,0,0,,225001,625001,ff,Plugging counter\n" //
                        +"793,24,47,1,0,0,,225002,625002,ff,Wake Up counter\n" //
                        +"793,31,31,1,0,0,,225003,625003,ff,Raw A/C plug interlock value,0:open;1:closed\n" //
                        +"793,31,31,1,0,0,,225005,625005,ff,CAN activity for JB2,0:No activity;1:Activity\n" //
                        +"793,31,31,1,0,0,,225008,625008,ff,Raw JB2 hood plug interlock value,0:open;1:closed\n" //
                        +"793,31,31,1,0,0,,225009,625009,ff,Raw HV Battery plug interlock value,0:open;1:closed\n" //
                        +"793,31,31,1,0,0,,22500B,62500B,ff,Raw mains interlock value,0:open;1:closed\n" //
                        +"793,31,31,1,0,0,,22500C,62500C,ff,Mains tri phases permutation,0:v1/v2/v3 off;1:v1/v2/v3 on\n" //
                        +"793,31,31,1,0,0,,22500D,62500D,ff,Mains voltage presence,0:Absent;1:Present\n" //
                        +"793,31,31,1,0,0,,22500E,62500E,ff,Wake-Up request from Mains or plug presence,0:No wake-up request;1:Wake-up request\n" //
                        +"793,31,31,1,0,0,,22500F,62500F,ff,Raw PEB plug interlock value,0:open;1:closed\n" //
                        +"793,31,31,1,0,0,,225010,625010,ff,Raw PEB neutral interlock value,0:open;1:closed\n" //
                        +"793,31,31,1,0,0,,225011,625011,ff,Raw PTC plug interlock value,0:open;1:closed\n" //
                        +"793,30,31,1,0,0,,225015,625015,ff,Wake-Up / Sleep request from EVC,0:Wake-up;1:Sleep;2:UV\n" //
                        +"793,29,31,1,0,0,,225016,625016,ff,Junction box 2 state request from EVC,0:init;1:Charge;4:Wait;5:Power Off/Sleep\n" //
                        +"793,29,31,1,0,0,,225017,625017,ff,Mains current type,0:Nok;1:AC mono;2:AC tri;3:DC;4:AC bi\n" //
                        +"793,30,31,1,0,0,,225018,625018,ff,Mains plug unlocking CAN request,0:Unavailable;1:Unlock request;2:No unlock request\n" //
                        +"793,30,31,1,0,0,,225019,625019,ff,Memorized value of the wake up activation,0:Wake-up activated;1:Wake-up deactivated\n" //
                        +"793,31,31,1,0,0,,22501A,62501A,ff,BCM wake-up by wire,0:No wake-up;1:Wake-up\n" //
                        +"793,31,31,1,0,0,,22501C,62501C,ff,S2 relay control,0:Open;1:Close\n" //
                        +"793,30,31,1,0,0,,22501D,62501D,ff,Charge state request,0:No charge;1:Charge;2:Emergency stop\n" //
                        +"793,29,31,1,0,0,,22501E,62501E,ff,Global system state,0:Init;1:Actif;2:Sleep\n" //
                        +"793,30,31,1,0,0,,22501F,62501F,ff,System Temperature State,0:Ok;1:Derating;2:Nok\n" //
                        +"793,30,31,1,0,0,,225021,625021,ff,Cooling Temperature State,0:Ok;1:Derating;2:Nok\n" //
                        +"793,30,31,1,0,0,,225022,625022,ff,Auto wake-up enabling,0:No change;1:Activation;2:Deactivation\n" //
                        +"793,24,39,.0625,880,0,°C,225023,625023,ff,Raw CPU temperature value from BI\n" //
                        +"793,24,39,.0625,3200,0,A,225024,625024,ff,Measured neutral current filtred\n" //
                        +"793,24,39,.0625,3200,0,A,225025,625025,ff,Neutral current value from BI\n" //
                        +"793,24,31,1,0,0,%,225026,625026,ff,J1772 control pilot raw duty cycle\n" //
                        +"793,24,31,.25,60,0,V,225027,625027,ff,J1772 control pilot raw voltage\n" //
                        +"793,24,31,1,40,0,°C,225028,625028,ff,Mains input filter temperature from SPI\n" //
                        +"793,24,39,.0625,3200,0,A,225029,625029,ff,Raw AC mains phase 1 measured current\n" //
                        +"793,24,39,.0625,3200,0,A,22502A,62502A,ff,Raw AC mains phase 2 measured current\n" //
                        +"793,24,39,.0625,3200,0,A,22502B,62502B,ff,Raw AC mains phase 3 measured current\n" //
                        +"793,24,39,.5,32000,0,V,22502C,62502C,ff,Raw AC mains phase 1 measured voltage\n" //
                        +"793,24,39,.5,32000,0,V,22502D,62502D,ff,Raw AC mains phase 2 measured voltage\n" //
                        +"793,24,39,.5,32000,0,V,22502E,62502E,ff,Raw AC mains phase 3 measured voltage\n" //
                        +"793,192,199,1,0,0,,2180,6180,ff,ApprovalNumber.BasicPartList,1:HMLGT\n" //
                        +"793,160,175,1,0,0,,2180,6180,ff,CalibrationNumber\n" //
                        +"793,56,63,1,0,0,,2180,6180,ff,DiagnosticIdentificationCode\n" //
                        +"793,144,159,1,0,0,,2180,6180,ff,EditionNumber\n" //
                        +"793,184,191,1,0,0,,2180,6180,ff,HardwareNumber.BasicPartList,1:296H5\n" //
                        +"793,88,127,1,0,0,,2180,6180,2ff,HardwareNumber.LowerPart\n" //
                        +"793,200,207,1,0,0,,2180,6180,ff,ManufacturerIdentificationCode,0:Renault R1;128:Nissan N1;129:Nissan N2;130:Nissan N3;136:5DIGITS;255:Renault R3\n" //
                        +"793,176,183,1,0,0,,2180,6180,ff,PartNumber.BasicPartList,1:296H5;2:296H0\n" //
                        +"793,16,55,1,0,0,,2180,6180,2ff,PartNumber.LowerPart\n" //
                        +"793,128,143,1,0,0,,2180,6180,ff,SoftwareNumber\n" //
                        +"793,64,87,1,0,0,,2180,6180,2ff,SupplierNumber.ITG\n" //
                        +"793,152,167,1,0,0,,2181,6181,ff,Vin CRC\n" //
                        +"793,16,151,1,0,0,,2181,6181,2ff,Vin In Ascii\n" //
                        +"793,16,39,1,0,0,,2184,6184,ff,ITG Supplier Number\n" //
                        +"793,40,47,1,0,0,,2184,6184,2ff,Traceability Factory Code\n" //
                        +"793,48,143,1,0,0,,2184,6184,2ff,Traceability Serial Number\n" //
                        +"793,16,103,1,0,0,,21B7,61B7,ff,Configuration of Electrical Vehicle Networks\n" //
                        +"793,16,103,1,0,0,,21B8,61B8,ff,List of Electrical Vehicle ECUs with After-sales diagnostic\n" //
                        +"793,160,175,1,0,0,,21F0,61F0,ff,CalibrationNumber\n" //
                        +"793,56,63,1,0,0,,21F0,61F0,ff,DiagnosticIdentificationCode\n" //
                        +"793,128,143,1,0,0,,21F0,61F0,ff,SoftwareNumber\n" //
                        +"793,64,87,1,0,0,,21F0,61F0,ff,SupplierNumber\n" //
                        +"793,144,159,1,0,0,,21F0,61F0,ff,EditionNumber\n" //
                        +"793,176,183,1,0,0,,21F0,61F0,ff,PartNumber.BasicPartList,1:296H5;2:296H0\n" //
                        +"793,184,191,1,0,0,,21F0,61F0,ff,HardwareNumber.BasicPartList,1:296H5\n" //
                        +"793,192,199,1,0,0,,21F0,61F0,ff,ApprovalNumber.BasicPartList,1:HMLGT\n" //
                        +"793,16,55,1,0,0,,21F0,61F0,2ff,PartNumber.LowerPart\n" //
                        +"793,88,127,1,0,0,,21F0,61F0,2ff,HardwareNumber.LowerPart\n" //
                        +"793,200,207,1,0,0,,21F0,61F0,ff,ManufacturerIdentificationCode,0:Renault R1;128:Nissan N1;129:Nissan N2;130:Nissan N3;136:5DIGITS;255:Renault R3\n" //
                        +"793,16,55,1,0,0,,21F1,61F1,2ff,ApprovalNumber.LowerPart\n" //
                        +"793,56,95,1,0,0,,21F1,61F1,2ff,ProgrammingSiteReference\n" //
                        +"793,96,135,1,0,0,,21F1,61F1,2ff,ProgrammingToolReference\n" //
                        +"793,136,143,1,0,0,,21F1,61F1,ff,NumberOfReprogrammings\n" //
                        +"793,144,167,1,0,0,,21F1,61F1,ff,DateOfReprogramming\n" //
                        +"793,184,191,1,0,0,,21F1,61F1,ff,SaveMarking\n" //
                        +"793,192,207,1,0,0,,21F1,61F1,ff,CrcOfLogSave\n" //
                        +"793,168,183,1,0,0,,21F1,61F1,ff,TimeOfReprogramming\n" //
                        +"793,24,39,.0625,3200,0,A,222001,622001,ff,Mains phase 1 current RMS value\n" //
                        +"793,24,39,.0625,0,0,V,222002,622002,ff,CPU voltage supply from BI\n" //
                        +"793,25,31,1,0,0,%,225007,625007,ff,MMI Green LED PWM request\n" //
                        +"793,31,31,1,0,0,,225012,625012,ff,Mains phase 1-2 voltage presence,0:Absent;1:Present\n" //
                        +"793,31,31,1,0,0,,225013,625013,ff,Mains phase 2-3 voltage presence,0:Absent;1:Present\n" //
                        +"793,31,31,1,0,0,,225014,625014,ff,Mains phase 3-1 voltage presence,0:Absent;1:Present\n" //
                        +"793,25,31,1,0,0,%,22502F,62502F,ff,MMI Red LED PWM request\n" //
                        +"793,31,31,1,0,0,,225030,625030,ff,Raw fuse interlock value,0:open;1:closed\n" //
                        +"793,31,31,1,0,0,,225031,625031,ff,Raw Relay box plug interlock closed,0:open;1:closed\n" //
                        +"793,24,39,1,0,0,V,225032,625032,ff,Driver module power supply voltage from BI\n" //
                        +"793,24,39,.0625,32768,0,mA,225035,625035,ff,Mains ground current 1st measurement\n" //
                        +"793,24,39,.0625,32768,0,mA,225036,625036,ff,Mains ground current 2nd measurement\n" //
                        +"793,31,31,1,0,0,,225038,625038,ff,Mains Plug Locking Switch raw information,0:Unlocked;1:Locked\n" //
                        +"793,30,31,1,0,0,,225039,625039,ff,Mains plug locking control,0:No motion;1:Close;2:Open\n" //
                        +"793,24,39,.0625,3200,0,A,22503A,62503A,ff,Mains phase 2 current RMS value\n" //
                        +"793,24,39,.0625,3200,0,A,22503B,62503B,ff,Mains phase 3 current RMS value\n" //
                        +"793,24,39,.0625,3200,0,A,22503C,62503C,ff,Mains current phase 1 mean value\n" //
                        +"793,24,39,.0625,3200,0,A,22503D,62503D,ff,Mains current phase 2 mean value\n" //
                        +"793,24,39,.0625,3200,0,A,22503E,62503E,ff,Mains current phase 3 mean value\n" //
                        +"793,24,39,.5,0,0,V,22503F,62503F,ff,Mains phase 1-2 voltage RMS value\n" //
                        +"793,24,39,.5,0,0,V,225041,625041,ff,Mains phase 2-3 voltage RMS value\n" //
                        +"793,24,39,.5,0,0,V,225042,625042,ff,Mains phase 3-1 voltage RMS value\n" //
                        +"793,24,39,.5,32000,0,V,225043,625043,ff,AC mains phase 1-2 measured voltage\n" //
                        +"793,24,39,.5,32000,0,V,225044,625044,ff,AC mains phase 2-3 measured voltage\n" //
                        +"793,24,39,.5,32000,0,V,225045,625045,ff,AC mains phase 3-1 measured voltage\n" //
                        +"793,24,39,.5,32000,0,V,225046,625046,ff,Mains phase 1-2 mean voltage\n" //
                        +"793,24,39,.5,32000,0,V,225047,625047,ff,Mains phase 2-3 mean voltage\n" //
                        +"793,24,39,.5,32000,0,V,225048,625048,ff,Mains phase 3-1 mean voltage\n" //
                        +"793,24,39,.0078125,-1280,0,Hz,225049,625049,ff,Mains phase frequency\n" //
                        +"793,24,39,1,20000,0,W,22504A,62504A,ff,Mains active power consumed\n" //
                        +"793,24,39,.0625,9600,0,A,22504B,62504B,ff,Mains current sum\n" //
                        +"793,24,39,.5,32000,0,V,22504C,62504C,ff,Mains voltage sum\n" //
                        +"793,24,39,.0625,3200,0,A,22504D,62504D,ff,HV Network measured current\n" //
                        +"793,24,39,1,1023,0,V,22504E,62504E,ff,HV voltage\n" //
                        +"793,24,31,1,0,0,,22504F,62504F,ff,Mains leakage current strategy state,0:OFF/Init;1:OK;2:NOK\n" //
                        +"793,24,24,1,0,0,,225050,625050,ff,External controls denial status flags.0,0:Reserved for future use_0\n" //
                        +"793,24,31,1,0,0,%,222004,622004,ff,System Relative Temperature in %\n" //
                        +"793,24,39,.0002441406,0,0,V,225033,625033,ff,Raw Detection Logic line value\n" //
                        +"793,24,31,1,40,0,°C,225051,625051,ff,CPU temperature value\n" //
                        +"793,24,31,1,40,0,°C,225052,625052,ff,IGBT Buck arm 1 temperature\n" //
                        +"793,24,31,1,40,0,°C,225053,625053,ff,Raw IGBT Buck arm 1 temperature\n" //
                        +"793,24,31,1,40,0,°C,225054,625054,ff,Mains input filter hot spot temperature\n" //
                        +"793,24,31,1,40,0,°C,225055,625055,ff,Mains input filter hot spot temperature from SPI\n" //
                        +"793,24,31,1,40,0,°C,225056,625056,ff,Mains input filter temperature\n" //
                        +"793,24,39,.0625,32768,0,mA,225057,625057,ff,Raw leakage current - DC part measurement\n" //
                        +"793,24,39,.0625,32768,0,mA,225058,625058,ff,Raw leakage current - High Frequency 10kHz part measurement\n" //
                        +"793,24,39,.0625,32768,0,mA,225059,625059,ff,Raw leakage current - High Frequency 1st part measurement\n" //
                        +"793,24,39,.0625,32768,0,mA,22505A,62505A,ff,Raw leakage current - Low Frequency part measurement (50Hz)\n" //
                        +"793,16,23,1,0,0,,21EF,61EF,2ff,Hardware Part Number 1\n" //
                        +"793,24,31,1,0,0,,21EF,61EF,2ff,Hardware Part Number 2\n" //
                        +"793,32,39,1,0,0,,21EF,61EF,2ff,Hardware Part Number 3\n" //
                        +"793,40,47,1,0,0,,21EF,61EF,2ff,Hardware Part Number 4\n" //
                        +"793,48,55,1,0,0,,21EF,61EF,2ff,Hardware Part Number 5\n" //
                        +"793,56,63,1,0,0,,21EF,61EF,2ff,Hardware Part Number 6\n" //
                        +"793,64,71,1,0,0,,21EF,61EF,2ff,Hardware Part Number 7\n" //
                        +"793,72,79,1,0,0,,21EF,61EF,2ff,Hardware Part Number 8\n" //
                        +"793,80,87,1,0,0,,21EF,61EF,2ff,Hardware Part Number 9\n" //
                        +"793,88,95,1,0,0,,21EF,61EF,2ff,Hardware Part Number 10\n" //
                        +"793,96,103,1,0,0,,21EF,61EF,2ff,Software Part Number 1\n" //
                        +"793,104,111,1,0,0,,21EF,61EF,2ff,Software Part Number 2\n" //
                        +"793,112,119,1,0,0,,21EF,61EF,2ff,Software Part Number 3\n" //
                        +"793,120,127,1,0,0,,21EF,61EF,2ff,Software Part Number 4\n" //
                        +"793,128,135,1,0,0,,21EF,61EF,2ff,Software Part Number 5\n" //
                        +"793,136,143,1,0,0,,21EF,61EF,2ff,Software Part Number 6\n" //
                        +"793,144,151,1,0,0,,21EF,61EF,2ff,Software Part Number 7\n" //
                        +"793,152,159,1,0,0,,21EF,61EF,2ff,Software Part Number 8\n" //
                        +"793,160,167,1,0,0,,21EF,61EF,2ff,Software Part Number 9\n" //
                        +"793,168,175,1,0,0,,21EF,61EF,2ff,Software Part Number 10\n" //
                        +"793,31,31,1,0,0,,22505B,62505B,ff,Virgin neutral current acquisition tuning detection\n" //
                        +"793,31,31,1,0,0,,22505C,62505C,ff,Inform on spi communication error\n" //
                        +"793,31,31,1,0,0,,22505D,62505D,ff,Global checksum result on SPI frames\n" //
                        +"793,24,39,1,0,0,,22505E,62505E,ff,FPGA ident\n" //
                        +"793,24,39,1,0,0,ms,225061,625061,ff,Low part of CPU clock since power up\n" //
                        +"793,24,39,1,0,0,ms,22505F,62505F,ff,High part of CPU clock since power up\n" //
                        +"793,24,39,1,0,0,Ohm,225062,625062,ff,Mains ground resistance\n" //
                        +"793,24,31,1,0,0,,225063,625063,ff,Supervisor state,0:Init;1:Wait;2:ClosingS2;3:InitType;4:InitLkg;5:InitChg;6:Charge;7:ZeroAmpMode;8:EndOfChg;9:OpeningS2;10:ReadyToSleep;11:EmergencyStop;12:InitChargeDF;13:OCPStop;14:WaitS2\n" //
                        +"793,24,31,1,0,0,,225064,625064,ff,Leakage current diagnostic stored completion status,0:init;1:HF10;3:Mains Ground Default;5:Earth Current default;9:Ground Default;17:Means Leakage DC;33:Means Leakage LF;49:Means Leakage DC+LF;65:Means Leakage HF;81:Means Leakage DC+HF;97:Means Leakage LF+HF;113:Means Leakage DC+LF+HF\n" //
                        +"793,24,39,1,32768,0,mA,225065,625065,ff,Leakage DC current saved indicator after failure\n" //
                        +"793,24,39,1,32768,0,mA,225066,625066,ff,Leakage HF 10kHz current saved indicator after failure\n" //
                        +"793,24,39,1,32768,0,mA,225067,625067,ff,Leakage HF current saved indicator after failure\n" //
                        +"793,24,39,1,32768,0,mA,225068,625068,ff,Leakage LF current saved indicator after failure\n" //
                        +"793,24,31,1,0,0,,225069,625069,ff,LED absence configuration,0:LED present;1:LED absent\n" //
                        +"793,24,39,1,0,0,,22506C,62506C,ff,15V from Driver flyback\n" //
                        +"793,24,31,1,0,0,,22506A,62506A,ff,Old MMI configuration,0:New MMI;1:Old MMI\n" //
                        +"793,24,31,1,0,0,,22506B,62506B,ff,Vehicle configuration type,0:X10 configuration;1:X07 configuration\n" //
                        +"793,24,39,1,0,0,,22506D,62506D,ff,Saved number of quickdrop values\n" //

                ;

        String dtcDef =
                ""

                        +"0103,BCB_Water_Temp signal\n" // this block for BCB
                        +"0104,CTRL_Pilot\n" //
                        +"0120,RSA_NCH_DET_LOGIC\n" //
                        +"0105,RSA_NCH_DET_LOGIC2\n" //
                        +"0107,Wake_Up_System\n" //
                        +"0108,LED_ILIMIT \n" //
                        +"0110,14V_Switched logic\n" //
                        +"0111,Battery_supply \n" //
                        +"0113,Output PFC \n" //
                        +"0109,K-Line\n" //
                        +"0114,Output DCDC\n" //
                        +"0117,HV_Battery\n" //
                        +"0115,PWM HV Current Consign\n" //
                        +"0116,PWM HV Voltage Consign\n" //
                        +"0122,BCB_Amb_Temp signal 1\n" //
                        +"0123,BCB_Amb_Temp signal 2\n" //
                        +"0124,BCB_Amb_Temp signal 3\n" //
                        +"011D,Relay_DCDC_P/Coil_DCDC_P\n" //
                        +"0127,Switching Box relay 1\n" //
                        +"0128,Switching Box relay 2\n" //
                        +"0131,Mains Interlock\n" //
                        +"0130,PPPrimary_Power_Module_Temp\n" //
                        +"0112,Mains input (Line1and Line2)\n" //
                        +"0100, Current_Limit_Switch\n" //
                        +"0102,BCB_Amb_Temp\n" //
                        +"010A,5V_Supply\n" //
                        +"010B,Primary Power Plant supply\n" //
                        +"010D,BCB_Ambient_Temp_Pim\n" //
                        +"010E,Secondary Power Plant supply\n" //
                        +"0118,Primary Micro Controller\n" //
                        +"0119,Secondary Micro Controller\n" //
                        +"011E,SCI_Supervisor_Primary\n" //
                        +"011F,SCI_Primary_Secondary\n" //
                        +"0121,Internal Interlock\n" //
                        +"0125,Temperature sensors\n" //
                        +"0126,Supervisor Micro Controller\n" //
                        +"0129,CTRL_Pilot Failure in S2 switch\n" //

                        +"030D,EVC\n" // this block for JB2
                        +"0305,Lithium Battery Controler\n" //
                        +"030C,BCM (Body Control Module)\n" //
                        +"0309,CAN\n" //
                        +"0436,Junction box hood interlock\n" //
                        +"0437,HV Battery interlock\n" //
                        +"0438,Mains plug interlock\n" //
                        +"0439,PEB interlock\n" //
                        +"043A,PEB neutral interlock\n" //
                        +"043E,PTC interlock\n" //
                        +"0466,Cooling Temperature Sensor\n" //
                        +"046F,Cooling temperature\n" //
                        +"0602,Charging stop\n" //
                        +"0613,Charge yield\n" //
                        +"0612,S2 relay control\n" //
                        +"0620,CPU temperature\n" //
                        +"0621,Electronic card voltage supply\n" //
                        +"0622,CPU Temperature sensor\n" //
                        +"0630,Electronic power driver\n" //
                        +"0632,Electronic power driver temperature sensor\n" //
                        +"0631,Electronic power driver temperature\n" //
                        +"0640,Mains identification\n" //
                        +"0669,Mains communication line\n" //
                        +"0642,Mains phase 1 current\n" //
                        +"0643,Mains phase 2 current\n" //
                        +"0644,Mains phase 3 current\n" //
                        +"0645,Mains phase mono current\n" //
                        +"0646,Mains current regulation\n" //
                        +"0647,Mains locking input line\n" //
                        +"0649,Mains phases voltage\n" //
                        +"064A,Mains phase 1 voltage sensor\n" //
                        +"064B,Mains phase 2 voltage sensor\n" //
                        +"064C,Mains phase 3 voltage sensor\n" //
                        +"064D,Mains mono phased voltage sensors\n" //
                        +"065E,Mains phase 1 current sensor\n" //
                        +"065F,Mains phase 2 current sensor\n" //
                        +"0660,Mains phase 3 current sensor\n" //
                        +"0670,Neutral current sensor\n" //
                        +"0671,Neutral current regulation\n" //
                        +"043D,Air conditionned Interlock\n" //
                        +"043B,Fuse interlock\n" //
                        +"043C,Relay box interlock\n" //
                        +"0623,Internal power supply voltage (+5V)\n" //
                        +"0625,SPI communication\n" //
                        +"0662,Mains locking motor command\n" //
                        +"0663,Mains ground\n" //
                        +"0664,Capacity module\n" //
                        +"0626,Rectifier micro-controler\n" //
                        +"0665,Mains phase current sensor\n" //
                        +"0666,Mains phase voltage sensor\n" //
                        +"0668,Earth current sensor\n" //
                        +"0680,Blue Led\n" //
                        +"0681,Red Led\n" //
                        +"066A,Input filter temperature\n" //
                        +"066B,Input filter i\n" //
                        +"066C,Mains neutral/phase3\n" //
                        +"0648,Mains leakage\n" //
                        +"0627,Driver Flyback\n" //

                        +"D003,CAN Bus off\n" // this block for BCB
                        +"D002,CAN node EVC missing\n" //

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
                        +"28,signal bias level out of range\n" //
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
                        +"F1,Safety Case 1\n" // extra's  for JB2
                        +"F2,Safety Case 2\n" //
                        +"F3,Multiple Failure\n" //
                        +"F4,Temperature too Low\n" //
                        +"F5,Negative Loop Error\n" //
                        +"F6,Positive Loop Error\n" //
                        +"F7,Initialisation not valid\n" //
                        +"F8,Overspeed\n" //
                        +"F9,Limp Home\n" //
                        +"FA,Specific Supplier\n" //
                        +"FB,-\n" //
                        +"FC,--\n" //
                        +"FD,Drift low\n" //
                        +"FE,Drift  high\n" //

                ;

        Frames.getInstance().load("793,0,0,BCB\n");
        Frames.getInstance().load("793,0,0,BCB\n");
        Fields.getInstance().load(fieldDef1); // BCB
        Fields.getInstance().load(fieldDef2); // JB2
        Dtcs.getInstance().load(dtcDef, testDef);
    }
}