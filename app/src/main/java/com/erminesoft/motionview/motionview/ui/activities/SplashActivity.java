package com.erminesoft.motionview.motionview.ui.activities;


import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import com.erminesoft.motionview.motionview.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;


public class SplashActivity extends Activity{

    private BluetoothAdapter mBluetoothAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isPlayServiceArePresents(getBaseContext())) {
                    if(isNetworkAvailable()){
                        if(bluetoothCheckConnection(mBluetoothAdapter)){
                            Intent intent = new Intent(getBaseContext(), MainActivity.class);
                            startActivity(intent);
                        } else {
                            Toast.makeText(getBaseContext(), "Check bluetooth", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getBaseContext(), "Check internet connection", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(getBaseContext(),"Play Services are missed", Toast.LENGTH_LONG).show();
                }
            }
        }, 5000L);

    }

    private boolean bluetoothCheckConnection(BluetoothAdapter mBluetoothAdapter){
        return BluetoothAdapter.STATE_ON == mBluetoothAdapter.getState();
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


    private boolean isPlayServiceArePresents(Context context) {
        int statusCode = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(context);
        return statusCode == ConnectionResult.SUCCESS;
    }

}
