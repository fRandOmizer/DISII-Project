package com.distopia.absoluteregulatortestapp;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;

/**
 * TODO: document your custom view class.
 * TODO: draw current and target values (improve)
 * TODO: add vibration feedback
 * TODO: add view attributes
 * TODO: delete default stuff
 */
public class AbsoluteRegulatorView extends View {
    // default stuff
    private TextPaint mTextPaint;
    private float mTextWidth;
    private float mTextHeight;

    // dimensions
    private int width;
    private int height;

    // paint objects
    private Paint currentPaint;
    private Paint targetPaint;

    // regulator specific attributes

    // image views which are displayed next to each other
    private BitmapDrawable imageMin;
    private BitmapDrawable imageMax;
    private LayerDrawable layer;

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

        // TODO: remove
        /*if (a.hasValue(R.styleable.AbsoluteRegulatorView_exampleDrawable)) {
            mExampleDrawable = a.getDrawable(
                    R.styleable.AbsoluteRegulatorView_exampleDrawable);
            mExampleDrawable.setCallback(this);
        }*/

        a.recycle();

        // Set up a default TextPaint object
        mTextPaint = new TextPaint();
        mTextPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setTextAlign(Paint.Align.LEFT);

        // paint objects for value indicator lines
        targetPaint = new Paint();
        targetPaint.setColor(Color.RED);
        targetPaint.setStrokeWidth(50);

        currentPaint = new Paint();
        currentPaint.setColor(Color.BLUE);
        currentPaint.setStrokeWidth(50);

        // add images
        width = 900;
        height = 1400;
        resizeImages(width, height);

        ClipDrawable clippy = new ClipDrawable(imageMax, Gravity.BOTTOM, 2);
        clippy.setVisible(true, true);
        clippy.setLevel(10000);

        Drawable[] array = new Drawable[2];
        array[0] = imageMin;
        array[1] = clippy;
        layer = new LayerDrawable(array);

        // Update TextPaint and text measurements from attributes
        invalidateTextPaintAndMeasurements();
    }

    private void invalidateTextPaintAndMeasurements() {
        mTextPaint.setTextSize(75);
        mTextPaint.setColor(Color.BLACK);
        mTextWidth = mTextPaint.measureText("99");

        Paint.FontMetrics fontMetrics = mTextPaint.getFontMetrics();
        mTextHeight = fontMetrics.bottom;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if ((width != getWidth()) && (height != getHeight()) ) {
            width = getWidth();
            height = getHeight();
            resizeImages(width, height);
        }

        // draw images
        Rect bounds = new Rect(0,0,width,height);
        layer.setBounds(bounds);
        imageMin.setBounds(bounds);
        bounds.set(0, Math.round(height*currentValue/maxValue), width, height);
        imageMax.setBounds(bounds);
        layer.getDrawable(1).setBounds(bounds);
        layer.draw(canvas);

        // draw lines
        canvas.drawLine(width/2,height*currentValue/maxValue,width,height*currentValue/maxValue,currentPaint);
        canvas.drawText(Float.toString(currentValue), width - mTextWidth, height*currentValue/maxValue-mTextHeight, mTextPaint);
        canvas.drawLine(width/2,height*targetValue/maxValue,width,height*targetValue/maxValue,targetPaint);
        canvas.drawText(Float.toString(targetValue), width - mTextWidth, height*targetValue/maxValue-mTextHeight, mTextPaint);
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
        this.currentValue = currentValue;
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
        setCurrentValue(targetValue);
        invalidate();
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        // get vertical pos
        float y = e.getY();
        // linear transform to target value
        float value = (y) / (getHeight()) * (maxValue - minValue) + minValue;
        // set slider value
        this.setTargetValue(value);
        return true;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        // scale pictures
        resizeImages(w, h);
    }

    private void resizeImages(int w, int h)
    {
        // create new images
        Bitmap bmMin = BitmapFactory.decodeResource(getResources(), R.drawable.thermometer_min);
        Bitmap bmMax = BitmapFactory.decodeResource(getResources(), R.drawable.thermometer_max);
        imageMin = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(bmMin, w, h, false));
        imageMin.setGravity(Gravity.BOTTOM);
        imageMax = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(bmMax, w, h, false));
        imageMax.setGravity(Gravity.BOTTOM);
    }
}
