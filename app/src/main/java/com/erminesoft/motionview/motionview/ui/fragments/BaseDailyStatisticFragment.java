package com.erminesoft.motionview.motionview.ui.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.erminesoft.motionview.motionview.R;
import com.erminesoft.motionview.motionview.bridge.EventBridge;
import com.erminesoft.motionview.motionview.core.callback.BucketsResultListener;
import com.erminesoft.motionview.motionview.util.ChartDataWorker;
import com.erminesoft.motionview.motionview.util.TimeWorker;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.CombinedData;
import com.google.android.gms.fitness.data.Bucket;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.Field;

import java.util.List;

import static com.github.mikephil.charting.charts.CombinedChart.DrawOrder;

public abstract class BaseDailyStatisticFragment extends GenericFragment implements EventBridge {
    private static final int DAILY_GOAL = 10000;

    protected TextView mStepsTextView;
    protected TextView mCaloriesTextView;
    protected TextView mTimeTextView;
    protected TextView mDistanceTextView;
    protected ProgressBar mProgressBar;

    protected CombinedChart mCombinedChart;
    protected PieChart mActivitiesChart;

    protected long mTimestamp;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_today, container, false);

        mStepsTextView = (TextView) view.findViewById(R.id.steps_text_view);
        mCaloriesTextView = (TextView) view.findViewById(R.id.calories_text_view);
        mTimeTextView = (TextView) view.findViewById(R.id.total_time_text_view);
        mDistanceTextView = (TextView) view.findViewById(R.id.distance_text_view);

        mProgressBar = (ProgressBar) view.findViewById(R.id.daily_progress_bar);
        mProgressBar.setMax(DAILY_GOAL);

        mCombinedChart = (CombinedChart) view.findViewById(R.id.fragment_today_hours_chart);
        mActivitiesChart = (PieChart) view.findViewById(R.id.fragment_today_activities_chart);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        initCharts();
    }

    private void initCharts() {
        initCombinedChart();
        initActivitiesPieChart();

        setDataForCombinedChart();
    }

    private void initCombinedChart() {
        mCombinedChart.setDescription("");
        mCombinedChart.setDrawGridBackground(false);
        mCombinedChart.setDrawBarShadow(false);

        mCombinedChart.setAutoScaleMinMaxEnabled(false);
        mCombinedChart.setDoubleTapToZoomEnabled(false);

        mCombinedChart.setDrawOrder(new DrawOrder[]{
                DrawOrder.BAR,
                DrawOrder.BUBBLE,
                DrawOrder.CANDLE,
                DrawOrder.LINE,
                DrawOrder.SCATTER
        });

        YAxis rightAxis = mCombinedChart.getAxisRight();
        rightAxis.setDrawGridLines(false);
        rightAxis.setAxisMinValue(0f);

        YAxis leftAxis = mCombinedChart.getAxisLeft();
        leftAxis.setDrawGridLines(false);
        leftAxis.setAxisMinValue(0f);

        XAxis xAxis = mCombinedChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTH_SIDED);
    }

    private void initActivitiesPieChart() {
        mActivitiesChart.setUsePercentValues(true);
        mActivitiesChart.setDescription("");

        mActivitiesChart.setDrawHoleEnabled(true);
        mActivitiesChart.setHoleColor(Color.TRANSPARENT);

        mActivitiesChart.setTransparentCircleColor(Color.WHITE);
        mActivitiesChart.setTransparentCircleAlpha(110);

        mActivitiesChart.setHoleRadius(58f);
        mActivitiesChart.setTransparentCircleRadius(61f);

        mActivitiesChart.setDrawCenterText(true);

        mActivitiesChart.setRotationAngle(0);
        mActivitiesChart.setRotationEnabled(true);
        mActivitiesChart.setHighlightPerTapEnabled(true);

        mActivitiesChart.animateY(1400, Easing.EasingOption.EaseInOutQuad);
    }

    private void setDataForCombinedChart() {
        mGoogleClientFacade.getHoursDataPerDay(mTimestamp, new BucketsResultListener() {

            @Override
            public void onSuccess(List<Bucket> buckets) {
                CombinedData data = new CombinedData(new String[]{"3", "6", "9", "12", "15", "18", "21", "24"});
                data.setData(ChartDataWorker.processStepsBuckets(buckets));
                data.setData(ChartDataWorker.processCaloriesData(buckets));

                mCombinedChart.setData(data);
                onCombinedChartDataSet();
            }

            @Override
            public void onError(String error) {
                Log.e(TAG, error);
            }
        });
    }

    private void onCombinedChartDataSet() {
        mCombinedChart.invalidate();
        mCombinedChart.animateXY(1000, 1000);
    }

    private void setDataForActivitiesChart(List<DataPoint> dataPoints) {
        mActivitiesChart.setData(ChartDataWorker.processActivitiesData(dataPoints));
    }

    @Override
    public void onTotalTimeChanged(List<DataPoint> dataPoints) {
        int totalActivityTime = 0;

        setDataForActivitiesChart(dataPoints);

        for (DataPoint dataPoint : dataPoints) {
            int activityType = dataPoint.getValue(Field.FIELD_ACTIVITY).asInt();

            if (activityType != 3) {
                totalActivityTime = dataPoint.getValue(Field.FIELD_DURATION).asInt();
            }
        }

        if (isResumed()) {
            mTimeTextView.setText(TimeWorker.processMillisecondsToString(totalActivityTime, getContext()));
        }
    }

    @Override
    public void onDistanceChanged(List<DataPoint> dataPoints) {
        int distance = 0;

        if (dataPoints.size() > 0) {
            DataPoint dataPoint = dataPoints.get(0);

            distance = (int) dataPoint.getValue(Field.FIELD_DISTANCE).asFloat();
        }

        if (isResumed()) {
            mDistanceTextView.setText(getString(R.string.total_distance_format, distance));
        }
    }

    @Override
    public void onCaloriesChanged(List<DataPoint> dataPoints) {
        int calories = 0;

        if (dataPoints.size() > 0) {
            DataPoint dataPoint = dataPoints.get(0);

            calories = (int) dataPoint.getValue(Field.FIELD_CALORIES).asFloat();
        }

        if (isResumed()) {
            mCaloriesTextView.setText(getString(R.string.total_calories_format, calories));
        }
    }

    @Override
    public void onStepsChanged(List<DataPoint> dataPoints) {
        int steps = 0;

        if (dataPoints.size() > 0) {
            DataPoint datapoint = dataPoints.get(0);

            steps = datapoint.getValue(Field.FIELD_STEPS).asInt();
        }

        if (isResumed()) {
            mProgressBar.setProgress(steps);
            mStepsTextView.setText(getString(R.string.total_steps_text_format, steps));
        }
    }
}
