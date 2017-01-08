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
import android.view.Menu;

import lu.fisch.canze.R;
import lu.fisch.canze.widgets.WidgetView;

public class WidgetActivity extends CanzeActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        widgetView=true;
        widgetClicked=true;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_widget);

        WidgetView wv = (WidgetView) findViewById(R.id.selectedWidget);
        wv.setClickable(false);
        if(WidgetView.selectedDrawable!=null) {
            wv.setDrawable(WidgetView.selectedDrawable);
            if(WidgetView.selectedDrawable.getField()!=null)
                wv.setFieldSID(WidgetView.selectedDrawable.getField().getSID());
            setTitle(WidgetView.selectedDrawable.getTitle());
        }

    }

    protected void initListeners () {}

    @Override
    protected void onDestroy() {
        WidgetView wv = (WidgetView) findViewById(R.id.selectedWidget);
        wv.reset();

        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_empty, menu);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        widgetClicked=true;
    }



}
