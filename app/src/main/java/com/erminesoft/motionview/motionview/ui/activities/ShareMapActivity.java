package com.erminesoft.motionview.motionview.ui.activities;


import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.erminesoft.motionview.motionview.R;
import com.erminesoft.motionview.motionview.util.Utils;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.plus.PlusShare;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ShareMapActivity extends GenericActivity implements OnMapReadyCallback {

    public static void start(Activity activity) {
        activity.startActivity(new Intent(activity, ShareMapActivity.class));
    }

    private List<LatLng> pointsOnMap = new ArrayList<>();
    private View view;
    private Button share;
    private Button shareToGooglePlus;
    private CallbackManager callbackManager;
    private LoginManager loginManager;
    private SupportMapFragment mapFragment;
    private GoogleMap mMap;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHomeAsUpEnabled(true);
        view = View.inflate(this, R.layout.activity_sharing_map, null);
        setContentView(view);
        Bundle mapData = getIntent().getBundleExtra("mapPoints");
        pointsOnMap = mapData.getParcelableArrayList("mapPoints");
        share = (Button) view.findViewById(R.id.share_map_button);
        shareToGooglePlus = (Button) findViewById(R.id.share_google_button);
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_for_share);
        mapFragment.getMapAsync(this);







        Utils utils = new Utils();
        Uri uri;

        StringBuilder text = new StringBuilder();
        float distance = 12;
        Log.d("!!!!!!", "" + distance);

        text.append("I just done ")
                .append(distance)
                .append(" km\n")
                .append(" with application Motion View");


    /*    Bitmap bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.run_icon_active);
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        ContentResolver contentResolver = getApplicationContext().getContentResolver();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(contentResolver, bitmap, "Title", null);
        uri = Uri.parse(path);
        String mime = contentResolver.getType(uri);

        PlusShare.Builder shareBuild = new PlusShare.Builder(ShareMapActivity.this);
        shareBuild.setText(text);
        shareBuild.addStream(uri);
        shareBuild.setType(mime);
        shareBuild.setContentUrl(uri);
        Intent newInt = shareBuild.getIntent();
        startActivity(newInt);*/





    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(data != null){
            Log.d("!!!!!","result data: "+data.toString());
        }
        if (requestCode != 0 && resultCode != 0) {
            Log.d("!!!!!","result code: "+resultCode);
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
        if(resultCode == -1){
            finish();
        }
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
    mMap = googleMap;
        if(pointsOnMap.size() > 1) {
            mGoogleFitnessFacade.drawRouteByPointsOnMap(pointsOnMap,googleMap);
        } else { Log.d("!!!!", "No data from intent"); }

        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FacebookSdk.sdkInitialize(getApplicationContext());
                callbackManager = CallbackManager.Factory.create();
                List<String> permissionNeeds = Arrays.asList("publish_actions");
                loginManager = LoginManager.getInstance();
                loginManager.logInWithPublishPermissions(ShareMapActivity.this, permissionNeeds);
                loginManager.registerCallback(callbackManager, new FacebookLoginPostCallback(ShareMapActivity.this,googleMap,pointsOnMap));
            }
        });

        shareToGooglePlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMap.snapshot(new GoogleMap.SnapshotReadyCallback() {
                    @Override
                    public void onSnapshotReady(Bitmap bitmap) {
                        Utils utils = new Utils();
                        Uri uri;

                        StringBuilder text = new StringBuilder();
                        float distance = 12;
                        Log.d("!!!!!!", "" + distance);

                        text.append("I just done ")
                                .append(distance)
                                .append(" km\n")
                                .append(" with application Motion View");


                        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                        ContentResolver contentResolver = getApplicationContext().getContentResolver();
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
                        String path = MediaStore.Images.Media.insertImage(contentResolver, bitmap, "Title", null);
                        uri = Uri.parse(path);
                        String mime = contentResolver.getType(uri);

                        PlusShare.Builder shareBuild = new PlusShare.Builder(ShareMapActivity.this);
                        shareBuild.setText(text);
                        shareBuild.addStream(uri);
                        shareBuild.setType(mime);
                        Intent newInt = shareBuild.getIntent();

                        startActivityForResult(newInt, 100);

                    }
                });








            }
        });

    }

    @Override
    public void onStart() {
        super.onStart();
        AppEventsLogger.activateApp(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        AppEventsLogger.deactivateApp(this);
    }


}

class FacebookLoginPostCallback implements FacebookCallback<LoginResult> {
    private Utils utils;
    private Context context;
    private GoogleMap mMap;
    private List<LatLng> pointsOnMap;

    public FacebookLoginPostCallback(Context ctx, GoogleMap map, List<LatLng> points) {
        utils = new Utils();
        mMap = map;
        context = ctx;
        pointsOnMap = points;
    }
    @Override
    public void onSuccess(LoginResult loginResult) {
        makeMapSnapShot(mMap);
        Log.d("!!!!!!", "Login Success");
    }
    @Override
    public void onCancel() {
        Log.d("!!!!!!", "Login Canceled");
    }
    @Override
    public void onError(FacebookException error) {
        Log.d("!!!!!!", "Login Error - " + error.toString());
    }

    public void makeMapSnapShot(GoogleMap mMap) {
        mMap.snapshot(new GoogleMap.SnapshotReadyCallback() {
            @Override
            public void onSnapshotReady(Bitmap bitmap) {

                StringBuilder text = new StringBuilder();
                float distance = utils.calculateDistanceBetweenPoints(pointsOnMap);
                Log.d("!!!!!!", "" + distance);

                text.append("I just done ")
                        .append(distance)
                        .append(" km\n")
                        .append(" with application Motion View");

                utils.sharePhotoToFacebook(bitmap, context, text.toString());
            }
        });
    }

}