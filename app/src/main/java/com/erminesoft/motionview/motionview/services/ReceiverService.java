package com.erminesoft.motionview.motionview.services;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.annotation.Nullable;
import android.util.Log;

import com.erminesoft.motionview.motionview.core.MVApplication;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.data.DataSource;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Subscription;
import com.google.android.gms.fitness.request.DataSourcesRequest;

import java.util.concurrent.Executors;

public class ReceiverService extends Service {

    private GoogleApiClient mClient;
    private Receiver mReceiver;
    private PowerManager.WakeLock mWakeLock;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mClient = ((MVApplication) getApplication()).getGoogleClientFacade().mClient;

        Executors.newSingleThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {
                for (Subscription s : Fitness.RecordingApi.listSubscriptions(mClient).await().getSubscriptions()) {
                    Log.i("SERVICE", "Subscription: " + s.toDebugString());
                }

                for (DataSource dataSource : Fitness.SensorsApi.findDataSources(mClient, new DataSourcesRequest.Builder()
                        .setDataTypes(DataType.TYPE_STEP_COUNT_DELTA, DataType.TYPE_DISTANCE_DELTA)
                        .setDataSourceTypes(DataSource.TYPE_DERIVED)
                        .build()).await().getDataSources()) {
                    Log.i("RECEIVER", "DataSource: " + dataSource.toDebugString());
                }
            }
        });

        return BIND_AUTO_CREATE;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mReceiver = new Receiver();
        mWakeLock = ((PowerManager) getApplicationContext().getSystemService(POWER_SERVICE)).newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "tage");
        registerReceiver(mReceiver, new IntentFilter(Intent.ACTION_SCREEN_OFF));

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
        mWakeLock.release();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public class Receiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i("RECEIVER", "Is client connected: " + mClient.isConnected());
            mWakeLock.acquire();

            Executors.newSingleThreadExecutor().execute(new Runnable() {
                @Override
                public void run() {
                    for (Subscription s : Fitness.RecordingApi.listSubscriptions(mClient).await().getSubscriptions()) {
                        Log.i("RECEIVER", "Subscription: " + s.toDebugString());
                    }

                    for (DataSource dataSource : Fitness.SensorsApi.findDataSources(mClient, new DataSourcesRequest.Builder()
                            .setDataTypes(DataType.TYPE_STEP_COUNT_DELTA)
                            .setDataSourceTypes(DataSource.TYPE_DERIVED)
                            .build()).await().getDataSources()) {
                        Log.i("RECEIVER", "DataSource: " + dataSource.toDebugString());

                        Fitness.RecordingApi.subscribe(mClient, dataSource);

                        while (true) {
                            Log.i("RECEIVER", Fitness.HistoryApi.readDailyTotal(mClient, dataSource.getDataType()).await().getTotal().getDataPoints().toString());

                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            });
        }
    }
}
