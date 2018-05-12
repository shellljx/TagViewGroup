package com.licrafter.sample.utils;

import android.util.SparseArray;

import com.licrafter.tagview.DIRECTION;

/**
 * Created by lijx on 2018/5/12.
 * Gmail: shellljx@gmail.com
 */

public class DirectionUtils {

    private static SparseArray<DIRECTION> map;

    public static DIRECTION getDirection(int value) {
        if (map == null) {
            initMap();
        }
        return map.get(value);
    }

    public static int getValue(DIRECTION direction) {
        if (map == null) {
            initMap();
        }
        return map.keyAt(map.indexOfValue(direction));
    }

    private static void initMap() {
        map = new SparseArray<>();
        map.put(0, DIRECTION.CENTER);
        map.put(1, DIRECTION.RIGHT_BOTTOM);
        map.put(2, DIRECTION.RIGHT_BOTTOM_STRAIGHT);
        map.put(3, DIRECTION.RIGHT_CENTER);
        map.put(4, DIRECTION.RIGHT_TOP);
        map.put(5, DIRECTION.RIGHT_TOP_STRAIGHT);
        map.put(6, DIRECTION.LEFT_BOTTOM);
        map.put(7, DIRECTION.LEFT_BOTTOM_STRAIGHT);
        map.put(8, DIRECTION.LEFT_CENTER);
        map.put(9, DIRECTION.LEFT_TOP);
        map.put(10, DIRECTION.LEFT_TOP_STRAIGHT);

    }

}
