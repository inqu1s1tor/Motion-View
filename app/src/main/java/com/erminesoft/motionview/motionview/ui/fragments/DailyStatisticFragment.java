package com.erminesoft.motionview.motionview.ui.fragments;

import android.os.Bundle;

import com.google.android.gms.fitness.data.DataSet;

import java.util.ArrayList;
import java.util.List;

public class DailyStatisticFragment extends GenericFragment {

    private static final String DATASETS_EXTRA = "datasets_Extra";
    private static final String TIMESTAMP_EXTRA = "timestamp_Extra";

    public static Bundle buildArgs(List<DataSet> datasets, long timestamp) {
        Bundle args = new Bundle();
        args.putParcelableArrayList(DATASETS_EXTRA, (ArrayList<DataSet>) datasets);
        args.putLong(TIMESTAMP_EXTRA, timestamp);
        return args;
    }



}
