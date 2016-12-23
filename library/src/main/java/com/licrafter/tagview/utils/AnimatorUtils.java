package com.licrafter.tagview.utils;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import com.licrafter.tagview.TagViewGroup;

/**
 * author: shell
 * date 2016/12/22 下午12:53
 **/
public class AnimatorUtils {

    public static Animator getTagShowAnimator(final TagViewGroup target) {
        AnimatorSet set = new AnimatorSet();
        set.playSequentially(circleRadiusAnimator(target), linesAnimator(target), tagTextAnimator(target));
        return set;
    }

    public static Animator getTagHideAnimator(final TagViewGroup target) {
        AnimatorSet together = new AnimatorSet();
        AnimatorSet sequential = new AnimatorSet();
        ObjectAnimator linesAnimator = ObjectAnimator.ofFloat(target, "LinesRatio", 1, 0);
        ObjectAnimator tagTextAnimator = ObjectAnimator.ofFloat(target, "TagAlpha", 1, 0);
        Animator circleAnimator = circleRadiusAnimator(target);
        together.playTogether(linesAnimator, tagTextAnimator);
        together.setDuration(400);
        together.setInterpolator(new DecelerateInterpolator());
        sequential.playSequentially(circleAnimator, together);
        return sequential;
    }

    private static Animator tagTextAnimator(TagViewGroup target) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(target, "TagAlpha", 0, 1);
        animator.setDuration(200);
        animator.setInterpolator(new DecelerateInterpolator());
        return animator;
    }

    private static Animator linesAnimator(TagViewGroup target) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(target, "LinesRatio", 0, 1);
        animator.setDuration(300);
        animator.setInterpolator(new DecelerateInterpolator());
        return animator;
    }

    private static Animator circleRadiusAnimator(TagViewGroup target) {
        int defaultRadius = DipConvertUtils.dip2px(target.getContext(), TagViewGroup.DEFAULT_INNER_RADIUS);
        ObjectAnimator animator = ObjectAnimator.ofInt(target, "CircleRadius",
                defaultRadius - 10, defaultRadius + 10, defaultRadius);
        animator.setDuration(400);
        return animator;
    }
}
