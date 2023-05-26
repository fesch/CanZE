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


/*
 * Helper class to manage the Bluetooth connection
 */
package lu.fisch.canze.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Build;
import android.os.ParcelFileDescriptor;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.security.InvalidParameterException;
import java.util.UUID;

import lu.fisch.canze.activities.MainActivity;
import lu.fisch.canze.interfaces.BluetoothEvent;

/**
 * Created by robertfisch on 03.09.2015.
 */
public class BluetoothManager {

    /* --------------------------------
     * Sigleton stuff
     \ ------------------------------ */

    private static BluetoothManager instance = null;

    private InputStream inputStream = null;
    private OutputStream outputStream = null;

    public static BluetoothManager getInstance() {
        if (instance == null)
            instance = new BluetoothManager();
        return instance;
    }

    /* --------------------------------
     * Attributes
     \ ------------------------------ */
    // SPP UUID service
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    public static final int STATE_BLUETOOTH_NOT_AVAILABLE = -1;
    public static final int STATE_BLUETOOTH_ACTIVE = 1;
    public static final int STATE_BLUETOOTH_NOT_ACTIVE = 0;


    private BluetoothAdapter bluetoothAdapter;
    private BluetoothSocket bluetoothSocket = null;

    public boolean isDummyMode() {
        return dummyMode;
    }

    private boolean dummyMode = false;

    private BluetoothEvent bluetoothEvent;

    public static final int RETRIES_NONE = 0;
    public static final int RETRIES_INFINITE = -1;
    private Thread retryThread = null;
    private final String retryLock = ""; // ugly! but retryThread itself can be null

    private String connectBluetoothAddress = null;
    private boolean connectSecure;
    private int connectRetries;
    private boolean retry = true;

    private void debug(String text) {
        MainActivity.debug(this.getClass().getSimpleName() + ": " + text);
    }

