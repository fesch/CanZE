package lu.fisch.canze.devices;

import java.util.ArrayList;

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

    @Override
    protected ArrayList<Message> processData(int[] input) {
        return null;
    }
}
