package com.example.krishna.kalories;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.List;

public class CustomAdapter extends ArrayAdapter<Food> {
    Context mainActivityContext;
    int layoutID;
    List<Food> list;

    public CustomAdapter(Context context, int resource, List<Food> objects) {
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
        TextView calories = (TextView) adapterLayout.findViewById(R.id.calories);
        calories.setTypeface(font);
        TextView protein = (TextView) adapterLayout.findViewById(R.id.protein);
        protein.setTypeface(font);
        TextView fat = (TextView) adapterLayout.findViewById(R.id.fat);
        fat.setTypeface(font);
        TextView carbs = (TextView) adapterLayout.findViewById(R.id.carbs);
        carbs.setTypeface(font);
        foodItem.setText(""+list.get(position).toString());
        calories.setText(""+list.get(position).getCalories()+" Calories");
        protein.setText(""+list.get(position).getProtein()+"g of Protein");
        fat.setText(""+list.get(position).getFat()+"g of Fat");
        carbs.setText(""+list.get(position).getCarbs()+"g of Carbohydrates");


        return adapterLayout;
    }
}
