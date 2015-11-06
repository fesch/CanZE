package lu.fisch.canze.actors;

/**
 * ECU
 */
public class Ecu {

    private String name;
    private int renaultId;
    private String networks; // single letter network names, semicolon separated, V, M, O, E
    private int fromId;
    private int toId;
    private String mnemonic;
    private String aliases; // semicolon separated

    public Ecu(String name, int renaultId, String networks, int fromId, int toId, String mnemonic, String aliases) {
        this.name = name;
        this.renaultId = renaultId;
        this.networks = networks;
        this.fromId = fromId;
        this.toId = toId;
        this.mnemonic = mnemonic;
        this.aliases = aliases;
    }

    public String getName() {
        return name;
    }

    public int getRenaultId() {
        return renaultId;
    }

    public String getNetworks() {
        return networks;
    }

    public int getFromId() {
        return fromId;
    }

    public int getToId() {
        return toId;
    }

    public String getMnemonic() {
        return mnemonic;
    }

    public String getAliases() {
        return aliases;
    }

}
