package com.moko.life.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.moko.life.R;
import com.moko.life.base.BaseActivity;
import com.moko.life.utils.Utils;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * @Date 2018/6/7
 * @Author wenzheng.liu
 * @Description
 * @ClassPath com.moko.life.activity.AboutActivity
 */
public class AboutActivity extends BaseActivity {


    @Bind(R.id.tv_app_version)
    TextView tvAppVersion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        ButterKnife.bind(this);
        tvAppVersion.setText(Utils.getVersionInfo(this));

    }

    public void back(View view) {
        finish();
    }
}
