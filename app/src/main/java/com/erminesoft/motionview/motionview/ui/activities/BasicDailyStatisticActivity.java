package com.erminesoft.motionview.motionview.ui.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.erminesoft.motionview.motionview.R;
import com.erminesoft.motionview.motionview.bridge.EventBridge;
import com.erminesoft.motionview.motionview.util.TimeWorker;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.Field;

import java.util.List;

public abstract class BasicDailyStatisticActivity extends GenericActivity
        implements EventBridge {
    protected static final int DAILY_GOAL = 10000;

    protected TextView mStepsTextView;
    protected TextView mCaloriesTextView;
    protected TextView mTimeTextView;
    protected TextView mSpeedTextView;
    protected TextView mDistanceTextView;
    protected ProgressBar mProgressBar;

    protected BarChart mHourStepsChart;
    protected PieChart mCaloriesChart;

    protected long mTimestamp;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    protected void initCharts(long timestamp) {
        mTimestamp = timestamp;

        mHourStepsChart = (BarChart) findViewById(R.id.activity_main_hours_chart);
        mCaloriesChart = (PieChart) findViewById(R.id.activity_main_calories_chart);

        if (TimeWorker.isCurrentDay(timestamp)) {

        }
    }

    @Override
    public void onTotalTimeChanged(List<DataPoint> dataPoints) {
        int totalActivityTime = 0;

        for (DataPoint dataPoint : dataPoints) {
            int activityType = dataPoint.getValue(Field.FIELD_ACTIVITY).asInt();

            // TODO: CHANGE ACTIVITY TYPE
            if (activityType == 3) {
                totalActivityTime = dataPoint.getValue(Field.FIELD_DURATION).asInt();
            }
        }

        mTimeTextView.setText(TimeWorker.processMillisecondsToString(totalActivityTime, this));
    }

    @Override
    public void onDistanceChanged(List<DataPoint> dataPoints) {
        int distance = 0;

        if (dataPoints.size() > 0) {
            DataPoint dataPoint = dataPoints.get(0);

            distance = (int) dataPoint.getValue(Field.FIELD_DISTANCE).asFloat();
        }

        mDistanceTextView.setText(getString(R.string.total_distance_format, distance));
    }

    @Override
    public void onCaloriesChanged(List<DataPoint> dataPoints) {
        int calories = 0;

        if (dataPoints.size() > 0) {
            DataPoint dataPoint = dataPoints.get(0);

            calories = (int) dataPoint.getValue(Field.FIELD_CALORIES).asFloat();
        }

        mCaloriesTextView.setText(getString(R.string.total_calories_format, calories));
    }

    @Override
    public void onStepsChanged(List<DataPoint> dataPoints) {
        int steps = 0;

        if (dataPoints.size() > 0) {
            DataPoint datapoint = dataPoints.get(0);

            steps = datapoint.getValue(Field.FIELD_STEPS).asInt();
        }

        mStepsTextView.setText(getString(R.string.total_steps_text_format, steps));
    }

    @Override
    public void onSpeedChanged(List<DataPoint> dataPoints) {
        float speed = 0;

        if (dataPoints.size() > 0) {
            DataPoint datapoint = dataPoints.get(0);

            speed = datapoint.getValue(Field.FIELD_AVERAGE).asFloat();
        }

        mSpeedTextView.setText(getString(R.string.avg_speed_format, speed));
    }

}
