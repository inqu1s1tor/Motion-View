package com.erminesoft.motionview.motionview.core.command;

import android.os.Bundle;

import com.erminesoft.motionview.motionview.net.GoogleClientFacade;

class GenericCommand implements Command {
    protected final String TAG = this.getClass().getSimpleName();

    private boolean mDenied;
    private boolean mRunning;
    private Bundle mBundle;

    protected GoogleClientFacade mGoogleClientFacade;

    void setGoogleClientFacade(GoogleClientFacade googleClientFacade) {
        mGoogleClientFacade = googleClientFacade;
    }

    @Override
    public final void execute(Bundle bundle) {
        mRunning = true;
        mDenied = false;

        mBundle = bundle;

        execute();

        onExecuteFinished();
    }

    protected void execute() {
        // Empty
    }

    boolean isDenied() {
        return mDenied;
    }

    private void onExecuteFinished() {
        mRunning = false;
    }

    @Override
    public boolean isRunning() {
        return mRunning;
    }

    @Override
    public void deny() {
        mDenied = true;
    }

    public Bundle getBundle() {
        return mBundle;
    }
}
