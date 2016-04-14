package com.erminesoft.motionview.motionview.ui;

import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;

import com.erminesoft.motionview.motionview.R;

public class ButtonsAnimationHelper {

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
        showTwitterButtonAnimation = AnimationUtils.loadAnimation(view.getContext(), R.anim.facebook_button_appearence);
        showGooglePlusButtonAnimation = AnimationUtils.loadAnimation(view.getContext(), R.anim.facebook_button_appearence);

        hideFacebookButtonAnimation = AnimationUtils.loadAnimation(view.getContext(), R.anim.facebook_button_hiding);
        hideTwitterButtonAnimation = AnimationUtils.loadAnimation(view.getContext(), R.anim.facebook_button_hiding);
        hideGooglePlusButtonAnimation = AnimationUtils.loadAnimation(view.getContext(), R.anim.facebook_button_hiding);
    }

    public void showAction(){
        if(isHided) {
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

    private void initButtonsForShowing(){
        showTwitterActionButtonInit();
        showFacebookActionButtonsInit();
        showGooglePlusActionButtonInit();
    }

    private void initButtonsForHiding(){
        hideFacebookActionButtonsInit();
        hideGoogleplusActionButtonsInit();
        hideTwitterActionButtonsInit();
    }

    private void showTwitterActionButtonInit(){
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) twitterActionButton.getLayoutParams();
        layoutParams.rightMargin += (int) (twitterActionButton.getWidth() * /*1.7*/ 3.5);
        layoutParams.bottomMargin += (int) (twitterActionButton.getHeight() * 0.75);
        twitterActionButton.setLayoutParams(layoutParams);
        twitterActionButton.setClickable(true);
    }

    private void showGooglePlusActionButtonInit(){
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) googlePlusActionButton.getLayoutParams();
        layoutParams.rightMargin += (int) (googlePlusActionButton.getWidth() * /*1.7*/ 2.5);
        layoutParams.bottomMargin += (int) (googlePlusActionButton.getHeight() * /*0.25*/ 0.50);
        googlePlusActionButton.setLayoutParams(layoutParams);
        googlePlusActionButton.setClickable(true);
    }

    private void showFacebookActionButtonsInit(){
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) facebookActionButton.getLayoutParams();
        layoutParams.rightMargin += (int) (facebookActionButton.getWidth() * 1.7);
        layoutParams.bottomMargin += (int) (facebookActionButton.getHeight() * 0.25);
        facebookActionButton.setLayoutParams(layoutParams);
        facebookActionButton.setClickable(true);
    }

    private void hideTwitterActionButtonsInit() {
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) twitterActionButton.getLayoutParams();
        layoutParams.rightMargin -= (int) (twitterActionButton.getWidth() * /*1.7*/ 3.5);
        layoutParams.bottomMargin -= (int) (twitterActionButton.getHeight() * /*0.25*/0.75);
        twitterActionButton.setLayoutParams(layoutParams);
        twitterActionButton.setClickable(false);
    }

    private void hideGoogleplusActionButtonsInit() {
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) googlePlusActionButton.getLayoutParams();
        layoutParams.rightMargin -= (int) (googlePlusActionButton.getWidth() * /*1.7*/ 2.5);
        layoutParams.bottomMargin -= (int) (googlePlusActionButton.getHeight() * /*0.25*/ 0.50);
        googlePlusActionButton.setLayoutParams(layoutParams);
        googlePlusActionButton.setClickable(false);
    }

    private void hideFacebookActionButtonsInit() {
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) facebookActionButton.getLayoutParams();
        layoutParams.rightMargin -= (int) (facebookActionButton.getWidth() * 1.7);
        layoutParams.bottomMargin -= (int) (facebookActionButton.getHeight() * 0.25);
        facebookActionButton.setLayoutParams(layoutParams);
        facebookActionButton.setClickable(false);
    }

    public void setActionCallback(FloatingActionButton.OnClickListener listener){
        commonActionButton.setOnClickListener(listener);
        facebookActionButton.setOnClickListener(listener);
        twitterActionButton.setOnClickListener(listener);
        googlePlusActionButton.setOnClickListener(listener);
    }
}
