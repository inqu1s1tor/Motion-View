package com.erminesoft.motionview.motionview.ui.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.erminesoft.motionview.motionview.R;
import com.erminesoft.motionview.motionview.core.bridge.ActivityBridge;
import com.erminesoft.motionview.motionview.ui.FragmentLauncher;
import com.erminesoft.motionview.motionview.ui.fragments.ErrorDialogFragment;

public class MainFragmentActivity extends GenericActivity implements ActivityBridge {
    private TabLayout mTabLayout;
    private Toolbar mToolbar;
    private FragmentLauncher mFragmentLauncher;

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

        mFragmentLauncher = new FragmentLauncher(getSupportFragmentManager());
        mFragmentLauncher.launchTodayFragment();

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

                mFragmentLauncher.launchTodayFragment();
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
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ErrorDialogFragment.REQUEST_RESOLVE_ERROR) {
            mGoogleClientFacade.tryConnectClient(resultCode);
        }
    }

    @Override
    public FragmentLauncher getFragmentLauncher() {
        return mFragmentLauncher;
    }

    @Override
    public void setTitle(String title) {
        super.setTitle(title);
    }

    @Override
    public void onDailyStatisticLaunched() {
        setHomeAsUpEnabled(true);
        mTabLayout.setVisibility(View.GONE);
    }

    @Override
    protected void onHomeButtonPressed() {
        mFragmentLauncher.launchHistoryFragment();

        setHomeAsUpEnabled(false);
        setTitle(getString(R.string.app_name));
        mTabLayout.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getMVApplication().getCommander().denyAll();
    }

    private final class OnTabSelectedListenerImpl implements TabLayout.OnTabSelectedListener {
        @Override
        public void onTabSelected(TabLayout.Tab tab) {
            mFragmentLauncher.launchByTag(String.valueOf(tab.getText()));
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
