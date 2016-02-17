package com.erminesoft.motionview.motionview.ui.activities;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;

import com.erminesoft.motionview.motionview.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

public class GoogleMapActivity extends GenericActivity implements OnMapReadyCallback {

    public static void start(Activity activity) {
        activity.startActivity(new Intent(activity, GoogleMapActivity.class));
    }

    private GoogleMap mMap;
    private static int UPDATE_INTERVAL = 10000; // 10 sec
    private static int FATEST_INTERVAL = 5000; // 5 sec
    private static int DISPLACEMENT = 11; // 10 meters
    //private Bitmap runIcon = BitmapFactory.decodeResource(getResources(), R.drawable.run_icon);
    private boolean routerStarted = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mGoogleClientFacade.setGoogleMap(mMap);
        mGoogleClientFacade.setMarkerAtFirstShow();


        ImageButton startWalkRouter = (ImageButton) findViewById(R.id.activity_maps_steps_button);
        startWalkRouter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!routerStarted) {
                    routerStarted = true;
                    showLongToast("router started");
                } else {
                    routerStarted = false;
                    showLongToast("router stopped");
                }
            }
        });



    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.settings:
                break;
            case R.id.history:
                HistoryActivity.start(this);
                break;
            case R.id.google_map:
                GoogleMapActivity.start(this);
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}
