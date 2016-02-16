package com.erminesoft.motionview.motionview.ui.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.erminesoft.motionview.motionview.R;

public abstract class BasicDailyStatisticActivity extends GenericActivity {
    protected static final int DAILY_GOAL = 1200;

    protected TextView mDateTextView;
    protected TextView mStepsTextView;
    protected ProgressBar mProgressBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mStepsTextView = (TextView) findViewById(R.id.steps_text_view);
        mDateTextView = (TextView) findViewById(R.id.date_text_view);
        mDateTextView.setText(getString(R.string.today_date_text));

        mProgressBar = (ProgressBar) findViewById(R.id.daily_progress_bar);

        mProgressBar.setMax(DAILY_GOAL);
    }
}
