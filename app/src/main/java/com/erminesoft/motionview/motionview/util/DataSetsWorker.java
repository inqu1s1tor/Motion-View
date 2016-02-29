package com.erminesoft.motionview.motionview.util;

import com.erminesoft.motionview.motionview.core.bridge.EventBridge;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataType;

import java.util.List;

public class DataSetsWorker {

    public static void processDataSets(List<DataSet> dataSets, EventBridge eventObject) {
        for (DataSet dataSet : dataSets) {
            DataType dataType = dataSet.getDataType();
            List<DataPoint> dataPoints = dataSet.getDataPoints();

            if (dataType.equals(DataType.AGGREGATE_ACTIVITY_SUMMARY)) {
                eventObject.onTotalTimeChanged(dataPoints);
                continue;
            }

            if (dataType.equals(DataType.AGGREGATE_CALORIES_EXPENDED)) {
                eventObject.onCaloriesChanged(dataPoints);
                continue;
            }

            if (dataType.equals(DataType.AGGREGATE_DISTANCE_DELTA)) {
                eventObject.onDistanceChanged(dataPoints);
                continue;
            }

            if (dataType.equals(DataType.AGGREGATE_STEP_COUNT_DELTA)) {
                eventObject.onStepsChanged(dataPoints);
            }
        }
    }
}
