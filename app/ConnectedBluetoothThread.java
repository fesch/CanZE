package lu.fisch.canze.bluetooth;

import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import lu.fisch.canze.activities.MainActivity;

/**
 * Created by robertfisch on 14.08.2015.
 */
public class ConnectedBluetoothThread extends Thread {

    public static final int BUFFER_SIZE = 1024;

    private final InputStream inputStream;
    private final OutputStream outputStream;
    private BluetoothSocket socket;

    //private Stack stack = null;

    private volatile boolean stopped = false;

    public ConnectedBluetoothThread(BluetoothSocket socket) {
        // store properties
        this.socket=socket;
        // reset streams
        InputStream tmpIn = null;
        OutputStream tmpOut = null;
        // Get the input and output streams, using temp objects because
        // member streams are final
        try {
            tmpIn = socket.getInputStream();
            tmpOut = socket.getOutputStream();
        }
        catch (IOException e) {
            // ignore
        }
        // assign streams
        inputStream = tmpIn;
        outputStream = tmpOut;
    }

    /*public ConnectedBluetoothThread(BluetoothSocket socket, Stack stack) {
        // store properties
        this.socket=socket;
        this.stack=stack;
        // reset streams
        InputStream tmpIn = null;
        OutputStream tmpOut = null;
        // Get the input and output streams, using temp objects because
        // member streams are final
        try {
            tmpIn = socket.getInputStream();
            tmpOut = socket.getOutputStream();
        }
        catch (IOException e) {
            // ignore
        }
        // assign streams
        inputStream = tmpIn;
        outputStream = tmpOut;
    }*/

    public void cleanStop()
    {
        stopped=true;
    }

    // this is no longer used, but it, we have to re-active it.
    // atually this "thread" gets never started
    public void run() {
    /*
        // buffer store for the stream
        byte[] buffer = new byte[BUFFER_SIZE];
        // bytes returned from read()
        int bytes; //

        // keep listening to the InputStream until an exception occurs
        // or the thread is being stopped
        while (true && !stopped) {
            try {
                // read from the InputStream
                // get number of bytes in "buffer"
                bytes = inputStream.read(buffer);
                // if we got something
                if(bytes!=-1) {
                    // convert byte[] to int[]
                    final int[] intArray = new int[bytes];
                    for(int i=0; i<bytes; i++)
                        intArray[i]=(buffer[i]<0?256+buffer[i]:buffer[i]);
                    // pass the data to the stack using a new thread
                    Thread t = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            synchronized (stack)
                            {
                                try {
                                    if(stack!=null)
                                        stack.process(intArray);
                                }
                                catch (Exception e)
                                {
                                    e.printStackTrace();
                                }
                            }
                        }
                    });
                    t.start();

                }
            }
            catch (Exception e)
            {
                break;
            }
        }
        */
    }

    // write a message to the output stream
    public void write(String message) {
        if(socket.isConnected()) {
            byte[] msgBuffer = message.getBytes();
            try {
                outputStream.write(msgBuffer);
            } catch (IOException e) {
                Log.d(MainActivity.TAG, "BT: Error sending > " + e.getMessage());
                /*Log.d(MainActivity.TAG, "BT: Error sending > restaring BT");

                (new Thread(new Runnable() {
                    @Override
                    public void run() {
                        // stop the BT but don't reset the device registered fields
                        MainActivity.getInstance().stopBluetooth(false);
                        // reload the BT with filter registration
                        MainActivity.getInstance().reloadBluetooth(false);
                    }
                })).start();
                /**/
            }
        }
        else MainActivity.debug("Write failed! Socket is closed ... M = "+message);
    }

    public int read(byte[] buffer) throws IOException {
        if(socket.isConnected())
            return inputStream.read(buffer);
        else
            return 0;
    }

    public int read() throws IOException {
        if(socket.isConnected())
            return inputStream.read();
        else
            return -1;
    }

    public int available() throws IOException {
        if(socket.isConnected())
            return inputStream.available();
        else
            return 0;
    }

    /*
    public void setStack(Stack stack) {
        this.stack = stack;
    }
    */
}