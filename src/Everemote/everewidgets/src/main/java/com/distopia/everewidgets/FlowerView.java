package com.distopia.everewidgets;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * Represents a circular widget with "flower pedals" that grow according to selection via thumb roll
 */
public class FlowerView extends View {

    //constants
    private final float STROKE_WIDTH = 5f;
    private final double DONT_GROW_ANGLE = -1;

    // paints
    private Paint mSeparatorPaint;
    private Paint mTextPaint;
    private Paint mInnerFillPaint;
    private Paint mOuterFillPaint;
    private Paint mMarkedOuterFillPaint;

    // measurements
    private float mMaxOuterRadius;
    private float mDefaultOuterRadius;
    private float mInnerRadius;
    private Point mMidPoint;

    // interaction
    private double mFirstContactX;
    private double mFirstContactY;
    private double mAngleOfMaxGrowth;
    private int mMarkedId = 0;

    private int[] mIconData;
    private Drawable[] mIconDataDrawables;
    private int arcCount = 10;
    private static Path[] mPathArray;
    private Rect mTextBounds = new Rect();

    // Is informed on every new value update of this view.
    private OnUpdateListener mListener = null;

    /**
     * Creates a new circle view
     * @param context The views context
     */
    public FlowerView(Context context) {
        super(context);
        init();
    }

    /**
     * Creates a new circle view
     * @param context The views context
     * @param attrs Attributes from the XML file
     */
    public FlowerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    /**
     * Creates a new circle view
     * @param context The views context
     * @param attrs Attributes from the XML file
     * @param defStyle Style information
     */
    public FlowerView(Context context, AttributeSet attrs, int defStyle) {
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

        mMarkedOuterFillPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mMarkedOuterFillPaint.setColor(Color.argb(255,217,217,217));
        mMarkedOuterFillPaint.setStyle(Paint.Style.FILL);

        mMaxOuterRadius = 0f;
        mDefaultOuterRadius = 0f;
        mInnerRadius = 0f;
        mMidPoint = new Point(0,0);

        mAngleOfMaxGrowth = DONT_GROW_ANGLE;

        mIconData = new int[0];
        mIconDataDrawables = new Drawable[0];
        arcCount = mIconData.length;

        mPathArray = new Path[arcCount];
        for (int i = 0; i < arcCount; i++) {
            mPathArray[i] = new Path();
        }
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
        mDefaultOuterRadius = mMaxOuterRadius / 3f * 2f;
        mInnerRadius = mDefaultOuterRadius / 2f;

        mMidPoint = new Point(width / 2, height / 2);
    }

    /**
     * Setter for displayed icons
     * @param iconData
     */
    public void setIconData(int[] iconData)
    {
        this.mIconData = iconData;
        this.arcCount = this.mIconData.length;

        this.mIconDataDrawables = new Drawable[this.mIconData.length];
        for (int i = 0; i < this.mIconData.length; i++)
        {
            this.mIconDataDrawables[i] = getResources().getDrawable(this.mIconData[i], null);
        }

        mPathArray = new Path[arcCount];
        for (int i = 0; i < arcCount; i++) {
            mPathArray[i] = new Path();
        }

        this.invalidate();
    }

    /**
     * Angle to circle x conversion
     * @param angle
     * @param radius
     * @param offset
     * @return
     */
    private float angleToCircleX(double angle, double radius, double offset) {
        return (float)(offset + Math.cos(Math.toRadians(angle)) * radius);
    }

    /**
     * Angle to circle y conversion
     * @param angle
     * @param radius
     * @param offset
     * @return
     */
    private float angleToCircleY(double angle, double radius, double offset) {
        return (float)(offset + Math.sin(Math.toRadians(angle)) * radius);
    }

    /**
     * Angle to radius using a bell curve function
     * @param angle
     * @param maxGrowthAngle
     * @return
     */
    private float determineGrowthFactorForAngle(double angle, double maxGrowthAngle) {
        if (maxGrowthAngle == DONT_GROW_ANGLE) {
            return 0f;
        }

        float result = Math.abs((0.5f - (float)(Math.abs(angle - maxGrowthAngle) / 360d)) * 2f);
        return (float)Math.min(Math.exp(-Math.pow((1f - result)*5,2))*1.05f, 1.0f);
    }

