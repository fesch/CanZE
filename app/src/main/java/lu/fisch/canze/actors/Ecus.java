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
 * Ecus
 */
public class Ecus {

    private final ArrayList<Ecu> ecus = new ArrayList<>();

    private static Ecus instance = null;

    private Ecus() {
        load();
    }

    public static Ecus getInstance()
    {
        if(instance==null) instance=new Ecus();
        return instance;
    }


    private void fillOneLine(String line) {
        if (line.contains("#")) line = line.substring(0, line.indexOf('#'));
        //Get all tokens available in line
        String[] tokens = line.split(",");
        if (tokens.length == 10) {
            //Create a new field object and fill his  data
            Ecu ecu = new Ecu(
                    tokens[0].trim(),                           // name
                    Integer.parseInt(tokens[1].trim()),         // Renault ID
                    tokens[2].trim(),                           // Network
                    Integer.parseInt(tokens[3].trim(), 16),     // From ID
                    Integer.parseInt(tokens[4].trim(), 16),     // To ID
                    tokens[5].trim(),                           // Mnemonic
                    tokens[6].trim(),                           // Aliasses, semicolon separated
                    tokens [7].trim(),                          // GetDtc responseIDs, semicolon separated
                    tokens [8].trim(),                          // startDiag
                    tokens [9].trim().compareTo("1") == 0       // Session required
            );
            // add the field to the list of available fields
            add(ecu);
            MainActivity.debug("ecu:" + tokens[5].trim());
        }
    }


    private void fillFromAsset (String assetName) {
        //Read text from asset
        AssetLoadHelper assetLoadHelper = new AssetLoadHelper(MainActivity.getInstance());
        BufferedReader bufferedReader = assetLoadHelper.getBufferedReaderFromAsset(assetName);
        if (bufferedReader == null) {
            MainActivity.toast(MainActivity.TOAST_NONE, "Can't access asset " + assetName);
            return;
        }
        try {
            String line;
            while ((line = bufferedReader.readLine()) != null)
                fillOneLine(line);
            bufferedReader.close();
        }
        catch (IOException e) {
            MainActivity.logExceptionToCrashlytics(e);
        }
    }

    public void load ()
    {
        load ("");
    }


    public void load (String assetName)
    {
        ecus.clear();
        if (assetName.equals("")) {
            fillFromAsset(getDefaultAssetName());
        } else {
            fillFromAsset(assetName);
        }
    }


    public void add(Ecu ecu) {
        ecus.add(ecu);
    }

    public Ecu getByMnemonic (String mnemonic) {
        for (Ecu ecu : ecus) {
            if (ecu.getMnemonic().equals(mnemonic) || ecu.getAliases().contains(mnemonic)) return ecu;
        }
        return null;
    }

    public Ecu getByRenaultId (int renaultId) {
        for (Ecu ecu : ecus) {
            if (ecu.getRenaultId() == renaultId) return ecu;
        }
        return null;
    }

    public Ecu getByFromId (int fromId) {
        for (Ecu ecu : ecus) {
            if (ecu.getFromId() == fromId) return ecu;
        }
        return null;
    }

    public Ecu getByToId (int toId) {
        for (Ecu ecu : ecus) {
            if (ecu.getToId() == toId) return ecu;
        }
        return null;
    }

    public ArrayList<Ecu> getAllEcus () {
        return ecus;
    }

    private String getDefaultAssetName() {
        if (MainActivity.isZOE()) return "_Ecus.csv";
        else if (MainActivity.isZOEZE50()) return "_EcusZE50.csv";
        return "_Ecus.csv";
    }

}
