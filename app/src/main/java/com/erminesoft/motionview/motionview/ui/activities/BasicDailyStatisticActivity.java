package com.erminesoft.motionview.motionview.ui.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.erminesoft.motionview.motionview.R;
import com.erminesoft.motionview.motionview.bridge.EventBridge;
import com.erminesoft.motionview.motionview.core.callback.DataChangedListener;
import com.erminesoft.motionview.motionview.util.TimeWorker;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataType;
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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mStepsTextView = (TextView) findViewById(R.id.steps_text_view);
        mCaloriesTextView = (TextView) findViewById(R.id.calories_text_view);
        mTimeTextView = (TextView) findViewById(R.id.total_time_text_view);
        mSpeedTextView = (TextView) findViewById(R.id.avg_speed_text_view);
        mDistanceTextView = (TextView) findViewById(R.id.distance_text_view);

        mProgressBar = (ProgressBar) findViewById(R.id.daily_progress_bar);

        mProgressBar.setMax(DAILY_GOAL);
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

    protected void processDataSets(List<DataSet> dataSets) {
        for (DataSet dataSet : dataSets) {
            DataType dataType = dataSet.getDataType();
            List<DataPoint> dataPoints = dataSet.getDataPoints();

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

            if (dataType.equals(DataType.AGGREGATE_SPEED_SUMMARY)) {
                onSpeedChanged(dataPoints);
                continue;
            }

            if (dataType.equals(DataType.AGGREGATE_STEP_COUNT_DELTA)) {
                onStepsChanged(dataPoints);
            }
        }
    }

    protected final class DataChangedListenerImpl implements DataChangedListener {

        @Override
        public void onSuccess(List<DataSet> dataSets) {
            processDataSets(dataSets);
        }

        @Override
        public void onError(String error) {
            Log.e(TAG, error);
        }

    }
}
