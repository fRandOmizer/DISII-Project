package com.distopia.everewidgets;

import android.content.Context;
import android.graphics.Rect;
import android.os.Vibrator;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RemoteViews;

/**
 * Represents a layout that arranges its child views in card box like manner
 */
@RemoteViews.RemoteView
public class CardLayout extends ViewGroup {
    private Rect mTmpContainerRect = new Rect();
    private int cardOffsetFactor = 0;
    private int cardOffsetManualChange = 0;
    private int mBoxHeaderSize = 0;
    private int mBoxHeaderPosition = 0;
    private float mlastDownY = 0;
    private boolean mMovementStarted = false;
    private int lastCount = 0;
    private Vibrator vibrator;

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
        if (!this.isInEditMode()) {
            vibrator = (Vibrator) this.getContext().getSystemService(Context.VIBRATOR_SERVICE);
        }
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
                measureChildWithMargins(child, widthMeasureSpec, 0, heightMeasureSpec, mBoxHeaderSize);

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


        mBoxHeaderSize = Math.abs((top - bottom) / 10);
        cardOffsetFactor = mBoxHeaderSize / (count == 0 ? 1 : count);
        mBoxHeaderPosition = top;

        int middleRight = right - left ;

        int parentBottom = bottom - top;
        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);

            if (child.getVisibility() != GONE) {
                mTmpContainerRect.left = getPaddingLeft() + (mBoxHeaderSize / count) * ((count - i) - 1);
                mTmpContainerRect.right = middleRight - getPaddingRight() - (mBoxHeaderSize / count) * ((count - i) - 1);

                mTmpContainerRect.top = top + (mBoxHeaderSize - cardOffsetFactor * (count - i)) - cardOffsetManualChange * i;
                mTmpContainerRect.bottom = parentBottom - cardOffsetFactor * (count - i) - cardOffsetManualChange * i;

                child.layout(mTmpContainerRect.left, mTmpContainerRect.top,
                        mTmpContainerRect.right, mTmpContainerRect.bottom);
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
        return new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
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
     * Standard touch event callback
     * @param event
     * @return
     */
    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            int count = getChildCount();
            mlastDownY = event.getY();
            mMovementStarted = false;

            if (event.getY() < mBoxHeaderSize + mBoxHeaderPosition + Math.abs(cardOffsetFactor * count) + Math.abs(cardOffsetManualChange * count))
            {
                return true;
            }
        }

        return false;
    }

    /**
     * Standard touch event callback
     * @param event
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            int count = getChildCount();
            mlastDownY = event.getY();
            mMovementStarted = false;

            if (event.getY() < mBoxHeaderSize + mBoxHeaderPosition + Math.abs(cardOffsetFactor * count) + Math.abs(cardOffsetManualChange * count))
            {
                return true;
            }
        }
        else if (event.getAction() == MotionEvent.ACTION_MOVE) {
            if (Math.abs(event.getY() - mlastDownY) > Math.abs(cardOffsetFactor/8)) {
                mMovementStarted = true;
            }

            if (mMovementStarted) {
                cardOffsetManualChange += (int)(mlastDownY - event.getY());
                cardOffsetManualChange = Math.min(cardOffsetManualChange, 0);
                invalidate();
                requestLayout();
            }

            mlastDownY = event.getY();
        }
        else if (event.getAction() == MotionEvent.ACTION_UP) {
            if (!mMovementStarted) {
                int index = (int)((event.getY() - mBoxHeaderPosition) / ((Math.abs(cardOffsetManualChange) + Math.abs(cardOffsetFactor))*2));
                cardOffsetManualChange = 0;
                bringChildToFront(getChildAt(index));
                invalidate();
                requestLayout();
            }
        }

        return false;
    }

    /**
     * Only children that are visible count
     * @return
     */
    @Override
    public int getChildCount () {
        int count = super.getChildCount();
        int countWithoutInvisible = 0;

        for (int i = 0; i < count; i++) {
            final View child = super.getChildAt(i);
            if (child.getVisibility() != GONE) {
                countWithoutInvisible++;
            }
        }

        // cards changed, reset movement
        if (lastCount != countWithoutInvisible) {
            lastCount = countWithoutInvisible;
            cardOffsetManualChange = 0;

            // more than one card
            if (countWithoutInvisible > 1 && !this.isInEditMode()) {

                long[] pattern = new long[countWithoutInvisible*2];

                for (int i = 0; i < pattern.length; i++) {
                    pattern[i] = 100;
                }

                vibrator.vibrate(pattern, -1);
            }
        }

        return countWithoutInvisible;
    }

    /**
     * Only get visible children
     * @param index
     * @return
     */
    @Override
    public View getChildAt (int index) {
        int count = super.getChildCount();

        for (int i = 0; i < count; i++) {
            View child = super.getChildAt(i);
            if (child.getVisibility() != GONE) {
                if (index == 0)
                {
                    return child;
                }
                else
                {
                    index--;
                }
            }
        }

        return super.getChildAt(0);
    }
}
