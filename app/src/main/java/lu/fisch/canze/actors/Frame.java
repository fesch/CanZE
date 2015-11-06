package lu.fisch.canze.actors;

/**
 * Frame
 */
public class Frame {

    private int id;
    private int interval; // in ms
    private Ecu sendingEcu;

    public Frame (int id, int interval, Ecu sendingEcu) {
        this.id = id;
        this.interval = interval;
        this.sendingEcu = sendingEcu;
    }

    public int getId() {
        return id;
    }

    public int getInterval() {
        return interval;
    }

    public Ecu getSendingEcu() {
        return sendingEcu;
    }

}
