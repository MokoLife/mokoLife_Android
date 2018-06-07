package com.moko.life.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.moko.life.R;
import com.moko.life.base.BaseActivity;

import butterknife.ButterKnife;

/**
 * @Date 2018/6/7
 * @Author wenzheng.liu
 * @Description
 * @ClassPath com.moko.life.activity.SelectDeviceTypeActivity
 */
public class SettingsActivity extends BaseActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ButterKnife.bind(this);

    }

    public void back(View view) {
        finish();
    }

    public void settingForDevice(View view) {
        startActivity(new Intent(this, SettingsDeviceActivity.class));
    }

    public void settingForAPP(View view) {
    }
}
