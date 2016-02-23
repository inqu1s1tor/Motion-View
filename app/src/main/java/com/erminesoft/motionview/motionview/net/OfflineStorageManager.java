package com.erminesoft.motionview.motionview.net;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.erminesoft.motionview.motionview.core.callback.BucketsResultListener;
import com.erminesoft.motionview.motionview.core.callback.DataChangedListener;
import com.erminesoft.motionview.motionview.util.ChartDataWorker;
import com.erminesoft.motionview.motionview.util.TimeWorker;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.FitnessActivities;
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
import java.util.Date;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

class OfflineStorageManager {
    private static final String NO_DATA_ERROR = "No data per day";

    private final Executor mExecutor;
    private final Handler mHandler;
    private GoogleApiClient mClient;

    public OfflineStorageManager() {
        mExecutor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() - 1);
        mHandler = new Handler(Looper.getMainLooper());
    }

    public void setClient(GoogleApiClient client) {
        mClient = client;
    }

    public void getStepsPerDayFromHistory(final DataChangedListener listener) {
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

                int steps = 0;
                if (!dataSet.getDataPoints().isEmpty()) {
                    final DataPoint dataPoint = dataSet.getDataPoints().get(0);
                    steps = dataPoint.getValue(Field.FIELD_STEPS).asInt();
                }

                final int finalSteps = steps;
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        listener.onStepsChanged(finalSteps);
                    }
                });
            }
        });
    }

    public void getCaloriesPerDay(final DataChangedListener listener) {
        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                DailyTotalResult result = Fitness.HistoryApi
                        .readDailyTotal(mClient, DataType.TYPE_CALORIES_EXPENDED).await();

                if (!result.getStatus().isSuccess() ||
                        result.getTotal() == null) {
                    listener.onError(NO_DATA_ERROR);
                    return;
                }

                DataSet dataSet = result.getTotal();

                float calories = 0;
                if (!dataSet.getDataPoints().isEmpty()) {
                    final DataPoint dataPoint = dataSet.getDataPoints().get(0);
                    calories = dataPoint.getValue(Field.FIELD_CALORIES).asFloat();
                }

                final float finalCalories = calories;
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        listener.onCaloriesChanged(finalCalories);
                    }
                });

            }
        });
    }

    public void getActiveTimePerDay(final DataChangedListener listener) {
        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                DailyTotalResult result = Fitness.HistoryApi
                        .readDailyTotal(mClient, DataType.TYPE_ACTIVITY_SEGMENT).await();

                if (!result.getStatus().isSuccess() ||
                        result.getTotal() == null) {
                    listener.onError(NO_DATA_ERROR);
                    return;
                }

                DataSet dataSet = result.getTotal();

                int totalTime = 0;

                for (DataPoint dataPoint : dataSet.getDataPoints()) {
                    if (dataPoint.getValue(Field.FIELD_ACTIVITY).asInt() ==
                            FitnessActivities.zzdm(FitnessActivities.STILL)) {
                        continue;
                    }

                    totalTime += dataPoint.getValue(Field.FIELD_DURATION).asInt();
                }

                final int finalTime = totalTime;
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        listener.onTimeChanged(finalTime);
                    }
                });

            }
        });
    }

    public void getAverageSpeedPerDay(final DataChangedListener listener) {
        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                DailyTotalResult result = Fitness.HistoryApi
                        .readDailyTotal(mClient, DataType.TYPE_SPEED).await();

                if (!result.getStatus().isSuccess() ||
                        result.getTotal() == null) {
                    listener.onError(NO_DATA_ERROR);
                    return;
                }

                DataSet dataSet = result.getTotal();

                float avgSpeed = 0;
                if (!dataSet.getDataPoints().isEmpty()) {
                    final DataPoint dataPoint = dataSet.getDataPoints().get(0);
                    avgSpeed = dataPoint.getValue(Field.FIELD_AVERAGE).asFloat();
                }

                final float finalSpeed = avgSpeed;
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        listener.onSpeedChanged(finalSpeed);
                    }
                });

            }
        });
    }

    public void getDistancePerDay(final DataChangedListener listener) {
        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                DailyTotalResult result = Fitness.HistoryApi
                        .readDailyTotal(mClient, DataType.TYPE_DISTANCE_DELTA).await();

                if (!result.getStatus().isSuccess() ||
                        result.getTotal() == null) {
                    listener.onError(NO_DATA_ERROR);
                    return;
                }

                DataSet dataSet = result.getTotal();

                float distance = 0;
                if (!dataSet.getDataPoints().isEmpty()) {
                    final DataPoint dataPoint = dataSet.getDataPoints().get(0);
                    distance = dataPoint.getValue(Field.FIELD_DISTANCE).asFloat();
                }

                final float finalDistance = distance;
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        listener.onDistanceChanged(finalDistance);
                    }
                });

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
                                           final BucketsResultListener resultListener) {
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

    public void getDataForAllTime(final BucketsResultListener resultListener) {
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

    public void saveUserHeight(int heightCentimeters) {
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

    public void saveUserWeight(float weight) {
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

    public void getDataPerDay(DataChangedListener listener) {
        getStepsPerDayFromHistory(listener);
        getDistancePerDay(listener);
        getCaloriesPerDay(listener);
        getActiveTimePerDay(listener);
        getAverageSpeedPerDay(listener);
    }
}
