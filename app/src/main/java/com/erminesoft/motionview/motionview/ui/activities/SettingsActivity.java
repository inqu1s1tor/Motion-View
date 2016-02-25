package com.erminesoft.motionview.motionview.ui.activities;


import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
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
import android.widget.RadioButton;
import android.widget.RadioGroup;
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
    private static final String FITNESS_HISTORY_INTENT = "com.google.android.gms.fitness.settings.GOOGLE_FITNESS_SETTINGS";

    private List<String> devicesArray = new ArrayList<>();
    private SharedDataManager mSharedDataManager;
    private ArrayAdapter adapter;
    private ListView btDeviceList;
    private ProgressBar pBar;
    private Button scanBtDevices;

    private TextView userWeightHeader;
    private EditText userWeightText;
    private EditText userHeightText;

    private RadioButton male;
    private RadioButton female;
    private RadioGroup radioGroup;

    public static void start(Activity activity) {
        activity.startActivity(new Intent(activity, SettingsActivity.class));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSharedDataManager = getMVApplication().getSharedDataManager();
        setContentView(R.layout.activity_settings);
        initSettings();

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
                BluetoothAdapter systemService = BluetoothAdapter.getDefaultAdapter();
                if (!systemService.isEnabled()) {
                    startActivityForResult(new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE), 200);
                } else {
                    devicesArray.clear();
                    btDeviceList.setVisibility(View.GONE);
                    pBar.setVisibility(View.VISIBLE);
                    mGoogleClientFacade.startBleScan();
                }
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSharedDataManager.writeInt(SharedDataManager.USER_HEIGHT, Integer.parseInt(String.valueOf(userHeightText.getText())));
        mSharedDataManager.writeInt(SharedDataManager.USER_WEIGHT, Integer.parseInt(String.valueOf(userWeightText.getText())));

        mGoogleClientFacade.saveUserHeight(mSharedDataManager.readInt(SharedDataManager.USER_HEIGHT));
        mGoogleClientFacade.saveUserWeight((float) mSharedDataManager.readInt(SharedDataManager.USER_WEIGHT));

        radioGroup = (RadioGroup) findViewById(R.id.settings_user_radio_group);
        RadioButton r = (RadioButton) findViewById(radioGroup.getCheckedRadioButtonId());
        if (r.getText().equals("Male")) {
            mSharedDataManager.writeString(SharedDataManager.USER_GENDER, "male");
        } else if (r.getText().equals("Female")) {
            mSharedDataManager.writeString(SharedDataManager.USER_GENDER, "female");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            devicesArray.clear();
            btDeviceList.setVisibility(View.GONE);
            pBar.setVisibility(View.VISIBLE);
            mGoogleClientFacade.startBleScan();
        }
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
        male = (RadioButton) findViewById(R.id.settings_user_male_radio_button);
        female = (RadioButton) findViewById(R.id.settings_user_female_radio_button);
        if (mSharedDataManager.readString(SharedDataManager.USER_GENDER).equals("male")) {
            male.setChecked(true);
            female.setChecked(false);
        } else if (mSharedDataManager.readString(SharedDataManager.USER_GENDER).equals("female")) {
            male.setChecked(false);
            female.setChecked(true);
        } else {
            male.setChecked(true);
        }
    }

    @Override
    public void initWeight() {
        userWeightHeader = (TextView) findViewById(R.id.settings_user_weight_height_header);
        userWeightHeader.setText(getString(R.string.settings_user_weight_height_header));
        userWeightText = (EditText) findViewById(R.id.settings_user_weight);
        userWeightText.setText(String.valueOf(mSharedDataManager.readInt(SharedDataManager.USER_WEIGHT)));
        userWeightText.requestFocus();
    }

    @Override
    public void initHeight() {
        userHeightText = (EditText) findViewById(R.id.settings_user_height);
        userHeightText.setText(String.valueOf(mSharedDataManager.readInt(SharedDataManager.USER_HEIGHT)));
        userHeightText.requestFocus();
        userHeightText.clearFocus();
    }

    @Override
    public void initCleanHistory() {
        findViewById(R.id.settings_clean_history_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent fitnessSettings = new Intent(FITNESS_HISTORY_INTENT);
                startActivity(fitnessSettings);
            }
        });
    }

    @Override
    public void initLoginToSocial() {

    }

    @Override
    public void initBtooth() {
        btDeviceList = (ListView) findViewById(R.id.bt_available_devices);
        pBar = (ProgressBar) findViewById(R.id.bt_scan_progress);
        scanBtDevices = (Button) findViewById(R.id.bt_scan_button);
        adapter = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, devicesArray);
        btDeviceList.setAdapter(adapter);
    }

    @Override
    public void initDisconnectOfSocialNetworks() {

    }

    @Override
    public void initConnectedSocialNetworks() {

    }
}
