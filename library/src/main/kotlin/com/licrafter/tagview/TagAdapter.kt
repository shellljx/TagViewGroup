package com.licrafter.tagview

import android.database.DataSetObservable
import android.database.DataSetObserver
import android.util.Log
import android.view.View
import android.view.ViewGroup
import com.licrafter.tagview.views.ITagView

/**
 * Created by lijx on 2018/5/7.
 * Gmail: shellljx@gmail.com
 */
abstract class TagAdapter {

    private var mObservable = DataSetObservable()

    /**
     * Return the number of ITagView.
     */
    abstract fun getCount(): Int

    abstract fun getItem(position: Int): ITagView

    fun instantiateItem(container: ViewGroup, position: Int): ITagView {
        val itemId = getItemId(position).toLong()
        val tagView = getItem(position)
        if (DEBUG) Log.v(TAG, "Adding tagView #$itemId: TagView=$tagView")
        container.addView(tagView as View)
        return tagView
    }

    fun isViewFromObject(view: View, obj: Any): Boolean {
        return obj == view
    }

    fun destroyItem(container: ViewGroup, position: Int, tagView: ITagView) {

    }

    fun getItemId(position: Int): Int {
        return position
    }

    fun notifyDataSetChanged() {
        mObservable.notifyChanged()
    }

    fun registerDataSetObserver(observer: DataSetObserver) {
        mObservable.registerObserver(observer)
    }

    fun unregisterDataSetObserver(observer: DataSetObserver) {
        mObservable.unregisterObserver(observer)
    }

    companion object {
        const val DEBUG = false
        const val TAG = "TagAdapter"
    }
}