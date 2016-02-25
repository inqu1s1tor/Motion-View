package com.erminesoft.motionview.motionview.ui;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;

import com.erminesoft.motionview.motionview.R;
import com.erminesoft.motionview.motionview.ui.fragments.DailyStatisticFragment;
import com.erminesoft.motionview.motionview.ui.fragments.GenericFragment;
import com.erminesoft.motionview.motionview.ui.fragments.TodayFragment;
import com.google.android.gms.fitness.data.DataSet;

import java.util.List;

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

        if (!TextUtils.isEmpty(tag)) {
            transaction.addToBackStack(tag);
        }

        transaction.replace(R.id.fragment_container, fragment);
        transaction.commitAllowingStateLoss();

    }


    public void launchTodayFragment() {
        GenericFragment fragment = new TodayFragment();
        launchWithoutAnimation(fragment, null);
    }

    public void launchDailyStatisticFragment(List<DataSet> datasets, long timestamp) {
        Bundle bundle = DailyStatisticFragment.buildArgs(datasets, timestamp);
        GenericFragment fragment = new DailyStatisticFragment();
        fragment.setArguments(bundle);
        launchWithoutAnimation(fragment, null);
    }

}
