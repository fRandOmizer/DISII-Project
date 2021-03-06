package com.distopia.everewidgets;

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
import android.os.Vibrator;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;

/**
 * TODO: document your custom view class.
 * TODO: add vibration feedback
 */
public class AbsoluteRegulatorView extends View {
    // constants
    public static final int BOTTOM_TO_TOP = 0;
    public static final int TOP_TO_BOTTOM = 1;
    public static final int LEFT_TO_RIGHT = 2;
    public static final int RIGHT_TO_LEFT = 3;

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

    // vibrator
    private Vibrator vibrator;

    // regulator specific attributes

    // image views which are displayed next to each other
    private int minImgId;
    private int maxImgId;

    private BitmapDrawable imageMin;
    private BitmapDrawable imageMax;
    private LayerDrawable layer;

    private int orientation;

    private int currentValueColor;
    private int targetValueColor;

    private int indicatorLineSize;
    private int indicatorTextSize;

    // regulator values
    private float minValue = 0;
    private float maxValue = 100;
    private float currentValue = 25;
    private float targetValue = 50;

    private String displayTextCurrent = "";
    private String displayTextTarget = "";
    private String unit = "";

    /**
     * The listener that is informed on slide event (if it's not null).
     */
    private OnUpdateListener mListener = null;

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

        if (a.hasValue((R.styleable.AbsoluteRegulatorView_minValue))) {
            setMinValue(a.getFloat(R.styleable.AbsoluteRegulatorView_minValue, 0));
        }
        if (a.hasValue((R.styleable.AbsoluteRegulatorView_maxValue))) {
            setMaxValue(a.getFloat(R.styleable.AbsoluteRegulatorView_maxValue, 100));
        }
        if (a.hasValue(R.styleable.AbsoluteRegulatorView_minImage)) {
            minImgId = a.getResourceId(R.styleable.AbsoluteRegulatorView_minImage, 0);
        }
        if (a.hasValue(R.styleable.AbsoluteRegulatorView_maxImage)) {
            maxImgId = a.getResourceId(R.styleable.AbsoluteRegulatorView_maxImage, 0);
        }
        if (a.hasValue(R.styleable.AbsoluteRegulatorView_currentValueColor)) {
            currentValueColor = a.getColor(R.styleable.AbsoluteRegulatorView_currentValueColor, 0);
        }
        if (a.hasValue(R.styleable.AbsoluteRegulatorView_targetValueColor)) {
            targetValueColor = a.getColor(R.styleable.AbsoluteRegulatorView_targetValueColor, 0);
        }
        if (a.hasValue(R.styleable.AbsoluteRegulatorView_indicatorLineSize)) {
            indicatorLineSize = a.getInteger(R.styleable.AbsoluteRegulatorView_indicatorLineSize, 0);
        }
        if (a.hasValue(R.styleable.AbsoluteRegulatorView_indicatorTextSize)) {
            indicatorTextSize = a.getInteger(R.styleable.AbsoluteRegulatorView_indicatorTextSize, 0);
        }
        if (a.hasValue(R.styleable.AbsoluteRegulatorView_orientation)) {
            orientation = a.getInteger(R.styleable.AbsoluteRegulatorView_orientation, 0);
        }
        if (a.hasValue(R.styleable.AbsoluteRegulatorView_unit)) {
            unit = a.getString(R.styleable.AbsoluteRegulatorView_unit);
        }

        a.recycle();

        // add vibrator
        if (!this.isInEditMode()) {
            vibrator = (Vibrator) this.getContext().getSystemService(Context.VIBRATOR_SERVICE);
        }

        // Set up a default TextPaint object
        mTextPaint = new TextPaint();
        mTextPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setTextAlign(Paint.Align.LEFT);

        // paint objects for value indicator lines
        targetPaint = new Paint();
        targetPaint.setColor(targetValueColor);
        targetPaint.setStrokeWidth(indicatorLineSize);

        currentPaint = new Paint();
        currentPaint.setColor(currentValueColor);
        currentPaint.setStrokeWidth(indicatorLineSize);

