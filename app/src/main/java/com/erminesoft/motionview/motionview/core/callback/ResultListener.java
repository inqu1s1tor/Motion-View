package com.erminesoft.motionview.motionview.core.callback;

import android.support.annotation.Nullable;

public interface ResultListener<T> {

    void onSuccess(@Nullable T result);
    void onError(String error);
}
