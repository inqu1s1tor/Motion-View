package com.erminesoft.motionview.motionview.core.callback;

import com.google.android.gms.fitness.data.Bucket;

import java.util.List;

public interface BucketsResultListener {

    void onSuccess(List<Bucket> buckets);
    void onError(String error);
}
