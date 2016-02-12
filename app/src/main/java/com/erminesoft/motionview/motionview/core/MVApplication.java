package com.erminesoft.motionview.motionview.core;

import android.app.Application;

import com.erminesoft.motionview.motionview.net.GoogleClientFacade;

public class MVApplication extends Application{
    private GoogleClientFacade mGoogleClientFacade;

    @Override
    public void onCreate() {
        super.onCreate();

        mGoogleClientFacade = new GoogleClientFacade();
    }

    public GoogleClientFacade getGoogleClientFacade() {
        return mGoogleClientFacade;
    }
}
