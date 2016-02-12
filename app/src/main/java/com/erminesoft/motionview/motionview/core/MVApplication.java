package com.erminesoft.motionview.motionview.core;

import android.app.Application;

import com.erminesoft.motionview.motionview.net.GoogleClientFacade;
import com.erminesoft.motionview.motionview.net.GoogleClientHelper;

public class MVApplication extends Application{
    private GoogleClientFacade mManagerFacade;
    private GoogleClientHelper mGoogleClientHelper;

    @Override
    public void onCreate() {
        super.onCreate();

        mManagerFacade = new GoogleClientFacade();
        mGoogleClientHelper = new GoogleClientHelper();
    }

    public GoogleClientFacade getManagerFacade() {
        return mManagerFacade;
    }

    public GoogleClientHelper getGoogleClientHelper() {
        return mGoogleClientHelper;
    }
}
