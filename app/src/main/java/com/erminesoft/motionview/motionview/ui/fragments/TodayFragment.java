package com.erminesoft.motionview.motionview.ui.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.erminesoft.motionview.motionview.core.callback.DataChangedListener;
import com.erminesoft.motionview.motionview.core.callback.ResultCallback;
import com.erminesoft.motionview.motionview.core.command.CommandType;
import com.erminesoft.motionview.motionview.core.command.ProcessDayDataCommand;
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

        mGoogleClientFacade.registerListenerForStepCounter(new DataChangedListenerImpl());
        Bundle bundle = ProcessDayDataCommand
                .generateBundle(this, mGoogleClientFacade, System.currentTimeMillis());

        mCommander.execute(bundle, new ResultCallback<String>() {
            @Override
            public void onError(String s) {

            }

            @Override
            public void onSuccess(String s) {

            }
        });
    }

    @Override
    public void onStop() {
        super.onStop();

        mCommander.abort(CommandType.PROCESS_DAY_DATA);
        mGoogleClientFacade.unregisterListener();
    }

    private final class DataChangedListenerImpl implements DataChangedListener {

        @Override
        public void onSuccess(final List<DataSet> dataSets) {
        }

        @Override
        public void onError(String error) {
            Log.e(TAG, error);
        }

    }
}
