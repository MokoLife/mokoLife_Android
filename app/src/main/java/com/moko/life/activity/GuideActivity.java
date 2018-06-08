package com.moko.life.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.widget.ImageView;

import com.moko.life.AppConstants;
import com.moko.life.R;
import com.moko.life.base.BaseActivity;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * @Date 2018/6/7
 * @Author wenzheng.liu
 * @Description
 * @ClassPath com.moko.life.activity.GuideActivity
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (!isWriteStoragePermissionOpen()) {
                    showRequestPermissionDialog();
                    return;
                }
            }
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AppConstants.REQUEST_CODE_PERMISSION) {
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (!isWriteStoragePermissionOpen()) {
                    showOpenSettingsDialog();
                } else {
                    delayGotoMain();
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case AppConstants.PERMISSION_REQUEST_CODE: {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                        // 判断用户是否 点击了不再提醒。(检测该权限是否还可以申请)
                        boolean shouldShowRequest = shouldShowRequestPermissionRationale(permissions[0]);
                        if (shouldShowRequest) {
                            showRequestPermissionDialog();
                        } else {
                            showOpenSettingsDialog();
                        }
                    } else {
                        delayGotoMain();
                    }
                }
            }
        }
    }

    private void showOpenSettingsDialog() {
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setCancelable(false)
                .setTitle(R.string.permission_storage_close_title)
                .setMessage(R.string.permission_storage_close_content)
                .setPositiveButton(getString(R.string.confirm), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        // 根据包名打开对应的设置界面
                        intent.setData(Uri.parse("package:" + getPackageName()));
                        startActivityForResult(intent, AppConstants.REQUEST_CODE_PERMISSION);
                    }
                })
                .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                        return;
                    }
                }).create();
        dialog.show();
    }

    private void showRequestPermissionDialog() {
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setCancelable(false)
                .setTitle(R.string.permission_storage_need_title)
                .setMessage(R.string.permission_storage_need_content)
                .setPositiveButton(getString(R.string.confirm), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions(GuideActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, AppConstants.PERMISSION_REQUEST_CODE);
                    }
                })
                .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                        return;
                    }
                }).create();
        dialog.show();
    }
}
