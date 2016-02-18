package com.erminesoft.motionview.motionview.ui.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.erminesoft.motionview.motionview.R;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;

import java.text.DateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class DailyStatisticActivity extends BasicDailyStatisticActivity {

    private static final String DATASET_EXTRA = "dataSetExtra";

    public static void start(Activity activity, DataSet dataSet) {
        Intent intent = new Intent(activity, DailyStatisticActivity.class);
        intent.putExtra(DATASET_EXTRA, dataSet);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(getString(R.string.daily_statistic_activity_name));

        setHomeAsUpEnabled();

        Intent intent = getIntent();
        DataSet dataSet = intent.getParcelableExtra(DATASET_EXTRA);

        for (DataPoint dataPoint : dataSet.getDataPoints()) {
            if (dataPoint.getDataType().equals(DataType.TYPE_STEP_COUNT_DELTA)) {
                int steps = dataPoint.getValue(Field.FIELD_STEPS).asInt();

                Date date = new Date(dataPoint.getTimestamp(TimeUnit.MILLISECONDS));
                DateFormat dateFormat = DateFormat.getDateInstance();
                mDateTextView.setText(dateFormat.format(date));

                mProgressBar.setProgress(steps);
                mStepsTextView
                        .setText(String.format(getString(R.string.total_steps_text_format), steps));
            }
        }
    }
}
