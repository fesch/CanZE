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

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import lu.fisch.canze.activities.MainActivity;

public class CanzeOpenHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "lu.fisch.canze.db";

    CanzeOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            // create the database
            db.execSQL("CREATE TABLE data (sid TEXT NOT NULL, moment INTEGER NOT NULL, value REAL NOT NULL)");
            // set an index on the "sid" field
            db.execSQL("CREATE INDEX indexSid ON data (sid)");
            //db.execSQL("CREATE TABLE data (id_data INTEGER PRIMARY KEY AUTOINCREMENT, sid TEXT NOT NULL, moment INTEGER NOT NULL, value REAL NOT NULL)");
        } catch (Exception e) {
            MainActivity.logExceptionToCrashlytics(e);
            // do nothing
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onCreate(db);
    }

    public void clear(SQLiteDatabase db) {
        try {
            db.execSQL("DROP TABLE IF EXISTS data");
        } catch (Exception e) {
            MainActivity.logExceptionToCrashlytics(e);
            // do nothing
        }
    }

    public void reinit(SQLiteDatabase db) {
        clear(db);
        onCreate(db);
    }
}
