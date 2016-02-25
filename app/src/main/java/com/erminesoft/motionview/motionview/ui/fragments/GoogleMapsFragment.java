package com.erminesoft.motionview.motionview.ui.fragments;


import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.erminesoft.motionview.motionview.R;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

public class GoogleMapsFragment extends GenericFragment implements OnMapReadyCallback {

    private GoogleMap mMap;
    private static int UPDATE_INTERVAL = 10000;
    private static int FATEST_INTERVAL = 5000;
    private static int DISPLACEMENT = 20;
    private boolean routerStarted = false;
    private ImageButton mStartWalkRouter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);

        mStartWalkRouter = (ImageButton) view.findViewById(R.id.activity_maps_steps_button);

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        return view;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mGoogleClientFacade.setGoogleMap(mMap);
        mGoogleClientFacade.createLocationRequest(UPDATE_INTERVAL, FATEST_INTERVAL, DISPLACEMENT);
        mGoogleClientFacade.setMarkerAtFirstShow();


        /*
        if(event == GpsStatus.GPS_EVENT_STARTED){
            Log.d("!!!!!", "" + event);
        } else if(event == GpsStatus.GPS_EVENT_STOPPED) {
            Log.d("!!!!!", ""+event);
        } else if(event == GpsStatus.GPS_EVENT_SATELLITE_STATUS){
            Log.d("!!!!!", ""+event);
        } else if(event == GpsStatus.GPS_EVENT_FIRST_FIX) {
            Log.d("!!!!!", ""+event);
        }*/

        mStartWalkRouter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!routerStarted) {

                    mStartWalkRouter.setBackgroundResource(R.drawable.run_icon_active);
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
                            if (location.distanceTo(mGoogleClientFacade.getPreLastLocation()) > 20 && location.distanceTo(mGoogleClientFacade.getPreLastLocation()) < 30) {
                                mGoogleClientFacade.addPointsToLineForRoute(new LatLng(location.getLatitude(), location.getLongitude()));
                                mGoogleClientFacade.startRouteOnMap();
                            }
                        }
                    });

                    mGoogleClientFacade.startLocation();
                } else {

                    mStartWalkRouter.setBackgroundResource(R.drawable.run_icon);
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
