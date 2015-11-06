package lu.fisch.canze.actors;

import java.util.ArrayList;

/**
 * Frames
 */
public class Frames {

    private final ArrayList<Frame> frames = new ArrayList<>();

    private static Frames instance = null;

    private Frames() {
        fillStatic();
    }

    public static Frames getInstance()
    {
        if(instance==null) instance=new Frames();
        return instance;
    }

    private void fillStatic() {
        String frameDef = // Id, interval, sendingEcu
                ""
                        + "23,4,AIBAG\n"
                        +"";
        String[] lines = frameDef.split("\n");
        for (String line : lines) {
            //Get all tokens available in line
            String[] tokens = line.split(",");
            if (tokens.length > 0) {
                //Create a new field object and fill his  data
                Ecu ecu = Ecus.getInstance().getByMnemonic(tokens[0].trim());
                Frame frame = new Frame(
                        Integer.parseInt(tokens[3].trim(), 16),
                        Integer.parseInt(tokens[3].trim(), 10),
                        ecu
                );
                // add the field to the list of available fields
                add(frame);
            }
        }
    }

    public void add(Frame frame) {
        frames.add(frame);
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
