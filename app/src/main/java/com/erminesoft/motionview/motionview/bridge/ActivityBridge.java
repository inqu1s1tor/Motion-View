package com.erminesoft.motionview.motionview.bridge;

import com.erminesoft.motionview.motionview.core.MVApplication;
import com.google.android.gms.fitness.data.DataSet;

import java.util.List;

public interface ActivityBridge {
    MVApplication getMVApplication();

    void setTitle(String title);

    void showDailyStatisticFragment(List<DataSet> dataSets, long timestamp);
}
