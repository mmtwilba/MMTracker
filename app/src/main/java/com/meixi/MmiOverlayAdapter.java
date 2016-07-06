package com.meixi;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import java.util.List;

public class MmiOverlayAdapter extends ArrayAdapter<MmiDataBlock> {
    public int selectedPos;

    public MmiOverlayAdapter(Context context, int textViewResourceId, List<MmiDataBlock> objects) {
        super(context, textViewResourceId, objects);
        this.selectedPos = -1;
    }

    public void setSelectedPosition(int pos) {
        this.selectedPos = pos;
        notifyDataSetChanged();
    }

    public void updateViews() {
        notifyDataSetChanged();
    }

    public int getSelectedPosition() {
        return this.selectedPos;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            v = ((LayoutInflater) getContext().getSystemService("layout_inflater")).inflate(C0047R.layout.overlay_list_row, null);
        }
        TextView label = (TextView) v.findViewById(C0047R.id.txtOverlayRow);
        if (this.selectedPos == position) {
            label.setBackgroundColor(-16776961);
        } else {
            label.setBackgroundColor(-16777216);
        }
        label.setText(((MmiDataBlock) getItem(position)).toString());
        return v;
    }
}