    /**
     * Standard draw callback
     * @param canvas
     */
    @Override
    protected void onDraw (Canvas canvas) {
        super.onDraw(canvas);

        // calculate growth
        double growthFactor = (mMaxOuterRadius - mDefaultOuterRadius);
        boolean alreadyMarked = false;

        for (int i = 0; i < arcCount; i++) {
            mPathArray[i].reset();
            mPathArray[i].moveTo(mMidPoint.x, mMidPoint.y);

            int arcPartCount = 20;
            double baseAngle = (360d / arcCount) * i;
            double angleStep = (360d / arcCount) / arcPartCount;
            boolean marked = false;

            for (int j = 0; j <= arcPartCount; j++) {
                double angle = baseAngle + angleStep * j;
                double size = determineGrowthFactorForAngle(angle, mAngleOfMaxGrowth) * growthFactor;
                if (determineGrowthFactorForAngle(angle, mAngleOfMaxGrowth) == 1f && !alreadyMarked) {
                    marked = true;
                    alreadyMarked = true;
                }
                mPathArray[i].lineTo(angleToCircleX(angle, mDefaultOuterRadius + size, mMidPoint.x), angleToCircleY(angle, mDefaultOuterRadius + size, mMidPoint.y));
            }

            if (marked){
                canvas.drawPath(mPathArray[i], mMarkedOuterFillPaint);
            } else {
                canvas.drawPath(mPathArray[i], mOuterFillPaint);
            }
            canvas.drawPath(mPathArray[i], mSeparatorPaint);

            double angle = (((360d / arcCount) * i) + ((360d / arcCount) * (i+1))) / 2d;
            double size = determineGrowthFactorForAngle(angle, mAngleOfMaxGrowth) * growthFactor;
            double minSize = Math.max((mDefaultOuterRadius - mInnerRadius)/2, size);
            float left = angleToCircleX(angle, (mInnerRadius+mDefaultOuterRadius)/2f + size/2, mMidPoint.x) - (float)minSize / 2;
            float top = angleToCircleY(angle, (mInnerRadius+mDefaultOuterRadius)/2f + size/2, mMidPoint.y) - (float)minSize / 2;
            float right = left + (float)minSize;
            float bottom = top + (float)minSize;
            mIconDataDrawables[i].setBounds((int)left, (int)top, (int)right, (int)bottom);
            mIconDataDrawables[i].draw(canvas);


            if (marked) {
                mMarkedId = i;
            }
        }

        // draw display area
        canvas.drawCircle(mMidPoint.x, mMidPoint.y, mInnerRadius, mInnerFillPaint);
        canvas.drawCircle(mMidPoint.x, mMidPoint.y, mInnerRadius, mSeparatorPaint);
        if (mMarkedId < arcCount)
        {
            float size = mInnerRadius * 1.5f;
            float left = mMidPoint.x - size / 2;
            float top = mMidPoint.y  - size / 2;
            float right = left + size;
            float bottom = top + size;
            mIconDataDrawables[mMarkedId].setBounds((int)left, (int)top, (int)right, (int)bottom);
            mIconDataDrawables[mMarkedId].draw(canvas);
        }
    }

    /**
     * Standard touch event callback
     * @param event
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            mFirstContactX = event.getX();
            mFirstContactY = event.getY();
            return true;
        }
        else if (event.getAction() == MotionEvent.ACTION_MOVE) {
            double nextAngle = Math.toDegrees(Math.atan2(event.getY() - mFirstContactY, event.getX() - mFirstContactX));
            if (nextAngle < 0) {
                nextAngle = 360 + nextAngle;
            }

            mAngleOfMaxGrowth = nextAngle;
            invalidate();

            return true;
        }
        else if (event.getAction() == MotionEvent.ACTION_UP) {
            mAngleOfMaxGrowth = DONT_GROW_ANGLE;
            invalidate();
            if(mListener != null) {
                mListener.onUpdate(mMarkedId);
            }
            return true;
        }

        return false;
    }

    public void setOnUpdateListener(OnUpdateListener listener) {
        mListener = listener;
    }

    /**
     * Every class that wants to listen to update events should implement this interface. A listener
     * can be registered by using the setOnUpdateListener() method of the FlowerView.
     */
    public interface OnUpdateListener {
        /**
         * Is called when the flower view updates its value.
         * @param value The value the user selected.
         */
        void onUpdate(int value);
    }
}
