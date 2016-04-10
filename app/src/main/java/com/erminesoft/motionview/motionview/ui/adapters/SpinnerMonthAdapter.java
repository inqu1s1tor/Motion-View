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
import com.erminesoft.motionview.motionview.util.ChartDataWorker;

import java.util.List;


public class SpinnerMonthAdapter extends ArrayAdapter<ChartDataWorker.Month>{

    private List<ChartDataWorker.Month> month;
    private Typeface typeFace;
    private Context context;

    public SpinnerMonthAdapter(Context context, int textView, List<ChartDataWorker.Month> objects) {
        super(context,textView, objects);
        typeFace = Typeface.createFromAsset(getContext().getAssets(),"fonts/ROBOTO-BOLD_0.TTF");
        this.context = context;
        this.month = objects;
    }

    @Override
    public ChartDataWorker.Month getItem(int position) {
        return month.get(position);
    }

    @Override
    public int getPosition(ChartDataWorker.Month item) {
        return month.indexOf(item);
    }






    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.history_spinner_item,null);
        TextView text = (TextView) view.findViewById(R.id.history_spinner_text_field);
        text.setTextColor(Color.parseColor("#b8b8b8"));
        text.setTypeface(typeFace);
        text.setText(this.getItem(position).getName());
        return view;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.history_spinner_item,null);
        TextView text = (TextView) view.findViewById(R.id.history_spinner_text_field);
        text.setTextColor(Color.parseColor("#b8b8b8"));
        text.setTypeface(typeFace);
        text.setText(this.getItem(position).getName());
        return view;
    }

}
