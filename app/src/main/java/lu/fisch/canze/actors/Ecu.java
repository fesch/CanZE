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

    private String name;
    private int renaultId;
    private String networks;    // single letter network names, semicolon separated, V, M, O, E
    private int fromId;
    private int toId;
    private String mnemonic;
    private String aliases;     // semicolon separated
    private Fields fields;
    private String getDtcs;   // semicolon separated

    public Ecu(String name, int renaultId, String networks, int fromId, int toId, String mnemonic, String aliases, String getDtcs) {
        this.name = name;
        this.renaultId = renaultId;
        this.networks = networks;
        this.fromId = fromId;
        this.toId = toId;
        this.mnemonic = mnemonic;
        this.aliases = aliases;
        this.getDtcs = getDtcs;
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
}
