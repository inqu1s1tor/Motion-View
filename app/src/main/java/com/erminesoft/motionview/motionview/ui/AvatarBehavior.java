package com.erminesoft.motionview.motionview.ui;

import android.content.Context;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import com.pkmmte.view.CircularImageView;

public class AvatarBehavior extends CoordinatorLayout.Behavior<CircularImageView> {

    private float mStartBottomPos;
    private int mStartHeight;

    public AvatarBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, CircularImageView child, View dependency) {
        return dependency instanceof AppBarLayout;
    }

    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, CircularImageView child, View dependency) {
        if (mStartBottomPos == 0) {
            mStartBottomPos = dependency.getBottom();
            mStartHeight = child.getHeight();
        }

        float scaleFactor = Math.abs((dependency.getBottom() / mStartBottomPos) - 1);

        if (scaleFactor <= 0.4) {
            ViewGroup.LayoutParams layoutParams = child.getLayoutParams();
            layoutParams.height = (int) (mStartHeight - (mStartHeight * scaleFactor));
            layoutParams.width = (int) (mStartHeight - (mStartHeight * scaleFactor));

            child.setLayoutParams(layoutParams);
        }

        int currentY = dependency.getBottom() - child.getHeight() - 15;
        int currentX = dependency.getWidth() - child.getWidth() - 15;

        child.setY(currentY);
        child.setX(currentX);

        return true;
    }
}
