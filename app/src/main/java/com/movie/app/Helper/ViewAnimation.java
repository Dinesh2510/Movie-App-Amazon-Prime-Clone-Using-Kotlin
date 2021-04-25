package com.movie.app.Helper;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.Transformation;

public class ViewAnimation {

    public static void expand(final View v, final AnimListener animListener) {
        Animation a = expandAction(v);
        a.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                animListener.onFinish();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        v.startAnimation(a);
    }


    private static Animation expandAction(final View v) {
        v.measure(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        final int targtetHeight = v.getMeasuredHeight();

        v.getLayoutParams().height = 0;
        v.setVisibility(View.VISIBLE);
        Animation a = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                v.getLayoutParams().height = interpolatedTime == 1
                        ? LayoutParams.WRAP_CONTENT
                        : (int) (targtetHeight * interpolatedTime);
                v.requestLayout();
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        a.setDuration((int) (targtetHeight / v.getContext().getResources().getDisplayMetrics().density));
        v.startAnimation(a);
        return a;
    }

    public static void collapse(final View v) {
        final int initialHeight = v.getMeasuredHeight();

        Animation a = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                if (interpolatedTime == 1) {
                    v.setVisibility(View.GONE);
                } else {
                    v.getLayoutParams().height = initialHeight - (int) (initialHeight * interpolatedTime);
                    v.requestLayout();
                }
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        a.setDuration((int) (initialHeight / v.getContext().getResources().getDisplayMetrics().density));
        v.startAnimation(a);
    }


    public static void flyOutDown(final View v, final AnimListener animListener) {
        v.setVisibility(View.VISIBLE);
        v.setAlpha(1.0f);
        v.setTranslationY(0);
        // Prepare the View for the animation
        v.animate()
                .setDuration(200)
                .translationY(v.getHeight())
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        if (animListener != null) animListener.onFinish();
                        super.onAnimationEnd(animation);
                    }
                })
                .alpha(0.0f)
                .start();
    }

    public static void fadeIn(final View v) {
        ViewAnimation.fadeIn(v, null);
    }

    public static void fadeIn(final View v, final AnimListener animListener) {
        v.setVisibility(View.GONE);
        v.setAlpha(0.0f);
        // Prepare the View for the animation
        v.animate()
                .setDuration(200)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        v.setVisibility(View.VISIBLE);
                        if (animListener != null) animListener.onFinish();
                        super.onAnimationEnd(animation);
                    }
                })
                .alpha(1.0f);
    }

    public static void fadeOut(final View v) {
        ViewAnimation.fadeOut(v, null);
    }

    public static void fadeOut(final View v, final AnimListener animListener) {
        v.setAlpha(1.0f);
        // Prepare the View for the animation
        v.animate()
                .setDuration(500)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        if (animListener != null) animListener.onFinish();
                        super.onAnimationEnd(animation);
                    }
                })
                .alpha(0.0f);
    }

    public interface AnimListener {
        void onFinish();
    }

}
