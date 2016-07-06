package com.meixi;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import java.util.ArrayList;
import java.util.Iterator;

public class MmiSearchActivity extends Activity {
    private static final int ACTIVITY_FILEBROWSER = 1;
    private static final int BUTTON_STATE_NO_MMI = 0;
    private static final int BUTTON_STATE_NO_SELECTION = 1;
    private static final int BUTTON_STATE_ONE_SELECTED = 2;
    private static final int MAX_MMI_ON_MAP = 2500;
    private static final int MMI_DATA_MAX = 25000;
    private static final int MMI_DATA_PACKAGE = 1000;
    EditText et1;
    final Handler mHandler;
    final Runnable mUpdateResults;
    Button m_CreateWpButton;
    MmiReader m_Mmi;
    MmiOverlayAdapter m_MmiDataAdapter;
    Button m_ViewButton;
    boolean m_bDataThreadRunning;
    boolean m_bFileOpen;
    boolean m_bResetDataThread;
    boolean m_bThreadFinished;
    boolean m_bWaitForUI;
    int m_iCurrentCategory;
    int m_iProgressBarWidth;
    int m_iThreadReadCount;
    int m_iTotalLoadedData;
    String m_sCurrentFileName;
    String m_sCurrentSearchText;
    ArrayAdapter<MmiCategory> m_sp1_adapter;
    ArrayList<MmiCategory> mmi_categories;
    ArrayList<MmiDataBlock> mmi_data;
    ArrayList<MmiDataBlock> mmi_temp_data;
    Spinner sp1;
    View vw1;

    /* renamed from: com.meixi.MmiSearchActivity.1 */
    class C00171 implements Runnable {
        C00171() {
        }

        public void run() {
            if (!(MmiSearchActivity.this.m_MmiDataAdapter == null || MmiSearchActivity.this.mmi_temp_data == null || MmiSearchActivity.this.mmi_data == null)) {
                if (MmiSearchActivity.this.mmi_temp_data.size() > 0) {
                    MmiSearchActivity.this.mmi_data.addAll(MmiSearchActivity.this.mmi_temp_data);
                    MmiSearchActivity.this.mmi_temp_data.clear();
                    MmiSearchActivity.this.m_MmiDataAdapter.notifyDataSetChanged();
                }
                if (MmiSearchActivity.this.m_bResetDataThread) {
                    MmiSearchActivity.this.mmi_data.clear();
                    MmiSearchActivity.this.m_MmiDataAdapter.notifyDataSetChanged();
                }
            }
            if (MmiSearchActivity.this.m_Mmi != null) {
                int iBase = MmiSearchActivity.this.m_Mmi.m_iDataBlocksCount;
                MmiSearchActivity.this.SetProgress(iBase - MmiSearchActivity.this.m_iThreadReadCount, iBase);
            }
            MmiSearchActivity.this.m_bWaitForUI = false;
        }
    }

    /* renamed from: com.meixi.MmiSearchActivity.2 */
    class C00182 implements OnItemClickListener {
        C00182() {
        }

