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
import org.eclipse.paho.client.mqttv3.MqttMessage;

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
        filter.addAction(AppConstants.ACTION_MODIFY_NAME);
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

                        String mqttConfigAppStr = SPUtiles.getStringValue(MainActivity.this, AppConstants.SP_KEY_MQTT_CONFIG_APP, "");
                        MQTTConfig appMqttConfig = new Gson().fromJson(mqttConfigAppStr, MQTTConfig.class);
                        // 订阅
                        for (String topic : device.getDeviceTopics()) {
                            try {
                                MokoSupport.getInstance().subscribe(topic, appMqttConfig.qos);
                            } catch (MqttException e) {
                                e.printStackTrace();
                            }

                        }
                    }
                }
            }
            if (MokoConstants.ACTION_MQTT_RECEIVE.equals(action)) {
                String topic = intent.getStringExtra(MokoConstants.EXTRA_MQTT_RECEIVE_TOPIC);
                if (devices.isEmpty()) {
                    return;
                }
                if (topic.contains(MokoDevice.DEVICE_TOPIC_SWITCH_STATE)) {
                    String message = intent.getStringExtra(MokoConstants.EXTRA_MQTT_RECEIVE_MESSAGE);
                    JsonObject object = new JsonParser().parse(message).getAsJsonObject();
                    String switch_state = object.get("switch_state").getAsString();
                    for (MokoDevice device : devices) {
                        if (device.getDeviceTopicSwitchState().equals(topic)) {
                            if (!switch_state.equals(device.on_off ? "on" : "off")) {
                                device.on_off = !device.on_off;
                                adapter.notifyDataSetChanged();
                            }
                            dismissLoadingProgressDialog();
                            break;
                        }
                    }
                }
            }
            if (AppConstants.ACTION_MODIFY_NAME.equals(action)) {
                devices.clear();
                devices.addAll(DBTools.getInstance(MainActivity.this).selectAllDevice());
                adapter.notifyDataSetChanged();
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
            if (ModifyNameActivity.TAG.equals(from)
                    || MoreActivity.TAG.equals(from)) {
                devices.clear();
                devices.addAll(DBTools.getInstance(this).selectAllDevice());
                adapter.notifyDataSetChanged();
                if (!devices.isEmpty()) {
                    lvDeviceList.setVisibility(View.VISIBLE);
                    rlEmpty.setVisibility(View.GONE);
                } else {
                    lvDeviceList.setVisibility(View.GONE);
                    rlEmpty.setVisibility(View.VISIBLE);
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
    public void deviceDetailClick(MokoDevice device) {
        LogModule.i("跳转详情");
        Intent intent = new Intent(this, DeviceDetailActivity.class);
        intent.putExtra(AppConstants.EXTRA_KEY_DEVICE, device);
        startActivity(intent);
    }

    @Override
    public void deviceSwitchClick(MokoDevice device) {
        if (MokoSupport.getInstance().isConnected()) {
            showLoadingProgressDialog(getString(R.string.wait));
            LogModule.i("切换开关");
            JsonObject json = new JsonObject();
            json.addProperty("switch_state", device.on_off ? "off" : "on");
            String mqttConfigAppStr = SPUtiles.getStringValue(MainActivity.this, AppConstants.SP_KEY_MQTT_CONFIG_APP, "");
            MQTTConfig appMqttConfig = new Gson().fromJson(mqttConfigAppStr, MQTTConfig.class);
            MqttMessage message = new MqttMessage();
            message.setPayload(json.toString().getBytes());
            message.setQos(appMqttConfig.qos);
            try {
                MokoSupport.getInstance().publish(device.getAppTopicSwitchState(), message);
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }
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
