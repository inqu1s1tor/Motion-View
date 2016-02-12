package com.erminesoft.motionview.motionview.net;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.erminesoft.motionview.motionview.core.callback.ResultListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataSource;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.request.DataSourcesRequest;
import com.google.android.gms.fitness.request.DataUpdateRequest;
import com.google.android.gms.fitness.request.OnDataPointListener;
import com.google.android.gms.fitness.request.SensorRequest;
import com.google.android.gms.fitness.result.DailyTotalResult;
import com.google.android.gms.fitness.result.DataSourcesResult;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class GoogleClientHelper {
    private final static String TAG = GoogleClientHelper.class.getSimpleName();

    private GoogleApiClient mClient;
    private Context mContext;
    private Executor mExecutor = Executors.newSingleThreadExecutor();
    private OnDataPointListener mListener;

    public GoogleClientHelper(Context context) {
        mContext = context;
    }

    public void buildGoogleApiClient(FragmentActivity fragmentActivity, GoogleApiClient.ConnectionCallbacks connectionCallbacks) {
        mClient = new GoogleApiClient.Builder(mContext)
                .addApi(Fitness.SENSORS_API)
                .addApi(Fitness.RECORDING_API)
                .addApi(Fitness.HISTORY_API)
                .addApi(Fitness.BLE_API)
                .addScope(new Scope(Scopes.FITNESS_ACTIVITY_READ_WRITE))
                .addConnectionCallbacks(connectionCallbacks)
                .enableAutoManage(fragmentActivity, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        Log.i(TAG, "Connection failed: " + connectionResult.toString());
                    }
                })
                .build();

        mClient.connect();
    }

    public void getStepsPerDayFromHistory(final ResultListener<Integer> listener) {
        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                DailyTotalResult totalResult = Fitness
                        .HistoryApi.readDailyTotal(mClient, DataType.TYPE_STEP_COUNT_DELTA).await();

                DataSet dataSet = totalResult.getTotal();

                if (dataSet == null || dataSet.isEmpty()) {
                    listener.onResult(null);
                    return;
                }

                DataPoint dataPoint = dataSet.getDataPoints().get(0);

                listener.onResult(dataPoint.getValue(Field.FIELD_STEPS).asInt());
            }
        });
    }

    public void subscribeForStepCounter() {
        Fitness.RecordingApi.subscribe(mClient, DataType.TYPE_STEP_COUNT_DELTA)
                .setResultCallback(new ResultCallback<Status>() {
                    @Override
                    public void onResult(@NonNull Status status) {
                        if (status.isSuccess()) {
                            Log.i(TAG, "RECORDING_API: subscribed.");
                        } else {
                            Log.i(TAG, "RECORDING_API: error while subscribing.");
                        }
                    }
                });
    }

    public void unSubscribeStepCounter() {
        Fitness.RecordingApi.unsubscribe(mClient, DataType.TYPE_STEP_COUNT_DELTA)
                .setResultCallback(new ResultCallback<Status>() {
                    @Override
                    public void onResult(@NonNull Status status) {
                        if (status.isSuccess()) {
                            Log.i(TAG, "RECORDING_API: unSubscribed.");
                        } else {
                            Log.i(TAG, "RECORDING_API: error while unSubscribing.");
                        }
                    }
                });
    }

    public void registerListenerForStepCounter(final ResultListener<Integer> resultListener) {
        Fitness.SensorsApi.findDataSources(mClient, new DataSourcesRequest.Builder()
                .setDataTypes(DataType.TYPE_STEP_COUNT_DELTA)
                .setDataSourceTypes(DataSource.TYPE_DERIVED)
                .build())
                .setResultCallback(new ResultCallback<DataSourcesResult>() {
                    @Override
                    public void onResult(@NonNull DataSourcesResult dataSourcesResult) {
                        for (DataSource dataSource : dataSourcesResult.getDataSources()) {
                            registerListener(dataSource, resultListener);
                        }
                    }
                });
    }


    private void registerListener(DataSource dataSource, final ResultListener<Integer> resultListener) {

        mListener = new OnDataPointListener() {
            @Override
            public void onDataPoint(final DataPoint dataPoint) {
                getStepsPerDayFromHistory(new ResultListener<Integer>() {
                    @Override
                    public void onResult(@Nullable Integer result) {
                        updateStepsInHistory(dataPoint, result == null ? 0 : result);
                        resultListener.onResult(result);
                    }
                });
            }
        };

        Fitness.SensorsApi.add(mClient, new SensorRequest.Builder()
                        .setDataSource(dataSource)
                        .setDataType(dataSource.getDataType())
                        .setSamplingRate(1, TimeUnit.SECONDS)
                        .build(),
                mListener)
                .setResultCallback(new ResultCallback<Status>() {
                    @Override
                    public void onResult(@NonNull Status status) {
                        if (status.isSuccess()) {
                            Log.i(TAG, "Listener registered");
                        } else {
                            Log.i(TAG, "Listener not registered");
                        }
                    }
                });
    }


    public void unregisterListener() {
        Fitness.SensorsApi.remove(mClient, mListener)
                .setResultCallback(new ResultCallback<Status>() {
                    @Override
                    public void onResult(@NonNull Status status) {
                        if (status.isSuccess()) {
                            Log.i(TAG, "SENSORS_API: Listener unregistered.");
                        } else {
                            Log.i(TAG, "SENSORS_API: Can't unregister listener " + mListener.toString());
                        }
                    }
                });
    }

    public void updateStepsInHistory(final DataPoint dataPoint, int steps) {
        DataSource dataSource = new DataSource.Builder()
                .setType(dataPoint.getDataSource().getType())
                .setAppPackageName(mContext)
                .setDataType(dataPoint.getDataType())
                .setDevice(dataPoint.getDataSource().getDevice())
                .build();

        final DataSet dataSet = DataSet.create(dataSource);
        final DataPoint generatedDataPoint = dataSet.createDataPoint();

        generatedDataPoint.setTimestamp(dataPoint.getTimestamp(
                TimeUnit.MILLISECONDS),
                TimeUnit.MILLISECONDS);

        generatedDataPoint.setTimeInterval(
                dataPoint.getStartTime(TimeUnit.MILLISECONDS),
                dataPoint.getEndTime(TimeUnit.MILLISECONDS),
                TimeUnit.MILLISECONDS);

        generatedDataPoint.getValue(Field.FIELD_STEPS).setInt(steps);

        dataSet.add(generatedDataPoint);

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
                                public void onResult(@Nullable Integer result) {
                                    Log.i(TAG, "HISTORY_API: read - " + result + " steps.");
                                }
                            });

                        } else {
                            Log.i(TAG, "HISTORY_API: Some error when updating data : " + status.toString());
                        }
                    }
                });
    }

}
