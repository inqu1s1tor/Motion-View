package com.erminesoft.motionview.motionview.ui.fragments;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.widget.Toast;

import com.erminesoft.motionview.motionview.core.bridge.ActivityBridge;
import com.erminesoft.motionview.motionview.core.command.Commander;
import com.erminesoft.motionview.motionview.net.GoogleClientFacade;
import com.erminesoft.motionview.motionview.storage.SharedDataManager;

public abstract class GenericFragment extends Fragment {

    public static final String HISTORY = "history";
    public static final String MAP = "map";
    public static final String TODAY = "today";
    public static final String DAILY_STATISTIC = "daily statistic";

    protected final String TAG = this.getClass().getSimpleName();
    protected GoogleClientFacade mGoogleClientFacade;
    protected SharedDataManager mSharedDataManager;
    protected Commander mCommander;
    protected ActivityBridge mActivity;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        mActivity = (ActivityBridge) getActivity();
        mGoogleClientFacade = mActivity.getMVApplication().getGoogleClientFacade();
        mSharedDataManager = mActivity.getMVApplication().getSharedDataManager();
        mCommander = mActivity.getMVApplication().getCommander();
    }

    public void showShortToast(int resId) {
        showShortToast(getString(resId));
    }

    public void showShortToast(String content) {
        Toast.makeText(getContext(), content, Toast.LENGTH_SHORT).show();
    }

    public void showLongToast(String content) {
        Toast.makeText(getContext(), content, Toast.LENGTH_LONG).show();
    }

}
