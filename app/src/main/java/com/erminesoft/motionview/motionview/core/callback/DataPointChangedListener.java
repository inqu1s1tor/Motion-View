package com.erminesoft.motionview.motionview.core.callback;

import com.google.android.gms.fitness.data.DataPoint;

import java.util.List;

public interface DataPointChangedListener {
    void onError(String error);

    void onSuccess(List<DataPoint> dataPoints);
}
