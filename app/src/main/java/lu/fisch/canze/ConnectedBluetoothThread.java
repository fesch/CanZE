package lu.fisch.canze;

import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by robertfisch on 14.08.2015.
 */
public class ConnectedBluetoothThread extends Thread {

    private final InputStream mmInStream;
    private final OutputStream mmOutStream;
    private BluetoothSocket socket;
    private Handler h;

    private volatile boolean stopped = false;

    public ConnectedBluetoothThread(BluetoothSocket socket, Handler h) {
        this.h=h;
        this.socket=socket;

        InputStream tmpIn = null;
        OutputStream tmpOut = null;

        // Get the input and output streams, using temp objects because
        // member streams are final
        try {
            tmpIn = socket.getInputStream();
            tmpOut = socket.getOutputStream();
        } catch (IOException e) { }

        mmInStream = tmpIn;
        mmOutStream = tmpOut;
    }

    public void cleanStop()
    {
        stopped=true;
    }

    public void run() {
        byte[] buffer = new byte[256];  // buffer store for the stream
        int bytes; // bytes returned from read()

        // Keep listening to the InputStream until an exception occurs
        while (true && !stopped) {
            try {
                // Read from the InputStream
                bytes = mmInStream.read(buffer);                // Get number of bytes and message in "buffer"
                h.obtainMessage(MainActivity.RECIEVE_MESSAGE,   // Send to message queue Handler
                        bytes, -1, buffer).sendToTarget();
            }
            catch (IOException e)
            {
                break;
            }
        }
    }

    /* Call this from the main activity to send data to the remote device */
    public void write(String message) {
        if(socket.isConnected()) {
            byte[] msgBuffer = message.getBytes();
            try {
                mmOutStream.write(msgBuffer);
            } catch (IOException e) {
                Log.d(MainActivity.TAG, "BT: Error sending > " + e.getMessage());
            }
        }
    }

    public Handler getHandler() {
        return h;
    }

    public int read(byte[] buffer) throws IOException {
        if(socket.isConnected())
            return mmInStream.read(buffer);
        else
            return 0;
    }

    public int available() throws IOException {
        if(socket.isConnected())
            return mmInStream.available();
        else
            return 0;
    }
}