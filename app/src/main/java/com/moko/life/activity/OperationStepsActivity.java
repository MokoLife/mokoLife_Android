package com.moko.life.activity;

import android.os.Bundle;
import android.view.View;

import com.moko.life.R;
import com.moko.life.base.BaseActivity;

import butterknife.ButterKnife;

/**
 * @Date 2018/6/11
 * @Author wenzheng.liu
 * @Description
 * @ClassPath com.moko.life.activity.OperationStepsActivity
 */
public class OperationStepsActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_operation_steps);
        ButterKnife.bind(this);
    }

    public void back(View view) {
        finish();
    }

    public void plugBlinking(View view) {
        setResult(RESULT_OK);
        finish();
    }
}
