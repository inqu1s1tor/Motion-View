package com.erminesoft.motionview.motionview.net.plus;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;

class PlusOperationsManager {
    private GoogleApiClient mClient;

    void setClient(GoogleApiClient client) {
        mClient = client;
    }

    Person getCurrentPlusProfile() {
        return Plus.PeopleApi.getCurrentPerson(mClient).freeze();
    }
}