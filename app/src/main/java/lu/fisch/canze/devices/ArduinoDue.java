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

package lu.fisch.canze.devices;

import java.util.ArrayList;

import lu.fisch.canze.actors.Field;
import lu.fisch.canze.actors.Message;

/**
 * Created by robertfisch on 07.09.2015.
 */
public class ArduinoDue extends Device {

    @Override
    public void initConnection() {

    }

    @Override
    public void registerFilter(int frameId) {

    }

    @Override
    public void unregisterFilter(int frameId) {

    }

    public void join() throws InterruptedException {

    }

    @Override
    protected ArrayList<Message> processData(int[] input) {
        return null;
    }

    @Override
    public String requestFreeFrame(Field field) {
        return null;
    }

    @Override
    public String requestIsoTpFrame(Field field) {
        return null;
    }

    @Override
    public boolean initDevice(int toughness) { return true; }

    @Override
    protected boolean initDevice(int toughness, int retries) {
        return false;
    }
}
