package com.moko.life.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.moko.life.entity.MokoDevice;

import java.util.ArrayList;

public class DBTools {
    private DBOpenHelper myDBOpenHelper;
    private SQLiteDatabase db;
    private static DBTools dbTools;

    public static DBTools getInstance(Context context) {
        if (dbTools == null) {
            dbTools = new DBTools(context);
            return dbTools;
        }
        return dbTools;
    }

    public DBTools(Context context) {
        myDBOpenHelper = new DBOpenHelper(context);
        db = myDBOpenHelper.getWritableDatabase();
    }

    public long insertDevice(MokoDevice mokoDevice) {
        ContentValues cv = new ContentValues();
        cv.put(DBConstants.DEVICE_FIELD_FUNCTION, mokoDevice.function);
        cv.put(DBConstants.DEVICE_FIELD_NAME, mokoDevice.name);
        cv.put(DBConstants.DEVICE_FIELD_NICK_NAME, mokoDevice.nickName);
        cv.put(DBConstants.DEVICE_FIELD_SPECIFICATIONS, mokoDevice.specifications);
        cv.put(DBConstants.DEVICE_FIELD_MAC, mokoDevice.mac);
        long row = db.insert(DBConstants.TABLE_NAME_DEVICE, null, cv);
        return row;
    }

    public ArrayList<MokoDevice> selectAllDevice() {
        Cursor cursor = db.query(DBConstants.TABLE_NAME_DEVICE, null, null, null,
                null, null, null);
        ArrayList<MokoDevice> mokoDevices = new ArrayList<>();
        while (cursor.moveToNext()) {
            MokoDevice mokoDevice = new MokoDevice();
            mokoDevice.id = cursor.getInt(cursor
                    .getColumnIndex(DBConstants.DEVICE_FIELD_ID));
            mokoDevice.function = cursor.getString(cursor
                    .getColumnIndex(DBConstants.DEVICE_FIELD_FUNCTION));
            mokoDevice.name = cursor.getString(cursor
                    .getColumnIndex(DBConstants.DEVICE_FIELD_NAME));
            mokoDevice.nickName = cursor.getString(cursor
                    .getColumnIndex(DBConstants.DEVICE_FIELD_NICK_NAME));
            mokoDevice.specifications = cursor.getString(cursor
                    .getColumnIndex(DBConstants.DEVICE_FIELD_SPECIFICATIONS));
            mokoDevice.mac = cursor.getString(cursor
                    .getColumnIndex(DBConstants.DEVICE_FIELD_MAC));
            mokoDevices.add(mokoDevice);
        }
        return mokoDevices;
    }

    public MokoDevice selectDevice(String mac) {
        Cursor cursor = db.query(DBConstants.TABLE_NAME_DEVICE, null, DBConstants.DEVICE_FIELD_MAC + " = ?", new String[]{mac}, null, null, null);
        MokoDevice mokoDevice = null;
        while (cursor.moveToFirst()) {
            mokoDevice = new MokoDevice();
            mokoDevice.id = cursor.getInt(cursor
                    .getColumnIndex(DBConstants.DEVICE_FIELD_ID));
            mokoDevice.function = cursor.getString(cursor
                    .getColumnIndex(DBConstants.DEVICE_FIELD_FUNCTION));
            mokoDevice.name = cursor.getString(cursor
                    .getColumnIndex(DBConstants.DEVICE_FIELD_NAME));
            mokoDevice.nickName = cursor.getString(cursor
                    .getColumnIndex(DBConstants.DEVICE_FIELD_NICK_NAME));
            mokoDevice.specifications = cursor.getString(cursor
                    .getColumnIndex(DBConstants.DEVICE_FIELD_SPECIFICATIONS));
            mokoDevice.mac = cursor.getString(cursor
                    .getColumnIndex(DBConstants.DEVICE_FIELD_MAC));
        }
        return mokoDevice;
    }


    public void updateDevice(MokoDevice mokoDevice) {
        String where = DBConstants.DEVICE_FIELD_MAC + " = ?";
        String[] whereValue = {mokoDevice.mac + ""};
        ContentValues cv = new ContentValues();
        cv.put(DBConstants.DEVICE_FIELD_NICK_NAME, mokoDevice.nickName);
        db.update(DBConstants.TABLE_NAME_DEVICE, cv, where, whereValue);
    }

    public void deleteAllData() {
        db.delete(DBConstants.TABLE_NAME_DEVICE, null, null);
    }

    public void deleteDevice(MokoDevice device) {
        String where = DBConstants.DEVICE_FIELD_MAC + " = ?";
        String[] whereValue = {device.mac + ""};
        db.delete(DBConstants.TABLE_NAME_DEVICE, where, whereValue);
    }

    // drop table;
    public void droptable(String tablename) {
        db.execSQL("DROP TABLE IF EXISTS " + tablename);
    }

    // close database;
    public void close(String databasename) {
        db.close();
    }

}
