package com.meixi;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

public class FileListActivity extends Activity {
    ArrayList<FileListEntry> file_names;
    private ListView lv1;
    private String m_sCurrentPath;
    private String m_sFileToExclude;
    private String m_sFilterExtension;

    /* renamed from: com.meixi.FileListActivity.1 */
    class C00051 implements OnItemClickListener {
        C00051() {
        }

        public void onItemClick(AdapterView<?> adapterView, View v, int position, long id) {
            if (((FileListEntry) FileListActivity.this.file_names.get(position)).m_bDirectory) {
                if (((FileListEntry) FileListActivity.this.file_names.get(position)).m_sName == "[..]") {
                    FileListActivity.this.m_sCurrentPath = new StringBuilder(String.valueOf(FileListActivity.this.m_sCurrentPath.substring(0, FileListActivity.this.m_sCurrentPath.lastIndexOf(47, FileListActivity.this.m_sCurrentPath.length() - 2)))).append("/").toString();
                } else {
                    FileListActivity.this.m_sCurrentPath = ((FileListEntry) FileListActivity.this.file_names.get(position)).m_sPath;
                    if (FileListActivity.this.m_sCurrentPath.charAt(FileListActivity.this.m_sCurrentPath.length() - 1) != '/') {
                        FileListActivity fileListActivity = FileListActivity.this;
                        fileListActivity.m_sCurrentPath = fileListActivity.m_sCurrentPath + "/";
                    }
                }
                FileListActivity.this.FillListview(FileListActivity.this.m_sCurrentPath);
                return;
            }
            Intent i = new Intent();
            i.putExtra("file", ((FileListEntry) FileListActivity.this.file_names.get(position)).m_sPath);
            i.putExtra(MapCacheHelper.KEY_NAME, ((FileListEntry) FileListActivity.this.file_names.get(position)).m_sName);
            FileListActivity.this.setResult(1, i);
            FileListActivity.this.finish();
        }
    }

    /* renamed from: com.meixi.FileListActivity.2 */
    class C00062 implements OnClickListener {
        C00062() {
        }

        public void onClick(DialogInterface dialog, int which) {
            Intent i = new Intent();
            i.putExtra("file", "");
            FileListActivity.this.setResult(2, i);
            FileListActivity.this.finish();
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
            if (MMTrackerActivity.m_CurrentMapName == null) {
                return "";
            }
            if (MMTrackerActivity.m_CurrentMapName.equals(this.m_sPath)) {
                return ">  " + this.m_sName;
            }
            return this.m_sName;
        }
    }

    public FileListActivity() {
        this.file_names = new ArrayList();
    }

    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setRequestedOrientation(MMTrackerActivity.m_SettingsOrientation);
        setContentView(C0047R.layout.file_selector);
        this.m_sCurrentPath = getIntent().getExtras().getString("path");
        this.m_sFilterExtension = getIntent().getExtras().getString("extension");
        this.m_sFileToExclude = getIntent().getExtras().getString("exclude");
        ((TextView) findViewById(C0047R.id.TextViewFilesHeader)).setText(getIntent().getExtras().getString("header"));
        if (FillListview(this.m_sCurrentPath)) {
            this.lv1.setOnItemClickListener(new C00051());
            return;
        }
        AlertDialog alertDialog = new Builder(this).create();
        alertDialog.setTitle("MM Tracker");
        alertDialog.setMessage(getString(C0047R.string.FileListActivity_invalid_map_dir));
        alertDialog.setButton("OK", new C00062());
        alertDialog.show();
    }

    boolean FillListview(String path) {
        boolean bExclude = false;
        File[] files = new File(path).listFiles(new CustomFileFilter(this.m_sFilterExtension, false));
        if (files == null) {
            return false;
        }
        this.file_names.clear();
        if (path.compareTo("/") != 0) {
            this.file_names.add(new FileListEntry("[..]", path, true));
        }
        for (File f : files) {
            if (!f.isDirectory()) {
                if (this.m_sFileToExclude != null) {
                    bExclude = this.m_sFileToExclude.equals(f.getAbsolutePath());
                }
                if (!bExclude) {
                    this.file_names.add(new FileListEntry(f.getName(), f.getAbsolutePath(), false));
                }
            } else if (!f.isHidden() && f.canRead()) {
                this.file_names.add(new FileListEntry("[" + f.getName() + "]", new StringBuilder(String.valueOf(path)).append(f.getName()).toString(), true));
            }
        }
        Collections.sort(this.file_names);
        this.lv1 = (ListView) findViewById(C0047R.id.ListViewFiles);
        this.lv1.setAdapter(new ArrayAdapter(this, 17367043, this.file_names));
        return true;
    }
}
