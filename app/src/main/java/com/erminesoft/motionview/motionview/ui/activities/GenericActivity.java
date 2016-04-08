package com.erminesoft.motionview.motionview.ui.activities;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.Toast;

import com.erminesoft.motionview.motionview.R;
import com.erminesoft.motionview.motionview.core.MVApplication;
import com.erminesoft.motionview.motionview.net.fitness.GoogleFitnessFacade;
import com.erminesoft.motionview.motionview.ui.fragments.ErrorDialogFragment;

@SuppressWarnings("WeakerAccess")
public abstract class GenericActivity extends AppCompatActivity {
    protected GoogleFitnessFacade mGoogleFitnessFacade;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mGoogleFitnessFacade = getMVApplication().getGoogleFitnessFacade();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
        }



        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    public final MVApplication getMVApplication() {
        return (MVApplication) getApplication();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ErrorDialogFragment.REQUEST_RESOLVE_ERROR) {
            mGoogleFitnessFacade.tryConnectClient(resultCode);
        }
    }

    protected final void setHomeAsUpEnabled(boolean enabled) {
        ActionBar actionBar = getSupportActionBar();

        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(enabled);
    }

    protected void showShortToast(String content) {
        Toast.makeText(this, content, Toast.LENGTH_SHORT).show();
    }

    protected void showLongToast(String content) {
        Toast.makeText(this, content, Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onHomeButtonPressed();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    protected void onHomeButtonPressed() {
        finish();
    }


}
