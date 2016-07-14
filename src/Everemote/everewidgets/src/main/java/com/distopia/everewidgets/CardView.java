package com.distopia.everewidgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RemoteViews;

/**
 * Represents a layout that arranges its child views and some special information, like an icon, in a card like manner
 */
@RemoteViews.RemoteView
public class CardView extends ViewGroup {
    private Paint mBoxPaint;
    private Paint mTextPaint;

    private int mCardHeaderSize = 0;

    private Rect mTmpContainerRect = new Rect();

    private Rect mTextBounds = new Rect();
    private Rect mCardBounds = new Rect();

    private Drawable mIcon;
    private int mBackgroundColor;
    private String mHeader;

    /**
     * Creates a new card view
     * @param context The views context
     */
    public CardView(Context context) {
        super(context);
        init();
    }

    /**
     * Creates a new card view
     * @param context The views context
     * @param attrs Attributes from the XML file
     */
    public CardView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);

        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.CardView, 0, 0);

        try {
            mHeader = a.getString(R.styleable.CardView_headerText);
            mBackgroundColor = a.getColor(R.styleable.CardView_colorBackground, 0);
            mIcon = a.getDrawable(R.styleable.CardView_iconDrawable);
        } finally {
            a.recycle();
        }


        init();
    }

    /**
     * Creates a new card view
     * @param context The views context
     * @param attrs Attributes from the XML file
     * @param defStyle Style information
     */
    public CardView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.CardView, 0, 0);

        try {
            mHeader = a.getString(R.styleable.CardView_headerText);
            mBackgroundColor = a.getColor(R.styleable.CardView_colorBackground, 0);
            mIcon = a.getDrawable(R.styleable.CardView_iconDrawable);
        } finally {
            a.recycle();
        }

        init();
    }

    /**
     * Initialize anything needed
     */
    private void init() {
        mBoxPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBoxPaint.setColor(mBackgroundColor);
        mBoxPaint.setStyle(Paint.Style.FILL);

        mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setColor(Color.argb(255,0,0,0));
        mTextPaint.setStyle(Paint.Style.FILL);
        mTextPaint.setTextAlign(Paint.Align.LEFT);
    }

    /**
     * No touch delay for the children in this layout
     * @return
     */
    @Override
    public boolean shouldDelayChildPressedState() {
        return false;
    }

    /**
     * Standard measurement callback
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int count = getChildCount();

        int maxHeight = 0;
        int maxWidth = 0;
        int childState = 0;

        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);
            if (child.getVisibility() != GONE) {
                measureChildWithMargins(child, widthMeasureSpec, 0, heightMeasureSpec, mCardHeaderSize);

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

    /**
     * Standard layout callback
     * @param changed
     * @param left
     * @param top
     * @param right
     * @param bottom
     */
    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        int count = getChildCount();

        if (mCardHeaderSize == 0) {
            mCardHeaderSize = Math.abs((top - bottom) / 8);
            requestLayout();
        }

        int middleLeft = getPaddingLeft();
        int middleRight = right - left - getPaddingRight();


        int parentTop = getPaddingTop();
        int parentBottom = bottom - top - getPaddingBottom();

        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);
            if (child.getVisibility() != GONE) {
                LayoutParams lp = (LayoutParams) child.getLayoutParams();

                mTmpContainerRect.left = middleLeft + lp.leftMargin;
                mTmpContainerRect.right = middleRight - lp.rightMargin;

                mTmpContainerRect.top = parentTop + lp.topMargin + mCardHeaderSize;
                mTmpContainerRect.bottom = parentBottom - lp.bottomMargin;

                child.layout(mTmpContainerRect.left, mTmpContainerRect.top,
                        mTmpContainerRect.right, mTmpContainerRect.bottom);
            }
        }

        mCardBounds.top = top;
        mCardBounds.bottom = mTmpContainerRect.bottom;
        mCardBounds.left = mTmpContainerRect.left;
        mCardBounds.right = mTmpContainerRect.right;
    }

    /**
     * Needed for the generation of layout parameters for the children
     * @param attrs
     * @return
     */
    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new CardView.LayoutParams(getContext(), attrs);
    }

    /**
     * Needed for the generation of layout parameters for the children
     * @return
     */
    @Override
    protected LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
    }

    /**
     * Needed for the generation of layout parameters for the children
     * @param p
     * @return
     */
    @Override
    protected ViewGroup.LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
        return new LayoutParams(p);
    }

    /**
     * Checks if layout parameter are sufficient for card layout
     * @param p
     * @return
     */
    @Override
    protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
        return p instanceof LayoutParams;
    }

    /**
     * Class holding layout parameters
     */
    public static class LayoutParams extends MarginLayoutParams {
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

    /**
     * Standard draw callback
     * @param canvas
     */
    @Override
    protected void dispatchDraw (Canvas canvas) {
        if(getChildCount() == 0)
        {
            return;
        }

        // Draw card background
        canvas.drawRect(mCardBounds.left, mCardBounds.top, mCardBounds.right, mCardBounds.bottom, mBoxPaint);

        // Draw icon in top left corner
        if (mIcon != null) {
            mIcon.setBounds(mCardBounds.left, mCardBounds.top, mCardBounds.left + mCardHeaderSize, mCardBounds.top + mCardHeaderSize);
            mIcon.draw(canvas);
        }

        // Draw header text
        if (mHeader != null) {
            mTextPaint.setTextSize(mCardHeaderSize);
            mTextPaint.getTextBounds(mHeader, 0, mHeader.length(), mTextBounds);
            canvas.drawText(mHeader, mCardBounds.left + mCardHeaderSize, mCardBounds.top + (mCardHeaderSize / 2) - mTextBounds.exactCenterY(), mTextPaint);
        }

        // Draw child
        super.dispatchDraw(canvas);
    }
}
