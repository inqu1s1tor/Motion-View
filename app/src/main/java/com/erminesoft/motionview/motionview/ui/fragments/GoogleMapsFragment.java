package com.erminesoft.motionview.motionview.ui.fragments;


import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.erminesoft.motionview.motionview.R;
import com.erminesoft.motionview.motionview.ui.activities.ShareMapActivity;
import com.erminesoft.motionview.motionview.util.ConnectivityChecker;
import com.erminesoft.motionview.motionview.util.DialogHelper;
import com.facebook.appevents.AppEventsLogger;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

public class GoogleMapsFragment extends GenericFragment implements OnMapReadyCallback, GpsStatus.Listener {
    private static final int UPDATE_INTERVAL = 10000;
    private static final int FATEST_INTERVAL = 5000;
    private static final int DISPLACEMENT = 10;

    private boolean routerStarted = false;

    private ImageButton mStartWalkRouter;
    private Button shareButton;
    private ImageView gpsStatus;
    private LocationManager locationManager;
    private TextView startStopTracking;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);
        mStartWalkRouter = (ImageButton) view.findViewById(R.id.activity_maps_steps_button);

        shareButton = (Button) view.findViewById(R.id.map_share_dialog);
        gpsStatus = (ImageView) view.findViewById(R.id.gps_status);
        locationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
        startStopTracking = (TextView) view.findViewById(R.id.map_fragment_start_tracking_text);

        if (ActivityCompat.checkSelfPermission(getContext(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            return null;
        }
        locationManager.addGpsStatusListener(this);

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        DialogHelper dialogCreator;
        if(!ConnectivityChecker.isNetworkAvailable(getContext())){
            dialogCreator = new DialogHelper(getContext(),"Wi-Fi/Internet is not active. \n Wi-Fi/Internet connection gives you faster location");
            dialogCreator.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    startActivityForResult(new Intent(Settings.ACTION_WIFI_SETTINGS), 100);
                }
            });
            dialogCreator.showAlertDialog();
        }

        if(!ConnectivityChecker.isLocationActive(getContext())){
            dialogCreator = new DialogHelper(getContext(), "GPS is not active. \n"+" Activated GPS gives you more accuracy location");
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

        mStartWalkRouter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!routerStarted) {
                    //mStartWalkRouter.setImageResource(R.drawable.stop);
                    mStartWalkRouter.setBackgroundResource(R.drawable.stop);
                    shareButton.setVisibility(View.GONE);
                    startStopTracking.setText(R.string.map_fragment_stop_tracking_text);
                    routerStarted = true;
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
                } else {
                    mStartWalkRouter.setImageResource(R.drawable.play);
                    //mStartWalkRouter.setBackgroundResource(R.drawable.play);
                    shareButton.setVisibility(View.VISIBLE);
                    startStopTracking.setText(R.string.map_fragment_start_tracking_text);
                    routerStarted = false;
                    mGoogleFitnessFacade.stopLocation();
                    mGoogleFitnessFacade.stopRouteOnMap();

                    shareButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ShareMapActivity.start(getActivity(), mGoogleFitnessFacade.getTrackPoints());
                        }
                    });
                }
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        AppEventsLogger.activateApp(getContext());
    }

    @Override
    public void onPause() {
        super.onPause();
        AppEventsLogger.deactivateApp(getContext());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("!!!!!!", "req code: " + requestCode + " res code:  " + resultCode);
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
                gpsStatus.setImageResource(R.drawable.gps_on);
            } else {
                gpsStatus.setImageResource(R.drawable.gps_off);
            }
        }
    }
}
