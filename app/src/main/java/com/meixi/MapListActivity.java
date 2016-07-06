package com.meixi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import java.util.ArrayList;

public class MapListActivity extends Activity {
    private ListView lv1;
    ArrayList<MapList> map_list;

    /* renamed from: com.meixi.MapListActivity.1 */
    class C00071 implements OnItemClickListener {
        C00071() {
        }

        public void onItemClick(AdapterView<?> adapterView, View v, int position, long id) {
            Intent i = new Intent();
            i.putExtra("map_path", ((MapList) MapListActivity.this.map_list.get(position)).m_sMapPath);
            MapListActivity.this.setResult(1, i);
            MapListActivity.this.finish();
        }
    }

    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(C0047R.layout.map_list);
        setRequestedOrientation(MMTrackerActivity.m_SettingsOrientation);
        this.map_list = MMTrackerActivity.getMapList();
        if (this.map_list.size() > 0) {
            this.lv1 = (ListView) findViewById(C0047R.id.ListView01);
            this.lv1.setAdapter(new ArrayAdapter(this, 17367043, this.map_list));
            this.lv1.setOnItemClickListener(new C00071());
        }
    }
}
