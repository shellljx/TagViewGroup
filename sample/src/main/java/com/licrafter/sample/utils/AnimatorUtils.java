package com.licrafter.sample.utils;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
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
        ObjectAnimator linesAnimator = ObjectAnimator.ofFloat(target, TagViewGroup.LINES_RATIO, 1, 0);
        ObjectAnimator tagTextAnimator = ObjectAnimator.ofFloat(target, TagViewGroup.TAG_ALPHA, 1, 0);
        Animator circleAnimator = circleRadiusAnimator(target);
        together.playTogether(linesAnimator, tagTextAnimator);
        together.setDuration(400);
        together.setInterpolator(new DecelerateInterpolator());
        sequential.playSequentially(circleAnimator, together);
        return sequential;
    }

    private static Animator tagTextAnimator(TagViewGroup target) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(target, TagViewGroup.TAG_ALPHA, 0, 1);
        animator.setDuration(200);
        animator.setInterpolator(new DecelerateInterpolator());
        return animator;
    }

    private static Animator linesAnimator(TagViewGroup target) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(target, TagViewGroup.LINES_RATIO, 0, 1);
        animator.setDuration(300);
        animator.setInterpolator(new DecelerateInterpolator());
        return animator;
    }

    public static AnimatorSet circleRadiusAnimator(TagViewGroup target) {

        int radius = target.getCircleRadius();
        int innerRadius = target.getCircleInnerRadius();
        AnimatorSet set = new AnimatorSet();
        set.playTogether(ObjectAnimator.ofInt(target, TagViewGroup.CIRCLE_RADIUS,
                radius - 10, radius + 10, radius),
                ObjectAnimator.ofInt(target, TagViewGroup.CIRCLE_INNER_RADIUS, innerRadius - 10, innerRadius + 10, innerRadius));
        set.setDuration(400);
        return set;
    }
}
