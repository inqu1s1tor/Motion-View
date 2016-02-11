package com.erminesoft.motionview.motionview.core.callback;

import android.support.annotation.Nullable;

public interface ResultListener<T> {
    void onResult(@Nullable T result);
}
