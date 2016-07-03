package com.distopia.absoluteregulatortestapp;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.RelativeLayout;

/**
 * TODO: document your custom view class.
 * TODO: add touch events
 * TODO: draw current and target values
 * TODO: add vibration feedback
 * TODO: add view attributes
 * TODO: delete default stuff
 * TODO: fix drawing
 */
public class AbsoluteRegulatorView extends RelativeLayout {
    // default stuff
    private Drawable mExampleDrawable;

    private TextPaint mTextPaint;
    private float mTextWidth;
    private float mTextHeight;

    // paint objects
    private Paint currentPaint;
    private Paint targetPaint;

    // regulator specific attributes

    // image views which are displayed next to each other
    private ImageView imageMin;
    private ImageView imageMax;

    // regulator values
    private float minValue = 0;
    private float maxValue = 100;
    private float currentValue = 25;
    private float targetValue = 50;

    public AbsoluteRegulatorView(Context context) {
        super(context);
        init(null, 0);
    }

    public AbsoluteRegulatorView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public AbsoluteRegulatorView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {
        // Load attributes
        final TypedArray a = getContext().obtainStyledAttributes(
                attrs, R.styleable.AbsoluteRegulatorView, defStyle, 0);

        if (a.hasValue(R.styleable.AbsoluteRegulatorView_exampleDrawable)) {
            mExampleDrawable = a.getDrawable(
                    R.styleable.AbsoluteRegulatorView_exampleDrawable);
            mExampleDrawable.setCallback(this);
        }

        a.recycle();

        // Set up a default TextPaint object
        mTextPaint = new TextPaint();
        mTextPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setTextAlign(Paint.Align.LEFT);

        // paint objects for value indicator lines
        targetPaint = new Paint();
        targetPaint.setColor(Color.RED);
        targetPaint.setStrokeWidth(5);

        currentPaint = new Paint();
        currentPaint.setColor(Color.BLUE);
        currentPaint.setStrokeWidth(5);

        // add images
        inflate(getContext(), R.layout.absolute_regulator, this);
        /*imageMin = (ImageView) findViewById(R.id.imageMin);
        imageMin.setImageResource(R.drawable.thermometer_min);

        imageMax = (ImageView) findViewById(R.id.imageMax);
        imageMax.setImageResource(R.drawable.thermometer_max);*/

        // Update TextPaint and text measurements from attributes
        invalidateTextPaintAndMeasurements();
    }

    private void invalidateTextPaintAndMeasurements() {
        mTextPaint.setTextSize(12);
        mTextPaint.setColor(Color.BLACK);
        mTextWidth = mTextPaint.measureText("Absolute Regulator");

        Paint.FontMetrics fontMetrics = mTextPaint.getFontMetrics();
        mTextHeight = fontMetrics.bottom;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // TODO: consider storing these as member variables to reduce
        // allocations per draw cycle.
        int paddingLeft = getPaddingLeft();
        int paddingTop = getPaddingTop();
        int paddingRight = getPaddingRight();
        int paddingBottom = getPaddingBottom();

        int contentWidth = getWidth() - paddingLeft - paddingRight;
        int contentHeight = getHeight() - paddingTop - paddingBottom;

        // Draw the text.
        canvas.drawText("Absolute Regulator",
                paddingLeft + (contentWidth - mTextWidth) / 2,
                paddingTop + (contentHeight + mTextHeight) / 2,
                mTextPaint);

        // Draw the example drawable on top of the text.
        if (mExampleDrawable != null) {
            mExampleDrawable.setBounds(paddingLeft, paddingTop,
                    paddingLeft + contentWidth, paddingTop + contentHeight);
            mExampleDrawable.draw(canvas);
        }

        // draw lines
        canvas.drawLine(canvas.getWidth()/2, canvas.getHeight()*currentValue, canvas.getWidth(), canvas.getHeight()*currentValue, currentPaint);
        canvas.drawLine(canvas.getWidth()/2, canvas.getHeight()*targetValue, canvas.getWidth(), canvas.getHeight()*targetValue, targetPaint);

    }

    /**
     * Gets the example drawable attribute value.
     *
     * @return The example drawable attribute value.
     */
    public Drawable getExampleDrawable() {
        return mExampleDrawable;
    }

    /**
     * Sets the view's example drawable attribute value. In the example view, this drawable is
     * drawn above the text.
     *
     * @param exampleDrawable The example drawable attribute value to use.
     */
    public void setExampleDrawable(Drawable exampleDrawable) {
        mExampleDrawable = exampleDrawable;
    }

    // getter and setter
    public float getMinValue() {
        return minValue;
    }

    public void setMinValue(float minValue) {
        this.minValue = minValue;
        invalidate();
        requestLayout();
    }

    public float getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(float maxValue) {
        this.maxValue = maxValue;
        invalidate();
        requestLayout();
    }

    public float getCurrentValue() {
        return currentValue;
    }

    public void setCurrentValue(float currentValue) {
        if (currentValue < minValue) {
            this.currentValue = minValue;
        } else if (currentValue > maxValue) {
            this.currentValue = maxValue;
        } else {
            this.currentValue = currentValue;
        }
        invalidate();
    }

    public float getTargetValue() {
        return targetValue;
    }

    public void setTargetValue(float targetValue) {
        if (targetValue < minValue) {
            this.targetValue = minValue;
        } else if (targetValue > maxValue) {
            this.targetValue = maxValue;
        } else {
            this.targetValue = targetValue;
        }
        invalidate();
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        // get vertical pos
        float y = e.getY();
        // linear transform to target value
        float value = (y - 0) / (getHeight() - 0) * (maxValue - minValue) + minValue;
        // set slider value
        this.setTargetValue(value);
        return true;
    }
}
