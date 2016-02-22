package com.erminesoft.motionview.motionview.core.callback;

public interface DataChangedListener {

    void onStepsChanged(int steps);

    void onCaloriesChanged(float calories);

    void onDistanceChanged(float distance);

    void onTimeChanged(int time);

    void onSpeedChanged(float speed);

    void onError(String error);
}
