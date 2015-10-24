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
            wv.setFieldSID(WidgetView.selectedDrawable.getField().getSID());
            setTitle(WidgetView.selectedDrawable.getTitle());
        }

    }

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