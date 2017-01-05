package com.licrafter.tagview.views;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateInterpolator;

import com.licrafter.tagview.DIRECTION;
import com.licrafter.tagview.utils.DipConvertUtils;

/**
 * author: shell
 * date 2016/12/25 下午7:18
 **/
public class RippleView extends View implements ITagView {

    private int mRadius;
    private int mAlpha;
    private DIRECTION mDirection;
    private Paint mPaint;
    private AnimatorSet mAnimator;
    private int mX, mY;

    public RippleView(Context context) {
        this(context, null);
    }

    public RippleView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RippleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mPaint = new Paint();
        mPaint.setColor(Color.WHITE);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mPaint.setAlpha(mAlpha);
        canvas.drawCircle(mX, mY, mRadius, mPaint);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        stopRipple();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        startRipple();
    }

    @Override
    public void setDirection(DIRECTION direction) {
        mDirection = direction;
    }

    @Override
    public DIRECTION getDirection() {
        return mDirection;
    }

    public void startRipple() {
        mAnimator.start();
    }

    public void stopRipple() {
        mAnimator.end();
    }

    //设置水波纹半径
    public void setRippleRadius(int radius) {
        mRadius = radius;
        invalidate();
    }

    //设置水波纹 alpha 范围[0-255]
    public void setRippleAlpha(int alpha) {
        mAlpha = alpha;
        invalidate();
    }

    public void setCenterPoint(int x, int y) {
        mX = x;
        mY = y;
    }

    public void initAnimator(int minRadius, int maxRadius, int alpha) {
        ObjectAnimator radiusAnimator = ObjectAnimator.ofInt(this, "RippleRadius", minRadius, maxRadius);
        radiusAnimator.setRepeatMode(ValueAnimator.RESTART);
        radiusAnimator.setRepeatCount(ValueAnimator.INFINITE);
        ObjectAnimator alphaAnimator = ObjectAnimator.ofInt(this, "RippleAlpha", alpha, 0);
        alphaAnimator.setRepeatMode(ValueAnimator.RESTART);
        alphaAnimator.setRepeatCount(ValueAnimator.INFINITE);
        mAnimator = new AnimatorSet();
        mAnimator.playTogether(radiusAnimator, alphaAnimator);
        mAnimator.setDuration(1000);
        mAnimator.setInterpolator(new AccelerateInterpolator());
    }
}
