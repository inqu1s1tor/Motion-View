package com.erminesoft.motionview.motionview.ui.factory;

import com.erminesoft.motionview.motionview.ui.fragments.GenericFragment;
import com.erminesoft.motionview.motionview.ui.fragments.GoogleMapsFragment;
import com.erminesoft.motionview.motionview.ui.fragments.HistoryFragment;
import com.erminesoft.motionview.motionview.ui.fragments.TodayFragment;

import static com.erminesoft.motionview.motionview.util.FragmentsType.HISTORY;
import static com.erminesoft.motionview.motionview.util.FragmentsType.MAP;
import static com.erminesoft.motionview.motionview.util.FragmentsType.TODAY;

public class FragmentsFactory {

    public static GenericFragment getFragment(String name) {
        GenericFragment fragment = null;

        switch (name.toLowerCase()) {
            case HISTORY:
                fragment = new HistoryFragment();
                break;
            case TODAY:
                fragment = new TodayFragment();
                break;
            case MAP:
                fragment = new GoogleMapsFragment();
                break;
        }

        return fragment;
    }
}
