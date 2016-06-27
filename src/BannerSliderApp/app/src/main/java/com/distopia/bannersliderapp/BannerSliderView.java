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
import android.view.ViewTreeObserver;
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

    private float mBottomOffset = 0.0f;

    public BannerSliderView(Context context) {
        super(context);
        init(context, null, 0);
    }

    public BannerSliderView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public BannerSliderView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs, defStyle);
    }

    private void init(Context context, AttributeSet attrs, int defStyle) {

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
        mPager.setClipToPadding(false);
        mPager.setOffscreenPageLimit(2);
        mPager.setCurrentItem(1);

        if(attrs != null) {
            TypedArray a = context.getTheme().obtainStyledAttributes(
                    attrs,
                    R.styleable.BannerSliderView,
                    0, 0);
            try {
                mBottomOffset = a.getFloat(R.styleable.BannerSliderView_bottomOffset, 0.0f);
            } finally {
                a.recycle();
            }

        }

        findViewById(R.id.marginleft).getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mPager.setPadding(findViewById(R.id.marginleft).getWidth(), 0, findViewById(R.id.marginleft).getWidth(), (int)(mBottomOffset * findViewById(R.id.image).getHeight()));
            }
        });

        findViewById(R.id.image).getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mPager.setPadding(findViewById(R.id.marginleft).getWidth(), 0, findViewById(R.id.marginleft).getWidth(), (int)(mBottomOffset * findViewById(R.id.image).getHeight()));
            }
        });
    }

    public void setBottomOffset(float bottomOffset) {
        mBottomOffset = bottomOffset;
    }

    public float getBottomOffset() {
        return mBottomOffset;
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
    }
}
