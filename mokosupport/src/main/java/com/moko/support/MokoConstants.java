package com.moko.support;

public class MokoConstants {
    // header
    public static final int HEADER_GET_DEVICE_INFO = 4001;
    public static final int HEADER_SET_MQTT_INFO = 4002;
    public static final int HEADER_SET_WIFI_INFO = 4003;
    // response
    public static final int RESPONSE_SUCCESS = 0;
    public static final int RESPONSE_FAILED_LENGTH = 1;
    public static final int RESPONSE_FAILED_DATA_FORMAT = 2;
    public static final int RESPONSE_FAILED_MQTT_WIFI = 3;
    // conn status
    public static final int CONN_STATUS_SUCCESS = 0;
    public static final int CONN_STATUS_CONNECTING = 1;
    public static final int CONN_STATUS_FAILED = 2;
    public static final int CONN_STATUS_TIMEOUT = 3;
    // action
    public static final String ACTION_CONNECT_STATUS = "ACTION_CONNECT_STATUS";
    public static final String ACTION_RESPONSE = "ACTION_RESPONSE";
    // extra
    public static final String EXTRA_CONNECT_STATUS = "EXTRA_CONNECT_STATUS";
    public static final String EXTRA_RESPONSE_INFO = "EXTRA_RESPONSE_INFO";

}
