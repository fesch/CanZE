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

public class EcuDiagBCB {

    void load () {

        String fieldDef1 =
                ""
                        +"793,0,7,1,0,0,,14ffff,54,ff\n" // Reset DTC
                        +"793,0,23,1,0,0,,19023b,5902ff,ff\n" // Query DTC

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

        String dtcDef =
                ""

                        +"0103,BCB_Water_Temp signal\n" //
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
                        +"D003,CAN Bus off\n" //
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

                ;

        Frames.getInstance().load("793,0,0,BCB\n");
        Fields.getInstance().load(fieldDef1);
        Dtcs.getInstance().load(dtcDef, testDef);
    }
}