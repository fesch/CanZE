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

        import lu.fisch.canze.R;

public class BatteryActivity extends CanzeActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_battery);

        TextView textView = (TextView) findViewById(R.id.link);
        textView.setText(Html.fromHtml("More detailed information about these data can be found in the <a href='http://canze.fisch.lu/qa/'>Q & A section</a> of the <a href='http://canze.fisch.lu/'>CanZE homepage</a>."));
        textView.setMovementMethod(LinkMovementMethod.getInstance());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_empty, menu);
        return true;
    }
}
