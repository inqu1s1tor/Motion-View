package com.erminesoft.motionview.motionview.net;

import com.erminesoft.motionview.motionview.core.callback.ResultCallback;
import com.erminesoft.motionview.motionview.util.TimeWorker;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.request.OnDataPointListener;
import com.google.android.gms.fitness.request.SensorRequest;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

class RegisterManager {
    private GoogleApiClient mClient;

    private OfflineStorageManager mOfflineStorageManager;
    private OnDataPointListener mSensorResultListener;

    RegisterManager(OfflineStorageManager manager) {
        mOfflineStorageManager = manager;
    }

    void setClient(GoogleApiClient client) {
        mClient = client;
    }


    void registerListener(DataType dataType,
                          final ResultCallback resultListenerFromActivity) {

        mSensorResultListener = getListener(dataType, resultListenerFromActivity);

        Fitness.SensorsApi.add(mClient, new SensorRequest.Builder()
                        .setDataType(dataType)
                        .setFastestRate(1, TimeUnit.SECONDS)
                        .setAccuracyMode(SensorRequest.ACCURACY_MODE_HIGH)
                        .build(),
                mSensorResultListener);
    }

    void unregisterListener() {
        Fitness.SensorsApi.remove(mClient, mSensorResultListener);
    }

    public OnDataPointListener getListener(DataType dataType, final ResultCallback resultListenerFromActivity) {

        if (dataType.equals(DataType.TYPE_STEP_COUNT_DELTA)) {
            return new OnDataPointListener() {
                @Override
                public void onDataPoint(DataPoint dataPoint) {
                    mOfflineStorageManager.insertSteps(dataPoint);
                    mOfflineStorageManager.getDataPerDay(
                            TimeWorker.getCurrentDay(),
                            TimeWorker.getCurrentMonth(),
                            TimeWorker.getCurrentYear(),
                            resultListenerFromActivity);
                }
            };
        } else if (dataType.equals(DataType.TYPE_LOCATION_SAMPLE)) {
            return new OnDataPointListener() {
                @Override
                public void onDataPoint(DataPoint dataPoint) {
                    mOfflineStorageManager.getCurrentLocationFromHistory(Calendar.getInstance().getTimeInMillis(), resultListenerFromActivity);
                }
            };
        } else {
            return null;
        }


    }

}


        /*new OnDataPointListener() {
            @Override
            public void onDataPoint(final DataPoint dataPoint) {
                mOfflineStorageManager.insertSteps(dataPoint);
                mOfflineStorageManager.getDataPerDay(
                        TimeWorker.getCurrentDay(),
                        TimeWorker.getCurrentMonth(),
                        TimeWorker.getCurrentYear(),
                        resultListenerFromActivity);
            }
        };*/