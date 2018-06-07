package com.moko.smartplug.activity;

import android.os.Bundle;
import android.view.View;

import com.moko.smartplug.R;
import com.moko.smartplug.base.BaseActivity;

import butterknife.ButterKnife;

/**
 * @Date 2018/6/7
 * @Author wenzheng.liu
 * @Description 设备列表
 * @ClassPath com.moko.smartplug.activity.MainActivity
 */
public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void mainSettings(View view) {
    }

    public void mainAddDevices(View view) {
    }
}
