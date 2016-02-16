package com.erminesoft.motionview.motionview.util;

import java.util.Calendar;

public class TimeWorker {

    public static boolean isCurrentDay(long time) {
        Calendar calendar = Calendar.getInstance();
        Calendar secondCalendar = Calendar.getInstance();
        secondCalendar.setTimeInMillis(time);

        return calendar.get(Calendar.DAY_OF_YEAR) == secondCalendar.get(Calendar.DAY_OF_YEAR) &&
                calendar.get(Calendar.YEAR) == secondCalendar.get(Calendar.YEAR);
    }

    public static int getCurrentMonth() {
        return Calendar.getInstance().get(Calendar.MONTH);
    }

    public static int getCurrentYear() {
        return Calendar.getInstance().get(Calendar.YEAR);
    }
}
