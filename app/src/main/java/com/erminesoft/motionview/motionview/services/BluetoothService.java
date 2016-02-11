package com.erminesoft.motionview.motionview.services;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.data.BleDevice;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.request.BleScanCallback;
import com.google.android.gms.fitness.request.StartBleScanRequest;

public class BluetoothService extends Service{

    String TAG = "BluetoothService";

    public void findNearbyDevices(){

        // 1. Define a callback object
        BleScanCallback callback = new BleScanCallback() {
            @Override
            public void onDeviceFound(BleDevice device) {
                // A device that provides the requested data types is available
                // -> Claim this BLE device (See next example)
                Log.d(TAG, ""+device.getName());
            }
            @Override
            public void onScanStopped() {
                // The scan timed out or was interrupted
                Log.d(TAG,"Scan stopped");
            }
        };

        // 2. Create a scan request object:
        // - Specify the data types you're interested in
        // - Provide the callback object
            StartBleScanRequest request = new StartBleScanRequest.Builder()
                    .setDataTypes(DataType.TYPE_DISTANCE_DELTA,DataType.TYPE_STEP_COUNT_DELTA,DataType.TYPE_LOCATION_SAMPLE)
                    .setBleScanCallback(callback)
                    .build();

        // 3. Invoke the Bluetooth Low Energy API with:
        // - The Google API client
        // - The scan request
            //PendingResult<Status> pendingResult =
                    //Fitness.BleApi.startBleScan(mClient, request);

        // 4. Check the result (see other examples)


    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
