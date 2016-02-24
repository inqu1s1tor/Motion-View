package com.erminesoft.motionview.motionview.ui.activities;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.erminesoft.motionview.motionview.R;
import com.erminesoft.motionview.motionview.bridge.SettingsBridge;
import com.erminesoft.motionview.motionview.storage.SharedDataManager;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.fitness.data.BleDevice;
import com.google.android.gms.fitness.request.BleScanCallback;

import java.util.ArrayList;
import java.util.List;

public class SettingsActivity extends GenericActivity implements SettingsBridge {

    private List<String> devicesArray = new ArrayList<>();
    private ArrayAdapter adapter;
    private ListView btDeviceList;
    private ProgressBar pBar;
    private Button scanBtDevices;

    private TextView userWeightHeader;
    private EditText userWeightText;
    private EditText userHeightText;


    public static void start(Activity activity) {
        activity.startActivity(new Intent(activity, SettingsActivity.class));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        setHomeAsUpEnabled();
        initSettings();

        Log.d("!!!!", "" + mSharedDataManager.readLong(SharedDataManager.FIRST_INSTALL_TIME));
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
                    noResults();
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
                btDeviceList.setVisibility(View.GONE);
                pBar.setVisibility(View.VISIBLE);
                mGoogleClientFacade.startBleScan();
            }
        });
    }


    public void initSettings() {
        initGender();
        initWeight();
        initHeight();
        initCleanHistory();
        initLoginToSocial();
        initBtooth();
        initDisconnectOfSocialNetworks();
        initConnectedSocialNetworks();
    }

    private void noResults() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                pBar.setVisibility(View.GONE);
                btDeviceList.setVisibility(View.VISIBLE);
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
                btDeviceList.setVisibility(View.VISIBLE);
                devicesArray.add(device.getName() + "  " + device.getAddress());
                adapter.notifyDataSetChanged();

            }
        });

    }

    @Override
    public void initGender() {

    }

    @Override
    public void initWeight() {
        userWeightHeader = (TextView) findViewById(R.id.settings_user_weight_height_header);
        userWeightHeader.setText(getString(R.string.settings_user_weight_height_header));

        userWeightText = (EditText) findViewById(R.id.settings_user_weight);
        userWeightText.setText("70");
        userWeightText.requestFocus();
    }

    @Override
    public void initHeight() {
        userHeightText = (EditText) findViewById(R.id.settings_user_height);
        userHeightText.setText("170");
        userHeightText.requestFocus();
        userHeightText.clearFocus();
    }


    @Override
    public void initCleanHistory() {

    }

    @Override
    public void initLoginToSocial() {

    }

    @Override
    public void initBtooth() {
        btDeviceList = (ListView) findViewById(R.id.bt_available_devices);
        pBar = (ProgressBar) findViewById(R.id.bt_scan_progress);
        scanBtDevices = (Button) findViewById(R.id.bt_scan_button);
        adapter = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, devicesArray);
        btDeviceList.setAdapter(adapter);
    }

    @Override
    public void initDisconnectOfSocialNetworks() {

    }

    @Override
    public void initConnectedSocialNetworks() {

    }
}
