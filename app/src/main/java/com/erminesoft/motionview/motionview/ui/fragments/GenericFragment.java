package com.erminesoft.motionview.motionview.ui.fragments;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.widget.Toast;

import com.erminesoft.motionview.motionview.core.bridge.ActivityBridge;
import com.erminesoft.motionview.motionview.core.command.Commander;
import com.erminesoft.motionview.motionview.net.fitness.GoogleFitnessFacade;
import com.erminesoft.motionview.motionview.storage.SharedDataManager;

@SuppressWarnings("WeakerAccess")
public abstract class GenericFragment extends Fragment {

    public static final String HISTORY = "history";
    public static final String MAP = "map";
    public static final String TODAY = "today";
    public static final String DAILY_STATISTIC = "daily statistic";

    protected final String TAG = this.getClass().getSimpleName();
    protected GoogleFitnessFacade mGoogleFitnessFacade;
    protected SharedDataManager mSharedDataManager;
    protected Commander mCommander;
    protected ActivityBridge mActivity;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        mActivity = (ActivityBridge) getActivity();
        mGoogleFitnessFacade = mActivity.getMVApplication().getGoogleFitnessFacade();
        mSharedDataManager = mActivity.getMVApplication().getSharedDataManager();
        mCommander = mActivity.getMVApplication().getCommander();
    }

    public void showShortToast(String content) {
        Toast.makeText(getContext(), content, Toast.LENGTH_SHORT).show();
    }
}
