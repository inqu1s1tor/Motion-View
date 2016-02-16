package com.erminesoft.motionview.motionview.net;

import android.support.annotation.NonNull;

import com.erminesoft.motionview.motionview.core.callback.ResultListener;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.data.Bucket;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataSource;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.request.DataReadRequest;
import com.google.android.gms.fitness.result.DataReadResult;

import java.util.Calendar;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

class OfflineStorageManager {
    private static final String NO_DATA_ERROR = "No data per day";

    private final Executor mExecutor;
    private GoogleApiClient mClient;

    OfflineStorageManager() {
        mExecutor = Executors.newSingleThreadExecutor();
    }

    public void setClient(GoogleApiClient client) {
        mClient = client;
    }

    public void getStepsPerDayFromHistory(final ResultListener<Integer> listener) {
        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                DataReadResult readResult = Fitness.HistoryApi.
                        readData(mClient, generateReadRequestForDay()).await();

                if (readResult.getBuckets().size() == 0 &&
                        readResult.getDataSets().size() == 0) {
                    listener.onError(NO_DATA_ERROR);
                }

                Bucket bucket = readResult.getBuckets().get(0);
                DataSet dataSet = bucket.getDataSets().get(0);
                DataPoint dataPoint = dataSet.getDataPoints().get(0);

                listener.onSuccess(dataPoint.getValue(Field.FIELD_STEPS).asInt());
            }
        });
    }

    private DataReadRequest generateReadRequestForDay() {
        Calendar calendar = Calendar.getInstance();

        long endTime = calendar.getTimeInMillis();

        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        long startTime = calendar.getTimeInMillis();

        return new DataReadRequest.Builder()
                .aggregate(DataType.TYPE_STEP_COUNT_DELTA, DataType.AGGREGATE_STEP_COUNT_DELTA)
                .bucketByTime(1, TimeUnit.DAYS)
                .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
                .build();
    }

    public Status insertSteps(final DataPoint dataPoint) {
        DataSet dataSet = generateDataSet(dataPoint);

        return Fitness.HistoryApi.insertData(mClient, dataSet).await();
    }

    private DataSet generateDataSet(DataPoint dataPoint) {
        DataSource dataSource = new DataSource.Builder()
                .setType(DataSource.TYPE_RAW)
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

        return dataSet;
    }

    public void getDataPerMonthFromHistory(int month, final ResultListener<List<Bucket>> resultListener) {
        DataReadRequest request = generateReadRequestForMonth(month);

        Fitness.HistoryApi.readData(mClient, request)
                .setResultCallback(new ResultCallback<DataReadResult>() {
                    @Override
                    public void onResult(@NonNull DataReadResult dataReadResult) {
                        if (dataReadResult.getBuckets().size() == 0) {
                            resultListener.onError("Empty datasets");
                            return;
                        }

                        resultListener.onSuccess(dataReadResult.getBuckets());
                    }
                });
    }

    private DataReadRequest generateReadRequestForMonth(int month) {
        Calendar calendar = Calendar.getInstance();
        int currentMonth = calendar.get(Calendar.MONTH);

        calendar.set(Calendar.MONTH, month + 1);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);

        long endTime = currentMonth == month ?
                System.currentTimeMillis() :
                calendar.getTimeInMillis();

        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, 1);

        long startTime = calendar.getTimeInMillis();
        return new DataReadRequest.Builder()
                .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
                .aggregate(DataType.TYPE_STEP_COUNT_DELTA, DataType.AGGREGATE_STEP_COUNT_DELTA)
                .bucketByTime(1, TimeUnit.DAYS)
                .build();
    }
}
