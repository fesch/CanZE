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

public class EcuDiagCLUSTER {

    // static final public String fieldsString () {
    void load () {

        String fieldDef1 =
                ""
                        +"763,0,7,1,0,0,,14ffff,54,ff\n" // Reset DTC
                        +"763,0,23,1,0,0,,1902ff,5902ff,ff\n" // Query DTC

                        +"763,24,55,1,0,0,Km,220206,620206,ff,Config Entretien_Odometre general\n" //
                        +"763,24,39,1,0,0,km,220202,620202,ff,Config Entretien_Vidange courant. distance\n" //
                        +"763,40,55,1,0,0,Jours,220202,620202,ff,Config Entretien_Vidange courant. Jours\n" //
                        +"763,24,39,1,0,0,km,220201,620201,ff,Config Entretien_Vidange init.Valeur initiale autonomie vidange distance\n" //
                        +"763,40,55,1,0,0,Jours,220201,620201,ff,Config Entretien_Vidange init.Valeur initiale autonomie vidange temps\n" //
                        +"763,24,55,1,0,0,m,222101,622101,ff,Infos Calcul_Distance parcourue ADAC\n" //
                        +"763,24,47,1,0,0,s,222104,622104,ff,Infos Calcul_Temps ADAC\n" //
                        +"763,38,38,1,0,0,,222407,622407,ff,Status Systeme_erreur configuration.Erreur Configuration SSPP,0:Pas d'erreur config SSPP;1:Erreur config SSPP\n" //
                        +"763,35,35,1,0,0,,222407,622407,ff,Status Systeme_erreur configuration.Erreur Configuration tableau jamais configure,0:Pas d'erreur : tdb configuré OK;1:Erreur : Tdb jamais configuré\n" //
                        +"763,30,30,1,0,0,,222407,622407,ff,Status Systeme_erreur configuration.Erreur Configuration UPA,0:Pas d'erreur config UPA;1:Erreur config UPA\n" //
                        +"763,29,29,1,0,0,,222407,622407,ff,Status Systeme_erreur configuration.Erreur Configuration TCU,0:Pas d'erreur config TCU;1:Erreur config TCU\n" //
                        +"763,28,28,1,0,0,,222407,622407,ff,Status Systeme_erreur configuration.Erreur Configuration CLIM,0:Pas d'erreur config CLIM;1:Erreur config CLIM\n" //
                        +"763,24,31,1,0,0,,222406,622406,ff,Status Systeme_number intended resets\n" //
                        +"763,24,31,1,0,0,,222405,622405,ff,Status Systeme_number resets by failures\n" //
                        +"763,24,31,1,0,0,,222403,622403,ff,Status Systeme_status EEPROM,0:Non défini;1:EEPROM Status OK;2:EEPROM Error\n" //
                        +"763,16,151,1,0,0,,2181,6181,2ff,VIN Numero Identification\n" //
                        +"763,16,47,1,0,0,,2100,6100,ff,Reponse LID utilises\n" //
                        +"763,24,55,1,0,0,,220127,620127,ff,Config Generale_VehicleIdent default value\n" //
                        +"763,24,31,1,0,0,,220122,620122,ff,Config Generale_Affichage temp exter,0:sans affichage température ext;16:Avec affichage température extérieure (par défaut)\n" //
                        +"763,24,31,1,0,0,,220101,620101,ff,Config Generale_Langue,0:Francais (par défaut);16:Anglais;32:Italien;48:Allemand;64:Espagnol;80:Hollandais;96:Portugais;112:Turc;128:Polonais;145:Suedois;146:Finnois;147:Bulgare;148:Slovene;149:Grec;150:Roumain;151:Hongrois;152:Slovaque;153:Tcheque;154:Danois;155:Estonien;156:Letton;157:Lituanien;158:Croate;255:Autre\n" //
                        +"763,24,31,1,0,0,,22010F,62010F,ff,Config Generale_Survitesse,0:Sans  survitesse (par défaut);16:Avec survitesse\n" //
                        +"763,24,31,1,0,0,,220111,620111,ff,Config Generale_ALS,0:Sans ALS (par défaut);16:Avec ALS\n" //
                        +"763,24,31,1,0,0,,220207,620207,ff,Config Entretien_Nb de maj par diag\n" //
                        +"763,40,41,1,0,0,,2182,6182,ff,Module BusMuet,0:Non muet;2:BusMuet;3:Confirmé BusMuet\n" //
                        +"763,42,47,1,0,0,,2182,6182,ff,Module BusMuet compteur,0:0;1:1;2:2;3:3;4:4;5:5;6:6;7:7;8:8;9:9;10:10;11:11;12:12;13:13;14:14;15:15;16:16;17:17;18:18;19:19;20:20;21:21;22:22;23:23;24:24;25:25;26:26;27:27;28:28;29:29;30:30;31:31;32:32;33:33;34:34;35:35;36:36;37:37;38:38;39:39;40:40;63:Non Contrôlé\n" //
                        +"763,32,33,1,0,0,,2182,6182,ff,Module BusOff,0:BusOn;2:BusOff;3:Confirmé BusOff\n" //
                        +"763,34,39,1,0,0,,2182,6182,ff,Module BusOff compteur,0:0;1:1;2:2;3:3;4:4;5:5;6:6;7:7;8:8;9:9;10:10;11:11;12:12;13:13;14:14;15:15;16:16;17:17;18:18;19:19;20:20;21:21;22:22;23:23;24:24;25:25;26:26;27:27;28:28;29:29;30:30;31:31;32:32;33:33;34:34;35:35;36:36;37:37;38:38;39:39;40:40;63:Non Contrôlé\n" //
                        +"763,56,57,1,0,0,,2182,6182,ff,Producteur MUX BRAKE,0:Présent;2:Absent;3:Confirmé Absent\n" //
                        +"763,58,63,1,0,0,,2182,6182,ff,Producteur MUX BRAKE compteur,0:0;1:1;2:2;3:3;4:4;5:5;6:6;7:7;8:8;9:9;10:10;11:11;12:12;13:13;14:14;15:15;16:16;17:17;18:18;19:19;20:20;21:21;22:22;23:23;24:24;25:25;26:26;27:27;28:28;29:29;30:30;31:31;32:32;33:33;34:34;35:35;36:36;37:37;38:38;39:39;40:40;63:Non Contrôlé\n" //
                        +"763,80,81,1,0,0,,2182,6182,ff,Producteur MUX Airbag,0:Présent;2:Absent;3:Confirmé Absent\n" //
                        +"763,82,87,1,0,0,,2182,6182,ff,Producteur MUX Airbag compteur,0:0;1:1;2:2;3:3;4:4;5:5;6:6;7:7;8:8;9:9;10:10;11:11;12:12;13:13;14:14;15:15;16:16;17:17;18:18;19:19;20:20;21:21;22:22;23:23;24:24;25:25;26:26;27:27;28:28;29:29;30:30;31:31;32:32;33:33;34:34;35:35;36:36;37:37;38:38;39:39;40:40;63:Non Contrôlé\n" //
                        +"763,184,185,1,0,0,,2182,6182,ff,Producteur MUX EPS Controle trajectoire,0:Présent;2:Absent;3:Confirmé Absent\n" //
                        +"763,186,191,1,0,0,,2182,6182,ff,Producteur MUX EPS Controle trajectoire compteur,0:0;1:1;2:2;3:3;4:4;5:5;6:6;7:7;8:8;9:9;10:10;11:11;12:12;13:13;14:14;15:15;16:16;17:17;18:18;19:19;20:20;21:21;22:22;23:23;24:24;25:25;26:26;27:27;28:28;29:29;30:30;31:31;32:32;33:33;34:34;35:35;36:36;37:37;38:38;39:39;40:40;63:Non Contrôlé\n" //
                        +"763,64,65,1,0,0,,2182,6182,ff,Producteur MUX Tabbeau de bord,0:Présent;2:Absent;3:Confirmé Absent\n" //
                        +"763,66,71,1,0,0,,2182,6182,ff,Producteur MUX Tabbeau de bord compteur,0:0;1:1;2:2;3:3;4:4;5:5;6:6;7:7;8:8;9:9;10:10;11:11;12:12;13:13;14:14;15:15;16:16;17:17;18:18;19:19;20:20;21:21;22:22;23:23;24:24;25:25;26:26;27:27;28:28;29:29;30:30;31:31;32:32;33:33;34:34;35:35;36:36;37:37;38:38;39:39;40:40;63:Non Contrôlé\n" //
                        +"763,72,73,1,0,0,,2182,6182,ff,Producteur MUX BCM,0:Présent;2:Absent;3:Confirmé Absent\n" //
                        +"763,74,79,1,0,0,,2182,6182,ff,Producteur MUX BCM compteur,0:0;1:1;2:2;3:3;4:4;5:5;6:6;7:7;8:8;9:9;10:10;11:11;12:12;13:13;14:14;15:15;16:16;17:17;18:18;19:19;20:20;21:21;22:22;23:23;24:24;25:25;26:26;27:27;28:28;29:29;30:30;31:31;32:32;33:33;34:34;35:35;36:36;37:37;38:38;39:39;40:40;63:Non Contrôlé\n" //
                        +"763,200,201,1,0,0,,2182,6182,ff,Producteur MUX USM,0:Présent;2:Absent;3:Confirmé Absent\n" //
                        +"763,202,207,1,0,0,,2182,6182,ff,Producteur MUX USM compteur,0:0;1:1;2:2;3:3;4:4;5:5;6:6;7:7;8:8;9:9;10:10;11:11;12:12;13:13;14:14;15:15;16:16;17:17;18:18;19:19;20:20;21:21;22:22;23:23;24:24;25:25;26:26;27:27;28:28;29:29;30:30;31:31;32:32;33:33;34:34;35:35;36:36;37:37;38:38;39:39;40:40;63:Non Contrôlé\n" //
                        +"763,16,31,1,0,0,,2182,6182,ff,Version Messagerie Mux\n" //
                        +"763,672,673,1,0,0,,2182,6182,ff,Producteur MUX TCU,0:Présent;2:Absent;3:Confirmé Absent\n" //
                        +"763,674,679,1,0,0,,2182,6182,ff,Producteur MUX TCU compteur,0:0;1:1;2:2;3:3;4:4;5:5;6:6;7:7;8:8;9:9;10:10;11:11;12:12;13:13;14:14;15:15;16:16;17:17;18:18;19:19;20:20;21:21;22:22;23:23;24:24;25:25;26:26;27:27;28:28;29:29;30:30;31:31;32:32;33:33;34:34;35:35;36:36;37:37;38:38;39:39;40:40;63:Non Contrôlé\n" //
                        +"763,664,665,1,0,0,,2182,6182,ff,Producteur MUX UBP,0:Présent;2:Absent;3:Confirmé Absent\n" //
                        +"763,666,671,1,0,0,,2182,6182,ff,Producteur MUX UBP compteur,0:0;1:1;2:2;3:3;4:4;5:5;6:6;7:7;8:8;9:9;10:10;11:11;12:12;13:13;14:14;15:15;16:16;17:17;18:18;19:19;20:20;21:21;22:22;23:23;24:24;25:25;26:26;27:27;28:28;29:29;30:30;31:31;32:32;33:33;34:34;35:35;36:36;37:37;38:38;39:39;40:40;63:Non Contrôlé\n" //
                        +"763,424,425,1,0,0,,2182,6182,ff,Producteur MUX EVC,0:Présent;2:Absent;3:Confirmé Absent\n" //
                        +"763,426,431,1,0,0,,2182,6182,ff,Producteur MUX EVC compteur,0:0;1:1;2:2;3:3;4:4;5:5;6:6;7:7;8:8;9:9;10:10;11:11;12:12;13:13;14:14;15:15;16:16;17:17;18:18;19:19;20:20;21:21;22:22;23:23;24:24;25:25;26:26;27:27;28:28;29:29;30:30;31:31;32:32;33:33;34:34;35:35;36:36;37:37;38:38;39:39;40:40;63:Non Contrôlé\n" //
                        +"763,104,105,1,0,0,,2182,6182,ff,Producteur MUX Clim,0:Présent;2:Absent;3:Confirmé Absent\n" //
                        +"763,106,111,1,0,0,,2182,6182,ff,Producteur MUX Clim compteur,0:0;1:1;2:2;3:3;4:4;5:5;6:6;7:7;8:8;9:9;10:10;11:11;12:12;13:13;14:14;15:15;16:16;17:17;18:18;19:19;20:20;21:21;22:22;23:23;24:24;25:25;26:26;27:27;28:28;29:29;30:30;31:31;32:32;33:33;34:34;35:35;36:36;37:37;38:38;39:39;40:40;63:Non Contrôlé\n" //
                        +"763,16,103,1,0,0,,21B4,61B4,ff,RECVAL2\n" //
                        +"763,16,103,1,0,0,,21B3,61B3,ff,RECVAL1\n" //
                        +"763,160,175,1,0,0,,2180,6180,ff,CalibrationNumber\n" //
                        +"763,56,63,1,0,0,,2180,6180,ff,DiagnosticIdentificationCode\n" //
                        +"763,128,143,1,0,0,,2180,6180,ff,SoftwareNumber\n" //
                        +"763,64,87,1,0,0,,2180,6180,2ff,SupplierNumber.ITG\n" //
                        +"763,144,159,1,0,0,,2180,6180,ff,EditionNumber\n" //
                        +"763,176,183,1,0,0,,2180,6180,ff,PartNumber.BasicPartList,0:N/A;2:285J2\n" //
                        +"763,184,191,1,0,0,,2180,6180,ff,HardwareNumber.BasicPartList,0:N/A;1:25046\n" //
                        +"763,192,199,1,0,0,,2180,6180,ff,ApprovalNumber.BasicPartList,0:N/A\n" //
                        +"763,16,55,1,0,0,,2180,6180,2ff,PartNumber.LowerPart\n" //
                        +"763,88,127,1,0,0,,2180,6180,2ff,HardwareNumber.LowerPart\n" //
                        +"763,200,207,1,0,0,,2180,6180,ff,ManufacturerIdentificationCode,0:Renault R1;128:Nissan N1;129:Nissan N2;130:Nissan N3;136:Magnéti Marelli\n" //
                        +"763,24,39,1,0,0,,222505,622505,ff,Causes allumage temoins Ref.REF Present Alerte 1\n" //
                        +"763,40,55,1,0,0,,222505,622505,ff,Causes allumage temoins Ref.REF Present Alerte 2\n" //
                        +"763,24,39,1,0,0,,222503,622503,ff,Causes allumage temoins Stop.STOP Present Alerte 1\n" //
                        +"763,40,55,1,0,0,,222503,622503,ff,Causes allumage temoins Stop.STOP Present Alerte 2\n" //
                        +"763,24,31,1,0,0,,222007,622007,ff,Entrees filaires_contact siege chauffant,1:0 siège chauffant actif (témoin éteint);2:1 siège chauffant activé (témoin allumé);4:2 sièges chauffants activés (témoin allumé)\n" //
                        +"763,24,39,1,0,0,,222501,622501,ff,Causes allumage temoins Service Present.SERVICE Present Alerte 1\n" //
                        +"763,40,55,1,0,0,,222501,622501,ff,Causes allumage temoins Service Present.SERVICE Present Alerte 2\n" //
                        +"763,38,38,1,0,0,,2220F0,6220F0,ff,Entrees filaires_Satellite_boutons.left\n" //
                        +"763,39,39,1,0,0,,2220F0,6220F0,ff,Entrees filaires_Satellite_boutons.right\n" //
                        +"763,41,41,1,0,0,,2220F0,6220F0,ff,Entrees filaires_Satellite_boutons.Rep since reset\n" //
                        +"763,42,42,1,0,0,,2220F0,6220F0,ff,Entrees filaires_Satellite_boutons.Val since reset\n" //
                        +"763,43,43,1,0,0,,2220F0,6220F0,ff,Entrees filaires_Satellite_boutons.Vol plus since reset\n" //
                        +"763,44,44,1,0,0,,2220F0,6220F0,ff,Entrees filaires_Satellite_boutons.Vol moins since reset\n" //
                        +"763,45,45,1,0,0,,2220F0,6220F0,ff,Entrees filaires_Satellite_boutons.Sup since reset\n" //
                        +"763,46,46,1,0,0,,2220F0,6220F0,ff,Entrees filaires_Satellite_boutons.High since reset\n" //
                        +"763,47,47,1,0,0,,2220F0,6220F0,ff,Entrees filaires_Satellite_boutons.Low since reset\n" //
                        +"763,55,55,1,0,0,,2220F0,6220F0,ff,Entrees filaires_Satellite_boutons.right since reset\n" //
                        +"763,54,54,1,0,0,,2220F0,6220F0,ff,Entrees filaires_Satellite_boutons.left since reset\n" //
                        +"763,25,25,1,0,0,,2220F0,6220F0,ff,Entrees filaires_Satellite_boutons.Rep\n" //
                        +"763,26,26,1,0,0,,2220F0,6220F0,ff,Entrees filaires_Satellite_boutons.Val\n" //
                        +"763,27,27,1,0,0,,2220F0,6220F0,ff,Entrees filaires_Satellite_boutons.Vol plus\n" //
                        +"763,28,28,1,0,0,,2220F0,6220F0,ff,Entrees filaires_Satellite_boutons.Vol moins\n" //
                        +"763,29,29,1,0,0,,2220F0,6220F0,ff,Entrees filaires_Satellite_boutons.Sup\n" //
                        +"763,30,30,1,0,0,,2220F0,6220F0,ff,Entrees filaires_Satellite_boutons.High\n" //
                        +"763,31,31,1,0,0,,2220F0,6220F0,ff,Entrees filaires_Satellite_boutons.Low\n" //
                        +"763,24,39,1,0,0,Jours,222602,622602,ff,DataRead Info Entretien Jours apres alerte\n" //
                        +"763,24,39,1,0,0,km,222601,622601,ff,DataRead Info Entretien Km apres alerte\n" //
                        +"763,24,39,1,0,0,km,222603,622603,ff,DataRead Info Entretien valeur affichee\n" //
                        +"763,24,39,1,0,0,,222502,622502,ff,Causes allumage temoins Service Memorise.SERVICE Memorise Alerte 1\n" //
                        +"763,40,55,1,0,0,,222502,622502,ff,Causes allumage temoins Service Memorise.SERVICE Memorise Alerte 2\n" //
                        +"763,24,39,1,0,0,,222504,622504,ff,Causes allumage temoin Stop Memorise.STOP Memorise Alerte 1\n" //
                        +"763,40,55,1,0,0,,222504,622504,ff,Causes allumage temoin Stop Memorise.STOP Memorise Alerte 2\n" //
                        +"763,24,39,1,0,0,,222506,622506,ff,Causes allumage temoin Ref Memorise.REF Memorise Alerte 1\n" //
                        +"763,40,55,1,0,0,,222506,622506,ff,Causes allumage temoin Ref Memorise.REF Memorise Alerte 2\n" //
                        +"763,24,39,1,0,0,V,222006,622006,ff,Entrees filaires_batterie\n" //
                        +"763,24,24,1,0,0,,2220F1,6220F1,ff,Entrees filaires_Satellite_rotary.Molette bas since reset\n" //
                        +"763,25,25,1,0,0,,2220F1,6220F1,ff,Entrees filaires_Satellite_rotary.Molette haut since reset\n" //
                        +"763,26,26,1,0,0,,2220F1,6220F1,ff,Entrees filaires_Satellite_rotary.Molette A since reset\n" //
                        +"763,27,27,1,0,0,,2220F1,6220F1,ff,Entrees filaires_Satellite_rotary.Molette B since reset\n" //
                        +"763,28,28,1,0,0,,2220F1,6220F1,ff,Entrees filaires_Satellite_rotary.Molette C since reset\n" //
                        +"763,32,32,1,0,0,,2220F1,6220F1,ff,Entrees filaires_Satellite_rotary.Molette bas\n" //
                        +"763,33,33,1,0,0,,2220F1,6220F1,ff,Entrees filaires_Satellite_rotary.Molette haut\n" //
                        +"763,34,34,1,0,0,,2220F1,6220F1,ff,Entrees filaires_Satellite_rotary.Molette A\n" //
                        +"763,35,35,1,0,0,,2220F1,6220F1,ff,Entrees filaires_Satellite_rotary.Molette B\n" //
                        +"763,36,36,1,0,0,,2220F1,6220F1,ff,Entrees filaires_Satellite_rotary.Molette C\n" //
                        +"763,26,26,1,0,0,,2220F2,6220F2,ff,Entrees filaires_Wake up Signals.system ON Request\n" //
                        +"763,28,28,1,0,0,,2220F2,6220F2,ff,Entrees filaires_Wake up Signals.Wake up\n" //
                        +"763,31,31,1,0,0,,2220F3,6220F3,ff,Entrees filaires_Output Lines.System On,1:System ON (ON command from MM= 1 if active)\n" //
                        +"763,24,31,1,0,0,,220108,620108,ff,Config Generale_RV_LV_ACC,0:Sans (par défaut);16:RV / LV présent;32:ACC / LV;48:LV seul\n" //
                        +"763,16,95,1,0,0,,2184,6184,2ff,ClusterProductionNumber en ASCII\n" //
                        +"763,96,143,1,0,0,,2184,6184,2ff,ClusterProductionDate en ASCII\n" //
                        +"763,24,103,1,0,0,,22F196,62F196,2ff,Numero homologation reference\n" //
                        +"763,24,103,1,0,0,,22F18E,62F18E,2ff,Reference piece VehicleManufacturer KitAssemblyPartNumber\n" //
                        +"763,24,103,1,0,0,,22F187,62F187,2ff,Reference piece VehicleManufacturer SparePartNumber\n" //
                        +"763,24,31,1,0,0,,220110,620110,ff,Config Generale_TCU,0:Sans TCU (par défaut);16:Avec TCU\n" //
                        +"763,24,31,1,0,0,,22010C,62010C,ff,Config Generale_Clim,0:Sans Clim (par défaut);16:Avec Clim\n" //
                        +"763,24,31,1,0,0,,22011C,62011C,ff,Config Generale_menu_retardateur,0:Sans Menu_Retardateur  (par défaut);16:Avec Menu_Retardateur\n" //
                        +"763,29,29,1,0,0,,22200B,62200B,ff,Entrees filaires_Input_boutons.Bouton Push to Talk\n" //
                        +"763,30,30,1,0,0,,22200B,62200B,ff,Entrees filaires_Input_boutons.Bouton Input LVE arriere\n" //
                        +"763,31,31,1,0,0,,22200B,62200B,ff,Entrees filaires_Input_boutons.Bouton Ambiance\n" //
                        +"763,25,25,1,0,0,,22200B,62200B,ff,Entrees filaires_Input_boutons.Push toTalk press since reset\n" //
                        +"763,27,27,1,0,0,,22200B,62200B,ff,Entrees filaires_Input_boutons.Bouton Ambiance press since reset\n" //
                        +"763,26,26,1,0,0,,22200B,62200B,ff,Entrees filaires_Input_boutons. Bouton LVE arriere press since reset\n" //
                        +"763,24,31,1,40,0,°C,222202,622202,ff,Mesures CTN_afficheur.Temperature Dalle\n" //
                        +"763,24,31,1,40,0,°C,222204,622204,ff,Mesures Temperature Exterieure.Temperature Exterieure\n" //
                        +"763,24,31,1,0,0,,220208,620208,ff,Config Entretien_Nb de maj par ABS\n" //
                        +"763,24,31,1,0,0,,22010D,62010D,ff,Config Generale_AAP UPA,0:Sans AAP (par défaut);16:Avec APP AV et AR;32:Avec APP AR uniquement\n" //
                        +"763,24,31,1,0,0,,220121,620121,ff,Config Generale_montre,0:sans affichage montre ( par défaut);16:montre 12 heures;32:montre 24 heures\n" //
                        +"763,24,31,1,0,0,,220119,620119,ff,Config Generale_Essuyage automatique,0:Sans CFG MPU Ess lunette marche arr (par défaut);16:Avec CFG MPU Essuyage lunette marche arriere\n" //
                        +"763,24,31,1,0,0,,220109,620109,ff,Config Generale_Inhib SBR_av,0:Buzzer SBR avant non inhibé (par défaut);16:Buzzer SBR avant inhibe\n" //
                        +"763,24,31,1,0,0,,220129,620129,ff,Config Generale_NAV,0:sans NAV (par defaut);16:MFD présent\n" //
                        +"763,24,31,1,0,0,,22010E,62010E,ff,Config Generale_SSPP TPMS,0:Sans SSPP (par défaut);16:Avec SSPP et sans stratégie de reset;32:Avec SSPP et avec stratégie de reset\n" //
                        +"763,24,31,1,0,0,,220104,620104,ff,Config Generale_Distance,0:Km (par défaut);16:Miles\n" //
                        +"763,24,31,1,0,0,,220107,620107,ff,Config Generale_pression,0:BAR (par défaut);16:PSI\n" //
                        +"763,24,47,1,0,0,Km,222605,622605,ff,DataRead Odo TdB avant recalage TdB par ABS\n" //
                        +"763,24,47,1,0,0,Km,222604,622604,ff,DataRead Odo TdB avant recalage ABS par TdB\n" //
                        +"763,24,31,1,0,0,,222001,622001,ff,Entrees filaires_contact frein a main,0:Frein desserré (contact ouvert);1:Frein serré (contact fermé)\n" //
                        +"763,24,39,1,0,0,mV,222002,622002,ff,Entrees filaires_incident frein NivoCod.Tension Nivocod\n" //
                        +"763,40,47,1,0,0,,222002,622002,ff,Entrees filaires_incident frein NivoCod.Entree Nivocod,0:Pas de défaut;1:Défaut Nivocod;2:Entrée en CC;3:Entrée en CO\n" //
                        +"763,24,31,1,0,0,,220116,620116,ff,Config Generale_Sons clignotants personalise,0:Not tuned (default value);16:Tuned\n" //
                        +"763,24,31,1,0,0,,220102,620102,ff,Config Generale_Sieges chauffants,0:Sans Sièges Chauffants;16:Avec Sièges Chauffants\n" //

                ;

        String dtcDef =
                ""

                        +"940D,Paramètre CAN produit Vitesse Affichée\n" //
                        +"934C,Surveillance du controleur CAN-V\n" //
                        +"9309,Surveillance de la sortie SYS-ON\n" //
                        +"934D,Surveillance du controleur CAN-M\n" //
                        +"9408,Surv Com Témoin Ceinture\n" //
                        +"9404,Surv Témoin Défaut Airbag\n" //
                        +"940C,Surv E2prom\n" //
                        +"F003,Surv Alimentation\n" //
                        +"C452,ValidAirbagInformation = 0 pendant 60 s\n" //
                        +"C418,Surveillance VehicleSpeed Invalide\n" //
                        +"C127,Perte trames TPMS (SSPP)_Id 0x673\n" //
                        +"C134,Perte trames TCU_ Id 0x634\n" //
                        +"C131,Perte trames EPS_ Id 0x62C\n" //
                        +"C133,Perte trames UBP_ Id 0x66D\n" //
                        +"C132,Perte trames Clim_ Id 0x668\n" //
                        +"C159,Perte trames UPA_ Id 0x5E9\n" //
                        +"C151,Perte trames Airbag_Id 0x653\n" //
                        +"C140,Perte trames BCM_Id 0x35C\n" //
                        +"C129,Perte trames Brake (ABS)_Id 0x5D7\n" //
                        +"C111,Perte trames USM_Id 0x212\n" //
                        +"C100,Perte trames SCH (EVC) _ Id 0x652\n" //
                        +"9412,sonde temperature\n" //

                ;

        String testDef =
                ""

                        +"47,watchdog / safety µC failure\n" //
                        +"76,wrong mounting position\n" //
                        +"83,value of signal protection calculation incorrect\n" //
                        +"05,System Programming Failures\n" //
                        +"04,System Internal Failures\n" //
                        +"48,supervision software failure\n" //
                        +"43,special memory failure\n" //
                        +"23,signal stuck low\n" //
                        +"24,signal stuck high\n" //
                        +"29,signal signal invalid\n" //
                        +"25,signal shape / waveform failure\n" //
                        +"26,signal rate of change below threshold\n" //
                        +"27,signal rate of change above threshold\n" //
                        +"64,signal plausibility failure\n" //
                        +"33,signal low time > maximum\n" //
                        +"32,signal low time < minimum\n" //
                        +"86,signal invalid\n" //
                        +"67,signal incorrect after event\n" //
                        +"35,signal high time > maximum\n" //
                        +"34,signal high time < minimum\n" //
                        +"66,signal has too many transitions / events\n" //
                        +"65,signal has too few transitions / events\n" //
                        +"36,signal frequency too low\n" //
                        +"37,signal frequency too high\n" //
                        +"38,signal frequency incorrect\n" //
                        +"2F,signal erratic\n" //
                        +"62,signal compare failure\n" //
                        +"61,signal calculation failure\n" //
                        +"28,signal bias level out of range / 0 adjust failure\n" //
                        +"84,signal below allowable range\n" //
                        +"22,signal amplitude > maximum\n" //
                        +"21,signal amplitude < minimum\n" //
                        +"85,signal above allowable range\n" //
                        +"45,program memory failure\n" //
                        +"92,performance or incorrect operation\n" //
                        +"91,parametric\n" //
                        +"4B,over temperature\n" //
                        +"51,not programmed\n" //
                        +"55,not configured\n" //
                        +"52,not activated\n" //
                        +"31,no signal\n" //
                        +"93,no operation\n" //
                        +"87,missing message\n" //
                        +"54,missing calibration\n" //
                        +"79,mechanical linkage failure\n" //
                        +"07,Mechanical Failures\n" //
                        +"7B,low fluid level\n" //
                        +"81,invalid serial data received\n" //
                        +"49,internal electronic failure\n" //
                        +"3A,incorrect has too many pulses\n" //
                        +"39,incorrect has too few pulses\n" //
                        +"4A,incorrect component installed\n" //
                        +"94,unexpected operation\n" //
                        +"02,General signal failure\n" //
                        +"42,general memory failure\n" //
                        +"01,General Electrical Failure\n" //
                        +"41,general checksum failure\n" //
                        +"03,FM (Frequency Modulated) / PWM  Failures\n" //
                        +"7A,fluid leak or seal failure\n" //
                        +"68,event information\n" //
                        +"8F,erratic\n" //
                        +"75,emergency position not reachable\n" //
                        +"00,Device and failure type ODB codding\n" //
                        +"53,deactivated\n" //
                        +"44,data memory failure\n" //
                        +"95,incorrect assembly\n" //
                        +"97,Component or system operation obstructed or blocke\n" //
                        +"96,component internal failure\n" //
                        +"09,Component Failures\n" //
                        +"77,commanded position not reachable\n" //
                        +"1C,circuit voltage out of range\n" //
                        +"16,circuit voltage below threshold\n" //
                        +"17,circuit voltage above threshold\n" //
                        +"14,circuit short to ground or open\n" //
                        +"11,circuit short to ground\n" //
                        +"15,circuit short to battery or open\n" //
                        +"12,circuit short to battery\n" //
                        +"1E,circuit resistance out of range\n" //
                        +"1A,circuit resistance below threshold\n" //
                        +"1B,circuit resistance above threshold\n" //
                        +"13,circuit open\n" //
                        +"1F,circuit intermittent\n" //
                        +"1D,circuit current out of range\n" //
                        +"18,circuit current below threshold\n" //
                        +"19,circuit current above threshold\n" //
                        +"63,circuit / component protection time-out\n" //
                        +"46,calibration / parameter memory failure\n" //
                        +"08,Bus Signal / Message Failures\n" //
                        +"88,bus off\n" //
                        +"82,alive / sequence counter incorrect / not updated\n" //
                        +"78,alignment or adjustment incorrect\n" //
                        +"06,Algorithm Based Failures\n" //
                        +"72,actuator stuck open\n" //
                        +"73,actuator stuck closed\n" //
                        +"71,actuator stuck\n" //
                        +"74,actuator slipping\n" //
                        +"98,component or system over temperature\n" //

                ;

        Frames.getInstance().load("763,0,0,CLUSTER\n");
        Fields.getInstance().load(fieldDef1);
        Dtcs.getInstance().load(dtcDef, testDef);
    }
}