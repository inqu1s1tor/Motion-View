package com.erminesoft.motionview.motionview.ui.fragments;

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
import com.erminesoft.motionview.motionview.core.callback.DataChangedListener;
import com.erminesoft.motionview.motionview.util.DataSetsWorker;
import com.erminesoft.motionview.motionview.util.TimeWorker;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.Field;

import java.util.List;

public class TodayFragment extends GenericFragment implements EventBridge {

    private static final int DAILY_GOAL = 10000;
    private TextView mStepsTextView;
    private TextView mCaloriesTextView;
    private TextView mTimeTextView;
    private TextView mSpeedTextView;
    private TextView mDistanceTextView;
    private ProgressBar mProgressBar;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_today, container, false);

        mStepsTextView = (TextView) view.findViewById(R.id.steps_text_view);
        mCaloriesTextView = (TextView) view.findViewById(R.id.calories_text_view);
        mTimeTextView = (TextView) view.findViewById(R.id.total_time_text_view);
        mSpeedTextView = (TextView) view.findViewById(R.id.avg_speed_text_view);
        mDistanceTextView = (TextView) view.findViewById(R.id.distance_text_view);

        mProgressBar = (ProgressBar) view.findViewById(R.id.daily_progress_bar);

        mProgressBar.setMax(DAILY_GOAL);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        mGoogleClientFacade.getDataPerDay(
                TimeWorker.getCurrentDay(),
                TimeWorker.getCurrentMonth(),
                TimeWorker.getCurrentYear(),
                new DataChangedListenerImpl());

        mGoogleClientFacade.registerListenerForStepCounter(new DataChangedListenerImpl());
    }

    @Override
    public void onStop() {
        super.onStop();

        mGoogleClientFacade.unregisterListener();
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

        mTimeTextView.setText(TimeWorker.processMillisecondsToString(totalActivityTime, getContext()));
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

    protected final class DataChangedListenerImpl implements DataChangedListener {

        @Override
        public void onSuccess(List<DataSet> dataSets) {
            if (isDetached()) {
                return;
            }

            DataSetsWorker.proccessDataSets(dataSets, TodayFragment.this);
        }

        @Override
        public void onError(String error) {
            Log.e(TAG, error);
        }

    }
}
