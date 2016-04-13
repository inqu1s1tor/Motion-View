package com.erminesoft.motionview.motionview.ui.activities;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

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
import com.twitter.sdk.android.tweetcomposer.TweetComposer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.fabric.sdk.android.Fabric;

public class ShareMapActivity extends GenericActivity implements OnMapReadyCallback {
    private static final int SHARE_TYPE_GPLUS = 160;
    private static final int SHARE_TYPE_FACEBOOK = 64206;
    private static final int SHARE_TYPE_TWITTER = 170;

    private static final String DATA_FOR_SHARE = "dataforshare";
    private static final String TOTAL_ROUTE = "totalroute";
    private static final String TOTAL_TIME = "totaltime";
    private static final String TOTAL_DISTANCE = "totaldistance";
    private static final String TOTAL_CALORIES = "totalcalories";

    private List<LatLng> pointsOnMap = new ArrayList<>();
    private Button shareToFacebook;
    private Button shareToGooglePlus;
    private Button shareToTwitter;
    private TextView distance;
    private TextView trackTime;
    private TextView calories;

    private Bundle dataForShareBundle;

    private CallbackManager callbackManager;
    private LoginManager loginManager;
    private GoogleMap mMap;
    private Utils utils;



    public static void start(Activity activity, Bundle dataForShare) {
        Intent intent = new Intent(activity, ShareMapActivity.class);
        intent.putExtra(DATA_FOR_SHARE, dataForShare);
        activity.startActivityForResult(intent, 100);
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = View.inflate(this, R.layout.activity_sharing_map, null);
        setContentView(view);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setHomeAsUpEnabled(true);
        setTitle(getString(R.string.share_activity_title));

        dataForShareBundle = new Bundle(getIntent().getBundleExtra(DATA_FOR_SHARE));

        utils = new Utils();

        distance = (TextView) findViewById(R.id.sharing_activity_distance);
        trackTime = (TextView) findViewById(R.id.sharing_activity_time);
        calories = (TextView) findViewById(R.id.sharing_activity_calories);

        pointsOnMap =  dataForShareBundle.getParcelableArrayList(TOTAL_ROUTE);
        distance.setText(dataForShareBundle.getString(TOTAL_DISTANCE));
        trackTime.setText(dataForShareBundle.getString(TOTAL_TIME));
        calories.setText(dataForShareBundle.getString(TOTAL_CALORIES));

        shareToFacebook = (Button) view.findViewById(R.id.share_map_button);
        shareToGooglePlus = (Button) findViewById(R.id.share_google_button);
        shareToTwitter = (Button) findViewById(R.id.share_to_twitter_button);

        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_for_share);
        mapFragment.getMapAsync(this);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
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
                case SHARE_TYPE_TWITTER:
                    showShortToast("Successfully Shared to Twitter");
                    finish();
            }
        }
    }


    @Override
    public void onMapReady(final GoogleMap googleMap) {
        mMap = googleMap;
        if (pointsOnMap.size() > 1) {
            mGoogleFitnessFacade.drawRouteByPointsOnMap(pointsOnMap, googleMap);
        } else {
            Log.d("!!!!", "No data from intent");
        }
        initShareButtons(googleMap);
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


    private void initShareButtons(final GoogleMap googleMap) {

        shareToFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FacebookSdk.sdkInitialize(getApplicationContext());
                callbackManager = CallbackManager.Factory.create();
                List<String> permissionNeeds = Collections.singletonList("publish_actions");
                loginManager = LoginManager.getInstance();
                loginManager.logInWithPublishPermissions(ShareMapActivity.this, permissionNeeds);
                loginManager.registerCallback(callbackManager, new FacebookLoginPostCallback(ShareMapActivity.this, googleMap, pointsOnMap));
            }
        });

        shareToGooglePlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!utils.isPackageInstalled("com.google.android.apps.plus",ShareMapActivity.this)) {
                    showShortToast("Google Plus Application must be installed");
                    return;
                }
                mMap.snapshot(new GoogleMap.SnapshotReadyCallback() {
                    @Override
                    public void onSnapshotReady(Bitmap bitmap) {
                        String additionalText = "";
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
                if (!utils.isPackageInstalled("com.twitter.android", ShareMapActivity.this)) {
                    showShortToast("Twitter Application must be installed");
                    return;
                }
                mMap.snapshot(new GoogleMap.SnapshotReadyCallback() {
                    @Override
                    public void onSnapshotReady(Bitmap bitmap) {
                        TwitterAuthConfig authConfig = new TwitterAuthConfig("oHn7BE22MILtYKAgjPvFgxA2k", "DxkB7exzqaR8sdvLFvBqPBew8vUkB81BY1fI1UiDfKO6VQiWtl");
                        Fabric.with(ShareMapActivity.this, new Twitter(authConfig));

                        TweetComposer.Builder builder = new TweetComposer.Builder(ShareMapActivity.this);

                        String additionalText = "";
                        float distance = utils.calculateDistanceBetweenPoints(pointsOnMap);
                        String formattedText = String.format("I just done %.3f km\n"
                                + additionalText + " with application Motion View ", distance);

                        builder.image(utils.getImageUri(ShareMapActivity.this, bitmap));
                        builder.text(formattedText);
                        //builder.show();
                        startActivityForResult(builder.createIntent(), SHARE_TYPE_TWITTER);

                    }
                });
            }
        });
    }


    private class FacebookLoginPostCallback implements FacebookCallback<LoginResult> {
        private final Utils utils;
        private final Context context;
        private final GoogleMap mMap;
        private final List<LatLng> pointsOnMap;

        FacebookLoginPostCallback(Context ctx, GoogleMap map, List<LatLng> points) {
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

        void makeMapSnapShot(GoogleMap mMap) {
            mMap.snapshot(new GoogleMap.SnapshotReadyCallback() {
                @Override
                public void onSnapshotReady(Bitmap bitmap) {
                    String additionalText = "";
                    float distance = utils.calculateDistanceBetweenPoints(pointsOnMap);
                    String formattedText = String.format("I just done %.3f km\n"
                            + additionalText + " with application Motion View ", distance);

                    utils.sharePhotoToFacebook(bitmap, context, formattedText);
                }
            });
        }
    }
}