package com.erminesoft.motionview.motionview.net;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.data.DataType;

class SubscribingManager {
    private GoogleApiClient mClient;

    public void setClient(GoogleApiClient client) {
        mClient = client;
    }

    public void subscribeForStepCounter() {
        Fitness.RecordingApi.subscribe(mClient, DataType.TYPE_STEP_COUNT_DELTA);
    }

    public void unSubscribeStepCounter() {
        Fitness.RecordingApi.unsubscribe(mClient, DataType.TYPE_STEP_COUNT_DELTA);
    }
}
