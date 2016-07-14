package com.distopia.everewidgets;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.Gravity;
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
    private Rect mTmpChildRect = new Rect();
    private int cardOffsetFactor = 0;
    private int mBoxHeaderSize = 0;
    private int mBoxHeaderPosition = 0;

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
                measureChildWithMargins(child, widthMeasureSpec, 0, heightMeasureSpec, -mBoxHeaderSize);

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

        if (mBoxHeaderSize == 0) {
            mBoxHeaderSize = Math.abs((top - bottom) / 10);
            cardOffsetFactor = mBoxHeaderSize / count;
            mBoxHeaderPosition = top;
        }

        int middleLeft = getPaddingLeft();
        int middleRight = right - left - getPaddingRight();


        int parentBottom = bottom - top;

        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);
            if (child.getVisibility() != GONE) {
                LayoutParams lp = (LayoutParams) child.getLayoutParams();

                int width = child.getMeasuredWidth();
                int height = child.getMeasuredHeight();

                mTmpContainerRect.left = middleLeft;
                mTmpContainerRect.right = middleRight;

                mTmpContainerRect.top = mBoxHeaderSize - cardOffsetFactor * (count - i);
                mTmpContainerRect.bottom = parentBottom;

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
     * Standard touch event callback
     * @param event
     * @return
     */
    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            System.out.println("-----------------------");
            System.out.println(mBoxHeaderPosition);
            System.out.println(cardOffsetFactor);
            System.out.println(event.getY());
            int count = getChildCount();
            /*int index = count - 1;

            for (int i = 0; i < count; i++) {
                View child = getChildAt(i);
                int[] location = new int[2];
                child.getLocationInWindow(location);

                if(event.getY() < location[1]) {
                    index = i;
                    break;
                }
            }*/

            int index = (int)((event.getY() - mBoxHeaderPosition) / (cardOffsetFactor*2));

            System.out.println(index);
            bringChildToFront(getChildAt(index));
            super.requestLayout();
            super.invalidate();
            return false;
        }

        return false;
    }
}
