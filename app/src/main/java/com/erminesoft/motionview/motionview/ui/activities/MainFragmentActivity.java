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

import com.erminesoft.motionview.motionview.R;
import com.erminesoft.motionview.motionview.ui.factory.FragmentsFactory;
import com.erminesoft.motionview.motionview.ui.fragments.ErrorDialogFragment;

public class MainFragmentActivity extends GenericActivity {
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
        mTabLayout.setSelected(true);
    }

    private void initTabs() {
        String[] tab_names = getResources().getStringArray(R.array.tabs_name);

        TabLayout.Tab tab;
        for (String name : tab_names) {
            tab = mTabLayout.newTab();

            tab.setText(name);
            mTabLayout.addTab(tab, name.equals(getString(R.string.today_date_text)));
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
                Intent settings = new Intent(FITNESS_HISTORY_INTENT);
                startActivity(settings);
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

    private final class OnTabSelectedListenerImpl implements TabLayout.OnTabSelectedListener {
        @Override
        public void onTabSelected(TabLayout.Tab tab) {
            FragmentTransaction transaction = mFragmentManager.beginTransaction();

            String tabName = String.valueOf(tab.getText());
            Fragment fragment = FragmentsFactory.getFragment(tabName);
            transaction.replace(R.id.fragment_container, fragment, tabName);

            transaction.commit();
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
