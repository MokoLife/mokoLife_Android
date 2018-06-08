package com.moko.life.entity;


import android.text.TextUtils;

import java.io.Serializable;

public class MQTTConfig implements Serializable {
    public String host = "";
    public String port = "";
    public boolean cleanSession;
    public int connectMode;
    public int qos;
    public int keepAlive = 60;
    public String clientId = "";
    public String username = "";
    public String password = "";

    public boolean isEmpty() {
        return TextUtils.isEmpty(host)
                || TextUtils.isEmpty(port)
                || keepAlive == 0
                || TextUtils.isEmpty(clientId)
                || TextUtils.isEmpty(username)
                || TextUtils.isEmpty(password);
    }

    public void reset() {
        host = "";
        port = "";
        cleanSession = false;
        connectMode = 0;
        qos = 0;
        keepAlive = 60;
        clientId = "";
        username = "";
        password = "";
    }
}
