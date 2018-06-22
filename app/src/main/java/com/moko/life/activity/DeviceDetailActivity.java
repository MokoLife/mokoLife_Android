package com.moko.life.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.moko.life.AppConstants;
import com.moko.life.R;
import com.moko.life.base.BaseActivity;
import com.moko.life.dialog.TimerDialog;
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
 * @ClassPath com.moko.life.activity.DeviceDetailActivity
 */
public class DeviceDetailActivity extends BaseActivity {
    @Bind(R.id.rl_title)
    RelativeLayout rlTitle;
    @Bind(R.id.iv_switch_state)
    ImageView ivSwitchState;
    @Bind(R.id.tv_device_schedule)
    TextView tvDeviceSchedule;
    @Bind(R.id.tv_device_timer)
    TextView tvDeviceTimer;
    @Bind(R.id.tv_device_statistics)
    TextView tvDeviceStatistics;
    @Bind(R.id.ll_bg)
    LinearLayout llBg;
    @Bind(R.id.tv_switch_state)
    TextView tvSwitchState;
    @Bind(R.id.tv_timer_state)
    TextView tvTimerState;
    private MokoDevice mokoDevice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_detail);
        ButterKnife.bind(this);
        if (getIntent().getExtras() != null) {
            mokoDevice = (MokoDevice) getIntent().getSerializableExtra(AppConstants.EXTRA_KEY_DEVICE);
            changeSwitchState();
        }
        // 注册广播接收器
        IntentFilter filter = new IntentFilter();
        filter.addAction(MokoConstants.ACTION_MQTT_CONNECTION);
        filter.addAction(MokoConstants.ACTION_MQTT_RECEIVE);
        filter.addAction(MokoConstants.ACTION_MQTT_PUBLISH);
        filter.addAction(AppConstants.ACTION_DEVICE_STATE);
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
                if (topic.equals(mokoDevice.getDeviceTopicSwitchState())) {
                    mokoDevice.isOnline = true;
                    String message = intent.getStringExtra(MokoConstants.EXTRA_MQTT_RECEIVE_MESSAGE);
                    JsonObject object = new JsonParser().parse(message).getAsJsonObject();
                    String switch_state = object.get("switch_state").getAsString();
                    if (!switch_state.equals(mokoDevice.on_off ? "on" : "off")) {
                        mokoDevice.on_off = !mokoDevice.on_off;
                        changeSwitchState();
                    }
                }
                if (topic.equals(mokoDevice.getDeviceTopicDelayTime())) {
                    String message = intent.getStringExtra(MokoConstants.EXTRA_MQTT_RECEIVE_MESSAGE);
                    JsonObject object = new JsonParser().parse(message).getAsJsonObject();
                    int delay_hour = object.get("delay_hour").getAsInt();
                    int delay_minute = object.get("delay_minute").getAsInt();
                    int delay_second = object.get("delay_second").getAsInt();
                    String switch_state = object.get("switch_state").getAsString();
                    if (delay_hour == 0 && delay_minute == 0 && delay_second == 0) {
                        tvTimerState.setVisibility(View.GONE);
                    } else {
                        tvTimerState.setVisibility(View.VISIBLE);
                        String timer = String.format("%s after %d:%d:%d", switch_state, delay_hour, delay_minute, delay_second);
                        tvTimerState.setText(timer);
                    }
                }
            }
            if (MokoConstants.ACTION_MQTT_PUBLISH.equals(action)) {
                int state = intent.getIntExtra(MokoConstants.EXTRA_MQTT_STATE, 0);
                dismissLoadingProgressDialog();
            }
            if (AppConstants.ACTION_DEVICE_STATE.equals(action)) {
                mokoDevice.isOnline = false;
                mokoDevice.on_off = false;
                changeSwitchState();
            }
        }
    };

    private void changeSwitchState() {
        rlTitle.setBackgroundColor(ContextCompat.getColor(this, mokoDevice.on_off ? R.color.blue_0188cc : R.color.black_303a4b));
        llBg.setBackgroundColor(ContextCompat.getColor(this, mokoDevice.on_off ? R.color.grey_f2f2f2 : R.color.black_303a4b));
        ivSwitchState.setImageDrawable(ContextCompat.getDrawable(this, mokoDevice.on_off ? R.drawable.switch_on : R.drawable.switch_off));
        tvSwitchState.setText(mokoDevice.on_off ? R.string.device_detail_switch_on : R.string.device_detail_switch_off);
        tvSwitchState.setTextColor(ContextCompat.getColor(this, mokoDevice.on_off ? R.color.blue_0188cc : R.color.grey_808080));
        Drawable drawableSchedult = ContextCompat.getDrawable(this, mokoDevice.on_off ? R.drawable.schedule_on : R.drawable.schedule_off);
        drawableSchedult.setBounds(0, 0, drawableSchedult.getMinimumWidth(), drawableSchedult.getMinimumHeight());
        tvDeviceSchedule.setCompoundDrawables(null, drawableSchedult, null, null);
        tvDeviceSchedule.setTextColor(ContextCompat.getColor(this, mokoDevice.on_off ? R.color.blue_0188cc : R.color.grey_808080));
        Drawable drawableTimer = ContextCompat.getDrawable(this, mokoDevice.on_off ? R.drawable.schedule_on : R.drawable.schedule_off);
        drawableTimer.setBounds(0, 0, drawableTimer.getMinimumWidth(), drawableTimer.getMinimumHeight());
        tvDeviceTimer.setCompoundDrawables(null, drawableTimer, null, null);
        tvDeviceTimer.setTextColor(ContextCompat.getColor(this, mokoDevice.on_off ? R.color.blue_0188cc : R.color.grey_808080));
        Drawable drawableStatistics = ContextCompat.getDrawable(this, mokoDevice.on_off ? R.drawable.schedule_on : R.drawable.schedule_off);
        drawableStatistics.setBounds(0, 0, drawableStatistics.getMinimumWidth(), drawableStatistics.getMinimumHeight());
        tvDeviceStatistics.setCompoundDrawables(null, drawableStatistics, null, null);
        tvDeviceStatistics.setTextColor(ContextCompat.getColor(this, mokoDevice.on_off ? R.color.blue_0188cc : R.color.grey_808080));
        tvTimerState.setTextColor(ContextCompat.getColor(this, mokoDevice.on_off ? R.color.blue_0188cc : R.color.grey_808080));
    }

    public void back(View view) {
        finish();
    }

    public void more(View view) {
        Intent intent = new Intent(this, MoreActivity.class);
        intent.putExtra(AppConstants.EXTRA_KEY_DEVICE, mokoDevice);
        startActivity(intent);
    }

    public void timerClick(View view) {
        if (isWindowLocked()) {
            return;
        }
        TimerDialog dialog = new TimerDialog(this);
        dialog.setData(mokoDevice.on_off);
        dialog.setListener(new TimerDialog.TimerListener() {
            @Override
            public void onConfirmClick(TimerDialog dialog) {
                if (!MokoSupport.getInstance().isConnected()) {
                    ToastUtils.showToast(DeviceDetailActivity.this, R.string.network_error);
                    return;
                }
                if (!mokoDevice.isOnline) {
                    ToastUtils.showToast(DeviceDetailActivity.this, R.string.device_offline);
                    return;
                }
                showLoadingProgressDialog(getString(R.string.wait));
                JsonObject json = new JsonObject();
                json.addProperty("delay_hour", dialog.getWvHour());
                json.addProperty("delay_minute", dialog.getWvMinute());
                String mqttConfigAppStr = SPUtiles.getStringValue(DeviceDetailActivity.this, AppConstants.SP_KEY_MQTT_CONFIG_APP, "");
                MQTTConfig appMqttConfig = new Gson().fromJson(mqttConfigAppStr, MQTTConfig.class);
                MqttMessage message = new MqttMessage();
                message.setPayload(json.toString().getBytes());
                message.setQos(appMqttConfig.qos);
                try {
                    MokoSupport.getInstance().publish(mokoDevice.getAppTopicDelayTime(), message);
                } catch (MqttException e) {
                    e.printStackTrace();
                }
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public void scheduleClick(View view) {
        ToastUtils.showToast(this, R.string.device_detail_schedule_tips);
    }

    public void statisticsClick(View view) {
        if (!MokoSupport.getInstance().isConnected()) {
            ToastUtils.showToast(this, R.string.network_error);
            return;
        }
        if (!mokoDevice.isOnline) {
            ToastUtils.showToast(this, R.string.device_offline);
            return;
        }
        startActivity(new Intent(this, ElectricityActivity.class));
    }

    public void switchClick(View view) {
        if (!MokoSupport.getInstance().isConnected()) {
            ToastUtils.showToast(this, R.string.network_error);
            return;
        }
        if (!mokoDevice.isOnline) {
            ToastUtils.showToast(this, R.string.device_offline);
            return;
        }
        showLoadingProgressDialog(getString(R.string.wait));
        LogModule.i("切换开关");
        JsonObject json = new JsonObject();
        json.addProperty("switch_state", mokoDevice.on_off ? "off" : "on");
        String mqttConfigAppStr = SPUtiles.getStringValue(DeviceDetailActivity.this, AppConstants.SP_KEY_MQTT_CONFIG_APP, "");
        MQTTConfig appMqttConfig = new Gson().fromJson(mqttConfigAppStr, MQTTConfig.class);
        MqttMessage message = new MqttMessage();
        message.setPayload(json.toString().getBytes());
        message.setQos(appMqttConfig.qos);
        try {
            MokoSupport.getInstance().publish(mokoDevice.getAppTopicSwitchState(), message);
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
