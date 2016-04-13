package com.erminesoft.motionview.motionview.ui.fragments;


import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.erminesoft.motionview.motionview.R;
import com.erminesoft.motionview.motionview.core.bridge.Receiver;
import com.erminesoft.motionview.motionview.core.command.CommandType;
import com.erminesoft.motionview.motionview.core.command.Commander;
import com.erminesoft.motionview.motionview.core.command.ExecutorType;
import com.erminesoft.motionview.motionview.core.command.ProcessDayDataCommand;
import com.erminesoft.motionview.motionview.storage.DataBuffer;
import com.erminesoft.motionview.motionview.ui.activities.ShareMapActivity;
import com.erminesoft.motionview.motionview.util.ConnectivityChecker;
import com.erminesoft.motionview.motionview.util.DialogHelper;
import com.erminesoft.motionview.motionview.util.TimeWorker;
import com.facebook.appevents.AppEventsLogger;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class GoogleMapsFragment extends GenericFragment implements OnMapReadyCallback, GpsStatus.Listener {
    private static final int UPDATE_INTERVAL = 10000;
    private static final int FATEST_INTERVAL = 5000;
    private static final int DISPLACEMENT = 10;

    private CheckBox mStartWalkRouter;
    private LocationManager locationManager;
    private TextView startStopTracking;
    private ImageView gpsIcon;
    private TextView gpsTextTop;
    private TextView gpsTextBottom;

    private TextView totalDistanceTw;
    private TextView totalKCalTw;
    private TextView totalTimeTw;

    private float startDistance;
    private float startKCal;

    private int totalTime;
    private float totalKCal;
    private float totalDistance;

    private Timer timer;

    private FloatingActionButton centerMapButton;

    private DataReceiver receiver;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);
        mStartWalkRouter = (CheckBox) view.findViewById(R.id.activity_maps_steps_button);

        locationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
        startStopTracking = (TextView) view.findViewById(R.id.map_fragment_start_tracking_text);
        gpsIcon = (ImageView) view.findViewById(R.id.map_fragment_gps_icon);
        gpsTextTop = (TextView) view.findViewById(R.id.map_fragment_gps_top_text);
        gpsTextBottom = (TextView) view.findViewById(R.id.map_fragment_gps_bottom_text);

        totalKCalTw = (TextView) view.findViewById(R.id.main_fragment_calories);
        totalDistanceTw = (TextView) view.findViewById(R.id.main_fragment_distance);
        totalTimeTw = (TextView) view.findViewById(R.id.main_fragment_time);

        if (ActivityCompat.checkSelfPermission(getContext(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            return null;
        }
        locationManager.addGpsStatusListener(this);
        centerMapButton = (FloatingActionButton) view.findViewById(R.id.map_menu_button);
        centerMapButton.setOnClickListener(new CenterMapListener());

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        DialogHelper dialogCreator;
        if (!ConnectivityChecker.isNetworkAvailable(getContext())) {
            dialogCreator = new DialogHelper(getContext(), "Wi-Fi/Internet is not active. \n Wi-Fi/Internet connection gives you faster location");
            dialogCreator.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    startActivityForResult(new Intent(Settings.ACTION_WIFI_SETTINGS), 100);
                }
            });
            dialogCreator.showAlertDialog();
        }

        if (!ConnectivityChecker.isLocationActive(getContext())) {
            dialogCreator = new DialogHelper(getContext(), "GPS is not active. \n" + " Activated GPS gives you more accuracy location");
            dialogCreator.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    startActivityForResult(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), 200);
                }
            });
            dialogCreator.showAlertDialog();
        }

        return view;
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleFitnessFacade.setGoogleMap(googleMap);
        mGoogleFitnessFacade.createLocationRequest(UPDATE_INTERVAL, FATEST_INTERVAL, DISPLACEMENT);
        mGoogleFitnessFacade.setMarkerAtFirstShow();



        mStartWalkRouter.setOnCheckedChangeListener(new CheckedListener());
    }

    @Override
    public void onStart() {
        super.onStart();
        AppEventsLogger.activateApp(getContext());

        receiver = new DataReceiver();
        DataBuffer.getInstance().register(CommandType.PROCESS_DAY_DATA, receiver);

        if (!mGoogleFitnessFacade.isMapReady()) {
            return;
        }

        mGoogleFitnessFacade.clearPoints();
        mGoogleFitnessFacade.clearMap();
        mGoogleFitnessFacade.setStartMarker();
    }

    @Override
    public void onPause() {
        super.onPause();
        AppEventsLogger.deactivateApp(getContext());
    }

    @Override
    public void onStop() {
        super.onStop();

        DataBuffer.getInstance().unregister(receiver);
        receiver = null;
    }

    @Override
    public void onGpsStatusChanged(int event) {
        if (event == GpsStatus.GPS_EVENT_SATELLITE_STATUS) {
            int satellitesInFix = 0;
            for (GpsSatellite sat : locationManager.getGpsStatus(null).getSatellites()) {
                if (sat.usedInFix()) {
                    satellitesInFix++;
                }
            }
            if (satellitesInFix > 0) {
                gpsIcon.setImageResource(R.drawable.ic_gps_on);
                gpsTextTop.setTextColor(Color.parseColor("#199BAE"));
                gpsTextBottom.setTextColor(Color.parseColor("#199BAE"));
            } else {
                gpsIcon.setImageResource(R.drawable.gps);
                gpsTextTop.setTextColor(Color.parseColor("#B8B8B8"));
                gpsTextBottom.setTextColor(Color.parseColor("#B8B8B8"));
            }
        }
    }

    private void startWalking() {
        timer = new Timer();

        final Bundle bundle = ProcessDayDataCommand.generateBundle(System.currentTimeMillis());
        final Commander commander = mActivity.getMVApplication().getCommander();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                commander.execute(bundle, ExecutorType.MAIN_FRAGMENT_ACTIVITY);

                totalTime += 1;

                if (isVisible()) {
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            totalTimeTw.setText(TimeWorker.processSecondsToString(totalTime));
                        }
                    });
                }
            }
        }, 0, 1000);

        startStopTracking.setText(R.string.map_fragment_stop_tracking_text);
        mGoogleFitnessFacade.clearPoints();
        mGoogleFitnessFacade.clearMap();
        mGoogleFitnessFacade.setStartMarker();
        mGoogleFitnessFacade.addPointsToLineForRoute(new LatLng(mGoogleFitnessFacade.getCurrentLocation().getLatitude(), mGoogleFitnessFacade.getCurrentLocation().getLongitude()));
        mGoogleFitnessFacade.setOnLocationChangeListener(new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                if (location.getAccuracy() < 100) {
                    mGoogleFitnessFacade.addPointsToLineForRoute(new LatLng(location.getLatitude(), location.getLongitude()));
                    mGoogleFitnessFacade.startRouteOnMap();
                }
            }
        });
        mGoogleFitnessFacade.startLocation();
    }

    private void stopWalking() {
        mActivity.getMVApplication().getCommander().denyAll(ExecutorType.MAIN_FRAGMENT_ACTIVITY);
        startStopTracking.setEnabled(false);
        timer.cancel();
        if (mGoogleFitnessFacade.getTrackPoints().size() > 1) {
            ShareMapActivity.start(getActivity(),
                    mGoogleFitnessFacade.getTrackPoints(),
                    totalDistance,
                    totalTime,
                    totalKCal);
        }

        totalKCal = 0;
        totalTime = 0;
        totalDistance = 0;

        startDistance = 0;
        startKCal = 0;

        totalTimeTw.setText("00:00:00");
        totalKCalTw.setText("0.0");
        totalDistanceTw.setText("0.0");

        startStopTracking.setText(R.string.map_fragment_start_tracking_text);
        mGoogleFitnessFacade.stopLocation();
        mGoogleFitnessFacade.stopRouteOnMap();

        startStopTracking.setEnabled(true);
    }

    private void processData(List<DataSet> data) {
        for (DataSet dataSet : data) {
            if (dataSet.getDataType().equals(DataType.AGGREGATE_DISTANCE_DELTA)) {
                onDistanceChanged(dataSet.getDataPoints());
                continue;
            }

            if (dataSet.getDataType().equals(DataType.AGGREGATE_CALORIES_EXPENDED)) {
                onCaloriesChanged(dataSet.getDataPoints());
            }
        }
    }

    private void onDistanceChanged(List<DataPoint> dataPoints) {
        float distance = 0;

        if (dataPoints.size() > 0) {
            DataPoint dataPoint = dataPoints.get(0);

            distance = dataPoint.getValue(Field.FIELD_DISTANCE).asFloat();

            if (startDistance == 0) {
                startDistance = distance;
            }
        }

        totalDistance = distance - startDistance;

        if (isVisible()) {
            totalDistanceTw.setText(String.valueOf(totalDistance));
        }
    }

    private void onCaloriesChanged(List<DataPoint> dataPoints) {
        int calories = 0;

        if (dataPoints.size() > 0) {
            DataPoint dataPoint = dataPoints.get(0);

            calories = (int) dataPoint.getValue(Field.FIELD_CALORIES).asFloat();

            if (startKCal == 0) {
                startKCal = calories;
            }
        }

        totalKCal = calories - startKCal;

        if (isVisible()) {
            totalKCalTw.setText(String.valueOf(totalKCal));
        }
    }

    private final class CheckedListener implements CompoundButton.OnCheckedChangeListener {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (isChecked) {
                startWalking();
            } else {
                stopWalking();
            }
        }
    }

    private final class CenterMapListener implements OnClickListener{
        @Override
        public void onClick(View v) {
            mGoogleFitnessFacade.centerMap();
        }
    }


    private class DataReceiver implements Receiver {
        @Override
        public void notify(final Object data, CommandType type) {
            switch (type) {
                case PROCESS_DAY_DATA: {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            processData((List<DataSet>) data);
                        }
                    });
                    break;
                }
            }
        }
    }
}
