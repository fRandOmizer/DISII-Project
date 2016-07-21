package com.distopia.everewidgets;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * Represents a circular widget for choosing values depending on relative angle changes between thumb and midpoint
 */
public class CircleView extends View {

    //constants
    private final float STROKE_WIDTH = 5f;

    // paints
    private Paint mSeparatorPaint;
    private Paint mTextPaint;
    private Paint mInnerFillPaint;
    private Paint mOuterFillPaint;

    // measurements
    private float mMaxOuterRadius;
    private float mInnerRadius;
    private Point mMidPoint;

    // interaction
    private double mAngle;
    private double mLastAngle;
    private String mText = "0";

    private Rect mTextBounds = new Rect();

    // Is informed on every new value update of this view.
    private OnUpdateListener mListener = null;
    private int mMin = 0;
    private int mMax = 1000;

    /**
     * Creates a new circle view
     * @param context The views context
     */
    public CircleView(Context context) {
        super(context);
        init();
    }

    /**
     * Creates a new circle view
     * @param context The views context
     * @param attrs Attributes from the XML file
     */
    public CircleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    /**
     * Creates a new circle view
     * @param context The views context
     * @param attrs Attributes from the XML file
     * @param defStyle Style information
     */
    public CircleView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    /**
     * Initialize anything needed
     */
    private void init() {
        mSeparatorPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mSeparatorPaint.setColor(Color.argb(255,38,38,38));
        mSeparatorPaint.setStyle(Paint.Style.STROKE);
        mSeparatorPaint.setStrokeWidth(STROKE_WIDTH);

        mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setColor(Color.argb(255,0,0,0));
        mTextPaint.setStyle(Paint.Style.FILL);

        mInnerFillPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mInnerFillPaint.setColor(Color.argb(255,255,255,255));
        mInnerFillPaint.setStyle(Paint.Style.FILL);

        mOuterFillPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mOuterFillPaint.setColor(Color.argb(255,115,115,115));
        mOuterFillPaint.setStyle(Paint.Style.FILL);

        mMaxOuterRadius = 0f;
        mInnerRadius = 0f;
        mMidPoint = new Point(0,0);

        mAngle = 0;
    }

    /**
     * Standard size changed callback
     * @param width
     * @param height
     * @param oldWidth
     * @param oldHeight
     */
    @Override
    public void onSizeChanged(int width, int height, int oldWidth, int oldHeight) {
        mMaxOuterRadius = Math.min(width - (getPaddingLeft() + getPaddingRight() + 2f * STROKE_WIDTH), height - (getPaddingTop() + getPaddingBottom() + 2 * STROKE_WIDTH)) / 2f;
        mInnerRadius = mMaxOuterRadius / 3f;

        mMidPoint = new Point(width / 2, height / 2);
    }

    /**
     * Standard draw callback
     * @param canvas
     */
    @Override
    protected void onDraw (Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawCircle(mMidPoint.x, mMidPoint.y, mMaxOuterRadius, mInnerFillPaint);

        canvas.drawArc(mMidPoint.x - mMaxOuterRadius, mMidPoint.y - mMaxOuterRadius, mMidPoint.x + mMaxOuterRadius, mMidPoint.y + mMaxOuterRadius, -90, (float) mAngle, true, mOuterFillPaint);

        // draw display area
        mText = Integer.toString((int) (mMin + (mMax - mMin) / 360f * mAngle));
        mTextPaint.setTextSize(mInnerRadius * 2);
        mTextPaint.getTextBounds(mText, 0, mText.length(), mTextBounds);
        canvas.drawText(mText, mMidPoint.x - mTextBounds.exactCenterX(), mMidPoint.y - mTextBounds.exactCenterY(), mTextPaint);
        canvas.drawCircle(mMidPoint.x, mMidPoint.y, mMaxOuterRadius, mSeparatorPaint);
    }

    /**
     * Standard touch event callback
     * @param event
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            mLastAngle = Math.toDegrees(Math.atan2(event.getY() - mMidPoint.y, event.getX() - mMidPoint.x)) + 90;

            if (mLastAngle < 0) {
                mLastAngle = 360 + mLastAngle;
            }

            return true;
        }
        else if (event.getAction() == MotionEvent.ACTION_MOVE) {
            double nextAngle = Math.toDegrees(Math.atan2(event.getY() - mMidPoint.y, event.getX() - mMidPoint.x)) + 90;

            if (nextAngle < 0) {
                nextAngle = 360 + nextAngle;
            }

            double changeAngle = nextAngle - mLastAngle;

            if (Math.abs(changeAngle) < 180) {
                mAngle += changeAngle;

                if (mAngle > 360) {
                    mAngle = 360;
                }

                if (mAngle < 0) {
                    mAngle = 0;
                }
            }

            mLastAngle = nextAngle;

            // TODO: Check if this makes sense here!
            if(mListener != null) {
                mListener.onUpdate((int) (mMin + (mMax - mMin) / 360f * mAngle));
            }

            invalidate();

            return true;
        }

        return false;
    }

    /**
     * Set mininmal value
     * @param min
     */
    public void setMin(int min)
    {
        this.mMin = min;
    }

    /**
     * Set maximal value
     * @param max
     */
    public void setMax(int max)
    {
        this.mMax = max;
    }

    public void setOnUpdateListener(OnUpdateListener listener) {
        mListener = listener;
    }

    /**
     * Every class that wants to listen to update events should implement this interface. A listener
     * can be registered by using the setOnUpdateListener() method of the CircleView.
     */
    public interface OnUpdateListener {
        /**
         * Is called when the circle updates its value.
         * @param degree The degree of of the circle, between 0 and 360.
         */
        void onUpdate(int degree);
    }
}
