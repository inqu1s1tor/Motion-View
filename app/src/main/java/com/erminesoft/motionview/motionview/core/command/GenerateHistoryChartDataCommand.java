package com.erminesoft.motionview.motionview.core.command;

import android.os.Bundle;

import com.erminesoft.motionview.motionview.core.callback.ResultCallback;
import com.erminesoft.motionview.motionview.util.ChartDataWorker;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.google.android.gms.fitness.data.Bucket;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.result.DataReadResult;

import java.util.ArrayList;
import java.util.List;

public class GenerateHistoryChartDataCommand extends GenericCommand {

    private static final String MONTH_KEY = "month";
    private static final String YEAR_KEY = "year";

    public static Bundle generateBundle(ChartDataWorker.Month month, int year) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(TRANSPORT_KEY, CommandType.GENERATE_HISTORY_CHART_DATA);
        bundle.putSerializable(MONTH_KEY, month);
        bundle.putInt(YEAR_KEY, year);

        return bundle;
    }

    @Override
    public void execute(final ResultCallback callback, Bundle bundle) {
        super.execute(callback, bundle);

        if (!bundle.containsKey(MONTH_KEY)) {
            callback.onError("EMPTY BUNDLE");
            return;
        }

        ChartDataWorker.Month month = (ChartDataWorker.Month) bundle.getSerializable(MONTH_KEY);
        int year = bundle.getInt(YEAR_KEY);

        DataReadResult result = mGoogleClientFacade.getDataPerMonthFromHistory(month, year);
        List<Bucket> buckets = result.getBuckets();

        if (buckets.get(0).getDataSets().isEmpty()) {
            callback.onError("EMPTY DATA in " + getClass().getSimpleName());
            return;
        }

        if (!isDenied()) {
            callback.onSuccess(processStepsBuckets(buckets));
        }
    }

    private BarData processStepsBuckets(List<Bucket> buckets) {
        List<String> xVals = new ArrayList<>();
        List<BarEntry> entries = new ArrayList<>();

        for (int i = 0; i < buckets.size(); i++) {
            Bucket bucket = buckets.get(i);
            DataSet dataSet = bucket.getDataSet(DataType.TYPE_STEP_COUNT_DELTA);

            xVals.add(String.valueOf(i + 1));

            float steps;
            if (dataSet.getDataPoints().size() > 0) {
                DataPoint dataPoint = dataSet.getDataPoints().get(0);
                steps = dataPoint.getValue(Field.FIELD_STEPS).asInt();
            } else {
                steps = 0f;
            }

            entries.add(new BarEntry(steps, i, bucket.getDataSets()));
        }

        BarDataSet dataSet = new BarDataSet(
                entries, "");
        return new BarData(xVals, dataSet);
    }
}