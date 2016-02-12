package com.erminesoft.motionview.motionview.ui.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.erminesoft.motionview.motionview.R;
import com.erminesoft.motionview.motionview.core.MVApplication;
import com.erminesoft.motionview.motionview.core.callback.ResultListener;
import com.erminesoft.motionview.motionview.net.GoogleClientHelper;

public class MainActivity extends GenericActivity {
    private static final int DAILY_GOAL = 100;

    private static final String TODAY = "TODAY";
    private static final String STEPS_TEXT_VIEW_FORMAT = "Total steps: %d";

    private TextView mDateTextView;
    private TextView mStepsTextView;
    private ProgressBar mProgressBar;

    private GoogleClientHelper mGoogleClientHelper;

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

        MVApplication application = (MVApplication) getApplication();
        mGoogleClientHelper = application.getGoogleClientHelper();

        mGoogleClientHelper.getStepsPerDayFromHistory(new StepsChangingListener());
    }

    @Override
    protected void onStart() {
        super.onStart();

        mGoogleClientHelper.unSubscribeStepCounter();
        mGoogleClientHelper.registerListenerForStepCounter(new StepsChangingListener());
    }

    @Override
    protected void onStop() {
        super.onStop();

        mGoogleClientHelper.unregisterListener();
        mGoogleClientHelper.subscribeForStepCounter();
    }

    private void setStepsCount(Integer result) {
        mProgressBar.setProgress(result);
        mStepsTextView.setText(String.format(STEPS_TEXT_VIEW_FORMAT, result));
    }

    private final class StepsChangingListener implements ResultListener<Integer> {

        @Override
        public void onSuccess(@Nullable final Integer result) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    setStepsCount(result);
                }
            });
        }

        @Override
        public void onError(String error) {
            Log.i(TAG, error);
        }
    }
}
