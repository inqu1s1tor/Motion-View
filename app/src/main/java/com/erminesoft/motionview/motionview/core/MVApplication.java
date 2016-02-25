package com.erminesoft.motionview.motionview.core;

import android.app.Application;

import com.erminesoft.motionview.motionview.net.GoogleClientFacade;
import com.erminesoft.motionview.motionview.storage.SharedDataManager;

public class MVApplication extends Application{
    private GoogleClientFacade mGoogleClientFacade;
    private SharedDataManager mSharedDataManager;

    @Override
    public void onCreate() {
        super.onCreate();

        mGoogleClientFacade = new GoogleClientFacade();
        mSharedDataManager = new SharedDataManager(this);
    }

    public GoogleClientFacade getGoogleClientFacade() {
        return mGoogleClientFacade;
    }

    public SharedDataManager getSharedDataManager() {
        return mSharedDataManager;
    }
}
