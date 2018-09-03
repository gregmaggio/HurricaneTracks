package ca.datamagic.hurricanetracks;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class ListViewAdapter extends ArrayAdapter<String> {
    private String[] _items = null;

    public ListViewAdapter(@NonNull Context context, String[] items) {
        super(context, R.layout.list_item, R.id.item_text);
        _items = items;
    }

    @Override
    public void clear() {
        super.clear();
        _items = null;
    }

    @Override
    public int getCount() {
        if (_items != null) {
            return _items.length;
        }
        return 0;
    }

    @Nullable
    @Override
    public String getItem(int position) {
        if (_items != null) {
            return _items[position];
        }
        return null;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = super.getView(position, convertView, parent);
        TextView textView = view.findViewById(R.id.item_text);
        textView.setText(getItem(position));
        return view;
    }
}
