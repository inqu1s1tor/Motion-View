package com.erminesoft.motionview.motionview.core.callback;

public interface ResultCallback {
    void onSuccess(Object result);

    void onError(String error);
}