        invalidateTextPaintAndMeasurements();
    }

    private void invalidateTextPaintAndMeasurements() {
        mTextPaint.setTextSize(indicatorTextSize);
        mTextPaint.setColor(Color.BLACK);
        mTextWidth = mTextPaint.measureText(Float.toString(maxValue));

        Paint.FontMetrics fontMetrics = mTextPaint.getFontMetrics();
        mTextHeight = fontMetrics.bottom;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if ((width != getWidth()) && (height != getHeight())) {
            width = getWidth();
            height = getHeight();
            resizeImages(width, height);
        }

        // set values according to orientation
        float ncv = 0;
        float ntv = 0;
        int x1 = 0;
        int y1 = 0;
        int x2 = width;
        int y2 = height;
        if (orientation == BOTTOM_TO_TOP) {
            ncv = 1 - normalizeValue(currentValue);
            ntv = 1 - normalizeValue(targetValue);
            y1 = Math.round(height * ncv);
        } else if ((orientation == TOP_TO_BOTTOM)) {
            ncv = normalizeValue(currentValue);
            ntv = normalizeValue(targetValue);
            y2 = Math.round(height * ncv);
        }

        // draw images
        Rect bounds = new Rect(0, 0, width, height);
        layer.setBounds(bounds);
        imageMin.setBounds(bounds);
        bounds.set(x1, y1, x2, y2);
        imageMax.setBounds(bounds);
        layer.getDrawable(1).setBounds(bounds);
        layer.draw(canvas);

        // draw lines
        int textHeightCur = Math.round(height * ncv + 2 * (mTextHeight + indicatorLineSize + 2));
        int textHeightTar = Math.round(height * ntv - mTextHeight + 2);
        // if tar < 0
        if (textHeightTar < ((mTextHeight + indicatorLineSize + 2) * 2)) {
            textHeightTar = Math.round(height * ntv + 2 * (mTextHeight + indicatorLineSize + 2));
        }
        // if cur and tar overlap
        if ((textHeightCur >= (textHeightTar - 2 * (mTextHeight + indicatorLineSize + 2))) && ((textHeightCur <= (textHeightTar + (mTextHeight + indicatorLineSize + 2))))) {
            textHeightCur -= 2 * (mTextHeight + indicatorLineSize + 2);
        }

        mTextWidth = mTextPaint.measureText(displayTextTarget + unit);
        canvas.drawLine(width * 0.75f, height * ntv, width, height * ntv, targetPaint);
        mTextPaint.setColor(targetValueColor);
        canvas.drawText(displayTextTarget, width - mTextWidth, textHeightTar, mTextPaint);

        // only print current if nessesary
        if (!displayTextTarget.contentEquals(displayTextCurrent)) {
            mTextWidth = mTextPaint.measureText(displayTextCurrent + unit);
            canvas.drawLine(width * 0.75f, height * ncv, width, height * ncv, currentPaint);
            mTextPaint.setColor(currentValueColor);
            canvas.drawText(displayTextCurrent, width - mTextWidth, textHeightCur, mTextPaint);
        }
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
        displayTextCurrent = printValueWithUnit(currentValue);
        invalidate();
    }

    public float getTargetValue() {
        return targetValue;
    }

    public void setTargetValue(float targetValue) {
        if (targetValue < minValue) {
            this.targetValue = minValue;
            if (!this.isInEditMode()) {
                vibrator.vibrate(2);
            }
        } else if (targetValue > maxValue) {
            this.targetValue = maxValue;
            if (!this.isInEditMode()) {
                vibrator.vibrate(2);
            }
        } else {
            this.targetValue = targetValue;
        }
        displayTextTarget = printValueWithUnit(this.targetValue);
        if(this.mListener != null) {
            this.mListener.onUpdate(this.targetValue);
        }
        invalidate();
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        // get vertical pos
        float y = e.getY();
        // linear transform to target value
        float value = mapToRange(y, 0, height, 0, 1);
        // invert if bottom to top
        if (orientation == BOTTOM_TO_TOP) {
            value = 1 - value;
        }
        value = mapToRange(value, 0, 1, minValue, maxValue);
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

    private void resizeImages(int w, int h) {
        // create new images
        Bitmap bmMin = BitmapFactory.decodeResource(getResources(), minImgId);
        Bitmap bmMax = BitmapFactory.decodeResource(getResources(), maxImgId);
        imageMin = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(bmMin, w, h, false));
        imageMax = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(bmMax, w, h, false));

        if (orientation == TOP_TO_BOTTOM) {
            imageMin.setGravity(Gravity.TOP);
            imageMax.setGravity(Gravity.TOP);
        } else {
            imageMin.setGravity(Gravity.BOTTOM);
            imageMax.setGravity(Gravity.BOTTOM);
        }

        ClipDrawable clippy;
        clippy = new ClipDrawable(imageMax, Gravity.BOTTOM, 2);
        clippy.setVisible(true, true);
        clippy.setLevel(10000);

        Drawable[] array = new Drawable[2];
        array[0] = imageMin;
        array[1] = clippy;
        layer = new LayerDrawable(array);
    }

    private float normalizeValue(float value) {
        if (orientation == TOP_TO_BOTTOM) {
            return mapToRange(value, getMinValue(), getMaxValue(), 0, 1);
        } else {
            return mapToRange(value, getMinValue(), getMaxValue(), 0, 1);
        }
    }

    private float mapToRange(float value, float inMin, float inMax, float outMin, float outMax) {
        return outMin + ((outMax - outMin) / (inMax - inMin)) * (value - inMin);
    }

    public void setOnUpdateListener(OnUpdateListener listener) {
        this.mListener = listener;
    }

    private String printValueWithUnit(float value) {
        String rounded = Float.toString(Math.round(value * 10) / 10.0f);
        rounded += unit;
        return rounded;
    }

    /**
     * Every class that wants to listen to update events should implement this interface. A listener
     * can be registered by using the setOnUpdateListener() method of the AbsoluteRegulatorView.
     */
    public interface OnUpdateListener {
        /**
         * Is called when the regulator view updates its value.
         *
         * @param value The target value the user selected, in percent between 0 and 100.
         */
        void onUpdate(float value);
    }
}
