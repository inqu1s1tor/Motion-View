package com.erminesoft.motionview.motionview.ui.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.text.DateFormat;
import java.util.Date;

public class DailyStatisticFragment extends BaseDailyStatisticFragment {
    private static final String TIMESTAMP_EXTRA = "timestamp_Extra";

    public static Bundle buildArgs(long timestamp) {
        Bundle args = new Bundle();
        args.putLong(TIMESTAMP_EXTRA, timestamp);

        return args;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Bundle args = getArguments();
        mTimestamp = args.getLong(TIMESTAMP_EXTRA);

        mActivity.setTitle(DateFormat.getDateInstance().format(new Date(mTimestamp)));

        return super.onCreateView(inflater, container, savedInstanceState);
    }
}
