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

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;

import lu.fisch.canze.activities.MainActivity;

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
        load();
    }

    public static Dtcs getInstance() {
        if (instance == null) instance = new Dtcs();
        return instance;
    }


    private void fillOneDtcLine(String line) {
        if (line.contains("#")) line = line.substring(0, line.indexOf('#'));
        //Get all tokens available in line
        String[] tokens = line.split(",");
        if (tokens.length == 2) {
            //Create a new dtc object and fill his  data
            Dtc dtc = new Dtc(
                    tokens[0].trim(),
                    tokens[1].trim()
            );
            // add the dtc to the list of available fields
            addDtc(dtc);
        }
    }

    private void fillOneTestLine(String line) {
        if (line.contains("#")) line = line.substring(0, line.indexOf('#'));
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


    private void fillFromAsset (String dtcsAssetName, String testsAssetName) {
        //Read text from asset
        AssetLoadHelper assetLoadHelper = new AssetLoadHelper(MainActivity.getInstance());
        BufferedReader bufferedReader = assetLoadHelper.getBufferedReaderFromAsset(dtcsAssetName);
        if (bufferedReader == null) {
            MainActivity.toast(-100, "Can't access asset " + dtcsAssetName);
            return;
        }
        try {
            String line;
            while ((line = bufferedReader.readLine()) != null)
                fillOneDtcLine(line);
            bufferedReader.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        bufferedReader = assetLoadHelper.getBufferedReaderFromAsset(testsAssetName);
        if (bufferedReader == null) {
            MainActivity.toast(-100, "Can't access asset " + testsAssetName);
            return;
        }
        try {
            String line;
            while ((line = bufferedReader.readLine()) != null)
                fillOneTestLine(line);
            bufferedReader.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void load ()
    {
        load ("", "");
    }

    public void load (String dtcsAssetName, String testsAssetName)
    {
        dtcs.clear();
        tests.clear();
        if (dtcsAssetName.equals("")) {
            fillFromAsset("_Dtcs.csv", "_Tests.csv");
        } else {
            fillFromAsset(dtcsAssetName, testsAssetName);
        }
    }


    public void addDtc(Dtc dtc) {
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
        Dtc dtc;
        Test test;
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

}
