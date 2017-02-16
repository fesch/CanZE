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

public class EcuDiagCLIM {

    void load () {

        String fieldDef1 =
                ""
                        +"764,0,7,1,0,0,,14ffff,54,ff\n" // Reset DTC
                        +"764,0,23,1,0,0,,1902ff,5902ff,ff\n" // Query DTC

                        +"764,34,39,1,0,0,,2182,6182,ff,M_Bus_OFF\n" //
                        +"764,33,33,1,0,0,,2182,6182,ff,ConfirmedFaultBus_OFF,0:False;1:True\n" //
                        +"764,32,32,1,0,0,,2182,6182,ff,CurrentFaultBus_OFF,0:False;1:True\n" //
                        +"764,42,47,1,0,0,,2182,6182,ff,M_Mute\n" //
                        +"764,50,55,1,0,0,,2182,6182,ff,M_ECM\n" //
                        +"764,58,63,1,0,0,,2182,6182,ff,M_ABS\n" //
                        +"764,74,79,1,0,0,,2182,6182,ff,M_BCM\n" //
                        +"764,202,207,1,0,0,,2182,6182,ff,M_USM\n" //
                        +"764,41,41,1,0,0,,2182,6182,ff,ConfirmedFaultMute,0:False;1:True\n" //
                        +"764,49,49,1,0,0,,2182,6182,ff,ConfirmedFaultECM,0:False;1:True\n" //
                        +"764,57,57,1,0,0,,2182,6182,ff,ConfirmedFaultABS,0:False;1:True\n" //
                        +"764,73,73,1,0,0,,2182,6182,ff,ConfirmedFaultBCM,0:False;1:True\n" //
                        +"764,201,201,1,0,0,,2182,6182,ff,ConfirmedFaultUSM,0:False;1:True\n" //
                        +"764,40,40,1,0,0,,2182,6182,ff,CurrentFaultMute,0:False;1:True\n" //
                        +"764,48,48,1,0,0,,2182,6182,ff,CurrentFaultECM,0:False;1:True\n" //
                        +"764,56,56,1,0,0,,2182,6182,ff,CurrentFaultABS,0:False;1:True\n" //
                        +"764,72,72,1,0,0,,2182,6182,ff,CurrentFaultBCM,0:False;1:True\n" //
                        +"764,200,200,1,0,0,,2182,6182,ff,CurrentFaultUSM,0:False;1:True\n" //
                        +"764,104,111,1,0,0,,2182,6182,ff,CLIM_Absent,0:False;1:True\n" //
                        +"764,16,31,1,0,0,,2182,6182,ff,MessagesSetReference\n" //
                        +"764,24,103,1,0,0,,22F18E,62F18E,2ff,VehicleManufacturerKitAssemblyPartNumber\n" //
                        +"764,160,175,1,0,0,,2180,6180,ff,CalibrationNumber\n" //
                        +"764,56,63,1,0,0,,2180,6180,ff,DiagnosticIdentificationCode\n" //
                        +"764,128,143,1,0,0,,2180,6180,ff,SoftwareNumber\n" //
                        +"764,64,87,1,0,0,,2180,6180,2ff,SupplierNumber.ITG\n" //
                        +"764,144,159,1,0,0,,2180,6180,ff,EditionNumber\n" //
                        +"764,176,183,1,0,0,,2180,6180,ff,PartNumber.BasicPartList,0:N/A;2:27531\n" //
                        +"764,184,191,1,0,0,,2180,6180,ff,HardwareNumber.BasicPartList,0:N/A;1:28526\n" //
                        +"764,192,199,1,0,0,,2180,6180,ff,ApprovalNumber.BasicPartList,0:N/A;1:RENO1;2:NISS1\n" //
                        +"764,16,55,1,0,0,,2180,6180,2ff,PartNumber.LowerPart\n" //
                        +"764,88,127,1,0,0,,2180,6180,2ff,HardwareNumber.LowerPart\n" //
                        +"764,200,207,1,0,0,,2180,6180,ff,ManufacturerIdentificationCode,0:Renault R1;128:Nissan N1;129:Nissan N2;130:Nissan N3;136:5DIGITS;255:Renault R3\n" //
                        +"764,24,39,1,0,0,,229102,629102,ff,EngineeringCalibrationNumber\n" //
                        +"764,24,39,1,0,0,,229101,629101,ff,EngineeringSoftwareNumber\n" //
                        +"764,17,23,1,0,0,% of total range [min - max],2127,6127,ff,OH_FeetVentActrReq\n" //
                        +"764,41,47,1,0,0,% of total range [min - max],2127,6127,ff,OH_RecyActrReq\n" //
                        +"764,49,55,1,0,0,% of total range [min - max],2127,6127,ff,OH_RightMixActrReq\n" //
                        +"764,65,71,1,0,0,% of total range [min - max],2127,6127,ff,IH_FeetVentActrPosition\n" //
                        +"764,89,95,1,0,0,% of total range [min - max],2127,6127,ff,IH_RecyActrPosition\n" //
                        +"764,97,103,1,0,0,% of total range [min - max],2127,6127,ff,IH_RightMixActrPosition\n" //
                        +"764,129,135,1,0,0,% of total range [min - max],2127,6127,ff,OH_BatteryCoolingActrReq\n" //
                        +"764,137,143,1,0,0,% of total range [min - max],2127,6127,ff,IH_BatteryCoolingActrPosition\n" //
                        +"764,18,18,1,0,0,,2126,6126,ff,VL_LeftSolarLevelDiag_OCFM,0:False;1:True\n" //
                        +"764,19,19,1,0,0,,2126,6126,ff,VL_LeftSolarLevelDiag_SCFM,0:False;1:True\n" //
                        +"764,20,20,1,0,0,,2126,6126,ff,VL_InCarTempDiag_OCFM,0:False;1:True\n" //
                        +"764,21,21,1,0,0,,2126,6126,ff,VL_InCarTempDiag_SCFM,0:False;1:True\n" //
                        +"764,22,22,1,0,0,,2126,6126,ff,VL_EvapTempDiag_OCFM,0:False;1:True\n" //
                        +"764,23,23,1,0,0,,2126,6126,ff,VL_EvapTempDiag_SCFM,0:False;1:True\n" //
                        +"764,24,24,1,0,0,,2126,6126,ff,VL_HeatAQSDiag_OCFM,0:False;1:True\n" //
                        +"764,25,25,1,0,0,,2126,6126,ff,VL_HeatAQSDiag_SCFM,0:False;1:True\n" //
                        +"764,26,26,1,0,0,,2126,6126,ff,VL_AQS_VrefSupply_BAT_SCFM,0:False;1:True\n" //
                        +"764,27,27,1,0,0,,2126,6126,ff,VL_AQS_VrefSupply_GND_SCFM,0:False;1:True\n" //
                        +"764,28,28,1,0,0,,2126,6126,ff,VL_OxydantDiag_OCFM,0:False;1:True\n" //
                        +"764,29,29,1,0,0,,2126,6126,ff,VL_OxydantDiag_SCFM,0:False;1:True\n" //
                        +"764,30,30,1,0,0,,2126,6126,ff,VL_ReductorDiag_OCFM,0:False;1:True\n" //
                        +"764,31,31,1,0,0,,2126,6126,ff,VL_ReductorDiag_SCFM,0:False;1:True\n" //
                        +"764,32,32,1,0,0,,2126,6126,ff,VL_LINComDiag_ERFM,0:False;1:True\n" //
                        +"764,35,35,1,0,0,,2126,6126,ff,VL_EEPROM_ERFM,0:False;1:True\n" //
                        +"764,36,36,1,0,0,,2126,6126,ff,VL_VBattDiag_FM,0:False;1:True\n" //
                        +"764,37,37,1,0,0,,2126,6126,ff,VL_RHumidityDiag_BFFM,0:False;1:True\n" //
                        +"764,38,38,1,0,0,,2126,6126,ff,VL_RHumidityDiag_OCFM,0:False;1:True\n" //
                        +"764,39,39,1,0,0,,2126,6126,ff,VL_RHumidityDiag_SCFM,0:False;1:True\n" //
                        +"764,40,40,1,0,0,,2126,6126,ff,VL_RecyActrDiag_OCFM,0:False;1:True\n" //
                        +"764,41,41,1,0,0,,2126,6126,ff,VL_RecyActrDiag_SCFM,0:False;1:True\n" //
                        +"764,42,42,1,0,0,,2126,6126,ff,VL_FeetVentActrDiag_OCFM,0:False;1:True\n" //
                        +"764,43,43,1,0,0,,2126,6126,ff,VL_FeetVentActrDiag_SCFM,0:False;1:True\n" //
                        +"764,44,44,1,0,0,,2126,6126,ff,VL_RightMixActrDiag_OCFM,0:False;1:True\n" //
                        +"764,45,45,1,0,0,,2126,6126,ff,VL_RightMixActrDiag_SCFM,0:False;1:True\n" //
                        +"764,49,49,1,0,0,,2126,6126,ff,VL_Config_ERFM,0:False;1:True\n" //
                        +"764,65,65,1,0,0,,2126,6126,ff,VL_AQMCP_Ionizer_OCFM,0:False;1:True\n" //
                        +"764,66,66,1,0,0,,2126,6126,ff,VL_AQMCP_Ionizer_SCFM,0:False;1:True\n" //
                        +"764,67,67,1,0,0,,2126,6126,ff,VL_AQMCP_FragranceBlower_OCFM,0:False;1:True\n" //
                        +"764,68,68,1,0,0,,2126,6126,ff,VL_AQMCP_FragranceBlower_SCFM,0:False;1:True\n" //
                        +"764,69,69,1,0,0,,2126,6126,ff,VL_AQMCP_FragranceMotor_OCFM,0:False;1:True\n" //
                        +"764,70,70,1,0,0,,2126,6126,ff,VL_AQMCP_FragranceMotor_SCFM,0:False;1:True\n" //
                        +"764,74,74,1,0,0,,2126,6126,ff,VL_IonizerMode_SCFM,0:False;1:True\n" //
                        +"764,73,73,1,0,0,,2126,6126,ff,VL_IonizerMode_OCFM,0:False;1:True\n" //
                        +"764,97,97,1,0,0,,2126,6126,ff,VL_BlowerControlDiag_OCFM,0:False;1:True\n" //
                        +"764,98,98,1,0,0,,2126,6126,ff,VL_BlowerControlDiag_SCFM,0:False;1:True\n" //
                        +"764,101,101,1,0,0,,2126,6126,ff,VL_RelayOutputDiag_OCFM,0:False;1:True\n" //
                        +"764,102,102,1,0,0,,2126,6126,ff,VL_RelayOutputDiag_SCFM,0:False;1:True\n" //
                        +"764,103,103,1,0,0,,2126,6126,ff,VL_CompValveDiag_OCFM,0:False;1:True\n" //
                        +"764,104,104,1,0,0,,2126,6126,ff,VL_CompValveDiag_SCFM,0:False;1:True\n" //
                        +"764,107,107,1,0,0,,2126,6126,ff,VL_BatteryCoolingActrDiag_OCFM,0:False;1:True\n" //
                        +"764,108,108,1,0,0,,2126,6126,ff,VL_BatteryCoolingActrDiag_SCFM,0:False;1:True\n" //
                        +"764,109,109,1,0,0,,2126,6126,ff,VL_LIN2ComDiag_ERFM,0:False;1:True\n" //
                        +"764,110,110,1,0,0,,2126,6126,ff,VL_FrontCPComDiag_DCFM,0:False;1:True\n" //
                        +"764,111,111,1,0,0,,2126,6126,ff,VL_HPMComDiag_DCFM,0:False;1:True\n" //
                        +"764,112,112,1,0,0,,2126,6126,ff,VL_HeatPumpValve1Diag_OCFM,0:False;1:True\n" //
                        +"764,113,113,1,0,0,,2126,6126,ff,VL_HeatPumpValve1Diag_SCFM,0:False;1:True\n" //
                        +"764,114,114,1,0,0,,2126,6126,ff,VL_HeatPumpValve2Diag_OCFM,0:False;1:True\n" //
                        +"764,115,115,1,0,0,,2126,6126,ff,VL_HeatPumpValve2Diag_SCFM,0:False;1:True\n" //
                        +"764,116,116,1,0,0,,2126,6126,ff,VL_HeatPumpSensor1Diag_OCFM,0:False;1:True\n" //
                        +"764,117,117,1,0,0,,2126,6126,ff,VL_HeatPumpSensor1Diag_SCFM,0:False;1:True\n" //
                        +"764,118,118,1,0,0,,2126,6126,ff,VL_HeatPumpSensor2Diag_OCFM,0:False;1:True\n" //
                        +"764,119,119,1,0,0,,2126,6126,ff,VL_HeatPumpSensor2Diag_SCFM,0:False;1:True\n" //
                        +"764,120,120,1,0,0,,2126,6126,ff,VL_HotSourceTempDiag_OCFM,0:False;1:True\n" //
                        +"764,121,121,1,0,0,,2126,6126,ff,VL_HotSourceTempDiag_SCFM,0:False;1:True\n" //
                        +"764,124,124,1,0,0,,2126,6126,ff,VL_HeatPumpValve1Diag_SCPFM,0:False;1:True\n" //
                        +"764,125,125,1,0,0,,2126,6126,ff,VL_HeatPumpValve2Diag_SCPFM,0:False;1:True\n" //
                        +"764,126,126,1,0,0,,2126,6126,ff,VL_BattBlowerSystDefaultType1_FM,0:No failure;1:Failure\n" //
                        +"764,127,127,1,0,0,,2126,6126,ff,VL_BattBlowerSystDefaultType2_FM,0:No failure;1:Failure\n" //
                        +"764,128,128,1,0,0,,2126,6126,ff,VL_BattEvapTempDiag_OCFM,0:False;1:True\n" //
                        +"764,129,129,1,0,0,,2126,6126,ff,VL_BattEvapTempDiag_SCFM,0:False;1:True\n" //
                        +"764,130,130,1,0,0,,2126,6126,ff,VL_CabinCoolingEV_OCFM,0:False;1:True\n" //
                        +"764,131,131,1,0,0,,2126,6126,ff,VL_CabinCoolingEV_SCFM,0:False;1:True\n" //
                        +"764,132,132,1,0,0,,2126,6126,ff,VL_BatteryCoolingEV_OCFM,0:False;1:True\n" //
                        +"764,133,133,1,0,0,,2126,6126,ff,VL_BatteryCoolingEV_SCFM,0:False;1:True\n" //
                        +"764,100,100,1,0,0,,2126,6126,ff,VL_BattBlowerControlDiag_OCFM,0:False;1:True\n" //
                        +"764,99,99,1,0,0,,2126,6126,ff,VL_BattBlowerControlDiag_SCFM,0:False;1:True\n" //
                        +"764,18,18,1,0,0,,2125,6125,ff,VL_LeftSolarLevelDiag_OCFP,0:False;1:True\n" //
                        +"764,19,19,1,0,0,,2125,6125,ff,VL_LeftSolarLevelDiag_SCFP,0:False;1:True\n" //
                        +"764,20,20,1,0,0,,2125,6125,ff,VL_InCarTempDiag_OCFP,0:False;1:True\n" //
                        +"764,21,21,1,0,0,,2125,6125,ff,VL_InCarTempDiag_SCFP,0:False;1:True\n" //
                        +"764,22,22,1,0,0,,2125,6125,ff,VL_EvapTempDiag_OCFP,0:False;1:True\n" //
                        +"764,23,23,1,0,0,,2125,6125,ff,VL_EvapTempDiag_SCFP,0:False;1:True\n" //
                        +"764,24,24,1,0,0,,2125,6125,ff,VL_HeatAQSDiag_OCFP,0:False;1:True\n" //
                        +"764,25,25,1,0,0,,2125,6125,ff,VL_HeatAQSDiag_SCFP,0:False;1:True\n" //
                        +"764,26,26,1,0,0,,2125,6125,ff,VL_AQS_VrefSupply_BAT_SCFP,0:False;1:True\n" //
                        +"764,27,27,1,0,0,,2125,6125,ff,VL_AQS_VrefSupply_GND_SCFP,0:False;1:True\n" //
                        +"764,28,28,1,0,0,,2125,6125,ff,VL_OxydantDiag_OCFP,0:False;1:True\n" //
                        +"764,29,29,1,0,0,,2125,6125,ff,VL_OxydantDiag_SCFP,0:False;1:True\n" //
                        +"764,30,30,1,0,0,,2125,6125,ff,VL_ReductorDiag_OCFP,0:False;1:True\n" //
                        +"764,31,31,1,0,0,,2125,6125,ff,VL_ReductorDiag_SCFP,0:False;1:True\n" //
                        +"764,32,32,1,0,0,,2125,6125,ff,VL_LINComDiag_ERFP,0:False;1:True\n" //
                        +"764,35,35,1,0,0,,2125,6125,ff,VL_EEPROM_ERFP,0:False;1:True\n" //
                        +"764,36,36,1,0,0,,2125,6125,ff,VL_VBattDiag_FP,0:False;1:True\n" //
                        +"764,37,37,1,0,0,,2125,6125,ff,VL_RHumidityDiag_BFFP,0:False;1:True\n" //
                        +"764,38,38,1,0,0,,2125,6125,ff,VL_RHumidityDiag_OCFP,0:False;1:True\n" //
                        +"764,39,39,1,0,0,,2125,6125,ff,VL_RHumidityDiag_SCFP,0:False;1:True\n" //
                        +"764,40,40,1,0,0,,2125,6125,ff,VL_RecyActrDiag_OCFP,0:False;1:True\n" //
                        +"764,41,41,1,0,0,,2125,6125,ff,VL_RecyActrDiag_SCFP,0:False;1:True\n" //
                        +"764,42,42,1,0,0,,2125,6125,ff,VL_FeetVentActrDiag_OCFP,0:False;1:True\n" //
                        +"764,43,43,1,0,0,,2125,6125,ff,VL_FeetVentActrDiag_SCFP,0:False;1:True\n" //
                        +"764,44,44,1,0,0,,2125,6125,ff,VL_RightMixActrDiag_OCFP,0:False;1:True\n" //
                        +"764,45,45,1,0,0,,2125,6125,ff,VL_RightMixActrDiag_SCFP,0:False;1:True\n" //
                        +"764,49,49,1,0,0,,2125,6125,ff,VL_Config_ERFP,0:False;1:True\n" //
                        +"764,65,65,1,0,0,,2125,6125,ff,VL_AQMCP_Ionizer_OCFP,0:False;1:True\n" //
                        +"764,66,66,1,0,0,,2125,6125,ff,VL_AQMCP_Ionizer_SCFP,0:False;1:True\n" //
                        +"764,67,67,1,0,0,,2125,6125,ff,VL_AQMCP_FragranceBlower_OCFP,0:False;1:True\n" //
                        +"764,68,68,1,0,0,,2125,6125,ff,VL_AQMCP_FragranceBlower_SCFP,0:False;1:True\n" //
                        +"764,69,69,1,0,0,,2125,6125,ff,VL_AQMCP_FragranceMotor_OCFP,0:False;1:True\n" //
                        +"764,70,70,1,0,0,,2125,6125,ff,VL_AQMCP_FragranceMotor_SCFP,0:False;1:True\n" //
                        +"764,73,73,1,0,0,,2125,6125,ff,VL_IonizerMode_OCFP,0:False;1:True\n" //
                        +"764,74,74,1,0,0,,2125,6125,ff,VL_IonizerMode_SCFP,0:False;1:True\n" //
                        +"764,97,97,1,0,0,,2125,6125,ff,VL_BlowerControlDiag_OCFP,0:False;1:True\n" //
                        +"764,98,98,1,0,0,,2125,6125,ff,VL_BlowerControlDiag_SCFP,0:False;1:True\n" //
                        +"764,103,103,1,0,0,,2125,6125,ff,VL_CompValveDiag_OCFP,0:False;1:True\n" //
                        +"764,104,104,1,0,0,,2125,6125,ff,VL_CompValveDiag_SCFP,0:False;1:True\n" //
                        +"764,101,101,1,0,0,,2125,6125,ff,VL_RelayOutputDiag_OCFP,0:False;1:True\n" //
                        +"764,102,102,1,0,0,,2125,6125,ff,VL_RelayOutputDiag_SCFP,0:False;1:True\n" //
                        +"764,107,107,1,0,0,,2125,6125,ff,VL_BatteryCoolingActrDiag_OCFP,0:False;1:True\n" //
                        +"764,108,108,1,0,0,,2125,6125,ff,VL_BatteryCoolingActrDiag_SCFP,0:False;1:True\n" //
                        +"764,110,110,1,0,0,,2125,6125,ff,VL_FrontCPComDiag_DCFP,0:False;1:True\n" //
                        +"764,111,111,1,0,0,,2125,6125,ff,VL_HPMComDiag_DCFP,0:False;1:True\n" //
                        +"764,109,109,1,0,0,,2125,6125,ff,VL_LIN2ComDiag_ERFP,0:False;1:True\n" //
                        +"764,116,116,1,0,0,,2125,6125,ff,VL_HeatPumpSensor1Diag_OCFP,0:False;1:True\n" //
                        +"764,117,117,1,0,0,,2125,6125,ff,VL_HeatPumpSensor1Diag_SCFP,0:False;1:True\n" //
                        +"764,118,118,1,0,0,,2125,6125,ff,VL_HeatPumpSensor2Diag_OCFP,0:False;1:True\n" //
                        +"764,119,119,1,0,0,,2125,6125,ff,VL_HeatPumpSensor2Diag_SCFP,0:False;1:True\n" //
                        +"764,112,112,1,0,0,,2125,6125,ff,VL_HeatPumpValve1Diag_OCFP,0:False;1:True\n" //
                        +"764,113,113,1,0,0,,2125,6125,ff,VL_HeatPumpValve1Diag_SCFP,0:False;1:True\n" //
                        +"764,124,124,1,0,0,,2125,6125,ff,VL_HeatPumpValve1Diag_SCPFP,0:False;1:True\n" //
                        +"764,114,114,1,0,0,,2125,6125,ff,VL_HeatPumpValve2Diag_OCFP,0:False;1:True\n" //
                        +"764,115,115,1,0,0,,2125,6125,ff,VL_HeatPumpValve2Diag_SCFP,0:False;1:True\n" //
                        +"764,125,125,1,0,0,,2125,6125,ff,VL_HeatPumpValve2Diag_SCPFP,0:False;1:True\n" //
                        +"764,120,120,1,0,0,,2125,6125,ff,VL_HotSourceTempDiag_OCFP,0:False;1:True\n" //
                        +"764,121,121,1,0,0,,2125,6125,ff,VL_HotSourceTempDiag_SCFP,0:False;1:True\n" //
                        +"764,126,126,1,0,0,,2125,6125,ff,VL_BattBlowerSystDefaultType1_FP,0:No failure;1:Failure\n" //
                        +"764,127,127,1,0,0,,2125,6125,ff,VL_BattBlowerSystDefaultType2_FP,0:No failure;1:Failure\n" //
                        +"764,128,128,1,0,0,,2125,6125,ff,VL_BattEvapTempDiag_OCFP,0:False;1:True\n" //
                        +"764,129,129,1,0,0,,2125,6125,ff,VL_BattEvapTempDiag_SCFP,0:False;1:True\n" //
                        +"764,130,130,1,0,0,,2125,6125,ff,VL_CabinCoolingEV_OCFP,0:False;1:True\n" //
                        +"764,131,131,1,0,0,,2125,6125,ff,VL_CabinCoolingEV_SCFP,0:False;1:True\n" //
                        +"764,132,132,1,0,0,,2125,6125,ff,VL_BatteryCoolingEV_OCFP,0:False;1:True\n" //
                        +"764,133,133,1,0,0,,2125,6125,ff,VL_BatteryCoolingEV_SCFP,0:False;1:True\n" //
                        +"764,100,100,1,0,0,,2125,6125,ff,VL_BattBlowerControlDiag_OCFP,0:False;1:True\n" //
                        +"764,99,99,1,0,0,,2125,6125,ff,VL_BattBlowerControlDiag_SCFP,0:False;1:True\n" //
                        +"764,22,22,1,0,0,,2128,6128,ff,IH_AutoNormalLongPushState,0:no long push detected;1:long push detected\n" //
                        +"764,23,23,1,0,0,,2128,6128,ff,IH_AutoNormalPushToggle,0:transition to 0 means switch pressed;1:transition to 1 means switch pressed\n" //
                        +"764,26,26,1,0,0,,2128,6128,ff,IH_BlowerDnLongPushState,0:no long push detected;1:long push detected\n" //
                        +"764,27,27,1,0,0,,2128,6128,ff,IH_BlowerDnPushToggle,0:transition to 0 means switch pressed;1:transition to 1 means switch pressed\n" //
                        +"764,28,28,1,0,0,,2128,6128,ff,IH_BlowerUpLongPushState,0:no long push detected;1:long push detected\n" //
                        +"764,29,29,1,0,0,,2128,6128,ff,IH_BlowerUpPushToggle,0:transition to 0 means switch pressed;1:transition to 1 means switch pressed\n" //
                        +"764,30,30,1,0,0,,2128,6128,ff,IH_ClearnessLongPushState,0:no long push detected;1:long push detected\n" //
                        +"764,31,31,1,0,0,,2128,6128,ff,IH_ClearnessPushToggle,0:transition to 0 means switch pressed;1:transition to 1 means switch pressed\n" //
                        +"764,32,32,1,0,0,,2128,6128,ff,IH_DistDegiMildLongPushState,0:no long push detected;1:long push detected\n" //
                        +"764,33,33,1,0,0,,2128,6128,ff,IH_DistDegiMildPushToggle,0:transition to 0 means switch pressed;1:transition to 1 means switch pressed\n" //
                        +"764,34,34,1,0,0,,2128,6128,ff,IH_DistFeetLongPushState,0:no long push detected;1:long push detected\n" //
                        +"764,35,35,1,0,0,,2128,6128,ff,IH_DistFeetPushToggle,0:transition to 0 means switch pressed;1:transition to 1 means switch pressed\n" //
                        +"764,36,36,1,0,0,,2128,6128,ff,IH_DistVentLongPushState,0:no long push detected;1:long push detected\n" //
                        +"764,37,37,1,0,0,,2128,6128,ff,IH_DistVentPushToggle,0:transition to 0 means switch pressed;1:transition to 1 means switch pressed\n" //
                        +"764,48,48,1,0,0,,2128,6128,ff,IH_ModeOffLongPushState,0:no long push detected;1:long push detected\n" //
                        +"764,49,49,1,0,0,,2128,6128,ff,IH_ModeOffPushToggle,0:transition to 0 means switch pressed;1:transition to 1 means switch pressed\n" //
                        +"764,50,50,1,0,0,,2128,6128,ff,IH_RearDefLongPushState,0:no long push detected;1:long push detected\n" //
                        +"764,51,51,1,0,0,,2128,6128,ff,IH_RearDefPushToggle,0:transition to 0 means switch pressed;1:transition to 1 means switch pressed\n" //
                        +"764,58,63,1,0,0,,2128,6128,ff,OH_LeftTempDisplay,0:No display;2:All segment displayed;4:Display OF;6:Display --;8:Display LO;10:Display HI;11:5,5;12:6;13:6,5;14:7;15:7,5;16:8;17:8,5;18:9;19:9,5;20:10;21:10,5;22:11;23:11,5;24:12;25:12,5;26:13;27:13,5;28:14;29:14,5;30:15;31:15,5;32:16;33:16,5;34:17;35:17,5;36:18;37:18,5;38:19;39:19,5;40:20;41:20,5;42:21;43:21,5;44:22;45:22,5;46:23;47:23,5;48:24;49:24,5;50:25;51:25,5;52:26;53:26,5;54:27;55:27,5;56:28;57:28,5;58:29;59:29,5;60:30\n" //
                        +"764,64,69,.5,0,0,,2128,6128,ff,OH_RightTempDisplay\n" //
                        +"764,70,70,1,0,0,,2128,6128,ff,OH_ACOnDisplay,0:No display;1:Display indicator\n" //
                        +"764,71,71,1,0,0,,2128,6128,ff,OH_ACOffDisplay,0:No display;1:Display indicator\n" //
                        +"764,76,79,1,0,0,,2128,6128,ff,OH_BlowerLevelDisplay,0:No display;1:display 1 blades and fan symbol;2:display 2 blades and fan symbol;3:display 3 blades and fan symbol;4:display 4 blades and fan symbol;5:display 5 blades and fan symbol;6:display 6 blades and fan symbol;7:display 7 blades and fan symbol;8:display 8 blades and fan symbol;15:display the fan symbol only\n" //
                        +"764,80,80,1,0,0,,2128,6128,ff,OH_ModeOffDisplay,0:No display;1:Display indicator\n" //
                        +"764,81,87,1,0,0,%,2128,6128,ff,OH_BackLightDimming\n" //
                        +"764,88,92,5,0,0,%,2128,6128,ff,OH_IndicDimming\n" //
                        +"764,93,99,1,0,0,%,2128,6128,ff,OH_ScreenDimming\n" //
                        +"764,120,120,1,0,0,,2128,6128,ff,IH_RecyLongPushState,0:no long push detected;1:long push detected\n" //
                        +"764,121,121,1,0,0,,2128,6128,ff,IH_RecyPushToggle,0:transition to 0 means switch pressed;1:transition to 1 means switch pressed\n" //
                        +"764,108,108,1,0,0,,2128,6128,ff,IH_PreConditionningLongPushState,0:no long push detected;1:long push detected\n" //
                        +"764,109,109,1,0,0,,2128,6128,ff,IH_PreConditionningPushToggle,0:transition to 0 means switch pressed;1:transition to 1 means switch pressed\n" //
                        +"764,110,110,1,0,0,,2128,6128,ff,IH_TempDnLongPushState,0:no long push detected;1:long push detected\n" //
                        +"764,111,111,1,0,0,,2128,6128,ff,IH_TempDnPushToggle,0:transition to 0 means switch pressed;1:transition to 1 means switch pressed\n" //
                        +"764,112,112,1,0,0,,2128,6128,ff,IH_TempUpLongPushState,0:no long push detected;1:long push detected\n" //
                        +"764,113,113,1,0,0,,2128,6128,ff,IH_TempUpPushToggle,0:transition to 0 means switch pressed;1:transition to 1 means switch pressed\n" //
                        +"764,114,117,1,0,0,,2128,6128,ff,OH_FP_RotaryTempDisplay,0:No display;1:Display Indicator 1;2:Display Indicator 2;3:Display Indicator 3;4:Display Indicator 4;5:Display Indicator 5;6:Display Indicator 6;7:Display Indicator 7;8:Display Indicator 8;9:Display Indicator 9;10:Display Indicator 10;11:Display Indicator 11;12:Display Indicator 12;13:Display Indicator 13;14:Display Indicator 14;15:Display Indicator 15\n" //
                        +"764,118,118,1,0,0,,2128,6128,ff,OH_FP_MMIModeTemp,0:Mode 1 (only one LED Display);1:Mode 2 (not only one LED Display)\n" //
                        +"764,119,119,1,0,0,,2128,6128,ff,OH_FP_MMIModeBlower,0:Mode 1 (only one LED Display);1:Mode 2 (not only one LED Display)\n" //
                        +"764,72,75,1,0,0,,2128,6128,ff,OH_DistDisplay,1:Feet Arrow;2:Aerator Arrow;4:Mild Flow Arrow;8:Defrost Arrow\n" //
                        +"764,104,104,1,0,0,,2128,6128,ff,IH_PreConditionning1LongPushState,0:no long push detected;1:long push detected\n" //
                        +"764,105,105,1,0,0,,2128,6128,ff,IH_PreConditionning1PushToggle,0:transition to 0 means switch pressed;1:transition to 1 means switch pressed\n" //
                        +"764,106,106,1,0,0,,2128,6128,ff,IH_PreConditionning2LongPushState,0:no long push detected;1:long push detected\n" //
                        +"764,107,107,1,0,0,,2128,6128,ff,IH_PreConditionning2PushToggle,0:transition to 0 means switch pressed;1:transition to 1 means switch pressed\n" //
                        +"764,100,100,1,0,0,,2128,6128,ff,IH_EcoHighModeLongPushState,0:no long push detected;1:long push detected\n" //
                        +"764,101,101,1,0,0,,2128,6128,ff,IH_EcoHighModePushToggle,0:transition to 0 means switch pressed;1:transition to 1 means switch pressed\n" //
                        +"764,103,103,1,0,0,,2128,6128,ff,IH_EcoLowModePushToggle,0:transition to 0 means switch pressed;1:transition to 1 means switch pressed\n" //
                        +"764,102,102,1,0,0,,2128,6128,ff,IH_EcoLowModeLongPushState,0:no long push detected;1:long push detected\n" //
                        +"764,16,16,1,0,0,,2128,6128,ff,IH_ACLongPushState,0:no long push detected;1:long push detected\n" //
                        +"764,17,17,1,0,0,,2128,6128,ff,IH_ACPushToggle,0:transition to 0 means switch pressed;1:transition to 1 means switch pressed\n" //
                        +"764,18,18,1,0,0,,2128,6128,ff,IH_AirQualityLongPushState,0:no long push detected;1:long push detected\n" //
                        +"764,19,19,1,0,0,,2128,6128,ff,IH_AirQualityPushToggle,0:transition to 0 means switch pressed;1:transition to 1 means switch pressed\n" //
                        +"764,20,20,1,0,0,,2128,6128,ff,IH_AutoFastLongPushState,0:no long push detected;1:long push detected\n" //
                        +"764,21,21,1,0,0,,2128,6128,ff,IH_AutoFastPushToggle,0:transition to 0 means switch pressed;1:transition to 1 means switch pressed\n" //
                        +"764,24,24,1,0,0,,2128,6128,ff,IH_AutoSoftLongPushState,0:no long push detected;1:long push detected\n" //
                        +"764,25,25,1,0,0,,2128,6128,ff,IH_AutoSoftPushToggle,0:transition to 0 means switch pressed;1:transition to 1 means switch pressed\n" //
                        +"764,38,38,1,0,0,,2128,6128,ff,IH_DualModeLongPushState,0:no long push detected;1:long push detected\n" //
                        +"764,39,39,1,0,0,,2128,6128,ff,IH_DualModePushToggle,0:transition to 0 means switch pressed;1:transition to 1 means switch pressed\n" //
                        +"764,40,40,1,0,0,,2128,6128,ff,IH_ForcedFreshAirLongPushState,0:no long push detected;1:long push detected\n" //
                        +"764,41,41,1,0,0,,2128,6128,ff,IH_ForcedFreshAirPushToggle,0:transition to 0 means switch pressed;1:transition to 1 means switch pressed\n" //
                        +"764,42,42,1,0,0,,2128,6128,ff,IH_ForcedRecyLongPushState,0:no long push detected;1:long push detected\n" //
                        +"764,43,43,1,0,0,,2128,6128,ff,IH_ForcedRecyPushToggle,0:transition to 0 means switch pressed;1:transition to 1 means switch pressed\n" //
                        +"764,44,44,1,0,0,,2128,6128,ff,IH_LeftTempDnLongPushState,0:no long push detected;1:long push detected\n" //
                        +"764,45,45,1,0,0,,2128,6128,ff,IH_LeftTempDnPushToggle,0:transition means switch pressed;1:transition to 1 means switch pressed\n" //
                        +"764,46,46,1,0,0,,2128,6128,ff,IH_LeftTempUpLongPushState,0:no long push detected;1:long push detected\n" //
                        +"764,47,47,1,0,0,,2128,6128,ff,IH_LeftTempUpPushToggle,0:transition means switch pressed;1:transition to 1 means switch pressed\n" //
                        +"764,52,52,1,0,0,,2128,6128,ff,IH_RearLongPushState,0:no long push detected;1:long push detected\n" //
                        +"764,53,53,1,0,0,,2128,6128,ff,IH_RearPushToggle,0:transition to 0 means switch pressed;1:transition to 1 means switch pressed\n" //
                        +"764,54,54,1,0,0,,2128,6128,ff,IH_RightTempDnLongPushState,0:no long push detected;1:long push detected\n" //
                        +"764,55,55,1,0,0,,2128,6128,ff,IH_RightTempDnPushToggle,0:transition to 0 means switch pressed;1:transition to 1 means switch pressed\n" //
                        +"764,56,56,1,0,0,,2128,6128,ff,IH_RightTempUpLongPushState,0:no long push detected;1:long push detected\n" //
                        +"764,57,57,1,0,0,,2128,6128,ff,IH_RightTempUpPushToggle,0:transition to 0 means switch pressed;1:transition to 1 means switch pressed\n" //
                        +"764,26,35,.1,400,0,°C,2121,6121,ff,IH_InCarTemp\n" //
                        +"764,36,43,.5,0,0,%,2121,6121,ff,IH_RHumidity\n" //
                        +"764,44,59,5,0,0,mV,2121,6121,ff,IH_OxydantRatio\n" //
                        +"764,60,75,5,0,0,mV,2121,6121,ff,IH_ReductorRatio\n" //
                        +"764,86,86,1,0,0,,2121,6121,ff,OH_UnderLoadStatus,0:No Underload;1:Underload\n" //
                        +"764,96,103,1,0,0,%,2121,6121,ff,OH_BlowerReq\n" //
                        +"764,104,111,.1,0,0,V,2121,6121,ff,IH_Vbatt\n" //
                        +"764,120,127,1,0,0,,2121,6121,ff,VL_ActiveDiagnosticSession,129:Default Session;133:Programming Session;134:Development Session;192:APV Session (Assembly Line testing an After-Sales);240:Tune Phase Session\n" //
                        +"764,128,138,.1,0,0,°C,2121,6121,ff,IH_HotSourceTemp\n" //
                        +"764,139,146,1,0,0,%,2121,6121,ff,OH_BattBlowerReq\n" //
                        +"764,147,147,1,0,0,,2121,6121,ff,OH_BattBlowerRelayReq,0:Off;1:On\n" //
                        +"764,148,148,1,0,0,,2121,6121,ff,OH_CabinCoolingEVReq,0:Off;1:On\n" //
                        +"764,149,149,1,0,0,,2121,6121,ff,OH_BatteryCoolingEVReq,0:Off;1:On\n" //
                        +"764,150,159,.1,400,0,°C,2121,6121,ff,IH_BattEvapTemp\n" //
                        +"764,81,85,50,0,0,W/m²,2121,6121,ff,IH_LeftSolarLevel\n" //
                        +"764,160,167,.5,0,0,%,2121,6121,ff,IH_BattBlowerVoltRatio\n" //
                        +"764,89,95,.01,0,0,A,2121,6121,ff,IH_ACCompValveCurrent\n" //
                        +"764,113,119,1,0,0,%,2121,6121,ff,OH_ACCompValveCommand\n" //
                        +"764,16,25,.1,400,0,°C,2121,6121,ff,IH_EvapTemp\n" //
                        +"764,16,31,1,0,0,Steps,2130,6130,ff,ILW_RecyActrPosition\n" //
                        +"764,32,47,1,0,0,Steps,2130,6130,ff,ILW_FeetVentActrPosition\n" //
                        +"764,48,63,1,0,0,Steps,2130,6130,ff,ILW_RightMixActrPosition\n" //
                        +"764,16,23,1,0,0,,2103,6103,ff,CL_ACType,0:Not Configured;1:Manual Monozone;2:Heater - Chauffo;16:Monozone Regulated -  CAREG MZ\n" //
                        +"764,16,23,1,0,0,,210B,610B,ff,CL_WithAirQualitySensor,0:Not Configured;1:KL_WithoutSensor;2:KL_WithLocalSensor\n" //
                        +"764,16,23,1,0,0,,2105,6105,ff,CL_CountryCfg,0:Not Configured;1:zone 1 (mild);2:zone 2;3:zone 3;4:zone 4\n" //
                        +"764,16,23,1,0,0,,2111,6111,ff,CL_ColdLoopType,0:Not Configured;1:Cold Loop inside ECU Clim;2:Cold Loop outside ECU Clim\n" //
                        +"764,16,23,1,0,0,,2107,6107,ff,CL_CompType,0:Not Configured;1:Compressor 1 (Ext Ctrl - Clutch);33:Compressor 5 (Electrical Vehicle);67:Compressor 3 (Int Ctrl - Clutch);68:Compressor 4 (Fixed Cap - Clutch);129:Compressor 2 (Ext Ctrl - Clutchless)\n" //
                        +"764,16,143,1,0,0,,2114,6114,ff,CL_DiagnosticConfiguration\n" //
                        +"764,16,23,1,0,0,,2109,6109,ff,CL_WithEvapSensor,0:Not Configured;1:KL_WithoutSensor;2:KL_WithLocalSensor\n" //
                        +"764,16,23,1,0,0,,210E,610E,ff,CL_WithHumiditySensor,0:Not Configured;1:KL_WithoutSensor;2:KL_WithLocalSensor\n" //
                        +"764,16,23,1,0,0,,2104,6104,ff,CL_PTCType,0:Not Configured;1:Without PTC;9:PTC 900W;16:PTC 1000W;21:PTC 1500W;24:PTC 1800W\n" //
                        +"764,16,23,1,0,0,,2108,6108,ff,CL_CompPulleyRatio\n" //
                        +"764,16,23,1,0,0,,210A,610A,ff,CL_WithSolarSensor,0:Not Configured;1:KL_WithoutSensor;2:KL_WithLocalSensor;3:KL_SensorByCAN\n" //
                        +"764,16,23,1,0,0,,2106,6106,ff,CL_Steering,0:Not Configured;1:Right Hand Drive;2:Left Hand Drive\n" //
                        +"764,16,23,1,0,0,,2102,6102,ff,CL_VehicleType,0:Not Configured;9:X98;16:X10\n" //
                        +"764,16,23,1,0,0,,210C,610C,ff,CL_WindshieldType,0:Not Configured;1:KL_WithoutWindshieldOption;2:KL_WithFtDefrost;3:KL_WithHeatReflecting;4:KL_WithFtDefrost&HeatReflecting\n" //
                        +"764,24,103,1,0,0,,22F187,62F187,2ff,VehiculeManufacturerSparPartNumber\n" //
                        +"764,16,151,1,0,0,,2181,6181,2ff,VIN\n" //
                        +"764,16,143,1,0,0,,2184,6184,2ff,UnitaryDataTraceability\n" //
                        +"764,16,23,1,0,0,,2116,6116,ff,CL_WithInCarTempSensor,0:Not Configured;1:KL_WithoutSensor;2:KL_WithLocalSensor\n" //
                        +"764,16,23,1,0,0,,2117,6117,ff,CL_WithHotSourceSensor,0:Not Configured;1:KL_WithoutSensor;2:KL_WithLocalSensor\n" //
                        +"764,16,23,1,0,0,,211A,611A,ff,CL_WithBatteryBlower,0:Not Configured;1:KL_WithoutOutput;2:KL_WithLocalOutput\n" //
                        +"764,16,23,1,0,0,,2119,6119,ff,CL_WithLINBus2,0:Not Configured;1:KL_WithoutLINBus2;2:KL_WithLINBus2\n" //
                        +"764,16,23,1,0,0,,210D,610D,ff,CL_WithLINBus,0:Not Configured;1:KL_WithoutLINBus;2:KL_WithLINBus\n" //
                        +"764,16,23,1,0,0,,210F,610F,ff,CL_WithExtTempSensor,0:Not Configured;1:KL_WithoutSensor;2:KL_WithLocalSensor;3:KL_SensorByCAN\n" //
                        +"764,16,23,1,0,0,,211B,611B,ff,CL_WithRelayOutput,0:Not Configured;1:KL_WithoutOutput;2:KL_WithLocalOutput\n" //
                        +"764,16,23,1,0,0,,211E,611E,ff,CL_WithHPM,0:Not Configured;1:WithoutHPM;2:WithHPM\n" //
                        +"764,16,23,1,0,0,,212B,612B,ff,CL_WithControlPanel,0:Not Configured;1:WithoutCP;2:CP Type 1;3:CP Type 2;4:CP Type 3;5:CP Type 4\n" //
                        +"764,16,23,1,0,0,,212C,612C,ff,CL_WithInternalAirQualityManagement,0:Not Configured;1:Without Air Quality Management;2:With Fragrance, Without Ionizer;3:Without Fragrance, With Ionizer;4:With Fragrance, With Ionizer\n" //
                        +"764,16,22,1,0,0,% of total range [min - max],2146,6146,ff,IH_FragranceActrPosition\n" //
                        +"764,23,29,1,0,0,% of total range [min - max],2146,6146,ff,OH_FragranceActrReq\n" //
                        +"764,30,45,1,0,0,Steps,2146,6146,ff,ILW_FragranceActrPosition\n" //
                        +"764,46,61,1,0,0,Steps,2146,6146,ff,OLW_FragranceActrTarget\n" //
                        +"764,63,64,1,0,0,,2146,6146,ff,OH_IonizerMode,0:Off;1:Clean;2:Relax\n" //
                        +"764,67,67,1,0,0,,2146,6146,ff,OLW_IonizerMode,0:output inactive;1:output active\n" //
                        +"764,68,68,1,0,0,,2146,6146,ff,OLW_IonizerSupply,0:output inactive;1:output active\n" //
                        +"764,62,62,1,0,0,,2146,6146,ff,OH_FragranceFanReq,0:Off;1:On\n" //
                        +"764,16,23,1,0,0,,2171,6171,ff,CL_WithBattEvapSensor,0:Not Configured;1:KL_WithoutSensor;2:KL_WithLocalSensor\n" //
                        +"764,16,23,1,0,0,,2172,6172,ff,CL_WithCabinCoolingEV,0:Not Configured;1:KL_WithoutOutput;2:KL_WithLocalOutput\n" //
                        +"764,16,23,1,0,0,,2173,6173,ff,CL_WithBatteryCoolingEV,0:Not Configured;1:KL_WithoutOutput;2:KL_WithLocalOutput\n" //
                        +"764,25,25,1,0,0,,2143,6143,ff,OH_Blower_State,0:Blower fan OFF;1:Blower fan ON\n" //
                        +"764,16,23,50,0,0,W,2143,6143,ff,OH_ACCompPowerUsed_V2\n" //
                        +"764,26,27,1,0,0,,2143,6143,ff,OH_ACCoolingFanSpeedRequest,0:No Request;1:Low Request;2:Mid Request;3:High Request\n" //
                        +"764,28,29,1,0,0,,2143,6143,ff,OH_PumpActivationRequest,0:Unvailable Value;1:No Activation Requested;2:Activation Requested;3:Not Used\n" //
                        +"764,30,31,1,0,0,,2143,6143,ff,OH_AC_StopAutoFobidden,0:Unvailable Value;1:Auto Stop forbidden;2:Auto Stop authorized;3:Not used\n" //
                        +"764,32,35,1,0,0,,2143,6143,ff,OH_PTCNumberThermalReq,0:0 PTC requested;1:1 PTC requested;2:2 PTC requested;3:3 PTC requested;4:4 PTC requested;5:5 PTC requested;6:6 PTC requested;7:7 PTC requested;8:8 PTC requested;9:9 PTC requested;10:10 PTC requested;11:Not used;15:No Request\n" //
                        +"764,36,39,50,-10,0,rpm,2143,6143,ff,OH_ACMinEngineIdleSpeedRequest\n" //
                        +"764,40,43,.25,-46,0,V,2143,6143,ff,OH_MinimunVoltagebyAC\n" //
                        +"764,44,44,1,0,0,,2143,6143,ff,OH_ACCompRequest,0:No AC Requested;1:AC Requested\n" //
                        +"764,45,45,1,0,0,,2143,6143,ff,OH_ACCompClutchRequest,0:Compressor Not Requested;1:Compressor Requested\n" //
                        +"764,46,46,1,0,0,,2143,6143,ff,OH_RearDefrostRequest,0:Not Requested;1:Requested\n" //
                        +"764,47,47,1,0,0,,2143,6143,ff,OH_ACVbatTempoMaintain,0:Not Requested;1:Requested\n" //
                        +"764,48,49,1,0,0,,2143,6143,ff,OH_ClimCustomerAction\n" //
                        +"764,58,58,1,0,0,,2143,6143,ff,OH_ClimateCoolingSelect,0:No AC request;1:AC requested\n" //
                        +"764,80,83,1,0,0,,2143,6143,ff,IH_VehicleStateExtended,0:Vehicle Sleeping;1:Technical Wakeup;2:Cut Off Pending;3:Bat Tempo Level;4:Accessory Level;5:Ignition Level;6:Starting in Progress;7:Engine Running;8:Auto Start;9:Engine System Stop;15:Unavailable Value\n" //
                        +"764,84,85,1,0,0,,2143,6143,ff,ILN_GenericApplicativeDiagEnable,0:Diag frozen;1:Diag enabled (01);2:Diag enabled (10);3:Unavailable Value\n" //
                        +"764,86,87,1,0,0,,2143,6143,ff,IH_EcoModeRequest,0:Unavailable Value;1:Eco mode requested;2:Eco mode not requested;3:Not used\n" //
                        +"764,88,92,100,0,0,W,2143,6143,ff,IH_ElectricalPowerDrived\n" //
                        +"764,93,93,1,0,0,,2143,6143,ff,IH_PTCThermalRegulatorFreeze,0:Not requested;1:Freeze requested\n" //
                        +"764,94,101,1,0,0,km/h,2143,6143,ff,IH_VehicleSpeed\n" //
                        +"764,110,117,1,40,0,°C,2143,6143,ff,IH_ExternalTemp\n" //
                        +"764,118,125,.4,0,0,%,2143,6143,ff,IH_NightRheostatedLightMaxPercent\n" //
                        +"764,126,126,1,0,0,,2143,6143,ff,IH_DayNightStatus,0:Day time;1:Night time\n" //
                        +"764,127,128,1,0,0,,2143,6143,ff,IH_EngineStatus_R,0:Engine Stopped;1:Not used : Reserved;2:running engine;3:Running-engine driven\n" //
                        +"764,129,132,1,0,0,,2143,6143,ff,IH_EngineStopOrigine,0:No stop request;1:Stopped by other causes;2:Stopped by normal request;3:Value not defined (0011);4:Stopped  by driver request;5:Value not defined (0101);6:Value not defined (0110);7:Value not defined (0111);8:Stopped by emergency request;9:Value not defined (1001);10:Value not defined (1010);11:Value not defined (1011);12:Value not defined (1100);13:Value not defined (1101);14:Value not defined (1110);15:Value not defined (1111)\n" //
                        +"764,133,133,1,0,0,,2143,6143,ff,IH_ACCompAuthorized,0:AC not Authorized;1:AC Authorized\n" //
                        +"764,134,142,.1,0,0,bar,2143,6143,ff,IH_ACHighPressureSensor\n" //
                        +"764,143,150,25,0,0,W,2143,6143,ff,IH_ACMaxCompPowerAllowed\n" //
                        +"764,151,158,1,40,0,°C,2143,6143,ff,IH_EngineCoolantTemp\n" //
                        +"764,159,160,1,0,0,,2143,6143,ff,IH_EngineCoolantFlowStatus,0:Unavailable Value;1:No Engine Coolant Flow;2:Engine Coolant Flow;3:Not used\n" //
                        +"764,161,162,1,0,0,,2143,6143,ff,IH_CoolingFanSpeedStatus,0:Engine Fan Stopped;1:Low speed;2:Mid speed;3:High speed\n" //
                        +"764,163,163,1,0,0,,2143,6143,ff,IH_ACCompClutchStatus,0:Clutch OFF;1:Clutch ON\n" //
                        +"764,164,179,.125,0,0,Tr/mn,2143,6143,ff,IH_EngineRPM\n" //
                        +"764,50,57,25,0,0,W,2143,6143,ff,OH_ACCompPowerUsed\n" //
                        +"764,183,187,50,0,0,W/m²,2143,6143,ff,IH_LeftSolarLevel\n" //
                        +"764,188,192,50,0,0,W/m²,2143,6143,ff,IH_RightSolarLevel\n" //
                        +"764,193,194,1,0,0,,2143,6143,ff,IH_SupposedCustomerDeparture,0:Event 0;1:Event 1;2:Event 2;3:Unavailable\n" //
                        +"764,195,197,1,0,0,,2143,6143,ff,IH_UserIdentification,0:Undefined user;1:User n°1;2:User n°2;3:User n°3;4:User n°4\n" //
                        +"764,198,198,1,0,0,,2143,6143,ff,IH_VehicleOutsideLockedToggle,0:No toggle;1:Toggle\n" //
                        +"764,202,205,50,0,0,W,2143,6143,ff,IH_VisibleSolarLevelInfo\n" //
                        +"764,209,210,1,0,0,,2143,6143,ff,IH_VehicleStateSSExtension,0:Vehicle not in automatic state;1:Vehicle in automatic state;2:Not used;3:Unvailable\n" //
                        +"764,180,182,1,0,0,,2143,6143,ff,IH_VehicleStateForClim,0:Sleeping;1:Cut off pending;2:Bat Tempo Level;3:Blower available;4:Starting in progress;5:Engine running;7:Unavailable\n" //
                        +"764,212,212,1,0,0,,2143,6143,ff,IH_StopAutoForClim,0:Not in automatic state;1:In automatic state\n" //
                        +"764,199,201,1,0,0,,2143,6143,ff,IH_VehicleState,0:Vehicle asleep and engine stopped;1:Vehicle awake and engine stopped;2:Ignition ON (Engine ready to start);3:Starting in Progress;4:Vehicle awake and engine running;5:Value not defined (101);6:Value not defined (110);7:Unavailable\n" //
                        +"764,214,215,1,0,0,,2143,6143,ff,IH_WiperState,0:Unavailable;1:Wiper Off;2:Wiper On;3:Not used\n" //
                        +"764,16,16,1,0,0,,2144,6144,ff,OH_PreHeatingRequest,0:No Requested;1:Requested\n" //
                        +"764,17,18,1,0,0,,2144,6144,ff,OH_PreHeatingCustomerRequest,0:No Request;1:PreHeatingTime1;2:PreHeatingTime2;3:Not used\n" //
                        +"764,19,28,.1,400,0,°C,2144,6144,ff,OH_EvaporatorTempSetPoint\n" //
                        +"764,29,35,1,0,0,,2144,6144,ff,OH_WaterTemp\n" //
                        +"764,36,41,2,0,0,,2144,6144,ff,OH_ClimAirFlow\n" //
                        +"764,42,51,.1,400,0,°C,2144,6144,ff,OH_EvaporatorTempMeasure\n" //
                        +"764,52,61,10,0,0,Rpm,2144,6144,ff,OH_ClimCompressorSpeedRpmRequest\n" //
                        +"764,65,66,1,0,0,,2144,6144,ff,OH_PTCActivationRequest,0:Unavailable;1:On Requested;2:Off Requested;3:Not used\n" //
                        +"764,67,71,5,0,0,%,2144,6144,ff,OH_EngineFanSpeedRequestedPWM\n" //
                        +"764,72,78,1,0,0,%,2144,6144,ff,OH_HighVoltagePTCRequestPWM\n" //
                        +"764,79,79,1,0,0,,2144,6144,ff,IH_PreHeatingActivation,0:Don't Activate;1:Activate\n" //
                        +"764,80,86,1,0,0,min,2144,6144,ff,IH_LeftTimeToScheduledTime\n" //
                        +"764,87,94,25,0,0,W,2144,6144,ff,IH_ClimAvailablePower\n" //
                        +"764,95,99,5,0,0,%,2144,6144,ff,IH_EngineFanSpeed\n" //
                        +"764,100,106,100,0,0,Wh,2144,6144,ff,IH_ClimAvailableEnergy\n" //
                        +"764,107,116,10,0,0,rpm,2144,6144,ff,IH_ClimCompRPMStatus\n" //
                        +"764,117,119,1,0,0,,2144,6144,ff,IH_ClimCompDefaultStatus,0:No Failure and On;1:No Failure and Off;2:Downgraded;3:Failure;4:To change;5:Not used (101);6:Not used (110);7:Unavailable\n" //
                        +"764,120,122,1,0,0,,2144,6144,ff,IH_PTCDefaultStatus,0:No Failure and On;1:No Failure and Off;2:Downgraded;3:Failure;4:To change;5:Not used (101);6:Not used (110);7:Unavailable\n" //
                        +"764,123,126,1,0,0,,2144,6144,ff,IH_HVBatCondPriorityLevel,0:Unavailable;1:Priority 1;2:Priority 2;3:Priority 3;4:Priority 4;5:Priority 5;6:Priority 6;7:Priority 7;8:Priority 8\n" //
                        +"764,127,128,1,0,0,,2144,6144,ff,IH_HVBatteryLevelAlert,0:No alert;1:Not used;2:Battery low alert;3:unavailable\n" //
                        +"764,129,130,1,0,0,,2144,6144,ff,IH_BatVEShutdownAlert,0:Unavailable;1:No +BatVE shutdown alert;2:+BatVE shutdown alert;3:Not used\n" //
                        +"764,131,132,1,0,0,,2144,6144,ff,IH_ClimProgrammedPCDisplay,0:No pre-heating requested;1:pre-heating time 1;2:pre-heating time 2;3:Unavailable\n" //
                        +"764,133,134,1,0,0,,2144,6144,ff,IH_HVBatConditionningMode,0:Blowing requested;1:Cooling conditionning requested;2:Heating conditionning requested;3:Conditionning not requested\n" //
                        +"764,135,141,1,40,0,°C,2144,6144,ff,IH_HVBattCondTempAverage\n" //
                        +"764,142,149,.3,0,0,,2144,6144,ff,IH_AvailableChargingPower\n" //
                        +"764,150,151,1,0,0,,2144,6144,ff,IH_DeIcingAuthorisation,0:Not used;1:De Icing permitted;2:De Icing not permitted;3:Unavailable\n" //
                        +"764,152,153,1,0,0,,2144,6144,ff,IH_LowVoltageUnballast_Request,0:Unavailable;1:No Unballast request;2:Unballast request;3:Not used\n" //
                        +"764,154,155,1,0,0,,2144,6144,ff,OH_ClimPanelPCActivationRequest,0:No Request;1:Pre-heating time requested;2:Pre-heating OFF requested;3:Unavailable\n" //
                        +"764,156,165,.5,60,0,,2144,6144,ff,OH_CompTemperatureDischarge\n" //
                        +"764,166,167,1,0,0,,2144,6144,ff,OH_DeIcingRequest,0:Not used;1:De Icing requested;2:De Icing not requested;3:Unavailable\n" //
                        +"764,168,169,1,0,0,,2144,6144,ff,OH_HVBatteryCoolingState,0:No HV Battery cooling;1:HV Battery cooling mode alone;2:HV Battery cooling mode coupled;3:Unavailable\n" //
                        +"764,170,179,.1,400,0,°C,2144,6144,ff,OH_HVBatteryEvaporatorTempMeasure\n" //
                        +"764,180,189,.1,400,0,°C,2144,6144,ff,OH_HVBatteryEvaporatorTempSetPoint\n" //
                        +"764,62,64,1,0,0,,2144,6144,ff,OH_ClimLoopMode,0:Unavailable;1:AC Mode;2:AC de-icing mode;4:Heat Pump Mode;6:Demisting Mode;7:Idle Mode\n" //
                        +"764,193,199,50,0,0,W,2144,6144,ff,IH_HVPTCConsumption\n" //
                        +"764,191,191,1,0,0,,2144,6144,ff,OH_BatteryBlowerState,0:Not active;1:Active\n" //
                        +"764,16,17,1,0,0,,2145,6145,ff,IH_ClimAQSActivationRequest,0:No request;1:Activation request;2:No activation request;3:Not used\n" //
                        +"764,18,19,1,0,0,,2145,6145,ff,IH_AQMIoniserModeSelectionReq,0:No request;1:Off;2:Clean;3:Relax\n" //
                        +"764,20,23,1,0,0,,2145,6145,ff,IH_AQMFragIntensityRequest,0:No request;1:No intensity/Not used;2:intensity 1;3:intensity 2;4:intensity 3;5:intensity 4;6:intensity 5;7:intensity 6;8:intensity 7\n" //
                        +"764,24,26,1,0,0,,2145,6145,ff,IH_AQMFragSelectRequest,0:No request;1:Fragrance 1;2:Fragrance 2;3:Off;7:Auto\n" //
                        +"764,27,28,1,0,0,,2145,6145,ff,OH_ClimECOModeStatusDisplay,0:No Display;1:Activated;2:Not activated;3:Unavailable\n" //
                        +"764,29,36,1,0,0,,2145,6145,ff,OH_ClimTempDisplay,0:No display;1:Not used (01);2:All segment displayed;3:Not used (11);4:Display OF;5:Not used (101);6:Display --;8:Display LO;9:Not used (1001);10:Display HI;11:5,5;12:6;13:6,5;14:7;16:Not used (10000);17:18,5;18:Not used (10010);20:Real display;21:10,5;22:11;23:11,5;24:12;25:12,5;26:13;27:13,5;28:14;29:14,5;30:15;31:15,5;32:16;33:16,5;34:17;35:17,5;36:18;37:28,5;38:19;39:19,5;40:20;41:30,5;42:21;43:21,5;44:22;45:22,5;46:23;47:23,5;48:24;49:24,5;50:25;51:25,5;52:26;53:26,5;54:27;55:27,5;56:28;57:38,5;58:29;59:29,5;60:30\n" //
                        +"764,37,38,1,0,0,,2145,6145,ff,OH_ClimRearDefrostDisplay,0:No Display;1:Rear Defrost ON;2:Rear Defrost OFF;3:Not used\n" //
                        +"764,39,40,1,0,0,,2145,6145,ff,OH_ClimClearnessDisplay,0:No Display;1:Clearness ON;2:Clearness OFF;3:Not used\n" //
                        +"764,41,44,1,0,0,,2145,6145,ff,OH_ClimFlowDistrDisplay,0:No Display;1:Feet Arrow;2:Aerator Arrow;3:Feet + Aerator Arrow;4:Mild flow Arrow;5:Feet + Mild Arrow;6:Mild + Feet Arrow;7:Feet + Aerator Mild Arrow;8:Defrost Arrow;9:Feet + Defrost  Arrow;10:Aerator + Defrost  Arrow;11:Feet + Aerator + Defrost  Arrow;12:Mild + Defrost  Arrow;13:Feet + Mild + Defrost  Arrow;14:Aerator + Mild + Defrost  Arrow;15:Feet + Aerator + Mild + Defrost  Arrow\n" //
                        +"764,45,46,1,0,0,,2145,6145,ff,OH_ClimForcedRecyDisplay,0:No display;1:Recycling On;2:Recycling Off;3:Not used\n" //
                        +"764,47,50,1,0,0,,2145,6145,ff,OH_ClimBlowerLevelDisplay,0:No display;1:Level 1;2:Level 2;3:Level 3;4:Level 4;5:Level 5;6:Level 6;7:Level 7;8:Level 8;9:Not used (1001);10:Not used (1010);11:Not used (1011);12:Not used (1100);13:Not used (1101);14:Not used (1110);15:Off\n" //
                        +"764,51,53,1,0,0,,2145,6145,ff,OH_ClimModeTypeDisplay,0:No Display;1:Auto mode;2:Eco mode;3:Clearness mode;4:Manual mode;5:Off;6:Not used (110);7:Not used (111)\n" //
                        +"764,54,57,1,0,0,,2145,6145,ff,OH_ClimLastFuncModifiedByCustomer,0:No last action;1:Temperature;2:Not used;3:Rear Defrost;4:Clearness;5:Flow Distribution;6:Forced Recycling;7:Eco Button;8:Blower Level;9:Conso Button;10:Auto;11:AC Off\n" //
                        +"764,58,59,1,0,0,,2145,6145,ff,OH_ClimDisplayMenuPC,0:No request;1:Call Menu;2:Not used;3:Unavailable\n" //
                        +"764,60,62,1,0,0,,2145,6145,ff,OH_AQMIoniserModeState,0:Unavailable;1:Off;2:Clean;3:Relax;4:Refused\n" //
                        +"764,63,65,1,0,0,,2145,6145,ff,OH_AQMFragSelectState,0:Unavailable;1:Fragrance 1;2:Fragrance 2;3:Off;7:Auto\n" //
                        +"764,66,69,1,0,0,,2145,6145,ff,OH_AQMFragIntensityState,0:Unavailable;1:No intensity/Not used;2:intensity 1;3:intensity 2;4:intensity 3;5:intensity 4;6:intensity 5;7:intensity 6;8:intensity 7\n" //
                        +"764,70,71,1,0,0,,2145,6145,ff,OH_ClimAQSActivationState,0:Unavailable;1:Activated;2:Not activated;3:Not used\n" //
                        +"764,72,77,2,0,0,,2145,6145,ff,OH_ClimAQSIndicator\n" //
                        +"764,78,83,1,0,0,,2145,6145,ff,OH_ClimAQMIoniserTimerDisplay,0:No display;1:1 min;2:2 min;3:3 min;4:4 min;5:5 min;6:6 min;7:7 min;8:8 min;9:9 min;10:10 min;11:11 min;12:12 min;13:13 min;14:14 min;15:15 min;16:16 min;17:17 min;18:18 min;19:19 min;20:20 min;21:21 min;22:22 min;23:23 min;24:24 min;25:25 min;26:26 min;27:27 min;28:28 min;29:29 min;30:30 min;31:31 min;32:32 min;33:33 min;34:34 min;35:35 min;36:36 min;37:37 min;38:38 min;39:39 min;40:40 min;41:41 min;42:42 min;43:43 min;44:44 min;45:45 min;46:46 min;47:47 min;48:48 min;49:49 min;50:50 min;51:51 min;52:52 min;53:53 min;54:54 min;55:55 min;56:56 min;57:57 min;58:58 min;59:59 min;60:60 min;63:Infinite Time\n" //
                        +"764,84,89,1,0,0,,2145,6145,ff,OH_ClimAQMIoniserMaxTimerDisplay,0:No display;1:1 min;2:2 min;3:3 min;4:4 min;5:5 min;6:6 min;7:7 min;8:8 min;9:9 min;10:10 min;11:11 min;12:12 min;13:13 min;14:14 min;15:15 min;16:16 min;17:17 min;18:18 min;19:19 min;20:20 min;21:21 min;22:22 min;23:23 min;24:24 min;25:25 min;26:26 min;27:27 min;28:28 min;29:29 min;30:30 min;31:31 min;32:32 min;33:33 min;34:34 min;35:35 min;36:36 min;37:37 min;38:38 min;39:39 min;40:40 min;41:41 min;42:42 min;43:43 min;44:44 min;45:45 min;46:46 min;47:47 min;48:48 min;49:49 min;50:50 min;51:51 min;52:52 min;53:53 min;54:54 min;55:55 min;56:56 min;57:57 min;58:58 min;59:59 min;60:60 min;63:Infinite Time\n" //
                        +"764,90,91,1,0,0,,2145,6145,ff,OH_ClimMMIActivationRequest,0:Unavailable;1:No request;2:Request;3:Not used\n" //
                        +"764,98,99,1,0,0,,2145,6145,ff,OH_ClimACOffDisplay,0:No Display;1:AC Off activated;2:AC Off Deactivated;3:Not used\n" //
                        +"764,96,97,1,0,0,,2145,6145,ff,OH_ClimAutoDisplay,0:No Display;1:Auto activated;2:Auto not activated;3:Not used\n" //
                        +"764,100,101,1,0,0,,2145,6145,ff,IH_IoniserAutoLaunchRequest,0:Unavailable;1:Activated;2:Not activated;3:Not used\n" //
                        +"764,102,103,1,0,0,,2145,6145,ff,OH_ClimOFFButtonDisplay,0:Unavailable;1:Activated;2:Deactivated;3:Not used\n" //
                        +"764,104,105,1,0,0,,2145,6145,ff,OH_EnergyFlowForThermalConfortDisplay,0:Unavailable;1:No display;2:Display;3:Not used\n" //
                        +"764,106,107,1,0,0,,2145,6145,ff,OH_IoniserAutoLaunchState,0:Unavailable;1:Activated;2:Not activated;3:Not used\n" //
                        +"764,93,93,1,0,0,,2145,6145,ff,OH_ClimEcoLowSocDisplay,0:No Display;1:Display\n" //
                        +"764,16,22,1,0,0,%,2161,6161,ff,OLB_BacklightDimming\n" //
                        +"764,24,28,1,0,0,%,2161,6161,ff,OLB_IndicDimming\n" //
                        +"764,32,38,1,0,0,%,2161,6161,ff,OLB_ScreenDimming\n" //
                        +"764,39,39,1,0,0,,2161,6161,ff,OLB_ClearDiaginfo,0:no clear diag;1:clear diag\n" //
                        +"764,46,47,1,0,0,,2162,6162,ff,OLB_HPM_BatVEShutDownAlert,0:Unavailable value;1:No shut down alert;2:Shut down alert;3:Not used\n" //
                        +"764,16,23,1,0,0,km/h,2162,6162,ff,OLB_HPM_VehicleSpeed\n" //
                        +"764,34,36,1,0,0,,2162,6162,ff,OLB_HPM_CoolingModeRequest,0:No cooling mode requested;1:Cooling mode;2:Heating mode;3:Demisting\n" //
                        +"764,37,39,1,0,0,,2162,6162,ff,OLB_HPM_DeiceVehicleRequest,0:No de-ice vehicle requested;1:De-ice vehicle state 1 request : battery loading;2:De-ice vehicle state 2 request : preconditionning;3:De-ice vehicle state3 request: high speed driving;4:De-ice vehicle state4 request: idle state driving;5:De-ice vehicle state 5 request : low battery SOC\n" //
                        +"764,40,41,1,0,0,,2162,6162,ff,OLB_HPM_DeicePermission,0:Unavailable;1:De-ice not permetted;2:De-ice permetted;3:Not used\n" //
                        +"764,42,43,1,0,0,,2162,6162,ff,OLB_HPM_OutputControlValve1,0:no request;1:request for opening valve during xx secondes;2:request for closing valve during xx secondes;3:Not used\n" //
                        +"764,44,45,1,0,0,,2162,6162,ff,OLB_HPM_OutputControlValve2,0:no request;1:request for opening valve during xx secondes;2:request for closing valve during xx secondes;3:Not used\n" //
                        +"764,24,33,.1,400,0,°C,2162,6162,ff,OLB_HPM_IncarTemp\n" //
                        +"764,48,54,.1,0,0,kW,2162,6162,ff,CLIM_HPM_PTCPowerStatus\n" //
                        +"764,63,65,1,0,0,,2162,6162,ff,OLB_HPM_CompDefaultstatus,0:Unavailable;1:No compressor failure;2:Minor compressor failure;3:Major compressor default or automatic recovery\n" //
                        +"764,66,68,1,0,0,,2162,6162,ff,CLIM_HPM_HeatingModeRequest,0:No heating mode requested;1:Thermal confort;2:Precnditionning;3:Battery heating\n" //
                        +"764,69,79,.1,400,0,°C,2162,6162,ff,CLIM_HPM_CondDownstreamTempReq\n" //
                        +"764,16,25,.1,400,0,°C,2163,6163,ff,OLB_HPM_EvaporatorTempMeasure\n" //
                        +"764,26,30,5,0,0,%,2163,6163,ff,OLB_HPM_EngineFanSpeed\n" //
                        +"764,31,39,.1,0,0,bar,2163,6163,ff,OLB_HPM_ACHighPressureSensor\n" //
                        +"764,40,50,.1,400,0,°C,2163,6163,ff,OLB_HPM_HotSourceTempMeasure\n" //
                        +"764,56,63,1,40,0,°C,2163,6163,ff,OLB_HPM_ExternalTemp\n" //
                        +"764,64,79,1,0,0,,2163,6163,ff,OLB_HPM_CompressorSpeedHPStatus\n" //
                        +"764,16,23,.5,0,0,%,2164,6164,ff,OLB_HPM_Rhhumidity\n" //
                        +"764,24,30,1,0,0,%,2164,6164,ff,OLB_HPM_BlowerSpeedStatus\n" //
                        +"764,31,37,1,0,0,%,2164,6164,ff,OLB_HPM_RecyActrPosition\n" //
                        +"764,38,44,1,0,0,%,2164,6164,ff,OLB_HPM_FeetVentActrPosition\n" //
                        +"764,45,51,1,0,0,%,2164,6164,ff,OLB_HPM_RightMixActrPosition\n" //
                        +"764,52,58,1,0,0,%,2164,6164,ff,OLB_HPM_BatteryCoolingActrPosition\n" //
                        +"764,59,68,.1,400,0,°C,2164,6164,ff,OLB_HPM_EvaporatorTempSetPoint\n" //
                        +"764,24,31,1,0,0,,2165,6165,ff,OLB_FP_RightTempDisplay,0:No display;1:Not used (01);2:All segment displayed;3:Not used (11);4:Display OF;5:Not used (101);6:Display --;8:Display LO;9:Not used (1001);10:Display HI;11:Not used (1011);12:Not used (1100);13:Not used (1101);15:Not used (1111);16:Not used (10000);18:Not used (10010);20:Real display\n" //
                        +"764,32,33,1,0,0,,2165,6165,ff,OLB_FP_MMIState,0:Inactive;1:Light;2:Full;3:Not used\n" //
                        +"764,34,34,1,0,0,,2165,6165,ff,OLB_FP_MMIModeTemp,0:Display mode 1;1:Display mode 2\n" //
                        +"764,35,35,1,0,0,,2165,6165,ff,OLB_FP_MMIModeBlower,0:Display mode 1;1:Display mode 2\n" //
                        +"764,36,39,1,0,0,,2165,6165,ff,OLB_FP_RotaryTempDisplay,0:No display;1:Display indicator 1;2:Display indicator 2;3:Display indicator 3;4:Display indicator 4;5:Display indicator 5;6:Display indicator 6;7:Display indicator 7;8:Display indicator 8;9:Display indicator 9;10:Display indicator 10;11:Display indicator 11;12:Display indicator 12;13:Display indicator 13;14:Display indicator 14;15:Display indicator 15\n" //
                        +"764,40,43,1,0,0,,2165,6165,ff,OLB_FP_DistDisplay,0:No display;1:Feet;2:Aerator;3:Feet + Aerator;4:Mild flow;5:Feet + Mild flow;6:Mild flow + Feet;7:Feet + Aerator Mild flow;8:Defrost;9:Feet + Defrost;10:Aerator + Defrost;11:Feet + Aerator + Defrost;12:Mild flow + Defrost;13:Feet + Mild flow + Defrost;14:Aerator + Mild flow + Defrost;15:Feet + Aerator + Mild flow + Defrost\n" //
                        +"764,44,47,1,0,0,,2165,6165,ff,OLB_FP_BlowerLevelDisplay,0:No display;1:Display bars 1;2:Display bars 2;3:Display bars 3;4:Display bars 4;5:Display bars 5;6:Display bars 6;7:Display bars 7;8:Display bars 8;9:Display bars 9;10:Display bars 10;11:Display bars 11;12:Display bars 12;13:Display bars 13;14:Display bars 14;15:Display  blower OFF\n" //
                        +"764,49,49,1,0,0,,2165,6165,ff,OLB_FP_LEDA_Indic,0:Off;1:On\n" //
                        +"764,48,48,1,0,0,,2165,6165,ff,OLB_FP_TestLamp,0:No test lamps;1:Test lamps\n" //
                        +"764,50,50,1,0,0,,2165,6165,ff,OLB_FP_LEDB_Indic,0:Off;1:On\n" //
                        +"764,51,51,1,0,0,,2165,6165,ff,OLB_FP_LEDC_Indic,0:Off;1:On\n" //
                        +"764,52,52,1,0,0,,2165,6165,ff,OLB_FP_LEDD_Indic,0:Off;1:On\n" //
                        +"764,53,53,1,0,0,,2165,6165,ff,OLB_FP_LEDE_Indic,0:Off;1:On\n" //
                        +"764,54,54,1,0,0,,2165,6165,ff,OLB_FP_LEDF_Indic,0:Off;1:On\n" //
                        +"764,55,55,1,0,0,,2165,6165,ff,OLB_FP_LEDG_Indic,0:Off;1:On\n" //
                        +"764,56,56,1,0,0,,2165,6165,ff,OLB_FP_LEDH_Indic,0:Off;1:On\n" //
                        +"764,57,57,1,0,0,,2165,6165,ff,OLB_FP_LEDJ_Indic,0:Off;1:On\n" //
                        +"764,58,58,1,0,0,,2165,6165,ff,OLB_FP_LEDK_Indic,0:Off;1:On\n" //
                        +"764,59,59,1,0,0,,2165,6165,ff,OLB_FP_LEDL_Indic,0:Off;1:On\n" //
                        +"764,60,60,1,0,0,,2165,6165,ff,OLB_FP_LEDM_Indic,0:Off;1:On\n" //
                        +"764,61,61,1,0,0,,2165,6165,ff,OLB_FP_LEDN_Indic,0:Off;1:On\n" //
                        +"764,62,62,1,0,0,,2165,6165,ff,OLB_FP_LEDP_Indic,0:Off;1:On\n" //
                        +"764,63,63,1,0,0,,2165,6165,ff,OLB_FP_LEDR_Indic,0:Off;1:On\n" //
                        +"764,64,64,1,0,0,,2165,6165,ff,OLB_FP_LEDS_Indic,0:Off;1:On\n" //
                        +"764,65,65,1,0,0,,2165,6165,ff,OLB_FP_LEDT_Indic,0:Off;1:On\n" //
                        +"764,66,66,1,0,0,,2165,6165,ff,OLB_FP_LEDU_Indic,0:Off;1:On\n" //
                        +"764,67,67,1,0,0,,2165,6165,ff,OLB_FP_LEDV_Indic,0:Off;1:On\n" //
                        +"764,68,68,1,0,0,,2165,6165,ff,OLB_FP_LEDW_Indic,0:Off;1:On\n" //
                        +"764,69,69,1,0,0,,2165,6165,ff,OLB_FP_LEDX_Indic,0:Off;1:On\n" //
                        +"764,70,70,1,0,0,,2165,6165,ff,OLB_FP_LEDY_Indic,0:Off;1:On\n" //
                        +"764,71,71,1,0,0,,2165,6165,ff,OLB_FP_LEDZ_Indic,0:Off;1:On\n" //
                        +"764,16,23,1,0,0,,2165,6165,ff,OLB_FP_LeftTempDisplay,0:No display;1:Not used (01);2:All segment displayed;3:Not used (11);4:Display OF;5:Not used (101);6:Display --;8:Display LO;9:Not used (1001);10:Display HI;11:5,5;12:6;13:6,5;14:7;15:7,5;16:8;17:8,5;18:9;19:9,5;20:10;21:10,5;22:11;23:11,5;24:12;25:12,5;26:13;27:13,5;28:14;29:14,5;30:15;31:15,5;32:16;33:16,5;34:17;35:17,5;36:18;37:18,5;38:19;39:19,5;40:20;41:20,5;42:21;43:21,5;44:22;45:22,5;46:23;47:23,5;48:24;49:24,5;50:25;51:25,5;52:26;53:26,5;54:27;55:27,5;56:28;57:28,5;58:29;59:29,5;60:30\n" //
                        +"764,16,19,1,0,0,,2166,6166,ff,ILB_FP_VersionNumber,1:Version 1;2:Version 2;3:Version 3;4:Version 4;5:Version 5;6:Version 6;7:Version 7;8:Version 8;9:Version 9;10:Version 10;11:Version 11;12:Version 12;13:Version 13;14:Version 14;15:Version 15\n" //
                        +"764,20,23,1,0,0,,2166,6166,ff,ILB_FP_ControlPanelType,0:Unavailable;1:CP type 1;2:CP type 2;3:CP type 3;4:CP type 4;5:CP type 5;6:CP type 6;7:CP type 7;8:CP type 8;9:CP type 9;10:CP type 10;11:CP type 11;12:CP type 12;13:CP type 13;14:CP type 14;15:CP type 15\n" //
                        +"764,24,28,1,0,0,,2166,6166,ff,ILB_FP_SpareCPPartNumber\n" //
                        +"764,29,29,1,0,0,,2166,6166,ff,ILB_FCP_Response_Error,0:No response error;1:Response error\n" //
                        +"764,30,30,1,0,0,,2166,6166,ff,ILB_FP_ResetState,0:No reset;1:Exit from reset\n" //
                        +"764,31,31,1,0,0,,2166,6166,ff,ILB_FP_SwitchAPushToggle,0:(0) transition means switch pressed;1:(1) transition means switch pressed\n" //
                        +"764,32,32,1,0,0,,2166,6166,ff,ILB_FP_SwitchALongPushState,0:No long push in progress;1:Long push in progress\n" //
                        +"764,34,34,1,0,0,,2166,6166,ff,ILB_FP_SwitchBLongPushState,0:No long push in progress;1:Long push in progress\n" //
                        +"764,35,35,1,0,0,,2166,6166,ff,ILB_FP_SwitchCPushToggle,0:(0) transition means switch pressed;1:(1) transition means switch pressed\n" //
                        +"764,33,33,1,0,0,,2166,6166,ff,ILB_FP_SwitchBPushToggle,0:(0) transition means switch pressed;1:(1) transition means switch pressed\n" //
                        +"764,36,36,1,0,0,,2166,6166,ff,ILB_FP_SwitchCLongPushState,0:No long push in progress;1:Long push in progress\n" //
                        +"764,37,37,1,0,0,,2166,6166,ff,ILB_FP_SwitchDPushToggle,0:(0) transition means switch pressed;1:(1) transition means switch pressed\n" //
                        +"764,38,38,1,0,0,,2166,6166,ff,ILB_FP_SwitchDLongPushState,0:No long push in progress;1:Long push in progress\n" //
                        +"764,39,39,1,0,0,,2166,6166,ff,ILB_FP_SwitchEPushToggle,0:(0) transition means switch pressed;1:(1) transition means switch pressed\n" //
                        +"764,40,40,1,0,0,,2166,6166,ff,ILB_FP_SwitchELongPushState,0:No long push in progress;1:Long push in progress\n" //
                        +"764,41,41,1,0,0,,2166,6166,ff,ILB_FP_SwitchFPushToggle,0:(0) transition means switch pressed;1:(1) transition means switch pressed\n" //
                        +"764,42,42,1,0,0,,2166,6166,ff,ILB_FP_SwitchFLongPushState,0:No long push in progress;1:Long push in progress\n" //
                        +"764,43,43,1,0,0,,2166,6166,ff,ILB_FP_SwitchGPushToggle,0:(0) transition means switch pressed;1:(1) transition means switch pressed\n" //
                        +"764,44,44,1,0,0,,2166,6166,ff,ILB_FP_SwitchGLongPushState,0:No long push in progress;1:Long push in progress\n" //
                        +"764,45,45,1,0,0,,2166,6166,ff,ILB_FP_SwitchHPushToggle,0:(0) transition means switch pressed;1:(1) transition means switch pressed\n" //
                        +"764,46,46,1,0,0,,2166,6166,ff,ILB_FP_SwitchHLongPushState,0:No long push in progress;1:Long push in progress\n" //

                ;

        String fieldDef2 =
                ""

                        +"764,47,47,1,0,0,,2166,6166,ff,ILB_FP_SwitchJPushToggle,0:(0) transition means switch pressed;1:(1) transition means switch pressed\n" //
                        +"764,48,48,1,0,0,,2166,6166,ff,ILB_FP_SwitchJLongPushState,0:No long push in progress;1:Long push in progress\n" //
                        +"764,49,49,1,0,0,,2166,6166,ff,ILB_FP_SwitchKPushToggle,0:(0) transition means switch pressed;1:(1) transition means switch pressed\n" //
                        +"764,50,50,1,0,0,,2166,6166,ff,ILB_FP_SwitchKLongPushState,0:No long push in progress;1:Long push in progress\n" //
                        +"764,51,51,1,0,0,,2166,6166,ff,ILB_FP_SwitchLPushToggle,0:(0) transition means switch pressed;1:(1) transition means switch pressed\n" //
                        +"764,52,52,1,0,0,,2166,6166,ff,ILB_FP_SwitchLLongPushState,0:No long push in progress;1:Long push in progress\n" //
                        +"764,53,53,1,0,0,,2166,6166,ff,ILB_FP_SwitchMPushToggle,0:(0) transition means switch pressed;1:(1) transition means switch pressed\n" //
                        +"764,54,54,1,0,0,,2166,6166,ff,ILB_FP_SwitchMLongPushState,0:No long push in progress;1:Long push in progress\n" //
                        +"764,55,55,1,0,0,,2166,6166,ff,ILB_FP_SwitchNPushToggle,0:(0) transition means switch pressed;1:(1) transition means switch pressed\n" //
                        +"764,56,56,1,0,0,,2166,6166,ff,ILB_FP_SwitchNLongPushState,0:No long push in progress;1:Long push in progress\n" //
                        +"764,57,57,1,0,0,,2166,6166,ff,ILB_FP_SwitchPPushToggle,0:(0) transition means switch pressed;1:(1) transition means switch pressed\n" //
                        +"764,58,58,1,0,0,,2166,6166,ff,ILB_FP_SwitchPLongPushState,0:No long push in progress;1:Long push in progress\n" //
                        +"764,59,59,1,0,0,,2166,6166,ff,ILB_FP_SwitchRPushToggle,0:(0) transition means switch pressed;1:(1) transition means switch pressed\n" //
                        +"764,60,60,1,0,0,,2166,6166,ff,ILB_FP_SwitchRLongPushState,0:No long push in progress;1:Long push in progress\n" //
                        +"764,61,61,1,0,0,,2166,6166,ff,ILB_FP_SwitchSPushToggle,0:(0) transition means switch pressed;1:(1) transition means switch pressed\n" //
                        +"764,62,62,1,0,0,,2166,6166,ff,ILB_FP_SwitchSLongPushState,0:No long push in progress;1:Long push in progress\n" //
                        +"764,63,63,1,0,0,,2166,6166,ff,ILB_FP_SwitchTPushToggle,0:(0) transition means switch pressed;1:(1) transition means switch pressed\n" //
                        +"764,64,64,1,0,0,,2166,6166,ff,ILB_FP_SwitchTLongPushState,0:No long push in progress;1:Long push in progress\n" //
                        +"764,65,65,1,0,0,,2166,6166,ff,ILB_FP_SwitchUPushToggle,0:(0) transition means switch pressed;1:(1) transition means switch pressed\n" //
                        +"764,66,66,1,0,0,,2166,6166,ff,ILB_FP_SwitchULongPushState,0:No long push in progress;1:Long push in progress\n" //
                        +"764,67,67,1,0,0,,2166,6166,ff,ILB_FP_SwitchVPushToggle,0:(0) transition means switch pressed;1:(1) transition means switch pressed\n" //
                        +"764,68,68,1,0,0,,2166,6166,ff,ILB_FP_SwitchVLongPushState,0:No long push in progress;1:Long push in progress\n" //
                        +"764,69,69,1,0,0,,2166,6166,ff,ILB_FP_SwitchWPushToggle,0:(0) transition means switch pressed;1:(1) transition means switch pressed\n" //
                        +"764,70,70,1,0,0,,2166,6166,ff,ILB_FP_SwitchWLongPushState,0:No long push in progress;1:Long push in progress\n" //
                        +"764,71,71,1,0,0,,2166,6166,ff,ILB_FP_SwitchXPushToggle,0:(0) transition means switch pressed;1:(1) transition means switch pressed\n" //
                        +"764,72,72,1,0,0,,2166,6166,ff,ILB_FP_SwitchXLongPushState,0:No long push in progress;1:Long push in progress\n" //
                        +"764,73,73,1,0,0,,2166,6166,ff,ILB_FP_SwitchYPushToggle,0:(0) transition means switch pressed;1:(1) transition means switch pressed\n" //
                        +"764,74,74,1,0,0,,2166,6166,ff,ILB_FP_SwitchYLongPushState,0:No long push in progress;1:Long push in progress\n" //
                        +"764,75,75,1,0,0,,2166,6166,ff,ILB_FP_SwitchZPushToggle,0:(0) transition means switch pressed;1:(1) transition means switch pressed\n" //
                        +"764,76,76,1,0,0,,2166,6166,ff,ILB_FP_SwitchZLongPushState,0:No long push in progress;1:Long push in progress\n" //
                        +"764,16,20,5,0,0,%,2167,6167,ff,ILB_HPM_EngineFanSpeedRequestPWM\n" //
                        +"764,21,23,1,0,0,,2167,6167,ff,ILB_HPM_ClimLoopMode,0:Unavailable;1:AC mode;2:AC de-icing mode;3:Not used (100);4:Heat Pump mode;5:Not used (101);6:Demisting mode;7:Idle mode\n" //
                        +"764,24,39,1,0,0,Rpm,2167,6167,ff,ILB_HPM_ClimCompressorSpeedRpmRequest\n" //
                        +"764,40,41,1,0,0,,2167,6167,ff,ILB_HPM_DeiceLoopRequest,0:Unavailable;1:De-ice loop requested;2:No de-ice loop requested;3:Not used\n" //
                        +"764,42,51,.5,60,0,°C,2167,6167,ff,ILB_HPM_State_Sensor1_Status\n" //
                        +"764,52,61,.5,80,0,°C,2167,6167,ff,ILB_HPM_State_Sensor2_Status\n" //
                        +"764,16,23,1,0,0,,2168,6168,ff,ILB_HPM_Version_rel_HW\n" //
                        +"764,24,31,1,0,0,,2168,6168,ff,ILB_HPM_Version_rel_SW\n" //
                        +"764,32,35,1,0,0,,2168,6168,ff,ILB_HPM_FailureStatus_Valve1,0:No failure;1:Open circuit;2:Short circuit to battery;3:Short circuit to ground\n" //
                        +"764,36,39,1,0,0,,2168,6168,ff,ILB_HPM_FailureStatus_Valve2,0:No failure;1:Open circuit;2:Short circuit to battery;3:Short circuit to ground\n" //
                        +"764,40,43,1,0,0,,2168,6168,ff,ILB_HPM_FailureStatus_Sensor1,0:No failure;1:Short circuit to ground;2:Open circuit/Short circuit to battery;4:Over max threshold;8:Under min threshold\n" //
                        +"764,44,47,1,0,0,,2168,6168,ff,ILB_HPM_FailureStatus_Sensor2,0:No failure;1:Short circuit to ground;2:Open circuit/Short circuit to battery;4:Over max threshold;8:Under min threshold\n" //
                        +"764,48,51,1,0,0,,2168,6168,ff,ILB_HPM_State_Valve1_Status,0:Not active;1:Active;15:Unavailable\n" //
                        +"764,52,55,1,0,0,,2168,6168,ff,ILB_HPM_State_Valve2_Status,0:Not active;1:Active;15:Unavailable\n" //
                        +"764,56,63,1,0,0,,2168,6168,ff,ILB_HPM_VersionMessageSet\n" //
                        +"764,78,78,1,0,0,,2168,6168,ff,ILB_HPM_Response_Error,0:No response error;1:Response error\n" //
                        +"764,16,22,1,0,0,%,2169,6169,ff,ILB_HPM_BlowerReq\n" //
                        +"764,23,29,1,0,0,%,2169,6169,ff,ILB_HPM_RecyActrTarget\n" //
                        +"764,37,43,1,0,0,%,2169,6169,ff,OLB_HPM_RightMixActrTarget\n" //
                        +"764,30,36,1,0,0,%,2169,6169,ff,OLB_HPM_FeetVentActrTarget\n" //
                        +"764,44,50,1,0,0,%,2169,6169,ff,OLB_HPM_BatteryCoolingtActrTarget\n" //
                        +"764,16,23,1,0,0,,2115,6115,ff,CL_EngineType,0:Not Configured;1:Thermic engine;3:Electrical engine\n" //
                        +"764,16,23,1,0,0,,2174,6174,ff,CL_BatteryPTCType,0:Not Configured;1:Without PTC;16:PTC 1kW;24:PTC 1,8kW\n" //
                        +"764,16,23,1,0,0,,211F,611F,ff,KH_RemoteEnable,0:Enable (0);1:Disable (1)\n" //
                        +"764,16,23,1,0,0,,2175,6175,ff,CL_HVPTCType,0:Not Configured;1:Without HV PTC;35:HV PTC 3,5 kW;50:HV PTC 5kW\n" //
                        +"764,16,23,1,0,0,,212D,612D,ff,CL_CompValveType,0:Not configured;1:without Compressor Valve;2:with Compressor Valve type 1;3:with Compressor Valve type 2;4:with Compressor Valve type 3\n" //
                        +"764,16,23,1,0,0,,217D,617D,ff,CL_MMIArchitecture,0:Not Configured;1:Front Control Panel only;2:Multimedia Display + Front Control Panel\n" //
                        +"764,16,31,1,0,0,,2152,6152,ff,VH_TimerPCImm\n" //
                        +"764,47,47,1,0,0,,2152,6152,ff,VH_PreHeatingForRequest\n" //
                        +"764,32,39,1,0,0,,2152,6152,ff,VH_Misting\n" //
                        +"764,48,55,1,0,0,,2152,6152,ff,VH_AQSRecyclingNeed\n" //
                        +"764,56,63,1,0,0,,2152,6152,ff,VH_ComfortRecyPartialReq\n" //
                        +"764,64,71,1,0,0,,2152,6152,ff,VH_ComfortRecyPerfoReq\n" //
                        +"764,72,79,1,0,0,,2152,6152,ff,VH_DemistingRecyReq\n" //

                ;

        String dtcDef =
                ""

                        +"F003,VBatt\n" //
                        +"9101,InCarTemp\n" //
                        +"9104,EvapTemp\n" //
                        +"9105,ECU\n" //
                        +"9160,RecyActr\n" //
                        +"9171,RightMixActr\n" //
                        +"9162,FeetVentActr\n" //
                        +"910B,AQS_Reductor\n" //
                        +"911B,AQS_Oxydant\n" //
                        +"912B,AQS_Supply\n" //
                        +"913B,AQS_PreHeating\n" //
                        +"9106,LeftSolarSensor\n" //
                        +"9103,HumiditySensor\n" //
                        +"9150,LIN_Bus\n" //
                        +"9165,AQM_FragranceMotor\n" //
                        +"9166,AQM_Ionizer\n" //
                        +"9175,AQM_FragranceBlower\n" //
                        +"9151,LIN2_Bus\n" //
                        +"9176,AQM_IonizerMode\n" //
                        +"91A7,FrontControlPanel\n" //
                        +"9114,HotSourceTemp\n" //
                        +"9113,BatteryEvapTemp\n" //
                        +"9109,BlowerControl\n" //
                        +"9184,RelayOutput\n" //
                        +"9133,CompValve\n" //
                        +"9185,BatteryBlowerSystem1\n" //
                        +"9186,BatteryBlowerSystem2\n" //
                        +"9187,CabinCoolingElectroVanne\n" //
                        +"9188,BatteryCoolingElectroVanne\n" //
                        +"91D4,HeatPumpECU\n" //
                        +"91D7,HeatPumpSensor1\n" //
                        +"91D8,HeatPumpSensor2\n" //
                        +"91D5,HeatPumpValve1\n" //
                        +"91D6,HeatPumpValve2\n" //
                        +"9161,DistMildActr\n" //
                        +"9182,BatteryCoolingActr\n" //
                        +"9183,BatteryBlowerControl\n" //
                        +"91BD,SpareOutput1\n" //
                        +"91CB,SpareInput1\n" //
                        +"91CC,SpareInput2\n" //
                        +"9170,LeftMixActr\n" //

                ;

        String testDef =
                ""

                        +"00,Device and failure type ODB codding\n" //
                        +"01,General Electrical Failure\n" //
                        +"02,General signal failure\n" //
                        +"03,FM (Frequency Modulated) - PWM (Pulse Width Modulated) Failures\n" //
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

        Frames.getInstance().load("764,0,0,CLIM\n");
        Fields.getInstance().load(fieldDef1);
        Fields.getInstance().loadMore(fieldDef2);
        Dtcs.getInstance().load(dtcDef, testDef);
    }
}