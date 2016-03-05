package com.erminesoft.motionview.motionview.net.fitness;

import android.app.Activity;
import android.support.v4.app.FragmentActivity;

import com.erminesoft.motionview.motionview.net.BaseBuildManager;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.plus.Plus;

class BuildManager extends BaseBuildManager{
    private boolean mResolvingError = false;
    private GoogleApiClient mClient;

    GoogleApiClient buildGoogleApiClient(
            final FragmentActivity fragmentActivity,
            GoogleApiClient.ConnectionCallbacks connectionCallbacks) {

        setFragmentActivity(fragmentActivity);

        mClient = new GoogleApiClient.Builder(fragmentActivity)
                .addApi(Fitness.SENSORS_API)
                .addApi(Fitness.RECORDING_API)
                .addApi(Fitness.HISTORY_API)
                .addApi(Fitness.BLE_API)
                .addApi(LocationServices.API)
                .addApi(Plus.API)
                .addScope(new Scope(Scopes.FITNESS_ACTIVITY_READ_WRITE))
                .addScope(new Scope(Scopes.FITNESS_BODY_READ_WRITE))
                .addScope(new Scope(Scopes.FITNESS_LOCATION_READ_WRITE))
                .addScope(Plus.SCOPE_PLUS_LOGIN)
                .addScope(Plus.SCOPE_PLUS_PROFILE)
                .addConnectionCallbacks(connectionCallbacks)
                .addOnConnectionFailedListener(new OnConnectionFailedListenerImpl(mClient))
                .build();

        if (!mResolvingError) {
            mClient.connect();
        }

        return mClient;
    }

    void tryConnectClient(GoogleApiClient client, int resultCode) {
        mResolvingError = false;
        if (resultCode == Activity.RESULT_OK) {
            if (!client.isConnecting() &&
                    !client.isConnected()) {
                client.connect();
            }
        }
    }
}
