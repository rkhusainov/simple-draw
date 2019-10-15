package com.github.rkhusainov.simpledraw;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import com.github.rkhusainov.simpledraw.model.Box;
import com.github.rkhusainov.simpledraw.model.Curve;
import com.github.rkhusainov.simpledraw.model.Line;

import java.util.ArrayList;
import java.util.List;

public class DrawView extends View {
    private Paint mBackgroundPaint = new Paint();

    private Path mDrawPath = new Path();
    private Paint mCurvePaint = new Paint();
    private List<Curve> mCurves = new ArrayList<>();
    private Curve mCurve;

    private Paint mLinePaint = new Paint();
    private List<Line> mLines = new ArrayList<>();
    private Line mLine;

    private Paint mBoxPaint = new Paint();
    private List<Box> mBoxes = new ArrayList<>();
    private Box mCurrentBox;

    private int mCurrentColor = getContext().getResources().getColor(R.color.colorBlack);

    private DrawType mDrawType = DrawType.CURVE;

    public DrawView(Context context) {
        this(context, null);
    }

    public DrawView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setupPaint();
    }

    private void setupPaint() {
        mBackgroundPaint.setColor(Color.WHITE);

        mCurvePaint.setColor(mCurrentColor);
        mCurvePaint.setAntiAlias(true);
        mCurvePaint.setStyle(Paint.Style.STROKE);
        mCurvePaint.setStrokeWidth(10f);

        mLinePaint.setColor(mCurrentColor);
        mLinePaint.setAntiAlias(true);
        mLinePaint.setStyle(Paint.Style.STROKE);
        mLinePaint.setStrokeWidth(10f);

        mBoxPaint.setColor(mCurrentColor);
        mBoxPaint.setAntiAlias(true);
        mBoxPaint.setStyle(Paint.Style.FILL);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // кривые
        curveDraw(canvas);
        // прямые
        lineDraw(canvas);
        // прямоугольники
        boxDraw(canvas);
    }

    private void curveDraw(Canvas canvas) {
        for (Curve curve : mCurves) {
            mCurvePaint.setColor(curve.getColor());
            canvas.drawPath(curve.getPath(), mCurvePaint);
        }
    }

    private void lineDraw(Canvas canvas) {
        for (Line line : mLines) {
            float startX = line.getStart().x;
            float startY = line.getStart().y;
            float endX = line.getEnd().x;
            float endY = line.getEnd().y;
            mLinePaint.setColor(line.getColor());
            canvas.drawLine(startX, startY, endX, endY, mLinePaint);
        }
    }

    private void boxDraw(Canvas canvas) {
        for (Box box : mBoxes) {
            float left = Math.min(box.getCurrent().x, box.getOrigin().x);
            float right = Math.max(box.getCurrent().x, box.getOrigin().x);
            float top = Math.min(box.getCurrent().y, box.getOrigin().y);
            float bottom = Math.max(box.getCurrent().y, box.getOrigin().y);
            mBoxPaint.setColor(box.getColor());
            canvas.drawRect(left, top, right, bottom, mBoxPaint);
        }
    }

    public void setDrawType(DrawType drawType) {
        mDrawType = drawType;
    }

    public void setPaintColor(int color) {
        mCurrentColor = color;
    }

    public void clear() {
        mLines.clear();
        mCurves.clear();
        mBoxes.clear();
        invalidate();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (mDrawType) {
            case CURVE:
                curveEvent(event);
                break;
            case LINE:
                lineEvent(event);
                break;
            case BOX:
                boxEvent(event);
                break;
            default:
                return super.onTouchEvent(event);
        }
        invalidate();
        return true;
    }

    private void curveEvent(MotionEvent event) {
        int action = event.getAction();
        PointF currentPoint = new PointF(event.getX(), event.getY());

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mDrawPath = new Path();
                mDrawPath.moveTo(currentPoint.x, currentPoint.y);
                break;
            case MotionEvent.ACTION_MOVE:
                mDrawPath.lineTo(currentPoint.x, currentPoint.y);
                mCurve = new Curve(mDrawPath, mCurrentColor);
                mCurves.add(mCurve);
            case MotionEvent.ACTION_CANCEL:
                break;
        }
    }

    private void lineEvent(MotionEvent event) {
        int action = event.getAction();
        PointF currentPoint = new PointF(event.getX(), event.getY());

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mLine = new Line(currentPoint, currentPoint, mCurrentColor);
                mLines.add(mLine);
                break;
            case MotionEvent.ACTION_MOVE:
                if (mLine != null) {
                    mLine.setEnd(currentPoint);
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                mLine = null;
                break;
        }
    }

    private void boxEvent(MotionEvent event) {
        int action = event.getAction();
        PointF currentPoint = new PointF(event.getX(), event.getY());

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mCurrentBox = new Box(currentPoint, mCurrentColor);
                mBoxes.add(mCurrentBox);
                break;
            case MotionEvent.ACTION_MOVE:
                if (mCurrentBox != null) {
                    mCurrentBox.setCurrent(currentPoint);
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                mCurrentBox = null;
                break;
        }
    }
}
