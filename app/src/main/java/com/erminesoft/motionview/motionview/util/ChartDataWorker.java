package com.erminesoft.motionview.motionview.util;

import android.content.Context;

import com.erminesoft.motionview.motionview.R;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.google.android.gms.fitness.data.Bucket;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class ChartDataWorker {

    public static BarData processListOfBuckets(List<Bucket> buckets, Context context) {
        List<String> xVals = new ArrayList<>();
        List<BarEntry> entries = new ArrayList<>();

        for (int i = 0; i < buckets.size(); i++) {
            Bucket bucket = buckets.get(i);
            DataSet dataSet = bucket.getDataSet(DataType.TYPE_STEP_COUNT_DELTA);

            xVals.add(String.valueOf(i + 1));

            float steps;
            if (dataSet.getDataPoints().size() > 0) {
                DataPoint dataPoint = dataSet.getDataPoints().get(0);
                steps = dataPoint.getValue(Field.FIELD_STEPS).asInt();
            } else {
                steps = 0f;
            }

            entries.add(new BarEntry(steps, i, dataSet));
        }

        BarDataSet dataSet = new BarDataSet(
                entries, context.getString(R.string.chart_steps));
        return new BarData(xVals, dataSet);
    }

    public static Map<Integer, List<String>> getAvailableMonthsForSpinner(List<Bucket> buckets, Context context) {
        if (buckets.isEmpty()) {
            return null;
        }

        Map<Integer, List<String>> yearsMonthsMap = new HashMap<>();
        List<String> months = Arrays
                .asList(context.getResources().getStringArray(R.array.month_array));

        long startTime = System.currentTimeMillis();
        long endTime = startTime;

        for (Bucket bucket : buckets) {
            if (!bucket.getDataSets().isEmpty()) {
                startTime = bucket.getStartTime(TimeUnit.MILLISECONDS);
                break;
            }
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(startTime);

        int startYear = calendar.get(Calendar.YEAR);
        int startMonth = calendar.get(Calendar.MONTH);

        yearsMonthsMap.put(startYear++, months.subList(startMonth, months.size() - 1));

        calendar.setTimeInMillis(endTime);

        int currentYear = calendar.get(Calendar.YEAR);
        int currentMonth = calendar.get(Calendar.MONTH);

        for (int i = startYear; i < currentYear; i++) {
            yearsMonthsMap.put(i, months);
        }

        yearsMonthsMap.put(currentYear, months.subList(0, currentMonth));

        return yearsMonthsMap;
    }
}
