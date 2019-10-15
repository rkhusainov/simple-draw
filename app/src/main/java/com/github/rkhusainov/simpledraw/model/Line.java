package com.github.rkhusainov.simpledraw.model;

import android.graphics.PointF;

public class Line {
    private PointF mStart;
    private PointF mEnd;
    private int mColor;

    public Line(PointF start, PointF end, int color) {
        mStart = start;
        mEnd = end;
        mColor = color;
    }

    public PointF getStart() {
        return mStart;
    }

    public void setStart(PointF start) {
        mStart = start;
    }

    public PointF getEnd() {
        return mEnd;
    }

    public void setEnd(PointF end) {
        mEnd = end;
    }

    public int getColor() {
        return mColor;
    }

    public void setColor(int color) {
        mColor = color;
    }
}
