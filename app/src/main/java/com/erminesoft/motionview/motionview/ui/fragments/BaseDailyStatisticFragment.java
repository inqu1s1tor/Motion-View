package com.erminesoft.motionview.motionview.ui.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.erminesoft.motionview.motionview.R;
import com.erminesoft.motionview.motionview.bridge.EventBridge;
import com.erminesoft.motionview.motionview.util.TimeWorker;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.Field;

import java.util.List;

public abstract class BaseDailyStatisticFragment extends GenericFragment implements EventBridge {
    private static final int DAILY_GOAL = 10000;

    protected TextView mStepsTextView;
    protected TextView mCaloriesTextView;
    protected TextView mTimeTextView;
    protected TextView mDistanceTextView;
    protected ProgressBar mProgressBar;

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

        return view;
    }

    @Override
    public void onTotalTimeChanged(List<DataPoint> dataPoints) {
        int totalActivityTime = 0;

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
