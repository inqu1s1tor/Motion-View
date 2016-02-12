package com.erminesoft.motionview.motionview.net;

import android.support.v4.app.FragmentActivity;

import com.erminesoft.motionview.motionview.core.callback.ResultListener;
import com.google.android.gms.common.api.GoogleApiClient;

public class GoogleClientFacade {

    private GoogleApiClient mClient;
    private BuildManager mBuildManager;

    public GoogleClientFacade() {
        mBuildManager = new BuildManager();
    }

    public void buildGoogleApiClient(
            FragmentActivity activity,
            GoogleApiClient.ConnectionCallbacks googleConnectionCallback) {
        mClient = mBuildManager.buildGoogleApiClient(activity, googleConnectionCallback);
    }

    public void tryConnectClient(int resultCode) {
        mBuildManager.tryConnectClient(mClient, resultCode);
    }

    public void onDialogDismissed() {
        mBuildManager.onDialogDismissed();
    }

    public void getStepsPerDayFromHistory(ResultListener<Integer> stepsChangingListener) {

    }

    public void unSubscribeStepCounter() {

    }

    public void registerListenerForStepCounter(ResultListener<Integer> stepsChangingListener) {

    }

    public void unregisterListener() {

    }

    public void subscribeForStepCounter() {

    }
}
