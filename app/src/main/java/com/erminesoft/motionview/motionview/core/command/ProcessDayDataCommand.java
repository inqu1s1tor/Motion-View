package com.erminesoft.motionview.motionview.core.command;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.erminesoft.motionview.motionview.core.bridge.EventBridge;
import com.erminesoft.motionview.motionview.core.callback.DataChangedListener;
import com.erminesoft.motionview.motionview.core.callback.ResultCallback;
import com.erminesoft.motionview.motionview.net.GoogleClientFacade;
import com.erminesoft.motionview.motionview.util.TimeWorker;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataType;

import java.util.ArrayList;
import java.util.List;

public class ProcessDayDataCommand extends GenericCommand {
    private static final String TAG = ProcessDayDataCommand.class.getSimpleName();
    private static final String EVENT_BRIDGE_KEY = "eventView_KEY";
    private static final String GOOGLE_CLIENT_KEY = "googleClientFACADE";
    private static final String TIMESTAMP_KEY = "timestamp";

    private final Handler mHandler;
    private EventBridge mEventView;

    public ProcessDayDataCommand() {
        mHandler = new Handler(Looper.getMainLooper());
    }

    public static Bundle generateBundle(EventBridge eventView,
                                        GoogleClientFacade googleClientFacade,
                                        long timestamp) {
        Bundle bundle = new Bundle();

        bundle.putSerializable(Command.TRANSPORT_KEY, CommandType.PROCESS_DAY_DATA);
        bundle.putSerializable(EVENT_BRIDGE_KEY, eventView);
        bundle.putSerializable(GOOGLE_CLIENT_KEY, googleClientFacade);
        bundle.putLong(TIMESTAMP_KEY, timestamp);

        return bundle;
    }

    @Override
    public void execute(ResultCallback callback, Bundle bundle) {
        super.execute(callback, bundle);

        if (bundle.equals(Bundle.EMPTY)) {
            callback.onError("EMPTY BUNDLE");
            return;
        }

        mEventView = (EventBridge) bundle.getSerializable(EVENT_BRIDGE_KEY);
        GoogleClientFacade googleClientFacade =
                (GoogleClientFacade) bundle.getSerializable(GOOGLE_CLIENT_KEY);

        if (googleClientFacade == null) {
            callback.onError("EMPTY GOOGLE CLIENT FACADE");
            return;
        }

        long timestamp = bundle.getLong(TIMESTAMP_KEY);

        googleClientFacade.getDataPerDay(
                TimeWorker.getDay(timestamp),
                TimeWorker.getMonth(timestamp),
                TimeWorker.getYear(timestamp),
                new DataChangedListenerImpl());
    }

    private void processDataSets(List<DataSet> dataSets) {
        List<Runnable> runnableList = new ArrayList<>();

        for (DataSet dataSet : dataSets) {
            DataType dataType = dataSet.getDataType();
            final List<DataPoint> dataPoints = dataSet.getDataPoints();

            Runnable runnable = null;

            if (dataType.equals(DataType.AGGREGATE_ACTIVITY_SUMMARY)) {
                runnable = new Runnable() {
                    @Override
                    public void run() {
                        mEventView.onTotalTimeChanged(dataPoints);
                    }
                };
            }

            if (dataType.equals(DataType.AGGREGATE_CALORIES_EXPENDED)) {
                runnable = new Runnable() {
                    @Override
                    public void run() {
                        mEventView.onCaloriesChanged(dataPoints);
                    }
                };
            }

            if (dataType.equals(DataType.AGGREGATE_DISTANCE_DELTA)) {
                runnable = new Runnable() {
                    @Override
                    public void run() {
                        mEventView.onDistanceChanged(dataPoints);

                    }
                };
            }

            if (dataType.equals(DataType.AGGREGATE_STEP_COUNT_DELTA)) {
                runnable = new Runnable() {
                    @Override
                    public void run() {
                        mEventView.onStepsChanged(dataPoints);
                    }
                };
            }

            runnableList.add(runnable);
        }

        for (Runnable runnable : runnableList) {
            if (isDenied()) {
                return;
            }

            mHandler.post(runnable);
        }
    }


    private final class DataChangedListenerImpl implements DataChangedListener {

        @Override
        public void onSuccess(final List<DataSet> dataSets) {
            processDataSets(dataSets);
        }

        @Override
        public void onError(String error) {
            Log.e(TAG, error);
        }

    }
}