package com.erminesoft.motionview.motionview.ui.activities;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.erminesoft.motionview.motionview.BuildConfig;
import com.erminesoft.motionview.motionview.R;
import com.erminesoft.motionview.motionview.ui.ButtonsAnimationHelper;
import com.erminesoft.motionview.motionview.util.DialogHelper;
import com.erminesoft.motionview.motionview.util.TimeWorker;
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
import java.util.Locale;

import io.fabric.sdk.android.Fabric;


public class ShareMapActivity extends GenericActivity implements OnMapReadyCallback {
    private static final int SHARE_TYPE_GPLUS = 160;
    private static final int SHARE_TYPE_FACEBOOK = 64206;
    private static final int SHARE_TYPE_TWITTER = 170;

    private static final String DATA_POINTS_EXTRA = "datapoints";
    private static final String SHARE_DISTANCE = "distance";
    private static final String SHARE_TIME = "time";
    private static final String SHARE_KCAL = "kcal";

    private ButtonsAnimationHelper animationHelper;

    private List<LatLng> pointsOnMap = new ArrayList<>();
    private FloatingActionButton shareToFacebook;
    private FloatingActionButton shareToGooglePlus;
    private FloatingActionButton shareToTwitter;
    private CallbackManager callbackManager;
    private LoginManager loginManager;
    private GoogleMap mMap;

    private TextView distance;
    private TextView trackTime;
    private TextView calories;

    private float distanceValue;

    private ProgressDialog progressDialog;

    public static void start(Activity activity, List<LatLng> dataPoints, float totalDistance, int totalTime, float totalKCal) {
        Intent intent = new Intent(activity, ShareMapActivity.class);
        intent.putParcelableArrayListExtra(DATA_POINTS_EXTRA, (ArrayList<LatLng>) dataPoints);
        intent.putExtra(SHARE_DISTANCE, totalDistance);
        intent.putExtra(SHARE_TIME, totalTime);
        intent.putExtra(SHARE_KCAL, totalKCal);
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

        distance = (TextView) findViewById(R.id.sharing_activity_distance);
        trackTime = (TextView) findViewById(R.id.sharing_activity_time);
        calories = (TextView) findViewById(R.id.sharing_activity_calories);

        Intent intent = getIntent();

        distanceValue = intent.getFloatExtra(SHARE_DISTANCE, 0);
        distance.setText(String.format(Locale.getDefault(), "%.3f", distanceValue));
        trackTime.setText(TimeWorker.processSecondsToString(intent.getIntExtra(SHARE_TIME, 0)));
        calories.setText(String.valueOf(intent.getFloatExtra(SHARE_KCAL, 0)));

        pointsOnMap = intent.getParcelableArrayListExtra(DATA_POINTS_EXTRA);


        FloatingActionButton commonActionButton = (FloatingActionButton) view.findViewById(R.id.share_menu_button);

        animationHelper = new ButtonsAnimationHelper(view,commonActionButton);

        shareToFacebook = (FloatingActionButton) view.findViewById(R.id.facebookActionButton);
        shareToGooglePlus = (FloatingActionButton) findViewById(R.id.googlePlusActionButton);
        shareToTwitter = (FloatingActionButton) findViewById(R.id.twitterActionButton);

        progressDialog = DialogHelper.createProgressDialog(this);

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
                    break;
                case SHARE_TYPE_GPLUS:
                    showShortToast("Successfully Shared to G+");
                    break;
                case SHARE_TYPE_TWITTER:
                    showShortToast("Successfully Shared to Twitter");
                    break;
            }
        }
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        mMap = googleMap;
            mGoogleFitnessFacade.drawRouteByPointsOnMap(pointsOnMap, googleMap);


        animationHelper.setActionCallback(new ShareButtonListener(googleMap));
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

    private void shareToFacebook(GoogleMap googleMap){
        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        List<String> permissionNeeds = Collections.singletonList("publish_actions");
        loginManager = LoginManager.getInstance();
        loginManager.logInWithPublishPermissions(ShareMapActivity.this, permissionNeeds);
        loginManager.registerCallback(callbackManager, new FacebookLoginPostCallback(ShareMapActivity.this, googleMap, pointsOnMap));
    }

    private void shareToGooglePlus(){
        if (!Utils.isPackageInstalled("com.google.android.apps.plus", ShareMapActivity.this)) {
            showShortToast("Google Plus Application must be installed");
            return;
        }
        progressDialog.show();
        mMap.snapshot(new GoogleMap.SnapshotReadyCallback() {
            @Override
            public void onSnapshotReady(Bitmap bitmap) {
                String formattedText = String.format("I just done %.3f km for %s\n with application Motion View ", distanceValue, trackTime.getText());
                Intent shareIntent = Utils.shareToGooglePlus(bitmap, ShareMapActivity.this, formattedText);
                startActivityForResult(shareIntent, SHARE_TYPE_GPLUS);
                progressDialog.dismiss();
            }
        });
    }

    private void setShareToTwitter(){
        if (!Utils.isPackageInstalled("com.twitter.android", ShareMapActivity.this)) {
            showShortToast("Twitter Application must be installed");
            return;
        }

        progressDialog.show();

        mMap.snapshot(new GoogleMap.SnapshotReadyCallback() {
            @Override
            public void onSnapshotReady(Bitmap bitmap) {
                TwitterAuthConfig authConfig = new TwitterAuthConfig(BuildConfig.TWITTER_CONSUMER_KEY, BuildConfig.TWITTER_SECRET_KEY);
                Fabric.with(ShareMapActivity.this, new Twitter(authConfig));

                TweetComposer.Builder builder = new TweetComposer.Builder(ShareMapActivity.this);

                String formattedText = String.format("I just done %.3f km for %s\n with application Motion View ", distanceValue, trackTime.getText());

                builder.image(Utils.getImageUri(ShareMapActivity.this, bitmap));
                builder.text(formattedText);

                startActivityForResult(builder.createIntent(), SHARE_TYPE_TWITTER);

                progressDialog.dismiss();
            }
        });
    }

    private class ShareButtonListener implements FloatingActionButton.OnClickListener{
        private GoogleMap googleMap;
        public ShareButtonListener(GoogleMap googleMap) {
            this.googleMap = googleMap;
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.share_menu_button:
                    animationHelper.showAction();
                    break;

                case R.id.facebookActionButton:
                    shareToFacebook(googleMap);
                    break;

                case R.id.twitterActionButton:
                    setShareToTwitter();
                    break;

                case R.id.googlePlusActionButton:
                    shareToGooglePlus();
                    break;
            }
        }
    }

    private class FacebookLoginPostCallback implements FacebookCallback<LoginResult> {
        private final Context context;
        private final GoogleMap mMap;

        FacebookLoginPostCallback(Context ctx, GoogleMap map, List<LatLng> points) {
            mMap = map;
            context = ctx;
            pointsOnMap = points;
        }

        @Override
        public void onSuccess(LoginResult loginResult) {
            makeMapSnapShot(mMap);
        }

        @Override
        public void onCancel() {
        }

        @Override
        public void onError(FacebookException error) {
        }

        void makeMapSnapShot(GoogleMap mMap) {
            mMap.snapshot(new GoogleMap.SnapshotReadyCallback() {
                @Override
                public void onSnapshotReady(Bitmap bitmap) {
                    String formattedText = String.format("I just done %.3f km for %s\n with application Motion View ", distanceValue, trackTime.getText());

                    Utils.sharePhotoToFacebook(bitmap, context, formattedText);
                }
            });
        }
    }
}