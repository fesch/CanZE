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

    static final public String fieldsString () {

        String fieldDef = // ID (hex), startBit, endBit, resolution, offset (aplied BEFORE resolution multiplication), decimals, unit, requestID (hex string), responseID (hex string),
                // options (hex, see MainActivity for definitions), optional name, optional list
                ""

                        + "7bb,32,47,1,5000,0,A,2101,6101,ff,DataRead.2101:21_01_#05_Current sensor offset value\n" //
                        + "7bb,176,191,.01,0,0,kW,2101,6101,ff,DataRead.2101:21_01_#23_Input Possible Power CAN Output (After Restriction)\n" //
                        + "7bb,192,207,.01,0,0,kW,2101,6101,ff,DataRead.2101:21_01_#25_Output Possible Power CAN Output (After Restriction)\n" //
                        + "7bb,224,239,.01,0,0,V,2101,6101,ff,DataRead.2101:21_01_#29_12V Battery Voltage (Auxiliary Battery Voltage)\n" //
                        + "7bb,288,319,.0001,0,0,%,2101,6101,ff,DataRead.2101:21_01_#37_SOC CAN Output (USOC)\n" //
                        + "7bb,336,351,.01,0,0,kW,2101,6101,ff,DataRead.2101:21_01_#43_Acceptable Max Charge Power\n" //
                        + "7bb,359,359,1,0,0,,2101,6101,ff,DataRead.2101:21_01_#45_bit0_SD Switch Interlock flag,0:opened;1:closed\n" //
                        + "7bb,358,358,1,0,0,,2101,6101,ff,DataRead.2101:21_01_#45_bit1_Charge End Flag,0:End of charge not detected;1:End of charge detected\n" //
                        + "7bb,357,357,1,0,0,,2101,6101,ff,DataRead.2101:21_01_#45_bit2_Filtered Poor Joint Detector Input Port Flug,0:Circuit no Failure;1:Circuit Failure\n" //
                        + "7bb,360,375,100,0,0,ohm,2101,6101,ff,DataRead.2101:21_01_#46_HVIsolationImpedance\n" //
                        + "7bb,144,159,1,0,0,A,2101,6101,ff,DataRead.2101:21_01_#19_Internal_AD_value_of_3V_Reference_Voltage\n" //
                        + "7bb,160,175,1,0,0,,2101,6101,ff,DataRead.2101:21_01_#21_Internal_AD_value_of_current_Sensor_power_supply_Voltage\n" //
                        + "7bb,354,355,1,0,0,,2101,6101,ff,DataRead.2101:21_01_#45_bit4_bit5_Interlock Status sent by CAN(regarding CAN data about interlock 'interlock battery'),0:Not used;1:Opened;2:closed;3:Unavailable\n" //
                        + "7bb,356,356,1,0,0,,2101,6101,ff,DataRead.2101:21_01_#45_bit3_HV Interlock Flag,0:opened;1:closed\n" //
                        + "7bb,16,31,1,5000,0,A,2101,6101,ff,DataRead.2101:21_01_#03 Electric_Current_of_Battery\n" //
                        + "7bb,16,47,.0001,0,0,Ah,2161,6161,ff,DataRead.2161:21_61_#03_Present Full Capacity\n" //
                        + "7bb,72,79,.5,0,0,%,2161,6161,ff,DataRead.2161:21_61_#10_Battery State of Health (average)\n" //
                        + "7bb,88,119,1,0,0,km,2161,6161,ff,DataRead.2161:21_61_#12_Battery Mileage\n" //
                        + "7bb,120,151,1,0,0,kWh,2161,6161,ff,DataRead.2161:21_61_#16_Sum of kWh from beginning of Battery life\n" //
                        + "7bb,56,63,.5,0,0,,2161,6161,ff,DataRead.2161:21_61_#08 SOHP_value_of_group2\n" //
                        + "7bb,80,87,.5,0,0,%,2161,6161,ff,DataRead.2161:21_61_#11_Battery State of Health (minimal)\n" //
                        + "7bb,48,55,.5,0,0,,2161,6161,ff,DataRead.2161:21_61_#07 SOHP_value_of_group1\n" //
                        + "7bb,64,71,.5,0,0,,2161,6161,ff,DataRead.2161:21_61_#09 SOHP_value_of_group3\n" //
                        + "7bb,152,159,.5,0,0,,2161,6161,ff,DataRead.2161:21_61_#20 SOHP_value_of_group4\n" //
                        + "7bb,16,23,1,0,0,,2161,6161,ff,DataRead.2161:LID_61_données completes\n" //
                        + "7bb,96,111,.01,0,0,V,2103,6103,ff,DataRead.2103:21_03_#13_Maximum_Cell_voltage\n" //
                        + "7bb,112,127,.01,0,0,V,2103,6103,ff,DataRead.2103:21_03_#15_Minimum_Cell_voltage\n" //

                ;
        return (fieldDef);
    }


    static final public String framesString () {
        String frameDef = // Id, interval, sendingEcu
                ""
                        + "7BB,0,0,LBC\n"
;

        return(frameDef);
    }
}
