package com.erminesoft.motionview.motionview.net.fitness;

import android.location.Location;
import android.support.v4.app.FragmentActivity;

import com.erminesoft.motionview.motionview.core.callback.ResultCallback;
import com.erminesoft.motionview.motionview.util.ChartDataWorker;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.result.DataReadResult;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

import java.util.List;

public class GoogleFitnessFacade {

    private GoogleApiClient mClient;
    private final BuildManager mBuildManager;
    private final RegisterManager mRegisterManager;
    private final OfflineStorageManager mOfflineStorageManager;
    private final SubscribingManager mSubscribingManager;
    private final GoogleMapManager mMapManager;
    private final BluetoothManager mBluetoothManager;

    public GoogleFitnessFacade() {
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

    public DataReadResult getDataPerDay(int day, int month, int year) {
        return mOfflineStorageManager.getDataPerDay(day, month, year);
    }

    public DataReadResult getDataPerMonthFromHistory(ChartDataWorker.Month month, int year) {
        return mOfflineStorageManager.getDataPerMonthFromHistory(month, year);
    }

    public DataReadResult getHoursDataPerDay(long timeStamp) {
        return mOfflineStorageManager.getHoursDataPerDay(timeStamp);
    }

    public void saveUserHeight(int heightCentimeters) {
        mOfflineStorageManager.saveUserHeight(heightCentimeters);
    }

    public void saveUserWeight(float weight) {
        mOfflineStorageManager.saveUserWeight(weight);
    }

    public void subscribe() {
        mSubscribingManager.subscribe();
    }

    public void unsubscribe() {
        mSubscribingManager.unsubscribe();
    }

    public void registerListenerForStepCounter(ResultCallback stepsChangingListener) {
        mRegisterManager.registerListener(DataType.TYPE_STEP_COUNT_DELTA, stepsChangingListener);
    }


    public void unregisterListener() {
        mRegisterManager.unregisterListener();
    }

    public void createLocationRequest(int updateInterval, int fastestInterval, int displacement) {
        mMapManager.createLocationRequest(updateInterval, fastestInterval, displacement);
    }

    public List<LatLng> getTrackPoints(){
        return mMapManager.getTrackPoints();
    }

    public void startLocation() {
        mMapManager.startLocationUpdates();
    }

    public void stopLocation() {
        mMapManager.stopLocationUpdates();
    }

    public Location getCurrentLocation() {
        return mMapManager.getCurrentLocation();
    }

    public void setOnLocationChangeListener(LocationListener listener) {
        mMapManager.setOnChangeLocationListener(listener);
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

    public void setMarkerAtFirstShow() {
        mMapManager.setMarkerAtFirstShow();
    }

    public void setStartMarker() {
        mMapManager.setStartMarker();
    }

    public void startRouteOnMap() {
        mMapManager.startRouteOnMap();
    }

    public void stopRouteOnMap() {
        mMapManager.stopRouteOnMap();
    }

    public void drawRouteByPointsOnMap(List<LatLng> points,GoogleMap googleMap){ mMapManager.drawRouteByPointsOnMap(points,googleMap);}

    public boolean isMapReady() {
        return mMapManager.isMapReady();
    }
}
