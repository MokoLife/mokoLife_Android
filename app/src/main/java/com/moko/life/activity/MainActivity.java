package com.moko.life.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.moko.life.AppConstants;
import com.moko.life.R;
import com.moko.life.adapter.DeviceAdapter;
import com.moko.life.base.BaseActivity;
import com.moko.life.db.DBTools;
import com.moko.life.entity.MQTTConfig;
import com.moko.life.entity.MokoDevice;
import com.moko.life.service.MokoService;
import com.moko.life.utils.SPUtiles;
import com.moko.support.MokoConstants;
import com.moko.support.MokoSupport;
import com.moko.support.log.LogModule;

import org.eclipse.paho.client.mqttv3.MqttException;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * @Date 2018/6/7
 * @Author wenzheng.liu
 * @Description 设备列表
 * @ClassPath com.moko.life.activity.MainActivity
 */
public class MainActivity extends BaseActivity implements DeviceAdapter.AdapterClickListener {

    @Bind(R.id.rl_empty)
    RelativeLayout rlEmpty;
    @Bind(R.id.lv_device_list)
    ListView lvDeviceList;
    @Bind(R.id.tv_title)
    TextView tvTitle;
    private ArrayList<MokoDevice> devices;
    private DeviceAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        devices = DBTools.getInstance(this).selectAllDevice();
        adapter = new DeviceAdapter(this);
        adapter.setListener(this);
        adapter.setItems(devices);
        lvDeviceList.setAdapter(adapter);
        if (devices.isEmpty()) {
            rlEmpty.setVisibility(View.VISIBLE);
            lvDeviceList.setVisibility(View.GONE);
        } else {
            lvDeviceList.setVisibility(View.VISIBLE);
            rlEmpty.setVisibility(View.GONE);
        }
        startService(new Intent(this, MokoService.class));
        // 注册广播接收器
        IntentFilter filter = new IntentFilter();
        filter.addAction(MokoConstants.ACTION_MQTT_CONNECTION);
        filter.addAction(MokoConstants.ACTION_MQTT_RECEIVE);
        registerReceiver(mReceiver, filter);
    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (MokoConstants.ACTION_MQTT_CONNECTION.equals(action)) {
                int state = intent.getIntExtra(MokoConstants.EXTRA_MQTT_CONNECTION_STATE, 0);
                tvTitle.setText(state == 1 ? R.string.mqtt_connected : R.string.mqtt_connecting);
                if (state == 1) {
                    if (devices.isEmpty()) {
                        return;
                    }
                    for (MokoDevice device : devices) {
                        String topicPre = device.function
                                + "/" + device.name
                                + "/" + device.specifications
                                + "/" + device.mac
                                + "/" + "device"
                                + "/";
                        // 订阅设备主题
                        String topicSwitchState = topicPre + "switch_state";
                        String topicFirmwareInfo = topicPre + "firmware_infor";
                        String topicDelayTime = topicPre + "delay_time";
                        String topicOTAUpgradeState = topicPre + "ota_upgrade_state";
                        String topicDeleteDevice = topicPre + "delete_device";
                        String topicElectricityInfo = topicPre + "electricity_information";
                        String mqttConfigAppStr = SPUtiles.getStringValue(MainActivity.this, AppConstants.SP_KEY_MQTT_CONFIG_APP, "");
                        MQTTConfig appMqttConfig = new Gson().fromJson(mqttConfigAppStr, MQTTConfig.class);
                        // 订阅
                        try {
                            MokoSupport.getInstance().subscribe(topicSwitchState, appMqttConfig.qos);
                            MokoSupport.getInstance().subscribe(topicFirmwareInfo, appMqttConfig.qos);
                            MokoSupport.getInstance().subscribe(topicDelayTime, appMqttConfig.qos);
                            MokoSupport.getInstance().subscribe(topicOTAUpgradeState, appMqttConfig.qos);
                            MokoSupport.getInstance().subscribe(topicDeleteDevice, appMqttConfig.qos);
                            MokoSupport.getInstance().subscribe(topicElectricityInfo, appMqttConfig.qos);
                        } catch (MqttException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
            if (MokoConstants.ACTION_MQTT_RECEIVE.equals(action)) {
                String topic = intent.getStringExtra(MokoConstants.EXTRA_MQTT_RECEIVE_TOPIC);
                String mac = topic.split("/")[3];
                String type = topic.split("/")[5];
                if ("switch_state".equals(type)) {
                    if (devices.isEmpty()) {
                        return;
                    }
                    String message = intent.getStringExtra(MokoConstants.EXTRA_MQTT_RECEIVE_MESSAGE);
                    JsonObject object = new JsonParser().parse(message).getAsJsonObject();
                    String switch_state = object.get("switch_state").getAsString();
                    for (MokoDevice device : devices) {
                        if (device.mac.equals(mac)) {
                            if (!switch_state.equals(device.on_off ? "on" : "off")) {
                                device.on_off = !device.on_off;
                                adapter.notifyDataSetChanged();
                            }
                            break;
                        }

                    }
                }
            }
        }
    };


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        LogModule.i("onNewIntent...");
        setIntent(intent);
        if (getIntent().getExtras() != null) {
            String from = getIntent().getStringExtra(AppConstants.EXTRA_KEY_FROM_ACTIVITY);
            if (ModifyNameActivity.TAG.equals(from)) {
                devices.clear();
                devices.addAll(DBTools.getInstance(this).selectAllDevice());
                adapter.notifyDataSetChanged();
                if (!devices.isEmpty()) {
                    lvDeviceList.setVisibility(View.VISIBLE);
                    rlEmpty.setVisibility(View.GONE);
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopService(new Intent(this, MokoService.class));
        unregisterReceiver(mReceiver);
    }

    public void mainSettings(View view) {
        startActivity(new Intent(this, SettingsActivity.class));
    }

    public void mainAddDevices(View view) {
        String mqttAppConfigStr = SPUtiles.getStringValue(this, AppConstants.SP_KEY_MQTT_CONFIG_APP, "");
        String mqttDeviceConfigStr = SPUtiles.getStringValue(this, AppConstants.SP_KEY_MQTT_CONFIG, "");
        if (TextUtils.isEmpty(mqttDeviceConfigStr)) {
            startActivity(new Intent(this, SetDeviceMqttActivity.class));
            return;
        }
        if (TextUtils.isEmpty(mqttAppConfigStr)) {
            startActivity(new Intent(this, SetAppMqttActivity.class));
            return;
        }
        MQTTConfig mqttConfig = new Gson().fromJson(mqttAppConfigStr, MQTTConfig.class);
        if (TextUtils.isEmpty(mqttConfig.host)) {
            startActivity(new Intent(this, SetAppMqttActivity.class));
            return;
        }
        startActivity(new Intent(this, SelectDeviceTypeActivity.class));
    }

    @Override
    public void deviceDetailClick() {
        LogModule.i("跳转详情");
    }

    @Override
    public void deviceSwitchClick() {
        LogModule.i("切换开关");
    }

    @Override
    public void deviceDelete(MokoDevice device) {
        LogModule.i("长按删除");
        DBTools.getInstance(this).deleteDevice(device);
        devices.remove(device);
        if (devices.isEmpty()) {
            rlEmpty.setVisibility(View.VISIBLE);
            lvDeviceList.setVisibility(View.GONE);
        }
    }
}
