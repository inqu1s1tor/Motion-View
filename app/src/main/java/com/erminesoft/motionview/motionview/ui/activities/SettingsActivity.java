package com.erminesoft.motionview.motionview.ui.activities;


import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.erminesoft.motionview.motionview.R;
import com.erminesoft.motionview.motionview.core.bridge.Receiver;
import com.erminesoft.motionview.motionview.core.command.CommandType;
import com.erminesoft.motionview.motionview.core.command.ExecutorType;
import com.erminesoft.motionview.motionview.core.command.GetPersonCommand;
import com.erminesoft.motionview.motionview.net.plus.GooglePlusFacade;
import com.erminesoft.motionview.motionview.storage.DataBuffer;
import com.erminesoft.motionview.motionview.storage.SharedDataManager;
import com.google.android.gms.plus.model.people.Person;
import com.squareup.picasso.Picasso;

public class SettingsActivity extends GenericActivity implements Receiver {
    private static final String FITNESS_HISTORY_INTENT = "com.google.android.gms.fitness.settings.GOOGLE_FITNESS_SETTINGS";
    private static final String TAG = SettingsActivity.class.getSimpleName();

    private SharedDataManager mSharedDataManager;

    private TextInputLayout mUserWeightTextIl;
    private EditText mUserWeightText;

    private EditText mUserHeightText;

    private GooglePlusFacade mGooglePlusFacade;

    public static void start(Activity activity) {
        activity.startActivity(new Intent(activity, SettingsActivity.class));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mSharedDataManager = getMVApplication().getSharedDataManager();
        mGooglePlusFacade = getMVApplication().getGooglePlusFacade();
        mGooglePlusFacade.buildGoogleApiClient(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setTitle(getString(R.string.settings));
        initSettings();
        setHomeAsUpEnabled(true);
    }

    @Override
    protected void onStart() {
        super.onStart();

        DataBuffer.getInstance().register(CommandType.GET_PERSON, this);
        mGooglePlusFacade.signIn(this);
    }

    private void saveData() {
        String heightStr = String.valueOf(mUserHeightText.getText());
        int height = Integer.parseInt(heightStr);

        if (TextUtils.isEmpty(heightStr)) {
            mUserWeightTextIl.setError("Error");
            return;
        } else {
            mSharedDataManager.writeInt(SharedDataManager.USER_HEIGHT, Integer.parseInt(heightStr));
            mGoogleFitnessFacade.saveUserWeight((float) height);
        }

        int weight = Integer.parseInt(String.valueOf(mUserWeightText.getText()));

        mSharedDataManager.writeInt(SharedDataManager.USER_WEIGHT, weight);
        mGoogleFitnessFacade.saveUserHeight(weight);
    }

    @Override
    protected void onHomeButtonPressed() {
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1) {
            Bundle bundle = GetPersonCommand.generateBundle(data);
            getMVApplication().getCommander().execute(bundle, ExecutorType.SETTINGS_ACTIVITY);
        }
    }

    private void initSettings() {
        initWeight();
        initHeight();
        initCleanHistory();
    }

    private void initWeight() {
        mUserWeightText = (EditText) findViewById(R.id.settings_user_weight);
        mUserWeightText.setText(String.valueOf(mSharedDataManager.readInt(SharedDataManager.USER_WEIGHT)));
        mUserWeightTextIl = (TextInputLayout) findViewById(R.id.settings_user_weight_il);
        mUserWeightText.clearFocus();
    }

    private void initHeight() {
        mUserHeightText = (EditText) findViewById(R.id.settings_user_height);
        mUserHeightText.setText(String.valueOf(mSharedDataManager.readInt(SharedDataManager.USER_HEIGHT)));
        mUserHeightText.clearFocus();
    }

    private void initCleanHistory() {
        findViewById(R.id.settings_clean_history_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent fitnessSettings = new Intent(FITNESS_HISTORY_INTENT);
                startActivity(fitnessSettings);
            }
        });
    }

    @Override
    public void notify(Object data, CommandType type) {
        if (!(data instanceof Person)) {
            Log.e(TAG, "WRONG DATA");
            return;
        }
        Log.i(TAG, "notified");
        Person person = (Person) data;

        String coverPath;

        if (person.getCover() == null) {
            coverPath = ContentResolver.SCHEME_ANDROID_RESOURCE +
                    "://" + getResources().getResourcePackageName(R.drawable.default_cover)
                    + '/' + getResources().getResourceTypeName(R.drawable.default_cover)
                    + '/' + getResources().getResourceEntryName(R.drawable.default_cover);
        } else {
            coverPath = person.getCover().getCoverPhoto().getUrl();
        }

        Picasso.with(this).load(person.getImage().getUrl()).into((ImageView) findViewById(R.id.settings_avatar));
        ImageView coverView = (ImageView) findViewById(R.id.settings_profile_cover_image);
        Picasso.with(this)
                .load(coverPath)
                .centerCrop()
                .resize(coverView.getWidth(), coverView.getHeight())
                .into(coverView);
    }

    @Override
    protected void onPause() {
        super.onPause();

        saveData();
    }

    @Override
    protected void onStop() {
        super.onStop();

        DataBuffer.getInstance().unregister(this);
        getMVApplication().getCommander().denyAll(ExecutorType.SETTINGS_ACTIVITY);
    }
}
