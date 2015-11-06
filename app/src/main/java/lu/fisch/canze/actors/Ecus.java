package lu.fisch.canze.actors;

import java.util.ArrayList;

/**
 * Ecus
 */
public class Ecus {

    private final ArrayList<Ecu> ecus = new ArrayList<>();

    private static Ecus instance = null;

    private Ecus() {
        fillStatic();
    }

    public static Ecus getInstance()
    {
        if(instance==null) instance=new Ecus();
        return instance;
    }

    private void fillStatic() {
        String ecuDef = // name,renaultId,networks,fromId,toId,mnemonic,aliases
                ""
                        + "Electric Vehicle Controller,946,V;E,7ec,7e4,EVC,SCH\n"
                        + "Telematics Control Unit,2152,V;M,7da,7ca,TCU,\n"
                        + "Lithium Battery Controller,938,E,7bb,79b,LBC,\n"
                        + "Power Electronics Block,2092,E,77e,75a,PEB,\n"
                        + "Airbag,756,V,772,752,AIBAG,AIRBAG\n"
                        + "U Safety Module,1337,V,76d,74d,USM,UPC;UCM\n"
                        + "\n"
                        + "\n"
                        + "\n"
                        + "\n"
                        + "\n"
                        + "\n"
                        + "\n"
                        + "\n"
                        + "\n"
                        + "\n"
                        + "\n"
                        + "\n"
                        + "\n"
                        + "\n"

                        +"";
        String[] lines = ecuDef.split("\n");
        for (String line : lines) {
            //Get all tokens available in line
            String[] tokens = line.split(",");
            if (tokens.length > 0) {
                //Create a new field object and fill his  data
                Ecu ecu = new Ecu(
                        tokens[0].trim(),
                        Integer.parseInt(tokens[1].trim()),
                        tokens[2].trim(),
                        Integer.parseInt(tokens[3].trim(), 16),
                        Integer.parseInt(tokens[4].trim(), 16),
                        tokens[2].trim(),
                        tokens[2].trim()
                );
                // add the field to the list of available fields
                add(ecu);
            }
        }
    }

    public void add(Ecu ecu) {
        ecus.add(ecu);
    }

    public Ecu getByMnemonic (String mnemonic) {
        return null;
    }

    public Ecu getByAlias (String mnemonic) {
        return null;
    }

    public Ecu getByRenaultId (int renaultId) {
        return null;
    }



}
