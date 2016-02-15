package com.erminesoft.motionview.motionview.core.callback;

public interface ResultListener<T> {

    void onSuccess(T result);
    void onError(String error);
}
