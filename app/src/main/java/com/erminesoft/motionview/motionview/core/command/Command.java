package com.erminesoft.motionview.motionview.core.command;

import android.os.Bundle;

import com.erminesoft.motionview.motionview.core.callback.ResultCallback;

public interface Command {

    final String TRANSPORT_KEY = "transport";

    void execute(ResultCallback callback, Bundle bundle);

    void deny();
}
