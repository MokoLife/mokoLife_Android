package com.moko.life.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.moko.life.AppConstants;
import com.moko.life.entity.MQTTConfig;
import com.moko.life.utils.SPUtiles;
import com.moko.support.MokoSupport;
import com.moko.support.log.LogModule;

import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;


/**
 * @Date 2017/12/7 0007
 * @Author wenzheng.liu
 * @Description
 * @ClassPath com.moko.life.service.MokoService
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
        String mqttAppConfigStr = SPUtiles.getStringValue(this, AppConstants.SP_KEY_MQTT_CONFIG_APP, "");
        if (!TextUtils.isEmpty(mqttAppConfigStr)) {
            MQTTConfig mqttConfig = new Gson().fromJson(mqttAppConfigStr, MQTTConfig.class);
            if (!mqttConfig.isEmpty()) {
                MokoSupport.getInstance().creatClient(mqttConfig.host, mqttConfig.port, mqttConfig.clientId, mqttConfig.connectMode == 1);
                MqttConnectOptions connOpts = new MqttConnectOptions();
                connOpts.setAutomaticReconnect(true);
                connOpts.setCleanSession(mqttConfig.cleanSession);
                connOpts.setKeepAliveInterval(mqttConfig.keepAlive);
                connOpts.setUserName(mqttConfig.username);
                connOpts.setPassword(mqttConfig.password.toCharArray());
                try {
                    MokoSupport.getInstance().connectMqtt(connOpts);
                } catch (MqttException e) {
                    e.printStackTrace();
                }
                return;
            }
        }
    }

    @Override
    public void onDestroy() {
        LogModule.i("关闭后台服务");
        super.onDestroy();
        try {
            MokoSupport.getInstance().disconnectMqtt();
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }
}
