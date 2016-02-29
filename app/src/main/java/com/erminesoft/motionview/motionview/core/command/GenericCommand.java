package com.erminesoft.motionview.motionview.core.command;

import android.os.Bundle;

import com.erminesoft.motionview.motionview.core.callback.ResultCallback;

public class GenericCommand implements Command {
    private boolean mDenied;

    @Override
    public void execute(ResultCallback callback, Bundle bundle) {
        mDenied = false;
    }

    @Override
    public void deny() {
        mDenied = true;
    }

    protected boolean isDenied() {
        return mDenied;
    }
}
