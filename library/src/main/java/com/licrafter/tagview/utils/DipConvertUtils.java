package com.licrafter.tagview.utils;

import android.content.Context;

/**
 * author: shell
 * date 2016/12/22 下午1:04
 **/
public class DipConvertUtils {

    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}
