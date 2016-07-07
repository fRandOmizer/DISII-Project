package com.distopia.bannersliderapp;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;

public class BannerSliderViewPager extends ViewPager {
    private static final String TAG = "BannerSliderViewPager";

    public BannerSliderViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BannerSliderViewPager(Context context) {
        super(context);
    }

}