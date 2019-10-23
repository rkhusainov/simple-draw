package com.github.rkhusainov.simpledraw.model;

public class Poly {
    private Point mPoint;
    private int color;

    public Poly(Point point, int color) {
        mPoint = point;
        this.color = color;
    }

    public Point getPoint() {
        return mPoint;
    }

    public void setPoint(Point point) {
        mPoint = point;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }
}
