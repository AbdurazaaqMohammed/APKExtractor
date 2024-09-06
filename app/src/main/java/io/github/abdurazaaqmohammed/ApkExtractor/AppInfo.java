package io.github.abdurazaaqmohammed.ApkExtractor;

import android.graphics.drawable.Drawable;

public class AppInfo {
    String name;
    String packageName;
    Drawable icon;
    boolean enabled;
    boolean isSplit;
    String firstInstalled;
    String lastUpdated;
    String versionName;
    int versionCode;
    public AppInfo(String name, Drawable icon, String packageName, boolean enabled, boolean isSplit, String firstInstalled, String lastUpdated, int versionCode, String versionName) {
        this.name = name;
        this.icon = icon;
        this.packageName = packageName;
        this.enabled = enabled;
        this.isSplit = isSplit;
        this.firstInstalled = firstInstalled;
        this.lastUpdated = lastUpdated;
        this.versionName = versionName;
        this.versionCode = versionCode;
    }
}
