package com.erminesoft.motionview.motionview.ui.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.widget.TextView;

import com.erminesoft.motionview.motionview.R;

public class MainActivity extends BasicDailyStatisticActivity {
    private TextView mDateTextView;

    public static void start(Activity activity) {
        activity.startActivity(new Intent(activity, MainActivity.class));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.app_name);

        mDateTextView = (TextView) findViewById(R.id.date_text_view);
        mDateTextView.setText(getString(R.string.today_date_text));

        TabLayout tabLayout = (TabLayout) findViewById(R.id.main_fragment_container_tab_container);

        TabLayout.Tab tab = tabLayout.newTab();
        tab.setText("ASDASD");
        tabLayout.addTab(tab, true);
    }


}
