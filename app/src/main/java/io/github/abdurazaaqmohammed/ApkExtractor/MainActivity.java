package io.github.abdurazaaqmohammed.ApkExtractor;

import static android.provider.Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION;
import static io.github.abdurazaaqmohammed.ApkExtractor.LegacyUtils.canSetNotificationBarTransparent;
import static io.github.abdurazaaqmohammed.ApkExtractor.LegacyUtils.supportsActionBar;
import static io.github.abdurazaaqmohammed.ApkExtractor.LegacyUtils.supportsArraysCopyOfAndDownloadManager;
import static io.github.abdurazaaqmohammed.ApkExtractor.LegacyUtils.supportsFileChannel;
import static com.reandroid.apkeditor.merge.LogUtil.logEnabled;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.LightingColorFilter;
import android.graphics.Paint;
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
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

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

/** @noinspection deprecation*/
public class MainActivity extends Activity {
    private static boolean ask = true;
    private static boolean signApk;
    public static int textColor;
    public static int bgColor;
    public static boolean errorOccurred;
    public static String lang;
    private static String pkgName;
    public static boolean showIcon;
    public static boolean antisplit;
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        Drawable icon = menu.findItem(R.id.action_settings).getIcon();
        if (icon != null) {
            icon.mutate();
            icon.setColorFilter(new LightingColorFilter(0xFF000000, textColor));
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)  {
        ScrollView l = (ScrollView) LayoutInflater.from(this).inflate(R.layout.dialog_settings, null);
        l.setBackgroundColor(bgColor);

        setColor(textColor, true, l);

        ((TextView) l.findViewById(R.id.langPicker)).setText(rss.getString(R.string.lang));
        final boolean supportsSwitch = Build.VERSION.SDK_INT > 13;
        ((TextView) l.findViewById(supportsSwitch ? R.id.ask : R.id.askText)).setText(rss.getString(R.string.ask));
        ((TextView) l.findViewById(supportsSwitch ? R.id.signToggle : R.id.signToggleText)).setText(rss.getString(R.string.sign_apk));
        ((TextView) l.findViewById(supportsSwitch ? R.id.showIconToggle : R.id.showIconToggleText)).setText(rss.getString(R.string.show_icons));
        ((TextView) l.findViewById(R.id.changeTextColor)).setText(rss.getString(R.string.change_text_color));
        ((TextView) l.findViewById(R.id.changeBgColor)).setText(rss.getString(R.string.change_background_color));

        CompoundButton signToggle = l.findViewById(R.id.signToggle);
        signToggle.setChecked(signApk);
        signToggle.setOnCheckedChangeListener((buttonView, isChecked) -> signApk = isChecked);
        signToggle.setVisibility(antisplit ? View.VISIBLE : View.GONE);
        if(!supportsSwitch) findViewById(R.id.signToggleText).setVisibility(antisplit ? View.VISIBLE : View.GONE);

        CompoundButton antisplitToggle = l.findViewById(R.id.antisplitToggle);
        antisplitToggle.setChecked(antisplit);
        antisplitToggle.setOnCheckedChangeListener((buttonView, isChecked) -> {
            antisplit = isChecked;
            signToggle.setVisibility(isChecked ? View.VISIBLE : View.GONE);
            if(!supportsSwitch) findViewById(R.id.signToggleText).setVisibility(isChecked ? View.VISIBLE : View.GONE);
        });

        CompoundButton showIconToggle = l.findViewById(R.id.showIconToggle);
        showIconToggle.setChecked(showIcon);
        showIconToggle.setOnCheckedChangeListener((buttonView, isChecked) -> {
            Toast.makeText(this, rss.getString(R.string.changes_msg), Toast.LENGTH_SHORT).show();
            showIcon = isChecked;
        });

        CompoundButton askSwitch = l.findViewById(R.id.ask);
        if(LegacyUtils.doesNotSupportInbuiltAndroidFilePicker) {
            ask = false;
            askSwitch.setVisibility(View.GONE);
        }
        else {
            askSwitch.setChecked(ask);
            askSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
                ask = isChecked;
                if(!isChecked) checkStoragePerm();
            });
        }

        Button langPicker = l.findViewById(R.id.langPicker);
        setButtonBorder(langPicker);
        langPicker.setOnClickListener(v2 -> {
            int curr = -1;

            String[] langs = rss.getStringArray(R.array.langs);

            String[] display = rss.getStringArray(R.array.langs_display);

            styleAlertDialog(new AlertDialog.Builder(this).setSingleChoiceItems(display, curr, (dialog, which) -> {
                updateLang(LocaleHelper.setLocale(MainActivity.this, lang = langs[which]).getResources(), l);
                dialog.dismiss();
            }).create(), display, true, null);
        });

        l.findViewById(R.id.changeBgColor).setOnClickListener(v3 -> showColorPickerDialog(false, bgColor, l));
        l.findViewById(R.id.changeTextColor).setOnClickListener(v4 -> showColorPickerDialog(true, textColor, l));
        TextView title = new TextView(this);
        title.setText(rss.getString(R.string.settings));
        title.setTextColor(textColor);
        title.setTextSize(25);
        styleAlertDialog(
                new AlertDialog.Builder(this).setCustomTitle(title).setView(l)
                        .setPositiveButton(rss.getString(R.string.close), (dialog, which) -> dialog.dismiss()).create(), null, false, null);
        return super.onOptionsItemSelected(item);
    }
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
        if(isTextColor) {
            textColor = color;
            if(fromSettingsMenu) {
                ((TextView) settingsMenu.findViewById(supportsSwitch ? R.id.ask : R.id.askText)).setTextColor(color);
                ((TextView) settingsMenu.findViewById(supportsSwitch ? R.id.signToggle : R.id.signToggleText)).setTextColor(color);
                ((TextView) settingsMenu.findViewById(supportsSwitch ? R.id.showIconToggle : R.id.showIconToggleText)).setTextColor(color);
                ((TextView) settingsMenu.findViewById(supportsSwitch ? R.id.antisplitToggle : R.id.antisplitText)).setTextColor(color);
            }
        } else findViewById(R.id.main).setBackgroundColor(bgColor = color);

        LightingColorFilter themeColor = new LightingColorFilter(0xFF000000, textColor);
