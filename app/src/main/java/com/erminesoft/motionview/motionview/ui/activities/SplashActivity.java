package com.erminesoft.motionview.motionview.ui.activities;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

import com.erminesoft.motionview.motionview.R;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.fitness.data.BleDevice;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.request.BleScanCallback;
import com.google.android.gms.fitness.request.StartBleScanRequest;


public class SplashActivity extends GenericActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        getMVapplication()
                .getGoogleClientHelper()
                .buildGoogleApiClient(this, new GoogleConnectionCallback());

        BleScanCallback scanCallback = new BleScanCallback() {
            @Override
            public void onDeviceFound(BleDevice device) {
                // A device that provides the requested data types is available
                Log.d(TAG, "Nearest device : " + device.getName());
            }
            @Override
            public void onScanStopped() {
                // The scan timed out or was interrupted
                Log.d(TAG,"Scan stopped");
            }
        };

        ResultCallback<Status> resultCallback = new ResultCallback<Status>() {
            @Override
            public void onResult(Status status) {
                Log.d(TAG, " Scan result : " + status.toString());
                Log.d(TAG, " Scan result : " + status.getStatusMessage());
                Log.d(TAG, " Scan result : " + status.getStatus());
            }
        };

        StartBleScanRequest request = new StartBleScanRequest.Builder()
                .setTimeoutSecs(10)
                .setBleScanCallback(scanCallback)

                .setDataTypes(DataType.TYPE_ACTIVITY_SAMPLE,
                        DataType.AGGREGATE_STEP_COUNT_DELTA)

                .setDataTypes(DataType.AGGREGATE_ACTIVITY_SUMMARY)
                .setDataTypes(DataType.AGGREGATE_LOCATION_BOUNDING_BOX)
                .setDataTypes(DataType.AGGREGATE_POWER_SUMMARY)
                .setDataTypes(DataType.AGGREGATE_DISTANCE_DELTA)
                .setDataTypes(DataType.AGGREGATE_WEIGHT_SUMMARY)

                .setDataTypes(DataType.TYPE_ACTIVITY_SEGMENT)
                .setDataTypes(DataType.TYPE_LOCATION_TRACK)
                .setDataTypes(DataType.TYPE_WORKOUT_EXERCISE)
                .setDataTypes(DataType.TYPE_DISTANCE_DELTA,
                              DataType.TYPE_STEP_COUNT_DELTA,
                              DataType.TYPE_LOCATION_SAMPLE)
                .setDataTypes(DataType.TYPE_STEP_COUNT_CUMULATIVE)
                .setDataTypes(DataType.TYPE_STEP_COUNT_CADENCE)
                .setDataTypes(DataType.TYPE_WEIGHT)
                .setDataTypes(DataType.TYPE_POWER_SAMPLE)
                .setDataTypes(DataType.TYPE_CALORIES_EXPENDED)

                .build();

        // Закомментил, так как убрал геттер клиента из хелпера
        //PendingResult<Status> pendingResult = Fitness.BleApi.startBleScan(client, request);
        //pendingResult.setResultCallback(resultCallback);



    }

    private class GoogleConnectionCallback implements GoogleApiClient.ConnectionCallbacks {
        @Override
        public void onConnected(@Nullable Bundle bundle) {
            if (checkConnectivity()) {
                MainActivity.start(SplashActivity.this);
            }
        }

        @Override
        public void onConnectionSuspended(int i) {

        }
    }

    private boolean checkConnectivity() {

        /*if (!ConnectivityChecker.isPlayServiceArePresents(getApplicationContext())) {
            showShortToast("Play Services are missed");
            return false;
        }

        if (!ConnectivityChecker.isNetworkAvailable(getApplicationContext())) {
            Toast.makeText(getBaseContext(), "Check internet connection", Toast.LENGTH_LONG).show();
            return false;
        }

        if (!ConnectivityChecker.bluetoothCheckConnection(BluetoothAdapter.getDefaultAdapter())) {
            Toast.makeText(getBaseContext(), "Check bluetooth", Toast.LENGTH_SHORT).show();
            return false;
        }*/

        return true;
    }
}
