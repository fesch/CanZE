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

/**
 * Created by robertfisch on 07.09.2015.
 * Main loop fir ELM
 */
public class ELM327OverHttp extends Device {

    private int timeoutLogLevel = MainActivity.toastLevel;
    // private String urlLeader = "http://wemos-1.notice.lan/"; // need to be picked up from settings
    private String urlLeader = "http://solarmax.trekvalk.nl:8123/wemos/";

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
        if (timeoutLogLevel >= 1) MainActivity.toast("Hard reset failed, restarting Bluetooth ...");
        MainActivity.debug("ELM327: Hard reset failed, restarting Bluetooth ...");
        return false;
    }


    public boolean initDevice(int toughness) {
        MainActivity.debug("ELM327Http: initDevice");
        lastInitProblem = "";
        someThingWrong = false;
        String msg = getMessage ("Init");
        if (msg == null) return false;
        if (msg.compareTo ("OK") == 0) {
            return true;
        }
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
        String msg = getMessage ("Free?f=" + frame.getHexId());
        if (msg == null) return null;
        return new Message (frame, msg);
    }

    @Override
    public Message requestIsoTpFrame(Frame frame) {
        MainActivity.debug("ELM327Http: request IsoTp frame");
        String msg = getMessage ("IsoTp?f=" + frame.getSendingEcu().getHexFromId() + "." + frame.getSendingEcu().getHexToId() + "." + frame.getResponseId());
        if (msg == null) return null;
        MainActivity.debug("ELM327Http: request IsoTp frame result " + msg);
        return new Message (frame, msg);
    }

    private String getMessage (String command) {

        if (someThingWrong) { return null ; }

        String result;

        try {
            String jsonLine = httpGet (urlLeader + command);
            MainActivity.debug("ELM327Http: jsonLineResult:" + jsonLine);
            JsonElement jelement = new JsonParser().parse(jsonLine);
            result = jelement.getAsJsonObject().get("R").getAsString();


            MainActivity.debug("ELM327Http: getMessageResult:[" + result + "]");
            if (result.isEmpty()) {
                someThingWrong |= true;
                return null;
            }

        } catch (Exception e) {
            someThingWrong |= true;
            return null;
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
                StringBuffer stringBuffer = new StringBuffer();
                while((st=reader.readLine())!=null) {
                    stringBuffer.append(st);
                }
                return stringBuffer.toString();
            } finally {
                urlConnection.disconnect();
            }
        } catch (Exception e) {

        }
        return "";
    }
}
