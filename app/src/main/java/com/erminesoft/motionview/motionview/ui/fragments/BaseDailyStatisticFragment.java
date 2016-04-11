package com.erminesoft.motionview.motionview.ui.fragments;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.erminesoft.motionview.motionview.R;
import com.erminesoft.motionview.motionview.core.bridge.Receiver;
import com.erminesoft.motionview.motionview.core.command.CommandType;
import com.erminesoft.motionview.motionview.core.command.ExecutorType;
import com.erminesoft.motionview.motionview.core.command.GenerateCombinedChartDataCommand;
import com.erminesoft.motionview.motionview.core.command.ProcessDayDataCommand;
import com.erminesoft.motionview.motionview.storage.DataBuffer;
import com.erminesoft.motionview.motionview.storage.SharedDataManager;
import com.erminesoft.motionview.motionview.ui.view.CircularProgress;
import com.erminesoft.motionview.motionview.util.TimeWorker;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;

import java.util.Collections;
import java.util.List;

@SuppressWarnings("WeakerAccess")
abstract class BaseDailyStatisticFragment extends GenericFragment implements Receiver {
    private LineChart lineChart;
    private CircularProgress mProgress;
    private TextView distanceText;
    private TextView activityTimeText;
    private TextView caloriesInfoText;
    private TextView distanceTitle;
    private TextView timeTitle;
    private TextView caloriesTitle;

    protected long mTimestamp;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_today, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Typeface robotoMediumFont = Typeface.createFromAsset(getActivity().getAssets(),"fonts/ROBOTO-MEDIUM_0.TTF");


        mProgress = (CircularProgress) view.findViewById(R.id.circular_progress);
        mProgress.setMaxProgress(mSharedDataManager.readInt(SharedDataManager.USER_DAILY_GOAL));

        lineChart = (LineChart) view.findViewById(R.id.fragment_today_hours_chart);

        distanceTitle = (TextView) view.findViewById(R.id.main_fragment_distance_text);
        distanceTitle.setTypeface(robotoMediumFont);
        //distanceTitle.setTextSize(Utils.pixelsToSp(getContext(), 30));

        timeTitle = (TextView) view.findViewById(R.id.main_fragment_time_text);
        timeTitle.setTypeface(robotoMediumFont);
        //timeTitle.setTextSize(Utils.pixelsToSp(getContext(), 30));

        caloriesTitle = (TextView) view.findViewById(R.id.main_fragment_calories_text);
        caloriesTitle.setTypeface(robotoMediumFont);
        //caloriesTitle.setTextSize(Utils.pixelsToSp(getContext(), 30));

        distanceText = (TextView) view.findViewById(R.id.main_fragment_distance);
        distanceText.setTypeface(robotoMediumFont);
        //distanceText.setTextSize(Utils.pixelsToSp(getContext(), 40));

        activityTimeText = (TextView) view.findViewById(R.id.main_fragment_time);
        activityTimeText.setTypeface(robotoMediumFont);
        //activityTimeText.setTextSize(Utils.pixelsToSp(getContext(), 40));

        caloriesInfoText = (TextView) view.findViewById(R.id.main_fragment_calories);
        caloriesInfoText.setTypeface(robotoMediumFont);
        //caloriesInfoText.setTextSize(Utils.pixelsToSp(getContext(), 40));


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

            if (dataType.equals(DataType.AGGREGATE_CALORIES_EXPENDED)) {
                onCaloriesChanged(dataPoints);
                continue;
            }

