package lu.fisch.canze.activities;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;

import lu.fisch.canze.R;
import lu.fisch.canze.actors.Field;
import lu.fisch.canze.actors.Fields;
import lu.fisch.canze.adapters.FieldAdapter;
import lu.fisch.canze.classes.LoggingLogger;

public class LoggingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logging);

        MainActivity.debug("LoggingActivity: onCreate");

        // load logging logger
        LoggingLogger.getInstance();
        updateList();

        ArrayAdapter<Field> arrayFieldAdapter;
        final Spinner field = findViewById(R.id.field);
        arrayFieldAdapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1);
        arrayFieldAdapter.add(Fields.getInstance().getBySID("5d7.0"));       // speed
        arrayFieldAdapter.add(Fields.getInstance().getBySID("800.6109.24"));      // power
        field.setAdapter(arrayFieldAdapter);

        ArrayAdapter<String> arrayStringAdapter;
        final Spinner interval = findViewById(R.id.interval);
        arrayStringAdapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1);
        for(int i = 5; i<30; i+=5)
            arrayStringAdapter.add(i+" s");
        for(int i = 30; i<=120; i+=30)
            arrayStringAdapter.add(i+" s");
        interval.setAdapter(arrayStringAdapter);

        Button addButton = findViewById(R.id.addButton);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoggingLogger loggingLogger = LoggingLogger.getInstance();

                String intervalString = interval.getSelectedItem().toString();
                intervalString = intervalString.replace(" s","");
                int interval = Integer.valueOf(intervalString);

                // add to logger
                loggingLogger.add((Field) field.getSelectedItem(),interval);
                updateList();
            }
        });
    }


    private void updateList()
    {
        ListView listView = findViewById(R.id.selectedFieldsList);
        FieldAdapter fieldAdapter = new FieldAdapter(MainActivity.getInstance() ,R.layout.logger_field, LoggingLogger.getInstance().getLoggingFields());
        listView.setAdapter(fieldAdapter);

        /*
        ArrayAdapter arrayAdapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1);

        LoggingLogger loggingLogger = LoggingLogger.getInstance();
        MainActivity.debug("Logger: actual fields in list = "+loggingLogger.size());
        for(int i=0; i<loggingLogger.size(); i++) {
            arrayAdapter.add(loggingLogger.getField(i)+"\n"+loggingLogger.getIntnerval(i)+" s");
        }
        listView.setAdapter(arrayAdapter);
        */
    }

}
