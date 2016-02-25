package com.erminesoft.motionview.motionview.ui.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.erminesoft.motionview.motionview.R;
import com.erminesoft.motionview.motionview.bridge.ActivityBridge;
import com.erminesoft.motionview.motionview.ui.factory.FragmentsFactory;
import com.erminesoft.motionview.motionview.ui.fragments.DailyStatisticFragment;
import com.erminesoft.motionview.motionview.ui.fragments.ErrorDialogFragment;
import com.erminesoft.motionview.motionview.util.FragmentsType;
import com.google.android.gms.fitness.data.DataSet;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;

public class MainFragmentActivity extends GenericActivity implements ActivityBridge {
    private static final String FITNESS_HISTORY_INTENT = "com.google.android.gms.fitness.settings.GOOGLE_FITNESS_SETTINGS";

    private TabLayout mTabLayout;
    private Toolbar mToolbar;
    private FragmentManager mFragmentManager;

    public static void start(Activity activity) {
        activity.startActivity(new Intent(activity, MainFragmentActivity.class));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment_container);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        setTitle(getString(R.string.app_name));

        mFragmentManager = getSupportFragmentManager();

        mTabLayout = (TabLayout) findViewById(R.id.main_fragment_container_tab_container);

        initTabs();
        mTabLayout.setOnTabSelectedListener(new OnTabSelectedListenerImpl());
    }

    private void initTabs() {
        String[] tab_names = getResources().getStringArray(R.array.tabs_name);

        TabLayout.Tab tab;
        for (String name : tab_names) {
            tab = mTabLayout.newTab();

            tab.setText(name);
            boolean setSelected = false;

            if (name.equals(getString(R.string.today_date_text))) {
                setSelected = true;

                changeFragment(name);
            }

            mTabLayout.addTab(tab, setSelected);
        }
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
            case R.id.clear_history:
                Intent fitnessSettings = new Intent(FITNESS_HISTORY_INTENT);
                startActivity(fitnessSettings);
                break;
            case android.R.id.home:
                closeDailyStatisticFragment();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ErrorDialogFragment.REQUEST_RESOLVE_ERROR) {
            mGoogleClientFacade.tryConnectClient(resultCode);
        }
    }

    @Override
    public void showDailyStatisticFragment(List<DataSet> dataSets, long timestamp) {
        DateFormat format = DateFormat.getDateInstance();

        setTitle(format.format(new Date(timestamp)));
        setHomeAsUpEnabled(true);
        hideTabs();

        Fragment dailyStatistic = DailyStatisticFragment.create(dataSets, timestamp);
        replaceFragment(dailyStatistic);
    }

    private void closeDailyStatisticFragment() {
        changeFragment(FragmentsType.HISTORY);

        setHomeAsUpEnabled(false);
        showTabs();

        setTitle(getString(R.string.app_name));
    }

    private void changeFragment(String name) {
        Fragment fragment = FragmentsFactory.getFragment(name);
        replaceFragment(fragment, name);
    }

    private void replaceFragment(Fragment fragment) {
        replaceFragment(fragment, fragment.getClass().getSimpleName());
    }

    private void replaceFragment(Fragment fragment, @Nullable String tag) {
        FragmentTransaction transaction = mFragmentManager.beginTransaction();

        transaction.replace(R.id.fragment_container, fragment, tag);
        transaction.commit();
    }

    private void showTabs() {
        mTabLayout.setVisibility(View.VISIBLE);
    }

    private void hideTabs() {
        mTabLayout.setVisibility(View.GONE);
    }

    private final class OnTabSelectedListenerImpl implements TabLayout.OnTabSelectedListener {
        @Override
        public void onTabSelected(TabLayout.Tab tab) {
            changeFragment(String.valueOf(tab.getText()));
        }

        @Override
        public void onTabUnselected(TabLayout.Tab tab) {
            // Empty
        }

        @Override
        public void onTabReselected(TabLayout.Tab tab) {
            // Empty
        }
    }
}
