package com.erminesoft.motionview.motionview.bridge;

import com.erminesoft.motionview.motionview.core.MVApplication;
import com.erminesoft.motionview.motionview.ui.FragmentLauncher;
import com.google.android.gms.fitness.data.DataSet;

import java.util.List;

public interface ActivityBridge {

    MVApplication getMVApplication();

    FragmentLauncher getFragmentLauncher();

    void setTabState(int visibility);

    void setTitle(String title);

    void showDailyStatisticFragment(List<DataSet> dataSets, long timestamp);
}
