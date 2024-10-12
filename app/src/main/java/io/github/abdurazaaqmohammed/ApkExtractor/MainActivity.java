package io.github.abdurazaaqmohammed.ApkExtractor;

import static android.provider.Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION;
import static io.github.abdurazaaqmohammed.ApkExtractor.LegacyUtils.canSetNotificationBarTransparent;
import static io.github.abdurazaaqmohammed.ApkExtractor.LegacyUtils.supportsActionBar;
import static io.github.abdurazaaqmohammed.ApkExtractor.LegacyUtils.supportsArraysCopyOfAndDownloadManager;
import static io.github.abdurazaaqmohammed.ApkExtractor.LegacyUtils.supportsFileChannel;
import static io.github.abdurazaaqmohammed.ApkExtractor.LegacyUtils.supportsSplits;
import static com.reandroid.apkeditor.merge.LogUtil.logEnabled;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.LightingColorFilter;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.OpenableColumns;
import android.support.v4.content.FileProvider;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.apksig.internal.util.Pair;
import com.reandroid.apk.ApkBundle;
import com.reandroid.apkeditor.merge.LogUtil;
import com.reandroid.apkeditor.merge.Merger;

import java.io.File;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.nio.channels.ClosedByInterruptException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import com.github.paul035.LocaleHelper;
import com.starry.FileUtils;

import yuku.ambilwarna.AmbilWarnaDialog;

/** @noinspection deprecation, rawtypes */
public class MainActivity extends Activity {
    private static boolean ask = true;
    private static boolean signApk;
    public static int textColor;
    public static int bgColor;
    public static boolean errorOccurred;
    public static String lang;
    public static boolean showIcon;
    public static boolean antisplit;
    public static boolean showLastUpdate;
    public static boolean showFirstInstalled;
    public static boolean showVersionCode;
    public static boolean showVersionName;
    public static boolean showPackageName;
    public static boolean showAppName;
    private final AppExpandableListAdapter[] appAdapter = new AppExpandableListAdapter[2];

    public void setButtonBorder(Button button) {
        ShapeDrawable border = new ShapeDrawable(new RectShape());
        Paint paint = border.getPaint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(textColor);
        paint.setStrokeWidth(4);

        button.setTextColor(textColor);
        button.setBackgroundDrawable(border);
    }

    private void setColor(int color, boolean isTextColor, ScrollView settingsMenu) {
        final boolean supportsSwitch = Build.VERSION.SDK_INT > 13;
        boolean fromSettingsMenu = settingsMenu != null;
        //if(fromSettingsMenu) settingsMenu.setBackgroundColor(color);
        if (isTextColor) {
            this.<TextView>findViewById(R.id.search_bar).setTextColor(color);
            LightingColorFilter lcf = new LightingColorFilter(Color.BLACK, textColor = color);
            this.<ImageView>findViewById(R.id.settingsButton).setColorFilter(lcf);
            this.<ImageView>findViewById(R.id.filterButton).setColorFilter(lcf);
            ((TextView) findViewById(system ? R.id.userApps : R.id.systemApps)).setTextColor(color);
            setButtonBorder(findViewById(system ? R.id.systemApps : R.id.userApps));
            if (fromSettingsMenu) {
                ((TextView) settingsMenu.findViewById(supportsSwitch ? R.id.ask : R.id.askText)).setTextColor(color);
                ((TextView) settingsMenu.findViewById(supportsSwitch ? R.id.signToggle : R.id.signToggleText)).setTextColor(color);
                ((TextView) settingsMenu.findViewById(supportsSwitch ? R.id.showIconToggle : R.id.showIconToggleText)).setTextColor(color);
                ((TextView) settingsMenu.findViewById(supportsSwitch ? R.id.antisplitToggle : R.id.antisplitText)).setTextColor(color);
                ((TextView) settingsMenu.findViewById(supportsSwitch ? R.id.showLastUpdateToggle : R.id.showLastUpdateToggleText))
                        .setTextColor(color);

                ((TextView) settingsMenu.findViewById(supportsSwitch ? R.id.showFirstInstallToggle : R.id.showFirstInstallToggleText))
                        .setTextColor(color);

                ((TextView) settingsMenu.findViewById(supportsSwitch ? R.id.showVersionNameToggle : R.id.showVersionNameToggleText))
                        .setTextColor(color);

                ((TextView) settingsMenu.findViewById(supportsSwitch ? R.id.showVersionCodeToggle : R.id.showVersionCodeToggleText))
                        .setTextColor(color);

                ((TextView) settingsMenu.findViewById(supportsSwitch ? R.id.showAppNameToggle : R.id.showAppNameToggleText))
                        .setTextColor(color);

                ((TextView) settingsMenu.findViewById(supportsSwitch ? R.id.showPkgNameToggle : R.id.showPkgNameToggleText))
                        .setTextColor(color);
            }
        } else findViewById(R.id.main).setBackgroundColor(bgColor = color);
        ((ListView) findViewById(R.id.user_app_list_view)).invalidateViews();

        if (fromSettingsMenu) {
            setButtonBorder(settingsMenu.findViewById(R.id.langPicker));
            setButtonBorder(settingsMenu.findViewById(R.id.changeTextColor));
            setButtonBorder(settingsMenu.findViewById(R.id.changeBgColor));
            if (!supportsSwitch) {
                setButtonBorder(settingsMenu.findViewById(R.id.ask));
                setButtonBorder(settingsMenu.findViewById(R.id.signToggle));
                setButtonBorder(settingsMenu.findViewById(R.id.antisplitToggle));
                setButtonBorder(settingsMenu.findViewById(R.id.showIconToggle));
                setButtonBorder(settingsMenu.findViewById(R.id.showAppNameToggle));
                setButtonBorder(settingsMenu.findViewById(R.id.showFirstInstallToggle));
                setButtonBorder(settingsMenu.findViewById(R.id.showLastUpdateToggle));
                setButtonBorder(settingsMenu.findViewById(R.id.showVersionCodeToggle));
                setButtonBorder(settingsMenu.findViewById(R.id.showVersionNameToggle));
                setButtonBorder(settingsMenu.findViewById(R.id.showPkgNameToggle));
            }
        }
    }

