package com.erminesoft.motionview.motionview.net;

import android.location.Location;
import android.support.v4.app.FragmentActivity;

import com.erminesoft.motionview.motionview.core.callback.BucketsResultListener;
import com.erminesoft.motionview.motionview.core.callback.DataChangedListener;
import com.erminesoft.motionview.motionview.util.ChartDataWorker;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.request.BleScanCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

public class GoogleClientFacade {

    private GoogleApiClient mClient;
    private BuildManager mBuildManager;
    private RegisterManager mRegisterManager;
    private OfflineStorageManager mOfflineStorageManager;
    private SubscribingManager mSubscribingManager;
    private GoogleMapManager mMapManager;
    private BluetoothManager mBluetoothManager;

    public GoogleClientFacade() {
        mBuildManager = new BuildManager();
        mOfflineStorageManager = new OfflineStorageManager();
        mSubscribingManager = new SubscribingManager();
        mRegisterManager = new RegisterManager(mOfflineStorageManager);
        mMapManager = new GoogleMapManager();
        mBluetoothManager = new BluetoothManager();
    }

    public void buildGoogleApiClient(
            FragmentActivity activity,
            GoogleApiClient.ConnectionCallbacks googleConnectionCallback) {
        mClient = mBuildManager.buildGoogleApiClient(activity, googleConnectionCallback);

        mOfflineStorageManager.setClient(mClient);
        mSubscribingManager.setClient(mClient);
        mRegisterManager.setClient(mClient);
        mMapManager.setClient(mClient);
        mBluetoothManager.setClient(mClient);
    }

    public void tryConnectClient(int resultCode) {
        mBuildManager.tryConnectClient(mClient, resultCode);
    }

    public void onDialogDismissed() {
        mBuildManager.onDialogDismissed();
    }

    public void getDataPerDay(int day, int month, int year, DataChangedListener stepsChangingListener) {
        mOfflineStorageManager.getDataPerDay(day, month, year, stepsChangingListener);
    }

    public void getDataPerMonthFromHistory(ChartDataWorker.Month month, int year, BucketsResultListener resultListener) {
        mOfflineStorageManager.getDataPerMonthFromHistory(month, year, resultListener);
    }

    public void getDataForInitHistory(long installTime, BucketsResultListener resultListener) {
        mOfflineStorageManager.getDataForInitHistory(installTime, resultListener);
    }

    public void subscribe() {
        mSubscribingManager.subscribe();
    }

    public void unsubscribe() {
        mSubscribingManager.unsubscribe();
    }

    public void registerListenerForStepCounter(DataChangedListener stepsChangingListener) {
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

    public Location getPreLastLocation() {
        return mMapManager.getPreLastLocation();
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

    public void addPointsToLineForRoute(LatLng mPoint) {
        mMapManager.addPointsToLineForRoute(mPoint);
    }

    public void clearPoints() {
        mMapManager.clearPoints();
    }

    public void clearMap() {
        mMapManager.clearMap();
    }

    public void clearRouteLine() {
        mMapManager.clearRouteLine();
    }

    public void setMarkerAtFirstShow() {
        mMapManager.setMarkerAtFirstShow();
    }

    public void startRouteOnMap() {
        mMapManager.startRouteOnMap();
    }

    public void stopRouteOnMap() {
        mMapManager.stopRouteOnMap();
    }


    public void startBleScan() {
        mBluetoothManager.startBleScan();
    }

    public void setRequest(int timeout) {
        mBluetoothManager.setRequest(timeout);
    }

    public void setResultCallback(ResultCallback<Status> callback) {
        mBluetoothManager.setResultCallback(callback);
    }

    public void setBleScanCallback(BleScanCallback callback) {
        mBluetoothManager.setBleScanCallback(callback);
    }

    public void saveUserHeight(int heightCentimeters) {
        mOfflineStorageManager.saveUserHeight(heightCentimeters);
    }

    public void saveUserWeight(float weight) {
        mOfflineStorageManager.saveUserWeight(weight);
    }

    public void getHoursDataPerDay(long timeStamp, BucketsResultListener listener) {
        mOfflineStorageManager.getHoursDataPerDay(timeStamp, listener);
    }
}
