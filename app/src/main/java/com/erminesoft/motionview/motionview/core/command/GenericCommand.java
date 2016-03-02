package com.erminesoft.motionview.motionview.core.command;

import android.os.Bundle;

import com.erminesoft.motionview.motionview.core.callback.ResultCallback;
import com.erminesoft.motionview.motionview.net.GoogleClientFacade;

class GenericCommand implements Command {
    private boolean mDenied;
    protected GoogleClientFacade mGoogleClientFacade;

    @Override
    public void execute(ResultCallback callback, Bundle bundle) {
        mDenied = false;
    }

    @Override
    public void deny() {
        mDenied = true;
    }

    boolean isDenied() {
        return mDenied;
    }

    void setGoogleClientFacade(GoogleClientFacade googleClientFacade) {
        mGoogleClientFacade = googleClientFacade;
    }
}
