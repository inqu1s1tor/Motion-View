package com.erminesoft.motionview.motionview.util;

import android.content.Context;

import com.erminesoft.motionview.motionview.R;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

public class TimeWorker {

    private static final int MINUTES_IN_HOUR = 60;

    public static boolean isCurrentDay(long time) {
        Calendar calendar = Calendar.getInstance();
        Calendar secondCalendar = Calendar.getInstance();
        secondCalendar.setTimeInMillis(time);

        return calendar.get(Calendar.DAY_OF_YEAR) == secondCalendar.get(Calendar.DAY_OF_YEAR) &&
                calendar.get(Calendar.YEAR) == secondCalendar.get(Calendar.YEAR);
    }

    public static int getCurrentDay() {
        return Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
    }

    public static int getCurrentMonth() {
        return Calendar.getInstance().get(Calendar.MONTH);
    }

    public static int getCurrentYear() {
        return Calendar.getInstance().get(Calendar.YEAR);
    }

    public static void setMidnight(Calendar calendar) {
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
    }

    public static String processMillisecondsToString(int milliseconds, Context context) {
        long minutes = TimeUnit.MILLISECONDS.toMinutes(milliseconds) % MINUTES_IN_HOUR;
        long hours = TimeUnit.MILLISECONDS.toHours(milliseconds);

        return String.format(context.getString(R.string.total_activity_time_format), hours, minutes);
    }

    public static int getDay(long timestamp) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timestamp);

        return calendar.get(Calendar.DAY_OF_MONTH);
    }

    public static int getMonth(long timestamp) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timestamp);

        return calendar.get(Calendar.MONTH);
    }

    public static int getYear(long timestamp) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timestamp);

        return calendar.get(Calendar.YEAR);
    }
}
