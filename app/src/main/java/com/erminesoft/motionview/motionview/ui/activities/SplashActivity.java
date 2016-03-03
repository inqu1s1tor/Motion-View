package com.erminesoft.motionview.motionview.ui.activities;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.util.Log;

import com.erminesoft.motionview.motionview.R;
import com.erminesoft.motionview.motionview.storage.SharedDataManager;
import com.erminesoft.motionview.motionview.util.ConnectivityChecker;
import com.erminesoft.motionview.motionview.util.DialogHelper;
import com.google.android.gms.common.api.GoogleApiClient;

public class SplashActivity extends GenericActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        if (checkConnectivity()) {
            mGoogleClientFacade.buildGoogleApiClient(this, new GoogleConnectionCallback());
        } else {
            finish();
        }
    }

    private class GoogleConnectionCallback implements GoogleApiClient.ConnectionCallbacks {
        @Override
        public void onConnected(@Nullable Bundle bundle) {
            mGoogleClientFacade.subscribe();
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
