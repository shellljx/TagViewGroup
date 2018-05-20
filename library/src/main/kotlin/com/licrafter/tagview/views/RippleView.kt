package com.licrafter.tagview.views

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import android.view.animation.AccelerateInterpolator
import com.licrafter.tagview.DIRECTION
import com.licrafter.tagview.R
import com.licrafter.tagview.utils.DipConvertUtils

/**
 * Created by lijx on 2018/5/7.
 * Gmail: shellljx@gmail.com
 */
class RippleView : View, ITagView {

    private var mAnimator: AnimatorSet? = null
    private var mDirection: DIRECTION? = null
    private val mPaint = Paint()
    private var mRadius: Float = 0f
    private var mAlpha: Int = 0
    private var mX: Float = 0f
    private var mY: Float = 0f

    private var mRippleMaxRadius: Int   //水波纹最大半径
    private var mRippleAlpha: Int       //水波纹起始透明度

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        mPaint.color = Color.WHITE
        val theme = context.theme
        val array = theme.obtainStyledAttributes(attrs, R.styleable.RippleView, defStyleAttr, 0)
        try {
            mRippleMaxRadius = array.getDimensionPixelSize(R.styleable.RippleView_ripple_maxRadius, DipConvertUtils.dip2px(context, DEFAULT_RIPPLE_MAX_RADIUS.toFloat()))
            mRippleAlpha = array.getInteger(R.styleable.RippleView_ripple_alpha, DEFULT_RIPPLE_ALPHA)
        } finally {
            array.recycle()
        }
        initAnimator(0, mRippleMaxRadius, mRippleAlpha)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        setMeasuredDimension(mRippleMaxRadius * 2, mRippleMaxRadius * 2)
        mX = mRippleMaxRadius.toFloat()
        mY = mRippleMaxRadius.toFloat()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        mPaint.alpha = mAlpha
        canvas.drawCircle(mX, mY, mRadius, mPaint)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        mAnimator?.end()
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        mAnimator?.start()
    }

    //设置水波纹半径
    fun setRippleRadius(radius: Int) {
        mRadius = radius.toFloat()
        invalidate()
    }

    //设置水波纹 alpha 范围[0-255]
    fun setRippleAlpha(alpha: Int) {
        mAlpha = alpha
        invalidate()
    }

    fun getRippleAlpha(): Int {
        return mRippleAlpha
    }

    /**
     * 设置水波纹最大半径
     *
     * @param radius 最大半径
     */
    fun setRippleMaxRadius(radius: Int) {
        mRippleMaxRadius = radius
    }

    fun getRippleMaxRadius(): Int {
        return mRippleMaxRadius
    }

    override fun getDirection(): DIRECTION {
        return mDirection ?: throw RuntimeException("RippleView has no direction")
    }

    override fun setDirection(direction: DIRECTION) {
        mDirection = direction
    }

    private fun initAnimator(minRadius: Int, maxRadius: Int, alpha: Int) {
        val radiusAnimator = ObjectAnimator.ofInt(this, "RippleRadius", minRadius, maxRadius)
        radiusAnimator.repeatMode = ValueAnimator.RESTART
        radiusAnimator.repeatCount = ValueAnimator.INFINITE
        val alphaAnimator = ObjectAnimator.ofInt(this, "RippleAlpha", alpha, 0)
        alphaAnimator.repeatMode = ValueAnimator.RESTART
        alphaAnimator.repeatCount = ValueAnimator.INFINITE
        mAnimator = AnimatorSet()
        mAnimator!!.playTogether(radiusAnimator, alphaAnimator)
        mAnimator!!.duration = 1000
        mAnimator!!.interpolator = AccelerateInterpolator()
    }

    companion object {
        //水波纹默认最大半径
        const val DEFAULT_RIPPLE_MAX_RADIUS: Int = 20
        //默认水波纹透明度
        const val DEFULT_RIPPLE_ALPHA: Int = 100
    }
}