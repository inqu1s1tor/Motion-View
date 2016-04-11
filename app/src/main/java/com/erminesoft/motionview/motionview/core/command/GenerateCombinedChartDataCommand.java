package com.erminesoft.motionview.motionview.core.command;

import android.graphics.Color;
import android.os.Bundle;

import com.erminesoft.motionview.motionview.R;
import com.erminesoft.motionview.motionview.storage.DataBuffer;
import com.erminesoft.motionview.motionview.util.TypeFaceHelper;
import com.github.mikephil.charting.components.YAxis;
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
    private static final String[] HOURS_IN_DAY = new String[]{"", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23"};

    private LineData lineData;

    public static Bundle generateBundle(long timestamp) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(Command.TRANSPORT_KEY, CommandType.GENERATE_COMBINED_CHART_DATA);
        bundle.putLong(TIMESTAMP_KEY, timestamp);

        return bundle;
    }

    @Override
    public void execute() {
        Bundle bundle = getBundle();

        if (!bundle.containsKey(TIMESTAMP_KEY)) {
            return;
        }

        long timeStamp = bundle.getLong(TIMESTAMP_KEY);

        if (mGoogleFitnessFacade == null) {
            return;
        }

        lineData = new LineData(HOURS_IN_DAY);

        DataReadResult result = mGoogleFitnessFacade.getHoursDataPerDay(timeStamp);
        List<Bucket> buckets = result.getBuckets();

        processCaloriesData(buckets);

        if (!isDenied()) {
            DataBuffer.getInstance().putData(lineData, CommandType.GENERATE_COMBINED_CHART_DATA);
        }
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

        LineDataSet set = new LineDataSet(entries, "");

        set.setColor(Color.rgb(240, 238, 70));
        set.setLineWidth(2.5f);

        set.setCircleColor(Color.rgb(240, 238, 70));
        set.setCircleRadius(5f);
        set.setFillColor(Color.rgb(240, 238, 70));
        set.setDrawCubic(true);
        set.setDrawValues(false);
        set.setDrawCircles(false);
        set.setDrawFilled(true);
        set.setDrawHorizontalHighlightIndicator(false);
        set.setHighlightLineWidth(2f);
        set.setHighLightColor(Color.rgb(168, 68, 68));
        set.setColor(Color.rgb(236, 124, 42));
        set.setFillDrawable(TypeFaceHelper.getInstance().getContext().getResources().getDrawable(R.drawable.chart_grad));
        set.setValueTextSize(10f);
        set.setValueTextColor(Color.rgb(240, 238, 70));
        set.setAxisDependency(YAxis.AxisDependency.LEFT);

        lineData.addDataSet(set);
    }
}
