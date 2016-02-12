package com.erminesoft.motionview.motionview.net;


import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.data.BleDevice;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.request.BleScanCallback;
import com.google.android.gms.fitness.request.StartBleScanRequest;

class BluetoothManager {

    private BleScanCallback scanCallback;
    private ResultCallback<Status> resultCallback;
    private StartBleScanRequest request;
    private PendingResult<Status> pendingResult;
    private String TAG = "BluetoothManager";


    private void setBleScanCallback (){
        this.scanCallback = new BleScanCallback() {
            @Override
            public void onDeviceFound(BleDevice device) {
                // A device that provides the requested data types is available
                Log.d(TAG, "Nearest device : " + device.getName());
            }

            @Override
            public void onScanStopped() {
                // The scan timed out or was interrupted
                Log.d(TAG, "Scan stopped");
            }
        };
    }


    private void setResultCallback (){
        this.resultCallback = new ResultCallback<Status>() {
            @Override
            public void onResult(Status status) {
                Log.d(TAG, " Scan result : " + status.toString());
                Log.d(TAG, " Scan result : " + status.getStatusMessage());
                Log.d(TAG, " Scan result : " + status.getStatus());
            }
        };
    }



    private void setRequest(int timeout){
        if(scanCallback == null) { return; }
        this.request = new StartBleScanRequest.Builder()
                .setTimeoutSecs(timeout)
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
    }

    private void getScanResult(GoogleApiClient client){
        if(this.request == null){ return; }
        PendingResult<Status> pendingResult = Fitness.BleApi.startBleScan(client, request);
        pendingResult.setResultCallback(resultCallback);
    }

}
