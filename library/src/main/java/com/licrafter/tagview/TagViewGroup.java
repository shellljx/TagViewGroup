package com.licrafter.tagview;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.RectF;
import android.support.annotation.NonNull;
import android.support.v4.view.GestureDetectorCompat;
import android.util.AttributeSet;
import android.util.Property;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.licrafter.tagview.utils.DipConvertUtils;
import com.licrafter.tagview.views.ITagView;
import com.licrafter.tagview.views.RippleView;

import java.util.ArrayList;
import java.util.List;


/**
 * author: shell
 * date 2016/12/20 下午2:24
 **/
public class TagViewGroup extends ViewGroup {

    public static final int DEFAULT_RADIUS = 8;//默认外圆半径
    public static final int DEFAULT_INNER_RADIUS = 4;//默认内圆半径
    public static final int DEFAULT_V_DISTANCE = 28;//默认竖直(上/下)方向线条长度
    public static final int DEFAULT_TILT_DISTANCE = 20;//默认斜线长度
    public static final int DEFAULT_LINES_WIDTH = 1;//默认线宽
    public static final int DEFAULT_MAX_TAG = 6;//默认标签最大数量
    private static final int DEFAULT_RIPPLE_MAX_RADIUS = 20;//水波纹默认最大半径
    private static final int DEFULT_RIPPLE_ALPHA = 100;//默认水波纹透明度

    private Paint mPaint;
    private Path mPath;
    private Path mDstPath;
    private PathMeasure mPathMeasure;
    private Animator mShowAnimator;
    private Animator mHideAnimator;
    private GestureDetectorCompat mGestureDetector;
    private OnTagGroupClickListener mClickListener;

