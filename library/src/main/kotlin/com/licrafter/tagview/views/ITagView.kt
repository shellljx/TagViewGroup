package com.licrafter.tagview.views

import com.licrafter.tagview.DIRECTION

/**
 * Created by lijx on 2018/5/7.
 * Gmail: shellljx@gmail.com
 */
interface ITagView {

    // set tag direction
    fun setDirection(direction: DIRECTION)

    // get tag dirction
    fun getDirection(): DIRECTION

    fun getMeasuredWidth(): Int

    fun getMeasuredHeight(): Int

    fun getTop(): Int

    fun getLeft(): Int

    fun getRight(): Int

    fun getBottom(): Int

    fun layout(left: Int, top: Int, right: Int, bottom: Int)

}