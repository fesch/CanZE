package lu.fisch.canze.actors;

/**
 * Frame
 */
public class Frame {

    private int Id;
    private int interval; // in ms
    private Ecu sendingEcu;

    public Frame (int Id, int interval, Ecu sendingEcu) {
        this.Id = Id;
        this.interval = interval;
        this.sendingEcu = sendingEcu;
    }

    public int getId() {
        return Id;
    }

    public int getInterval() {
        return interval;
    }

    public Ecu getSendingEcu() {
        return sendingEcu;
    }

}
