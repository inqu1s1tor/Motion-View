package com.erminesoft.motionview.motionview.ui.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.erminesoft.motionview.motionview.core.callback.ResultCallback;
import com.erminesoft.motionview.motionview.util.DataSetsWorker;
import com.google.android.gms.fitness.data.DataSet;

import java.util.List;

public class TodayFragment extends BaseDailyStatisticFragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mTimestamp = System.currentTimeMillis();
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();

        mGoogleClientFacade.registerListenerForStepCounter(new ResultCallback() {
            @Override
            public void onSuccess(Object dataSets) {
                if (!(dataSets instanceof List<?>)){
                    onError("WRONG DATA");
                    return;
                }

                DataSetsWorker.processDataSets((List<DataSet>) dataSets, TodayFragment.this);
            }

            @Override
            public void onError(String error) {
                Log.e(TAG, error);
            }

        });
    }

    @Override
    public void onStop() {
        super.onStop();

        mGoogleClientFacade.unregisterListener();
    }
}
