package com.erminesoft.motionview.motionview.ui.fragments;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;

import com.erminesoft.motionview.motionview.ui.activities.GenericActivity;
import com.google.android.gms.common.GoogleApiAvailability;

public class ErrorDialogFragment extends android.support.v4.app.DialogFragment {
    public static final int REQUEST_RESOLVE_ERROR = 1001;
    private static final String DIALOG_ERROR = "dialog_error";

    public ErrorDialogFragment() {
    }

    public static void showErrorDialog(FragmentActivity fragmentActivity, int errorCode) {
        ErrorDialogFragment dialogFragment = new ErrorDialogFragment();
        Bundle args = new Bundle();
        args.putInt(DIALOG_ERROR, errorCode);
        dialogFragment.setArguments(args);
        dialogFragment.show(fragmentActivity.getSupportFragmentManager(), DIALOG_ERROR);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        int errorCode = this.getArguments().getInt(DIALOG_ERROR);
        return GoogleApiAvailability.getInstance().getErrorDialog(
                this.getActivity(), errorCode, REQUEST_RESOLVE_ERROR);
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        GenericActivity activity = (GenericActivity) getActivity();
        activity.getMVApplication().getGoogleFitnessFacade().onDialogDismissed();
    }
}
