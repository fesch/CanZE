package lu.fisch.canze.classes;

import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import lu.fisch.canze.activities.MainActivity;
import lu.fisch.canze.actors.Field;
import lu.fisch.canze.actors.Fields;
import lu.fisch.canze.adapters.LoggerField;
import lu.fisch.canze.interfaces.FieldListener;

/**
 * Created by robert.fisch on 07.03.17.
 */

public class LoggingLogger implements FieldListener {

    /* ****************************
     * Singleton stuff
     * ****************************/

    private static LoggingLogger loggingLogger = null;

    private LoggingLogger() {
        load();
    }

    public static LoggingLogger getInstance() {
        if(loggingLogger ==null) loggingLogger =new LoggingLogger();
        return loggingLogger;
    }

    /* ****************************
     * fields to register
     * ****************************/

    private final ArrayList<LoggerField> loggerFields = new ArrayList<>();

    public  ArrayList<LoggerField> getLoggingFields()
    {
        return loggerFields;
    }

    /**
     *
     * @param field     field to add to the logger
     * @param interval  in secondes!!
     */
    public void add(Field field, int interval) {
        LoggerField loggerField = new LoggerField(field,interval);
        if(!loggerFields.contains(loggerField)) {
            MainActivity.device.addApplicationField(field, interval * 1000);
            loggerFields.add(loggerField);
            save();
        }
    }

    public Field getField(int index) {
        return loggerFields.get(index).field;
    }

    public Integer getIntnerval(int index) {
        return loggerFields.get(index).interval;
    }

    public int size() {
        return loggerFields.size();
    }

    public void remove(int index) {
        loggerFields.remove(index);
        save();
    }

    public void remove(Field field) {
        loggerFields.remove(field);
        save();
    }

    public void clear() {
        loggerFields.clear();
        save();
    }

    /* ****************************
     * save / load a set of SIDs
     * ****************************/

    public void save()
    {
        SharedPreferences settings = MainActivity.getInstance().getSharedPreferences(MainActivity.PREFERENCES_FILE, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putStringSet("loggingFields",getFields());
        editor.putStringSet("loggingIntervals",getIntervals());
        editor.apply();
    }

    public void load()
    {
        SharedPreferences settings = MainActivity.getInstance().getSharedPreferences(MainActivity.PREFERENCES_FILE, 0);
        Set<String> loggingFields = settings.getStringSet("loggingFields",new HashSet<String>());
        Set<String> loggingIntervals = settings.getStringSet("loggingIntervals",new HashSet<String>());
        setFieldsIntervals(loggingFields,loggingIntervals);
    }

    public HashSet<String> getFields()
    {
        HashSet<String> result = new HashSet<>();
        for(int i=0; i<size(); i++)
        {
            result.add(loggerFields.get(i).field.getSID());
            MainActivity.debug("LoggerSize Save Field "+i+" = "+loggerFields.get(i).field.getSID());
        }
            return result;
    }

    public HashSet<String> getIntervals()
    {
        HashSet<String> result = new HashSet<>();
        for(int i=0; i<size(); i++)
        {
            result.add(loggerFields.get(i).interval+"");
            MainActivity.debug("LoggerSize Save Interval "+i+" = "+loggerFields.get(i).interval);
        }
        return result;
    }

    public void setFieldsIntervals(Set<String> fields, Set<String> intervalls)
    {
        clear();

        MainActivity.debug("LoggerSize Load = "+fields.size());

        String[] fieldArray = fields.toArray(new String[fields.size()]);
        String[] intervalArray = intervalls.toArray(new String[intervalls.size()]);

        if(fieldArray.length==intervalArray.length) {
            for (int i = 0; i < fieldArray.length; i++) {
                add(Fields.getInstance().getBySID(fieldArray[i]), Integer.valueOf(intervalArray[i]));
            }
        }
        else MainActivity.debug("LoggingLogger: list of SID's ("+fieldArray.length+") has different size than list of intervals ("+intervalArray.length+").");
    }

    /* ****************************
     * loggingLogger stuff
     * ****************************/
    public void onFieldUpdateEvent(final Field field) {

    }
}
