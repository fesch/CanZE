package lu.fisch.canze.actors;

/**
 * Quick and dirty DTC lookup. Needs to be made fast and als needs an int lookup.
 */
public class Dtcs {

    private static final String [] dtc = {
            "AE01F0,COMMUNICATION PROTOCOL ERROR",
            "921315,RIGHT-HAND DIPPED HEADLIGHT CIRCUIT",
            "060198,ELECTRIC MOTOR PERFORMANCE"
    };

    static public String getDescription (String dtcCode) {
        for (String aDtc : dtc) {
            if (aDtc.startsWith(dtcCode)) {
                return (aDtc.substring(7));
            }
        }
        return "";
    }
}
