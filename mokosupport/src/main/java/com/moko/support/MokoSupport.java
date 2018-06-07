package com.moko.support;

import android.content.Context;

import com.moko.support.log.LogModule;

/**
 * @Date 2017/12/7 0007
 * @Author wenzheng.liu
 * @Description
 * @ClassPath com.moko.support.MokoSupport
 */
public class MokoSupport {

    private static volatile MokoSupport INSTANCE;

    private Context mContext;

    private MokoSupport() {
    }

    public static MokoSupport getInstance() {
        if (INSTANCE == null) {
            synchronized (MokoSupport.class) {
                if (INSTANCE == null) {
                    INSTANCE = new MokoSupport();
                }
            }
        }
        return INSTANCE;
    }

    public void init(Context context) {
        LogModule.init(context);
        mContext = context;
    }
}
