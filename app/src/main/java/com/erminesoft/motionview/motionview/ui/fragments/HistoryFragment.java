package com.erminesoft.motionview.motionview.ui.fragments;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.erminesoft.motionview.motionview.R;
import com.erminesoft.motionview.motionview.core.bridge.Receiver;
import com.erminesoft.motionview.motionview.core.command.CommandType;
import com.erminesoft.motionview.motionview.core.command.ExecutorType;
import com.erminesoft.motionview.motionview.core.command.GenerateHistoryChartDataCommand;
import com.erminesoft.motionview.motionview.storage.DataBuffer;
import com.erminesoft.motionview.motionview.storage.SharedDataManager;
import com.erminesoft.motionview.motionview.ui.adapters.SpinnerAdapter;
import com.erminesoft.motionview.motionview.util.ChartDataWorker;
import com.erminesoft.motionview.motionview.util.TimeWorker;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataType;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class HistoryFragment extends GenericFragment implements Receiver {

    private static final float MIN_VALUES = 2f;
    private static final float MAX_VALUES = 2f;
    private static final String EMPTY_STRING = "";

    private Map<Integer, List<ChartDataWorker.Month>> mAvailableHistory;

    private ArrayAdapter<Integer> mYearAdapter;
    private ArrayAdapter<ChartDataWorker.Month> mMonthAdapter;

    List<ChartDataWorker.Month> monthsForAdapter;

    private BarChart mBarChart;
    private Spinner mMonthSpinner;
    private Spinner mYearSpinner;
    private LinearLayout mProgressBar;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_history, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mBarChart = (BarChart) view.findViewById(R.id.bar_chart);

        mYearSpinner = (Spinner) view.findViewById(R.id.fragment_history_year_spinner);
        mMonthSpinner = (Spinner) view.findViewById(R.id.fragment_history_month_spinner);
        mProgressBar = (LinearLayout) view.findViewById(R.id.fragment_history_progress_bar);

        monthsForAdapter = new ArrayList<>();
    }

    @Override
    public void onStart() {
        super.onStart();

        DataBuffer.getInstance()
                .register(CommandType.GENERATE_HISTORY_CHART_DATA, this);

        initDataForSpinners();
        initChart();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);

        if (!hidden) {
            updateChartData(mMonthAdapter.getItem(mMonthSpinner.getSelectedItemPosition()));
        }
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

        List<Integer> years = new ArrayList<>();
        for (Integer year : mAvailableHistory.keySet()) {
            years.add(year);
        }

        mYearAdapter = new SpinnerAdapter(getContext(), R.layout.history_spinner_item,years);
        mMonthAdapter = new SpinnerAdapter(getContext(),R.layout.history_spinner_item, monthsForAdapter);

    }

    private void initChart() {

        mBarChart.setDescription(EMPTY_STRING);
        mBarChart.getLegend().setEnabled(false);

        mBarChart.setDrawGridBackground(false);
        mBarChart.getXAxis().setDrawGridLines(false);
        mBarChart.getXAxis().setTextColor(Color.GRAY);
        mBarChart.getXAxis().setTextSize(12);
        mBarChart.getXAxis().setTypeface(Typeface.createFromAsset(getActivity().getAssets(), "fonts/ROBOTO-REGULAR_0.TTF"));

        mBarChart.getAxis(YAxis.AxisDependency.LEFT).setTextColor(Color.GRAY);
        mBarChart.getAxis(YAxis.AxisDependency.RIGHT).setTextColor(Color.GRAY);
        mBarChart.getAxis(YAxis.AxisDependency.LEFT).setTypeface(Typeface.createFromAsset(getActivity().getAssets(), "fonts/ROBOTO-REGULAR_0.TTF"));
        mBarChart.getAxis(YAxis.AxisDependency.RIGHT).setTypeface(Typeface.createFromAsset(getActivity().getAssets(), "fonts/ROBOTO-REGULAR_0.TTF"));
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
        monthsForAdapter = mAvailableHistory.get(year);

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

    private void updateChartData(ChartDataWorker.Month month) {
        int year = getYearBySpinnerPosition(mYearSpinner.getSelectedItemPosition());

        updateChartData(month, year);
    }

    private void updateChartData(ChartDataWorker.Month currentMonth, int year) {
        Bundle bundle =
                GenerateHistoryChartDataCommand.generateBundle(currentMonth, year);

        mCommander.execute(bundle, ExecutorType.MAIN_FRAGMENT_ACTIVITY);
    }

    private int getYearBySpinnerPosition(int position) {
        Calendar calendar = Calendar.getInstance();
        int currentYear = calendar.get(Calendar.YEAR);
        int yearsDelta = mAvailableHistory.size() - 1 - position;
        return currentYear - yearsDelta;
    }

    @Override
    public void notify(Object data, CommandType type) {
        if (!BarData.class.isInstance(data)) {
            Log.e(TAG, "WRONG TYPE");
            return;
        }

        final BarData barData = BarData.class.cast(data);

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                setChartData(barData);
            }
        });
    }

    private void setChartData(BarData data) {
        mProgressBar.setVisibility(View.GONE);

        mBarChart.clear();
        mBarChart.setData(data);

        mBarChart.setVisibleXRange(MIN_VALUES, MAX_VALUES);
        mBarChart.moveViewToX(data.getXValCount());
    }

    @Override
    public void onStop() {
        super.onStop();

        DataBuffer.getInstance().unregister(this);
    }

    private final class OnYearSpinnerItemSelectedListener implements AdapterView.OnItemSelectedListener {

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            updateMonthAndChart(position);
            mProgressBar.setVisibility(View.VISIBLE);
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
        }

    }

    private final class OnMonthSpinnerItemSelectedListener implements AdapterView.OnItemSelectedListener {

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            updateChartData(mMonthAdapter.getItem(position));
            mProgressBar.setVisibility(View.VISIBLE);
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

            mActivity.getFragmentLauncher().launchDailyStatisticFragment(timestamp);
            mActivity.onDailyStatisticLaunched();
        }

        @Override
        public void onNothingSelected() {
            Log.i(TAG, "Chart selection listener: Nothing selected.");
        }
    }
}


