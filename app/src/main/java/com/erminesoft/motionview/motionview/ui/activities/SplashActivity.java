package com.erminesoft.motionview.motionview.ui.activities;


import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;

import com.erminesoft.motionview.motionview.R;
import com.erminesoft.motionview.motionview.core.MVApplication;
import com.google.android.gms.common.api.GoogleApiClient;


public class SplashActivity extends GenericActivity {
    String TAG = "SplashActivity--";

    private MVApplication mAplication;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);


        mAplication = (MVApplication) getApplication();
        mAplication.getGoogleClientHelper().buildGoogleApiClient(new GoogleApiClient.ConnectionCallbacks() {
            @Override
            public void onConnected(@Nullable Bundle bundle) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        checkConnectivity();
                    }
                }, 5000L);
            }

            @Override
            public void onConnectionSuspended(int i) {

            }
        });
    }

    private void checkConnectivity() {

        /*if (!ConnectivityChecker.isPlayServiceArePresents(getApplicationContext())) {
            showShortToast("Play Services are missed");
            return;
        }

        if (!ConnectivityChecker.isNetworkAvailable(getApplicationContext())) {
            Toast.makeText(getBaseContext(), "Check internet connection", Toast.LENGTH_LONG).show();
            return;
        }

        if (!ConnectivityChecker.bluetoothCheckConnection(BluetoothAdapter.getDefaultAdapter())) {
            Toast.makeText(getBaseContext(), "Check bluetooth", Toast.LENGTH_SHORT).show();
            return;
        }*/

        MainActivity.start(this);

    }
}
