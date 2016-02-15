package com.erminesoft.motionview.motionview.net;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.erminesoft.motionview.motionview.core.callback.ResultListener;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
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
    public static final String TAG = RegisterManager.class.getSimpleName();

    private GoogleApiClient mClient;

    private OfflineStorageManager mOfflineStorageManager;

    private ResultCallback<DataSourcesResult> mFindedDataSourcesResultCallback;
    private OnDataPointListener mSensorResultListener;

    public RegisterManager(OfflineStorageManager manager) {
        mOfflineStorageManager = manager;
    }

    public void setClient(GoogleApiClient client) {
        mClient = client;
    }

    public void registerListener(final DataType dataType,
                                 final ResultListener<Integer> resultListener) {
        mFindedDataSourcesResultCallback = new ResultCallback<DataSourcesResult>() {
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
                .setResultCallback(mFindedDataSourcesResultCallback);
    }

    public void registerListener(List<DataSource> dataSources,
                                 final ResultListener<Integer> resultListenerFromActivity) {
        for (DataSource dataSource : dataSources) {
            mSensorResultListener = new OnDataPointListener() {
                @Override
                public void onDataPoint(final DataPoint dataPoint) {
                    Status resultStatus = mOfflineStorageManager.insertSteps(dataPoint);

                    if (resultStatus.isSuccess()) {
                        mOfflineStorageManager.getStepsPerDayFromHistory(new ResultListener<Integer>() {
                            @Override
                            public void onSuccess(@Nullable Integer result) {
                                resultListenerFromActivity.onSuccess(result);
                            }

                            @Override
                            public void onError(String error) {
                                Log.i(TAG, error);
                            }
                        });
                    } else {
                        resultListenerFromActivity.onError("Error reading data.");
                    }
                }
            };

            Fitness.SensorsApi.add(mClient, new SensorRequest.Builder()
                            .setDataSource(dataSource)
                            .setDataType(dataSource.getDataType())
                            .setSamplingRate(1, TimeUnit.SECONDS)
                            .setAccuracyMode(SensorRequest.ACCURACY_MODE_DEFAULT)
                            .build(),
                    mSensorResultListener);
        }
    }

    public void unregisterListener() {
        Fitness.SensorsApi.remove(mClient, mSensorResultListener);
    }

}
