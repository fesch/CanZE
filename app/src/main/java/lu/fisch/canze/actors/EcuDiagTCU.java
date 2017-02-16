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

public class EcuDiagTCU {

    void load () {

        String fieldDef1 =
                ""
                        +"7da,0,23,1,0,0,,19023b,5902ff,ff\n" // Query DTC

                        +"7da,24,39,1,0,0,,22FE12,62FE12,ff,Last Wake Up Cause.Cause of last Wake Up,0:None;1:Reset;2:RTC;8:Movement sensor;16:Wake up 1;32:Wake up 2;64:LIN;128:CAN\n" //
                        +"7da,24,31,1,0,0,,226C52,626C52,ff,PPP Connection Retry\n" //
                        +"7da,24,39,1,0,0,s,226C54,626C54,ff,PPP Failure Time out\n" //
                        +"7da,24,39,1,0,0,s,226C56,626C56,ff,PPP Disconnection time out\n" //
                        +"7da,24,31,1,0,0,,226C58,626C58,ff,PPP Rejection retry,0:No retry until next ON cycle;1:Retry as PPP sequence timeout\n" //
                        +"7da,24,39,1,0,0,,220107,620107,ff,Vehicle Definition,0:Default value;1:X61VE;2:X61ice;17:X61ph2;256:L38VE;257:L38ph2;513:X10;1025:X98;1538:X62th;2049:X45;2305:X87;2561:X95ph2;3585:X43\n" //
                        +"7da,36,36,1,0,0,,226C6A,626C6A,ff,Probe Service Activation Status.REMOTE_DIAG.activation,0:do nothing;1:activate REMOTE DIAG\n" //
                        +"7da,30,30,1,0,0,,226C6A,626C6A,ff,Probe Service Activation Status.PAYD.Deactivation,0:do nothing;1:Deactivate PAYD\n" //
                        +"7da,38,38,1,0,0,,226C6A,626C6A,ff,Probe Service Activation Status.PAYD.activation,0:do nothing;1:activate PAYD\n" //
                        +"7da,31,31,1,0,0,,226C6A,626C6A,ff,Probe Service Activation Status.FLEET.Deactivation,0:do nothing;1:Deactivate FLEET\n" //
                        +"7da,39,39,1,0,0,,226C6A,626C6A,ff,Probe Service Activation Status.FLEET.activation,0:do nothing;1:activate FLEET\n" //
                        +"7da,29,29,1,0,0,,226C6A,626C6A,ff,Probe Service Activation Status.ECO.Deactivation,0:do nothing;1:Deactivate ECO\n" //
                        +"7da,37,37,1,0,0,,226C6A,626C6A,ff,Probe Service Activation Status.ECO.activation,0:do nothing;1:activate ECO\n" //
                        +"7da,28,28,1,0,0,,226C6A,626C6A,ff,Probe Service Activation Status.REMOTE_DIAG.Deactivation,0:do nothing;1:Deactivate REMOTE DIAG\n" //
                        +"7da,24,47,1,0,0,s,226C6B,626C6B,ff,Probe Status Data Frequency Upload\n" //
                        +"7da,24,39,1,0,0,s,226C6C,626C6C,ff,Probe Server Demand Checking Frequency\n" //
                        +"7da,24,47,1,0,0,h,226C6E,626C6E,ff,EV Status Data Frequency Upload\n" //
                        +"7da,24,39,1,0,0,s,226C76,626C76,ff,EV ChargeIn Progress TimeOut\n" //
                        +"7da,24,39,1,0,0,s,226C74,626C74,ff,EV WakeUp Frequency\n" //
                        +"7da,24,1047,1,0,0,,226C10,626C10,2ff,OBS URL\n" //
                        +"7da,24,279,1,0,0,,226C12,626C12,2ff,OBS PORT\n" //
                        +"7da,24,31,1,0,0,,226C62,626C62,ff,External GSM Antenna,0:No External GSM antenna connection;1:External GSM antenna connection\n" //
                        +"7da,29,39,1,0,0,,22FE03,62FE03,ff,Temperature Status.PCBTEMPSENSE\n" //
                        +"7da,45,55,1,0,0,,22FE03,62FE03,ff,Temperature Status.BATTERY_TEMP_SENSE\n" //
                        +"7da,108,119,1,0,0,,22FE02,62FE02,ff,Parameters.Status.SPK_MMI_N_SENS\n" //
                        +"7da,28,39,1,0,0,,22FE02,62FE02,ff,Parameters.Status.SENSE_MIC1_P\n" //
                        +"7da,60,71,1,0,0,,22FE02,62FE02,ff,Parameters.Status.SPK_HU_P_SENS\n" //
                        +"7da,44,55,1,0,0,,22FE02,62FE02,ff,Parameters.Status.SPK_HU_N_SENS\n" //
                        +"7da,92,103,1,0,0,,22FE02,62FE02,ff,Parameters.Status.SENSE_MUTE\n" //
                        +"7da,76,87,1,0,0,,22FE02,62FE02,ff,Parameters.Status.OUTPUT_ON_REQ_SENSE\n" //
                        +"7da,124,135,1,0,0,,22FE02,62FE02,ff,Parameters.Status.SENSE_MIC_OUT_P\n" //
                        +"7da,28,39,219.3,-23.8139534883721,0,,22FE04,62FE04,ff,Rtc.Data.12V_BAT_SENSE\n" //
                        +"7da,60,71,36,0,0,,22FE04,62FE04,ff,Rtc.Data.SENSE_3V_PMC\n" //
                        +"7da,44,55,1,0,0,,22FE04,62FE04,ff,Rtc.Data.SGN_WAKE_UP_SENSE,0:signal is not present;4096:signal Failure\n" //
                        +"7da,76,87,1,0,0,,22FE04,62FE04,ff,Rtc.Data.SGN_WAKE_UP2_SENSE,0:signal is not present;4096:signal Failure\n" //
                        +"7da,16,39,1,0,0,,2184,6184,2ff,SupplierNumber.ITG\n" //
                        +"7da,40,71,1,0,0,,2184,6184,2ff,Traceability year\n" //
                        +"7da,72,87,1,0,0,,2184,6184,2ff,Traceability week\n" //
                        +"7da,88,135,1,0,0,,2184,6184,2ff,Tracability serial number\n" //
                        +"7da,16,55,1,0,0,,21FC,61FC,2ff,IdentPreviousZone.RenaultReference\n" //
                        +"7da,56,63,1,0,0,,21FC,61FC,ff,IdentPreviousZone.DiagnosticIdentificationCode\n" //
                        +"7da,64,87,1,0,0,,21FC,61FC,2ff,IdentPreviousZone.SupplierNumber\n" //
                        +"7da,88,127,1,0,0,,21FC,61FC,2ff,IdentPreviousZone.HardwareNumber.LowerPart\n" //
                        +"7da,128,143,1,0,0,,21FC,61FC,ff,IdentPreviousZone.SoftwareNumber\n" //
                        +"7da,144,159,1,0,0,,21FC,61FC,ff,IdentPreviousZone.EditionNumber\n" //
                        +"7da,160,175,1,0,0,,21FC,61FC,ff,IdentPreviousZone.CalibrationNumber\n" //
                        +"7da,176,183,1,0,0,,21FC,61FC,ff,IdentPreviousZone.PartNumber.BasicPartList\n" //
                        +"7da,184,191,1,0,0,,21FC,61FC,ff,IdentPreviousZone.HardwareNumber.BasicPartList\n" //
                        +"7da,192,199,1,0,0,,21FC,61FC,ff,IdentPreviousZone.ApprovalNumber.BasicPartList\n" //
                        +"7da,200,207,1,0,0,,21FC,61FC,ff,IdentPreviousZone.ManufacturerIdentificationCode,0:Renault R1;128:Nissan N1;129:Nissan N2;130:Nissan N3;136:5DIGITS;255:Renault R3\n" //
                        +"7da,16,55,1,0,0,,21F0,61F0,2ff,IdentFirstZone.RenaultReference\n" //
                        +"7da,56,63,1,0,0,,21F0,61F0,ff,IdentFirstZone.DiagnosticIdentificationCode\n" //
                        +"7da,64,87,1,0,0,,21F0,61F0,2ff,IdentFirstZone.SupplierNumber\n" //
                        +"7da,88,127,1,0,0,,21F0,61F0,2ff,IdentFirstZone.HardwareNumber.LowerPart\n" //
                        +"7da,128,143,1,0,0,,21F0,61F0,ff,IdentFirstZone.SoftwareNumber\n" //
                        +"7da,144,159,1,0,0,,21F0,61F0,ff,IdentFirstZone.EditionNumber\n" //
                        +"7da,160,175,1,0,0,,21F0,61F0,ff,IdentFirstZone.CalibrationNumber\n" //
                        +"7da,176,183,1,0,0,,21F0,61F0,ff,IdentFirstZone.PartNumber.BasicPartList\n" //
                        +"7da,184,191,1,0,0,,21F0,61F0,ff,IdentFirstZone.HardwareNumber.BasicPartList\n" //
                        +"7da,192,199,1,0,0,,21F0,61F0,ff,IdentFirstZone.ApprovalNumber.BasicPartList\n" //
                        +"7da,200,207,1,0,0,,21F0,61F0,ff,IdentFirstZone.ManufacturerIdentificationCode,0:Renault R1;128:Nissan N1;129:Nissan N2;130:Nissan N3;136:5DIGITS;255:Renault R3\n" //
                        +"7da,16,55,1,0,0,,21FE,61FE,2ff,IdentCurrentZone.RenaultReference\n" //
                        +"7da,56,63,1,0,0,,21FE,61FE,ff,IdentCurrentZone.DiagnosticIdentificationCode\n" //
                        +"7da,64,87,1,0,0,,21FE,61FE,2ff,IdentCurrentZone.SupplierNumber\n" //
                        +"7da,88,127,1,0,0,,21FE,61FE,2ff,IdentCurrentZone.HardwareNumber.LowerPart\n" //
                        +"7da,128,143,1,0,0,,21FE,61FE,ff,IdentCurrentZone.SoftwareNumber\n" //
                        +"7da,144,159,1,0,0,,21FE,61FE,ff,IdentCurrentZone.EditionNumber\n" //
                        +"7da,160,175,1,0,0,,21FE,61FE,ff,IdentCurrentZone.CalibrationNumber\n" //
                        +"7da,176,183,1,0,0,,21FE,61FE,ff,IdentCurrentZone.PartNumber.BasicPartList\n" //
                        +"7da,184,191,1,0,0,,21FE,61FE,ff,IdentCurrentZone.HardwareNumber.BasicPartList\n" //
                        +"7da,192,199,1,0,0,,21FE,61FE,ff,IdentCurrentZone.ApprovalNumber.BasicPartList\n" //
                        +"7da,200,207,1,0,0,,21FE,61FE,ff,IdentCurrentZone.ManufacturerIdentificationCode,0:Renault R1;128:Nissan N1;129:Nissan N2;130:Nissan N3;136:5DIGITS;255:Renault R3\n" //
                        +"7da,96,135,1,0,0,,21F1,61F1,2ff,LogFirstZone.SoftDownloadToolRef\n" //
                        +"7da,144,167,1,0,0,,21F1,61F1,ff,LogFirstZone.DateOfFirstSoftDownload\n" //
                        +"7da,192,207,1,0,0,,21F1,61F1,ff,LogFirstZone.CRClogRecord\n" //
                        +"7da,56,95,1,0,0,,21F1,61F1,2ff,LogFirstZone.SoftDownloadSiteRef\n" //
                        +"7da,168,183,1,0,0,,21F1,61F1,ff,LogFirstZone.HourOfSoftwareDownloads\n" //
                        +"7da,136,143,1,0,0,,21F1,61F1,ff,LogFirstZone.NumberOfSoftwareDownloads\n" //
                        +"7da,96,135,1,0,0,,21FD,61FD,2ff,LogPreviousZone.SoftDownloadToolRef\n" //
                        +"7da,144,167,1,0,0,,21FD,61FD,ff,LogPreviousZone.DateOfFirstSoftDownload\n" //
                        +"7da,192,207,1,0,0,,21FD,61FD,ff,LogPreviousZone.CRClogRecord\n" //
                        +"7da,56,95,1,0,0,,21FD,61FD,2ff,LogPreviousZone.SoftDownloadSiteRef\n" //
                        +"7da,168,183,1,0,0,,21FD,61FD,ff,LogPreviousZone.HourOfSoftwareDownloads\n" //
                        +"7da,136,143,1,0,0,,21FD,61FD,ff,LogPreviousZone.NumberOfSoftwareDownloads\n" //
                        +"7da,96,135,1,0,0,,21FF,61FF,2ff,LogCurrentZone.SoftDownloadToolRef\n" //
                        +"7da,144,167,1,0,0,,21FF,61FF,ff,LogCurrentZone.DateOfFirstSoftDownload\n" //
                        +"7da,192,207,1,0,0,,21FF,61FF,ff,LogCurrentZone.CRClogRecord\n" //
                        +"7da,56,95,1,0,0,,21FF,61FF,2ff,LogCurrentZone.SoftDownloadSiteRef\n" //
                        +"7da,136,143,1,0,0,,21FF,61FF,ff,LogCurrentZone.NumberOfSoftwareDownloads\n" //
                        +"7da,168,183,1,0,0,,21FF,61FF,ff,LogCurrentZone.HourOfSoftwareDownloads\n" //
                        +"7da,88,95,1,0,0,,2185,6185,ff,CANM.AudioECU_FaultCounter\n" //
                        +"7da,32,39,1,0,0,,2185,6185,ff,CANM.CANmBussOffCounter\n" //
                        +"7da,40,47,1,0,0,,2185,6185,ff,CANM.CANmMuteCounter\n" //
                        +"7da,56,63,1,0,0,,2185,6185,ff,CANM.CentralSwitchECU_FaultCounter\n" //
                        +"7da,152,159,1,0,0,,2185,6185,ff,CANM.GATEWAY_ECU_FaultCounter\n" //
                        +"7da,280,287,1,0,0,,2185,6185,ff,CANM.NavigationECU_FaultCounter\n" //
                        +"7da,112,119,1,0,0,,2185,6185,ff,CANM.RearCameraECU_FaultCounter\n" //
                        +"7da,48,55,1,0,0,,2185,6185,ff,CANM.TCU_ECU_FaultCounter\n" //
                        +"7da,200,207,1,0,0,,2182,6182,ff,CANV.USMAbsentCounter\n" //
                        +"7da,672,679,1,0,0,,2182,6182,ff,CANV.TCUCounter\n" //
                        +"7da,424,431,1,0,0,,2182,6182,ff,CANV.SCH_EVC_AbsentCounter\n" //
                        +"7da,184,191,1,0,0,,2182,6182,ff,CANV.EPSAbsentCounter\n" //
                        +"7da,48,55,1,0,0,,2182,6182,ff,CANV.ECMAbsentCounter\n" //
                        +"7da,64,71,1,0,0,,2182,6182,ff,CANV.ClusterAbsentCounter\n" //
                        +"7da,40,47,1,0,0,,2182,6182,ff,CANV.CANvMuteCounter\n" //
                        +"7da,32,39,1,0,0,,2182,6182,ff,CANV.CANvBussOffCounter\n" //
                        +"7da,56,63,1,0,0,,2182,6182,ff,CANV.BrakeAbsentCounter\n" //
                        +"7da,72,79,1,0,0,,2182,6182,ff,CANV.BCMAbsentCounter\n" //
                        +"7da,80,87,1,0,0,,2182,6182,ff,CANV.AIRBAGAbsentCounter\n" //
                        +"7da,152,167,1,0,0,,2181,6181,ff,VIN.CRC\n" //
                        +"7da,16,151,1,0,0,,2181,6181,2ff,VIN.VIN\n" //
                        +"7da,32,71,1,0,0,,22FD05,62FD05,2ff,Speaker Settings.TCU Vol Speaker\n" //
                        +"7da,24,135,1,0,0,,22FD05,62FD05,2ff,Speaker Settings.all\n" //
                        +"7da,128,135,1,0,0,,22FD05,62FD05,ff,Speaker Settings.Politone\n" //
                        +"7da,80,119,1,0,0,,22FD05,62FD05,2ff,Speaker Settings.Gain\n" //
                        +"7da,72,79,1,0,0,,22FD05,62FD05,ff,Speaker Settings.Echo\n" //
                        +"7da,120,127,1,0,0,,22FD05,62FD05,ff,Speaker Settings.Beep\n" //
                        +"7da,280,535,1,0,0,,22FD10,62FD10,2ff,InSIM GPRS Parameters.User\n" //
                        +"7da,536,791,1,0,0,,22FD10,62FD10,2ff,InSIM GPRS Parameters.Password\n" //
                        +"7da,1048,1303,1,0,0,,22FD10,62FD10,2ff,InSIM GPRS Parameters.DNS2\n" //
                        +"7da,792,1047,1,0,0,,22FD10,62FD10,2ff,InSIM GPRS Parameters.DNS1\n" //
                        +"7da,24,279,1,0,0,,22FD10,62FD10,2ff,InSIM GPRS Parameters.APN\n" //
                        +"7da,24,279,1,0,0,,22FD11,62FD11,2ff,Application parameters.Application name\n" //
                        +"7da,536,791,1,0,0,,22FD11,62FD11,2ff,Application parameters.Application Version\n" //
                        +"7da,280,535,1,0,0,,22FD11,62FD11,2ff,Application parameters.Company name\n" //
                        +"7da,280,535,1,0,0,,22FD30,62FD30,2ff,External SIM GPRS Parameters.User\n" //
                        +"7da,536,791,1,0,0,,22FD30,62FD30,2ff,External SIM GPRS Parameters.Password\n" //
                        +"7da,1048,1303,1,0,0,,22FD30,62FD30,2ff,External SIM GPRS Parameters.DNS2\n" //
                        +"7da,24,279,1,0,0,,22FD30,62FD30,2ff,External SIM GPRS Parameters.APN\n" //
                        +"7da,792,1047,1,0,0,,22FD30,62FD30,2ff,External SIM GPRS Parameters.DNS1\n" //
                        +"7da,24,223,1,0,0,,22FD70,62FD70,2ff,Communication network name.name\n" //
                        +"7da,224,423,1,0,0,,22FD70,62FD70,2ff,Communication network name.id\n" //
                        +"7da,24,31,1,0,0,,22FD71,62FD71,ff,Registered to communication network,0:non registered;1:registered;2:Not Registered - searching;3:Denied register;4:Unknown;5:Registered (Roaming mode)\n" //
                        +"7da,24,47,1,0,0,,22FE64,62FE64,2ff,TCU test mode\n" //
                        +"7da,24,47,1,0,0,,22FE63,62FE63,ff,FICOSA TCU EOL mode\n" //
                        +"7da,28,39,219.3,-95.2558139534884,0,V,22FE01,62FE01,ff,C_V.Status.12V_BAT_SENSE\n" //
                        +"7da,140,151,3,1024,0,,22FE01,62FE01,ff,C_V.Status.BATTERY_CURRENT_SENSE\n" //
                        +"7da,156,167,9,0,0,,22FE01,62FE01,ff,C_V.Status.BATTERY_VOLTAGE_SENSE\n" //
                        +"7da,172,183,300,0,0,,22FE01,62FE01,ff,C_V.Status.GPS_RF_SUP_CURRENT_SENS\n" //
                        +"7da,188,199,1563,-133.650671785029,0,,22FE01,62FE01,ff,C_V.Status.KLINE_SENSE\n" //
                        +"7da,76,87,36,0,0,,22FE01,62FE01,ff,C_V.Status.SENSE_3V\n" //
                        +"7da,108,119,36,0,0,,22FE01,62FE01,ff,C_V.Status.SENSE_3V_GPS\n" //
                        +"7da,124,135,6,0,0,,22FE01,62FE01,ff,C_V.Status.SENSE_4.4V_VBAT_AUXWR\n" //
                        +"7da,60,71,6,0,0,,22FE01,62FE01,ff,C_V.Status.SENSE_5V\n" //
                        +"7da,44,55,1,0,0,V,22FE01,62FE01,ff,C_V.Status.SENSE_7.1V\n" //
                        +"7da,92,103,1,0,0,,22FE01,62FE01,ff,C_V.Status.SENSE_V_BAT_GPS\n" //
                        +"7da,204,215,1,0,0,,22FE01,62FE01,ff,C_V.Status.VCC_2V8_SENSE\n" //
                        +"7da,220,231,1,0,0,,22FE01,62FE01,ff,C_V.Status.VCC_1V8_SENSE\n" //
                        +"7da,236,247,1,0,0,,22FE01,62FE01,ff,C_V.Status.SENSE_5V_B_PIN\n" //
                        +"7da,252,263,1,0,0,,22FE01,62FE01,ff,C_V.Status.SENSE_5V_ANT_PIN\n" //
                        +"7da,268,279,1,0,0,,22FE01,62FE01,ff,C_V.Status.SENSE_3V_PMC\n" //
                        +"7da,24,143,1,0,0,,22FD1C,62FD1C,2ff,IMEI\n" //
                        +"7da,24,31,1,0,0,,22FD1D,62FD1D,ff,RSSI\n" //
                        +"7da,24,31,1,0,0,,22FE08,62FE08,ff,TCU State,20:TCU ON;42:TCU OFF GSM\n" //
                        +"7da,24,279,1,0,0,,226C22,626C22,2ff,inSIM SMS Center\n" //
                        +"7da,24,31,1,0,0,,226C29,626C29,ff,Ecall Number Selection,0:ecall number - Operational;1:ecall number - Life cycle Test\n" //
                        +"7da,24,31,1,0,0,,226C2A,626C2A,ff,Ecall Number Selection Ing,0:ecall number - Operational;1:ecall number - Life cycle Test;10:ecall number - Validation Test\n" //
                        +"7da,24,279,1,0,0,,226C2B,626C2B,2ff,BCall Number\n" //
                        +"7da,24,31,1,0,0,,226C2F,626C2F,ff,ECall Auto Activation,0:ecall auto activated;1:ecall auto deactivated\n" //
                        +"7da,24,31,1,0,0,,226C42,626C42,ff,InSIM PIN use,0:Deactivated;1:activated\n" //
                        +"7da,24,279,1,0,0,,226C40,626C40,2ff,InSIM PIN\n" //
                        +"7da,24,31,1,0,0,,226C46,626C46,ff,ExtSIM PIN use,0:Deactivated;1:activated\n" //
                        +"7da,24,279,1,0,0,,226C44,626C44,2ff,ExtSIM PIN\n" //
                        +"7da,24,31,1,0,0,,220114,620114,ff,USB connected input line,0:Not connected;1:connected\n" //
                        +"7da,24,31,1,0,0,,220111,620111,ff,Blocking the charge status,0:not use;1:NO (allow the charge);16:YES (block the charge);17:INVALID\n" //
                        +"7da,24,31,1,0,0,,22011A,62011A,ff,LIN\n" //
                        +"7da,24,31,1,0,0,,226C64,626C64,ff,External GPS Antenna,0:No External GPS antenna connection;1:External GPS antenna connection\n" //
                        +"7da,24,31,1,0,0,,226C91,626C91,ff,WakeUp by CAN,0:Wake Up by CAN;1:BAT VBR\n" //
                        +"7da,24,279,1,0,0,,226C93,626C93,2ff,InSIM ISDN\n" //
                        +"7da,24,279,1,0,0,,226C95,626C95,2ff,ExtSIM ISDN\n" //
                        +"7da,24,279,1,0,0,,226C38,626C38,2ff,PID InSIM UserID\n" //
                        +"7da,24,279,1,0,0,,226C3A,626C3A,2ff,PWD InSIM Password\n" //
                        +"7da,24,279,1,0,0,,226C24,626C24,2ff,ExtSIM SMS Center\n" //
                        +"7da,24,279,1,0,0,,226C3C,626C3C,2ff,PID ExtSIM UserID\n" //
                        +"7da,24,279,1,0,0,,226C3D,626C3D,2ff,PWD ExtSIM Password\n" //
                        +"7da,24,39,1,0,0,,226C8E,626C8E,ff,EV VSOC transmission Frequency\n" //
                        +"7da,24,31,1,0,0,,22012A,62012A,ff,K_Line SVT In\n" //
                        +"7da,24,31,1,0,0,,22012D,62012D,ff,K_Line SVT out\n" //
                        +"7da,24,31,1,0,0,,220124,620124,ff,MMI SUPPLY ON_OFF output line,0:Supply OFF;1:Supply ON\n" //
                        +"7da,24,31,1,0,0,,220117,620117,ff,USB supply ON_OFF,0:supply OFF;1:supply ON\n" //
                        +"7da,28,39,1,0,0,,22FE27,62FE27,ff,MMI supply Value.3V sense\n" //
                        +"7da,44,55,1,0,0,,22FE27,62FE27,ff,MMI supply Value.5V sense\n" //
                        +"7da,24,279,1,0,0,,226C26,626C26,2ff,Ecall Number _ Operational\n" //
                        +"7da,24,279,1,0,0,,226C28,626C28,2ff,Ecall Number _ Life cycle Test\n" //
                        +"7da,24,31,1,0,0,,226C6D,626C6D,ff,EV Service Activation Status,0:Desactivated;1:Activated\n" //
                        +"7da,24,31,1,0,0,,22FD04,62FD04,ff,GSM external antenna sensing,0:antenna OK;1:antenna SC GND;2:antenna SC 16V;3:antenna removed\n" //
                        +"7da,24,111,1,0,0,,22FD06,62FD06,ff,Microphone Settings 1.all\n" //
                        +"7da,64,103,1,0,0,,22FD06,62FD06,ff,Microphone Settings 1.Gain_MIC\n" //
                        +"7da,104,111,1,0,0,,22FD06,62FD06,ff,Microphone Settings 1.Filter_MIC\n" //
                        +"7da,104,111,1,0,0,,22FD20,62FD20,ff,Microphone Settings 2.Filter_MIC\n" //
                        +"7da,64,103,1,0,0,,22FD20,62FD20,ff,Microphone Settings 2.Gain_MIC\n" //
                        +"7da,24,111,1,0,0,,22FD20,62FD20,ff,Microphone Settings 2.all\n" //
                        +"7da,24,279,1,0,0,,226C2C,626C2C,2ff,CCall Number\n" //
                        +"7da,160,175,1,0,0,,22F190,62F190,ff,DID_VIN.CRC\n" //
                        +"7da,24,159,1,0,0,,22F190,62F190,2ff,DID_VIN.VIN\n" //
                        +"7da,24,127,1,0,0,,22F192,62F192,2ff,ECUHardware.Number\n" //
                        +"7da,128,151,1,0,0,,22F192,62F192,2ff,ECUHardware.VersionNumber\n" //
                        +"7da,24,127,1,0,0,,22F194,62F194,2ff,ECUSoftware.Number\n" //
                        +"7da,128,151,1,0,0,,22F194,62F194,2ff,ECUSoftware.VersionNumber\n" //
                        +"7da,8,15,1,0,0,,23121E3301,63121E3301,ff,USM_GeneralStatus.APCLineStatus\n" //
                        +"7da,24,31,1,0,0,,226C8C,626C8C,ff,EV BSOC Difference For Transmission\n" //
                        +"7da,24,31,1,0,0,s,226CA5,626CA5,ff,EV Activation Time Check\n" //
                        +"7da,24,31,1,0,0,s,226CA8,626CA8,ff,EV SMS Waiting Time\n" //
                        +"7da,24,31,1,0,0,,22FD26,62FD26,ff,GPS number of satellites\n" //
                        +"7da,24,31,1,0,0,,22FD23,62FD23,ff,GPS Sensing\n" //
                        +"7da,24,31,1,0,0,,226C02,626C02,ff,Ecall Vehicle Type,1:M1;4:N1\n" //
                        +"7da,24,31,1,0,0,,226C04,626C04,ff,Ecall Vehicle Propulsion Storage\n" //
                        +"7da,24,31,1,0,0,,226CB0,626CB0,ff,Ecall Activation Status,0:Ecall activated;1:Ecall Deactivated\n" //
                        +"7da,24,31,1,0,0,,226CB2,626CB2,ff,Bcall Activation Status,0:Ecall activated;1:Ecall Deactivated\n" //
                        +"7da,24,31,1,0,0,,220138,620138,ff,MMI Bcall button Status,0:Button not activated;1:Button activated\n" //
                        +"7da,24,31,1,0,0,,220133,620133,ff,MMI Ecall button Status,0:Button not activated;1:Button activated\n" //
                        +"7da,8,15,1,0,0,,23121E1C01,63121E1C01,ff,Airbag.DriverSafetyBeltSwitch\n" //
                        +"7da,8,15,1,0,0,,23121E1E01,63121E1E01,ff,Airbag.crashdetectionoutoforder_84\n" //
                        +"7da,8,15,1,0,0,,23121E1D01,63121E1D01,ff,Airbag.PassengerAIRBAG_OFF\n" //
                        +"7da,8,15,1,0,0,,23121E1701,63121E1701,ff,BCM.DiagMuxOn_BCM\n" //
                        +"7da,8,15,1,0,0,,23121E1B01,63121E1B01,ff,BCM.ExternalTemp_84\n" //
                        +"7da,8,15,1,0,0,,23121E1801,63121E1801,ff,BCM.CloseActiveBrakeSwitch_UCH\n" //
                        +"7da,8,15,1,0,0,,23121E0B01,63121E0B01,ff,Brake.ABSinRegulation\n" //
                        +"7da,8,15,1,0,0,,23121E0C01,63121E0C01,ff,Brake.ABSMalfunction\n" //
                        +"7da,8,15,1,0,0,,23121E0A01,63121E0A01,ff,Brake.ASRinRegulation\n" //
                        +"7da,8,15,1,0,0,,23121E0701,63121E0701,ff,Brake.ASRMalfunction\n" //
                        +"7da,8,15,1,0,0,,23121E0801,63121E0801,ff,Brake.MSRDisabled\n" //
                        +"7da,8,15,1,0,0,,23121E0901,63121E0901,ff,Brake.MSRinRegulation\n" //
                        +"7da,8,23,1,0,0,,23121E0D02,63121E0D02,ff,Brake.VehicleSpeed\n" //
                        +"7da,8,15,1,0,0,,23121E1401,63121E1401,ff,Cluster.BrakeSystemMalfunction\n" //
                        +"7da,8,39,1,0,0,,23121E1004,63121E1004,ff,Cluster.DistanceTotalizer\n" //
                        +"7da,8,23,1,0,0,,23121E1502,63121E1502,ff,Cluster.ShortVehicleID\n" //
                        +"7da,8,15,1,0,0,,23121E0501,63121E0501,ff,ECM.RawPedal\n" //
                        +"7da,8,15,1,0,0,,23121E0F01,63121E0F01,ff,EPS.SteeringFailure\n" //
                        +"7da,8,23,1,0,0,,23121DF902,63121DF902,ff,MMI_SCH_CANHS.AverageConsumption\n" //
                        +"7da,8,15,1,0,0,,23121E0101,63121E0101,ff,MMI_SCH_CANHS.ChargingPlugConnected\n" //
                        +"7da,8,15,1,0,0,,23121E0001,63121E0001,ff,MMI_SCH_CANHS.ElectricalChargingType\n" //
                        +"7da,8,15,1,0,0,,23121E0401,63121E0401,ff,MMI_SCH_CANHS.ETAlertETSDefault\n" //
                        +"7da,8,15,1,0,0,,23121DFF01,63121DFF01,ff,MMI_SCH_CANHS.HVBatteryEnergyLevel\n" //
                        +"7da,8,15,1,0,0,,23121DF801,63121DF801,ff,MMI_SCH_CANHS.HVBatteryLow\n" //
                        +"7da,8,23,1,0,0,,23121DFD02,63121DFD02,ff,MMI_SCH_CANHS.SlowChargeRemainingTime\n" //
                        +"7da,8,23,1,0,0,,23121E0202,63121E0202,ff,MMI_SCH_CANHS.TotalConsumption\n" //
                        +"7da,8,23,1,0,0,,23121DFB02,63121DFB02,ff,MMI_SCH_CANHS.VehicleAutonomy\n" //
                        +"7da,8,15,1,0,0,,23121DF501,63121DF501,ff,SCH_CANHS.ChargeInProgress\n" //
                        +"7da,8,15,1,0,0,,23121DF601,63121DF601,ff,SCH_CANHS.HVBatHealth\n" //
                        +"7da,8,15,1,0,0,,23121E0601,63121E0601,ff,ECM.EngineStatus_84\n" //
                        +"7da,24,31,1,0,0,,226CAA,626CAA,ff,MFD NFA definition,0:Nothing connected (nothing configured);1:MFD present (and USB);2:NFA present (no USB)\n" //
                        +"7da,160,175,1,0,0,,2180,6180,ff,Identification.CalibrationNumber\n" //
                        +"7da,56,63,1,0,0,,2180,6180,ff,Identification.DiagnosticIdentificationCode\n" //
                        +"7da,128,143,1,0,0,,2180,6180,ff,Identification.SoftwareNumber\n" //
                        +"7da,144,159,1,0,0,,2180,6180,ff,Identification.EditionNumber\n" //
                        +"7da,176,183,1,0,0,,2180,6180,ff,PartNumber.BasicPartList,2:28275;3:28277\n" //
                        +"7da,184,191,1,0,0,,2180,6180,ff,HardwareNumber.BasicPartList,0:48651\n" //
                        +"7da,192,199,1,0,0,,2180,6180,ff,ApprovalNumber.BasicPartList,0:N/A\n" //
                        +"7da,16,55,1,0,0,,2180,6180,2ff,Identification.PartNumber.LowerPart\n" //
                        +"7da,88,127,1,0,0,,2180,6180,2ff,Identification.HardwareNumber.LowerPart\n" //
                        +"7da,200,207,1,0,0,,2180,6180,ff,Identification.ManufacturerIdentificationCode,0:Renault R1;128:Nissan N1;129:Nissan N2;130:Nissan N3;136:5DIGITS;255:Renault R3\n" //
                        +"7da,64,87,1,0,0,,2180,6180,2ff,Identification.SupplierNumber\n" //
                        +"7da,16,95,1,0,0,,21EF,61EF,2ff,DaimlerHardwarePartNumber\n" //
                        +"7da,96,175,1,0,0,,21EF,61EF,2ff,DaimlerSoftwarePartNumber\n" //
                        +"7da,30,31,1,0,0,,226C6F,626C6F,ff,EV Service Provisionning Functions configuration.Charging Spot Finder 3 points.status.activation,0:unavailable function;1:deactivated function;2:activated function;3:not used\n" //
                        +"7da,28,29,1,0,0,,226C6F,626C6F,ff,EV Service Provisionning Functions configuration.Charge Spot short term booking.status.activation,0:unavailable function;1:deactivated function;2:activated function;3:not used\n" //
                        +"7da,26,27,1,0,0,,226C6F,626C6F,ff,EV Service Provisionning Functions configuration.Battery charging.status.activation,0:unavailable function;1:deactivated function;2:activated function;3:not used\n" //
                        +"7da,24,25,1,0,0,,226C6F,626C6F,ff,EV Service Provisionning Functions configuration.Charging historical.status.activation,0:unavailable function;1:deactivated function;2:activated function;3:not used\n" //
                        +"7da,38,39,1,0,0,,226C6F,626C6F,ff,EV Service Provisionning Functions configuration.Charging activation or stop by remote.status.activation,0:unavailable function;1:deactivated function;2:activated function;3:not used\n" //
                        +"7da,36,37,1,0,0,,226C6F,626C6F,ff,EV Service Provisionning Functions configuration.Charging schedular.status.activation,0:unavailable function;1:deactivated function;2:activated function;3:not used\n" //
                        +"7da,34,35,1,0,0,,226C6F,626C6F,ff,EV Service Provisionning Functions configuration.Remote HVAC control.status.activation,0:unavailable function;1:deactivated function;2:activated function;3:not used\n" //
                        +"7da,32,33,1,0,0,,226C6F,626C6F,ff,EV Service Provisionning Functions configuration.Plug In missing.status.activation,0:unavailable function;1:deactivated function;2:activated function;3:not used\n" //
                        +"7da,46,47,1,0,0,,226C6F,626C6F,ff,EV Service Provisionning Functions configuration.Low battery alert upgrade on remote phone.status.activation,0:unavailable function;1:deactivated function;2:activated function;3:not used\n" //
                        +"7da,44,45,1,0,0,,226C6F,626C6F,ff,EV Service Provisionning Functions configuration.Battery usage and charge history.status.activation,0:unavailable function;1:deactivated function;2:activated function;3:not used\n" //
                        +"7da,42,43,1,0,0,,226C6F,626C6F,ff,EV Service Provisionning Functions configuration.Inhibition Desinhibition of Charge as no pay upgrade.status.activation,0:unavailable function;1:deactivated function;2:activated function;3:not used\n" //
                        +"7da,40,41,1,0,0,,226C6F,626C6F,ff,EV Service Provisionning Functions configuration.Charging Spot Finder upgrade 3 to 24 spots.status.activation,0:unavailable function;1:deactivated function;2:activated function;3:not used\n" //
                        +"7da,54,55,1,0,0,,226C6F,626C6F,ff,EV Service Provisionning Functions configuration.Create private charge spot off board and download OTA.status.activation,0:unavailable function;1:deactivated function;2:activated function;3:not used\n" //
                        +"7da,52,53,1,0,0,,226C6F,626C6F,ff,EV Service Provisionning Functions configuration.Charging Spot Finder upgrade availability and compatibility.status.activation,0:unavailable function;1:deactivated function;2:activated function;3:not used\n" //
                        +"7da,50,51,1,0,0,,226C6F,626C6F,ff,EV Service Provisionning Functions configuration.FLEET Probe service 1.status.activation,0:unavailable function;1:deactivated function;2:activated function;3:not used\n" //
                        +"7da,48,49,1,0,0,,226C6F,626C6F,ff,EV Service Provisionning Functions configuration.Internet_widget widget store.status.activation,0:unavailable function;1:deactivated function;2:activated function;3:not used\n" //
                        +"7da,60,61,1,0,0,,226C6F,626C6F,ff,EV Service Provisionning Functions configuration.LIVE All Tomtom Services.status.activation,0:unavailable function;1:deactivated function;2:activated function;3:not used\n" //
                        +"7da,62,63,1,0,0,,226C6F,626C6F,ff,EV Service Provisionning Functions configuration.ECO Scoring Challenge Service.status.activation,0:unavailable function;1:deactivated function;2:activated function;3:not used\n" //

                ;

        String dtcDef =
                ""

                        +"93F0,BAT\n" //
                        +"9302,GSM External Antenna Failure\n" //
                        +"93D9,USB Host Presence not detected\n" //
                        +"AE21,Output_On_Request\n" //
                        +"93E1,CAN-M Failure\n" //
                        +"9341,TCU\n" //
                        +"9304,GSM Internal Antenna Failure\n" //
                        +"AE00,SMS\n" //
                        +"AE03,GDC Fonctional\n" //
                        +"AE05,GDC Configuration\n" //
                        +"9324,GPS External Antenna\n" //
                        +"AEF0,Internal battery\n" //
                        +"AE02,GDC Authentification\n" //
                        +"AE80,WMP SPI communications failure\n" //
                        +"AE81,PMC SPI communications failure\n" //
                        +"AE82,I2C communications failure\n" //
                        +"AE83,TCU reset procedure failure\n" //
                        +"AE84,WMP Flash memory corrupted\n" //
                        +"AE85,WMP secure memory corrupted\n" //
                        +"AE22,K-line IN SVT failure\n" //
                        +"AE23,K-line OUT SVT failure\n" //
                        +"AE24,SVT Blocking\n" //
                        +"9328,Mic out line Failure\n" //
                        +"9305,Mic in Line failure\n" //
                        +"AE11,Temperature Sensor Failure\n" //
                        +"930D,AUDIO_HU_OUT Line Failure\n" //
                        +"AE10,GPS Internal Antenna Failure\n" //
                        +"AE30,LIN Failure\n" //
                        +"AE31,MMI Audio Out Line Failure\n" //
                        +"AE32,MMI Supply 4V Failure\n" //
                        +"AE91,MMI Ecall Button Failure\n" //
                        +"AE92,MMI Bcall Button Failure\n" //
                        +"AE33,MMI Led Failure\n" //
                        +"AE34,MMI Supply 7V Failure\n" //
                        +"AE12,Accelerometer Failure\n" //
                        +"AE04,Internal SIM internal Failure\n" //
                        +"AE06,External SIM internal Failure\n" //
                        +"AE01,PPP Data Communication Authentication\n" //
                        +"AE07,Invalid TCU Certificate\n" //
                        +"AE08,TCU datas discarded by GDC\n" //
                        +"9360,Voice Communication Failure (GSM)\n" //
                        +"AE13,GPS Communication Failure\n" //
                        +"93E2,CAN-V Failure\n" //
                        +"933B,AUX_IN_INFO Line Failure\n" //

                ;

        String testDef =
                ""

                        +"00,Device and failure type ODB codding\n" //
                        +"01,General Electrical Failure\n" //
                        +"02,General signal failure\n" //
                        +"03,FM  / PWM Failures\n" //
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
                        +"28,signal bias level oor/ zero adjustment failure\n" //
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

        Frames.getInstance().load("7DA,0,0,TCU\n");
        Fields.getInstance().load(fieldDef1);
        Dtcs.getInstance().load(dtcDef, testDef);
    }
}