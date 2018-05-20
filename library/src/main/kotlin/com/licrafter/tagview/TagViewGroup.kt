package com.licrafter.tagview

import android.animation.Animator
import android.content.Context
import android.database.DataSetObserver
import android.graphics.*
import android.support.v4.view.GestureDetectorCompat
import android.util.AttributeSet
import android.util.Property
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import com.licrafter.tagview.utils.DipConvertUtils
import com.licrafter.tagview.utils.TagGroupUtils
import com.licrafter.tagview.views.ITagView
import java.util.ArrayList

/**
 * Created by lijx on 2018/4/22.
 * Gmail: shellljx@gmail.com
 */
class TagViewGroup : ViewGroup {

    private var mPaint = Paint()
    private var mPath = Path()
    private var mDstPath = Path()
    private val mPathMeasure = PathMeasure()
    private var mShowAnimator: Animator? = null
    private var mHideAnimator: Animator? = null
    private var mAdapter: TagAdapter? = null
    private var mGestureDetector: GestureDetectorCompat
    private var mClickListener: OnTagGroupClickListener? = null
    private var mScrollListener: OnTagGroupDragListener? = null
    private val mObserver = TagSetObserver()

    private var mRadius: Int            //外圆半径
    private var mInnerRadius: Int       //内圆半径
    private var mTDistance: Int         //斜线长度
    private var mVDistance: Int         //竖直(上/下)方向线条长度
    private var mTagAlpha: Float = 0f   //Tag标签的透明度
    private val mCenterRect = RectF()
    private val mItems = ArrayList<ItemInfo>()
    private var mChildUsed: IntArray = IntArray(4)
    private var mCenterX: Int = 0      //圆心 X 坐标
    private var mCenterY: Int = 0      //圆心 Y 坐标
    private var mPercentX: Float = 0f
    private var mPercentY: Float = 0f
    private var mLinesWidth: Int = 0    //线条宽度

    private var mLinesRatio = 1f


    class ItemInfo {
        var item: ITagView? = null
        var position: Int = 0
        var rectF = RectF()
    }

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        val theme = context.theme
        val array = theme.obtainStyledAttributes(attrs, R.styleable.TagViewGroup, defStyleAttr, 0)
        try {
            mRadius = array.getDimensionPixelSize(R.styleable.TagViewGroup_radius, DipConvertUtils.dip2px(context, DEFAULT_RADIUS.toFloat()))
            mInnerRadius = array.getDimensionPixelSize(R.styleable.TagViewGroup_inner_radius, DipConvertUtils.dip2px(context, DEFAULT_INNER_RADIUS.toFloat()))
            mTDistance = array.getDimensionPixelSize(R.styleable.TagViewGroup_tilt_distance, DipConvertUtils.dip2px(context, DEFAULT_TILT_DISTANCE.toFloat()))
            mVDistance = array.getDimensionPixelSize(R.styleable.TagViewGroup_v_distance, DipConvertUtils.dip2px(context, DEFAULT_V_DISTANCE.toFloat()))
            mLinesWidth = array.getDimensionPixelSize(R.styleable.TagViewGroup_line_width, DipConvertUtils.dip2px(context, DEFAULT_LINES_WIDTH.toFloat()))
        } finally {
            array.recycle()
        }

