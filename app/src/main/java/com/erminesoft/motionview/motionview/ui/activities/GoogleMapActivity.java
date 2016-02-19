package com.erminesoft.motionview.motionview.ui.activities;


import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.erminesoft.motionview.motionview.R;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

public class GoogleMapActivity extends GenericActivity implements OnMapReadyCallback {

    public static void start(Activity activity) {
        activity.startActivity(new Intent(activity, GoogleMapActivity.class));
    }

    private GoogleMap mMap;
    private static int UPDATE_INTERVAL = 10000;
    private static int FATEST_INTERVAL = 5000;
    private static int DISPLACEMENT = 5;
    private boolean routerStarted = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        setHomeAsUpEnabled();
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mGoogleClientFacade.setGoogleMap(mMap);
        mGoogleClientFacade.createLocationRequest(UPDATE_INTERVAL, FATEST_INTERVAL, DISPLACEMENT);
        mGoogleClientFacade.setMarkerAtFirstShow();

        final ImageButton startWalkRouter = (ImageButton) findViewById(R.id.activity_maps_steps_button);
        startWalkRouter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!routerStarted) {

                    startWalkRouter.setBackgroundResource(R.drawable.run_icon_active);
                    showShortToast("Router started");

                    routerStarted = true;
                    mGoogleClientFacade.clearPoints();
                    mGoogleClientFacade.clearRouteLine();
                    mGoogleClientFacade.clearMap();
                    mGoogleClientFacade.setMarkerAtFirstShow();
                    mGoogleClientFacade.addPointsToLineForRoute(new LatLng(mGoogleClientFacade.getCurrentLocation().getLatitude(), mGoogleClientFacade.getCurrentLocation().getLongitude()));
                    mGoogleClientFacade.setOnLocationChangeListener(new LocationListener() {
                        @Override
                        public void onLocationChanged(Location location) {
                            if (location.distanceTo(mGoogleClientFacade.getPreLastLocation()) > 4) {
                                mGoogleClientFacade.addPointsToLineForRoute(new LatLng(location.getLatitude(), location.getLongitude()));
                                mGoogleClientFacade.startRouteOnMap();
                            }
                        }
                    });
                    mGoogleClientFacade.startLocation();
                } else {

                    startWalkRouter.setBackgroundResource(R.drawable.run_icon);
                    showShortToast("Router stopped");

                    routerStarted = false;
                    mGoogleClientFacade.stopLocation();
                    mGoogleClientFacade.clearMap();
                    mGoogleClientFacade.stopRouteOnMap();
                }
            }
        });
    }


}
