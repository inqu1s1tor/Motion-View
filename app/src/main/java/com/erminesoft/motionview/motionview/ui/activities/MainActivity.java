package com.erminesoft.motionview.motionview.ui.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.erminesoft.motionview.motionview.R;
import com.erminesoft.motionview.motionview.core.callback.ResultListener;

public class MainActivity extends GenericActivity {
    private static final int DAILY_GOAL = 100;

    private TextView mDateTextView;
    private TextView mStepsTextView;
    private ProgressBar mProgressBar;

    public static void start(Activity activity) {
        activity.startActivity(new Intent(activity, MainActivity.class));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mStepsTextView = (TextView) findViewById(R.id.steps_text_view);
        mDateTextView = (TextView) findViewById(R.id.date_text_view);
        mDateTextView.setText(getString(R.string.today_date_text));

        mProgressBar = (ProgressBar) findViewById(R.id.daily_progress_bar);

        mProgressBar.setMax(DAILY_GOAL);

        mGoogleClientFacade.getStepsPerDayFromHistory(new StepsChangingListener());
    }

    @Override
    protected void onStart() {
        super.onStart();

        mGoogleClientFacade.unSubscribeStepCounter();
        mGoogleClientFacade.registerListenerForStepCounter(new StepsChangingListener());
    }

    @Override
    protected void onStop() {
        super.onStop();

        mGoogleClientFacade.unregisterListener();
        mGoogleClientFacade.subscribeForStepCounter();
    }

    private void setStepsCount(Integer result) {
        mProgressBar.setProgress(result);
        mStepsTextView.setText(String.format(getString(R.string.total_steps_text_format), result));
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
