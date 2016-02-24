package com.erminesoft.motionview.motionview.ui.factory;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.erminesoft.motionview.motionview.ui.fragments.HistoryFragment;
import com.erminesoft.motionview.motionview.ui.fragments.TodayFragment;

public class FragmentsFactory {

    public static
    @Nullable
    Fragment getFragment(String name) {
        Fragment fragment = null;

        switch (name.toLowerCase()) {
            case "history":
                fragment = new HistoryFragment();
                break;
            case "today":
                fragment = new TodayFragment();
                break;
            case "map":
                fragment = new Fragment();
                break;
        }

        return fragment;
    }
}
