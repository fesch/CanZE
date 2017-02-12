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

public class EcuDiagEVC {

    void load () {

        String fieldDef1 =
                ""
                        +"7ec,0,7,1,0,0,,14ffffff,54,ff\n" // Reset DTC
                        +"7ec,0,23,1,0,0,,1902af,5902af,ff\n" // Query DTC

                        +"7ec,24,31,1,40,0,°C,222001,622001,ff,Battery Rack temperature\n" //
                        +"7ec,24,39,.02,0,0,%,222002,622002,ff,State Of Charge (SOC) HV battery\n" //
                        +"7ec,24,39,.01,0,0,km/h,222003,622003,ff,Vehicle speed\n" //
                        +"7ec,24,39,.01,0,0,V,222005,622005,ff,Battery voltage 14v\n" //
                        +"7ec,24,47,1,0,0,km,222006,622006,ff,Total vehicle distance\n" //
                        +"7ec,24,39,1,0,0,mV,22200B,62200B,ff,Accelerator pedal voltage _ track 1\n" //
                        +"7ec,24,39,1,0,0,mV,22200C,62200C,ff,Accelerator pedal voltage _ track 2\n" //
                        +"7ec,31,31,1,0,0,,22200E,62200E,ff,Key state,0:key off;1:key on\n" //
                        +"7ec,28,31,1,0,0,,22200F,62200F,ff,Brake pedal _ switches consolidation state,0:Not used;1:not pressed;2:pressed;3:Not Used;4:confirmed pressed\n" //
                        +"7ec,24,39,1,0,0,mV,222021,622021,ff,Sensors supply voltage 1\n" //
                        +"7ec,24,39,1,0,0,mV,222022,622022,ff,Sensors supply voltage 2\n" //
                        +"7ec,30,31,1,0,0,,222025,622025,ff,Brake pedal _ close active switch state,0:reserved;1:not pressed;2:pressed\n" //
                        +"7ec,30,31,1,0,0,,222026,622026,ff,Brake pedal _ open active switch state,0:reserved;1:not pressed;2:pressed\n" //
                        +"7ec,24,39,.00125,0,0,-,22202E,62202E,ff,Accelerator pedal position\n" //
                        +"7ec,24,31,1,0,0,km/h,222035,622035,ff,CCSL _ Cruise control speed setpoint\n" //
                        +"7ec,24,31,1,0,0,-,222036,622036,ff,Counter of inconsistencies between accelerator pedal and brake\n" //
                        +"7ec,24,39,1,0,0,mV,222039,622039,ff,CCSL _ Steering wheel push buttons voltage\n" //
                        +"7ec,31,31,1,0,0,,22203A,62203A,ff,Configuration _ Cruise control (option),0:without;1:with\n" //
                        +"7ec,31,31,1,0,0,,22203B,62203B,ff,Configuration _ Cruise control (OnOff button),0:without;1:with\n" //
                        +"7ec,31,31,1,0,0,,22203C,62203C,ff,Cruise control OnOff button state,0:off;1:on\n" //
                        +"7ec,31,31,1,0,0,,22203D,62203D,ff,Configuration _ Speed limiter (option),0:without;1:with\n" //
                        +"7ec,31,31,1,0,0,,22203E,62203E,ff,Configuration _ Speed limiter (OnOff button),0:without;1:with\n" //
                        +"7ec,31,31,1,0,0,,22203F,62203F,ff,Speed limiter OnOff button state,0:off;1:on\n" //
                        +"7ec,31,31,1,0,0,,222041,622041,ff,Configuration _ CCSL steering wheel push buttons,0:without;1:with\n" //
                        +"7ec,31,31,1,0,0,,222047,622047,ff,First start done,0:no;1:yes\n" //
                        +"7ec,29,31,1,0,0,,22204B,62204B,ff,CCSL _ Steering wheel push buttons state,0:unpressed;1:invalid voltage;2:open circuit;3:suspend button pressed;4:set - button pressed;5:set + button pressed;6:resume button pressed\n" //
                        +"7ec,29,31,1,0,0,,22204C,62204C,ff,CCSL _ Status,0:CC and SL off;1:SL on (active or over-speed);2:SL on (awaiting or supsended);3:SL requested and in failure;4:CC on (active);5:CC on (awaiting or supsended);6:CC requested and in failure;7:CC and SL not present\n" //
                        +"7ec,24,39,.01,0,0,km/h,222050,622050,ff,Displayed vehicle speed received on the CAN network\n" //
                        +"7ec,31,31,1,0,0,,222051,622051,ff,Displayed vehicle speed unit,0:km/h;1:mph\n" //
                        +"7ec,31,31,1,0,0,,22205B,62205B,ff,Limp home activation state,0:deactivated;1:activated\n" //
                        +"7ec,24,39,1,0,0,s,22206A,62206A,ff,Brake pedal _ duration of the close active switch blocked\n" //
                        +"7ec,24,39,1,0,0,s,22206B,62206B,ff,Brake pedal _ duration of the open active switch blocked\n" //
                        +"7ec,31,31,1,0,0,,22206E,62206E,ff,Configuration _ Brake pedal _ open active switch,0:without;1:with\n" //
                        +"7ec,24,31,1,0,0,s,22207A,62207A,ff,Maximum duration of resume button pressed\n" //
                        +"7ec,24,31,1,0,0,s,22207B,62207B,ff,Maximum duration of set + button pressed\n" //
                        +"7ec,24,31,1,0,0,s,22207C,62207C,ff,Maximum duration of set manus button pressed\n" //
                        +"7ec,24,31,1,0,0,s,22207D,62207D,ff,Maximum duration of suspend button pressed\n" //
                        +"7ec,24,31,1,0,0,-,22207E,62207E,ff,Maximum value of blocked button detection counter\n" //
                        +"7ec,31,31,1,0,0,,222166,622166,ff,External controls safety authorization flag,0:Not enabled;1:Enabled\n" //
                        +"7ec,24,55,.1,0,0,km,222181,622181,ff,Mileage recording\n" //
                        +"7ec,24,39,.01,0,0,km/h,222184,622184,ff,Requested speed setpoint for FSL function\n" //
                        +"7ec,29,31,1,1,0,,222C04,622C04,ff,Current gear engaged\n" //
                        +"7ec,31,31,1,0,0,,222C2C,622C2C,ff,Kick down state,0:no kick down;1:kick down\n" //
                        +"7ec,31,31,1,0,0,,222221,622221,ff,Brake pedal states validity indicator is in the limp home data status,0:deactivated;1:activated\n" //
                        +"7ec,31,31,1,0,0,,222223,622223,ff,Engine control request for cruise control abnormal deactivation,0:deactivated;1:activated\n" //
                        +"7ec,31,31,1,0,0,,222224,622224,ff,Engine control request for cruise control system deactivation,0:deactivated;1:activated\n" //
                        +"7ec,31,31,1,0,0,,222225,622225,ff,Vehicle speed received on the CAN network not available after a filtering time,0:deactivated;1:activated\n" //
                        +"7ec,31,31,1,0,0,,222226,622226,ff,Displayed vehicle speed received on the CAN network not available after a filtering time,0:deactivated;1:activated\n" //
                        +"7ec,31,31,1,0,0,,222227,622227,ff,Engine control request for speed limiter abnormal deactivation,0:deactivated;1:activated\n" //
                        +"7ec,31,31,1,0,0,,222228,622228,ff,Engine control request for speed limiter system deactivation,0:deactivated;1:activated\n" //
                        +"7ec,31,31,1,0,0,,22222B,62222B,ff,Automatic or manual parking brake detected,0:deactivated;1:activated\n" //
                        +"7ec,31,31,1,0,0,,22222F,62222F,ff,Authorization to connect cruise control and speed limiter options,0:Disabled;1:Enabled\n" //
                        +"7ec,31,31,1,0,0,,222230,622230,ff,ABS in regulation,0:No activated;1:Activated\n" //
                        +"7ec,31,31,1,0,0,,222231,622231,ff,Authorized ASR torque request,0:Not Autorized;1:Autorized\n" //
                        +"7ec,31,31,1,0,0,,222232,622232,ff,AYC in regulation,0:No activated;1:Activated\n" //
                        +"7ec,31,31,1,0,0,,222233,622233,ff,Request to turn the brake lights on (1) during regenerative braking,0:Brake light off;1:Brake light on\n" //
                        +"7ec,31,31,1,0,0,,222234,622234,ff,PTC accessories activation flag.,0:No activated;1:Activated\n" //
                        +"7ec,31,31,1,0,0,,222235,622235,ff,Flag indicating that a MSR torque request is being applied,0:No request of MSR torque;1:Request of MSR torque\n" //
                        +"7ec,31,31,1,0,0,,222236,622236,ff,Foot_up situation detected,0:No foot up detected;1:Foot up detected\n" //
                        +"7ec,31,31,1,0,0,,222237,622237,ff,Flag to limit the regenerative braking demand in case MSR function is faulty,0:No limitation;1:Limitation activated\n" //
                        +"7ec,29,31,1,0,0,,222238,622238,ff,Gear level position,0:transient;1:park;2:reverse;3:neutral;4:drive\n" //
                        +"7ec,24,39,.00125,0,0,-,222239,622239,ff,Powertrain setpoint (%) requested by the driver via the accelerator pedal\n" //
                        +"7ec,24,39,.5,32768,0,N,22223A,62223A,ff,Force demand at the wheels (output of the drivers' arbitration)\n" //
                        +"7ec,24,39,.03125,32768,0,N.m,22223B,62223B,ff,Electrical motor (EM) torque setpoint before AJS Correction action\n" //
                        +"7ec,24,39,.03125,12800,0,N.m,22223C,62223C,ff,ASR setpoint torque sent to torque arbitration\n" //
                        +"7ec,24,39,.03125,32768,0,N.m,22223D,62223D,ff,Torque correction calculated by AJCOR function\n" //
                        +"7ec,24,39,.03125,32768,0,N.m,22223E,62223E,ff,Final torque setpoint after AJMOD action\n" //
                        +"7ec,24,39,.5,32768,0,N,22223F,62223F,ff,Force demand derived from accelerator pedal position\n" //
                        +"7ec,24,39,.03125,12800,0,N.m,222241,622241,ff,MSR torque setting sent to torque arbitration\n" //
                        +"7ec,24,39,1,32768,0,rpm,222242,622242,ff,Filtered electric motor (EM) speed\n" //
                        +"7ec,24,39,.03125,32768,0,N.m,222243,622243,ff,Final effective torque request to the electric motor (EM)\n" //
                        +"7ec,28,31,1,0,0,-,222244,622244,ff,Lever position word\n" //
                        +"7ec,24,39,.03125,32768,0,N.m,222245,622245,ff,Limited electrical motor (EM) effective torque setpoint\n" //
                        +"7ec,24,39,.03125,32768,0,N.m,222246,622246,ff,Electrical motor (EM) maximum effective torque available\n" //
                        +"7ec,24,39,.03125,32768,0,N.m,222247,622247,ff,Minimum effective torque that can be requested to the electrical motor (EM)\n" //
                        +"7ec,24,39,.5,32768,0,N,222248,622248,ff,Force demand (at the wheels) when powertrain setpoint is equal to zero\n" //
                        +"7ec,24,39,.00062561,0,0,V,22224A,62224A,ff,Voltage measure of main relay (VBR)\n" //
                        +"7ec,30,31,1,0,0,,22224B,62224B,ff,Data used to validated a detection of braking,0:Nok;1:Ok;2:Failed 2\n" //
                        +"7ec,30,31,1,0,0,,22224D,62224D,ff,METS Vehicle speed validity flag,0:Nok;1:Ok;2:Failed 2 Degraded mode (Vehicle speed from engine speed);3:Failed 3 Degraded mode (ESP ECU absent but not confirmed absent; vehicle speed frozen)\n" //
                        +"7ec,30,31,1,0,0,,22224E,62224E,ff,Engine speed validity flag from ECC,0:Nok;1:Ok;2:Failed 2\n" //
                        +"7ec,30,31,1,0,0,,223001,623001,ff,Verlog state,1:Vehicle Not Locked;2:Vehicle Locked;3:Not used\n" //
                        +"7ec,30,31,1,0,0,,223006,623006,ff,HV Network discharge request,0:Not used;1:Discharge not requested;2:Discharge requested\n" //
                        +"7ec,31,31,1,0,0,,223007,623007,ff,HV Network discharge Feedback,0:Active Discharge Not In Progress;1:Active Discharge In Progress\n" //
                        +"7ec,24,39,.5,0,0,V,223008,623008,ff,HV PEB voltage measure\n" //
                        +"7ec,31,31,1,0,0,,223022,623022,ff,DCDC activation request,0:DCDC Off;1:DCDC On\n" //
                        +"7ec,24,31,.05,-240,0,V,223023,623023,ff,14V voltage request\n" //
                        +"7ec,24,31,.1,-40,0,V,223024,623024,ff,14V DCDC voltage measure\n" //
                        +"7ec,24,31,1,0,0,A,223025,623025,ff,14V DCDC current measure\n" //
                        +"7ec,24,31,.390625,0,0,%,223028,623028,ff,DCDC Load\n" //
                        +"7ec,25,31,1,0,0,%,223029,623029,ff,DCDC temperature indicator\n" //
                        +"7ec,30,31,1,0,0,,22302A,62302A,ff,General DCDC state,0:Standby;1:wake up but not activated;2:wake up and activated\n" //
                        +"7ec,24,39,.5,32768,0,A,223042,623042,ff,HV INV current measure\n" //
                        +"7ec,24,39,1,32768,0,N.m,223043,623043,ff,Torque request\n" //
                        +"7ec,24,39,10,32768,0,rpm,223045,623045,ff,Electrical Motor speed\n" //
                        +"7ec,24,39,.02,0,0,%,223047,623047,ff,INV temperature indicator\n" //
                        +"7ec,29,31,1,0,0,,223101,623101,ff,Charger Bloc state,0:Unvailable Value;1:SlowCharge and Diag State;2:Quick Charge and Diag State;3:Nissan Quick Charge and Diag State;4:Diagnosis State;5:No State;6:Not used\n" //
                        +"7ec,24,39,.5,0,0,V,223103,623103,ff,HV BCB voltage measure\n" //
                        +"7ec,31,31,1,0,0,,223104,623104,ff,Mains Network detection,0:Domestic Network is not present;1:Domestic Network is present\n" //
                        +"7ec,30,31,1,0,0,,223107,623107,ff,Charge current limitation,0:Unvailable value;1:10A Type;2:16A Type;3:Not used\n" //
                        +"7ec,31,31,1,0,0,,223108,623108,ff,Earth plug connection,0:No ground plug connection Failure;1:Ground Plug Connection Failure\n" //
                        +"7ec,25,31,1,0,0,°C,223109,623109,ff,BCB water temperature\n" //
                        +"7ec,24,31,1,0,0,%,22310A,62310A,ff,BCB internal temperature\n" //
                        +"7ec,30,31,1,0,0,,22310B,62310B,ff,interlock connection HV Bat, PEB,0:Unvailable Value;1:Open;2:Closed;3:Not used\n" //
                        +"7ec,30,31,1,0,0,,22310D,62310D,ff,Interlock JB cover,0:Unvailable Value;1:Open;2:Closed;3:Not used\n" //
                        +"7ec,30,31,1,0,0,,22310E,62310E,ff,Interlock JB Charge,0:Unvailable Value;1:Open;2:Closed;3:Not used\n" //
                        +"7ec,24,31,1,0,0,A,223110,623110,ff,Charge current measure\n" //
                        +"7ec,31,31,1,0,0,,223111,623111,ff,Quick charge start,0:Quick Charge Start Not Requested;1:Quick Charge Start Requested\n" //
                        +"7ec,31,31,1,0,0,,223112,623112,ff,Quick charge stop,0:Quick Charge Stop Not Requested;1:Quick Charge Stop Requested\n" //
                        +"7ec,24,31,1,0,0,A,223113,623113,ff,Quick charge current request\n" //
                        +"7ec,29,31,1,0,0,,223201,623201,ff,LBC mode request,0:Not Request;1:Slow Charge;2:Fast Charge;3:Normal Charge;4:Quick Drop;5:Not used;7:Unavailable value\n" //
                        +"7ec,29,31,1,0,0,,223202,623202,ff,LBC fonctionning state,0:Not used;1:Slow Charge;2:Fast Charge;3:Init;4:Transitory;5:Normal;6:BHV_QuickDrop_mode;7:Unavailable value\n" //
                        +"7ec,24,39,.5,0,0,V,223203,623203,ff,HV LBC voltage measure\n" //
                        +"7ec,24,39,1,32768,0,A,223204,623204,ff,HV LBC current measure\n" //
                        +"7ec,24,31,1,0,0,%,223206,623206,ff,State Of Health (SOH) HV battery\n" //
                        +"7ec,30,31,1,0,0,,223208,623208,ff,Caution flag state,0:C_HVBatteryNoDefault;1:C_HVBatteryLevel1Default;2:C_HVBatteryLevel2Default;3:C_HVBatteryDriving30sDefault\n" //
                        +"7ec,30,31,1,0,0,,223209,623209,ff,Failure flag state,0:Not used;1:No Default;2:Failure : Level2 Failure;3:Unavailable Value\n" //
                        +"7ec,24,31,.5,0,0,Kw,22320A,62320A,ff,Available discharge power\n" //
                        +"7ec,24,31,.5,0,0,Kw,22320B,62320B,ff,Available charge power\n" //
                        +"7ec,24,39,.005,0,0,kwh,22320C,62320C,ff,Available discharge Energy\n" //
                        +"7ec,39,39,1,0,0,,22320D,62320D,ff,Saving of the state of the interlocks.0,0:No default;1:Default present\n" //
                        +"7ec,38,38,1,0,0,,22320D,62320D,ff,Saving of the state of the interlocks.1,0:No default;1:Default present\n" //
                        +"7ec,37,37,1,0,0,,22320D,62320D,ff,Saving of the state of the interlocks.2,0:No default;1:Default present\n" //
                        +"7ec,36,36,1,0,0,,22320D,62320D,ff,Saving of the state of the interlocks.3,0:No default;1:Default present\n" //
                        +"7ec,35,35,1,0,0,,22320D,62320D,ff,Saving of the state of the interlocks.4,0:No default;1:Default present\n" //
                        +"7ec,34,34,1,0,0,,22320D,62320D,ff,Saving of the state of the interlocks.5,0:No default;1:Default present\n" //
                        +"7ec,33,33,1,0,0,,22320D,62320D,ff,Saving of the state of the interlocks.6,0:No default;1:Default present\n" //
                        +"7ec,32,32,1,0,0,,22320D,62320D,ff,Saving of the state of the interlocks.7,0:No default;1:Default present\n" //
                        +"7ec,31,31,1,0,0,,22320D,62320D,ff,Saving of the state of the interlocks.8,0:No default;1:Default present\n" //
                        +"7ec,24,31,.0625,0,0,V,223301,623301,ff,USM 14V Voltage measure (from CAN)\n" //
                        +"7ec,31,31,1,0,0,,223302,623302,ff,Wake Up Type,0:Customer Wake Up;1:System Wake Up\n" //
                        +"7ec,24,31,1,40,0,°C,223307,623307,ff,Heat water temperature\n" //
                        +"7ec,30,31,1,0,0,,223308,623308,ff,HV relay connection state,0:HV_Open;1:HV_Intermediate;2:HV_Connected\n" //
                        +"7ec,31,31,1,0,0,,22330B,62330B,ff,HV Battery relay A request,0:Request for opening the precharge relay A;1:Request for closing the precharge relay A\n" //
                        +"7ec,30,31,1,0,0,,22330C,62330C,ff,HV Network Insulation level,0:No InsulationFailure;1:Insulation Failure Level1;2:Insulation Failure Level2;3:Unavailable Insulation Detection\n" //
                        +"7ec,31,31,1,0,0,,22330D,62330D,ff,14V Unballasting request,0:Not Unballasting request;1:Unballasting request\n" //
                        +"7ec,31,31,1,0,0,,223310,623310,ff,14V Batterie charge request,0:Charge not requested;1:Charge requested\n" //
                        +"7ec,31,31,1,0,0,,223311,623311,ff,Heater switch state,0:Heater button deactivated;1:Heater button activated\n" //
                        +"7ec,31,31,1,0,0,,223312,623312,ff,Hot switch state,0:Hot heater button deactivated;1:Hot heater button activated\n" //
                        +"7ec,31,31,1,0,0,,223313,623313,ff,Cold switch state,0:Cold heater button deactivated;1:Cold heater button activated\n" //
                        +"7ec,31,31,1,0,0,,223315,623315,ff,QuickDrop lock 1st loop state,0:at least one battery lock open;1:both two battery locks are closed\n" //
                        +"7ec,31,31,1,0,0,,223316,623316,ff,QuickDrop lock 2nd loop state,0:at least one battery lock open;1:both two battery locks are closed\n" //
                        +"7ec,30,31,1,0,0,,223317,623317,ff,QuickDrop lock global state,0:BatteryLock open;1:Only one BatteryLock open;2:BatteryLock closed\n" //
                        +"7ec,24,31,1,0,0,-,223318,623318,ff,Driving pump Feedback\n" //
                        +"7ec,24,31,1,0,0,-,223319,623319,ff,Charge pump Feedback\n" //
                        +"7ec,24,31,1,0,0,Hz,22331A,62331A,ff,Heat pump Feedback\n" //
                        +"7ec,31,31,1,0,0,,22331B,62331B,ff,Cooling valve request,0:Request for opening the water gate of the charge mode;1:Request for closing the water gate of the charge mode\n" //
                        +"7ec,31,31,1,0,0,,22331D,62331D,ff,Pulser relay request,0:Request for opening the pulser relay;1:Request for closing the pulser relay\n" //
                        +"7ec,31,31,1,0,0,,22331E,62331E,ff,Heater request,0:Request for opening the heater relay;1:Request for closing the heater relay\n" //
                        +"7ec,31,31,1,0,0,,223322,623322,ff,Heater led state,0:Request for switching off the heater led;1:Request for switching on the heater led\n" //
                        +"7ec,31,31,1,0,0,,223325,623325,ff,Shiftlock request,0:Request for deactivate the shiftlock (P signal);1:Request for activate the shiftlock (P signal)\n" //
                        +"7ec,25,31,1,0,0,%,223326,623326,ff,Driving pump Request\n" //
                        +"7ec,25,31,1,0,0,%,223327,623327,ff,Charge pump Request\n" //
                        +"7ec,25,31,1,0,0,%,223328,623328,ff,Heat pump Request\n" //
                        +"7ec,31,31,1,0,0,,22332A,62332A,ff,Fan cabin status,0:Blower OK;1:Blower not OK\n" //
                        +"7ec,29,31,1,0,0,,22332B,62332B,ff,ETS state,0:ETS_Standby;1:ETS_WakeUp;2:ETS_Comfort;3:ETS_Driving;4:ETS_QuickDrop\n" //
                        +"7ec,29,31,1,0,0,,22332C,62332C,ff,Start refuse Reason,0:NO_REFUSE;1:REFUSE_PLUG;2:REFUSE_VERRLOGLOCKED;3:REFUSE_PRND_notN;4:REFUSE_PRND_notP;5:REFUSE_BATTERYLOCK;6:REFUSE_ENGINE\n" //
                        +"7ec,31,31,1,0,0,,22332D,62332D,ff,14V Network functionning Authorization,0:Not Autorized;1:Autorized\n" //
                        +"7ec,31,31,1,0,0,,22332E,62332E,ff,PowerTrain functionning Authorization,0:Not Autorized;1:Autorized\n" //
                        +"7ec,30,31,1,0,0,,22332F,62332F,ff,Thermal Comfort functionning Authorization,0:Not Authorised;1:PTC Authorised only;2:AC Authorised only;3:PTC and AC Authorised\n" //
                        +"7ec,30,31,1,0,0,,223330,623330,ff,Charge Management functionning Authorization,0:Not Autorized;1:Autorized;2:Zero Ampere mode Activation request;3:Quick Charge Activation request\n" //
                        +"7ec,30,31,1,0,0,,223331,623331,ff,PreHeating functionning Authorization,0:Not Authorised;1:PTC Authorised only;2:AC Authorised only;3:PTC and AC Authorised\n" //
                        +"7ec,30,31,1,0,0,,223332,623332,ff,Electrical Machine functionning Authorization,0:Not used;1:Inverter Off;2:Inverter On\n" //
                        +"7ec,30,31,1,0,0,,223335,623335,ff,1st level insulation Alert MMI request,0:Unavailable ;1:No ET Alert Isolation Level 1 Requested ;2:ET Alert Isolation Level 1 Requested ;3:not used \n" //
                        +"7ec,30,31,1,0,0,,223336,623336,ff,2nd level insulation Alert MMI request,0:Unavailable ;1:No ET Alert Isolation Level 2 Requested ;2:ET Alert Isolation Level 2 Requested ;3:not used \n" //
                        +"7ec,30,31,1,0,0,,223337,623337,ff,ETS Alert MMI request,0:Unavailable ;1:No ET Alert ETS Default Requested ;2:ET Alert ETS Default Requested ;3:not used \n" //
                        +"7ec,30,31,1,0,0,,223338,623338,ff,Motor Alert MMI request,0:Unavailable value;1:No ET Alert Motor Default Requested;2:ET Alert Motor Default Requested;3:Not used\n" //
                        +"7ec,30,31,1,0,0,,22333A,62333A,ff,Low Battery 14V MMI request,2:14VBattery Low Display Requested;1:No 14VBattery Low Display Requested;0:Unavailable value;3:Not used\n" //
                        +"7ec,31,31,1,0,0,,22333D,62333D,ff,Brake assist pump state,0:Pump Off;1:Pump On\n" //
                        +"7ec,24,31,1,0,0,%,223348,623348,ff,PTC request\n" //
                        +"7ec,24,55,1,0,0,-,223349,623349,ff,Time Counter for the driving WEP in Low Speed\n" //
                        +"7ec,24,55,1,0,0,-,22334A,62334A,ff,Time Counter for the driving WEP in Middle Speed\n" //
                        +"7ec,24,55,1,0,0,-,22334B,62334B,ff,Time Counter for the driving WEP in High Speed\n" //
                        +"7ec,24,55,1,0,0,-,22334D,62334D,ff,Time Counter for the load WEP in Low Speed\n" //
                        +"7ec,24,55,1,0,0,-,22334E,62334E,ff,Time Counter for the load WEP in Middle Speed\n" //
                        +"7ec,24,55,1,0,0,-,22334F,62334F,ff,Time Counter for the load WEP in High Speed\n" //
                        +"7ec,24,55,1,0,0,-,223351,623351,ff,Time Counter for the Cooling fan in Low Speed\n" //
                        +"7ec,24,55,1,0,0,-,223352,623352,ff,Time Counter for the Cooling fan in Middle Speed\n" //
                        +"7ec,24,55,1,0,0,-,223353,623353,ff,Time Counter for the Cooling fan in High Speed\n" //
                        +"7ec,24,31,1,0,0,%,22336A,62336A,ff,Maximale value of PEB temperature\n" //
                        +"7ec,24,31,1,0,0,%,22336B,62336B,ff,Maximale value of BCB temperature\n" //
                        +"7ec,24,39,1,0,0,-,22336C,62336C,ff,Quick Drop operating counter\n" //
                        +"7ec,24,47,1,0,0,s,22336D,62336D,ff,Data store memory use to store the activation time of the pump\n" //
                        +"7ec,24,39,.01,0,0,km/h,22336E,62336E,ff,Value of the vehicle speed before a safety level 2 hotreset\n" //
                        +"7ec,24,39,1,0,0,-,22336F,62336F,ff,Value of the safety monitoring status flags before the hotreset\n" //
                        +"7ec,24,39,1,32768,0,rpm,223370,623370,ff,Value of the electric motor speed before a safety level 2 hotreset\n" //
                        +"7ec,24,31,.005,0,0,Bar,223371,623371,ff,Pressure value max in the booster during failure confirmation time\n" //
                        +"7ec,24,31,.005,0,0,Bar,223372,623372,ff,Current pressure in the mastervac\n" //
                        +"7ec,30,31,1,0,0,,223373,623373,ff,The most critical state of the brake assist system between detected and confirmed states of a mastervac failure,0:unavailable;1:Vacuum pressure below 'Safety Threshold';2:Vacuum pressure between 'Safety Threshold' and 'Pump On Threshold' ;3:Vacuum pressure between 'Pump On Threshold' and 'Pump Off Threshold' \n" //
                        +"7ec,30,31,1,0,0,,223374,623374,ff,The most critical state of the Brake assist system when a failure has been confirmed,0:unavailable;1:Vacuum pressure below 'Safety Threshold';2:Vacuum pressure between 'Safety Threshold' and 'Pump On Threshold' ;3:Vacuum pressure between 'Pump On Threshold' and 'Pump Off Threshold' \n" //
                        +"7ec,24,39,.1,0,0,bar,223376,623376,ff,Measure of the freon pressure\n" //
                        +"7ec,30,31,1,0,0,,223379,623379,ff,Interlock of PTCs,0:Unvailable Value;1:Open;2:Closed;3:Not used\n" //
                        +"7ec,31,31,1,0,0,,22337D,62337D,ff,Context not safety datas microcontroller memorised carrier 1.0-diagnostic of NVMY,0:No default;1:Default present\n" //
                        +"7ec,30,30,1,0,0,,22337D,62337D,ff,Context not safety datas microcontroller memorised carrier 1.1\n" //
                        +"7ec,29,29,1,0,0,,22337D,62337D,ff,Context not safety datas microcontroller memorised carrier 1.2-Internal diag RELAY A,0:No default;1:Default present\n" //
                        +"7ec,28,28,1,0,0,,22337D,62337D,ff,Context not safety datas microcontroller memorised carrier 1.3-Internal diag RELAY P,0:No default;1:Default present\n" //
                        +"7ec,27,27,1,0,0,,22337D,62337D,ff,Context not safety datas microcontroller memorised carrier 1.4-Internal diag RELAY N,0:No default;1:Default present\n" //
                        +"7ec,26,26,1,0,0,,22337D,62337D,ff,Context not safety datas microcontroller memorised carrier 1.5-Internal diag Shift Lock,0:No default;1:Default present\n" //
                        +"7ec,25,25,1,0,0,,22337D,62337D,ff,Context not safety datas microcontroller memorised carrier 1.6-Internal diag Water Cooling Valve,0:No default;1:Default present\n" //
                        +"7ec,24,24,1,0,0,,22337D,62337D,ff,Context not safety datas microcontroller memorised carrier 1.7-Internal diag Stop Light Relay,0:No default;1:Default present\n" //
                        +"7ec,24,31,1,0,0,,22337F,62337F,ff,Context safety datas memorised in main microcontroller,0:No error;1:Header-byte error detected on MC;2:Checksum error detected on MC;3:Parity error detected on MC;4:Wrong configuration data sent by MU;5:Error in switch-off path check;6:Error in compatibility check on MC;7:Error in Standard ROM check;8:Error in Standard RAM check;9:Error in Level 2 ROM check;10:Error in Level 2 RAM check;11:FS-IST MU sent wrong question;12:FS-IST MU does not react to deliberately wrong answer;13:FS-IST MU does not decrease its FS-IST anti-bounce counter;14:Error in check of MU readiness;15:not used;16:PFM MU does not toggle PFM bit 0 in time;17:PFM MU does not toggle PFM bit 1 in time;18:PFM MU does not toggle PFM bit 2 in time;19:PFM MU does not toggle PFM bit 3 in time;20:PFM MU does not toggle PFM bit 4 in time;21:PFM MU does not toggle PFM bit 5 in time;22:PFM MU does not toggle PFM bit 6 in time;23:Invalid state transition or undefined state on MC;24:PFM MU does not react to deliberately wrong PFM bit 0;25:PFM MU does not react to deliberately wrong PFM bit 1;26:PFM MU does not react to deliberately wrong PFM bit 2;27:PFM MU does not react to deliberately wrong PFM bit 3;28:PFM MU does not react to deliberately wrong PFM bit 4;29:PFM MU does not react to deliberately wrong PFM bit 5;30:PFM MU does not react to deliberately wrong PFM bit 6;31:Not used\n" //
                        +"7ec,24,31,1,0,0,-,223381,623381,ff,Context safty datas memorised in Safety microcontroller\n" //
                        +"7ec,24,31,1,0,0,-,223384,623384,ff,Mission counter without driver door transition\n" //
                        +"7ec,24,47,1,0,0,s,223386,623386,ff,Life time of the pump read from the VPM memory via LIN\n" //
                        +"7ec,28,31,1,0,0,,223387,623387,ff,Brake pedal state during diagnostic confirmation,0:Not used;1:not pressed;2:pressed;3:Not Used;4:confirmed pressed\n" //
                        +"7ec,31,31,1,0,0,,223388,623388,ff,Bcb Activation failed Flag : charge or pre_heating not authorized any more,0:no failure ;1:failure\n" //
                        +"7ec,31,31,1,0,0,,223389,623389,ff,Request to deactivate BCB Automatic wake up on network presence,0:desactivated;1:activated\n" //
                        +"7ec,31,31,1,0,0,,22338A,62338A,ff,Customer WakeUp flag after internal processing,0:No Customer WakeUp;1:Customer WakeUp\n" //
                        +"7ec,31,31,1,0,0,,22338B,62338B,ff,Event type technical WakeUp flag after internal processing,0:No Event Technical WakeUp;1:Event technical WakeUp\n" //
                        +"7ec,31,31,1,0,0,,22338C,62338C,ff,ComfortMode failed and ended after confirmed non HV relays connexion,0:Confort mode ok ;1:Confort mode failed\n" //
                        +"7ec,31,31,1,0,0,,22338D,62338D,ff,Comfort mode disabled : _ Exit after no activity _ HV relays do not connect,0:Allowed;1:Not Allowed\n" //
                        +"7ec,31,31,1,0,0,,22338E,62338E,ff,Indicates the status of the driver door of the vehicle,0:Closed;1:Opened\n" //
                        +"7ec,31,31,1,0,0,,22338F,62338F,ff,Driving Restart flag (after EVC reset),0:desactivated;1:activated\n" //
                        +"7ec,31,31,1,0,0,,223390,623390,ff,EVC reset flag for HV connexion after EVC reset management,0:desactivated;1:activated\n" //
                        +"7ec,31,31,1,0,0,,223392,623392,ff,400 V Battery Cooling Needed flag,0:No request;1:Request\n" //
                        +"7ec,31,31,1,0,0,,223393,623393,ff,HV Network Power cut request,0:no request;1:power cut request\n" //
                        +"7ec,31,31,1,0,0,,223394,623394,ff,Flag for LBC Actual status(= to 1 when LBC status is Driving ). AfterK_BHVStateConfirmed_status processing,0:Not ready for driving;1:Ready for driving\n" //
                        +"7ec,31,31,1,0,0,,223395,623395,ff,Internal condition for Zero Ampere mode Autorisation,0:Allowed;1:Not Allowed\n" //
                        +"7ec,31,31,1,0,0,,223397,623397,ff,Gaz Heater Activation Authorisation flag,0:Not Autorized;1:Autorized\n" //
                        +"7ec,31,31,1,0,0,,223398,623398,ff,HV Battery cooling Authorisation flag,0:Not Autorized;1:Autorized\n" //
                        +"7ec,31,31,1,0,0,,223399,623399,ff,Quick Drop flag memorisation (EEPROM memorisation),0:Not Memorized;1:Memorized\n" //
                        +"7ec,31,31,1,0,0,,22339A,62339A,ff,No Major failure .rebuild from : _ ETS Diag Flags, Crash status and EmergencyEngineStop request from BCM,0:No failure;1:failure\n" //
                        +"7ec,31,31,1,0,0,,22339B,62339B,ff,Front passenger door state from CANV,0:Closed;1:Opened\n" //
                        +"7ec,31,31,1,0,0,,22339C,62339C,ff,PEB status after internal processing,0:NOK;1:OK for driving\n" //
                        +"7ec,31,31,1,0,0,,22339D,62339D,ff,Charging Plug detected as present by the Charger.processed,0:No charging plug;1:Charging plug detected\n" //
                        +"7ec,31,31,1,0,0,,22339E,62339E,ff,Quick drop condition is active,0:No Active;1:Active\n" //
                        +"7ec,31,31,1,0,0,,22339F,62339F,ff,Soc authorisation for pre_heating authorisation during charge,0:Not Autorized;1:Autorized\n" //
                        +"7ec,31,31,1,0,0,,2233A1,6233A1,ff,BCM absent and vehicle stopped flag : driving interruptiion,0:Not Autorized;1:Autorized\n" //
                        +"7ec,31,31,1,0,0,,2233A2,6233A2,ff,Button AC from the cluster (X61) or AC request from CAREG (L38),0:no AC requested ;1:AC requested\n" //
                        +"7ec,31,31,1,0,0,,2233A3,6233A3,ff,For L38 vehicule, this is the demand from the CAREG to start a PreHeating.,0:no PreHeat demand from CAREG  ;1:PreHeat demand from CAREG\n" //
                        +"7ec,24,31,2,0,0,%,2233A4,6233A4,ff,Measure of the speed of the cabin blower\n" //
                        +"7ec,30,31,1,0,0,,2233A5,6233A5,ff,Compressor configuration,0:no compressor;1:compressor Denso ES27;2:Not used;3:Not Used\n" //
                        +"7ec,24,31,1,0,0,,2233A8,6233A8,ff,Request of FAN activation from the thermal comfort,0:no FAN requested;1:(61: low, 38: very low) speed fan request;2:61: not used, 38: low speed fan request;3:high speed fan request\n" //
                        +"7ec,30,31,1,0,0,,2233A9,6233A9,ff,Gasoline heater configuration,0:no heater;1:Eberspächer Hydronic 2;2:not used;3:Not used\n" //
                        +"7ec,31,31,1,0,0,,2233AA,6233AA,ff,Minimum gasoline Level indicator in the tank supplying the heater,0:minimum level not reached ;1:minimum level reached\n" //
                        +"7ec,29,31,1,0,0,,2233AB,6233AB,ff,State of PreHeating to display,0:PreHeating OFF;1:PreHeating requested for H1;2:PreHeating requested for H2;3:PreHeating Not Available;4:PreHeating in progress;5:PreHeating Finished OK;6:PreHeating Finished not OK;7:not used\n" //
                        +"7ec,30,31,1,0,0,,2233AC,6233AC,ff,Status of pre_heating sent to Manage ETS,0:pre-heating available;1:pre-heating running;2:pre-heating refused;3:pre-heating waiting\n" //
                        +"7ec,31,31,1,0,0,,2233AD,6233AD,ff,Computation of PTC defaults.0,0:No default;1:Default present\n" //
                        +"7ec,30,30,1,0,0,,2233AD,6233AD,ff,Computation of PTC defaults.1,0:No default;1:Default present\n" //
                        +"7ec,29,29,1,0,0,,2233AD,6233AD,ff,Computation of PTC defaults.2,0:No default;1:Default present\n" //
                        +"7ec,28,28,1,0,0,,2233AD,6233AD,ff,Computation of PTC defaults.3,0:No default;1:Default present\n" //
                        +"7ec,27,27,1,0,0,,2233AD,6233AD,ff,Computation of PTC defaults.4,0:No default;1:Default present\n" //
                        +"7ec,26,26,1,0,0,,2233AD,6233AD,ff,Computation of PTC defaults.5,0:No default;1:Default present\n" //
                        +"7ec,24,39,1,2730,0,°C,2233AF,6233AF,ff,temperature used to prevent the evaporator from freezing within the heating venting and air conditionner system (HVAC)\n" //
                        +"7ec,24,39,.1,2730,0,°C,2233B0,6233B0,ff,evaporator temperature setpoint for compressor control\n" //
                        +"7ec,24,31,1,40,0,°C,2233B1,6233B1,ff,CAN signal for external temperature of the vehicle.\n" //
                        +"7ec,24,31,1,40,0,°C,2233B3,6233B3,ff,water temperature setpoint for PTC control\n" //
                        +"7ec,31,31,1,0,0,,2233B5,6233B5,ff,HV Relays Connection Autorisation synthesis flag,0:Not Autorized;1:Autorized\n" //
                        +"7ec,30,31,1,0,0,,2233B6,6233B6,ff,Charge Management status for ETS,0:NoActiveMode;1:SlowCharge;2:ZAMode;3:QuickCharge\n" //
                        +"7ec,30,31,1,0,0,,2233B7,6233B7,ff,Technical WakeUp type,0:not used;1:Periodic technical WakeUo;2:Event technical WakeUp;3:Not used\n" //
                        +"7ec,30,31,1,0,0,,2233B8,6233B8,ff,Confirmation of the state of the battery from ManageHVBattery,0:NoState_Confirmed;1:SlowCharge_Confirmed;2:QuickCharge_Confirmed;3:Driving_Confirmed\n" //
                        +"7ec,30,31,1,0,0,,2233B9,6233B9,ff,Start_up Authorisation _ Anti_kick off management,0:CrankingNotAllowed Start up not allowed by anti-kick off;1:CrankingAllowed Start_up allowed by anti-kick off\n" //
                        +"7ec,31,31,1,0,0,,2233BB,6233BB,ff,Signal coming from Manage HVBattery indicating if the battery need to be charged or to be cooled,0:NoHVBChargeNeeded;1:HVBChargeRequest\n" //
                        +"7ec,30,31,1,0,0,,2233BD,6233BD,ff,Plug status validity flag,0:Nok;1:Ok;2:failed 2\n" //
                        +"7ec,30,31,1,0,0,,2233BE,6233BE,ff,LBC sleep mode request,0:Normal Sleep;1:14 Supervision;2:Quick Drop\n" //
                        +"7ec,29,31,1,0,0,,2233BF,6233BF,ff,HV connection request,0:Disfunctional disconnexion request;1:Nominal disconnexion request;2:Dysfunctional connexion request;3:Nominal connexion request;4:Not Used;5:Emergency disconnexion request\n" //
                        +"7ec,30,31,1,0,0,,2233C1,6233C1,ff,K_StartingMode_BCM_req input validity state,0:BCM absent or confirmed unavailable value for K_StartingMode_BCM_req;1:K_StartingMode_BCM_req available and valid;2:Invalid value for K_StartingMode_BCM_req\n" //
                        +"7ec,30,31,1,0,0,,2233C2,6233C2,ff,IVLD of the signal Pct_UserSOC_bhv_est,0:value invalid;1:value valid and available;2:Invalid value 1;3:Invalid value 2;4:Invalid value 3\n" //
                        +"7ec,29,31,1,0,0,,2233C3,6233C3,ff,PEB status from CAN ETS,0:Starting up or power off;2:stand-by;4:power on;7:invalid\n" //
                        +"7ec,30,31,1,0,0,,2233C4,6233C4,ff,Preheating requested by the vehicle, signal coming from CAN, not checked : Boolean maintained value for RH1 Event for RH2 (3 states),0:no preheating requested;1:preheating requested\n" //
                        +"7ec,30,31,1,0,0,,2233C5,6233C5,ff,ETS System Sleeping authorisation synthesis,0:Not used;1:Refuse to sleep;2:Ready to sleep;3:not used\n" //
                        +"7ec,30,31,1,0,0,,2233C6,6233C6,ff,Electrical Motor state requested bu user and build by the BCM, signal coming from CAN, not checked,0:Motor stop requested;1:Unavailable;2:+APC requested;3:Starting requested\n" //
                        +"7ec,29,31,1,0,0,,2233C7,6233C7,ff,Driver requests synthesis,0:Sleep request;1:Wake up request;2:Transient state before sleep;3:Life on Board request (HV network request);4:Driving request\n" //
                        +"7ec,30,31,1,0,0,,2233C8,6233C8,ff,ETS Organs on CAN_E Wake up or Sleep request,0:GotoSleep;1:Not used;2:not used;3:WakeUp\n" //
                        +"7ec,31,31,1,0,0,,2233C9,6233C9,ff,High level failure in the BCB,0:No failure;1:High level failure in the BCB\n" //
                        +"7ec,31,31,1,0,0,,2233CA,6233CA,ff,Low level failure in the BCB.,0:No failure;1:Low level failure in the BCB\n" //
                        +"7ec,31,31,1,0,0,,2233CC,6233CC,ff,Request for the Gasoline heater relay,0:no request ;1:request\n" //
                        +"7ec,31,31,1,0,0,,2233CD,6233CD,ff,Request of Hvac power Relay,0:no request ;1:request\n" //
                        +"7ec,31,31,1,0,0,,2233CE,6233CE,ff,Software failure detected in the quick charge stateflow,0:No failure;1:Failure detected\n" //
                        +"7ec,31,31,1,0,0,,2233CF,6233CF,ff,Specific failure during the quick charge,0:No failure;1:Failure detected\n" //
                        +"7ec,31,31,1,0,0,,2233D0,6233D0,ff,This signal indicates that a quick drop is finished,0:No quick-drop has occured;1:A quick-drop has occured\n" //
                        +"7ec,31,31,1,0,0,,2233D1,6233D1,ff,Specific failure during slow charge or zero ampere mode,0:No failure;1:Failure detected\n" //
                        +"7ec,103,103,1,0,0,,2233D3,6233D3,ff,BPLC spot presence status to be read by diag tool.0,0:spot not present;1:spot present\n" //
                        +"7ec,95,95,1,0,0,,2233D3,6233D3,ff,BPLC spot presence status to be read by diag tool.1,0:spot not present;1:spot present\n" //
                        +"7ec,87,87,1,0,0,,2233D3,6233D3,ff,BPLC spot presence status to be read by diag tool.2,0:spot not present;1:spot present\n" //
                        +"7ec,79,79,1,0,0,,2233D3,6233D3,ff,BPLC spot presence status to be read by diag tool.3,0:spot not present;1:spot present\n" //
                        +"7ec,71,71,1,0,0,,2233D3,6233D3,ff,BPLC spot presence status to be read by diag tool.4,0:spot not present;1:spot present\n" //
                        +"7ec,63,63,1,0,0,,2233D3,6233D3,ff,BPLC spot presence status to be read by diag tool.5,0:spot not present;1:spot present\n" //
                        +"7ec,55,55,1,0,0,,2233D3,6233D3,ff,BPLC spot presence status to be read by diag tool.6,0:spot not present;1:spot present\n" //
                        +"7ec,47,47,1,0,0,,2233D3,6233D3,ff,BPLC spot presence status to be read by diag tool.7,0:spot not present;1:spot present\n" //
                        +"7ec,39,39,1,0,0,,2233D3,6233D3,ff,BPLC spot presence status to be read by diag tool.8,0:spot not present;1:spot present\n" //
                        +"7ec,31,31,1,0,0,,2233D3,6233D3,ff,BPLC spot presence status to be read by diag tool.9,0:spot not present;1:spot present\n" //
                        +"7ec,240,263,1,0,0,km,2233D4,6233D4,ff,Total travelled distance to be read by diag tool.0\n" //
                        +"7ec,216,239,1,0,0,km,2233D4,6233D4,ff,Total travelled distance to be read by diag tool.1\n" //
                        +"7ec,192,215,1,0,0,km,2233D4,6233D4,ff,Total travelled distance to be read by diag tool.2\n" //
                        +"7ec,168,191,1,0,0,km,2233D4,6233D4,ff,Total travelled distance to be read by diag tool.3\n" //
                        +"7ec,144,167,1,0,0,km,2233D4,6233D4,ff,Total travelled distance to be read by diag tool.4\n" //
                        +"7ec,120,143,1,0,0,km,2233D4,6233D4,ff,Total travelled distance to be read by diag tool.5\n" //
                        +"7ec,96,119,1,0,0,km,2233D4,6233D4,ff,Total travelled distance to be read by diag tool.6\n" //
                        +"7ec,72,95,1,0,0,km,2233D4,6233D4,ff,Total travelled distance to be read by diag tool.7\n" //
                        +"7ec,48,71,1,0,0,km,2233D4,6233D4,ff,Total travelled distance to be read by diag tool.8\n" //
                        +"7ec,24,47,1,0,0,km,2233D4,6233D4,ff,Total travelled distance to be read by diag tool.9\n" //
                        +"7ec,102,103,1,0,0,,2233D5,6233D5,ff,Reason of end of charge to be read by diag tool.0,0:Nominal End of Charge;1:Unplugging;2:Power Cut;3:Charge Failure\n" //
                        +"7ec,94,95,1,0,0,,2233D5,6233D5,ff,Reason of end of charge to be read by diag tool.1,0:Nominal End of Charge;1:Unplugging;2:Power Cut;3:Charge Failure\n" //
                        +"7ec,86,87,1,0,0,,2233D5,6233D5,ff,Reason of end of charge to be read by diag tool.2,0:Nominal End of Charge;1:Unplugging;2:Power Cut;3:Charge Failure\n" //
                        +"7ec,78,79,1,0,0,,2233D5,6233D5,ff,Reason of end of charge to be read by diag tool.3,0:Nominal End of Charge;1:Unplugging;2:Power Cut;3:Charge Failure\n" //
                        +"7ec,70,71,1,0,0,,2233D5,6233D5,ff,Reason of end of charge to be read by diag tool.4,0:Nominal End of Charge;1:Unplugging;2:Power Cut;3:Charge Failure\n" //
                        +"7ec,62,63,1,0,0,,2233D5,6233D5,ff,Reason of end of charge to be read by diag tool.5,0:Nominal End of Charge;1:Unplugging;2:Power Cut;3:Charge Failure\n" //
                        +"7ec,54,55,1,0,0,,2233D5,6233D5,ff,Reason of end of charge to be read by diag tool.6,0:Nominal End of Charge;1:Unplugging;2:Power Cut;3:Charge Failure\n" //
                        +"7ec,46,47,1,0,0,,2233D5,6233D5,ff,Reason of end of charge to be read by diag tool.7,0:Nominal End of Charge;1:Unplugging;2:Power Cut;3:Charge Failure\n" //
                        +"7ec,38,39,1,0,0,,2233D5,6233D5,ff,Reason of end of charge to be read by diag tool.8,0:Nominal End of Charge;1:Unplugging;2:Power Cut;3:Charge Failure\n" //
                        +"7ec,30,31,1,0,0,,2233D5,6233D5,ff,Reason of end of charge to be read by diag tool.9,0:Nominal End of Charge;1:Unplugging;2:Power Cut;3:Charge Failure\n" //
                        +"7ec,96,103,1,0,0,,2233D6,6233D6,ff,Type of charge to be read by diag tool.0,0:Slow Charge;1:Quick Charge;2:Quick Drop;3:Unknown;4:Charge scheduled\n" //
                        +"7ec,88,95,1,0,0,,2233D6,6233D6,ff,Type of charge to be read by diag tool.1,0:Slow Charge;1:Quick Charge;2:Quick Drop;3:Unknown;4:Charge scheduled\n" //
                        +"7ec,80,87,1,0,0,,2233D6,6233D6,ff,Type of charge to be read by diag tool.2,0:Slow Charge;1:Quick Charge;2:Quick Drop;3:Unknown;4:Charge scheduled\n" //
                        +"7ec,72,79,1,0,0,,2233D6,6233D6,ff,Type of charge to be read by diag tool.3,0:Slow Charge;1:Quick Charge;2:Quick Drop;3:Unknown;4:Charge scheduled\n" //
                        +"7ec,64,71,1,0,0,,2233D6,6233D6,ff,Type of charge to be read by diag tool.4,0:Slow Charge;1:Quick Charge;2:Quick Drop;3:Unknown;4:Charge scheduled\n" //
                        +"7ec,56,63,1,0,0,,2233D6,6233D6,ff,Type of charge to be read by diag tool.5,0:Slow Charge;1:Quick Charge;2:Quick Drop;3:Unknown;4:Charge scheduled\n" //
                        +"7ec,48,55,1,0,0,,2233D6,6233D6,ff,Type of charge to be read by diag tool.6,0:Slow Charge;1:Quick Charge;2:Quick Drop;3:Unknown;4:Charge scheduled\n" //
                        +"7ec,40,47,1,0,0,,2233D6,6233D6,ff,Type of charge to be read by diag tool.7,0:Slow Charge;1:Quick Charge;2:Quick Drop;3:Unknown;4:Charge scheduled\n" //
                        +"7ec,32,39,1,0,0,,2233D6,6233D6,ff,Type of charge to be read by diag tool.8,0:Slow Charge;1:Quick Charge;2:Quick Drop;3:Unknown;4:Charge scheduled\n" //
                        +"7ec,24,31,1,0,0,,2233D6,6233D6,ff,Type of charge to be read by diag tool.9,0:Slow Charge;1:Quick Charge;2:Quick Drop;3:Unknown;4:Charge scheduled\n" //
                        +"7ec,168,183,.2,0,0,%,2233D7,6233D7,ff,State of charge (UserSOC) to be read by diag tool.0\n" //
                        +"7ec,152,167,.2,0,0,%,2233D7,6233D7,ff,State of charge (UserSOC) to be read by diag tool.1\n" //
                        +"7ec,136,151,.2,0,0,%,2233D7,6233D7,ff,State of charge (UserSOC) to be read by diag tool.2\n" //
                        +"7ec,120,135,.2,0,0,%,2233D7,6233D7,ff,State of charge (UserSOC) to be read by diag tool.3\n" //
                        +"7ec,104,119,.2,0,0,%,2233D7,6233D7,ff,State of charge (UserSOC) to be read by diag tool.4\n" //
                        +"7ec,88,103,.2,0,0,%,2233D7,6233D7,ff,State of charge (UserSOC) to be read by diag tool.5\n" //
                        +"7ec,72,87,.2,0,0,%,2233D7,6233D7,ff,State of charge (UserSOC) to be read by diag tool.6\n" //
                        +"7ec,56,71,.2,0,0,%,2233D7,6233D7,ff,State of charge (UserSOC) to be read by diag tool.7\n" //
                        +"7ec,40,55,.2,0,0,%,2233D7,6233D7,ff,State of charge (UserSOC) to be read by diag tool.8\n" //
                        +"7ec,24,39,.2,0,0,%,2233D7,6233D7,ff,State of charge (UserSOC) to be read by diag tool.9\n" //
                        +"7ec,96,103,1,40,0,°C,2233D8,6233D8,ff,Battery temperature to be read by diag tool.0\n" //
                        +"7ec,88,95,1,40,0,°C,2233D8,6233D8,ff,Battery temperature to be read by diag tool.1\n" //
                        +"7ec,80,87,1,40,0,°C,2233D8,6233D8,ff,Battery temperature to be read by diag tool.2\n" //
                        +"7ec,72,79,1,40,0,°C,2233D8,6233D8,ff,Battery temperature to be read by diag tool.3\n" //
                        +"7ec,64,71,1,40,0,°C,2233D8,6233D8,ff,Battery temperature to be read by diag tool.4\n" //
                        +"7ec,56,63,1,40,0,°C,2233D8,6233D8,ff,Battery temperature to be read by diag tool.5\n" //
                        +"7ec,48,55,1,40,0,°C,2233D8,6233D8,ff,Battery temperature to be read by diag tool.6\n" //
                        +"7ec,40,47,1,40,0,°C,2233D8,6233D8,ff,Battery temperature to be read by diag tool.7\n" //
                        +"7ec,32,39,1,40,0,°C,2233D8,6233D8,ff,Battery temperature to be read by diag tool.8\n" //
                        +"7ec,24,31,1,40,0,°C,2233D8,6233D8,ff,Battery temperature to be read by diag tool.9\n" //
                        +"7ec,168,183,1,0,0,min,2233D9,6233D9,ff,Total duration of the charge sequence to be read by diag tool.0\n" //
                        +"7ec,152,167,1,0,0,min,2233D9,6233D9,ff,Total duration of the charge sequence to be read by diag tool.1\n" //
                        +"7ec,136,151,1,0,0,min,2233D9,6233D9,ff,Total duration of the charge sequence to be read by diag tool.2\n" //
                        +"7ec,120,135,1,0,0,min,2233D9,6233D9,ff,Total duration of the charge sequence to be read by diag tool.3\n" //
                        +"7ec,104,119,1,0,0,min,2233D9,6233D9,ff,Total duration of the charge sequence to be read by diag tool.4\n" //
                        +"7ec,88,103,1,0,0,min,2233D9,6233D9,ff,Total duration of the charge sequence to be read by diag tool.5\n" //
                        +"7ec,72,87,1,0,0,min,2233D9,6233D9,ff,Total duration of the charge sequence to be read by diag tool.6\n" //
                        +"7ec,56,71,1,0,0,min,2233D9,6233D9,ff,Total duration of the charge sequence to be read by diag tool.7\n" //
                        +"7ec,40,55,1,0,0,min,2233D9,6233D9,ff,Total duration of the charge sequence to be read by diag tool.8\n" //
                        +"7ec,24,39,1,0,0,min,2233D9,6233D9,ff,Total duration of the charge sequence to be read by diag tool.9\n" //
                        +"7ec,24,55,.0002,0,0,km,2233E1,6233E1,ff,Travelled distance cumulated by EVC\n" //
                        +"7ec,29,31,1,0,0,,2233E2,6233E2,ff,Flow that requests the BCB to make available certain functionalities,0:init;1:slow charge or ZA or Diag;2:Quick charge or Diag;3:Nissan quick charge or Diag;4:Diag;5:Power off;6:Not used;7:unavailable\n" //
                        +"7ec,28,31,1,0,0,,2233E3,6233E3,ff,Status of Manage HV Battery Master Stateflow,0:NoRequest;1:BHV_SlowCharge requested;2:BHV_QuickCharge requested;3:BHV_Normal requested;4:BHV_QuickDrop requested;5:BHV_SlowCharge confirmed;6:BHV_Normal confirmed;7:BHV_QuickCharge confirmed;8:BHV_QuickDrop_Confirmed\n" //
                        +"7ec,30,31,1,0,0,,2233E4,6233E4,ff,Cooling Management status answering to cooling request from Manage ETS,0:Cooling ON;1:Cooling ON Downgraded;2:Cooling OFF;3:Cooling OFF Failure\n" //
                        +"7ec,28,31,1,0,0,,2233E5,6233E5,ff,Charge WEP Feedback Diag,0:No defined value;1:PWM between 0% and 2%;2:Warning pump;3:Pump to zero RPM;4:Pump can not read Input;5:Normal operation (750 - 4700 rpm);6:Over or Undervoltage;7:Fault Pump;8:PWM between 92 and 94;9:PWM between 96 and 100\n" //
                        +"7ec,28,31,1,0,0,,2233E6,6233E6,ff,Driving WEP Feedback Diag,0:No defined value;1:PWM between 0% and 2%;2:Warning pump;3:Pump to zero RPM;4:Pump can not read Input;5:Normal operation (750 - 4700 rpm);6:Over or Undervoltage;7:Fault Pump;8:PWM between 92 and 94;9:PWM between 96 and 100\n" //
                        +"7ec,24,39,1,0,0,,2233E7,6233E7,ff,Code Failure of All the sensors of temperature,0:No sensor failure;1:BCB Ambient temperature sensor;2:BCB Water temperature sensor;4:External temperature sensor;8:Inverter Water temperature sensor;16:DCDC Water temperature sensor;32:Inverter Ambient temperature sensor;64:EM Ambient temperature sensor;128:BHV temperature sensor;256:DCDC Ambient temperature;512:BHV temp max sensor;1024:BCB temp max RCY (redundant temp)\n" //
                        +"7ec,30,31,1,0,0,,2233E8,6233E8,ff,JB_AC Interlock status,0:Unavailable Value;1:Interlock Open;2:Interlock Close;3:Not used\n" //
                        +"7ec,30,31,1,0,0,,2233E9,6233E9,ff,JB_PTC Interlock status,0:Unavailable Value;1:Interlock Open;2:Interlock Close;3:Not used\n" //
                        +"7ec,29,31,1,0,0,,2233EA,6233EA,ff,CAN signal for the status of the plugs connection,0:No plug connected;2:Plug connected, no button pressed;4:1 Plug connected, button pressed;6:2 Plugs connected;7:Unavaliable Value\n" //
                        +"7ec,29,31,1,0,0,,2233EC,6233EC,ff,Different states of CM master stateflow,0:BCB_Awake;1:BCB_Normal;2:BCB_Standby;3:BCB_Slow_Charge;4:BCB_Quick_Charge;5:BCB_Nissan_Quick_Charge;6:BCB_Zero_Ampere\n" //
                        +"7ec,29,31,1,0,0,,2233ED,6233ED,ff,Different states of quick charge management stateflow,0:NoState;1:BCB_QuickCharge_ICId;2:BCB_QuickCharge_ICSftL;3:BCB_QuickCharge_ICRelClos;4:BCB_QuickCharge_ICStrt;5:BCB_QuickCharge_InProgress;6:BCB_QuickCharge_NominalExit;7:BCB_QuickCharge_DysfExit\n" //
                        +"7ec,29,31,1,0,0,,2233EF,6233EF,ff,Different states of slow charge management stateflow,0:NoState;1:BCB_SlowCharge_InitCheck;2:BCB_SlowCharge_InProgress;3:BCB_SlowCharge_NominalExit;4:BCB_SlowCharge_DysfunctExit\n" //
                        +"7ec,30,31,1,0,0,,2233F0,6233F0,ff,Different states of the stand_by management stateflow,0:NoState;1:BCB_Standby_StandByRequest;2:BCB_Standby_StandByConfirmed;3:BCB_Standby_PossibleRestart\n" //
                        +"7ec,24,39,.02,0,0,%,2233F1,6233F1,ff,Electrical Machine normalised temperature\n" //
                        +"7ec,24,31,1,40,0,°C,2233F2,6233F2,ff,High Voltage Battery maximal Temperature\n" //
                        +"7ec,24,39,.1,10240,0,N.m,2233F3,6233F3,ff,Maximal torque without unballasting\n" //
                        +"7ec,24,39,.1,10240,0,N.m,2233F4,6233F4,ff,Minimal torque\n" //
                        +"7ec,24,31,1,40,0,°C,2233F5,6233F5,ff,Cooling Temperature dcdc mesured\n" //
                        +"7ec,24,31,1,40,0,°C,2233F6,6233F6,ff,Temperature of the inverter given by PEB (CAN ETS)\n" //
                        +"7ec,24,47,1,0,0,-,2233F7,6233F7,ff,Counter of Efan speed on Low and very low speed\n" //
                        +"7ec,29,31,1,0,0,,2233F8,6233F8,ff,Different states of the Zero Ampere mode management stateflow.,0:NoState;1:BCB_ZeroAmpere_InitCheck;2:BCB_ZeroAmpere_InProgress;3:BCB_ZeroAmpere_NominalExit;4:BCB_ZeroAmpere_DysfExit\n" //
                        +"7ec,30,31,1,0,0,,2233F9,6233F9,ff,Primary ignition supply state, signal coming from CAN, not checked,0:Not used;1:+ACC off;2:+ACC on;3:not used\n" //
                        +"7ec,31,31,1,0,0,,2233FA,6233FA,ff,Pedal pressed flag internal processing,0:pedal not pressed;1:pedal pressed\n" //
                        +"7ec,31,31,1,0,0,,2233FB,6233FB,ff,Preheating requested by the vehicle. Processed,0:no request;1:request\n" //
                        +"7ec,30,31,1,0,0,,2233FC,6233FC,ff,Electrical Motor state requested by user and build by the BCM,0:Motor stop resquested;1:Unavailable;2:+APC requested;3:Starting requested\n" //
                        +"7ec,31,31,1,0,0,,2233FD,6233FD,ff,Peltier Control Order,0:no control;1:control ativated\n" //
                        +"7ec,31,31,1,0,0,,2233FE,6233FE,ff,Peltier Fan control order,0:no control;1:control ativated\n" //
                        +"7ec,31,31,1,0,0,,223401,623401,ff,Manage Failure Authorization of the AC,0:Not Autorized;1:Autorized\n" //
                        +"7ec,31,31,1,0,0,,223402,623402,ff,Manage Failure Authorization of the 14V Network management,0:Not Autorized;1:Autorized\n" //
                        +"7ec,31,31,1,0,0,,223403,623403,ff,Manage Failure Authorization of the heater,0:Not Autorized;1:Autorized\n" //
                        +"7ec,31,31,1,0,0,,223404,623404,ff,Manage Failure Authorization of the Peltier,0:Not Autorized;1:Autorized\n" //
                        +"7ec,31,31,1,0,0,,223405,623405,ff,Manage Failure Authorization of the PowerTrain,0:Not Autorized;1:Autorized\n" //
                        +"7ec,31,31,1,0,0,,223407,623407,ff,Manage Failure Authorization of the RSA Quick Charge,0:Not Autorized;1:Autorized\n" //
                        +"7ec,31,31,1,0,0,,223408,623408,ff,Manage Failure Authorization of the Slow Charge,0:Not Autorized;1:Autorized\n" //
                        +"7ec,30,31,1,0,0,,223409,623409,ff,Signal of requesting the MMI EBAAlertLevel1 (brake failure with SERV),0:Not used;1:alert off;2:alert on;3:not used\n" //
                        +"7ec,30,31,1,0,0,,22340A,62340A,ff,Signal of requesting the MMI EBAAlertLevel1 (brake failure with STOP),0:Not used;1:alert off;2:alert on;3:not used\n" //
                        +"7ec,30,31,1,0,0,,22340B,62340B,ff,Signal of requesting the MMI QuickDropIterationExceededDisplay,0:Not used;1:alert off;2:alert on;3:not used\n" //
                        +"7ec,30,31,1,0,0,,22340C,62340C,ff,Signal of requesting the MMI QuickDropLockFailureDisplay,0:Not used;1:alert off;2:alert on;3:not used\n" //
                        +"7ec,30,31,1,0,0,,22340D,62340D,ff,Signal of requesting the MMI QuickDropUnlockedDisplay,0:Not used;1:alert off;2:alert on;3:not used\n" //
                        +"7ec,30,31,1,0,0,,22340E,62340E,ff,Signal of requesting the MMI ETAlertDCDC,0:Not used;1:alert off;2:alert on;3:not used\n" //
                        +"7ec,30,31,1,0,0,,22340F,62340F,ff,Signal of requesting the MMI ETAlertRepairETS,0:Unavailable value;1:alert off;2:alert on;3:not used\n" //
                        +"7ec,31,31,1,0,0,,223418,623418,ff,DCDC desactivation requested by diagnostic tool in plant,0:Activated ;1:Desactevated\n" //
                        +"7ec,31,31,1,0,0,,223419,623419,ff,Memorized crash level 1 (medium) detection,0:no memorized ;1:Memorized\n" //
                        +"7ec,31,31,1,0,0,,22341A,62341A,ff,Memorized crash level 2 (violent) detection,0:no memorized ;1:Memorized\n" //
                        +"7ec,24,39,1,0,0,Hz,22341E,62341E,ff,Rotation speed of External Fan for battery cooling (Peltier)\n" //
                        +"7ec,31,31,1,0,0,,223423,623423,ff,P_R_N_D_P_N,0:12 V;1:0V\n" //
                        +"7ec,31,31,1,0,0,,223424,623424,ff,P_R_N_D_L2,0:12 V;1:0V\n" //
                        +"7ec,31,31,1,0,0,,223425,623425,ff,P_R_N_D_L3,0:12 V;1:0V\n" //
                        +"7ec,31,31,1,0,0,,223426,623426,ff,P_R_N_D_L4,0:12 V;1:0V\n" //
                        +"7ec,160,175,1,0,0,,2180,6180,ff,CalibrationNumber\n" //
                        +"7ec,56,63,1,0,0,,2180,6180,ff,DiagnosticIdentificationCode\n" //
                        +"7ec,128,143,1,0,0,,2180,6180,ff,SoftwareNumber\n" //
                        +"7ec,64,87,1,0,0,,2180,6180,2ff,SupplierNumber.ITG\n" //
                        +"7ec,144,159,1,0,0,,2180,6180,ff,EditionNumber\n" //
                        +"7ec,176,183,1,0,0,,2180,6180,ff,PartNumber.BasicPartList,0:N/A;1:237D4;2:237D3\n" //
                        +"7ec,184,191,1,0,0,,2180,6180,ff,HardwareNumber.BasicPartList,0:N/A;1:237D4\n" //
                        +"7ec,192,199,1,0,0,,2180,6180,ff,ApprovalNumber.BasicPartList,0:N/A;1:HMLGT\n" //
                        +"7ec,16,55,1,0,0,,2180,6180,2ff,PartNumber.LowerPart\n" //
                        +"7ec,88,127,1,0,0,,2180,6180,2ff,HardwareNumber.LowerPart\n" //
                        +"7ec,200,207,1,0,0,,2180,6180,ff,ManufacturerIdentificationCode,0:Renault R1;128:Nissan N1;129:Nissan N2;130:Nissan N3;136:5DIGITS;255:Renault R3\n" //
                        +"7ec,160,175,1,0,0,,21F0,61F0,ff,CalibrationNumber\n" //
                        +"7ec,56,63,1,0,0,,21F0,61F0,ff,DiagnosticIdentificationCode\n" //
                        +"7ec,128,143,1,0,0,,21F0,61F0,ff,SoftwareNumber\n" //
                        +"7ec,64,87,1,0,0,,21F0,61F0,2ff,SupplierNumber.ITG\n" //
                        +"7ec,144,159,1,0,0,,21F0,61F0,ff,EditionNumber\n" //
                        +"7ec,176,183,1,0,0,,21F0,61F0,ff,PartNumber.BasicPartList,0:N/A;1:237D4;2:237D3\n" //
                        +"7ec,184,191,1,0,0,,21F0,61F0,ff,HardwareNumber.BasicPartList,0:N/A;1:237D4\n" //
                        +"7ec,192,199,1,0,0,,21F0,61F0,ff,ApprovalNumber.BasicPartList,0:N/A;1:HMLGT\n" //
                        +"7ec,16,55,1,0,0,,21F0,61F0,2ff,PartNumber.LowerPart\n" //
                        +"7ec,88,127,1,0,0,,21F0,61F0,2ff,HardwareNumber.LowerPart\n" //
                        +"7ec,200,207,1,0,0,,21F0,61F0,ff,ManufacturerIdentificationCode,0:Renault R1;128:Nissan N1;129:Nissan N2;130:Nissan N3;136:5DIGITS;255:Renault R3\n" //
                        +"7ec,16,55,1,0,0,,21F1,61F1,2ff,ApprovalNumber.LowerPart\n" //
                        +"7ec,56,95,1,0,0,,21F1,61F1,2ff,ProgrammingSiteReference\n" //
                        +"7ec,96,135,1,0,0,,21F1,61F1,2ff,ProgrammingToolReference\n" //
                        +"7ec,136,143,1,0,0,,21F1,61F1,ff,NumberOfReprogrammings\n" //
                        +"7ec,144,167,1,0,0,,21F1,61F1,ff,DateOfReprogramming\n" //
                        +"7ec,184,191,1,0,0,,21F1,61F1,ff,SaveMarking\n" //
                        +"7ec,192,207,1,0,0,,21F1,61F1,ff,CrcOfLogSave\n" //
                        +"7ec,168,183,1,0,0,,21F1,61F1,ff,TimeOfReprogramming\n" //
                        +"7ec,55,55,1,0,0,,223383,623383,ff,Reasons of cut off PTCs.-1.0 Over temperature,0:No default;1:Default present\n" //
                        +"7ec,54,54,1,0,0,,223383,623383,ff,Reasons of cut off PTCs.-1.1 Under Voltage,0:No default;1:Default present\n" //
                        +"7ec,53,53,1,0,0,,223383,623383,ff,Reasons of cut off PTCs.-1.2 Error PTC stage 1,0:No default;1:Default present\n" //
                        +"7ec,52,52,1,0,0,,223383,623383,ff,Reasons of cut off PTCs.-1.3 Error PTC stage 2,0:No default;1:Default present\n" //
                        +"7ec,51,51,1,0,0,,223383,623383,ff,Reasons of cut off PTCs.-1.4 Error PTC stage 3,0:No default;1:Default present\n" //
                        +"7ec,50,55,1,0,0,,223383,623383,ff,Reasons of cut off PTCs.-1\n" //
                        +"7ec,44,49,1,0,0,,223383,623383,ff,Reasons of cut off PTCs.-2\n" //
                        +"7ec,38,43,1,0,0,,223383,623383,ff,Reasons of cut off PTCs.-3\n" //
                        +"7ec,32,37,1,0,0,,223383,623383,ff,Reasons of cut off PTCs.-4\n" //
                        +"7ec,26,31,1,0,0,,223383,623383,ff,Reasons of cut off PTCs.-5\n" //
                        +"7ec,31,31,1,0,0,,223396,623396,ff,PEB State failure defect present : PEB is Absent or PEB Status is not power on . PEB status is in limp mode,0:PEB Power ON;1:PEB Not Power ON\n" //
                        +"7ec,39,39,1,0,0,,22337E,62337E,ff,Context not safety data microcontroller memorised Carrier 2.0-ERR_SYM_ROM,0:No default;1:Default present\n" //
                        +"7ec,38,38,1,0,0,,22337E,62337E,ff,Context not safety data microcontroller memorised Carrier 2.1-ERR_SYM_RAM,0:No default;1:Default present\n" //
                        +"7ec,37,37,1,0,0,,22337E,62337E,ff,Context not safety data microcontroller memorised Carrier 2.2-ERR_SYM_SPI-0,0:No default;1:Default present\n" //
                        +"7ec,35,35,1,0,0,,22337E,62337E,ff,Context not safety data microcontroller memorised Carrier 2.4-ERR_SYM_SPI-2,0:No default;1:Default present\n" //
                        +"7ec,34,34,1,0,0,,22337E,62337E,ff,Context not safety data microcontroller memorised Carrier 2.5-ERR_SYM_SPI-3,0:No default;1:Default present\n" //
                        +"7ec,33,33,1,0,0,,22337E,62337E,ff,Context not safety data microcontroller memorised Carrier 2.6-ERR_SYM_SPI-4,0:No default;1:Default present\n" //
                        +"7ec,32,32,1,0,0,,22337E,62337E,ff,Context not safety data microcontroller memorised Carrier 2.7\n" //
                        +"7ec,36,36,1,0,0,,22337E,62337E,ff,Context not safety data microcontroller memorised Carrier 2.3-ERR_SYM_SPI-1,0:No default;1:Default present\n" //
                        +"7ec,31,31,1,0,0,,223391,623391,ff,Gaz Heater not allowed flag : _ Cluster ECU is absent _ Calabration constant C_GasHeater_Enable= 0 and does not enable gaz heater,0:Allowed;1:Not Allowed\n" //
                        +"7ec,30,31,1,0,0,,223378,623378,ff,Interlock HV battery,0:Not used;1:Open;2:Closed;3:Unvailable Value\n" //
                        +"7ec,24,39,100,0,0,Ohm,2233EE,6233EE,ff,Resistance insulation value\n" //
                        +"7ec,31,31,1,0,0,,2233CB,6233CB,ff,This flag from Charge Management indicates that a charge is finished.,0:Charge not finished;1:Charge finished\n" //
                        +"7ec,24,55,1,0,0,-,223364,623364,ff,Time Counter for the BCB temperature in driving mode above 70 degrees celcius\n" //
                        +"7ec,24,55,1,0,0,-,22335F,62335F,ff,Time Counter for the BCB temperature in driving mode below 40 degrees celcius\n" //
                        +"7ec,24,55,1,0,0,-,223361,623361,ff,Time Counter for the BCB temperature in driving mode between 40 degrees celcius and 50 degrees celcius\n" //
                        +"7ec,24,55,1,0,0,-,223362,623362,ff,Time Counter for the BCB temperature in driving mode between 50 degrees celcius and 60 degrees celcius\n" //
                        +"7ec,24,55,1,0,0,-,223363,623363,ff,Time Counter for the BCB temperature in driving mode between 60 degrees celcius and 70 degrees celcius\n" //
                        +"7ec,24,55,1,0,0,-,223365,623365,ff,Time Counter for the BCB temperature in load mode below 40 degrees celcius\n" //
                        +"7ec,24,55,1,0,0,-,223369,623369,ff,Time Counter for the BCB temperature in load mode above 70 degrees celcius\n" //
                        +"7ec,24,55,1,0,0,-,223366,623366,ff,Time Counter for the BCB temperature in load mode between 40 degrees celcius and 50 degrees celcius\n" //
                        +"7ec,24,55,1,0,0,-,223367,623367,ff,Time Counter for the BCB temperature in load mode between 50 degrees celcius and 60 degrees celcius\n" //
                        +"7ec,24,55,1,0,0,-,223368,623368,ff,Time Counter for the BCB temperature in load mode between 60 degrees celcius and 70 degrees celcius\n" //
                        +"7ec,24,55,1,0,0,-,223359,623359,ff,Time Counter for the PEB temperature in driving mode above 70 degrees celcius\n" //
                        +"7ec,24,55,1,0,0,-,223355,623355,ff,Time Counter for the PEB temperature in driving mode below 40 degrees celcius\n" //
                        +"7ec,24,55,1,0,0,-,223356,623356,ff,Time Counter for the PEB temperature in driving mode between 40 degrees celcius and 50 degrees celcius\n" //
                        +"7ec,24,55,1,0,0,-,223357,623357,ff,Time Counter for the PEB temperature in driving mode between 50degrees celcius and 60degrees celcius\n" //
                        +"7ec,24,55,1,0,0,-,223358,623358,ff,Time Counter for the PEB temperature in driving mode between 60degrees celcius and 70degrees celcius\n" //
                        +"7ec,24,55,1,0,0,-,22335E,62335E,ff,Time Counter for the PEB temperature in load mode above 70 degrees celcius\n" //
                        +"7ec,24,55,1,0,0,-,22335A,62335A,ff,Time Counter for the PEB temperature in load mode below 40 degrees celcius\n" //
                        +"7ec,24,55,1,0,0,-,22335B,62335B,ff,Time Counter for the PEB temperature in load mode between 40 degrees celcius and 50 degrees celcius\n" //
                        +"7ec,24,55,1,0,0,-,22335C,62335C,ff,Time Counter for the PEB temperature in load mode between 50 degrees celcius and 60 degrees celcius\n" //
                        +"7ec,24,55,1,0,0,-,22335D,62335D,ff,Time Counter for the PEB temperature in load mode between 60 degrees celcius and 70 degrees celcius\n" //
                        +"7ec,30,31,1,0,0,,22224C,62224C,ff,Gear lever position validity flag,0:Nok;1:Ok;2:Failed 2\n" //
                        +"7ec,29,31,1,0,0,,223410,623410,ff,HV Network Diagnosis synthesis,0:No connection Allowed but connection can stand still;1:Connection Allowed;3:Not used;4:No Connection Allowed and Disconnection Needed;5:Emergency disconnection Needed\n" //
                        +"7ec,31,31,1,0,0,,2233FF,6233FF,ff,Major failure detected on the ETS,0:no failure;1:failure\n" //
                        +"7ec,168,183,.01,0,0,kWh,223416,623416,ff,Memorization of accumulated electrical DCDC energy for energy consumption journal.0\n" //
                        +"7ec,152,167,.01,0,0,kWh,223416,623416,ff,Memorization of accumulated electrical DCDC energy for energy consumption journal.1\n" //
                        +"7ec,136,151,.01,0,0,kWh,223416,623416,ff,Memorization of accumulated electrical DCDC energy for energy consumption journal.2\n" //
                        +"7ec,120,135,.01,0,0,kWh,223416,623416,ff,Memorization of accumulated electrical DCDC energy for energy consumption journal.3\n" //
                        +"7ec,104,119,.01,0,0,kWh,223416,623416,ff,Memorization of accumulated electrical DCDC energy for energy consumption journal.4\n" //
                        +"7ec,88,103,.01,0,0,kWh,223416,623416,ff,Memorization of accumulated electrical DCDC energy for energy consumption journal.5\n" //
                        +"7ec,72,87,.01,0,0,kWh,223416,623416,ff,Memorization of accumulated electrical DCDC energy for energy consumption journal.6\n" //
                        +"7ec,56,71,.01,0,0,kWh,223416,623416,ff,Memorization of accumulated electrical DCDC energy for energy consumption journal.7\n" //
                        +"7ec,40,55,.01,0,0,kWh,223416,623416,ff,Memorization of accumulated electrical DCDC energy for energy consumption journal.8\n" //
                        +"7ec,24,39,.01,0,0,kWh,223416,623416,ff,Memorization of accumulated electrical DCDC energy for energy consumption journal.9\n" //
                        +"7ec,168,183,.01,0,0,kWh,223413,623413,ff,Memorized accumulated electrical inverter energy for energy consumption journal.0\n" //
                        +"7ec,152,167,.01,0,0,kWh,223413,623413,ff,Memorized accumulated electrical inverter energy for energy consumption journal.1\n" //
                        +"7ec,136,151,.01,0,0,kWh,223413,623413,ff,Memorized accumulated electrical inverter energy for energy consumption journal.2\n" //
                        +"7ec,120,135,.01,0,0,kWh,223413,623413,ff,Memorized accumulated electrical inverter energy for energy consumption journal.3\n" //
                        +"7ec,104,119,.01,0,0,kWh,223413,623413,ff,Memorized accumulated electrical inverter energy for energy consumption journal.4\n" //
                        +"7ec,88,103,.01,0,0,kWh,223413,623413,ff,Memorized accumulated electrical inverter energy for energy consumption journal.5\n" //
                        +"7ec,72,87,.01,0,0,kWh,223413,623413,ff,Memorized accumulated electrical inverter energy for energy consumption journal.6\n" //
                        +"7ec,56,71,.01,0,0,kWh,223413,623413,ff,Memorized accumulated electrical inverter energy for energy consumption journal.7\n" //
                        +"7ec,40,55,.01,0,0,kWh,223413,623413,ff,Memorized accumulated electrical inverter energy for energy consumption journal.8\n" //
                        +"7ec,24,39,.01,0,0,kWh,223413,623413,ff,Memorized accumulated electrical inverter energy for energy consumption journal.9\n" //
                        +"7ec,168,183,.01,0,0,kWh,223412,623412,ff,Memorized accumulated HV battery output energy for energy consumption journal.0\n" //
                        +"7ec,152,167,.01,0,0,kWh,223412,623412,ff,Memorized accumulated HV battery output energy for energy consumption journal.1\n" //
                        +"7ec,136,151,.01,0,0,kWh,223412,623412,ff,Memorized accumulated HV battery output energy for energy consumption journal.2\n" //
                        +"7ec,120,135,.01,0,0,kWh,223412,623412,ff,Memorized accumulated HV battery output energy for energy consumption journal.3\n" //
                        +"7ec,104,119,.01,0,0,kWh,223412,623412,ff,Memorized accumulated HV battery output energy for energy consumption journal.4\n" //
                        +"7ec,88,103,.01,0,0,kWh,223412,623412,ff,Memorized accumulated HV battery output energy for energy consumption journal.5\n" //
                        +"7ec,72,87,.01,0,0,kWh,223412,623412,ff,Memorized accumulated HV battery output energy for energy consumption journal.6\n" //
                        +"7ec,56,71,.01,0,0,kWh,223412,623412,ff,Memorized accumulated HV battery output energy for energy consumption journal.7\n" //
                        +"7ec,40,55,.01,0,0,kWh,223412,623412,ff,Memorized accumulated HV battery output energy for energy consumption journal.8\n" //
                        +"7ec,24,39,.01,0,0,kWh,223412,623412,ff,Memorized accumulated HV battery output energy for energy consumption journal.9\n" //
                        +"7ec,168,183,.01,0,0,kWh,223414,623414,ff,Memorized accumutation of the energy used by the thermal comfort for energy consumption journal.0\n" //
                        +"7ec,152,167,.01,0,0,kWh,223414,623414,ff,Memorized accumutation of the energy used by the thermal comfort for energy consumption journal.1\n" //
                        +"7ec,136,151,.01,0,0,kWh,223414,623414,ff,Memorized accumutation of the energy used by the thermal comfort for energy consumption journal.2\n" //
                        +"7ec,120,135,.01,0,0,kWh,223414,623414,ff,Memorized accumutation of the energy used by the thermal comfort for energy consumption journal.3\n" //

                ;

        String fieldDef2 =
                ""

                        +"7ec,104,119,.01,0,0,kWh,223414,623414,ff,Memorized accumutation of the energy used by the thermal comfort for energy consumption journal.4\n" //
                        +"7ec,88,103,.01,0,0,kWh,223414,623414,ff,Memorized accumutation of the energy used by the thermal comfort for energy consumption journal.5\n" //
                        +"7ec,72,87,.01,0,0,kWh,223414,623414,ff,Memorized accumutation of the energy used by the thermal comfort for energy consumption journal.6\n" //
                        +"7ec,56,71,.01,0,0,kWh,223414,623414,ff,Memorized accumutation of the energy used by the thermal comfort for energy consumption journal.7\n" //
                        +"7ec,40,55,.01,0,0,kWh,223414,623414,ff,Memorized accumutation of the energy used by the thermal comfort for energy consumption journal.8\n" //
                        +"7ec,24,39,.01,0,0,kWh,223414,623414,ff,Memorized accumutation of the energy used by the thermal comfort for energy consumption journal.9\n" //
                        +"7ec,168,183,.02,0,0,%,223415,623415,ff,Memorized User SOC for energy consumption journal.0\n" //
                        +"7ec,152,167,.02,0,0,%,223415,623415,ff,Memorized User SOC for energy consumption journal.1\n" //
                        +"7ec,136,151,.02,0,0,%,223415,623415,ff,Memorized User SOC for energy consumption journal.2\n" //
                        +"7ec,120,135,.02,0,0,%,223415,623415,ff,Memorized User SOC for energy consumption journal.3\n" //
                        +"7ec,104,119,.02,0,0,%,223415,623415,ff,Memorized User SOC for energy consumption journal.4\n" //
                        +"7ec,88,103,.02,0,0,%,223415,623415,ff,Memorized User SOC for energy consumption journal.5\n" //
                        +"7ec,72,87,.02,0,0,%,223415,623415,ff,Memorized User SOC for energy consumption journal.6\n" //
                        +"7ec,56,71,.02,0,0,%,223415,623415,ff,Memorized User SOC for energy consumption journal.7\n" //
                        +"7ec,40,55,.02,0,0,%,223415,623415,ff,Memorized User SOC for energy consumption journal.8\n" //
                        +"7ec,24,39,.02,0,0,%,223415,623415,ff,Memorized User SOC for energy consumption journal.9\n" //
                        +"7ec,24,39,.5,32768,0,N,222249,622249,ff,Force demand (at the wheels) when powertrain setpoint is equal to zero, before saturation accounting for minimum torque demand available\n" //
                        +"7ec,30,31,1,0,0,,2233BA,6233BA,ff,Indicates if the external power supply is present and the type of power supply (slow charge source or quick charge source).,0:NoExtEnergyAvail;1:SlowExtEnergyAvail;2:QuickExtEnergyAvail\n" //
                        +"7ec,24,31,1,40,0,°C,223421,623421,ff,For reading Battery temperature starting point of the Peltier System (Diagnostic Input)\n" //
                        +"7ec,31,31,1,0,0,,223429,623429,ff,DCDC ready or not (internal EVC),0:DCDC Off;1:DCDC On\n" //
                        +"7ec,39,39,1,0,0,,22342A,62342A,ff,DCDC HW Failures from PEB.DCDC HW failures from PEB.0\n" //
                        +"7ec,38,38,1,0,0,,22342A,62342A,ff,DCDC HW Failures from PEB.DCDC HW failures from PEB.1,0:No default;1:Default present\n" //
                        +"7ec,37,37,1,0,0,,22342A,62342A,ff,DCDC HW Failures from PEB.DCDC HW failures from PEB.2,0:No default;1:Default present\n" //
                        +"7ec,36,36,1,0,0,,22342A,62342A,ff,DCDC HW Failures from PEB.DCDC HW failures from PEB.3,0:No default;1:Default present\n" //
                        +"7ec,35,35,1,0,0,,22342A,62342A,ff,DCDC HW Failures from PEB.DCDC HW failures from PEB.4,0:No default;1:Default present\n" //
                        +"7ec,34,34,1,0,0,,22342A,62342A,ff,DCDC HW Failures from PEB.DCDC HW failures from PEB.5,0:No default;1:Default present\n" //
                        +"7ec,33,33,1,0,0,,22342A,62342A,ff,DCDC HW Failures from PEB.DCDC HW failures from PEB.6,0:No default;1:Default present\n" //
                        +"7ec,32,32,1,0,0,,22342A,62342A,ff,DCDC HW Failures from PEB.DCDC HW failures from PEB.7,0:No default;1:Default present\n" //
                        +"7ec,31,31,1,0,0,,22342A,62342A,ff,DCDC HW Failures from PEB.DCDC HW failures from PEB.8,0:No default;1:Default present\n" //
                        +"7ec,30,30,1,0,0,,22342A,62342A,ff,DCDC HW Failures from PEB.DCDC HW failures from PEB.9,0:No default;1:Default present\n" //
                        +"7ec,29,29,1,0,0,,22342A,62342A,ff,DCDC HW Failures from PEB.DCDC HW failures from PEB.10,0:No default;1:Default present\n" //
                        +"7ec,28,28,1,0,0,,22342A,62342A,ff,DCDC HW Failures from PEB.DCDC HW failures from PEB.11,0:No default;1:Default present\n" //
                        +"7ec,27,27,1,0,0,,22342A,62342A,ff,DCDC HW Failures from PEB.DCDC HW failures from PEB.12,0:No default;1:Default present\n" //
                        +"7ec,26,26,1,0,0,,22342A,62342A,ff,DCDC HW Failures from PEB.DCDC HW failures from PEB.13,0:No default;1:Default present\n" //
                        +"7ec,240,263,1,0,0,km,223411,623411,ff,Distance memorized for energy consumption journal.0\n" //
                        +"7ec,216,239,1,0,0,km,223411,623411,ff,Distance memorized for energy consumption journal.1\n" //
                        +"7ec,192,215,1,0,0,km,223411,623411,ff,Distance memorized for energy consumption journal.2\n" //
                        +"7ec,168,191,1,0,0,km,223411,623411,ff,Distance memorized for energy consumption journal.3\n" //
                        +"7ec,144,167,1,0,0,km,223411,623411,ff,Distance memorized for energy consumption journal.4\n" //
                        +"7ec,120,143,1,0,0,km,223411,623411,ff,Distance memorized for energy consumption journal.5\n" //
                        +"7ec,96,119,1,0,0,km,223411,623411,ff,Distance memorized for energy consumption journal.6\n" //
                        +"7ec,72,95,1,0,0,km,223411,623411,ff,Distance memorized for energy consumption journal.7\n" //
                        +"7ec,48,71,1,0,0,km,223411,623411,ff,Distance memorized for energy consumption journal.8\n" //
                        +"7ec,24,47,1,0,0,km,223411,623411,ff,Distance memorized for energy consumption journal.9\n" //
                        +"7ec,56,63,1,0,0,,223375,623375,ff,Memorized reason of Starting Refuse for Start Refuse History elaboration.0,0:C_Driving_No_Refuse;1:C_Driving_Refuse_Plug;2:C_Driving_Refuse_Verrloglocked;3:C_Driving_Refuse_PRND_notN;4:C_Driving_Refuse_PRND_notP;5:C_Driving_Refuse_Batterylock;6:C_Driving_Refuse_Engine\n" //
                        +"7ec,48,55,1,0,0,,223375,623375,ff,Memorized reason of Starting Refuse for Start Refuse History elaboration.1,0:C_Driving_No_Refuse;1:C_Driving_Refuse_Plug;2:C_Driving_Refuse_Verrloglocked;3:C_Driving_Refuse_PRND_notN;4:C_Driving_Refuse_PRND_notP;5:C_Driving_Refuse_Batterylock;6:C_Driving_Refuse_Engine\n" //
                        +"7ec,40,47,1,0,0,,223375,623375,ff,Memorized reason of Starting Refuse for Start Refuse History elaboration.2,0:C_Driving_No_Refuse;1:C_Driving_Refuse_Plug;2:C_Driving_Refuse_Verrloglocked;3:C_Driving_Refuse_PRND_notN;4:C_Driving_Refuse_PRND_notP;5:C_Driving_Refuse_Batterylock;6:C_Driving_Refuse_Engine\n" //
                        +"7ec,32,39,1,0,0,,223375,623375,ff,Memorized reason of Starting Refuse for Start Refuse History elaboration.3,0:C_Driving_No_Refuse;1:C_Driving_Refuse_Plug;2:C_Driving_Refuse_Verrloglocked;3:C_Driving_Refuse_PRND_notN;4:C_Driving_Refuse_PRND_notP;5:C_Driving_Refuse_Batterylock;6:C_Driving_Refuse_Engine\n" //
                        +"7ec,24,31,1,0,0,,223375,623375,ff,Memorized reason of Starting Refuse for Start Refuse History elaboration.4,0:C_Driving_No_Refuse;1:C_Driving_Refuse_Plug;2:C_Driving_Refuse_Verrloglocked;3:C_Driving_Refuse_PRND_notN;4:C_Driving_Refuse_PRND_notP;5:C_Driving_Refuse_Batterylock;6:C_Driving_Refuse_Engine\n" //
                        +"7ec,31,31,1,0,0,,222079,622079,ff,CCSL - State of the system causes for normal CCSL deactivation.0,0:Start button not pressed;1:Start button pressed\n" //
                        +"7ec,30,30,1,0,0,,222079,622079,ff,CCSL - State of the system causes for normal CCSL deactivation.1,0:Gearbox not on a reverse position;1:Gearbox on a reverse position\n" //
                        +"7ec,29,29,1,0,0,,222079,622079,ff,CCSL - State of the system causes for normal CCSL deactivation.2,0:ASR or AYC not in regulation;1:ASR or AYC in regulation\n" //
                        +"7ec,28,28,1,0,0,,222079,622079,ff,CCSL - State of the system causes for normal CCSL deactivation.3,0:Speed / setpoint ratio not too small;1:Speed / setpoint ratio too small\n" //
                        +"7ec,27,27,1,0,0,,222079,622079,ff,CCSL - State of the system causes for normal CCSL deactivation.4,0:No Assisted nor manual parking brake;1:Assisted or manual parking brake\n" //
                        +"7ec,26,26,1,0,0,,222079,622079,ff,CCSL - State of the system causes for normal CCSL deactivation.5,0:No CC system engine control inhibition;1:CC system engine control inhibition\n" //
                        +"7ec,25,25,1,0,0,,222079,622079,ff,CCSL - State of the system causes for normal CCSL deactivation.6,0:No SL system engine control inhibition;1:SL system engine control inhibition\n" //
                        +"7ec,31,31,1,0,0,,222078,622078,ff,CCSL - State of the causes for normal CCSL deactivation.0,0:No Main switch changed;1:Main switch changed\n" //
                        +"7ec,30,30,1,0,0,,222078,622078,ff,CCSL - State of the causes for normal CCSL deactivation.1,0:No Suspend button pressed;1:Suspend button pressed\n" //
                        +"7ec,29,29,1,0,0,,222078,622078,ff,CCSL - State of the causes for normal CCSL deactivation.2,0:No Braking;1:Braking\n" //
                        +"7ec,28,28,1,0,0,,222078,622078,ff,CCSL - State of the causes for normal CCSL deactivation.3,0:no Clutch pedal pressed or no connection;1:Clutch pedal pressed or no connection\n" //
                        +"7ec,27,27,1,0,0,,222078,622078,ff,CCSL - State of the causes for normal CCSL deactivation.4,0:No Gearbox on a neutral position;1:Gearbox on a neutral position\n" //
                        +"7ec,26,26,1,0,0,,222078,622078,ff,CCSL - State of the causes for normal CCSL deactivation.5,0:No Eco mode request change;1:Eco mode request change\n" //
                        +"7ec,31,31,1,0,0,,22204F,62204F,ff,CCSL - State of the reversible failures not due to CC which cause CC failure.0\n" //
                        +"7ec,30,30,1,0,0,,22204F,62204F,ff,CCSL - State of the reversible failures not due to CC which cause CC failure.1\n" //
                        +"7ec,29,29,1,0,0,,22204F,62204F,ff,CCSL - State of the reversible failures not due to CC which cause CC failure.2,0:No brake information unavailable;1:Brake information unavailable\n" //
                        +"7ec,28,28,1,0,0,,22204F,62204F,ff,CCSL - State of the reversible failures not due to CC which cause CC failure.3,0:No brake information absence;1:Brake information absence\n" //
                        +"7ec,27,27,1,0,0,,22204F,62204F,ff,CCSL - State of the reversible failures not due to CC which cause CC failure.4,0:No braking detected without brake information;1:Braking detected without brake information\n" //
                        +"7ec,26,26,1,0,0,,22204F,62204F,ff,CCSL - State of the reversible failures not due to CC which cause CC failure.5,0:No sudden braking detected without brake information;1:Sudden braking detected without brake information\n" //
                        +"7ec,25,25,1,0,0,,22204F,62204F,ff,CCSL - State of the reversible failures not due to CC which cause CC failure.6,0:No Multi Function Contactor failure;1:Multi Function Contactor failure\n" //
                        +"7ec,24,24,1,0,0,,22204F,62204F,ff,CCSL - State of the reversible failures not due to CC which cause CC failure.7,0:No CC engine inhibition;1:CC engine inhibition\n" //
                        +"7ec,31,31,1,0,0,,22204E,62204E,ff,CCSL - State of the reversible failures not due to CCSL which cause CCSL failure.0,0:No real vehicle speed unavailable;1:Real vehicle speed unavailable\n" //
                        +"7ec,30,30,1,0,0,,22204E,62204E,ff,CCSL - State of the reversible failures not due to CCSL which cause CCSL failure.1,0:No displayed vehicle speed unavailable;1:Displayed vehicle speed unavailable\n" //
                        +"7ec,29,29,1,0,0,,22204E,62204E,ff,CCSL - State of the reversible failures not due to CCSL which cause CCSL failure.2,0:No real vehicle speed absence;1:Real vehicle speed absence\n" //
                        +"7ec,28,28,1,0,0,,22204E,62204E,ff,CCSL - State of the reversible failures not due to CCSL which cause CCSL failure.3,0:No displayed vehicle speed absence;1:Displayed vehicle speed absence\n" //
                        +"7ec,27,27,1,0,0,,22204E,62204E,ff,CCSL - State of the reversible failures not due to CCSL which cause CCSL failure.4,0:No change of the displayed speed unit;1:Change of the displayed speed unit\n" //
                        +"7ec,26,26,1,0,0,,22204E,62204E,ff,CCSL - State of the reversible failures not due to CCSL which cause CCSL failure.5,0:No SL engine inhibition;1:SL engine inhibition\n" //
                        +"7ec,31,31,1,0,0,,22204D,62204D,ff,CCSL - State of the failures which cause irreversible CC safety failure.0,0:No Presence of CC force request despite the CC deactivation;1:Presence of CC force request despite the CC deactivation\n" //
                        +"7ec,30,30,1,0,0,,22204D,62204D,ff,CCSL - State of the failures which cause irreversible CC safety failure.1,0:No activation of the open brake switch without CC deactivation;1:activation of the open brake switch without CC deactivation\n" //
                        +"7ec,29,29,1,0,0,,22204D,62204D,ff,CCSL - State of the failures which cause irreversible CC safety failure.2,0:No activation of the minimum travel clutch switch without CC deactivation;1:activation of the minimum travel clutch switch without CC deactivation\n" //
                        +"7ec,24,47,1,0,0,min,223431,623431,ff,Time since the vehicle has been parked (calculated in SCH)\n" //
                        +"7ec,24,39,.0005,0,0,V,223432,623432,ff,Battery voltage no load estimation memorization (internal SCH)\n" //
                        +"7ec,24,31,.05,-240,0,V,223433,623433,ff,Battery voltage request (internal SCH)\n" //
                        +"7ec,29,31,1,0,0,,22342D,62342D,ff,Class of the DCDC HW Failures from PEB,0:No Failure;1:Downgraded Mode;2:Class C;3:Class D;4:Class E\n" //
                        +"7ec,16,39,1,0,0,,2184,6184,ff,ITG Supplier Number\n" //
                        +"7ec,40,47,1,0,0,,2184,6184,2ff,Traceability Factory Code\n" //
                        +"7ec,48,143,1,0,0,,2184,6184,2ff,Traceability Serial Number\n" //
                        +"7ec,152,167,1,0,0,,2181,6181,ff,CRC\n" //
                        +"7ec,16,151,1,0,0,,2181,6181,2ff,V.I.N\n" //
                        +"7ec,30,31,1,0,0,,22342E,62342E,ff,DCDC ready to sleep (internal EVC),0:Not Used;1:Refuse to Sleep;2:Ready To Sleep;3:Not used\n" //
                        +"7ec,30,31,1,0,0,,223434,623434,ff,PTC_Config,0:no HV PTC;1:HV PTC with water;2:HV PTC with air\n" //
                        +"7ec,30,31,1,0,0,,2233BC,6233BC,ff,HV connection request from LBC,0:Not used;1:Closing of Power Contacts Allowed;2:Closing of Power Contacts Not Allowed;3:Unavailable Value\n" //
                        +"7ec,24,39,.0005,0,0,V,223428,623428,ff,Battery 14V Voltage after everychecking (internal SCH)\n" //
                        +"7ec,24,55,.0002,0,0,km,2233DF,6233DF,ff,This variable allows to memorize the distance\n" //
                        +"7ec,24,55,.0001,0,0,km,2233DE,6233DE,ff,This variable allows to memorize the reseted distance\n" //
                        +"7ec,24,47,.001,0,0,kwh,2233DD,6233DD,ff,This variable allows to memorize the reseted energy consumed\n" //
                        +"7ec,24,47,.001,0,0,kwh,2233DC,6233DC,ff,This variable allows to memorize the energy consumed\n" //
                        +"7ec,24,39,.01,0,0,kWh,2233DB,6233DB,ff,This variable allows to memorize the consumed domestic energy\n" //
                        +"7ec,31,31,1,0,0,,223461,623461,ff,Frame consistency ok,0:UBP frame Nok;1:UBP frame Ok\n" //
                        +"7ec,31,31,1,0,0,,223462,623462,ff,Vehicle configured with UBP (1) or VPM (0),0:configured with VPM;1:configured with UBP\n" //
                        +"7ec,24,31,.01,0,0,,223464,623464,ff,UBP brake pedal ratio from CAN\n" //
                        +"7ec,24,39,1,0,0,N·m,223465,623465,ff,UBP wheel torque request from CAN\n" //
                        +"7ec,30,31,1,0,0,,223435,623435,ff,AC compressor authorization command,0:Disable;1:Enable;2:Not used;3:Signal invalid\n" //
                        +"7ec,24,39,1,0,0,rpm,223436,623436,ff,AC compressor RPM real value\n" //
                        +"7ec,24,31,2,0,0,V,223437,623437,ff,AC compressor High Voltage value\n" //
                        +"7ec,24,31,1,40,0,°C,223438,623438,ff,AC compressor Power Module Temperature\n" //
                        +"7ec,30,31,1,0,0,,223439,623439,ff,AC compressor Error Status,0:No problem;1:Degraded mode;2:Compressor shut-off safety reasons;3:Compressor has to te changed\n" //
                        +"7ec,24,31,.1,0,0,,22343A,62343A,ff,AC compressor Motor Phase Current\n" //
                        +"7ec,30,31,1,0,0,,22343B,62343B,ff,AC compressor State,0:Signal Invalid;1:Compressor Off;2:Compressor is starting;3:Compressor On\n" //
                        +"7ec,24,31,1,0,0,,22343C,62343C,ff,Dysfunctional disconnections counter\n" //
                        +"7ec,24,55,1,0,0,,22343D,62343D,ff,Identification number of the batteries for the 10 last quick drop.9\n" //
                        +"7ec,56,87,1,0,0,,22343D,62343D,ff,Identification number of the batteries for the 10 last quick drop.8\n" //
                        +"7ec,88,119,1,0,0,,22343D,62343D,ff,Identification number of the batteries for the 10 last quick drop.7\n" //
                        +"7ec,120,151,1,0,0,,22343D,62343D,ff,Identification number of the batteries for the 10 last quick drop.6\n" //
                        +"7ec,152,183,1,0,0,,22343D,62343D,ff,Identification number of the batteries for the 10 last quick drop.5\n" //
                        +"7ec,184,215,1,0,0,,22343D,62343D,ff,Identification number of the batteries for the 10 last quick drop.4\n" //
                        +"7ec,216,247,1,0,0,,22343D,62343D,ff,Identification number of the batteries for the 10 last quick drop.3\n" //
                        +"7ec,248,279,1,0,0,,22343D,62343D,ff,Identification number of the batteries for the 10 last quick drop.2\n" //
                        +"7ec,280,311,1,0,0,,22343D,62343D,ff,Identification number of the batteries for the 10 last quick drop.1\n" //
                        +"7ec,312,343,1,0,0,,22343D,62343D,ff,Identification number of the batteries for the 10 last quick drop.0\n" //
                        +"7ec,24,55,1,0,0,,22343F,62343F,ff,Identification number of the battery received by CAN\n" //
                        +"7ec,31,31,1,0,0,,223441,623441,ff,CAN signal for the status of the K-Line communication,0:No presence;1:presence\n" //
                        +"7ec,30,31,1,0,0,,223442,623442,ff,ETS Sleeping authorisation according to HV Battery Management Strategies,0:Not used;1:Refuse to sleep;2:Ready to sleep;3:unused\n" //
                        +"7ec,24,31,.1,0,0,kW,223443,623443,ff,Available BCB power for a battery charge\n" //
                        +"7ec,24,31,.3,0,0,kW,223444,623444,ff,CAN signal for the maximal charging power authorized by the HV battery\n" //
                        +"7ec,24,39,100,0,0,W,223445,623445,ff,CAN signal for the estimation of the BCB input power (power consumed from the Mains network)\n" //
                        +"7ec,31,31,1,0,0,,223446,623446,ff,eco mode request,0:Mode eco not requested;1:Mode eco requested\n" //
                        +"7ec,24,39,1,32768,0,N·m,223448,623448,ff,Electrical motor (EM) maximum effective torque available, after eco mode limitation\n" //
                        +"7ec,24,31,1,0,0,,22344A,62344A,ff,This paramater is needed in read mode to know the counter of the functional diagnostic which aim at detecting a too often low performance of battery cooling system in study\n" //
                        +"7ec,31,31,1,0,0,,22344B,62344B,ff,Allow to control the BattVE Relay which gives energy to several ECU linked to thermal comfort,0:Opened;1:Closed\n" //
                        +"7ec,31,31,1,0,0,,22344C,62344C,ff,Plug Unlock/ Trap Open Push Button request,0:No request;1:Request activated\n" //
                        +"7ec,31,31,1,0,0,,22344D,62344D,ff,Energy Trap sensor status,0:Closed;1:Open\n" //
                        +"7ec,31,31,1,0,0,,22344E,62344E,ff,Energy Trap Actuator Command,0:No open trap command;1:Open trap command\n" //
                        +"7ec,31,31,1,0,0,,22344F,62344F,ff,Plug Unlock/ Trap Open Push Button Status,0:Not pressed;1:Pressed\n" //
                        +"7ec,31,31,1,0,0,,223450,623450,ff,Configuration of the Quick Drop (Enable or disable),0:No quickdrop;1:Quickdrop\n" //
                        +"7ec,24,39,1,0,0,km,223451,623451,ff,Estimated kilometric cruising range sent to MMI\n" //
                        +"7ec,31,31,1,0,0,,223453,623453,ff,EEPROM memorized value of the eco mode switch request,0:Mode ECO not requested;1:Mode ECO requested\n" //
                        +"7ec,31,31,1,0,0,,223452,623452,ff,Eco mode request to be sent on the CAN for CLIM and MMC,0:Mode ECO not requested;1:Mode ECO requested\n" //
                        +"7ec,48,71,.001,0,0,kW,223454,623454,ff,Contributions of anticipation lack and excessive vehicle speed to electrical overconsumption since cluster reset.0\n" //
                        +"7ec,24,47,.001,0,0,kW,223454,623454,ff,Contributions of anticipation lack and excessive vehicle speed to electrical overconsumption since cluster reset.1\n" //
                        +"7ec,24,39,1,-1,0,kW,223455,623455,ff,Mean eco electrical consumption (if driver has economical driving behavior)\n" //
                        +"7ec,24,39,1,0,0,km,223456,623456,ff,Temporary estimated kilometric cruising range sent to MMI (economical driving)\n" //
                        +"7ec,24,39,1,-1,0,kW,223457,623457,ff,Mean pessimistic electrical consumption (worst possible consumption)\n" //
                        +"7ec,24,39,1,0,0,km,223458,623458,ff,Temporary estimated minimum kilometric cruising range sent to MMI\n" //
                        +"7ec,24,39,1,-1,0,kW,223459,623459,ff,Mean consumption per unit of distance, used for calculation of kilometric range\n" //
                        +"7ec,24,31,1,0,0,,22345A,62345A,ff,Indicator relative to anticipation for economical scoring\n" //
                        +"7ec,24,31,1,0,0,,22345B,62345B,ff,Indicator of economical monitoring\n" //
                        +"7ec,24,31,1,0,0,,22345C,62345C,ff,Indicator relative to vehicle speed for economical scoring\n" //
                        +"7ec,24,31,1,0,0,,22345D,62345D,ff,Information about the performance of Battery conditionning system memorized in E2PROM\n" //
                        +"7ec,24,31,1,0,0,%,22345E,62345E,ff,FAN global request from ETS in PWM including AC, Cooling needs and Relay protection check\n" //
                        +"7ec,39,39,1,0,0,,22345F,62345F,ff,This internal M14N variable is a frame composed of every RTS from all ETS system.0,0:EM OK;1:EM NOK\n" //
                        +"7ec,38,38,1,0,0,,22345F,62345F,ff,This internal M14N variable is a frame composed of every RTS from all ETS system.1,0:BCB OK;1:BCB NOK\n" //
                        +"7ec,37,37,1,0,0,,22345F,62345F,ff,This internal M14N variable is a frame composed of every RTS from all ETS system.2,0:BHV OK;1:BHV NOK\n" //
                        +"7ec,36,36,1,0,0,,22345F,62345F,ff,This internal M14N variable is a frame composed of every RTS from all ETS system.3,0:MCS OK;1:MCS NOK\n" //
                        +"7ec,35,35,1,0,0,,22345F,62345F,ff,This internal M14N variable is a frame composed of every RTS from all ETS system.4,0:TC OK;1:TC NOK\n" //
                        +"7ec,34,34,1,0,0,,22345F,62345F,ff,This internal M14N variable is a frame composed of every RTS from all ETS system.5,0:MC OK;1:MC NOK\n" //
                        +"7ec,33,33,1,0,0,,22345F,62345F,ff,This internal M14N variable is a frame composed of every RTS from all ETS system.6,0:MHVB OK;1:MHVB NOK\n" //
                        +"7ec,32,32,1,0,0,,22345F,62345F,ff,This internal M14N variable is a frame composed of every RTS from all ETS system.7,0:METS OK;1:METS NOK\n" //
                        +"7ec,31,31,1,0,0,,22345F,62345F,ff,This internal M14N variable is a frame composed of every RTS from all ETS system.8,0:MHVN OK;1:MHVN NOK\n" //
                        +"7ec,30,30,1,0,0,,22345F,62345F,ff,This internal M14N variable is a frame composed of every RTS from all ETS system.9,0:PWT OK;1:PWT NOK\n" //
                        +"7ec,29,29,1,0,0,,22345F,62345F,ff,This internal M14N variable is a frame composed of every RTS from all ETS system.10,0:Peltier OK;1:Peltier NOK\n" //
                        +"7ec,31,31,1,0,0,,223463,623463,ff,electrical braking torque setpoint,0:unvalid braking torque setpoint;1:valid braking torque setpoint\n" //
                        +"7ec,30,31,1,0,0,,222093,622093,ff,Test of underload made on the freon pressure measure,0:Test conditions not satisfied;1:Reserved;2:Test conditions satisfied and incorrect cooling gas load;3:Test conditions satisfied and correct cooling gas load\n" //
                        +"7ec,31,31,1,0,0,,22212E,62212E,ff,A crash occured and the diagnostic add-on strategy is frozen,0:No crash detected;1:Crash detected\n" //
                        +"7ec,31,31,1,0,0,,222170,622170,ff,CCSL - State of the system causes for normal CCSL deactivation (2nd byte).0,1:vehicle STOP information on the cluster\n" //
                        +"7ec,30,30,1,0,0,,222170,622170,ff,CCSL - State of the system causes for normal CCSL deactivation (2nd byte).1,1:Reserved\n" //
                        +"7ec,29,29,1,0,0,,222170,622170,ff,CCSL - State of the system causes for normal CCSL deactivation (2nd byte).2,1:Reserved\n" //
                        +"7ec,28,28,1,0,0,,222170,622170,ff,CCSL - State of the system causes for normal CCSL deactivation (2nd byte).3,1:Reserved\n" //
                        +"7ec,27,27,1,0,0,,222170,622170,ff,CCSL - State of the system causes for normal CCSL deactivation (2nd byte).4,1:Reserved\n" //
                        +"7ec,26,26,1,0,0,,222170,622170,ff,CCSL - State of the system causes for normal CCSL deactivation (2nd byte).5,1:Reserved\n" //
                        +"7ec,25,25,1,0,0,,222170,622170,ff,CCSL - State of the system causes for normal CCSL deactivation (2nd byte).6,1:Reserved\n" //
                        +"7ec,24,24,1,0,0,,222170,622170,ff,CCSL - State of the system causes for normal CCSL deactivation (2nd byte).7,1:Reserved\n" //
                        +"7ec,30,31,1,0,0,,2233EB,6233EB,ff,Power supply relay of fan requested by EVC,0:open relay;3:closed relay\n" //
                        +"7ec,24,31,1,40,0,°C,223422,623422,ff,External temperature (Peltier side)\n" //
                        +"7ec,31,31,1,0,0,,223466,623466,ff,memorisation flag in NVMY of HV battery over-heating,0:no HV battery over-heating memorisation in NVMY;1:HV battery over-heating memorisation in NVMY\n" //
                        +"7ec,31,31,1,0,0,,223469,623469,ff,Flag to prevent the activation of downgraded mode when MSR function is disabled,0:No MSR Limp Home mode deactivation requested;1:MSR Limp Home mode deactivation requested\n" //
                        +"7ec,24,31,1,40,0,°C,22346A,62346A,ff,High Voltage Battery maximal Temperature from reductant LBC\n" //
                        +"7ec,24,31,1,0,0,,22346B,62346B,ff,No pump allowed neither DCDC if this boolean is set to 0,0:no pump neither DCDC;1:pump and DCDC activation authorized\n" //
                        +"7ec,30,31,1,0,0,,22346C,62346C,ff,CAN signal for PEB charge mode status,0:Unavailable value;1:Not in charge;2:In charge;3:Not used\n" //
                        +"7ec,30,31,1,0,0,,22346D,62346D,ff,Request for PEB Charge mode,0:Unavailable value;1:No charge requested;2:Charge requested;3:Not used\n" //
                        +"7ec,29,31,1,0,0,,22346E,62346E,ff,CAN signal for the PEB class of failure status (on charge),0:Classe A;1:Classe B;2:Classe C;3:Classe D;7:Unavailable value\n" //
                        +"7ec,29,31,1,0,0,,22346F,62346F,ff,Synthesis of JB charger fault type,0:No fault type;1:Fault type 1;2:Fault type 2;3:Unavailable value\n" //
                        +"7ec,24,39,.1,0,0,kW,223470,623470,ff,Available JB2 power for a battery charge\n" //
                        +"7ec,24,31,5,0,0,%,223471,623471,ff,Request for engine fan speed from ClimBox\n" //
                        +"7ec,24,39,.1,0,0,%,223472,623472,ff,Lever 1st track duty cycle\n" //
                        +"7ec,24,39,.1,0,0,%,223473,623473,ff,Lever 2nd track duty cycle\n" //
                        +"7ec,29,31,1,0,0,,223474,623474,ff,PWM gearbox lever track 1 position,0:transient;1:park;2:reverse;3:neutral;4:drive\n" //
                        +"7ec,29,31,1,0,0,,223475,623475,ff,PWM gearbox lever track 2 position,0:transient;1:park;2:reverse;3:neutral;4:drive\n" //
                        +"7ec,48,71,25,0,0,J,223476,623476,ff,Counter during one trip for contributions of anticipation lack and excessive vehicle speed to overconsumption.0\n" //
                        +"7ec,24,47,25,0,0,J,223476,623476,ff,Counter during one trip for contributions of anticipation lack and excessive vehicle speed to overconsumption.1\n" //
                        +"7ec,24,47,.0000000762,-1,0,kWh/km,223477,623477,ff,Raw mean electrical consumption while economical driving\n" //
                        +"7ec,24,47,.0000000762,-1,0,kWh/km,223478,623478,ff,Raw mean consumption per unit of distance\n" //
                        +"7ec,24,47,.0000000596,-1,0,kWh/km,223479,623479,ff,Average over-consumption used for economical monitoring\n" //
                        +"7ec,31,31,1,0,0,,22347A,62347A,ff,Request to turn the reverse lights on (1) during reverse mode,0:Reverse light off;1:Reverse light on\n" //
                        +"7ec,31,31,1,0,0,,22347B,62347B,ff,Pulse box command,0:SOF test not requested;1:SOF test requested\n" //
                        +"7ec,31,31,1,0,0,,22347C,62347C,ff,Pulse box diagnosis status,0:Pulse box not activated;1:Pulse box activated\n" //
                        +"7ec,31,31,1,0,0,,22347D,62347D,ff,Internal errors detected by Battery Current Sensor.0,1:Umin not available\n" //
                        +"7ec,30,30,1,0,0,,22347D,62347D,ff,Internal errors detected by Battery Current Sensor.1,1:Value not consistent\n" //
                        +"7ec,29,29,1,0,0,,22347D,62347D,ff,Internal errors detected by Battery Current Sensor.2,1:Trigger inconsistency - Min found at beginning of interval\n" //
                        +"7ec,28,28,1,0,0,,22347D,62347D,ff,Internal errors detected by Battery Current Sensor.3,1:Trigger inconsistency - Min found at end of interval\n" //
                        +"7ec,24,47,1,0,0,d,22347E,62347E,ff,Memorization of next 4 years alert value in time\n" //
                        +"7ec,31,31,1,0,0,,22347F,62347F,ff,Reinitialisation of memorization of next 4 years alert value in time,0:Do nothing;1:Reinitialize\n" //
                        +"7ec,24,31,1,0,0,,223481,623481,ff,Internal counter for SOF test KO\n" //
                        +"7ec,24,31,1,0,0,,223482,623482,ff,Internal counter for SOF test failed\n" //
                        +"7ec,24,55,1,0,0,min,223483,623483,ff,Time of the last SOF Test that occured\n" //
                        +"7ec,24,39,1,32767,0,A,223484,623484,ff,Current measurement given by BCS Battery Current Sensor\n" //
                        +"7ec,24,39,.001,0,0,V,223485,623485,ff,Voltage measurement given by BCS Battery Current Sensor\n" //
                        +"7ec,29,31,1,0,0,,223486,623486,ff,Alert for changing battery,0:No display;1:Safety Reserved_1;2:Safety Reserved_2;3:Information: Battery should be changed soon;4:Safety Reserved_4;5:Alert Battery should be changed Now;6:Safety Reserved_6;7:Safety Reserved_7\n" //
                        +"7ec,24,55,1,0,0,s,223487,623487,ff,Counter of PWM Efan speed on Low speeds\n" //
                        +"7ec,24,55,1,0,0,s,223488,623488,ff,Counter of PWM Efan speed on Medium speeds\n" //
                        +"7ec,24,55,1,0,0,s,223489,623489,ff,Counter of PWM Efan speed on High speeds\n" //
                        +"7ec,248,279,1,0,0,,22F804,62F804,ff,$09 - OBD calibration identifications.0\n" //
                        +"7ec,216,247,1,0,0,,22F804,62F804,ff,$09 - OBD calibration identifications.1\n" //
                        +"7ec,184,215,1,0,0,,22F804,62F804,ff,$09 - OBD calibration identifications.2\n" //
                        +"7ec,152,183,1,0,0,,22F804,62F804,ff,$09 - OBD calibration identifications.3\n" //
                        +"7ec,120,151,1,0,0,,22F804,62F804,ff,$09 - OBD calibration identifications.4\n" //
                        +"7ec,88,119,1,0,0,,22F804,62F804,ff,$09 - OBD calibration identifications.5\n" //
                        +"7ec,56,87,1,0,0,,22F804,62F804,ff,$09 - OBD calibration identifications.6\n" //
                        +"7ec,24,55,1,0,0,,22F804,62F804,ff,$09 - OBD calibration identifications.7\n" //
                        +"7ec,31,31,1,0,0,,223309,623309,ff,HV relay P1 command,0:Request for opening the main relay P1;1:Request for closing the main relay P1\n" //
                        +"7ec,31,31,1,0,0,,22330A,62330A,ff,HV relay P2 command,0:Request for opening the main relay P2;1:Request for closing the main relay P2\n" //
                        +"7ec,328,343,1,32767,0,A,22348A,62348A,ff,Current memorized for SOF test.1\n" //
                        +"7ec,312,327,1,32767,0,A,22348A,62348A,ff,Current memorized for SOF test.2\n" //
                        +"7ec,296,311,1,32767,0,A,22348A,62348A,ff,Current memorized for SOF test.3\n" //
                        +"7ec,280,295,1,32767,0,A,22348A,62348A,ff,Current memorized for SOF test.4\n" //
                        +"7ec,264,279,1,32767,0,A,22348A,62348A,ff,Current memorized for SOF test.5\n" //
                        +"7ec,248,263,1,32767,0,A,22348A,62348A,ff,Current memorized for SOF test.6\n" //
                        +"7ec,232,247,1,32767,0,A,22348A,62348A,ff,Current memorized for SOF test.7\n" //
                        +"7ec,216,231,1,32767,0,A,22348A,62348A,ff,Current memorized for SOF test.8\n" //
                        +"7ec,200,215,1,32767,0,A,22348A,62348A,ff,Current memorized for SOF test.9\n" //
                        +"7ec,184,199,1,32767,0,A,22348A,62348A,ff,Current memorized for SOF test.10\n" //
                        +"7ec,168,183,1,32767,0,A,22348A,62348A,ff,Current memorized for SOF test.11\n" //
                        +"7ec,152,167,1,32767,0,A,22348A,62348A,ff,Current memorized for SOF test.12\n" //
                        +"7ec,136,151,1,32767,0,A,22348A,62348A,ff,Current memorized for SOF test.13\n" //
                        +"7ec,120,135,1,32767,0,A,22348A,62348A,ff,Current memorized for SOF test.14\n" //
                        +"7ec,104,119,1,32767,0,A,22348A,62348A,ff,Current memorized for SOF test.15\n" //
                        +"7ec,88,103,1,32767,0,A,22348A,62348A,ff,Current memorized for SOF test.16\n" //
                        +"7ec,72,87,1,32767,0,A,22348A,62348A,ff,Current memorized for SOF test.17\n" //
                        +"7ec,56,71,1,32767,0,A,22348A,62348A,ff,Current memorized for SOF test.18\n" //
                        +"7ec,40,55,1,32767,0,A,22348A,62348A,ff,Current memorized for SOF test.19\n" //
                        +"7ec,24,39,1,32767,0,A,22348A,62348A,ff,Current memorized for SOF test.20\n" //
                        +"7ec,480,503,1,0,0,,22348B,62348B,ff,Result of measure memorized for SOF test.1\n" //
                        +"7ec,456,479,1,0,0,,22348B,62348B,ff,Result of measure memorized for SOF test.2\n" //
                        +"7ec,432,455,1,0,0,,22348B,62348B,ff,Result of measure memorized for SOF test.3\n" //
                        +"7ec,408,431,1,0,0,,22348B,62348B,ff,Result of measure memorized for SOF test.4\n" //
                        +"7ec,384,407,1,0,0,,22348B,62348B,ff,Result of measure memorized for SOF test.5\n" //
                        +"7ec,360,383,1,0,0,,22348B,62348B,ff,Result of measure memorized for SOF test.6\n" //
                        +"7ec,336,359,1,0,0,,22348B,62348B,ff,Result of measure memorized for SOF test.7\n" //
                        +"7ec,312,335,1,0,0,,22348B,62348B,ff,Result of measure memorized for SOF test.8\n" //
                        +"7ec,288,311,1,0,0,,22348B,62348B,ff,Result of measure memorized for SOF test.9\n" //
                        +"7ec,264,287,1,0,0,,22348B,62348B,ff,Result of measure memorized for SOF test.10\n" //
                        +"7ec,240,263,1,0,0,,22348B,62348B,ff,Result of measure memorized for SOF test.11\n" //
                        +"7ec,216,239,1,0,0,,22348B,62348B,ff,Result of measure memorized for SOF test.12\n" //
                        +"7ec,192,215,1,0,0,,22348B,62348B,ff,Result of measure memorized for SOF test.13\n" //
                        +"7ec,168,191,1,0,0,,22348B,62348B,ff,Result of measure memorized for SOF test.14\n" //
                        +"7ec,144,167,1,0,0,,22348B,62348B,ff,Result of measure memorized for SOF test.15\n" //
                        +"7ec,120,143,1,0,0,,22348B,62348B,ff,Result of measure memorized for SOF test.16\n" //
                        +"7ec,96,119,1,0,0,,22348B,62348B,ff,Result of measure memorized for SOF test.17\n" //
                        +"7ec,72,95,1,0,0,,22348B,62348B,ff,Result of measure memorized for SOF test.18\n" //
                        +"7ec,48,71,1,0,0,,22348B,62348B,ff,Result of measure memorized for SOF test.19\n" //
                        +"7ec,24,47,1,0,0,,22348B,62348B,ff,Result of measure memorized for SOF test.20\n" //
                        +"7ec,480,503,1,0,0,min,22348C,62348C,ff,Absolut time memorized for SOF test.1\n" //
                        +"7ec,456,479,1,0,0,min,22348C,62348C,ff,Absolut time memorized for SOF test.2\n" //
                        +"7ec,432,455,1,0,0,min,22348C,62348C,ff,Absolut time memorized for SOF test.3\n" //
                        +"7ec,408,431,1,0,0,min,22348C,62348C,ff,Absolut time memorized for SOF test.4\n" //
                        +"7ec,384,407,1,0,0,min,22348C,62348C,ff,Absolut time memorized for SOF test.5\n" //
                        +"7ec,360,383,1,0,0,min,22348C,62348C,ff,Absolut time memorized for SOF test.6\n" //
                        +"7ec,336,359,1,0,0,min,22348C,62348C,ff,Absolut time memorized for SOF test.7\n" //
                        +"7ec,312,335,1,0,0,min,22348C,62348C,ff,Absolut time memorized for SOF test.8\n" //
                        +"7ec,288,311,1,0,0,min,22348C,62348C,ff,Absolut time memorized for SOF test.9\n" //
                        +"7ec,264,287,1,0,0,min,22348C,62348C,ff,Absolut time memorized for SOF test.10\n" //
                        +"7ec,240,263,1,0,0,min,22348C,62348C,ff,Absolut time memorized for SOF test.11\n" //
                        +"7ec,216,239,1,0,0,min,22348C,62348C,ff,Absolut time memorized for SOF test.12\n" //
                        +"7ec,192,215,1,0,0,min,22348C,62348C,ff,Absolut time memorized for SOF test.13\n" //
                        +"7ec,168,191,1,0,0,min,22348C,62348C,ff,Absolut time memorized for SOF test.14\n" //
                        +"7ec,144,167,1,0,0,min,22348C,62348C,ff,Absolut time memorized for SOF test.15\n" //
                        +"7ec,120,143,1,0,0,min,22348C,62348C,ff,Absolut time memorized for SOF test.16\n" //
                        +"7ec,96,119,1,0,0,min,22348C,62348C,ff,Absolut time memorized for SOF test.17\n" //
                        +"7ec,72,95,1,0,0,min,22348C,62348C,ff,Absolut time memorized for SOF test.18\n" //
                        +"7ec,48,71,1,0,0,min,22348C,62348C,ff,Absolut time memorized for SOF test.19\n" //
                        +"7ec,24,47,1,0,0,min,22348C,62348C,ff,Absolut time memorized for SOF test.20\n" //
                        +"7ec,328,343,.001,0,0,V,22348D,62348D,ff,Voltage memorized for SOF test.1\n" //
                        +"7ec,312,327,.001,0,0,V,22348D,62348D,ff,Voltage memorized for SOF test.2\n" //
                        +"7ec,296,311,.001,0,0,V,22348D,62348D,ff,Voltage memorized for SOF test.3\n" //
                        +"7ec,280,295,.001,0,0,V,22348D,62348D,ff,Voltage memorized for SOF test.4\n" //
                        +"7ec,264,279,.001,0,0,V,22348D,62348D,ff,Voltage memorized for SOF test.5\n" //
                        +"7ec,248,263,.001,0,0,V,22348D,62348D,ff,Voltage memorized for SOF test.6\n" //
                        +"7ec,232,247,.001,0,0,V,22348D,62348D,ff,Voltage memorized for SOF test.7\n" //
                        +"7ec,216,231,.001,0,0,V,22348D,62348D,ff,Voltage memorized for SOF test.8\n" //
                        +"7ec,200,215,.001,0,0,V,22348D,62348D,ff,Voltage memorized for SOF test.9\n" //
                        +"7ec,184,199,.001,0,0,V,22348D,62348D,ff,Voltage memorized for SOF test.10\n" //
                        +"7ec,168,183,.001,0,0,V,22348D,62348D,ff,Voltage memorized for SOF test.11\n" //
                        +"7ec,152,167,.001,0,0,V,22348D,62348D,ff,Voltage memorized for SOF test.12\n" //
                        +"7ec,136,151,.001,0,0,V,22348D,62348D,ff,Voltage memorized for SOF test.13\n" //
                        +"7ec,120,135,.001,0,0,V,22348D,62348D,ff,Voltage memorized for SOF test.14\n" //
                        +"7ec,104,119,.001,0,0,V,22348D,62348D,ff,Voltage memorized for SOF test.15\n" //
                        +"7ec,88,103,.001,0,0,V,22348D,62348D,ff,Voltage memorized for SOF test.16\n" //
                        +"7ec,72,87,.001,0,0,V,22348D,62348D,ff,Voltage memorized for SOF test.17\n" //
                        +"7ec,56,71,.001,0,0,V,22348D,62348D,ff,Voltage memorized for SOF test.18\n" //
                        +"7ec,40,55,.001,0,0,V,22348D,62348D,ff,Voltage memorized for SOF test.19\n" //
                        +"7ec,24,39,.001,0,0,V,22348D,62348D,ff,Voltage memorized for SOF test.20\n" //
                        +"7ec,24,31,.05,-240,0,V,22348E,62348E,ff,DCDC voltage control request calculated and used when no default on 14V network is detected\n" //
                        +"7ec,31,31,1,0,0,,22348F,62348F,ff,Boolean Variable : M14N resquest for activation when SOC low. (possible only during a technical WakeUp and DeliveryModeActivation ON),0:Not Activation;1:Activation of DCDC\n" //
                        +"7ec,30,31,1,0,0,,223490,623490,ff,Critical Failure detected on the 14V network (internal SCH),0:Unavailable value;1:C_K_DefaultPresent_status;2:Not used;3:C_K_DefaultConfirmed_status\n" //
                        +"7ec,31,31,1,0,0,,223491,623491,ff,Request of ETS to force 14v network energy management into traction battery (from ETS CAN),0:Not Autorized;1:Autorized\n" //
                        +"7ec,31,31,1,0,0,,223492,623492,ff,Boolean that indicates the loss of a correct measurement on 14V,0:Tension Battery Available;1:Tension Battery Unavailable\n" //
                        +"7ec,31,31,1,0,0,,223493,623493,ff,Authorization from METS to activare DCDC converter,0:Not Autorized DCDC Activation;1:Autorized DCDC Activation\n" //
                        +"7ec,24,39,10,0,0,W,223494,623494,ff,Instantaneous DCDC output power\n" //
                        +"7ec,24,31,.390625,0,0,%,223495,623495,ff,Current ratio from DCDC. (Idcdc / Idcdcmax)\n" //
                        +"7ec,31,31,1,0,0,,223496,623496,ff,14 V battery charge Autorisation flag from METS,0:dcdc activation forbidden;1:dcdc activation authorised\n" //
                        +"7ec,31,31,1,0,0,,223497,623497,ff,Better Place Config,0:NO BetterPlace Vehicle;1:Better Place Vehicle\n" //
                        +"7ec,31,31,1,0,0,,223498,623498,ff,This flag shows the battery relay PTC n°1(X10,L38G2,X61G3) request,0:no activation;1:activation\n" //
                        +"7ec,31,31,1,0,0,,223499,623499,ff,relay thermoplunger (X07) n°1 request,0:no activation;1:activation\n" //
                        +"7ec,31,31,1,0,0,,22349A,62349A,ff,This flag shows the battery relay PTC n°2 (X10,L38G2,X61G3) request,0:no activation;1:activation\n" //
                        +"7ec,31,31,1,0,0,,22349B,62349B,ff,relay thermoplunger (X07) n°2 request,0:no activation;1:activation\n" //
                        +"7ec,31,31,1,0,0,,22349C,62349C,ff,This flag shows the battery relay PTC n°3 (X10,L38G2,X61G3) request,0:no activation;1:activation\n" //
                        +"7ec,31,31,1,0,0,,22349D,62349D,ff,relay thermoplunger (X07) n°3 request,0:no activation;1:activation\n" //
                        +"7ec,31,31,1,0,0,,22349E,62349E,ff,Battery evaporator electrical Valve status (Valve for battery HVAC),0:no activation;1:activation\n" //
                        +"7ec,31,31,1,0,0,,22349F,62349F,ff,Cabin evaporator electrical Valve status (Valve for cabin HVAC),0:no activation;1:activation\n" //
                        +"7ec,24,39,1,0,0,Hz,2234A1,6234A1,ff,Battery blower speed rotation (Feedback signal)\n" //
                        +"7ec,24,39,.1,2730,0,°C,2234A2,6234A2,ff,Battery Evaporator thermal sensor\n" //
                        +"7ec,24,39,.1,2730,0,°C,2234A3,6234A3,ff,Rear Evaporator temperature target\n" //
                        +"7ec,29,31,1,0,0,,2234A4,6234A4,ff,Battery cooling order,0:No_Cool_Bat;1:Cool_Bat_HVAC;2:Cool_Bat_PAC;3:Cool_Bat_Chiller;4:Warm_Bat_Int_CTP;5:Warm_Bat_Ext_CTP;6:Warm_Bat_THP\n" //
                        +"7ec,30,31,1,0,0,,2234A5,6234A5,ff,TC flag ready to sleep,0:NA_0;1:refuse to sleep;2:ready to sleep;3:NA_3\n" //
                        +"7ec,25,31,1,0,0,%,2234A6,6234A6,ff,Battery HVAC speed rotation request (command of the blower)\n" //
                        +"7ec,29,31,1,0,0,,2234A7,6234A7,ff,Battery Cooling System Type,0:without;1:Air Cooling by PAC;2:Air Cooling by HVAC EVC;3:Water Cooling by Chiller;4:Water Cooling without Chiller\n" //
                        +"7ec,29,31,1,0,0,,2234A8,6234A8,ff,Battery Heating System Type,0:without;1:Air PTC by evc Power n°1(2relays);2:Air PTC by evc Power n°2(3relays);3:Air PTC via Mux;4:Water Coolant Heaters(CTP) evc (1 relay)\n" //
                        +"7ec,31,31,1,0,0,,2234A9,6234A9,ff,Configuration of the TCU (Enable or disable),0:sans TCU;1:avec TCU\n" //
                        +"7ec,31,31,1,0,0,,2234AA,6234AA,ff,Differential Protection in EVC,0:NO Differential Protection in EVC;1:Differential Protection in EVC Active\n" //
                        +"7ec,29,31,1,0,0,,2233A6,6233A6,ff,AC compressor DTC Rootcause,0:No fault;1:CPU Self Error;2:Internal Communication Error;3:Short Circuit;4:Open Contact;5:Current sensor error;6:Abnormal Run-up;7:High-Voltage Error\n" //
                        +"7ec,24,39,1,0,0,,223417,623417,ff,Saving of the NAVL state of the interlocks,1:K_InterlockBatt_Interlock_status;2:K_HVBatPEB_Interlock_status;4:K_JunctBox_Interlock_status;8:K_Interlock_peb_emr_status;16:K_SQCharge_Interlock_status;32:K_JB_AC_Interlock_status;64:K_Interlock_CI_Interlock_status;128:K_JB_PTC_Interlock_status;256:K_Interlock_PTC_Interlock_status\n" //
                        +"7ec,31,31,1,0,0,,2E346901,6E346901,ff,Flag to prevent the activation of downgraded mode when MSR function is disabled,0:No MSR Limp Home mode deactivation requested;1:MSR Limp Home mode deactivation requested\n" //
                        +"7ec,31,31,1,0,0,,2E346900,6E346900,ff,Flag to prevent the activation of downgraded mode when MSR function is disabled,0:No MSR Limp Home mode deactivation requested;1:MSR Limp Home mode deactivation requested\n" //
                        +"7ec,24,39,.5,0,0,V,222004,622004,ff,consolidated HV voltage\n" //
                        +"7ec,31,31,1,0,0,,223427,623427,ff,debounced signal from eco mode button,0:switch not pushed;1:switch pushed\n" //
                        +"7ec,16,23,1,0,0,,21EF,61EF,2ff,Hardware Part Number 1\n" //
                        +"7ec,24,31,1,0,0,,21EF,61EF,2ff,Hardware Part Number 2\n" //
                        +"7ec,32,39,1,0,0,,21EF,61EF,2ff,Hardware Part Number 3\n" //
                        +"7ec,40,47,1,0,0,,21EF,61EF,2ff,Hardware Part Number 4\n" //
                        +"7ec,48,55,1,0,0,,21EF,61EF,2ff,Hardware Part Number 5\n" //
                        +"7ec,56,63,1,0,0,,21EF,61EF,2ff,Hardware Part Number 6\n" //
                        +"7ec,64,71,1,0,0,,21EF,61EF,2ff,Hardware Part Number 7\n" //
                        +"7ec,72,79,1,0,0,,21EF,61EF,2ff,Hardware Part Number 8\n" //
                        +"7ec,80,87,1,0,0,,21EF,61EF,2ff,Hardware Part Number 9\n" //
                        +"7ec,88,95,1,0,0,,21EF,61EF,2ff,Hardware Part Number 10\n" //
                        +"7ec,96,103,1,0,0,,21EF,61EF,2ff,Software Part Number 1\n" //
                        +"7ec,104,111,1,0,0,,21EF,61EF,2ff,Software Part Number 2\n" //
                        +"7ec,112,119,1,0,0,,21EF,61EF,2ff,Software Part Number 3\n" //
                        +"7ec,120,127,1,0,0,,21EF,61EF,2ff,Software Part Number 4\n" //
                        +"7ec,128,135,1,0,0,,21EF,61EF,2ff,Software Part Number 5\n" //
                        +"7ec,136,143,1,0,0,,21EF,61EF,2ff,Software Part Number 6\n" //
                        +"7ec,144,151,1,0,0,,21EF,61EF,2ff,Software Part Number 7\n" //
                        +"7ec,152,159,1,0,0,,21EF,61EF,2ff,Software Part Number 8\n" //
                        +"7ec,160,167,1,0,0,,21EF,61EF,2ff,Software Part Number 9\n" //
                        +"7ec,168,175,1,0,0,,21EF,61EF,2ff,Software Part Number 10\n" //
                        +"7ec,16,103,1,0,0,,21B7,61B7,ff,Configuration of Electrical Vehicle Networks\n" //
                        +"7ec,16,103,1,0,0,,21B8,61B8,ff,List of Electrical Vehicle ECUs with After-sales diagnostic\n" //
                        +"7ec,30,31,1,0,0,,2234B2,6234B2,ff,Gasoline heater configuration V2,0:no heater;1:Eberspächer Hydronic 2;2:Do not use (2);3:Do not use (3)\n" //
                        +"7ec,30,31,1,0,0,,2234B3,6234B3,ff,PTC_Config V2,0:no HV PTC;1:HV PTC with water;2:HV PTC with air\n" //
                        +"7ec,30,31,1,0,0,,2234B1,6234B1,ff,Compressor Configuration V2,0:no compressor;1:Denso compressor;2:Do not use (2);3:Do not use (3)\n" //
                        +"7ec,24,39,1,32768,0,A,2234AD,6234AD,ff,Set-point for the charge current for the JB2 (I_JB2SetPoint_ets_req 34AD)\n" //
                        +"7ec,24,31,1,0,0,,2234AE,6234AE,ff,PEB frame monitoring status flags (Vxx_sfty_mon_emot_n_mem 34AE).all\n" //
                        +"7ec,31,31,1,0,0,,2234AE,6234AE,ff,PEB frame monitoring status flags (Vxx_sfty_mon_emot_n_mem 34AE).B0,0:Default on engine speed frame sent by PEB (see other bits);1:No default on engine speed frame sent by PEB\n" //
                        +"7ec,30,30,1,0,0,,2234AE,6234AE,ff,PEB frame monitoring status flags (Vxx_sfty_mon_emot_n_mem 34AE).B1,0:No clock default;1:Clock default\n" //
                        +"7ec,29,29,1,0,0,,2234AE,6234AE,ff,PEB frame monitoring status flags (Vxx_sfty_mon_emot_n_mem 34AE).B2,0:No CRC default;1:CRC default\n" //
                        +"7ec,27,27,1,0,0,,2234AE,6234AE,ff,PEB frame monitoring status flags (Vxx_sfty_mon_emot_n_mem 34AE).B4,0:Frame presence;1:No frame\n" //
                        +"7ec,31,31,1,0,0,,2234AF,6234AF,ff,EVC waiting for isolation confirmation before starting charge (B_MngETS_ChgWait_HvIns 34AF),0:Driving mode;1:Waiting for charge\n" //
                        +"7ec,31,31,1,0,0,,2234B0,6234B0,ff,Configuration of the Load Shedding (LoadSheddingConfig 34B0),0:No load shedding;1:Load shedding\n" //
                        +"7ec,30,31,1,0,0,,2234DC,6234DC,ff,CAN signal for PEB charge mode status V2,0:Unavailable value;1:Not in charge;2:In charge;3:Not used\n" //
                        +"7ec,30,31,1,0,0,,2234DD,6234DD,ff,Request for PEB Charge mode V2,0:Unavailable value;1:No charge requested;2:Charge requested;3:Not used\n" //
                        +"7ec,30,31,1,0,0,,2234DE,6234DE,ff,Compressor Configuration V3,0:no compressor;1:Denso compressor;2:Do not use (2);3:Do not use (3)\n" //
                        +"7ec,31,31,1,0,0,,2234B4,6234B4,ff,Cut off charge request by thermal comfort to start compressor with safety conditions (34B4),0:Cut off for Charge requested;1:No cut off for charge requested\n" //
                        +"7ec,30,31,1,0,0,,2234B5,6234B5,ff,Authorization for deicing function ($34B5)\n" //
                        +"7ec,24,31,1,0,0,Wh,2234B6,6234B6,ff,Pre-conditionning available energy ($34B6)\n" //
                        +"7ec,29,31,1,0,0,,2234B7,6234B7,ff,Defaults and state of the compressor description ($34B7),0:On;1:Off;2:On downgraded;3:Off with fault;4:Off to change\n" //
                        +"7ec,31,31,1,0,0,,2234B8,6234B8,ff,Request for activation of preconditionning from EVC to Climbox ($34B8),0:No PC requested;1:PC requested\n" //
                        +"7ec,30,31,1,0,0,,2234B9,6234B9,ff,Pre Conditionning activation request from cluster ($34B9),0:No PreHeating;1:PreHeating Prog n1;2:PreHeating Prog n2\n" //
                        +"7ec,30,31,1,0,0,,2234BA,6234BA,ff,Pre-conditionning request memorized by EVC ($34BA)\n" //
                        +"7ec,30,31,1,0,0,,2234BB,6234BB,ff,Request for low level battery ($34BB),0:No alert;1:Not used;2:Battery low alert;3:Unavailable\n" //
                        +"7ec,30,31,1,0,0,,2234BC,6234BC,ff,Request for flashers when Immediate PreHeating is requested ($34BC),0:Unavailable;1:No pre heat requested;2:Immediate pre heating authorized;3:Immediate pre heating not authorized\n" //
                        +"7ec,24,31,1,0,0,min,2234BD,6234BD,ff,Left time calculated before the customer hour programmation ($34BD)\n" //
                        +"7ec,24,39,1,0,0,min,2234BE,6234BE,ff,Local time received by EVC ($34BE)\n" //
                        +"7ec,30,31,1,0,0,,2234BF,6234BF,ff,Immediate pre-conditionning request ($34BF),0:C_METS_TC_NoAuth (No Authorization);1:C_METS_TC_PTCAuth (PTC Authorization only);2:C_METS_TC_ACAuth (AC Authorization only);3:C_METS_TC_Auth ( PTC and AC Authorization)\n" //
                        +"7ec,30,31,1,0,0,,2234C1,6234C1,ff,Pre Conditionning activation request from climbox ($34C1),0:No PreHeating;1:PreHeating Prog n1;2:PreHeating Prog n2\n" //
                        +"7ec,30,31,1,0,0,,2234C2,6234C2,ff,Defaults and state of the HV PTC description ($34C2),0:On;1:Off;2:On downgraded;3:Off with fault\n" //
                        +"7ec,24,39,1,0,0,min,2234C3,6234C3,ff,Scheduled time 1 requested by the customer for the preconditionning system ($34C3)\n" //
                        +"7ec,24,39,1,0,0,min,2234C4,6234C4,ff,Scheduled time 2 requested by the customer for the preconditionning system ($34C4)\n" //
                        +"7ec,31,31,1,0,0,,2234C5,6234C5,ff,Request to the BCM to start the counter for vehicle wakeup due to preconditionning ($34C5)\n" //
                        +"7ec,31,31,1,0,0,,2234C6,6234C6,ff,Request to the BCM to stop the counter for vehicle wakeup due to preconditionning ($34C6)\n" //
                        +"7ec,30,31,1,0,0,,2234C7,6234C7,ff,Supposed customer departure value ($34C7)\n" //
                        +"7ec,24,31,10,0,0,kW,2234C8,6234C8,ff,Power available for Climate functions ($34C8)\n" //
                        +"7ec,24,39,10,0,0,kW,2234C9,6234C9,ff,Available charging power necessary to authorize some functions (preconditionning, battery cooling)($34C9)\n" //
                        +"7ec,24,31,1,0,0,W,2234CA,6234CA,ff,Available power for preconditionning ($34CA)\n" //
                        +"7ec,24,31,1,0,0,%,2234CB,6234CB,ff,Cabin blower command ($34CB)\n" //
                        +"7ec,24,39,1,0,0,min,2234CC,6234CC,ff,Scheduled Time 1 memorized by EVC ($34CC)\n" //
                        +"7ec,24,39,1,0,0,min,2234CD,6234CD,ff,Scheduled Time 2 memorized by EVC ($34CD)\n" //
                        +"7ec,31,31,1,0,0,,2234CE,6234CE,ff,Manage Failure Authorization of the HV PTC ($34CE),0:HV PTC not authorized;1:HV PTC authorized\n" //
                        +"7ec,24,31,1,0,0,,2234CF,6234CF,ff,Power consumed by the 400V components for Thermal Comfort ($34CF)\n" //
                        +"7ec,24,55,.01,0,0,km,223449,623449,ff,Total vehicle distance associated with each last ten Quick Drop.9\n" //
                        +"7ec,56,87,.01,0,0,km,223449,623449,ff,Total vehicle distance associated with each last ten Quick Drop.8\n" //
                        +"7ec,88,119,.01,0,0,km,223449,623449,ff,Total vehicle distance associated with each last ten Quick Drop.7\n" //
                        +"7ec,120,151,.01,0,0,km,223449,623449,ff,Total vehicle distance associated with each last ten Quick Drop.6\n" //
                        +"7ec,152,183,.01,0,0,km,223449,623449,ff,Total vehicle distance associated with each last ten Quick Drop.5\n" //
                        +"7ec,184,215,.01,0,0,km,223449,623449,ff,Total vehicle distance associated with each last ten Quick Drop.4\n" //
                        +"7ec,216,247,.01,0,0,km,223449,623449,ff,Total vehicle distance associated with each last ten Quick Drop.3\n" //
                        +"7ec,248,279,.01,0,0,km,223449,623449,ff,Total vehicle distance associated with each last ten Quick Drop.2\n" //
                        +"7ec,280,311,.01,0,0,km,223449,623449,ff,Total vehicle distance associated with each last ten Quick Drop.1\n" //
                        +"7ec,312,343,.01,0,0,km,223449,623449,ff,Total vehicle distance associated with each last ten Quick Drop.0\n" //
                        +"7ec,102,103,1,0,0,,223382,623382,ff,AC compressor Error Bits Recordings.-1.00 CI_Error_Status,0:No default;1:Degraded mode;2:Compressor shut-off safety reasons;3:Compressor has to be changed\n" //
                        +"7ec,92,92,1,0,0,,223382,623382,ff,AC compressor Error Bits Recordings.-1.11 Internal_communication_error,0:No default;1:Default present\n" //
                        +"7ec,101,101,1,0,0,,223382,623382,ff,AC compressor Error Bits Recordings.-1.02 High_Voltage_error,0:No default;1:Default present\n" //
                        +"7ec,100,100,1,0,0,,223382,623382,ff,AC compressor Error Bits Recordings.-1.03 Abnormal_Run_up,0:No default;1:Default present\n" //
                        +"7ec,93,93,1,0,0,,223382,623382,ff,AC compressor Error Bits Recordings.-1.10 Over_heat_warning,0:No default;1:Default present\n" //
                        +"7ec,99,99,1,0,0,,223382,623382,ff,AC compressor Error Bits Recordings.-1.04 Over_heat,0:No default;1:Default present\n" //
                        +"7ec,98,98,1,0,0,,223382,623382,ff,AC compressor Error Bits Recordings.-1.05 Over_load,0:No default;1:Default present\n" //
                        +"7ec,97,97,1,0,0,,223382,623382,ff,AC compressor Error Bits Recordings.-1.06 Current_sensor_Error,0:No default;1:Default present\n" //
                        +"7ec,96,96,1,0,0,,223382,623382,ff,AC compressor Error Bits Recordings.-1.07 Open_contact,0:No default;1:Default present\n" //
                        +"7ec,95,95,1,0,0,,223382,623382,ff,AC compressor Error Bits Recordings.-1.08 Short_Circuit,0:No default;1:Default present\n" //
                        +"7ec,94,94,1,0,0,,223382,623382,ff,AC compressor Error Bits Recordings.-1.09 Low_Voltage_error,0:No default;1:Default present\n" //
                        +"7ec,88,88,1,0,0,,223382,623382,ff,AC compressor Error Bits Recordings.-1.15 Under_voltage_HV,0:No default;1:Default present\n" //
                        +"7ec,72,87,1,0,0,,223382,623382,ff,AC compressor Error Bits Recordings.-2\n" //
                        +"7ec,56,71,1,0,0,,223382,623382,ff,AC compressor Error Bits Recordings.-3\n" //
                        +"7ec,40,55,1,0,0,,223382,623382,ff,AC compressor Error Bits Recordings.-4\n" //
                        +"7ec,24,39,1,0,0,,223382,623382,ff,AC compressor Error Bits Recordings.-5\n" //
                        +"7ec,91,91,1,0,0,,223382,623382,ff,AC compressor Error Bits Recordings.-1.12 Voltage_Restriction,0:No default;1:Default present\n" //
                        +"7ec,90,90,1,0,0,,223382,623382,ff,AC compressor Error Bits Recordings.-1.13 Current_Restriction,0:No default;1:Default present\n" //
                        +"7ec,89,89,1,0,0,,223382,623382,ff,AC compressor Error Bits Recordings.-1.14 Over_voltage_HV,0:No default;1:Default present\n" //
                        +"7ec,24,39,1,0,0,rpm,2233AE,6233AE,ff,AC compressor RPM Command\n" //
                        +"7ec,24,31,25,0,0,W,2233A7,6233A7,ff,AC compressor Power Consumption Estimation\n" //
                        +"7ec,30,31,1,0,0,,223377,623377,ff,AC compressor Interlock State,0:Unvailable Value;1:Open;2:Closed;3:Sensor Unavailable\n" //
                        +"7ec,24,24,1,0,0,,222875,622875,ff,External controls denial status flag.7,0:Temperature before turbine correcte;1:Temperature before turbine too high\n" //
                        +"7ec,25,25,1,0,0,,222875,622875,ff,External controls denial status flag.6,0:Pression turbo correcte;1:Turbocharger/Manifold overpressure\n" //
                        +"7ec,26,26,1,0,0,,222875,622875,ff,External controls denial status flag.5,0:Normal Engine Speed;1:Engine overspeed\n" //
                        +"7ec,27,27,1,0,0,,222875,622875,ff,External controls denial status flag.4,0:Stop and Start not ON;1:Stop and Start not OFF\n" //
                        +"7ec,28,28,1,0,0,,222875,622875,ff,External controls denial status flag.3,0:Not null vehicle speed;1:Unavailable vehicle speed\n" //
                        +"7ec,29,29,1,0,0,,222875,622875,ff,External controls denial status flag.2,0:Accelerator pedal Not presed;1:Pressed accelerator pedal\n" //
                        +"7ec,30,30,1,0,0,,222875,622875,ff,External controls denial status flag.1,0:Ignition in ON position;1:Ignition in OFF position\n" //
                        +"7ec,31,31,1,0,0,,222875,622875,ff,External controls denial status flag.0,0:MGB in neutral position OR other kind of transmission in park or in neutral position;1:BVA/BVR en position autre que N ou P\n" //
                        +"7ec,32,32,1,0,0,,222875,622875,ff,External controls denial status flag.15,0:-;1:reserved fot futur use\n" //
                        +"7ec,33,33,1,0,0,,222875,622875,ff,External controls denial status flag.14,0:-;1:reserved fot futur use\n" //
                        +"7ec,34,34,1,0,0,,222875,622875,ff,External controls denial status flag.13,0:-;1:reserved fot futur use\n" //
                        +"7ec,35,35,1,0,0,,222875,622875,ff,External controls denial status flag.12,0:-;1:reserved fot futur use\n" //
                        +"7ec,36,36,1,0,0,,222875,622875,ff,External controls denial status flag.11,0:brake pedal not pressed;1:brake pedal pressed\n" //
                        +"7ec,37,37,1,0,0,,222875,622875,ff,External controls denial status flag.10,0:Normal coolant temperature;1:Excessive coolant temperature\n" //
                        +"7ec,38,38,1,0,0,,222875,622875,ff,External controls denial status flag.9,0:Normal Manifold pressure;1:Manifold overpressure\n" //
                        +"7ec,39,39,1,0,0,,222875,622875,ff,External controls denial status flag.8,0:pression rail correcte;1:Excessive rail pressure\n" //

                ;

        String dtcDef =
                ""

                        +"0022,Mastervac brake assistance system\n" //
                        +"0023,Brake assistance Vacuum Pump\n" //
                        +"0026,Mastervac control unit\n" //
                        +"0027,Mastervac sensor power supply\n" //
                        +"0060,Door switch\n" //
                        +"0070,Shift Lock electrical control circuit\n" //
                        +"0071,Gear lever\n" //
                        +"0080,Charge spot\n" //
                        +"0105,Passenger compartment Thermistor\n" //
                        +"0106,Heat water temperature sensor\n" //
                        +"0121,Heater request relay electrical command circuit\n" //
                        +"0130,Pulser relay power control circuit\n" //
                        +"0140,Feedback Heater Water pump of passenger compartement\n" //
                        +"0141,Heater Water pump of passenger compartement\n" //
                        +"0152,Computer of Air Conditioning\n" //
                        +"0156,Evaporator sensor for AC\n" //
                        +"0160,Heater Led control circuit\n" //
                        +"0170,Pulser power supply relay\n" //
                        +"0171,Cooling Fan Motor\n" //
                        +"0225,Accelerator Pedal Position Sensor - Track 1\n" //
                        +"0226,Accelerator Pedal Position Sensor\n" //
                        +"0301,Underhood switch module (USM)\n" //
                        +"0303,EVC\n" //
                        +"0304,Power electronic bloc\n" //
                        +"0305,Lithium Battery Controler\n" //
                        +"0306,Battery Charger Bloc\n" //
                        +"0307,Power electronic bloc data\n" //
                        +"0404,14v static converter in Default (from CAN)\n" //
                        +"0405,14v static converter\n" //
                        +"0406,EM inverter in Default (from CAN)\n" //
                        +"0411,Battery Charger Bloc in Default (from CAN)\n" //
                        +"0414,High voltage network\n" //
                        +"0420,LBC in Default (from CAN)\n" //
                        +"0431,Charge Interlock\n" //
                        +"0432,Power train interlocks\n" //
                        +"0433,Comfort or Passenger compartment Thermistor Interlock\n" //
                        +"0434,Comfort or Air conditionned Interlock\n" //
                        +"0440,Electric Insulation\n" //
                        +"0441,Insulation Sensor\n" //
                        +"0460,Engine Coolant Temperature Sensor\n" //
                        +"0461,Cooling valve 1 control circuit\n" //
                        +"0462,Feedback driving mode water pump\n" //
                        +"0463,Driving mode water pump\n" //
                        +"0464,Feedback load mode water pump\n" //
                        +"0465,Load mode water pump\n" //
                        +"0468,Peltier fan request electrical command circuit\n" //
                        +"0469,Peltier external fan\n" //
                        +"0470,HV voltage sensor\n" //
                        +"0480,Battery high voltage power relay A\n" //
                        +"0491,Battery high voltage lock loop 1\n" //
                        +"0495,Temperature sensors from CAN\n" //
                        +"0503,Mastervac Voltage supply\n" //
                        +"0504,Brake Pedal\n" //
                        +"0530,A/C Refrigerant Pressure Sensor\n" //
                        +"0560,Battery Voltage 14 V\n" //
                        +"0564,Cruise Control/Speed Limiter - Paddle Control Button\n" //
                        +"0571,Closed Brake Sensor\n" //
                        +"0574,Cruise Control/Speed Limiter - Displayed Speed\n" //
                        +"0575,Cruise Control/Speed Limiter - Switch Button\n" //
                        +"0606,Engine Control Module - Processor\n" //
                        +"0610,ECM torque calculation\n" //
                        +"0641,Voltage supply range 1\n" //
                        +"0651,Voltage supply range 2\n" //
                        +"0657,Powerlatch relay\n" //
                        +"0703,Open Brake Pedal Switch\n" //
                        +"0833,Begining of Clutch Pedal Move Switch\n" //
                        +"1525,Cruise Control/Speed Limiter Deactivation\n" //
                        +"2120,Accelerator Pedal Position Sensor - Track 2\n" //
                        +"060B,Engine Control Module - A/D Converter\n" //
                        +"160C,Internal Engine Control Module Error\n" //
                        +"046A,Peltier internal fan\n" //
                        +"046B,Feedback of Peltier external fan\n" //
                        +"046C,Feedback of Peltier internal fan\n" //
                        +"046D,Peltier request relay electrical command circuit\n" //
                        +"C121,Lost Communication With Anti-Lock Brake System (ABS) Control Module\n" //
                        +"C415,Invalid Data Received From Anti-Lock Brake System Control Module\n" //
                        +"0525,Cruise Control Servo Control Circuit Range/Performance\n" //
                        +"0601,Electric Motor Prestation\n" //
                        +"0435,Interlock Sensor\n" //
                        +"0490,Battery high voltage lock\n" //
                        +"0020,Vacuum Sensor pressure\n" //
                        +"046E,Peltier Temperature\n" //
                        +"047E,Peltier Cells\n" //
                        +"0081,Charge plug trigger\n" //
                        +"0492,Battery high voltage lock loop 2\n" //
                        +"0302,Air conditionned system\n" //
                        +"0161,Self preconditioning relay\n" //
                        +"C418,Invalid data received from uncoupled brake pedal control module\n" //
                        +"030A,Inverter (for charge function)\n" //
                        +"0308,Lithium battery (reductant dignosis)\n" //
                        +"0471,HV Battery\n" //
                        +"0502,Battery temperature sensors\n" //
                        +"0120,Heater electrical command circuit\n" //
                        +"0500,No ETS Sleeping that leads to a flat lead 12V battery\n" //
                        +"0011,Reverse Light relay command\n" //
                        +"0063,Door plug command\n" //
                        +"0072,Gear lever sensor - track 1\n" //
                        +"0073,Gear lever sensor - track 2\n" //
                        +"0090,Eco mode switch\n" //
                        +"0180,Battery Conditioning system (AC, CTP)\n" //
                        +"0481,Battery high voltage power relay P2\n" //
                        +"0482,Battery high voltage power relay P1\n" //
                        +"0483,Battery high voltage power relay A or P1\n" //
                        +"0485,Battery high voltage power relay A or P2\n" //
                        +"0486,Battery high voltage power relay P1 or P2\n" //
                        +"0510,State of function (SOF) of 14V battery\n" //
                        +"0511,14V Battery current sensor\n" //
                        +"0512,Pulse Box\n" //
                        +"C122,Communication With Vehicle Dynamics Control Module\n" //
                        +"C198,Communication With Telematic Control Module\n" //
                        +"C140,Communication With Body Control Module\n" //
                        +"C155,Communication With Instrument Panel Cluster (IPC) Control Module\n" //
                        +"0062,Door plug status\n" //
                        +"C146,Communication With Gateway 'A' (BIC)\n" //
                        +"C164,Communication With HVAC Control Module\n" //
                        +"0181,Battery CTP/THP relay 1\n" //
                        +"0182,Battery CTP/THP relay 2\n" //
                        +"0183,Battery CTP/THP relay 3\n" //
                        +"0184,Battery HVAC relay electrovalve 1\n" //
                        +"0185,Battery HVAC relay electrovalve 2\n" //
                        +"0186,Battery HVAC fan out (command)\n" //
                        +"0187,Battery HVAC temperature sensor\n" //
                        +"C103,Communication With Gear Shift Control Module (Shifter)\n" //
                        +"060A,Engine Control Module - Electronic Stability Program - Control Function (Level 2)\n" //
                        +"C129,Communication With Brake System Control Module\n" //
                        +"0010,Stop Light relay command\n" //
                        +"04A0,SOC error during charge\n" //
                        +"0188,Battery blower rotation speed feedback line\n" //
                        +"0415,Power electronic bloc (safety level 2 detection)\n" //
                        +"0442,Insulation motor\n" //
                        +"0407,DCDC C class default\n" //
                        +"C19A,Communication with CSHV Unit (Dedicated for Crash when APC is off for daimler application)\n" //
                        +"C199,Communication with Energy Management Module\n" //
                        +"1654,Communication with Aribag Control module\n" //
                        +"0436,JB interlocks\n" //
                        +"C19B,Communication with LBC8 Unit\n" //

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
                        +"0A,Not compatible\n" //
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
                        +"20,test\n" //
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
                        +"F1,Safety Case 1\n" //
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

        Frames.getInstance().load("7EC,0,0,EVC\n");
        Fields.getInstance().load(fieldDef1);
        Fields.getInstance().loadMore(fieldDef1);
        Fields.getInstance().loadMore(fieldDef2);
        Dtcs.getInstance().load(dtcDef, testDef);

    }
}
