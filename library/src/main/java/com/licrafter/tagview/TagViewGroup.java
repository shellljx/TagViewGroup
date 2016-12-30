package com.licrafter.tagview;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.RectF;
import android.support.annotation.NonNull;
import android.support.v4.view.GestureDetectorCompat;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.licrafter.tagview.utils.DipConvertUtils;
import com.licrafter.tagview.views.ITagView;
import com.licrafter.tagview.views.RippleView;


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
    private GestureDetectorCompat mGestureDetector;
    private TagGroupClickListener mClickListener;

    private RippleView mRippleView;
    private int mRadius;//外圆半径
    private int mInnerRadius;//内圆半径
    private int mTDistance;//斜线长度
    private int mVDistance;//竖直(上/下)方向线条长度
    private RectF mCenterRect;
    private RectF[] mRectArray;
    private int[] mChildUsed;
    private int mTagCount;
    private int mCenterX;//圆心 X 坐标
    private int mCenterY;//圆心 Y 坐标
    private float mPercentX;
    private float mPercentY;
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
        mRadius = DipConvertUtils.dip2px(context, DEFAULT_RADIUS);
        mInnerRadius = DipConvertUtils.dip2px(context, DEFAULT_INNER_RADIUS);
        mTDistance = DipConvertUtils.dip2px(context, DEFAULT_TILT_DISTANCE);
        mVDistance = DipConvertUtils.dip2px(context, DEFAULT_V_DISTANCE);
        mBoderWidth = DipConvertUtils.dip2px(context, DEFAULT_BODER_WIDTH);
        mPaint = new Paint();
        mPath = new Path();
        mDstPath = new Path();
        mPathMeasure = new PathMeasure();
        mPaint.setAntiAlias(true);
        mGestureDetector = new GestureDetectorCompat(context, new TagOnGestureListener());
        mChildUsed = new int[4];
        mCenterRect = new RectF();
        mRectArray = new RectF[6];
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        measureChildren(widthMeasureSpec, heightMeasureSpec);
        mChildUsed = getChildUsed();
        //园中心默认在左上角 (0,0)
        mCenterX = (int) (getMeasuredWidth() * mPercentX);
        mCenterY = (int) (getMeasuredHeight() * mPercentY);
        mCenterRect.set(mCenterX - mRadius, mCenterY - mRadius, mCenterX + mRadius, mCenterY + mRadius);
        if (mRippleView != null) {
            mRippleView.setCenterPoint(mCenterX, mCenterY);
        }
    }

    /**
     * 获取中心圆上下左右各个方向的宽度
     *
     * @return int[]{left,top,right,bottom}
     */
    private int[] getChildUsed() {
        int childCount = getChildCount();
        int leftMax = mVDistance, topMax = mVDistance, rightMax = mVDistance, bottomMax = mVDistance;

        for (int i = 0; i < childCount; i++) {
            ITagView child = (ITagView) getChildAt(i);
            switch (child.getDirection()) {
                case RIGHT_TOP_TILT://右上斜线
                    rightMax = Math.max(rightMax, mTDistance + child.getMeasuredWidth());
                    topMax = Math.max(topMax, child.getMeasuredHeight() + mTDistance);
                    break;
                case RIGHT_TOP://右上
                    rightMax = Math.max(rightMax, child.getMeasuredWidth());
                    topMax = Math.max(topMax, child.getMeasuredHeight() + mVDistance);
                    break;
                case RIGHT_CENTER://右中
                    rightMax = Math.max(rightMax, child.getMeasuredWidth());
                    topMax = Math.max(topMax, Math.max(mVDistance, child.getMeasuredHeight()));
                    break;
                case RIGHT_BOTTOM://右下
                    rightMax = Math.max(rightMax, child.getMeasuredWidth());
                    bottomMax = mVDistance;
                    break;
                case RIGHT_BOTTOM_TILT:
                    rightMax = Math.max(rightMax, mTDistance + child.getMeasuredWidth());
                    bottomMax = mTDistance;
                    break;
                case LEFT_TOP://左上
                    leftMax = Math.max(leftMax, child.getMeasuredWidth());
                    topMax = Math.max(topMax, child.getMeasuredHeight() + mVDistance);
                    break;
                case LEFT_TOP_TILT://左上斜线
                    leftMax = Math.max(leftMax, child.getMeasuredWidth() + mTDistance);
                    topMax = Math.max(topMax, child.getMeasuredHeight() + mTDistance);
                    break;
                case LEFT_CENTER://左中
                    leftMax = Math.max(leftMax, child.getMeasuredWidth());
                    topMax = Math.max(topMax, Math.max(mVDistance, child.getMeasuredHeight()));
                    break;
                case LEFT_BOTTOM://左下
                    leftMax = Math.max(leftMax, child.getMeasuredWidth());
                    bottomMax = mVDistance;
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
                    top = mCenterY - mTDistance - child.getMeasuredHeight();
                    left = mCenterX + mTDistance;
                    break;
                case RIGHT_TOP://右上
                    left = mCenterX;
                    top = mCenterY - mVDistance - child.getMeasuredHeight();
                    break;
                case RIGHT_CENTER://右中
                    left = mCenterX;
                    top = mCenterY - child.getMeasuredHeight();
                    break;
                case RIGHT_BOTTOM://右下
                    left = mCenterX;
                    top = mVDistance + mCenterY - child.getMeasuredHeight();
                    break;
                case RIGHT_BOTTOM_TILT://右下斜线
                    left = mCenterX + mTDistance;
                    top = mTDistance + mCenterY - child.getMeasuredHeight();
                    break;
                case LEFT_TOP://左上
                    left = mCenterX - child.getMeasuredWidth();
                    top = mCenterY - mVDistance - child.getMeasuredHeight();
                    break;
                case LEFT_TOP_TILT://左上斜线
                    left = mCenterX - child.getMeasuredWidth() - mTDistance;
                    top = mCenterY - mTDistance - child.getMeasuredHeight();
                    break;
                case LEFT_CENTER://左中
                    left = mCenterX - child.getMeasuredWidth();
                    top = mCenterY - child.getMeasuredHeight();
                    break;
                case LEFT_BOTTOM://左下
                    left = mCenterX - child.getMeasuredWidth();
                    top = mVDistance + mCenterY - child.getMeasuredHeight();
                    break;
                case LEFT_BOTTOM_TILT://左下斜线
                    left = mCenterX - child.getMeasuredWidth() - mTDistance;
                    top = mTDistance + mCenterY - child.getMeasuredHeight();
                    break;
                case CENTER:
                    left = 0;
                    top = 0;
                    break;
            }
            child.layout(left, top, left + child.getMeasuredWidth(), top + child.getMeasuredHeight());
        }
        refreshTagsRect();
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
        View tagView = ((View) tag);
        tagView.setTag(mTagCount);
        mRectArray[mTagCount] = new RectF();
        addView(tagView);
        mTagCount++;
        return this;
    }

    public void addRipple() {
        mRippleView = new RippleView(getContext());
        mRippleView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        mRippleView.setDirection(DIRECTION.CENTER);
        addView(mRippleView);
    }

    private void refreshTagsRect() {
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            if (((ITagView) child).getDirection() != DIRECTION.CENTER) {
                mRectArray[(int) child.getTag()].set(child.getLeft(), child.getTop(), child.getRight(), child.getBottom());
            }
        }
    }

    private boolean isTouchingTags(float x, float y) {
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            if (((ITagView) child).getDirection() != DIRECTION.CENTER && mRectArray[(int) child.getTag()].contains(x, y)) {
                return true;
            }
        }
        return false;
    }

    //设置中心圆点坐标占整个 ViewGroup 的比例

    public void setPercent(float percentX, float percentY) {
        mPercentX = percentX;
        mPercentY = percentY;
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

    /**
     * 属性 CircleRadius 的属性动画调用，设置中心圆的半径
     */
    @SuppressWarnings("unused")
    public void setCircleRadius(int radius) {
        mInnerRadius = radius;
        mRadius = mInnerRadius + DipConvertUtils.dip2px(getContext(), 4);
        invalidate();
    }

    /**
     * 属性 LinesRatio 的属性动画调用，设置线条显示比例
     */
    @SuppressWarnings("unused")
    public void setLinesRatio(float ratio) {
        mLinesRatio = ratio;
        invalidate();
    }

    /**
     * 属性 TagAlpha 的属性动画调用，设置 Tag 的透明度
     */
    @SuppressWarnings("unused")
    public void setTagAlpha(float alpha) {
        drawTagAlpha(alpha);
    }


    public boolean isHiden() {
        return mIsHiden;
    }

    public void setHiden(boolean hiden) {
        mIsHiden = hiden;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mClickListener != null) {
            return mGestureDetector.onTouchEvent(event);
        }
        return super.onTouchEvent(event);
    }

    public void setTagGroupClickListener(TagGroupClickListener listener) {
        mClickListener = listener;
    }

    public interface TagGroupClickListener {

        //TagGroup 中心圆点被点击
        void onCircleClicked();

        //TagGroup Tag子view被点击
        void onTagsClicked();

        //TagGroup 被长按
        void onLongPress();
    }

    //内部处理 touch 事件监听器
    private class TagOnGestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onDown(MotionEvent e) {
            if (mClickListener != null) {
                float x = e.getX();
                float y = e.getY();
                if (mCenterRect.contains(x, y) || isTouchingTags(x, y)) {
                    return true;
                }
            }
            return super.onDown(e);
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            float x = e.getX();
            float y = e.getY();

            if (mCenterRect.contains(x, y)) {
                mClickListener.onCircleClicked();
            } else {
                mClickListener.onTagsClicked();
            }
            return true;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            float currentX = mCenterX - distanceX;
            float currentY = mCenterY - distanceY;
            currentX = Math.min(Math.max(currentX, mChildUsed[0]), getMeasuredWidth() - mChildUsed[2]);
            currentY = Math.min(Math.max(currentY, mChildUsed[1]), getMeasuredHeight() - mChildUsed[3]);
            mPercentX = currentX / getMeasuredWidth();
            mPercentY = currentY / getMeasuredHeight();
            requestLayout();
            return true;
        }

        @Override
        public void onLongPress(MotionEvent e) {
            super.onLongPress(e);
            float x = e.getX();
            float y = e.getY();
            if (mCenterRect.contains(x, y) || isTouchingTags(x, y)) {
                mClickListener.onLongPress();
            }
        }
    }
}
