package com.erminesoft.motionview.motionview.core.command;

import android.os.Bundle;

import com.erminesoft.motionview.motionview.core.callback.ResultCallback;
import com.erminesoft.motionview.motionview.util.TimeWorker;
import com.google.android.gms.fitness.data.Bucket;
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
    public void execute(final ResultCallback callback, Bundle bundle) {
        super.execute(callback, bundle);

        if (bundle.equals(Bundle.EMPTY)) {
            callback.onError("EMPTY BUNDLE");
            return;
        }

        if (mGoogleClientFacade == null) {
            callback.onError("EMPTY GOOGLE CLIENT FACADE");
            return;
        }

        long timestamp = bundle.getLong(TIMESTAMP_KEY);

        DataReadResult result = mGoogleClientFacade.getDataPerDay(
                TimeWorker.getDay(timestamp),
                TimeWorker.getMonth(timestamp),
                TimeWorker.getYear(timestamp));

        final List<Bucket> buckets = result.getBuckets();
        if (buckets == null || buckets.size() == 0) {
            callback.onError("NO DATA");
            return;
        }

        if (!isDenied()) {
            callback.onSuccess(buckets.get(0).getDataSets());
        }
    }
}