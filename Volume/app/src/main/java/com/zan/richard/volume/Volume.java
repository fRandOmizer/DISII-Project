package com.zan.richard.volume;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by zanri_000 on 03-Jul-16.
 */
public class Volume extends View {
    private boolean ShowLines;
    Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private String TAG = "RichardZan";
    private float initialX = 0.0f;
    private float initialY = 0.0f;
    public float CurrentValue = 50.0f;
    public float Max = 100.0f;
    public float Min = 0.0f;


    public Volume(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.ShowLines = false;

        paint.setColor(Color.BLACK);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float diffX = 0.0f;
        float diffY = 0.0f;
        float diff = 0.0f;
        float sign = 1.0f;

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
                diffX = (initialX - event.getX()) * (initialX - event.getX());
                diffY = (initialY - event.getY()) * (initialY - event.getY());
                diff = sign * ((float) Math.sqrt(diffX+diffY)) / (super.getMeasuredHeight()/256);

                if (diff + CurrentValue > Min && diff + CurrentValue < Max)
                {
                    CurrentValue += diff;
                }
                else
                {
                    if(diff + CurrentValue < Min)
                    {
                        CurrentValue = Min;
                    }

                    if(diff + CurrentValue > Max)
                    {
                        CurrentValue = Max;
                    }
                }

                initialX = event.getX();
                initialY = event.getY();
                break;

            case MotionEvent.ACTION_UP:
                if (initialY<event.getY())
                {
                    sign = -1;
                }
                diffX = (initialX - event.getX()) * (initialX - event.getX());
                diffY = (initialY - event.getY()) * (initialY - event.getY());
                diff = sign * ((float) Math.sqrt(diffX+diffY)) / (super.getMeasuredHeight()/256);

                if (diff + CurrentValue > Min && diff + CurrentValue < Max)
                {
                    CurrentValue += diff;
                }
                else
                {
                    if(diff + CurrentValue < Min)
                    {
                        CurrentValue = Min;
                    }

                    if(diff + CurrentValue > Max)
                    {
                        CurrentValue = Max;
                    }
                }

                initialX = 0.0f;
                initialY = 0.0f;
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
    public void onDraw(Canvas canvas) {

        float widthCenter = super.getMeasuredWidth()/2;
        float heightBase = 2*super.getMeasuredHeight()/3;
        float currentLevel = 2 * (super.getMeasuredHeight()/3)
                - (super.getMeasuredHeight()/(3*(Max-Min)))*CurrentValue;

        paint.setColor(Color.BLACK);
        paint.setTextSize(20);
        canvas.drawText(Integer.toString(Math.round(CurrentValue)), widthCenter, heightBase+10, paint);

        Paint myPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        myPaint.setStrokeWidth(20);
        myPaint.setColor(0xffff0000);   //color.RED

        canvas.drawLine(widthCenter,
                currentLevel,
                widthCenter,
                heightBase,
                myPaint);


    }
}