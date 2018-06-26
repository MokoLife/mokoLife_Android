package com.moko.life.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.moko.life.AppConstants;
import com.moko.life.R;
import com.moko.life.base.BaseActivity;
import com.moko.life.entity.MQTTConfig;
import com.moko.life.utils.SPUtiles;
import com.moko.life.utils.ToastUtils;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * @Date 2018/6/7
 * @Author wenzheng.liu
 * @Description
 * @ClassPath com.moko.life.activity.SetDeviceMqttActivity
 */
public class SetDeviceMqttActivity extends BaseActivity implements RadioGroup.OnCheckedChangeListener {


    @Bind(R.id.et_mqtt_host)
    EditText etMqttHost;
    @Bind(R.id.et_mqtt_port)
    EditText etMqttPort;
    @Bind(R.id.iv_clean_session)
    ImageView ivCleanSession;
    @Bind(R.id.rg_conn_mode)
    RadioGroup rgConnMode;
    @Bind(R.id.tv_qos)
    TextView tvQos;
    @Bind(R.id.et_mqtt_client_id)
    EditText etMqttClientId;
    @Bind(R.id.et_mqtt_username)
    EditText etMqttUsername;
    @Bind(R.id.et_mqtt_password)
    EditText etMqttPassword;
    @Bind(R.id.et_keep_alive)
    EditText etKeepAlive;
    @Bind(R.id.title)
    TextView title;
    @Bind(R.id.rl_client_id)
    RelativeLayout rlClientId;

    private String[] mQosArray = new String[]{"0", "1", "2"};


    private MQTTConfig mqttConfig;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mqtt_device);
        ButterKnife.bind(this);
        String mqttConfigStr = SPUtiles.getStringValue(this, AppConstants.SP_KEY_MQTT_CONFIG, "");
        title.setText(R.string.settings_mqtt_device);
        rlClientId.setVisibility(View.GONE);
        if (TextUtils.isEmpty(mqttConfigStr)) {
            mqttConfig = new MQTTConfig();
        } else {
            Gson gson = new Gson();
            mqttConfig = gson.fromJson(mqttConfigStr, MQTTConfig.class);
        }
        initData();
    }

    private void initData() {
        etMqttHost.setText(mqttConfig.host);
        etMqttHost.setSelection(mqttConfig.host.length());
        etMqttPort.setText(mqttConfig.port);
        tvQos.setText(mQosArray[mqttConfig.qos]);
        ivCleanSession.setImageDrawable(ContextCompat.getDrawable(this, mqttConfig.cleanSession ? R.drawable.checkbox_open : R.drawable.checkbox_close));
        rgConnMode.check(mqttConfig.connectMode == 0 ? R.id.rb_conn_mode_tcp : R.id.rb_conn_mode_ssl);
        rgConnMode.setOnCheckedChangeListener(this);
        etKeepAlive.setText(mqttConfig.keepAlive + "");
        etMqttClientId.setText(mqttConfig.clientId);
        etMqttUsername.setText(mqttConfig.username);
        etMqttPassword.setText(mqttConfig.password);
    }

    public void back(View view) {
        finish();
    }

    public void clearSettings(View view) {
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Clear All Parameters")
                .setMessage("Please confirm whether to clear all parameters?")
                .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mqttConfig.reset();
                        initData();
                        dialog.dismiss();
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).create();
        dialog.show();
    }

    public void checkQos(View view) {
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setCancelable(true)
                .setSingleChoiceItems(mQosArray, mqttConfig.qos, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mqttConfig.qos = which;
                        tvQos.setText(mQosArray[mqttConfig.qos]);
                        dialog.dismiss();
                    }
                })
                .create();
        dialog.show();
    }

    public void saveSettings(View view) {
        mqttConfig.host = etMqttHost.getText().toString().replaceAll(" ", "");
        mqttConfig.port = etMqttPort.getText().toString();
        mqttConfig.keepAlive = Integer.parseInt(etKeepAlive.getText().toString());
        mqttConfig.clientId = etMqttClientId.getText().toString().replaceAll(" ", "");
        mqttConfig.username = etMqttUsername.getText().toString().replaceAll(" ", "");
        mqttConfig.password = etMqttPassword.getText().toString().replaceAll(" ", "");
        if (mqttConfig.isEmpty()) {
            ToastUtils.showToast(this, getString(R.string.mqtt_verify_empty));
            return;
        }
        String port = etMqttPort.getText().toString();
        if (Integer.parseInt(port) > 65535) {
            ToastUtils.showToast(this, getString(R.string.mqtt_verify_port));
            return;
        }
        String mqttConfigStr = new Gson().toJson(mqttConfig, MQTTConfig.class);
        SPUtiles.setStringValue(this, AppConstants.SP_KEY_MQTT_CONFIG, mqttConfigStr);
        ToastUtils.showToast(this, getString(R.string.success));
        String mqttAppConfigStr = SPUtiles.getStringValue(this, AppConstants.SP_KEY_MQTT_CONFIG_APP, "");
        if (TextUtils.isEmpty(mqttAppConfigStr)) {
            startActivity(new Intent(this, SetAppMqttActivity.class));
        }
        finish();
    }

    public void cleanSession(View view) {
        mqttConfig.cleanSession = !mqttConfig.cleanSession;
        ivCleanSession.setImageDrawable(ContextCompat.getDrawable(this, mqttConfig.cleanSession ? R.drawable.checkbox_open : R.drawable.checkbox_close));
    }

    @Override
    public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
        switch (checkedId) {
            case R.id.rb_conn_mode_tcp:
                mqttConfig.connectMode = 0;
                break;
            case R.id.rb_conn_mode_ssl:
                mqttConfig.connectMode = 1;
                break;
        }
    }
}
