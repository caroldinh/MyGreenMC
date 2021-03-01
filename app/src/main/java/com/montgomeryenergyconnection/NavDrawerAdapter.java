package com.montgomeryenergyconnection;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class NavDrawerAdapter extends ArrayAdapter<String> {

    private final Context context;
    private final int layoutResourceId;
    private String data[] = null;

    public NavDrawerAdapter(Context context, int layoutResourceId, String[] data)
    {
        super(context, layoutResourceId, data);
        this.context = context;
        this.layoutResourceId = layoutResourceId;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        LayoutInflater inflater = ((Activity) context).getLayoutInflater();

        View v = inflater.inflate(layoutResourceId, parent, false);

        TextView textView = v.findViewById(R.id.label);

        String choice = data[position];

        textView.setText(choice);

        return v;
    }


}