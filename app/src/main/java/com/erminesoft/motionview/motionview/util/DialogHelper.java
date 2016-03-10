package com.erminesoft.motionview.motionview.util;

import android.app.AlertDialog;
import android.content.Context;

public class DialogHelper extends AlertDialog.Builder{
    private final String message;

    public DialogHelper(Context context, String message ) {
        super(context);
        this.message = message;
    }

    public void showAlertDialog(){
        this.setMessage(message);
        this.setTitle("Attention");
        this.create();
        this.show();
    }
}
