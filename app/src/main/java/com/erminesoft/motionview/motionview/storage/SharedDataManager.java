package com.erminesoft.motionview.motionview.storage;


import android.content.Context;
import android.content.SharedPreferences;

public class SharedDataManager {
    private static final int DEFAULT_DAILY_GOAL = 10000;

    private final SharedPreferences sharedPreferences;

    private static final String APP_PREFERENCES = "motion_view_settings";
    public static final String FIRST_INSTALL_TIME = "FIRST_INSTALL_TIME";
    public static final String USER_WEIGHT = "USER_WEIGHT";
    public static final String USER_HEIGHT = "USER_HEIGHT";
    public static final String USER_DAILY_GOAL = "USER_DAILY_GOAL";

    public SharedDataManager(Context context) {
        sharedPreferences = context.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);

        long firstStart = sharedPreferences.getLong(FIRST_INSTALL_TIME, -1);
        if (firstStart == -1) {
            sharedPreferences.edit().putLong(FIRST_INSTALL_TIME, System.currentTimeMillis()).apply();
            sharedPreferences.edit().putInt(USER_DAILY_GOAL, DEFAULT_DAILY_GOAL);
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
