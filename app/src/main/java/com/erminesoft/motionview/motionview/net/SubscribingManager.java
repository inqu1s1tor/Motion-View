package com.erminesoft.motionview.motionview.net;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.data.DataType;

class SubscribingManager {
    private GoogleApiClient mClient;

    public void setClient(GoogleApiClient client) {
        mClient = client;
    }

    public void subscribe() {
        Fitness.RecordingApi.subscribe(mClient, DataType.TYPE_DISTANCE_DELTA);
        Fitness.RecordingApi.subscribe(mClient, DataType.TYPE_CALORIES_EXPENDED);
        Fitness.RecordingApi.subscribe(mClient, DataType.TYPE_STEP_COUNT_DELTA);
        Fitness.RecordingApi.subscribe(mClient, DataType.TYPE_ACTIVITY_SEGMENT);
    }

    public void unsubscribe() {
        Fitness.RecordingApi.unsubscribe(mClient, DataType.TYPE_DISTANCE_DELTA);
        Fitness.RecordingApi.unsubscribe(mClient, DataType.TYPE_CALORIES_EXPENDED);
        Fitness.RecordingApi.unsubscribe(mClient, DataType.TYPE_DISTANCE_DELTA);
        Fitness.RecordingApi.unsubscribe(mClient, DataType.TYPE_ACTIVITY_SEGMENT);
    }
}
