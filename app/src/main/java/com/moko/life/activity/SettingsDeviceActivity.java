package com.moko.life.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.moko.life.R;
import com.moko.life.base.BaseActivity;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @Date 2018/6/7
 * @Author wenzheng.liu
 * @Description
 * @ClassPath com.moko.life.activity.SelectDeviceTypeActivity
 */
public class SettingsDeviceActivity extends BaseActivity {


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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mqtt_device);
        ButterKnife.bind(this);

    }

    public void back(View view) {
        finish();
    }

    public void clearSettings(View view) {
    }

    public void checkQos(View view) {
    }

    public void saveSettings(View view) {
    }

    @OnClick({R.id.iv_clean_session, R.id.tv_qos})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_clean_session:
                break;
            case R.id.tv_qos:
                break;
        }
    }
}
