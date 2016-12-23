package com.licrafter.tagview;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import com.licrafter.tagview.utils.DipConvertUtils;
import com.licrafter.tagview.views.ITagView;

/**
 * author: shell
 * date 2016/12/20 下午2:24
 **/
public class TagViewGroup extends ViewGroup {

    public static final int DEFAULT_RADIUS = 8;//默认外圆半径
    public static final int DEFAULT_INNER_RADIUS = 4;//默认内圆半径
    public static final int DEFAULT_V_DISTANCE = 28;//默认竖直(上/下)方向线条长度
    public static final int DEFAULT_TILT_DISTANCE = 20;//默认斜线长度
    public static final int DEFAULT_BODER_WIDTH = 1;//默认线宽

    private Paint mPaint;
    private Path mPath;
    private Path mDstPath;
    private PathMeasure mPathMeasure;
    private Animator mShowAnimator;
    private Animator mHideAnimator;

    private Context mContext;
    private int mRadius;//外圆半径
    private int mInnerRadius;//内圆半径
    private int mTDistance;//斜线长度
    private int mVTagDistance;//竖直(上/下)方向线条长度
    private int[] mChildUsed;
    private int mCenterX;//圆心 X 坐标
    private int mCenterY;//圆心 Y 坐标
    private int mBoderWidth;//线条宽度
    private boolean mIsHiden;

    private float mLinesRatio = 1;

    public TagViewGroup(Context context) {
        this(context, null);
    }

