package com.erminesoft.motionview.motionview.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.Toast;

import com.erminesoft.motionview.motionview.core.MVApplication;
import com.erminesoft.motionview.motionview.net.GoogleClientFacade;
import com.erminesoft.motionview.motionview.ui.fragments.ErrorDialogFragment;

public abstract class GenericActivity extends AppCompatActivity {
    protected final String TAG = this.getClass().getSimpleName();
    protected GoogleClientFacade mGoogleClientFacade;
    protected ActionBar mActionBar;

    public final MVApplication getMVapplication() {
        return (MVApplication) getApplication();
    }

    public void showShortToast(int resId) {
        showShortToast(getString(resId));
    }

    public void showShortToast(String content) {
        Toast.makeText(this, content, Toast.LENGTH_SHORT).show();
    }

    public void showLongToast(String content) {
        Toast.makeText(this, content, Toast.LENGTH_LONG).show();
    }
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mGoogleClientFacade = getMVapplication().getGoogleClientFacade();
        mActionBar = getSupportActionBar();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ErrorDialogFragment.REQUEST_RESOLVE_ERROR) {
            mGoogleClientFacade.tryConnectClient(resultCode);
        }
    }

    protected void setHomeAsUpEnabled() {
        mActionBar.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}
