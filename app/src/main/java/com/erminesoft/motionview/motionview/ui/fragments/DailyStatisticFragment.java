package com.erminesoft.motionview.motionview.ui.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.erminesoft.motionview.motionview.util.DataSetsWorker;
import com.google.android.gms.fitness.data.DataSet;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DailyStatisticFragment extends BaseDailyStatisticFragment {

    private static final String DATASETS_EXTRA = "datasets_Extra";
    private static final String TIMESTAMP_EXTRA = "timestamp_Extra";

    private ArrayList<DataSet> mDataSets;

    public static Bundle buildArgs(List<DataSet> datasets, long timestamp) {
        Bundle args = new Bundle();
        args.putParcelableArrayList(DATASETS_EXTRA, (ArrayList<DataSet>) datasets);
        args.putLong(TIMESTAMP_EXTRA, timestamp);
        return args;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Bundle args = getArguments();
        mDataSets = args.getParcelableArrayList(DATASETS_EXTRA);
        mTimestamp = args.getLong(TIMESTAMP_EXTRA);

        mActivity.setTitle(DateFormat.getDateInstance().format(new Date(mTimestamp)));

        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();

        DataSetsWorker.processDataSets(mDataSets, this);
    }
}
