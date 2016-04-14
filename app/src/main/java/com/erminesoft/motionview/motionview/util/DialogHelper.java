package com.erminesoft.motionview.motionview.util;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;

public class DialogHelper {

    public static AlertDialog.Builder createAlertDialog(Context context, String message) {
        return new AlertDialog.Builder(context)
                .setTitle("Attention")
                .setMessage(message);
    }

    public static ProgressDialog createProgressDialog(Context context) {
        ProgressDialog dialog = new ProgressDialog(context);
        dialog.setMessage("Loading...");
        dialog.setCancelable(false);

        return dialog;
    }
}
