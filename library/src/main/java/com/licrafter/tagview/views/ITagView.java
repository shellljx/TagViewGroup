package com.licrafter.tagview.views;

import com.licrafter.tagview.DIRECTION;

/**
 * Created by lijx on 2016/12/22.
 */

public interface ITagView {

    //设置Tag的方向
    public void setDirection(DIRECTION direction);

    //得到Tag的方向
    public DIRECTION getDirection();

    public int getMeasuredWidth();

    public int getMeasuredHeight();

    public int getTop();

    public int getLeft();

    public int getRight();

    public int getBottom();

    public void layout(int left, int top, int right, int bottom);

}
