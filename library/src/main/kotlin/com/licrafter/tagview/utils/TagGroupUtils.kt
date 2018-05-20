package com.licrafter.tagview.utils

import android.graphics.Point
import com.licrafter.tagview.DIRECTION
import com.licrafter.tagview.TagViewGroup
import com.licrafter.tagview.views.ITagView

/**
 * Created by lijx on 2018/5/20.
 * Gmail: shellljx@gmail.com
 */
object TagGroupUtils {

    /**
     * 获取中心圆上下左右各个方向所占空间
     *
     * @return int[]{left,top,right,bottom}
     */
    @JvmStatic
    fun getTagViewUsed(group: TagViewGroup): IntArray {
        var leftMax = group.getCircleRadius()
        var topMax = group.getCircleRadius()
        var rightMax = group.getCircleRadius()
        var bottomMax = group.getCircleRadius()

        for (i in 0 until group.childCount) {
            val child = group.getChildAt(i) as? ITagView ?: continue
            when (child.getDirection()) {
                DIRECTION.RIGHT_TOP_STRAIGHT//右上斜直线
                -> {
                    rightMax = Math.max(rightMax, group.getTitlDistance() + child.getMeasuredWidth())
                    topMax = Math.max(topMax, child.getMeasuredHeight() + group.getTitlDistance())
                }
                DIRECTION.RIGHT_TOP//右上折线
                -> {
                    rightMax = Math.max(rightMax, child.getMeasuredWidth())
                    topMax = Math.max(topMax, child.getMeasuredHeight() + group.getVDistance())
                }
                DIRECTION.RIGHT_CENTER//右中折线
                -> {
                    rightMax = Math.max(rightMax, child.getMeasuredWidth())
                    topMax = Math.max(topMax, Math.max(group.getVDistance(), child.getMeasuredHeight()))
                }
                DIRECTION.RIGHT_BOTTOM//右下折线
                -> {
                    rightMax = Math.max(rightMax, child.getMeasuredWidth())
                    bottomMax = group.getVDistance()
                }
                DIRECTION.RIGHT_BOTTOM_STRAIGHT -> {
                    rightMax = Math.max(rightMax, group.getTitlDistance() + child.getMeasuredWidth())
                    bottomMax = group.getTitlDistance()
                }
                DIRECTION.LEFT_TOP//左上折线
                -> {
                    leftMax = Math.max(leftMax, child.getMeasuredWidth())
                    topMax = Math.max(topMax, child.getMeasuredHeight() + group.getVDistance())
                }
                DIRECTION.LEFT_TOP_STRAIGHT//左上斜直线
                -> {
                    leftMax = Math.max(leftMax, child.getMeasuredWidth() + group.getTitlDistance())
                    topMax = Math.max(topMax, child.getMeasuredHeight() + group.getTitlDistance())
                }
                DIRECTION.LEFT_CENTER//左中折线
                -> {
                    leftMax = Math.max(leftMax, child.getMeasuredWidth())
                    topMax = Math.max(topMax, Math.max(group.getVDistance(), child.getMeasuredHeight()))
                }
                DIRECTION.LEFT_BOTTOM//左下折线
                -> {
                    leftMax = Math.max(leftMax, child.getMeasuredWidth())
                    bottomMax = group.getVDistance()
                }
                DIRECTION.LEFT_BOTTOM_STRAIGHT//左下斜直线
                -> {
                    leftMax = Math.max(leftMax, child.getMeasuredWidth() + group.getTitlDistance())
                    bottomMax = group.getTitlDistance()
                }
                DIRECTION.CENTER
                -> {
                    leftMax = Math.max(leftMax, child.getMeasuredWidth() / 2)
                    bottomMax = Math.max(bottomMax, child.getMeasuredHeight() / 2)
                }
            }
        }
        return intArrayOf(leftMax, topMax, rightMax, bottomMax)
    }

    @JvmStatic
    fun getStartPoint(group: TagViewGroup, child: ITagView): Point {
        val x: Int
        val y: Int

        val centerX = group.getCenterPoint().x
        val centerY = group.getCenterPoint().y

        when (child.getDirection()) {
            DIRECTION.RIGHT_TOP_STRAIGHT//右上斜线
            -> {
                y = centerY - group.getTitlDistance() - child.getMeasuredHeight()
                x = centerX + group.getTitlDistance()
            }
            DIRECTION.RIGHT_TOP//右上
            -> {
                x = centerX
                y = centerY - group.getVDistance() - child.getMeasuredHeight()
            }
            DIRECTION.RIGHT_CENTER//右中
            -> {
                x = centerX
                y = centerY - child.getMeasuredHeight()
            }
            DIRECTION.RIGHT_BOTTOM//右下
            -> {
                x = centerX
                y = group.getVDistance() + centerY - child.getMeasuredHeight()
            }
            DIRECTION.RIGHT_BOTTOM_STRAIGHT//右下斜线
            -> {
                x = centerX + group.getTitlDistance()
                y = group.getTitlDistance() + centerY - child.getMeasuredHeight()
            }
            DIRECTION.LEFT_TOP//左上
            -> {
                x = centerX - child.getMeasuredWidth()
                y = centerY - group.getVDistance() - child.getMeasuredHeight()
            }
            DIRECTION.LEFT_TOP_STRAIGHT//左上斜线
            -> {
                x = centerX - child.getMeasuredWidth() - group.getTitlDistance()
                y = centerY - group.getTitlDistance() - child.getMeasuredHeight()
            }
            DIRECTION.LEFT_CENTER//左中
            -> {
                x = centerX - child.getMeasuredWidth()
                y = centerY - child.getMeasuredHeight()
            }
            DIRECTION.LEFT_BOTTOM//左下
            -> {
                x = centerX - child.getMeasuredWidth()
                y = group.getVDistance() + centerY - child.getMeasuredHeight()
            }
            DIRECTION.LEFT_BOTTOM_STRAIGHT//左下斜线
            -> {
                x = centerX - child.getMeasuredWidth() - group.getTitlDistance()
                y = group.getTitlDistance() + centerY - child.getMeasuredHeight()
            }
            DIRECTION.CENTER -> {
                x = centerX - child.getMeasuredWidth() / 2
                y = centerY - child.getMeasuredHeight() / 2
            }
        }
        return Point(x, y)
    }
}