package com.moko.smartplug.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import com.moko.smartplug.R;
import com.moko.smartplug.base.BaseActivity;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * @Date 2018/6/7
 * @Author wenzheng.liu
 * @Description
 * @ClassPath com.moko.smartplug.activity.GuideActivity
 */
public class GuideActivity extends BaseActivity {

    @Bind(R.id.iv_logo)
    ImageView ivLogo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);
        ButterKnife.bind(this);
        if ((getIntent().getFlags() & Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT) != 0) {
            finish();
            return;
        }
        delayGotoMain();
    }

    private void delayGotoMain() {
        ivLogo.postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(GuideActivity.this, MainActivity.class));
                GuideActivity.this.finish();
            }
        }, 2000);
    }
}
