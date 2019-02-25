package com.alphaae.android.audiotest.base;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.alphaae.android.audiotest.collector.ServiceCollector;

public abstract class BaseService extends Service {

    @Override
    public void onCreate() {
        super.onCreate();
        ServiceCollector.addServier(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ServiceCollector.remove(this);
    }
}
