package com.erminesoft.motionview.motionview.ui.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;

import com.erminesoft.motionview.motionview.R;
import com.erminesoft.motionview.motionview.core.callback.ResultCallback;
import com.erminesoft.motionview.motionview.core.command.CommandType;
import com.erminesoft.motionview.motionview.core.command.GenerateHistoryChartDataCommand;
import com.erminesoft.motionview.motionview.storage.SharedDataManager;
import com.erminesoft.motionview.motionview.util.ChartDataWorker;
import com.erminesoft.motionview.motionview.util.TimeWorker;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataType;

import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class HistoryFragment extends GenericFragment {

    private static final float MIN_VALUES = 2f;
    private static final float MAX_VALUES = 2f;
    private static final int ANIMATE_DURATION_MILLIS = 1000;
    private static final String EMPTY_STRING = "";

    private Map<Integer, List<ChartDataWorker.Month>> mAvailableHistory;

    private ArrayAdapter<Integer> mYearAdapter;
    private ArrayAdapter<ChartDataWorker.Month> mMonthAdapter;

    private BarChart mBarChart;
    private Spinner mMonthSpinner;
    private Spinner mYearSpinner;
    private ProgressBar mProgressBar;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history, container, false);

        mBarChart = (BarChart) view.findViewById(R.id.bar_chart);

        mYearSpinner = (Spinner) view.findViewById(R.id.fragment_history_year_spinner);
        mMonthSpinner = (Spinner) view.findViewById(R.id.fragment_history_month_spinner);
        mProgressBar = (ProgressBar) view.findViewById(R.id.fragment_history_progress_bar);

        initDataForSpinners();
        initChart();

        return view;
    }

    private void initDataForSpinners() {
        ChartDataWorker.init(getContext());

        long firstInstallTime = mSharedDataManager.readLong(SharedDataManager.FIRST_INSTALL_TIME);
        mAvailableHistory = ChartDataWorker.getAvailableYearsMonthsForSpinner(firstInstallTime);

        initSpinners();
    }

    private void initSpinners() {
        initAdapters();

        mYearSpinner.setAdapter(mYearAdapter);
        mMonthSpinner.setAdapter(mMonthAdapter);

        mYearSpinner.setOnItemSelectedListener(new OnYearSpinnerItemSelectedListener());
        mMonthSpinner.setOnItemSelectedListener(new OnMonthSpinnerItemSelectedListener());

        mYearSpinner.setSelection(mYearAdapter.getCount() - 1, true);
    }

    private void initAdapters() {
        mYearAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item);
        mMonthAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item);

        for (Integer year : mAvailableHistory.keySet()) {
            mYearAdapter.add(year);
        }

        mYearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mMonthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    }

    private void initChart() {
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
        mBarChart.setOnChartValueSelectedListener(new OnChartValueSelectedListenerImpl());
    }

    private void updateMonthAndChart(int position) {
        int year = getYearBySpinnerPosition(position);
        List<ChartDataWorker.Month> months = mAvailableHistory.get(year);

        mMonthAdapter.clear();
        mMonthAdapter.addAll(months);
        mMonthAdapter.notifyDataSetChanged();

        int selectedPosition = mMonthSpinner.getSelectedItemPosition();

        if (selectedPosition == AdapterView.INVALID_POSITION) {
            for (ChartDataWorker.Month month : months) {
                if (month.getIndex() != TimeWorker.getCurrentMonth()) {
                    continue;
                }

                selectedPosition = mMonthAdapter.getPosition(month);
                break;
            }
        }

        mMonthSpinner.setSelection(selectedPosition, true);
    }

    private void updateChartData(ChartDataWorker.Month item) {
        int year = getYearBySpinnerPosition(mYearSpinner.getSelectedItemPosition());

        updateChartData(item, year);
    }

    private void updateChartData(ChartDataWorker.Month currentMonth, int year) {
        Bundle bundle = GenerateHistoryChartDataCommand.generateBundle(currentMonth, year);

        mCommander.execute(bundle, new ResultCallback() {
            @Override
            public void onSuccess(Object result) {
                if (!(result instanceof BarData)) {
                    onError("WRONG DATA");
                    return;
                }

                BarData data = (BarData) result;
                setChartData(data);
            }

            @Override
            public void onError(String error) {
                Log.e(TAG, error);
            }
        });
    }

    private int getYearBySpinnerPosition(int position) {
        Calendar calendar = Calendar.getInstance();
        int currentYear = calendar.get(Calendar.YEAR);
        int yearsDelta = mAvailableHistory.size() - 1 - position;
        return currentYear - yearsDelta;
    }

    private void setChartData(final BarData data) {
        if (mProgressBar.getVisibility() != View.GONE) {
            mProgressBar.setVisibility(View.GONE);
        }

        mBarChart.clear();
        mBarChart.setData(data);

        mBarChart.setVisibleXRange(MIN_VALUES, MAX_VALUES);
        mBarChart.moveViewToX(data.getXValCount());
        mBarChart.animateY(ANIMATE_DURATION_MILLIS);
    }

    @Override
    public void onStop() {
        super.onStop();

        mCommander.deny(CommandType.GENERATE_HISTORY_CHART_DATA);
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
            updateChartData(mMonthAdapter.getItem(position));
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
        }

    }

    private final class OnChartValueSelectedListenerImpl implements OnChartValueSelectedListener {

        @Override
        public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {
            List<DataSet> dataSets = (List<DataSet>) e.getData();
            DataSet stepDataSet = null;
            for (DataSet dataSet : dataSets) {

                if (!dataSet.getDataType().equals(DataType.TYPE_STEP_COUNT_DELTA)) {
                    continue;
                }

                stepDataSet = dataSet;

                if (dataSet.getDataPoints().isEmpty()) {
                    showShortToast(getString(R.string.no_data_for_this_day));
                    return;
                }
            }

            if (stepDataSet == null) {
                showShortToast(getString(R.string.no_data_for_this_day));
                return;
            }

            long timestamp = stepDataSet.getDataPoints().get(0).getStartTime(TimeUnit.MILLISECONDS);

            mActivity.getFragmentLauncher().launchDailyStatisticFragment(dataSets, timestamp);
            mActivity.onDailyStatisticLaunched();
        }

        @Override
        public void onNothingSelected() {
            Log.i(TAG, "Chart selection listener: Nothing selected.");
        }
    }
}
