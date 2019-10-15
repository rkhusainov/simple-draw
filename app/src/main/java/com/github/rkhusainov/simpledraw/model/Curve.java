package com.github.rkhusainov.simpledraw.model;

import android.graphics.Path;

public class Curve {
    private Path mPath;
    private int color;

    public Curve(Path path, int color) {
        mPath = path;
        this.color = color;
    }


    public Path getPath() {
        return mPath;
    }

    public void setPath(Path path) {
        mPath = path;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }
}