        mPaint.isAntiAlias = true
        mGestureDetector = GestureDetectorCompat(context, TagOnGestureListener())
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        measureChildren(widthMeasureSpec, heightMeasureSpec)
        mChildUsed = TagGroupUtils.getTagViewUsed(this)
        //园中心默认在左上角 (0,0)
        mCenterX = (measuredWidth * mPercentX).toInt()
        mCenterY = (measuredHeight * mPercentY).toInt()
        checkBounds()
        mCenterRect.set((mCenterX - mRadius).toFloat(), (mCenterY - mRadius).toFloat(), (mCenterX + mRadius).toFloat(), (mCenterY + mRadius).toFloat())
    }

    private fun checkBounds() {
        val rightAvailable = measuredWidth - mCenterX
        val leftAvailable = mCenterX
        if (mChildUsed[2] > rightAvailable) {
            mCenterX -= mChildUsed[2] - rightAvailable
        }
        if (mChildUsed[0] > leftAvailable) {
            mCenterX += mChildUsed[0] - leftAvailable
        }
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        for (i in 0 until childCount) {
            if (getChildAt(i) !is ITagView) {
                continue
            }
            val child = getChildAt(i) as ITagView
            val point = TagGroupUtils.getStartPoint(this, child)
            child.layout(point.x, point.y, point.x + child.getMeasuredWidth(), point.y + child.getMeasuredHeight())
            refreshTagsInfo(child)
        }
    }

    override fun dispatchDraw(canvas: Canvas) {
        super.dispatchDraw(canvas)
        //绘制折线
        drawLines(canvas)
        //绘制外圆
        mPaint.style = Paint.Style.FILL
        mPaint.color = Color.parseColor("#30000000")
        canvas.drawCircle(mCenterX.toFloat(), mCenterY.toFloat(), mRadius.toFloat(), mPaint)
        //绘制内圆
        mPaint.style = Paint.Style.FILL
        mPaint.color = Color.WHITE
        canvas.drawCircle(mCenterX.toFloat(), mCenterY.toFloat(), mInnerRadius.toFloat(), mPaint)
    }

    private fun drawTagAlpha(alpha: Float) {
        for (i in 0 until childCount) {
            getChildAt(i).alpha = alpha
        }
    }

    private fun drawLines(canvas: Canvas) {
        mPaint.color = Color.WHITE
        mPaint.strokeWidth = mLinesWidth.toFloat()
        mPaint.style = Paint.Style.STROKE

        for (i in 0 until childCount) {
            val child = getChildAt(i) as ITagView
            mPath.reset()
            mPath.moveTo(mCenterX.toFloat(), mCenterY.toFloat())
            mDstPath.reset()
            mDstPath.rLineTo(0f, 0f)
            when (child.getDirection()) {
                DIRECTION.RIGHT_TOP//右上
                    , DIRECTION.RIGHT_BOTTOM//右下
                    , DIRECTION.RIGHT_TOP_STRAIGHT//右上斜线
                    , DIRECTION.RIGHT_BOTTOM_STRAIGHT//右下斜线
                -> {
                    mPath.lineTo(child.getLeft().toFloat(), child.getBottom().toFloat())
                    mPath.lineTo(child.getRight().toFloat(), child.getBottom().toFloat())
                }
                DIRECTION.RIGHT_CENTER//右中
                -> mPath.lineTo(child.getRight().toFloat(), child.getBottom().toFloat())
                DIRECTION.LEFT_TOP//左上
                    , DIRECTION.LEFT_TOP_STRAIGHT//左上斜线
                    , DIRECTION.LEFT_BOTTOM//左下
                    , DIRECTION.LEFT_BOTTOM_STRAIGHT//左下斜线
                -> {
                    mPath.lineTo(child.getRight().toFloat(), child.getBottom().toFloat())
                    mPath.lineTo(child.getLeft().toFloat(), child.getBottom().toFloat())
                }
                DIRECTION.LEFT_CENTER//左中
                -> mPath.lineTo(child.getLeft().toFloat(), child.getBottom().toFloat())
            }
            mPathMeasure.setPath(mPath, false)
            mPathMeasure.getSegment(0f, mPathMeasure.length * mLinesRatio, mDstPath, true)
            canvas.drawPath(mDstPath, mPaint)
        }
    }

    private fun addnewItem(position: Int): ItemInfo {
        val itemInfo = ItemInfo()
        itemInfo.position = position
        itemInfo.item = mAdapter?.instantiateItem(this, position)
        mItems.add(itemInfo)
        return itemInfo
    }

    /**
     * 得到 TagViewGroup 中的所有标签列表
     *
     * @return
     */
    fun getTagList(): List<ITagView> {
        val list = ArrayList<ITagView>()
        for (i in mItems.indices) {
            list.add(mItems[i].item!!)
        }
        return list
    }

    fun startShowAnimator() {
        mShowAnimator?.let {
            if (!it.isRunning) {
                it.start()
            }
        }
    }

    fun startHideAnimator() {
        mHideAnimator?.let {
            if (!it.isRunning) {
                it.start()
            }
        }
    }

    fun setShowAnimator(animator: Animator): TagViewGroup {
        mShowAnimator = animator
        return this
    }

    fun setHideAnimator(animator: Animator): TagViewGroup {
        mHideAnimator = animator
        return this
    }

    fun setTagAdapter(adapter: TagAdapter) {
        mAdapter?.let {
            it.unregisterDataSetObserver(mObserver)
            clearGroup()
        }
        mAdapter = adapter
        mAdapter?.registerDataSetObserver(mObserver)
        populate()
    }

    fun getTagAdapter(): TagAdapter? {
        return mAdapter
    }

    private fun clearGroup() {
        for (i in mItems.indices) {
            val itemInfo = mItems[i]
            mAdapter?.destroyItem(this, itemInfo.position, itemInfo.item!!)
        }
        mItems.clear()
        removeAllViews()
    }

    private fun populate() {
        var count = mAdapter?.getCount() ?: 0
        if (count < 0 || count > DEFAULT_MAX_TAG) {
            throw IllegalStateException("TagView count must >= 0 and <= $DEFAULT_MAX_TAG")
        }
        for (i in 0 until count) {
            addnewItem(i)
        }
    }


    internal fun dataSetChanged() {
        clearGroup()
        populate()
    }

    internal fun infoForChild(child: View): ItemInfo? {
        for (i in mItems.indices) {
            val info = mItems[i]
            mAdapter?.let {
                if (it.isViewFromObject(child, info)) return info
            }
        }
        return null
    }

    private fun refreshTagsInfo(child: ITagView) {
        if (child.getDirection() != DIRECTION.CENTER) {
            infoForTagView(child)!!.rectF.set(child.getLeft().toFloat(), child.getTop().toFloat(), child.getRight().toFloat(), child.getBottom().toFloat())
        }
    }

    private fun infoForTagView(tagView: ITagView): ItemInfo? {
        for (i in mItems.indices) {
            val info = mItems[i]
            if (info.item == tagView) {
                return info
            }
        }
        return null
    }


    /**
     * 检测 Touch 事件发生在哪个 Tag 上
     *
     * @param x
     * @param y
     * @return
     */
    private fun isTouchingTags(x: Float, y: Float): ItemInfo? {
        for (i in mItems.indices) {
            val info = mItems[i]
            if (info.rectF.contains(x, y)) {
                return info
            }
        }
        return null
    }

    //设置中心圆点坐标占整个 ViewGroup 的比例

    fun setPercent(percentX: Float, percentY: Float) {
        mPercentX = percentX
        mPercentY = percentY
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        return mClickListener?.let {
            return mGestureDetector.onTouchEvent(event)
        } ?: super.onTouchEvent(event)
    }

    fun setOnTagGroupClickListener(listener: OnTagGroupClickListener) {
        mClickListener = listener
    }

    fun setOnTagGroupDragListener(listener: OnTagGroupDragListener) {
        this.mScrollListener = listener
    }

    interface OnTagGroupClickListener {

        //TagGroup 中心圆点被点击
        fun onCircleClick(container: TagViewGroup)

        //TagGroup Tag子view被点击
        fun onTagClick(container: TagViewGroup, tag: ITagView, position: Int)

        //TagGroup 被长按
        fun onLongPress(container: TagViewGroup)
    }

    interface OnTagGroupDragListener {
        //TagGroup 拖动
        fun onDrag(container: TagViewGroup, percentX: Float, percentY: Float)
    }

    //内部处理 touch 事件监听器
    private inner class TagOnGestureListener : GestureDetector.SimpleOnGestureListener() {
        override fun onDown(e: MotionEvent): Boolean {
            return if (mCenterRect.contains(e.x, e.y) || isTouchingTags(e.x, e.y) != null) {
                true
            } else super.onDown(e)
        }

        override fun onSingleTapUp(e: MotionEvent): Boolean {
            val x = e.x
            val y = e.y

            if (mCenterRect.contains(x, y)) {
                mClickListener?.onCircleClick(this@TagViewGroup)
            } else {
                val info = isTouchingTags(x, y)
                info?.let {
                    mClickListener?.onTagClick(this@TagViewGroup, it.item!!, info.position)
                }
            }
            return true
        }

        override fun onScroll(e1: MotionEvent, e2: MotionEvent, distanceX: Float, distanceY: Float): Boolean {
            mScrollListener?.let {
                var currentX = mCenterX - distanceX
                var currentY = mCenterY - distanceY
                currentX = Math.min(Math.max(currentX, mChildUsed[0].toFloat()), (measuredWidth - mChildUsed[2]).toFloat())
                currentY = Math.min(Math.max(currentY, mChildUsed[1].toFloat()), (measuredHeight - mChildUsed[3]).toFloat())
                mPercentX = currentX / measuredWidth
                mPercentY = currentY / measuredHeight
                invalidate()
                requestLayout()
                it.onDrag(this@TagViewGroup, mPercentX, mPercentY)
            }
            return true
        }

        override fun onLongPress(e: MotionEvent) {
            super.onLongPress(e)
            val x = e.x
            val y = e.y
            if (mCenterRect.contains(x, y) || isTouchingTags(x, y) != null) {
                mClickListener?.onLongPress(this@TagViewGroup)
            }
        }
    }

    fun getCenterPoint(): Point {
        return Point(mCenterX, mCenterY)
    }

    /**
     * 设置中心圆的半径
     */
    fun setCircleRadius(radius: Int) {
        mRadius = radius
        invalidate()
    }

    fun getCircleRadius(): Int {
        return mRadius
    }

    /**
     * 设置中心内圆半径
     */
    fun setCircleInnerRadius(innerRadius: Int) {
        mInnerRadius = innerRadius
        invalidate()
    }

    fun getCircleInnerRadius(): Int {
        return mInnerRadius
    }

    /**
     * 设置线条显示比例
     */
    fun setLinesRatio(ratio: Float) {
        mLinesRatio = ratio
        invalidate()
    }

    fun getLinesRatio(): Float {
        return mLinesRatio
    }

    /**
     * 设置 Tag 的透明度
     */
    fun setTagAlpha(alpha: Float) {
        mTagAlpha = alpha
        drawTagAlpha(mTagAlpha)
    }

    fun getTagAlpha(): Float {
        return mTagAlpha
    }

    /**
     * 设置线条宽度
     *
     * @param lineWidth 线条宽度
     */
    fun setLineWidth(lineWidth: Int) {
        mLinesWidth = lineWidth
        invalidate()
    }

    /**
     * 得到线条宽度
     *
     * @return
     */
    fun getLineWidth(): Int {
        return mLinesWidth
    }

    /**
     * 设置圆心到折点的垂直距离
     *
     * @param vDistance 垂直距离
     */
    fun setVDistance(vDistance: Int) {
        mVDistance = vDistance
    }

    fun getVDistance(): Int {
        return mVDistance
    }

    /**
     * 设置圆心到斜线折点的垂直距离
     *
     * @param titlDistance 垂直距离
     */
    fun setTitlDistance(titlDistance: Int) {
        mTDistance = titlDistance
    }

    fun getTitlDistance(): Int {
        return mTDistance
    }

    inner class TagSetObserver : DataSetObserver() {

        override fun onChanged() {
            dataSetChanged()
        }
    }

    companion object {
        //默认外圆半径
        const val DEFAULT_RADIUS: Int = 8
        //默认内圆半径
        const val DEFAULT_INNER_RADIUS: Int = 4
        //默认竖直(上/下)方向线条长度
        const val DEFAULT_V_DISTANCE: Int = 28
        //默认斜线长度
        const val DEFAULT_TILT_DISTANCE: Int = 20
        //默认线宽
        const val DEFAULT_LINES_WIDTH: Int = 1
        //默认标签最大数量
        const val DEFAULT_MAX_TAG: Int = 6

        @JvmField
        val CIRCLE_RADIUS: Property<TagViewGroup, Int> = object : Property<TagViewGroup, Int>(Int::class.java, "circleRadius") {
            override fun get(obj: TagViewGroup): Int {
                return obj.getCircleRadius()
            }

            override fun set(obj: TagViewGroup, value: Int) {
                obj.setCircleRadius(value)
            }
        }

        @JvmField
        val CIRCLE_INNER_RADIUS: Property<TagViewGroup, Int> = object : Property<TagViewGroup, Int>(Int::class.java, "circleInnerRadius") {
            override fun get(obj: TagViewGroup): Int {
                return obj.getCircleInnerRadius()
            }

            override fun set(obj: TagViewGroup, value: Int) {
                obj.setCircleInnerRadius(value)
            }
        }

        @JvmField
        val LINES_RATIO: Property<TagViewGroup, Float> = object : Property<TagViewGroup, Float>(Float::class.java, "linesRatio") {
            override fun get(obj: TagViewGroup): Float {
                return obj.getLinesRatio()
            }

            override fun set(obj: TagViewGroup, value: Float) {
                obj.setLinesRatio(value)
            }
        }

        @JvmField
        val TAG_ALPHA: Property<TagViewGroup, Float> = object : Property<TagViewGroup, Float>(Float::class.java, "tagAlpha") {
            override fun get(obj: TagViewGroup): Float {
                return obj.getTagAlpha()
            }

            override fun set(obj: TagViewGroup, value: Float) {
                obj.setTagAlpha(value)
            }
        }
    }
}