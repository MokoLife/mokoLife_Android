package com.moko.life.entity;


import android.text.TextUtils;

import java.io.Serializable;
import java.util.ArrayList;

public class MokoDevice implements Serializable {
    public static final String DEVICE_TOPIC_SWITCH_STATE = "device/switch_state";
    public static final String DEVICE_TOPIC_FIRMWARE_INFO = "device/firmware_infor";
    public static final String DEVICE_TOPIC_DELAY_TIME = "device/delay_time";
    public static final String DEVICE_TOPIC_OTA_UPGRADE_STATE = "device/ota_upgrade_state";
    public static final String DEVICE_TOPIC_DELETE_DEVICE = "device/delete_device";
    public static final String DEVICE_TOPIC_ELECTRICITY_INFORMATION = "device/electricity_information";

    public static final String APP_TOPIC_SWITCH_STATE = "app/switch_state";
    public static final String APP_TOPIC_DELAY_TIME = "app/delay_time";
    public static final String APP_TOPIC_DELAY_TIME_1 = "app/delay_time_01";
    public static final String APP_TOPIC_DELAY_TIME_2 = "app/delay_time_02";
    public static final String APP_TOPIC_DELAY_TIME_3 = "app/delay_time_03";
    public static final String APP_TOPIC_RESET = "app/reset";
    public static final String APP_TOPIC_UPGRADE = "app/upgrade";
    public static final String APP_TOPIC_READ_FIRMWARE_INFOR = "app/read_firmware_infor";

    public int id;
    public String name;
    public String nickName;
    public String function;
    public String specifications;
    public String mac;
    public String type;
    public boolean on_off;
    public String topicPre;
    public String company_name;
    public String production_date;
    public String product_model;
    public String firmware_version;
    public boolean on_off_1;
    public boolean on_off_2;
    public boolean on_off_3;
    public boolean isOnline;

    public ArrayList<String> subscribeTopics;
    public Runnable deviceStateRunnable;

    public String getTopicPre() {
        if (TextUtils.isEmpty(topicPre)) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(this.function);
            stringBuilder.append("/");
            stringBuilder.append(this.name);
            stringBuilder.append("/");
            stringBuilder.append(this.specifications);
            stringBuilder.append("/");
            stringBuilder.append(this.mac);
            stringBuilder.append("/");
            topicPre = stringBuilder.toString();
        }
        return topicPre;
    }

    public ArrayList<String> getDeviceTopics() {
        if (subscribeTopics == null) {
            if ("iot_wall_switch".equals(function)) {
                subscribeTopics = new ArrayList<>();
                subscribeTopics.add(getDeviceTopicSwitchState());
                subscribeTopics.add(getDeviceTopicDelayTime());
                subscribeTopics.add(getDeviceTopicDeleteDevice());
            } else if ("iot_plug".equals(function)) {
                subscribeTopics = new ArrayList<>();
                subscribeTopics.add(getDeviceTopicSwitchState());
                subscribeTopics.add(getDeviceTopicDelayTime());
                subscribeTopics.add(getDeviceTopicDeleteDevice());
                subscribeTopics.add(getDeviceTopicElectricityInformation());
            }
        }
        return subscribeTopics;
    }

    public String getDeviceTopicSwitchState() {
        return getTopicPre() + DEVICE_TOPIC_SWITCH_STATE;
    }

    public String getDeviceTopicFirmwareInfo() {
        return getTopicPre() + DEVICE_TOPIC_FIRMWARE_INFO;
    }

    public String getDeviceTopicDelayTime() {
        return getTopicPre() + DEVICE_TOPIC_DELAY_TIME;
    }

    public String getDeviceTopicUpgradeState() {
        return getTopicPre() + DEVICE_TOPIC_OTA_UPGRADE_STATE;
    }

    public String getDeviceTopicDeleteDevice() {
        return getTopicPre() + DEVICE_TOPIC_DELETE_DEVICE;
    }

    public String getDeviceTopicElectricityInformation() {
        return getTopicPre() + DEVICE_TOPIC_ELECTRICITY_INFORMATION;
    }

    public String getAppTopicSwitchState() {
        return getTopicPre() + APP_TOPIC_SWITCH_STATE;
    }

    public String getAppTopicDelayTime() {
        return getTopicPre() + APP_TOPIC_DELAY_TIME;
    }

    public String getAppTopicDelayTime1() {
        return getTopicPre() + APP_TOPIC_DELAY_TIME_1;
    }

    public String getAppTopicDelayTime2() {
        return getTopicPre() + APP_TOPIC_DELAY_TIME_2;
    }

    public String getAppTopicDelayTime3() {
        return getTopicPre() + APP_TOPIC_DELAY_TIME_3;
    }

    public String getAppTopicReset() {
        return getTopicPre() + APP_TOPIC_RESET;
    }

    public String getAppTopicUpgrade() {
        return getTopicPre() + APP_TOPIC_UPGRADE;
    }

    public String getAppTopicReadFirmwareInfor() {
        return getTopicPre() + APP_TOPIC_READ_FIRMWARE_INFOR;
    }
}
