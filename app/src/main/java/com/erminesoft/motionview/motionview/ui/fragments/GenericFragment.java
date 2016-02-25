package com.erminesoft.motionview.motionview.ui.fragments;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.widget.Toast;

import com.erminesoft.motionview.motionview.bridge.ActivityBridge;
import com.erminesoft.motionview.motionview.net.GoogleClientFacade;

public abstract class GenericFragment extends Fragment {
    protected final String TAG = this.getClass().getSimpleName();
    protected GoogleClientFacade mGoogleClientFacade;
    protected ActivityBridge mActivity;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        mActivity = (ActivityBridge) getActivity();
        mGoogleClientFacade = mActivity.getMVApplication().getGoogleClientFacade();
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
