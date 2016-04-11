package com.erminesoft.motionview.motionview.util;

import android.content.Context;
import android.graphics.Typeface;
import android.widget.TextView;

public class TypeFaceHelper {
    private static volatile TypeFaceHelper instance;

    private Context context;

    private TypeFaceHelper() {
    }

    public static TypeFaceHelper getInstance() {
        TypeFaceHelper temp = instance;
        if (temp == null) {
            temp = instance;
            synchronized (TypeFaceHelper.class) {
                if (temp == null) {
                    temp = instance = new TypeFaceHelper();
                }
            }
        }

        return temp;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public Context getContext() {
        return context;
    }

    public Typeface getTypeFace(String fontPath) {
        return Typeface.createFromAsset(context.getAssets(), fontPath);
    }

    public void applyTypeFace(TextView textView, String fontPath) {
        textView.setTypeface(getTypeFace(fontPath));
    }
}
