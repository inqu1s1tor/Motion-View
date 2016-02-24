package com.erminesoft.motionview.motionview.ui.fragments;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.widget.Toast;

import com.erminesoft.motionview.motionview.net.GoogleClientFacade;
import com.erminesoft.motionview.motionview.ui.activities.GenericActivity;

public class GenericFragment extends Fragment {
    protected final String TAG = this.getClass().getSimpleName();
    protected GoogleClientFacade mGoogleClientFacade;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        GenericActivity activity = (GenericActivity) getActivity();
        mGoogleClientFacade = activity.getMVapplication().getGoogleClientFacade();
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
