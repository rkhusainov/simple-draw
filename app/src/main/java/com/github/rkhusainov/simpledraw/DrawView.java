package com.github.rkhusainov.simpledraw;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.util.Pair;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import com.github.rkhusainov.simpledraw.model.Box;
import com.github.rkhusainov.simpledraw.model.Curve;
import com.github.rkhusainov.simpledraw.model.Line;
import com.github.rkhusainov.simpledraw.model.Point;

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

    private Paint mPolyPaint = new Paint();
    private List<Point> mPoints = new ArrayList<>();
    private Point mPoint;

    private int mCurrentColor = getContext().getResources().getColor(R.color.colorBlack);

    private DrawType mDrawType = DrawType.CURVE;

    private GestureDetector mGestureDetector;
    private boolean mScrolls;

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

        mPolyPaint.setColor(mCurrentColor);
        mPolyPaint.setAntiAlias(true);
        mPolyPaint.setStyle(Paint.Style.STROKE);
        mPolyPaint.setStrokeWidth(10f);

        initGestureDetector();
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
        // мультитач
        polyDraw(canvas);
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

    private void polyDraw(Canvas canvas) {

        if (mPoints.isEmpty()) {
            return;
        }

        if (mPoints.size() == 1) {
            canvas.drawPoint(mPoints.get(0).getCurrent().x, mPoints.get(0).getCurrent().y, mPolyPaint);
        } else {
            for (int i = 1; i < mPoints.size(); i++) {
                PointF one = mPoints.get(i - 1).getCurrent();
                PointF two = mPoints.get(i).getCurrent();

                canvas.drawLine(one.x, one.y, two.x, two.y, mPolyPaint);
            }
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
        mPoints.clear();
        mScrolls = false;
        invalidate();
    }

    public void setScrolls(boolean scrolls) {
        mScrolls = scrolls;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mScrolls) {
            mGestureDetector.onTouchEvent(event);
        }

        int action = event.getActionMasked();
        PointF currentPoint = new PointF(event.getX(), event.getY());

        switch (mDrawType) {
            case CURVE:
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
                    default:
                        return super.onTouchEvent(event);
                }
                break;

            case LINE:
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
                    default:
                        return super.onTouchEvent(event);
                }
                break;

            case BOX:
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
                    default:
                        return super.onTouchEvent(event);
                }
                break;

            case POLY:
                switch (event.getActionMasked()) {
                    case MotionEvent.ACTION_DOWN:
                        mPoint = new Point(currentPoint);
                        mPoint.getCurrent().x = event.getX();
                        mPoint.getCurrent().y = event.getY();
                        mPoints.add(mPoint);
                        break;

                    case MotionEvent.ACTION_POINTER_DOWN:
                        int pointerId = event.getPointerId(event.getActionIndex());

                        if (mPoints.size() == pointerId) {
                            mPoint = new Point(currentPoint);
                            mPoint = new Point(currentPoint);
                            mPoint.getCurrent().x = event.getX(event.getActionIndex());
                            mPoint.getCurrent().y = event.getY(event.getActionIndex());
                            mPoints.add(mPoint);

                        } else {
                            Point point = mPoints.get(pointerId);
                            point.getCurrent().x = event.getX(event.getActionIndex());
                            point.getCurrent().y = event.getY(event.getActionIndex());
                        }
                        break;

                    case MotionEvent.ACTION_MOVE:
                        for (int i = 0; i < event.getPointerCount(); i++) {
                            int id = event.getPointerId(i);
                            mPoint = mPoints.get(id);
                            mPoint.getCurrent().x = event.getX(i);
                            mPoint.getCurrent().y = event.getY(i);
                        }
                        break;
                    case MotionEvent.ACTION_POINTER_UP:
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        break;
                    default:
                        return super.onTouchEvent(event);
                }
        }

        invalidate();
        return true;
    }

    private void initGestureDetector() {
        mGestureDetector = new GestureDetector(getContext(), new GestureDetector.OnGestureListener() {
            @Override
            public boolean onDown(MotionEvent e) {
                return false;
            }

            @Override
            public void onShowPress(MotionEvent e) {

            }

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return false;
            }

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                for (Point point : mPoints) {
                    point.getCurrent().x += distanceX;
                    point.getCurrent().y += distanceY;
                }
                invalidate();
                return true;
            }

            @Override
            public void onLongPress(MotionEvent e) {

            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                return false;
            }
        });
    }
}
