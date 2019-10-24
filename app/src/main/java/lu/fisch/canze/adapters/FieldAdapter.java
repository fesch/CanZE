package lu.fisch.canze.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

import lu.fisch.canze.R;
import lu.fisch.canze.classes.LoggingLogger;


/**
 * Created by robert.fisch on 16.03.17.
 */

public class FieldAdapter extends ArrayAdapter {
    private final Context context;
    private final int layoutResourceId;
    // the list will hold a reference to the entire data structure
    private final ArrayList<LoggerField> data;

    public FieldAdapter(Context context, int layoutResourceId, ArrayList<LoggerField> data)
    {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent)
    {
        View row = convertView;
        FieldHolder holder;

        if(row == null)
        {
            // this will inflate the XML layout to the row
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new FieldHolder();
            // get the references to the persons list information
            holder.field = row.findViewById(R.id.sid);
            holder.interval  = row.findViewById(R.id.interval);
            holder.deleteButton = row.findViewById(R.id.deleteButton);

            row.setTag(holder);
        }
        else
        {
            holder = (FieldHolder)row.getTag();
        }

        // get the person
        final LoggerField loggerField = data.get(position);
        // fill the list items
        holder.field.setText(loggerField.field.toString());
        holder.interval.setText(loggerField.interval+" s");
        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoggingLogger.getInstance().remove(loggerField.field);
                data.remove(position);
                FieldAdapter.this.notifyDataSetChanged();
            }
        });

        return row;
    }

    /**
     * a static data class to hold a persons information as show in the list
     */
    static class FieldHolder
    {
        TextView field;
        TextView interval;
        Button deleteButton;
    }
}
