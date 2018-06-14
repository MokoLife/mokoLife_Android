package com.moko.life.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.google.gson.Gson;
import com.moko.life.AppConstants;
import com.moko.life.R;
import com.moko.life.adapter.DeviceAdapter;
import com.moko.life.base.BaseActivity;
import com.moko.life.db.DBTools;
import com.moko.life.entity.MQTTConfig;
import com.moko.life.entity.MokoDevice;
import com.moko.life.service.MokoService;
import com.moko.life.utils.SPUtiles;

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
    private ArrayList<MokoDevice> devices;
    private DeviceAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        devices = DBTools.getInstance(this).selectAllDevice();
        if (devices.isEmpty()) {
            rlEmpty.setVisibility(View.VISIBLE);
            lvDeviceList.setVisibility(View.GONE);
        } else {
            lvDeviceList.setVisibility(View.VISIBLE);
            rlEmpty.setVisibility(View.GONE);
            adapter = new DeviceAdapter(this);
            adapter.setListener(this);
            adapter.setItems(devices);
            lvDeviceList.setAdapter(adapter);
        }
        startService(new Intent(this, MokoService.class));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopService(new Intent(this, MokoService.class));
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

    }

    @Override
    public void deviceSwitchClick() {

    }
}
