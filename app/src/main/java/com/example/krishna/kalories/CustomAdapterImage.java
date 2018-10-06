package com.example.krishna.kalories;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class CustomAdapterImage extends ArrayAdapter<String> {
    Context mainActivityContext;
    int layoutID;
    List<String> list;

    public CustomAdapterImage(Context context, int resource, List<String> objects) {
        super(context, resource, objects);
        mainActivityContext = context;
        layoutID = resource;
        list = objects;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater layoutInflater=(LayoutInflater)mainActivityContext.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        View adapterLayout= layoutInflater.inflate(layoutID,null);
        Typeface font = Typeface.createFromAsset(mainActivityContext.getAssets(),  "fonts/default.ttf");

        TextView foodItem = (TextView) adapterLayout.findViewById(R.id.foodItem);
        foodItem.setTypeface(font);
        foodItem.setText(list.get(position));

        return adapterLayout;
    }
}

