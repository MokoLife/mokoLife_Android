package com.moko.life.utils;

import android.content.Context;
import android.text.TextUtils;
import android.view.Gravity;
import android.widget.Toast;

/**
 * toast方法
 *
 * @author jianweiwang
 */
final public class ToastUtils {

    /**
     * 之前显示的内容
     */
    private static String oldMsg;
    /**
     * Toast对象
     */
    private static Toast toast = null;
    /**
     * 第一次时间
     */
    private static long oneTime = 0;
    /**
     * 第二次时间
     */
    private static long twoTime = 0;

    public static final void showToast(Context context, String tip) {
        showToast(context, tip, true);
    }

    public static final void showToast(Context context, int tipID) {
        showToast(context, tipID, true);
    }

    public static final void showToast(Context context, int tipID,
                                       boolean isCenter) {
        String tip = (String) context.getResources().getText(tipID);
        showToast(context, tip, isCenter);
    }

    /**
     * toast n个字以上 LENGTH_LONG
     *
     * @param context
     * @param tip
     * @param isCenter
     */
    public static final void showToast(Context context, String tip,
                                       boolean isCenter) {
        int duration = Toast.LENGTH_SHORT;
        if (TextUtils.isEmpty(tip)) {
            return;
        }
        if (tip.length() >= 15) {
            duration = Toast.LENGTH_LONG;
        }
        if (toast == null) {
            toast = Toast.makeText(context.getApplicationContext(), tip, duration);
            if (isCenter) {
                toast.setGravity(Gravity.CENTER, 0, 0);
            }
            toast.show();
            oneTime = System.currentTimeMillis();
        } else {
            if (isCenter) {
                toast.setGravity(Gravity.CENTER, 0, 0);
            }
            twoTime = System.currentTimeMillis();
            if (tip.equals(oldMsg)) {
                if (twoTime - oneTime > duration) {
                    toast.show();
                }
            } else {
                oldMsg = tip;
                toast.setText(tip);
                toast.show();
            }
        }
        oneTime = twoTime;
        toast.show();
    }

}
