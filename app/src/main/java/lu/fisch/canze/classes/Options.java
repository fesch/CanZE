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

package lu.fisch.canze.classes;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.HashMap;

/**
 * Created by robertfisch on 07.11.2015.
 */
public class Options {

    private HashMap<String,String> intervals = new HashMap<>();

    public Options() {}

    public Options(String json) {
        Gson gson = new Gson();
        Type tt = new TypeToken<HashMap<String,String>>() {}.getType();
        intervals = gson.fromJson(json,tt);
    }

    public String getOption(String sid) {
        if(intervals.containsKey(sid))
            return intervals.get(sid);
        else
            return null;
    }

    public String getJson() {

        return (new Gson()).toJson(intervals.clone());
    }

    public void add(String sid, String option)
    {
        intervals.put(sid,option);
    }
}
