package lu.fisch.canze.adapters;

import lu.fisch.canze.actors.Field;

/**
 * Created by robert.fisch on 16.03.17.
 */

public class LoggerField {
    public final Field field;
    public final int interval;

    public LoggerField(Field field, int interval) {
        this.field = field;
        this.interval = interval;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LoggerField)) return false;

        LoggerField that = (LoggerField) o;

        //if (interval != that.interval) return false;
        return field != null ? field.equals(that.field) : that.field == null;

    }

    @Override
    public int hashCode() {
        return field != null ? field.hashCode() : 0;
    }
}
