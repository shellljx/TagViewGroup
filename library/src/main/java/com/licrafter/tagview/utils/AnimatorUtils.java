package com.licrafter.tagview.utils;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.view.animation.DecelerateInterpolator;

import com.licrafter.tagview.TagViewGroup;

/**
 * author: shell
 * date 2016/12/22 下午12:53
 **/
public class AnimatorUtils {

    public static void showTagGroup(final TagViewGroup target) {
        target.setAlpha(1);
        Animator circleAnimator = circleRadiusAnimator(target);
        final Animator linesAnimator = linesAnimator(target);
        final Animator alphaAnimator = showTagTextAnimator(target);
        circleAnimator.start();
        circleAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                linesAnimator.start();
            }

            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
            }
        });
        linesAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                alphaAnimator.start();
            }
        });
        alphaAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                target.setHiden(false);
            }
        });
    }

    public static void hideTagGroup(final TagViewGroup target) {
        final AnimatorSet set = new AnimatorSet();
        ObjectAnimator linesAnimator = ObjectAnimator.ofFloat(target, "LinesRatio", 1, 0);
        ObjectAnimator tagTextAnimator = ObjectAnimator.ofFloat(target, "TagAlpha", 1, 0);
        Animator circleAnimator = circleRadiusAnimator(target);
        set.playTogether(linesAnimator, tagTextAnimator);
        set.setDuration(500);
        set.setTarget(target);
        set.setInterpolator(new DecelerateInterpolator());
        circleAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                set.start();
            }
        });
        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                target.setAlpha(0);
                target.setHiden(true);
            }
        });
        circleAnimator.start();
    }

    public static Animator showTagTextAnimator(TagViewGroup target) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(target, "TagAlpha", 0, 1);
        animator.setDuration(200);
        animator.setInterpolator(new DecelerateInterpolator());
        return animator;
    }

    public static Animator linesAnimator(TagViewGroup target) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(target, "LinesRatio", 0, 1);
        animator.setDuration(500);
        animator.setInterpolator(new DecelerateInterpolator());
        return animator;
    }

    public static Animator circleRadiusAnimator(TagViewGroup target) {
        int defaultRadius = DipConvertUtils.dip2px(target.getContext(), TagViewGroup.DEFAULT_INNER_RADIUS);
        ObjectAnimator animator = ObjectAnimator.ofInt(target, "CircleRadius",
                defaultRadius - 10, defaultRadius + 10, defaultRadius);
        animator.setDuration(400);
        return animator;
    }
}
