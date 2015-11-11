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

import lu.fisch.canze.activities.MainActivity;
import lu.fisch.canze.actors.Field;
import lu.fisch.canze.classes.TimePoint;
import lu.fisch.canze.interfaces.FieldListener;

public class CanzeDataSource implements FieldListener
{
    //private static long LIMIT = 24*60*60*1000;  // 24 h
    private static long LIMIT = 60*60*1000;  // 1 h

    /*
     * Singleton stuff
     */
    private static CanzeDataSource instance = null;

    public static CanzeDataSource getInstance()
    {
        if(instance==null) throw new Error("Must call at least once with given context!");
        return instance;
    }

    public static CanzeDataSource getInstance(Context context)
    {
        if(instance==null) instance = new CanzeDataSource(context);
        return instance;
    }

    private CanzeDataSource(Context context)
    {
      dbHelper = new CanzeOpenHelper(context);
    }

    // Database fields
    private SQLiteDatabase database;
    private CanzeOpenHelper dbHelper;

    public void open() throws SQLException
    {
      database = dbHelper.getWritableDatabase();
    }

    public void close()
    {
      dbHelper.close();
    }

    public void reinit()
    {
      dbHelper.reinit(database);
    }

    public void insert(Field field)
    {
        if(!Double.isNaN(field.getValue())) {
            //MainActivity.debug("CanzeDataSource: inserting "+field.getValue()+" for "+field.getSID());
            ContentValues values = new ContentValues();
            values.put("sid", field.getSID());
            values.put("moment", Calendar.getInstance().getTimeInMillis());
            values.put("value", field.getValue());
            database.insert("data", null, values);
        }
    }

    public void cleanUp()
    {
        long limit = Calendar.getInstance().getTimeInMillis()-LIMIT;
        database.rawQuery("DELETE FROM data WHERE moment<" + limit, null);
    }

    public void clear()
    {
        dbHelper.reinit(database);
        //database.rawQuery("DELETE FROM data",null);
    }

    public double getLast(String sid)
    {
        double data = Double.NaN;

        Cursor c = database.rawQuery("SELECT * FROM data WHERE sid='"+sid+"' ORDER BY moment DESC LIMIT 1", null);
        //MainActivity.debug("CanzeDataSource: getting last for "+sid);
        c.moveToFirst();
        if (!c.isAfterLast()) {
            data = c.getDouble(c.getColumnIndex("value"));
            //MainActivity.debug("CanzeDataSource: got value "+data);
        }
        // make sure to close the cursor
        c.close();

        return data;
    }

    public double getMax(String sid)
    {
        double data = Double.NaN;

        Cursor c = database.rawQuery("SELECT MAX(value) FROM data WHERE sid='"+sid+"' ORDER BY moment DESC LIMIT 1", null);
        //MainActivity.debug("CanzeDataSource: getting last for "+sid);
        c.moveToFirst();
        if (!c.isAfterLast()) {
            data = c.getDouble(0);
            //MainActivity.debug("CanzeDataSource: got value "+data);
        }
        // make sure to close the cursor
        c.close();

        return data;
    }

    public double getMin(String sid)
    {
        double data = Double.NaN;

        Cursor c = database.rawQuery("SELECT MIN(value) FROM data WHERE sid='"+sid+"' ORDER BY moment DESC LIMIT 1", null);
        //MainActivity.debug("CanzeDataSource: getting last for "+sid);
        c.moveToFirst();
        if (!c.isAfterLast()) {
            data = c.getDouble(0);
            //MainActivity.debug("CanzeDataSource: got value "+data);
        }
        // make sure to close the cursor
        c.close();

        return data;
    }

    public ArrayList<TimePoint> getData(String sid)
    {
        ArrayList<TimePoint> data = new ArrayList<>();

        Cursor c = database.rawQuery("SELECT * FROM data WHERE sid='"+sid+"' ORDER BY moment ASC", null);
        c.moveToFirst();
        while (!c.isAfterLast())
        {
            TimePoint b = new TimePoint(
                    c.getLong(c.getColumnIndex("moment")),
                    c.getDouble(c.getColumnIndex("value"))
            );
            data.add(b);
            c.moveToNext();
        }
        // make sure to close the cursor
        c.close();

        return data;
    }

    /*
     * Singleton stuff
     */
    @Override
    public void onFieldUpdateEvent(Field field) {
        insert(field);
    }
}
