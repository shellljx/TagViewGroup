package com.licrafter.tagview;

/**
 * Created by lijx on 2016/12/20.
 */

public enum DIRECTION {
    LEFT_CENTER(8), LEFT_TOP(10), LEFT_TOP_TILT(9), LEFT_BOTTOM(6), LEFT_BOTTOM_TILT(7),
    RIGHT_CENTER(3), RIGHT_TOP(1), RIGHT_TOP_TILT(2), RIGHT_BOTTOM(5), RIGHT_BOTTOM_TILT(4),
    CENTER(0);

    private int value;

    DIRECTION(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static DIRECTION valueOf(int value) {
        switch (value) {
            case 0:
                return CENTER;
            case 1:
                return RIGHT_TOP;
            case 2:
                return RIGHT_TOP_TILT;
            case 3:
                return RIGHT_CENTER;
            case 4:
                return RIGHT_BOTTOM_TILT;
            case 5:
                return RIGHT_BOTTOM;
            case 6:
                return LEFT_BOTTOM;
            case 7:
                return LEFT_BOTTOM_TILT;
            case 8:
                return LEFT_CENTER;
            case 9:
                return LEFT_TOP_TILT;
            case 10:
                return LEFT_TOP;
            default:
                return CENTER;
        }
    }
}
