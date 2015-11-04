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
import android.database.DatabaseUtils;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;

import lu.fisch.canze.actors.Field;
import lu.fisch.canze.classes.TimePoint;

public class CanzeDataSource
{
    // Database fields
    private SQLiteDatabase database;
    private CanzeOpenHelper dbHelper;

    public CanzeDataSource(Context context)
    {
      dbHelper = new CanzeOpenHelper(context);
    }

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
        ContentValues values = new ContentValues();
        values.put("sid", field.getSID());
        values.put("moment", Calendar.getInstance().getTimeInMillis());
        values.put("value",field.getValue());
        database.insert("data", null, values);
    }

    public void clean(Field field, long limit)
    {
        database.rawQuery("DELETE FROM data WHERE sid = '" + field.getSID() + "' AND moment<" + limit, null);
    }

    public ArrayList<TimePoint> getData(Field field)
    {
        ArrayList<TimePoint> data = new ArrayList<>();

        Cursor c = database.rawQuery("SELECT * FROM data WHERE sid='"+field.getSID()+"' ORDER BY moment DESC", null);
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
}
