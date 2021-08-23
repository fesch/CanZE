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
 * ECU
 */
public class Ecu {

    private final String name;
    private final int renaultId;
    private final String networks;    // single letter network names, semicolon separated, V, M, O, E
    private final int fromId;
    private final int toId;
    private final String mnemonic;
    private final String aliases;     // semicolon separated
    private final String getDtcs;   // semicolon separated
    private final String startDiag;
    private final boolean sessionRequired;

    private Fields fields;

    public Ecu(String name, int renaultId, String networks, int fromId, int toId, String mnemonic, String aliases, String getDtcs, String startDiag, boolean sessionRequired) {
        this.name = name;
        this.renaultId = renaultId;
        this.networks = networks;
        this.fromId = fromId;
        this.toId = toId;
        this.mnemonic = mnemonic;
        this.aliases = aliases;
        this.getDtcs = getDtcs;
        this.startDiag = startDiag;
        this.sessionRequired = sessionRequired;
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

    public String getHexFromId() {
        return Integer.toHexString(fromId);
    }

    public int getToId() {
        return toId;
    }

    public String getHexToId() {
        return Integer.toHexString(toId);
    }

    public String getMnemonic() {
        return mnemonic;
    }

    public String getAliases() {
        return aliases;
    }

    public Fields getFields() {
        return fields;
    }

    public void setFields(Fields fields) {
        this.fields = fields;
    }

    public String getGetDtcs() {
        return getDtcs;
    }

    public String getStartDiag() {
        return startDiag;
    }

    public boolean getSessionRequired() {
        return sessionRequired;
    }
}
