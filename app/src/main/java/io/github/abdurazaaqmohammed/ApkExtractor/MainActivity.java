package io.github.abdurazaaqmohammed.ApkExtractor;

import static android.provider.Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION;
import static io.github.abdurazaaqmohammed.ApkExtractor.LegacyUtils.supportsActionBar;
import static io.github.abdurazaaqmohammed.ApkExtractor.LegacyUtils.supportsArraysCopyOfAndDownloadManager;
import static io.github.abdurazaaqmohammed.ApkExtractor.LegacyUtils.supportsSplits;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LightingColorFilter;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
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
import android.provider.Settings;
import android.support.v4.content.FileProvider;
import android.text.ClipboardManager;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
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
import com.reandroid.apkeditor.merge.Merger;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import com.github.paul035.LocaleHelper;
import com.starry.FileUtils;

import yuku.ambilwarna.AmbilWarnaDialog;

/** @noinspection deprecation, rawtypes */
public class MainActivity extends Activity {
    private static boolean ask;
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
    public static boolean showExtractIcon;
    public static boolean showExtractRes;
    public static boolean showExtractDex;
    public static boolean showExtractManifest;
    public static boolean showExtractBase;
    public static boolean showExtractSplit;
    public static boolean showExtractLibs;
    private final AppExpandableListAdapter[] appAdapter = new AppExpandableListAdapter[2];
    private RunUtil runUtil;

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
                ((TextView) settingsMenu.findViewById(supportsSwitch ? R.id.showExtractIconToggle : R.id.showExtractIconToggleText)).setTextColor(color);
                ((TextView) settingsMenu.findViewById(supportsSwitch ? R.id.showExtractResToggle : R.id.showExtractResToggleText)).setTextColor(color);
                ((TextView) settingsMenu.findViewById(supportsSwitch ? R.id.showExtractDexToggle : R.id.showExtractDexToggleText)).setTextColor(color);
                ((TextView) settingsMenu.findViewById(supportsSwitch ? R.id.showExtractManifestToggle : R.id.showExtractManifestToggleText)).setTextColor(color);
                ((TextView) settingsMenu.findViewById(supportsSwitch ? R.id.showExtractBaseToggle : R.id.showExtractBaseToggleText)).setTextColor(color);
                ((TextView) settingsMenu.findViewById(supportsSwitch ? R.id.showExtractSplitToggle : R.id.showExtractSplitToggleText)).setTextColor(color);
                ((TextView) settingsMenu.findViewById(supportsSwitch ? R.id.showExtractLibsToggle : R.id.showExtractLibsToggleText)).setTextColor(color);

                ((TextView) settingsMenu.findViewById(R.id.displayFuncTitle)).setTextColor(color);
                ((TextView) settingsMenu.findViewById(R.id.displayInfoTitle)).setTextColor(color);
            }
        } else findViewById(R.id.main).setBackgroundColor(bgColor = color);
        ((ListView) findViewById(R.id.user_app_list_view)).invalidateViews();
        ActionBar ab;
        if (supportsActionBar && (ab = getActionBar()) != null) {
            if(isTextColor) {
                Spannable text = new SpannableString(getString(R.string.app_name));
                text.setSpan(new ForegroundColorSpan(textColor), 0, text.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                ab.setTitle(text);
            } else ab.setBackgroundDrawable(new ColorDrawable(bgColor));
        }
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
                setButtonBorder(settingsMenu.findViewById(R.id.showExtractIconToggle));
                setButtonBorder(settingsMenu.findViewById(R.id.showExtractResToggle));
                setButtonBorder(settingsMenu.findViewById(R.id.showExtractDexToggle));
                setButtonBorder(settingsMenu.findViewById(R.id.showExtractManifestToggle));
                setButtonBorder(settingsMenu.findViewById(R.id.showExtractBaseToggle));
                setButtonBorder(settingsMenu.findViewById(R.id.showExtractSplitToggle));
                setButtonBorder(settingsMenu.findViewById(R.id.showExtractLibsToggle));
            }
        }
    }

