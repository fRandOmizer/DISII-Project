package com.distopia.bannersliderapp;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    private int[] mChannels = {R.drawable.das_erste_s, R.drawable.zdf_s, R.drawable.rtl_s};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BannerSliderView slider = (BannerSliderView) findViewById(R.id.bannerslider);
        if(slider != null) {
            slider.setImages(mChannels);
        }
    }

}
