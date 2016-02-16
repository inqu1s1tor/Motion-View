package com.erminesoft.motionview.motionview.ui.activities;


import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.erminesoft.motionview.motionview.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class GoogleMapActivity extends GenericActivity implements OnMapReadyCallback,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    public static void start(Activity activity) {
        activity.startActivity(new Intent(activity, GoogleMapActivity.class));
    }

    private GoogleMap mMap;

    private GoogleApiClient mGoogleApiClient;
    private boolean mRequestingLocationUpdates = false;
    private Location mLastLocation;
    private LocationRequest mLocationRequest;

    // Location updates intervals in sec
    private static int UPDATE_INTERVAL = 10000; // 10 sec
    private static int FATEST_INTERVAL = 5000; // 5 sec
    private static int DISPLACEMENT = 1; // 10 meters


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mGoogleApiClient = mGoogleClientFacade.getApiClient();

        setContentView(R.layout.activity_maps);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);
        createLocationRequest();


        if (displayLocation() != null) {
            Log.d("!!!!!!", "" + displayLocation().getLatitude() + "  --  " + displayLocation().getLongitude());
        } else {
            Log.d("!!!!!!", "displayLocation returned NULL ");
        }

        togglePeriodicLocationUpdates();
    }

    @Override
    protected void onStart() {
        super.onStart();
        startLocationUpdates();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }


    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
    }


    private Location displayLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return null;
        }
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {
            return mLastLocation;
        } else {
            return null;
        }
    }

    private void togglePeriodicLocationUpdates() {
        if (!mRequestingLocationUpdates) {
            mRequestingLocationUpdates = true;
            startLocationUpdates();
            Log.d(TAG, "Periodic location updates started!");
        } else {
            mRequestingLocationUpdates = false;
            // Stopping the location updates
            stopLocationUpdates();
            Log.d(TAG, "Periodic location updates stopped!");
        }
    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FATEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setSmallestDisplacement(DISPLACEMENT);
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = "
                + result.getErrorCode());
    }

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;
        showShortToast("Location changed!");
        displayLocation();
    }

    protected void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);
    }

    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient, this);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng startLocation;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        Location startLoc = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (startLoc != null) {
            startLocation = new LatLng(startLoc.getLatitude(), startLoc.getLongitude());
        } else {
            startLocation = new LatLng(0, 0);
            showShortToast("Can't find current location");
        }

        mMap.addMarker(new MarkerOptions().position(startLocation).title("Current location"));
        mMap.moveCamera(CameraUpdateFactory.zoomTo(19));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(startLocation));
    }

}
