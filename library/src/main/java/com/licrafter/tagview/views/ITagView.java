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

}
