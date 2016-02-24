package com.erminesoft.motionview.motionview.ui.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.erminesoft.motionview.motionview.util.DataSetsWorker;
import com.google.android.gms.fitness.data.DataSet;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DailyStatisticActivity extends BasicDailyStatisticActivity {

    private static final String DATASET_EXTRA = "dataSetExtra";
    private static final String TIMESTAMP_EXTRA = "timestamp";

    public static void start(Activity activity, List<DataSet> dataSet, long timestamp) {
        Intent intent = new Intent(activity, DailyStatisticActivity.class);
        intent.putParcelableArrayListExtra(DATASET_EXTRA, (ArrayList<DataSet>) dataSet);
        intent.putExtra(TIMESTAMP_EXTRA, timestamp);

        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        Intent intent = getIntent();
        List<DataSet> dataSets = intent.getParcelableArrayListExtra(DATASET_EXTRA);
        long timestamp = intent.getLongExtra(TIMESTAMP_EXTRA, -1L);

        processDataSets(dataSets, timestamp);
    }

    private void processDataSets(List<DataSet> dataSets, long timestamp) {
        Date date = new Date(timestamp);
        DateFormat dateFormat = DateFormat.getDateInstance();
        setTitle(dateFormat.format(date));

        DataSetsWorker.proccessDataSets(dataSets, this);
        initCharts(timestamp);
    }
}
