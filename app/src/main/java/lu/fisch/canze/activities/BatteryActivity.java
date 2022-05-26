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
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.Menu;
import android.widget.TextView;

import java.util.Locale;

import lu.fisch.canze.R;
import lu.fisch.canze.classes.Sid;
import lu.fisch.canze.actors.Field;
import lu.fisch.canze.interfaces.DebugListener;

public class BatteryActivity extends CanzeActivity implements DebugListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(MainActivity.isSpring() ? R.layout.activity_battery72 : R.layout.activity_battery);

        TextView textView = findViewById(R.id.link);
        textView.setText(Html.fromHtml(MainActivity.getStringSingle(R.string.help_QA)));
        textView.setMovementMethod(LinkMovementMethod.getInstance());
    }

    protected void initListeners () {
        MainActivity.getInstance().setDebugListener(this);
        addField(Sid.BatterySerial, 60000);
    }

    /********************************/

    @Override
    public void onFieldUpdateEvent(final Field field) {
        // the update has to be done in a separate thread
        // otherwise the UI will not be repainted
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                TextView tv;

                switch (field.getSID()) {
                    // positive torque
                    case Sid.BatterySerial:
                        tv = findViewById(R.id.textBatterySerial);
                        if (tv != null) tv.setText(String.format(Locale.getDefault(), "Serial: %X", (long)field.getValue()).replace (" 26", "F"));
                        //tv = null;

                        //This fields holds VIN for Dacia Spring
                        if (MainActivity.isSpring()) {
                        if (tv != null) tv.setText(field.getStringValue());
                        }
                }
                // set regular new content, all exceptions handled above
/*              if (tv != null) {
                    double val = field.getValue();
                    tv.setText(Double.isNaN(val) ? "" : String.format(Locale.getDefault(), "%.1f", val));
                } */
            }
        });
    }

}
