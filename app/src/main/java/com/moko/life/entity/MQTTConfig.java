package com.moko.life.entity;


import android.text.TextUtils;

import java.io.Serializable;

public class MQTTConfig implements Serializable {
    public String host = "http://45.77.124.18";
    public String port = "1883";
    public boolean cleanSession = true;
    public int connectMode;
    public int qos = 2;
    public int keepAlive = 60;
    public String clientId = "";
    public String username = "admin";
    public String password = "public";

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
