package com.moko.life.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;

import com.moko.life.AppConstants;
import com.moko.life.R;
import com.moko.life.base.BaseActivity;
import com.moko.life.db.DBTools;
import com.moko.life.entity.MokoDevice;
import com.moko.life.utils.ToastUtils;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * @Date 2018/6/7
 * @Author wenzheng.liu
 * @Description
 * @ClassPath com.moko.life.activity.ModifyNameActivity
 */
public class ModifyNameActivity extends BaseActivity {
    public static String TAG = "ModifyNameActivity";

    @Bind(R.id.et_nick_name)
    EditText etNickName;
    private MokoDevice device;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_device_name);
        ButterKnife.bind(this);
        device = (MokoDevice) getIntent().getSerializableExtra("mokodevice");
        etNickName.setText(device.nickName);
        etNickName.setSelection(etNickName.getText().toString().length());
    }


    public void modifyDone(View view) {
        String nickName = etNickName.getText().toString();
        if (TextUtils.isEmpty(nickName)) {
            ToastUtils.showToast(this, R.string.modify_device_name_empty);
            return;
        }
        device.nickName = nickName;
        DBTools.getInstance(this).updateDevice(device);
        // 跳转首页，刷新数据
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(AppConstants.EXTRA_KEY_FROM_ACTIVITY, TAG);
        startActivity(intent);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }
}
