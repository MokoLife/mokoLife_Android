package com.moko.life;

public class AppConstants {
    // data time pattern
    public static final String PATTERN_HH_MM = "HH:mm";
    public static final String PATTERN_YYYY_MM_DD = "yyyy-MM-dd";
    public static final String PATTERN_MM_DD = "MM/dd";
    public static final String PATTERN_MM_DD_2 = "MM-dd";
    public static final String PATTERN_YYYY_MM_DD_HH_MM = "yyyy-MM-dd HH:mm";
    // action
    public static final String ACTION_FINISH_ACTIVITY = "com.moko.life.action.finishActivity";
    // sp
    public static final String SP_NAME = "sp_name_life";
    public static final String SP_KEY_MQTT_CONFIG = "SP_KEY_MQTT_CONFIG";
    public static final String SP_KEY_MQTT_CONFIG_APP = "SP_KEY_MQTT_CONFIG_APP";


    public static final String SP_KEY_DEVICE_ADDRESS = "sp_key_device_address";
    // extra_key
    // 设备列表
    public static final String EXTRA_KEY_RESPONSE_ORDER_TYPE = "EXTRA_KEY_RESPONSE_ORDER_TYPE";
    public static final String EXTRA_KEY_RESPONSE_VALUE = "EXTRA_KEY_RESPONSE_VALUE";
    public static final String EXTRA_KEY_DEVICE_CONFIG = "EXTRA_KEY_DEVICE_CONFIG";
    public static final String EXTRA_KEY_TEMP_TARGET = "EXTRA_KEY_TEMP_TARGET";
    public static final String EXTRA_KEY_TEMP_HOUR = "EXTRA_KEY_TEMP_HOUR";
    public static final String EXTRA_KEY_TEMP_MINUTE = "EXTRA_KEY_TEMP_MINUTE";
    public static final String EXTRA_KEY_FROM_ACTIVITY = "EXTRA_KEY_FROM_ACTIVITY";
    public static final String EXTRA_KEY_DEVICE= "EXTRA_KEY_DEVICE";
    // request_code
    public static final int REQUEST_CODE_TEMP_TARGET = 100;
    public static final int REQUEST_CODE_TIMER = 101;
    public static final int REQUEST_CODE_DELAY = 102;
    public static final int REQUEST_CODE_SELECT_FIRMWARE = 103;

    public static final int REQUEST_CODE_WIFI_SETTING = 0x10;
    public static final int REQUEST_CODE_OPERATION_STEP = 0x11;
    public static final int REQUEST_CODE_PERMISSION = 120;

    public static final int PERMISSION_REQUEST_CODE = 1;
    // result_code
    public static final int RESULT_CONN_DISCONNECTED = 2;
}
