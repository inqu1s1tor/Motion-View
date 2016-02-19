package com.erminesoft.motionview.motionview.ui.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.erminesoft.motionview.motionview.R;
import com.erminesoft.motionview.motionview.core.callback.ResultListener;

public class MainActivity extends BasicDailyStatisticActivity {
    public static void start(Activity activity) {
        activity.startActivity(new Intent(activity, MainActivity.class));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.app_name);
    }

    @Override
    protected void onStart() {
        super.onStart();

        mGoogleClientFacade.getStepsPerDayFromHistory(new StepsChangingListener());
        mGoogleClientFacade.registerListenerForStepCounter(new StepsChangingListener());
    }

    @Override
    protected void onStop() {
        super.onStop();

        mGoogleClientFacade.unregisterListener();
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
                SettingsActivity.start(this);
                break;
            case R.id.history:
                HistoryActivity.start(this);
                break;
            case R.id.google_map:
                GoogleMapActivity.start(this);
                break;
            case R.id.clear_history:
                mGoogleClientFacade.clearData();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void setStepsCount(Integer result) {
        mProgressBar.setProgress(result);
        mStepsTextView.setText(String.format(getString(R.string.total_steps_text_format), result));
    }

    private final class StepsChangingListener implements ResultListener<Integer> {

        @Override
        public void onSuccess(@Nullable final Integer result) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    setStepsCount(result);
                }
            });
        }

        @Override
        public void onError(String error) {
            Log.e(TAG, error);
        }
    }
}
