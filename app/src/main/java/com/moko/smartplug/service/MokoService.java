package com.moko.smartplug.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.Message;

import com.moko.support.handler.BaseMessageHandler;
import com.moko.support.log.LogModule;


/**
 * @Date 2017/12/7 0007
 * @Author wenzheng.liu
 * @Description
 * @ClassPath com.moko.smartplug.service.MokoService
 */
public class MokoService extends Service {
    private IBinder mBinder = new LocalBinder();

    public class LocalBinder extends Binder {
        public MokoService getService() {
            return MokoService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        LogModule.i("启动后台服务");
        mHandler = new ServiceHandler(this);

    }

    @Override
    public void onDestroy() {
        LogModule.i("关闭后台服务");
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    public ServiceHandler mHandler;

    public class ServiceHandler extends BaseMessageHandler<MokoService> {

        public ServiceHandler(MokoService service) {
            super(service);
        }

        @Override
        protected void handleMessage(MokoService service, Message msg) {
        }
    }
}
