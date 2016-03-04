package com.erminesoft.motionview.motionview.ui.activities;


import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.*;

import com.erminesoft.motionview.motionview.R;
import com.erminesoft.motionview.motionview.core.bridge.SettingsBridge;
import com.erminesoft.motionview.motionview.storage.SharedDataManager;
import com.erminesoft.motionview.motionview.util.ConnectivityChecker;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.fitness.data.BleDevice;
import com.google.android.gms.fitness.request.BleScanCallback;
import com.google.android.gms.plus.People;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.internal.PlusSession;
import com.google.android.gms.plus.model.people.Person;
import com.google.android.gms.plus.model.people.PersonBuffer;
import com.google.android.gms.signin.internal.AuthAccountResult;
import com.squareup.picasso.Picasso;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

/*import com.facebook.appevents.AppEventsLogger;
import com.facebook.share.model.ShareLinkContent;*/

public class SettingsActivity extends GenericActivity implements SettingsBridge {
    private static final String FITNESS_HISTORY_INTENT = "com.google.android.gms.fitness.settings.GOOGLE_FITNESS_SETTINGS";

    private List<String> devicesArray = new ArrayList<>();
    private SharedDataManager mSharedDataManager;
    private ArrayAdapter adapter;
    private ListView btDeviceList;
    private ProgressBar pBar;
    private Button scanBtDevices;

    private TextInputLayout userWeightTextIl;
    private EditText userWeightText;

    private EditText userHeightText;

    public static void start(Activity activity) {
        activity.startActivity(new Intent(activity, SettingsActivity.class));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        printKeyHash(this);

        mSharedDataManager = getMVApplication().getSharedDataManager();
        setContentView(R.layout.activity_settings);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Person person = Plus.PeopleApi.getCurrentPerson(mGoogleClientFacade.mClient).freeze();

        Picasso.with(this).load(person.getCover().getCoverPhoto().getUrl()).into((ImageView) findViewById(R.id.settings_profile_cover_image));
        Picasso.with(this).load(person.getImage().getUrl()).into((ImageView) findViewById(R.id.settings_avatar));

        setTitle(getString(R.string.settings));

        initSettings();

        setHomeAsUpEnabled(true);

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
                if (!ConnectivityChecker.bluetoothCheckConnection(BluetoothAdapter.getDefaultAdapter())) {
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
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onPause() {
        super.onPause();

        String weight = String.valueOf(userHeightText.getText());

        if(TextUtils.isEmpty(weight)){
            userWeightTextIl.setError("Error");
            return;
        } else {
            mSharedDataManager.writeInt(SharedDataManager.USER_HEIGHT, Integer.parseInt(weight));
        }



        mSharedDataManager.writeInt(SharedDataManager.USER_WEIGHT, Integer.parseInt(String.valueOf(userWeightText.getText())));

        mGoogleClientFacade.saveUserHeight(mSharedDataManager.readInt(SharedDataManager.USER_HEIGHT));
        mGoogleClientFacade.saveUserWeight((float) mSharedDataManager.readInt(SharedDataManager.USER_WEIGHT));
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

    @Override
    protected void onHomeButtonPressed() {
        finish();
    }

    public void initSettings() {
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
    public void initWeight() {
        userWeightText = (EditText) findViewById(R.id.settings_user_weight);
        userWeightText.setText(String.valueOf(mSharedDataManager.readInt(SharedDataManager.USER_WEIGHT)));
        userWeightTextIl = (TextInputLayout) findViewById(R.id.settings_user_weight_il);
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

    public String printKeyHash(Activity context) {
        PackageInfo packageInfo;
        String key = null;
        try {
            //getting application package name, as defined in manifest
            String packageName = context.getApplicationContext().getPackageName();

            //Retriving package info
            packageInfo = context.getPackageManager().getPackageInfo(packageName,
                    PackageManager.GET_SIGNATURES);

            Log.e("Package Name=", context.getApplicationContext().getPackageName());

            for (Signature signature : packageInfo.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                key = new String(Base64.encode(md.digest(), 0));

                // String key = new String(Base64.encodeBytes(md.digest()));
                Log.e("Key Hash=", key);
            }
        } catch (PackageManager.NameNotFoundException e1) {
            Log.e("Name not found", e1.toString());
        }
        catch (NoSuchAlgorithmException e) {
            Log.e("No such an algorithm", e.toString());
        } catch (Exception e) {
            Log.e("Exception", e.toString());
        }

        return key;
    }
}
