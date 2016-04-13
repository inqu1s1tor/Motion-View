package com.erminesoft.motionview.motionview.util;

import java.util.Calendar;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class TimeWorker {

    private static final int MINUTES_IN_HOUR = 60;

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

    public static String processSecondsToString(int s) {
        long minutes = TimeUnit.SECONDS.toMinutes(s) % MINUTES_IN_HOUR;
        long hours = TimeUnit.SECONDS.toHours(s);
        long seconds = TimeUnit.SECONDS.toSeconds(s) % MINUTES_IN_HOUR;

        String hs = hours < 10 ? "0" + hours : String.valueOf(hours);
        String ms = minutes < 10 ? "0" + minutes : String.valueOf(minutes);
        String ss = seconds < 10 ? "0" + seconds : String.valueOf(seconds);

        return String.format(Locale.getDefault(), "%1$s:%2$s:%3$s", hs, ms, ss);
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
