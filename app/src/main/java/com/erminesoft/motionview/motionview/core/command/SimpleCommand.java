package com.erminesoft.motionview.motionview.core.command;

import android.os.Bundle;
import android.util.Log;

import com.erminesoft.motionview.motionview.core.callback.ResultCallback;

public class SimpleCommand implements Command {
    private static final String TAG = SimpleCommand.class.getSimpleName();

    @Override
    public void execute(ResultCallback callback, Bundle bundle) {
        execute(callback);
    }

    @Override
    public void execute(ResultCallback callback) {
        Log.i(TAG, "Executed simple command. Nothing changed.");
    }

    @Override
    public void abort() {
        Log.i(TAG, "Aborted simple command. Nothing changed.");
    }
}
