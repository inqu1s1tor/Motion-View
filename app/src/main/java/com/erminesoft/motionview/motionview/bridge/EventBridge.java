package com.erminesoft.motionview.motionview.bridge;

import com.google.android.gms.fitness.data.DataPoint;

import java.util.List;

public interface EventBridge {
    void onTotalTimeChanged(List<DataPoint> dataPoints);

    void onDistanceChanged(List<DataPoint> dataPoints);

    void onCaloriesChanged(List<DataPoint> dataPoints);

    void onStepsChanged(List<DataPoint> dataPoints);

    void onSpeedChanged(List<DataPoint> dataPoints);
}
