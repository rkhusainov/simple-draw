package com.github.rkhusainov.simpledraw;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.github.rkhusainov.simpledraw.model.Box;
import com.github.rkhusainov.simpledraw.model.Curve;
import com.github.rkhusainov.simpledraw.model.Line;

import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;

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

    private GestureDetector mGestureDetector;
    private boolean mScrolls;

    private List<FigureDrawable> mFigures = new ArrayList<>();
    private FigureDrawable mCurrentFigure;

    public DrawView(Context context) {
        this(context, null);
        setupPaint();
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
        for (FigureDrawable figure : mFigures) {
            figure.draw(canvas);
        }
        Log.d(TAG, "polyDraw: " + mFigures.size());
        if (mCurrentFigure != null) {
            mCurrentFigure.draw(canvas);
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
        mScrolls = false;
        mFigures.clear();
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
                        mCurrentFigure = new FigureDrawable(mCurrentColor);
                        currentPoint = mCurrentFigure.getPoint(0);
                        break;

                    case MotionEvent.ACTION_POINTER_DOWN:
                        int pointerId = event.getPointerId(event.getActionIndex());
                        currentPoint = mCurrentFigure.getPoint(pointerId);
                        break;

                    case MotionEvent.ACTION_MOVE:
                        for (int i = 0; i < event.getPointerCount(); i++) {
                            int pointId = event.getPointerId(i);
                            mCurrentFigure.getPoint(pointId).x = event.getX();
                            mCurrentFigure.getPoint(pointId).y = event.getY();

                        }
                        break;
                    case MotionEvent.ACTION_POINTER_UP:
                    case MotionEvent.ACTION_UP:
                        mFigures.add(mCurrentFigure);
                        mCurrentFigure = null;
                    case MotionEvent.ACTION_CANCEL:
                        break;
                    default:
                        return super.onTouchEvent(event);
                }

                if (currentPoint != null) {
                    currentPoint.x = event.getX(event.getActionIndex());
                    currentPoint.y = event.getY(event.getActionIndex());
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

                for (FigureDrawable figure : mFigures) {
                    for (PointF point : figure.mPoints) {
                        float x = point.x;
                        float y = point.y;

                        point.x = x - distanceX;
                        point.y = y - distanceY;
                    }
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

    public static class FigureDrawable extends Drawable {
        private Paint mPaint;
        private Paint mPolyPaint;

        private Path mPolyPath;
        private int mColor;
        private float mLineWidth = 8f;
        private List<PointF> mPoints = new ArrayList<>();

        public FigureDrawable(int color) {
            mColor = color;
            initPaint();
        }

        private void initPaint() {
            mPaint = new Paint();
            mPaint.setColor(mColor);
            mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
            mPaint.setStrokeCap(Paint.Cap.ROUND);
            mPaint.setStrokeWidth(mLineWidth);

            mPolyPaint = new Paint(mPaint);
            mPolyPaint.setStyle(Paint.Style.FILL);
        }

        @Override
        public void draw(@NonNull Canvas canvas) {
            switch (mPoints.size()) {
                case 1:
                    drawSinglePoint(mPoints.get(0), canvas);
                    break;
                case 2:
                    Log.d(TAG, "draw: " + mPoints.size());
                    drawLine(mPoints.get(0), mPoints.get(1), canvas);
                    break;
                default:
                    drawPolyFigure(canvas);
            }
        }

        public PointF getPoint(int index) {
            while (index >= mPoints.size()) {
                mPoints.add(new PointF());
            }

            return mPoints.get(index);
        }

        @Override
        public void setAlpha(int alpha) {
            mPaint.setAlpha(alpha);
        }

        @Override
        public void setColorFilter(@Nullable ColorFilter colorFilter) {
            mPaint.setColorFilter(colorFilter);
        }

        @Override
        public int getOpacity() {
            return PixelFormat.TRANSLUCENT;
        }

        private void drawSinglePoint(PointF point, Canvas canvas) {
            float x = point.x;
            float y = point.y;

            canvas.drawPoint(x, y, mPaint);
        }

        private void drawLine(PointF one, PointF two, Canvas canvas) {
            canvas.drawLine(one.x, one.y, two.x, two.y, mPaint);
        }

        private void drawPolyFigure(Canvas canvas) {
            if (mPolyPath == null) {
                mPolyPath = new Path();
            }

            mPolyPath.reset();

            for (PointF point : mPoints) {
                if (mPolyPath.isEmpty()) {
                    mPolyPath.moveTo(point.x, point.y);
                } else {
                    mPolyPath.lineTo(point.x, point.y);
                }
            }

            mPolyPath.close();
            canvas.drawPath(mPolyPath, mPolyPaint);
        }
    }
}
