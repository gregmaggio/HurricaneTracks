package ca.datamagic.hurricanetracks;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class SpinnerAdapter extends ArrayAdapter<String> {
    private String[] items = null;

    public SpinnerAdapter(@NonNull Context context, @NonNull String[] items) {
        super(context, R.layout.spinner_item_layout, R.id.spinnerText);
        this.items = items;
    }

    @Override
    public void clear() {
        super.clear();
        this.items = null;
    }

    @Override
    public int getCount() {
        if (this.items != null) {
            return this.items.length;
        }
        return 0;
    }

    @Nullable
    @Override
    public String getItem(int position) {
        if (this.items != null) {
            return this.items[position];
        }
        return null;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = super.getView(position, convertView, parent);
        TextView textView = view.findViewById(R.id.spinnerText);
        textView.setText(getItem(position));
        return view;
    }
}
