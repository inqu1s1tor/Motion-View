package com.erminesoft.motionview.motionview.net.fitness;

import android.os.Handler;
import android.os.Looper;
import com.erminesoft.motionview.motionview.core.callback.DataChangedListener;
import com.erminesoft.motionview.motionview.core.callback.ResultCallback;
import com.erminesoft.motionview.motionview.util.ChartDataWorker;
import com.erminesoft.motionview.motionview.util.TimeWorker;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.data.*;
import com.google.android.gms.fitness.request.DataReadRequest;
import com.google.android.gms.fitness.result.DataReadResult;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

class OfflineStorageManager {
    private static final String NO_DATA_ERROR = "No data per day";

    private final Executor mExecutor;
    private final Handler mHandler;
    private GoogleApiClient mClient;

    OfflineStorageManager() {
        mExecutor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() - 1);
        mHandler = new Handler(Looper.getMainLooper());
    }

    void setClient(GoogleApiClient client) {
        mClient = client;
    }

    void insertSteps(final DataPoint dataPoint) {
        DataSet dataSet = generateDataSet(dataPoint);

        Fitness.HistoryApi.insertData(mClient, dataSet);
    }

    DataReadResult getDataPerMonthFromHistory(final ChartDataWorker.Month month, final int year) {
        DataReadRequest request = generateReadRequestForMonth(month, year);

        return Fitness.HistoryApi.readData(mClient, request).await();
    }

    DataReadResult getHoursDataPerDay(final long timeStamp) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timeStamp);

        TimeWorker.setMidnight(calendar);
        long startTime = calendar.getTimeInMillis();

        calendar.add(Calendar.DAY_OF_YEAR, 1);
        long endTime = calendar.getTimeInMillis();

        return Fitness.HistoryApi.readData(mClient, new DataReadRequest.Builder()
                .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
                .aggregate(DataType.TYPE_CALORIES_EXPENDED, DataType.AGGREGATE_CALORIES_EXPENDED)
                .aggregate(DataType.TYPE_STEP_COUNT_DELTA, DataType.AGGREGATE_STEP_COUNT_DELTA)
                .bucketByTime(3, TimeUnit.HOURS)
                .build()).await();
    }

    DataReadResult getDataPerDay(int day, int month, int year) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, day);

        if (TimeWorker.getCurrentDay() != day ||
                TimeWorker.getCurrentMonth() != month ||
                TimeWorker.getCurrentYear() != year) {
            calendar.add(Calendar.DAY_OF_YEAR, 1);
            TimeWorker.setMidnight(calendar);
            calendar.add(Calendar.MILLISECOND, -1);
        }

        long endTime = calendar.getTimeInMillis();

        calendar.set(Calendar.DAY_OF_MONTH, day);
        TimeWorker.setMidnight(calendar);

        long startTime = calendar.getTimeInMillis();

        DataReadRequest request = buildReadRequest(startTime, endTime);

        return Fitness.HistoryApi.readData(mClient, request).await();
    }


    void getDataForNow(final int day, final DataChangedListener listener) {
        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.DAY_OF_MONTH, day);
                calendar.add(Calendar.DAY_OF_MONTH, 1);
                TimeWorker.setMidnight(calendar);
                long endTime = calendar.getTimeInMillis();
                calendar.set(Calendar.DAY_OF_MONTH, day);
                TimeWorker.setMidnight(calendar);
                long startTime = calendar.getTimeInMillis();

                DataReadRequest request = buildReadRequest(startTime, endTime);

                DataReadResult result = Fitness.HistoryApi.readData(mClient, request).await();

                final List<Bucket> buckets = result.getBuckets();
                if (buckets == null || buckets.size() == 0) {
                    listener.onError(NO_DATA_ERROR);
                    return;
                }
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        listener.onSuccess(buckets.get(0).getDataSets());
                    }
                });
            }
        });
    }

    void saveUserWeight(float weight) {
        Calendar cal = Calendar.getInstance();
        Date now = new Date();
        cal.setTime(now);
        long endTime = cal.getTimeInMillis();
        cal.add(Calendar.DAY_OF_YEAR, -1);
        long startTime = cal.getTimeInMillis();

        DataSet weightDataSet = createDataForRequest(
                DataType.TYPE_WEIGHT,
                DataSource.TYPE_RAW,
                weight,
                startTime,
                endTime,
                TimeUnit.MILLISECONDS
        );

        Fitness.HistoryApi.insertData(mClient, weightDataSet);
    }

    void saveUserHeight(int heightCentimeters) {
        float height = ((float) heightCentimeters) / 100.0f;
        Calendar cal = Calendar.getInstance();
        Date now = new Date();
        cal.setTime(now);
        long endTime = cal.getTimeInMillis();
        cal.add(Calendar.DAY_OF_YEAR, -1);
        long startTime = cal.getTimeInMillis();

        DataSet heightDataSet = createDataForRequest(
                DataType.TYPE_HEIGHT,
                DataSource.TYPE_RAW,
                height,
                startTime,
                endTime,
                TimeUnit.MILLISECONDS
        );

        Fitness.HistoryApi.insertData(mClient, heightDataSet);
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
        return buildReadRequest(startTime, endTime);
    }

    private DataReadRequest buildReadRequest(long startTime, long endTime) {
        return new DataReadRequest.Builder()
                .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
                .aggregate(DataType.TYPE_STEP_COUNT_DELTA, DataType.AGGREGATE_STEP_COUNT_DELTA)
                .aggregate(DataType.TYPE_ACTIVITY_SEGMENT, DataType.AGGREGATE_ACTIVITY_SUMMARY)
                .aggregate(DataType.TYPE_DISTANCE_DELTA, DataType.AGGREGATE_DISTANCE_DELTA)
                .aggregate(DataType.TYPE_CALORIES_EXPENDED, DataType.AGGREGATE_CALORIES_EXPENDED)
                .bucketByTime(1, TimeUnit.DAYS)
                .build();
    }

    private DataSet createDataForRequest(DataType dataType,
                                         int dataSourceType,
                                         Object values,
                                         long startTime,
                                         long endTime,
                                         TimeUnit timeUnit) {
        DataSource dataSource = new DataSource.Builder()
                .setAppPackageName(mClient.getContext())
                .setDataType(dataType)
                .setType(dataSourceType)
                .build();

        DataSet dataSet = DataSet.create(dataSource);
        DataPoint dataPoint = dataSet.createDataPoint().setTimeInterval(startTime, endTime, timeUnit);

        if (values instanceof Integer) {
            dataPoint = dataPoint.setIntValues((Integer) values);
        } else {
            dataPoint = dataPoint.setFloatValues((Float) values);
        }

        dataSet.add(dataPoint);

        return dataSet;
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
}
