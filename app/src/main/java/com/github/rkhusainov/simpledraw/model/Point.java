package com.github.rkhusainov.simpledraw.model;

import android.graphics.PointF;

public class Point {
    private PointF mOrigin;
    private PointF mCurrent;

    public Point(PointF origin) {
        mOrigin = origin;
        mCurrent = origin;
    }

    public PointF getOrigin() {
        return mOrigin;
    }

    public void setOrigin(PointF origin) {
        mOrigin = origin;
    }

    public PointF getCurrent() {
        return mCurrent;
    }

    public void setCurrent(PointF current) {
        mCurrent = current;
    }
}