    private RippleView mRippleView;
    private int mRippleMaxRadius;//水波纹最大半径
    private int mRippleMinRadius;//水波纹最小半径
    private int mRippleAlpha;//水波纹起始透明度
    private int mRadius;//外圆半径
    private int mInnerRadius;//内圆半径
    private int mTDistance;//斜线长度
    private int mVDistance;//竖直(上/下)方向线条长度
    private float mTagAlpha;//Tag标签的透明度
    private RectF mCenterRect;
    private RectF[] mRectArray;
    private int[] mChildUsed;
    private int mTagCount;
    private int mCenterX;//圆心 X 坐标
    private int mCenterY;//圆心 Y 坐标
    private float mPercentX;
    private float mPercentY;
    private int mLinesWidth;//线条宽度
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
        Resources.Theme theme = context.getTheme();
        TypedArray array = theme.obtainStyledAttributes(attrs, R.styleable.TagViewGroup, defStyleAttr, 0);
        mRadius = array.getDimensionPixelSize(R.styleable.TagViewGroup_radius, DipConvertUtils.dip2px(context, DEFAULT_RADIUS));
        mInnerRadius = array.getDimensionPixelSize(R.styleable.TagViewGroup_inner_radius, DipConvertUtils.dip2px(context, DEFAULT_INNER_RADIUS));
        mTDistance = array.getDimensionPixelSize(R.styleable.TagViewGroup_tilt_distance, DipConvertUtils.dip2px(context, DEFAULT_TILT_DISTANCE));
        mVDistance = array.getDimensionPixelSize(R.styleable.TagViewGroup_v_distance, DipConvertUtils.dip2px(context, DEFAULT_V_DISTANCE));
        mLinesWidth = array.getDimensionPixelSize(R.styleable.TagViewGroup_line_width, DipConvertUtils.dip2px(context, DEFAULT_LINES_WIDTH));
        mRippleMaxRadius = array.getDimensionPixelSize(R.styleable.TagViewGroup_ripple_maxRadius, DipConvertUtils.dip2px(context, DEFAULT_RIPPLE_MAX_RADIUS));
        mRippleAlpha = array.getInteger(R.styleable.TagViewGroup_ripple_alpha, DEFULT_RIPPLE_ALPHA);
        mRippleMinRadius = mInnerRadius + (mRadius - mInnerRadius) / 2;
        array.recycle();
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
        //绘制外圆
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(Color.parseColor("#30000000"));
        canvas.drawCircle(mCenterX, mCenterY, mRadius, mPaint);
        //绘制内圆
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(Color.WHITE);
        canvas.drawCircle(mCenterX, mCenterY, mInnerRadius, mPaint);
    }

    private void drawTagAlpha(float alpha) {
        for (int i = 0; i < getChildCount(); i++) {
            getChildAt(i).setAlpha(alpha);
        }
    }

    private void drawLines(Canvas canvas) {
        mPaint.setColor(Color.WHITE);
        mPaint.setStrokeWidth(mLinesWidth);
        mPaint.setStyle(Paint.Style.STROKE);

        for (int i = 0; i < getChildCount(); i++) {
            ITagView child = (ITagView) getChildAt(i);
            mPath.reset();
            mPath.moveTo(mCenterX, mCenterY);
            mDstPath.reset();
            mDstPath.rLineTo(0, 0);
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

    /**
     * 添加 Tag 列表
     *
     * @param tagList 要添加的 Tag 列表
     * @return 返回 标签组
     */
    public TagViewGroup addTagList(@NonNull List<ITagView> tagList) {
        for (ITagView tag : tagList) {
            addTag(tag);
        }
        return this;
    }

    /**
     * 添加单个 Tag
     *
     * @param tag 要添加的 Tag
     * @return 返回 标签组
     */
    public TagViewGroup addTag(@NonNull ITagView tag) {
        if (mTagCount >= DEFAULT_MAX_TAG) {
            throw new RuntimeException("The number of tags exceeds the maximum value(6)");
        }
        tag.setTag(mTagCount);
        addView((View) tag);
        mRectArray[mTagCount] = new RectF();
        mTagCount++;
        return this;
    }

    /**
     * 得到 TagViewGroup 中的所有标签列表
     *
     * @return
     */
    public List<ITagView> getTagList() {
        List<ITagView> list = new ArrayList<>();
        for (int i = 0; i < getChildCount(); i++) {
            ITagView tag = (ITagView) getChildAt(i);
            if (tag.getDirection() != DIRECTION.CENTER) {
                list.add(tag);
            }
        }
        return list;
    }

    /**
     * 得到 TagViewGroup 中的标签数量
     *
     * @return
     */
    public int getTagCount() {
        return mTagCount;
    }

    /**
     * 添加水波纹
     */
    public void addRipple() {
        mRippleView = new RippleView(getContext());
        mRippleView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        mRippleView.setDirection(DIRECTION.CENTER);
        mRippleView.initAnimator(mRippleMinRadius, mRippleMaxRadius, mRippleAlpha);
        addView(mRippleView);
    }

    private void refreshTagsRect() {
        for (int i = 0; i < getChildCount(); i++) {
            ITagView child = (ITagView) getChildAt(i);
            if (child.getDirection() != DIRECTION.CENTER) {
                int index = (int) child.getTag();
                if (mRectArray[index] == null) {
                    mRectArray[index] = new RectF();
                }
                mRectArray[index].set(child.getLeft(), child.getTop(), child.getRight(), child.getBottom());
            }
        }
    }

    /**
     * 检测 Touch 事件发生在哪个 Tag 上
     *
     * @param x
     * @param y
     * @return
     */
    private ITagView isTouchingTags(float x, float y) {
        for (int i = 0; i < getChildCount(); i++) {
            ITagView child = (ITagView) getChildAt(i);
            if (child.getDirection() != DIRECTION.CENTER && mRectArray[(int) child.getTag()].contains(x, y)) {
                return child;
            }
        }
        return null;
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

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mClickListener != null) {
            return mGestureDetector.onTouchEvent(event);
        }
        return super.onTouchEvent(event);
    }

    public void setOnTagGroupClickListener(OnTagGroupClickListener listener) {
        mClickListener = listener;
    }

    public interface OnTagGroupClickListener {

        //TagGroup 中心圆点被点击
        void onCircleClick(TagViewGroup group);

        //TagGroup Tag子view被点击
        void onTagClick(TagViewGroup group, ITagView tag, int index);

        //TagGroup 被长按
        void onLongPress(TagViewGroup group);

        //TagGroup 移动
        void onScroll(TagViewGroup group, float percentX, float percentY);
    }

    //内部处理 touch 事件监听器
    private class TagOnGestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onDown(MotionEvent e) {
            float x = e.getX();
            float y = e.getY();
            if (mCenterRect.contains(x, y) || isTouchingTags(x, y) != null) {
                return true;
            }
            return super.onDown(e);
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            float x = e.getX();
            float y = e.getY();

            if (mCenterRect.contains(x, y)) {
                mClickListener.onCircleClick(TagViewGroup.this);
            } else {
                ITagView clickedTag = isTouchingTags(x, y);
                if (clickedTag != null) {
                    mClickListener.onTagClick(TagViewGroup.this, clickedTag, (int) clickedTag.getTag());
                }
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
            invalidate();
            requestLayout();
            mClickListener.onScroll(TagViewGroup.this, mPercentX, mPercentY);
            return true;
        }

        @Override
        public void onLongPress(MotionEvent e) {
            super.onLongPress(e);
            float x = e.getX();
            float y = e.getY();
            if (mCenterRect.contains(x, y) || isTouchingTags(x, y) != null) {
                mClickListener.onLongPress(TagViewGroup.this);
            }
        }
    }

    /**
     * 设置中心圆的半径
     */
    @SuppressWarnings("unused")
    public void setCircleRadius(int radius) {
        mRadius = radius;
        invalidate();
    }

    public int getCircleRadius() {
        return mRadius;
    }

    /**
     * 设置中心内圆半径
     */
    @SuppressWarnings("unused")
    public void setCircleInnerRadius(int innerRadius) {
        mInnerRadius = innerRadius;
        invalidate();
    }

    public int getCircleInnerRadius() {
        return mInnerRadius;
    }

    /**
     * 设置线条显示比例
     */
    @SuppressWarnings("unused")
    public void setLinesRatio(float ratio) {
        mLinesRatio = ratio;
        invalidate();
    }

    public float getLinesRatio() {
        return mLinesRatio;
    }

    /**
     * 设置 Tag 的透明度
     */
    @SuppressWarnings("unused")
    public void setTagAlpha(float alpha) {
        mTagAlpha = alpha;
        drawTagAlpha(mTagAlpha);
    }

    public float getTagAlpha() {
        return mTagAlpha;
    }


    public boolean isHiden() {
        return mIsHiden;
    }

    public void setHiden(boolean hiden) {
        mIsHiden = hiden;
    }

    /**
     * 设置线条宽度
     *
     * @param lineWidth 线条宽度
     */
    public void setLineWidth(int lineWidth) {
        mLinesWidth = lineWidth;
        invalidate();
    }

    /**
     * 得到线条宽度
     *
     * @return
     */
    public int getLineWidth() {
        return mLinesWidth;
    }

    /**
     * 设置圆心到折点的垂直距离
     *
     * @param vDistance 垂直距离
     */
    public void setVDistance(int vDistance) {
        mVDistance = vDistance;
    }

    public int getVDistance() {
        return mVDistance;
    }

    /**
     * 设置圆心到斜线折点的垂直距离
     *
     * @param titlDistance 垂直距离
     */
    public void setTitlDistance(int titlDistance) {
        mTDistance = titlDistance;
    }

    public int getTitlDistance() {
        return mTDistance;
    }

    /**
     * 设置水波纹最大半径
     *
     * @param radius 最大半径
     */
    public void setRippleMaxRadius(int radius) {
        mRippleMaxRadius = radius;
    }

    public int getRippleMaxRadius() {
        return mRippleMaxRadius;
    }

    /**
     * 设置水波纹起始透明度
     *
     * @param alpha 透明度
     */
    public void setRippleAlpha(int alpha) {
        mRippleAlpha = alpha;
    }

    public int getRippleAlpha() {
        return mRippleAlpha;
    }

    public static final Property<TagViewGroup, Integer> CIRCLE_RADIUS = new Property<TagViewGroup, Integer>(Integer.class, "circleRadius") {
        @Override
        public Integer get(TagViewGroup object) {
            return object.getCircleRadius();
        }

        @Override
        public void set(TagViewGroup object, Integer value) {
            object.setCircleRadius(value);
        }
    };

    public static final Property<TagViewGroup, Integer> CIRCLE_INNER_RADIUS = new Property<TagViewGroup, Integer>(Integer.class, "circleInnerRadius") {
        @Override
        public Integer get(TagViewGroup object) {
            return object.getCircleInnerRadius();
        }

        @Override
        public void set(TagViewGroup object, Integer value) {
            object.setCircleInnerRadius(value);
        }
    };

    public static final Property<TagViewGroup, Float> LINES_RATIO = new Property<TagViewGroup, Float>(Float.class, "linesRatio") {
        @Override
        public Float get(TagViewGroup object) {
            return object.getLinesRatio();
        }

        @Override
        public void set(TagViewGroup object, Float value) {
            object.setLinesRatio(value);
        }
    };

    public static final Property<TagViewGroup, Float> TAG_ALPHA = new Property<TagViewGroup, Float>(Float.class, "tagAlpha") {
        @Override
        public Float get(TagViewGroup object) {
            return object.getTagAlpha();
        }

        @Override
        public void set(TagViewGroup object, Float value) {
            object.setTagAlpha(value);
        }
    };
}
