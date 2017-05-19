package com.licrafter.tagview.views;

import com.licrafter.tagview.DIRECTION;

/**
 * author: shell
 * date 2016/12/22 下午1:04
 **/

public interface ITagView {

    // set tag direction
    void setDirection(DIRECTION direction);

    // get tag dirction
    DIRECTION getDirection();

    int getMeasuredWidth();

    int getMeasuredHeight();

    int getTop();

    int getLeft();

    int getRight();

    int getBottom();

    void layout(int left, int top, int right, int bottom);

}
