package com.licrafter.tagview.views;

import com.licrafter.tagview.DIRECTION;

/**
 * author: shell
 * date 2016/12/22 下午1:04
 **/

public interface ITagView {

    //设置Tag的方向
    void setDirection(DIRECTION direction);

    //得到Tag的方向
    DIRECTION getDirection();

    int getMeasuredWidth();

    int getMeasuredHeight();

    int getTop();

    int getLeft();

    int getRight();

    int getBottom();

    Object getTag();

    void setTag(Object tag);

    void setTag(int key, Object tag);

    void layout(int left, int top, int right, int bottom);

}