    /**
     * Create a new manager
     */
    private BluetoothManager() {
        // get Bluetooth adapter
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    /**
     * Determine the state of the Bluetooth hardware
     *
     * @return the state of the Bluetooth hardware
     */
    public int getHardwareState() {
        // Check for Bluetooth support and then check to make sure it is turned on
        // Emulator doesn't support Bluetooth and will return null

        if (dummyMode) return STATE_BLUETOOTH_ACTIVE;

        if (bluetoothAdapter == null) {
            return STATE_BLUETOOTH_NOT_AVAILABLE;
        } else {
            if (bluetoothAdapter.isEnabled()) {
                return STATE_BLUETOOTH_ACTIVE;
            } else {
                return STATE_BLUETOOTH_NOT_ACTIVE;
            }
        }
    }

    /**
     * Creates a new Bluetooth socket from a given device
     */
    private BluetoothSocket createBluetoothSocket(BluetoothDevice device, boolean secure) throws IOException {
        try {
            final Method m = device.getClass().getMethod(secure ? "createRfcommSocketToServiceRecor" : "createInsecureRfcommSocketToServiceRecord", new Class[]{UUID.class});
            return (BluetoothSocket) m.invoke(device, MY_UUID);
        } catch (Exception e) {
            debug("Could not create RFComm Connection");
        }
        return device.createRfcommSocketToServiceRecord(MY_UUID);
    }

    public void connect() {
        if (dummyMode) return;

        if (connectBluetoothAddress == null)
            throw new InvalidParameterException("connect() has to be called at least once with parameters!");
        connect(connectBluetoothAddress, connectSecure, connectRetries);
    }

    public void connect(final String bluetoothAddress, final boolean secure, final int retries) {

        if (dummyMode) return;

        retry = true;
        privateConnect(bluetoothAddress, secure, retries);
    }

    private void privateConnect(final String bluetoothAddress, final boolean secure, final int retries) {
        synchronized (retryLock) {
            if (!(retryThread == null || !retryThread.isAlive())) {
                debug("BT: aborting connect (another one is in progress ...)");
                return;
            }
        }

        if (retry) {
            // remember parameters
            connectBluetoothAddress = bluetoothAddress;
            connectSecure = secure;
            connectRetries = retries;

            // only continue if we got an address
            if (bluetoothAddress != null && !bluetoothAddress.isEmpty() && getHardwareState() == STATE_BLUETOOTH_ACTIVE) {

                // make sure there is no more active connection
                if (bluetoothSocket != null && bluetoothSocket.isConnected()) {
                    try {
                        debug("Closing previous socket");
                        // bluetoothSocket.close();
                        closeSocketAndclearFileDescriptor();
                        bluetoothSocket = null;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                // execute attached event
                if (bluetoothEvent != null) bluetoothEvent.onBeforeConnect();

                // set up a pointer to the remote node using it's address.
                debug("Get remote device: " + bluetoothAddress);
                BluetoothDevice device = bluetoothAdapter.getRemoteDevice(bluetoothAddress);

                // create a socket
                try {
                    debug("Create new socket");
                    bluetoothSocket = createBluetoothSocket(device, secure);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                // discovery is resource intensive so make sure it is stopped
                debug("Cancel discovery");
                bluetoothAdapter.cancelDiscovery();

                try {
                    debug("Connect the socket");
                    if (bluetoothSocket != null)
                        bluetoothSocket.connect();

                    debug("Connect the streams");
                    // init with null
                    inputStream = null;
                    outputStream = null;
                    // connect input
                    if(bluetoothSocket!=null)
                        inputStream = bluetoothSocket.getInputStream();

                    // connect output
                    if(bluetoothSocket!=null)
                        outputStream = bluetoothSocket.getOutputStream();

                    // execute attached event
                    if (bluetoothEvent != null)
                        bluetoothEvent.onAfterConnect(bluetoothSocket);

                    // stop here and return if everything went well
                    if(inputStream != null && outputStream != null) {
                        debug("Connected");
                        return;
                    }
                } catch (IOException | SecurityException e) {
                    //e.printStackTrace();
                }
            }
            // if we reach this line, something went wrong and no connection has been established
            debug("Something went wrong");
            if (bluetoothAddress == null || bluetoothAddress.isEmpty())
                debug("No device address given");
            else if (getHardwareState() == STATE_BLUETOOTH_NOT_ACTIVE)
                debug("Bluetooth not active");

            if (bluetoothSocket != null)
                // try {
                    debug("Closing socket again ...");
                    // bluetoothSocket.close();
                    closeSocketAndclearFileDescriptor();
                // } catch (IOException e) {
                //     e.printStackTrace();
                // }

            debug(retries + " tries left");
            if (retries != RETRIES_NONE) {
                // avoid thread state mismatches
                synchronized (retryLock) {
                    if (retryThread == null || !retryThread.isAlive()) {
                        if (retryThread != null) {
                            retryThread.interrupt();
                        }
                        debug("Starting new try");
                        retryThread = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    Thread.sleep(2 * 1000);
                                } catch (Exception e) {
                                    // ignore
                                }
                                (new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        BluetoothManager.getInstance().privateConnect(bluetoothAddress, secure, retries - 1);
                                    }
                                })).start();
                            }
                        });
                        // if STILL something goes wrong, in spite of the synchronize, throw hands in the air and do nothing
                        try {
                            retryThread.start();
                        } catch (Exception e) {
                            // do nothing
                        }
                    } else {
                        debug("Another try is still running --> abort this one");
                        debug("Alive: " + retryThread.isAlive());
                    }
                }
            }
        }
    }

    public void disconnect() {

        if (dummyMode) return;

        try {
            // execute attached event
            if (bluetoothEvent != null) bluetoothEvent.onBeforeDisconnect(bluetoothSocket);

            retry = false;

            if (retryThread != null && retryThread.isAlive()) {
                debug("Waiting for retry-thread to stop ...");
                retryThread.join();
            }

            debug("Closing socket");
            // close the socket
            if (bluetoothSocket != null)
                // bluetoothSocket.close();
                closeSocketAndclearFileDescriptor();

            // execute attached event
            if (bluetoothEvent != null) bluetoothEvent.onAfterDisconnect();

            debug("Closed");
        // } catch (IOException e) {
        //     e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /* --------------------------------
     * input / output
     \ ------------------------------ */

    // write a message to the output stream
    public void write(String message) {

        if (dummyMode) return;

        if (bluetoothSocket != null && bluetoothSocket.isConnected()) {
            byte[] msgBuffer = message.getBytes();
            try {
                // outputstream should never be nll here, but seen it in crash reports so
                // the strategy is to simply avoid
                if (outputStream != null) {
                    outputStream.write(msgBuffer);
                } else {
                    MainActivity.debug("BluetoothManager.write: outputStream is null");
                }
            } catch (IOException e) {
                MainActivity.debug("BT: Error sending > " + e.getMessage());
            }
        } else {
            MainActivity.debug("Write failed! Socket is closed ... M = " + message);
        }
    }

    public int read(byte[] buffer) throws IOException {

        if (dummyMode || bluetoothSocket == null) return 0;

        if (bluetoothSocket.isConnected())
            return inputStream.read(buffer);
        else
            return 0;
    }

    public int read() throws IOException {

        if (dummyMode || bluetoothSocket == null) return -1;

        if (bluetoothSocket.isConnected() && inputStream != null)
            return inputStream.read();
        else
            return -1;
    }

    public int available() throws IOException {

        if (dummyMode || bluetoothSocket == null) return 0;

        if (bluetoothSocket.isConnected() && inputStream != null)
            return inputStream.available();
        else
            return 0;
    }

    public boolean isConnected() {

        if (dummyMode) return true;

        if (bluetoothSocket == null) return false;
        return bluetoothSocket.isConnected();
    }

    /* --------------------------------
     * Events
     \ ------------------------------ */

    public void setBluetoothEvent(BluetoothEvent bluetoothEvent) {

        if (dummyMode) return;

        this.bluetoothEvent = bluetoothEvent;
    }

    public void setDummyMode(boolean dummyMode) {
        this.dummyMode = dummyMode;
    }


    /*
    * https://codeday.me/jp/qa/20190630/1142774.html (use google translate)
    * https://stackoverflow.com/questions/21166222/connecting-to-bluetooth-device-fails-in-deep-sleep
    * lint balks ar getDeclaredField. Ignore that
    *
    */
    private synchronized void closeSocketAndclearFileDescriptor() {
        if (bluetoothSocket == null) return;
        try {
            if (Build.VERSION.SDK_INT < 23) {
                Field field = BluetoothSocket.class.getDeclaredField("mPfd");
                field.setAccessible(true);
                ParcelFileDescriptor mPfd = (ParcelFileDescriptor) field.get(bluetoothSocket);
                if (null != mPfd) {
                    mPfd.close();
                }
            }
            bluetoothSocket.close();
        } catch (Exception e) {
            /* do nothing */
        }
    }

}
