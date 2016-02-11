package com.erminesoft.motionview.motionview.ui.activities;

import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

public abstract class GenericActivity extends AppCompatActivity {

    public void showShortToast(int resId){
        showShortToast(getString(resId));
    }

    public void showShortToast(String content){
        Toast.makeText(this, content, Toast.LENGTH_SHORT).show();
    }
}
