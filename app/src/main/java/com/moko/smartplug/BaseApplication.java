package com.moko.smartplug;

import android.app.Application;
import android.content.Intent;

import com.moko.smartplug.service.MokoService;
import com.moko.support.MokoSupport;

public class BaseApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        MokoSupport.getInstance().init(getApplicationContext());
        // 启动服务
        startService(new Intent(this, MokoService.class));
    }
}
