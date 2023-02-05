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

package lu.fisch.canze.activities;

import android.os.Bundle;

import lu.fisch.canze.R;
import lu.fisch.canze.classes.Sid;
import lu.fisch.canze.devices.Device;
import lu.fisch.canze.interfaces.DebugListener;

public class LeakCurrentsActivity extends CanzeActivity implements DebugListener {

    // Bcbtesterinit and -awake are hardcoded here, but shoud of course be optionally taken from
    // the ECU getSessionRequired and getStartDiag. However, since the BCB fields are aliassed in
    // Ph2 to the 11 bits address, we leave it like this for now
    
    /* Todo
         Add unaliased settings for BCB tester fields in _FieldsPh2, and make tester logic here
         use the formal ECU settings
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leak_currents);
    }

    protected void initListeners() {
        MainActivity.getInstance().setDebugListener(this);
            addField(Sid.BcbTesterInit, Device.INTERVAL_ONCE);
            addField(Sid.BcbTesterAwake, 1500);
    }

}
