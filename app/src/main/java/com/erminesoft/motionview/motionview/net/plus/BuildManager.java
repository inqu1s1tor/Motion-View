package com.erminesoft.motionview.motionview.net.plus;

import android.support.v4.app.FragmentActivity;

import com.erminesoft.motionview.motionview.net.BaseBuildManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.plus.Plus;

class BuildManager extends BaseBuildManager {
    private GoogleApiClient mClient;

    GoogleApiClient buildConnectClient(final FragmentActivity activity) {


        if (mClient == null) {
            GoogleSignInOptions options = new GoogleSignInOptions
                    .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestScopes(Plus.SCOPE_PLUS_PROFILE)
                    .requestScopes(Plus.SCOPE_PLUS_LOGIN)
                    .requestScopes(new Scope(Scopes.PLUS_ME))
                    .build();

            mClient = new GoogleApiClient.Builder(activity)
                    .addApi(Auth.GOOGLE_SIGN_IN_API, options)
                    .addOnConnectionFailedListener(new OnConnectionFailedListenerImpl(mClient))
                    .build();
        }

        mClient.connect(GoogleApiClient.SIGN_IN_MODE_OPTIONAL);

        return mClient;
    }
}
