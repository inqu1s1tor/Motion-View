package com.erminesoft.motionview.motionview.ui;

import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;

import com.erminesoft.motionview.motionview.R;

public class ButtonsAnimationHelper {

    private static final float FACEBOOK_X_MARGIN_SCALE = 1f;
    private static final float FACEBOOK_Y_MARGIN_SCALE = 0.8f;

    private static final float TWITTER_X_MARGIN_SCALE = 0.85f;
    private static final float TWITTER_Y_MARGIN_SCALE = 1.5f;

    private static final float GOOGLE_PLUS_X_MARGIN_SCALE = 0.15f;
    private static final float GOOGLE_PLUS_Y_MARGIN_SCALE = 1.85f;

    private FloatingActionButton commonActionButton;
    private FloatingActionButton facebookActionButton;
    private FloatingActionButton twitterActionButton;
    private FloatingActionButton googlePlusActionButton;

    private Animation showFacebookButtonAnimation;
    private Animation hideFacebookButtonAnimation;

    private Animation showTwitterButtonAnimation;
    private Animation hideTwitterButtonAnimation;

    private Animation showGooglePlusButtonAnimation;
    private Animation hideGooglePlusButtonAnimation;

    private boolean isHided = true;

    public ButtonsAnimationHelper(View view, FloatingActionButton commonButton) {
        commonActionButton = commonButton;
        facebookActionButton = (FloatingActionButton) view.findViewById(R.id.facebookActionButton);
        twitterActionButton = (FloatingActionButton) view.findViewById(R.id.twitterActionButton);
        googlePlusActionButton = (FloatingActionButton) view.findViewById(R.id.googlePlusActionButton);

        showFacebookButtonAnimation = AnimationUtils.loadAnimation(view.getContext(), R.anim.facebook_button_appearence);
        showTwitterButtonAnimation = AnimationUtils.loadAnimation(view.getContext(), R.anim.twitter_button_appearence);
        showGooglePlusButtonAnimation = AnimationUtils.loadAnimation(view.getContext(), R.anim.google_plus_button_appearence);

        hideFacebookButtonAnimation = AnimationUtils.loadAnimation(view.getContext(), R.anim.facebook_button_hiding);
        hideTwitterButtonAnimation = AnimationUtils.loadAnimation(view.getContext(), R.anim.twitter_button_hiding);
        hideGooglePlusButtonAnimation = AnimationUtils.loadAnimation(view.getContext(), R.anim.google_plus_button_hiding);
    }

    public void showAction() {
        if (isHided) {
            isHided = false;
            initButtonsForShowing();
            facebookActionButton.startAnimation(showFacebookButtonAnimation);
            twitterActionButton.startAnimation(showTwitterButtonAnimation);
            googlePlusActionButton.startAnimation(showGooglePlusButtonAnimation);
            commonActionButton.setImageResource(R.drawable.close);
        } else {
            isHided = true;
            initButtonsForHiding();
            facebookActionButton.startAnimation(hideFacebookButtonAnimation);
            twitterActionButton.startAnimation(hideTwitterButtonAnimation);
            googlePlusActionButton.startAnimation(hideGooglePlusButtonAnimation);
            commonActionButton.setImageResource(R.drawable.share);
        }
    }

    private void initButtonsForShowing() {
        showTwitterActionButtonInit();
        showFacebookActionButtonsInit();
        showGooglePlusActionButtonInit();
    }

    private void initButtonsForHiding() {
        hideFacebookActionButtonsInit();
        hideGoogleplusActionButtonsInit();
        hideTwitterActionButtonsInit();
    }

    private void showTwitterActionButtonInit() {
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) twitterActionButton.getLayoutParams();
        layoutParams.rightMargin += (int) (twitterActionButton.getWidth() * TWITTER_X_MARGIN_SCALE);
        layoutParams.bottomMargin += (int) (twitterActionButton.getHeight() * TWITTER_Y_MARGIN_SCALE);
        twitterActionButton.setLayoutParams(layoutParams);
        twitterActionButton.setClickable(true);
    }

    private void showGooglePlusActionButtonInit() {
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) googlePlusActionButton.getLayoutParams();
        layoutParams.rightMargin += (int) (googlePlusActionButton.getWidth() * GOOGLE_PLUS_X_MARGIN_SCALE);
        layoutParams.bottomMargin += (int) (googlePlusActionButton.getHeight() * GOOGLE_PLUS_Y_MARGIN_SCALE);
        googlePlusActionButton.setLayoutParams(layoutParams);
        googlePlusActionButton.setClickable(true);
    }

    private void showFacebookActionButtonsInit() {
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) facebookActionButton.getLayoutParams();
        layoutParams.rightMargin += (int) (facebookActionButton.getWidth() * FACEBOOK_X_MARGIN_SCALE);
        layoutParams.bottomMargin += (int) (facebookActionButton.getHeight() * FACEBOOK_Y_MARGIN_SCALE);
        facebookActionButton.setLayoutParams(layoutParams);
        facebookActionButton.setClickable(true);
    }

    private void hideTwitterActionButtonsInit() {
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) twitterActionButton.getLayoutParams();
        layoutParams.rightMargin -= (int) (twitterActionButton.getWidth() * TWITTER_X_MARGIN_SCALE);
        layoutParams.bottomMargin -= (int) (twitterActionButton.getHeight() * TWITTER_Y_MARGIN_SCALE);
        twitterActionButton.setLayoutParams(layoutParams);
        twitterActionButton.setClickable(false);
    }

    private void hideGoogleplusActionButtonsInit() {
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) googlePlusActionButton.getLayoutParams();
        layoutParams.rightMargin -= (int) (googlePlusActionButton.getWidth() * GOOGLE_PLUS_X_MARGIN_SCALE);
        layoutParams.bottomMargin -= (int) (googlePlusActionButton.getHeight() * GOOGLE_PLUS_Y_MARGIN_SCALE);
        googlePlusActionButton.setLayoutParams(layoutParams);
        googlePlusActionButton.setClickable(false);
    }

    private void hideFacebookActionButtonsInit() {
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) facebookActionButton.getLayoutParams();
        layoutParams.rightMargin -= (int) (facebookActionButton.getWidth() * FACEBOOK_X_MARGIN_SCALE);
        layoutParams.bottomMargin -= (int) (facebookActionButton.getHeight() * FACEBOOK_Y_MARGIN_SCALE);
        facebookActionButton.setLayoutParams(layoutParams);
        facebookActionButton.setClickable(false);
    }

    public void setActionCallback(FloatingActionButton.OnClickListener listener) {
        commonActionButton.setOnClickListener(listener);
        facebookActionButton.setOnClickListener(listener);
        twitterActionButton.setOnClickListener(listener);
        googlePlusActionButton.setOnClickListener(listener);
    }
}
