package com.erminesoft.motionview.motionview.services;


import com.google.android.gms.common.*;
import com.google.android.gms.common.api.*;
import com.google.android.gms.fitness.*;
import com.google.android.gms.fitness.data.*;
import com.google.android.gms.fitness.service.*;

import java.util.ArrayList;
import java.util.List;


public class MySensorService extends FitnessSensorService {

    @Override
    public void onCreate() {
        super.onCreate();
        // 1. Initialize your software sensor(s).
        // 2. Create DataSource representations of your software sensor(s).
        // 3. Initialize some data structure to keep track of a registration for each sensor.
    }

    @Override
    public List<DataSource> onFindDataSources(List<DataType> dataTypes) {
        // 1. Find which of your software sensors provide the data types requested.
        // 2. Return those as a list of DataSource objects.
        List<DataSource> src = new ArrayList<>();
        return src;
    }

    @Override
    public boolean onRegister(FitnessSensorServiceRequest request) {
        // 1. Determine which sensor to register with request.getDataSource().
        // 2. If a registration for this sensor already exists, replace it with this one.
        // 3. Keep (or update) a reference to the request object.
        // 4. Configure your sensor according to the request parameters.
        // 5. When the sensor has new data, deliver it to the platform by calling
        //    request.getDispatcher().publish(List<DataPoint> dataPoints)
        return false;
    }

    @Override
    public boolean onUnregister(DataSource dataSource) {
        // 1. Configure this sensor to stop delivering data to the platform
        // 2. Discard the reference to the registration request object
        return false;
    }

}




