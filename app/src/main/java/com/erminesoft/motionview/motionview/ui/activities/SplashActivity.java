package com.erminesoft.motionview.motionview.ui.activities;


import android.bluetooth.BluetoothAdapter;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.erminesoft.motionview.motionview.R;
import com.erminesoft.motionview.motionview.services.BluetoothService;
import com.erminesoft.motionview.motionview.core.MVApplication;
import com.erminesoft.motionview.motionview.util.ConnectivityChecker;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.data.BleDevice;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.request.BleScanCallback;
import com.google.android.gms.fitness.request.StartBleScanRequest;
import com.google.android.gms.common.api.GoogleApiClient;

import java.util.concurrent.TimeUnit;


public class SplashActivity extends GenericActivity {
    String TAG = "SplashActivity--";

    private MVApplication mAplication;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);


        mAplication = (MVApplication) getApplication();
        mAplication.getGoogleClientHelper().buildGoogleApiClient(new GoogleApiClient.ConnectionCallbacks() {
            @Override
            public void onConnected(@Nullable Bundle bundle) {
            }
            @Override
            public void onConnectionSuspended(int i) {
            }
        });

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                checkConnectivity();


                GoogleApiClient client = new GoogleApiClient.Builder(getBaseContext())
                        .addApi(Fitness.BLE_API)
                        .addScope(new Scope(Scopes.FITNESS_ACTIVITY_READ_WRITE))
                        .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                            @Override
                            public void onConnected(Bundle bundle) {
                                Log.d(TAG, " Client connection success ");
                            }

                            @Override
                            public void onConnectionSuspended(int i) {
                                Log.d(TAG, " Client connection fail ");
                            }
                        })
                        .enableAutoManage(SplashActivity.this, new GoogleApiClient.OnConnectionFailedListener() {
                            @Override
                            public void onConnectionFailed(ConnectionResult connectionResult) {
                                Log.d(TAG, "client ERROR : " + connectionResult.getErrorMessage());
                            }
                        })
                        .build();








                BleScanCallback scanCallback = new BleScanCallback() {
                    @Override
                    public void onDeviceFound(BleDevice device) {
                        // A device that provides the requested data types is available
                        Log.d(TAG, "Nearest device : " + device.getName());
                    }
                    @Override
                    public void onScanStopped() {
                        // The scan timed out or was interrupted
                        Log.d(TAG,"Scan stopped");
                    }
                };

                ResultCallback<Status> resultCallback = new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        Log.d(TAG, " Scan result : " + status.toString());
                    }
                };

                StartBleScanRequest request = new StartBleScanRequest.Builder()
                        .setDataTypes(DataType.TYPE_DISTANCE_DELTA, DataType.TYPE_STEP_COUNT_DELTA, DataType.TYPE_LOCATION_SAMPLE)
                        .setDataTypes(DataType.TYPE_ACTIVITY_SAMPLE, DataType.AGGREGATE_STEP_COUNT_DELTA)
                        .setDataTypes(DataType.AGGREGATE_ACTIVITY_SUMMARY)
                        .setDataTypes(DataType.AGGREGATE_LOCATION_BOUNDING_BOX)
                        .setDataTypes(DataType.AGGREGATE_POWER_SUMMARY)
                        .setDataTypes(DataType.TYPE_ACTIVITY_SEGMENT)
                        .setDataTypes(DataType.TYPE_LOCATION_TRACK)
                        .setDataTypes(DataType.TYPE_WORKOUT_EXERCISE)
                        .setBleScanCallback(scanCallback)
                        .build();




                PendingResult<Status> pendingResult = Fitness.BleApi.startBleScan(client, request);
                pendingResult.setResultCallback(resultCallback,3000, TimeUnit.MILLISECONDS);




            }
        }, 5000L);

    }

    private void checkConnectivity() {

        /*if (!ConnectivityChecker.isPlayServiceArePresents(getApplicationContext())) {
            showShortToast("Play Services are missed");
            return;
        }

        if (!ConnectivityChecker.isNetworkAvailable(getApplicationContext())) {
            Toast.makeText(getBaseContext(), "Check internet connection", Toast.LENGTH_LONG).show();
            return;
        }

        if (!ConnectivityChecker.bluetoothCheckConnection(BluetoothAdapter.getDefaultAdapter())) {
            Toast.makeText(getBaseContext(), "Check bluetooth", Toast.LENGTH_SHORT).show();
            return;
        }*/

        MainActivity.start(this);

    }
}