    public static int sortMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        new AppLoaderTask(this).execute();
        super.onCreate(savedInstanceState);
        handler = new Handler(Looper.getMainLooper());

        deleteDir(getCacheDir());

        setContentView(R.layout.activity_main);

        // Fetch settings from SharedPreferences
        SharedPreferences settings = getSharedPreferences("set", Context.MODE_PRIVATE);
        setColor(settings.getInt("textColor", Color.WHITE), true, null);
        setColor(settings.getInt("backgroundColor", Color.BLACK), false, null);


        ActionBar ab;
        if (supportsActionBar && (ab = getActionBar()) != null) {
            Spannable text = new SpannableString(getString(R.string.app_name));
            text.setSpan(new ForegroundColorSpan(textColor), 0, text.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
            ab.setTitle(text);
            ab.setBackgroundDrawable(new ColorDrawable(bgColor));
        }

        // Load preferences
        signApk = settings.getBoolean("signApk", true);
        logEnabled = settings.getBoolean("logEnabled", true);
        ask = settings.getBoolean("ask", true);
        showIcon = settings.getBoolean("showIcon", true);
        showFirstInstalled = settings.getBoolean("showFirstInstalled", true);
        showAppName = settings.getBoolean("showAppName", true);
        showLastUpdate = settings.getBoolean("showLastUpdate", true);
        showPackageName = settings.getBoolean("showPackageName", true);
        showVersionCode = settings.getBoolean("showVersionCode", true);
        showVersionName = settings.getBoolean("showVersionName", true);
        antisplit = settings.getBoolean("antisplit", false);
        sortMode = settings.getInt("sortMode", 0);

        lang = settings.getString("lang", "en");
        if (Objects.equals(lang, Locale.getDefault().getLanguage())) rss = getResources();
        else updateLang(LocaleHelper.setLocale(MainActivity.this, lang).getResources(), null);

        findViewById(R.id.settingsButton).setOnClickListener(v -> {
            ScrollView l = (ScrollView) LayoutInflater.from(this).inflate(R.layout.dialog_settings, null);
            l.setBackgroundColor(bgColor);

            setColor(textColor, true, l);

            ((TextView) l.findViewById(R.id.langPicker)).setText(rss.getString(R.string.lang));
            boolean supportsSwitch = Build.VERSION.SDK_INT > 13;
            ((TextView) l.findViewById(supportsSwitch ? R.id.ask : R.id.askText)).setText(rss.getString(R.string.ask));
            ((TextView) l.findViewById(supportsSwitch ? R.id.signToggle : R.id.signToggleText)).setText(rss.getString(R.string.sign_apk));
            ((TextView) l.findViewById(supportsSwitch ? R.id.showIconToggle : R.id.showIconToggleText)).setText(rss.getString(R.string.show_icons));
            ((TextView) l.findViewById(supportsSwitch ? R.id.showLastUpdateToggle : R.id.showLastUpdateToggleText))
                    .setText(rss.getString(R.string.show_last_updated));

            ((TextView) l.findViewById(supportsSwitch ? R.id.showFirstInstallToggle : R.id.showFirstInstallToggleText))
                    .setText(rss.getString(R.string.show_first_install));

            ((TextView) l.findViewById(supportsSwitch ? R.id.showVersionNameToggle : R.id.showVersionNameToggleText))
                    .setText(rss.getString(R.string.show_v_name));

            ((TextView) l.findViewById(supportsSwitch ? R.id.showVersionCodeToggle : R.id.showVersionCodeToggleText))
                    .setText(rss.getString(R.string.show_v_code));

            ((TextView) l.findViewById(supportsSwitch ? R.id.showAppNameToggle : R.id.showAppNameToggleText))
                    .setText(rss.getString(R.string.show_app));

            ((TextView) l.findViewById(supportsSwitch ? R.id.showPkgNameToggle : R.id.showPkgNameToggleText))
                    .setText(rss.getString(R.string.show_pkg));
            ((TextView) l.findViewById(R.id.changeTextColor)).setText(rss.getString(R.string.change_text_color));
            ((TextView) l.findViewById(R.id.changeBgColor)).setText(rss.getString(R.string.change_background_color));

            CompoundButton signToggle = l.findViewById(R.id.signToggle);
            signToggle.setChecked(signApk);
            signToggle.setOnCheckedChangeListener((buttonView, isChecked) -> signApk = isChecked);
            signToggle.setVisibility(antisplit ? View.VISIBLE : View.GONE);
            if (!supportsSwitch)
                findViewById(R.id.signToggleText).setVisibility(antisplit ? View.VISIBLE : View.GONE);

            CompoundButton antisplitToggle = l.findViewById(R.id.antisplitToggle);
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                antisplit = false;
                antisplitToggle.setVisibility(View.GONE);
            } else {
                antisplitToggle.setChecked(antisplit);
                antisplitToggle.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    antisplit = isChecked;
                    signToggle.setVisibility(isChecked ? View.VISIBLE : View.GONE);
                });
            }

