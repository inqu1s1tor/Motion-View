package com.erminesoft.motionview.motionview.ui.factory;

import android.support.v4.app.Fragment;

import com.erminesoft.motionview.motionview.ui.fragments.HistoryFragment;
import com.erminesoft.motionview.motionview.ui.fragments.TodayFragment;

import static com.erminesoft.motionview.motionview.util.FragmentsType.HISTORY;
import static com.erminesoft.motionview.motionview.util.FragmentsType.MAP;
import static com.erminesoft.motionview.motionview.util.FragmentsType.TODAY;

public class FragmentsFactory {

    public static Fragment getFragment(String name) {
        Fragment fragment = null;

        switch (name.toLowerCase()) {
            case HISTORY:
                fragment = new HistoryFragment();
                break;
            case TODAY:
                fragment = new TodayFragment();
                break;
            case MAP:
                fragment = new Fragment();
                break;
        }

        return fragment;
    }
}
