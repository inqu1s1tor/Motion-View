package com.erminesoft.motionview.motionview.util;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;

public class DialogHelper extends AlertDialog.Builder{

    Context context;
    String message;
    String buttonText;

    public DialogHelper(Context context, String message ) {
        super(context);
        this.context = context;
        this.message = message;
    }

    public DialogHelper(Context context, int themeResId, String message ) {
        super(context, themeResId);
        this.context = context;
        this.message = message;

    }

    public void showAlertDialog(){
        this.setMessage(message);
        this.setTitle("Attention");
        this.create();
        this.show();
    }
}
