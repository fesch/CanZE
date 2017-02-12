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

public class EcuDiagLBC {

    //static final public String fieldsString (int index) {

    void load () {

        String fieldDef1 =
                ""
                        +"7bb,0,7,1,0,0,,14ffff,54,ff\n" // Reset DTC
                        +"7bb,0,23,1,0,0,,19020f,59020f,ff\n" // Query DTC

                        +"7bb,32,47,1,5000,0,A,2101,6101,ff,21_01_#05_Current sensor offset value\n" //
                        +"7bb,176,191,.01,0,0,kW,2101,6101,ff,21_01_#23_Input Possible Power CAN Output (After Restriction)\n" //
                        +"7bb,192,207,.01,0,0,kW,2101,6101,ff,21_01_#25_Output Possible Power CAN Output (After Restriction)\n" //
                        +"7bb,224,239,.01,0,0,V,2101,6101,ff,21_01_#29_12V Battery Voltage (Auxiliary Battery Voltage)\n" //
                        +"7bb,288,319,.0001,0,0,%,2101,6101,ff,21_01_#37_SOC CAN Output (USOC)\n" //
                        +"7bb,336,351,.01,0,0,kW,2101,6101,ff,21_01_#43_Acceptable Max Charge Power\n" //
                        +"7bb,359,359,1,0,0,,2101,6101,ff,21_01_#45_bit0_SD Switch Interlock flag,0:opened;1:closed\n" //
                        +"7bb,358,358,1,0,0,,2101,6101,ff,21_01_#45_bit1_Charge End Flag,0:End of charge not detected;1:End of charge detected\n" //
                        +"7bb,357,357,1,0,0,,2101,6101,ff,21_01_#45_bit2_Filtered Poor Joint Detector Input Port Flug,0:Circuit no Failure;1:Circuit Failure\n" //
                        +"7bb,360,375,100,0,0,ohm,2101,6101,ff,21_01_#46_HVIsolationImpedance\n" //
                        +"7bb,144,159,1,0,0,A,2101,6101,ff,21_01_#19_Internal_AD_value_of_3V_Reference_Voltage\n" //
                        +"7bb,160,175,1,0,0,,2101,6101,ff,21_01_#21_Internal_AD_value_of_current_Sensor_power_supply_Voltage\n" //
                        +"7bb,354,355,1,0,0,,2101,6101,ff,21_01_#45_bit4_bit5_Interlock Status sent by CAN(regarding CAN data about interlock 'interlock battery'),0:Not used;1:Opened;2:closed;3:Unavailable\n" //
                        +"7bb,356,356,1,0,0,,2101,6101,ff,21_01_#45_bit3_HV Interlock Flag,0:opened;1:closed\n" //
                        +"7bb,16,31,1,5000,0,A,2101,6101,ff,21_01_#03 Electric_Current_of_Battery\n" //
                        +"7bb,16,159,1,0,0,,2161,6161,ff,LID_61_données completes\n" //
                        +"7bb,72,79,.5,0,0,%,2161,6161,ff,21_61_#10_Battery State of Health (average)\n" //
                        +"7bb,88,119,1,0,0,km,2161,6161,ff,21_61_#12_Battery Mileage\n" //
                        +"7bb,120,151,1,0,0,kWh,2161,6161,ff,21_61_#16_Sum of kWh from beginning of Battery life\n" //
                        +"7bb,56,63,.5,0,0,,2161,6161,ff,21_61_#08 SOHP_value_of_group2\n" //
                        +"7bb,80,87,.5,0,0,%,2161,6161,ff,21_61_#11_Battery State of Health (minimal)\n" //
                        +"7bb,48,55,.5,0,0,,2161,6161,ff,21_61_#07 SOHP_value_of_group1\n" //
                        +"7bb,64,71,.5,0,0,,2161,6161,ff,21_61_#09 SOHP_value_of_group3\n" //
                        +"7bb,152,159,.5,0,0,,2161,6161,ff,21_61_#20 SOHP_value_of_group4\n" //
                        +"7bb,96,111,.01,0,0,V,2103,6103,ff,21_03_#13_Maximum_Cell_voltage\n" //
                        +"7bb,112,127,.01,0,0,V,2103,6103,ff,21_03_#15_Minimum_Cell_voltage\n" //
                        +"7bb,136,151,1,0,0,,2103,6103,ff,21_03_#18_LBC F/S mode,4:HVBatLevel1Failure is set;6:HVBatLevel2Failure is set\n" //
                        +"7bb,152,167,1,254,0,Nm,2103,6103,ff,21_03_#20_MG_Torque\n" //
                        +"7bb,168,183,1,2000,0,rpm,2103,6103,ff,21_03_#22_MG_Output_revolution\n" //
                        +"7bb,192,207,.01,0,0,%,2103,6103,ff,21_03_#25_Battery SOC\n" //
                        +"7bb,190,191,1,0,0,,2103,6103,ff,21_03_#24_bit0-bit1_Relay_on_permit_flag,0:Not used;1:Allowed;2:Not Allowed;3:Unavailable\n" //
                        +"7bb,188,189,1,0,0,,2103,6103,ff,21_03_#24_bit2-bit3_High_voltage_relay_status_flag,0:Precharge;1:Closed;2:Opened;3:Transitory\n" //
                        +"7bb,224,231,1,0,0,,2103,6103,ff,21_03_#29 Vehicle Mode Request,0:NO REQUEST;1:Slow charging;2:Fast charging;3:Normal;4:Quick Drop;5:Not used (101);6:Not used (110);7:Unavailable value\n" //
                        +"7bb,16,31,.01,0,0,V,2103,6103,ff,21_03#03 OCV (Open Circuit Voltage)\n" //
                        +"7bb,32,47,.01,0,0,%,2103,6103,ff,21_03#05 Battery SOC of cell lowest voltage\n" //
                        +"7bb,48,63,.01,0,0,%,2103,6103,ff,21_03#07 Battery SOC of cell highest voltage\n" //
                        +"7bb,64,95,1,0,0,,2103,6103,ff,21_03#09 Blank(0x00)\n" //
                        +"7bb,128,135,1,0,0,,2103,6103,ff,21_03_#17_Safety Mode 1 Flags,0:NO SAFETY FLAG 1;1:SAFETY MODE FLAG 1 PRESENT\n" //
                        +"7bb,16,31,1,0,0,mV,2104,6104,ff,21_04_#03_Thermo_Sensor_1_AD_value\n" //
                        +"7bb,32,39,1,40,0,degC,2104,6104,ff,21_04_#05_Battery_Temperature_1\n" //
                        +"7bb,40,55,1,0,0,mV,2104,6104,ff,21_04_#06_Thermo_Sensor_2_AD_value\n" //
                        +"7bb,56,63,1,40,0,degC,2104,6104,ff,21_04_#08_Battery_Temperature_2\n" //
                        +"7bb,64,79,1,0,0,mV,2104,6104,ff,21_04_#09_Thermo_Sensor_3_AD_value\n" //
                        +"7bb,80,87,1,40,0,degC,2104,6104,ff,21_04_#11_Battery_Temperature_3\n" //
                        +"7bb,88,103,1,0,0,mV,2104,6104,ff,21_04_#12_Thermo_Sensor_4_AD_value\n" //
                        +"7bb,104,111,1,40,0,degC,2104,6104,ff,21_04_#14_Battery_Temperature_4\n" //
                        +"7bb,128,135,1,40,0,degC,2104,6104,ff,21_04_#17_Battery_Temperature_5\n" //
                        +"7bb,152,159,1,40,0,degC,2104,6104,ff,21_04_#20_Battery_Temperature_6\n" //
                        +"7bb,176,183,1,40,0,degC,2104,6104,ff,21_04_#23_Battery_Temperature_7\n" //
                        +"7bb,200,207,1,40,0,degC,2104,6104,ff,21_04_#26_Battery_Temperature_8\n" //
                        +"7bb,224,231,1,40,0,degC,2104,6104,ff,21_04_#29_Battery_Temperature_9\n" //
                        +"7bb,248,255,1,40,0,degC,2104,6104,ff,21_04_#32_Battery_Temperature_10\n" //
                        +"7bb,272,279,1,40,0,degC,2104,6104,ff,21_04_#35_Battery_Temperature_11\n" //
                        +"7bb,296,303,1,40,0,degC,2104,6104,ff,21_04_#38_Battery_Temperature_12\n" //
                        +"7bb,112,127,1,0,0,mV,2104,6104,ff,21_04_#15_Thermo_Sensor_5_AD_value\n" //
                        +"7bb,136,151,1,0,0,mV,2104,6104,ff,21_04_#18_Thermo_Sensor_6_AD_value\n" //
                        +"7bb,160,175,1,0,0,mV,2104,6104,ff,21_04_#21_Thermo_Sensor_7_AD_value\n" //
                        +"7bb,184,199,1,0,0,mV,2104,6104,ff,21_04_#24_Thermo_Sensor_8_AD_value\n" //
                        +"7bb,208,223,1,0,0,mV,2104,6104,ff,21_04_#27_Thermo_Sensor_9_AD_value\n" //
                        +"7bb,232,247,1,0,0,mV,2104,6104,ff,21_04_#30_Thermo_Sensor_10_AD_value\n" //
                        +"7bb,256,271,1,0,0,mV,2104,6104,ff,21_04_#33_Thermo_Sensor_11_AD_value\n" //
                        +"7bb,280,295,1,0,0,mV,2104,6104,ff,21_04_#36_Thermo_Sensor_12_AD_value\n" //
                        +"7bb,304,319,1,0,0,mV,2104,6104,ff,21_04_#39_Thermo_Sensor_13_AD_value\n" //
                        +"7bb,328,343,1,0,0,mV,2104,6104,ff,21_04_#42_Thermo_Sensor_14_AD_value\n" //
                        +"7bb,352,367,1,0,0,mV,2104,6104,ff,21_04_#45_Thermo_Sensor_15_AD_value\n" //
                        +"7bb,376,391,1,0,0,mV,2104,6104,ff,21_04_#48_Thermo_Sensor_16_AD_value\n" //
                        +"7bb,400,415,1,0,0,mV,2104,6104,ff,21_04_#51_Thermo_Sensor_17_AD_value\n" //
                        +"7bb,424,439,1,0,0,mV,2104,6104,ff,21_04_#54_Thermo_Sensor_18_AD_value\n" //
                        +"7bb,448,463,1,0,0,mV,2104,6104,ff,21_04_#57_Thermo_Sensor_19_AD_value\n" //
                        +"7bb,472,487,1,0,0,mV,2104,6104,ff,21_04_#60_Thermo_Sensor_20_AD_value\n" //
                        +"7bb,496,511,1,0,0,mV,2104,6104,ff,21_04_#63_Thermo_Sensor_21_AD_value\n" //
                        +"7bb,520,535,1,0,0,mV,2104,6104,ff,21_04_#66_Thermo_Sensor_22_AD_value\n" //
                        +"7bb,544,559,1,0,0,mV,2104,6104,ff,21_04_#69_Thermo_Sensor_23_AD_value\n" //
                        +"7bb,568,583,1,0,0,mV,2104,6104,ff,21_04_#72_Thermo_Sensor_24_AD_value\n" //
                        +"7bb,320,327,1,40,0,degC,2104,6104,ff,21_04_#41_Battery_Temperature_13\n" //
                        +"7bb,344,351,1,40,0,degC,2104,6104,ff,21_04_#44_Battery_Temperature_14\n" //
                        +"7bb,368,375,1,40,0,degC,2104,6104,ff,21_04_#47_Battery_Temperature_15\n" //
                        +"7bb,392,399,1,40,0,degC,2104,6104,ff,21_04_#50_Battery_Temperature_16\n" //
                        +"7bb,416,423,1,40,0,degC,2104,6104,ff,21_04_#53_Battery_Temperature_17\n" //
                        +"7bb,440,447,1,40,0,degC,2104,6104,ff,21_04_#56_Battery_Temperature_18\n" //
                        +"7bb,464,471,1,40,0,degC,2104,6104,ff,21_04_#59_Battery_Temperature_19\n" //
                        +"7bb,488,495,1,40,0,degC,2104,6104,ff,21_04_#62_Battery_Temperature_20\n" //
                        +"7bb,512,519,1,40,0,degC,2104,6104,ff,21_04_#65_Battery_Temperature_21\n" //
                        +"7bb,536,543,1,40,0,degC,2104,6104,ff,21_04_#68_Battery_Temperature_22\n" //
                        +"7bb,560,567,1,40,0,degC,2104,6104,ff,21_04_#71_Battery_Temperature_23\n" //
                        +"7bb,584,591,1,40,0,degC,2104,6104,ff,21_04_#74_Battery_Temperature_24\n" //
                        +"7bb,592,599,1,40,0,degC,2104,6104,ff,21_04_#75_Minimum_Battery_Temperature\n" //
                        +"7bb,600,607,1,40,0,degC,2104,6104,ff,21_04_#76_Average_Battery_Temperature\n" //
                        +"7bb,608,615,1,40,0,,2104,6104,ff,21_04_#77 Maximum Battery Temperature\n" //
                        +"7bb,16,31,1,0,0,,2166,6166,ff,21_66_#03_Quick Charge counter\n" //
                        +"7bb,32,47,1,0,0,,2166,6166,ff,21_66_#05_Normal Charge counter\n" //
                        +"7bb,48,63,1,0,0,,2166,6166,ff,21_66_#07_Full Charge counter\n" //
                        +"7bb,64,79,1,0,0,,2166,6166,ff,21_66_#09_Partial Charge counter\n" //
                        +"7bb,96,111,1,0,0,,2166,6166,ff,21_66_#13_Bolt Meter counter\n" //
                        +"7bb,80,95,1,0,0,,2166,6166,ff,21_66_#11_High voltage connecter counter\n" //
                        +"7bb,16,31,1,0,0,Mn,2167,6167,ff,21_67_#003_Driving History Data[0][0]\n" //
                        +"7bb,32,47,1,0,0,Mn,2167,6167,ff,21_67_#005_Driving History Data[0][1]\n" //
                        +"7bb,48,63,1,0,0,Mn,2167,6167,ff,21_67_#007_Driving History Data[0][2]\n" //
                        +"7bb,64,79,1,0,0,Mn,2167,6167,ff,21_67_#009_Driving History Data[0][3]\n" //
                        +"7bb,80,95,1,0,0,Mn,2167,6167,ff,21_67_#011_Driving History Data[0][4]\n" //
                        +"7bb,96,111,1,0,0,Mn,2167,6167,ff,21_67_#013_Driving History Data[1][0]\n" //
                        +"7bb,112,127,1,0,0,Mn,2167,6167,ff,21_67_#015_Driving History Data[1][1]\n" //
                        +"7bb,128,143,1,0,0,Mn,2167,6167,ff,21_67_#017_Driving History Data[1][2]\n" //
                        +"7bb,144,159,1,0,0,Mn,2167,6167,ff,21_67_#019_Driving History Data[1][3]\n" //
                        +"7bb,160,175,1,0,0,Mn,2167,6167,ff,21_67_#021_Driving History Data[1][4]\n" //
                        +"7bb,176,191,1,0,0,Mn,2167,6167,ff,21_67_#023_Driving History Data[2][0]\n" //
                        +"7bb,192,207,1,0,0,Mn,2167,6167,ff,21_67_#025_Driving History Data[2][1]\n" //
                        +"7bb,208,223,1,0,0,Mn,2167,6167,ff,21_67_#027_Driving History Data[2][2]\n" //
                        +"7bb,224,239,1,0,0,Mn,2167,6167,ff,21_67_#029_Driving History Data[2][3]\n" //
                        +"7bb,240,255,1,0,0,Mn,2167,6167,ff,21_67_#031_Driving History Data[2][4]\n" //
                        +"7bb,256,271,1,0,0,Mn,2167,6167,ff,21_67_#033_Driving History Data[3][0]\n" //
                        +"7bb,272,287,1,0,0,Mn,2167,6167,ff,21_67_#035_Driving History Data[3][1]\n" //
                        +"7bb,288,303,1,0,0,Mn,2167,6167,ff,21_67_#037_Driving History Data[3][2]\n" //
                        +"7bb,304,319,1,0,0,Mn,2167,6167,ff,21_67_#039_Driving History Data[3][3]\n" //
                        +"7bb,320,335,1,0,0,Mn,2167,6167,ff,21_67_#041_Driving History Data[3][4]\n" //
                        +"7bb,336,351,1,0,0,Mn,2167,6167,ff,21_67_#043_Driving History Data[4][0]\n" //
                        +"7bb,352,367,1,0,0,Mn,2167,6167,ff,21_67_#045_Driving History Data[4][1]\n" //
                        +"7bb,368,383,1,0,0,Mn,2167,6167,ff,21_67_#047_Driving History Data[4][2]\n" //
                        +"7bb,384,399,1,0,0,Mn,2167,6167,ff,21_67_#049_Driving History Data[4][3]\n" //
                        +"7bb,400,415,1,0,0,Mn,2167,6167,ff,21_67_#051_Driving History Data[4][4]\n" //
                        +"7bb,416,431,1,0,0,,2167,6167,ff,21_67_#053_Parking History Data[0][0]\n" //
                        +"7bb,432,447,1,0,0,,2167,6167,ff,21_67_#055_Parking History Data[0][1]\n" //
                        +"7bb,448,463,1,0,0,,2167,6167,ff,21_67_#057_Parking History Data[0][2]\n" //
                        +"7bb,464,479,1,0,0,,2167,6167,ff,21_67_#059_Parking History Data[0][3]\n" //
                        +"7bb,480,495,1,0,0,,2167,6167,ff,21_67_#061_Parking History Data[0][4]\n" //
                        +"7bb,496,511,1,0,0,,2167,6167,ff,21_67_#063_Parking History Data[1][0]\n" //
                        +"7bb,512,527,1,0,0,,2167,6167,ff,21_67_#065_Parking History Data[1][1]\n" //
                        +"7bb,528,543,1,0,0,,2167,6167,ff,21_67_#067_Parking History Data[1][2]\n" //
                        +"7bb,544,559,1,0,0,,2167,6167,ff,21_67_#069_Parking History Data[1][3]\n" //
                        +"7bb,560,575,1,0,0,,2167,6167,ff,21_67_#071_Parking History Data[1][4]\n" //
                        +"7bb,576,591,1,0,0,,2167,6167,ff,21_67_#073_Parking History Data[2][0]\n" //
                        +"7bb,592,607,1,0,0,,2167,6167,ff,21_67_#075_Parking History Data[2][1]\n" //
                        +"7bb,608,623,1,0,0,,2167,6167,ff,21_67_#077_Parking History Data[2][2]\n" //
                        +"7bb,624,639,1,0,0,,2167,6167,ff,21_67_#079_Parking History Data[2][3]\n" //
                        +"7bb,640,655,1,0,0,,2167,6167,ff,21_67_#081_Parking History Data[2][4]\n" //
                        +"7bb,656,671,1,0,0,,2167,6167,ff,21_67_#083_Parking History Data[3][0]\n" //
                        +"7bb,672,687,1,0,0,,2167,6167,ff,21_67_#085_Parking History Data[3][1]\n" //
                        +"7bb,688,703,1,0,0,,2167,6167,ff,21_67_#087_Parking History Data[3][2]\n" //
                        +"7bb,704,719,1,0,0,,2167,6167,ff,21_67_#089_Parking History Data[3][3]\n" //
                        +"7bb,720,735,1,0,0,,2167,6167,ff,21_67_#091_Parking History Data[3][4]\n" //
                        +"7bb,736,751,1,0,0,,2167,6167,ff,21_67_#093_Parking History Data[4][0]\n" //
                        +"7bb,752,767,1,0,0,,2167,6167,ff,21_67_#095_Parking History Data[4][1]\n" //
                        +"7bb,768,783,1,0,0,,2167,6167,ff,21_67_#097_Parking History Data[4][2]\n" //
                        +"7bb,784,799,1,0,0,,2167,6167,ff,21_67_#099_Parking History Data[4][3]\n" //
                        +"7bb,800,815,1,0,0,,2167,6167,ff,21_67_#101_Parking History Data[4][4]\n" //
                        +"7bb,16,23,1,0,0,,2107,6107,ff,21_07_#03_Balancing Switch 1 Status\n" //
                        +"7bb,24,31,1,0,0,,2107,6107,ff,21_07_#04_Balancing Switch 2 Status\n" //
                        +"7bb,32,39,1,0,0,,2107,6107,ff,21_07_#05_Balancing Switch 3 Status\n" //
                        +"7bb,40,47,1,0,0,,2107,6107,ff,21_07_#06_Balancing Switch 4 Status\n" //
                        +"7bb,48,55,1,0,0,,2107,6107,ff,21_07_#07_Balancing Switch 5 Status\n" //
                        +"7bb,56,63,1,0,0,,2107,6107,ff,21_07_#08_Balancing Switch 6 Status\n" //
                        +"7bb,64,71,1,0,0,,2107,6107,ff,21_07_#09_Balancing Switch 7 Status\n" //
                        +"7bb,72,79,1,0,0,,2107,6107,ff,21_07_#10_Balancing Switch 8 Status\n" //
                        +"7bb,80,87,1,0,0,,2107,6107,ff,21_07_#11_Balancing Switch 9 Status\n" //
                        +"7bb,88,95,1,0,0,,2107,6107,ff,21_07_#12_Balancing Switch 10 Status\n" //
                        +"7bb,96,103,1,0,0,,2107,6107,ff,21_07_#13_Balancing Switch 11 Status\n" //
                        +"7bb,104,111,1,0,0,,2107,6107,ff,21_07_#14_Balancing Switch 12 Status\n" //
                        +"7bb,48,79,1,0,0,,2106,6106,ff,21_06_#07_Vehicle ID 00\n" //
                        +"7bb,80,111,1,0,0,,2106,6106,ff,21_06_#11_Vehicle ID 01\n" //
                        +"7bb,112,143,1,0,0,,2106,6106,ff,21_06_#15_Vehicle ID 02\n" //
                        +"7bb,144,175,1,0,0,,2106,6106,ff,21_06_#19_Vehicle ID 03\n" //
                        +"7bb,176,207,1,0,0,,2106,6106,ff,21_06_#23_Vehicle ID 04\n" //
                        +"7bb,208,239,1,0,0,,2106,6106,ff,21_06_#27_Vehicle ID 05\n" //
                        +"7bb,240,271,1,0,0,,2106,6106,ff,21_06_#31_Vehicle ID 06\n" //
                        +"7bb,272,303,1,0,0,,2106,6106,ff,21_06_#35_Vehicle ID 07\n" //
                        +"7bb,304,335,1,0,0,,2106,6106,ff,21_06_#39_Vehicle ID 08\n" //
                        +"7bb,336,367,1,0,0,,2106,6106,ff,21_06_#43_Vehicle ID 09\n" //
                        +"7bb,368,399,1,0,0,,2106,6106,ff,21_06_#47_Vehicle ID 10\n" //
                        +"7bb,400,431,1,0,0,,2106,6106,ff,21_06_#51_Vehicle ID 11\n" //
                        +"7bb,432,463,1,0,0,,2106,6106,ff,21_06_#55_Vehicle ID 12\n" //
                        +"7bb,464,495,1,0,0,,2106,6106,ff,21_06_#59_Vehicle ID 13\n" //
                        +"7bb,496,527,1,0,0,,2106,6106,ff,21_06_#63_Vehicle ID 14\n" //
                        +"7bb,528,559,1,0,0,,2106,6106,ff,21_06_#67_Vehicle ID 15\n" //
                        +"7bb,560,567,1,0,0,,2106,6106,ff,21_06_#71_Quick_Drop_Counter 00\n" //
                        +"7bb,568,575,1,0,0,,2106,6106,ff,21_06_#72_Quick_Drop_Counter 01\n" //
                        +"7bb,576,583,1,0,0,,2106,6106,ff,21_06_#73_Quick_Drop_Counter 02\n" //
                        +"7bb,584,591,1,0,0,,2106,6106,ff,21_06_#74_Quick_Drop_Counter 03\n" //
                        +"7bb,592,599,1,0,0,,2106,6106,ff,21_06_#75_Quick_Drop_Counter 04\n" //
                        +"7bb,600,607,1,0,0,,2106,6106,ff,21_06_#76_Quick_Drop_Counter 05\n" //
                        +"7bb,608,615,1,0,0,,2106,6106,ff,21_06_#77_Quick_Drop_Counter 06\n" //
                        +"7bb,616,623,1,0,0,,2106,6106,ff,21_06_#78_Quick_Drop_Counter 07\n" //
                        +"7bb,624,631,1,0,0,,2106,6106,ff,21_06_#79_Quick_Drop_Counter 08\n" //
                        +"7bb,632,639,1,0,0,,2106,6106,ff,21_06_#80_Quick_Drop_Counter 09\n" //
                        +"7bb,640,647,1,0,0,,2106,6106,ff,21_06_#81_Quick_Drop_Counter 10\n" //
                        +"7bb,648,655,1,0,0,,2106,6106,ff,21_06_#82_Quick_Drop_Counter 11\n" //
                        +"7bb,656,663,1,0,0,,2106,6106,ff,21_06_#83_Quick_Drop_Counter 12\n" //
                        +"7bb,664,671,1,0,0,,2106,6106,ff,21_06_#84_Quick_Drop_Counter 13\n" //
                        +"7bb,672,679,1,0,0,,2106,6106,ff,21_06_#85_Quick_Drop_Counter 14\n" //
                        +"7bb,680,687,1,0,0,,2106,6106,ff,21_06_#86_Quick_Drop_Counter 15\n" //
                        +"7bb,16,47,1,0,0,,2162,6162,ff,21_62_#03_Serial_Number (FIN)\n" //
                        +"7bb,40,47,1,0,0,,2130,6130,ff,21_30 #006_b0_MSN_HIS[00]Ampere_hour_discharge\n" //
                        +"7bb,56,63,1,40,0,,2130,6130,ff,21_30 #008_b0_MSN_HIS[00]Battery_temp_end\n" //
                        +"7bb,64,71,1,40,0,,2130,6130,ff,21_30 #009_b0_MSN_HIS[00]Battery_temp_start\n" //
                        +"7bb,16,18,1,0,0,,2130,6130,ff,21_30 #003_b0_MSN_HIS[00]Operation_type\n" //
                        +"7bb,22,30,.2,0,0,%,2130,6130,ff,21_30 #003_b6_MSN_HIS[00]User_SOC_end\n" //
                        +"7bb,31,39,.2,0,0,%,2130,6130,ff,21_30 #004_b7_MSN_HIS[00]User_SOC_start\n" //
                        +"7bb,48,55,1,0,0,,2130,6130,ff,21_30 #007_b0_MSN_HIS[00]Ampere_hour_charge\n" //
                        +"7bb,72,77,1,0,0,,2130,6130,ff,defauts mission 0\n" //
                        +"7bb,73,73,1,0,0,,2130,6130,ff,21_30 #010_b1_MSN_HIS[00]Diag_NG_over_discharge_batt\n" //
                        +"7bb,74,74,1,0,0,,2130,6130,ff,21_30 #010_b2_MSN_HIS[00]Diag_NG_over_charge_cell\n" //
                        +"7bb,75,75,1,0,0,,2130,6130,ff,21_30 #010_b3_MSN_HIS[00]Diag_NG_over_discharge_cell\n" //
                        +"7bb,76,76,1,0,0,,2130,6130,ff,21_30 #010_b4_MSN_HIS[00]Diag_NG_over_current\n" //
                        +"7bb,77,77,1,0,0,,2130,6130,ff,21_30 #010_b5_MSN_HIS[00]Diag_NG_over_temp\n" //
                        +"7bb,80,82,1,0,0,,2130,6130,ff,21_30 #011_b0_MSN_HIS[01]Operation_type\n" //
                        +"7bb,86,94,.2,0,0,%,2130,6130,ff,21_30 #011_b6_MSN_HIS[01]User_SOC_end\n" //
                        +"7bb,95,103,.2,0,0,%,2130,6130,ff,21_30 #012_b7_MSN_HIS[01]User_SOC_start\n" //
                        +"7bb,104,111,1,0,0,,2130,6130,ff,21_30 #014_b0_MSN_HIS[01]Ampere_hour_discharge\n" //
                        +"7bb,112,119,1,0,0,,2130,6130,ff,21_30 #015_b0_MSN_HIS[01]Ampere_hour_charge\n" //
                        +"7bb,120,127,1,40,0,,2130,6130,ff,21_30 #016_b0_MSN_HIS[01]Battery_temp_end\n" //
                        +"7bb,128,135,1,40,0,,2130,6130,ff,21_30 #017_b0_MSN_HIS[01]Battery_temp_start\n" //
                        +"7bb,136,141,1,0,0,,2130,6130,ff,defauts mission 1\n" //
                        +"7bb,137,137,1,0,0,,2130,6130,ff,21_30 #018_b1_MSN_HIS[01]Diag_NG_over_discharge_batt\n" //
                        +"7bb,138,138,1,0,0,,2130,6130,ff,21_30 #018_b2_MSN_HIS[01]Diag_NG_over_charge_cell\n" //
                        +"7bb,139,139,1,0,0,,2130,6130,ff,21_30 #018_b3_MSN_HIS[01]Diag_NG_over_discharge_cell\n" //
                        +"7bb,140,140,1,0,0,,2130,6130,ff,21_30 #018_b4_MSN_HIS[01]Diag_NG_over_current\n" //
                        +"7bb,141,141,1,0,0,,2130,6130,ff,21_30 #018_b5_MSN_HIS[01]Diag_NG_over_temp\n" //
                        +"7bb,144,146,1,0,0,,2130,6130,ff,21_30 #019_b0_MSN_HIS[02]Operation_type\n" //
                        +"7bb,150,158,.2,0,0,%,2130,6130,ff,21_30 #019_b6_MSN_HIS[02]User_SOC_end\n" //
                        +"7bb,159,167,.2,0,0,%,2130,6130,ff,21_30 #020_b7_MSN_HIS[02]User_SOC_start\n" //
                        +"7bb,168,175,1,0,0,,2130,6130,ff,21_30 #022_b0_MSN_HIS[02]Ampere_hour_discharge\n" //
                        +"7bb,176,183,1,0,0,,2130,6130,ff,21_30 #023_b0_MSN_HIS[02]Ampere_hour_charge\n" //
                        +"7bb,184,191,1,40,0,,2130,6130,ff,21_30 #024_b0_MSN_HIS[02]Battery_temp_end\n" //
                        +"7bb,192,199,1,40,0,,2130,6130,ff,21_30 #025_b0_MSN_HIS[02]Battery_temp_start\n" //
                        +"7bb,200,205,1,0,0,,2130,6130,ff,defauts mission 2\n" //
                        +"7bb,201,201,1,0,0,,2130,6130,ff,21_30 #026_b1_MSN_HIS[02]Diag_NG_over_discharge_batt\n" //
                        +"7bb,202,202,1,0,0,,2130,6130,ff,21_30 #026_b2_MSN_HIS[02]Diag_NG_over_charge_cell\n" //
                        +"7bb,203,203,1,0,0,,2130,6130,ff,21_30 #026_b3_MSN_HIS[02]Diag_NG_over_discharge_cell\n" //
                        +"7bb,204,204,1,0,0,,2130,6130,ff,21_30 #026_b4_MSN_HIS[02]Diag_NG_over_current\n" //
                        +"7bb,205,205,1,0,0,,2130,6130,ff,21_30 #026_b5_MSN_HIS[02]Diag_NG_over_temp\n" //
                        +"7bb,208,210,1,0,0,,2130,6130,ff,21_30 #027_b0_MSN_HIS[03]Operation_type\n" //
                        +"7bb,214,222,.2,0,0,%,2130,6130,ff,21_30 #027_b6_MSN_HIS[03]User_SOC_end\n" //
                        +"7bb,223,231,.2,0,0,%,2130,6130,ff,21_30 #028_b7_MSN_HIS[03]User_SOC_start\n" //
                        +"7bb,232,239,1,0,0,,2130,6130,ff,21_30 #030_b0_MSN_HIS[03]Ampere_hour_discharge\n" //
                        +"7bb,240,247,1,0,0,,2130,6130,ff,21_30 #031_b0_MSN_HIS[03]Ampere_hour_charge\n" //
                        +"7bb,248,255,1,40,0,,2130,6130,ff,21_30 #032_b0_MSN_HIS[03]Battery_temp_end\n" //
                        +"7bb,256,263,1,40,0,,2130,6130,ff,21_30 #033_b0_MSN_HIS[03]Battery_temp_start\n" //
                        +"7bb,264,269,1,0,0,,2130,6130,ff,defauts mission 3\n" //
                        +"7bb,265,265,1,0,0,,2130,6130,ff,21_30 #034_b1_MSN_HIS[03]Diag_NG_over_discharge_batt\n" //
                        +"7bb,266,266,1,0,0,,2130,6130,ff,21_30 #034_b2_MSN_HIS[03]Diag_NG_over_charge_cell\n" //
                        +"7bb,267,267,1,0,0,,2130,6130,ff,21_30 #034_b3_MSN_HIS[03]Diag_NG_over_discharge_cell\n" //
                        +"7bb,268,268,1,0,0,,2130,6130,ff,21_30 #034_b4_MSN_HIS[03]Diag_NG_over_current\n" //
                        +"7bb,269,269,1,0,0,,2130,6130,ff,21_30 #034_b5_MSN_HIS[03]Diag_NG_over_temp\n" //
                        +"7bb,272,274,1,0,0,,2130,6130,ff,21_30 #035_b0_MSN_HIS[04]Operation_type\n" //
                        +"7bb,278,286,.2,0,0,%,2130,6130,ff,21_30 #035_b6_MSN_HIS[04]User_SOC_end\n" //
                        +"7bb,287,295,.2,0,0,%,2130,6130,ff,21_30 #036_b7_MSN_HIS[04]User_SOC_start\n" //
                        +"7bb,296,303,1,0,0,,2130,6130,ff,21_30 #038_b0_MSN_HIS[04]Ampere_hour_discharge\n" //
                        +"7bb,304,311,1,0,0,,2130,6130,ff,21_30 #039_b0_MSN_HIS[04]Ampere_hour_charge\n" //
                        +"7bb,312,319,1,40,0,,2130,6130,ff,21_30 #040_b0_MSN_HIS[04]Battery_temp_end\n" //
                        +"7bb,320,327,1,40,0,,2130,6130,ff,21_30 #041_b0_MSN_HIS[04]Battery_temp_start\n" //
                        +"7bb,328,333,1,0,0,,2130,6130,ff,defauts mission 4\n" //
                        +"7bb,329,329,1,0,0,,2130,6130,ff,21_30 #042_b1_MSN_HIS[04]Diag_NG_over_discharge_batt\n" //
                        +"7bb,330,330,1,0,0,,2130,6130,ff,21_30 #042_b2_MSN_HIS[04]Diag_NG_over_charge_cell\n" //
                        +"7bb,331,331,1,0,0,,2130,6130,ff,21_30 #042_b3_MSN_HIS[04]Diag_NG_over_discharge_cell\n" //
                        +"7bb,332,332,1,0,0,,2130,6130,ff,21_30 #042_b4_MSN_HIS[04]Diag_NG_over_current\n" //
                        +"7bb,333,333,1,0,0,,2130,6130,ff,21_30 #042_b5_MSN_HIS[04]Diag_NG_over_temp\n" //
                        +"7bb,336,338,1,0,0,,2130,6130,ff,21_30 #043_b0_MSN_HIS[05]Operation_type\n" //
                        +"7bb,342,350,.2,0,0,%,2130,6130,ff,21_30 #043_b6_MSN_HIS[05]User_SOC_end\n" //
                        +"7bb,351,359,.2,0,0,%,2130,6130,ff,21_30 #044_b7_MSN_HIS[05]User_SOC_start\n" //
                        +"7bb,360,367,1,0,0,,2130,6130,ff,21_30 #046_b0_MSN_HIS[05]Ampere_hour_discharge\n" //
                        +"7bb,368,375,1,0,0,,2130,6130,ff,21_30 #047_b0_MSN_HIS[05]Ampere_hour_charge\n" //
                        +"7bb,376,383,1,40,0,,2130,6130,ff,21_30 #048_b0_MSN_HIS[05]Battery_temp_end\n" //
                        +"7bb,384,391,1,40,0,,2130,6130,ff,21_30 #049_b0_MSN_HIS[05]Battery_temp_start\n" //
                        +"7bb,392,397,1,0,0,,2130,6130,ff,defauts mission 5\n" //
                        +"7bb,393,393,1,0,0,,2130,6130,ff,21_30 #050_b1_MSN_HIS[05]Diag_NG_over_discharge_batt\n" //
                        +"7bb,394,394,1,0,0,,2130,6130,ff,21_30 #050_b2_MSN_HIS[05]Diag_NG_over_charge_cell\n" //
                        +"7bb,395,395,1,0,0,,2130,6130,ff,21_30 #050_b3_MSN_HIS[05]Diag_NG_over_discharge_cell\n" //
                        +"7bb,396,396,1,0,0,,2130,6130,ff,21_30 #050_b4_MSN_HIS[05]Diag_NG_over_current\n" //
                        +"7bb,397,397,1,0,0,,2130,6130,ff,21_30 #050_b5_MSN_HIS[05]Diag_NG_over_temp\n" //
                        +"7bb,400,402,1,0,0,,2130,6130,ff,21_30 #051_b0_MSN_HIS[06]Operation_type\n" //
                        +"7bb,406,414,.2,0,0,%,2130,6130,ff,21_30 #051_b6_MSN_HIS[06]User_SOC_end\n" //
                        +"7bb,415,423,.2,0,0,%,2130,6130,ff,21_30 #052_b7_MSN_HIS[06]User_SOC_start\n" //
                        +"7bb,424,431,1,0,0,,2130,6130,ff,21_30 #054_b0_MSN_HIS[06]Ampere_hour_discharge\n" //
                        +"7bb,432,439,1,0,0,,2130,6130,ff,21_30 #055_b0_MSN_HIS[06]Ampere_hour_charge\n" //
                        +"7bb,440,447,1,40,0,,2130,6130,ff,21_30 #056_b0_MSN_HIS[06]Battery_temp_end\n" //
                        +"7bb,448,455,1,40,0,,2130,6130,ff,21_30 #057_b0_MSN_HIS[06]Battery_temp_start\n" //
                        +"7bb,456,461,1,0,0,,2130,6130,ff,defauts mission 6\n" //
                        +"7bb,457,457,1,0,0,,2130,6130,ff,21_30 #058_b1_MSN_HIS[06]Diag_NG_over_discharge_batt\n" //
                        +"7bb,458,458,1,0,0,,2130,6130,ff,21_30 #058_b2_MSN_HIS[06]Diag_NG_over_charge_cell\n" //
                        +"7bb,459,459,1,0,0,,2130,6130,ff,21_30 #058_b3_MSN_HIS[06]Diag_NG_over_discharge_cell\n" //
                        +"7bb,460,460,1,0,0,,2130,6130,ff,21_30 #058_b4_MSN_HIS[06]Diag_NG_over_current\n" //
                        +"7bb,461,461,1,0,0,,2130,6130,ff,21_30 #058_b5_MSN_HIS[06]Diag_NG_over_temp\n" //
                        +"7bb,464,466,1,0,0,,2130,6130,ff,21_30 #059_b0_MSN_HIS[07]Operation_type\n" //
                        +"7bb,470,478,.2,0,0,%,2130,6130,ff,21_30 #059_b6_MSN_HIS[07]User_SOC_end\n" //
                        +"7bb,479,487,.2,0,0,%,2130,6130,ff,21_30 #060_b7_MSN_HIS[07]User_SOC_start\n" //
                        +"7bb,488,495,1,0,0,,2130,6130,ff,21_30 #062_b0_MSN_HIS[07]Ampere_hour_discharge\n" //
                        +"7bb,496,503,1,0,0,,2130,6130,ff,21_30 #063_b0_MSN_HIS[07]Ampere_hour_charge\n" //
                        +"7bb,504,511,1,40,0,,2130,6130,ff,21_30 #064_b0_MSN_HIS[07]Battery_temp_end\n" //
                        +"7bb,512,519,1,40,0,,2130,6130,ff,21_30 #065_b0_MSN_HIS[07]Battery_temp_start\n" //
                        +"7bb,520,525,1,0,0,,2130,6130,ff,defauts mission 7\n" //
                        +"7bb,521,521,1,0,0,,2130,6130,ff,21_30 #066_b1_MSN_HIS[07]Diag_NG_over_discharge_batt\n" //
                        +"7bb,522,522,1,0,0,,2130,6130,ff,21_30 #066_b2_MSN_HIS[07]Diag_NG_over_charge_cell\n" //
                        +"7bb,523,523,1,0,0,,2130,6130,ff,21_30 #066_b3_MSN_HIS[07]Diag_NG_over_discharge_cell\n" //
                        +"7bb,524,524,1,0,0,,2130,6130,ff,21_30 #066_b4_MSN_HIS[07]Diag_NG_over_current\n" //
                        +"7bb,525,525,1,0,0,,2130,6130,ff,21_30 #066_b5_MSN_HIS[07]Diag_NG_over_temp\n" //
                        +"7bb,528,530,1,0,0,,2130,6130,ff,21_30 #067_b0_MSN_HIS[08]Operation_type\n" //
                        +"7bb,534,542,.2,0,0,%,2130,6130,ff,21_30 #067_b6_MSN_HIS[08]User_SOC_end\n" //
                        +"7bb,543,551,.2,0,0,%,2130,6130,ff,21_30 #068_b7_MSN_HIS[08]User_SOC_start\n" //
                        +"7bb,552,559,1,0,0,,2130,6130,ff,21_30 #070_b0_MSN_HIS[08]Ampere_hour_discharge\n" //
                        +"7bb,560,567,1,0,0,,2130,6130,ff,21_30 #071_b0_MSN_HIS[08]Ampere_hour_charge\n" //
                        +"7bb,568,575,1,40,0,,2130,6130,ff,21_30 #072_b0_MSN_HIS[08]Battery_temp_end\n" //
                        +"7bb,576,583,1,40,0,,2130,6130,ff,21_30 #073_b0_MSN_HIS[08]Battery_temp_start\n" //
                        +"7bb,584,589,1,0,0,,2130,6130,ff,defauts mission 8\n" //
                        +"7bb,585,585,1,0,0,,2130,6130,ff,21_30 #074_b1_MSN_HIS[08]Diag_NG_over_discharge_batt\n" //
                        +"7bb,586,586,1,0,0,,2130,6130,ff,21_30 #074_b2_MSN_HIS[08]Diag_NG_over_charge_cell\n" //
                        +"7bb,587,587,1,0,0,,2130,6130,ff,21_30 #074_b3_MSN_HIS[08]Diag_NG_over_discharge_cell\n" //
                        +"7bb,588,588,1,0,0,,2130,6130,ff,21_30 #074_b4_MSN_HIS[08]Diag_NG_over_current\n" //
                        +"7bb,589,589,1,0,0,,2130,6130,ff,21_30 #074_b5_MSN_HIS[08]Diag_NG_over_temp\n" //
                        +"7bb,592,594,1,0,0,,2130,6130,ff,21_30 #075_b0_MSN_HIS[09]Operation_type\n" //
                        +"7bb,598,606,.2,0,0,%,2130,6130,ff,21_30 #075_b6_MSN_HIS[09]User_SOC_end\n" //
                        +"7bb,607,615,.2,0,0,%,2130,6130,ff,21_30 #076_b7_MSN_HIS[09]User_SOC_start\n" //
                        +"7bb,616,623,1,0,0,,2130,6130,ff,21_30 #078_b0_MSN_HIS[09]Ampere_hour_discharge\n" //
                        +"7bb,624,631,1,0,0,,2130,6130,ff,21_30 #079_b0_MSN_HIS[09]Ampere_hour_charge\n" //
                        +"7bb,632,639,1,40,0,,2130,6130,ff,21_30 #080_b0_MSN_HIS[09]Battery_temp_end\n" //
                        +"7bb,640,647,1,40,0,,2130,6130,ff,21_30 #081_b0_MSN_HIS[09]Battery_temp_start\n" //
                        +"7bb,648,653,1,0,0,,2130,6130,ff,defauts mission 9\n" //
                        +"7bb,649,649,1,0,0,,2130,6130,ff,21_30 #082_b1_MSN_HIS[09]Diag_NG_over_discharge_batt\n" //
                        +"7bb,650,650,1,0,0,,2130,6130,ff,21_30 #082_b2_MSN_HIS[09]Diag_NG_over_charge_cell\n" //
                        +"7bb,651,651,1,0,0,,2130,6130,ff,21_30 #082_b3_MSN_HIS[09]Diag_NG_over_discharge_cell\n" //
                        +"7bb,652,652,1,0,0,,2130,6130,ff,21_30 #082_b4_MSN_HIS[09]Diag_NG_over_current\n" //
                        +"7bb,653,653,1,0,0,,2130,6130,ff,21_30 #082_b5_MSN_HIS[09]Diag_NG_over_temp\n" //
                        +"7bb,656,658,1,0,0,,2130,6130,ff,21_30 #083_b0_MSN_HIS[10]Operation_type\n" //
                        +"7bb,662,670,.2,0,0,%,2130,6130,ff,21_30 #083_b6_MSN_HIS[10]User_SOC_end\n" //
                        +"7bb,671,679,.2,0,0,%,2130,6130,ff,21_30 #084_b7_MSN_HIS[10]User_SOC_start\n" //
                        +"7bb,680,687,1,0,0,,2130,6130,ff,21_30 #086_b0_MSN_HIS[10]Ampere_hour_discharge\n" //
                        +"7bb,688,695,1,0,0,,2130,6130,ff,21_30 #087_b0_MSN_HIS[10]Ampere_hour_charge\n" //
                        +"7bb,696,703,1,40,0,,2130,6130,ff,21_30 #088_b0_MSN_HIS[10]Battery_temp_end\n" //
                        +"7bb,704,711,1,40,0,,2130,6130,ff,21_30 #089_b0_MSN_HIS[10]Battery_temp_start\n" //
                        +"7bb,712,717,1,0,0,,2130,6130,ff,defauts mission 10\n" //
                        +"7bb,713,713,1,0,0,,2130,6130,ff,21_30 #090_b1_MSN_HIS[10]Diag_NG_over_discharge_batt\n" //
                        +"7bb,714,714,1,0,0,,2130,6130,ff,21_30 #090_b2_MSN_HIS[10]Diag_NG_over_charge_cell\n" //
                        +"7bb,715,715,1,0,0,,2130,6130,ff,21_30 #090_b3_MSN_HIS[10]Diag_NG_over_discharge_cell\n" //
                        +"7bb,716,716,1,0,0,,2130,6130,ff,21_30 #090_b4_MSN_HIS[10]Diag_NG_over_current\n" //
                        +"7bb,717,717,1,0,0,,2130,6130,ff,21_30 #090_b5_MSN_HIS[10]Diag_NG_over_temp\n" //
                        +"7bb,720,722,1,0,0,,2130,6130,ff,21_30 #091_b0_MSN_HIS[11]Operation_type\n" //
                        +"7bb,726,734,.2,0,0,%,2130,6130,ff,21_30 #091_b6_MSN_HIS[11]User_SOC_end\n" //
                        +"7bb,735,743,.2,0,0,%,2130,6130,ff,21_30 #092_b7_MSN_HIS[11]User_SOC_start\n" //
                        +"7bb,744,751,1,0,0,,2130,6130,ff,21_30 #094_b0_MSN_HIS[11]Ampere_hour_discharge\n" //
                        +"7bb,752,759,1,0,0,,2130,6130,ff,21_30 #095_b0_MSN_HIS[11]Ampere_hour_charge\n" //
                        +"7bb,760,767,1,40,0,,2130,6130,ff,21_30 #096_b0_MSN_HIS[11]Battery_temp_end\n" //
                        +"7bb,768,775,1,40,0,,2130,6130,ff,21_30 #097_b0_MSN_HIS[11]Battery_temp_start\n" //
                        +"7bb,776,781,1,0,0,,2130,6130,ff,defauts mission 11\n" //
                        +"7bb,777,777,1,0,0,,2130,6130,ff,21_30 #098_b1_MSN_HIS[11]Diag_NG_over_discharge_batt\n" //
                        +"7bb,778,778,1,0,0,,2130,6130,ff,21_30 #098_b2_MSN_HIS[11]Diag_NG_over_charge_cell\n" //
                        +"7bb,779,779,1,0,0,,2130,6130,ff,21_30 #098_b3_MSN_HIS[11]Diag_NG_over_discharge_cell\n" //
                        +"7bb,780,780,1,0,0,,2130,6130,ff,21_30 #098_b4_MSN_HIS[11]Diag_NG_over_current\n" //
                        +"7bb,781,781,1,0,0,,2130,6130,ff,21_30 #098_b5_MSN_HIS[11]Diag_NG_over_temp\n" //
                        +"7bb,784,786,1,0,0,,2130,6130,ff,21_30 #099_b0_MSN_HIS[12]Operation_type\n" //
                        +"7bb,790,798,.2,0,0,%,2130,6130,ff,21_30 #099_b6_MSN_HIS[12]User_SOC_end\n" //
                        +"7bb,799,807,.2,0,0,%,2130,6130,ff,21_30 #100_b7_MSN_HIS[12]User_SOC_start\n" //
                        +"7bb,808,815,1,0,0,,2130,6130,ff,21_30 #102_b0_MSN_HIS[12]Ampere_hour_discharge\n" //
                        +"7bb,816,823,1,0,0,,2130,6130,ff,21_30 #103_b0_MSN_HIS[12]Ampere_hour_charge\n" //
                        +"7bb,824,831,1,40,0,,2130,6130,ff,21_30 #104_b0_MSN_HIS[12]Battery_temp_end\n" //
                        +"7bb,832,839,1,40,0,,2130,6130,ff,21_30 #105_b0_MSN_HIS[12]Battery_temp_start\n" //
                        +"7bb,840,845,1,0,0,,2130,6130,ff,defauts mission 12\n" //
                        +"7bb,841,841,1,0,0,,2130,6130,ff,21_30 #106_b1_MSN_HIS[12]Diag_NG_over_discharge_batt\n" //
                        +"7bb,842,842,1,0,0,,2130,6130,ff,21_30 #106_b2_MSN_HIS[12]Diag_NG_over_charge_cell\n" //
                        +"7bb,843,843,1,0,0,,2130,6130,ff,21_30 #106_b3_MSN_HIS[12]Diag_NG_over_discharge_cell\n" //
                        +"7bb,844,844,1,0,0,,2130,6130,ff,21_30 #106_b4_MSN_HIS[12]Diag_NG_over_current\n" //
                        +"7bb,845,845,1,0,0,,2130,6130,ff,21_30 #106_b5_MSN_HIS[12]Diag_NG_over_temp\n" //
                        +"7bb,848,850,1,0,0,,2130,6130,ff,21_30 #107_b0_MSN_HIS[13]Operation_type\n" //
                        +"7bb,854,862,.2,0,0,%,2130,6130,ff,21_30 #107_b6_MSN_HIS[13]User_SOC_end\n" //
                        +"7bb,863,871,.2,0,0,%,2130,6130,ff,21_30 #108_b7_MSN_HIS[13]User_SOC_start\n" //
                        +"7bb,872,879,1,0,0,,2130,6130,ff,21_30 #110_b0_MSN_HIS[13]Ampere_hour_discharge\n" //
                        +"7bb,880,887,1,0,0,,2130,6130,ff,21_30 #111_b0_MSN_HIS[13]Ampere_hour_charge\n" //
                        +"7bb,888,895,1,40,0,,2130,6130,ff,21_30 #112_b0_MSN_HIS[13]Battery_temp_end\n" //
                        +"7bb,896,903,1,40,0,,2130,6130,ff,21_30 #113_b0_MSN_HIS[13]Battery_temp_start\n" //
                        +"7bb,904,909,1,0,0,,2130,6130,ff,defauts mission 13\n" //
                        +"7bb,905,905,1,0,0,,2130,6130,ff,21_30 #114_b1_MSN_HIS[13]Diag_NG_over_discharge_batt\n" //
                        +"7bb,906,906,1,0,0,,2130,6130,ff,21_30 #114_b2_MSN_HIS[13]Diag_NG_over_charge_cell\n" //
                        +"7bb,907,907,1,0,0,,2130,6130,ff,21_30 #114_b3_MSN_HIS[13]Diag_NG_over_discharge_cell\n" //
                        +"7bb,908,908,1,0,0,,2130,6130,ff,21_30 #114_b4_MSN_HIS[13]Diag_NG_over_current\n" //
                        +"7bb,909,909,1,0,0,,2130,6130,ff,21_30 #114_b5_MSN_HIS[13]Diag_NG_over_temp\n" //
                        +"7bb,912,914,1,0,0,,2130,6130,ff,21_30 #115_b0_MSN_HIS[14]Operation_type\n" //
                        +"7bb,918,926,.2,0,0,%,2130,6130,ff,21_30 #115_b6_MSN_HIS[14]User_SOC_end\n" //
                        +"7bb,927,935,32,0,0,%,2130,6130,ff,21_30 #116_b7_MSN_HIS[14]User_SOC_start\n" //
                        +"7bb,936,943,1,0,0,,2130,6130,ff,21_30 #118_b0_MSN_HIS[14]Ampere_hour_discharge\n" //
                        +"7bb,944,951,1,0,0,,2130,6130,ff,21_30 #119_b0_MSN_HIS[14]Ampere_hour_charge\n" //
                        +"7bb,952,959,1,40,0,,2130,6130,ff,21_30 #120_b0_MSN_HIS[14]Battery_temp_end\n" //
                        +"7bb,960,967,1,40,0,,2130,6130,ff,21_30 #121_b0_MSN_HIS[14]Battery_temp_start\n" //
                        +"7bb,968,973,1,0,0,,2130,6130,ff,defauts mission 14\n" //
                        +"7bb,969,969,1,0,0,,2130,6130,ff,21_30 #122_b1_MSN_HIS[14]Diag_NG_over_discharge_batt\n" //
                        +"7bb,970,970,1,0,0,,2130,6130,ff,21_30 #122_b2_MSN_HIS[14]Diag_NG_over_charge_cell\n" //
                        +"7bb,971,971,1,0,0,,2130,6130,ff,21_30 #122_b3_MSN_HIS[14]Diag_NG_over_discharge_cell\n" //
                        +"7bb,972,972,1,0,0,,2130,6130,ff,21_30 #122_b4_MSN_HIS[14]Diag_NG_over_current\n" //
                        +"7bb,973,973,1,0,0,,2130,6130,ff,21_30 #122_b5_MSN_HIS[14]Diag_NG_over_temp\n" //
                        +"7bb,16,18,1,0,0,,2131,6131,ff,21_31 #003_b0_MSN_HIS[15]Operation_type\n" //
                        +"7bb,22,30,.2,0,0,%,2131,6131,ff,21_31 #003_b6_MSN_HIS[15]User_SOC_end\n" //
                        +"7bb,31,39,.2,0,0,%,2131,6131,ff,21_31 #004_b7_MSN_HIS[15]User_SOC_start\n" //
                        +"7bb,40,47,1,0,0,,2131,6131,ff,21_31 #006_b0_MSN_HIS[15]Ampere_hour_discharge\n" //
                        +"7bb,48,55,1,0,0,,2131,6131,ff,21_31 #007_b0_MSN_HIS[15]Ampere_hour_charge\n" //
                        +"7bb,56,63,1,40,0,,2131,6131,ff,21_31 #008_b0_MSN_HIS[15]Battery_temp_end\n" //
                        +"7bb,64,71,1,40,0,,2131,6131,ff,21_31 #009_b0_MSN_HIS[15]Battery_temp_start\n" //
                        +"7bb,72,77,1,0,0,,2131,6131,ff,defauts mission 15\n" //
                        +"7bb,73,73,1,0,0,,2131,6131,ff,21_31 #010_b1_MSN_HIS[15]Diag_NG_over_discharge_batt\n" //
                        +"7bb,74,74,1,0,0,,2131,6131,ff,21_31 #010_b2_MSN_HIS[15]Diag_NG_over_charge_cell\n" //
                        +"7bb,75,75,1,0,0,,2131,6131,ff,21_31 #010_b3_MSN_HIS[15]Diag_NG_over_discharge_cell\n" //
                        +"7bb,76,76,1,0,0,,2131,6131,ff,21_31 #010_b4_MSN_HIS[15]Diag_NG_over_current\n" //
                        +"7bb,77,77,1,0,0,,2131,6131,ff,21_31 #010_b5_MSN_HIS[15]Diag_NG_over_temp\n" //
                        +"7bb,80,82,1,0,0,,2131,6131,ff,21_31 #011_b0_MSN_HIS[16]Operation_type\n" //
                        +"7bb,86,94,.2,0,0,%,2131,6131,ff,21_31 #011_b6_MSN_HIS[16]User_SOC_end\n" //
                        +"7bb,95,103,.2,0,0,%,2131,6131,ff,21_31 #012_b7_MSN_HIS[16]User_SOC_start\n" //
                        +"7bb,104,111,1,0,0,,2131,6131,ff,21_31 #014_b0_MSN_HIS[16]Ampere_hour_discharge\n" //
                        +"7bb,112,119,1,0,0,,2131,6131,ff,21_31 #015_b0_MSN_HIS[16]Ampere_hour_charge\n" //
                        +"7bb,120,127,1,40,0,,2131,6131,ff,21_31 #016_b0_MSN_HIS[16]Battery_temp_end\n" //
                        +"7bb,128,135,1,40,0,,2131,6131,ff,21_31 #017_b0_MSN_HIS[16]Battery_temp_start\n" //
                        +"7bb,136,141,1,0,0,,2131,6131,ff,defauts mission 16\n" //
                        +"7bb,137,137,1,0,0,,2131,6131,ff,21_31 #018_b1_MSN_HIS[16]Diag_NG_over_discharge_batt\n" //
                        +"7bb,138,138,1,0,0,,2131,6131,ff,21_31 #018_b2_MSN_HIS[16]Diag_NG_over_charge_cell\n" //
                        +"7bb,139,139,1,0,0,,2131,6131,ff,21_31 #018_b3_MSN_HIS[16]Diag_NG_over_discharge_cell\n" //
                        +"7bb,140,140,1,0,0,,2131,6131,ff,21_31 #018_b4_MSN_HIS[16]Diag_NG_over_current\n" //
                        +"7bb,141,141,1,0,0,,2131,6131,ff,21_31 #018_b5_MSN_HIS[16]Diag_NG_over_temp\n" //
                        +"7bb,144,146,1,0,0,,2131,6131,ff,21_31 #019_b0_MSN_HIS[17]Operation_type\n" //
                        +"7bb,150,158,.2,0,0,%,2131,6131,ff,21_31 #019_b6_MSN_HIS[17]User_SOC_end\n" //
                        +"7bb,159,167,.2,0,0,%,2131,6131,ff,21_31 #020_b7_MSN_HIS[17]User_SOC_start\n" //
                        +"7bb,168,175,1,0,0,,2131,6131,ff,21_31 #022_b0_MSN_HIS[17]Ampere_hour_discharge\n" //
                        +"7bb,176,183,1,0,0,,2131,6131,ff,21_31 #023_b0_MSN_HIS[17]Ampere_hour_charge\n" //
                        +"7bb,184,191,1,40,0,,2131,6131,ff,21_31 #024_b0_MSN_HIS[17]Battery_temp_end\n" //
                        +"7bb,192,199,1,40,0,,2131,6131,ff,21_31 #025_b0_MSN_HIS[17]Battery_temp_start\n" //
                        +"7bb,200,205,1,0,0,,2131,6131,ff,defauts mission 17\n" //
                        +"7bb,201,201,1,0,0,,2131,6131,ff,21_31 #026_b1_MSN_HIS[17]Diag_NG_over_discharge_batt\n" //
                        +"7bb,202,202,1,0,0,,2131,6131,ff,21_31 #026_b2_MSN_HIS[17]Diag_NG_over_charge_cell\n" //
                        +"7bb,203,203,1,0,0,,2131,6131,ff,21_31 #026_b3_MSN_HIS[17]Diag_NG_over_discharge_cell\n" //
                        +"7bb,204,204,1,0,0,,2131,6131,ff,21_31 #026_b4_MSN_HIS[17]Diag_NG_over_current\n" //
                        +"7bb,205,205,1,0,0,,2131,6131,ff,21_31 #026_b5_MSN_HIS[17]Diag_NG_over_temp\n" //
                        +"7bb,208,210,1,0,0,,2131,6131,ff,21_31 #027_b0_MSN_HIS[18]Operation_type\n" //
                        +"7bb,214,222,.2,0,0,%,2131,6131,ff,21_31 #027_b6_MSN_HIS[18]User_SOC_end\n" //
                        +"7bb,223,231,.2,0,0,%,2131,6131,ff,21_31 #028_b7_MSN_HIS[18]User_SOC_start\n" //
                        +"7bb,232,239,1,0,0,,2131,6131,ff,21_31 #030_b0_MSN_HIS[18]Ampere_hour_discharge\n" //
                        +"7bb,240,247,1,0,0,,2131,6131,ff,21_31 #031_b0_MSN_HIS[18]Ampere_hour_charge\n" //
                        +"7bb,248,255,1,40,0,,2131,6131,ff,21_31 #032_b0_MSN_HIS[18]Battery_temp_end\n" //
                        +"7bb,256,263,1,40,0,,2131,6131,ff,21_31 #033_b0_MSN_HIS[18]Battery_temp_start\n" //
                        +"7bb,264,269,1,0,0,,2131,6131,ff,defauts mission 18\n" //
                        +"7bb,265,265,1,0,0,,2131,6131,ff,21_31 #034_b1_MSN_HIS[18]Diag_NG_over_discharge_batt\n" //
                        +"7bb,266,266,1,0,0,,2131,6131,ff,21_31 #034_b2_MSN_HIS[18]Diag_NG_over_charge_cell\n" //
                        +"7bb,267,267,1,0,0,,2131,6131,ff,21_31 #034_b3_MSN_HIS[18]Diag_NG_over_discharge_cell\n" //
                        +"7bb,268,268,1,0,0,,2131,6131,ff,21_31 #034_b4_MSN_HIS[18]Diag_NG_over_current\n" //
                        +"7bb,269,269,1,0,0,,2131,6131,ff,21_31 #034_b5_MSN_HIS[18]Diag_NG_over_temp\n" //
                        +"7bb,272,274,1,0,0,,2131,6131,ff,21_31 #035_b0_MSN_HIS[19]Operation_type\n" //
                        +"7bb,278,286,.2,0,0,%,2131,6131,ff,21_31 #035_b6_MSN_HIS[19]User_SOC_end\n" //
                        +"7bb,287,295,.2,0,0,%,2131,6131,ff,21_31 #036_b7_MSN_HIS[19]User_SOC_start\n" //
                        +"7bb,296,303,1,0,0,,2131,6131,ff,21_31 #038_b0_MSN_HIS[19]Ampere_hour_discharge\n" //
                        +"7bb,304,311,1,0,0,,2131,6131,ff,21_31 #039_b0_MSN_HIS[19]Ampere_hour_charge\n" //
                        +"7bb,312,319,1,40,0,,2131,6131,ff,21_31 #040_b0_MSN_HIS[19]Battery_temp_end\n" //
                        +"7bb,320,327,1,40,0,,2131,6131,ff,21_31 #041_b0_MSN_HIS[19]Battery_temp_start\n" //
                        +"7bb,328,333,1,0,0,,2131,6131,ff,defauts mission 19\n" //
                        +"7bb,329,329,1,0,0,,2131,6131,ff,21_31 #042_b1_MSN_HIS[19]Diag_NG_over_discharge_batt\n" //
                        +"7bb,330,330,1,0,0,,2131,6131,ff,21_31 #042_b2_MSN_HIS[19]Diag_NG_over_charge_cell\n" //
                        +"7bb,331,331,1,0,0,,2131,6131,ff,21_31 #042_b3_MSN_HIS[19]Diag_NG_over_discharge_cell\n" //
                        +"7bb,332,332,1,0,0,,2131,6131,ff,21_31 #042_b4_MSN_HIS[19]Diag_NG_over_current\n" //
                        +"7bb,333,333,1,0,0,,2131,6131,ff,21_31 #042_b5_MSN_HIS[19]Diag_NG_over_temp\n" //
                        +"7bb,336,338,1,0,0,,2131,6131,ff,21_31 #043_b0_MSN_HIS[20]Operation_type\n" //
                        +"7bb,342,350,.2,0,0,%,2131,6131,ff,21_31 #043_b6_MSN_HIS[20]User_SOC_end\n" //
                        +"7bb,351,359,.2,0,0,%,2131,6131,ff,21_31 #044_b7_MSN_HIS[20]User_SOC_start\n" //
                        +"7bb,360,367,1,0,0,,2131,6131,ff,21_31 #046_b0_MSN_HIS[20]Ampere_hour_discharge\n" //
                        +"7bb,368,375,1,0,0,,2131,6131,ff,21_31 #047_b0_MSN_HIS[20]Ampere_hour_charge\n" //
                        +"7bb,376,383,1,40,0,,2131,6131,ff,21_31 #048_b0_MSN_HIS[20]Battery_temp_end\n" //
                        +"7bb,384,391,1,40,0,,2131,6131,ff,21_31 #049_b0_MSN_HIS[20]Battery_temp_start\n" //
                        +"7bb,392,397,1,0,0,,2131,6131,ff,defauts mission 20\n" //
                        +"7bb,393,393,1,0,0,,2131,6131,ff,21_31 #050_b1_MSN_HIS[20]Diag_NG_over_discharge_batt\n" //
                        +"7bb,394,394,1,0,0,,2131,6131,ff,21_31 #050_b2_MSN_HIS[20]Diag_NG_over_charge_cell\n" //
                        +"7bb,395,395,1,0,0,,2131,6131,ff,21_31 #050_b3_MSN_HIS[20]Diag_NG_over_discharge_cell\n" //
                        +"7bb,396,396,1,0,0,,2131,6131,ff,21_31 #050_b4_MSN_HIS[20]Diag_NG_over_current\n" //
                        +"7bb,397,397,1,0,0,,2131,6131,ff,21_31 #050_b5_MSN_HIS[20]Diag_NG_over_temp\n" //
                        +"7bb,400,402,1,0,0,,2131,6131,ff,21_31 #051_b0_MSN_HIS[21]Operation_type\n" //
                        +"7bb,406,414,.2,0,0,%,2131,6131,ff,21_31 #051_b6_MSN_HIS[21]User_SOC_end\n" //
                        +"7bb,415,423,.2,0,0,%,2131,6131,ff,21_31 #052_b7_MSN_HIS[21]User_SOC_start\n" //
                        +"7bb,424,431,1,0,0,,2131,6131,ff,21_31 #054_b0_MSN_HIS[21]Ampere_hour_discharge\n" //
                        +"7bb,432,439,1,0,0,,2131,6131,ff,21_31 #055_b0_MSN_HIS[21]Ampere_hour_charge\n" //
                        +"7bb,440,447,1,40,0,,2131,6131,ff,21_31 #056_b0_MSN_HIS[21]Battery_temp_end\n" //
                        +"7bb,448,455,1,40,0,,2131,6131,ff,21_31 #057_b0_MSN_HIS[21]Battery_temp_start\n" //
                        +"7bb,456,461,1,0,0,,2131,6131,ff,defauts mission 21\n" //
                        +"7bb,457,457,1,0,0,,2131,6131,ff,21_31 #058_b1_MSN_HIS[21]Diag_NG_over_discharge_batt\n" //
                        +"7bb,458,458,1,0,0,,2131,6131,ff,21_31 #058_b2_MSN_HIS[21]Diag_NG_over_charge_cell\n" //
                        +"7bb,459,459,1,0,0,,2131,6131,ff,21_31 #058_b3_MSN_HIS[21]Diag_NG_over_discharge_cell\n" //
                        +"7bb,460,460,1,0,0,,2131,6131,ff,21_31 #058_b4_MSN_HIS[21]Diag_NG_over_current\n" //
                        +"7bb,461,461,1,0,0,,2131,6131,ff,21_31 #058_b5_MSN_HIS[21]Diag_NG_over_temp\n" //
                        +"7bb,464,466,1,0,0,,2131,6131,ff,21_31 #059_b0_MSN_HIS[22]Operation_type\n" //
                        +"7bb,470,478,.2,0,0,%,2131,6131,ff,21_31 #059_b6_MSN_HIS[22]User_SOC_end\n" //
                        +"7bb,479,487,.2,0,0,%,2131,6131,ff,21_31 #060_b7_MSN_HIS[22]User_SOC_start\n" //
                        +"7bb,488,495,1,0,0,,2131,6131,ff,21_31 #062_b0_MSN_HIS[22]Ampere_hour_discharge\n" //
                        +"7bb,496,503,1,0,0,,2131,6131,ff,21_31 #063_b0_MSN_HIS[22]Ampere_hour_charge\n" //
                        +"7bb,504,511,1,40,0,,2131,6131,ff,21_31 #064_b0_MSN_HIS[22]Battery_temp_end\n" //
                        +"7bb,512,519,1,40,0,,2131,6131,ff,21_31 #065_b0_MSN_HIS[22]Battery_temp_start\n" //
                        +"7bb,520,525,1,0,0,,2131,6131,ff,defauts mission 22\n" //
                        +"7bb,521,521,1,0,0,,2131,6131,ff,21_31 #066_b1_MSN_HIS[22]Diag_NG_over_discharge_batt\n" //
                        +"7bb,522,522,1,0,0,,2131,6131,ff,21_31 #066_b2_MSN_HIS[22]Diag_NG_over_charge_cell\n" //
                        +"7bb,523,523,1,0,0,,2131,6131,ff,21_31 #066_b3_MSN_HIS[22]Diag_NG_over_discharge_cell\n" //
                        +"7bb,524,524,1,0,0,,2131,6131,ff,21_31 #066_b4_MSN_HIS[22]Diag_NG_over_current\n" //
                        +"7bb,525,525,1,0,0,,2131,6131,ff,21_31 #066_b5_MSN_HIS[22]Diag_NG_over_temp\n" //
                        +"7bb,528,530,1,0,0,,2131,6131,ff,21_31 #067_b0_MSN_HIS[23]Operation_type\n" //
                        +"7bb,534,542,.2,0,0,%,2131,6131,ff,21_31 #067_b6_MSN_HIS[23]User_SOC_end\n" //
                        +"7bb,543,551,.2,0,0,%,2131,6131,ff,21_31 #068_b7_MSN_HIS[23]User_SOC_start\n" //
                        +"7bb,552,559,1,0,0,,2131,6131,ff,21_31 #070_b0_MSN_HIS[23]Ampere_hour_discharge\n" //
                        +"7bb,560,567,1,0,0,,2131,6131,ff,21_31 #071_b0_MSN_HIS[23]Ampere_hour_charge\n" //
                        +"7bb,568,575,1,40,0,,2131,6131,ff,21_31 #072_b0_MSN_HIS[23]Battery_temp_end\n" //
                        +"7bb,576,583,1,40,0,,2131,6131,ff,21_31 #073_b0_MSN_HIS[23]Battery_temp_start\n" //
                        +"7bb,584,589,1,0,0,,2131,6131,ff,defauts mission 23\n" //
                        +"7bb,585,585,1,0,0,,2131,6131,ff,21_31 #074_b1_MSN_HIS[23]Diag_NG_over_discharge_batt\n" //
                        +"7bb,586,586,1,0,0,,2131,6131,ff,21_31 #074_b2_MSN_HIS[23]Diag_NG_over_charge_cell\n" //
                        +"7bb,587,587,1,0,0,,2131,6131,ff,21_31 #074_b3_MSN_HIS[23]Diag_NG_over_discharge_cell\n" //
                        +"7bb,588,588,1,0,0,,2131,6131,ff,21_31 #074_b4_MSN_HIS[23]Diag_NG_over_current\n" //

                ;

        String fieldDef2 =
                ""

                        +"7bb,589,589,1,0,0,,2131,6131,ff,21_31 #074_b5_MSN_HIS[23]Diag_NG_over_temp\n" //
                        +"7bb,592,594,1,0,0,,2131,6131,ff,21_31 #075_b0_MSN_HIS[24]Operation_type\n" //
                        +"7bb,598,606,.2,0,0,%,2131,6131,ff,21_31 #075_b6_MSN_HIS[24]User_SOC_end\n" //
                        +"7bb,607,615,.2,0,0,%,2131,6131,ff,21_31 #076_b7_MSN_HIS[24]User_SOC_start\n" //
                        +"7bb,616,623,1,0,0,,2131,6131,ff,21_31 #078_b0_MSN_HIS[24]Ampere_hour_discharge\n" //
                        +"7bb,624,631,1,0,0,,2131,6131,ff,21_31 #079_b0_MSN_HIS[24]Ampere_hour_charge\n" //
                        +"7bb,632,639,1,40,0,,2131,6131,ff,21_31 #080_b0_MSN_HIS[24]Battery_temp_end\n" //
                        +"7bb,640,647,1,40,0,,2131,6131,ff,21_31 #081_b0_MSN_HIS[24]Battery_temp_start\n" //
                        +"7bb,648,653,1,0,0,,2131,6131,ff,defauts mission 24\n" //
                        +"7bb,649,649,1,0,0,,2131,6131,ff,21_31 #082_b1_MSN_HIS[24]Diag_NG_over_discharge_batt\n" //
                        +"7bb,650,650,1,0,0,,2131,6131,ff,21_31 #082_b2_MSN_HIS[24]Diag_NG_over_charge_cell\n" //
                        +"7bb,651,651,1,0,0,,2131,6131,ff,21_31 #082_b3_MSN_HIS[24]Diag_NG_over_discharge_cell\n" //
                        +"7bb,652,652,1,0,0,,2131,6131,ff,21_31 #082_b4_MSN_HIS[24]Diag_NG_over_current\n" //
                        +"7bb,653,653,1,0,0,,2131,6131,ff,21_31 #082_b5_MSN_HIS[24]Diag_NG_over_temp\n" //
                        +"7bb,656,658,1,0,0,,2131,6131,ff,21_31 #083_b0_MSN_HIS[25]Operation_type\n" //
                        +"7bb,662,670,.2,0,0,%,2131,6131,ff,21_31 #083_b6_MSN_HIS[25]User_SOC_end\n" //
                        +"7bb,671,679,.2,0,0,%,2131,6131,ff,21_31 #084_b7_MSN_HIS[25]User_SOC_start\n" //
                        +"7bb,680,687,1,0,0,,2131,6131,ff,21_31 #086_b0_MSN_HIS[25]Ampere_hour_discharge\n" //
                        +"7bb,688,695,1,0,0,,2131,6131,ff,21_31 #087_b0_MSN_HIS[25]Ampere_hour_charge\n" //
                        +"7bb,696,703,1,40,0,,2131,6131,ff,21_31 #088_b0_MSN_HIS[25]Battery_temp_end\n" //
                        +"7bb,704,711,1,40,0,,2131,6131,ff,21_31 #089_b0_MSN_HIS[25]Battery_temp_start\n" //
                        +"7bb,712,717,1,0,0,,2131,6131,ff,defauts mission 25\n" //
                        +"7bb,713,713,1,0,0,,2131,6131,ff,21_31 #090_b1_MSN_HIS[25]Diag_NG_over_discharge_batt\n" //
                        +"7bb,714,714,1,0,0,,2131,6131,ff,21_31 #090_b2_MSN_HIS[25]Diag_NG_over_charge_cell\n" //
                        +"7bb,715,715,1,0,0,,2131,6131,ff,21_31 #090_b3_MSN_HIS[25]Diag_NG_over_discharge_cell\n" //
                        +"7bb,716,716,1,0,0,,2131,6131,ff,21_31 #090_b4_MSN_HIS[25]Diag_NG_over_current\n" //
                        +"7bb,717,717,1,0,0,,2131,6131,ff,21_31 #090_b5_MSN_HIS[25]Diag_NG_over_temp\n" //
                        +"7bb,720,722,1,0,0,,2131,6131,ff,21_31 #091_b0_MSN_HIS[26]Operation_type\n" //
                        +"7bb,726,734,.2,0,0,%,2131,6131,ff,21_31 #091_b6_MSN_HIS[26]User_SOC_end\n" //
                        +"7bb,735,743,.2,0,0,%,2131,6131,ff,21_31 #092_b7_MSN_HIS[26]User_SOC_start\n" //
                        +"7bb,744,751,1,0,0,,2131,6131,ff,21_31 #094_b0_MSN_HIS[26]Ampere_hour_discharge\n" //
                        +"7bb,752,759,1,0,0,,2131,6131,ff,21_31 #095_b0_MSN_HIS[26]Ampere_hour_charge\n" //
                        +"7bb,760,767,1,40,0,,2131,6131,ff,21_31 #096_b0_MSN_HIS[26]Battery_temp_end\n" //
                        +"7bb,768,775,1,40,0,,2131,6131,ff,21_31 #097_b0_MSN_HIS[26]Battery_temp_start\n" //
                        +"7bb,776,781,1,0,0,,2131,6131,ff,defauts mission 26\n" //
                        +"7bb,777,777,1,0,0,,2131,6131,ff,21_31 #098_b1_MSN_HIS[26]Diag_NG_over_discharge_batt\n" //
                        +"7bb,778,778,1,0,0,,2131,6131,ff,21_31 #098_b2_MSN_HIS[26]Diag_NG_over_charge_cell\n" //
                        +"7bb,779,779,1,0,0,,2131,6131,ff,21_31 #098_b3_MSN_HIS[26]Diag_NG_over_discharge_cell\n" //
                        +"7bb,780,780,1,0,0,,2131,6131,ff,21_31 #098_b4_MSN_HIS[26]Diag_NG_over_current\n" //
                        +"7bb,781,781,1,0,0,,2131,6131,ff,21_31 #098_b5_MSN_HIS[26]Diag_NG_over_temp\n" //
                        +"7bb,784,786,1,0,0,,2131,6131,ff,21_31 #099_b0_MSN_HIS[27]Operation_type\n" //
                        +"7bb,790,798,.2,0,0,%,2131,6131,ff,21_31 #099_b6_MSN_HIS[27]User_SOC_end\n" //
                        +"7bb,799,807,.2,0,0,%,2131,6131,ff,21_31 #100_b7_MSN_HIS[27]User_SOC_start\n" //
                        +"7bb,808,815,1,0,0,,2131,6131,ff,21_31 #102_b0_MSN_HIS[27]Ampere_hour_discharge\n" //
                        +"7bb,816,823,1,0,0,,2131,6131,ff,21_31 #103_b0_MSN_HIS[27]Ampere_hour_charge\n" //
                        +"7bb,824,831,1,40,0,,2131,6131,ff,21_31 #104_b0_MSN_HIS[27]Battery_temp_end\n" //
                        +"7bb,832,839,1,40,0,,2131,6131,ff,21_31 #105_b0_MSN_HIS[27]Battery_temp_start\n" //
                        +"7bb,840,845,1,0,0,,2131,6131,ff,defauts mission 27\n" //
                        +"7bb,841,841,1,0,0,,2131,6131,ff,21_31 #106_b1_MSN_HIS[27]Diag_NG_over_discharge_batt\n" //
                        +"7bb,842,842,1,0,0,,2131,6131,ff,21_31 #106_b2_MSN_HIS[27]Diag_NG_over_charge_cell\n" //
                        +"7bb,843,843,1,0,0,,2131,6131,ff,21_31 #106_b3_MSN_HIS[27]Diag_NG_over_discharge_cell\n" //
                        +"7bb,844,844,1,0,0,,2131,6131,ff,21_31 #106_b4_MSN_HIS[27]Diag_NG_over_current\n" //
                        +"7bb,845,845,1,0,0,,2131,6131,ff,21_31 #106_b5_MSN_HIS[27]Diag_NG_over_temp\n" //
                        +"7bb,848,850,1,0,0,,2131,6131,ff,21_31 #107_b0_MSN_HIS[28]Operation_type\n" //
                        +"7bb,854,862,.2,0,0,%,2131,6131,ff,21_31 #107_b6_MSN_HIS[28]User_SOC_end\n" //
                        +"7bb,863,871,.2,0,0,%,2131,6131,ff,21_31 #108_b7_MSN_HIS[28]User_SOC_start\n" //
                        +"7bb,872,879,1,0,0,,2131,6131,ff,21_31 #110_b0_MSN_HIS[28]Ampere_hour_discharge\n" //
                        +"7bb,880,887,1,0,0,,2131,6131,ff,21_31 #111_b0_MSN_HIS[28]Ampere_hour_charge\n" //
                        +"7bb,888,895,1,40,0,,2131,6131,ff,21_31 #112_b0_MSN_HIS[28]Battery_temp_end\n" //
                        +"7bb,896,903,1,40,0,,2131,6131,ff,21_31 #113_b0_MSN_HIS[28]Battery_temp_start\n" //
                        +"7bb,904,909,1,0,0,,2131,6131,ff,defauts mission 28\n" //
                        +"7bb,905,905,1,0,0,,2131,6131,ff,21_31 #114_b1_MSN_HIS[28]Diag_NG_over_discharge_batt\n" //
                        +"7bb,906,906,1,0,0,,2131,6131,ff,21_31 #114_b2_MSN_HIS[28]Diag_NG_over_charge_cell\n" //
                        +"7bb,907,907,1,0,0,,2131,6131,ff,21_31 #114_b3_MSN_HIS[28]Diag_NG_over_discharge_cell\n" //
                        +"7bb,908,908,1,0,0,,2131,6131,ff,21_31 #114_b4_MSN_HIS[28]Diag_NG_over_current\n" //
                        +"7bb,909,909,1,0,0,,2131,6131,ff,21_31 #114_b5_MSN_HIS[28]Diag_NG_over_temp\n" //
                        +"7bb,912,914,1,0,0,,2131,6131,ff,21_31 #115_b0_MSN_HIS[29]Operation_type\n" //
                        +"7bb,918,926,.2,0,0,%,2131,6131,ff,21_31 #115_b6_MSN_HIS[29]User_SOC_end\n" //
                        +"7bb,927,935,.2,0,0,%,2131,6131,ff,21_31 #116_b7_MSN_HIS[29]User_SOC_start\n" //
                        +"7bb,936,943,1,0,0,,2131,6131,ff,21_31 #118_b0_MSN_HIS[29]Ampere_hour_discharge\n" //
                        +"7bb,944,951,1,0,0,,2131,6131,ff,21_31 #119_b0_MSN_HIS[29]Ampere_hour_charge\n" //
                        +"7bb,952,959,1,40,0,,2131,6131,ff,21_31 #120_b0_MSN_HIS[29]Battery_temp_end\n" //
                        +"7bb,960,967,1,40,0,,2131,6131,ff,21_31 #121_b0_MSN_HIS[29]Battery_temp_start\n" //
                        +"7bb,968,973,1,0,0,,2131,6131,ff,defauts mission 29\n" //
                        +"7bb,969,969,1,0,0,,2131,6131,ff,21_31 #122_b1_MSN_HIS[29]Diag_NG_over_discharge_batt\n" //
                        +"7bb,970,970,1,0,0,,2131,6131,ff,21_31 #122_b2_MSN_HIS[29]Diag_NG_over_charge_cell\n" //
                        +"7bb,971,971,1,0,0,,2131,6131,ff,21_31 #122_b3_MSN_HIS[29]Diag_NG_over_discharge_cell\n" //
                        +"7bb,972,972,1,0,0,,2131,6131,ff,21_31 #122_b4_MSN_HIS[29]Diag_NG_over_current\n" //
                        +"7bb,973,973,1,0,0,,2131,6131,ff,21_31 #122_b5_MSN_HIS[29]Diag_NG_over_temp\n" //
                        +"7bb,16,18,1,0,0,,2132,6132,ff,21_32 #003_b0_MSN_HIS[30]Operation_type\n" //
                        +"7bb,22,30,.2,0,0,%,2132,6132,ff,21_32 #003_b6_MSN_HIS[30]User_SOC_end\n" //
                        +"7bb,31,39,.2,0,0,%,2132,6132,ff,21_32 #004_b7_MSN_HIS[30]User_SOC_start\n" //
                        +"7bb,40,47,1,0,0,,2132,6132,ff,21_32 #006_b0_MSN_HIS[30]Ampere_hour_discharge\n" //
                        +"7bb,48,55,1,0,0,,2132,6132,ff,21_32 #007_b0_MSN_HIS[30]Ampere_hour_charge\n" //
                        +"7bb,56,63,1,40,0,,2132,6132,ff,21_32 #008_b0_MSN_HIS[30]Battery_temp_end\n" //
                        +"7bb,64,71,1,40,0,,2132,6132,ff,21_32 #009_b0_MSN_HIS[30]Battery_temp_start\n" //
                        +"7bb,72,77,1,0,0,,2132,6132,ff,defauts mission 30\n" //
                        +"7bb,73,73,1,0,0,,2132,6132,ff,21_32 #010_b1_MSN_HIS[30]Diag_NG_over_discharge_batt\n" //
                        +"7bb,74,74,1,0,0,,2132,6132,ff,21_32 #010_b2_MSN_HIS[30]Diag_NG_over_charge_cell\n" //
                        +"7bb,75,75,1,0,0,,2132,6132,ff,21_32 #010_b3_MSN_HIS[30]Diag_NG_over_discharge_cell\n" //
                        +"7bb,76,76,1,0,0,,2132,6132,ff,21_32 #010_b4_MSN_HIS[30]Diag_NG_over_current\n" //
                        +"7bb,77,77,1,0,0,,2132,6132,ff,21_32 #010_b5_MSN_HIS[30]Diag_NG_over_temp\n" //
                        +"7bb,80,82,1,0,0,,2132,6132,ff,21_32 #011_b0_MSN_HIS[31]Operation_type\n" //
                        +"7bb,86,94,.2,0,0,%,2132,6132,ff,21_32 #011_b6_MSN_HIS[31]User_SOC_end\n" //
                        +"7bb,95,103,.2,0,0,%,2132,6132,ff,21_32 #012_b7_MSN_HIS[31]User_SOC_start\n" //
                        +"7bb,104,111,1,0,0,,2132,6132,ff,21_32 #014_b0_MSN_HIS[31]Ampere_hour_discharge\n" //
                        +"7bb,112,119,1,0,0,,2132,6132,ff,21_32 #015_b0_MSN_HIS[31]Ampere_hour_charge\n" //
                        +"7bb,120,127,1,40,0,,2132,6132,ff,21_32 #016_b0_MSN_HIS[31]Battery_temp_end\n" //
                        +"7bb,128,135,1,40,0,,2132,6132,ff,21_32 #017_b0_MSN_HIS[31]Battery_temp_start\n" //
                        +"7bb,136,141,1,0,0,,2132,6132,ff,defauts mission 31\n" //
                        +"7bb,137,137,1,0,0,,2132,6132,ff,21_32 #018_b1_MSN_HIS[31]Diag_NG_over_discharge_batt\n" //
                        +"7bb,138,138,1,0,0,,2132,6132,ff,21_32 #018_b2_MSN_HIS[31]Diag_NG_over_charge_cell\n" //
                        +"7bb,139,139,1,0,0,,2132,6132,ff,21_32 #018_b3_MSN_HIS[31]Diag_NG_over_discharge_cell\n" //
                        +"7bb,140,140,1,0,0,,2132,6132,ff,21_32 #018_b4_MSN_HIS[31]Diag_NG_over_current\n" //
                        +"7bb,141,141,1,0,0,,2132,6132,ff,21_32 #018_b5_MSN_HIS[31]Diag_NG_over_temp\n" //
                        +"7bb,144,146,1,0,0,,2132,6132,ff,21_32 #019_b0_MSN_HIS[32]Operation_type\n" //
                        +"7bb,150,158,.2,0,0,%,2132,6132,ff,21_32 #019_b6_MSN_HIS[32]User_SOC_end\n" //
                        +"7bb,159,167,.2,0,0,%,2132,6132,ff,21_32 #020_b7_MSN_HIS[32]User_SOC_start\n" //
                        +"7bb,168,175,1,0,0,,2132,6132,ff,21_32 #022_b0_MSN_HIS[32]Ampere_hour_discharge\n" //
                        +"7bb,176,183,1,0,0,,2132,6132,ff,21_32 #023_b0_MSN_HIS[32]Ampere_hour_charge\n" //
                        +"7bb,184,191,1,40,0,,2132,6132,ff,21_32 #024_b0_MSN_HIS[32]Battery_temp_end\n" //
                        +"7bb,192,199,1,40,0,,2132,6132,ff,21_32 #025_b0_MSN_HIS[32]Battery_temp_start\n" //
                        +"7bb,200,205,1,0,0,,2132,6132,ff,defauts mission 32\n" //
                        +"7bb,201,201,1,0,0,,2132,6132,ff,21_32 #026_b1_MSN_HIS[32]Diag_NG_over_discharge_batt\n" //
                        +"7bb,202,202,1,0,0,,2132,6132,ff,21_32 #026_b2_MSN_HIS[32]Diag_NG_over_charge_cell\n" //
                        +"7bb,203,203,1,0,0,,2132,6132,ff,21_32 #026_b3_MSN_HIS[32]Diag_NG_over_discharge_cell\n" //
                        +"7bb,204,204,1,0,0,,2132,6132,ff,21_32 #026_b4_MSN_HIS[32]Diag_NG_over_current\n" //
                        +"7bb,205,205,1,0,0,,2132,6132,ff,21_32 #026_b5_MSN_HIS[32]Diag_NG_over_temp\n" //
                        +"7bb,208,210,1,0,0,,2132,6132,ff,21_32 #027_b0_MSN_HIS[33]Operation_type\n" //
                        +"7bb,214,222,.2,0,0,%,2132,6132,ff,21_32 #027_b6_MSN_HIS[33]User_SOC_end\n" //
                        +"7bb,223,231,.2,0,0,%,2132,6132,ff,21_32 #028_b7_MSN_HIS[33]User_SOC_start\n" //
                        +"7bb,232,239,1,0,0,,2132,6132,ff,21_32 #030_b0_MSN_HIS[33]Ampere_hour_discharge\n" //
                        +"7bb,240,247,1,0,0,,2132,6132,ff,21_32 #031_b0_MSN_HIS[33]Ampere_hour_charge\n" //
                        +"7bb,248,255,1,40,0,,2132,6132,ff,21_32 #032_b0_MSN_HIS[33]Battery_temp_end\n" //
                        +"7bb,256,263,1,40,0,,2132,6132,ff,21_32 #033_b0_MSN_HIS[33]Battery_temp_start\n" //
                        +"7bb,264,269,1,0,0,,2132,6132,ff,defauts mission 33\n" //
                        +"7bb,265,265,1,0,0,,2132,6132,ff,21_32 #034_b1_MSN_HIS[33]Diag_NG_over_discharge_batt\n" //
                        +"7bb,266,266,1,0,0,,2132,6132,ff,21_32 #034_b2_MSN_HIS[33]Diag_NG_over_charge_cell\n" //
                        +"7bb,267,267,1,0,0,,2132,6132,ff,21_32 #034_b3_MSN_HIS[33]Diag_NG_over_discharge_cell\n" //
                        +"7bb,268,268,1,0,0,,2132,6132,ff,21_32 #034_b4_MSN_HIS[33]Diag_NG_over_current\n" //
                        +"7bb,269,269,1,0,0,,2132,6132,ff,21_32 #034_b5_MSN_HIS[33]Diag_NG_over_temp\n" //
                        +"7bb,272,274,1,0,0,,2132,6132,ff,21_32 #035_b0_MSN_HIS[34]Operation_type\n" //
                        +"7bb,278,286,.2,0,0,%,2132,6132,ff,21_32 #035_b6_MSN_HIS[34]User_SOC_end\n" //
                        +"7bb,287,295,.2,0,0,%,2132,6132,ff,21_32 #036_b7_MSN_HIS[34]User_SOC_start\n" //
                        +"7bb,296,303,1,0,0,,2132,6132,ff,21_32 #038_b0_MSN_HIS[34]Ampere_hour_discharge\n" //
                        +"7bb,304,311,1,0,0,,2132,6132,ff,21_32 #039_b0_MSN_HIS[34]Ampere_hour_charge\n" //
                        +"7bb,312,319,1,40,0,,2132,6132,ff,21_32 #040_b0_MSN_HIS[34]Battery_temp_end\n" //
                        +"7bb,320,327,1,40,0,,2132,6132,ff,21_32 #041_b0_MSN_HIS[34]Battery_temp_start\n" //
                        +"7bb,328,333,1,0,0,,2132,6132,ff,defauts mission 34\n" //
                        +"7bb,329,329,1,0,0,,2132,6132,ff,21_32 #042_b1_MSN_HIS[34]Diag_NG_over_discharge_batt\n" //
                        +"7bb,330,330,1,0,0,,2132,6132,ff,21_32 #042_b2_MSN_HIS[34]Diag_NG_over_charge_cell\n" //
                        +"7bb,331,331,1,0,0,,2132,6132,ff,21_32 #042_b3_MSN_HIS[34]Diag_NG_over_discharge_cell\n" //
                        +"7bb,332,332,1,0,0,,2132,6132,ff,21_32 #042_b4_MSN_HIS[34]Diag_NG_over_current\n" //
                        +"7bb,333,333,1,0,0,,2132,6132,ff,21_32 #042_b5_MSN_HIS[34]Diag_NG_over_temp\n" //
                        +"7bb,336,338,1,0,0,,2132,6132,ff,21_32 #043_b0_MSN_HIS[35]Operation_type\n" //
                        +"7bb,342,350,.2,0,0,%,2132,6132,ff,21_32 #043_b6_MSN_HIS[35]User_SOC_end\n" //
                        +"7bb,351,359,.2,0,0,%,2132,6132,ff,21_32 #044_b7_MSN_HIS[35]User_SOC_start\n" //
                        +"7bb,360,367,1,0,0,,2132,6132,ff,21_32 #046_b0_MSN_HIS[35]Ampere_hour_discharge\n" //
                        +"7bb,368,375,1,0,0,,2132,6132,ff,21_32 #047_b0_MSN_HIS[35]Ampere_hour_charge\n" //
                        +"7bb,376,383,1,40,0,,2132,6132,ff,21_32 #048_b0_MSN_HIS[35]Battery_temp_end\n" //
                        +"7bb,384,391,1,40,0,,2132,6132,ff,21_32 #049_b0_MSN_HIS[35]Battery_temp_start\n" //
                        +"7bb,392,397,1,0,0,,2132,6132,ff,defauts mission 35\n" //
                        +"7bb,393,393,1,0,0,,2132,6132,ff,21_32 #050_b1_MSN_HIS[35]Diag_NG_over_discharge_batt\n" //
                        +"7bb,394,394,1,0,0,,2132,6132,ff,21_32 #050_b2_MSN_HIS[35]Diag_NG_over_charge_cell\n" //
                        +"7bb,395,395,1,0,0,,2132,6132,ff,21_32 #050_b3_MSN_HIS[35]Diag_NG_over_discharge_cell\n" //
                        +"7bb,396,396,1,0,0,,2132,6132,ff,21_32 #050_b4_MSN_HIS[35]Diag_NG_over_current\n" //
                        +"7bb,397,397,1,0,0,,2132,6132,ff,21_32 #050_b5_MSN_HIS[35]Diag_NG_over_temp\n" //
                        +"7bb,400,402,1,0,0,,2132,6132,ff,21_32 #051_b0_MSN_HIS[36]Operation_type\n" //
                        +"7bb,406,414,.2,0,0,%,2132,6132,ff,21_32 #051_b6_MSN_HIS[36]User_SOC_end\n" //
                        +"7bb,415,423,.2,0,0,%,2132,6132,ff,21_32 #052_b7_MSN_HIS[36]User_SOC_start\n" //
                        +"7bb,424,431,1,0,0,,2132,6132,ff,21_32 #054_b0_MSN_HIS[36]Ampere_hour_discharge\n" //
                        +"7bb,432,439,1,0,0,,2132,6132,ff,21_32 #055_b0_MSN_HIS[36]Ampere_hour_charge\n" //
                        +"7bb,440,447,1,40,0,,2132,6132,ff,21_32 #056_b0_MSN_HIS[36]Battery_temp_end\n" //
                        +"7bb,448,455,1,40,0,,2132,6132,ff,21_32 #057_b0_MSN_HIS[36]Battery_temp_start\n" //
                        +"7bb,456,461,1,0,0,,2132,6132,ff,defauts mission 36\n" //
                        +"7bb,457,457,1,0,0,,2132,6132,ff,21_32 #058_b1_MSN_HIS[36]Diag_NG_over_discharge_batt\n" //
                        +"7bb,458,458,1,0,0,,2132,6132,ff,21_32 #058_b2_MSN_HIS[36]Diag_NG_over_charge_cell\n" //
                        +"7bb,459,459,1,0,0,,2132,6132,ff,21_32 #058_b3_MSN_HIS[36]Diag_NG_over_discharge_cell\n" //
                        +"7bb,460,460,1,0,0,,2132,6132,ff,21_32 #058_b4_MSN_HIS[36]Diag_NG_over_current\n" //
                        +"7bb,461,461,1,0,0,,2132,6132,ff,21_32 #058_b5_MSN_HIS[36]Diag_NG_over_temp\n" //
                        +"7bb,464,466,1,0,0,,2132,6132,ff,21_32 #059_b0_MSN_HIS[37]Operation_type\n" //
                        +"7bb,470,478,.2,0,0,%,2132,6132,ff,21_32 #059_b6_MSN_HIS[37]User_SOC_end\n" //
                        +"7bb,479,487,.2,0,0,%,2132,6132,ff,21_32 #060_b7_MSN_HIS[37]User_SOC_start\n" //
                        +"7bb,488,495,1,0,0,,2132,6132,ff,21_32 #062_b0_MSN_HIS[37]Ampere_hour_discharge\n" //
                        +"7bb,496,503,1,0,0,,2132,6132,ff,21_32 #063_b0_MSN_HIS[37]Ampere_hour_charge\n" //
                        +"7bb,504,511,1,40,0,,2132,6132,ff,21_32 #064_b0_MSN_HIS[37]Battery_temp_end\n" //
                        +"7bb,512,519,1,40,0,,2132,6132,ff,21_32 #065_b0_MSN_HIS[37]Battery_temp_start\n" //
                        +"7bb,520,525,1,0,0,,2132,6132,ff,defauts mission 37\n" //
                        +"7bb,521,521,1,0,0,,2132,6132,ff,21_32 #066_b1_MSN_HIS[37]Diag_NG_over_discharge_batt\n" //
                        +"7bb,522,522,1,0,0,,2132,6132,ff,21_32 #066_b2_MSN_HIS[37]Diag_NG_over_charge_cell\n" //
                        +"7bb,523,523,1,0,0,,2132,6132,ff,21_32 #066_b3_MSN_HIS[37]Diag_NG_over_discharge_cell\n" //
                        +"7bb,524,524,1,0,0,,2132,6132,ff,21_32 #066_b4_MSN_HIS[37]Diag_NG_over_current\n" //
                        +"7bb,525,525,1,0,0,,2132,6132,ff,21_32 #066_b5_MSN_HIS[37]Diag_NG_over_temp\n" //
                        +"7bb,528,530,1,0,0,,2132,6132,ff,21_32 #067_b0_MSN_HIS[38]Operation_type\n" //
                        +"7bb,534,542,.2,0,0,%,2132,6132,ff,21_32 #067_b6_MSN_HIS[38]User_SOC_end\n" //
                        +"7bb,543,551,.2,0,0,%,2132,6132,ff,21_32 #068_b7_MSN_HIS[38]User_SOC_start\n" //
                        +"7bb,552,559,1,0,0,,2132,6132,ff,21_32 #070_b0_MSN_HIS[38]Ampere_hour_discharge\n" //
                        +"7bb,560,567,1,0,0,,2132,6132,ff,21_32 #071_b0_MSN_HIS[38]Ampere_hour_charge\n" //
                        +"7bb,568,575,1,40,0,,2132,6132,ff,21_32 #072_b0_MSN_HIS[38]Battery_temp_end\n" //
                        +"7bb,576,583,1,40,0,,2132,6132,ff,21_32 #073_b0_MSN_HIS[38]Battery_temp_start\n" //
                        +"7bb,584,589,1,0,0,,2132,6132,ff,defauts mission 38\n" //
                        +"7bb,585,585,1,0,0,,2132,6132,ff,21_32 #074_b1_MSN_HIS[38]Diag_NG_over_discharge_batt\n" //
                        +"7bb,586,586,1,0,0,,2132,6132,ff,21_32 #074_b2_MSN_HIS[38]Diag_NG_over_charge_cell\n" //
                        +"7bb,587,587,1,0,0,,2132,6132,ff,21_32 #074_b3_MSN_HIS[38]Diag_NG_over_discharge_cell\n" //
                        +"7bb,588,588,1,0,0,,2132,6132,ff,21_32 #074_b4_MSN_HIS[38]Diag_NG_over_current\n" //
                        +"7bb,589,589,1,0,0,,2132,6132,ff,21_32 #074_b5_MSN_HIS[38]Diag_NG_over_temp\n" //
                        +"7bb,592,594,1,0,0,,2132,6132,ff,21_32 #075_b0_MSN_HIS[39]Operation_type\n" //
                        +"7bb,598,606,.2,0,0,%,2132,6132,ff,21_32 #075_b6_MSN_HIS[39]User_SOC_end\n" //
                        +"7bb,607,615,.2,0,0,%,2132,6132,ff,21_32 #076_b7_MSN_HIS[39]User_SOC_start\n" //
                        +"7bb,616,623,1,0,0,,2132,6132,ff,21_32 #078_b0_MSN_HIS[39]Ampere_hour_discharge\n" //
                        +"7bb,624,631,1,0,0,,2132,6132,ff,21_32 #079_b0_MSN_HIS[39]Ampere_hour_charge\n" //
                        +"7bb,632,639,1,40,0,,2132,6132,ff,21_32 #080_b0_MSN_HIS[39]Battery_temp_end\n" //
                        +"7bb,640,647,1,40,0,,2132,6132,ff,21_32 #081_b0_MSN_HIS[39]Battery_temp_start\n" //
                        +"7bb,648,653,1,0,0,,2132,6132,ff,defauts mission 39\n" //
                        +"7bb,649,649,1,0,0,,2132,6132,ff,21_32 #082_b1_MSN_HIS[39]Diag_NG_over_discharge_batt\n" //
                        +"7bb,650,650,1,0,0,,2132,6132,ff,21_32 #082_b2_MSN_HIS[39]Diag_NG_over_charge_cell\n" //
                        +"7bb,651,651,1,0,0,,2132,6132,ff,21_32 #082_b3_MSN_HIS[39]Diag_NG_over_discharge_cell\n" //
                        +"7bb,652,652,1,0,0,,2132,6132,ff,21_32 #082_b4_MSN_HIS[39]Diag_NG_over_current\n" //
                        +"7bb,653,653,1,0,0,,2132,6132,ff,21_32 #082_b5_MSN_HIS[39]Diag_NG_over_temp\n" //
                        +"7bb,656,658,1,0,0,,2132,6132,ff,21_32 #083_b0_MSN_HIS[40]Operation_type\n" //
                        +"7bb,662,670,.2,0,0,%,2132,6132,ff,21_32 #083_b6_MSN_HIS[40]User_SOC_end\n" //
                        +"7bb,671,679,.2,0,0,%,2132,6132,ff,21_32 #084_b7_MSN_HIS[40]User_SOC_start\n" //
                        +"7bb,680,687,1,0,0,,2132,6132,ff,21_32 #086_b0_MSN_HIS[40]Ampere_hour_discharge\n" //
                        +"7bb,688,695,1,0,0,,2132,6132,ff,21_32 #087_b0_MSN_HIS[40]Ampere_hour_charge\n" //
                        +"7bb,696,703,1,40,0,,2132,6132,ff,21_32 #088_b0_MSN_HIS[40]Battery_temp_end\n" //
                        +"7bb,704,711,1,40,0,,2132,6132,ff,21_32 #089_b0_MSN_HIS[40]Battery_temp_start\n" //
                        +"7bb,712,717,1,0,0,,2132,6132,ff,defauts mission 40\n" //
                        +"7bb,713,713,1,0,0,,2132,6132,ff,21_32 #090_b1_MSN_HIS[40]Diag_NG_over_discharge_batt\n" //
                        +"7bb,714,714,1,0,0,,2132,6132,ff,21_32 #090_b2_MSN_HIS[40]Diag_NG_over_charge_cell\n" //
                        +"7bb,715,715,1,0,0,,2132,6132,ff,21_32 #090_b3_MSN_HIS[40]Diag_NG_over_discharge_cell\n" //
                        +"7bb,716,716,1,0,0,,2132,6132,ff,21_32 #090_b4_MSN_HIS[40]Diag_NG_over_current\n" //
                        +"7bb,717,717,1,0,0,,2132,6132,ff,21_32 #090_b5_MSN_HIS[40]Diag_NG_over_temp\n" //
                        +"7bb,720,722,1,0,0,,2132,6132,ff,21_32 #091_b0_MSN_HIS[41]Operation_type\n" //
                        +"7bb,726,734,.2,0,0,%,2132,6132,ff,21_32 #091_b6_MSN_HIS[41]User_SOC_end\n" //
                        +"7bb,735,743,.2,0,0,%,2132,6132,ff,21_32 #092_b7_MSN_HIS[41]User_SOC_start\n" //
                        +"7bb,744,751,1,0,0,,2132,6132,ff,21_32 #094_b0_MSN_HIS[41]Ampere_hour_discharge\n" //
                        +"7bb,752,759,1,0,0,,2132,6132,ff,21_32 #095_b0_MSN_HIS[41]Ampere_hour_charge\n" //
                        +"7bb,760,767,1,40,0,,2132,6132,ff,21_32 #096_b0_MSN_HIS[41]Battery_temp_end\n" //
                        +"7bb,768,775,1,40,0,,2132,6132,ff,21_32 #097_b0_MSN_HIS[41]Battery_temp_start\n" //
                        +"7bb,776,781,1,0,0,,2132,6132,ff,defauts mission 41\n" //
                        +"7bb,777,777,1,0,0,,2132,6132,ff,21_32 #098_b1_MSN_HIS[41]Diag_NG_over_discharge_batt\n" //
                        +"7bb,778,778,1,0,0,,2132,6132,ff,21_32 #098_b2_MSN_HIS[41]Diag_NG_over_charge_cell\n" //
                        +"7bb,779,779,1,0,0,,2132,6132,ff,21_32 #098_b3_MSN_HIS[41]Diag_NG_over_discharge_cell\n" //
                        +"7bb,780,780,1,0,0,,2132,6132,ff,21_32 #098_b4_MSN_HIS[41]Diag_NG_over_current\n" //
                        +"7bb,781,781,1,0,0,,2132,6132,ff,21_32 #098_b5_MSN_HIS[41]Diag_NG_over_temp\n" //
                        +"7bb,784,786,1,0,0,,2132,6132,ff,21_32 #099_b0_MSN_HIS[42]Operation_type\n" //
                        +"7bb,790,798,.2,0,0,%,2132,6132,ff,21_32 #099_b6_MSN_HIS[42]User_SOC_end\n" //
                        +"7bb,799,807,.2,0,0,%,2132,6132,ff,21_32 #100_b7_MSN_HIS[42]User_SOC_start\n" //
                        +"7bb,808,815,1,0,0,,2132,6132,ff,21_32 #102_b0_MSN_HIS[42]Ampere_hour_discharge\n" //
                        +"7bb,816,823,1,0,0,,2132,6132,ff,21_32 #103_b0_MSN_HIS[42]Ampere_hour_charge\n" //
                        +"7bb,824,831,1,40,0,,2132,6132,ff,21_32 #104_b0_MSN_HIS[42]Battery_temp_end\n" //
                        +"7bb,832,839,1,40,0,,2132,6132,ff,21_32 #105_b0_MSN_HIS[42]Battery_temp_start\n" //
                        +"7bb,840,845,1,0,0,,2132,6132,ff,defauts mission 42\n" //
                        +"7bb,841,841,1,0,0,,2132,6132,ff,21_32 #106_b1_MSN_HIS[42]Diag_NG_over_discharge_batt\n" //
                        +"7bb,842,842,1,0,0,,2132,6132,ff,21_32 #106_b2_MSN_HIS[42]Diag_NG_over_charge_cell\n" //
                        +"7bb,843,843,1,0,0,,2132,6132,ff,21_32 #106_b3_MSN_HIS[42]Diag_NG_over_discharge_cell\n" //
                        +"7bb,844,844,1,0,0,,2132,6132,ff,21_32 #106_b4_MSN_HIS[42]Diag_NG_over_current\n" //
                        +"7bb,845,845,1,0,0,,2132,6132,ff,21_32 #106_b5_MSN_HIS[42]Diag_NG_over_temp\n" //
                        +"7bb,848,850,1,0,0,,2132,6132,ff,21_32 #107_b0_MSN_HIS[43]Operation_type\n" //
                        +"7bb,854,862,.2,0,0,%,2132,6132,ff,21_32 #107_b6_MSN_HIS[43]User_SOC_end\n" //
                        +"7bb,863,871,.2,0,0,%,2132,6132,ff,21_32 #108_b7_MSN_HIS[43]User_SOC_start\n" //
                        +"7bb,872,879,1,0,0,,2132,6132,ff,21_32 #110_b0_MSN_HIS[43]Ampere_hour_discharge\n" //
                        +"7bb,880,887,1,0,0,,2132,6132,ff,21_32 #111_b0_MSN_HIS[43]Ampere_hour_charge\n" //
                        +"7bb,888,895,1,40,0,,2132,6132,ff,21_32 #112_b0_MSN_HIS[43]Battery_temp_end\n" //
                        +"7bb,896,903,1,40,0,,2132,6132,ff,21_32 #113_b0_MSN_HIS[43]Battery_temp_start\n" //
                        +"7bb,904,909,1,0,0,,2132,6132,ff,defauts mission 43\n" //
                        +"7bb,905,905,1,0,0,,2132,6132,ff,21_32 #114_b1_MSN_HIS[43]Diag_NG_over_discharge_batt\n" //
                        +"7bb,906,906,1,0,0,,2132,6132,ff,21_32 #114_b2_MSN_HIS[43]Diag_NG_over_charge_cell\n" //
                        +"7bb,907,907,1,0,0,,2132,6132,ff,21_32 #114_b3_MSN_HIS[43]Diag_NG_over_discharge_cell\n" //
                        +"7bb,908,908,1,0,0,,2132,6132,ff,21_32 #114_b4_MSN_HIS[43]Diag_NG_over_current\n" //
                        +"7bb,909,909,1,0,0,,2132,6132,ff,21_32 #114_b5_MSN_HIS[43]Diag_NG_over_temp\n" //
                        +"7bb,912,914,1,0,0,,2132,6132,ff,21_32 #115_b0_MSN_HIS[44]Operation_type\n" //
                        +"7bb,918,926,.2,0,0,%,2132,6132,ff,21_32 #115_b6_MSN_HIS[44]User_SOC_end\n" //
                        +"7bb,927,935,.2,0,0,%,2132,6132,ff,21_32 #116_b7_MSN_HIS[44]User_SOC_start\n" //
                        +"7bb,936,943,1,0,0,,2132,6132,ff,21_32 #118_b0_MSN_HIS[44]Ampere_hour_discharge\n" //
                        +"7bb,944,951,1,0,0,,2132,6132,ff,21_32 #119_b0_MSN_HIS[44]Ampere_hour_charge\n" //
                        +"7bb,952,959,1,40,0,,2132,6132,ff,21_32 #120_b0_MSN_HIS[44]Battery_temp_end\n" //
                        +"7bb,960,967,1,40,0,,2132,6132,ff,21_32 #121_b0_MSN_HIS[44]Battery_temp_start\n" //
                        +"7bb,968,973,1,0,0,,2132,6132,ff,defauts mission 44\n" //
                        +"7bb,969,969,1,0,0,,2132,6132,ff,21_32 #122_b1_MSN_HIS[44]Diag_NG_over_discharge_batt\n" //
                        +"7bb,970,970,1,0,0,,2132,6132,ff,21_32 #122_b2_MSN_HIS[44]Diag_NG_over_charge_cell\n" //
                        +"7bb,971,971,1,0,0,,2132,6132,ff,21_32 #122_b3_MSN_HIS[44]Diag_NG_over_discharge_cell\n" //
                        +"7bb,972,972,1,0,0,,2132,6132,ff,21_32 #122_b4_MSN_HIS[44]Diag_NG_over_current\n" //
                        +"7bb,973,973,1,0,0,,2132,6132,ff,21_32 #122_b5_MSN_HIS[44]Diag_NG_over_temp\n" //
                        +"7bb,16,18,1,0,0,,2133,6133,ff,21_33 #003_b0_MSN_HIS[45]Operation_type\n" //
                        +"7bb,22,30,.2,0,0,%,2133,6133,ff,21_33 #003_b6_MSN_HIS[45]User_SOC_end\n" //
                        +"7bb,31,39,.2,0,0,%,2133,6133,ff,21_33 #004_b7_MSN_HIS[45]User_SOC_start\n" //
                        +"7bb,40,47,1,0,0,,2133,6133,ff,21_33 #006_b0_MSN_HIS[45]Ampere_hour_discharge\n" //
                        +"7bb,48,55,1,0,0,,2133,6133,ff,21_33 #007_b0_MSN_HIS[45]Ampere_hour_charge\n" //
                        +"7bb,56,63,1,40,0,,2133,6133,ff,21_33 #008_b0_MSN_HIS[45]Battery_temp_end\n" //
                        +"7bb,64,71,1,40,0,,2133,6133,ff,21_33 #009_b0_MSN_HIS[45]Battery_temp_start\n" //
                        +"7bb,72,77,1,0,0,,2133,6133,ff,defauts mission 45\n" //
                        +"7bb,73,73,1,0,0,,2133,6133,ff,21_33 #010_b1_MSN_HIS[45]Diag_NG_over_discharge_batt\n" //
                        +"7bb,74,74,1,0,0,,2133,6133,ff,21_33 #010_b2_MSN_HIS[45]Diag_NG_over_charge_cell\n" //
                        +"7bb,75,75,1,0,0,,2133,6133,ff,21_33 #010_b3_MSN_HIS[45]Diag_NG_over_discharge_cell\n" //
                        +"7bb,76,76,1,0,0,,2133,6133,ff,21_33 #010_b4_MSN_HIS[45]Diag_NG_over_current\n" //
                        +"7bb,77,77,1,0,0,,2133,6133,ff,21_33 #010_b5_MSN_HIS[45]Diag_NG_over_temp\n" //
                        +"7bb,80,82,1,0,0,,2133,6133,ff,21_33 #011_b0_MSN_HIS[46]Operation_type\n" //
                        +"7bb,86,94,.2,0,0,%,2133,6133,ff,21_33 #011_b6_MSN_HIS[46]User_SOC_end\n" //
                        +"7bb,95,103,.2,0,0,%,2133,6133,ff,21_33 #012_b7_MSN_HIS[46]User_SOC_start\n" //
                        +"7bb,104,111,1,0,0,,2133,6133,ff,21_33 #014_b0_MSN_HIS[46]Ampere_hour_discharge\n" //
                        +"7bb,112,119,1,0,0,,2133,6133,ff,21_33 #015_b0_MSN_HIS[46]Ampere_hour_charge\n" //
                        +"7bb,120,127,1,40,0,,2133,6133,ff,21_33 #016_b0_MSN_HIS[46]Battery_temp_end\n" //
                        +"7bb,128,135,1,40,0,,2133,6133,ff,21_33 #017_b0_MSN_HIS[46]Battery_temp_start\n" //
                        +"7bb,136,141,1,0,0,,2133,6133,ff,defauts mission 46\n" //
                        +"7bb,137,137,1,0,0,,2133,6133,ff,21_33 #018_b1_MSN_HIS[46]Diag_NG_over_discharge_batt\n" //
                        +"7bb,138,138,1,0,0,,2133,6133,ff,21_33 #018_b2_MSN_HIS[46]Diag_NG_over_charge_cell\n" //
                        +"7bb,139,139,1,0,0,,2133,6133,ff,21_33 #018_b3_MSN_HIS[46]Diag_NG_over_discharge_cell\n" //
                        +"7bb,140,140,1,0,0,,2133,6133,ff,21_33 #018_b4_MSN_HIS[46]Diag_NG_over_current\n" //
                        +"7bb,141,141,1,0,0,,2133,6133,ff,21_33 #018_b5_MSN_HIS[46]Diag_NG_over_temp\n" //
                        +"7bb,144,146,1,0,0,,2133,6133,ff,21_33 #019_b0_MSN_HIS[47]Operation_type\n" //
                        +"7bb,150,158,1,0,0,%,2133,6133,ff,21_33 #019_b6_MSN_HIS[47]User_SOC_end\n" //
                        +"7bb,159,167,1,0,0,%,2133,6133,ff,21_33 #020_b7_MSN_HIS[47]User_SOC_start\n" //
                        +"7bb,168,175,1,0,0,,2133,6133,ff,21_33 #022_b0_MSN_HIS[47]Ampere_hour_discharge\n" //
                        +"7bb,176,183,1,0,0,,2133,6133,ff,21_33 #023_b0_MSN_HIS[47]Ampere_hour_charge\n" //
                        +"7bb,184,191,1,40,0,,2133,6133,ff,21_33 #024_b0_MSN_HIS[47]Battery_temp_end\n" //
                        +"7bb,192,199,1,40,0,,2133,6133,ff,21_33 #025_b0_MSN_HIS[47]Battery_temp_start\n" //
                        +"7bb,200,205,1,0,0,,2133,6133,ff,defauts mission 47\n" //
                        +"7bb,201,201,1,0,0,,2133,6133,ff,21_33 #026_b1_MSN_HIS[47]Diag_NG_over_discharge_batt\n" //
                        +"7bb,202,202,1,0,0,,2133,6133,ff,21_33 #026_b2_MSN_HIS[47]Diag_NG_over_charge_cell\n" //
                        +"7bb,203,203,1,0,0,,2133,6133,ff,21_33 #026_b3_MSN_HIS[47]Diag_NG_over_discharge_cell\n" //
                        +"7bb,204,204,1,0,0,,2133,6133,ff,21_33 #026_b4_MSN_HIS[47]Diag_NG_over_current\n" //
                        +"7bb,205,205,1,0,0,,2133,6133,ff,21_33 #026_b5_MSN_HIS[47]Diag_NG_over_temp\n" //
                        +"7bb,208,210,1,0,0,,2133,6133,ff,21_33 #027_b0_MSN_HIS[48]Operation_type\n" //
                        +"7bb,214,222,1,0,0,%,2133,6133,ff,21_33 #027_b6_MSN_HIS[48]User_SOC_end\n" //
                        +"7bb,223,231,1,0,0,%,2133,6133,ff,21_33 #028_b7_MSN_HIS[48]User_SOC_start\n" //
                        +"7bb,232,239,1,0,0,,2133,6133,ff,21_33 #030_b0_MSN_HIS[48]Ampere_hour_discharge\n" //
                        +"7bb,240,247,1,0,0,,2133,6133,ff,21_33 #031_b0_MSN_HIS[48]Ampere_hour_charge\n" //
                        +"7bb,248,255,1,40,0,,2133,6133,ff,21_33 #032_b0_MSN_HIS[48]Battery_temp_end\n" //
                        +"7bb,256,263,1,40,0,,2133,6133,ff,21_33 #033_b0_MSN_HIS[48]Battery_temp_start\n" //
                        +"7bb,264,269,1,0,0,,2133,6133,ff,defauts mission 48\n" //
                        +"7bb,265,265,1,0,0,,2133,6133,ff,21_33 #034_b1_MSN_HIS[48]Diag_NG_over_discharge_batt\n" //
                        +"7bb,266,266,1,0,0,,2133,6133,ff,21_33 #034_b2_MSN_HIS[48]Diag_NG_over_charge_cell\n" //
                        +"7bb,267,267,1,0,0,,2133,6133,ff,21_33 #034_b3_MSN_HIS[48]Diag_NG_over_discharge_cell\n" //
                        +"7bb,268,268,1,0,0,,2133,6133,ff,21_33 #034_b4_MSN_HIS[48]Diag_NG_over_current\n" //
                        +"7bb,269,269,1,0,0,,2133,6133,ff,21_33 #034_b5_MSN_HIS[48]Diag_NG_over_temp\n" //
                        +"7bb,272,274,1,0,0,,2133,6133,ff,21_33 #035_b0_MSN_HIS[49]Operation_type\n" //
                        +"7bb,278,286,1,0,0,%,2133,6133,ff,21_33 #035_b6_MSN_HIS[49]User_SOC_end\n" //
                        +"7bb,287,295,1,0,0,%,2133,6133,ff,21_33 #036_b7_MSN_HIS[49]User_SOC_start\n" //
                        +"7bb,296,303,1,0,0,,2133,6133,ff,21_33 #038_b0_MSN_HIS[49]Ampere_hour_discharge\n" //
                        +"7bb,304,311,1,0,0,,2133,6133,ff,21_33 #039_b0_MSN_HIS[49]Ampere_hour_charge\n" //
                        +"7bb,312,319,1,40,0,,2133,6133,ff,21_33 #040_b0_MSN_HIS[49]Battery_temp_end\n" //
                        +"7bb,320,327,1,40,0,,2133,6133,ff,21_33 #041_b0_MSN_HIS[49]Battery_temp_start\n" //
                        +"7bb,328,333,1,0,0,,2133,6133,ff,defauts mission 49\n" //
                        +"7bb,329,329,1,0,0,,2133,6133,ff,21_33 #042_b1_MSN_HIS[49]Diag_NG_over_discharge_batt\n" //
                        +"7bb,330,330,1,0,0,,2133,6133,ff,21_33 #042_b2_MSN_HIS[49]Diag_NG_over_charge_cell\n" //
                        +"7bb,331,331,1,0,0,,2133,6133,ff,21_33 #042_b3_MSN_HIS[49]Diag_NG_over_discharge_cell\n" //
                        +"7bb,332,332,1,0,0,,2133,6133,ff,21_33 #042_b4_MSN_HIS[49]Diag_NG_over_current\n" //
                        +"7bb,333,333,1,0,0,,2133,6133,ff,21_33 #042_b5_MSN_HIS[49]Diag_NG_over_temp\n" //
                        +"7bb,336,338,1,0,0,,2133,6133,ff,21_33 #043_b0_MSN_HIS[50]Operation_type\n" //
                        +"7bb,342,350,1,0,0,%,2133,6133,ff,21_33 #043_b6_MSN_HIS[50]User_SOC_end\n" //
                        +"7bb,351,359,1,0,0,%,2133,6133,ff,21_33 #044_b7_MSN_HIS[50]User_SOC_start\n" //
                        +"7bb,360,367,1,0,0,,2133,6133,ff,21_33 #046_b0_MSN_HIS[50]Ampere_hour_discharge\n" //
                        +"7bb,368,375,1,0,0,,2133,6133,ff,21_33 #047_b0_MSN_HIS[50]Ampere_hour_charge\n" //
                        +"7bb,376,383,1,40,0,,2133,6133,ff,21_33 #048_b0_MSN_HIS[50]Battery_temp_end\n" //
                        +"7bb,384,391,1,40,0,,2133,6133,ff,21_33 #049_b0_MSN_HIS[50]Battery_temp_start\n" //
                        +"7bb,392,397,1,0,0,,2133,6133,ff,defauts mission 50\n" //
                        +"7bb,393,393,1,0,0,,2133,6133,ff,21_33 #050_b1_MSN_HIS[50]Diag_NG_over_discharge_batt\n" //
                        +"7bb,394,394,1,0,0,,2133,6133,ff,21_33 #050_b2_MSN_HIS[50]Diag_NG_over_charge_cell\n" //
                        +"7bb,395,395,1,0,0,,2133,6133,ff,21_33 #050_b3_MSN_HIS[50]Diag_NG_over_discharge_cell\n" //
                        +"7bb,396,396,1,0,0,,2133,6133,ff,21_33 #050_b4_MSN_HIS[50]Diag_NG_over_current\n" //
                        +"7bb,397,397,1,0,0,,2133,6133,ff,21_33 #050_b5_MSN_HIS[50]Diag_NG_over_temp\n" //
                        +"7bb,400,402,1,0,0,,2133,6133,ff,21_33 #051_b0_MSN_HIS[51]Operation_type\n" //
                        +"7bb,406,414,1,0,0,%,2133,6133,ff,21_33 #051_b6_MSN_HIS[51]User_SOC_end\n" //
                        +"7bb,415,423,1,0,0,%,2133,6133,ff,21_33 #052_b7_MSN_HIS[51]User_SOC_start\n" //
                        +"7bb,424,431,1,0,0,,2133,6133,ff,21_33 #054_b0_MSN_HIS[51]Ampere_hour_discharge\n" //
                        +"7bb,432,439,1,0,0,,2133,6133,ff,21_33 #055_b0_MSN_HIS[51]Ampere_hour_charge\n" //
                        +"7bb,440,447,1,40,0,,2133,6133,ff,21_33 #056_b0_MSN_HIS[51]Battery_temp_end\n" //
                        +"7bb,448,455,1,40,0,,2133,6133,ff,21_33 #057_b0_MSN_HIS[51]Battery_temp_start\n" //
                        +"7bb,456,461,1,0,0,,2133,6133,ff,defauts mission 51\n" //
                        +"7bb,457,457,1,0,0,,2133,6133,ff,21_33 #058_b1_MSN_HIS[51]Diag_NG_over_discharge_batt\n" //
                        +"7bb,458,458,1,0,0,,2133,6133,ff,21_33 #058_b2_MSN_HIS[51]Diag_NG_over_charge_cell\n" //
                        +"7bb,459,459,1,0,0,,2133,6133,ff,21_33 #058_b3_MSN_HIS[51]Diag_NG_over_discharge_cell\n" //
                        +"7bb,460,460,1,0,0,,2133,6133,ff,21_33 #058_b4_MSN_HIS[51]Diag_NG_over_current\n" //
                        +"7bb,461,461,1,0,0,,2133,6133,ff,21_33 #058_b5_MSN_HIS[51]Diag_NG_over_temp\n" //
                        +"7bb,464,466,1,0,0,,2133,6133,ff,21_33 #059_b0_MSN_HIS[52]Operation_type\n" //
                        +"7bb,470,478,1,0,0,%,2133,6133,ff,21_33 #059_b6_MSN_HIS[52]User_SOC_end\n" //
                        +"7bb,479,487,1,0,0,%,2133,6133,ff,21_33 #060_b7_MSN_HIS[52]User_SOC_start\n" //
                        +"7bb,488,495,1,0,0,,2133,6133,ff,21_33 #062_b0_MSN_HIS[52]Ampere_hour_discharge\n" //
                        +"7bb,496,503,1,0,0,,2133,6133,ff,21_33 #063_b0_MSN_HIS[52]Ampere_hour_charge\n" //
                        +"7bb,504,511,1,40,0,,2133,6133,ff,21_33 #064_b0_MSN_HIS[52]Battery_temp_end\n" //
                        +"7bb,512,519,1,40,0,,2133,6133,ff,21_33 #065_b0_MSN_HIS[52]Battery_temp_start\n" //
                        +"7bb,520,525,1,0,0,,2133,6133,ff,defauts mission 52\n" //
                        +"7bb,521,521,1,0,0,,2133,6133,ff,21_33 #066_b1_MSN_HIS[52]Diag_NG_over_discharge_batt\n" //
                        +"7bb,522,522,1,0,0,,2133,6133,ff,21_33 #066_b2_MSN_HIS[52]Diag_NG_over_charge_cell\n" //
                        +"7bb,523,523,1,0,0,,2133,6133,ff,21_33 #066_b3_MSN_HIS[52]Diag_NG_over_discharge_cell\n" //
                        +"7bb,524,524,1,0,0,,2133,6133,ff,21_33 #066_b4_MSN_HIS[52]Diag_NG_over_current\n" //
                        +"7bb,525,525,1,0,0,,2133,6133,ff,21_33 #066_b5_MSN_HIS[52]Diag_NG_over_temp\n" //
                        +"7bb,528,530,1,0,0,,2133,6133,ff,21_33 #067_b0_MSN_HIS[53]Operation_type\n" //
                        +"7bb,534,542,1,0,0,%,2133,6133,ff,21_33 #067_b6_MSN_HIS[53]User_SOC_end\n" //
                        +"7bb,543,551,1,0,0,%,2133,6133,ff,21_33 #068_b7_MSN_HIS[53]User_SOC_start\n" //
                        +"7bb,552,559,1,0,0,,2133,6133,ff,21_33 #070_b0_MSN_HIS[53]Ampere_hour_discharge\n" //
                        +"7bb,560,567,1,0,0,,2133,6133,ff,21_33 #071_b0_MSN_HIS[53]Ampere_hour_charge\n" //
                        +"7bb,568,575,1,40,0,,2133,6133,ff,21_33 #072_b0_MSN_HIS[53]Battery_temp_end\n" //
                        +"7bb,576,583,1,40,0,,2133,6133,ff,21_33 #073_b0_MSN_HIS[53]Battery_temp_start\n" //
                        +"7bb,584,589,1,0,0,,2133,6133,ff,defauts mission 53\n" //
                        +"7bb,585,585,1,0,0,,2133,6133,ff,21_33 #074_b1_MSN_HIS[53]Diag_NG_over_discharge_batt\n" //
                        +"7bb,586,586,1,0,0,,2133,6133,ff,21_33 #074_b2_MSN_HIS[53]Diag_NG_over_charge_cell\n" //
                        +"7bb,587,587,1,0,0,,2133,6133,ff,21_33 #074_b3_MSN_HIS[53]Diag_NG_over_discharge_cell\n" //
                        +"7bb,588,588,1,0,0,,2133,6133,ff,21_33 #074_b4_MSN_HIS[53]Diag_NG_over_current\n" //
                        +"7bb,589,589,1,0,0,,2133,6133,ff,21_33 #074_b5_MSN_HIS[53]Diag_NG_over_temp\n" //
                        +"7bb,592,594,1,0,0,,2133,6133,ff,21_33 #075_b0_MSN_HIS[54]Operation_type\n" //
                        +"7bb,598,606,1,0,0,%,2133,6133,ff,21_33 #075_b6_MSN_HIS[54]User_SOC_end\n" //
                        +"7bb,607,615,1,0,0,%,2133,6133,ff,21_33 #076_b7_MSN_HIS[54]User_SOC_start\n" //
                        +"7bb,616,623,1,0,0,,2133,6133,ff,21_33 #078_b0_MSN_HIS[54]Ampere_hour_discharge\n" //
                        +"7bb,624,631,1,0,0,,2133,6133,ff,21_33 #079_b0_MSN_HIS[54]Ampere_hour_charge\n" //
                        +"7bb,632,639,1,40,0,,2133,6133,ff,21_33 #080_b0_MSN_HIS[54]Battery_temp_end\n" //
                        +"7bb,640,647,1,40,0,,2133,6133,ff,21_33 #081_b0_MSN_HIS[54]Battery_temp_start\n" //
                        +"7bb,648,653,1,0,0,,2133,6133,ff,defauts mission 54\n" //
                        +"7bb,649,649,1,0,0,,2133,6133,ff,21_33 #082_b1_MSN_HIS[54]Diag_NG_over_discharge_batt\n" //
                        +"7bb,650,650,1,0,0,,2133,6133,ff,21_33 #082_b2_MSN_HIS[54]Diag_NG_over_charge_cell\n" //
                        +"7bb,651,651,1,0,0,,2133,6133,ff,21_33 #082_b3_MSN_HIS[54]Diag_NG_over_discharge_cell\n" //
                        +"7bb,652,652,1,0,0,,2133,6133,ff,21_33 #082_b4_MSN_HIS[54]Diag_NG_over_current\n" //
                        +"7bb,653,653,1,0,0,,2133,6133,ff,21_33 #082_b5_MSN_HIS[54]Diag_NG_over_temp\n" //
                        +"7bb,656,658,1,0,0,,2133,6133,ff,21_33 #083_b0_MSN_HIS[55]Operation_type\n" //
                        +"7bb,662,670,1,0,0,%,2133,6133,ff,21_33 #083_b6_MSN_HIS[55]User_SOC_end\n" //
                        +"7bb,671,679,1,0,0,%,2133,6133,ff,21_33 #084_b7_MSN_HIS[55]User_SOC_start\n" //
                        +"7bb,680,687,1,0,0,,2133,6133,ff,21_33 #086_b0_MSN_HIS[55]Ampere_hour_discharge\n" //
                        +"7bb,688,695,1,0,0,,2133,6133,ff,21_33 #087_b0_MSN_HIS[55]Ampere_hour_charge\n" //
                        +"7bb,696,703,1,40,0,,2133,6133,ff,21_33 #088_b0_MSN_HIS[55]Battery_temp_end\n" //
                        +"7bb,704,711,1,40,0,,2133,6133,ff,21_33 #089_b0_MSN_HIS[55]Battery_temp_start\n" //
                        +"7bb,712,717,1,0,0,,2133,6133,ff,defauts mission 55\n" //
                        +"7bb,713,713,1,0,0,,2133,6133,ff,21_33 #090_b1_MSN_HIS[55]Diag_NG_over_discharge_batt\n" //
                        +"7bb,714,714,1,0,0,,2133,6133,ff,21_33 #090_b2_MSN_HIS[55]Diag_NG_over_charge_cell\n" //
                        +"7bb,715,715,1,0,0,,2133,6133,ff,21_33 #090_b3_MSN_HIS[55]Diag_NG_over_discharge_cell\n" //
                        +"7bb,716,716,1,0,0,,2133,6133,ff,21_33 #090_b4_MSN_HIS[55]Diag_NG_over_current\n" //
                        +"7bb,717,717,1,0,0,,2133,6133,ff,21_33 #090_b5_MSN_HIS[55]Diag_NG_over_temp\n" //
                        +"7bb,720,722,1,0,0,,2133,6133,ff,21_33 #091_b0_MSN_HIS[56]Operation_type\n" //
                        +"7bb,726,734,1,0,0,%,2133,6133,ff,21_33 #091_b6_MSN_HIS[56]User_SOC_end\n" //
                        +"7bb,735,743,1,0,0,%,2133,6133,ff,21_33 #092_b7_MSN_HIS[56]User_SOC_start\n" //
                        +"7bb,744,751,1,0,0,,2133,6133,ff,21_33 #094_b0_MSN_HIS[56]Ampere_hour_discharge\n" //
                        +"7bb,752,759,1,0,0,,2133,6133,ff,21_33 #095_b0_MSN_HIS[56]Ampere_hour_charge\n" //
                        +"7bb,760,767,1,40,0,,2133,6133,ff,21_33 #096_b0_MSN_HIS[56]Battery_temp_end\n" //
                        +"7bb,768,775,1,40,0,,2133,6133,ff,21_33 #097_b0_MSN_HIS[56]Battery_temp_start\n" //
                        +"7bb,776,781,1,0,0,,2133,6133,ff,defauts mission 56\n" //
                        +"7bb,777,777,1,0,0,,2133,6133,ff,21_33 #098_b1_MSN_HIS[56]Diag_NG_over_discharge_batt\n" //
                        +"7bb,778,778,1,0,0,,2133,6133,ff,21_33 #098_b2_MSN_HIS[56]Diag_NG_over_charge_cell\n" //
                        +"7bb,779,779,1,0,0,,2133,6133,ff,21_33 #098_b3_MSN_HIS[56]Diag_NG_over_discharge_cell\n" //
                        +"7bb,780,780,1,0,0,,2133,6133,ff,21_33 #098_b4_MSN_HIS[56]Diag_NG_over_current\n" //
                        +"7bb,781,781,1,0,0,,2133,6133,ff,21_33 #098_b5_MSN_HIS[56]Diag_NG_over_temp\n" //
                        +"7bb,784,786,1,0,0,,2133,6133,ff,21_33 #099_b0_MSN_HIS[57]Operation_type\n" //
                        +"7bb,790,798,1,0,0,%,2133,6133,ff,21_33 #099_b6_MSN_HIS[57]User_SOC_end\n" //
                        +"7bb,799,807,1,0,0,%,2133,6133,ff,21_33 #100_b7_MSN_HIS[57]User_SOC_start\n" //
                        +"7bb,808,815,1,0,0,,2133,6133,ff,21_33 #102_b0_MSN_HIS[57]Ampere_hour_discharge\n" //
                        +"7bb,816,823,1,0,0,,2133,6133,ff,21_33 #103_b0_MSN_HIS[57]Ampere_hour_charge\n" //
                        +"7bb,824,831,1,40,0,,2133,6133,ff,21_33 #104_b0_MSN_HIS[57]Battery_temp_end\n" //
                        +"7bb,832,839,1,40,0,,2133,6133,ff,21_33 #105_b0_MSN_HIS[57]Battery_temp_start\n" //
                        +"7bb,840,845,1,0,0,,2133,6133,ff,defauts mission 57\n" //
                        +"7bb,841,841,1,0,0,,2133,6133,ff,21_33 #106_b1_MSN_HIS[57]Diag_NG_over_discharge_batt\n" //
                        +"7bb,842,842,1,0,0,,2133,6133,ff,21_33 #106_b2_MSN_HIS[57]Diag_NG_over_charge_cell\n" //
                        +"7bb,843,843,1,0,0,,2133,6133,ff,21_33 #106_b3_MSN_HIS[57]Diag_NG_over_discharge_cell\n" //
                        +"7bb,844,844,1,0,0,,2133,6133,ff,21_33 #106_b4_MSN_HIS[57]Diag_NG_over_current\n" //
                        +"7bb,845,845,1,0,0,,2133,6133,ff,21_33 #106_b5_MSN_HIS[57]Diag_NG_over_temp\n" //
                        +"7bb,848,850,1,0,0,,2133,6133,ff,21_33 #107_b0_MSN_HIS[58]Operation_type\n" //
                        +"7bb,854,862,1,0,0,%,2133,6133,ff,21_33 #107_b6_MSN_HIS[58]User_SOC_end\n" //
                        +"7bb,863,871,1,0,0,%,2133,6133,ff,21_33 #108_b7_MSN_HIS[58]User_SOC_start\n" //
                        +"7bb,872,879,1,0,0,,2133,6133,ff,21_33 #110_b0_MSN_HIS[58]Ampere_hour_discharge\n" //
                        +"7bb,880,887,1,0,0,,2133,6133,ff,21_33 #111_b0_MSN_HIS[58]Ampere_hour_charge\n" //
                        +"7bb,888,895,1,40,0,,2133,6133,ff,21_33 #112_b0_MSN_HIS[58]Battery_temp_end\n" //
                        +"7bb,896,903,1,40,0,,2133,6133,ff,21_33 #113_b0_MSN_HIS[58]Battery_temp_start\n" //
                        +"7bb,904,909,1,0,0,,2133,6133,ff,defauts mission 58\n" //
                        +"7bb,905,905,1,0,0,,2133,6133,ff,21_33 #114_b1_MSN_HIS[58]Diag_NG_over_discharge_batt\n" //
                        +"7bb,906,906,1,0,0,,2133,6133,ff,21_33 #114_b2_MSN_HIS[58]Diag_NG_over_charge_cell\n" //
                        +"7bb,907,907,1,0,0,,2133,6133,ff,21_33 #114_b3_MSN_HIS[58]Diag_NG_over_discharge_cell\n" //
                        +"7bb,908,908,1,0,0,,2133,6133,ff,21_33 #114_b4_MSN_HIS[58]Diag_NG_over_current\n" //
                        +"7bb,909,909,1,0,0,,2133,6133,ff,21_33 #114_b5_MSN_HIS[58]Diag_NG_over_temp\n" //
                        +"7bb,912,914,1,0,0,,2133,6133,ff,21_33 #115_b0_MSN_HIS[59]Operation_type\n" //
                        +"7bb,918,926,1,0,0,%,2133,6133,ff,21_33 #115_b6_MSN_HIS[59]User_SOC_end\n" //
                        +"7bb,927,935,1,0,0,%,2133,6133,ff,21_33 #116_b7_MSN_HIS[59]User_SOC_start\n" //
                        +"7bb,936,943,1,0,0,,2133,6133,ff,21_33 #118_b0_MSN_HIS[59]Ampere_hour_discharge\n" //
                        +"7bb,944,951,1,0,0,,2133,6133,ff,21_33 #119_b0_MSN_HIS[59]Ampere_hour_charge\n" //
                        +"7bb,952,959,1,40,0,,2133,6133,ff,21_33 #120_b0_MSN_HIS[59]Battery_temp_end\n" //
                        +"7bb,960,967,1,40,0,,2133,6133,ff,21_33 #121_b0_MSN_HIS[59]Battery_temp_start\n" //
                        +"7bb,968,973,1,0,0,,2133,6133,ff,defauts mission 59\n" //
                        +"7bb,969,969,1,0,0,,2133,6133,ff,21_33 #122_b1_MSN_HIS[59]Diag_NG_over_discharge_batt\n" //
                        +"7bb,970,970,1,0,0,,2133,6133,ff,21_33 #122_b2_MSN_HIS[59]Diag_NG_over_charge_cell\n" //
                        +"7bb,971,971,1,0,0,,2133,6133,ff,21_33 #122_b3_MSN_HIS[59]Diag_NG_over_discharge_cell\n" //
                        +"7bb,972,972,1,0,0,,2133,6133,ff,21_33 #122_b4_MSN_HIS[59]Diag_NG_over_current\n" //
                        +"7bb,973,973,1,0,0,,2133,6133,ff,21_33 #122_b5_MSN_HIS[59]Diag_NG_over_temp\n" //
                        +"7bb,16,18,1,0,0,,2134,6134,ff,21_34 #003_b0_MSN_HIS[60]Operation_type\n" //
                        +"7bb,22,30,1,0,0,%,2134,6134,ff,21_34 #003_b6_MSN_HIS[60]User_SOC_end\n" //
                        +"7bb,31,39,1,0,0,%,2134,6134,ff,21_34 #004_b7_MSN_HIS[60]User_SOC_start\n" //
                        +"7bb,40,47,1,0,0,,2134,6134,ff,21_34 #006_b0_MSN_HIS[60]Ampere_hour_discharge\n" //
                        +"7bb,48,55,1,0,0,,2134,6134,ff,21_34 #007_b0_MSN_HIS[60]Ampere_hour_charge\n" //
                        +"7bb,56,63,1,40,0,,2134,6134,ff,21_34 #008_b0_MSN_HIS[60]Battery_temp_end\n" //
                        +"7bb,64,71,1,40,0,,2134,6134,ff,21_34 #009_b0_MSN_HIS[60]Battery_temp_start\n" //
                        +"7bb,72,77,1,0,0,,2134,6134,ff,defauts mission 60\n" //
                        +"7bb,73,73,1,0,0,,2134,6134,ff,21_34 #010_b1_MSN_HIS[60]Diag_NG_over_discharge_batt\n" //
                        +"7bb,74,74,1,0,0,,2134,6134,ff,21_34 #010_b2_MSN_HIS[60]Diag_NG_over_charge_cell\n" //
                        +"7bb,75,75,1,0,0,,2134,6134,ff,21_34 #010_b3_MSN_HIS[60]Diag_NG_over_discharge_cell\n" //
                        +"7bb,76,76,1,0,0,,2134,6134,ff,21_34 #010_b4_MSN_HIS[60]Diag_NG_over_current\n" //
                        +"7bb,77,77,1,0,0,,2134,6134,ff,21_34 #010_b5_MSN_HIS[60]Diag_NG_over_temp\n" //
                        +"7bb,80,82,1,0,0,,2134,6134,ff,21_34 #011_b0_MSN_HIS[61]Operation_type\n" //
                        +"7bb,86,94,1,0,0,%,2134,6134,ff,21_34 #011_b6_MSN_HIS[61]User_SOC_end\n" //
                        +"7bb,95,103,1,0,0,%,2134,6134,ff,21_34 #012_b7_MSN_HIS[61]User_SOC_start\n" //
                        +"7bb,104,111,1,0,0,,2134,6134,ff,21_34 #014_b0_MSN_HIS[61]Ampere_hour_discharge\n" //
                        +"7bb,112,119,1,0,0,,2134,6134,ff,21_34 #015_b0_MSN_HIS[61]Ampere_hour_charge\n" //
                        +"7bb,120,127,1,40,0,,2134,6134,ff,21_34 #016_b0_MSN_HIS[61]Battery_temp_end\n" //
                        +"7bb,128,135,1,40,0,,2134,6134,ff,21_34 #017_b0_MSN_HIS[61]Battery_temp_start\n" //
                        +"7bb,136,141,1,0,0,,2134,6134,ff,defauts mission 61\n" //
                        +"7bb,137,137,1,0,0,,2134,6134,ff,21_34 #018_b1_MSN_HIS[61]Diag_NG_over_discharge_batt\n" //
                        +"7bb,138,138,1,0,0,,2134,6134,ff,21_34 #018_b2_MSN_HIS[61]Diag_NG_over_charge_cell\n" //
                        +"7bb,139,139,1,0,0,,2134,6134,ff,21_34 #018_b3_MSN_HIS[61]Diag_NG_over_discharge_cell\n" //
                        +"7bb,140,140,1,0,0,,2134,6134,ff,21_34 #018_b4_MSN_HIS[61]Diag_NG_over_current\n" //
                        +"7bb,141,141,1,0,0,,2134,6134,ff,21_34 #018_b5_MSN_HIS[61]Diag_NG_over_temp\n" //
                        +"7bb,144,146,1,0,0,,2134,6134,ff,21_34 #019_b0_MSN_HIS[62]Operation_type\n" //
                        +"7bb,150,158,1,0,0,%,2134,6134,ff,21_34 #019_b6_MSN_HIS[62]User_SOC_end\n" //
                        +"7bb,159,167,1,0,0,%,2134,6134,ff,21_34 #020_b7_MSN_HIS[62]User_SOC_start\n" //
                        +"7bb,168,175,1,0,0,,2134,6134,ff,21_34 #022_b0_MSN_HIS[62]Ampere_hour_discharge\n" //
                        +"7bb,176,183,1,0,0,,2134,6134,ff,21_34 #023_b0_MSN_HIS[62]Ampere_hour_charge\n" //
                        +"7bb,184,191,1,40,0,,2134,6134,ff,21_34 #024_b0_MSN_HIS[62]Battery_temp_end\n" //

                ;

        String fieldDef3 =
                ""

                        +"7bb,192,199,1,40,0,,2134,6134,ff,21_34 #025_b0_MSN_HIS[62]Battery_temp_start\n" //
                        +"7bb,200,205,1,0,0,,2134,6134,ff,defauts mission 62\n" //
                        +"7bb,201,201,1,0,0,,2134,6134,ff,21_34 #026_b1_MSN_HIS[62]Diag_NG_over_discharge_batt\n" //
                        +"7bb,202,202,1,0,0,,2134,6134,ff,21_34 #026_b2_MSN_HIS[62]Diag_NG_over_charge_cell\n" //
                        +"7bb,203,203,1,0,0,,2134,6134,ff,21_34 #026_b3_MSN_HIS[62]Diag_NG_over_discharge_cell\n" //
                        +"7bb,204,204,1,0,0,,2134,6134,ff,21_34 #026_b4_MSN_HIS[62]Diag_NG_over_current\n" //
                        +"7bb,205,205,1,0,0,,2134,6134,ff,21_34 #026_b5_MSN_HIS[62]Diag_NG_over_temp\n" //
                        +"7bb,208,210,1,0,0,,2134,6134,ff,21_34 #027_b0_MSN_HIS[63]Operation_type\n" //
                        +"7bb,214,222,1,0,0,%,2134,6134,ff,21_34 #027_b6_MSN_HIS[63]User_SOC_end\n" //
                        +"7bb,223,231,1,0,0,%,2134,6134,ff,21_34 #028_b7_MSN_HIS[63]User_SOC_start\n" //
                        +"7bb,232,239,1,0,0,,2134,6134,ff,21_34 #030_b0_MSN_HIS[63]Ampere_hour_discharge\n" //
                        +"7bb,240,247,1,0,0,,2134,6134,ff,21_34 #031_b0_MSN_HIS[63]Ampere_hour_charge\n" //
                        +"7bb,248,255,1,40,0,,2134,6134,ff,21_34 #032_b0_MSN_HIS[63]Battery_temp_end\n" //
                        +"7bb,256,263,1,40,0,,2134,6134,ff,21_34 #033_b0_MSN_HIS[63]Battery_temp_start\n" //
                        +"7bb,264,269,1,0,0,,2134,6134,ff,defauts mission 63\n" //
                        +"7bb,265,265,1,0,0,,2134,6134,ff,21_34 #034_b1_MSN_HIS[63]Diag_NG_over_discharge_batt\n" //
                        +"7bb,266,266,1,0,0,,2134,6134,ff,21_34 #034_b2_MSN_HIS[63]Diag_NG_over_charge_cell\n" //
                        +"7bb,267,267,1,0,0,,2134,6134,ff,21_34 #034_b3_MSN_HIS[63]Diag_NG_over_discharge_cell\n" //
                        +"7bb,268,268,1,0,0,,2134,6134,ff,21_34 #034_b4_MSN_HIS[63]Diag_NG_over_current\n" //
                        +"7bb,269,269,1,0,0,,2134,6134,ff,21_34 #034_b5_MSN_HIS[63]Diag_NG_over_temp\n" //
                        +"7bb,16,31,.001,0,0,V,2141,6141,ff,21_41_#003_Cell Voltage[00]\n" //
                        +"7bb,32,47,.001,0,0,V,2141,6141,ff,21_41_#005_Cell Voltage[01]\n" //
                        +"7bb,48,63,.001,0,0,V,2141,6141,ff,21_41_#007_Cell Voltage[02]\n" //
                        +"7bb,64,79,.001,0,0,V,2141,6141,ff,21_41_#009_Cell Voltage[03]\n" //
                        +"7bb,80,95,.001,0,0,V,2141,6141,ff,21_41_#011_Cell Voltage[04]\n" //
                        +"7bb,96,111,.001,0,0,V,2141,6141,ff,21_41_#013_Cell Voltage[05]\n" //
                        +"7bb,112,127,.001,0,0,V,2141,6141,ff,21_41_#015_Cell Voltage[06]\n" //
                        +"7bb,128,143,.001,0,0,V,2141,6141,ff,21_41_#017_Cell Voltage[07]\n" //
                        +"7bb,144,159,.001,0,0,V,2141,6141,ff,21_41_#019_Cell Voltage[08]\n" //
                        +"7bb,160,175,.001,0,0,V,2141,6141,ff,21_41_#021_Cell Voltage[09]\n" //
                        +"7bb,176,191,.001,0,0,V,2141,6141,ff,21_41_#023_Cell Voltage[10]\n" //
                        +"7bb,192,207,.001,0,0,V,2141,6141,ff,21_41_#025_Cell Voltage[11]\n" //
                        +"7bb,208,223,.001,0,0,V,2141,6141,ff,21_41_#027_Cell Voltage[12]\n" //
                        +"7bb,224,239,.001,0,0,V,2141,6141,ff,21_41_#029_Cell Voltage[13]\n" //
                        +"7bb,240,255,.001,0,0,V,2141,6141,ff,21_41_#031_Cell Voltage[14]\n" //
                        +"7bb,256,271,.001,0,0,V,2141,6141,ff,21_41_#033_Cell Voltage[15]\n" //
                        +"7bb,272,287,.001,0,0,V,2141,6141,ff,21_41_#035_Cell Voltage[16]\n" //
                        +"7bb,288,303,.001,0,0,V,2141,6141,ff,21_41_#037_Cell Voltage[17]\n" //
                        +"7bb,304,319,.001,0,0,V,2141,6141,ff,21_41_#039_Cell Voltage[18]\n" //
                        +"7bb,320,335,.001,0,0,V,2141,6141,ff,21_41_#041_Cell Voltage[19]\n" //
                        +"7bb,336,351,.001,0,0,V,2141,6141,ff,21_41_#043_Cell Voltage[20]\n" //
                        +"7bb,352,367,.001,0,0,V,2141,6141,ff,21_41_#045_Cell Voltage[21]\n" //
                        +"7bb,368,383,.001,0,0,V,2141,6141,ff,21_41_#047_Cell Voltage[22]\n" //
                        +"7bb,384,399,.001,0,0,V,2141,6141,ff,21_41_#049_Cell Voltage[23]\n" //
                        +"7bb,400,415,.001,0,0,V,2141,6141,ff,21_41_#051_Cell Voltage[24]\n" //
                        +"7bb,416,431,.001,0,0,V,2141,6141,ff,21_41_#053_Cell Voltage[25]\n" //
                        +"7bb,432,447,.001,0,0,V,2141,6141,ff,21_41_#055_Cell Voltage[26]\n" //
                        +"7bb,448,463,.001,0,0,V,2141,6141,ff,21_41_#057_Cell Voltage[27]\n" //
                        +"7bb,464,479,.001,0,0,V,2141,6141,ff,21_41_#059_Cell Voltage[28]\n" //
                        +"7bb,480,495,.001,0,0,V,2141,6141,ff,21_41_#061_Cell Voltage[29]\n" //
                        +"7bb,496,511,.001,0,0,V,2141,6141,ff,21_41_#063_Cell Voltage[30]\n" //
                        +"7bb,512,527,.001,0,0,V,2141,6141,ff,21_41_#065_Cell Voltage[31]\n" //
                        +"7bb,528,543,.001,0,0,V,2141,6141,ff,21_41_#067_Cell Voltage[32]\n" //
                        +"7bb,544,559,.001,0,0,V,2141,6141,ff,21_41_#069_Cell Voltage[33]\n" //
                        +"7bb,560,575,.001,0,0,V,2141,6141,ff,21_41_#071_Cell Voltage[34]\n" //
                        +"7bb,576,591,.001,0,0,V,2141,6141,ff,21_41_#073_Cell Voltage[35]\n" //
                        +"7bb,592,607,.001,0,0,V,2141,6141,ff,21_41_#075_Cell Voltage[36]\n" //
                        +"7bb,608,623,.001,0,0,V,2141,6141,ff,21_41_#077_Cell Voltage[37]\n" //
                        +"7bb,624,639,.001,0,0,V,2141,6141,ff,21_41_#079_Cell Voltage[38]\n" //
                        +"7bb,640,655,.001,0,0,V,2141,6141,ff,21_41_#081_Cell Voltage[39]\n" //
                        +"7bb,656,671,.001,0,0,V,2141,6141,ff,21_41_#083_Cell Voltage[40]\n" //
                        +"7bb,672,687,.001,0,0,V,2141,6141,ff,21_41_#085_Cell Voltage[41]\n" //
                        +"7bb,688,703,.001,0,0,V,2141,6141,ff,21_41_#087_Cell Voltage[42]\n" //
                        +"7bb,704,719,.001,0,0,V,2141,6141,ff,21_41_#089_Cell Voltage[43]\n" //
                        +"7bb,720,735,.001,0,0,V,2141,6141,ff,21_41_#091_Cell Voltage[44]\n" //
                        +"7bb,736,751,.001,0,0,V,2141,6141,ff,21_41_#093_Cell Voltage[45]\n" //
                        +"7bb,752,767,.001,0,0,V,2141,6141,ff,21_41_#095_Cell Voltage[46]\n" //
                        +"7bb,768,783,.001,0,0,V,2141,6141,ff,21_41_#097_Cell Voltage[47]\n" //
                        +"7bb,784,799,.001,0,0,V,2141,6141,ff,21_41_#099_Cell Voltage[48]\n" //
                        +"7bb,800,815,.001,0,0,V,2141,6141,ff,21_41_#101_Cell Voltage[49]\n" //
                        +"7bb,816,831,.001,0,0,V,2141,6141,ff,21_41_#103_Cell Voltage[50]\n" //
                        +"7bb,832,847,.001,0,0,V,2141,6141,ff,21_41_#105_Cell Voltage[51]\n" //
                        +"7bb,848,863,.001,0,0,V,2141,6141,ff,21_41_#107_Cell Voltage[52]\n" //
                        +"7bb,864,879,.001,0,0,V,2141,6141,ff,21_41_#109_Cell Voltage[53]\n" //
                        +"7bb,880,895,.001,0,0,V,2141,6141,ff,21_41_#111_Cell Voltage[54]\n" //
                        +"7bb,896,911,.001,0,0,V,2141,6141,ff,21_41_#113_Cell Voltage[55]\n" //
                        +"7bb,912,927,.001,0,0,V,2141,6141,ff,21_41_#115_Cell Voltage[56]\n" //
                        +"7bb,928,943,.001,0,0,V,2141,6141,ff,21_41_#117_Cell Voltage[57]\n" //
                        +"7bb,944,959,.001,0,0,V,2141,6141,ff,21_41_#119_Cell Voltage[58]\n" //
                        +"7bb,960,975,.001,0,0,V,2141,6141,ff,21_41_#121_Cell Voltage[59]\n" //
                        +"7bb,976,991,.001,0,0,V,2141,6141,ff,21_41_#123_Cell Voltage[60]\n" //
                        +"7bb,992,1007,.001,0,0,V,2141,6141,ff,21_41_#125_Cell Voltage[61]\n" //
                        +"7bb,16,31,.001,0,0,V,2142,6142,ff,21_42_#003_Cell Voltage[62]\n" //
                        +"7bb,32,47,.001,0,0,V,2142,6142,ff,21_42_#005_Cell Voltage[63]\n" //
                        +"7bb,48,63,.001,0,0,V,2142,6142,ff,21_42_#007_Cell Voltage[64]\n" //
                        +"7bb,64,79,.001,0,0,V,2142,6142,ff,21_42_#009_Cell Voltage[65]\n" //
                        +"7bb,80,95,.001,0,0,V,2142,6142,ff,21_42_#011_Cell Voltage[66]\n" //
                        +"7bb,96,111,.001,0,0,V,2142,6142,ff,21_42_#013_Cell Voltage[67]\n" //
                        +"7bb,112,127,.001,0,0,V,2142,6142,ff,21_42_#015_Cell Voltage[68]\n" //
                        +"7bb,128,143,.001,0,0,V,2142,6142,ff,21_42_#017_Cell Voltage[69]\n" //
                        +"7bb,144,159,.001,0,0,V,2142,6142,ff,21_42_#019_Cell Voltage[70]\n" //
                        +"7bb,160,175,.001,0,0,V,2142,6142,ff,21_42_#021_Cell Voltage[71]\n" //
                        +"7bb,176,191,.001,0,0,V,2142,6142,ff,21_42_#023_Cell Voltage[72]\n" //
                        +"7bb,192,207,.001,0,0,V,2142,6142,ff,21_42_#025_Cell Voltage[73]\n" //
                        +"7bb,208,223,.001,0,0,V,2142,6142,ff,21_42_#027_Cell Voltage[74]\n" //
                        +"7bb,224,239,.001,0,0,V,2142,6142,ff,21_42_#029_Cell Voltage[75]\n" //
                        +"7bb,240,255,.001,0,0,V,2142,6142,ff,21_42_#031_Cell Voltage[76]\n" //
                        +"7bb,256,271,.001,0,0,V,2142,6142,ff,21_42_#033_Cell Voltage[77]\n" //
                        +"7bb,272,287,.001,0,0,V,2142,6142,ff,21_42_#035_Cell Voltage[78]\n" //
                        +"7bb,288,303,.001,0,0,V,2142,6142,ff,21_42_#037_Cell Voltage[79]\n" //
                        +"7bb,304,319,.001,0,0,V,2142,6142,ff,21_42_#039_Cell Voltage[80]\n" //
                        +"7bb,320,335,.001,0,0,V,2142,6142,ff,21_42_#041_Cell Voltage[81]\n" //
                        +"7bb,336,351,.001,0,0,V,2142,6142,ff,21_42_#043_Cell Voltage[82]\n" //
                        +"7bb,352,367,.001,0,0,V,2142,6142,ff,21_42_#045_Cell Voltage[83]\n" //
                        +"7bb,368,383,.001,0,0,V,2142,6142,ff,21_42_#047_Cell Voltage[84]\n" //
                        +"7bb,384,399,.001,0,0,V,2142,6142,ff,21_42_#049_Cell Voltage[85]\n" //
                        +"7bb,400,415,.001,0,0,V,2142,6142,ff,21_42_#051_Cell Voltage[86]\n" //
                        +"7bb,416,431,.001,0,0,V,2142,6142,ff,21_42_#053_Cell Voltage[87]\n" //
                        +"7bb,432,447,.001,0,0,V,2142,6142,ff,21_42_#055_Cell Voltage[88]\n" //
                        +"7bb,448,463,.001,0,0,V,2142,6142,ff,21_42_#057_Cell Voltage[89]\n" //
                        +"7bb,464,479,.001,0,0,V,2142,6142,ff,21_42_#059_Cell Voltage[90]\n" //
                        +"7bb,480,495,.001,0,0,V,2142,6142,ff,21_42_#061_Cell Voltage[91]\n" //
                        +"7bb,496,511,.001,0,0,V,2142,6142,ff,21_42_#063_Cell Voltage[92]\n" //
                        +"7bb,512,527,.001,0,0,V,2142,6142,ff,21_42_#065_Cell Voltage[93]\n" //
                        +"7bb,528,543,.001,0,0,V,2142,6142,ff,21_42_#067_Cell Voltage[94]\n" //
                        +"7bb,544,559,.001,0,0,V,2142,6142,ff,21_42_#069_Cell Voltage[95]\n" //
                        +"7bb,560,575,1,0,0,V,2142,6142,ff,21_42_#071_Sum_of_cell_voltage\n" //
                        +"7bb,576,591,1,0,0,V,2142,6142,ff,21_42_#073_Pack_Voltage\n" //
                        +"7bb,192,199,1,0,0,,2180,6180,ff,ApprovalNumber.BasicPartList\n" //
                        +"7bb,160,175,1,0,0,,2180,6180,ff,CalibrationNumber\n" //
                        +"7bb,56,63,1,0,0,,2180,6180,ff,DiagnosticIdentificationCode\n" //
                        +"7bb,144,159,1,0,0,,2180,6180,ff,EditionNumber\n" //
                        +"7bb,184,191,1,0,0,,2180,6180,ff,HardwareNumber.BasicPartList,1:293A0\n" //
                        +"7bb,88,127,1,0,0,,2180,6180,2ff,HardwareNumber.LowerPart\n" //
                        +"7bb,200,207,1,0,0,,2180,6180,ff,ManufacturerIdentificationCode,136:5DIGITS R2;255:Renault R3;0:Renault R1;128:Nissan N1;129:Nissan N2;130:Nissan N3\n" //
                        +"7bb,176,183,1,0,0,,2180,6180,ff,PartNumber.BasicPartList,2:293A0\n" //
                        +"7bb,16,55,1,0,0,,2180,6180,2ff,PartNumber.LowerPart\n" //
                        +"7bb,128,143,1,0,0,,2180,6180,ff,SoftwareNumber\n" //
                        +"7bb,64,87,1,0,0,,2180,6180,2ff,SupplierNumber.ITG\n" //
                        +"7bb,24,31,1,0,0,%,221404,621404,ff,1404_Battery SOC\n" //
                        +"7bb,24,31,1,0,0,%,221405,621405,ff,1405_Battery USOC\n" //
                        +"7bb,24,31,1,0,0,%,221406,621406,ff,1406_Battery SOH\n" //
                        +"7bb,24,39,1,5000,0,A,221409,621409,ff,1409_Instant Current of Battery\n" //
                        +"7bb,24,39,1,0,0,,22140A,62140A,ff,140A_Current sensor A/D value\n" //
                        +"7bb,24,39,1,5000,0,A,22140B,62140B,ff,140B_Current Sensor Offset value\n" //
                        +"7bb,24,39,1,0,0,,22140C,62140C,ff,140C_AD value of Sensor Power Supply Voltage\n" //
                        +"7bb,24,31,1,40,0,degC,22140E,62140E,ff,140E_Maximum Temperature\n" //
                        +"7bb,24,31,1,40,0,degC,22140F,62140F,ff,140F_Minimum Temperature\n" //
                        +"7bb,24,39,1000,0,0,Ohm,221411,621411,ff,1411_Isolation Impedance\n" //
                        +"7bb,24,39,1,0,0,,221412,621412,ff,1412_Isolation Positive A/D value\n" //
                        +"7bb,24,39,1,0,0,,221413,621413,ff,1413_Isolation Negative A/D value\n" //
                        +"7bb,24,55,1,0,0,V,221416,621416,ff,1416_Sum of Cell voltage\n" //
                        +"7bb,24,39,.001,0,0,V,221417,621417,ff,1417_Maximum Cell Voltage\n" //
                        +"7bb,24,31,1,0,0,,221418,621418,ff,1418_Maximum Cell Voltage No.\n" //
                        +"7bb,24,39,.001,0,0,V,221419,621419,ff,1419_Minimum Cell Voltage\n" //
                        +"7bb,24,31,1,0,0,,22141A,62141A,ff,141A Minimum cell Voltage number\n" //
                        +"7bb,24,39,1,2000,0,rpm,221426,621426,ff,1426 ElecMachineSpeed From Vehicle (PEB_CANHS_R02/03)\n" //
                        +"7bb,16,31,.001,0,0,V,2143,6143,ff,21_43_#03 BusBar Voltage01\n" //
                        +"7bb,32,47,.001,0,0,V,2143,6143,ff,21_43_#05 BusBar Voltage02\n" //
                        +"7bb,48,63,.001,0,0,V,2143,6143,ff,21_43_#07 BusBar Voltage03\n" //
                        +"7bb,64,79,.001,0,0,V,2143,6143,ff,21_43_#09 BusBar Voltage04\n" //
                        +"7bb,80,95,.001,0,0,V,2143,6143,ff,21_43_#11 BusBar Voltage05\n" //
                        +"7bb,96,111,.001,0,0,V,2143,6143,ff,21_43_#13 BusBar Voltage06\n" //
                        +"7bb,112,127,.001,0,0,V,2143,6143,ff,21_43_#15 BusBar Voltage07\n" //
                        +"7bb,128,143,.001,0,0,V,2143,6143,ff,21_43_#17 BusBar Voltage08\n" //
                        +"7bb,144,159,.001,0,0,V,2143,6143,ff,21_43_#19 BusBar Voltage09\n" //
                        +"7bb,160,175,.001,0,0,V,2143,6143,ff,21_43_#21 BusBar Voltage10\n" //
                        +"7bb,176,191,.001,0,0,V,2143,6143,ff,21_43_#23 BusBar Voltage11\n" //
                        +"7bb,192,207,.001,0,0,V,2143,6143,ff,21_43_#25 BusBar Voltage12\n" //
                        +"7bb,208,223,.001,0,0,V,2143,6143,ff,21_43_#27 BusBar Voltage13\n" //
                        +"7bb,224,239,.001,0,0,V,2143,6143,ff,21_43_#29 BusBar Voltage14\n" //
                        +"7bb,240,255,.001,0,0,V,2143,6143,ff,21_43_#31 BusBar Voltage15\n" //
                        +"7bb,256,271,.001,0,0,V,2143,6143,ff,21_43_#33 BusBar Voltage16\n" //
                        +"7bb,272,287,.001,0,0,V,2143,6143,ff,21_43_#35 BusBar Voltage17\n" //
                        +"7bb,288,303,.001,0,0,V,2143,6143,ff,21_43_#37 BusBar Voltage18\n" //
                        +"7bb,304,319,.001,0,0,V,2143,6143,ff,21_43_#39 BusBar Voltage19\n" //
                        +"7bb,320,335,.001,0,0,V,2143,6143,ff,21_43_#41 BusBar Voltage20\n" //
                        +"7bb,352,367,.001,0,0,V,2143,6143,ff,21_43_#45 BusBar Voltage22\n" //
                        +"7bb,368,383,.001,0,0,V,2143,6143,ff,21_43_#47 BusBar Voltage23\n" //
                        +"7bb,336,351,.001,0,0,V,2143,6143,ff,21_43_#41 BusBar Voltage21\n" //
                        +"7bb,24,39,1,0,0,V,221415,621415,ff,1415_Pack Voltage\n" //
                        +"7bb,24,39,1,0,0,kW,221407,621407,ff,1407_Input Possible Power (After Restriction)\n" //
                        +"7bb,24,39,1,0,0,kW,221408,621408,ff,1408_Output Possible (After Restriction)\n" //
                        +"7bb,24,31,1,40,0,degC,221410,621410,ff,1410_Average Temperature\n" //
                        +"7bb,24,39,.01,0,0,V,22140D,62140D,ff,140D_B+ Voltage\n" //
                        +"7bb,16,47,1,0,0,,2191,6191,ff,21_91#03 SW Version of Master\n" //
                        +"7bb,48,79,1,0,0,,2191,6191,ff,21_91#05 SW Version of Safety\n" //
                        +"7bb,80,111,1,0,0,,2191,6191,ff,21_91#11 SW Version of Slave1\n" //
                        +"7bb,112,143,1,0,0,,2191,6191,ff,21_91#15 SW Version of Slave2\n" //
                        +"7bb,144,175,1,0,0,,2191,6191,ff,21_91#19 SW Version of Slave3\n" //
                        +"7bb,176,207,1,0,0,,2191,6191,ff,21_91#23 SW Version of Slave4\n" //
                        +"7bb,208,239,1,0,0,,2191,6191,ff,21_91#27 SW Version of Slave5\n" //
                        +"7bb,240,271,1,0,0,,2191,6191,ff,21_91#31 SW Version of Slave6\n" //
                        +"7bb,272,303,1,0,0,,2191,6191,ff,21_91#35 SW Version of Slave7\n" //
                        +"7bb,304,335,1,0,0,,2191,6191,ff,21_91#39 SW Version of Slave8\n" //
                        +"7bb,336,367,1,0,0,,2191,6191,ff,21_91#43 SW Version of Slave9\n" //
                        +"7bb,368,399,1,0,0,,2191,6191,ff,21_91#47 SW Version of Slave10\n" //
                        +"7bb,400,431,1,0,0,,2191,6191,ff,21_91#51 SW Version of Slave11\n" //
                        +"7bb,432,463,1,0,0,,2191,6191,ff,21_91#55 SW Version of Slave12\n" //
                        +"7bb,16,47,1,0,0,,2193,6193,ff,21_93#03 EOL Command for LGIT\n" //
                        +"7bb,48,55,1,0,0,,2193,6193,ff,21_93#07 EOL Command for LGIT2\n" //
                        +"7bb,16,31,1,0,0,,2194,6194,ff,21_94#03 IR Filtering Parameter Postive\n" //
                        +"7bb,32,47,1,0,0,,2194,6194,ff,21_94#05 IR Filtering Parameter Negative\n" //
                        +"7bb,64,79,1,0,0,,2194,6194,ff,21_94#09 IR Diag. Switch Parameter1 (P1)\n" //
                        +"7bb,48,63,1,0,0,,2194,6194,ff,21_94#07 IR Diag. Threshold\n" //
                        +"7bb,80,95,1,0,0,,2194,6194,ff,21_94#11 IR Diag. Switch Parameter2 (P2)\n" //
                        +"7bb,96,111,1,0,0,,2194,6194,ff,21_94#13 IR Diag. Switch Parameter3 (P3)\n" //
                        +"7bb,112,127,1,0,0,,2194,6194,ff,21_94#15 IR Diag. Switch Parameter4 (P4)\n" //
                        +"7bb,128,143,1,0,0,,2194,6194,ff,21_94#17 IR Diag. Accuracy Positive Parameter\n" //
                        +"7bb,144,159,1,0,0,,2194,6194,ff,21_94#19 IR Diag. Accuracy Negative Parameter\n" //
                        +"7bb,176,191,1,0,0,,2194,6194,ff,21_94#23 IR Diag. Reserved2\n" //
                        +"7bb,160,175,1,0,0,,2194,6194,ff,21_94#21 IR Diag. Reserved1\n" //
                        +"7bb,192,207,1,0,0,,2194,6194,ff,21_94#25 IR Diag. Reserved3\n" //
                        +"7bb,208,223,1,0,0,,2194,6194,ff,21_94#27 IR Diag. Reserved4\n" //
                        +"7bb,24,39,1,0,0,,221414,621414,ff,1414_Pack Voltage A/D Value\n" //
                        +"7bb,39,39,1,0,0,,22141B,62141B,ff,141B Relavant Module no..141B Failure Slave 01,0:No Slave Failure;1:Slave Failure\n" //
                        +"7bb,38,38,1,0,0,,22141B,62141B,ff,141B Relavant Module no..141B Failure Slave 02,0:No Slave Failure;1:Slave Failure\n" //
                        +"7bb,37,37,1,0,0,,22141B,62141B,ff,141B Relavant Module no..141B Failure Slave 03,0:No Slave Failure;1:Slave Failure\n" //
                        +"7bb,36,36,1,0,0,,22141B,62141B,ff,141B Relavant Module no..141B Failure Slave 04,0:No Slave Failure;1:Slave Failure\n" //
                        +"7bb,35,35,1,0,0,,22141B,62141B,ff,141B Relavant Module no..141B Failure Slave 05,0:No Slave Failure;1:Slave Failure\n" //
                        +"7bb,34,34,1,0,0,,22141B,62141B,ff,141B Relavant Module no..141B Failure Slave 06,0:No Slave Failure;1:Slave Failure\n" //
                        +"7bb,33,33,1,0,0,,22141B,62141B,ff,141B Relavant Module no..141B Failure Slave 07,0:No Slave Failure;1:Slave Failure\n" //
                        +"7bb,32,32,1,0,0,,22141B,62141B,ff,141B Relavant Module no..141B Failure Slave 08,0:No Slave Failure;1:Slave Failure\n" //
                        +"7bb,31,31,1,0,0,,22141B,62141B,ff,141B Relavant Module no..141B Failure Slave 09,0:No Slave Failure;1:Slave Failure\n" //
                        +"7bb,30,30,1,0,0,,22141B,62141B,ff,141B Relavant Module no..141B Failure Slave 10,0:No Slave Failure;1:Slave Failure\n" //
                        +"7bb,29,29,1,0,0,,22141B,62141B,ff,141B Relavant Module no..141B Failure Slave 11,0:No Slave Failure;1:Slave Failure\n" //
                        +"7bb,28,28,1,0,0,,22141B,62141B,ff,141B Relavant Module no..141B Failure Slave 12,0:No Slave Failure;1:Slave Failure\n" //
                        +"7bb,16,175,1,0,0,,2184,6184,ff,TraceAbility_Data\n" //
                        +"7bb,32,39,1,0,0,,2184,6184,2ff,21_84#05_Energy provider\n" //
                        +"7bb,24,31,1,0,0,,2184,6184,ff,21_84#04_Battery Supplier,76:LG\n" //
                        +"7bb,40,47,1,0,0,,2184,6184,ff,21_84#06_Battery factory,70:Flins;75:Korea\n" //
                        +"7bb,48,55,1,0,0,,2184,6184,2ff,21_84#07_Functional battery\n" //
                        +"7bb,56,63,1,0,0,,2184,6184,ff,21_84#08_Production year,67:2010;68:2011;69:2012;70:2013;71:2014;72:2015\n" //
                        +"7bb,64,71,1,0,0,,2184,6184,ff,21_84#09_Production month,49:Janvier;50:Fevrier;51:Mars;52:Avril;53:Mai;54:Juin;55:Jullet;56:Aout;57:Septembre;58:Octobre;59:Novembre;60:Decembre\n" //
                        +"7bb,72,87,1,0,0,,2184,6184,ff,21_84#10_Serial number (five last bits) high part\n" //
                        +"7bb,88,111,1,0,0,,2184,6184,ff,21_84#10_Serial number (five last bits) low part\n" //
                        +"7bb,112,143,1,0,0,,2184,6184,2ff,21_84#11_Supplier identification (LID $90) high part\n" //
                        +"7bb,144,175,1,0,0,,2184,6184,2ff,21_84#11_Supplier identification (LID $90) low part\n" //
                        +"7bb,16,23,1,0,0,,2190,6190,2ff,21_90#03 Product number for LG\n" //
                        +"7bb,24,31,1,0,0,,2190,6190,2ff,21_90#04 Product number for LG\n" //
                        +"7bb,32,39,1,0,0,,2190,6190,2ff,21_90#05 Product number for LG\n" //
                        +"7bb,40,47,1,0,0,,2190,6190,2ff,21_90#06 Product number for LG\n" //
                        +"7bb,48,55,1,0,0,,2190,6190,2ff,21_90#07 Product number for LG\n" //
                        +"7bb,56,63,1,0,0,,2190,6190,2ff,21_90#08 Product number for LG\n" //
                        +"7bb,64,71,1,0,0,,2190,6190,2ff,21_90#09 Product number for LG\n" //
                        +"7bb,72,79,1,0,0,,2190,6190,2ff,21_90#10 Product number for LG\n" //
                        +"7bb,16,55,1,0,0,,21FF,61FF,2ff,21_FF#03 ApprovalNumber\n" //
                        +"7bb,56,95,1,0,0,,21FF,61FF,2ff,21_FF#08 ProgrammingSite\n" //
                        +"7bb,96,135,1,0,0,,21FF,61FF,2ff,21_FF#13 Programming Tool reference\n" //
                        +"7bb,136,143,1,0,0,,21FF,61FF,ff,21_FF#18 NumberOfReprogrammings\n" //
                        +"7bb,144,167,1,0,0,,21FF,61FF,ff,21_FF#19 Date of Programming\n" //
                        +"7bb,168,183,1,0,0,,21FF,61FF,ff,21_FF#22 Reserve\n" //
                        +"7bb,184,191,1,0,0,,21FF,61FF,ff,21_FF#24 SaveMarking\n" //
                        +"7bb,192,207,1,0,0,,21FF,61FF,ff,21_FF#25 CRC of log record\n" //
                        +"7bb,16,55,1,0,0,,21FE,61FE,2ff,21_FE#03 Part Number Lower Part\n" //
                        +"7bb,56,63,1,0,0,,21FE,61FE,ff,21_FE#08 Diagnostic identification code\n" //
                        +"7bb,64,87,1,0,0,,21FE,61FE,2ff,21_FE#09 Supplier Number\n" //
                        +"7bb,88,127,1,0,0,,21FE,61FE,2ff,21_FE#12 Hardware Number Lower Part\n" //
                        +"7bb,128,143,1,0,0,,21FE,61FE,ff,21_FE#17 Software Number\n" //
                        +"7bb,144,159,1,0,0,,21FE,61FE,ff,21_FE#19 Edition Number\n" //
                        +"7bb,160,175,1,0,0,,21FE,61FE,ff,21_FE#21 Calibratin Number\n" //
                        +"7bb,176,183,1,0,0,,21FE,61FE,ff,21_FE#23 Part Number Basic Part List\n" //
                        +"7bb,184,191,1,0,0,,21FE,61FE,ff,21_FE#24 HardwareNumber Basic Part List\n" //
                        +"7bb,192,199,1,0,0,,21FE,61FE,ff,21_FE#25 ApprovalNumber Basic Part List\n" //
                        +"7bb,200,207,1,0,0,,21FE,61FE,ff,21_FE#26 Car manufacturer identification code\n" //
                        +"7bb,16,55,1,0,0,,21FD,61FD,2ff,21_FD#03 ApprovalNumber\n" //
                        +"7bb,56,95,1,0,0,,21FD,61FD,2ff,21_FD#08 ProgrammingSite\n" //
                        +"7bb,96,135,1,0,0,,21FD,61FD,2ff,21_FD#13 Programming Tool reference\n" //
                        +"7bb,136,143,1,0,0,,21FD,61FD,ff,21_FD#18 NumberOfReprogrammings\n" //
                        +"7bb,144,167,1,0,0,,21FD,61FD,ff,21_FD#19 Date of Programming\n" //
                        +"7bb,168,183,1,0,0,,21FD,61FD,ff,21_FD#22 Reserve\n" //
                        +"7bb,184,191,1,0,0,,21FD,61FD,ff,21_FD#24 SaveMarking\n" //
                        +"7bb,192,207,1,0,0,,21FD,61FD,ff,21_FD#25 CRC of log record\n" //
                        +"7bb,16,55,1,0,0,,21FC,61FC,2ff,21_FC#03 Part Number Lower Part\n" //
                        +"7bb,56,63,1,0,0,,21FC,61FC,ff,21_FC#08 Diagnostic identification code\n" //
                        +"7bb,64,87,1,0,0,,21FC,61FC,2ff,21_FC#09 Supplier Number\n" //
                        +"7bb,88,127,1,0,0,,21FC,61FC,2ff,21_FC#12 Hardware Number Lower Part\n" //
                        +"7bb,128,143,1,0,0,,21FC,61FC,ff,21_FC#17 Software Number\n" //
                        +"7bb,144,159,1,0,0,,21FC,61FC,ff,21_FC#19 Edition Number\n" //
                        +"7bb,160,175,1,0,0,,21FC,61FC,ff,21_FC#21 Calibratin Number\n" //
                        +"7bb,176,183,1,0,0,,21FC,61FC,ff,21_FC#23 Part Number Basic Part List\n" //
                        +"7bb,184,191,1,0,0,,21FC,61FC,ff,21_FC#24 HardwareNumber Basic Part List\n" //
                        +"7bb,192,199,1,0,0,,21FC,61FC,ff,21_FC#25 ApprovalNumber Basic Part List\n" //
                        +"7bb,200,207,1,0,0,,21FC,61FC,ff,21_FC#26 Car manufacturer identification code\n" //
                        +"7bb,16,55,1,0,0,,21FB,61FB,2ff,21_FB#03 ApprovalNumber\n" //
                        +"7bb,56,95,1,0,0,,21FB,61FB,2ff,21_FB#08 ProgrammingSite\n" //
                        +"7bb,96,135,1,0,0,,21FB,61FB,2ff,21_FB#13 Programming Tool reference\n" //
                        +"7bb,136,143,1,0,0,,21FB,61FB,ff,21_FB#18 NumberOfReprogrammings\n" //
                        +"7bb,144,167,1,0,0,,21FB,61FB,ff,21_FB#19 Date of Programming\n" //
                        +"7bb,168,183,1,0,0,,21FB,61FB,ff,21_FB#22 Reserve\n" //
                        +"7bb,184,191,1,0,0,,21FB,61FB,ff,21_FB#24 SaveMarking\n" //
                        +"7bb,192,207,1,0,0,,21FB,61FB,ff,21_FB#25 CRC of log record\n" //
                        +"7bb,16,55,1,0,0,,21FA,61FA,2ff,21_FA#03 Part Number Lower Part\n" //
                        +"7bb,56,63,1,0,0,,21FA,61FA,ff,21_FA#08 Diagnostic identification code\n" //
                        +"7bb,64,87,1,0,0,,21FA,61FA,2ff,21_FA#09 Supplier Number\n" //
                        +"7bb,88,127,1,0,0,,21FA,61FA,2ff,21_FA#12 Hardware Number Lower Part\n" //
                        +"7bb,128,143,1,0,0,,21FA,61FA,ff,21_FA#17 Software Number\n" //
                        +"7bb,144,159,1,0,0,,21FA,61FA,ff,21_FA#19 Edition Number\n" //
                        +"7bb,160,175,1,0,0,,21FA,61FA,ff,21_FA#21 Calibratin Number\n" //
                        +"7bb,176,183,1,0,0,,21FA,61FA,ff,21_FA#23 Part Number Basic Part List\n" //
                        +"7bb,184,191,1,0,0,,21FA,61FA,ff,21_FA#24 HardwareNumber Basic Part List\n" //
                        +"7bb,192,199,1,0,0,,21FA,61FA,ff,21_FA#25 ApprovalNumber Basic Part List\n" //
                        +"7bb,200,207,1,0,0,,21FA,61FA,ff,21_FA#26 Car manufacturer identification code\n" //
                        +"7bb,16,55,1,0,0,,21F9,61F9,2ff,21_F9#03 ApprovalNumber\n" //
                        +"7bb,56,95,1,0,0,,21F9,61F9,2ff,21_F9#08 ProgrammingSite\n" //
                        +"7bb,96,135,1,0,0,,21F9,61F9,2ff,21_F9#13 Programming Tool reference\n" //
                        +"7bb,136,143,1,0,0,,21F9,61F9,ff,21_F9#18 NumberOfReprogrammings\n" //
                        +"7bb,144,167,1,0,0,,21F9,61F9,ff,21_F9#19 Date of Programming\n" //
                        +"7bb,168,183,1,0,0,,21F9,61F9,ff,21_F9#22 Reserve\n" //
                        +"7bb,184,191,1,0,0,,21F9,61F9,ff,21_F9#24 SaveMarking\n" //
                        +"7bb,192,207,1,0,0,,21F9,61F9,ff,21_F9#25 CRC of log record\n" //
                        +"7bb,16,55,1,0,0,,21F8,61F8,2ff,21_F8#03 Part Number Lower Part\n" //
                        +"7bb,56,63,1,0,0,,21F8,61F8,ff,21_F8#08 Diagnostic identification code\n" //
                        +"7bb,64,87,1,0,0,,21F8,61F8,2ff,21_F8#09 Supplier Number\n" //
                        +"7bb,88,127,1,0,0,,21F8,61F8,2ff,21_F8#12 Hardware Number Lower Part\n" //
                        +"7bb,128,143,1,0,0,,21F8,61F8,ff,21_F8#17 Software Number\n" //
                        +"7bb,144,159,1,0,0,,21F8,61F8,ff,21_F8#19 Edition Number\n" //
                        +"7bb,160,175,1,0,0,,21F8,61F8,ff,21_F8#21 Calibratin Number\n" //
                        +"7bb,176,183,1,0,0,,21F8,61F8,ff,21_F8#23 Part Number Basic Part List\n" //
                        +"7bb,184,191,1,0,0,,21F8,61F8,ff,21_F8#24 HardwareNumber Basic Part List\n" //
                        +"7bb,192,199,1,0,0,,21F8,61F8,ff,21_F8#25 ApprovalNumber Basic Part List\n" //
                        +"7bb,200,207,1,0,0,,21F8,61F8,ff,21_F8#26 Car manufacturer identification code\n" //
                        +"7bb,16,55,1,0,0,,21F7,61F7,2ff,21_F7#03 ApprovalNumber\n" //
                        +"7bb,56,95,1,0,0,,21F7,61F7,2ff,21_F7#08 ProgrammingSite\n" //
                        +"7bb,96,135,1,0,0,,21F7,61F7,2ff,21_F7#13 Programming Tool reference\n" //
                        +"7bb,136,143,1,0,0,,21F7,61F7,ff,21_F7#18 NumberOfReprogrammings\n" //
                        +"7bb,144,167,1,0,0,,21F7,61F7,ff,21_F7#19 Date of Programming\n" //
                        +"7bb,168,183,1,0,0,,21F7,61F7,ff,21_F7#22 Reserve\n" //
                        +"7bb,184,191,1,0,0,,21F7,61F7,ff,21_F7#24 SaveMarking\n" //
                        +"7bb,192,207,1,0,0,,21F7,61F7,ff,21_F7#25 CRC of log record\n" //
                        +"7bb,16,55,1,0,0,,21F6,61F6,2ff,21_F6#03 Part Number Lower Part\n" //
                        +"7bb,56,63,1,0,0,,21F6,61F6,ff,21_F6#08 Diagnostic identification code\n" //
                        +"7bb,64,87,1,0,0,,21F6,61F6,2ff,21_F6#09 Supplier Number\n" //
                        +"7bb,88,127,1,0,0,,21F6,61F6,2ff,21_F6#12 Hardware Number Lower Part\n" //
                        +"7bb,128,143,1,0,0,,21F6,61F6,ff,21_F6#17 Software Number\n" //
                        +"7bb,144,159,1,0,0,,21F6,61F6,ff,21_F6#19 Edition Number\n" //
                        +"7bb,160,175,1,0,0,,21F6,61F6,ff,21_F6#21 Calibratin Number\n" //
                        +"7bb,176,183,1,0,0,,21F6,61F6,ff,21_F6#23 Part Number Basic Part List\n" //
                        +"7bb,184,191,1,0,0,,21F6,61F6,ff,21_F6#24 HardwareNumber Basic Part List\n" //
                        +"7bb,192,199,1,0,0,,21F6,61F6,ff,21_F6#25 ApprovalNumber Basic Part List\n" //
                        +"7bb,200,207,1,0,0,,21F6,61F6,ff,21_F6#26 Car manufacturer identification code\n" //
                        +"7bb,16,55,1,0,0,,21F5,61F5,2ff,21_F5#03 ApprovalNumber\n" //
                        +"7bb,56,95,1,0,0,,21F5,61F5,2ff,21_F5#08 ProgrammingSite\n" //
                        +"7bb,96,135,1,0,0,,21F5,61F5,2ff,21_F5#13 Programming Tool reference\n" //
                        +"7bb,136,143,1,0,0,,21F5,61F5,ff,21_F5#18 NumberOfReprogrammings\n" //
                        +"7bb,144,167,1,0,0,,21F5,61F5,ff,21_F5#19 Date of Programming\n" //
                        +"7bb,168,183,1,0,0,,21F5,61F5,ff,21_F5#22 Reserve\n" //
                        +"7bb,184,191,1,0,0,,21F5,61F5,ff,21_F5#24 SaveMarking\n" //
                        +"7bb,192,207,1,0,0,,21F5,61F5,ff,21_F5#25 CRC of log record\n" //
                        +"7bb,16,55,1,0,0,,21F4,61F4,2ff,21_F4#03 Part Number Lower Part\n" //
                        +"7bb,56,63,1,0,0,,21F4,61F4,ff,21_F4#08 Diagnostic identification code\n" //
                        +"7bb,64,87,1,0,0,,21F4,61F4,2ff,21_F4#09 Supplier Number\n" //
                        +"7bb,88,127,1,0,0,,21F4,61F4,2ff,21_F4#12 Hardware Number Lower Part\n" //
                        +"7bb,128,143,1,0,0,,21F4,61F4,ff,21_F4#17 Software Number\n" //
                        +"7bb,144,159,1,0,0,,21F4,61F4,ff,21_F4#19 Edition Number\n" //
                        +"7bb,160,175,1,0,0,,21F4,61F4,ff,21_F4#21 Calibratin Number\n" //
                        +"7bb,176,183,1,0,0,,21F4,61F4,ff,21_F4#23 Part Number Basic Part List\n" //
                        +"7bb,184,191,1,0,0,,21F4,61F4,ff,21_F4#24 HardwareNumber Basic Part List\n" //
                        +"7bb,192,199,1,0,0,,21F4,61F4,ff,21_F4#25 ApprovalNumber Basic Part List\n" //
                        +"7bb,200,207,1,0,0,,21F4,61F4,ff,21_F4#26 Car manufacturer identification code\n" //
                        +"7bb,16,55,1,0,0,,21F3,61F3,2ff,21_F3#03 ApprovalNumber\n" //
                        +"7bb,56,95,1,0,0,,21F3,61F3,2ff,21_F3#08 ProgrammingSite\n" //
                        +"7bb,96,135,1,0,0,,21F3,61F3,2ff,21_F3#13 Programming Tool reference\n" //
                        +"7bb,136,143,1,0,0,,21F3,61F3,ff,21_F3#18 NumberOfReprogrammings\n" //
                        +"7bb,144,167,1,0,0,,21F3,61F3,ff,21_F3#19 Date of Programming\n" //
                        +"7bb,168,183,1,0,0,,21F3,61F3,ff,21_F3#22 Reserve\n" //
                        +"7bb,184,191,1,0,0,,21F3,61F3,ff,21_F3#24 SaveMarking\n" //
                        +"7bb,192,207,1,0,0,,21F3,61F3,ff,21_F3#25 CRC of log record\n" //
                        +"7bb,16,55,1,0,0,,21F2,61F2,2ff,21_F2#03 Part Number Lower Part\n" //
                        +"7bb,56,63,1,0,0,,21F2,61F2,ff,21_F2#08 Diagnostic identification code\n" //
                        +"7bb,64,87,1,0,0,,21F2,61F2,2ff,21_F2#09 Supplier Number\n" //
                        +"7bb,88,127,1,0,0,,21F2,61F2,2ff,21_F2#12 Hardware Number Lower Part\n" //
                        +"7bb,128,143,1,0,0,,21F2,61F2,ff,21_F2#17 Software Number\n" //
                        +"7bb,144,159,1,0,0,,21F2,61F2,ff,21_F2#19 Edition Number\n" //
                        +"7bb,160,175,1,0,0,,21F2,61F2,ff,21_F2#21 Calibratin Number\n" //
                        +"7bb,176,183,1,0,0,,21F2,61F2,ff,21_F2#23 Part Number Basic Part List\n" //
                        +"7bb,184,191,1,0,0,,21F2,61F2,ff,21_F2#24 HardwareNumber Basic Part List\n" //
                        +"7bb,192,199,1,0,0,,21F2,61F2,ff,21_F2#25 ApprovalNumber Basic Part List\n" //
                        +"7bb,200,207,1,0,0,,21F2,61F2,ff,21_F2#26 Car manufacturer identification code\n" //
                        +"7bb,16,55,1,0,0,,21F1,61F1,2ff,21_F1#03 ApprovalNumber\n" //
                        +"7bb,56,95,1,0,0,,21F1,61F1,2ff,21_F1#08 ProgrammingSite\n" //
                        +"7bb,96,135,1,0,0,,21F1,61F1,2ff,21_F1#13 Programming Tool reference\n" //
                        +"7bb,136,143,1,0,0,,21F1,61F1,ff,21_F1#18 NumberOfReprogrammings\n" //
                        +"7bb,144,167,1,0,0,,21F1,61F1,ff,21_F1#19 Date of Programming\n" //
                        +"7bb,168,183,1,0,0,,21F1,61F1,ff,21_F1#22 Reserve\n" //
                        +"7bb,184,191,1,0,0,,21F1,61F1,ff,21_F1#24 SaveMarking\n" //
                        +"7bb,192,207,1,0,0,,21F1,61F1,ff,21_F1#25 CRC of log record\n" //
                        +"7bb,16,55,1,0,0,,21F0,61F0,2ff,21_F0#03 Part Number Lower Part\n" //
                        +"7bb,56,63,1,0,0,,21F0,61F0,ff,21_F0#08 Diagnostic identification code\n" //
                        +"7bb,64,87,1,0,0,,21F0,61F0,2ff,21_F0#09 Supplier Number\n" //
                        +"7bb,88,127,1,0,0,,21F0,61F0,2ff,21_F0#12 Hardware Number Lower Part\n" //
                        +"7bb,128,143,1,0,0,,21F0,61F0,ff,21_F0#17 Software Number\n" //
                        +"7bb,144,159,1,0,0,,21F0,61F0,ff,21_F0#19 Edition Number\n" //
                        +"7bb,160,175,1,0,0,,21F0,61F0,ff,21_F0#21 Calibratin Number\n" //
                        +"7bb,176,183,1,0,0,,21F0,61F0,ff,21_F0#23 Part Number Basic Part List\n" //
                        +"7bb,184,191,1,0,0,,21F0,61F0,ff,21_F0#24 HardwareNumber Basic Part List\n" //
                        +"7bb,192,199,1,0,0,,21F0,61F0,ff,21_F0#25 ApprovalNumber Basic Part List\n" //
                        +"7bb,200,207,1,0,0,,21F0,61F0,ff,21_F0#26 Car manufacturer identification code\n" //
                        +"7bb,24,39,.001,0,0,V,22141C,62141C,ff,141C Cell Voltage No.1 of Failure Slave\n" //
                        +"7bb,24,39,.001,0,0,V,22141D,62141D,ff,141D Cell Voltage No.2 of Failure Slave\n" //
                        +"7bb,24,39,.001,0,0,V,22141F,62141F,ff,141F Cell Voltage No.4 of Failure Slave\n" //
                        +"7bb,24,39,.001,0,0,V,22141E,62141E,ff,141E Cell Voltage No.3 of Failure Slave\n" //
                        +"7bb,24,39,.001,0,0,V,221421,621421,ff,1421 Cell Voltage No.6 of Failure Slave\n" //
                        +"7bb,24,26,1,0,0,,221424,621424,ff,1424 BMS State (LBC_CANHS_R_01/02/03).1424_BMS State,0:Not Used;1:Slow charge;2:Fast charge;3:Init;4:Transitory;5:Normal;6:Quick Drop;7:Unavalaible Vlaue\n" //
                        +"7bb,27,28,1,0,0,,221424,621424,ff,1424 BMS State (LBC_CANHS_R_01/02/03).1424_LBC RefusetoSleep,0:Not Used;1:LBC Refuse To Sleep;2:LBC Ready To Sleep;3:Not Used 2\n" //
                        +"7bb,29,30,1,0,0,,221424,621424,ff,1424 BMS State (LBC_CANHS_R_01/02/03).1424_HVpowerConnection,1:HVPowerConnection allowed;2:HVPowerConnection Not allowed\n" //
                        +"7bb,31,32,1,0,0,,221424,621424,ff,1424 BMS State (LBC_CANHS_R_01/02/03).1424_IsolDiagAuthorisation,0:Not Used;1:Diag of isolation allowed;2:Diag of isolation not allowed;3:Unavailable value\n" //
                        +"7bb,33,33,1,0,0,,221424,621424,ff,1424 BMS State (LBC_CANHS_R_01/02/03).1424_HVIL 1_SD,0:Opened;1:Closed\n" //
                        +"7bb,34,34,1,0,0,,221424,621424,ff,1424 BMS State (LBC_CANHS_R_01/02/03).1424_HVIL 2_QD,0:Opened;1:Closed\n" //
                        +"7bb,35,39,1,0,0,,221424,621424,ff,1424 BMS State (LBC_CANHS_R_01/02/03).1424_Reserved\n" //
                        +"7bb,24,25,1,0,0,,221425,621425,ff,1425 Request From Vehicle (SCH_CANHS__R02/R03).1425_ETS_SleepMode,2:Quick Drop;0:Normal Sleep;1:14V Battery Management;3:Unavailable Value\n" //
                        +"7bb,26,27,1,0,0,,221425,621425,ff,1425 Request From Vehicle (SCH_CANHS__R02/R03).1425_SCH_WakeUpSleepCommand,2:Not Used 2;1:Not Used 1;0:Go to Sleep;3:Wake Up\n" //
                        +"7bb,28,28,1,0,0,,221425,621425,ff,1425 Request From Vehicle (SCH_CANHS__R02/R03).1425_WakeUpType,0:Customer Wake Up;1:Technical Wake Up\n" //
                        +"7bb,29,31,1,0,0,,221425,621425,ff,1425 Request From Vehicle (SCH_CANHS__R02/R03).1425_OperatingType,0:No Request;1:Slow Charge;2:Fast Charge;3:Normal;4:Quick Drop;5:Not Used 1;6:Not Used 2;7:Unavailable Value\n" //
                        +"7bb,32,33,1,0,0,,221425,621425,ff,1425 Request From Vehicle (SCH_CANHS__R02/R03).1425_PowerRelayState,0:Precharge;2:Opened;1:Closed;3:Transitory States\n" //
                        +"7bb,34,39,1,0,0,,221425,621425,ff,1425 Request From Vehicle (SCH_CANHS__R02/R03).1425_Reserved\n" //
                        +"7bb,16,215,1,0,0,,21A0,61A0,2ff,21_A0_#03_BIN\n" //
                        +"7bb,24,39,.001,0,0,V,221420,621420,ff,1420 Cell Voltage No.5 of Failure Slave\n" //
                        +"7bb,24,39,.001,0,0,V,221422,621422,ff,1422 Cell Voltage No.7 of Failure Slave\n" //
                        +"7bb,24,39,.001,0,0,V,221423,621423,ff,1423 Cell Voltage No.8 of Failure Slave\n" //
                        +"7bb,24,39,1,0,0,KW,221427,621427,ff,1427 Charge Possible Power (After Restriction)\n" //
                        +"7bb,16,39,1,0,0,,2160,6160,ff,21_60_#003_AbsTimePack\n" //
                        +"7bb,40,47,1,0,0,,2160,6160,ff,21_60_#006_Simpler SOHE (RSA Model)\n" //
                        +"7bb,48,55,1,0,0,,2160,6160,ff,21_60_#007_Average SOHE (LGC Model)\n" //
                        +"7bb,64,71,1,0,0,,2160,6160,ff,21_60_#009_BMS_Update_command_applied,1:APPLIED;255:NOT APPLIED\n" //
                        +"7bb,56,63,1,0,0,,2160,6160,ff,21_60_#008_SOHE(module)_Update_command_applied,1:APPLIED;255:NOT APPLIED\n" //
                        +"7bb,72,79,1,0,0,,2160,6160,ff,21_60_#010_Storage_Update_Command_Applied,1:APPLIED 1 time;2:APPLIED 2 times;3:APPLIED 3 times;4:APPLIED 4 times;5:APPLIED 5 times;6:APPLIED 6 times;7:APPLIED 7 times;8:APPLIED 8 times;9:APPLIED 9 times;10:APPLIED 10 times;255:NOT APPLIED\n" //
                        +"7bb,16,31,1,0,0,%,2163,6163,ff,63_03 Init SOC\n" //
                        +"7bb,32,39,1,0,0,,2163,6163,ff,63_05 Init Method,1:SOC_EKF;2:SOC_OCV\n" //
                        +"7bb,40,47,1,40,0,°C,2163,6163,ff,63_06 Init Min Temp\n" //
                        +"7bb,48,79,1,0,0,Minutes,2163,6163,ff,63_07 Init Driving Time\n" //
                        +"7bb,80,111,1,0,0,Minutes,2163,6163,ff,63_11 Init Parking Time\n" //
                        +"7bb,112,143,1,2000000000,0,A,2163,6163,ff,63_15 Init Current Sum\n" //
                        +"7bb,144,159,1,0,0,%,2163,6163,ff,63_19 Final SOC [Segment 1]\n" //
                        +"7bb,160,167,1,0,0,,2163,6163,ff,63_21 Final Method [Segment 1],1:SOC_EKF;2:SOC_OCV\n" //
                        +"7bb,168,175,1,40,0,°C,2163,6163,ff,63_22 Final Min Temp [Segment 1]\n" //
                        +"7bb,176,207,1,0,0,Minutes,2163,6163,ff,63_23 Final Driving Time [Segment 1]\n" //
                        +"7bb,208,239,1,0,0,Minutes,2163,6163,ff,63_27 Final Parking Time [Segment 1]\n" //
                        +"7bb,240,271,1,2000000000,0,A,2163,6163,ff,63_31 Final Current Sum [Segment 1]\n" //
                        +"7bb,272,287,1,0,0,%,2163,6163,ff,63_35 Final SOC [Segment 2]\n" //
                        +"7bb,288,295,1,0,0,,2163,6163,ff,63_37 Final Method [Segment 2],1:SOC_EKF;2:SOC_OCV\n" //
                        +"7bb,296,303,1,40,0,°C,2163,6163,ff,63_38 Final Min Temp [Segment 2]\n" //
                        +"7bb,304,335,1,0,0,Minutes,2163,6163,ff,63_39 Final Driving Time [Segment 2]\n" //
                        +"7bb,336,367,1,0,0,Minutes,2163,6163,ff,63_43 Final Parking Time [Segment 2]\n" //
                        +"7bb,368,399,1,2000000000,0,A,2163,6163,ff,63_47 Final Current Sum [Segment 2]\n" //
                        +"7bb,400,415,1,0,0,%,2163,6163,ff,63_51 Final SOC [Segment 3]\n" //
                        +"7bb,416,423,1,0,0,,2163,6163,ff,63_53 Final Method [Segment 3],1:SOC_EKF;2:SOC_OCV\n" //
                        +"7bb,424,431,1,40,0,°C,2163,6163,ff,63_54 Final Min Temp [Segment 3]\n" //
                        +"7bb,432,463,1,0,0,Minutes,2163,6163,ff,63_55 Final Driving Time [Segment 3]\n" //
                        +"7bb,464,495,1,0,0,Minutes,2163,6163,ff,63_59 Final Parking Time [Segment 3]\n" //
                        +"7bb,496,527,1,2000000000,0,A,2163,6163,ff,63_63 Final Current Sum [Segment 3]\n" //
                        +"7bb,528,543,1,0,0,%,2163,6163,ff,63_67 Final SOC [Segment 4]\n" //
                        +"7bb,544,551,1,0,0,,2163,6163,ff,63_69 Final Method [Segment 4],1:SOC_EKF;2:SOC_OCV\n" //
                        +"7bb,552,559,1,40,0,°C,2163,6163,ff,63_70 Final Min Temp [Segment 4]\n" //
                        +"7bb,560,591,1,0,0,Minutes,2163,6163,ff,63_71 Final Driving Time [Segment 4]\n" //
                        +"7bb,592,623,1,0,0,Minutes,2163,6163,ff,63_75 Final Parking Time [Segment 4]\n" //
                        +"7bb,624,655,1,2000000000,0,A,2163,6163,ff,63_79 Final Current Sum [Segment 4]\n" //
                        +"7bb,656,671,1,0,0,%,2163,6163,ff,63_83 Final SOC [Segment 5]\n" //
                        +"7bb,672,679,1,0,0,,2163,6163,ff,63_85 Final Method [Segment 5],1:SOC_EKF;2:SOC_OCV\n" //
                        +"7bb,680,687,1,40,0,°C,2163,6163,ff,63_86 Final Min Temp [Segment 5]\n" //
                        +"7bb,688,719,1,0,0,Minutes,2163,6163,ff,63_87 Final Driving Time [Segment 5]\n" //
                        +"7bb,720,751,1,0,0,Minutes,2163,6163,ff,63_91 Final Parking Time [Segment 5]\n" //
                        +"7bb,752,783,1,2000000000,0,A,2163,6163,ff,63_95 Final Current Sum [Segment 5]\n" //
                        +"7bb,16,31,1,0,0,%,2164,6164,ff,64_03 Latest Init SOC\n" //
                        +"7bb,32,39,1,0,0,,2164,6164,ff,64_05 Latest Init Method,1:SOC_EKF;2:SOC_OCV\n" //
                        +"7bb,40,47,1,40,0,°C,2164,6164,ff,64_06 Latest Init Min Temp\n" //
                        +"7bb,48,79,1,0,0,Minutes,2164,6164,ff,64_07 Latest Init Driving Time\n" //
                        +"7bb,80,111,1,0,0,Minutes,2164,6164,ff,64_11 Latest Init Parking Time\n" //
                        +"7bb,112,143,1,2000000000,0,A,2164,6164,ff,64_15 Latest Init Current Sum\n" //
                        +"7bb,144,159,1,0,0,%,2164,6164,ff,64_19 Latest Final SOC [Segment 1]\n" //
                        +"7bb,160,167,1,0,0,,2164,6164,ff,64_21 Latest Final Method [Segment 1],1:SOC_EKF;2:SOC_OCV\n" //
                        +"7bb,168,175,1,40,0,°C,2164,6164,ff,64_22 Latest Final Min Temp [Segment 1]\n" //
                        +"7bb,176,207,1,0,0,Minutes,2164,6164,ff,64_23 Latest Final Driving Time [Segment 1]\n" //
                        +"7bb,208,239,1,0,0,Minutes,2164,6164,ff,64_27 Latest Final Parking Time [Segment 1]\n" //
                        +"7bb,240,271,1,2000000000,0,A,2164,6164,ff,64_31 Latest Final Current Sum [Segment 1]\n" //
                        +"7bb,272,287,1,0,0,%,2164,6164,ff,64_35 Latest Final SOC [Segment 2]\n" //
                        +"7bb,288,295,1,0,0,,2164,6164,ff,64_37 Latest Final Method [Segment 2],1:SOC_EKF;2:SOC_OCV\n" //
                        +"7bb,296,303,1,40,0,°C,2164,6164,ff,64_38 Latest Final Min Temp [Segment 2]\n" //
                        +"7bb,304,335,1,0,0,Minutes,2164,6164,ff,64_39 Latest Final Driving Time [Segment 2]\n" //
                        +"7bb,336,367,1,0,0,Minutes,2164,6164,ff,64_43 Latest Final Parking Time [Segment 2]\n" //
                        +"7bb,368,399,1,2000000000,0,A,2164,6164,ff,64_47 Latest Final Current Sum [Segment 2]\n" //
                        +"7bb,400,415,1,0,0,%,2164,6164,ff,64_51 Latest Final SOC [Segment 3]\n" //
                        +"7bb,416,423,1,0,0,,2164,6164,ff,64_53 Latest Final Method [Segment 3],1:SOC_EKF;2:SOC_OCV\n" //
                        +"7bb,424,431,1,40,0,°C,2164,6164,ff,64_54 Latest Final Min Temp [Segment 3]\n" //
                        +"7bb,432,463,1,0,0,Minutes,2164,6164,ff,64_55 Latest Final Driving Time [Segment 3]\n" //
                        +"7bb,464,495,1,0,0,Minutes,2164,6164,ff,64_59 Latest Final Parking Time [Segment 3]\n" //
                        +"7bb,496,527,1,2000000000,0,A,2164,6164,ff,64_63 Latest Final Current Sum [Segment 3]\n" //
                        +"7bb,528,543,1,0,0,%,2164,6164,ff,64_67 Latest Final SOC [Segment 4]\n" //
                        +"7bb,544,551,1,0,0,,2164,6164,ff,64_69 Latest Final Method [Segment 4],1:SOC_EKF;2:SOC_OCV\n" //
                        +"7bb,552,559,1,40,0,°C,2164,6164,ff,64_70 Latest Final Min Temp [Segment 4]\n" //
                        +"7bb,560,591,1,0,0,Minutes,2164,6164,ff,64_71 Latest Final Driving Time [Segment 4]\n" //
                        +"7bb,592,623,1,0,0,Minutes,2164,6164,ff,64_75 Latest Final Parking Time [Segment 4]\n" //
                        +"7bb,624,655,1,2000000000,0,A,2164,6164,ff,64_79 Latest Final Current Sum [Segment 4]\n" //
                        +"7bb,656,671,1,0,0,%,2164,6164,ff,64_83 Latest Final SOC [Segment 5]\n" //
                        +"7bb,672,679,1,0,0,,2164,6164,ff,64_85 Latest Final Method [Segment 5],1:SOC_EKF;2:SOC_OCV\n" //
                        +"7bb,680,687,1,40,0,°C,2164,6164,ff,64_86 Latest Final Min Temp [Segment 5]\n" //
                        +"7bb,688,719,1,0,0,Minutes,2164,6164,ff,64_87 Latest Final Driving Time [Segment 5]\n" //
                        +"7bb,720,751,1,0,0,Minutes,2164,6164,ff,64_91 Latest Final Parking Time [Segment 5]\n" //
                        +"7bb,752,783,1,2000000000,0,A,2164,6164,ff,64_95 Latest Final Current Sum [Segment 5]\n" //
                        +"7bb,16,31,1,0,0,%,2165,6165,ff,65_03 Latest SOHE_1Min [#00] Segment 0->1\n" //
                        +"7bb,32,39,1,0,0,%,2165,6165,ff,65_05 Latest WeightFactor [#00] Segment 0->1\n" //
                        +"7bb,40,47,1,0,0,,2165,6165,ff,65_06 Latest dSOHE/SOHE [#00] Segment 0->1\n" //
                        +"7bb,48,63,1,0,0,%,2165,6165,ff,65_07 Latest SOHE_1Min [#01] Segment 0->2\n" //
                        +"7bb,64,71,1,0,0,%,2165,6165,ff,65_09 Latest WeightFactor [#00] Segment 0->2\n" //
                        +"7bb,72,79,1,0,0,,2165,6165,ff,65_10 Latest dSOHE/SOHE [#01] Segment 0->2\n" //
                        +"7bb,80,95,1,0,0,%,2165,6165,ff,65_11 Latest SOHE_1Min [#02] Segment 0->3\n" //
                        +"7bb,96,103,1,0,0,%,2165,6165,ff,65_13 Latest WeightFactor [#02] Segment 0->3\n" //
                        +"7bb,104,111,1,0,0,,2165,6165,ff,65_14 Latest dSOHE/SOHE [#02] Segment 0->3\n" //
                        +"7bb,112,127,1,0,0,%,2165,6165,ff,65_15 Latest SOHE_1Min [#03] Segment 0->4\n" //
                        +"7bb,128,135,1,0,0,%,2165,6165,ff,65_17 Latest WeightFactor [#03] Segment 0->4\n" //
                        +"7bb,136,143,1,0,0,,2165,6165,ff,65_18 Latest dSOHE/SOHE [#03] Segment 0->4\n" //

                ;

        String fieldDef4 =
                ""

                        +"7bb,144,159,1,0,0,%,2165,6165,ff,65_19 Latest SOHE_1Min [#04] Segment 0->5\n" //
                        +"7bb,160,167,1,0,0,%,2165,6165,ff,65_21 Latest WeightFactor [#04] Segment 0->5\n" //
                        +"7bb,168,175,1,0,0,,2165,6165,ff,65_22 Latest dSOHE/SOHE [#04] Segment 0->5\n" //
                        +"7bb,176,191,1,0,0,%,2165,6165,ff,65_23 Latest SOHE_1Min [#05] Segment 1->2\n" //
                        +"7bb,192,199,1,0,0,%,2165,6165,ff,65_25 Latest WeightFactor [#05] Segment 1->2\n" //
                        +"7bb,200,207,1,0,0,,2165,6165,ff,65_26 Latest dSOHE/SOHE [#05] Segment 1->2\n" //
                        +"7bb,208,223,1,0,0,%,2165,6165,ff,65_27 Latest SOHE_1Min [#06] Segment 1->3\n" //
                        +"7bb,224,231,1,0,0,%,2165,6165,ff,65_29 Latest WeightFactor [#06] Segment 1->3\n" //
                        +"7bb,232,239,1,0,0,,2165,6165,ff,65_30 Latest dSOHE/SOHE [#06] Segment 1->3\n" //
                        +"7bb,240,255,1,0,0,%,2165,6165,ff,65_31 Latest SOHE_1Min [#07] Segment 1->4\n" //
                        +"7bb,256,263,1,0,0,%,2165,6165,ff,65_33 Latest WeightFactor [#07] Segment 1->4\n" //
                        +"7bb,264,271,1,0,0,,2165,6165,ff,65_34 Latest dSOHE/SOHE [#07] Segment 1->4\n" //
                        +"7bb,272,287,1,0,0,%,2165,6165,ff,65_35 Latest SOHE_1Min [#08] Segment 1->5\n" //
                        +"7bb,288,295,1,0,0,%,2165,6165,ff,65_37 Latest WeightFactor [#08] Segment 1->5\n" //
                        +"7bb,296,303,1,0,0,,2165,6165,ff,65_38 Latest dSOHE/SOHE [#08] Segment 1->5\n" //
                        +"7bb,304,319,1,0,0,%,2165,6165,ff,65_39 Latest SOHE_1Min [#09] Segment 2->3\n" //
                        +"7bb,320,327,1,0,0,%,2165,6165,ff,65_41 Latest WeightFactor [#09] Segment 2->3\n" //
                        +"7bb,328,335,1,0,0,,2165,6165,ff,65_42 Latest dSOHE/SOHE [#09] Segment 2->3\n" //
                        +"7bb,336,351,1,0,0,%,2165,6165,ff,65_43 Latest SOHE_1Min [#10] Segment 2->4\n" //
                        +"7bb,352,359,1,0,0,%,2165,6165,ff,65_45 Latest WeightFactor [#10] Segment 2->4\n" //
                        +"7bb,360,367,1,0,0,,2165,6165,ff,65_46 Latest dSOHE/SOHE [#10] Segment 2->4\n" //
                        +"7bb,368,383,1,0,0,%,2165,6165,ff,65_47 Latest SOHE_1Min [#11] Segment 2->5\n" //
                        +"7bb,384,391,1,0,0,%,2165,6165,ff,65_49 Latest WeightFactor [#11] Segment 2->5\n" //
                        +"7bb,392,399,1,0,0,,2165,6165,ff,65_50 Latest dSOHE/SOHE [#11] Segment 2->5\n" //
                        +"7bb,400,415,1,0,0,%,2165,6165,ff,65_51 Latest SOHE_1Min [#12] Segment 3->4\n" //
                        +"7bb,416,423,1,0,0,%,2165,6165,ff,65_53 Latest WeightFactor [#12] Segment 3->4\n" //
                        +"7bb,424,431,1,0,0,,2165,6165,ff,65_54 Latest dSOHE/SOHE [#12] Segment 3->4\n" //
                        +"7bb,432,447,1,0,0,%,2165,6165,ff,65_55 Latest SOHE_1Min [#13] Segment 3->5\n" //
                        +"7bb,448,455,1,0,0,%,2165,6165,ff,65_57 Latest WeightFactor [#13] Segment 3->5\n" //
                        +"7bb,456,463,1,0,0,,2165,6165,ff,65_58 Latest dSOHE/SOHE [#13] Segment 3->5\n" //
                        +"7bb,464,479,1,0,0,%,2165,6165,ff,65_59 Latest SOHE_1Min [#14] Segment 4->5\n" //
                        +"7bb,480,487,1,0,0,%,2165,6165,ff,65_61 Latest WeightFactor [#14] Segment 4->5\n" //
                        +"7bb,488,495,1,0,0,,2165,6165,ff,65_62 Latest dSOHE/SOHE [#14] Segment 4->5\n" //
                        +"7bb,16,47,1,2000000000,0,A,2168,6168,ff,68_03 Current Sum [Buffer #00]\n" //
                        +"7bb,48,63,1,0,0,%,2168,6168,ff,68_07 Init SOC [Buffer #00]\n" //
                        +"7bb,64,79,1,0,0,%,2168,6168,ff,68_09 Final SOC [Buffer #00]]\n" //
                        +"7bb,80,87,1,0,0,,2168,6168,ff,68_11 Init Method / Final Method [Buffer #00],18:EKF(Init)+OCV(Final);33:OCV(Init)+EKF(Final);34:OCV(Init)+OCV(Final)\n" //
                        +"7bb,88,95,1,0,0,,2168,6168,ff,68_12 NumOfSegmentCase [Buffer #00]\n" //
                        +"7bb,96,111,1,0,0,%,2168,6168,ff,68_13 SOHE_1Min [Buffer #00]\n" //
                        +"7bb,112,119,1,0,0,%,2168,6168,ff,68_15 WeightFactor [Buffer #00]\n" //
                        +"7bb,120,127,1,0,0,,2168,6168,ff,68_16 Updated State [Buffer #00],0:Unknown (Transitory);4:Normal;5:Charge\n" //
                        +"7bb,128,135,1,0,0,%,2168,6168,ff,68_17 dSOHE/SOHE [#00] Segment [Buffer #00]\n" //
                        +"7bb,136,167,1,2000000000,0,A,2168,6168,ff,68_18 Current Sum [Buffer #01]\n" //
                        +"7bb,168,183,1,0,0,%,2168,6168,ff,68_22 Init SOC [Buffer #01]\n" //
                        +"7bb,184,199,1,0,0,%,2168,6168,ff,68_24 Final SOC [Buffer #01]\n" //
                        +"7bb,200,207,1,0,0,,2168,6168,ff,68_26 Init Method / Final Method [Buffer #01],18:EKF(Init)+OCV(Final);33:OCV(Init)+EKF(Final);34:OCV(Init)+OCV(Final)\n" //
                        +"7bb,208,215,1,0,0,,2168,6168,ff,68_27 NumOfSegmentCase [Buffer #01]\n" //
                        +"7bb,216,231,1,0,0,%,2168,6168,ff,68_28 SOHE_1Min [Buffer #01]\n" //
                        +"7bb,232,239,1,0,0,%,2168,6168,ff,68_30 WeightFactor [Buffer #01]\n" //
                        +"7bb,240,247,1,0,0,,2168,6168,ff,68_31 Updated State [Buffer #01],0:Unknown (Transitory);4:Normal;5:Charge\n" //
                        +"7bb,248,255,1,0,0,%,2168,6168,ff,68_32 dSOHE/SOHE [#00] Segment [Buffer #01]\n" //
                        +"7bb,256,287,1,2000000000,0,A,2168,6168,ff,68_33 Current Sum [Buffer #02]\n" //
                        +"7bb,288,303,1,0,0,%,2168,6168,ff,68_37 Init SOC [Buffer #02]\n" //
                        +"7bb,304,319,1,0,0,%,2168,6168,ff,68_39 Final SOC [Buffer #02]\n" //
                        +"7bb,320,327,1,0,0,,2168,6168,ff,68_41 Init Method / Final Method [Buffer #02],18:EKF(Init)+OCV(Final);33:OCV(Init)+EKF(Final);34:OCV(Init)+OCV(Final)\n" //
                        +"7bb,328,335,1,0,0,,2168,6168,ff,68_42 NumOfSegmentCase [Buffer #02]\n" //
                        +"7bb,336,351,1,0,0,%,2168,6168,ff,68_43 SOHE_1Min [Buffer #02]\n" //
                        +"7bb,352,359,1,0,0,%,2168,6168,ff,68_45 WeightFactor [Buffer #02]\n" //
                        +"7bb,360,367,1,0,0,,2168,6168,ff,68_46 Updated State [Buffer #02],0:Unknown (Transitory);4:Normal;5:Charge\n" //
                        +"7bb,368,375,1,0,0,%,2168,6168,ff,68_47 dSOHE/SOHE [#00] Segment [Buffer #02]\n" //
                        +"7bb,376,407,1,2000000000,0,A,2168,6168,ff,68_48 Current Sum [Buffer #03]\n" //
                        +"7bb,408,423,1,0,0,%,2168,6168,ff,68_52 Init SOC [Buffer #03]\n" //
                        +"7bb,424,439,1,0,0,%,2168,6168,ff,68_54 Final SOC [Buffer #03]\n" //
                        +"7bb,440,447,1,0,0,,2168,6168,ff,68_56 Init Method / Final Method [Buffer #03],18:EKF(Init)+OCV(Final);33:OCV(Init)+EKF(Final);34:OCV(Init)+OCV(Final)\n" //
                        +"7bb,448,455,1,0,0,,2168,6168,ff,68_57 NumOfSegmentCase [Buffer #03]\n" //
                        +"7bb,456,471,1,0,0,%,2168,6168,ff,68_58 SOHE_1Min [Buffer #03]\n" //
                        +"7bb,472,479,1,0,0,%,2168,6168,ff,68_60 WeightFactor [Buffer #03]\n" //
                        +"7bb,480,487,1,0,0,,2168,6168,ff,68_61 Updated State [Buffer #03],0:Unknown (Transitory);4:Normal;5:Charge\n" //
                        +"7bb,488,495,1,0,0,%,2168,6168,ff,68_62 dSOHE/SOHE [#00] Segment [Buffer #03]\n" //
                        +"7bb,496,527,1,2000000000,0,A,2168,6168,ff,68_63 Current Sum [Buffer #04]\n" //
                        +"7bb,528,543,1,0,0,%,2168,6168,ff,68_67 Init SOC [Buffer #04]\n" //
                        +"7bb,544,559,1,0,0,%,2168,6168,ff,68_69 Final SOC [Buffer #04]\n" //
                        +"7bb,560,567,1,0,0,,2168,6168,ff,68_71 Init Method / Final Method [Buffer #04],18:EKF(Init)+OCV(Final);33:OCV(Init)+EKF(Final);34:OCV(Init)+OCV(Final)\n" //
                        +"7bb,568,575,1,0,0,,2168,6168,ff,68_72 NumOfSegmentCase [Buffer #04]\n" //
                        +"7bb,576,591,1,0,0,%,2168,6168,ff,68_73 SOHE_1Min [Buffer #04]\n" //
                        +"7bb,592,599,1,0,0,%,2168,6168,ff,68_75 WeightFactor [Buffer #04]\n" //
                        +"7bb,600,607,1,0,0,,2168,6168,ff,68_76 Updated State [Buffer #04],0:Unknown (Transitory);4:Normal;5:Charge\n" //
                        +"7bb,608,615,1,0,0,%,2168,6168,ff,68_77 dSOHE/SOHE [#00] Segment [Buffer #04]\n" //
                        +"7bb,616,647,1,2000000000,0,A,2168,6168,ff,68_78 Current Sum [Buffer #05]\n" //
                        +"7bb,648,663,1,0,0,%,2168,6168,ff,68_82 Init SOC [Buffer #05]\n" //
                        +"7bb,664,679,1,0,0,%,2168,6168,ff,68_84 Final SOC [Buffer #05]\n" //
                        +"7bb,680,687,1,0,0,,2168,6168,ff,68_86 Init Method / Final Method [Buffer #05],18:EKF(Init)+OCV(Final);33:OCV(Init)+EKF(Final);34:OCV(Init)+OCV(Final)\n" //
                        +"7bb,688,695,1,0,0,,2168,6168,ff,68_87 NumOfSegmentCase [Buffer #05]\n" //
                        +"7bb,696,711,1,0,0,%,2168,6168,ff,68_88 SOHE_1Min [Buffer #05]\n" //
                        +"7bb,712,719,1,0,0,%,2168,6168,ff,68_90 WeightFactor [Buffer #05]\n" //
                        +"7bb,720,727,1,0,0,,2168,6168,ff,68_91 Updated State [Buffer #05],0:Unknown (Transitory);4:Normal;5:Charge\n" //
                        +"7bb,728,735,1,0,0,%,2168,6168,ff,68_92 dSOHE/SOHE [#00] Segment [Buffer #05]\n" //
                        +"7bb,736,767,1,2000000000,0,A,2168,6168,ff,68_93 Current Sum [Buffer #06]\n" //
                        +"7bb,768,783,1,0,0,%,2168,6168,ff,68_97 Init SOC [Buffer #06]\n" //
                        +"7bb,784,799,1,0,0,%,2168,6168,ff,68_99 Final SOC [Buffer #06]\n" //
                        +"7bb,800,807,1,0,0,,2168,6168,ff,68_101 Init Method / Final Method [Buffer #06],18:EKF(Init)+OCV(Final);33:OCV(Init)+EKF(Final);34:OCV(Init)+OCV(Final)\n" //
                        +"7bb,808,815,1,0,0,,2168,6168,ff,68_102 NumOfSegmentCase [Buffer #06]\n" //
                        +"7bb,816,831,1,0,0,%,2168,6168,ff,68_103 SOHE_1Min [Buffer #06]\n" //
                        +"7bb,832,839,1,0,0,%,2168,6168,ff,68_105 WeightFactor [Buffer #06]\n" //
                        +"7bb,840,847,1,0,0,,2168,6168,ff,68_106 Updated State [Buffer #06],0:Unknown (Transitory);4:Normal;5:Charge\n" //
                        +"7bb,848,855,1,0,0,%,2168,6168,ff,68_107 dSOHE/SOHE [#00] Segment [Buffer #06]\n" //
                        +"7bb,856,887,1,2000000000,0,A,2168,6168,ff,68_108 Current Sum [Buffer #07]\n" //
                        +"7bb,888,903,1,0,0,%,2168,6168,ff,68_112 Init SOC [Buffer #07]\n" //
                        +"7bb,904,919,1,0,0,%,2168,6168,ff,68_114 Final SOC [Buffer #07]\n" //
                        +"7bb,920,927,1,0,0,,2168,6168,ff,68_116 Init Method / Final Method [Buffer #07],18:EKF(Init)+OCV(Final);33:OCV(Init)+EKF(Final);34:OCV(Init)+OCV(Final)\n" //
                        +"7bb,928,935,1,0,0,,2168,6168,ff,68_117 NumOfSegmentCase [Buffer #07]\n" //
                        +"7bb,936,951,1,0,0,%,2168,6168,ff,68_118 SOHE_1Min [Buffer #07]\n" //
                        +"7bb,952,959,1,0,0,%,2168,6168,ff,68_120 WeightFactor [Buffer #07]\n" //
                        +"7bb,960,967,1,0,0,,2168,6168,ff,68_121 Updated State [Buffer #07],0:Unknown (Transitory);4:Normal;5:Charge\n" //
                        +"7bb,968,975,1,0,0,%,2168,6168,ff,68_122 dSOHE/SOHE [#00] Segment [Buffer #07]\n" //
                        +"7bb,976,1007,1,2000000000,0,A,2168,6168,ff,68_123 Current Sum [Buffer #08]\n" //
                        +"7bb,1008,1023,1,0,0,%,2168,6168,ff,68_127 Init SOC [Buffer #08]\n" //
                        +"7bb,1024,1039,1,0,0,%,2168,6168,ff,68_129 Final SOC [Buffer #08]\n" //
                        +"7bb,1040,1047,1,0,0,,2168,6168,ff,68_131 Init Method / Final Method [Buffer #08],18:EKF(Init)+OCV(Final);33:OCV(Init)+EKF(Final);34:OCV(Init)+OCV(Final)\n" //
                        +"7bb,1048,1055,1,0,0,,2168,6168,ff,68_132 NumOfSegmentCase [Buffer #08]\n" //
                        +"7bb,1056,1071,1,0,0,%,2168,6168,ff,68_133 SOHE_1Min [Buffer #08]\n" //
                        +"7bb,1072,1079,1,0,0,%,2168,6168,ff,68_135 WeightFactor [Buffer #08]\n" //
                        +"7bb,1080,1087,1,0,0,,2168,6168,ff,68_136 Updated State [Buffer #08],0:Unknown (Transitory);4:Normal;5:Charge\n" //
                        +"7bb,1088,1095,1,0,0,%,2168,6168,ff,68_137 dSOHE/SOHE [#00] Segment [Buffer #08]\n" //
                        +"7bb,1096,1127,1,2000000000,0,A,2168,6168,ff,68_138 Current Sum [Buffer #09]\n" //
                        +"7bb,1128,1143,1,0,0,%,2168,6168,ff,68_142 Init SOC [Buffer #09]\n" //
                        +"7bb,1144,1159,1,0,0,%,2168,6168,ff,68_144 Final SOC [Buffer #09]\n" //
                        +"7bb,1160,1167,1,0,0,,2168,6168,ff,68_146 Init Method / Final Method [Buffer #09],18:EKF(Init)+OCV(Final);33:OCV(Init)+EKF(Final);34:OCV(Init)+OCV(Final)\n" //
                        +"7bb,1168,1175,1,0,0,,2168,6168,ff,68_147 NumOfSegmentCase [Buffer #09]\n" //
                        +"7bb,1176,1191,1,0,0,%,2168,6168,ff,68_148 SOHE_1Min [Buffer #09]\n" //
                        +"7bb,1192,1199,1,0,0,%,2168,6168,ff,68_150 WeightFactor [Buffer #09]\n" //
                        +"7bb,1200,1207,1,0,0,,2168,6168,ff,68_151 Updated State [Buffer #09],0:Unknown (Transitory);4:Normal;5:Charge\n" //
                        +"7bb,1208,1215,1,0,0,%,2168,6168,ff,68_152 dSOHE/SOHE [#00] Segment [Buffer #09]\n" //
                        +"7bb,16,47,1,2000000000,0,A,2169,6169,ff,69_03 Current Sum [Buffer #10]\n" //
                        +"7bb,48,63,1,0,0,%,2169,6169,ff,69_07 Init SOC [Buffer #10]\n" //
                        +"7bb,64,79,1,0,0,%,2169,6169,ff,69_09 Final SOC [Buffer #10]]\n" //
                        +"7bb,80,87,1,0,0,,2169,6169,ff,69_11 Init Method / Final Method [Buffer #10],18:EKF(Init)+OCV(Final);33:OCV(Init)+EKF(Final);34:OCV(Init)+OCV(Final)\n" //
                        +"7bb,88,95,1,0,0,,2169,6169,ff,69_12 NumOfSegmentCase [Buffer #10]\n" //
                        +"7bb,96,111,1,0,0,%,2169,6169,ff,69_13 SOHE_1Min [Buffer #10]\n" //
                        +"7bb,112,119,1,0,0,%,2169,6169,ff,69_15 WeightFactor [Buffer #10]\n" //
                        +"7bb,120,127,1,0,0,,2169,6169,ff,69_16 Updated State [Buffer #10],0:Unknown (Transitory);4:Normal;5:Charge\n" //
                        +"7bb,128,135,1,0,0,%,2169,6169,ff,69_17 dSOHE/SOHE [#00] Segment [Buffer #10]\n" //
                        +"7bb,136,167,1,2000000000,0,A,2169,6169,ff,69_18 Current Sum [Buffer #11]\n" //
                        +"7bb,168,183,1,0,0,%,2169,6169,ff,69_22 Init SOC [Buffer #11]\n" //
                        +"7bb,184,199,1,0,0,%,2169,6169,ff,69_24 Final SOC [Buffer #11]\n" //
                        +"7bb,200,207,1,0,0,,2169,6169,ff,69_26 Init Method / Final Method [Buffer #11],18:EKF(Init)+OCV(Final);33:OCV(Init)+EKF(Final);34:OCV(Init)+OCV(Final)\n" //
                        +"7bb,208,215,1,0,0,,2169,6169,ff,69_27 NumOfSegmentCase [Buffer #11]\n" //
                        +"7bb,216,231,1,0,0,%,2169,6169,ff,69_28 SOHE_1Min [Buffer #11]\n" //
                        +"7bb,232,239,1,0,0,%,2169,6169,ff,69_30 WeightFactor [Buffer #11]\n" //
                        +"7bb,240,247,1,0,0,,2169,6169,ff,69_31 Updated State [Buffer #11],0:Unknown (Transitory);4:Normal;5:Charge\n" //
                        +"7bb,248,255,1,0,0,%,2169,6169,ff,69_32 dSOHE/SOHE [#00] Segment [Buffer #11]\n" //
                        +"7bb,256,287,1,2000000000,0,A,2169,6169,ff,69_33 Current Sum [Buffer #12]\n" //
                        +"7bb,288,303,1,0,0,%,2169,6169,ff,69_37 Init SOC [Buffer #12]\n" //
                        +"7bb,304,319,1,0,0,%,2169,6169,ff,69_39 Final SOC [Buffer #12]\n" //
                        +"7bb,320,327,1,0,0,,2169,6169,ff,69_41 Init Method / Final Method [Buffer #12],18:EKF(Init)+OCV(Final);33:OCV(Init)+EKF(Final);34:OCV(Init)+OCV(Final)\n" //
                        +"7bb,328,335,1,0,0,,2169,6169,ff,69_42 NumOfSegmentCase [Buffer #12]\n" //
                        +"7bb,336,351,1,0,0,%,2169,6169,ff,69_43 SOHE_1Min [Buffer #12]\n" //
                        +"7bb,352,359,1,0,0,%,2169,6169,ff,69_45 WeightFactor [Buffer #12]\n" //
                        +"7bb,360,367,1,0,0,,2169,6169,ff,69_46 Updated State [Buffer #12],0:Unknown (Transitory);4:Normal;5:Charge\n" //
                        +"7bb,368,375,1,0,0,%,2169,6169,ff,69_47 dSOHE/SOHE [#00] Segment [Buffer #12]\n" //
                        +"7bb,376,407,1,2000000000,0,A,2169,6169,ff,69_48 Current Sum [Buffer #13]\n" //
                        +"7bb,408,423,1,0,0,%,2169,6169,ff,69_52 Init SOC [Buffer #13]\n" //
                        +"7bb,424,439,1,0,0,%,2169,6169,ff,69_54 Final SOC [Buffer #13]\n" //
                        +"7bb,440,447,1,0,0,,2169,6169,ff,69_56 Init Method / Final Method [Buffer #13],18:EKF(Init)+OCV(Final);33:OCV(Init)+EKF(Final);34:OCV(Init)+OCV(Final)\n" //
                        +"7bb,448,455,1,0,0,,2169,6169,ff,69_57 NumOfSegmentCase [Buffer #13]\n" //
                        +"7bb,456,471,1,0,0,%,2169,6169,ff,69_58 SOHE_1Min [Buffer #13]\n" //
                        +"7bb,472,479,1,0,0,%,2169,6169,ff,69_60 WeightFactor [Buffer #13]\n" //
                        +"7bb,480,487,1,0,0,,2169,6169,ff,69_61 Updated State [Buffer #13],0:Unknown (Transitory);4:Normal;5:Charge\n" //
                        +"7bb,488,495,1,0,0,%,2169,6169,ff,69_62 dSOHE/SOHE [#00] Segment [Buffer #13]\n" //
                        +"7bb,496,527,1,2000000000,0,A,2169,6169,ff,69_63 Current Sum [Buffer #14]\n" //
                        +"7bb,528,543,1,0,0,%,2169,6169,ff,69_67 Init SOC [Buffer #14]\n" //
                        +"7bb,544,559,1,0,0,%,2169,6169,ff,69_69 Final SOC [Buffer #14]\n" //
                        +"7bb,560,567,1,0,0,,2169,6169,ff,69_71 Init Method / Final Method [Buffer #14],18:EKF(Init)+OCV(Final);33:OCV(Init)+EKF(Final);34:OCV(Init)+OCV(Final)\n" //
                        +"7bb,568,575,1,0,0,,2169,6169,ff,69_72 NumOfSegmentCase [Buffer #14]\n" //
                        +"7bb,576,591,1,0,0,%,2169,6169,ff,69_73 SOHE_1Min [Buffer #14]\n" //
                        +"7bb,592,599,1,0,0,%,2169,6169,ff,69_75 WeightFactor [Buffer #14]\n" //
                        +"7bb,600,607,1,0,0,,2169,6169,ff,69_76 Updated State [Buffer #14],0:Unknown (Transitory);4:Normal;5:Charge\n" //
                        +"7bb,608,615,1,0,0,%,2169,6169,ff,69_77 dSOHE/SOHE [#00] Segment [Buffer #14]\n" //
                        +"7bb,616,647,1,2000000000,0,A,2169,6169,ff,69_78 Current Sum [Buffer #15]\n" //
                        +"7bb,648,663,1,0,0,%,2169,6169,ff,69_82 Init SOC [Buffer #15]\n" //
                        +"7bb,664,679,1,0,0,%,2169,6169,ff,69_84 Final SOC [Buffer #15]\n" //
                        +"7bb,680,687,1,0,0,,2169,6169,ff,69_86 Init Method / Final Method [Buffer #15],18:EKF(Init)+OCV(Final);33:OCV(Init)+EKF(Final);34:OCV(Init)+OCV(Final)\n" //
                        +"7bb,688,695,1,0,0,,2169,6169,ff,69_87 NumOfSegmentCase [Buffer #15]\n" //
                        +"7bb,696,711,1,0,0,%,2169,6169,ff,69_88 SOHE_1Min [Buffer #15]\n" //
                        +"7bb,712,719,1,0,0,%,2169,6169,ff,69_90 WeightFactor [Buffer #15]\n" //
                        +"7bb,720,727,1,0,0,,2169,6169,ff,69_91 Updated State [Buffer #15],0:Unknown (Transitory);4:Normal;5:Charge\n" //
                        +"7bb,728,735,1,0,0,%,2169,6169,ff,69_92 dSOHE/SOHE [#00] Segment [Buffer #15]\n" //
                        +"7bb,736,767,1,2000000000,0,A,2169,6169,ff,69_93 Current Sum [Buffer #16]\n" //
                        +"7bb,768,783,1,0,0,%,2169,6169,ff,69_97 Init SOC [Buffer #16]\n" //
                        +"7bb,784,799,1,0,0,%,2169,6169,ff,69_99 Final SOC [Buffer #16]\n" //
                        +"7bb,800,807,1,0,0,,2169,6169,ff,69_101 Init Method / Final Method [Buffer #16],18:EKF(Init)+OCV(Final);33:OCV(Init)+EKF(Final);34:OCV(Init)+OCV(Final)\n" //
                        +"7bb,808,815,1,0,0,,2169,6169,ff,69_102 NumOfSegmentCase [Buffer #16]\n" //
                        +"7bb,816,831,1,0,0,%,2169,6169,ff,69_103 SOHE_1Min [Buffer #16]\n" //
                        +"7bb,832,839,1,0,0,%,2169,6169,ff,69_105 WeightFactor [Buffer #16]\n" //
                        +"7bb,840,847,1,0,0,,2169,6169,ff,69_106 Updated State [Buffer #16],0:Unknown (Transitory);4:Normal;5:Charge\n" //
                        +"7bb,848,855,1,0,0,%,2169,6169,ff,69_107 dSOHE/SOHE [#00] Segment [Buffer #16]\n" //
                        +"7bb,856,887,1,2000000000,0,A,2169,6169,ff,69_108 Current Sum [Buffer #17]\n" //
                        +"7bb,888,903,1,0,0,%,2169,6169,ff,69_112 Init SOC [Buffer #17]\n" //
                        +"7bb,904,919,1,0,0,%,2169,6169,ff,69_114 Final SOC [Buffer #17]\n" //
                        +"7bb,920,927,1,0,0,,2169,6169,ff,69_116 Init Method / Final Method [Buffer #17],18:EKF(Init)+OCV(Final);33:OCV(Init)+EKF(Final);34:OCV(Init)+OCV(Final)\n" //
                        +"7bb,928,935,1,0,0,,2169,6169,ff,69_117 NumOfSegmentCase [Buffer #17]\n" //
                        +"7bb,936,951,1,0,0,%,2169,6169,ff,69_118 SOHE_1Min [Buffer #17]\n" //
                        +"7bb,952,959,1,0,0,%,2169,6169,ff,69_120 WeightFactor [Buffer #17]\n" //
                        +"7bb,960,967,1,0,0,,2169,6169,ff,69_121 Updated State [Buffer #17],0:Unknown (Transitory);4:Normal;5:Charge\n" //
                        +"7bb,968,975,1,0,0,%,2169,6169,ff,69_122 dSOHE/SOHE [#00] Segment [Buffer #17]\n" //
                        +"7bb,976,1007,1,2000000000,0,A,2169,6169,ff,69_123 Current Sum [Buffer #18]\n" //
                        +"7bb,1008,1023,1,0,0,%,2169,6169,ff,69_127 Init SOC [Buffer #18]\n" //
                        +"7bb,1024,1039,1,0,0,%,2169,6169,ff,69_129 Final SOC [Buffer #18]\n" //
                        +"7bb,1040,1047,1,0,0,,2169,6169,ff,69_131 Init Method / Final Method [Buffer #18],18:EKF(Init)+OCV(Final);33:OCV(Init)+EKF(Final);34:OCV(Init)+OCV(Final)\n" //
                        +"7bb,1048,1055,1,0,0,,2169,6169,ff,69_132 NumOfSegmentCase [Buffer #18]\n" //
                        +"7bb,1056,1071,1,0,0,%,2169,6169,ff,69_133 SOHE_1Min [Buffer #18]\n" //
                        +"7bb,1072,1079,1,0,0,%,2169,6169,ff,69_135 WeightFactor [Buffer #18]\n" //
                        +"7bb,1080,1087,1,0,0,,2169,6169,ff,69_136 Updated State [Buffer #18],0:Unknown (Transitory);4:Normal;5:Charge\n" //
                        +"7bb,1088,1095,1,0,0,%,2169,6169,ff,69_137 dSOHE/SOHE [#00] Segment [Buffer #18]\n" //
                        +"7bb,1096,1127,1,2000000000,0,A,2169,6169,ff,69_138 Current Sum [Buffer #19]\n" //
                        +"7bb,1128,1143,1,0,0,%,2169,6169,ff,69_142 Init SOC [Buffer #19]\n" //
                        +"7bb,1144,1159,1,0,0,%,2169,6169,ff,69_144 Final SOC [Buffer #19]\n" //
                        +"7bb,1160,1167,1,0,0,,2169,6169,ff,69_146 Init Method / Final Method [Buffer #19],18:EKF(Init)+OCV(Final);33:OCV(Init)+EKF(Final);34:OCV(Init)+OCV(Final)\n" //
                        +"7bb,1168,1175,1,0,0,,2169,6169,ff,69_147 NumOfSegmentCase [Buffer #19]\n" //
                        +"7bb,1176,1191,1,0,0,%,2169,6169,ff,69_148 SOHE_1Min [Buffer #19]\n" //
                        +"7bb,1192,1199,1,0,0,%,2169,6169,ff,69_150 WeightFactor [Buffer #19]\n" //
                        +"7bb,1200,1207,1,0,0,,2169,6169,ff,69_151 Updated State [Buffer #19],0:Unknown (Transitory);4:Normal;5:Charge\n" //
                        +"7bb,1208,1215,1,0,0,%,2169,6169,ff,69_152 dSOHE/SOHE [#00] Segment [Buffer #19]\n" //

                ;

        String dtcDef =
                ""

                        +"33E2,[P33E2]Battery High Temperature Diagnosis\n" //
                        +"33E3,[P33E3]Battery Low Temperature Diagnosis\n" //
                        +"0A1F,[P0A1F]BATTERY ENERGY CONTROL MODULE\n" //
                        +"3332,[P3332] Cell 2nd Over voltage happens\n" //
                        +"33C1,[P33C1] Temperature Sensor 14 out of range\n" //
                        +"33C0,[P33C0] Temperature Sensor 13 out of range\n" //
                        +"33C2,[P33C2] Temperature Sensor 15 out of range\n" //
                        +"33C4,[P33C4] Temperature Sensor 17 out of ange\n" //
                        +"33C5,[P33C5] Temperature Sensor 18 out of range\n" //
                        +"33C6,[P33C6] Temperature Sensor 19 out of range\n" //
                        +"33C7,[P33C7] Temperature Sensor 20 out of range\n" //
                        +"33C8,[P33C8] Temperature Sensor 21 out of range\n" //
                        +"33C9,[P33C9] Temperature Sensor 22 out of range\n" //
                        +"33CB,[P33CB] Temperature Sensor 24 out of range\n" //
                        +"33CA,[P33CA] Temperature Sensor 23 out of range\n" //
                        +"33D3,[P33D3] Temperature Sensor 12 out of range\n" //
                        +"33D2,[P33D2] Temperature Sensor 11 out of range\n" //
                        +"33D0,[P33D0] Temperature Sensor 9 out of range\n" //
                        +"33D1,[P33D1] Temperature Sensor 10 out of range\n" //
                        +"33D8,[P33D8] Temperature Sensor 5 out of range\n" //
                        +"33DD,[P33DD] Temperature Sensor 4 out of range\n" //
                        +"33DB,[P33DB] Temperature Sensor 3 out of range\n" //
                        +"33D9,[P33D9] Temperature Sensor 2 out of range\n" //
                        +"33D7,[P33D7] Temperature Sensor 1 out of range\n" //
                        +"33DC,[P33DC] Temperature Sensor 7 out of range\n" //
                        +"33DE,[P33DE] Temperature Sensor 8 out of range\n" //
                        +"33DA,[P33DA] Temperature Sensor 6 out of range\n" //
                        +"33D4,[P33D4] Battery State of Health Power has low value\n" //
                        +"33D6,[P33D6] CS (Current Sensing) Supply Failure\n" //
                        +"308B,[P308B]Cell_Line_Failure_(ASIC1)\n" //
                        +"308C,[P308C]Cell_Line_Failure_(ASIC2)\n" //
                        +"308D,[P308D]Cell_Line_Failure_(ASIC3)\n" //
                        +"308E,[P308E]Cell_Line_Failure_(ASIC4)\n" //
                        +"308F,[P308F]Cell_Line_Failure_(ASIC5)\n" //
                        +"3090,[P3090]Cell_Line_Failure_(ASIC6)\n" //
                        +"3092,[P3092]Cell_Line_Failure_(ASIC8)\n" //
                        +"3093,[P3093]Cell_Line_Failure_(ASIC9)\n" //
                        +"3094,[P3094]Cell_Line_Failure_(ASIC10)\n" //
                        +"3095,[P3095]Cell_Line_Failure_(ASIC11)\n" //
                        +"3096,[P3096]Cell_Line_Failure_(ASIC12)\n" //
                        +"3091,[P3091]Cell_Line_Failure_(ASIC7)\n" //
                        +"0A0A,[0A0A] HV Interlock circuit failure\n" //
                        +"0A0D,[P0A0D] SD interlock circuit failure\n" //
                        +"3020,[P3020] Private CAN Failure\n" //
                        +"3062,[P3062] cell balancing circuit failure\n" //
                        +"3063,[P3063] Cell Voltage measurement Failure\n" //
                        +"3064,[P3064]Second protection circuit failure\n" //
                        +"30F4,[P30F4] Pack Voltage Out of Range\n" //
                        +"30F5,[P30F5] Pack Voltage Measurement Failure\n" //
                        +"30FC,[P30FC] Pack over Current\n" //
                        +"30FE,[P30FE]Aux V has a Failure\n" //
                        +"3300,[P3300] Pack Over Voltage\n" //
                        +"3302,[P3302]Cell Over Voltage (Module1)\n" //
                        +"3303,[P3303]Cell Over Voltage (Module2)\n" //
                        +"3304,[P3304]Cell Over Voltage (Module3)\n" //
                        +"3305,[P3305]Cell Over Voltage (Module4)\n" //
                        +"3306,[P3306]Cell Over Voltage (Module5)\n" //
                        +"3307,[P3307]Cell Over Voltage (Module6)\n" //
                        +"3308,[P3308]Cell Over Voltage (Module7)\n" //
                        +"3309,[P3309]Cell Over Voltage (Module8)\n" //
                        +"330A,[P330A]Cell Over Voltage (Module9)\n" //
                        +"330B,[P330B]Cell Over Voltage (Module10)\n" //
                        +"330C,[P330C]Cell Over Voltage (Module11)\n" //
                        +"330D,[P330D]Cell Over Voltage (Module12)\n" //
                        +"3373,[P3373]Pack Under Voltage\n" //
                        +"3375,[P3375] Cell UnderVoltage module 1\n" //
                        +"3376,[P3376] Cell UnderVoltage module 2\n" //
                        +"3377,[P3377] Cell UnderVoltage module 3\n" //
                        +"3378,[P3378] Cell UnderVoltagemodule 4\n" //
                        +"3379,[P3379] Cell UnderVoltage module 5\n" //
                        +"337A,[P337A] Cell UnderVoltage module 6\n" //
                        +"337B,[P337B] Cell UnderVoltage module 7\n" //
                        +"337C,[P337C] Cell UnderVoltage module 8\n" //
                        +"337D,[P337D] Cell UnderVoltage module 9\n" //
                        +"337E,[P337E] Cell UnderVoltage module 10\n" //
                        +"337F,[P337F] Cell UnderVoltage module 11\n" //
                        +"3380,[P3380] Cell UnderVoltage module 12\n" //
                        +"33B1,[P33B1] Battery System has many failure on thermistors (1-12)\n" //
                        +"33B2,[P33B2] Battery System has many failure on thermistors (13-24)\n" //
                        +"33D5,[P33D5] CS (Current Sensing) Offset high\n" //
                        +"33C3,[P33C3] Temperature Sensor 16 out of range\n" //
                        +"33E1,[P33E1]IR Measurement Circuit Failure\n" //
                        +"33E6,[P33E6] Pack Unbalanced\n" //
                        +"33ED,[P33ED]Bus Bar Fixing Failure\n" //
                        +"D000,[D000] Loss of Communication CAN\n" //
                        +"330E,[P330E]Cell Over Voltage (Module13)\n" //
                        +"330F,[P330F]Cell Over Voltage (Module14)\n" //
                        +"3310,[P3310]Cell Over Voltage (Module15)\n" //
                        +"3311,[P3311]Cell Over Voltage (Module16)\n" //
                        +"3312,[P3312]Cell Over Voltage (Module17)\n" //
                        +"3313,[P3313]Cell Over Voltage (Module18)\n" //
                        +"3314,[P3314]Cell Over Voltage (Module19)\n" //
                        +"3315,[P3315]Cell Over Voltage (Module20)\n" //
                        +"3316,[P3316]Cell Over Voltage (Module21)\n" //
                        +"3317,[P3317]Cell Over Voltage (Module22)\n" //
                        +"3318,[P3318]Cell Over Voltage (Module23)\n" //
                        +"3319,[P3319]Cell Over Voltage (Module24)\n" //
                        +"3381,[P3381] Cell UnderVoltage module 13\n" //
                        +"3382,[P3382] Cell UnderVoltage module 14\n" //
                        +"3383,[P3383] Cell UnderVoltage module 15\n" //
                        +"3384,[P3384] Cell UnderVoltagemodule 16\n" //
                        +"3385,[P3385] Cell UnderVoltage module 17\n" //
                        +"3386,[P3386] Cell UnderVoltage module 18\n" //
                        +"3387,[P3387] Cell UnderVoltage module 19\n" //
                        +"3388,[P3388] Cell UnderVoltage module 20\n" //
                        +"3389,[P3389] Cell UnderVoltage module 21\n" //
                        +"338A,[P338A] Cell UnderVoltage module 22\n" //
                        +"338B,[P338B] Cell UnderVoltage module 23\n" //
                        +"338C,[P338C] Cell UnderVoltage module 24\n" //

                ;

        String testDef =
                ""

                        +"00,Device and failure type ODB codding\n" //
                        +"01,General Electrical Failure\n" //
                        +"02,General signal failure\n" //
                        +"03,FM / PWM (Pulse Width Modulated) Failures\n" //
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
                        +"28,signal bias level oor / zero adjustment failure\n" //
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
                        +"47,watchdog / safety ?C failure\n" //
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
                        +"97,Component or system operation obstructedorblocked\n" //
                        +"98,component or system over temperature\n" //

                ;

        Frames.getInstance().load("7BB,0,0,LBC\n");
        Fields.getInstance().load(fieldDef1);
        Fields.getInstance().loadMore(fieldDef2);
        Fields.getInstance().loadMore(fieldDef3);
        Fields.getInstance().loadMore(fieldDef4);
        Dtcs.getInstance().load(dtcDef, testDef);
    }
}