//        ImageView settingsButton = findViewById(R.id.settingsButton);
//        settingsButton.setColorFilter(themeColor);

        if(fromSettingsMenu) {
            setButtonBorder(settingsMenu.findViewById(R.id.langPicker));
            setButtonBorder(settingsMenu.findViewById(R.id.changeTextColor));
            setButtonBorder(settingsMenu.findViewById(R.id.changeBgColor));
            if(!supportsSwitch) {
                setButtonBorder(settingsMenu.findViewById(R.id.ask));
                setButtonBorder(settingsMenu.findViewById(R.id.signToggle));
            }
        }
    }

    public Handler getHandler() {
        return handler;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        handler = new Handler(Looper.getMainLooper());

        deleteDir(getCacheDir());

        setContentView(R.layout.activity_main);

        // Fetch settings from SharedPreferences
        SharedPreferences settings = getSharedPreferences("set", Context.MODE_PRIVATE);
        setColor(settings.getInt("textColor", 0xffffffff), true, null);
        setColor(settings.getInt("backgroundColor", 0xff000000), false, null);

        ActionBar ab;
        if(supportsActionBar && (ab = getActionBar()) != null) {
            Spannable text = new SpannableString(getString(R.string.app_name));
            text.setSpan(new ForegroundColorSpan(textColor), 0, text.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
            ab.setTitle(text);
            ab.setBackgroundDrawable(new ColorDrawable(bgColor));
        }

        signApk = settings.getBoolean("signApk", true);
        logEnabled = settings.getBoolean("logEnabled", true);
        ask = settings.getBoolean("ask", true);
        showIcon = settings.getBoolean("showIcon", true);
        antisplit = settings.getBoolean("antisplit", false);

        lang = settings.getString("lang", "en");
        if(Objects.equals(lang, Locale.getDefault().getLanguage())) rss = getResources();
        else updateLang(LocaleHelper.setLocale(MainActivity.this, lang).getResources(), null);

        ImageView loadingImage = new ImageView(this);
        loadingImage.setImageDrawable(rss.getDrawable(R.drawable.reload));
        loadingImage.setContentDescription("Loading image");
        loadingImage.setColorFilter(new LightingColorFilter(0xFF000000, textColor));
        Dialog d = new Dialog(this);
        d.setContentView(loadingImage);
        loadingImage.startAnimation(AnimationUtils.loadAnimation(this, R.anim.loading));

        d.show();

        PackageManager pm = getPackageManager();
        List<AppInfo> appInfoList = new ArrayList<>();

        for (PackageInfo packageInfo : pm.getInstalledPackages(0)) {
            try {
                String packageName = packageInfo.packageName;
                ApplicationInfo ai = pm.getApplicationInfo(packageName, 0);
                appInfoList.add(new AppInfo(
                    (String) pm.getApplicationLabel(ai),
                    pm.getApplicationIcon(ai),
                    packageName,
                    ai.enabled,
              canSetNotificationBarTransparent && ai.splitSourceDirs != null,
                    supportsArraysCopyOfAndDownloadManager ? new Date(packageInfo.firstInstallTime).toString() : "Unknown",
                    supportsArraysCopyOfAndDownloadManager ? new Date(packageInfo.lastUpdateTime).toString() : "Unknown",
                    packageInfo.versionCode,
                    packageInfo.versionName));
            } catch (PackageManager.NameNotFoundException ignored) {}
        }
        Collections.sort(appInfoList, Comparator.comparing((AppInfo p) -> p.name.toLowerCase(supportsArraysCopyOfAndDownloadManager ? Locale.ROOT : Locale.getDefault())));

        ListView listView = findViewById(R.id.list_view);
        final AppListArrayAdapter adapter = new AppListArrayAdapter(this, appInfoList);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener((parent, view, position, id) -> {
            pkgName = adapter.filteredAppInfoList.get(position).packageName;
            try {
                selectDirToSaveAPKOrSaveNow();
            } catch (PackageManager.NameNotFoundException e) {
                showError(e);
            }
        });

        EditText searchBar = findViewById(R.id.search_bar);
        searchBar.setTextColor(textColor);
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // No action needed here
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.getFilter().filter(s);
            }

            @Override
            public void afterTextChanged(Editable s) {
                // No action needed here
            }
        });

        loadingImage.clearAnimation();
        d.dismiss();

    /*ImageView settingsButton = findViewById(R.id.settingsButton);
        findViewById(R.id.decodeButton);
        Button selectFromInstalledApps = findViewById(R.id.fromAppsButton);
        if(LegacyUtils.canSetNotificationBarTransparent) selectFromInstalledApps.setOnClickListener(this::selectAppsListener);
        else selectFromInstalledApps.setVisibility(View.GONE);
        ((LinearLayout) findViewById(R.id.topButtons)).setGravity(Gravity.CENTER_VERTICAL);
        findViewById(R.id.processButtons).setLayoutParams(new LinearLayout.LayoutParams((int) (rss.getDisplayMetrics().widthPixels * 0.8), LinearLayout.LayoutParams.FILL_PARENT));

        settingsButton.setOnClickListener(v -> {
         */
    }

    /** @noinspection rawtypes*/
    public void styleAlertDialog(AlertDialog ad, String[] display, boolean isLang, ArrayAdapter adapter) {
        GradientDrawable border = new GradientDrawable();
        border.setColor(bgColor); // Background color
        border.setStroke(5, textColor); // Border width and color
        border.setCornerRadius(16);

        runOnUiThread(() -> {
            ad.show();
            ListView lv;
            if((adapter != null || display != null) && (lv = ad.getListView()) != null) lv.setAdapter(adapter == null ? new CustomArrayAdapter(this, display, textColor, isLang) : adapter);
            Window w = ad.getWindow();

            Button positiveButton = ad.getButton(AlertDialog.BUTTON_POSITIVE);
            if(positiveButton != null) positiveButton.setTextColor(textColor);

            Button negativeButton = ad.getButton(AlertDialog.BUTTON_NEGATIVE);
            if(negativeButton != null) negativeButton.setTextColor(textColor);

            Button neutralButton = ad.getButton(AlertDialog.BUTTON_NEUTRAL);
            if(neutralButton != null) neutralButton.setTextColor(textColor);

            if (w != null) {
                View dv = w.getDecorView();
                dv.getBackground().setColorFilter(new LightingColorFilter(0xFF000000, bgColor));
                w.setBackgroundDrawable(border);

                int padding = 16;
                dv.setPadding(padding, padding, padding, padding);
            }
        });
    }

    public static Resources rss;

    private void updateLang(Resources res, ScrollView settingsDialog) {
        rss = res;
     /*   Button decodeButton = findViewById(R.id.decodeButton);
        decodeButton.setText(res.getString(R.string.merge));
        setButtonBorder(decodeButton);
        Button fromAppsButton = findViewById(R.id.fromAppsButton);
        fromAppsButton.setText(res.getString(R.string.select_from_installed_apps));
        setButtonBorder(fromAppsButton);
        ImageView settingsButton = findViewById(R.id.settingsButton);
        settingsButton.setContentDescription(res.getString(R.string.settings));
        findViewById(R.id.installButton).setContentDescription(res.getString(R.string.install));
        decodeButton.post(() -> {
            int buttonHeight = decodeButton.getHeight();
            int size = (int) (buttonHeight * 0.75);
            ViewGroup.LayoutParams params = settingsButton.getLayoutParams();
            params.height = size;
            params.width = size;
            settingsButton.setLayoutParams(params);
        });
        ((LinearLayout) findViewById(R.id.topButtons)).setGravity(Gravity.CENTER_VERTICAL);
        findViewById(R.id.processButtons).setLayoutParams(new LinearLayout.LayoutParams((int) (rss.getDisplayMetrics().widthPixels * 0.8), LinearLayout.LayoutParams.FILL_PARENT));
*/
        if(settingsDialog != null) {
            ((TextView) settingsDialog.findViewById(R.id.langPicker)).setText(res.getString(R.string.lang));
            final boolean supportsSwitch = Build.VERSION.SDK_INT > 13;
            ((TextView) settingsDialog.findViewById(supportsSwitch ? R.id.ask : R.id.askText)).setText(res.getString(R.string.ask));
            ((TextView) settingsDialog.findViewById(supportsSwitch ? R.id.signToggle : R.id.signToggleText)).setText(res.getString(R.string.sign_apk));
            ((TextView) settingsDialog.findViewById(supportsSwitch ? R.id.showIconToggle : R.id.showIconToggleText)).setText(res.getString(R.string.sign_apk));
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
        if(doesNotHaveStoragePerm(this)) {
            Toast.makeText(this, rss.getString(R.string.grant_storage), Toast.LENGTH_LONG).show();
            if(LegacyUtils.supportsWriteExternalStorage) requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
            else startActivityForResult(new Intent(ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION, Uri.parse("package:" + getPackageName())), 0);
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
                .putBoolean("antisplit", antisplit)
                .putInt("textColor", textColor)
                .putInt("backgroundColor", bgColor)
                .putString("lang", lang);
        if (supportsArraysCopyOfAndDownloadManager) e.apply();
        else e.commit();
        super.onPause();
    }

    private Handler handler;

    /** @noinspection ResultOfMethodCallIgnored, DataFlowIssue */
    public static void deleteDir(File dir) {
        // There should never be folders in here.
        for (String child : dir.list()) new File(dir, child).delete();
    }

    @Override
    protected void onDestroy() {
        deleteDir(getCacheDir());
        super.onDestroy();
    }

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
                if (antisplit) try(ApkBundle bundle = new ApkBundle()) {
                    bundle.loadApkDirectory(apkDirectory, false, activity);
                    Merger.run(bundle, cacheDir, uris[0], activity, signApk);
                }
                else try (OutputStream os = FileUtils.getOutputStream(uris[0], activity)) {
                    if (!canSetNotificationBarTransparent || ai.splitSourceDirs == null) FileUtils.copyFile(baseApk, os);
                    else try (ZipOutputStream zos = new ZipOutputStream(os)) {
                        for (File f : apkDirectory.listFiles()) {
                            String fileName = f.getName();
                            if (f.isFile() && fileName.endsWith(".apk")) {
                                zos.putNextEntry(new ZipEntry(fileName));
                                FileUtils.copyFile(f, zos);
                            }
                        }
                    }
                }
            } catch (Exception e) {
                activity.showError(e);
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            MainActivity activity = activityReference.get();

            pkgName = null;
            activity.showSuccess();
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) switch (requestCode) {
            case 0:
                checkStoragePerm();
                break;
            case 2:
                // going to process and save a file now
                process(data.getData());
                break;
        }
    }

    private void process(Uri outputUri) {
        ProcessTask processTask = new ProcessTask(this, pkgName);
        processTask.execute(outputUri);

       /* ImageView cancelButton = findViewById(R.id.cancelButton);
        cancelButton.setColorFilter(new LightingColorFilter(0xFF000000, textColor));
        ViewSwitcher vs = findViewById(R.id.viewSwitcher);
        vs.setVisibility(View.VISIBLE);
        if(Objects.equals(vs.getCurrentView(), findViewById(R.id.finishedButtons))) vs.showNext();
        cancelButton.setOnClickListener(v -> {
            Intent intent;
            if(supportsActionBar && (intent = getPackageManager().getLaunchIntentForPackage(getPackageName())) != null) {
                startActivity(Intent.makeRestartActivityTask(intent.getComponent()));
                Runtime.getRuntime().exit(0);
            } else {
                processTask.cancel(true);
                intent = getIntent();
                finish();
                startActivity(intent);
            }
            //viewSwitcher.setVisibility(View.GONE);
        });*/
    }

    private void showSuccess() {
        if(errorOccurred) return;
        final String success = rss.getString(R.string.success_saved);
        LogUtil.logMessage(success);
        runOnUiThread(() -> Toast.makeText(this, success, Toast.LENGTH_SHORT).show());
        File output;
        /*if(signApk && (output = Merger.signedApk) != null && output.exists() && output.length() > 999) {
            installButton.setColorFilter(cf);
            installButton.setVisibility(View.VISIBLE);
            installButton.setOnClickListener(v ->          //if (supportsFileChannel && !getPackageManager().canRequestPackageInstalls()) startActivityForResult(new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES).setData(Uri.parse(String.format("package:%s", getPackageName()))), 1234);
                    startActivity(new Intent(Build.VERSION.SDK_INT > 13 ? Intent.ACTION_INSTALL_PACKAGE : Intent.ACTION_VIEW)
                            .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                            .setData(FileProvider.getUriForFile(this, "io.github.abdurazaaqmohammed.ApkExtractor.provider", output))));
        } else installButton.setVisibility(View.GONE);*/

    }

    private void copyText(CharSequence text) {
        if(supportsActionBar) ((ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE)).setPrimaryClip(ClipData.newPlainText("log", text));
        else ((android.text.ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE)).setText(text);
        Toast.makeText(this, rss.getString(R.string.copied_log), Toast.LENGTH_SHORT).show();
    }

    private void showError(Exception e) {
        if(!(e instanceof ClosedByInterruptException)) {
            final String mainErr = e.toString();
            errorOccurred = !mainErr.equals(rss.getString(R.string.sign_failed));

            StringBuilder stackTrace = new StringBuilder().append(mainErr).append('\n');
            for(StackTraceElement line : e.getStackTrace()) stackTrace.append(line).append('\n');
            AlertDialog.Builder b = new AlertDialog.Builder(this)
                    .setNegativeButton(rss.getString(R.string.cancel), null)
                    .setPositiveButton(rss.getString(R.string.create_issue), (dialog, which) -> startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/AbdurazaaqMohammed/AntiSplit-M/issues/new?title=Crash%20Report&body=" + stackTrace))))
                    .setNeutralButton(rss.getString(R.string.copy_log), (dialog, which) -> copyText(stackTrace));
            runOnUiThread(() -> {
                TextView title = new TextView( this);
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
                styleAlertDialog(b.setCustomTitle(title).setView(sv).create(), null, false, null);
            /*TextView errorBox = findViewById(R.id.errorField);
            errorBox.setVisibility(View.VISIBLE);
            errorBox.setText(stackTrace);
            Toast.makeText(this, mainErr, Toast.LENGTH_SHORT).show();*/
            });
        }
    }

    public static String getOriginalFileName(Context context, Uri uri) {
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
            return supportsFileChannel && antisplit && context.getPackageManager().getApplicationInfo(pkgName, 0).splitNames != null ? result.replaceFirst("\\.(?:xapk|aspk|apk[sm])", "_antisplit.apk") : result;
        } catch (Exception ignored) {
            return "filename_not_found";
        }
    }

    @SuppressLint("InlinedApi")
    private void selectDirToSaveAPKOrSaveNow() throws PackageManager.NameNotFoundException {
        if (ask) startActivityForResult(new Intent(Intent.ACTION_CREATE_DOCUMENT)
                .addCategory(Intent.CATEGORY_OPENABLE)
                .setType(antisplit || getPackageManager().getApplicationInfo(pkgName, 0).splitNames == null ? "application/vnd.android.package-archive" : "application/zip")
                .putExtra(Intent.EXTRA_TITLE, pkgName), 2);
        else {
            checkStoragePerm();
            new ProcessTask(this, pkgName).execute(Uri.fromFile(new File(getAppFolder(), pkgName)));
        }
    }
}