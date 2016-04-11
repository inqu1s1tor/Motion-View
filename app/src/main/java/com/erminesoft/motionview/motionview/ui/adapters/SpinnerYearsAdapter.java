package com.erminesoft.motionview.motionview.ui.adapters;


import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.erminesoft.motionview.motionview.R;

import java.util.List;

public class SpinnerYearsAdapter extends ArrayAdapter<Integer>{
    private List<Integer> years;
    private Context context;
    private Typeface typeFace;

    public SpinnerYearsAdapter(Context context, int resource, List<Integer> objects) {
        super(context, resource, objects);
        typeFace = Typeface.createFromAsset(getContext().getAssets(),"fonts/ROBOTO-BOLD_0.TTF");
        this.years = objects;
        this.context = context;
    }

    @Override
    public Integer getItem(int position) {
        return years.get(position);
    }

    @Override
    public int getPosition(Integer item) {
        return years.indexOf(item);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.history_spinner_item,null);
        TextView text = (TextView) view.findViewById(R.id.history_spinner_text_field);
        text.setTextColor(Color.parseColor("#b8b8b8"));
        text.setTypeface(typeFace);
        text.setText(this.getItem(position).toString());
        return view;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.history_spinner_item,null);
        TextView text = (TextView) view.findViewById(R.id.history_spinner_text_field);
        text.setTextColor(Color.parseColor("#b8b8b8"));
        text.setTypeface(typeFace);
        text.setText(this.getItem(position).toString());
        return view;
    }

}
