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

import android.graphics.Point;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Menu;
import android.widget.TextView;

import lu.fisch.canze.R;

public class TemperatureActivity extends CanzeActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_termperature);

        TextView textView = (TextView) findViewById(R.id.text);

        String text = "";
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        text += "Size: " + size.x + "x" + size.y + "\n";
        try {
            display.getRealSize(size);
        } catch (NoSuchMethodError e) {
            size = new Point(display.getWidth(), display.getHeight());
        }
        text += "Real Size: " + size.x + "x" + size.y + "\n";
        text += "\n";
        text += "Density = "+getResources().getDisplayMetrics().density+"\n";
        text += "Scaled Density = "+getResources().getDisplayMetrics().scaledDensity+"\n";
        text += "\n";
        text += "widthPixels = "+getResources().getDisplayMetrics().widthPixels+"\n";
        text += "heightPixels = "+getResources().getDisplayMetrics().heightPixels+"\n";
        text += "xdpi = "+getResources().getDisplayMetrics().xdpi+"\n";
        text += "ydpi = "+getResources().getDisplayMetrics().ydpi+"\n";


        textView.setText(text);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_empty, menu);
        return true;
    }

}
