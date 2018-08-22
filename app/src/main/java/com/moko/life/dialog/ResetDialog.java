package com.moko.life.dialog;

import android.content.Context;
import android.view.View;

import com.moko.life.R;
import com.moko.life.base.BaseDialog;

import butterknife.OnClick;

/**
 * @Date 2018/6/21
 * @Author wenzheng.liu
 * @Description
 * @ClassPath com.moko.life.dialog.ResetDialog
 */
public class ResetDialog extends BaseDialog<Boolean> {

    public ResetDialog(Context context) {
        super(context);
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.dialog_reset_device;
    }

    @Override
    protected void renderConvertView(View convertView, Boolean on_off) {

    }


    @OnClick({R.id.tv_cancel, R.id.tv_confirm})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_cancel:
                dismiss();
                break;
            case R.id.tv_confirm:
                listener.onConfirmClick(this);
                break;
        }
    }

    private ResetListener listener;

    public void setListener(ResetListener listener) {
        this.listener = listener;
    }

    public interface ResetListener {
        void onConfirmClick(ResetDialog dialog);
    }
}
