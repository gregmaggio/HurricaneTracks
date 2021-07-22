package ca.datamagic.hurricanetracks;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ca.datamagic.hurricanetracks.dto.BasinDTO;

public class BasinsAdapter extends BaseAdapter implements View.OnClickListener {
    private LayoutInflater inflater = null;
    private List<BasinDTO> basins = null;
    private List<BasinsAdapterListener> listeners = new ArrayList<BasinsAdapterListener>();

    public BasinsAdapter(Context context, List<BasinDTO> basins) {
        this.inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.basins = basins;
    }

    public void addListener(BasinsAdapterListener listener) {
        this.listeners.add(listener);
    }

    public void removeListener(BasinsAdapterListener listener) {
        this.listeners.remove(listener);
    }

    private void fireBasinSelect(BasinDTO basin) {
        for (int ii = 0; ii < this.listeners.size(); ii++) {
            try {
                this.listeners.get(ii).onBasinSelect(basin);
            } catch (Throwable t) {
                // TODO
            }
        }
    }

    @Override
    public void onClick(View view) {
        Object tag = view.getTag();
        if (tag != null) {
            if (tag instanceof BasinDTO) {
                fireBasinSelect((BasinDTO)tag);
            }
        }
    }

    @Override
    public int getCount() {
        return this.basins.size();
    }

    @Override
    public Object getItem(int position) {
        return this.basins.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        BasinDTO basin = null;
        if (this.basins.size() > 0) {
            basin = this.basins.get(position);
        }
        View view = convertView;
        boolean viewInflated = false;
        if (view == null) {
            view = this.inflater.inflate(R.layout.basin_choose, null);
            viewInflated = true;
        }
        TextView basinName = view.findViewById(R.id.basin_name);
        if (basin != null) {
            basinName.setTag(basin);
            basinName.setText(basin.getDescription());
        }
        if (viewInflated) {
            basinName.setOnClickListener(this);
        }
        return view;
    }

    public interface BasinsAdapterListener {
        public void onBasinSelect(BasinDTO basin);
    }
}
