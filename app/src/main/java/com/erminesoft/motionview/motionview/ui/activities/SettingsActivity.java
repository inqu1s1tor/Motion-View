package com.erminesoft.motionview.motionview.ui.activities;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.erminesoft.motionview.motionview.R;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.fitness.data.BleDevice;
import com.google.android.gms.fitness.request.BleScanCallback;

import java.util.ArrayList;
import java.util.List;

public class SettingsActivity extends GenericActivity {

    private List<String> devicesArray = new ArrayList<>();
    private ArrayAdapter adapter = null;
    private ListView deviceList;
    private ProgressBar pBar;
    private Button scanBtDevices;

    public static void start(Activity activity) {
        activity.startActivity(new Intent(activity, SettingsActivity.class));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        setHomeAsUpEnabled();

        deviceList = (ListView) findViewById(R.id.bt_available_devices);
        pBar = (ProgressBar) findViewById(R.id.bt_scan_progress);
        scanBtDevices = (Button) findViewById(R.id.bt_scan_button);
        adapter = new ArrayAdapter<String>(mGoogleClientFacade.getApiClient().getContext(), R.layout.support_simple_spinner_dropdown_item, devicesArray);
        deviceList.setAdapter(adapter);

        mGoogleClientFacade.setBleScanCallback(new BleScanCallback() {
            @Override
            public void onDeviceFound(BleDevice device) {
                Log.d("!!!!!!", "Nearest device : " + device.getName());
                haveResults(device);
            }

            @Override
            public void onScanStopped() {
                Log.d("!!!!!!", "Scan stopped");
                if (devicesArray.isEmpty()) {
                    noRelusts();
                }

            }
        });

        mGoogleClientFacade.setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(Status status) {
                Log.d("!!!!!!", " Scan result : " + status.getStatus());
            }
        });

        mGoogleClientFacade.setRequest(10);

        scanBtDevices.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                devicesArray.clear();
                deviceList.setVisibility(View.GONE);
                pBar.setVisibility(View.VISIBLE);
                mGoogleClientFacade.startBleScan();
            }
        });

    }

    private void noRelusts() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                pBar.setVisibility(View.GONE);
                deviceList.setVisibility(View.VISIBLE);
                devicesArray.add("Not found any BLE device");
                adapter.notifyDataSetChanged();
            }
        });
    }

    private void haveResults(final BleDevice device) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                pBar.setVisibility(View.GONE);
                deviceList.setVisibility(View.VISIBLE);
                devicesArray.add(device.getName() + "  " + device.getAddress());
                adapter.notifyDataSetChanged();

            }
        });

    }
}
