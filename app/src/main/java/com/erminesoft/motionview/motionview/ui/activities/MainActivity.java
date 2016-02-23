package com.erminesoft.motionview.motionview.ui.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.erminesoft.motionview.motionview.R;
import com.erminesoft.motionview.motionview.util.TimeWorker;

public class MainActivity extends BasicDailyStatisticActivity {
    private TextView mDateTextView;

    public static void start(Activity activity) {
        activity.startActivity(new Intent(activity, MainActivity.class));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.app_name);

        mDateTextView = (TextView) findViewById(R.id.date_text_view);
        mDateTextView.setText(getString(R.string.today_date_text));
    }

    @Override
    protected void onStart() {
        super.onStart();

        mGoogleClientFacade.getDataPerDay(
                TimeWorker.getCurrentDay(),
                TimeWorker.getCurrentMonth(),
                TimeWorker.getCurrentYear(),
                new DataChangedListenerImpl());

        mGoogleClientFacade.registerListenerForStepCounter(new DataChangedListenerImpl());
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
}
