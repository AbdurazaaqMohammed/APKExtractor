package io.github.abdurazaaqmohammed.ApkExtractor;

import static io.github.abdurazaaqmohammed.ApkExtractor.MainActivity.showIcon;
import static io.github.abdurazaaqmohammed.ApkExtractor.MainActivity.textColor;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/** @noinspection NullableProblems*/
public class AppListArrayAdapter extends ArrayAdapter<AppInfo> implements Filterable {
    private final Context context;
    private final List<AppInfo> originalAppInfoList;
    public List<AppInfo> filteredAppInfoList;
    private AppInfoFilter filter;

    public AppListArrayAdapter(Context context, List<AppInfo> appInfoList) {
        super(context, R.layout.list_item, appInfoList);
        this.context = context;
        this.originalAppInfoList = new ArrayList<>(appInfoList);
        this.filteredAppInfoList = new ArrayList<>(appInfoList);
    }

    @Override
    public int getCount() {
        return filteredAppInfoList.size();
    }

    @Override
    public AppInfo getItem(int position) {
        return filteredAppInfoList.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
        }

        convertView.setBackgroundColor(MainActivity.bgColor);

        TextView appNameView = convertView.findViewById(R.id.appName);
        ImageView iconView = convertView.findViewById(R.id.icon_view);
        TextView packageNameView = convertView.findViewById(R.id.package_name_view);
        TextView firstInstallDateView = convertView.findViewById(R.id.first_installed_view);
        TextView lastUpdateDateView = convertView.findViewById(R.id.last_updated_view);

        AppInfo appInfo = getItem(position);
        //if(appInfo == null) return convertView;

        appNameView.setText(MainActivity.rss.getString(R.string.app_name_display, appInfo.name, appInfo.versionName, appInfo.versionCode));
        appNameView.setTextColor(textColor);

        if (showIcon) {
            iconView.setImageDrawable(appInfo.icon);
            iconView.setVisibility(View.VISIBLE);
            iconView.setContentDescription(MainActivity.rss.getString(R.string.app_icon_list_label));
        } else {
            iconView.setVisibility(View.GONE);
        }

        packageNameView.setText(appInfo.packageName);
        packageNameView.setTextColor(textColor);

        if(LegacyUtils.supportsArraysCopyOfAndDownloadManager) {
            lastUpdateDateView.setText(MainActivity.rss.getString(R.string.last_update, appInfo.lastUpdated));
            lastUpdateDateView.setTextColor(textColor);
            firstInstallDateView.setText(MainActivity.rss.getString(R.string.first_installed, appInfo.firstInstalled));
            firstInstallDateView.setTextColor(textColor);
        } else {
            firstInstallDateView.setVisibility(View.GONE);
            lastUpdateDateView.setVisibility(View.GONE);
        }

        return convertView;
    }

    @Override
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
                results.values = new ArrayList<>(originalAppInfoList);
                results.count = originalAppInfoList.size();
            } else {
                List<AppInfo> filteredItems = new ArrayList<>();
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (AppInfo appInfo : originalAppInfoList) {
                    if (appInfo.name.toLowerCase().contains(filterPattern)) {
                        filteredItems.add(appInfo);
                    }
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
}