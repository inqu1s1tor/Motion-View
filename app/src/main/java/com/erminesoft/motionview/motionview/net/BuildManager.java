package com.erminesoft.motionview.motionview.net;

import android.app.Activity;
import android.content.IntentSender;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;

import com.erminesoft.motionview.motionview.ui.fragments.ErrorDialogFragment;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.fitness.Fitness;

class BuildManager {

    private boolean mResolvingError = false;
    private GoogleApiClient mClient;

    public GoogleApiClient buildGoogleApiClient(
            final FragmentActivity fragmentActivity,
            GoogleApiClient.ConnectionCallbacks connectionCallbacks) {
        mClient = new GoogleApiClient.Builder(fragmentActivity)
                .addApi(Fitness.SENSORS_API)
                .addApi(Fitness.RECORDING_API)
                .addApi(Fitness.HISTORY_API)
                .addApi(Fitness.BLE_API)
                .addScope(new Scope(Scopes.FITNESS_ACTIVITY_READ_WRITE))
                .addConnectionCallbacks(connectionCallbacks)
                .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        if (mResolvingError) {
                            return;
                        }

                        if (connectionResult.hasResolution()) {
                            try {
                                mResolvingError = true;
                                connectionResult.startResolutionForResult(
                                        fragmentActivity, ErrorDialogFragment.REQUEST_RESOLVE_ERROR);
                            } catch (IntentSender.SendIntentException e) {
                                mClient.connect();
                            }
                        } else {
                            showErrorDialog(fragmentActivity, connectionResult.getErrorCode());
                            mResolvingError = true;
                        }
                    }
                })
                .build();

        if (!mResolvingError) {
            mClient.connect();
        }

        return mClient;
    }

    public void tryConnectClient(GoogleApiClient client, int resultCode) {
        mResolvingError = false;
        if (resultCode == Activity.RESULT_OK) {
            if (!client.isConnecting() &&
                    !client.isConnected()) {
                client.connect();
            }
        }
    }

    private void showErrorDialog(FragmentActivity fragmentActivity, int errorCode) {
        ErrorDialogFragment.showErrorDialog(fragmentActivity, errorCode);
    }

    public void onDialogDismissed() {
        mResolvingError = false;
    }
}
