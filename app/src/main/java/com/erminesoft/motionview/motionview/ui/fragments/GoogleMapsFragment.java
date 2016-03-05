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
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;

public class GoogleMapsFragment extends GenericFragment implements OnMapReadyCallback, GpsStatus.Listener {

    private GoogleMap mMap;
    private PolylineOptions polyLine;
    private static int UPDATE_INTERVAL = 10000;
    private static int FATEST_INTERVAL = 5000;
    private static int DISPLACEMENT = 10;
    private boolean routerStarted = false;

    private View view;

    private List<LatLng> mPoints = new ArrayList<>();
    private ImageButton mStartWalkRouter;
    private Button shareButton;
    private Button share;
    private ImageView gpsStatus;
    private DialogHelper dialogCreator;
    private LocationManager locationManager;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_map, container, false);
        mStartWalkRouter = (ImageButton) view.findViewById(R.id.activity_maps_steps_button);
        shareButton = (Button) view.findViewById(R.id.map_share_dialog);
        gpsStatus = (ImageView) view.findViewById(R.id.gps_status);
        locationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(getContext(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            return null;
        }
        locationManager.addGpsStatusListener(this);

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

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
        mMap = googleMap;
        mGoogleClientFacade.setGoogleMap(mMap);
        mGoogleClientFacade.createLocationRequest(UPDATE_INTERVAL, FATEST_INTERVAL, DISPLACEMENT);
        mGoogleClientFacade.setMarkerAtFirstShow();



        mStartWalkRouter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!routerStarted) {
                    mStartWalkRouter.setBackgroundResource(R.drawable.run_icon_active);
                    shareButton.setVisibility(View.GONE);
                    showShortToast("Router started");
                    routerStarted = true;
                    mGoogleClientFacade.clearPoints();
                    mGoogleClientFacade.clearMap();
                    mGoogleClientFacade.setStartMarker();
                    mGoogleClientFacade.addPointsToLineForRoute(new LatLng(mGoogleClientFacade.getCurrentLocation().getLatitude(), mGoogleClientFacade.getCurrentLocation().getLongitude()));
                    mGoogleClientFacade.setOnLocationChangeListener(new LocationListener() {
                        @Override
                        public void onLocationChanged(Location location) {
                            if (location.getAccuracy() < 100) {
                                mGoogleClientFacade.addPointsToLineForRoute(new LatLng(location.getLatitude(), location.getLongitude()));
                                mGoogleClientFacade.startRouteOnMap();
                            }
                        }
                    });
                    mGoogleClientFacade.startLocation();
                } else {
                    mStartWalkRouter.setBackgroundResource(R.drawable.run_icon);
                    shareButton.setVisibility(View.VISIBLE);
                    showShortToast("Router stopped");
                    routerStarted = false;
                    mGoogleClientFacade.stopLocation();
                    mGoogleClientFacade.stopRouteOnMap();


                    shareButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Bundle dataPoints = new Bundle();
                            dataPoints.putParcelableArrayList("mapPoints", (ArrayList<LatLng>) mGoogleClientFacade.getTrackPoints());
                            Intent shareIntent = new Intent(getContext(), ShareMapActivity.class);
                            shareIntent.putExtra("mapPoints",dataPoints);
                            startActivityForResult(shareIntent,100);
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
            int satellites = 0;
            int satellitesInFix = 0;
            int timetofix = locationManager.getGpsStatus(null).getTimeToFirstFix();
            //showShortToast("Time to first fix = " + timetofix);
            for (GpsSatellite sat : locationManager.getGpsStatus(null).getSatellites()) {
                if (sat.usedInFix()) {
                    satellitesInFix++;
                }
                satellites++;
            }
            if (satellitesInFix > 0) {
                gpsStatus.setImageResource(R.drawable.gps_on);
                //showShortToast(satellites + " Used In Last Fix (" + satellitesInFix + ")");
            } else {
                gpsStatus.setImageResource(R.drawable.gps_off);
                //showShortToast(satellites + "(NO SATTEL.) Used In Last Fix (" + satellitesInFix + ")");
            }
        }
    }
}
