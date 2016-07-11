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
 * Created by Adrian on 27.06.2016.
 */
public class CircleWidget extends View {

    //constants
    final float strokeWidth = 5f;

    // paints
    Paint mSeparatorPaint;
    Paint mTextPaint;
    Paint mInnerFillPaint;
    Paint mOuterFillPaint;

    // measurements
    float mMaxOuterRadius;
    float mInnerRadius;
    Point mMidPoint;

    // interaction
    double mAngle;
    double mLastAngle;
    String mText = "0";

    private Rect textBounds = new Rect();

    public CircleWidget(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        mSeparatorPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mSeparatorPaint.setColor(Color.argb(255,38,38,38));
        mSeparatorPaint.setStyle(Paint.Style.STROKE);
        mSeparatorPaint.setStrokeWidth(strokeWidth);

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

    @Override
    public void onSizeChanged(int width, int height, int oldWidth, int oldHeight) {
        mMaxOuterRadius = Math.min(width - (getPaddingLeft() + getPaddingRight() + 2f * strokeWidth), height - (getPaddingTop() + getPaddingBottom() + 2 * strokeWidth)) / 2f;
        mInnerRadius = mMaxOuterRadius / 3f;

        mMidPoint = new Point(width / 2, height / 2);
    }

    @Override
    protected void onDraw (Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawCircle(mMidPoint.x, mMidPoint.y, mMaxOuterRadius, mInnerFillPaint);

        canvas.drawArc(mMidPoint.x - mMaxOuterRadius, mMidPoint.y - mMaxOuterRadius, mMidPoint.x + mMaxOuterRadius, mMidPoint.y + mMaxOuterRadius, -90, (float) mAngle, true, mOuterFillPaint);

        // draw display area
        mText = Integer.toString((int) mAngle);
        mTextPaint.setTextSize(mInnerRadius * 2);
        mTextPaint.getTextBounds(mText, 0, mText.length(), textBounds);
        canvas.drawText(mText, mMidPoint.x - textBounds.exactCenterX(), mMidPoint.y - textBounds.exactCenterY(), mTextPaint);
        canvas.drawCircle(mMidPoint.x, mMidPoint.y, mMaxOuterRadius, mSeparatorPaint);
    }

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

            invalidate();

            return true;
        }

        return false;
    }
}
