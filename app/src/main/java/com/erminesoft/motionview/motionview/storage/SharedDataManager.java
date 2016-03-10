package com.erminesoft.motionview.motionview.storage;


import android.content.Context;
import android.content.SharedPreferences;

public class SharedDataManager {

    private final SharedPreferences sharedPreferences;

    private static final String APP_PREFERENCES = "motion_view_settings";
    public static final String FIRST_INSTALL_TIME = "FIRST_INSTALL_TIME";
    public static final String USER_WEIGHT = "USER_WEIGHT";
    public static final String USER_HEIGHT = "USER_HEIGHT";
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

    public int readInt(String fieldName) {
        return sharedPreferences.getInt(fieldName, 0);
    }

    public long readLong(String fieldName) {
        return sharedPreferences.getLong(fieldName, 0);
    }
}
