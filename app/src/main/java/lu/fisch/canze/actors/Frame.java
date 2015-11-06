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
 * Frame
 */
public class Frame {

    private int id;
    private int interval; // in ms
    private Ecu sendingEcu;

    public Frame (int id, int interval, Ecu sendingEcu) {
        this.id = id;
        this.interval = interval;
        this.sendingEcu = sendingEcu;
    }

    public int getId() {
        return id;
    }

    public int getInterval() {
        return interval;
    }

    public Ecu getSendingEcu() {
        return sendingEcu;
    }

}
