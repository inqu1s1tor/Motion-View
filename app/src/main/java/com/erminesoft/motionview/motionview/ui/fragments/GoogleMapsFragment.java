package com.erminesoft.motionview.motionview.ui.fragments;


import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.erminesoft.motionview.motionview.R;
import com.erminesoft.motionview.motionview.core.callback.ResultCallback;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;

public class GoogleMapsFragment extends GenericFragment implements OnMapReadyCallback/*, GpsStatus.Listener*/ {

    private GoogleMap mMap;
    private PolylineOptions polyLine;
    private static int UPDATE_INTERVAL = 10000;
    private static int FATEST_INTERVAL = 5000;
    private static int DISPLACEMENT = 10;
    private boolean routerStarted = false;
    private List<LatLng> mPoints = new ArrayList<>();
    private ImageButton mStartWalkRouter;
    private LocationManager locationManager;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);
        mStartWalkRouter = (ImageButton) view.findViewById(R.id.activity_maps_steps_button);

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);


        mGoogleClientFacade.registerListenerForCurrentLocation(new ResultCallback() {
            @Override
            public void onSuccess(Object dataPoint) {
                if (!(dataPoint instanceof DataPoint)) {
                    onError("WRONG DATA");
                    return;
                }

                DataPoint dp = (DataPoint) dataPoint;
                if (Double.valueOf(String.valueOf(dp.getValue(Field.FIELD_ACCURACY))) < 11) {
                    double lat = Double.valueOf(String.valueOf(dp.getValue(Field.FIELD_LATITUDE)));
                    double longtitude = Double.valueOf(String.valueOf(dp.getValue(Field.FIELD_LONGITUDE)));
                    final LatLng lt = new LatLng(lat, longtitude);
                    mPoints.add(lt);
                    polyLine = new PolylineOptions().width(12f).visible(true).geodesic(true);
                    polyLine.add(mPoints.get(mPoints.size() - 1));
                    polyLine.add(lt);
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mMap.addMarker(new MarkerOptions().position(lt));
                            mMap.addPolyline(polyLine).setVisible(true);
                        }
                    });

                }

                Log.d("!!!", "" + dataPoint.toString());
            }

            @Override
            public void onError(String error) {
                Log.d("!!!", "" + error);
            }
        });


        mapFragment.getMapAsync(this);

       /* if(!ConnectivityChecker.isNetworkAvailable(getContext())){
            AlertDialog.Builder alertBuilder = new AlertDialog.Builder(getContext());
            alertBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            });
            alertBuilder.setCancelable(true).setMessage("Wi-Fi is not active. \n Please activate Wi-Fi connection").create().show();
        }

        if(!ConnectivityChecker.isLocationActive(getContext())){
            AlertDialog.Builder alertBuilder = new AlertDialog.Builder(getContext());
            alertBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            alertBuilder.setCancelable(true).setMessage("GPS is not active. \n" +
                    " Please activate gps connection").create().show();
        }*/

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
                    showShortToast("Router started");
                    routerStarted = true;
                    mGoogleClientFacade.clearPoints();
                    mGoogleClientFacade.clearMap();
                    mGoogleClientFacade.setStartMarker();
                    mGoogleClientFacade.addPointsToLineForRoute(new LatLng(mGoogleClientFacade.getCurrentLocation().getLatitude(), mGoogleClientFacade.getCurrentLocation().getLongitude()));
                    mGoogleClientFacade.setOnLocationChangeListener(new LocationListener() {
                        @Override
                        public void onLocationChanged(Location location) {
                            //showShortToast("getAccuracy "+location.getAccuracy());
                            if (location.getAccuracy() < 10) {
                                mGoogleClientFacade.addPointsToLineForRoute(new LatLng(location.getLatitude(), location.getLongitude()));
                                mGoogleClientFacade.startRouteOnMap();
                            }
                        }
                    });
                    mGoogleClientFacade.startLocation();
                } else {
                    mStartWalkRouter.setBackgroundResource(R.drawable.run_icon);
                    //showShortToast("Router stopped");
                    routerStarted = false;
                    mGoogleClientFacade.stopLocation();
                    mGoogleClientFacade.stopRouteOnMap();
                }
            }
        });
    }

    @Override
    public void onStop() {
        super.onStop();
        mGoogleClientFacade.unregisterListener();
    }


   /* @Override
    public void onGpsStatusChanged(int event) {

        if (event == GpsStatus.GPS_EVENT_SATELLITE_STATUS) {
            int satellites = 0;
            int satellitesInFix = 0;
            int timetofix = locationManager.getGpsStatus(null).getTimeToFirstFix();

            showShortToast("Time to first fix = " + timetofix);
            for (GpsSatellite sat : locationManager.getGpsStatus(null).getSatellites()) {
                if (sat.usedInFix()) {
                    satellitesInFix++;
                }
                satellites++;
            }
            if (satellitesInFix > 0) {
                showShortToast(satellites + " Used In Last Fix (" + satellitesInFix + ")");
            } else {
                showShortToast(satellites + "(NO SATTEL.) Used In Last Fix (" + satellitesInFix + ")");
            }
        }
    }*/

}
