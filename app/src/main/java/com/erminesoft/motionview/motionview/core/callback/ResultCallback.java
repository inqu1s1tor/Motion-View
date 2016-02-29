package com.erminesoft.motionview.motionview.core.callback;

public interface ResultCallback<T> {
    void onSuccess(T result);

    void onError(String error);
}
