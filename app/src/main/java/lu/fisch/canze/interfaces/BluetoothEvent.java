package lu.fisch.canze.interfaces;

import android.bluetooth.BluetoothSocket;

import lu.fisch.canze.bluetooth.ConnectedBluetoothThread;

/**
 * Created by robertfisch on 04.09.2015.
 */
public interface BluetoothEvent {
    public void onBeforeConnect();
    public void onAfterConnect(BluetoothSocket bluetoothSocket, ConnectedBluetoothThread connectedBluetoothThread);
    public void onBeforeDisconnect(BluetoothSocket bluetoothSocket, ConnectedBluetoothThread connectedBluetoothThread);
    public void onAfterDisconnect();
}
