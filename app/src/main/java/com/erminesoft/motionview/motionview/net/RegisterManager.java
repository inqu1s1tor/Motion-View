package com.erminesoft.motionview.motionview.net;

import com.erminesoft.motionview.motionview.core.callback.DataChangedListener;
import com.erminesoft.motionview.motionview.util.TimeWorker;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.request.OnDataPointListener;
import com.google.android.gms.fitness.request.SensorRequest;

import java.util.concurrent.TimeUnit;

class RegisterManager {
    private GoogleApiClient mClient;

    private OfflineStorageManager mOfflineStorageManager;
    private OnDataPointListener mSensorResultListener;

    public RegisterManager(OfflineStorageManager manager) {
        mOfflineStorageManager = manager;
    }

    public void setClient(GoogleApiClient client) {
        mClient = client;
    }

    public void registerListener(DataType dataType,
                                 final DataChangedListener resultListenerFromActivity) {
        mSensorResultListener = new OnDataPointListener() {
            @Override
            public void onDataPoint(final DataPoint dataPoint) {
                mOfflineStorageManager.insertSteps(dataPoint);

                mOfflineStorageManager.getDataPerDay(
                        TimeWorker.getCurrentDay(),
                        TimeWorker.getCurrentMonth(),
                        TimeWorker.getCurrentYear(),
                        resultListenerFromActivity);
            }
        };

        Fitness.SensorsApi.add(mClient, new SensorRequest.Builder()
                        .setDataType(dataType)
                        .setFastestRate(1, TimeUnit.SECONDS)
                        .setAccuracyMode(SensorRequest.ACCURACY_MODE_HIGH)
                        .build(),
                mSensorResultListener);
    }

    public void unregisterListener() {
        Fitness.SensorsApi.remove(mClient, mSensorResultListener);
    }

}
