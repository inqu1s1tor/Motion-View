package com.erminesoft.motionview.motionview.net;


import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.request.BleScanCallback;
import com.google.android.gms.fitness.request.StartBleScanRequest;

class BluetoothManager {

    private BleScanCallback scanCallback;
    private ResultCallback<Status> resultStatusCallback;
    private StartBleScanRequest request;
    private GoogleApiClient mGoogleApiClient;

    void setClient(GoogleApiClient client) {
        mGoogleApiClient = client;
    }

    void setBleScanCallback(BleScanCallback scanCallback) {
        this.scanCallback = scanCallback;
    }

    void setResultCallback(ResultCallback<Status> resultStatusCallback) {
        this.resultStatusCallback = resultStatusCallback;
    }

    void setRequest(int timeout) {
        if (scanCallback == null) {
            return;
        }
        this.request = new StartBleScanRequest.Builder()
                .setTimeoutSecs(timeout)
                .setBleScanCallback(scanCallback)
                .setDataTypes(DataType.AGGREGATE_STEP_COUNT_DELTA)
                .setDataTypes(DataType.AGGREGATE_ACTIVITY_SUMMARY)
                .setDataTypes(DataType.AGGREGATE_LOCATION_BOUNDING_BOX)
                .setDataTypes(DataType.AGGREGATE_POWER_SUMMARY)
                .setDataTypes(DataType.AGGREGATE_DISTANCE_DELTA)
                .setDataTypes(DataType.AGGREGATE_WEIGHT_SUMMARY)
                .setDataTypes(DataType.TYPE_ACTIVITY_SAMPLE)
                .setDataTypes(DataType.TYPE_ACTIVITY_SEGMENT)
                .setDataTypes(DataType.TYPE_LOCATION_TRACK)
                .setDataTypes(DataType.TYPE_LOCATION_SAMPLE)
                .setDataTypes(DataType.TYPE_WORKOUT_EXERCISE)
                .setDataTypes(DataType.TYPE_STEP_COUNT_DELTA)
                .setDataTypes(DataType.TYPE_STEP_COUNT_CUMULATIVE)
                .setDataTypes(DataType.TYPE_STEP_COUNT_CADENCE)
                .setDataTypes(DataType.TYPE_WEIGHT)
                .setDataTypes(DataType.TYPE_POWER_SAMPLE)
                .setDataTypes(DataType.TYPE_CALORIES_EXPENDED)
                .setDataTypes(DataType.TYPE_DISTANCE_DELTA)
                .setDataTypes(DataType.TYPE_BASAL_METABOLIC_RATE)
                .setDataTypes(DataType.TYPE_HEART_RATE_BPM)
                .build();
    }

    void startBleScan() {
        if (this.request == null) {
            return;
        }
        PendingResult<Status> pendingResult = Fitness.BleApi.startBleScan(mGoogleApiClient, request);
        pendingResult.setResultCallback(resultStatusCallback);
    }

}












