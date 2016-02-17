package com.erminesoft.motionview.motionview.net;

import android.location.Location;
import android.support.v4.app.FragmentActivity;

import com.erminesoft.motionview.motionview.core.callback.ResultListener;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.fitness.data.Bucket;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.maps.GoogleMap;

import java.util.List;

public class GoogleClientFacade {

    private GoogleApiClient mClient;
    private BuildManager mBuildManager;
    private RegisterManager mRegisterManager;
    private OfflineStorageManager mOfflineStorageManager;
    private SubscribingManager mSubscribingManager;
    private GoogleMapManager mMapManager;

    public GoogleClientFacade() {
        mBuildManager = new BuildManager();
        mOfflineStorageManager = new OfflineStorageManager();
        mSubscribingManager = new SubscribingManager();
        mRegisterManager = new RegisterManager(mOfflineStorageManager);
        mMapManager = new GoogleMapManager();
    }

    public void buildGoogleApiClient(
            FragmentActivity activity,
            GoogleApiClient.ConnectionCallbacks googleConnectionCallback) {
        mClient = mBuildManager.buildGoogleApiClient(activity, googleConnectionCallback);

        mOfflineStorageManager.setClient(mClient);
        mSubscribingManager.setClient(mClient);
        mRegisterManager.setClient(mClient);
        mMapManager.setClient(mClient);
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

    public void getDataPerMonthFromHistory(int month, ResultListener<List<Bucket>> resultListener) {
        mOfflineStorageManager.getDataPerMonthFromHistory(month, resultListener);
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


    public void createLocationRequest(int updateInterval, int fastestInterval, int displacement) {
        mMapManager.createLocationRequest(updateInterval, fastestInterval, displacement);
    }

    public void startLocation() {
        mMapManager.startLocationUpdates();
    }

    public void stopLocation() {
        mMapManager.stopLocationUpdates();
    }

    public Location getCurrentLocation() {
        return mMapManager.getLocation();
    }

    public void setOnLocationChangeListener(LocationListener listener) {
        mMapManager.setOnChangeLocationListener(listener);
    }

    public void togglePeriodicLocationUpdate() {
        mMapManager.togglePeriodicLocationUpdates();
    }

    public void setGoogleMap(GoogleMap gm) {
        mMapManager.setGoogleMap(gm);
    }

    public void setMarkerAtFirstShow() {
        mMapManager.setMarkerAtFirstShow();
    }

    public GoogleApiClient getApiClient() {
        return mClient;
    }
}
