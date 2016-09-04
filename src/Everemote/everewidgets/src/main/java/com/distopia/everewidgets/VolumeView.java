package com.distopia.everewidgets;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by Richard Zan
 */
public class VolumeView extends View {

    private TextPaint paintText;
    private Paint paintWaves;

    private float initialX;
    private float initialY;

    public float CurrentValue;

    private float Max;
    private float Min;
    private int WavesColor;
    private int NumberOfWaves;

    // Is informed on every new value update of this view.
    private OnUpdateListener mListener = null;

    public VolumeView(Context context, AttributeSet attrs)
    {
        super(context, attrs);

        //setting the text painter properties
        paintText = new TextPaint(Paint.ANTI_ALIAS_FLAG | Paint.SUBPIXEL_TEXT_FLAG);
        paintText.setTextAlign(Paint.Align.CENTER);
        paintText.setColor(Color.BLACK);
        paintText.setTextSize(20);

        //setting the drawing painter properties
        paintWaves = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintWaves.setColor(WavesColor);
        paintWaves.setStrokeWidth(10);
        paintWaves.setStyle(Paint.Style.STROKE);

        //inner values for motion capturing
        initialX = 0.0f;
        initialY = 0.0f;

        //widget properties
        CurrentValue = 50.0f;
        Max = 100.0f;
        Min = 0.0f;
        WavesColor = Color.BLACK;
        paintWaves.setColor(WavesColor);
        NumberOfWaves = 5;
    }

    //setter
    public void setView(float min, float max, float currentValue, int wavesColor, int numberOfWaves)
    {
        CurrentValue = currentValue;
        Min=min;
        Max=max;
        WavesColor=wavesColor;
        NumberOfWaves = numberOfWaves;

        this.invalidate();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        //temp values for calculation
        float diffX = 0.0f;
        float diffY = 0.0f;
        float diff = 0.0f;
        float sign = 1.0f;
        float speed = 200.0f;

        switch (event.getActionMasked()) {

            case MotionEvent.ACTION_DOWN:
                initialX = event.getX();
                initialY = event.getY();
                break;

            case MotionEvent.ACTION_MOVE:
                if (initialY<event.getY())
                {
                    sign = -1;
                }
                //calculating euclidean distance + difference which will be use for widget
                diffX = (initialX - event.getX()) * (initialX - event.getX());
                diffY = (initialY - event.getY()) * (initialY - event.getY());
                diff = sign * ((float) Math.sqrt(diffX+diffY)) / (super.getMeasuredHeight()/speed);

                //checking if we got to the boundaries
                if (diff + CurrentValue > Min && diff + CurrentValue < Max)
                {
                    CurrentValue += diff;
                }
                else
                {
                    //checking if we got the minimum or maximum
                    if(diff + CurrentValue < Min)
                    {
                        CurrentValue = Min;
                    }

                    if(diff + CurrentValue > Max)
                    {
                        CurrentValue = Max;
                    }
                }

                //changing global variables
                initialX = event.getX();
                initialY = event.getY();
                
                if(mListener != null) {
                    mListener.onUpdate(CurrentValue);
                }

                break;

            case MotionEvent.ACTION_UP:
                if (initialY<event.getY())
                {
                    sign = -1;
                }
                //calculating euclidean distance + difference which will be use for widget
                diffX = (initialX - event.getX()) * (initialX - event.getX());
                diffY = (initialY - event.getY()) * (initialY - event.getY());
                diff = sign * ((float) Math.sqrt(diffX+diffY)) / (super.getMeasuredHeight()/speed);

                //checking if we got to the boundaries
                if (diff + CurrentValue > Min && diff + CurrentValue < Max)
                {
                    CurrentValue += diff;
                }
                else
                {
                    //checking if we got the minimum or maximum
                    if(diff + CurrentValue < Min)
                    {
                        CurrentValue = Min;
                    }

                    if(diff + CurrentValue > Max)
                    {
                        CurrentValue = Max;
                    }
                }

                //changing global variables
                initialX = 0.0f;
                initialY = 0.0f;

                // TODO: Check if this makes sense here!
                //if(mListener != null) {
                //    mListener.onUpdate(CurrentValue);
                //}

                break;

            case MotionEvent.ACTION_CANCEL:
                break;

            case MotionEvent.ACTION_OUTSIDE:
                break;
        }

        this.invalidate();

        return true;
    }

    @Override
    public void onDraw(Canvas canvas)
    {
        //temp variables used for center of widget
        float widthCenter = super.getMeasuredWidth()/2;
        float heightCenter = 2*super.getMeasuredHeight()/3;

        //drawing text
        canvas.drawText(Integer.toString(Math.round(CurrentValue)), widthCenter, heightCenter+15, paintText);

        //drawing waves
        for (int i=0; i<NumberOfWaves; i++)
        {
            //calculating radius, from smallest to biggest
            float radius = (i+1)*widthCenter/(NumberOfWaves);

            //creating circle
            final RectF oval = new RectF();
            oval.set(widthCenter - radius,
                    heightCenter - radius,
                    widthCenter + radius,
                    heightCenter + radius);

            //checking if we should draw another wave
            if(((i)*(Max-Min)/NumberOfWaves)<=(CurrentValue-Min))
            {
                if(CurrentValue != Min)
                {
                    canvas.drawArc(oval, 240, 60, false, paintWaves);
                }
            }
        }
    }

    public void setOnUpdateListener(OnUpdateListener listener) {
        mListener = listener;
    }

    /**
     * Every class that wants to listen to update events should implement this interface. A listener
     * can be registered by using the setOnUpdateListener() method of the VolumeView.
     */
    public interface OnUpdateListener {
        /**
         * Is called when the volume view updates its value.
         * @param value The value the user selected, in percent between 0 and 100.
         */
        void onUpdate(float value);
    }
}