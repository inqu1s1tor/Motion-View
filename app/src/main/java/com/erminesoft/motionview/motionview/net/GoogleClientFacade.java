package com.erminesoft.motionview.motionview.net;

import android.content.Context;

import com.erminesoft.motionview.motionview.core.MVApplication;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.request.BleScanCallback;
import com.google.android.gms.fitness.request.StartBleScanRequest;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.erminesoft.motionview.motionview.core.MVApplication;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.data.BleDevice;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.request.BleScanCallback;
import com.google.android.gms.fitness.request.StartBleScanRequest;

public class GoogleClientFacade {

public class BluetoothManagerFacade {

    private static final String TAG = "FACADE";
    private Context context;
    private MVApplication mAplication;
    private BleScanCallback scanCallback;
    private ResultCallback<Status> resultCallback;

    public BluetoothManagerFacade(Context cntx) {
        this.context = cntx;
    }


    public void setScanCallback(BleScanCallback clbk) {
        this.scanCallback = clbk;
    }


    public void setResultCallback(ResultCallback<Status> resClbk) {
        this.resultCallback = resClbk;
    }

    public StartBleScanRequest setRequst() {
        StartBleScanRequest request = new StartBleScanRequest.Builder()
                .setTimeoutSecs(10)
                .setBleScanCallback(this.scanCallback)

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

        return request;
    }
}
}

