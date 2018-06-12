package com.moko.life.activity;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;
import com.moko.life.AppConstants;
import com.moko.life.R;
import com.moko.life.base.BaseActivity;
import com.moko.life.dialog.CustomDialog;
import com.moko.life.entity.RequestDeviceInfo;
import com.moko.life.utils.ToastUtils;
import com.moko.support.MokoConstants;
import com.moko.support.entity.DeviceResponse;
import com.moko.support.entity.DeviceResult;
import com.moko.support.log.LogModule;
import com.moko.support.service.SocketService;

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
    private CustomDialog wifiAlertDialog;
    private SocketService mService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_device);
        ButterKnife.bind(this);
        notBlinkingTips.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG); //下划线
        notBlinkingTips.getPaint().setAntiAlias(true);//抗锯齿
        bindService(new Intent(this, SocketService.class), mServiceConnection, BIND_AUTO_CREATE);
    }

    private ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            LogModule.i("连接服务onServiceConnected...");
            mService = ((SocketService.LocalBinder) service).getService();
            // 注册广播接收器
            IntentFilter filter = new IntentFilter();
            filter.addAction(MokoConstants.ACTION_CONNECT_STATUS);
            filter.addAction(MokoConstants.ACTION_RESPONSE);
            filter.setPriority(100);
            registerReceiver(mReceiver, filter);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            LogModule.i("断开服务onServiceDisconnected...");
            // mMokoService = null;
        }
    };

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (MokoConstants.ACTION_CONNECT_STATUS.equals(action)) {
                int status = intent.getIntExtra(MokoConstants.EXTRA_CONNECT_STATUS, -1);
                if (status == MokoConstants.CONN_STATUS_SUCCESS) {
                    RequestDeviceInfo requestDeviceInfo = new RequestDeviceInfo();
                    requestDeviceInfo.header = MokoConstants.HEADER_GET_DEVICE_INFO;
                    mService.sendMessage(new Gson().toJson(requestDeviceInfo));
                }
            }
            if (MokoConstants.ACTION_RESPONSE.equals(action)) {
                DeviceResponse response = (DeviceResponse) intent.getSerializableExtra(MokoConstants.EXTRA_RESPONSE_INFO);
                if (response.code == MokoConstants.RESPONSE_SUCCESS) {
                    switch (response.code) {
                        case MokoConstants.HEADER_GET_DEVICE_INFO:
                            DeviceResult result = response.result;
                            // TODO: 2018/6/12 获取设备信息，设置成功后保存
                            mService.closeSocket();
                            break;
                    }
                } else {
                    ToastUtils.showToast(AddDeviceActivity.this, response.message);
                }
            }
        }
    };

    public void back(View view) {
        finish();
    }

    /**
     * @Date 2018/6/12
     * @Author wenzheng.liu
     * @Description 查看打开AP步骤
     * @ClassPath com.moko.life.activity.AddDeviceActivity
     */
    public void notBlinking(View view) {
        startActivityForResult(new Intent(this, OperationStepsActivity.class), AppConstants.REQUEST_CODE_OPERATION_STEP);
    }

    /**
     * @Date 2018/6/12
     * @Author wenzheng.liu
     * @Description 判断是否连接设备wifi
     */
    public void plugBlinking(View view) {
        checkWifiInfo();
    }

    private void checkWifiInfo() {
        if (!isWifiCorrect()) {
            View wifiAlertView = LayoutInflater.from(this).inflate(R.layout.wifi_setting_content, null);
            wifiAlertDialog = new CustomDialog.Builder(this)
                    .setContentView(wifiAlertView)
                    .setPositiveButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .setNegativeButton(R.string.connect, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // 跳转系统WIFI页面
                            Intent intent = new Intent();
                            intent.setAction("android.net.wifi.PICK_WIFI_NETWORK");
                            startActivityForResult(intent, AppConstants.REQUEST_CODE_WIFI_SETTING);
                        }
                    })
                    .create();
            wifiAlertDialog.show();
        } else {
            // 弹出输入WIFI弹框
            showWifiInputDialog();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AppConstants.REQUEST_CODE_WIFI_SETTING) {
            if (isWifiCorrect()) {
                // 弹出输入WIFI弹框
                if (wifiAlertDialog != null && !isFinishing() && wifiAlertDialog.isShowing()) {
                    wifiAlertDialog.dismiss();
                }
                showWifiInputDialog();
            }
        }
        if (requestCode == AppConstants.REQUEST_CODE_OPERATION_STEP) {
            if (resultCode == RESULT_OK) {
                checkWifiInfo();
            }
        }
    }

    private void showWifiInputDialog() {
        View wifiInputView = LayoutInflater.from(this).inflate(R.layout.wifi_input_content, null);
        final EditText etSSID = ButterKnife.findById(wifiInputView, R.id.et_ssid);
        final EditText etPassword = ButterKnife.findById(wifiInputView, R.id.et_password);
        CustomDialog dialog = new CustomDialog.Builder(this)
                .setContentView(wifiInputView)
                .setPositiveButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setNegativeButton(R.string.confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        // TODO: 2018/6/12 获取WIFI后，连接成功后发给设备
                        // 连接设备
                        mService.startSocket();

                    }
                })
                .create();
        dialog.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
        unbindService(mServiceConnection);
    }
}
