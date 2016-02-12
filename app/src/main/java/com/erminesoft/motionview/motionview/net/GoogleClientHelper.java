package com.erminesoft.motionview.motionview.net;

import android.app.Activity;
import android.content.IntentSender;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.erminesoft.motionview.motionview.core.callback.ResultListener;
import com.erminesoft.motionview.motionview.ui.fragments.ErrorDialogFragment;
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
    private boolean mResolvingError = false;

    private final Executor mExecutor;

    private GoogleApiClient mClient;

    private OnDataPointListener mListener;

    public GoogleClientHelper() {
        mExecutor = Executors.newSingleThreadExecutor();
    }

    public void buildGoogleApiClient(
            final FragmentActivity fragmentActivity,
            GoogleApiClient.ConnectionCallbacks connectionCallbacks) {
        mClient = new GoogleApiClient.Builder(fragmentActivity)
                .addApi(Fitness.SENSORS_API)
                .addApi(Fitness.RECORDING_API)
                .addApi(Fitness.HISTORY_API)
                .addApi(Fitness.BLE_API)
                .addScope(new Scope(Scopes.FITNESS_ACTIVITY_READ_WRITE))
                .addConnectionCallbacks(connectionCallbacks)
                .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        if (mResolvingError) {
                            return;
                        }

                        if (connectionResult.hasResolution()) {
                            try {
                                mResolvingError = true;
                                connectionResult.startResolutionForResult(
                                        fragmentActivity, ErrorDialogFragment.REQUEST_RESOLVE_ERROR);
                            } catch (IntentSender.SendIntentException e) {
                                mClient.connect();
                            }
                        } else {
                            showErrorDialog(fragmentActivity, connectionResult.getErrorCode());
                            mResolvingError = true;
                        }
                    }
                })
                .build();

        if (!mResolvingError) {
            mClient.connect();
        }
    }

    public void tryConnectClient(int resultCode) {
        mResolvingError = false;
        if (resultCode == Activity.RESULT_OK) {
            if (!mClient.isConnecting() &&
                    !mClient.isConnected()) {
                mClient.connect();
            }
        }
    }

    public void onDialogDismissed() {
        mResolvingError = false;
    }

    private void showErrorDialog(FragmentActivity fragmentActivity, int errorCode) {
        ErrorDialogFragment dialogFragment = new ErrorDialogFragment();
        Bundle args = new Bundle();
        args.putInt(ErrorDialogFragment.DIALOG_ERROR, errorCode);
        dialogFragment.setArguments(args);
        dialogFragment.show(fragmentActivity.getSupportFragmentManager(), ErrorDialogFragment.DIALOG_ERROR);
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
                updateStepsInHistory(dataPoint);

                getStepsPerDayFromHistory(new ResultListener<Integer>() {
                    @Override
                    public void onSuccess(@Nullable Integer result) {
                        resultListener.onSuccess(result);
                    }

                    @Override
                    public void onError(String error) {

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
                            Log.i(TAG, "Listener registered " + mClient.isConnected());
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