            CompoundButton showIconToggle = l.findViewById(R.id.showIconToggle);
            showIconToggle.setChecked(showIcon);
            showIconToggle.setOnCheckedChangeListener((buttonView, isChecked) -> {
                showIcon = isChecked;
                ((ListView) findViewById(R.id.user_app_list_view)).invalidateViews();
            });

            CompoundButton showLastUpdateToggle = l.findViewById(R.id.showLastUpdateToggle);
            showLastUpdateToggle.setChecked(showLastUpdate);
            showLastUpdateToggle.setOnCheckedChangeListener((buttonView, isChecked) -> {
                showLastUpdate = isChecked;
                ((ListView) findViewById(R.id.user_app_list_view)).invalidateViews();
            });

            CompoundButton showFirstInstallToggle = l.findViewById(R.id.showFirstInstallToggle);
            showFirstInstallToggle.setChecked(showFirstInstalled);
            showFirstInstallToggle.setOnCheckedChangeListener((buttonView, isChecked) -> {
                showFirstInstalled = isChecked;
                ((ListView) findViewById(R.id.user_app_list_view)).invalidateViews();
            });

            CompoundButton showVersionCodeToggle = l.findViewById(R.id.showVersionCodeToggle);
            showVersionCodeToggle.setChecked(showVersionCode);
            showVersionCodeToggle.setOnCheckedChangeListener((buttonView, isChecked) -> {
                showVersionCode = isChecked;
                ((ListView) findViewById(R.id.user_app_list_view)).invalidateViews();
            });

            CompoundButton showVersionNameToggle = l.findViewById(R.id.showVersionNameToggle);
            showVersionNameToggle.setChecked(showVersionName);
            showVersionNameToggle.setOnCheckedChangeListener((buttonView, isChecked) -> {
                showVersionName = isChecked;
                ((ListView) findViewById(R.id.user_app_list_view)).invalidateViews();
            });

            CompoundButton showAppNameToggle = l.findViewById(R.id.showAppNameToggle);
            showAppNameToggle.setChecked(showAppName);
            showAppNameToggle.setOnCheckedChangeListener((buttonView, isChecked) -> {
                showAppName = isChecked;
                ((ListView) findViewById(R.id.user_app_list_view)).invalidateViews();
            });

            CompoundButton showPkgNameToggle = l.findViewById(R.id.showPkgNameToggle);
            showPkgNameToggle.setChecked(showPackageName);
            showPkgNameToggle.setOnCheckedChangeListener((buttonView, isChecked) -> {
                showPackageName = isChecked;
                ((ListView) findViewById(R.id.user_app_list_view)).invalidateViews();
            });

