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

/**
 * Created by lijx on 2018/5/7.
 * Gmail: shellljx@gmail.com
 */
class RippleView : View, ITagView {

    private var mRadius: Float = 0f
    private var mAlpha: Int = 0
    private var mDirection: DIRECTION? = null
    private val mPaint = Paint()
    private lateinit var mAnimator: AnimatorSet
    private var mX: Float = 0f
    private var mY: Float = 0f

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        mPaint.color = Color.WHITE
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        mPaint.alpha = mAlpha
        canvas.drawCircle(mX, mY, mRadius, mPaint)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        stopRipple()
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        startRipple()
    }

    fun startRipple() {
        mAnimator.start()
    }

    fun stopRipple() {
        mAnimator.end()
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

    fun setCenterPoint(x: Int, y: Int) {
        mX = x.toFloat()
        mY = y.toFloat()
    }

    override fun getDirection(): DIRECTION {
        return mDirection ?: throw RuntimeException("RippleView has no direction")
    }

    override fun setDirection(direction: DIRECTION) {
        mDirection = direction
    }

    fun initAnimator(minRadius: Int, maxRadius: Int, alpha: Int) {
        val radiusAnimator = ObjectAnimator.ofInt(this, "RippleRadius", minRadius, maxRadius)
        radiusAnimator.repeatMode = ValueAnimator.RESTART
        radiusAnimator.repeatCount = ValueAnimator.INFINITE
        val alphaAnimator = ObjectAnimator.ofInt(this, "RippleAlpha", alpha, 0)
        alphaAnimator.repeatMode = ValueAnimator.RESTART
        alphaAnimator.repeatCount = ValueAnimator.INFINITE
        mAnimator = AnimatorSet()
        mAnimator.playTogether(radiusAnimator, alphaAnimator)
        mAnimator.duration = 1000
        mAnimator.interpolator = AccelerateInterpolator()
    }
}