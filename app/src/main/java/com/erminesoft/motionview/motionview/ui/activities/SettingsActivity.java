package com.erminesoft.motionview.motionview.ui.activities;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

import com.erminesoft.motionview.motionview.R;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.fitness.data.BleDevice;
import com.google.android.gms.fitness.request.BleScanCallback;

public class SettingsActivity extends GenericActivity {
    public static void start(Activity activity) {
        activity.startActivity(new Intent(activity, SettingsActivity.class));
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        setHomeAsUpEnabled();

        mGoogleClientFacade.setBleScanCallback(new BleScanCallback() {

            @Override
            public void onDeviceFound(BleDevice device) {
                // A device that provides the requested data types is available
                Log.d("!!!!!!", "Nearest device : " + device.getName());
            }

            @Override
            public void onScanStopped() {
                // The scan timed out or was interrupted
                Log.d("!!!!!!", "Scan stopped");
            }
        });

        mGoogleClientFacade.setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(Status status) {
                Log.d("!!!!!!", " Scan result : " + status.toString());
                Log.d("!!!!!!", " Scan result : " + status.getStatusMessage());
                Log.d("!!!!!!", " Scan result : " + status.getStatus());
            }
        });


        mGoogleClientFacade.setRequest(60);
        mGoogleClientFacade.startBleScan();

    }
}
