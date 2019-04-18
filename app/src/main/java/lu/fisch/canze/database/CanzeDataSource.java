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

package lu.fisch.canze.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import lu.fisch.canze.activities.MainActivity;
import lu.fisch.canze.actors.Field;
import lu.fisch.canze.classes.TimePoint;
import lu.fisch.canze.interfaces.FieldListener;

public class CanzeDataSource implements FieldListener {

    private static long LIMIT = 60 * 60 * 1000;  // 1 h

    /*
     * Singleton stuff
     */
    private static CanzeDataSource instance = null;

    public static CanzeDataSource getInstance() {
        // let it crash on the caller to see where it comes from
        // if (instance == null) throw new Error("Must call at least once with given context!");
        return instance;
    }

    public static CanzeDataSource getInstance(Context context) {
        if (instance == null) instance = new CanzeDataSource(context);
        return instance;
    }

    private CanzeDataSource(Context context) {
        dbHelper = new CanzeOpenHelper(context);
    }

    // Database fields
    private SQLiteDatabase database;
    private CanzeOpenHelper dbHelper;

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public void reinit() {
        dbHelper.reinit(database);
    }

    private HashMap<String, TimePoint> lasts = new HashMap<>();

    public void insert(Field field) {
        if (!Double.isNaN(field.getValue())) {
            //MainActivity.debug("CanzeDataSource: inserting "+field.getValue()+" for "+field.getSID());
            ContentValues values = new ContentValues();
            values.put("sid", field.getSID());
            //values.put("moment", Calendar.getInstance().getTimeInMillis());
            long iTime = Calendar.getInstance().getTimeInMillis();
            // with the new dongle, fields may come in too fast, so let's
            // make sure we do not get an overflow >> very slow app reaction
            // maximum each second a new value!
            iTime = (iTime / 1000) * 1000;
            values.put("moment", iTime);
            values.put("value", field.getValue());

            // if this is really a new point, insert it
            //if(getLastTime(field.getSID())!=iTime)
            TimePoint lastTP = lasts.get(field.getSID());
            try {
                if (lastTP == null || lastTP.date != iTime)
                    database.insert("data", null, values);
                    // but if not, insert the max ... so check if the inserted value is lower than the new one
                    //else if(getLast(field.getSID())<field.getValue())
                else if (lastTP.value < field.getValue()) {
                    // delete the value from the DB
                    delete(field.getSID(), iTime);
                    // insert a new value into the DB
                    database.insert("data", null, values);
                }
            } catch (Exception e) {
                // do nothing
            }
            lasts.put(field.getSID(), new TimePoint(iTime, field.getValue()));
        }
    }

    public void cleanUp() {
        try {
            long limit = Calendar.getInstance().getTimeInMillis() - LIMIT;
            Cursor c = database.rawQuery("DELETE FROM data WHERE moment<" + limit, null);
            c.close();
        } catch (Exception e) {
            // do nothing
        }
    }

    public void clear() {
        try {
            dbHelper.reinit(database);
            //database.rawQuery("DELETE FROM data",null);
        } catch (Exception e) {
            // do nothing
        }
    }

    public void delete(String sid, long moment) {
        try {
            Cursor c = database.rawQuery("DELETE FROM data WHERE sid='" + sid + "' AND moment='" + moment + "'", null);
            c.close();
        } catch (Exception e) {
            // do nothing
        }
    }

    public double getLast(String sid) {
        double data = Double.NaN;

        try {
            Cursor c = database.rawQuery("SELECT * FROM data WHERE sid='" + sid + "' ORDER BY moment DESC LIMIT 1", null);
            //MainActivity.debug("CanzeDataSource: getting last for "+sid);
            c.moveToFirst();
            if (!c.isAfterLast()) {
                data = c.getDouble(c.getColumnIndex("value"));
                //MainActivity.debug("CanzeDataSource: got value "+data);
            }
            // make sure to close the cursor
            c.close();
        } catch (Exception e) {
            // do nothing
        }

        return data;
    }

    public long getLastTime(String sid) {
        long data = -1;

        try {
            Cursor c = database.rawQuery("SELECT * FROM data WHERE sid='" + sid + "' ORDER BY moment DESC LIMIT 1", null);
            //MainActivity.debug("CanzeDataSource: getting last for "+sid);
            c.moveToFirst();
            if (!c.isAfterLast()) {
                data = c.getLong(c.getColumnIndex("moment"));
                //MainActivity.debug("CanzeDataSource: got value "+data);
            }
            // make sure to close the cursor
            c.close();
        } catch (Exception e) {
            // do nothing
        }

        return data;
    }

    public double getMax(String sid) {
        double data = Double.NaN;

        try {
            Cursor c = database.rawQuery("SELECT MAX(value) FROM data WHERE sid='" + sid + "' ORDER BY moment DESC LIMIT 1", null);
            //MainActivity.debug("CanzeDataSource: getting last for "+sid);
            c.moveToFirst();
            if (!c.isAfterLast()) {
                data = c.getDouble(0);
                //MainActivity.debug("CanzeDataSource: got value "+data);
            }
            // make sure to close the cursor
            c.close();
        } catch (Exception e) {
            // do nothing
        }

        return data;
    }

    public double getMin(String sid) {
        double data = Double.NaN;

        try {
            Cursor c = database.rawQuery("SELECT MIN(value) FROM data WHERE sid='" + sid + "' ORDER BY moment DESC LIMIT 1", null);
            //MainActivity.debug("CanzeDataSource: getting last for "+sid);
            c.moveToFirst();
            if (!c.isAfterLast()) {
                data = c.getDouble(0);
                //MainActivity.debug("CanzeDataSource: got value "+data);
            }
            // make sure to close the cursor
            c.close();
        } catch (Exception e) {
            // do nothing
        }


        return data;
    }

    public ArrayList<TimePoint> getData(String sid) {
        ArrayList<TimePoint> data = new ArrayList<>();

        try {
            Cursor c = database.rawQuery("SELECT * FROM data WHERE sid='" + sid + "' ORDER BY moment ASC", null);
            c.moveToFirst();
            while (!c.isAfterLast()) {
                TimePoint b = new TimePoint(
                        c.getLong(c.getColumnIndex("moment")),
                        c.getDouble(c.getColumnIndex("value"))
                );
                data.add(b);
                c.moveToNext();
            }
            // make sure to close the cursor
            c.close();
        } catch (Exception e) {
            // do nothing
        }

        return data;
    }

    /*
     * FieldListener implementation
     */
    @Override
    public void onFieldUpdateEvent(Field field) {
        insert(field);
    }
}
