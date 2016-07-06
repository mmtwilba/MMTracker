package com.meixi;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import java.util.List;

public class TrackOverlayAdapter extends ArrayAdapter<Track> {
    Drawable m_drawHide;
    Drawable m_drawHideArrow;
    Drawable m_drawUnhide;
    Drawable m_drawUnhideArrow;
    public int selectedPos;

    public TrackOverlayAdapter(Context context, int textViewResourceId, List<Track> objects) {
        super(context, textViewResourceId, objects);
        this.selectedPos = -1;
        this.m_drawUnhide = context.getResources().getDrawable(C0047R.drawable.list_icon_visible_green);
        this.m_drawHide = context.getResources().getDrawable(C0047R.drawable.list_icon_visible_gray);
        this.m_drawUnhideArrow = context.getResources().getDrawable(C0047R.drawable.list_icon_visible_green_arrow);
        this.m_drawHideArrow = context.getResources().getDrawable(C0047R.drawable.list_icon_visible_gray_arrow);
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
        if (((Track) getItem(position)).m_bVisible) {
            if (((Track) getItem(position)).m_bActive) {
                label.setCompoundDrawablesWithIntrinsicBounds(this.m_drawUnhideArrow, null, null, null);
            } else {
                label.setCompoundDrawablesWithIntrinsicBounds(this.m_drawUnhide, null, null, null);
            }
        } else if (((Track) getItem(position)).m_bActive) {
            label.setCompoundDrawablesWithIntrinsicBounds(this.m_drawHideArrow, null, null, null);
        } else {
            label.setCompoundDrawablesWithIntrinsicBounds(this.m_drawHide, null, null, null);
        }
        if (this.selectedPos == position) {
            label.setBackgroundColor(-16776961);
        } else {
            label.setBackgroundColor(-16777216);
        }
        label.setText(((Track) getItem(position)).toString());
        return v;
    }
}
