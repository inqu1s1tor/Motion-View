package com.erminesoft.motionview.motionview.storage;


import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import java.util.Date;

public class SharedDataManager {

    private SharedPreferences sharedPreferences;

    private static final String APP_PREFERENCES = "motion_view_settings";
    public static final String FIRST_INSTALL_TIME = "FIRST_INSTALL_TIME";
    public static final String USER_WEIGHT = "USER_WEIGHT";
    public static final String USER_HEIGHT = "USER_HEIGHT";
    public static final String USER_GENDER = "USER_GENDER";
    public static final String USER_DAY_GOAL = "USER_DAY_GOAL";

    public SharedDataManager(Context context) {
        sharedPreferences = context.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);

        long firstStart = sharedPreferences.getLong(FIRST_INSTALL_TIME, -1);
        if (firstStart == -1) {
            sharedPreferences.edit().putLong(FIRST_INSTALL_TIME,System.currentTimeMillis()).apply();
        }
    }

    public void writeInt(String fieldName, int data) {
        sharedPreferences.edit().putInt(fieldName, data).apply();
    }

    public void writeLong(String fieldName, long data) {
        sharedPreferences.edit().putLong(fieldName, data).apply();
    }

    public void writeString(String fieldName, String data) {
        sharedPreferences.edit().putString(fieldName, data).apply();
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
