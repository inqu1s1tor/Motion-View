package com.erminesoft.motionview.motionview;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.TextView;

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
import com.google.android.gms.fitness.request.OnDataPointListener;
import com.google.android.gms.fitness.request.SensorRequest;
import com.google.android.gms.fitness.result.DailyTotalResult;
import com.google.android.gms.fitness.result.DataSourcesResult;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {
    private static String TAG = MainActivity.class.getSimpleName();

    private GoogleApiClient mClient = null;

    private TextView mTextView = null;
    private int mTotalStepsCount = 0;
    private static final Executor mExecutor = Executors.newSingleThreadExecutor();
    private OnDataPointListener mListener;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mTextView = (TextView) findViewById(R.id.text_view);
    }

    @Override
    protected void onStart() {
        super.onStart();
        buildFitnessClient();
    }

    @Override
    protected void onStop() {
        super.onStop();
        subscribeForStepCounter();
    }

    private void buildFitnessClient() {
        if (mClient != null) {
            return;
        }

        mClient = new GoogleApiClient.Builder(this)
                .addApi(Fitness.SENSORS_API)
                .addApi(Fitness.RECORDING_API)
                .addApi(Fitness.HISTORY_API)
                .addScope(new Scope(Scopes.FITNESS_ACTIVITY_READ_WRITE))
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(@Nullable Bundle bundle) {
                        Log.e(TAG, "Connected!!!");

                        onClientConnected();
                    }

                    @Override
                    public void onConnectionSuspended(int causeID) {
                        String lostConnectionCause = "Connection Lost. Cause: ";

                        switch (causeID) {
                            case GoogleApiClient.ConnectionCallbacks.CAUSE_NETWORK_LOST:
                                lostConnectionCause += "Network lost.";
                                break;
                            case GoogleApiClient.ConnectionCallbacks.CAUSE_SERVICE_DISCONNECTED:
                                lostConnectionCause += "Service Disconnected";
                                break;
                        }

                        Log.e(TAG, lostConnectionCause);
                    }
                })
                .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        Log.e(TAG, "error");
                    }
                })
                .build();
    }

    private void onClientConnected() {
        unSubscribeStepCounter();
        readAndChangeTotalStepsPerDay();
        registerListenerForStepCounter();
    }

    private void readAndChangeTotalStepsPerDay() {
        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                DailyTotalResult totalResult = Fitness
                        .HistoryApi.readDailyTotal(mClient, DataType.TYPE_STEP_COUNT_DELTA).await();

                DataSet dataSet = totalResult.getTotal();

                if (dataSet != null && dataSet.isEmpty()) {
                    return;
                }

                for (DataPoint dataPoint : dataSet.getDataPoints()) {
                    changeStepsCount(dataPoint);
                }
            }
        });
    }

    private void changeStepsCount(DataPoint dataPoint) {

        for (Field field : dataPoint.getDataType().getFields()) {
            mTotalStepsCount += dataPoint.getValue(field).asInt();
        }

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mTextView.setText(String.format("Total steps: %d", mTotalStepsCount));
            }
        });
    }

    private void subscribeForStepCounter() {
        Fitness.RecordingApi.subscribe(mClient, DataType.TYPE_STEP_COUNT_DELTA)
                .setResultCallback(new ResultCallback<Status>() {
                    @Override
                    public void onResult(@NonNull Status status) {
                        if (status.isSuccess()) {
                            Log.i(TAG, "Subscribed for step counter.");
                        } else {
                            Log.i(TAG, "Not Subscribed for step counter.");
                        }
                    }
                });
    }

    private void unSubscribeStepCounter() {
        Fitness.RecordingApi.unsubscribe(mClient, DataType.TYPE_STEP_COUNT_DELTA)
                .setResultCallback(new ResultCallback<Status>() {
                    @Override
                    public void onResult(@NonNull Status status) {
                        if (status.isSuccess()) {
                            Log.i(TAG, "unSubscribed for step counter.");
                        } else {
                            Log.i(TAG, "Not unSubscribed for step counter.");
                        }
                    }
                });
    }

    private void registerListenerForStepCounter() {
        Fitness.SensorsApi.findDataSources(mClient, new DataSourcesRequest.Builder()
                .setDataTypes(DataType.TYPE_STEP_COUNT_DELTA)
                .setDataSourceTypes(DataSource.TYPE_DERIVED)
                .build())
                .setResultCallback(new ResultCallback<DataSourcesResult>() {
                    @Override
                    public void onResult(@NonNull DataSourcesResult dataSourcesResult) {
                        for (DataSource dataSource : dataSourcesResult.getDataSources()) {
                            registerListener(dataSource);
                        }
                    }
                });
    }

    private void registerListener(DataSource dataSource) {
        mListener = new OnDataPointListener() {
            @Override
            public void onDataPoint(DataPoint dataPoint) {
                changeStepsCount(dataPoint);
                updateDataInHistory(dataPoint);
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

    private void updateDataInHistory(DataPoint dataPoint) {

    }
}
