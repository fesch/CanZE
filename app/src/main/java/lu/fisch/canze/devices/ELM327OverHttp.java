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
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;

import lu.fisch.canze.activities.MainActivity;
import lu.fisch.canze.actors.Ecu;
import lu.fisch.canze.actors.Ecus;
import lu.fisch.canze.actors.Field;
import lu.fisch.canze.actors.Frame;
import lu.fisch.canze.actors.Message;
import lu.fisch.canze.bluetooth.BluetoothManager;
import lu.fisch.canze.fragments.MainFragment;

public class ELM327OverHttp extends Device {

    private int timeoutLogLevel = MainActivity.toastLevel;
    // private String urlLeader = "http://wemos-1.notice.lan/"; // need to be picked up from settings
    private String urlLeader;
    private boolean someThingWrong = false;


    @Override
    public void registerFilter(int frameId) {
        // not needed for this device
    }

    @Override
    public void unregisterFilter(int frameId) {
        // not needed for this device
    }


    protected boolean initDevice (int toughness, int retries) {
        if (initDevice(toughness)) return true;
        while (retries-- > 0) {
            MainActivity.debug("ELM327Http: initDevice ("+toughness+"), "+retries+" retries left");
            if (initDevice(toughness)) return true;
        }
        if (timeoutLogLevel >= 1) MainActivity.toast("Hard reset failed, restarting device ...");
        MainActivity.debug(lastInitProblem);
        MainActivity.debug("ELM327Http: Hard reset failed, restarting device ...");
        return false;
    }


    public boolean initDevice(int toughness) {
        MainActivity.debug("ELM327Http: initDevice");
        // hard address used, the phone uses google DNS instead of the local WiFi DNS :-(
        //urlLeader = MainActivity.getBluetoothDeviceAddress().compareTo ("Emulator") == 0 ? "http://solarmax.trekvalk.nl:8123/wemos/" : "http://172.19.3.216/";
        // urlLeader = MainActivity.getBluetoothDeviceAddress().compareTo ("Emulator") == 0 ? "http://solarmax.trekvalk.nl:8123/wemos/" : "http://192.168.200.135/";

        /*if(MainActivity.getBluetoothDeviceAddress().equals("HTTP Emulator"))
            urlLeader = "http://solarmax.trekvalk.nl:8123/wemos/";
        else if(MainActivity.getBluetoothDeviceAddress().equals("Gateway-B"))
            urlLeader = "http://192.168.200.135/";
        else if(MainActivity.getBluetoothDeviceAddress().equals("Gateway-J"))
            urlLeader = "http://172.19.3.216/";
            */

        urlLeader = MainActivity.getBluetoothDeviceAddress();

        /*
        switch (MainActivity.getBluetoothDeviceAddress()) {
            case "Emulator":
                urlLeader = "http://solarmax.trekvalk.nl:8123/wemos/";
            case "Gateway-B":
                urlLeader = "http://192.168.200.135/";
            case "Gateway-J":
                urlLeader = "http://172.19.3.216/";
        }
        */

        MainActivity.debug("Using ADR = "+MainActivity.getBluetoothDeviceAddress());
        MainActivity.debug("Using URL = "+urlLeader);

        lastInitProblem = "";
        someThingWrong = false;
        String msg = getMessage ("Init");
        if (msg.compareTo ("OK") == 0) return true;
        lastInitProblem = msg;
        someThingWrong = true;
        return false;
    }


    @Override
    public void clearFields() {
        super.clearFields();
    }

    @Override
    public Message requestFreeFrame(Frame frame) {
        MainActivity.debug("ELM327Http: request Free frame");

        if (someThingWrong) {return new Message(frame, "-E-Re-initialisation needed", true); }

        String msg = getMessage ("Free?f=" + frame.getHexId());
        MainActivity.debug("ELM327Http: request Free frame result " + msg);

        return new Message (frame, msg, msg.substring(0,1).compareTo("-") == 0);
    }

    @Override
    public Message requestIsoTpFrame(Frame frame) {
        MainActivity.debug("ELM327Http: request IsoTp frame");

        if (someThingWrong) {return new Message(frame, "-E-Re-initialisation needed", true); }

        String msg = getMessage ("IsoTp?f=" + frame.getSendingEcu().getHexFromId() + "." + frame.getSendingEcu().getHexToId() + "." + frame.getResponseId());
        MainActivity.debug("ELM327Http: request IsoTp frame result " + msg);

        return new Message (frame, msg, msg.substring(0,1).compareTo("-") == 0);
    }

    private String getMessage (String command) {

        String result;

        try {
            String jsonLine = httpGet (urlLeader + command);
            MainActivity.debug("ELM327Http: jsonLineResult:" + jsonLine);
            if (jsonLine.compareTo("") == 0) {
                someThingWrong |= true;
                return "-E-result from httpGet empty";
            }

            JsonElement jelement = new JsonParser().parse(jsonLine);
            result = jelement.getAsJsonObject().get("R").getAsString();

            MainActivity.debug("ELM327Http: getMessageResult:[" + result + "]");
            if (result.compareTo("") == 0) {
                MainActivity.debug("ELM327Http: getMessageResult is empty");
                someThingWrong |= true;
                return "-E-result from json element R empty";
            }

            if (result.substring(0,1).compareTo("-") == 0) {
                MainActivity.debug("ELM327Http: getMessageResult is an error or warning");
                someThingWrong |= true;
                return result;
            }

        } catch (Exception e) {
            MainActivity.debug("ELM327Http: Exception");
            someThingWrong |= true;
            return "-E-Exception";
        }
        return result;
    }

    private String httpGet (String urlString) {
        try {
            URL url = new URL(urlString);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            try {
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                String st;
                StringBuilder stringBuilder = new StringBuilder(200);
                while ((st = reader.readLine()) != null) {
                    stringBuilder.append(st);
                }
                return stringBuilder.toString();
            } catch(Exception e) {
                    e.printStackTrace();
            } finally {
                urlConnection.disconnect();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
}
