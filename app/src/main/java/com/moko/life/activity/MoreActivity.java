package com.moko.life.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.moko.life.AppConstants;
import com.moko.life.R;
import com.moko.life.base.BaseActivity;
import com.moko.life.db.DBTools;
import com.moko.life.dialog.CustomDialog;
import com.moko.life.dialog.RemoveDialog;
import com.moko.life.dialog.ResetDialog;
import com.moko.life.entity.MQTTConfig;
import com.moko.life.entity.MokoDevice;
import com.moko.life.utils.SPUtiles;
import com.moko.life.utils.ToastUtils;
import com.moko.support.MokoConstants;
import com.moko.support.MokoSupport;
import com.moko.support.log.LogModule;

import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * @Date 2018/6/7
 * @Author wenzheng.liu
 * @Description
 * @ClassPath com.moko.life.activity.MoreActivity
 */
public class MoreActivity extends BaseActivity {

    public static String TAG = "MoreActivity";

    @Bind(R.id.tv_device_name)
    TextView tvDeviceName;
    private MokoDevice mokoDevice;
    private String currentTopic;
    private MQTTConfig appMqttConfig;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more);
        ButterKnife.bind(this);
        if (getIntent().getExtras() != null) {
            mokoDevice = (MokoDevice) getIntent().getSerializableExtra(AppConstants.EXTRA_KEY_DEVICE);
            tvDeviceName.setText(mokoDevice.nickName);
        }
        // 注册广播接收器
        IntentFilter filter = new IntentFilter();
        filter.addAction(MokoConstants.ACTION_MQTT_CONNECTION);
        filter.addAction(MokoConstants.ACTION_MQTT_RECEIVE);
        filter.addAction(MokoConstants.ACTION_MQTT_SUBSCRIBE);
        filter.addAction(MokoConstants.ACTION_MQTT_UNSUBSCRIBE);
        filter.addAction(MokoConstants.ACTION_MQTT_PUBLISH);
        filter.addAction(AppConstants.ACTION_DEVICE_STATE);
        registerReceiver(mReceiver, filter);
        String mqttConfigAppStr = SPUtiles.getStringValue(MoreActivity.this, AppConstants.SP_KEY_MQTT_CONFIG_APP, "");
        appMqttConfig = new Gson().fromJson(mqttConfigAppStr, MQTTConfig.class);
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
                if (topic.equals(mokoDevice.getDeviceTopicFirmwareInfo())) {
                    try {
                        MokoSupport.getInstance().unSubscribe(mokoDevice.getDeviceTopicFirmwareInfo());
                    } catch (MqttException e) {
                        e.printStackTrace();
                    }
                    String message = intent.getStringExtra(MokoConstants.EXTRA_MQTT_RECEIVE_MESSAGE);
                    JsonObject object = new JsonParser().parse(message).getAsJsonObject();
                    String company_name = object.get("company_name").getAsString();
                    String production_date = object.get("production_date").getAsString();
                    String product_model = object.get("product_model").getAsString();
                    String firmware_version = object.get("firmware_version").getAsString();
                    String firmwar_mac = "";
                    if (object.get("device_mac") != null) {
                        firmwar_mac = object.get("device_mac").getAsString();
                    }
                    mokoDevice.company_name = company_name;
                    mokoDevice.production_date = production_date;
                    mokoDevice.product_model = product_model;
                    mokoDevice.firmware_version = firmware_version;
                    mokoDevice.mac = firmwar_mac;
                    Intent i = new Intent(MoreActivity.this, DeviceInfoActivity.class);
                    i.putExtra(AppConstants.EXTRA_KEY_DEVICE, mokoDevice);
                    startActivity(i);
                }
                if (topic.equals(mokoDevice.getDeviceTopicSwitchState())) {
                    mokoDevice.isOnline = true;
                }
            }
            if (MokoConstants.ACTION_MQTT_PUBLISH.equals(action)) {
                int state = intent.getIntExtra(MokoConstants.EXTRA_MQTT_STATE, 0);
                if (state == MokoConstants.MQTT_STATE_SUCCESS) {
                    if (!TextUtils.isEmpty(currentTopic) && currentTopic.equals(mokoDevice.getAppTopicReset())) {
                        LogModule.i("重置设备成功");
                        // 取消订阅
                        for (String deviceTopic : mokoDevice.getDeviceTopics()) {
                            try {
                                MokoSupport.getInstance().unSubscribe(deviceTopic);
                            } catch (MqttException e) {
                                e.printStackTrace();
                            }
                        }
                        DBTools.getInstance(MoreActivity.this).deleteDevice(mokoDevice);
                        tvDeviceName.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                dismissLoadingProgressDialog();
                                // 跳转首页，刷新数据
                                Intent intent = new Intent(MoreActivity.this, MainActivity.class);
                                intent.putExtra(AppConstants.EXTRA_KEY_FROM_ACTIVITY, TAG);
                                startActivity(intent);
                            }
                        }, 500);
                        dismissLoadingProgressDialog();
                    }
                    if (!TextUtils.isEmpty(currentTopic) && currentTopic.equals(mokoDevice.getAppTopicReadFirmwareInfor())) {
                        dismissLoadingProgressDialog();
                    }
                }
            }
            if (MokoConstants.ACTION_MQTT_SUBSCRIBE.equals(action)) {
                int state = intent.getIntExtra(MokoConstants.EXTRA_MQTT_STATE, 0);
                if (state == MokoConstants.MQTT_STATE_SUCCESS) {
                    String topic = intent.getStringExtra(MokoConstants.EXTRA_MQTT_RECEIVE_TOPIC);
                    if (TextUtils.isEmpty(topic)) {
                        return;
                    }
                    if (topic.equals(mokoDevice.getDeviceTopicFirmwareInfo())) {
                        MqttMessage message = new MqttMessage();
                        message.setPayload("".getBytes());
                        message.setQos(appMqttConfig.qos);
                        currentTopic = mokoDevice.getAppTopicReadFirmwareInfor();
                        try {
                            MokoSupport.getInstance().publish(mokoDevice.getAppTopicReadFirmwareInfor(), message);
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
                String topic = intent.getStringExtra(MokoConstants.EXTRA_MQTT_RECEIVE_TOPIC);
                if (topic.equals(mokoDevice.getDeviceTopicSwitchState())) {
                    mokoDevice.isOnline = false;
                }
            }
        }
    };

    public void back(View view) {
        finish();
    }

    public void modifyName(View view) {
        View content = LayoutInflater.from(this).inflate(R.layout.modify_name, null);
        final EditText etDeviceName = ButterKnife.findById(content, R.id.et_device_name);
        etDeviceName.setText(mokoDevice.nickName);
        etDeviceName.setSelection(mokoDevice.nickName.length());
        CustomDialog dialog = new CustomDialog.Builder(this)
                .setContentView(content)
                .setPositiveButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setNegativeButton(R.string.save, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String nickName = etDeviceName.getText().toString();
                        if (TextUtils.isEmpty(nickName)) {
                            ToastUtils.showToast(MoreActivity.this, R.string.more_modify_name_tips);
                            return;
                        }
                        mokoDevice.nickName = nickName;
                        DBTools.getInstance(MoreActivity.this).updateDevice(mokoDevice);
                        Intent intent = new Intent(AppConstants.ACTION_MODIFY_NAME);
                        MoreActivity.this.sendBroadcast(intent);
                        tvDeviceName.setText(nickName);
                        dialog.dismiss();
                    }
                })
                .create();
        dialog.show();
    }

    public void deviceInfo(View view) {
        if (!MokoSupport.getInstance().isConnected()) {
            ToastUtils.showToast(this, R.string.network_error);
            return;
        }
        if (!mokoDevice.isOnline) {
            ToastUtils.showToast(this, R.string.device_offline);
            return;
        }
        showLoadingProgressDialog(getString(R.string.wait));
        LogModule.i("读取设备信息");
        try {
            MokoSupport.getInstance().subscribe(mokoDevice.getDeviceTopicFirmwareInfo(), appMqttConfig.qos);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public void checkNewFirmware(View view) {
        if (!MokoSupport.getInstance().isConnected()) {
            ToastUtils.showToast(this, R.string.network_error);
            return;
        }
        if (!mokoDevice.isOnline) {
            ToastUtils.showToast(this, R.string.device_offline);
            return;
        }
        Intent intent = new Intent(this, CheckFirmwareUpdateActivity.class);
        intent.putExtra(AppConstants.EXTRA_KEY_DEVICE, mokoDevice);
        startActivity(intent);
    }

    public void removeDevice(View view) {
        RemoveDialog dialog = new RemoveDialog(this);
        dialog.setListener(new RemoveDialog.RemoveListener() {
            @Override
            public void onConfirmClick(RemoveDialog dialog) {
                if (!MokoSupport.getInstance().isConnected()) {
                    ToastUtils.showToast(MoreActivity.this, R.string.network_error);
                    return;
                }
                showLoadingProgressDialog(getString(R.string.wait));
                // 取消订阅
                for (String topic : mokoDevice.getDeviceTopics()) {
                    try {
                        MokoSupport.getInstance().unSubscribe(topic);
                    } catch (MqttException e) {
                        e.printStackTrace();
                    }
                }
                LogModule.i("删除设备");
                DBTools.getInstance(MoreActivity.this).deleteDevice(mokoDevice);
                tvDeviceName.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        dismissLoadingProgressDialog();
                        // 跳转首页，刷新数据
                        Intent intent = new Intent(MoreActivity.this, MainActivity.class);
                        intent.putExtra(AppConstants.EXTRA_KEY_FROM_ACTIVITY, TAG);
                        startActivity(intent);
                    }
                }, 500);
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public void resetDevice(View view) {
        ResetDialog dialog = new ResetDialog(this);
        dialog.setListener(new ResetDialog.ResetListener() {
            @Override
            public void onConfirmClick(ResetDialog dialog) {
                if (!MokoSupport.getInstance().isConnected()) {
                    ToastUtils.showToast(MoreActivity.this, R.string.network_error);
                    return;
                }
                if (!mokoDevice.isOnline) {
                    ToastUtils.showToast(MoreActivity.this, R.string.device_offline);
                    return;
                }
                showLoadingProgressDialog(getString(R.string.wait));
                LogModule.i("重置设备");

                MqttMessage message = new MqttMessage();
                message.setQos(appMqttConfig.qos);
                currentTopic = mokoDevice.getAppTopicReset();
                try {
                    MokoSupport.getInstance().publish(mokoDevice.getAppTopicReset(), message);
                } catch (MqttException e) {
                    e.printStackTrace();
                }
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
    }

    public void about(View view) {
        startActivity(new Intent(this, AboutActivity.class));
    }
}
