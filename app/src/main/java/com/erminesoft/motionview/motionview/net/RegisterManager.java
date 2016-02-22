package com.erminesoft.motionview.motionview.net;

import android.support.annotation.NonNull;

import com.erminesoft.motionview.motionview.core.callback.DataChangedListener;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSource;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.request.DataSourcesRequest;
import com.google.android.gms.fitness.request.OnDataPointListener;
import com.google.android.gms.fitness.request.SensorRequest;
import com.google.android.gms.fitness.result.DataSourcesResult;

import java.util.List;
import java.util.concurrent.TimeUnit;

class RegisterManager {
    private GoogleApiClient mClient;

    private OfflineStorageManager mOfflineStorageManager;

    private ResultCallback<DataSourcesResult> mFoundDataSourcesResultCallback;
    private OnDataPointListener mSensorResultListener;

    public RegisterManager(OfflineStorageManager manager) {
        mOfflineStorageManager = manager;
    }

    public void setClient(GoogleApiClient client) {
        mClient = client;
    }

    public void registerListener(final DataType dataType,
                                 final DataChangedListener resultListener) {
        mFoundDataSourcesResultCallback = new ResultCallback<DataSourcesResult>() {
            @Override
            public void onResult(@NonNull DataSourcesResult dataSourcesResult) {
                registerListener(dataSourcesResult.getDataSources(), resultListener);
            }
        };

        findDataSources(dataType);
    }

    private void findDataSources(DataType dataType) {
        Fitness.SensorsApi.findDataSources(mClient, new DataSourcesRequest.Builder()
                .setDataTypes(dataType)
                .setDataSourceTypes(DataSource.TYPE_DERIVED)
                .build())
                .setResultCallback(mFoundDataSourcesResultCallback);
    }

    public void registerListener(List<DataSource> dataSources,
                                 final DataChangedListener resultListenerFromActivity) {
        for (DataSource dataSource : dataSources) {
            mSensorResultListener = new OnDataPointListener() {
                @Override
                public void onDataPoint(final DataPoint dataPoint) {
                    mOfflineStorageManager.insertSteps(dataPoint);

                    mOfflineStorageManager.getStepsPerDayFromHistory(resultListenerFromActivity);
                    mOfflineStorageManager.getActiveTimePerDay(resultListenerFromActivity);
                    mOfflineStorageManager.getAverageSpeedPerDay(resultListenerFromActivity);
                    mOfflineStorageManager.getCaloriesPerDay(resultListenerFromActivity);
                    mOfflineStorageManager.getDistancePerDay(resultListenerFromActivity);
                }
            };

            Fitness.SensorsApi.add(mClient, new SensorRequest.Builder()
                            .setDataSource(dataSource)
                            .setDataType(dataSource.getDataType())
                            .setMaxDeliveryLatency(100, TimeUnit.MILLISECONDS)
                            .setSamplingRate(1, TimeUnit.SECONDS)
                            .setAccuracyMode(SensorRequest.ACCURACY_MODE_HIGH)
                            .build(),
                    mSensorResultListener);
        }
    }

    public void unregisterListener() {
        Fitness.SensorsApi.remove(mClient, mSensorResultListener);
    }

}
