package com.example.adrian.playground;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RemoteViews;


@RemoteViews.RemoteView
public class CardLayout extends ViewGroup {
    private Paint mBoxPaint;
    private Paint mTextPaint;

    private int shownChild = 0;
    private int mCardHeaderSize = 0;
    private int mCardHeaderPosition = 0;

    private Rect mTmpContainerRect = new Rect();
    private Rect mTmpChildRect = new Rect();

    public CardLayout(Context context) {

        super(context);
        init();
    }

    public CardLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        init();
    }

    public CardLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        mBoxPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBoxPaint.setColor(Color.argb(255,122,0,0));
        mBoxPaint.setStyle(Paint.Style.FILL);

        mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setColor(Color.argb(255,0,0,0));
        mTextPaint.setStyle(Paint.Style.FILL);
        mTextPaint.setTextAlign(Paint.Align.LEFT);
    }

    @Override
    public boolean shouldDelayChildPressedState() {
        return false;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int count = getChildCount();

        int maxHeight = 0;
        int maxWidth = 0;
        int childState = 0;

        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);
            if (child.getVisibility() != GONE) {
                measureChildWithMargins(child, widthMeasureSpec, 0, heightMeasureSpec, 0);

                LayoutParams lp = (LayoutParams) child.getLayoutParams();

                maxWidth = Math.max(maxWidth, child.getMeasuredWidth() + lp.leftMargin + lp.rightMargin);
                maxHeight = Math.max(maxHeight, child.getMeasuredHeight() + lp.topMargin + lp.bottomMargin);
                childState = combineMeasuredStates(childState, child.getMeasuredState());
            }
        }

        maxHeight = Math.max(maxHeight, getSuggestedMinimumHeight());
        maxWidth = Math.max(maxWidth, getSuggestedMinimumWidth());

        setMeasuredDimension(resolveSizeAndState(maxWidth, widthMeasureSpec, childState),
                resolveSizeAndState(maxHeight, heightMeasureSpec,
                        childState << MEASURED_HEIGHT_STATE_SHIFT));
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        int count = getChildCount();

        mCardHeaderSize = (top - bottom) / 8;
        mCardHeaderPosition = top;

        int middleLeft = getPaddingLeft();
        int middleRight = right - left - getPaddingRight();


        int parentTop = getPaddingTop();
        int parentBottom = bottom - top - getPaddingBottom();

        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);
            if (child.getVisibility() != GONE) {
                LayoutParams lp = (LayoutParams) child.getLayoutParams();

                int width = child.getMeasuredWidth();
                int height = child.getMeasuredHeight();

                mTmpContainerRect.left = middleLeft + lp.leftMargin;
                mTmpContainerRect.right = middleRight - lp.rightMargin;

                mTmpContainerRect.top = parentTop + lp.topMargin - mCardHeaderSize;
                mTmpContainerRect.bottom = parentBottom - lp.bottomMargin;

                Gravity.apply(lp.gravity, width, height, mTmpContainerRect, mTmpChildRect);

                child.layout(mTmpChildRect.left, mTmpChildRect.top,
                        mTmpChildRect.right, mTmpChildRect.bottom);
            }
        }
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new CardLayout.LayoutParams(getContext(), attrs);
    }

    @Override
    protected LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
    }

    @Override
    protected ViewGroup.LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
        return new LayoutParams(p);
    }

    @Override
    protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
        return p instanceof LayoutParams;
    }

    public static class LayoutParams extends MarginLayoutParams {
        public int gravity = Gravity.TOP | Gravity.START;

        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
        }

        public LayoutParams(int width, int height) {
            super(width, height);
        }

        public LayoutParams(ViewGroup.LayoutParams source) {
            super(source);
        }
    }

    @Override
    protected void dispatchDraw (Canvas canvas) {
        int count = getChildCount();
        int shrinkFactor = (mTmpContainerRect.right - mTmpContainerRect.left) / (3*count);

        for (int i = 0; i < count; i++) {
            canvas.drawRect(mTmpContainerRect.left + (shrinkFactor * i), mCardHeaderPosition - mCardHeaderSize + (i + 1) * (mCardHeaderSize / count), mTmpContainerRect.right - (shrinkFactor * i), mCardHeaderPosition - mCardHeaderSize + i * (mCardHeaderSize / count), mBoxPaint);

            String text = Integer.toString((shownChild + i) % count);
            mTextPaint.setTextSize(Math.abs(mCardHeaderSize / count));
            canvas.drawText(text, mTmpContainerRect.left + (shrinkFactor * i), mCardHeaderPosition - mCardHeaderSize + i * (mCardHeaderSize / count), mTextPaint);
        }

        super.dispatchDraw(canvas);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {

            if (event.getY() < mTmpContainerRect.top - mCardHeaderSize) {
                int count = getChildCount();

                shownChild = (shownChild + 1) % count;

                for (int i = 0; i < count; i++) {
                    View child = getChildAt(i);
                    if (i == shownChild) {
                        child.setVisibility(VISIBLE);
                    } else {
                        child.setVisibility(GONE);
                    }
                }

                return true;
            }

            return false;
        }

        return false;
    }
}
