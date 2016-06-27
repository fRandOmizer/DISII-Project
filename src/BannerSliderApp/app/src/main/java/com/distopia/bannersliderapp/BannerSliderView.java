package com.distopia.bannersliderapp;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.Layout;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

/**
 * TODO: document your custom view class.
 */
public class BannerSliderView extends LinearLayout {

    /**
     * The number of pages (wizard steps) to show in this demo.
     */
    private static final int NUM_PAGES = 5;

    /**
     * The pager widget, which handles animation and allows swiping horizontally to access previous
     * and next wizard steps.
     */
    private ViewPager mPager;

    /**
     * The pager adapter, which provides the pages to the view pager widget.
     */
    private PagerAdapter mPagerAdapter;

    private String mExampleString = "Bla"; // TODO: use a default from R.string...
    private int mExampleColor = Color.RED; // TODO: use a default from R.color...
    private float mExampleDimension = 0; // TODO: use a default from R.dimen...

    private TextPaint mTextPaint;
    private float mTextWidth;
    private float mTextHeight;

    public BannerSliderView(Context context) {
        super(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.banner_slider_view, this);
        // Instantiate a ViewPager and a PagerAdapter.
        mPager = (BannerSliderViewPager) findViewById(R.id.pager);
        try {
            FragmentManager fragmentManager = ((FragmentActivity) context).getSupportFragmentManager();
            mPagerAdapter = new BannerSliderPagerAdapter(fragmentManager);
        } catch (ClassCastException e) {
            Log.e("BannerSliderView", "Can't get fragment manager");
        }
        mPager.setAdapter(mPagerAdapter);
        init(null, 0);
    }

    public BannerSliderView(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.banner_slider_view, this);
        // Instantiate a ViewPager and a PagerAdapter.
        mPager = (BannerSliderViewPager) findViewById(R.id.pager);
        try {
            FragmentManager fragmentManager = ((FragmentActivity) context).getSupportFragmentManager();
            mPagerAdapter = new BannerSliderPagerAdapter(fragmentManager);
        } catch (ClassCastException e) {
            Log.e("BannerSliderView", "Can't get fragment manager");
        }
        mPager.setAdapter(mPagerAdapter);
        init(attrs, 0);
    }

    public BannerSliderView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.banner_slider_view, this);
        // Instantiate a ViewPager and a PagerAdapter.
        mPager = (BannerSliderViewPager) findViewById(R.id.pager);
        try {
            FragmentManager fragmentManager = ((FragmentActivity) context).getSupportFragmentManager();
            mPagerAdapter = new BannerSliderPagerAdapter(fragmentManager);
        } catch (ClassCastException e) {
            Log.e("BannerSliderView", "Can't get fragment manager");
        }
        mPager.setAdapter(mPagerAdapter);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {
        // Load attributes
        final TypedArray a = getContext().obtainStyledAttributes(
                attrs, R.styleable.BannerSliderView, defStyle, 0);

        mExampleString = a.getString(
                R.styleable.BannerSliderView_exampleString);
        mExampleColor = a.getColor(
                R.styleable.BannerSliderView_exampleColor,
                mExampleColor);
        // Use getDimensionPixelSize or getDimensionPixelOffset when dealing with
        // values that should fall on pixel boundaries.
        mExampleDimension = a.getDimension(
                R.styleable.BannerSliderView_exampleDimension,
                mExampleDimension);

        a.recycle();

        // Set up a default TextPaint object
        mTextPaint = new TextPaint();
        mTextPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setTextAlign(Paint.Align.LEFT);

        // Update TextPaint and text measurements from attributes
        invalidateTextPaintAndMeasurements();
    }

    private void invalidateTextPaintAndMeasurements() {
        mTextPaint.setTextSize(mExampleDimension);
        mTextPaint.setColor(mExampleColor);
        mTextWidth = mTextPaint.measureText(mExampleString);

        Paint.FontMetrics fontMetrics = mTextPaint.getFontMetrics();
        mTextHeight = fontMetrics.bottom;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        /**super.onDraw(canvas);

        // TODO: consider storing these as member variables to reduce
        // allocations per draw cycle.
        int paddingLeft = getPaddingLeft();
        int paddingTop = getPaddingTop();
        int paddingRight = getPaddingRight();
        int paddingBottom = getPaddingBottom();

        int contentWidth = getWidth() - paddingLeft - paddingRight;
        int contentHeight = getHeight() - paddingTop - paddingBottom;

        // Draw the text.
        canvas.drawText(mExampleString,
                paddingLeft + (contentWidth - mTextWidth) / 2,
                paddingTop + (contentHeight + mTextHeight) / 2,
                mTextPaint);**/
    }

    /**
     * Gets the example string attribute value.
     *
     * @return The example string attribute value.
     */
    public String getExampleString() {
        return mExampleString;
    }

    /**
     * Sets the view's example string attribute value. In the example view, this string
     * is the text to draw.
     *
     * @param exampleString The example string attribute value to use.
     */
    public void setExampleString(String exampleString) {
        mExampleString = exampleString;
        invalidateTextPaintAndMeasurements();
    }

    /**
     * Gets the example color attribute value.
     *
     * @return The example color attribute value.
     */
    public int getExampleColor() {
        return mExampleColor;
    }

    /**
     * Sets the view's example color attribute value. In the example view, this color
     * is the font color.
     *
     * @param exampleColor The example color attribute value to use.
     */
    public void setExampleColor(int exampleColor) {
        mExampleColor = exampleColor;
        invalidateTextPaintAndMeasurements();
    }

    /**
     * Gets the example dimension attribute value.
     *
     * @return The example dimension attribute value.
     */
    public float getExampleDimension() {
        return mExampleDimension;
    }

    /**
     * Sets the view's example dimension attribute value. In the example view, this dimension
     * is the font size.
     *
     * @param exampleDimension The example dimension attribute value to use.
     */
    public void setExampleDimension(float exampleDimension) {
        mExampleDimension = exampleDimension;
        invalidateTextPaintAndMeasurements();
    }

    /**
     * A simple pager adapter that represents 5 ScreenSlidePageFragment objects, in
     * sequence.
     */
    private class BannerSliderPagerAdapter extends FragmentStatePagerAdapter {
        public BannerSliderPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return new BannerSliderPageFragment();
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }

        @Override
        public float getPageWidth(int position) {
            return 0.6f;
        }
    }
}
