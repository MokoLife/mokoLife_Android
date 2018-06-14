package com.moko.life.entity;


import android.text.TextUtils;

import java.io.Serializable;

public class MQTTConfig implements Serializable {
    public String host = "";
    public String port = "";
    public boolean cleanSession = true;
    public int connectMode;
    public int qos = 2;
    public int keepAlive = 60;
    public String clientId = "";
    public String username = "";
    public String password = "";

    public boolean isEmpty() {
        return TextUtils.isEmpty(host)
                || TextUtils.isEmpty(port)
                || keepAlive == 0
                || TextUtils.isEmpty(username)
                || TextUtils.isEmpty(password);
    }

    public void reset() {
        host = "";
        port = "";
        cleanSession = true;
        connectMode = 0;
        qos = 2;
        keepAlive = 60;
        clientId = "";
        username = "";
        password = "";
    }
}