            CompoundButton askSwitch = l.findViewById(R.id.ask);
            if (LegacyUtils.doesNotSupportInbuiltAndroidFilePicker) {
                ask = false;
                askSwitch.setVisibility(View.GONE);
            } else {
                askSwitch.setChecked(ask);
                askSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    ask = isChecked;
                    if (!isChecked) checkStoragePerm();
                });
            }

            Button langPicker = l.findViewById(R.id.langPicker);
            setButtonBorder(langPicker);
            langPicker.setOnClickListener(v2 -> {
                String[] langs = rss.getStringArray(R.array.langs);

                String[] display = rss.getStringArray(R.array.langs_display);

                AlertDialog ad = new AlertDialog.Builder(this).setSingleChoiceItems(display, -1, (dialog, which) -> {
                    updateLang(LocaleHelper.setLocale(MainActivity.this, lang = langs[which]).getResources(), l);
                    dialog.dismiss();
                }).create();
                styleAlertDialog(ad, display, null);
                for (int i = 0; i < langs.length; i++) {
                    if (Objects.equals(lang, langs[i])) {
                        ad.getListView().setItemChecked(i, true);
                        break;
                    }
                }
            });

            l.findViewById(R.id.changeBgColor).setOnClickListener(v3 -> showColorPickerDialog(false, bgColor, l));
            l.findViewById(R.id.changeTextColor).setOnClickListener(v4 -> showColorPickerDialog(true, textColor, l));
            TextView title = new TextView(this);
            title.setText(rss.getString(R.string.settings));
            title.setTextColor(textColor);
            title.setTextSize(25);
            styleAlertDialog(
                    new AlertDialog.Builder(this).setCustomTitle(title).setView(l)
                            .setPositiveButton(rss.getString(R.string.close), (dialog, which) -> dialog.dismiss()).create(), null, null);
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    private static class AppLoaderTask extends AsyncTask<Void, Void, Pair<List<AppInfo>, List<AppInfo>>> {
        private final WeakReference<MainActivity> activityRef;
        private final PackageManager pm;

        AppLoaderTask(MainActivity activity) {
            this.activityRef = new WeakReference<>(activity);
            this.pm = activity.getPackageManager();
        }

        @Override
        protected Pair<List<AppInfo>, List<AppInfo>> doInBackground(Void... voids) {
            List<AppInfo> userApps = new ArrayList<>();
            List<AppInfo> systemApps = new ArrayList<>();

            // Use PackageManager.GET_META_DATA flag to load minimal info first
            List<ApplicationInfo> apps = pm.getInstalledApplications(PackageManager.GET_META_DATA);

            for (ApplicationInfo app : apps) {
                try {
                    String packageName = app.packageName;
                    boolean isSystemApp = (app.flags & ApplicationInfo.FLAG_SYSTEM) != 0;

                    // Create AppInfo object with minimal data first
                    AppInfo appInfo = new AppInfo(
                            app.loadLabel(pm).toString(),
                            null,
                            packageName,
                            app.enabled,
                            supportsSplits && app.splitSourceDirs != null,
                            "", // Will be populated later if needed
                            "",
                            0,
                            ""
                    );

                    if (isSystemApp) {
                        systemApps.add(appInfo);
                    } else {
                        userApps.add(appInfo);
                    }
                } catch (Exception ignored) {
                }
            }

            Comparator<AppInfo> comparator = Comparator.comparing((AppInfo p) -> sortMode == 0 ? p.name.toLowerCase(supportsArraysCopyOfAndDownloadManager ? Locale.ROOT : Locale.getDefault()) : sortMode == 1 ? p.lastUpdated : p.firstInstalled);
            if(sortMode != 0) comparator = comparator.reversed();
            Collections.sort(userApps, comparator);
            Collections.sort(systemApps, comparator);

            return new Pair<>(userApps, systemApps);
        }

        @Override
        protected void onPostExecute(Pair<List<AppInfo>, List<AppInfo>> result) {
            MainActivity activity = activityRef.get();
            if (activity == null) return;

            activity.userAppInfoList = result.getFirst();
            activity.systemAppInfoList = result.getSecond();

            // Setup UI with loaded apps
            activity.setupAppLists();

            // Start loading additional app details in background
            new AppDetailsLoaderTask(activity).execute();
        }
    }

    private List<AppInfo> userAppInfoList;
    private List<AppInfo> systemAppInfoList;

    private void setupAppLists() {
        ExpandableListView userAppListView = findViewById(R.id.user_app_list_view);
        ExpandableListView systemAppListView = findViewById(R.id.system_app_list_view);

        appAdapter[0] = new AppExpandableListAdapter(this, userAppInfoList);
        appAdapter[1] = new AppExpandableListAdapter(this, systemAppInfoList);

        userAppListView.setAdapter(appAdapter[0]);
        systemAppListView.setAdapter(appAdapter[1]);
        Button userAppsButton = findViewById(R.id.userApps);
        Button systemAppsButton = findViewById(R.id.systemApps);
        setButtonBorder(userAppsButton);
        systemAppsButton.setTextColor(textColor);
        systemAppListView.setBackgroundColor(bgColor);

        userAppsButton.setOnClickListener(v -> {
            setButtonBorder(userAppsButton);
            systemAppsButton.setBackgroundDrawable(null);
            userAppListView.setVisibility(View.VISIBLE);
            systemAppListView.setVisibility(View.GONE);
            system = false;
        });
        systemAppsButton.setOnClickListener(v -> {
            setButtonBorder(systemAppsButton);
            userAppsButton.setBackgroundDrawable(null);
            systemAppListView.setVisibility(View.VISIBLE);
            userAppListView.setVisibility(View.GONE);
            system = true;
        });

        findViewById(R.id.filterButton).setOnClickListener(v -> {
            String[] display = new String[]{"Name", "Last updated date", "First install date"};
            AlertDialog ad = new AlertDialog.Builder(this).setSingleChoiceItems(display, sortMode, (dialog, which) -> {
                Comparator<AppInfo> comparator = Comparator.comparing((AppInfo p) -> (sortMode = which) == 0 ? p.name.toLowerCase(supportsArraysCopyOfAndDownloadManager ? Locale.ROOT : Locale.getDefault()) : which == 1 ? p.lastUpdated : p.firstInstalled);
                if (which != 0) comparator = comparator.reversed();
                Collections.sort(appAdapter[0].appInfoList, comparator);
                appAdapter[0].notifyDataSetChanged();
                Collections.sort(appAdapter[1].appInfoList, comparator);
                appAdapter[1].notifyDataSetChanged();
            }).create();
            styleAlertDialog(ad, display, null);
            ad.getListView().setItemChecked(sortMode, true);
        });

        EditText searchBar = findViewById(R.id.search_bar);
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                appAdapter[system ? 1 : 0].getFilter().filter(s);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private static class AppDetailsLoaderTask extends AsyncTask<Void, Void, Void> {
        private final WeakReference<MainActivity> activityRef;
        private final PackageManager pm;

        AppDetailsLoaderTask(MainActivity activity) {
            this.activityRef = new WeakReference<>(activity);
            this.pm = activity.getPackageManager();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            MainActivity activity = activityRef.get();
            if (activity == null) return null;

            loadAdditionalDetails(activity.userAppInfoList);
            loadAdditionalDetails(activity.systemAppInfoList);

            return null;
        }

        private void loadAdditionalDetails(List<AppInfo> apps) {
            for (AppInfo app : apps) {
                try {
                    PackageInfo packageInfo = pm.getPackageInfo(app.packageName, 0);
                    app.icon = showIcon && packageInfo.applicationInfo != null ? packageInfo.applicationInfo.loadIcon(pm) : null;
                    app.firstInstalled = supportsArraysCopyOfAndDownloadManager ?
                            new Date(packageInfo.firstInstallTime).toString() : "Unknown";
                    app.lastUpdated = supportsArraysCopyOfAndDownloadManager ?
                            new Date(packageInfo.lastUpdateTime).toString() : "Unknown";
                    app.versionCode = packageInfo.versionCode;
                    app.versionName = packageInfo.versionName;
                } catch (PackageManager.NameNotFoundException ignored) {
                }
            }
        }

        @Override
        protected void onPostExecute(Void result) {
            MainActivity activity = activityRef.get();
            if (activity == null) return;

            activity.appAdapter[0].notifyDataSetChanged();
            activity.appAdapter[1].notifyDataSetChanged();
        }
    }

    private void extract(int pos) {
        try {
            AppInfo ai = appAdapter[system ? 1 : 0].filteredAppInfoList.get(pos);
            selectDirToSaveAPKOrSaveNow(ai.packageName, ai.versionName, pos);
        } catch (PackageManager.NameNotFoundException e) {
            showError(e);
        }
    }

    private void share(int pos) {
        try {
            ApplicationInfo ai = getPackageManager().getApplicationInfo(/*pkgName = */appAdapter[system ? 1 : 0].filteredAppInfoList.get(pos).packageName, 0);
            File toShare;
            boolean split = supportsSplits && ai.splitSourceDirs != null;
            if(split && antisplit) {
                toShare = new File(getCacheDir(), "t");
                try (ZipOutputStream zos = new ZipOutputStream(FileUtils.getOutputStream(toShare))) {
                    for (File f : new File(ai.sourceDir).listFiles()) {
                        String fileName = f.getName();
                        if (f.isFile() && fileName.endsWith(".apk")) {
                            zos.putNextEntry(new ZipEntry(fileName));
                            FileUtils.copyFile(f, zos);
                        }
                    }
                }
            } else toShare = new File(ai.sourceDir);
            Uri u = FileProvider.getUriForFile(this, "io.github.abdurazaaqmohammed.ApkExtractor.provider", toShare);
            String mimeType = antisplit || !split ? "application/vnd.android.package-archive" : "application/zip";
            Intent intent = new Intent(Intent.ACTION_SEND).setDataAndType(u, mimeType);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                intent.setClipData(new ClipData("aa", new String[]{mimeType}, new ClipData.Item(u)));
            }
            startActivity(intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION));
        } catch (Exception e) {
            showError(e);
        }
    }

    boolean system = false;

    public static class AppExpandableListAdapter extends BaseExpandableListAdapter {

        private final MainActivity context;
        private final List<AppInfo> appInfoList;
        private final List<AppInfo> filteredAppInfoList;

        public AppExpandableListAdapter(MainActivity context, List<AppInfo> appInfoList) {
            this.context = context;
            this.appInfoList = appInfoList;
            this.filteredAppInfoList = appInfoList;
        }

        @Override
        public int getGroupCount() {
            return filteredAppInfoList.size();
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            return 1;
        }

        @Override
        public Object getGroup(int groupPosition) {
            return filteredAppInfoList.get(groupPosition);
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            return filteredAppInfoList.get(groupPosition);
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

        AppInfoFilter filter = null;

        public Filter getFilter() {
            if (filter == null) {
                filter = new AppInfoFilter();
            }
            return filter;
        }

        private class AppInfoFilter extends Filter {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                if (constraint == null || constraint.length() == 0) {
                    results.values = new ArrayList<>(appInfoList);
                    results.count = appInfoList.size();
                } else {
                    List<AppInfo> filteredItems = new ArrayList<>();
                    String filterPattern = constraint.toString().toLowerCase().trim();
                    for (AppInfo appInfo : appInfoList) {
                        if (appInfo.name.toLowerCase().contains(filterPattern))
                            filteredItems.add(appInfo);
                    }
                    results.values = filteredItems;
                    results.count = filteredItems.size();
                }
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                filteredAppInfoList.clear();
                filteredAppInfoList.addAll((List<AppInfo>) results.values);
                notifyDataSetChanged();
            }
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            GroupViewHolder viewHolder;
            AppInfo appInfo = (AppInfo) getGroup(groupPosition);

            if (convertView == null) {
                LayoutInflater inflater = LayoutInflater.from(context);
                convertView = inflater.inflate(R.layout.list_item, parent, false);

                viewHolder = new GroupViewHolder();
                viewHolder.appName = convertView.findViewById(R.id.appName);
                viewHolder.packageNameView = convertView.findViewById(R.id.package_name_view);
                viewHolder.appIconView = convertView.findViewById(R.id.icon_view);
                //viewHolder.shareIconView = convertView.findViewById(R.id.shareApk);
                viewHolder.extractIconView = convertView.findViewById(R.id.extract);

                convertView.setTag(viewHolder);
            } else {
                viewHolder = (GroupViewHolder) convertView.getTag();
            }
            viewHolder.appName.setText(appInfo.name);
            viewHolder.packageNameView.setText(appInfo.packageName);
            viewHolder.appIconView.setImageDrawable(appInfo.icon);
            LightingColorFilter lcf = new LightingColorFilter(Color.BLACK, textColor);
            viewHolder.extractIconView.setColorFilter(lcf);
            viewHolder.extractIconView.setOnClickListener(v -> context.extract(groupPosition));
//            viewHolder.shareIconView.setColorFilter(lcf);
//            viewHolder.shareIconView.setOnClickListener(v -> context.share(groupPosition));
            return convertView;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            ChildViewHolder viewHolder;
            AppInfo appInfo = (AppInfo) getChild(groupPosition, childPosition);

            if (convertView == null) {
                LayoutInflater inflater = LayoutInflater.from(context);
                convertView = inflater.inflate(R.layout.list_item_child, parent, false);

                viewHolder = new ChildViewHolder();
                viewHolder.firstInstalledView = convertView.findViewById(R.id.first_installed_view);
                viewHolder.lastUpdatedView = convertView.findViewById(R.id.last_updated_view);
                viewHolder.btnLaunch = convertView.findViewById(R.id.btn_launch);
                viewHolder.btnUninstall = convertView.findViewById(R.id.btn_uninstall);
                viewHolder.btnShare = convertView.findViewById(R.id.btn_share);
                viewHolder.btnAntisplitShare = convertView.findViewById(R.id.btn_antisplit_share);
                viewHolder.btnInfo = convertView.findViewById(R.id.btn_info);
                viewHolder.btnExtract = convertView.findViewById(R.id.btn_extract);
                viewHolder.btnExtractAntiSplit = convertView.findViewById(R.id.btn_extract_antisplit);

                convertView.setTag(viewHolder);
            } else viewHolder = (ChildViewHolder) convertView.getTag();
            viewHolder.btnUninstall.setTextColor(textColor);
            viewHolder.btnLaunch.setTextColor(textColor);
            viewHolder.btnInfo.setTextColor(textColor);
            if (showFirstInstalled) {
                viewHolder.firstInstalledView.setVisibility(View.VISIBLE);
                viewHolder.firstInstalledView.setTextColor(textColor);
                viewHolder.firstInstalledView.setText(rss.getString(R.string.first_installed, appInfo.firstInstalled));
            } else {
                viewHolder.firstInstalledView.setVisibility(View.GONE);
            }

            if (showLastUpdate) {
                viewHolder.lastUpdatedView.setVisibility(View.VISIBLE);
                viewHolder.lastUpdatedView.setTextColor(textColor);
                viewHolder.lastUpdatedView.setText(rss.getString(R.string.last_update, appInfo.lastUpdated));
            } else {
                viewHolder.lastUpdatedView.setVisibility(View.GONE);
            }

            final String packageName = appInfo.packageName;

            viewHolder.btnLaunch.setOnClickListener(v -> {
                Intent launchIntent = context.getPackageManager().getLaunchIntentForPackage(packageName);
                if (launchIntent != null) {
                    context.startActivity(launchIntent);
                } else {
                    Toast.makeText(context, "Cannot launch this app", Toast.LENGTH_SHORT).show();
                }
            });

            viewHolder.btnUninstall.setOnClickListener(v -> {
                Intent intent = new Intent(Intent.ACTION_UNINSTALL_PACKAGE).setData(Uri.parse("package:" + packageName));
                try {
                    context.startActivity(intent);
                } catch (Exception e) {
                    context.startActivity(intent.setAction(Intent.ACTION_DELETE));
                }
            });

            viewHolder.btnExtract.setTextColor(textColor);
            viewHolder.btnExtract.setOnClickListener(v -> context.extract(groupPosition));

            viewHolder.btnShare.setTextColor(textColor);
            viewHolder.btnShare.setOnClickListener(v -> context.share(groupPosition));

            viewHolder.btnInfo.setOnClickListener(v -> context.startActivity(
                    supportsArraysCopyOfAndDownloadManager ?
                            new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS).setData(Uri.parse("package:" + packageName))
                            : new Intent(Intent.ACTION_VIEW).setClassName("com.android.settings", "com.android.settings.InstalledAppDetails")
                            .putExtra(Build.VERSION.SDK_INT == 8 ? "pkg" : "com.android.settings.ApplicationPkgName", packageName)));

            if (appInfo.isSplit) {
                viewHolder.btnAntisplitShare.setTextColor(textColor);
                viewHolder.btnExtractAntiSplit.setTextColor(textColor);
                viewHolder.btnAntisplitShare.setOnClickListener(v -> {
                    boolean realAntiSplitValue = antisplit;
                    antisplit = true;
                    context.share(groupPosition);
                    antisplit = realAntiSplitValue;
                });
                viewHolder.btnExtractAntiSplit.setOnClickListener(v -> {
                    boolean realAntiSplitValue = antisplit;
                    antisplit = true;
                    context.extract(groupPosition);
                    antisplit = realAntiSplitValue;
                });
            } else {
                viewHolder.btnAntisplitShare.setVisibility(View.GONE);
                viewHolder.btnExtractAntiSplit.setVisibility(View.GONE);
            }
            return convertView;
        }

        static class GroupViewHolder {
            TextView appName;
            TextView packageNameView;
            ImageView appIconView;
            //ImageView shareIconView;
            ImageView extractIconView;
        }

        static class ChildViewHolder {
            TextView firstInstalledView;
            TextView lastUpdatedView;
            TextView btnLaunch;
            TextView btnUninstall;
            TextView btnShare;
            TextView btnAntisplitShare;
            TextView btnInfo;
            TextView btnExtract;
            TextView btnExtractAntiSplit;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }
    }

    public void styleAlertDialog(AlertDialog ad, String[] display, ArrayAdapter adapter) {
        GradientDrawable border = new GradientDrawable();
        border.setColor(bgColor); // Background color
        border.setStroke(5, textColor); // Border width and color
        border.setCornerRadius(16);
        runOnUiThread(() -> {
            ad.show();
            ListView lv;
            if ((adapter != null || display != null) && (lv = ad.getListView()) != null)
                lv.setAdapter(adapter == null ? new CustomArrayAdapter(this, display, textColor) : adapter);
            Window w = ad.getWindow();

            Button b = ad.getButton(AlertDialog.BUTTON_POSITIVE);
            if (b != null) b.setTextColor(textColor);
            if ((b = ad.getButton(AlertDialog.BUTTON_NEGATIVE)) != null) b.setTextColor(textColor);
            if ((b = ad.getButton(AlertDialog.BUTTON_NEUTRAL)) != null) b.setTextColor(textColor);

            if (w != null) {
                View dv = w.getDecorView();
                dv.getBackground().setColorFilter(new LightingColorFilter(Color.BLACK, bgColor));
                w.setBackgroundDrawable(border);

                int padding = 16;
                dv.setPadding(padding, padding, padding, padding);
            }
        });
    }

    public static Resources rss;

    private void updateLang(Resources res, ScrollView settingsDialog) {
        rss = res;
        if (settingsDialog != null) {
            ((TextView) settingsDialog.findViewById(R.id.langPicker)).setText(res.getString(R.string.lang));
            final boolean supportsSwitch = Build.VERSION.SDK_INT > 13;
            ((TextView) settingsDialog.findViewById(supportsSwitch ? R.id.ask : R.id.askText)).setText(res.getString(R.string.ask));
            ((TextView) settingsDialog.findViewById(supportsSwitch ? R.id.signToggle : R.id.signToggleText)).setText(res.getString(R.string.sign_apk));
            ((TextView) settingsDialog.findViewById(supportsSwitch ? R.id.showIconToggle : R.id.showIconToggleText)).setText(res.getString(R.string.sign_apk));
            ((TextView) settingsDialog.findViewById(supportsSwitch ? R.id.showLastUpdateToggle : R.id.showLastUpdateToggleText))
                    .setText(rss.getString(R.string.show_last_updated));

            ((TextView) settingsDialog.findViewById(supportsSwitch ? R.id.showFirstInstallToggle : R.id.showFirstInstallToggleText))
                    .setText(rss.getString(R.string.show_first_install));

            ((TextView) settingsDialog.findViewById(supportsSwitch ? R.id.showVersionNameToggle : R.id.showVersionNameToggleText))
                    .setText(rss.getString(R.string.show_v_name));

            ((TextView) settingsDialog.findViewById(supportsSwitch ? R.id.showVersionCodeToggle : R.id.showVersionCodeToggleText))
                    .setText(rss.getString(R.string.show_v_code));

            ((TextView) settingsDialog.findViewById(supportsSwitch ? R.id.showAppNameToggle : R.id.showAppNameToggleText))
                    .setText(rss.getString(R.string.show_app));

            ((TextView) settingsDialog.findViewById(supportsSwitch ? R.id.showPkgNameToggle : R.id.showPkgNameToggleText))
                    .setText(rss.getString(R.string.show_pkg));
            ((TextView) settingsDialog.findViewById(R.id.changeTextColor)).setText(res.getString(R.string.change_text_color));
            ((TextView) settingsDialog.findViewById(R.id.changeBgColor)).setText(res.getString(R.string.change_background_color));
        }
    }

    private void showColorPickerDialog(boolean isTextColor, int currentColor, ScrollView from) {
        new AmbilWarnaDialog(this, currentColor, new AmbilWarnaDialog.OnAmbilWarnaListener() {
            @Override
            public void onOk(AmbilWarnaDialog dialog1, int color) {
                setColor(color, isTextColor, from);
                Toast.makeText(MainActivity.this, rss.getString(R.string.changes_msg), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancel(AmbilWarnaDialog dialog1) {
                // cancel was selected by the user
            }
        }).show();
    }

    final File getAppFolder() {
        final File appFolder = new File(Environment.getExternalStorageDirectory(), "APK Extractor M");
        return appFolder.exists() || appFolder.mkdir() ? appFolder : new File(Environment.getExternalStorageDirectory(), "Download");
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void checkStoragePerm() {
        if (doesNotHaveStoragePerm(this)) {
            Toast.makeText(this, rss.getString(R.string.grant_storage), Toast.LENGTH_LONG).show();
            if (LegacyUtils.supportsWriteExternalStorage)
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, -1);
            else
                startActivityForResult(new Intent(ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION, Uri.parse("package:" + getPackageName())), -1);
        }
    }

    public static boolean doesNotHaveStoragePerm(Context context) {
        if (Build.VERSION.SDK_INT < 23) return false;
        return LegacyUtils.supportsWriteExternalStorage ?
                context.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED :
                !Environment.isExternalStorageManager();
    }

    @Override
    protected void onPause() {
        SharedPreferences.Editor e = getSharedPreferences("set", Context.MODE_PRIVATE).edit()
                .putBoolean("logEnabled", logEnabled)
                .putBoolean("ask", ask)
                .putBoolean("signApk", signApk)
                .putBoolean("showIcon", showIcon)
                .putBoolean("showAppName", showAppName)
                .putBoolean("showVersionName", showVersionName)
                .putBoolean("showLastUpdate", showLastUpdate)
                .putBoolean("showVersionCode", showVersionCode)
                .putBoolean("showPackageName", showPackageName)
                .putBoolean("showAppName", showAppName)
                .putBoolean("showFirstInstalled", showFirstInstalled)
                .putBoolean("antisplit", antisplit)
                .putInt("textColor", textColor)
                .putInt("backgroundColor", bgColor)
                .putInt("sortMode", sortMode)
                .putString("lang", lang);
        if (supportsArraysCopyOfAndDownloadManager) e.apply();
        else e.commit();
        super.onPause();
    }

    private Handler handler;

    public static void deleteDir(File dir) {
        // There should never be folders in here.
        for (String child : dir.list()) new File(dir, child).delete();
    }

    @Override
    protected void onDestroy() {
        deleteDir(getCacheDir());
        super.onDestroy();
    }

    /**
     * @noinspection deprecation
     */
    static class ProcessTask extends AsyncTask<Uri, Void, Void> {
        private final WeakReference<MainActivity> activityReference;
        private final String packageNameFromAppList;

        // only retain a weak reference to the activity
        ProcessTask(MainActivity context, String fromAppList) {
            activityReference = new WeakReference<>(context);
            this.packageNameFromAppList = fromAppList;
        }

        @Override
        protected Void doInBackground(Uri... uris) {
            MainActivity activity = activityReference.get();
            if (activity == null) return null;

            final File cacheDir = activity.getCacheDir();
            deleteDir(cacheDir);

            try {
                ApplicationInfo ai = activity.getPackageManager().getPackageInfo(packageNameFromAppList, 0).applicationInfo;
                File baseApk = new File(ai.sourceDir);
                File apkDirectory = baseApk.getParentFile();
                boolean split = canSetNotificationBarTransparent && ai.splitSourceDirs != null;
                if (split && antisplit) try (ApkBundle bundle = new ApkBundle()) {
                    bundle.loadApkDirectory(apkDirectory, false, activity);
                    Merger.run(bundle, cacheDir, uris[0], activity, signApk);
                }
                else try (OutputStream os = FileUtils.getOutputStream(uris[0], activity)) {
                    if (split) try (ZipOutputStream zos = new ZipOutputStream(os)) {
                        for (File f : apkDirectory.listFiles()) {
                            String fileName = f.getName();
                            if (f.isFile() && fileName.endsWith(".apk")) {
                                zos.putNextEntry(new ZipEntry(fileName));
                                FileUtils.copyFile(f, zos);
                            }
                        }
                    }
                    else FileUtils.copyFile(baseApk, os);
                }
            } catch (Exception e) {
                activity.showError(e);
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            MainActivity activity = activityReference.get();
            activity.showSuccess();
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) if (requestCode == -1) {
            checkStoragePerm();
        } else {// going to process and save a file now
            new ProcessTask(this, appAdapter[system ? 1 : 0].filteredAppInfoList.get(requestCode).packageName).execute(data.getData());
        }
    }

    private void showSuccess() {
        if (errorOccurred) return;
        final String success = rss.getString(R.string.success_saved);
        LogUtil.logMessage(success);
        runOnUiThread(() -> Toast.makeText(this, success, Toast.LENGTH_SHORT).show());
    }

    private void copyText(CharSequence text) {
        ((android.text.ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE)).setText(text);
        Toast.makeText(this, rss.getString(R.string.copied_log), Toast.LENGTH_SHORT).show();
    }

    private void showError(Exception e) {
        if (!(e instanceof ClosedByInterruptException)) {
            final String mainErr = e.toString();
            errorOccurred = !mainErr.equals(rss.getString(R.string.sign_failed));

            StringBuilder stackTrace = new StringBuilder().append(mainErr).append('\n');
            for (StackTraceElement line : e.getStackTrace()) stackTrace.append(line).append('\n');
            AlertDialog.Builder b = new AlertDialog.Builder(this)
                    .setNegativeButton(rss.getString(R.string.cancel), null)
                    .setPositiveButton(rss.getString(R.string.create_issue), (dialog, which) -> startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/AbdurazaaqMohammed/AntiSplit-M/issues/new?title=Crash%20Report&body=" + stackTrace))))
                    .setNeutralButton(rss.getString(R.string.copy_log), (dialog, which) -> copyText(stackTrace));
            runOnUiThread(() -> {
                TextView title = new TextView(this);
                title.setText(mainErr);
                title.setTextColor(textColor);
                title.setTextSize(20);

                TextView msg = new TextView(this);
                msg.setText(stackTrace);
                msg.setTextColor(textColor);
                ScrollView sv = new ScrollView(this);
                sv.setBackgroundColor(bgColor);
                msg.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, (int) (rss.getDisplayMetrics().heightPixels * 0.6)));
                sv.addView(msg);
                styleAlertDialog(b.setCustomTitle(title).setView(sv).create(), null, null);
            });
        }
    }

    public static String getOriginalFileName(Context context, Uri uri, boolean isSplit) {
        String result = null;
        try {
            if (Objects.equals(uri.getScheme(), "content")) {
                try (Cursor cursor = context.getContentResolver().query(uri, null, null, null, null)) {
                    if (cursor != null && cursor.moveToFirst()) {
                        result = cursor.getString(cursor.getColumnIndexOrThrow(OpenableColumns.DISPLAY_NAME));
                    }
                }
            }
            if (result == null) {
                result = uri.getPath();
                int cut = Objects.requireNonNull(result).lastIndexOf('/'); // Ensure it throw the NullPointerException here to be caught
                if (cut != -1) result = result.substring(cut + 1);
            }
            LogUtil.logMessage(result);
            return supportsFileChannel && isSplit && antisplit
                    //&& context.getPackageManager().getApplicationInfo(pkgName, 0).splitNames != null
                    ? result.replaceFirst("\\.(?:xapk|aspk|apk[sm])", "_antisplit.apk") : result;
        } catch (Exception ignored) {
            return "filename_not_found";
        }
    }

    @SuppressLint("InlinedApi")
    private void selectDirToSaveAPKOrSaveNow(String pkgName, String versionName, int pos) throws PackageManager.NameNotFoundException {
        if (ask) {
            startActivityForResult(new Intent(Intent.ACTION_CREATE_DOCUMENT)
                    .addCategory(Intent.CATEGORY_OPENABLE)
                    .setType(antisplit || getPackageManager().getApplicationInfo(pkgName, 0).splitNames == null ? "application/vnd.android.package-archive" : "application/zip")
                    .putExtra(Intent.EXTRA_TITLE, pkgName + '_' + versionName), pos);
        } else {
            checkStoragePerm();
            new ProcessTask(this, pkgName).execute(Uri.fromFile(new File(getAppFolder(), pkgName)));
        }
    }
}