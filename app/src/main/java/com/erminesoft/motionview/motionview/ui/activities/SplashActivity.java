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
        if (checkConnectivity()) {
            mGoogleFitnessFacade.buildGoogleApiClient(this, new GoogleConnectionCallback());
        } else {
            finish();
        }
    }

    private class GoogleConnectionCallback implements GoogleApiClient.ConnectionCallbacks {
        @Override
        public void onConnected(@Nullable Bundle bundle) {
            mGoogleFitnessFacade.subscribe();
            MainFragmentActivity.start(SplashActivity.this);
            finish();
        }
        @Override
        public void onConnectionSuspended(int i) {
        }
    }

    private boolean checkConnectivity() {

        if (!ConnectivityChecker.isPlayServiceArePresents(this)) {
            showLongToast("Play Services are missed");
            return false;
        }

        if(!ConnectivityChecker.isNetworkAvailable(this)) {
            showLongToast("Wi-Fi/Internet is not active.");
            return false;
        }

        return true;
    }

}
