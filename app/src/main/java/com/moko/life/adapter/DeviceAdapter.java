package com.moko.life.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.moko.life.R;
import com.moko.life.base.BaseAdapter;
import com.moko.life.entity.MokoDevice;

import butterknife.Bind;
import butterknife.ButterKnife;


/**
 * @Date 2018/6/8
 * @Author wenzheng.liu
 * @Description
 * @ClassPath com.moko.life.adapter.DeviceAdapter
 */
public class DeviceAdapter extends BaseAdapter<MokoDevice> {


    public DeviceAdapter(Context context) {
        super(context);
    }

    @Override
    protected void bindViewHolder(int position, ViewHolder viewHolder, View convertView, ViewGroup parent) {
        final DeviceViewHolder holder = (DeviceViewHolder) viewHolder;
        final MokoDevice device = getItem(position);
        setView(holder, device);
    }

    private void setView(DeviceViewHolder holder, final MokoDevice device) {
        holder.ivSwitch.setImageDrawable(ContextCompat.getDrawable(mContext, device.on_off ? R.drawable.checkbox_open : R.drawable.checkbox_close));
        holder.tvDeviceName.setText(device.nickName);
        holder.tvDeviceSwitch.setText(device.on_off ? mContext.getString(R.string.switch_on) : mContext.getString(R.string.switch_off));
        holder.tvDeviceSwitch.setTextColor(ContextCompat.getColor(mContext, device.on_off ? R.color.blue_0188cc : R.color.grey_cccccc));
        holder.ivSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.deviceSwitchClick();
            }
        });
        holder.rlDeviceDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.deviceDetailClick();
            }
        });
        holder.rlDeviceDetail.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                listener.deviceDelete(device);
                return true;
            }}
        );
    }

    @Override
    protected ViewHolder createViewHolder(int position, LayoutInflater inflater, ViewGroup parent) {
        final View convertView = inflater.inflate(R.layout.device_item, parent, false);
        return new DeviceViewHolder(convertView);
    }

    public void setListener(AdapterClickListener listener) {
        this.listener = listener;
    }

    static class DeviceViewHolder extends ViewHolder {
        @Bind(R.id.rl_device_detail)
        RelativeLayout rlDeviceDetail;
        @Bind(R.id.tv_device_name)
        TextView tvDeviceName;
        @Bind(R.id.tv_device_switch)
        TextView tvDeviceSwitch;
        @Bind(R.id.iv_switch)
        ImageView ivSwitch;

        public DeviceViewHolder(View convertView) {
            super(convertView);
            ButterKnife.bind(this, convertView);
        }
    }

    private AdapterClickListener listener;

    public interface AdapterClickListener {
        void deviceDetailClick();

        void deviceSwitchClick();

        void deviceDelete(MokoDevice device);
    }
}
