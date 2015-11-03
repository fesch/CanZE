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

package lu.fisch.canze.interfaces;

import android.bluetooth.BluetoothSocket;

/**
 * Created by robertfisch on 04.09.2015.
 */
public interface BluetoothEvent {
    public void onBeforeConnect();
    public void onAfterConnect(BluetoothSocket bluetoothSocket);
    public void onBeforeDisconnect(BluetoothSocket bluetoothSocket);
    public void onAfterDisconnect();
}
