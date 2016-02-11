package com.erminesoft.motionview.motionview.core;

import android.app.Application;

import com.erminesoft.motionview.motionview.net.BluetoothManagerFacade;

public class MVApplication extends Application{

    private BluetoothManagerFacade managerFacade;

    @Override
    public void onCreate() {
        super.onCreate();

        managerFacade = new BluetoothManagerFacade();
    }

    public BluetoothManagerFacade getManagerFacade() {
        return managerFacade;
    }
}
