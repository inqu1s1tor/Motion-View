package com.erminesoft.motionview.motionview.ui.adapters;


import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.erminesoft.motionview.motionview.util.TypeFaceHelper;

import java.util.List;

public class SpinnerAdapter<T> extends ArrayAdapter<T>{
    private List<T> objects;
    private Typeface typeFace;

    public SpinnerAdapter(Context context, int resource, List<T> objects) {
        super(context, resource, objects);
        typeFace = TypeFaceHelper.getInstance().getTypeFace("fonts/ROBOTO-BOLD_0.TTF");
        this.objects = objects;
    }

    @Override
    public T getItem(int position) {
        return objects.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = super.getView(position, convertView, parent);
        TextView text = (TextView) view;
        text.setTextColor(Color.parseColor("#b8b8b8"));
        text.setTypeface(typeFace);
        return view;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        View view = super.getDropDownView(position,convertView,parent);
        TextView text = (TextView) view;
        text.setTextColor(Color.parseColor("#b8b8b8"));
        text.setTypeface(typeFace);
        return view;
    }

}
