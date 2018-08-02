package com.moko.life.entity;


import android.content.Context;
import android.text.TextUtils;

import com.moko.life.R;
import com.moko.life.utils.ToastUtils;

import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MQTTConfig implements Serializable {
    public String host = "45.77.124.18";
    public String port = "1883";
    public boolean cleanSession = true;
    public int connectMode;
    public int qos = 2;
    public int keepAlive = 60;
    public String clientId = "";
    public String username = "admin";
    public String password = "public";

    public boolean isError(Context context) {
        if (context == null) {
            return isHostError()
                    || TextUtils.isEmpty(port)
                    || keepAlive == 0
                    || TextUtils.isEmpty(username)
                    || TextUtils.isEmpty(password);
        } else {
            if (isHostError()) {
                ToastUtils.showToast(context, context.getString(R.string.mqtt_verify_host));
                return true;
            }
            if (TextUtils.isEmpty(port)) {
                ToastUtils.showToast(context, context.getString(R.string.mqtt_verify_port_empty));
                return true;
            }
            if (Integer.parseInt(port) > 65535) {
                ToastUtils.showToast(context, context.getString(R.string.mqtt_verify_port));
                return true;
            }
            if (keepAlive < 60 || keepAlive > 120) {
                ToastUtils.showToast(context, context.getString(R.string.mqtt_verify_keep_alive));
                return true;
            }
            if (TextUtils.isEmpty(username)) {
                ToastUtils.showToast(context, context.getString(R.string.mqtt_verify_username));
                return true;
            }
            if (TextUtils.isEmpty(password)) {
                ToastUtils.showToast(context, context.getString(R.string.mqtt_verify_password));
                return true;
            }
        }
        return false;
    }

    private boolean isHostError() {
        if (TextUtils.isEmpty(host)) {
            return true;
        } else {
            Pattern pattern = Pattern.compile("((2[0-4]\\d|25[0-5]|[01]?\\d\\d?)\\.){3}(2[0-4]\\d|25[0-5]|[01]?\\d\\d?)");
            Matcher matcher = pattern.matcher(host);
            Pattern pattern2 = Pattern.compile("[a-zA-z]+://[^\\\\s]*");
            Matcher matcher2 = pattern2.matcher(host);
            if (!matcher.matches() && !matcher2.matches()) {
                return true;
            }
        }
        return false;
    }

    public void reset() {
        host = "";
        port = "1883";
        cleanSession = true;
        connectMode = 0;
        qos = 2;
        keepAlive = 60;
        clientId = "";
        username = "";
        password = "";
    }
}
