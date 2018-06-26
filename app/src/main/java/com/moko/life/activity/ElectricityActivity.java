package com.moko.life.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.moko.life.R;
import com.moko.life.base.BaseActivity;
import com.moko.life.entity.MokoDevice;
import com.moko.support.MokoConstants;

import java.text.DecimalFormat;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * @Date 2018/6/7
 * @Author wenzheng.liu
 * @Description
 * @ClassPath com.moko.life.activity.ElectricityActivity
 */
public class ElectricityActivity extends BaseActivity {


    @Bind(R.id.tv_current)
    TextView tvCurrent;
    @Bind(R.id.tv_voltage)
    TextView tvVoltage;
    @Bind(R.id.tv_power)
    TextView tvPower;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_electricity_manager);
        ButterKnife.bind(this);
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
            }
            if (MokoConstants.ACTION_MQTT_RECEIVE.equals(action)) {
                String topic = intent.getStringExtra(MokoConstants.EXTRA_MQTT_RECEIVE_TOPIC);
                if (topic.contains(MokoDevice.DEVICE_TOPIC_ELECTRICITY_INFORMATION)) {
                    String message = intent.getStringExtra(MokoConstants.EXTRA_MQTT_RECEIVE_MESSAGE);
                    JsonObject object = new JsonParser().parse(message).getAsJsonObject();
                    int voltage = object.get("voltage").getAsInt();
                    int current = object.get("current").getAsInt();
                    int power = object.get("power").getAsInt();
                    tvCurrent.setText(current + "");
                    tvVoltage.setText(new DecimalFormat().format(voltage * 0.1));
                    tvPower.setText(power + "");
                }
            }
        }
    };

    public void back(View view) {
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
    }

}
