package com.erminesoft.motionview.motionview.util;

import android.content.Context;

import com.erminesoft.motionview.motionview.R;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class ChartDataWorker {
    private static final List<Month> MONTHS_LIST = new ArrayList<>();

    public static void init(Context context) {
        List<String> months = Arrays
                .asList(context.getResources().getStringArray(R.array.month_array));

        for (int i = 0; i < months.size(); i++) {
            MONTHS_LIST.add(new Month(i, months.get(i)));
        }
    }

    public static Map<Integer, List<Month>> getAvailableYearsMonthsForSpinner(long firstInstallTime) {
        Map<Integer, List<Month>> yearsMonthsMap = new TreeMap<>();

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(firstInstallTime);

        int startYear = calendar.get(Calendar.YEAR);
        int startMonth = calendar.get(Calendar.MONTH);

        int currentYear = TimeWorker.getCurrentYear();
        int currentMonth = TimeWorker.getCurrentMonth();

        for (int year = startYear; year <= currentYear; ++year) {
            List<Month> months = new ArrayList<>();
            boolean isCurrentYear = year == currentYear;

            for (int month = startMonth;
                 month <= (isCurrentYear ? currentMonth : MONTHS_LIST.size() - 1);
                 ++month) {
                months.add(MONTHS_LIST.get(month));
            }

            yearsMonthsMap.put(year, months);
        }

        return yearsMonthsMap;
    }

    public static class Month implements Serializable {
        private final int mIndex;
        private final String mName;

        Month(int index, String name) {
            mIndex = index;
            mName = name;
        }

        public int getIndex() {
            return mIndex;
        }

        public String getName() {
            return mName;
        }

        @Override
        public String toString() {
            return getName();
        }
    }
}

