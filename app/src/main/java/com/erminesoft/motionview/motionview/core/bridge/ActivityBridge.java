package com.erminesoft.motionview.motionview.core.bridge;

import com.erminesoft.motionview.motionview.core.MVApplication;
import com.erminesoft.motionview.motionview.ui.FragmentLauncher;

public interface ActivityBridge {

    MVApplication getMVApplication();

    FragmentLauncher getFragmentLauncher();

    void setTitle(String title);

    void onDailyStatisticLaunched();
}
