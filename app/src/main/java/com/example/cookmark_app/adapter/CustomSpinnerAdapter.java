package com.example.cookmark_app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.example.cookmark_app.R;

import java.util.List;

public class CustomSpinnerAdapter extends ArrayAdapter<CharSequence> {

    public CustomSpinnerAdapter(@NonNull Context context, int resource, @NonNull List<CharSequence> objects) {
        super(context, resource, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
//        return getCustomView(position, convertView, parent);

        View view = super.getDropDownView(position, convertView, parent);

        if (position == 0) {
            TextView textView = (TextView) view;
            textView.setTextColor(ContextCompat.getColor(getContext(), R.color.greyHint));
        }

        return view;
    }

    private View getCustomView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.spinner_dropdown_item, parent, false);
        }

        TextView textView = convertView.findViewById(R.id.customSpinnerItemText);
        textView.setText(getItem(position));

        return convertView;
    }

    public boolean isEnabled(int position) {
        return position != 0;
    }
}