package com.erminesoft.motionview.motionview.ui.fragments;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.erminesoft.motionview.motionview.ui.activities.GenericActivity;
import com.google.android.gms.common.GoogleApiAvailability;

public class ErrorDialogFragment extends android.support.v4.app.DialogFragment {
    public static final int REQUEST_RESOLVE_ERROR = 1001;
    public static final String DIALOG_ERROR = "dialog_error";

    public ErrorDialogFragment() {
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
        activity.getMVapplication().getGoogleClientHelper().onDialogDismissed();
    }
}
