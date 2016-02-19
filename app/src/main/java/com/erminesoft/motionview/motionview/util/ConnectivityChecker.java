package com.erminesoft.motionview.motionview.util;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.GpsStatus;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

public class ConnectivityChecker {

    public static boolean bluetoothCheckConnection(BluetoothAdapter mBluetoothAdapter){
        return BluetoothAdapter.STATE_ON == mBluetoothAdapter.getState();
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


    public static boolean isPlayServiceArePresents(Context context) {
        int statusCode = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(context);
        return statusCode == ConnectionResult.SUCCESS;
    }

    public static boolean isLocationActive(final Context context) {
        final LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        GpsStatus.Listener listener = new GpsStatus.Listener() {
            @Override
            public void onGpsStatusChanged(int event) {
                if (event == GpsStatus.GPS_EVENT_STARTED) {
                    Log.d("!!!!!", "GPS_EVENT_STARTED");
                } else if (GpsStatus.GPS_EVENT_SATELLITE_STATUS == event) {
                    Log.d("!!!!!", "GPS_EVENT_SATELLITE_STATUS");
                    GpsStatus status = locationManager.getGpsStatus(null);
                    // Check number of satellites in list to determine fix state
                } else if (GpsStatus.GPS_EVENT_FIRST_FIX == event) {
                    Log.d("!!!!!", "GPS_EVENT_FIRST_FIX");
                } else if (GpsStatus.GPS_EVENT_STOPPED == event) {
                    Log.d("!!!!!", "GPS_EVENT_STOPPED");
                }
            }
        };

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return false;
        }
        locationManager.addGpsStatusListener(listener);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }
}
