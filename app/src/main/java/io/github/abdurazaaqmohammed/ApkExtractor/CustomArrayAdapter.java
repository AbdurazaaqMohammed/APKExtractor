package io.github.abdurazaaqmohammed.ApkExtractor;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/** @noinspection NullableProblems*/
public class CustomArrayAdapter extends ArrayAdapter<String> {
    private final Context context;
    private final String[] values;
    private final int textColor;

    public CustomArrayAdapter(Context context, String[] values, int textColor) {
        super(context, android.R.layout.select_dialog_singlechoice, values);
        this.context = context;
        this.values = values;
        this.textColor = textColor;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(android.R.layout.select_dialog_singlechoice, parent, false);
        }
        TextView textView = convertView.findViewById(android.R.id.text1);
        textView.setText(values[position]);
        textView.setTextColor(textColor);
        return convertView;
    }
}