package com.licrafter.tagview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.util.AttributeSet;
import android.view.ViewGroup;

import com.licrafter.tagview.utils.AnimatorUtils;
import com.licrafter.tagview.utils.DipConvertUtils;
import com.licrafter.tagview.views.TagView;

/**
 * author: shell
 * date 2016/12/20 下午2:24
 **/
public class TagViewGroup extends ViewGroup {

    public static final int DEFAULT_RADIUS = 8;
    public static final int DEFAULT_INNER_RADIUS = 4;
    public static final int DEFAULT_V_DISTANCE = 25;
    public static final int DEFAULT_H_DISTANCE = 0;
    public static final int DEFAULT_BODER_WIDTH = 1;

    private Paint mPaint;
    private Path mPath;
    private Path mDstPath;
    private PathMeasure mPathMeasure;

    private Context mContext;
    private int mRadius;
    private int mInnerRadius;
    private int mVTagDistance;//竖直方向上TagView与外圆边的距离
    private int mHTagDistance;//水平方向上TagView与外圆边的距离
    private int[] mChildUsed;
    private int mCenterX;
    private int mCenterY;
    private int mBoderWidth;
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
        mVTagDistance = DipConvertUtils.dip2px(mContext, DEFAULT_V_DISTANCE);
        mHTagDistance = DipConvertUtils.dip2px(mContext, DEFAULT_H_DISTANCE);
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
        android.util.Log.d("ljx", "onmeasure");
        measureChildren(widthMeasureSpec, heightMeasureSpec);
        mChildUsed = getChildUsed();
        mCenterX = mChildUsed[0] + mRadius;
        mCenterY = mChildUsed[1] + mRadius;
        int widthSize = (mChildUsed[0] + mChildUsed[2] + mRadius * 2);
        int heightSize = (mChildUsed[1] + mChildUsed[3] + mBoderWidth + mRadius * 2);
        setMeasuredDimension(MeasureSpec.makeMeasureSpec(widthSize, MeasureSpec.AT_MOST), MeasureSpec.makeMeasureSpec(heightSize, MeasureSpec.AT_MOST));
    }

    private int[] getChildUsed() {
        int childCount = getChildCount();
        int leftMax = 0, topMax = 0, rightMax = 0, bottomMax = 0;

        for (int i = 0; i < childCount; i++) {
            TagView child = (TagView) getChildAt(i);
            switch (child.getDirection()) {
                case RIGHT_TOP://右上
                case RIGHT_TOP_TILT://右上斜线
                    rightMax = Math.max(rightMax, child.getMeasuredWidth() + mHTagDistance);
                    topMax = Math.max(topMax, child.getMeasuredHeight() + mVTagDistance);
                    break;
                case RIGHT_CENTER://右中
                    rightMax = Math.max(rightMax, child.getMeasuredWidth() + mHTagDistance);
                    int diffRight = child.getMeasuredHeight() - mRadius;
                    topMax = Math.max(topMax, diffRight > 0 ? diffRight : 0);
                    break;
                case RIGHT_BOTTOM://右下
                case RIGHT_BOTTOM_TILT://右下斜线
                    rightMax = Math.max(rightMax, child.getMeasuredWidth() + mHTagDistance);
                    bottomMax = mVTagDistance;
                    break;
                case LEFT_TOP://左上
                case LEFT_TOP_TILT://左上斜线
                    leftMax = Math.max(leftMax, child.getMeasuredWidth() + mHTagDistance);
                    topMax = Math.max(topMax, child.getMeasuredHeight() + mVTagDistance);
                    break;
                case LEFT_CENTER://左中
                    leftMax = Math.max(leftMax, child.getMeasuredWidth() + mHTagDistance);
                    int diffLeft = child.getMeasuredHeight() - mRadius;
                    topMax = Math.max(topMax, diffLeft > 0 ? diffLeft : 0);
                    break;
                case LEFT_BOTTOM://左下
                case LEFT_BOTTOM_TILE://左下斜线
                    leftMax = Math.max(leftMax, child.getMeasuredWidth() + mHTagDistance);
                    bottomMax = mVTagDistance;
                    break;
            }
        }
        return new int[]{leftMax, topMax, rightMax, bottomMax};
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        android.util.Log.d("ljx", "onlayout");
        int rightChildStartX = mCenterX + mRadius + mHTagDistance;
        int leftChildEndX = mCenterX - mRadius - mHTagDistance;
        int left = 0, top = 0;
        for (int i = 0; i < getChildCount(); i++) {
            TagView child = (TagView) getChildAt(i);
            switch (child.getDirection()) {
                case RIGHT_TOP_TILT://右上斜线
                case RIGHT_TOP://右上
                    left = rightChildStartX;
                    top = 0;
                    break;
                case RIGHT_CENTER://右中
                    left = rightChildStartX;
                    top = mCenterY - child.getMeasuredHeight();
                    break;
                case RIGHT_BOTTOM://右下
                case RIGHT_BOTTOM_TILT://右下斜线
                    left = rightChildStartX;
                    top = getMeasuredHeight() - child.getMeasuredHeight();
                    break;
                case LEFT_TOP://左上
                case LEFT_TOP_TILT://左上斜线
                    left = leftChildEndX - child.getMeasuredWidth();
                    top = 0;
                    break;
                case LEFT_CENTER://左中
                    left = leftChildEndX - child.getMeasuredWidth();
                    top = mCenterY - child.getMeasuredHeight();
                    break;
                case LEFT_BOTTOM://左下
                case LEFT_BOTTOM_TILE://左下斜线
                    left = leftChildEndX - child.getMeasuredWidth();
                    top = getMeasuredHeight() - child.getMeasuredHeight();
                    break;
            }
            child.layout(left, top, left + child.getMeasuredWidth(), top + child.getMeasuredHeight());
        }
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        android.util.Log.d("ljx", "ondispatchdraw");
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
            TagView child = (TagView) getChildAt(i);
            mPath.reset();
            mPath.moveTo(mCenterX, mCenterY);
            mDstPath.reset();
            mDstPath.moveTo(0, 0);
            switch (child.getDirection()) {
                case RIGHT_TOP://右上
                    mPath.lineTo(mCenterX, child.getBottom());
                    mPath.lineTo(child.getRight(), child.getBottom());
                    break;
                case RIGHT_TOP_TILT:
                    mPath.lineTo(child.getLeft(), child.getBottom());
                    mPath.lineTo(child.getRight(), child.getBottom());
                    break;
                case RIGHT_CENTER://右中
                    mPath.lineTo(child.getRight(), mCenterY);
                    break;
                case RIGHT_BOTTOM://右下
                    mPath.lineTo(mCenterX, getMeasuredHeight() - mBoderWidth);
                    mPath.lineTo(child.getRight(), getMeasuredHeight() - mBoderWidth);
                    break;
                case RIGHT_BOTTOM_TILT://右下斜线
                    break;
                case LEFT_TOP://左上
                    mPath.lineTo(mCenterX, child.getMeasuredHeight());
                    mPath.lineTo(child.getLeft(), child.getMeasuredHeight());
                    break;
                case LEFT_TOP_TILT://左上斜线
                    break;
                case LEFT_CENTER://左中
                    mPath.lineTo(child.getLeft(), mCenterY);
                    break;
                case LEFT_BOTTOM://左下
                    mPath.lineTo(mCenterX, getMeasuredHeight() - mBoderWidth);
                    mPath.lineTo(child.getLeft(), getMeasuredHeight() - mBoderWidth);
                    break;
                case LEFT_BOTTOM_TILE://左下斜线
                    mPath.lineTo(child.getRight(), getMeasuredHeight() - mBoderWidth);
                    mPath.lineTo(child.getLeft(), getMeasuredHeight() - mBoderWidth);
                    break;
            }
            mPathMeasure.setPath(mPath, false);
            mPathMeasure.getSegment(0, mPathMeasure.getLength() * mLinesRatio, mDstPath, true);
            canvas.drawPath(mDstPath, mPaint);
        }
    }

    public void showWithAnimation() {
        AnimatorUtils.showTagGroup(this);
    }

    public void hideWithAnimation() {
        AnimatorUtils.hideTagGroup(this);
    }

    public TagViewGroup addTag(TagView tag) {
        addView(tag);
        return this;
    }

    public void refreshChildDirection(TagView child) {
    }

    public void setCircleRadius(int radius) {
        mInnerRadius = radius;
        mRadius = mInnerRadius + DipConvertUtils.dip2px(mContext, 4);
        invalidate();
    }

    public void setLinesRatio(float ratio) {
        mLinesRatio = ratio;
        invalidate();
    }

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
