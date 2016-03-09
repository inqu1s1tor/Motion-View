package com.erminesoft.motionview.motionview.net.plus;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;

class AuthManager {
    void signIn(FragmentActivity activity, GoogleApiClient client) {
        Intent intent = Auth.GoogleSignInApi.getSignInIntent(client);
        activity.startActivityForResult(intent, 1);
    }

    Person getAccountInfo(Intent data, GoogleApiClient client) {
        GoogleSignInAccount account = Auth.GoogleSignInApi.getSignInResultFromIntent(data).getSignInAccount();

        if (account == null) {
            return null;
        }

        try {
            return Plus.PeopleApi.load(client, account.getId()).await().getPersonBuffer().get(0);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
