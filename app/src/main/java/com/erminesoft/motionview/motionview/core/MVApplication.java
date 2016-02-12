package com.erminesoft.motionview.motionview.core;

import android.app.Application;

import com.erminesoft.motionview.motionview.net.BluetoothManagerFacade;
import com.erminesoft.motionview.motionview.net.GoogleClientHelper;

public class MVApplication extends Application{
    private BluetoothManagerFacade mManagerFacade;
    private GoogleClientHelper mGoogleClientHelper;

    @Override
    public void onCreate() {
        super.onCreate();

        mManagerFacade = new BluetoothManagerFacade();
        mGoogleClientHelper = new GoogleClientHelper();
    }

    public BluetoothManagerFacade getManagerFacade() {
        return mManagerFacade;
    }

    public GoogleClientHelper getGoogleClientHelper() {
        return mGoogleClientHelper;
    }
}
