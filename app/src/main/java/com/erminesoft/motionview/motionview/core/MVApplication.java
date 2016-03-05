package com.erminesoft.motionview.motionview.core;

import android.app.Application;


import com.erminesoft.motionview.motionview.core.command.Commander;
import com.erminesoft.motionview.motionview.net.fitness.GoogleFitnessFacade;
import com.erminesoft.motionview.motionview.storage.SharedDataManager;

public class MVApplication extends Application{
    private GoogleFitnessFacade mGoogleFitnessFacade;
    private SharedDataManager mSharedDataManager;
    private Commander mCommander;

    @Override
    public void onCreate() {
        super.onCreate();

        mGoogleFitnessFacade = new GoogleFitnessFacade();
        mSharedDataManager = new SharedDataManager(this);
        mCommander = new Commander(mGoogleFitnessFacade);
    }

    public GoogleFitnessFacade getGoogleFitnessFacade() {
        return mGoogleFitnessFacade;
    }

    public SharedDataManager getSharedDataManager() {
        return mSharedDataManager;
    }

    public Commander getCommander() {
        return mCommander;
    }
}
