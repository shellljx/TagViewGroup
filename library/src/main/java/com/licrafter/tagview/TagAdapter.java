package com.licrafter.tagview;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.licrafter.tagview.views.ITagView;

/**
 * Created by lijx on 2017/5/17.
 */

public abstract class TagAdapter {

    private static final boolean DEBUG = false;
    private static final String TAG = "TagAdapter";

    /**
     * Return the number of ITagView.
     */
    public abstract int getCount();

    /**
     * Return the ITagView associated with a specified position.
     */
    public abstract ITagView getItem(int position);

    public ITagView instantiateItem(ViewGroup container, int position) {
        long itemId = getItemId(position);
        ITagView tagView = getItem(position);
        if (DEBUG) Log.v(TAG, "Adding tagView #" + itemId + ": TagView=" + tagView);
        container.addView((View) tagView);
        return tagView;
    }

    public boolean isViewFromObject(View view, Object object) {
        return object.equals(view);
    }

    public void destroyItem(ViewGroup container, int position, ITagView object) {

    }

    public int getItemId(int position) {
        return position;
    }

}
