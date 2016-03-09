package com.erminesoft.motionview.motionview.ui.activities;


import android.app.Activity;
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
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.tweetcomposer.TweetComposer;
import com.twitter.sdk.android.tweetui.TweetUi;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.fabric.sdk.android.Fabric;

public class ShareMapActivity extends GenericActivity implements OnMapReadyCallback {


    public static void start(Activity activity) {
        activity.startActivity(new Intent(activity, ShareMapActivity.class));
    }

    private List<LatLng> pointsOnMap = new ArrayList<>();
    private View view;
    private Button share;
    private Button shareToGooglePlus;
    private Button shareToTwitter;
    private CallbackManager callbackManager;
    private LoginManager loginManager;
    private SupportMapFragment mapFragment;
    private GoogleMap mMap;
    private Utils utils;
    private static final int SHARE_TYPE_FACEBOOK = 64206;
    private static final int SHARE_TYPE_GPLUS = 160;
















    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHomeAsUpEnabled(true);
        utils = new Utils();
        view = View.inflate(this, R.layout.activity_sharing_map, null);
        setContentView(view);
        Bundle mapData = getIntent().getBundleExtra("mapPoints");
        pointsOnMap = mapData.getParcelableArrayList("mapPoints");
        share = (Button) view.findViewById(R.id.share_map_button);
        shareToGooglePlus = (Button) findViewById(R.id.share_google_button);
        shareToTwitter = (Button) findViewById(R.id.share_to_twitter_button);
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_for_share);
        mapFragment.getMapAsync(this);





























    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            Log.d("!!!!!", "result data: " + data.toString());
        }
        if (requestCode != 0 && resultCode != 0) {
            Log.d("!!!!!", "result code: " + resultCode + " req code: " + requestCode);
            //callbackManager.onActivityResult(requestCode, resultCode, data);
        }
        if (resultCode == -1) {
            switch (requestCode) {
                case SHARE_TYPE_FACEBOOK:
                    callbackManager.onActivityResult(requestCode, resultCode, data);
                    finish();
                    break;
                case SHARE_TYPE_GPLUS:
                    showShortToast("Successfully Shared to G+");
                    finish();
                    break;
            }

        }
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        mMap = googleMap;
        if (pointsOnMap.size() > 1) {
            mGoogleClientFacade.drawRouteByPointsOnMap(pointsOnMap, googleMap);
        } else {
            Log.d("!!!!", "No data from intent");
        }

        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FacebookSdk.sdkInitialize(getApplicationContext());
                callbackManager = CallbackManager.Factory.create();
                List<String> permissionNeeds = Arrays.asList("publish_actions");
                loginManager = LoginManager.getInstance();
                loginManager.logInWithPublishPermissions(ShareMapActivity.this, permissionNeeds);
                loginManager.registerCallback(callbackManager, new FacebookLoginPostCallback(ShareMapActivity.this, googleMap, pointsOnMap));
            }
        });

        shareToGooglePlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               /* if (!utils.isPackageInstalled("com.google.android.gms.plus", ShareMapActivity.this)) {
                    showLongToast("Google Plus application is not installed");
                    //Intent webIntent = new Intent(Intent.ACTION_INSTALL_PACKAGE, Uri.parse("play.google.com/store/apps/details?id=com.google.android.apps.plus"));
                    //startActivity(webIntent);

                    Intent goToMarket = new Intent(Intent.ACTION_VIEW)
                            .setData(Uri.parse("market://details?id=com.google.android.apps.plus"));
                    startActivity(goToMarket);
                    return;
                }*/

                mMap.snapshot(new GoogleMap.SnapshotReadyCallback() {
                    @Override
                    public void onSnapshotReady(Bitmap bitmap) {
                        String additionalText = new String();
                        float distance = utils.calculateDistanceBetweenPoints(pointsOnMap);
                        String formattedText = String.format("I just done %.3f km\n"
                                + additionalText + " with application Motion View ", distance);
                        Intent shareIntent = utils.shareToGooglePlus(bitmap, ShareMapActivity.this, formattedText);
                        startActivityForResult(shareIntent, SHARE_TYPE_GPLUS);
                    }
                });
            }
        });


        shareToTwitter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLongToast("share to twitter");



                mMap.snapshot(new GoogleMap.SnapshotReadyCallback() {
                    @Override
                    public void onSnapshotReady(Bitmap bitmap) {

                        TwitterAuthConfig authConfig =  new TwitterAuthConfig("oHn7BE22MILtYKAgjPvFgxA2k", "DxkB7exzqaR8sdvLFvBqPBew8vUkB81BY1fI1UiDfKO6VQiWtl");
                        Fabric.with(ShareMapActivity.this, new Twitter(authConfig));
                        TwitterCore core = Twitter.getInstance().core;
                        TweetUi tweetUi = Twitter.getInstance().tweetUi;
                        TweetComposer composer = Twitter.getInstance().tweetComposer;

                        TweetComposer.Builder builder = new TweetComposer.Builder(ShareMapActivity.this);



                        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                        Bitmap inImage = Bitmap.createBitmap(bitmap);
                        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
                        String path = MediaStore.Images.Media.insertImage(ShareMapActivity.this.getContentResolver(), inImage, "Title", null);


                        builder.image(Uri.fromFile(new File(path)));
                        builder.text(path);

                        builder.show();
                        //Intent intentt = builder.createIntent();
                        //startActivity(intentt);




                        TwitterSession session = TwitterCore.getInstance().getSessionManager()
                                .getActiveSession();



































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

                String additionalText = new String();
                float distance = utils.calculateDistanceBetweenPoints(pointsOnMap);
                String formattedText = String.format("I just done %.3f km\n"
                        + additionalText + " with application Motion View ", distance);

                utils.sharePhotoToFacebook(bitmap, context, formattedText.toString());
            }
        });
    }

}