            if (dataType.equals(DataType.AGGREGATE_DISTANCE_DELTA)) {
                onDistanceChanged(dataPoints);
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
        lineChart.setHighlightPerDragEnabled(false);
        lineChart.setHighlightPerTapEnabled(true);

        lineChart.setPinchZoom(false);
        lineChart.setDoubleTapToZoomEnabled(false);

        lineChart.setScaleYEnabled(false);
        lineChart.setScaleXEnabled(false);

        lineChart.setAutoScaleMinMaxEnabled(false);
        lineChart.setDoubleTapToZoomEnabled(false);
        lineChart.setDrawMarkerViews(true);
        lineChart.setMarkerView(new MainChartMarker(getContext(), R.layout.main_chart_marker));

        Legend legend = lineChart.getLegend();
        legend.setCustom(Collections.singletonList(ColorTemplate.COLOR_NONE), Collections.singletonList("t"));
        legend.setFormToTextSpace(0f);
        legend.setTextColor(Color.GRAY);
        legend.setYOffset(18);
        legend.setXOffset(0);

        lineChart.getAxisRight().setEnabled(false);

        YAxis leftAxis = lineChart.getAxisLeft();
        leftAxis.setDrawGridLines(true);
        leftAxis.setAxisLineWidth(0);
        leftAxis.setAxisMinValue(0f);
        leftAxis.setDrawLabels(false);
        leftAxis.setDrawAxisLine(false);
        leftAxis.setAxisMaxValue(300);
        leftAxis.setLabelCount(3, true);
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setDrawGridLines(false);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setAvoidFirstLastClipping(true);
        xAxis.setAxisLineColor(Color.rgb(226, 138, 73));
        xAxis.setLabelsToSkip(2);
    }

    private void setDataForCombinedChart() {
        Bundle bundle = GenerateCombinedChartDataCommand
                .generateBundle(mTimestamp);

        mCommander.execute(bundle, ExecutorType.MAIN_FRAGMENT_ACTIVITY);
    }

    @Override
    public void notify(final Object data, CommandType type) {
        FragmentActivity activity = getActivity();
        if (activity == null) {
            return;
        }

        switch (type) {
            case PROCESS_DAY_DATA: {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        processData((List<DataSet>) data);
                    }
                });
                break;
            }
            case GENERATE_COMBINED_CHART_DATA: {
                activity.runOnUiThread(new Runnable() {
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

        int totalActivityTime = 0;

        for (DataPoint dataPoint : dataPoints) {
            int activityType = dataPoint.getValue(Field.FIELD_ACTIVITY).asInt();
            int color;


            switch (activityType) {
                case 7:
                    color = Color.GREEN;

                    int time = dataPoint.getValue(Field.FIELD_DURATION).asInt();
                    totalActivityTime += time;

                    addProgressPart(time, color);
                    break;
                case 8:
                    color = Color.YELLOW;

                    time = dataPoint.getValue(Field.FIELD_DURATION).asInt();
                    totalActivityTime += time;

                    addProgressPart(time, color);
                    break;
            }

        }

        activityTimeText.setText(TimeWorker.processMillisecondsToString(totalActivityTime, getContext()));
    }

    protected void addProgressPart(int time, int color) {
        mProgress.addPart(new CircularProgress.Part(time, color));
    }

    private void onDistanceChanged(List<DataPoint> dataPoints) {
        int distance = 0;

        if (dataPoints.size() > 0) {
            DataPoint dataPoint = dataPoints.get(0);

            distance = (int) dataPoint.getValue(Field.FIELD_DISTANCE).asFloat();
        }

        distanceText.setText(String.valueOf(distance));
    }

    private void onCaloriesChanged(List<DataPoint> dataPoints) {
        int calories = 0;

        if (dataPoints.size() > 0) {
            DataPoint dataPoint = dataPoints.get(0);

            calories = (int) dataPoint.getValue(Field.FIELD_CALORIES).asFloat();
        }

        caloriesInfoText.setText(String.valueOf(calories));
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

    private final class MainChartMarker extends MarkerView {

        private TextView textView;
        private ImageView imageView;

        public MainChartMarker(Context context, int marker) {
            super(context, marker);

            textView = (TextView) findViewById(R.id.marker_cal_text);
            imageView = (ImageView) findViewById(R.id.marker_image);
        }

        @Override
        public void refreshContent(Entry e, Highlight highlight) {
            textView.setText(String.valueOf((int) e.getVal()));
        }

        @Override
        public int getXOffset(float xpos) {
            return -(imageView.getWidth() / 2);
        }

        @Override
        public int getYOffset(float ypos) {
            return -getHeight() + (imageView.getHeight() / 2);
        }
    }
}
