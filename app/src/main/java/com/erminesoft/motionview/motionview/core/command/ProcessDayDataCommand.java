package com.erminesoft.motionview.motionview.core.command;

import android.os.Bundle;
import android.util.Log;

import com.erminesoft.motionview.motionview.storage.DataBuffer;
import com.erminesoft.motionview.motionview.util.TimeWorker;
import com.google.android.gms.fitness.data.Bucket;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.result.DataReadResult;

import java.util.List;

public class ProcessDayDataCommand extends GenericCommand {
    private static final String TIMESTAMP_KEY = "timestamp";

    public static Bundle generateBundle(long timestamp) {
        Bundle bundle = new Bundle();

        bundle.putSerializable(Command.TRANSPORT_KEY, CommandType.PROCESS_DAY_DATA);
        bundle.putLong(TIMESTAMP_KEY, timestamp);

        return bundle;
    }

    @Override
    protected void execute() {
        Bundle bundle = getBundle();

        long timestamp = bundle.getLong(TIMESTAMP_KEY);

        DataReadResult result = mGoogleClientFacade.getDataPerDay(
                TimeWorker.getDay(timestamp),
                TimeWorker.getMonth(timestamp),
                TimeWorker.getYear(timestamp));

        final List<Bucket> buckets = result.getBuckets();
        if (buckets == null || buckets.size() == 0) {
            Log.e(TAG, "NO DATA");
            return;
        }

        List<DataSet> dataSets = buckets.get(0).getDataSets();

        if (!isDenied()) {
            DataBuffer.getInstance().putData(dataSets, CommandType.PROCESS_DAY_DATA);
        }
    }
}