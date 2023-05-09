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

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import lu.fisch.canze.BuildConfig;
import lu.fisch.canze.activities.MainActivity;
import lu.fisch.canze.actors.Frame;
import lu.fisch.canze.actors.Message;

public class Http extends Device {

    private static final int TIMEOUT_DUMMMY = 0;
    
    private String urlLeader;

    public void join() throws InterruptedException {
        pollerThread.join();
    }

    private String sendAndWaitForAnswer(String command, int waitMillis, int timeout) {

        String result;

        try {
            result = httpGet(urlLeader + command);
            //MainActivity.debug("Http: jsonLineResult:" + jsonLine);
            if (result.compareTo("") == 0) {
                return "-E-result from httpGet empty";
            }

            if (result.compareTo("") == 0) {
                MainActivity.debug("Http: getMessageResult is empty");
                return "-E-result from json element R empty";
            }

            if (result.substring(0, 1).compareTo("-") == 0) {
                MainActivity.debug("Http: getMessageResult is an error or warning");
                return result;
            }

        } catch (Exception e) {
            MainActivity.debug("Http: Exception");
            return "-E-Exception";
        }
        return result;
    }

    private String httpGet(String urlString) {
        try {
            // MainActivity.debug("Http: httpGet url:" + urlString);
            URL url = new URL(urlString);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            try {
                urlConnection.setConnectTimeout(10000);
                // MainActivity.debug("Http: httpGet start connection and get result");
                InputStream ips = urlConnection.getInputStream();
                // MainActivity.debug("Http: httpGet ips opened");
                BufferedInputStream in = new BufferedInputStream(ips);
                // MainActivity.debug("Http: httpGet in opened");
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                String st;
                StringBuilder stringBuilder = new StringBuilder(200);
                while ((st = reader.readLine()) != null) {
                    // MainActivity.debug("Http: httpGet append " + st);
                    stringBuilder.append(st);
                }
                // MainActivity.debug("Http: httpGet return " + stringBuilder.toString());
                return stringBuilder.toString();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                urlConnection.disconnect();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
    
    @Override
    public void clearFields() {
        super.clearFields();
    }

    @Override
    public Message requestFreeFrame(Frame frame) {
        // build the command string to send to the remote device
        // CanSee maintains a table of all received free frames and immediately responds with
        // the last known frame
        String command = "g" + frame.getFromIdHex();
        return responseToMessage(frame, command, TIMEOUT_DUMMMY);
    }

    @Override
    public Message requestIsoTpFrame(Frame frame) {
        // build the command string to send to the remote device
        // Note that all ISOTP handling is done by the CanSee device
        String command = "i" + frame.getFromIdHex() + "," + frame.getRequestId() + "," + frame.getResponseId();
        return responseToMessage(frame, command, TIMEOUT_DUMMMY);
    }


    private Message responseToMessage(Frame frame, String command, int timeout) {
        // convert CanSee output to a Message object
        MainActivity.debug("Http.rtm.send [" + command + "]");
        String text = sendAndWaitForAnswer(command, 0, timeout);    // send and wait for an answer, no delay
        MainActivity.debug("Http.rtm.receive [" + text + "]");
        if (text.length() == 0) {
            return new Message(frame, "-E-Http.rtm.empty", true);
        }

        // split up the fields
        String[] pieces = text.trim().split(",");
        if (pieces.length < 2) {
            MainActivity.debug("Http.rtm.nocomma [" + text + "]");
            return new Message(frame, "-E-Http.rtm.nocomma:" + text, true);
        }

        int id;
        try {
            id = Integer.parseInt(pieces[0].trim(), 16);
        } catch (NumberFormatException e) {
            MainActivity.debug("Http.rtm.Nan [" + text + "]");
            return new Message(frame, "-E-Http.rtm.Nan:" + text, true);
        }

        if (id != frame.getFromId()) {
            MainActivity.debug("Http.rtm.diffid [" + text + "]");
            return new Message(frame, "-E-Http.rtm.diffid:" + text, true);
        }

        return new Message(frame, pieces[1].trim().toLowerCase(), false);
    }

    @Override
    public boolean initDevice(int toughness) {
        return initDevice(toughness, 1);
    }

    @Override
    protected boolean initDevice(int toughness, int retries) {
        urlLeader = MainActivity.getBluetoothDeviceAddress() + "?command=";
        if (BuildConfig.BRANCH.equals("master")) {
            sendAndWaitForAnswer("n110,0", 0, TIMEOUT_DUMMMY); // disable all serial when on master branch
            sendAndWaitForAnswer("n114,0", 0, TIMEOUT_DUMMMY); // disable all debugging when on master branch
        } else {
            sendAndWaitForAnswer("n110,1", 0, TIMEOUT_DUMMMY); // enable all serial
            sendAndWaitForAnswer("n114,f6", 0, TIMEOUT_DUMMMY); // enable all default debugging
        }
        lastInitProblem = "";
        return true;
    }
}
