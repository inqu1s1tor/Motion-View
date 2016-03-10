package com.erminesoft.motionview.motionview.net.plus;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.model.people.Person;

public class GooglePlusFacade {
    private GoogleApiClient mClient;
    private final BuildManager mBuildManager;
    private final AuthManager mAuthManager;

    public GooglePlusFacade() {
        mBuildManager = new BuildManager();
        mAuthManager = new AuthManager();
    }

    public void buildGoogleApiClient(FragmentActivity activity) {
        mClient = mBuildManager.buildConnectClient(activity);
    }

    public void signIn(FragmentActivity activity) {
        mAuthManager.signIn(activity, mClient);
    }

    public Person getAccountInfo(Intent data) {
        return mAuthManager.getAccountInfo(data, mClient);
    }
}
