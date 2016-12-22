package com.licrafter.tagview.views;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.TextView;

import com.licrafter.tagview.DIRECTION;
import com.licrafter.tagview.TagViewGroup;

/**
 * author: shell
 * date 2016/12/20 下午3:43
 **/
public class TagView extends TextView {

    private DIRECTION mDirection;

    public TagView(Context context) {
        this(context, null);
    }

    public TagView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TagView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setTextColor(Color.WHITE);
        setTextSize(13);
        setShadowLayer(7, 0, 0, Color.BLACK);
        setPadding(dip2px(getContext(), 5), dip2px(getContext(), 2), dip2px(getContext(), 5), dip2px(getContext(), 2));
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_UP:
                ((TagViewGroup) getParent()).refreshChildDirection(this);
                break;
        }
        return super.onTouchEvent(event);
    }

    public DIRECTION getDirection() {
        return mDirection;
    }

    public void setDirection(DIRECTION direction) {
        mDirection = direction;
    }

    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}
