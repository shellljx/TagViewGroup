package com.licrafter.tagview.utils

import android.content.Context

/**
 * Created by lijx on 2018/5/12.
 * Gmail: shellljx@gmail.com
 */
object DipConvertUtils {

    @JvmStatic
    fun dip2px(context: Context, dpValue: Float): Int {
        val scale = context.resources.displayMetrics.density
        return (dpValue * scale + 0.5f).toInt()
    }
}