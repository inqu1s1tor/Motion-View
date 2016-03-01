package com.erminesoft.motionview.motionview.net;


import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.erminesoft.motionview.motionview.R;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;

class GoogleMapManager {
    private static final String TAG = RegisterManager.class.getSimpleName();

    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private LocationRequest mLocationRequest;
    private LocationListener listener;
    private GoogleMap mMap;
    private List<LatLng> mPoints = new ArrayList<>();
    private PolylineOptions line;

    public void setClient(GoogleApiClient client) {
        mGoogleApiClient = client;
    }

    void setGoogleMap(GoogleMap gm) {
        mMap = gm;
    }

    void setOnChangeLocationListener(LocationListener l) {
        listener = l;
    }

    void addPointsToLineForRoute(LatLng point) {
        mPoints.add(point);
    }

    void clearPoints() {
        mPoints.clear();
    }

    void clearMap() {
        mMap.clear();
    }

    void createLocationRequest(int updateInterval, int fastestInterval, int displacement) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(updateInterval);
        mLocationRequest.setFastestInterval(fastestInterval);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setSmallestDisplacement(displacement);
    }

    Location getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(mGoogleApiClient.getContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(mGoogleApiClient.getContext(), Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "Have no permissions");

            return null;
        }
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {
            Log.d("!!!!! - all good - ", "" + mLastLocation);
            return mLastLocation;
        } else {
            mLastLocation = new Location(LocationManager.GPS_PROVIDER);
            Log.d("!!!!! - error - ", "" + mLastLocation);
            mLastLocation.setLatitude(2.0);
            mLastLocation.setLongitude(1.0);
            return mLastLocation;
        }
    }

    Location getPreLastLocation() {
        Location loc = new Location(LocationManager.GPS_PROVIDER);
        loc.reset();
        LatLng latLng;
        if (mPoints.size() >= 2) {
            latLng = mPoints.get(mPoints.size() - 2);
        } else {
            latLng = mPoints.get(mPoints.size() - 1);
        }
        loc.setLongitude(latLng.longitude);
        loc.setLatitude(latLng.latitude);
        return loc;
    }


    void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(mGoogleApiClient.getContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(mGoogleApiClient.getContext(),
                        Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "Have no permission for getting location!");
            return;
        }


        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, listener);
    }

    void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, listener);
    }

    void setMarkerAtFirstShow() {
        LatLng startLocation;
        if (ActivityCompat.checkSelfPermission(mGoogleApiClient.getContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(mGoogleApiClient.getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Location startLoc = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (startLoc != null) {
            startLocation = new LatLng(startLoc.getLatitude(), startLoc.getLongitude());
        } else {
            startLocation = new LatLng(0, 0);
        }

        mMap.addMarker(new MarkerOptions().position(startLocation).title("Current location").snippet("Start Point"));
        mMap.moveCamera(CameraUpdateFactory.zoomTo(16));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(startLocation));
    }

    void setStartMarker() {
        MarkerOptions startMarkerOptions = new MarkerOptions()
                .position(new LatLng(getCurrentLocation().getLatitude(), getCurrentLocation().getLongitude()))
                .anchor(0.5f, 1f)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.maps_start_icon));
        mMap.addMarker(startMarkerOptions);
    }

    void startRouteOnMap() {
        LatLng preLatLoc = new LatLng(getPreLastLocation().getLatitude(), getPreLastLocation().getLongitude());
        LatLng curLatLoc = new LatLng(getCurrentLocation().getLatitude(), getCurrentLocation().getLongitude());
        PolylineOptions line = new PolylineOptions().width(12f).color(R.color.greenRoute).geodesic(true);
        line.add(preLatLoc);
        line.add(curLatLoc);
        mMap.addPolyline(line).setGeodesic(true);
        mPoints.add(curLatLoc);

        LatLngBounds.Builder latLngBuilder = new LatLngBounds.Builder();
        latLngBuilder.include(new LatLng(getCurrentLocation().getLatitude(), getCurrentLocation().getLongitude()));
        LatLngBounds latLngBounds = latLngBuilder.build();
        int size = mGoogleApiClient.getContext().getResources().getDisplayMetrics().widthPixels;
        CameraUpdate track = CameraUpdateFactory.newLatLngBounds(latLngBounds, size, size, 15);
        mMap.moveCamera(track);
        mMap.moveCamera(CameraUpdateFactory.zoomTo(16));
    }

    void stopRouteOnMap() {
        MarkerOptions endMarkerOptions = new MarkerOptions()
                .position(new LatLng(getCurrentLocation().getLatitude(), getCurrentLocation().getLongitude()))
                .anchor(0.5f, 1f)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.maps_stop_icon)).visible(true);
        mMap.addMarker(endMarkerOptions);
    }

    /*void stopRouteOnMap() {
        LatLngBounds.Builder latLngBuilder = new LatLngBounds.Builder();
        for (int i = 0; i < mPoints.size(); i++) {
            if (i == 0) {
                MarkerOptions startMarkerOptions = new MarkerOptions()
                        .position(mPoints.get(i))
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.maps_start_icon));
                mMap.addMarker(startMarkerOptions);
            } else if (i == mPoints.size() - 1) {
                MarkerOptions endMarkerOptions = new MarkerOptions()
                        .position(mPoints.get(i))
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.maps_stop_icon)).visible(true);
                mMap.addMarker(endMarkerOptions);
            }
            line.add(mPoints.get(i));
            latLngBuilder.include(mPoints.get(i));
        }

        mMap.addPolyline(line);
        int size = mGoogleApiClient.getContext().getResources().getDisplayMetrics().widthPixels;
        LatLngBounds latLngBounds = latLngBuilder.build();
        CameraUpdate track = CameraUpdateFactory.newLatLngBounds(latLngBounds, size, size, 9);
        mMap.moveCamera(track);
        mMap.moveCamera(CameraUpdateFactory.zoomTo(16));
    }*/

}