//    @Override
//    public void onBackPressed() {
//        AppExpandableListAdapter adapter = getCurrentAdapter();
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU || adapter.selectedItems.isEmpty()) super.onBackPressed();
//        else adapter.selectedItems.forEach(adapter::toggleSelection);
//    }
    public static int sortMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        new AppLoaderTask(this).execute();
        super.onCreate(savedInstanceState);
        runUtil = new RunUtil(new Handler(Looper.getMainLooper()), this);

        deleteDir(getCacheDir());
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
//            getOnBackInvokedDispatcher() .registerOnBackInvokedCallback(999, () -> {
//                AppExpandableListAdapter adapter = getCurrentAdapter();
//                if(!adapter.selectedItems.isEmpty()) adapter.selectedItems.forEach(adapter::toggleSelection);
//            });
//        }
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
        ask = settings.getBoolean("ask", true);
        showIcon = settings.getBoolean("showIcon", true);
        showFirstInstalled = settings.getBoolean("showFirstInstalled", true);
        showAppName = settings.getBoolean("showAppName", true);
        showLastUpdate = settings.getBoolean("showLastUpdate", true);
        showPackageName = settings.getBoolean("showPackageName", true);
        showVersionCode = settings.getBoolean("showVersionCode", true);
        showVersionName = settings.getBoolean("showVersionName", true);
        showExtractIcon = settings.getBoolean("showExtractIcon", false);
        showExtractBase = settings.getBoolean("showExtractBase", false);
        showExtractDex = settings.getBoolean("showExtractDex", false);
        showExtractLibs = settings.getBoolean("showExtractLibs", false);
        showExtractRes = settings.getBoolean("showExtractRes", false);
        showExtractManifest = settings.getBoolean("showExtractManifest", false);
        showExtractSplit = settings.getBoolean("showExtractSplit", false);
        antisplit = settings.getBoolean("antisplit", false);
        sortMode = settings.getInt("sortMode", 0);

        lang = settings.getString("lang", "en");
        if (Objects.equals(lang, Locale.getDefault().getLanguage())) rss = getResources();
        else updateLang(LocaleHelper.setLocale(MainActivity.this, lang).getResources(), null);

        findViewById(R.id.settingsButton).setOnClickListener(v -> {
            ScrollView settingsMenu = (ScrollView) LayoutInflater.from(this).inflate(R.layout.dialog_settings, null);
            settingsMenu.setBackgroundColor(bgColor);

            setColor(textColor, true, settingsMenu);

            ((TextView) settingsMenu.findViewById(R.id.langPicker)).setText(rss.getString(R.string.lang));
            boolean supportsSwitch = Build.VERSION.SDK_INT > 13;
            ((TextView) settingsMenu.findViewById(supportsSwitch ? R.id.ask : R.id.askText)).setText(rss.getString(R.string.ask));
            ((TextView) settingsMenu.findViewById(supportsSwitch ? R.id.signToggle : R.id.signToggleText)).setText(rss.getString(R.string.sign_apk));
            ((TextView) settingsMenu.findViewById(supportsSwitch ? R.id.showIconToggle : R.id.showIconToggleText)).setText(rss.getString(R.string.show_icons));
            ((TextView) settingsMenu.findViewById(supportsSwitch ? R.id.showLastUpdateToggle : R.id.showLastUpdateToggleText))
                    .setText(rss.getString(R.string.show_last_updated));

            ((TextView) settingsMenu.findViewById(supportsSwitch ? R.id.showFirstInstallToggle : R.id.showFirstInstallToggleText))
                    .setText(rss.getString(R.string.show_first_install));

            ((TextView) settingsMenu.findViewById(supportsSwitch ? R.id.showVersionNameToggle : R.id.showVersionNameToggleText))
                    .setText(rss.getString(R.string.show_v_name));

            ((TextView) settingsMenu.findViewById(supportsSwitch ? R.id.showVersionCodeToggle : R.id.showVersionCodeToggleText))
                    .setText(rss.getString(R.string.show_v_code));

            ((TextView) settingsMenu.findViewById(supportsSwitch ? R.id.showAppNameToggle : R.id.showAppNameToggleText))
                    .setText(rss.getString(R.string.show_app));

            ((TextView) settingsMenu.findViewById(supportsSwitch ? R.id.showPkgNameToggle : R.id.showPkgNameToggleText))
                    .setText(rss.getString(R.string.show_pkg));
            ((TextView) settingsMenu.findViewById(R.id.displayFuncTitle)).setText(rss.getString(R.string.display_functions));
            ((TextView) settingsMenu.findViewById(R.id.displayInfoTitle)).setText(rss.getString(R.string.display_nfo));
            ((TextView) settingsMenu.findViewById(supportsSwitch ? R.id.showExtractIconToggle : R.id.showExtractIconToggleText)).setText(rss.getString(R.string.extract_icon, rss.getString(R.string.show)));
            ((TextView) settingsMenu.findViewById(supportsSwitch ? R.id.showExtractResToggle : R.id.showExtractResToggleText)).setText(rss.getString(R.string.extract_res, rss.getString(R.string.show)));
            ((TextView) settingsMenu.findViewById(supportsSwitch ? R.id.showExtractDexToggle : R.id.showExtractDexToggleText)).setText(rss.getString(R.string.extract_dex, rss.getString(R.string.show)));
            ((TextView) settingsMenu.findViewById(supportsSwitch ? R.id.showExtractManifestToggle : R.id.showExtractManifestToggleText)).setText(rss.getString(R.string.extract_manifest, rss.getString(R.string.show)));
            ((TextView) settingsMenu.findViewById(supportsSwitch ? R.id.showExtractBaseToggle : R.id.showExtractBaseToggleText)).setText(rss.getString(R.string.extract_base, rss.getString(R.string.show)));
            ((TextView) settingsMenu.findViewById(supportsSwitch ? R.id.showExtractSplitToggle : R.id.showExtractSplitToggleText)).setText(rss.getString(R.string.choose_split, rss.getString(R.string.show)));
            ((TextView) settingsMenu.findViewById(supportsSwitch ? R.id.showExtractLibsToggle : R.id.showExtractLibsToggleText)).setText(rss.getString(R.string.extract_libs, rss.getString(R.string.show)));

            ((TextView) settingsMenu.findViewById(R.id.changeTextColor)).setText(rss.getString(R.string.change_text_color));
            ((TextView) settingsMenu.findViewById(R.id.changeBgColor)).setText(rss.getString(R.string.change_background_color));

            CompoundButton signToggle = settingsMenu.findViewById(R.id.signToggle);
            signToggle.setChecked(signApk);
            signToggle.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if(isChecked) {
                    TextView title = new TextView(this);
                    title.setText(rss.getString(R.string.warning));
                    title.setTextColor(textColor);
                    title.setTextSize(25);
                    TextView msg = new TextView(this);
                    msg.setText(rss.getString(R.string.warn_sign));
                    msg.setTextColor(textColor);
                    msg.setTextSize(20);
                    styleAlertDialog(new AlertDialog.Builder(this)
                        .setCustomTitle(title)
                        .setView(msg)
                        .setNegativeButton(rss.getString(R.string.cancel), (dialog, which) -> {
                            signToggle.setChecked(signApk = false);
                            dialog.dismiss();
                        })
                        .setPositiveButton("OK", (dialog, which) -> {
                            signApk = true;
                            dialog.dismiss();
                        })
                        .create(), null, null);
                }
            });

            CompoundButton antisplitToggle = settingsMenu.findViewById(R.id.antisplitToggle);
            if (supportsSplits) {
                antisplitToggle.setChecked(antisplit);
                antisplitToggle.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    antisplit = isChecked;
                    signToggle.setVisibility(isChecked ? View.VISIBLE : View.GONE);
                });
            } else {
                antisplit = false;
                antisplitToggle.setVisibility(View.GONE);
                if (!supportsSwitch) {
                    settingsMenu.findViewById(R.id.signToggleText).setVisibility(View.GONE);
                    settingsMenu.findViewById(R.id.antisplitText).setVisibility(View.GONE);
                }
            }
            signToggle.setVisibility(antisplit ? View.VISIBLE : View.GONE);

            CompoundButton showIconToggle = settingsMenu.findViewById(R.id.showIconToggle);
            showIconToggle.setChecked(showIcon);
            showIconToggle.setOnCheckedChangeListener((buttonView, isChecked) -> {
                showIcon = isChecked;
                reloadListView();
            });

            CompoundButton showLastUpdateToggle = settingsMenu.findViewById(R.id.showLastUpdateToggle);
            showLastUpdateToggle.setChecked(showLastUpdate);
            showLastUpdateToggle.setOnCheckedChangeListener((buttonView, isChecked) -> {
                showLastUpdate = isChecked;
                reloadListView();
            });

            CompoundButton showFirstInstallToggle = settingsMenu.findViewById(R.id.showFirstInstallToggle);
            showFirstInstallToggle.setChecked(showFirstInstalled);
            showFirstInstallToggle.setOnCheckedChangeListener((buttonView, isChecked) -> {
                showFirstInstalled = isChecked;
                reloadListView();
            });

            CompoundButton showVersionCodeToggle = settingsMenu.findViewById(R.id.showVersionCodeToggle);
            showVersionCodeToggle.setChecked(showVersionCode);
            showVersionCodeToggle.setOnCheckedChangeListener((buttonView, isChecked) -> {
                showVersionCode = isChecked;
                reloadListView();
            });

            CompoundButton showVersionNameToggle = settingsMenu.findViewById(R.id.showVersionNameToggle);
            showVersionNameToggle.setChecked(showVersionName);
            showVersionNameToggle.setOnCheckedChangeListener((buttonView, isChecked) -> {
                showVersionName = isChecked;
                reloadListView();
            });

            CompoundButton showAppNameToggle = settingsMenu.findViewById(R.id.showAppNameToggle);
            showAppNameToggle.setChecked(showAppName);
            showAppNameToggle.setOnCheckedChangeListener((buttonView, isChecked) -> {
                showAppName = isChecked;
                reloadListView();
            });

            CompoundButton showPkgNameToggle = settingsMenu.findViewById(R.id.showPkgNameToggle);
            showPkgNameToggle.setChecked(showPackageName);
            showPkgNameToggle.setOnCheckedChangeListener((buttonView, isChecked) -> {
                showPackageName = isChecked;
                reloadListView();
            });

            CompoundButton askSwitch = settingsMenu.findViewById(R.id.ask);
            if (LegacyUtils.doesNotSupportInbuiltAndroidFilePicker) {
                ask = false;
                askSwitch.setVisibility(View.GONE);
                if(!supportsSwitch) settingsMenu.findViewById(R.id.askText).setVisibility(View.GONE);
            } else {
                askSwitch.setChecked(ask);
                askSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    ask = isChecked;
                    if (!isChecked) checkStoragePerm();
                });
            }

            CompoundButton showExtractIconToggle = settingsMenu.findViewById(R.id.showExtractIconToggle);
            showExtractIconToggle.setChecked(showExtractIcon);
            showExtractIconToggle.setOnCheckedChangeListener((buttonView, isChecked) -> {
                showExtractIcon = isChecked;
                reloadListView();
            });

            CompoundButton showExtractResToggle = settingsMenu.findViewById(R.id.showExtractResToggle);
            showExtractResToggle.setChecked(showExtractRes);
            showExtractResToggle.setOnCheckedChangeListener((buttonView, isChecked) -> {
                showExtractRes = isChecked;
                reloadListView();
            });

            CompoundButton showExtractDexToggle = settingsMenu.findViewById(R.id.showExtractDexToggle);
            showExtractDexToggle.setChecked(showExtractDex);
            showExtractDexToggle.setOnCheckedChangeListener((buttonView, isChecked) -> {
                showExtractDex = isChecked;
                reloadListView();
            });

            CompoundButton showExtractBaseToggle = settingsMenu.findViewById(R.id.showExtractBaseToggle);
            showExtractBaseToggle.setChecked(showExtractBase);
            showExtractBaseToggle.setOnCheckedChangeListener((buttonView, isChecked) -> {
                showExtractBase = isChecked;
                reloadListView();
            });

            CompoundButton showExtractManifestToggle = settingsMenu.findViewById(R.id.showExtractManifestToggle);
            showExtractManifestToggle.setChecked(showExtractManifest);
            showExtractManifestToggle.setOnCheckedChangeListener((buttonView, isChecked) -> {
                showExtractManifest = isChecked;
                reloadListView();
            });

            CompoundButton showExtractSplitToggle = settingsMenu.findViewById(R.id.showExtractSplitToggle);
            showExtractSplitToggle.setChecked(showExtractSplit);
            showExtractSplitToggle.setOnCheckedChangeListener((buttonView, isChecked) -> {
                showExtractSplit = isChecked;
                reloadListView();
            });

            CompoundButton showExtractLibsToggle = settingsMenu.findViewById(R.id.showExtractLibsToggle);
            showExtractLibsToggle.setChecked(showExtractLibs);
            showExtractLibsToggle.setOnCheckedChangeListener((buttonView, isChecked) -> {
                showExtractLibs = isChecked;
                reloadListView();
            });

            Button langPicker = settingsMenu.findViewById(R.id.langPicker);
            setButtonBorder(langPicker);
            langPicker.setOnClickListener(v2 -> {
                String[] langs = rss.getStringArray(R.array.langs);
                String[] display = rss.getStringArray(R.array.langs_display);

                AlertDialog ad = new AlertDialog.Builder(this).setSingleChoiceItems(display, -1, (dialog, which) -> {
                    updateLang(LocaleHelper.setLocale(MainActivity.this, lang = langs[which]).getResources(), settingsMenu);
                    dialog.dismiss();
                }).create();
                styleAlertDialog(ad, display, null);
                for (int i = 0; i < langs.length; i++) {
                    if (langs[i].equals(lang)) {
                        ad.getListView().setItemChecked(i, true);
                        break;
                    }
                }
            });

            settingsMenu.findViewById(R.id.changeBgColor).setOnClickListener(v3 -> showColorPickerDialog(false, bgColor, settingsMenu));
            settingsMenu.findViewById(R.id.changeTextColor).setOnClickListener(v4 -> showColorPickerDialog(true, textColor, settingsMenu));
            TextView title = new TextView(this);
            title.setText(rss.getString(R.string.settings));
            title.setTextColor(textColor);
            title.setTextSize(25);
            styleAlertDialog(
                    new AlertDialog.Builder(this).setCustomTitle(title).setView(settingsMenu)
                            .setPositiveButton(rss.getString(R.string.close), (dialog, which) -> dialog.dismiss()).create(), null, null);
        });
    }

    private void reloadListView() {
        ((ListView) findViewById(system ? R.id.user_app_list_view : R.id.system_app_list_view)).invalidateViews();
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

            for (ApplicationInfo app : apps) try {
                // Create AppInfo object with minimal data first
                AppInfo appInfo = new AppInfo(
                        app.loadLabel(pm).toString(),
                        null,
                        app.packageName,
                        app.enabled,
                        supportsSplits && app.splitSourceDirs != null,
                        "", // Will be populated later
                        "",
                        0,
                        ""
                );
                ((app.flags & ApplicationInfo.FLAG_SYSTEM) != 0 ? systemApps : userApps).add(appInfo);
            } catch (Exception ignored) {}

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
        userAppListView.setOnItemLongClickListener((parent, view, position, id) -> {
            long packedPosition = userAppListView.getExpandableListPosition(position);

            if (ExpandableListView.getPackedPositionType(packedPosition) == ExpandableListView.PACKED_POSITION_TYPE_GROUP) {
                findViewById(R.id.confirmButton).setVisibility(View.VISIBLE);
                appAdapter[0].toggleSelection(ExpandableListView.getPackedPositionGroup(packedPosition));
                userAppListView.setOnGroupClickListener((parent1, view1, position1, id1) -> {appAdapter[0].toggleSelection(position1); return true;});
                return true;
            }
            return false;
        });
        appAdapter[1] = new AppExpandableListAdapter(this, systemAppInfoList);
        systemAppListView.setOnItemLongClickListener((parent, view, position, id) -> {
            long packedPosition = systemAppListView.getExpandableListPosition(position);

            if (ExpandableListView.getPackedPositionType(packedPosition) == ExpandableListView.PACKED_POSITION_TYPE_GROUP) {
                findViewById(R.id.confirmButton).setVisibility(View.VISIBLE);
                appAdapter[1].toggleSelection(ExpandableListView.getPackedPositionGroup(packedPosition));
                systemAppListView.setOnGroupClickListener((parent1, view1, position1, id1) -> {appAdapter[1].toggleSelection(position1); return true;});
                return true;
            }
            return false;
        });
        findViewById(R.id.confirmButton).setOnClickListener(v -> {
            v.setVisibility(View.INVISIBLE);
            systemAppListView.setOnGroupClickListener(null);
            userAppListView.setOnGroupClickListener(null);
            String[] display = {"Extract APKs",
                    "Share APKs",
                    "Extract resources.arsc files",
                    "Extract classes.dex files",
                    "Extract AndroidManifest.xml files",
                    "Extract base.apk files",
                    "Extract libs", // Aargh
                   // "Extract /res", // Need to extract from splits again
                    "Extract app icon"};
            styleAlertDialog(new AlertDialog.Builder(this).setSingleChoiceItems(display, -1, (dialog, which) -> {
                AppExpandableListAdapter adapter = ask ? null : getCurrentAdapter();
                switch (which) {
                    case 0:
                        if (ask) MainActivity.this.startActivityForResult(
                                new Intent(Intent.ACTION_CREATE_DOCUMENT)
                                        .addCategory(Intent.CATEGORY_OPENABLE)
                                        .setType("application/zip")
                                        .putExtra(Intent.EXTRA_TITLE, "Extracted APKs.zip")
                                , 5007);
                        else runUtil.runInBackground(() -> {
                            adapter.selectedItems.forEach(integer -> {
                                AppInfo ai = adapter.filteredAppInfoList.get(integer);
                                String packageName = ai.packageName;
                                try {
                                    FileUtils.copyFile(new File(getPackageManager().getPackageInfo(packageName, 0).applicationInfo.sourceDir), new File(getAppFolder(), packageName + "_v" + ai.versionName + ".apk"));
                                } catch (Exception e) {
                                    showError(e);
                                }
                            });
                            return errorOccurred;
                        });
                        break;
                    case 1:
                        ArrayList<Uri> fileUris = new ArrayList<>();
                        try {
                            for (Integer selectedItem : adapter.selectedItems)
                                fileUris.add(FileProvider.getUriForFile(this, "io.github.abdurazaaqmohammed.ApkExtractor.provider", new File(getPackageManager().getPackageInfo(adapter.filteredAppInfoList.get(selectedItem).packageName, 0).applicationInfo.sourceDir)));
                        } catch (PackageManager.NameNotFoundException ignored) {}

                        startActivity(new Intent(Intent.ACTION_SEND_MULTIPLE)
                                .setType("application/vnd.android.package-archive")
                                .putParcelableArrayListExtra(Intent.EXTRA_STREAM, fileUris));
                        break;
                    case 2:
                        String thing = "resources.arsc";
                        if (ask) MainActivity.this.startActivityForResult(
                                new Intent(Intent.ACTION_CREATE_DOCUMENT)
                                        .addCategory(Intent.CATEGORY_OPENABLE)
                                        .setType("application/zip")
                                        .putExtra(Intent.EXTRA_TITLE, (CharSequence) new StringBuilder(thing).append(".zip"))
                                , 5002);
                        else runUtil.runInBackground(() -> {
                            File outputDir = MainActivity.this.getAppFolder();
                            adapter.selectedItems.forEach(integer -> {
                                AppInfo ai = adapter.filteredAppInfoList.get(integer);
                                String packageName = ai.packageName;
                                String dir;
                                try (ZipFile zf = new ZipFile(dir = MainActivity.this.getPackageManager().getPackageInfo(packageName, 0).applicationInfo.sourceDir);
                                     InputStream is = zf.getInputStream(zf.getEntry(thing))) {
                                    FileUtils.copyFile(is, FileUtils.getOutputStream(new File(outputDir, packageName + "_v" + ai.versionName + '_' + new File(dir).getName() + '_' + thing)));
                                } catch (Exception e) {
                                    MainActivity.this.showError(e);
                                }
                            });
                            return errorOccurred;
                        });
                        break;
                    case 3:
                        String classes = "classes";
                        String dex = ".dex";
                        if (ask) MainActivity.this.startActivityForResult(
                                new Intent(Intent.ACTION_CREATE_DOCUMENT)
                                        .addCategory(Intent.CATEGORY_OPENABLE)
                                        .setType("application/zip")
                                        .putExtra(Intent.EXTRA_TITLE, (CharSequence) new StringBuilder(classes).append(dex).append(".zip"))
                                , 5003);
                        else runUtil.runInBackground(() -> {
                            File outputDir = MainActivity.this.getAppFolder();
                            adapter.selectedItems.forEach(integer -> {
                                AppInfo ai = adapter.filteredAppInfoList.get(integer);
                                String packageName = ai.packageName;
                                try (ZipFile zf = new ZipFile(MainActivity.this.getPackageManager().getPackageInfo(packageName, 0).applicationInfo.sourceDir)) {
                                    int i = 2;
                                    ZipEntry curr = new ZipEntry(classes + dex);
                                    try (InputStream is = zf.getInputStream(curr)) {
                                        FileUtils.copyFile(is, new File(outputDir, packageName + "_v" + ai.versionName + '_' + curr));
                                    }

                                    while ((curr = zf.getEntry(classes + i + dex)) != null) {
                                        i++;
                                        try (InputStream is = zf.getInputStream(curr)) {
                                            FileUtils.copyFile(is, new File(outputDir, packageName + "_v" + ai.versionName + '_' + curr));
                                        }
                                    }
                                } catch (Exception e) {
                                    MainActivity.this.showError(e);
                                }
                            });
                            return errorOccurred;
                        });
                        break;
                    case 4:
                        String am = "AndroidManifest.xml";
                        if (ask) MainActivity.this.startActivityForResult(
                                new Intent(Intent.ACTION_CREATE_DOCUMENT)
                                        .addCategory(Intent.CATEGORY_OPENABLE)
                                        .setType("application/zip")
                                        .putExtra(Intent.EXTRA_TITLE, (CharSequence) new StringBuilder(am).append(".zip"))
                                , 5004);
                        else runUtil.runInBackground(() -> {
                            File outputDir = MainActivity.this.getAppFolder();
                            ZipEntry amEntry = new ZipEntry(am);
                            adapter.selectedItems.forEach(integer -> {
                                AppInfo ai = adapter.filteredAppInfoList.get(integer);
                                String packageName = ai.packageName;
                                try (ZipFile zf = new ZipFile(MainActivity.this.getPackageManager().getPackageInfo(packageName, 0).applicationInfo.sourceDir);
                                     InputStream is = zf.getInputStream(amEntry)) {
                                    FileUtils.copyFile(is, new File(outputDir, packageName + "_v" + ai.versionName + '_' + am));
                                } catch (Exception e) {
                                    MainActivity.this.showError(e);
                                }
                            });
                            return errorOccurred;
                        });
                        break;
                    case 5:
                        if (ask) MainActivity.this.startActivityForResult(
                                new Intent(Intent.ACTION_CREATE_DOCUMENT)
                                        .addCategory(Intent.CATEGORY_OPENABLE)
                                        .setType("application/zip")
                                        .putExtra(Intent.EXTRA_TITLE, "base.apk.zip")
                                , 5005);
                        else runUtil.runInBackground(() -> {
                            adapter.selectedItems.forEach(integer -> {
                                AppInfo ai = adapter.filteredAppInfoList.get(integer);
                                String packageName = ai.packageName;
                                try {
                                    FileUtils.copyFile(new File(MainActivity.this.getPackageManager().getPackageInfo(packageName, 0).applicationInfo.sourceDir), new File(MainActivity.this.getAppFolder(), packageName + "_v" + ai.versionName + '_' + "base.apk"));
                                } catch (Exception e) {
                                    MainActivity.this.showError(e);
                                }
                            });
                            return errorOccurred;
                        });
                        break;
                    case 6:
                        if (ask) startActivityForResult(
                                new Intent(Intent.ACTION_CREATE_DOCUMENT)
                                        .addCategory(Intent.CATEGORY_OPENABLE)
                                        .setType("application/zip")
                                        .putExtra(Intent.EXTRA_TITLE, "libs.zip")
                                , 5006);
                        else runUtil.runInBackground(() -> {
                            adapter.selectedItems.forEach(integer -> {
                                AppInfo ai = adapter.filteredAppInfoList.get(integer);
                                String packageName = ai.packageName;
                                try {
                                    for(File f : new File(getPackageManager().getPackageInfo(packageName, 0).applicationInfo.sourceDir).listFiles())
                                        if(f.getName().endsWith(".apk")) try (ZipFile zf = new ZipFile(f)) {
                                            Enumeration<? extends ZipEntry> entries = zf.entries();
                                            String prefix = "lib/";

                                            while (entries.hasMoreElements()) {
                                                ZipEntry ze = entries.nextElement();
                                                String ds = ze.getName();
                                                if (ds.startsWith(prefix))
                                                    try (InputStream is = zf.getInputStream(ze)) {
                                                        FileUtils.copyFile(is, new File(getAppFolder(), packageName + "_v" + ai.versionName + '_' + f.getName().replace(prefix, "").replace('/', '_') + '_' + ds));
                                                    }
                                            }
                                        } catch (Exception e) {
                                            showError(e);
                                        }
                                } catch (PackageManager.NameNotFoundException ignored) {}
                            });
                            return errorOccurred;
                        });
                        break;
//                    case 7:
//                        String filename = "res.zip";
//                        if (ask) startActivityForResult(
//                                new Intent(Intent.ACTION_CREATE_DOCUMENT)
//                                        .addCategory(Intent.CATEGORY_OPENABLE)
//                                        .setType("application/zip")
//                                        .putExtra(Intent.EXTRA_TITLE, filename)
//                                , 5008);
//                        else runUtil.runInBackground(() -> {
//                            adapter.selectedItems.forEach(integer -> {
//                                AppInfo ai = adapter.filteredAppInfoList.get(integer);
//                                String packageName = ai.packageName;
//                                File outputFile = new File(MainActivity.this.getAppFolder(), packageName + "_v" + ai.versionName + '_' + filename);
//                                try (ZipFile zf = new ZipFile(MainActivity.this.getPackageManager().getPackageInfo(packageName, 0).applicationInfo.sourceDir);
//                                OutputStream os = FileUtils.getOutputStream(outputFile); ZipOutputStream zipOutputStream = new ZipOutputStream(os)) {
//                                    Enumeration<? extends ZipEntry> entries = zf.entries();
//
//                                    while (entries.hasMoreElements()) {
//                                        ZipEntry ze = entries.nextElement();
//                                        String fileName = ze.getName();
//                                        if (fileName.startsWith("res/"))
//                                            try (InputStream is = zf.getInputStream(ze)) {
//                                                zipOutputStream.putNextEntry(new ZipEntry(packageName + '_' + fileName));
//                                                FileUtils.copyFile(is, zipOutputStream);
//                                            }
//                                    }
//                                } catch (Exception e) {
//                                    MainActivity.this.showError(e);
//                                }
//                            });
//                            return null;
//                        });
//                        break;
                    case 7:
                        if (ask) MainActivity.this.startActivityForResult(
                                new Intent(Intent.ACTION_CREATE_DOCUMENT)
                                        .addCategory(Intent.CATEGORY_OPENABLE)
                                        .setType("application/zip")
                                        .putExtra(Intent.EXTRA_TITLE, "icons.zip")
                                , 5009);
                        else runUtil.runInBackground(() -> {
                            adapter.selectedItems.forEach(integer -> {
                                AppInfo ai = adapter.filteredAppInfoList.get(integer);
                                String packageName = ai.packageName;
                                PackageManager pm = getPackageManager();
                                try (OutputStream os = FileUtils.getOutputStream(new File(this.getAppFolder(), packageName + "_v" + ai.versionName + '_' + "icon.png"))) {
                                    ApplicationInfo applicationInfo = pm.getPackageInfo(packageName, 0).applicationInfo;
                                    Bitmap bm = drawableToBitmap(applicationInfo.loadIcon(pm));
                                    bm.compress(Bitmap.CompressFormat.PNG, 100, os);
                                } catch (Exception e) {
                                    showError(e);
                                }
                            });
                            return errorOccurred;
                        });
                        break;
                }
            }).create(), display, null);
        });

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
                getCurrentAdapter().getFilter().filter(s);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    public Bitmap drawableToBitmap(Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }

        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
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
            AppInfo ai = getCurrentAdapter().filteredAppInfoList.get(pos);
            selectDirToSaveAPKOrSaveNow(ai.packageName, ai.versionName, pos);
        } catch (PackageManager.NameNotFoundException e) {
            showError(e);
        }
    }

    private void share(int pos) {
        try {
            ApplicationInfo ai = getPackageManager().getApplicationInfo(getCurrentAdapter().filteredAppInfoList.get(pos).packageName, 0);
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
            Intent intent = new Intent(Intent.ACTION_SEND).setType(mimeType).putExtra(Intent.EXTRA_STREAM, u);
           // if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) intent.setClipData(new ClipData("aa", new String[]{mimeType}, new ClipData.Item(u))); intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(Intent.createChooser(intent, rss.getString(R.string.share_apk)));
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
            this.filteredAppInfoList = new ArrayList<>(appInfoList);
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
                if (TextUtils.isEmpty(constraint)) {
                    results.values = appInfoList;
                    results.count = appInfoList.size();
                } else {
                    List<AppInfo> filteredItems = new ArrayList<>();
                    String filterPattern = constraint.toString().toLowerCase().trim();
                    for (AppInfo appInfo : appInfoList) if (appInfo.name.toLowerCase().contains(filterPattern)) filteredItems.add(appInfo);
                    results.values = filteredItems;
                    results.count = filteredItems.size();
                }
                return results;
            }

            /** @noinspection unchecked*/
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                filteredAppInfoList.clear();
                filteredAppInfoList.addAll((List<AppInfo>) results.values);
                notifyDataSetChanged();
            }
        }

        public HashSet<Integer> selectedItems = new HashSet<>();

        public void toggleSelection(int position) {
            if (selectedItems.contains(position)) selectedItems.remove(position);
            else selectedItems.add(position);
            notifyDataSetChanged();
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
            convertView.setBackgroundColor(selectedItems.contains(groupPosition) ? Color.GRAY : Color.TRANSPARENT);

            viewHolder.appName.setText(appInfo.name);
            viewHolder.appName.setTextColor(textColor);
            viewHolder.packageNameView.setText(appInfo.packageName);
            viewHolder.packageNameView.setTextColor(textColor);
            viewHolder.appIconView.setImageDrawable(appInfo.icon);
            LightingColorFilter lcf = new LightingColorFilter(Color.BLACK, textColor);
            viewHolder.extractIconView.setColorFilter(lcf);
            viewHolder.extractIconView.setOnClickListener(v -> context.extract(groupPosition));
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
                viewHolder.extractIcon = convertView.findViewById(R.id.extractIcon);
                viewHolder.extractBase = convertView.findViewById(R.id.extractBase);
                viewHolder.extractDex = convertView.findViewById(R.id.extractDex);
                viewHolder.extractLibs = convertView.findViewById(R.id.extractLibs);
                viewHolder.extractManifest = convertView.findViewById(R.id.extractManifest);
                viewHolder.extractRes = convertView.findViewById(R.id.extractRes);
                viewHolder.extractSplit = convertView.findViewById(R.id.extractSplit);

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
            } else viewHolder.lastUpdatedView.setVisibility(View.GONE);

            if(showExtractBase) {
                viewHolder.extractBase.setText(rss.getString(R.string.extract_base, ""));
                viewHolder.extractBase.setTextColor(textColor);
                viewHolder.extractBase.setVisibility(View.VISIBLE);
                viewHolder.extractBase.setOnClickListener(v -> {
                    if(ask) {
                        selectedItems.clear();
                        toggleSelection(groupPosition);
                        context.zip = false;
                        context.startActivityForResult(
                                new Intent(Intent.ACTION_CREATE_DOCUMENT)
                                        .addCategory(Intent.CATEGORY_OPENABLE)
                                        .setType("application/vnd.android.package-archive")
                                        .putExtra(Intent.EXTRA_TITLE, appInfo.packageName + "_v" + appInfo.versionName + '_' + "base.apk")
                                , 5005);
                    }
                    else {
                        context.runUtil.runInBackground(() -> {
                            String packageName = appInfo.packageName;
                            try {
                                FileUtils.copyFile(new File(context.getPackageManager().getPackageInfo(packageName, 0).applicationInfo.sourceDir), new File(context.getAppFolder(), packageName + "_v" + appInfo.versionName + '_' + "base.apk"));
                            } catch (Exception e) {
                                context.showError(e);
                            }
                            return errorOccurred;
                        });
                    }
                });
            } else viewHolder.extractBase.setVisibility(View.GONE);

            if(showExtractDex) {
                viewHolder.extractDex.setText(rss.getString(R.string.extract_dex, ""));
                viewHolder.extractDex.setTextColor(textColor);
                viewHolder.extractDex.setVisibility(View.VISIBLE);
                viewHolder.extractDex.setOnClickListener(v -> {
                    String classes = "classes";
                    String dex = ".dex";
                    if (ask) {
                        selectedItems.clear();
                        toggleSelection(groupPosition);
                        context.startActivityForResult(
                                new Intent(Intent.ACTION_CREATE_DOCUMENT)
                                        .addCategory(Intent.CATEGORY_OPENABLE)
                                        .setType("application/zip")
                                        .putExtra(Intent.EXTRA_TITLE, (CharSequence) new StringBuilder(classes).append(dex).append(".zip"))
                                , 5003);
                    }
                    else {
                        context.runUtil.runInBackground(() -> {
                            File outputDir = context.getAppFolder();
                            String packageName = appInfo.packageName;
                            try (ZipFile zf = new ZipFile(context.getPackageManager().getPackageInfo(packageName, 0).applicationInfo.sourceDir)) {
                                int i = 2;
                                ZipEntry curr = new ZipEntry(classes + dex);
                                try (InputStream is = zf.getInputStream(curr)) {
                                    FileUtils.copyFile(is, new File(outputDir, packageName + "_v" + appInfo.versionName + '_' + curr));
                                }

                                while ((curr = zf.getEntry(classes + i + dex)) != null) {
                                    i++;
                                    try (InputStream is = zf.getInputStream(curr)) {
                                        FileUtils.copyFile(is, new File(outputDir, packageName + "_v" + appInfo + '_' + curr));
                                    }
                                }
                            } catch (Exception e) {
                                context.showError(e);
                            }
                            return errorOccurred;
                        });
                    }
                });
            } else viewHolder.extractDex.setVisibility(View.GONE);

            if(showExtractIcon) {
                viewHolder.extractIcon.setText(rss.getString(R.string.extract_icon, ""));
                viewHolder.extractIcon.setTextColor(textColor);
                viewHolder.extractIcon.setVisibility(View.VISIBLE);
                viewHolder.extractIcon.setOnClickListener(v -> {
                    String packageName = appInfo.packageName;
                    String fileName = packageName + "_v" + appInfo.versionName + '_' + "icon.png";
                    if (ask) {
                        selectedItems.clear();
                        toggleSelection(groupPosition);
                        context.zip = false;
                        context.startActivityForResult(
                                new Intent(Intent.ACTION_CREATE_DOCUMENT)
                                        .addCategory(Intent.CATEGORY_OPENABLE)
                                        .setType("image/png")
                                        .putExtra(Intent.EXTRA_TITLE, fileName)
                                , 5009);
                    } else {
                        context.runUtil.runInBackground(() -> {
                            PackageManager pm = context.getPackageManager();
                            try (OutputStream outputStream = FileUtils.getOutputStream(new File(context.getAppFolder(), fileName))) {
                                context.drawableToBitmap(pm.getPackageInfo(packageName, 0).applicationInfo.loadIcon(pm)).compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                            } catch (Exception e) {
                                context.showError(e);
                            }
                            return errorOccurred;
                        });
                    }
                });
            } else viewHolder.extractIcon.setVisibility(View.GONE);

            if(showExtractLibs) {
                viewHolder.extractLibs.setText(rss.getString(R.string.extract_libs, ""));
                viewHolder.extractLibs.setTextColor(textColor);
                viewHolder.extractLibs.setVisibility(View.VISIBLE);
                viewHolder.extractLibs.setOnClickListener(v -> {
                    String packageName = appInfo.packageName;
                    String fileName = packageName + "_v" + appInfo.versionName + '_' + "libs.zip";
                    if (ask) {
                        selectedItems.clear();
                        toggleSelection(groupPosition);
                        context.startActivityForResult(
                                new Intent(Intent.ACTION_CREATE_DOCUMENT)
                                        .addCategory(Intent.CATEGORY_OPENABLE)
                                        .setType("application/zip")
                                        .putExtra(Intent.EXTRA_TITLE, fileName)
                                , 5006);
                    } else {
                        try {
                            for(File f : new File(context.getPackageManager().getPackageInfo(packageName, 0).applicationInfo.sourceDir).getParentFile().listFiles())
                                if(f.getName().endsWith(".apk")) try (ZipFile zf = new ZipFile(f)) {
                                    Enumeration<? extends ZipEntry> entries = zf.entries();
                                    String prefix = "lib/";

                                    while (entries.hasMoreElements()) {
                                        ZipEntry ze = entries.nextElement();
                                        String ds = ze.getName();
                                        if (ds.startsWith(prefix))
                                            try (InputStream is = zf.getInputStream(ze)) {
                                                FileUtils.copyFile(is, new File(context.getAppFolder(), packageName + "_v" + appInfo.versionName + '_' + f.getName().replace(prefix, "").replace('/', '_') + '_' + ds));
                                            }
                                    }
                                } catch (Exception e) {
                                    context.showError(e);
                                }
                        } catch (PackageManager.NameNotFoundException ignored) {}
                    }
                });
            } else viewHolder.extractLibs.setVisibility(View.GONE);

            if(showExtractRes) {
                viewHolder.extractRes.setText(rss.getString(R.string.extract_res, ""));
                viewHolder.extractRes.setTextColor(textColor);
                viewHolder.extractRes.setVisibility(View.VISIBLE);
                viewHolder.extractRes.setOnClickListener(v -> {
                    String thing = "resources.arsc";
                    if (ask) {
                        selectedItems.clear();
                        toggleSelection(groupPosition);
                        context.startActivityForResult(
                                new Intent(Intent.ACTION_CREATE_DOCUMENT)
                                        .addCategory(Intent.CATEGORY_OPENABLE)
                                        .setType("application/zip")
                                        .putExtra(Intent.EXTRA_TITLE, thing)
                                , 5002);
                    }
                    else context.runUtil.runInBackground(() -> {
                        File outputDir = context.getAppFolder();
                        selectedItems.forEach(integer -> {
                            String packageName = appInfo.packageName;
                            String dir;
                            try (ZipFile zf = new ZipFile(dir = context.getPackageManager().getPackageInfo(packageName, 0).applicationInfo.sourceDir);
                                 InputStream is = zf.getInputStream(zf.getEntry(thing))) {
                                FileUtils.copyFile(is,
                                    FileUtils.getOutputStream(
                                        new File(outputDir, packageName + "_v" + appInfo.versionName + '_' + new File(dir).getName() + '_' + thing)));
                            } catch (Exception e) {
                                context.showError(e);
                            }
                        });
                        return errorOccurred;
                    });
                });
            } else viewHolder.extractRes.setVisibility(View.GONE);

            if(showExtractManifest) {
                viewHolder.extractManifest.setText(rss.getString(R.string.extract_manifest, ""));
                viewHolder.extractManifest.setTextColor(textColor);
                viewHolder.extractManifest.setVisibility(View.VISIBLE);
                viewHolder.extractManifest.setOnClickListener(v -> {
                    String am = "AndroidManifest.xml";
                    if (ask) {
                        selectedItems.clear();
                        toggleSelection(groupPosition);
                        context.zip = false;
                        context.startActivityForResult(
                                new Intent(Intent.ACTION_CREATE_DOCUMENT)
                                        .addCategory(Intent.CATEGORY_OPENABLE)
                                        .setType("text/xml")
                                        .putExtra(Intent.EXTRA_TITLE, am)
                                , 5004);
                    }
                    else context.runUtil.runInBackground(() -> {
                        File outputDir = context.getAppFolder();
                        ZipEntry amEntry = new ZipEntry(am);
                        selectedItems.forEach(integer -> {
                            String packageName = appInfo.packageName;
                            try (ZipFile zf = new ZipFile(context.getPackageManager().getPackageInfo(packageName, 0).applicationInfo.sourceDir);
                                 InputStream is = zf.getInputStream(amEntry)) {
                                FileUtils.copyFile(is, new File(outputDir, packageName + "_v" + appInfo.versionName + '_' + am));
                            } catch (Exception e) {
                                context.showError(e);
                            }
                        });
                        return errorOccurred;
                    });
                });
            } else viewHolder.extractManifest.setVisibility(View.GONE);

            if(supportsSplits && appInfo.isSplit && showExtractSplit) {
                viewHolder.extractSplit.setText(rss.getString(R.string.choose_split, ""));
                viewHolder.extractSplit.setTextColor(textColor);
                viewHolder.extractSplit.setVisibility(View.VISIBLE);
                viewHolder.extractSplit.setOnClickListener(v -> {
                    try {
                        File[] splits = new File(context.getPackageManager().getPackageInfo(appInfo.packageName, 0).applicationInfo.sourceDir).getParentFile().listFiles();

                        ArrayList<File> splitting = new ArrayList<>();

                        ArrayList<String> splitties = new ArrayList<>();
                        for(File f : splits) {
                            String curr = f.getName();
                            if(curr.endsWith(".apk")) {
                                splitting.add(f);
                                splitties.add(curr);
                            }
                        }
                        CharSequence[] display = new CharSequence[splitties.size()];
                        //for (String s : splits) if(!s.contains(".apk"))
                        context.styleAlertDialog(new AlertDialog.Builder(context).setSingleChoiceItems(splitties.toArray(display), -1, (dialog, which) -> {
                            if(ask) {
                                File f = context.superSplit = splitting.get(which);
                                context.startActivityForResult(new Intent(Intent.ACTION_CREATE_DOCUMENT)
                                        .addCategory(Intent.CATEGORY_OPENABLE)
                                        .setType("application/vnd.android.package-archive")
                                        .putExtra(Intent.EXTRA_TITLE, f.getName()), 5010);
                            }
                        }).create(), display, null);
                    } catch (PackageManager.NameNotFoundException ignored) {}
                });
            } else viewHolder.extractSplit.setVisibility(View.GONE);

            final String packageName = appInfo.packageName;

            viewHolder.btnLaunch.setOnClickListener(v -> {
                Intent launchIntent = context.getPackageManager().getLaunchIntentForPackage(packageName);
                if (launchIntent == null) Toast.makeText(context, "Cannot launch this app", Toast.LENGTH_SHORT).show();
                else context.startActivity(launchIntent);
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
                            new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).setData(Uri.parse("package:" + packageName))
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
            TextView extractIcon;
            TextView extractRes;
            TextView extractDex;
            TextView extractManifest;
            TextView extractBase;
            TextView extractSplit;
            TextView extractLibs;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }
    }

    File superSplit;

    public void styleAlertDialog(AlertDialog ad, CharSequence[] display, ArrayAdapter adapter) {
        GradientDrawable border = new GradientDrawable();
        border.setColor(bgColor); // Background color
        border.setStroke(5, textColor); // Border width and color
        border.setCornerRadius(16);
        runOnUiThread(() -> {
            ad.show();
            ListView lv = ad.getListView();
            if(lv != null) {
                lv.setBackgroundColor(bgColor);
                if ((adapter != null || display != null)) lv.setAdapter(adapter == null ? new CustomArrayAdapter(this, display, textColor) : adapter);
            }

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
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 5001);
            else
                startActivityForResult(new Intent(ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION, Uri.parse("package:" + getPackageName())), 5001);
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
                .putBoolean("ask", ask)
                .putBoolean("signApk", signApk)
                .putBoolean("showIcon", showIcon)
                .putBoolean("showAppName", showAppName)
                .putBoolean("showVersionName", showVersionName)
                .putBoolean("showLastUpdate", showLastUpdate)
                .putBoolean("showVersionCode", showVersionCode)
                .putBoolean("showPackageName", showPackageName)
                .putBoolean("showAppName", showAppName)
                .putBoolean("showExtractIcon", showExtractIcon)
                .putBoolean("showExtractRes", showExtractRes)
                .putBoolean("showExtractBase", showExtractBase)
                .putBoolean("showExtractManifest", showExtractManifest)
                .putBoolean("showExtractDex", showExtractDex)
                .putBoolean("showExtractSplit", showExtractSplit)
                .putBoolean("showExtractLibs", showExtractLibs)
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
    static class ProcessTask extends AsyncTask<Uri, Void, String> {
        private final WeakReference<MainActivity> activityReference;
        private final String packageNameFromAppList;

        // only retain a weak reference to the activity
        ProcessTask(MainActivity context, String fromAppList) {
            activityReference = new WeakReference<>(context);
            this.packageNameFromAppList = fromAppList;
        }

        @Override
        protected String doInBackground(Uri... uris) {
            MainActivity activity = activityReference.get();
            if (activity == null) return null;

            final File cacheDir = activity.getCacheDir();
            deleteDir(cacheDir);

            try {
                ApplicationInfo ai = activity.getPackageManager().getPackageInfo(packageNameFromAppList, 0).applicationInfo;
                File baseApk = new File(ai.sourceDir);
                File apkDirectory = baseApk.getParentFile();
                boolean split = supportsSplits && ai.splitSourceDirs != null;
                if (split && antisplit) try (ApkBundle bundle = new ApkBundle()) {
                    bundle.loadApkDirectory(apkDirectory, false);
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
                    } else FileUtils.copyFile(baseApk, os);
                }
            } catch (Exception e) {
                activity.showError(e);
            }

            try {
                return FileUtils.getPath(uris[0], activity);
            } catch (IOException e) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(String result) {
            Activity activity = activityReference.get();
            if (errorOccurred) return;
            activity.runOnUiThread(() -> Toast.makeText(activity, rss.getString(R.string.success_saved, result), Toast.LENGTH_SHORT).show());
        }
    }

    boolean zip = true;

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            AppExpandableListAdapter adapter = getCurrentAdapter();
            switch (requestCode) {
                case 5010:
                    runUtil.runInBackground(() -> {
                        try(OutputStream os = FileUtils.getOutputStream(data.getData(), this)) {
                            FileUtils.copyFile(superSplit, os);
                        }
                        superSplit = null;
                        return errorOccurred;
                    });
                    break;
                case 5009:
                    runUtil.runInBackground(() -> {
                        PackageManager pm = getPackageManager();
                        for(int integer : adapter.selectedItems) {
                            AppInfo ai = adapter.filteredAppInfoList.get(integer);
                            String packageName = ai.packageName;
                            File temp = new File(getCacheDir(), "b");
                            try (OutputStream outputStream = FileUtils.getOutputStream(data.getData(), this);
                                 ZipOutputStream zos = zip ? new ZipOutputStream(outputStream) : null;
                                 OutputStream tempStream = FileUtils.getOutputStream(temp)) {
                                ApplicationInfo applicationInfo = pm.getPackageInfo(packageName, 0).applicationInfo;
                                Bitmap bm = drawableToBitmap(applicationInfo.loadIcon(pm));
                                if(zip) zos.putNextEntry(new ZipEntry(packageName + "_v" + ai.versionName + '_' + "icon.png"));
                                bm.compress(Bitmap.CompressFormat.PNG, 100, zip ? tempStream : outputStream);
                                if(zip) FileUtils.copyFile(temp, zos);
                            } catch (Exception e) {
                                showError(e);
                            }
                            if(!zip) {
                                zip = true;
                                break;
                            }
                        }
                        return errorOccurred;
                    });
                    break;
                case 5008:
                    runUtil.runInBackground(() -> {
                        adapter.selectedItems.forEach(integer -> {
                            AppInfo ai = adapter.filteredAppInfoList.get(integer);
                            String packageName = ai.packageName;
                            try (ZipFile zf = new ZipFile(MainActivity.this.getPackageManager().getPackageInfo(packageName, 0).applicationInfo.sourceDir);
                                 OutputStream os = FileUtils.getOutputStream(data.getData(), this); ZipOutputStream zipOutputStream = new ZipOutputStream(os)) {
                                Enumeration<? extends ZipEntry> entries = zf.entries();

                                while (entries.hasMoreElements()) {
                                    ZipEntry ze = entries.nextElement();
                                    String fileName = ze.getName();
                                    if (fileName.startsWith("res/"))
                                        try (InputStream is = zf.getInputStream(ze)) {
                                            zipOutputStream.putNextEntry(new ZipEntry(packageName + '_' + fileName));
                                            FileUtils.copyFile(is, zipOutputStream);
                                        }
                                }
                            } catch (Exception e) {
                                MainActivity.this.showError(e);
                            }
                        });
                        return errorOccurred;
                    });
                    break;
                case 5007:
                    runUtil.runInBackground(() -> {
                        try(OutputStream os = FileUtils.getOutputStream(data.getData(), this);
                            ZipOutputStream zos = new ZipOutputStream(os)) {
                            adapter.selectedItems.forEach(integer -> {
                                AppInfo ai = adapter.filteredAppInfoList.get(integer);
                                String packageName = ai.packageName;

                                try {
                                    ApplicationInfo applicationInfo = getPackageManager().getPackageInfo(packageName, 0).applicationInfo;
                                    StringBuilder type = new StringBuilder(packageName).append("_v").append(ai.versionName).append(".apk");
                                    File baseApk = new File(applicationInfo.sourceDir);
                                    File file;
                                    if(!supportsSplits || (antisplit && applicationInfo.splitSourceDirs != null)) {
                                        type.append('s');
                                        file = new File(getCacheDir(), type.toString());
                                        try (OutputStream outputStream = FileUtils.getOutputStream(file); ZipOutputStream zipOutputStream = new ZipOutputStream(outputStream)) {
                                            for (File f : baseApk.getParentFile().listFiles()) {
                                                String fileName = f.getName();
                                                if (f.isFile() && fileName.endsWith(".apk")) {
                                                    zipOutputStream.putNextEntry(new ZipEntry(fileName));
                                                    FileUtils.copyFile(f, zipOutputStream);
                                                }
                                            }
                                        }
                                    } else file = baseApk;
                                    zos.putNextEntry(new ZipEntry(type.toString()));
                                    FileUtils.copyFile(file, zos);
                                } catch (Exception e) {
                                    showError(e);
                                }
                            });
                        } catch (Exception e) {
                            showError(e);
                        }
                        return errorOccurred;
                    });
                    break;
                case 5001:
                    checkStoragePerm();
                    break;
                case 5002:
                runUtil.runInBackground(() -> {
                    try(OutputStream os = FileUtils.getOutputStream(data.getData(), this);
                        ZipOutputStream zos = new ZipOutputStream(os)) {
                        String thing = "resources.arsc";
                        adapter.selectedItems.forEach(integer -> {
                            AppInfo ai = adapter.filteredAppInfoList.get(integer);
                            String packageName = ai.packageName;
                            String apkDir;
                            try (ZipFile zf = new ZipFile(apkDir = getPackageManager().getPackageInfo(packageName, 0).applicationInfo.sourceDir)){
                                ZipEntry entry = zf.getEntry(thing);
                                for (File f : new File(apkDir).getParentFile().listFiles()) {
                                    String name = f.getName();
                                    if (name.contains("dpi"))
                                        try (ZipFile zipFile = new ZipFile(f); InputStream is = zipFile.getInputStream(zipFile.getEntry(thing))) {
                                            zos.putNextEntry(new ZipEntry(packageName + "_v" + ai.versionName + '_' + name + '_' + thing));
                                            FileUtils.copyFile(is, zos);
                                        }
                                }
                                if(entry != null) try(InputStream is = zf.getInputStream(entry)) {
                                    zos.putNextEntry(new ZipEntry(packageName + "_v" + ai.versionName + '_' + thing));
                                    FileUtils.copyFile(is, zos);
                                }
                            } catch (Exception e) {
                                showError(e);
                            }
                        });
                    } catch (Exception e) {
                        showError(e);
                    }
                    return errorOccurred;
                });
                    break;
                case 5003:
                    runUtil.runInBackground(() -> {
                        try(OutputStream os = FileUtils.getOutputStream(data.getData(), this);
                            ZipOutputStream zos = new ZipOutputStream(os)) {
                            String classes = "classes";
                            String dex = ".dex";
                            adapter.selectedItems.forEach(integer -> {
                                AppInfo ai = adapter.filteredAppInfoList.get(integer);
                                String packageName = ai.packageName;
                                try (ZipFile zf = new ZipFile(getPackageManager().getPackageInfo(packageName, 0).applicationInfo.sourceDir)) {
                                    int i = 2;
                                    ZipEntry curr = new ZipEntry(classes + dex);
                                    try(InputStream is = zf.getInputStream(curr)) {
                                        zos.putNextEntry(new ZipEntry(packageName + "_v" + ai.versionName + '_' + curr));
                                        FileUtils.copyFile(is, zos);
                                    }

                                    while((curr = zf.getEntry(classes + i + dex)) != null) {
                                        i++;
                                        try(InputStream is = zf.getInputStream(curr)) {
                                            zos.putNextEntry(new ZipEntry(packageName + "_v" + ai.versionName + '_' + curr));
                                            FileUtils.copyFile(is, zos);
                                        }
                                    }
                                } catch (Exception e) {
                                    showError(e);
                                }
                            });
                        } catch (Exception e) {
                            showError(e);
                        }
                        return errorOccurred;
                    });
                    break;
                case 5004:
                    runUtil.runInBackground(() -> {
                        try(OutputStream os = FileUtils.getOutputStream(data.getData(), this);
                            ZipOutputStream zos = zip ? new ZipOutputStream(os) : null) {
                            String am = "AndroidManifest.xml";
                                ZipEntry amEntry = new ZipEntry(am);
                                for(int integer : adapter.selectedItems) {
                                    AppInfo ai = adapter.filteredAppInfoList.get(integer);
                                    String packageName = ai.packageName;
                                    try (ZipFile zf = new ZipFile(getPackageManager().getPackageInfo(packageName, 0).applicationInfo.sourceDir);
                                         InputStream is = zf.getInputStream(amEntry)) {
                                        if(zip) zos.putNextEntry(new ZipEntry(packageName + "_v" + ai.versionName + '_' + am));
                                        FileUtils.copyFile(is, zip ? zos : os);
                                    } catch (Exception e) {
                                        showError(e);
                                    }
                                    if(!zip) {
                                        zip = true;
                                        break;
                                    }
                                }
                        } catch (Exception e) {
                            showError(e);
                        }
                        return errorOccurred;
                    });
                    break;
                case 5005:
                    runUtil.runInBackground(() -> {
                        try(OutputStream os = FileUtils.getOutputStream(data.getData(), this);
                        ZipOutputStream zos = zip ? new ZipOutputStream(os) : null) {
                            for(int integer : adapter.selectedItems) {
                                AppInfo ai = adapter.filteredAppInfoList.get(integer);
                                String packageName = ai.packageName;
                                try {
                                    if(zip) zos.putNextEntry(new ZipEntry(packageName + "_v" + ai.versionName + '_' + "base.apk"));
                                    FileUtils.copyFile(new File(getPackageManager().getPackageInfo(packageName, 0).applicationInfo.sourceDir), zip ? zos : os);
                                } catch (Exception e) {
                                    showError(e);
                                }
                                if(!zip) {
                                    zip = true;
                                    break;
                                }
                            }
                        } catch (IOException e) {
                            showError(e);
                        }
                        return errorOccurred;
                    });
                    break;
                case 5006:
                    runUtil.runInBackground(() -> {
                        try(OutputStream os = FileUtils.getOutputStream(data.getData(), this);
                            ZipOutputStream zos = new ZipOutputStream(os)) {
                                adapter.selectedItems.forEach(integer -> {
                                    AppInfo ai = adapter.filteredAppInfoList.get(integer);
                                    String packageName = ai.packageName;
//                                    if(supportsArraysCopyOfAndDownloadManager) {
//                                        try {
//                                            for(File f : new File(getPackageManager().getPackageInfo(packageName, 0).applicationInfo.nativeLibraryDir).listFiles())
//                                                if (f.isDirectory()) for (File sub : f.listFiles()) {
//                                                    zos.putNextEntry(new ZipEntry(packageName + "_v" + ai.versionName + '_' + f.getName() + '_' + sub.getName()));
//                                                    FileUtils.copyFile(sub, zos);
//                                                }
//                                        } catch (PackageManager.NameNotFoundException ignored) {} catch (IOException e) { showError(e); }
//                                    } else
                                    try {
                                        for(File f : new File(getPackageManager().getPackageInfo(packageName, 0).applicationInfo.sourceDir).getParentFile().listFiles()) {
                                            String name = f.getName();
                                            if(name.endsWith(".apk")) try (ZipFile zf = new ZipFile(f)) {
                                                Enumeration<? extends ZipEntry> entries = zf.entries();
                                                String prefix = "lib/";

                                                while (entries.hasMoreElements()) {
                                                    ZipEntry ze = entries.nextElement();
                                                    String fileName = ze.getName();
                                                    if (fileName.startsWith(prefix))
                                                        try (InputStream is = zf.getInputStream(ze)) {
                                                            zos.putNextEntry(new ZipEntry(packageName + "_v" + ai.versionName + '_' + name + '_' + fileName.replace(prefix, "").replace('/', '_')));
                                                            FileUtils.copyFile(is, zos);
                                                        }
                                                }
                                            } catch (Exception e) {
                                                showError(e);
                                            }
                                        }
                                    } catch (PackageManager.NameNotFoundException ignored) {}
                                });
                        } catch (IOException e) {
                            showError(e);
                        }
                        return errorOccurred;
                    });
                    break;
                default: // going to process and save a file now
                    new ProcessTask(this, adapter.filteredAppInfoList.get(requestCode).packageName).execute(data.getData());
                    break;
            }
        }
    }

    private void copyText(CharSequence text) {
        ((ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE)).setText(text);
        Toast.makeText(this, rss.getString(R.string.copied_log), Toast.LENGTH_SHORT).show();
    }


    void showError(Exception e) {
        final String mainErr = e.toString();
        errorOccurred = !mainErr.equals(rss.getString(R.string.sign_failed));

        StringBuilder stackTrace = new StringBuilder().append(mainErr).append('\n');
        for (StackTraceElement line : e.getStackTrace()) stackTrace.append(line).append('\n');
        AlertDialog.Builder b = new AlertDialog.Builder(this)
                .setNegativeButton(rss.getString(R.string.cancel), null)
                .setPositiveButton(rss.getString(R.string.create_issue), (dialog, which) -> startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/AbdurazaaqMohammed/APKExtractor/issues/new?title=Crash%20Report&body=" + stackTrace))))
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

    private AppExpandableListAdapter getCurrentAdapter() {
        return appAdapter[system ? 1 : 0];
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
            return supportsSplits && isSplit && antisplit
                    //&& context.getPackageManager().getApplicationInfo(pkgName, 0).splitNames != null
                    ? result.replaceFirst("\\.(?:xapk|aspk|apk[sm])", "_antisplit.apk") : result;
        } catch (Exception ignored) {
            return "filename_not_found";
        }
    }

    private void selectDirToSaveAPKOrSaveNow(String pkgName, String versionName, int pos) throws PackageManager.NameNotFoundException {
        StringBuilder fileName = new StringBuilder(pkgName).append('_').append(versionName);
        if (ask && supportsSplits) {
            startActivityForResult(new Intent(Intent.ACTION_CREATE_DOCUMENT)
                    .addCategory(Intent.CATEGORY_OPENABLE)
                    .setType(antisplit || getPackageManager().getApplicationInfo(pkgName, 0).splitSourceDirs == null ? "application/vnd.android.package-archive" : "application/zip")
                    .putExtra(Intent.EXTRA_TITLE, (CharSequence) fileName), pos);
        } else {
            checkStoragePerm();
            fileName.append(".apk");
            if (supportsSplits && getPackageManager().getApplicationInfo(pkgName, 0).splitSourceDirs != null && !antisplit) fileName.append('s');
            new ProcessTask(this, pkgName).execute(Uri.fromFile(new File(getAppFolder(), fileName.toString())));
        }
    }
}