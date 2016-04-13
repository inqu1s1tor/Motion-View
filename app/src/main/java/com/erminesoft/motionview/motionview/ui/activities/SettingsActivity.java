package com.erminesoft.motionview.motionview.ui.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

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

    private TextInputLayout mUserHeightTextIl;
    private EditText mUserHeightText;

    private TextInputLayout mUserDailyGoalTextIl;
    private EditText mUserDailyGoalText;
    private ImageView coverView;

    private GooglePlusFacade mGooglePlusFacade;

    public static void start(Activity activity) {
        activity.startActivity(new Intent(activity, SettingsActivity.class));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mSharedDataManager = getMVApplication().getSharedDataManager();
        mGooglePlusFacade = getMVApplication().getGooglePlusFacade();
        mGooglePlusFacade.buildGoogleApiClient(this);

        findViewById(R.id.settings_save_weight_height_button).setOnClickListener(new Clicker());

        coverView = (ImageView) findViewById(R.id.settings_profile_cover_image);

        mUserDailyGoalText = (EditText) findViewById(R.id.settings_daily_goal);
        mUserDailyGoalText.setText(
                String.valueOf(mSharedDataManager.readInt(SharedDataManager.USER_DAILY_GOAL)));

        mUserDailyGoalTextIl = (TextInputLayout) findViewById(R.id.settings_daily_goal_il);

        TextView cleanActivityHistory = (TextView) findViewById(R.id.settings_delete_history_header);
        cleanActivityHistory.setOnClickListener(new Clicker());

        findViewById(R.id.settings_clean_history_button).setOnClickListener(new Clicker());

        cleanActivityHistory.setPaintFlags(cleanActivityHistory.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        String coverURL = getMVApplication().getSharedDataManager().getCoverURL();

        Picasso.with(this)
                .load(coverURL)
                .placeholder(getResources().getDrawable(R.drawable.default_cover))
                .into(coverView);

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


    private boolean saveData() {
        String weightStr = String.valueOf(mUserWeightText.getText());
        if (TextUtils.isEmpty(weightStr)) {
            mUserWeightTextIl.setError(getString(R.string.settings_empty_error));
            return false;
        }

        int weight = Integer.parseInt(weightStr);
        if (weight > 300) {
            mUserWeightTextIl.setError(getString(R.string.settings_validate_weight_field));
            return false;
        }

        String heightStr = String.valueOf(mUserHeightText.getText());
        if (TextUtils.isEmpty(heightStr)) {
            mUserHeightTextIl.setError(getString(R.string.settings_empty_error));
            return false;
        }

        int height = Integer.parseInt(heightStr);
        if (height > 300) {
            mUserHeightTextIl.setError(getString(R.string.settings_validate_height_field));
            return false;
        }

        String dailyGoalStr = String.valueOf(mUserDailyGoalText.getText());
        if (TextUtils.isEmpty(dailyGoalStr)) {
            mUserDailyGoalTextIl.setError(getString(R.string.settings_empty_error));
            return false;
        }

        int dailyGoal = Integer.parseInt(dailyGoalStr);
        if (dailyGoal < 1000 || dailyGoal >= 100000) {
            mUserDailyGoalTextIl.setError(getString(R.string.settings_daily_goal_error));
            return false;
        }

        mUserWeightTextIl.setErrorEnabled(false);
        mUserHeightTextIl.setErrorEnabled(false);
        mUserDailyGoalTextIl.setErrorEnabled(false);


        mSharedDataManager.writeInt(SharedDataManager.USER_WEIGHT, weight);
        mGoogleFitnessFacade.saveUserHeight(weight);

        mSharedDataManager.writeInt(SharedDataManager.USER_HEIGHT, Integer.parseInt(heightStr));
        mGoogleFitnessFacade.saveUserWeight((float) height);

        mSharedDataManager.writeInt(SharedDataManager.USER_DAILY_GOAL, dailyGoal);

        return true;
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
        mUserHeightTextIl = (TextInputLayout) findViewById(R.id.settings_user_height_il);
        mUserHeightText.clearFocus();
    }

    private void initCleanHistory() {
        Intent fitnessSettings = new Intent(FITNESS_HISTORY_INTENT);
        startActivity(fitnessSettings);
    }

    @Override
    public void notify(Object data, CommandType type) {
        if (!(data instanceof Person)) {
            Log.e(TAG, "WRONG DATA");
            return;
        }

        Person person = (Person) data;

        String coverPath;

        if (person.getCover() != null) {

            coverPath = person.getCover().getCoverPhoto().getUrl();
            getMVApplication().getSharedDataManager().setCoverURL(coverPath);

            Picasso.with(this)
                    .load(coverPath)
                    .centerCrop()
                    .resize(coverView.getWidth(), coverView.getHeight())
                    .into(coverView);
        }

        String avatarURL = person.getImage().getUrl();
        StringBuilder builder = new StringBuilder(avatarURL);
        builder.replace(avatarURL.length() - 2, avatarURL.length() - 1, "100");

        Picasso.with(this)
                .load(builder.toString())
                .into((ImageView) findViewById(R.id.settings_avatar));
    }

    @Override
    protected void onStop() {
        super.onStop();

        DataBuffer.getInstance().unregister(this);
        getMVApplication().getCommander().denyAll(ExecutorType.SETTINGS_ACTIVITY);
    }

    private void buttonSavePressed() {

        if(saveData()) {
            InputMethodManager manager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);

            View view = getCurrentFocus();
            if (view != null) {
                manager.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }

            showShortToast(getString(R.string.saved_data_toast));

        }else {
//            showShortToast("data not save");


        }

    }

    private void buttonClearPressed() {
        mUserWeightText.setText("");
        mUserHeightText.setText("");
        mUserDailyGoalText.setText("");
    }

    private class Clicker implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.settings_delete_history_header:
                    initCleanHistory();
                    break;
                case R.id.settings_save_weight_height_button:
                    buttonSavePressed();
                    break;
                case R.id.settings_clean_history_button:
                    buttonClearPressed();
            }

        }
    }
}
