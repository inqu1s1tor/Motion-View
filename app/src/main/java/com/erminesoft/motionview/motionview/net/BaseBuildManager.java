package com.erminesoft.motionview.motionview.net;

import android.content.IntentSender;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;

import com.erminesoft.motionview.motionview.ui.fragments.ErrorDialogFragment;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

public class BaseBuildManager {

    private FragmentActivity mActivity;
    private boolean mResolvingError;

    protected void setFragmentActivity(FragmentActivity activity) {
        mActivity = activity;
    }

    private void showErrorDialog(FragmentActivity fragmentActivity, int errorCode) {
        ErrorDialogFragment.showErrorDialog(fragmentActivity, errorCode);
    }

    public void onDialogDismissed() {
        mResolvingError = false;
    }

    protected final class OnConnectionFailedListenerImpl implements GoogleApiClient.OnConnectionFailedListener {

        private final GoogleApiClient mClient;

        public OnConnectionFailedListenerImpl(GoogleApiClient client) {
            mClient = client;
        }

        @Override
        public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
            if (mResolvingError) {
                return;
            }

            if (connectionResult.hasResolution()) {
                try {
                    mResolvingError = true;
                    connectionResult.startResolutionForResult(
                            mActivity, ErrorDialogFragment.REQUEST_RESOLVE_ERROR);
                } catch (IntentSender.SendIntentException e) {
                    mClient.connect();
                }
            } else {
                showErrorDialog(mActivity, connectionResult.getErrorCode());
                mResolvingError = true;
            }
        }
    }
}
