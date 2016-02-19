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

    public static Map<Integer, List<Month>> getAvailableMonthsForSpinner(List<Bucket> buckets, Context context) {
        if (buckets.isEmpty()) {
            return null;
        }

        Map<Integer, List<Month>> yearsMonthsMap = new HashMap<>();

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

        yearsMonthsMap.put(currentYear, MONTHS_LIST.subList(0, currentMonth));

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

