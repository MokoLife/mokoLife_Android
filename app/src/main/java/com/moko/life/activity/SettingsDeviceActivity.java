package com.moko.life.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.moko.life.R;
import com.moko.life.base.BaseActivity;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * @Date 2018/6/7
 * @Author wenzheng.liu
 * @Description
 * @ClassPath com.moko.life.activity.SelectDeviceTypeActivity
 */
public class SettingsDeviceActivity extends BaseActivity implements RadioGroup.OnCheckedChangeListener {


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

    private int mCheckedQos;
    private String[] mQosArray = new String[]{"2", "1", "0"};

    private boolean mIsCleanSession;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mqtt_device);
        ButterKnife.bind(this);
        tvQos.setText(mQosArray[mCheckedQos]);
        ivCleanSession.setImageDrawable(ContextCompat.getDrawable(this, mIsCleanSession ? R.drawable.checkbox_open : R.drawable.checkbox_close));
        rgConnMode.setOnCheckedChangeListener(this);
    }

    public void back(View view) {
        finish();
    }

    public void clearSettings(View view) {
        etMqttHost.setText("");
        etMqttPort.setText("");
        etMqttClientId.setText("");
        etMqttUsername.setText("");
        etMqttPassword.setText("");
        etKeepAlive.setText("");
    }

    public void checkQos(View view) {
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setCancelable(true)
                .setSingleChoiceItems(mQosArray, mCheckedQos, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mCheckedQos = which;
                        tvQos.setText(mQosArray[mCheckedQos]);
                        dialog.dismiss();
                    }
                })
                .create();
        dialog.show();
    }

    public void saveSettings(View view) {
    }

    public void cleanSession(View view) {
        mIsCleanSession = !mIsCleanSession;
        ivCleanSession.setImageDrawable(ContextCompat.getDrawable(this, mIsCleanSession ? R.drawable.checkbox_open : R.drawable.checkbox_close));
    }

    @Override
    public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
        switch (checkedId) {
            case R.id.rb_conn_mode_tcp:
                break;
            case R.id.rb_conn_mode_ssl:
                break;
        }
    }
}
