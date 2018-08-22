package com.moko.life.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.moko.life.AppConstants;
import com.moko.life.R;
import com.moko.life.base.BaseActivity;
import com.moko.life.entity.MQTTConfig;
import com.moko.life.entity.MokoDevice;
import com.moko.life.utils.SPUtiles;
import com.moko.life.utils.ToastUtils;
import com.moko.support.MokoConstants;
import com.moko.support.MokoSupport;
import com.moko.support.log.LogModule;

import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * @Date 2018/6/7
 * @Author wenzheng.liu
 * @Description
 * @ClassPath com.moko.life.activity.CheckFirmwareUpdateActivity
 */
public class CheckFirmwareUpdateActivity extends BaseActivity {

    public static String TAG = "CheckFirmwareUpdateActivity";
    @Bind(R.id.rb_host_type_ip)
    RadioButton rbHostTypeIp;
    @Bind(R.id.et_host_content)
    EditText etHostContent;
    @Bind(R.id.et_host_port)
    EditText etHostPort;
    @Bind(R.id.et_host_catalogue)
    EditText etHostCatalogue;


    private MokoDevice mokoDevice;
    private MQTTConfig appMqttConfig;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_firmware);
        ButterKnife.bind(this);
        if (getIntent().getExtras() != null) {
            mokoDevice = (MokoDevice) getIntent().getSerializableExtra(AppConstants.EXTRA_KEY_DEVICE);
        }
        // 注册广播接收器
        IntentFilter filter = new IntentFilter();
        filter.addAction(MokoConstants.ACTION_MQTT_CONNECTION);
        filter.addAction(MokoConstants.ACTION_MQTT_RECEIVE);
        filter.addAction(MokoConstants.ACTION_MQTT_SUBSCRIBE);
        filter.addAction(MokoConstants.ACTION_MQTT_UNSUBSCRIBE);
        filter.addAction(AppConstants.ACTION_DEVICE_STATE);
        registerReceiver(mReceiver, filter);
        String mqttConfigAppStr = SPUtiles.getStringValue(CheckFirmwareUpdateActivity.this, AppConstants.SP_KEY_MQTT_CONFIG_APP, "");
        appMqttConfig = new Gson().fromJson(mqttConfigAppStr, MQTTConfig.class);
        rbHostTypeIp.setChecked(true);
    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (MokoConstants.ACTION_MQTT_CONNECTION.equals(action)) {
                int state = intent.getIntExtra(MokoConstants.EXTRA_MQTT_CONNECTION_STATE, 0);
            }
            if (MokoConstants.ACTION_MQTT_RECEIVE.equals(action)) {
                String topic = intent.getStringExtra(MokoConstants.EXTRA_MQTT_RECEIVE_TOPIC);
                if (topic.equals(mokoDevice.getDeviceTopicSwitchState())) {
                    mokoDevice.isOnline = true;
                }
                if (topic.equals(mokoDevice.getDeviceTopicUpgradeState())) {
                    String message = intent.getStringExtra(MokoConstants.EXTRA_MQTT_RECEIVE_MESSAGE);
                    JsonObject object = new JsonParser().parse(message).getAsJsonObject();
                    String ota_result = object.get("ota_result").getAsString();
                    if ("R1".equals(ota_result)) {
                        ToastUtils.showToast(CheckFirmwareUpdateActivity.this, R.string.success);
                    } else if ("R3".equals(ota_result) || "R4".equals(ota_result)) {
                        ToastUtils.showToast(CheckFirmwareUpdateActivity.this, R.string.failed);
                    }
                    try {
                        MokoSupport.getInstance().unSubscribe(mokoDevice.getDeviceTopicUpgradeState());
                    } catch (MqttException e) {
                        e.printStackTrace();
                    }
                }
            }
            if (MokoConstants.ACTION_MQTT_SUBSCRIBE.equals(action)) {
                int state = intent.getIntExtra(MokoConstants.EXTRA_MQTT_STATE, 0);
                if (state == 1) {
                    String topic = intent.getStringExtra(MokoConstants.EXTRA_MQTT_RECEIVE_TOPIC);
                    if (TextUtils.isEmpty(topic)) {
                        return;
                    }
                    if (topic.equals(mokoDevice.getDeviceTopicUpgradeState())) {
//                        json.addProperty("type", 0);
//                        json.addProperty("realm", "23.83.237.116");
//                        json.addProperty("port", 80);
//                        json.addProperty("catalogue", "smartplug/20180817/");
                        JsonObject json = new JsonObject();
                        json.addProperty("type", rbHostTypeIp.isChecked() ? 0 : 1);
                        json.addProperty("realm", etHostContent.getText().toString());
                        json.addProperty("port", Integer.parseInt(etHostPort.getText().toString()));
                        json.addProperty("catalogue", etHostCatalogue.getText().toString());
                        MqttMessage message = new MqttMessage();
                        message.setPayload(json.toString().getBytes());
                        message.setQos(appMqttConfig.qos);
                        try {
                            MokoSupport.getInstance().publish(mokoDevice.getAppTopicUpgrade(), message);
                        } catch (MqttException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
            if (MokoConstants.ACTION_MQTT_UNSUBSCRIBE.equals(action)) {
                int state = intent.getIntExtra(MokoConstants.EXTRA_MQTT_STATE, 0);
                dismissLoadingProgressDialog();
            }
            if (AppConstants.ACTION_DEVICE_STATE.equals(action)) {
                mokoDevice.isOnline = false;
            }
        }
    };

    public void back(View view) {
        finish();
    }

    public void startUpdate(View view) {
        if (!MokoSupport.getInstance().isConnected()) {
            ToastUtils.showToast(this, R.string.network_error);
            return;
        }
        if (!mokoDevice.isOnline) {
            ToastUtils.showToast(this, R.string.device_offline);
            return;
        }
        String hostStr = etHostContent.getText().toString();
        String portStr = etHostPort.getText().toString();
        String catalogueStr = etHostCatalogue.getText().toString();
        if (TextUtils.isEmpty(hostStr)) {
            ToastUtils.showToast(this, R.string.mqtt_verify_host);
            return;
        }
        if (rbHostTypeIp.isChecked()) {
            Pattern pattern = Pattern.compile("((2[0-4]\\d|25[0-5]|[01]?\\d\\d?)\\.){3}(2[0-4]\\d|25[0-5]|[01]?\\d\\d?)");
            Matcher matcher = pattern.matcher(hostStr);
            if (!matcher.matches()) {
                ToastUtils.showToast(this, R.string.mqtt_verify_host);
                return;
            }
        } else {
            Pattern pattern = Pattern.compile("[a-zA-z]+://[^\\\\s]*");
            Matcher matcher = pattern.matcher(hostStr);
            if (!matcher.matches()) {
                ToastUtils.showToast(this, R.string.mqtt_verify_host);
                return;
            }
        }
        if (!TextUtils.isEmpty(portStr) && Integer.parseInt(portStr) > 65535) {
            ToastUtils.showToast(this, R.string.mqtt_verify_port_empty);
            return;
        }
        if (TextUtils.isEmpty(catalogueStr)) {
            ToastUtils.showToast(this, R.string.mqtt_verify_catalogue);
            return;
        }
        LogModule.i("升级固件");
        showLoadingProgressDialog(getString(R.string.wait));
        try {
            MokoSupport.getInstance().subscribe(mokoDevice.getDeviceTopicUpgradeState(), appMqttConfig.qos);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
    }
}
