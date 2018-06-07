package com.moko.life.activity;

import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.moko.life.R;
import com.moko.life.base.BaseActivity;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * @Date 2018/6/7
 * @Author wenzheng.liu
 * @Description
 * @ClassPath com.moko.life.activity.AddDeviceActivity
 */
public class AddDeviceActivity extends BaseActivity {


    @Bind(R.id.not_blinking_tips)
    TextView notBlinkingTips;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_device);
        ButterKnife.bind(this);
        notBlinkingTips.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG); //下划线
        notBlinkingTips.getPaint().setAntiAlias(true);//抗锯齿
    }

    public void back(View view) {
        finish();
    }

    public void notBlinking(View view) {
    }

    public void plugBlinking(View view) {
    }
}
