package com.erminesoft.motionview.motionview.ui.activities;


import android.os.Bundle;
import android.support.annotation.Nullable;

import com.erminesoft.motionview.motionview.R;
import com.erminesoft.motionview.motionview.util.ConnectivityChecker;
import com.google.android.gms.common.api.GoogleApiClient;


public class SplashActivity extends GenericActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        mGoogleClientFacade.buildGoogleApiClient(this, new GoogleConnectionCallback());
    }

    private class GoogleConnectionCallback implements GoogleApiClient.ConnectionCallbacks {
        @Override
        public void onConnected(@Nullable Bundle bundle) {

            if (checkConnectivity()) {
                mGoogleClientFacade.subscribe();
                MainFragmentActivity.start(SplashActivity.this);
            } else {
                finish();
            }

            finish();
        }

        @Override
        public void onConnectionSuspended(int i) {

        }
    }


    private boolean checkConnectivity() {

        if (!ConnectivityChecker.isPlayServiceArePresents(getApplicationContext())) {
            showShortToast("Play Services are missed");
            return false;
        }
/*

        if (!ConnectivityChecker.isNetworkAvailable(getApplicationContext())) {
            Toast.makeText(getBaseContext(), "Check internet connection", Toast.LENGTH_LONG).show();
            return false;
        }
*/

        /*if (!ConnectivityChecker.bluetoothCheckConnection(BluetoothAdapter.getDefaultAdapter())) {
            Toast.makeText(getBaseContext(), "Check bluetooth", Toast.LENGTH_SHORT).show();
            return false;
        }*/

/*

        // checkout GPS connection & satellites quantity
        if (!ConnectivityChecker.isLocationActive(getApplicationContext())) {
            Toast.makeText(getBaseContext(), "Check location service", Toast.LENGTH_SHORT).show();
            return false;
        }
*/

        return true;
    }
}
