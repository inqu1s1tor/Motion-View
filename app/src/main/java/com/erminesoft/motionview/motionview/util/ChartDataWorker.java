package com.erminesoft.motionview.motionview.util;

import android.content.Context;
import android.graphics.Color;

import com.erminesoft.motionview.motionview.R;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
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
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

public class ChartDataWorker {
    private static final Map<String, Integer> MONTHS = new HashMap<>();
    private static final List<Month> MONTHS_LIST = new ArrayList<>();

    public static void init(Context context) {
        if (!MONTHS.isEmpty()) {
            return;
        }

        List<String> months = Arrays
                .asList(context.getResources().getStringArray(R.array.month_array));

        for (int i = 0; i < months.size(); i++) {
            MONTHS.put(months.get(i), i);
            MONTHS_LIST.add(new Month(i, months.get(i)));
        }
    }

    public static BarData processStepsBuckets(List<Bucket> buckets, Context context) {
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

            entries.add(new BarEntry(steps, i, bucket.getDataSets()));
        }

        BarDataSet dataSet = new BarDataSet(
                entries, context.getString(R.string.chart_steps));
        return new BarData(xVals, dataSet);
    }

    public static BarData processStepsBuckets(List<Bucket> buckets) {
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
        return barData;
    }

    public static LineData processCaloriesData(List<Bucket> buckets) {
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

        return lineData;
    }

    public static PieData processActivitiesData() {
        return null;
    }

    public static Map<Integer, List<Month>> getAvailableYearsMonthsForSpinner(List<Bucket> buckets) {
        if (buckets.isEmpty()) {
            return null;
        }

        Map<Integer, List<Month>> yearsMonthsMap = new TreeMap<>();

        long startTime = System.currentTimeMillis();
        long endTime = startTime;

        for (Bucket bucket : buckets) {
            DataSet stepsDataSet = bucket.getDataSet(DataType.TYPE_STEP_COUNT_DELTA);
            if (stepsDataSet.getDataPoints().size() == 0) {
                continue;
            }

            startTime = stepsDataSet.getDataPoints().get(0).getStartTime(TimeUnit.MILLISECONDS);
            break;
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(startTime);

        int startYear = calendar.get(Calendar.YEAR);
        int startMonth = calendar.get(Calendar.MONTH);

        calendar.setTimeInMillis(endTime);

        int currentYear = calendar.get(Calendar.YEAR);
        int currentMonth = calendar.get(Calendar.MONTH);

        if (startMonth == currentMonth && startYear == currentYear) {
            List<Month> availableMonth = new ArrayList<>();
            availableMonth.add(MONTHS_LIST.get(currentMonth));

            yearsMonthsMap.put(currentYear, availableMonth);
            return yearsMonthsMap;
        }
        yearsMonthsMap.put(startYear++, MONTHS_LIST.subList(startMonth, MONTHS_LIST.size()));

        for (int i = startYear; i < currentYear; i++) {
            yearsMonthsMap.put(i, MONTHS_LIST);
        }

        yearsMonthsMap.put(currentYear, MONTHS_LIST.subList(0, currentMonth + 1));

        return yearsMonthsMap;
    }

    public static int getIndexByName(String month) {
        return MONTHS.get(month);
    }

    public static String getNameByIndex(int index) {
        for (Map.Entry<String, Integer> entry : MONTHS.entrySet()) {
            if (entry.getValue().compareTo(index) == 0) {
                return entry.getKey();
            }
        }

        return null;
    }

    public static class Month {
        private int mIndex;
        private String mName;

        public Month(int index) {
            this(index, getNameByIndex(index));
        }

        public Month(String name) {
            this(getIndexByName(name), name);
        }

        public Month(int index, String name) {
            mIndex = index;
            mName = name;
        }

        public int getIndex() {
            return mIndex;
        }

        public String getName() {
            return mName;
        }

        @Override
        public String toString() {
            return getName();
        }
    }
}

