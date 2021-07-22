package ca.datamagic.hurricanetracks;

import android.content.Context;
import android.database.Cursor;
import androidx.cursoradapter.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class StormSearchAdapter extends CursorAdapter {
    public StormSearchAdapter(Context context, Cursor cursor) {
        super(context, cursor, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView textView = (TextView)view.findViewById(R.id.item);
        int basinIndex = cursor.getColumnIndex("basin");
        int yearIndex = cursor.getColumnIndex("year");
        int stormNameIndex = cursor.getColumnIndex("stormName");
        StringBuffer buffer = new StringBuffer();
        buffer.append(cursor.getString(stormNameIndex));
        buffer.append(" - ");
        buffer.append(Integer.toString(cursor.getInt(yearIndex)));
        buffer.append(" [");
        buffer.append(cursor.getString(basinIndex));
        buffer.append("]");
        textView.setText(buffer.toString());
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.search_item, parent, false);
        return view;
    }
}
