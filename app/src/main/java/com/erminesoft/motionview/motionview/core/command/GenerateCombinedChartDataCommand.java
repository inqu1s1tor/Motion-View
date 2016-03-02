package com.erminesoft.motionview.motionview.core.command;

import android.graphics.Color;
import android.os.Bundle;

import com.erminesoft.motionview.motionview.core.callback.ResultCallback;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.google.android.gms.fitness.data.Bucket;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.result.DataReadResult;

import java.util.ArrayList;
import java.util.List;

public class GenerateCombinedChartDataCommand extends GenericCommand {

    private static final String TIMESTAMP_KEY = "timestamp";
    private static final String[] HOURS_IN_DAY = new String[]{"3", "6", "9", "12", "15", "18", "21", "24"};

    private CombinedData mCombinedData;

    public static Bundle generateBundle(long timestamp) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(Command.TRANSPORT_KEY, CommandType.GENERATE_COMBINED_CHART_DATA);
        bundle.putLong(TIMESTAMP_KEY, timestamp);

        return bundle;
    }

    @Override
    public void execute(final ResultCallback callback, Bundle bundle) {
        super.execute(callback, bundle);

        if (!bundle.containsKey(TIMESTAMP_KEY)) {
            return;
        }

        long timeStamp = bundle.getLong(TIMESTAMP_KEY);

        if (mGoogleClientFacade == null) {
            return;
        }

        mCombinedData = new CombinedData(HOURS_IN_DAY);

        DataReadResult result = mGoogleClientFacade.getHoursDataPerDay(timeStamp);
        List<Bucket> buckets = result.getBuckets();

        processStepsData(buckets);
        processCaloriesData(buckets);

        if (!isDenied()) {
            callback.onSuccess(mCombinedData);
        }
    }

    private void processStepsData(List<Bucket> buckets) {
        List<BarEntry> entries = new ArrayList<>();

        for (int i = 0; i < buckets.size(); i++) {
            Bucket bucket = buckets.get(i);
            DataSet dataSet = bucket.getDataSet(DataType.TYPE_STEP_COUNT_DELTA);

            float steps;
            if (dataSet.getDataPoints().size() > 0) {
                DataPoint dataPoint = dataSet.getDataPoints().get(0);
                steps = dataPoint.getValue(Field.FIELD_STEPS).asInt();
            } else {
                steps = 0f;
            }

            entries.add(new BarEntry(steps, i, bucket.getDataSets()));
        }

        BarDataSet dataSet = new BarDataSet(
                entries, "steps");
        BarData barData = new BarData();

        barData.addDataSet(dataSet);

        mCombinedData.setData(barData);
    }

    private void processCaloriesData(List<Bucket> buckets) {
        List<Entry> entries = new ArrayList<>();
        for (int i = 0; i < buckets.size(); i++) {
            Bucket bucket = buckets.get(i);
            DataSet dataSet = bucket.getDataSet(DataType.TYPE_CALORIES_EXPENDED);

            float calories;
            if (dataSet.getDataPoints().size() > 0) {
                DataPoint dataPoint = dataSet.getDataPoints().get(0);
                calories = dataPoint.getValue(Field.FIELD_CALORIES).asFloat();
            } else {
                calories = 0f;
            }

            entries.add(new Entry(calories, i, bucket.getDataSets()));
        }

        LineDataSet set = new LineDataSet(entries, "Line DataSet");

        set.setColor(Color.rgb(240, 238, 70));
        set.setLineWidth(2.5f);
        set.setCircleColor(Color.rgb(240, 238, 70));
        set.setCircleRadius(5f);
        set.setFillColor(Color.rgb(240, 238, 70));
        set.setDrawCubic(true);
        set.setDrawValues(true);
        set.setValueTextSize(10f);
        set.setValueTextColor(Color.rgb(240, 238, 70));
        set.setAxisDependency(YAxis.AxisDependency.LEFT);

        LineData lineData = new LineData();
        lineData.addDataSet(set);

        mCombinedData.setData(lineData);
    }
}
