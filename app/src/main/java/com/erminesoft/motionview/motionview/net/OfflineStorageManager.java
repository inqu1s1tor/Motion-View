package com.erminesoft.motionview.motionview.net;

import android.support.annotation.NonNull;
import android.widget.Toast;

import com.erminesoft.motionview.motionview.core.callback.ResultListener;
import com.erminesoft.motionview.motionview.util.ChartDataWorker;
import com.erminesoft.motionview.motionview.util.TimeWorker;
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
import com.google.android.gms.fitness.request.DataDeleteRequest;
import com.google.android.gms.fitness.request.DataReadRequest;
import com.google.android.gms.fitness.result.DailyTotalResult;
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

    public OfflineStorageManager() {
        mExecutor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() - 1);
    }

    public void setClient(GoogleApiClient client) {
        mClient = client;
    }

    public void getStepsPerDayFromHistory(final ResultListener<Integer> listener) {
        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                DailyTotalResult readResult = Fitness.HistoryApi.
                        readDailyTotal(mClient, DataType.TYPE_STEP_COUNT_DELTA).await();

                if (!readResult.getStatus().isSuccess() ||
                        readResult.getTotal() == null) {
                    listener.onError(NO_DATA_ERROR);
                    return;
                }

                DataSet dataSet = readResult.getTotal();

                if (dataSet.getDataPoints().isEmpty()) {
                    listener.onSuccess(0);
                    return;
                }

                DataPoint dataPoint = dataSet.getDataPoints().get(0);

                listener.onSuccess(dataPoint.getValue(Field.FIELD_STEPS).asInt());
            }
        });
    }

    public void insertSteps(final DataPoint dataPoint) {
        DataSet dataSet = generateDataSet(dataPoint);

        Fitness.HistoryApi.insertData(mClient, dataSet);
    }

    private DataSet generateDataSet(DataPoint dataPoint) {
        DataSource dataSource = new DataSource.Builder()
                .setType(DataSource.TYPE_RAW)
                .setAppPackageName(mClient.getContext())
                .setDataType(dataPoint.getDataType())
                .setAppPackageName(mClient.getContext())
                .setDevice(dataPoint.getDataSource().getDevice())
                .setName(mClient.getContext().getPackageName() + " - steps")
                .setType(DataSource.TYPE_DERIVED)
                .build();

        final DataSet dataSet = DataSet.create(dataSource);

        DataPoint generatedDataPoint = DataPoint.create(dataSource);
        generatedDataPoint.getValue(Field.FIELD_STEPS)
                .setInt(dataPoint.getValue(Field.FIELD_STEPS).asInt());

        long startTime = dataPoint.getStartTime(TimeUnit.MILLISECONDS);
        long endTime = dataPoint.getEndTime(TimeUnit.MILLISECONDS);
        generatedDataPoint.setTimeInterval(startTime, endTime, TimeUnit.MILLISECONDS);

        dataSet.add(generatedDataPoint);

        return dataSet;
    }

    public void getDataPerMonthFromHistory(final ChartDataWorker.Month month, final int year,
                                           final ResultListener<List<Bucket>> resultListener) {
        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                DataReadRequest request = generateReadRequestForMonth(month, year);

                DataReadResult readResult = Fitness.HistoryApi.readData(mClient, request).await();

                if (isReadResultEmpty(readResult)) {
                    resultListener.onError(NO_DATA_ERROR);
                    return;
                }

                resultListener.onSuccess(readResult.getBuckets());
            }
        });
    }

    private boolean isReadResultEmpty(DataReadResult readResult) {
        return readResult.getBuckets().size() == 0 && readResult.getDataSets().size() == 0;
    }

    private DataReadRequest generateReadRequestForMonth(ChartDataWorker.Month month, int year) {
        Calendar calendar = Calendar.getInstance();
        int currentMonth = TimeWorker.getCurrentMonth();

        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month.getIndex() + 1);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);

        long endTime = currentMonth == month.getIndex() ?
                System.currentTimeMillis() :
                calendar.getTimeInMillis();

        calendar.set(Calendar.MONTH, month.getIndex());

        long startTime = calendar.getTimeInMillis();
        return new DataReadRequest.Builder()
                .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
                .aggregate(DataType.TYPE_STEP_COUNT_DELTA, DataType.AGGREGATE_STEP_COUNT_DELTA)
                .bucketByTime(1, TimeUnit.DAYS)
                .build();
    }

    public void getDataForAllTime(final ResultListener<List<Bucket>> resultListener) {
        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                Calendar calendar = Calendar.getInstance();
                long endTime = calendar.getTimeInMillis();

                calendar.add(Calendar.YEAR, -2);
                long startTime = calendar.getTimeInMillis();

                DataReadRequest request = new DataReadRequest.Builder()
                        .aggregate(DataType.TYPE_STEP_COUNT_DELTA, DataType.AGGREGATE_STEP_COUNT_DELTA)
                        .bucketByTime(1, TimeUnit.DAYS)
                        .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
                        .build();

                DataReadResult result = Fitness.HistoryApi
                        .readData(mClient, request).await(30, TimeUnit.SECONDS);

                if (!result.getStatus().isSuccess()) {
                    resultListener.onError(NO_DATA_ERROR);
                }

                resultListener.onSuccess(result.getBuckets());
            }
        });

    }

    public void clearHistory() {
        Calendar calendar = Calendar.getInstance();

        long endTime = calendar.getTimeInMillis();
        calendar.add(Calendar.MONTH, -1);
        long startTime = calendar.getTimeInMillis();

        Fitness.HistoryApi.deleteData(mClient, new DataDeleteRequest.Builder()
                .addDataType(DataType.TYPE_STEP_COUNT_DELTA)
                .deleteAllSessions()
                .setTimeInterval(startTime, endTime, TimeUnit.MILLISECONDS)
                .build())
                .setResultCallback(new ResultCallback<Status>() {
                    @Override
                    public void onResult(@NonNull Status status) {
                        if (status.isSuccess()) {
                            Toast.makeText(mClient.getContext(), "Deleting success", Toast.LENGTH_SHORT).show();
                        } else {

                            Toast.makeText(mClient.getContext(), status.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
