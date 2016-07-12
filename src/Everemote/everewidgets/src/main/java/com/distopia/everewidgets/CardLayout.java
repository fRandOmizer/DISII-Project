package com.distopia.everewidgets;

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

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a layout that arranges its child views and some special information, like an icon, in a card like manner
 */
@RemoteViews.RemoteView
public class CardLayout extends ViewGroup {
    private Paint mBoxPaint;
    private Paint mTextPaint;

    private int shownChild = 0;
    private int mCardHeaderSize = 0;
    private int mCardHeaderPosition = 0;

    private Rect mTmpContainerRect = new Rect();
    private Rect mTmpChildRect = new Rect();

    private Rect tmpBitmapRect = new Rect();
    private Rect textBounds = new Rect();

    private List<CardDefinition> cardDefinitions;

    /**
     * Creates a new card layout
     * @param context The views context
     */
    public CardLayout(Context context) {
        super(context);
        init();
    }

    /**
     * Creates a new card layout
     * @param context The views context
     * @param attrs Attributes from the XML file
     */
    public CardLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        init();
    }

    /**
     * Creates a new card layout
     * @param context The views context
     * @param attrs Attributes from the XML file
     * @param defStyle Style information
     */
    public CardLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    /**
     * Initialize anything needed
     */
    private void init() {
        cardDefinitions = new ArrayList<>();

        mBoxPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBoxPaint.setColor(Color.argb(255,122,0,0));
        mBoxPaint.setStyle(Paint.Style.FILL);

        mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setColor(Color.argb(255,0,0,0));
        mTextPaint.setStyle(Paint.Style.FILL);
        mTextPaint.setTextAlign(Paint.Align.LEFT);
    }

    /**
     * Adds a card with the given definition
     * @param card
     */
    public void addCard(CardDefinition card)
    {
        this.addView(card.getContent());
        cardDefinitions.add(card);
    }

    /**
     * Removes the card with the given definition
     * @param card
     */
    public void removeCard(CardDefinition card)
    {
        this.removeView(card.getContent());
        cardDefinitions.remove(card);
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
                measureChildWithMargins(child, widthMeasureSpec, 0, heightMeasureSpec, -mCardHeaderSize);

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

        mCardHeaderSize = ((top - bottom) / 10) * cardDefinitions.size();
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

    /**
     * Needed for the generation of layout parameters for the children
     * @param attrs
     * @return
     */
    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new CardLayout.LayoutParams(getContext(), attrs);
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

    /**
     * Standard draw callback
     * @param canvas
     */
    @Override
    protected void dispatchDraw (Canvas canvas) {
        int count = getChildCount();
        int shrinkFactor = ((mTmpContainerRect.right - mTmpContainerRect.left) / 30);

        for (int i = 0; i < count; i++) {
            getChildAt(i).setVisibility(GONE);
            int childId = (shownChild + i) % count;

            mBoxPaint.setColor(cardDefinitions.get(childId).getBackgroundColor());
            canvas.drawRect(mTmpContainerRect.left + (shrinkFactor * i), mCardHeaderPosition - mCardHeaderSize + (i + 1) * (mCardHeaderSize / count), mTmpContainerRect.right - (shrinkFactor * i), mCardHeaderPosition - mCardHeaderSize + i * (mCardHeaderSize / count), mBoxPaint);

            tmpBitmapRect.left = mTmpContainerRect.left + (shrinkFactor * i);
            tmpBitmapRect.top = mCardHeaderPosition - mCardHeaderSize + (i + 1) * (mCardHeaderSize / count);
            tmpBitmapRect.bottom = mCardHeaderPosition - mCardHeaderSize + i * (mCardHeaderSize / count);
            tmpBitmapRect.right = tmpBitmapRect.left + Math.abs(tmpBitmapRect.top - tmpBitmapRect.bottom);
            canvas.drawBitmap(cardDefinitions.get(childId).getIcon(), null, tmpBitmapRect, mBoxPaint);
            mBoxPaint.setColor(Color.BLACK);

            mTextPaint.setTextSize(Math.abs(mCardHeaderSize / count));
            mTextPaint.getTextBounds(cardDefinitions.get(childId).getHeader(), 0, cardDefinitions.get(childId).getHeader().length(), textBounds);
            canvas.drawText(cardDefinitions.get(childId).getHeader(), tmpBitmapRect.right, (tmpBitmapRect.top+tmpBitmapRect.bottom)/2 - textBounds.exactCenterY(), mTextPaint);
        }

        getChildAt(shownChild).setVisibility(VISIBLE);
        mBoxPaint.setColor(cardDefinitions.get(shownChild).getBackgroundColor());
        canvas.drawRect(mTmpContainerRect.left, mCardHeaderPosition - mCardHeaderSize, mTmpContainerRect.right, mTmpContainerRect.bottom, mBoxPaint);

        super.dispatchDraw(canvas);
    }

    /**
     * Standard touch event callback
     * @param event
     * @return
     */
    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {

            if (event.getY() < mCardHeaderPosition - mCardHeaderSize) {
                int count = getChildCount();

                int index = (count-1) - (int)((event.getY() - mCardHeaderPosition)/Math.abs(mCardHeaderSize/count));

                shownChild = (shownChild + index) % count;

                return true;
            }

            return false;
        }

        return false;
    }
}
