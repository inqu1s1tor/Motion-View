package com.erminesoft.motionview.motionview.core.command;

import android.os.Bundle;

import com.erminesoft.motionview.motionview.core.callback.ResultCallback;

public interface Command {

    void execute(ResultCallback callback);

    void execute(ResultCallback callback, Bundle bundle);

    void abort();

}
