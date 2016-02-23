package com.erminesoft.motionview.motionview.core.callback;

import com.google.android.gms.fitness.data.DataSet;

import java.util.List;

public interface DataChangedListener {

    void onError(String error);

    void onSuccess(List<DataSet> dataSets);
}
