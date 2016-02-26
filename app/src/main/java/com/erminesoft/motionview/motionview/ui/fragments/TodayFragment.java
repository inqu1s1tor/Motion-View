package com.erminesoft.motionview.motionview.ui.fragments;

import android.util.Log;

import com.erminesoft.motionview.motionview.core.callback.DataChangedListener;
import com.erminesoft.motionview.motionview.util.DataSetsWorker;
import com.erminesoft.motionview.motionview.util.TimeWorker;
import com.google.android.gms.fitness.data.DataSet;

import java.util.List;

public class TodayFragment extends BaseDailyStatisticFragment {

    private DataChangedListener mListener;

    @Override
    public void onStart() {
        super.onStart();

        mListener = new DataChangedListenerImpl();

        mGoogleClientFacade.registerListenerForStepCounter(mListener);
        mGoogleClientFacade.getDataPerDay(
                TimeWorker.getCurrentDay(),
                TimeWorker.getCurrentMonth(),
                TimeWorker.getCurrentYear(),
                mListener);
    }

    @Override
    public void onStop() {
        super.onStop();

        mListener = null;
        mGoogleClientFacade.unregisterListener();
    }

    private final class DataChangedListenerImpl implements DataChangedListener {

        @Override
        public void onSuccess(final List<DataSet> dataSets) {
            DataSetsWorker.processDataSets(dataSets, TodayFragment.this);
        }

        @Override
        public void onError(String error) {
            Log.e(TAG, error);
        }

    }
}
