package com.moko.life.utils;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Environment;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class Utils {


    public static File getFile(Context context, String fileName) {
        String devicePath;
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            // 优先保存到SD卡中
            devicePath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "MokoEmpty" + File.separator + fileName;
        } else {
            // 如果SD卡不存在，就保存到本应用的目录下
            devicePath = context.getFilesDir().getAbsolutePath() + File.separator + "MokoEmpty" + File.separator + fileName;
        }
        File deviceListFile = new File(devicePath);
        if (!deviceListFile.exists()) {
            try {
                File parent = deviceListFile.getParentFile();
                if (!parent.exists()) {
                    parent.mkdirs();
                }
                deviceListFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return deviceListFile;
    }

    /**
     * @Date 2017/4/6
     * @Author wenzheng.liu
     * @Description 发送邮件
     */
    public static void sendEmail(Context context, String address, String body, String subject, String tips, File... files) {
        if (files.length == 0) {
            return;
        }
        Intent intent;
        if (files.length == 1) {
            intent = new Intent(Intent.ACTION_SEND);
            intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(files[0]));
            intent.putExtra(Intent.EXTRA_TEXT, body);
        } else {
            ArrayList<Uri> uris = new ArrayList<>();
            for (int i = 0; i < files.length; i++) {
                uris.add(Uri.fromFile(files[i]));
            }
            intent = new Intent(Intent.ACTION_SEND_MULTIPLE);
            intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);
            ArrayList<CharSequence> charSequences = new ArrayList<>();
            charSequences.add(body);
            intent.putExtra(Intent.EXTRA_TEXT, charSequences);
        }
        String[] addresses = {address};
        intent.putExtra(Intent.EXTRA_EMAIL, addresses);
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.setType("message/rfc822");
        Intent.createChooser(intent, tips);
        if (intent.resolveActivity(context.getPackageManager()) != null) {
            context.startActivity(intent);
        }
    }

    public static String getWifiSSID(Context context) {
        WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = wifi.getConnectionInfo();
        return info.getSSID();
    }

    /**
     * 检查网络是否可用
     *
     * @param context
     * @return
     */
    public static boolean isNetworkAvailable(Context context) {

        ConnectivityManager manager = (ConnectivityManager) context
                .getApplicationContext().getSystemService(
                        Context.CONNECTIVITY_SERVICE);

        if (manager == null) {
            return false;
        }

        NetworkInfo networkinfo = manager.getActiveNetworkInfo();

        if (networkinfo == null || !networkinfo.isAvailable()) {
            return false;
        }

        return true;
    }
}
