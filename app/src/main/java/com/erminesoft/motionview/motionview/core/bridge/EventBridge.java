package com.erminesoft.motionview.motionview.core.bridge;

import com.google.android.gms.fitness.data.DataPoint;

import java.io.Serializable;
import java.util.List;

public interface EventBridge extends Serializable {
    void onTotalTimeChanged(List<DataPoint> dataPoints);

    void onDistanceChanged(List<DataPoint> dataPoints);

    void onCaloriesChanged(List<DataPoint> dataPoints);

    void onStepsChanged(List<DataPoint> dataPoints);
}
