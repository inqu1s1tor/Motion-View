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
import com.erminesoft.motionview.motionview.util.ChartDataWorker;
import com.erminesoft.motionview.motionview.util.TimeWorker;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.google.android.gms.fitness.data.Bucket;
import com.google.android.gms.fitness.data.DataSet;

import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class HistoryActivity extends GenericActivity {

    private static final float MIN_VALUES = 4f;
    private static final float MAX_VALUES = 4f;
    private static final int ANIMATE_DURATION_MILLIS = 1000;
    private static final String EMPTY_STRING = "";

    private Map<Integer, List<String>> mAvailableHistory;

    private ArrayAdapter<Integer> mYearAdapter;
    private ArrayAdapter<String> mMonthAdapter;

    private BarChart mBarChart;
    private Spinner mMonthSpinner;
    private Spinner mYearSpinner;

    public static void start(Activity activity) {
        activity.startActivity(new Intent(activity, HistoryActivity.class));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        setTitle(R.string.history_activity_title);

        setHomeAsUpEnabled();

        initDataForSpinners();

        initChart();
    }

    private void initDataForSpinners() {
        mGoogleClientFacade.getDataForAllTime(new OnGotAllDataListener());
    }

    private void initSpinners() {
        initAdapters();

        mYearSpinner = (Spinner) findViewById(R.id.activity_history_year_spinner);
        mMonthSpinner = (Spinner) findViewById(R.id.activity_history_month_spinner);

        mYearSpinner.setAdapter(mYearAdapter);
        mMonthSpinner.setAdapter(mMonthAdapter);

        mYearSpinner.setOnItemSelectedListener(new OnYearSpinnerItemSelectedListener());
        mMonthSpinner.setOnItemSelectedListener(new OnMonthSpinnerItemSelectedListener());

        mYearSpinner.setSelection(mYearAdapter.getCount() - 1, true);
    }

    private void initAdapters() {
        mYearAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item);
        mMonthAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item);

        for (Integer year : mAvailableHistory.keySet()) {
            mYearAdapter.add(year);
        }

        mYearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mMonthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
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

    private void updateMonthAndChart(int position) {
        Calendar calendar = Calendar.getInstance();
        int currentYear = calendar.get(Calendar.YEAR);
        int yearsDelta = mAvailableHistory.size() - position;
        int year = currentYear - yearsDelta;

        mMonthAdapter.clear();
        mMonthAdapter.addAll(mAvailableHistory.get(year));

        int currentMonth = mMonthSpinner.getSelectedItemPosition();
        mMonthSpinner.setSelection(currentMonth == Spinner.INVALID_POSITION ?
                calendar.get(Calendar.MONTH) : currentMonth);

        updateChartData(currentMonth, year);
    }

    //TODO add yearsDelta using
    private void updateChartData(int currentMonth, int yearsDelta) {
        mGoogleClientFacade.getDataPerMonthFromHistory(
                currentMonth,
                new OnGotDataResultListener());
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

    private final class OnYearSpinnerItemSelectedListener implements AdapterView.OnItemSelectedListener {

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            updateMonthAndChart(position);
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
        }

    }

    private final class OnMonthSpinnerItemSelectedListener implements AdapterView.OnItemSelectedListener {

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            updateChartData(position, 0);
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
        }

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

            BarData data = ChartDataWorker.processListOfBuckets(result, getApplicationContext());
            setChartData(data);
        }

        @Override
        public void onError(String error) {
            Log.i(TAG, error);
        }

    }


    private final class OnGotAllDataListener implements ResultListener<List<Bucket>> {

        @Override
        public void onSuccess(List<Bucket> result) {
            mAvailableHistory = ChartDataWorker.getAvailableMonthsForSpinner(result, getApplicationContext());

            if (mAvailableHistory == null) {
                showLongToast("We can't get your activities data.");
                return;
            }

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    initSpinners();
                }
            });
        }

        @Override
        public void onError(String error) {
            Log.e(TAG, error);
        }
    }
}
