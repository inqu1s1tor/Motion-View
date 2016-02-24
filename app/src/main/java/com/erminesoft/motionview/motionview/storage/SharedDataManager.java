package com.erminesoft.motionview.motionview.storage;


import android.content.Context;
import android.content.SharedPreferences;

import java.util.Date;

public class SharedDataManager {

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private static final String APP_PREFERENCES = "motion_view_settings";
    public static final String USER_WEIGHT = "USER_WEIGHT";
    public static final String USER_HEIGHT = "USER_HEIGHT";
    public static final String FIRST_INSTALL_TIME = "FIRST_INSTALL_TIME";
    public static final String USER_GENDER = "USER_GENDER";

    public SharedDataManager(Context context) {
        sharedPreferences = context.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        long firstStart = sharedPreferences.getLong(FIRST_INSTALL_TIME, 0);
        if (firstStart == 0) {
            Date d = new Date();
            editor.putLong(FIRST_INSTALL_TIME, d.getTime());
            editor.apply();
        }
    }

    public void writeInt(String fieldName, int data) {
        if (fieldName.length() == 0 || data == 0 || fieldName == null) {
            return;
        }
        editor.putInt(fieldName, data);
        editor.apply();
    }

    public void writeLong(String fieldName, long data) {
        if (fieldName.length() == 0 || data == 0 || fieldName == null) {
            return;
        }
        editor.putLong(fieldName, data);
        editor.apply();
    }

    public void writeString(String fieldName, String data) {
        if (fieldName.length() == 0 || fieldName == null || data == null) {
            return;
        }
        editor.putString(fieldName, data);
        editor.apply();
    }

    public String readString(String fieldName) {
        return sharedPreferences.getString(fieldName, "");
    }

    public int readInt(String fieldName) {
        return sharedPreferences.getInt(fieldName, 0);
    }

    public long readLong(String fieldName) {
        return sharedPreferences.getLong(fieldName, 0);
    }
}
