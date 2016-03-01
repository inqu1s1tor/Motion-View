package com.erminesoft.motionview.motionview.util;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.ActivityCompat;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

public class ConnectivityChecker {

    public static boolean bluetoothCheckConnection(BluetoothAdapter mBluetoothAdapter){
        return BluetoothAdapter.STATE_ON == mBluetoothAdapter.getState();
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo info = connectivity.getActiveNetworkInfo();
            if (info != null)
                if (info.getState() == NetworkInfo.State.CONNECTED) {
                    return true;
                }
        }
        return false;
    }

    public static boolean isPlayServiceArePresents(Context context) {
        int statusCode = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(context);
        return statusCode == ConnectionResult.SUCCESS;
    }

    public static boolean isLocationActive(final Context context) {
        final LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return false;
        }
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

}
