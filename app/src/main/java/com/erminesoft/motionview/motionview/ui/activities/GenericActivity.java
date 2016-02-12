package com.erminesoft.motionview.motionview.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.erminesoft.motionview.motionview.core.MVApplication;
import com.erminesoft.motionview.motionview.net.GoogleClientHelper;
import com.erminesoft.motionview.motionview.ui.fragments.ErrorDialogFragment;

public abstract class GenericActivity extends AppCompatActivity {
    protected final String TAG = this.getClass().getSimpleName();
    protected GoogleClientHelper mGoogleClientHelper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mGoogleClientHelper = getMVapplication().getGoogleClientHelper();
    }

    public final MVApplication getMVapplication() {
        return (MVApplication) getApplication();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ErrorDialogFragment.REQUEST_RESOLVE_ERROR) {
            mGoogleClientHelper.tryConnectClient(resultCode);
        }
    }

    public void showShortToast(int resId){
        showShortToast(getString(resId));
    }

    public void showShortToast(String content){
        Toast.makeText(this, content, Toast.LENGTH_SHORT).show();
    }
}
