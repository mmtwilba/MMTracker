package com.meixi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

public class DirListActivity extends Activity {
    private Button SelectButton;
    ArrayList<FileListEntry> file_names;
    private ListView lv1;
    boolean m_bIgnoreWriteable;
    private String m_sCurrentPath;
    private String m_sCurrentType;

    /* renamed from: com.meixi.DirListActivity.1 */
    class C00031 implements OnClickListener {
        C00031() {
        }

        public void onClick(View v) {
            Intent i = new Intent();
            i.putExtra("selected_path", DirListActivity.this.m_sCurrentPath);
            DirListActivity.this.setResult(1, i);
            DirListActivity.this.finish();
        }
    }

    /* renamed from: com.meixi.DirListActivity.2 */
    class C00042 implements OnItemClickListener {
        C00042() {
        }

        public void onItemClick(AdapterView<?> adapterView, View v, int position, long id) {
            if (((FileListEntry) DirListActivity.this.file_names.get(position)).m_bDirectory) {
                if (((FileListEntry) DirListActivity.this.file_names.get(position)).m_sName == "[..]") {
                    DirListActivity.this.m_sCurrentPath = new StringBuilder(String.valueOf(DirListActivity.this.m_sCurrentPath.substring(0, DirListActivity.this.m_sCurrentPath.lastIndexOf(47, DirListActivity.this.m_sCurrentPath.length() - 2)))).append("/").toString();
                } else {
                    DirListActivity.this.m_sCurrentPath = ((FileListEntry) DirListActivity.this.file_names.get(position)).m_sPath;
                    if (DirListActivity.this.m_sCurrentPath.charAt(DirListActivity.this.m_sCurrentPath.length() - 1) != '/') {
                        DirListActivity dirListActivity = DirListActivity.this;
                        dirListActivity.m_sCurrentPath = dirListActivity.m_sCurrentPath + "/";
                    }
                }
                if (DirListActivity.this.FillListview(DirListActivity.this.m_sCurrentPath)) {
                    ((TextView) DirListActivity.this.findViewById(C0047R.id.textPath)).setText(DirListActivity.this.m_sCurrentPath);
                }
            }
        }
    }

    private class FileListEntry implements Comparable<FileListEntry> {
        boolean m_bDirectory;
        String m_sName;
        String m_sPath;

        public FileListEntry(String name, String path, boolean dir) {
            this.m_sName = name;
            this.m_bDirectory = dir;
            this.m_sPath = path;
        }

        public int compareTo(FileListEntry e) {
            if (this.m_bDirectory == e.m_bDirectory) {
                return this.m_sName.toLowerCase().compareTo(e.m_sName.toLowerCase());
            }
            if (!this.m_bDirectory || e.m_bDirectory) {
                return 1;
            }
            return -1;
        }

        public String toString() {
            return this.m_sName;
        }
    }

    public DirListActivity() {
        this.file_names = new ArrayList();
    }

    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setRequestedOrientation(MMTrackerActivity.m_SettingsOrientation);
        setContentView(C0047R.layout.dir_selector);
        this.m_sCurrentPath = getIntent().getExtras().getString("path");
        this.m_sCurrentType = getIntent().getExtras().getString("type");
        this.m_bIgnoreWriteable = getIntent().getExtras().getBoolean("ignore_writeable");
        if (!new File(this.m_sCurrentPath).canWrite()) {
            this.m_sCurrentPath = "/";
        }
        ((TextView) findViewById(C0047R.id.textPath)).setText(this.m_sCurrentPath);
        ((TextView) findViewById(C0047R.id.textInfo)).setText(getString(C0047R.string.DirListActivity_select_dir) + this.m_sCurrentType);
        this.SelectButton = (Button) findViewById(C0047R.id.buttonSelect);
        this.SelectButton.setOnClickListener(new C00031());
        if (FillListview(this.m_sCurrentPath)) {
            this.lv1.setOnItemClickListener(new C00042());
        }
    }

    boolean FillListview(String path) {
        int i = 0;
        File[] files = new File(path).listFiles();
        if (files == null) {
            return false;
        }
        this.file_names.clear();
        if (path.compareTo("/") != 0) {
            this.file_names.add(new FileListEntry("[..]", path, true));
        }
        int length = files.length;
        while (i < length) {
            File f = files[i];
            if ((f.isDirectory() && !f.isHidden() && (f.canWrite() || this.m_bIgnoreWriteable)) || f.getName().toLowerCase().equals("external_sd") || f.getName().toLowerCase().equals("removable")) {
                this.file_names.add(new FileListEntry("[" + f.getName() + "]", new StringBuilder(String.valueOf(path)).append(f.getName()).toString(), true));
            }
            i++;
        }
        Collections.sort(this.file_names);
        this.lv1 = (ListView) findViewById(C0047R.id.ListViewDirs);
        this.lv1.setAdapter(new ArrayAdapter(this, 17367043, this.file_names));
        return true;
    }
}
