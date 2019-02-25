package com.alphaae.android.audiotest.collector;

import android.app.Service;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class ServiceCollector {

    public static List<Service> serviceList = new ArrayList<>();

    public static void addServier(Service service) {
        serviceList.add(service);
    }

    public static void remove(Service service) {
        serviceList.remove(service);
    }

    public static void stopSelfAll() {
        for (Service service : serviceList) {
            service.stopSelf();
        }
    }

    public static boolean isFinishing(Class mClass) {
        if (serviceList.size() == 0) {
            return true;
        }
        for (Service service : serviceList) {
//            Log.i("isFinishing", "" + service.getClass() + " / " + mClass + " / " + (service.getClass() != mClass));
            if (service.getClass() == mClass)
                return false;
        }
        return true;
    }

}
