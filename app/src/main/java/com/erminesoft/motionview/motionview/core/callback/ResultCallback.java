package com.erminesoft.motionview.motionview.core.callback;

import android.os.Bundle;

public interface ResultCallback {
    String RESULT_KEY = "result_key";

    void onSuccess(Bundle result);
}
