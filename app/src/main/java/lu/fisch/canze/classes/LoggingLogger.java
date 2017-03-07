package lu.fisch.canze.classes;

import java.util.ArrayList;

import lu.fisch.canze.activities.MainActivity;
import lu.fisch.canze.actors.Field;
import lu.fisch.canze.interfaces.FieldListener;

/**
 * Created by robert.fisch on 07.03.17.
 */

public class LoggingLogger implements FieldListener {

    /* ****************************
     * Singleton stuff
     * ****************************/

    private static LoggingLogger loggingLogger = null;

    private LoggingLogger() {}

    public static LoggingLogger getInstance() {
        if(loggingLogger ==null) loggingLogger =new LoggingLogger();
        return loggingLogger;
    }

    /* ****************************
     * fields to register
     * ****************************/

    private ArrayList<Field> fields = new ArrayList<>();
    private ArrayList<Integer> intervals = new ArrayList<>();

    /**
     *
     * @param field
     * @param interval  in secondes!!
     * @return
     */
    public void add(Field field, int interval) {
        if(!fields.contains(field)) {
            MainActivity.device.addApplicationField(field, interval * 1000);
            intervals.add(interval);
            fields.add(field);
        }
    }

    public Field getField(int index) {
        return fields.get(index);
    }

    public Integer getItnerval(int index) {
        return intervals.get(index);
    }

    public int size() {
        return fields.size();
    }

    public void remove(int index) {
        intervals.remove(index);
        fields.remove(index);
    }

    public boolean remove(Field field) {
        intervals.add(fields.indexOf(field));
        return fields.remove(field);
    }

    /* ****************************
    * loggingLogger stuff
    * ****************************/
    public void onFieldUpdateEvent(final Field field) {

    }
}
