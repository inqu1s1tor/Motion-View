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

import java.util.concurrent.TimeUnit;

class OfflineStorageManager {
    private static final String TAG = OfflineStorageManager.class.getSimpleName();
    private static final String EMPTY_DATASET_ERROR = "empty dataset";

    private GoogleApiClient mClient;

    public void setClient(GoogleApiClient client) {
        mClient = client;
    }

    public void getStepsPerDayFromHistory(final ResultListener<Integer> listener) {
        Fitness.HistoryApi.readDailyTotal(mClient, DataType.AGGREGATE_STEP_COUNT_DELTA)
                .setResultCallback(new ResultCallback<DailyTotalResult>() {
                    @Override
                    public void onResult(@NonNull DailyTotalResult result) {

                        DataSet dataSet = result.getTotal();

                        if (dataSet == null || dataSet.isEmpty()) {
                            listener.onError(EMPTY_DATASET_ERROR);
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

        DataPoint generatedDataPoint = DataPoint.create(dataSource);
        generatedDataPoint.getValue(Field.FIELD_STEPS).setInt(dataPoint.getValue(Field.FIELD_STEPS).asInt());

        long startTime = dataPoint.getStartTime(TimeUnit.MILLISECONDS);
        long endTime = dataPoint.getEndTime(TimeUnit.MILLISECONDS);
        generatedDataPoint.setTimeInterval(startTime, endTime, TimeUnit.MILLISECONDS);

        dataSet.add(generatedDataPoint);

        Fitness.HistoryApi.updateData(mClient, new DataUpdateRequest.Builder()
                .setDataSet(dataSet)
                .setTimeInterval(startTime, endTime, TimeUnit.MILLISECONDS)
                .build())
                .setResultCallback(new ResultCallback<Status>() {
                    @Override
                    public void onResult(@NonNull Status status) {
                        if (status.isSuccess()) {
                            getStepsPerDayFromHistory(new ResultListener<Integer>() {
                                @Override
                                public void onSuccess(@Nullable Integer result) {
                                    Log.i(TAG, "HISTORY_API: read - " + result + " steps.");
                                }

                                @Override
                                public void onError(String error) {
                                    Log.i(TAG, "HISTORY_API: read error - " + error);

                                    if (error.equals(EMPTY_DATASET_ERROR)) {
                                        insertStepsInHistory(dataSet);
                                    }
                                }
                            });

                        } else {
                            Log.i(TAG, "HISTORY_API: Some error when updating data : " + status.toString());
                        }
                    }
                });
    }

    private void insertStepsInHistory(final DataSet dataSet) {
        Fitness.HistoryApi.insertData(mClient, dataSet)
                .setResultCallback(new ResultCallback<Status>() {
                    @Override
                    public void onResult(@NonNull Status status) {
                        if (status.isSuccess()) {
                            Log.i(TAG, "HISTORY_API: Data input success - " + dataSet.toString());
                        } else {

                            Log.i(TAG, "HISTORY_API: Data input error - " + dataSet.toString());
                        }
                    }
                });
    }
}
