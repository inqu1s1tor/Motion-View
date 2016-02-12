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
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataSource;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.request.DataUpdateRequest;
import com.google.android.gms.fitness.result.DailyTotalResult;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

class OfflineStorageManager {
    private static final String TAG = OfflineStorageManager.class.getSimpleName();

    private final Executor mExecutor;

    private GoogleApiClient mClient;

    public OfflineStorageManager() {
        mExecutor = Executors.newSingleThreadExecutor();
    }

    public void setClient(GoogleApiClient client) {
        mClient = client;
    }

    public void getStepsPerDayFromHistory(final ResultListener<Integer> listener) {
        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                DailyTotalResult totalResult = Fitness
                        .HistoryApi.readDailyTotal(mClient, DataType.TYPE_STEP_COUNT_DELTA).await();

                DataSet dataSet = totalResult.getTotal();

                if (dataSet == null || dataSet.isEmpty()) {
                    listener.onError("Empty dataset");
                    return;
                }

                DataPoint dataPoint = dataSet.getDataPoints().get(0);

                listener.onSuccess(dataPoint.getValue(Field.FIELD_STEPS).asInt());
            }
        });
    }

    public void updateStepsInHistory(final DataPoint dataPoint) {
        DataSource dataSource = new DataSource.Builder()
                .setType(dataPoint.getDataSource().getType())
                .setAppPackageName(mClient.getContext())
                .setDataType(dataPoint.getDataType())
                .setDevice(dataPoint.getDataSource().getDevice())
                .build();

        final DataSet dataSet = DataSet.create(dataSource);

        DataPoint dataPoint1 = DataPoint.create(dataSource);
        dataPoint1.getValue(Field.FIELD_STEPS).setInt(dataPoint.getValue(Field.FIELD_STEPS).asInt());
        dataPoint1.setTimestamp(dataPoint.getTimestamp(TimeUnit.MILLISECONDS), TimeUnit.MILLISECONDS);
        dataSet.add(dataPoint1);

        Fitness.HistoryApi.updateData(mClient, new DataUpdateRequest.Builder()
                .setDataSet(dataSet)
                .setTimeInterval(dataPoint.getStartTime(TimeUnit.MILLISECONDS), dataPoint.getEndTime(TimeUnit.MILLISECONDS), TimeUnit.MILLISECONDS)
                .build())
                .setResultCallback(new ResultCallback<Status>() {
                    @Override
                    public void onResult(@NonNull Status status) {
                        if (status.isSuccess()) {
                            Log.i(TAG, "HISTORY_API: Data updated.");

                            getStepsPerDayFromHistory(new ResultListener<Integer>() {
                                @Override
                                public void onSuccess(@Nullable Integer result) {
                                    Log.i(TAG, "HISTORY_API: read - " + result + " steps.");
                                }

                                @Override
                                public void onError(String error) {

                                }
                            });

                        } else {
                            Log.i(TAG, "HISTORY_API: Some error when updating data : " + status.toString());
                        }
                    }
                });
    }
}
