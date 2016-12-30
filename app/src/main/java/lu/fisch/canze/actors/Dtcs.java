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

import java.util.ArrayList;

/**
 * DTC id's usually are 3 bytes long. The first 2 bytes are an ID, the third byte reperesent a test
 * The first nibble is often displayed in an alternative way
 *
 */
public class Dtcs {

    private final ArrayList<Dtc> dtcs = new ArrayList<>();
    private final ArrayList<Test> tests = new ArrayList<>();

    private static Dtcs instance = null;

    private Dtcs() {
        fillStatic();
    }

    public static Dtcs getInstance()
    {
        if(instance==null) instance=new Dtcs();
        return instance;
    }

    private void fillStatic() {
        String dtcDef =
                ""
            + "061263,BATTERY CHARGE CIRCUIT\n" //
            + "066263,BATTERY CHARGE CIRCUIT\n" //
            + "064913,BATTERY CHARGE CIRCUIT\n" //
            + "064764,BATTERY CHARGE CIRCUIT\n" //
            + "C10000,EVC ABSENT MULTIPLEX SIGNAL SENT\n" //
            + "518196,ESP MULTIPLEX INFORMATION PLAUSIBILITY\n" //
            + "C12200,NO ABS/ESP MULTIPLEX SIGNAL\n" //
            + "518296,EVC MULTIPLEX INFORMATION PLAUSIBILITY\n" //
            + "508E55,UBP SOLENOID VALVE\n" //
            + "C07300,MULTIPLEXED NETWORK\n" //
            + "C00100,NO MULTIPLEX SIGNAL\n" //
            + "921215,LEFT-HAND DIPPED HEADLIGHT CIRCUIT\n" //
            + "921315,RIGHT-HAND DIPPED HEADLIGHT CIRCUIT\n" //
            + "AE01F0,COMMUNICATION PROTOCOL ERROR\n" //
            + "041168,CAN COMMUNICATION\n" //
            + "1525F3,CONSISTENT MULTIPLEX SIGNALS FOR CC/SL\n" //
            + "060198,ELECTRIC MOTOR PERFORMANCE"
    ;

        String testDef =
                ""
                        + "104,event information\n" //

                ;

        fillDynamic(dtcDef, testDef);
}

    private void fillDynamic (String dtcDef, String testDef) {
        String[] lines = dtcDef.split("\n");
        for (String line : lines) {
            //Get all tokens available in line
            String[] tokens = line.split(",");
            if (tokens.length == 2) {
                //Create a new dtc object and fill his  data
                Dtc dtc = new Dtc(
                        tokens[0].trim(),
                        tokens[1].trim()
                );
                // add the dtc to the list of available fields
                add(dtc);
            }
        }
        lines = testDef.split("\n");
        for (String line : lines) {
            //Get all tokens available in line
            String[] tokens = line.split(",");
            if (tokens.length == 2) {
                //Create a new Test object and fill his  data
                Test test = new Test(
                        tokens[0].trim(),
                        tokens[1].trim()
                );
                // add the test to the list of available fields
                addTest(test);
            }
        }
    }

    public void add(Dtc dtc) {
        dtcs.add(dtc);
    }

    public void addTest(Test test) {
        tests.add(test);
    }

    public Dtc getDtcById (String id) {
        for (Dtc dtc : dtcs) {
            if (dtc.getId().compareTo(id) == 0) return dtc;
        }
        return null;
    }

    public Test getTestById (String id) {
        for (Test test : tests) {
            if (test.getId().compareTo(id) == 0) return test;
        }
        return null;
    }

    public String getDescriptionById (String id) {
        Dtc dtc = null;
        Test test = null;
        String result = "";

        if (id.length() >= 4){
            dtc = getDtcById(id.substring(0, 4));
            if (dtc != null) {
                result += dtc.getDescription();
            }
            if (id.length() >= 6) {
                test = getTestById(id.substring(4, 6));
                if (test != null) {
                    result += "\n" + test.getDescription();
                }
            }
        } else {
            result = "Too short DTC " + id;
        }
        return result;
    }

    public String getDisplayCodeById (String id) {
        if (id.length() == 0) return ("");
        switch (id.toUpperCase().charAt(0)) {
            case '0': return "P0" + id.substring(2);
            case '1': return "P1" + id.substring(2);
            case '2': return "P2" + id.substring(2);
            case '3': return "P3" + id.substring(2);
            case '4': return "C0" + id.substring(2);
            case '5': return "C1" + id.substring(2);
            case '6': return "C2" + id.substring(2);
            case '7': return "C3" + id.substring(2);
            case '8': return "B0" + id.substring(2);
            case '9': return "B1" + id.substring(2);
            case 'A': return "B2" + id.substring(2);
            case 'B': return "B3" + id.substring(2);
            case 'C': return "U0" + id.substring(2);
            case 'D': return "U1" + id.substring(2);
            case 'E': return "U2" + id.substring(2);
            case 'F': return "U3" + id.substring(2);
        }
        return id;
    }

    public String getFlagDescription (int flags) {
        String result = "";

        if ((flags & 0x01) != 0) result += ", tstFail";
        if ((flags & 0x02) != 0) result += ", tstFailThisOp";
        if ((flags & 0x04) != 0) result += ", pendingDtc";
        if ((flags & 0x08) != 0) result += ", confirmedDtc";
        if ((flags & 0x10) != 0) result += ", noCplSinceClear";
        if ((flags & 0x20) != 0) result += ", faildSinceClear";
        if ((flags & 0x40) != 0) result += ", tstNtCpl";
        if ((flags & 0x80) != 0) result += ", wrnLght";
        if (result.length() == 0) return "";
        return result.substring(2);
    }

    public ArrayList<Dtc> getAllDtcs () {
        return dtcs;
    }

    public ArrayList<Test> getAllTests () {
        return tests;
    }

    public void load ()
    {
        dtcs.clear();
        fillStatic();
    }

    public void load (String initDtcString, String initTestString)
    {
        dtcs.clear();
        fillDynamic(initDtcString, initTestString);
    }

}
