package com.moko.life;

import android.app.Application;

import com.moko.support.MokoSupport;

public class BaseApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        MokoSupport.getInstance().init(getApplicationContext());
    }
}
