package lu.fisch.canze.activities;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import lu.fisch.canze.R;
import lu.fisch.canze.classes.ActivityRegistry;
import lu.fisch.canze.fragments.CustomFragment;

public class SettingsCustomActivity extends AppCompatActivity {

    private ListView all;
    private ListView selected;
    private int allPos = -1;
    private int selPos = -1;
    private ActivityRegistry registry =  ActivityRegistry.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
            actionBar.setDisplayHomeAsUpEnabled(true);

        setContentView(R.layout.activity_settings_custom);

        all = (ListView) findViewById(R.id.lstAll);
        all.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        ArrayAdapter adapter = new ArrayAdapter<String>(getApplicationContext(),
                R.layout.spinner_item,
                registry.getActivities());
        all.setAdapter(adapter);
        all.setClickable(true);
        all.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                allPos = position;
            }
        });

        loadSelected();

        selected = (ListView) findViewById(R.id.lstSelected);
        selected.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        selected.setClickable(true);
        selected.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                selPos = position;
            }
        });

        // add Button
        findViewById(R.id.btnAdd).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(allPos!=-1) {
                    registry.addToSelected(allPos);
                    loadSelected();
                }
            }
        });

        // remove Button
        findViewById(R.id.btnRemove).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(selPos!=-1) {
                    registry.removeFromSelected(selPos);
                    loadSelected();
                }
            }
        });

        // up Button
        findViewById(R.id.btnUp).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(selPos!=-1) {
                    if(registry.moveSelectedUp(selPos)) {
                        loadSelected();
                        selPos--;
                        selected.requestFocusFromTouch();
                        selected.setItemChecked(selPos,true);
                        selected.setSelection(selPos);
                    }
                }
            }
        });

        // down Button
        findViewById(R.id.btnDown).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(selPos!=-1) {
                    if(registry.moveSelectedDown(selPos)) {
                        loadSelected();
                        selPos++;
                        selected.requestFocusFromTouch();
                        selected.setItemChecked(selPos,true);
                        selected.setSelection(selPos);
                    }
                }
            }
        });
    }

    private void loadSelected()
    {
        selected = (ListView) findViewById(R.id.lstSelected);
        ArrayAdapter adapter = new ArrayAdapter<String>(getApplicationContext(),
                R.layout.spinner_item,
                registry.getSelected());
        selected.setAdapter(adapter);
    }

    private void saveSettings()
    {
        // removed the MainActivity.getInstance(), as we got a nullException, 20210104
        SharedPreferences settings = getSharedPreferences(MainActivity.PREFERENCES_FILE, 0);
        SharedPreferences.Editor editor = settings.edit();
        for(int i = 0; i< CustomFragment.BUTTONCOUNT; i++)
        {
            if(i<registry.selectedSize())
                editor.putInt("buttonC"+i,registry.getPos(registry.selectedGet(i)));
            else
                editor.putInt("buttonC"+i,-1);
        }
        editor.apply();
    }

    @Override
    public void onBackPressed() {
        // MainActivity.toast(MainActivity.TOAST_NONE, R.string.toast_PleaseUseTop);
        saveSettings();
        MainActivity.getInstance().handleDarkMode();
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id==android.R.id.home) {
            saveSettings();
            //MainActivity.getInstance().handleDarkMode();
            finish();
            return true;
        } else if (id == R.id.action_cancel) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_settings, menu);
        return true;
    }
}
