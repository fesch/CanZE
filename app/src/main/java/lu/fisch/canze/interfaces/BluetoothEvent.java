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
