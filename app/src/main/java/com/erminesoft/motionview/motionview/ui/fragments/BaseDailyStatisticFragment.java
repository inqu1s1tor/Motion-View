package com.erminesoft.motionview.motionview.ui.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.erminesoft.motionview.motionview.R;
import com.erminesoft.motionview.motionview.core.bridge.Receiver;
import com.erminesoft.motionview.motionview.core.command.CommandType;
import com.erminesoft.motionview.motionview.core.command.ExecutorType;
import com.erminesoft.motionview.motionview.core.command.GenerateCombinedChartDataCommand;
import com.erminesoft.motionview.motionview.core.command.ProcessDayDataCommand;
import com.erminesoft.motionview.motionview.storage.DataBuffer;
import com.erminesoft.motionview.motionview.storage.SharedDataManager;
import com.erminesoft.motionview.motionview.ui.view.CircularProgress;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.LineData;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;

import java.util.List;

@SuppressWarnings("WeakerAccess")
abstract class BaseDailyStatisticFragment extends GenericFragment implements Receiver {
    private LineChart lineChart;
    private CircularProgress mProgress;

    protected long mTimestamp;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_today, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mProgress = (CircularProgress) view.findViewById(R.id.circular_progress);
        mProgress.setMaxProgress(mSharedDataManager.readInt(SharedDataManager.USER_DAILY_GOAL));

        lineChart = (LineChart) view.findViewById(R.id.fragment_today_hours_chart);
    }

    @Override
    public void onStart() {
        super.onStart();

        Bundle bundle = ProcessDayDataCommand.generateBundle(mTimestamp);
        mCommander.execute(bundle, ExecutorType.MAIN_FRAGMENT_ACTIVITY);

        DataBuffer.getInstance().register(CommandType.PROCESS_DAY_DATA, this);
        DataBuffer.getInstance().register(CommandType.GENERATE_COMBINED_CHART_DATA, this);

        initCharts();
    }

    protected void processData(List<DataSet> dataSets) {
        for (DataSet dataSet : dataSets) {
            DataType dataType = dataSet.getDataType();
            final List<DataPoint> dataPoints = dataSet.getDataPoints();

            if (dataType.equals(DataType.AGGREGATE_ACTIVITY_SUMMARY)) {
                onTotalTimeChanged(dataPoints);
                continue;
            }

            if (dataType.equals(DataType.AGGREGATE_STEP_COUNT_DELTA)) {
                onStepsChanged(dataPoints);
            }
        }

        mProgress.invalidate();
    }

    private void initCharts() {
        initCombinedChart();

        setDataForCombinedChart();
    }

    private void initCombinedChart() {
        lineChart.setDescription("");
        lineChart.setDrawGridBackground(false);
        lineChart.setHighlightPerDragEnabled(true);

        lineChart.setAutoScaleMinMaxEnabled(false);
        lineChart.setDoubleTapToZoomEnabled(false);

        lineChart.getLegend().setEnabled(false);

        YAxis rightAxis = lineChart.getAxisRight();
        rightAxis.setDrawGridLines(true);
        rightAxis.setAxisMinValue(0f);
        rightAxis.setStartAtZero(true);
        rightAxis.setDrawLabels(false);
        rightAxis.setDrawAxisLine(false);

        YAxis leftAxis = lineChart.getAxisLeft();
        leftAxis.setDrawGridLines(true);
        leftAxis.setAxisMinValue(0f);
        leftAxis.setDrawLabels(false);
        leftAxis.setDrawAxisLine(false);


        XAxis xAxis = lineChart.getXAxis();
        xAxis.setDrawGridLines(false);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setAvoidFirstLastClipping(true);
        xAxis.setAxisLineColor(Color.rgb(226, 138, 73));
    }

    private void setDataForCombinedChart() {
        Bundle bundle = GenerateCombinedChartDataCommand
                .generateBundle(mTimestamp);

        mCommander.execute(bundle, ExecutorType.MAIN_FRAGMENT_ACTIVITY);
    }

    @Override
    public void notify(final Object data, CommandType type) {
        switch (type) {
            case PROCESS_DAY_DATA: {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        processData((List<DataSet>) data);
                    }
                });
                break;
            }
            case GENERATE_COMBINED_CHART_DATA: {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        lineChart.setData((LineData) data);
                        onCombinedChartDataSet();
                    }
                });
                break;
            }

            default:
                Log.e(TAG, "WRONG DATA TYPE");
        }
    }

    private void onCombinedChartDataSet() {
        lineChart.invalidate();
        lineChart.animateXY(1000, 1000);
    }

    private void onTotalTimeChanged(List<DataPoint> dataPoints) {
        mProgress.clear();

        for (DataPoint dataPoint : dataPoints) {
            int activityType = dataPoint.getValue(Field.FIELD_ACTIVITY).asInt();
            int color;


            switch (activityType) {
                case 7:
                    color = Color.GREEN;

                    int time = dataPoint.getValue(Field.FIELD_DURATION).asInt();

                    addProgressPart(time, color);
                    break;
                case 8:
                    color = Color.YELLOW;

                    time = dataPoint.getValue(Field.FIELD_DURATION).asInt();

                    addProgressPart(time, color);
                    break;
            }

        }
    }

    protected void addProgressPart(int time, int color) {
        mProgress.addPart(new CircularProgress.Part(time, color));
    }

    private void onStepsChanged(List<DataPoint> dataPoints) {
        int steps = 0;

        if (dataPoints.size() > 0) {
            DataPoint datapoint = dataPoints.get(0);

            steps = datapoint.getValue(Field.FIELD_STEPS).asInt();
        }

        mProgress.setCurrentProgress(steps);
    }

    @Override
    public void onStop() {
        super.onStop();

        DataBuffer.getInstance().unregister(this);
    }
}
