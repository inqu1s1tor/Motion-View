package com.erminesoft.motionview.motionview.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.erminesoft.motionview.motionview.R;
import com.erminesoft.motionview.motionview.ui.fragments.DailyStatisticFragment;
import com.erminesoft.motionview.motionview.ui.fragments.GenericFragment;
import com.erminesoft.motionview.motionview.ui.fragments.GoogleMapsFragment;
import com.erminesoft.motionview.motionview.ui.fragments.HistoryFragment;
import com.erminesoft.motionview.motionview.ui.fragments.TodayFragment;

public class FragmentLauncher {
    private final FragmentManager manager;

    public FragmentLauncher(FragmentManager manager) {
        this.manager = manager;
    }

    private void launchWithoutAnimation(GenericFragment fragment, String tag) {
        launch(fragment, tag, 0, 0);
    }

    private void launch(GenericFragment fragment, String tag, int animationIn, int AnimationOut) {
        FragmentTransaction transaction = manager.beginTransaction();

        Fragment f = manager.findFragmentByTag(tag);
        Fragment visibleFragment = getVisibleFragment();

        if (visibleFragment != null) {
            transaction.hide(visibleFragment);
        }

        if (tag.compareTo(GenericFragment.DAILY_STATISTIC) == 0) {
            transaction.replace(R.id.fragment_container, fragment).commitAllowingStateLoss();
            return;
        }

        if (f == null) {
            transaction.add(R.id.fragment_container, fragment, tag);
        } else {
            transaction.show(f);
        }

        transaction.commitAllowingStateLoss();
    }

    private Fragment getVisibleFragment() {
        Fragment fragment = null;

        if (manager.getFragments() == null) {
            return null;
        }

        for (Fragment f : manager.getFragments()) {
            if (f != null && f.isVisible()) {
                fragment = f;
                break;
            }
        }

        return fragment;
    }


    public void launchTodayFragment() {
        GenericFragment fragment = new TodayFragment();
        launchWithoutAnimation(fragment, GenericFragment.TODAY);
    }

    //Chto eto?
    public void launchDailyStatisticFragment(long timestamp) {
        GenericFragment fragment = new DailyStatisticFragment();

        Bundle bundle = DailyStatisticFragment.buildArgs(timestamp);
        fragment.setArguments(bundle);

        launchWithoutAnimation(fragment, GenericFragment.DAILY_STATISTIC);
    }

    public void launchHistoryFragment() {
        GenericFragment fragment;

        Fragment existState = manager.findFragmentByTag(GenericFragment.HISTORY);
        if (existState == null) {
            fragment = new HistoryFragment();
        } else {
            fragment = (GenericFragment) existState;
        }
        launchWithoutAnimation(fragment, GenericFragment.HISTORY);
    }

    public void launchMapFragment() {
        GenericFragment fragment = new GoogleMapsFragment();
        launchWithoutAnimation(fragment, GenericFragment.MAP);
    }

    public void launchByTag(String tag) {
        switch (tag.toLowerCase()) {
            case GenericFragment.HISTORY:
                launchHistoryFragment();
                break;

            case GenericFragment.MAP:
                launchMapFragment();
                break;

            default:
                launchTodayFragment();
                break;
        }
    }
}
