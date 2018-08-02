package com.moko.life.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.moko.life.AppConstants;
import com.moko.life.R;
import com.moko.life.base.BaseActivity;
import com.moko.life.entity.MokoDevice;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * @Date 2018/6/7
 * @Author wenzheng.liu
 * @Description
 * @ClassPath com.moko.life.activity.DeviceInfoActivity
 */
public class DeviceInfoActivity extends BaseActivity {

    @Bind(R.id.tv_company_name)
    TextView tvCompanyName;
    @Bind(R.id.tv_device_date)
    TextView tvDeviceDate;
    @Bind(R.id.tv_device_name)
    TextView tvDeviceName;
    @Bind(R.id.tv_device_version)
    TextView tvDeviceVersion;
    @Bind(R.id.tv_device_mac)
    TextView tvDeviceMac;
    private MokoDevice mokoDevice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_info);
        ButterKnife.bind(this);
        if (getIntent().getExtras() != null) {
            mokoDevice = (MokoDevice) getIntent().getSerializableExtra(AppConstants.EXTRA_KEY_DEVICE);
            tvCompanyName.setText(mokoDevice.company_name);
            tvDeviceDate.setText(mokoDevice.production_date);
            tvDeviceName.setText(mokoDevice.product_model);
            tvDeviceVersion.setText(mokoDevice.firmware_version);
            tvDeviceMac.setText(mokoDevice.mac);
        }

    }

    public void back(View view) {
        finish();
    }
}
