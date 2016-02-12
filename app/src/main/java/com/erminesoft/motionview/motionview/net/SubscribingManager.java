package com.erminesoft.motionview.motionview.net;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.data.DataType;

class SubscribingManager {
    private static final String TAG = SubscribingManager.class.getSimpleName();

    private GoogleApiClient mClient;

    public void setClient(GoogleApiClient client) {
        mClient = client;
    }

    public void subscribeForStepCounter() {
        Fitness.RecordingApi.subscribe(mClient, DataType.TYPE_STEP_COUNT_DELTA)
                .setResultCallback(new ResultCallback<Status>() {
                    @Override
                    public void onResult(@NonNull Status status) {
                        if (status.isSuccess()) {
                            Log.i(TAG, "RECORDING_API: subscribed.");
                        } else {
                            Log.i(TAG, "RECORDING_API: error while subscribing.");
                        }
                    }
                });
    }

    public void unSubscribeStepCounter() {
        Fitness.RecordingApi.unsubscribe(mClient, DataType.TYPE_STEP_COUNT_DELTA)
                .setResultCallback(new ResultCallback<Status>() {
                    @Override
                    public void onResult(@NonNull Status status) {
                        if (status.isSuccess()) {
                            Log.i(TAG, "RECORDING_API: unSubscribed.");
                        } else {
                            Log.i(TAG, "RECORDING_API: error while unSubscribing.");
                        }
                    }
                });
    }
}