        public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
            if (MmiSearchActivity.this.m_bFileOpen) {
                MmiSearchActivity.this.m_MmiDataAdapter.setSelectedPosition(position);
                MmiSearchActivity.this.EnableButtons(MmiSearchActivity.BUTTON_STATE_ONE_SELECTED);
            }
        }
    }

    /* renamed from: com.meixi.MmiSearchActivity.3 */
    class C00193 implements TextWatcher {
        C00193() {
        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (MmiSearchActivity.this.m_bFileOpen) {
                MmiSearchActivity.this.m_sCurrentSearchText = MmiSearchActivity.this.et1.getText().toString();
                MmiSearchActivity.this.m_MmiDataAdapter.setSelectedPosition(-1);
                MmiSearchActivity.this.EnableButtons(MmiSearchActivity.BUTTON_STATE_NO_SELECTION);
                MmiSearchActivity.this.m_bResetDataThread = true;
            }
        }

        public void afterTextChanged(Editable s) {
        }

        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }
    }

    /* renamed from: com.meixi.MmiSearchActivity.4 */
    class C00204 implements OnItemSelectedListener {
        C00204() {
        }

        public void onItemSelected(AdapterView<?> adapterView, View selectedItemView, int position, long id) {
            if (MmiSearchActivity.this.m_bFileOpen) {
                MmiSearchActivity.this.m_iCurrentCategory = ((MmiCategory) MmiSearchActivity.this.mmi_categories.get(position)).m_iNumber;
                MmiSearchActivity.this.m_MmiDataAdapter.setSelectedPosition(-1);
                MmiSearchActivity.this.EnableButtons(MmiSearchActivity.BUTTON_STATE_NO_SELECTION);
                MmiSearchActivity.this.m_bResetDataThread = true;
            }
        }

        public void onNothingSelected(AdapterView<?> adapterView) {
        }
    }

    /* renamed from: com.meixi.MmiSearchActivity.5 */
    class C00215 implements OnClickListener {
        C00215() {
        }

        public void onClick(View v) {
            if (MmiSearchActivity.this.m_MmiDataAdapter.selectedPos >= 0 && MmiSearchActivity.this.m_bFileOpen) {
                MMTrackerActivity.m_dRequestViewLon = ((MmiDataBlock) MmiSearchActivity.this.m_MmiDataAdapter.getItem(MmiSearchActivity.this.m_MmiDataAdapter.selectedPos)).m_dLongitude;
                MMTrackerActivity.m_dRequestViewLat = ((MmiDataBlock) MmiSearchActivity.this.m_MmiDataAdapter.getItem(MmiSearchActivity.this.m_MmiDataAdapter.selectedPos)).m_dLatitude;
                MmiSearchActivity.this.m_bDataThreadRunning = false;
                MmiSearchActivity.this.finish();
            }
        }
    }

    /* renamed from: com.meixi.MmiSearchActivity.6 */
    class C00226 implements OnClickListener {
        C00226() {
        }

        public void onClick(View v) {
            if (MmiSearchActivity.this.m_MmiDataAdapter.selectedPos >= 0 && MmiSearchActivity.this.m_bFileOpen) {
                double dLon = ((MmiDataBlock) MmiSearchActivity.this.m_MmiDataAdapter.getItem(MmiSearchActivity.this.m_MmiDataAdapter.selectedPos)).m_dLongitude;
                double dLat = ((MmiDataBlock) MmiSearchActivity.this.m_MmiDataAdapter.getItem(MmiSearchActivity.this.m_MmiDataAdapter.selectedPos)).m_dLatitude;
                Waypoint w = new Waypoint(dLon, dLat, Waypoint.WAYPOINT_COLOR_DEFAULT, "Circle");
                if (w != null) {
                    w.m_sName = Tools.MakeProperFileName(((MmiDataBlock) MmiSearchActivity.this.m_MmiDataAdapter.getItem(MmiSearchActivity.this.m_MmiDataAdapter.selectedPos)).m_sName);
                    w.m_bCacheVaild = false;
                    w.m_bLocked = true;
                    w.m_bShowLabel = true;
                    w.WriteGpx(MMTrackerActivity.m_SettingsWaypointPath + w.m_sName + ".gpx");
                    MMTrackerActivity.waypoints.add(w);
                }
                MMTrackerActivity.m_dRequestViewLon = dLon;
                MMTrackerActivity.m_dRequestViewLat = dLat;
                MMTrackerActivity.m_requestedWpRefresh = w;
                MmiSearchActivity.this.m_bDataThreadRunning = false;
                MmiSearchActivity.this.finish();
            }
        }
    }

    /* renamed from: com.meixi.MmiSearchActivity.7 */
    class C00237 implements Runnable {
        C00237() {
        }

        public void run() {
            MmiSearchActivity.this.m_bThreadFinished = false;
            while (MmiSearchActivity.this.m_bDataThreadRunning) {
                if (MmiSearchActivity.this.m_bFileOpen) {
                    if (MmiSearchActivity.this.m_bResetDataThread && !MmiSearchActivity.this.m_bWaitForUI) {
                        MmiSearchActivity.this.m_iThreadReadCount = MmiSearchActivity.this.m_Mmi.m_iDataBlocksCount;
                        MmiSearchActivity.this.m_Mmi.ResetDataPointer();
                        MmiSearchActivity.this.m_iTotalLoadedData = MmiSearchActivity.BUTTON_STATE_NO_MMI;
                        MmiSearchActivity.this.m_bWaitForUI = true;
                        MmiSearchActivity.this.mHandler.post(MmiSearchActivity.this.mUpdateResults);
                        while (MmiSearchActivity.this.m_bWaitForUI) {
                            try {
                                Thread.sleep(10);
                            } catch (InterruptedException e) {
                            }
                        }
                        MmiSearchActivity.this.m_bResetDataThread = false;
                    }
                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException e2) {
                    }
                    if (MmiSearchActivity.this.m_iThreadReadCount <= 0 || MmiSearchActivity.this.m_iTotalLoadedData > MmiSearchActivity.MMI_DATA_MAX) {
                        MmiSearchActivity.this.m_iThreadReadCount = MmiSearchActivity.BUTTON_STATE_NO_MMI;
                    } else if (MmiSearchActivity.this.m_iThreadReadCount >= MmiSearchActivity.MMI_DATA_PACKAGE) {
                        if (MmiSearchActivity.this.m_sCurrentSearchText.length() == 0 && MmiSearchActivity.this.m_iCurrentCategory == -1) {
                            r0 = MmiSearchActivity.this;
                            r0.m_iTotalLoadedData += MmiSearchActivity.this.m_Mmi.ReadData(MmiSearchActivity.this.mmi_temp_data, MmiSearchActivity.MMI_DATA_PACKAGE, MmiSearchActivity.this.m_iCurrentCategory, MmiSearchActivity.this.m_sCurrentSearchText);
                        } else {
                            MmiSearchActivity.this.m_Mmi.Pause();
                            MmiSearchActivity.this.m_Mmi.JniOpenMmi(MmiSearchActivity.this.m_Mmi.m_sFileName);
                            int[] IndexArray = MmiSearchActivity.this.m_Mmi.JniReadBlocks(MmiSearchActivity.MMI_DATA_PACKAGE, MmiSearchActivity.this.m_Mmi.m_StoredFP, MmiSearchActivity.this.m_iCurrentCategory, MmiSearchActivity.this.m_sCurrentSearchText);
                            MmiSearchActivity.this.m_Mmi.JniCloseMmi();
                            MmiSearchActivity.this.m_Mmi.UnPause();
                            if (IndexArray != null) {
                                r0 = MmiSearchActivity.this;
                                r0.m_iTotalLoadedData += MmiSearchActivity.this.m_Mmi.ReadDataIndexed(MmiSearchActivity.this.mmi_temp_data, IndexArray);
                            }
                        }
                        r0 = MmiSearchActivity.this;
                        r0.m_iThreadReadCount -= 1000;
                    } else {
                        r0 = MmiSearchActivity.this;
                        r0.m_iTotalLoadedData += MmiSearchActivity.this.m_Mmi.ReadData(MmiSearchActivity.this.mmi_temp_data, MmiSearchActivity.this.m_iThreadReadCount, MmiSearchActivity.this.m_iCurrentCategory, MmiSearchActivity.this.m_sCurrentSearchText);
                        MmiSearchActivity.this.m_iThreadReadCount = MmiSearchActivity.BUTTON_STATE_NO_MMI;
                    }
                    MmiSearchActivity.this.mHandler.post(MmiSearchActivity.this.mUpdateResults);
                } else {
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e3) {
                    }
                }
            }
            MmiSearchActivity.this.m_bThreadFinished = true;
        }
    }

    public MmiSearchActivity() {
        this.m_MmiDataAdapter = null;
        this.m_bResetDataThread = false;
        this.m_bWaitForUI = false;
        this.m_bDataThreadRunning = false;
        this.m_iTotalLoadedData = BUTTON_STATE_NO_MMI;
        this.m_iThreadReadCount = BUTTON_STATE_NO_MMI;
        this.m_bFileOpen = false;
        this.m_bThreadFinished = true;
        this.mHandler = new Handler();
        this.mUpdateResults = new C00171();
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(MMTrackerActivity.m_SettingsOrientation);
        setContentView(C0047R.layout.mmi_search);
        this.mmi_categories = new ArrayList();
        this.mmi_data = new ArrayList();
        this.mmi_temp_data = new ArrayList();
        this.m_sCurrentSearchText = new String("");
        this.m_sCurrentFileName = MMTrackerActivity.m_CurrentMapName;
        if (this.m_sCurrentFileName.length() > 3) {
            this.m_sCurrentFileName = new StringBuilder(String.valueOf(this.m_sCurrentFileName.substring(BUTTON_STATE_NO_MMI, this.m_sCurrentFileName.length() - 3))).append("mmi").toString();
        } else {
            this.m_sCurrentFileName = "";
        }
        this.m_ViewButton = (Button) findViewById(C0047R.id.buttonMmiView);
        this.m_CreateWpButton = (Button) findViewById(C0047R.id.buttonMmiCreateWaypoint);
        this.et1 = (EditText) findViewById(C0047R.id.editTextMmiSearch);
        this.sp1 = (Spinner) findViewById(C0047R.id.spinnerMmiCategories);
        this.vw1 = findViewById(C0047R.id.viewProgressBar);
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        this.m_iProgressBarWidth = metrics.widthPixels;
        SetProgress(BUTTON_STATE_NO_MMI, 100);
        OpenMmi(this.m_sCurrentFileName);
        this.m_MmiDataAdapter = new MmiOverlayAdapter(this, BUTTON_STATE_NO_MMI, this.mmi_data);
        this.m_MmiDataAdapter.setNotifyOnChange(true);
        this.m_sp1_adapter = new ArrayAdapter(this, 17367048, this.mmi_categories);
        this.m_sp1_adapter.setDropDownViewResource(C0047R.layout.mmi_spinner_dropdown);
        this.sp1.setAdapter(this.m_sp1_adapter);
        ListView lv1 = (ListView) findViewById(C0047R.id.ListViewMmiData);
        lv1.setAdapter(this.m_MmiDataAdapter);
        lv1.setOnItemClickListener(new C00182());
        this.et1.addTextChangedListener(new C00193());
        this.sp1.setOnItemSelectedListener(new C00204());
        this.m_ViewButton.setOnClickListener(new C00215());
        this.m_CreateWpButton.setOnClickListener(new C00226());
    }

    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        this.m_iProgressBarWidth = metrics.widthPixels;
    }

    protected void onPause() {
        super.onPause();
        this.m_bWaitForUI = false;
        this.m_bDataThreadRunning = false;
    }

    public void onResume() {
        super.onResume();
        if (!this.m_bDataThreadRunning) {
            this.m_bDataThreadRunning = true;
            if (this.m_bThreadFinished) {
                StartDataThread();
            }
        }
    }

    private void SetProgress(int count, int base) {
        if (base > 0 && count >= 0 && this.vw1 != null) {
            this.vw1.getLayoutParams().width = (int) Math.round((((double) this.m_iProgressBarWidth) * ((double) count)) / ((double) base));
            this.vw1.requestLayout();
        }
    }

    private void OpenMmi(String sFile) {
        EnableButtons(BUTTON_STATE_NO_MMI);
        this.m_bDataThreadRunning = false;
        if (this.m_bFileOpen) {
            this.m_Mmi.Close();
        }
        this.m_bResetDataThread = false;
        this.m_bWaitForUI = false;
        this.m_bFileOpen = false;
        this.m_Mmi = new MmiReader();
        this.m_sCurrentFileName = sFile;
        boolean bResult = this.m_Mmi.Open(this.m_sCurrentFileName);
        if (!(bResult || MMTrackerActivity.m_sLastLoadedMmiFile == "")) {
            this.m_sCurrentFileName = MMTrackerActivity.m_sLastLoadedMmiFile;
            bResult = this.m_Mmi.Open(this.m_sCurrentFileName);
        }
        this.mmi_categories.clear();
        this.m_Mmi.ReadCategories(this.mmi_categories);
        this.m_iCurrentCategory = -1;
        this.sp1.setSelection(BUTTON_STATE_NO_MMI);
        if (this.m_sp1_adapter != null) {
            this.m_sp1_adapter.notifyDataSetChanged();
        }
        if (bResult) {
            EnableButtons(BUTTON_STATE_NO_SELECTION);
        } else {
            MmiDataBlock d = new MmiDataBlock();
            d.m_sName = getString(C0047R.string.MmiSearchActivity_no_mmi_for_map_found);
            this.mmi_data.add(d);
            EnableButtons(BUTTON_STATE_NO_MMI);
        }
        this.m_bFileOpen = bResult;
        this.m_bResetDataThread = true;
        this.m_bDataThreadRunning = true;
        StartDataThread();
    }

    private void StartDataThread() {
        new Thread(new C00237()).start();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(C0047R.menu.mmi_menu, menu);
        return true;
    }

    public boolean onPrepareOptionsMenu(Menu menu) {
        if (this.m_iThreadReadCount > 0) {
            menu.findItem(C0047R.id.itemCreateList).setEnabled(false);
        } else {
            menu.findItem(C0047R.id.itemCreateList).setEnabled(true);
        }
        if (MMTrackerActivity.m_bMmiListIsOnMap) {
            menu.findItem(C0047R.id.itemClearList).setEnabled(true);
        } else {
            menu.findItem(C0047R.id.itemClearList).setEnabled(false);
        }
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case C0047R.id.itemLoadMmi:
                ShowFileBrowser();
                return true;
            case C0047R.id.itemCreateList:
                CreateListOnMap();
                return true;
            case C0047R.id.itemClearList:
                ClearListFromMap();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void CreateListOnMap() {
        int i = BUTTON_STATE_NO_MMI;
        Iterator it = this.mmi_data.iterator();
        while (it.hasNext()) {
            MmiDataBlock mmi_entry = (MmiDataBlock) it.next();
            i += BUTTON_STATE_NO_SELECTION;
            Waypoint w = new Waypoint(mmi_entry.m_dLongitude, mmi_entry.m_dLatitude, Waypoint.WAYPOINT_COLOR_MMI_LIST, "Circle");
            if (w != null) {
                w.m_bFromFile = false;
                w.m_sName = mmi_entry.m_sName;
                w.m_bCacheVaild = false;
                w.m_bLocked = true;
                MMTrackerActivity.waypoints.add(w);
            }
            MMTrackerActivity.m_bRequestAllWpRefresh = true;
            MMTrackerActivity.m_bMmiListIsOnMap = true;
            if (i >= MAX_MMI_ON_MAP) {
                return;
            }
        }
    }

    public void ClearListFromMap() {
        ArrayList<Waypoint> temp_wp = new ArrayList();
        Iterator it = MMTrackerActivity.waypoints.iterator();
        while (it.hasNext()) {
            Waypoint w = (Waypoint) it.next();
            if (!w.m_bFromFile) {
                temp_wp.add(w);
            }
        }
        it = temp_wp.iterator();
        while (it.hasNext()) {
            MMTrackerActivity.waypoints.remove((Waypoint) it.next());
        }
        MMTrackerActivity.m_bMmiListIsOnMap = false;
    }

    public void ShowFileBrowser() {
        Intent i = new Intent(this, FileListActivity.class);
        i.putExtra("path", MMTrackerActivity.m_SettingsMapPath);
        i.putExtra("extension", "mmi");
        i.putExtra("header", getString(C0047R.string.MmiSearchActivity_file_browser_header));
        i.putExtra("exclude", "");
        startActivityForResult(i, BUTTON_STATE_NO_SELECTION);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == BUTTON_STATE_NO_SELECTION && data != null && resultCode == BUTTON_STATE_NO_SELECTION) {
            OpenMmi(data.getStringExtra("file"));
            if (this.m_bFileOpen) {
                MMTrackerActivity.m_sLastLoadedMmiFile = data.getStringExtra("file");
            }
        }
    }

    public void EnableButtons(int iState) {
        if (this.m_ViewButton != null && this.m_CreateWpButton != null) {
            switch (iState) {
                case BUTTON_STATE_NO_MMI /*0*/:
                    this.m_ViewButton.setEnabled(false);
                    this.m_CreateWpButton.setEnabled(false);
                    this.sp1.setEnabled(false);
                    this.et1.setEnabled(false);
                case BUTTON_STATE_NO_SELECTION /*1*/:
                    this.m_ViewButton.setEnabled(false);
                    this.m_CreateWpButton.setEnabled(false);
                    this.sp1.setEnabled(true);
                    this.et1.setEnabled(true);
                case BUTTON_STATE_ONE_SELECTED /*2*/:
                    this.m_ViewButton.setEnabled(true);
                    this.m_CreateWpButton.setEnabled(true);
                    this.sp1.setEnabled(true);
                    this.et1.setEnabled(true);
                default:
            }
        }
    }
}
