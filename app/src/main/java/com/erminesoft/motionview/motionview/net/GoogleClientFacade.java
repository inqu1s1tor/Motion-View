package com.erminesoft.motionview.motionview.net;

import android.support.v4.app.FragmentActivity;

import com.erminesoft.motionview.motionview.core.callback.ResultListener;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.fitness.data.DataType;

public class GoogleClientFacade {

    private GoogleApiClient mClient;
    private BuildManager mBuildManager;
    private RegisterManager mRegisterManager;
    private OfflineStorageManager mOfflineStorageManager;
    private SubscribingManager mSubscribingManager;

    public GoogleClientFacade() {
        mBuildManager = new BuildManager();
        mOfflineStorageManager = new OfflineStorageManager();
        mSubscribingManager = new SubscribingManager();
        mRegisterManager = new RegisterManager(mOfflineStorageManager);
    }

    public void buildGoogleApiClient(
            FragmentActivity activity,
            GoogleApiClient.ConnectionCallbacks googleConnectionCallback) {
        mClient = mBuildManager.buildGoogleApiClient(activity, googleConnectionCallback);

        mOfflineStorageManager.setClient(mClient);
        mSubscribingManager.setClient(mClient);
        mRegisterManager.setClient(mClient);
    }

    public void tryConnectClient(int resultCode) {
        mBuildManager.tryConnectClient(mClient, resultCode);
    }

    public void onDialogDismissed() {
        mBuildManager.onDialogDismissed();
    }

    public void getStepsPerDayFromHistory(ResultListener<Integer> stepsChangingListener) {
        mOfflineStorageManager.getStepsPerDayFromHistory(stepsChangingListener);
    }

    public void subscribeForStepCounter() {
        mSubscribingManager.subscribeForStepCounter();
    }

    public void unSubscribeStepCounter() {
        mSubscribingManager.unSubscribeStepCounter();
    }

    public void registerListenerForStepCounter(ResultListener<Integer> stepsChangingListener) {
        mRegisterManager.registerListener(DataType.TYPE_STEP_COUNT_DELTA, stepsChangingListener);
    }

    public void unregisterListener() {
        mRegisterManager.unregisterListener();
    }
}
