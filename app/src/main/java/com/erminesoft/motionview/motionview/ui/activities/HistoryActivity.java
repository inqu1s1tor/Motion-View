package com.erminesoft.motionview.motionview.ui.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.erminesoft.motionview.motionview.R;
import com.erminesoft.motionview.motionview.core.callback.ResultListener;
import com.erminesoft.motionview.motionview.util.TimeWorker;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.google.android.gms.fitness.data.Bucket;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class HistoryActivity extends GenericActivity {

    private static final float MIN_VALUES = 3f;
    private static final float MAX_VALUES = 5f;
    private static final int ANIMATE_DURATION_MILLIS = 2000;
    private static final String EMPTY_STRING = "";

    private BarChart mBarChart;

    public static void start(Activity activity) {
        activity.startActivity(new Intent(activity, HistoryActivity.class));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        setTitle(R.string.history_activity_title);

        mActionBar.setDisplayHomeAsUpEnabled(true);

        initChart();

        Spinner monthSpinner = (Spinner) findViewById(R.id.month_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.month_array, android.R.layout.simple_spinner_item);

        int currentMonth = TimeWorker.getCurrentMonth();

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        monthSpinner.setAdapter(adapter);
        monthSpinner.setOnItemSelectedListener(new SpinnerItemSelectedListener());
        monthSpinner.setSelection(currentMonth);
    }

    private void initChart() {
        mBarChart = (BarChart) findViewById(R.id.bar_chart);

        mBarChart.setDescription(EMPTY_STRING);
        mBarChart.getLegend().setEnabled(false);

        mBarChart.setDrawGridBackground(false);
        mBarChart.getXAxis().setDrawGridLines(false);

        mBarChart.setDrawMarkerViews(true);
        mBarChart.setScaleEnabled(false);
        mBarChart.setHardwareAccelerationEnabled(true);
        mBarChart.setDrawBarShadow(true);
        mBarChart.setDragDecelerationEnabled(true);
        mBarChart.setAutoScaleMinMaxEnabled(true);
        mBarChart.setOnChartValueSelectedListener(new OnCharValueSelectedListenerImpl());
    }

    private final class SpinnerItemSelectedListener implements AdapterView.OnItemSelectedListener {

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            updateChartData(position);
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
        }

    }

    private void updateChartData(int currentMonth) {
        mGoogleClientFacade.getDataPerMonthFromHistory(
                currentMonth,
                new OnGotDataResultListener());
    }

    private final class OnCharValueSelectedListenerImpl implements OnChartValueSelectedListener {

        @Override
        public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {
            DataSet valueDataSet = (DataSet) e.getData();

            long timestamp = valueDataSet.getDataPoints().get(0).getTimestamp(TimeUnit.MILLISECONDS);

            if (TimeWorker.isCurrentDay(timestamp)) {
                finish();
                return;
            }

            DailyStatisticActivity.start(HistoryActivity.this, valueDataSet);
        }

        @Override
        public void onNothingSelected() {
            Log.i(TAG, "Chart selection listener: Nothing selected.");
        }
    }

    private final class OnGotDataResultListener implements ResultListener<List<Bucket>> {

        @Override
        public void onSuccess(List<Bucket> result) {
            List<String> xVals = new ArrayList<>();
            List<BarEntry> entries = new ArrayList<>();

            for (int i = 0; i < result.size(); i++) {
                Bucket bucket = result.get(i);
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
                    entries, getString(R.string.chart_steps));
            BarData data = new BarData(xVals, dataSet);

            setChartData(data);
        }

        @Override
        public void onError(String error) {
            Log.i(TAG, error);
        }

    }

    private void setChartData(final BarData data) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mBarChart.clear();
                mBarChart.setData(data);

                mBarChart.setVisibleXRange(MIN_VALUES, MAX_VALUES);
                mBarChart.moveViewToX(data.getXValCount());
                mBarChart.animateY(ANIMATE_DURATION_MILLIS);
            }
        });
    }
}
