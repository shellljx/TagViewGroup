package com.licrafter.tagview.views

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.widget.TextView
import com.licrafter.tagview.DIRECTION
import com.licrafter.tagview.utils.DipConvertUtils

/**
 * Created by lijx on 2018/5/12.
 * Gmail: shellljx@gmail.com
 */
class TagTextView : TextView, ITagView {

    private var mDirection: DIRECTION? = null

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        setTextColor(Color.WHITE)
        textSize = 13f
        setShadowLayer(7f, 0f, 0f, Color.BLACK)
        setPadding(DipConvertUtils.dip2px(getContext(), 12f), DipConvertUtils.dip2px(getContext(), 4f), DipConvertUtils.dip2px(getContext(), 12f), DipConvertUtils.dip2px(getContext(), 4f))
    }

    override fun getDirection(): DIRECTION {
        return mDirection ?: throw RuntimeException("TagTextView has no direction")
    }

    override fun setDirection(direction: DIRECTION) {
        mDirection = direction
    }
}