    public TagViewGroup(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TagViewGroup(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        mRadius = DipConvertUtils.dip2px(mContext, DEFAULT_RADIUS);
        mInnerRadius = DipConvertUtils.dip2px(mContext, DEFAULT_INNER_RADIUS);
        mTDistance = DipConvertUtils.dip2px(mContext, DEFAULT_TILT_DISTANCE);
        mVTagDistance = DipConvertUtils.dip2px(mContext, DEFAULT_V_DISTANCE);
        mBoderWidth = DipConvertUtils.dip2px(mContext, DEFAULT_BODER_WIDTH);
        mPaint = new Paint();
        mPath = new Path();
        mDstPath = new Path();
        mPathMeasure = new PathMeasure();
        mPaint.setAntiAlias(true);
        mChildUsed = new int[4];
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        measureChildren(widthMeasureSpec, heightMeasureSpec);
        mChildUsed = getChildUsed();
        mCenterX = mChildUsed[0];
        mCenterY = mChildUsed[1];
        int widthSize = (mChildUsed[0] + mChildUsed[2]);
        int heightSize = (mChildUsed[1] + mChildUsed[3] + mBoderWidth);
        setMeasuredDimension(MeasureSpec.makeMeasureSpec(widthSize, MeasureSpec.AT_MOST), MeasureSpec.makeMeasureSpec(heightSize, MeasureSpec.AT_MOST));
    }

    /**
     * 获取中心圆上下左右各个方向的宽度
     *
     * @return int[]{left,top,right,bottom}
     */
    private int[] getChildUsed() {
        int childCount = getChildCount();
        int leftMax = mRadius * 2, topMax = mRadius * 2, rightMax = mRadius * 2, bottomMax = mRadius * 2;

        for (int i = 0; i < childCount; i++) {
            ITagView child = (ITagView) getChildAt(i);
            switch (child.getDirection()) {
                case RIGHT_TOP_TILT://右上斜线
                    rightMax = Math.max(rightMax, mTDistance + child.getMeasuredWidth());
                    topMax = Math.max(topMax, child.getMeasuredHeight() + mTDistance);
                    break;
                case RIGHT_TOP://右上
                    rightMax = Math.max(rightMax, child.getMeasuredWidth());
                    topMax = Math.max(topMax, child.getMeasuredHeight() + mVTagDistance);
                    break;
                case RIGHT_CENTER://右中
                    rightMax = Math.max(rightMax, child.getMeasuredWidth());
                    topMax = Math.max(topMax, Math.max(mVTagDistance, child.getMeasuredHeight()));
                    break;
                case RIGHT_BOTTOM://右下
                    rightMax = Math.max(rightMax, child.getMeasuredWidth());
                    bottomMax = mVTagDistance;
                    break;
                case RIGHT_BOTTOM_TILT:
                    rightMax = Math.max(rightMax, mTDistance + child.getMeasuredWidth());
                    bottomMax = mTDistance;
                    break;
                case LEFT_TOP://左上
                    leftMax = Math.max(leftMax, child.getMeasuredWidth());
                    topMax = Math.max(topMax, child.getMeasuredHeight() + mVTagDistance);
                    break;
                case LEFT_TOP_TILT://左上斜线
                    leftMax = Math.max(leftMax, child.getMeasuredWidth() + mTDistance);
                    topMax = Math.max(topMax, child.getMeasuredHeight() + mTDistance);
                    break;
                case LEFT_CENTER://左中
                    leftMax = Math.max(leftMax, child.getMeasuredWidth());
                    topMax = Math.max(topMax, Math.max(mVTagDistance, child.getMeasuredHeight()));
                    break;
                case LEFT_BOTTOM://左下
                    leftMax = Math.max(leftMax, child.getMeasuredWidth());
                    bottomMax = mVTagDistance;
                    break;
                case LEFT_BOTTOM_TILT://左下斜线
                    leftMax = Math.max(leftMax, child.getMeasuredWidth() + mTDistance);
                    bottomMax = mTDistance;
                    break;
            }

        }
        return new int[]{leftMax, topMax, rightMax, bottomMax};
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int left = 0, top = 0;
        for (int i = 0; i < getChildCount(); i++) {
            ITagView child = (ITagView) getChildAt(i);
            switch (child.getDirection()) {
                case RIGHT_TOP_TILT://右上斜线
                    top = 0;
                    left = mCenterX + mTDistance;
                    break;
                case RIGHT_TOP://右上
                    left = mCenterX;
                    top = 0;
                    break;
                case RIGHT_CENTER://右中
                    left = mCenterX;
                    top = mCenterY - child.getMeasuredHeight();
                    break;
                case RIGHT_BOTTOM://右下
                    left = mCenterX;
                    top = getMeasuredHeight() - child.getMeasuredHeight() - mBoderWidth;
                    break;
                case RIGHT_BOTTOM_TILT://右下斜线
                    left = mCenterX + mTDistance;
                    top = getMeasuredHeight() - child.getMeasuredHeight() - mBoderWidth;
                    break;
                case LEFT_TOP://左上
                    left = mCenterX - child.getMeasuredWidth();
                    top = 0;
                    break;
                case LEFT_TOP_TILT://左上斜线
                    left = mCenterX - child.getMeasuredWidth() - mTDistance;
                    top = 0;
                    break;
                case LEFT_CENTER://左中
                    left = mCenterX - child.getMeasuredWidth();
                    top = mCenterY - child.getMeasuredHeight();
                    break;
                case LEFT_BOTTOM://左下
                    left = mCenterX - child.getMeasuredWidth();
                    top = getMeasuredHeight() - child.getMeasuredHeight() - mBoderWidth;
                    break;
                case LEFT_BOTTOM_TILT://左下斜线
                    left = mCenterX - child.getMeasuredWidth() - mTDistance;
                    top = getMeasuredHeight() - child.getMeasuredHeight() - mBoderWidth;
                    break;
            }
            child.layout(left, top, left + child.getMeasuredWidth(), top + child.getMeasuredHeight());
        }
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        //绘制折线
        drawLines(canvas);
        //绘制内圆
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(Color.WHITE);
        canvas.drawCircle(mCenterX, mCenterY, mInnerRadius, mPaint);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(Color.parseColor("#30000000"));
        mPaint.setStrokeWidth(mRadius - mInnerRadius);
        //绘制外圆
        canvas.drawCircle(mCenterX, mCenterY, mInnerRadius + (mRadius - mInnerRadius) / 2, mPaint);
    }

    private void drawTagAlpha(float alpha) {
        for (int i = 0; i < getChildCount(); i++) {
            getChildAt(i).setAlpha(alpha);
        }
    }

    private void drawLines(Canvas canvas) {
        mPaint.setColor(Color.WHITE);
        mPaint.setStrokeWidth(mBoderWidth);
        mPaint.setStyle(Paint.Style.STROKE);

        for (int i = 0; i < getChildCount(); i++) {
            ITagView child = (ITagView) getChildAt(i);
            mPath.reset();
            mPath.moveTo(mCenterX, mCenterY);
            mDstPath.reset();
            mDstPath.moveTo(0, 0);
            switch (child.getDirection()) {
                case RIGHT_TOP://右上
                case RIGHT_BOTTOM://右下
                case RIGHT_TOP_TILT://右上斜线
                case RIGHT_BOTTOM_TILT://右下斜线
                    mPath.lineTo(child.getLeft(), child.getBottom());
                case RIGHT_CENTER://右中
                    mPath.lineTo(child.getRight(), child.getBottom());
                    break;
                case LEFT_TOP://左上
                case LEFT_TOP_TILT://左上斜线
                case LEFT_BOTTOM://左下
                case LEFT_BOTTOM_TILT://左下斜线
                    mPath.lineTo(child.getRight(), child.getBottom());
                case LEFT_CENTER://左中
                    mPath.lineTo(child.getLeft(), child.getBottom());
                    break;
            }
            mPathMeasure.setPath(mPath, false);
            mPathMeasure.getSegment(0, mPathMeasure.getLength() * mLinesRatio, mDstPath, true);
            canvas.drawPath(mDstPath, mPaint);
        }
    }

    public void showWithAnimation() {
        if (!checkAnimating()) {
            setVisibility(View.VISIBLE);
            mShowAnimator.start();
        }
    }

    public void hideWithAnimation() {
        if (!checkAnimating()) {
            mHideAnimator.start();
        }
    }

    private boolean checkAnimating() {
        return mShowAnimator == null || mHideAnimator == null
                || mShowAnimator.isRunning() || mHideAnimator.isRunning();
    }

    public TagViewGroup addTag(@NonNull ITagView tag) {
        addView((View) tag);
        return this;
    }

    public TagViewGroup setShowAnimator(Animator animator) {
        mShowAnimator = animator;
        mShowAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                setHiden(false);
            }
        });
        return this;
    }

    public TagViewGroup setHideAnimator(Animator animator) {
        mHideAnimator = animator;
        mHideAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                setVisibility(INVISIBLE);
                setHiden(true);
            }
        });
        return this;
    }

    public void refreshChildDirection(ITagView child) {
    }

    /**
     * 属性 CircleRadius 的属性动画调用，设置中心圆的半径
     */
    public void setCircleRadius(int radius) {
        mInnerRadius = radius;
        mRadius = mInnerRadius + DipConvertUtils.dip2px(mContext, 4);
        invalidate();
    }

    /**
     * 属性 LinesRatio 的属性动画调用，设置线条显示比例
     */
    public void setLinesRatio(float ratio) {
        mLinesRatio = ratio;
        invalidate();
    }

    /**
     * 属性 TagAlpha 的属性动画调用，设置Tag的透明度
     */
    public void setTagAlpha(float alpha) {
        drawTagAlpha(alpha);
    }


    public boolean isHiden() {
        return mIsHiden;
    }

    public void setHiden(boolean hiden) {
        mIsHiden = hiden;
    }
}
