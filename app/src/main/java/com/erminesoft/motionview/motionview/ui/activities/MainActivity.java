package com.erminesoft.motionview.motionview.ui.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.erminesoft.motionview.motionview.R;
import com.erminesoft.motionview.motionview.core.MVApplication;
import com.erminesoft.motionview.motionview.core.callback.ResultListener;
import com.erminesoft.motionview.motionview.net.GoogleClientHelper;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.request.OnDataPointListener;

public class MainActivity extends GenericActivity {
    private static final int DAILY_GOAL = 100;

    private static final String TODAY = "TODAY";
    private static final String STEPS_TEXT_VIEW_FORMAT = "Total steps: %d";

    private TextView mDateTextView;
    private TextView mStepsTextView;
    private ProgressBar mProgressBar;
    private OnDataPointListener mListener;
    private int mTotalStepsCount = 0;

    private GoogleClientHelper mGoogleClientHelper;

    private MVApplication mApplication;

    public static void start(Activity activity) {
        activity.startActivity(new Intent(activity, MainActivity.class));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mStepsTextView = (TextView) findViewById(R.id.steps_text_view);
        mDateTextView = (TextView) findViewById(R.id.date_text_view);
        mDateTextView.setText(TODAY);

        mProgressBar = (ProgressBar) findViewById(R.id.daily_progress_bar);

        mProgressBar.setMax(DAILY_GOAL);

        mApplication = (MVApplication) getApplication();
        mGoogleClientHelper = mApplication.getGoogleClientHelper();

        mGoogleClientHelper.getStepsPerDayFromHistory(new ResultListener<Integer>() {
            @Override
            public void onResult(@Nullable Integer result) {
                if (result != null) {
                    incrementStepsCount(result);
                }
            }
        });

        mListener = new OnDataPointListener() {
            @Override
            public void onDataPoint(DataPoint dataPoint) {
                incrementStepsCount(dataPoint.getValue(Field.FIELD_STEPS).asInt());
                mGoogleClientHelper.updateDataInHistory(dataPoint, mTotalStepsCount);
            }
        };
    }

    @Override
    protected void onStart() {
        super.onStart();

        mGoogleClientHelper.unSubscribeStepCounter();
        mGoogleClientHelper.registerListenerForStepCounter(mListener);
    }

    @Override
    protected void onStop() {
        super.onStop();

        mGoogleClientHelper.unregisterListener(mListener);
        mGoogleClientHelper.subscribeForStepCounter();
    }

    private void incrementStepsCount(int steps) {
        mTotalStepsCount += steps;

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mStepsTextView.setText(String.format(STEPS_TEXT_VIEW_FORMAT, mTotalStepsCount));
                mProgressBar.setProgress(mTotalStepsCount);
            }
        });
    }
}
