package com.erminesoft.motionview.motionview.services;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.data.BleDevice;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.request.BleScanCallback;
import com.google.android.gms.fitness.request.StartBleScanRequest;

public class BluetoothService extends Service{

    String TAG = "BluetoothService";

    @Override
    public void onCreate() {
        super.onCreate();
        findNearbyDevices();
    }

    public void findNearbyDevices(){

        GoogleApiClient client = new GoogleApiClient.Builder(getBaseContext())
                .addApi(Fitness.BLE_API)
                .build();
        client.connect();
        Log.d(TAG,""+client.isConnected());


        // 1. Define a callback object
        BleScanCallback callback = new BleScanCallback() {
            @Override
            public void onDeviceFound(BleDevice device) {
                // A device that provides the requested data types is available
                Log.d(TAG, "" + device.getName());
            }
            @Override
            public void onScanStopped() {
                // The scan timed out or was interrupted
                Log.d(TAG,"Scan stopped");
            }
        };


        StartBleScanRequest request = new StartBleScanRequest.Builder()
                .setDataTypes(DataType.TYPE_DISTANCE_DELTA,DataType.TYPE_STEP_COUNT_DELTA,DataType.TYPE_LOCATION_SAMPLE)
                .setBleScanCallback(callback)
                .build();

        // 4. Check the result (see other examples)

        PendingResult<Status> pendingResult = Fitness.BleApi.startBleScan(client, request);

    }




    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
