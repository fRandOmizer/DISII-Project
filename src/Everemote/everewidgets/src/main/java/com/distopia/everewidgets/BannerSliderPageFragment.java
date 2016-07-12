package com.distopia.everewidgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class BannerSliderPageFragment extends Fragment {
    private static final String TAG = "BannerSliderPageFrgmnt";

    private int mImage;
    private ImageView mImageView;

    @Override
    public void onInflate(Context context, AttributeSet attrs, Bundle savedInstanceState) {
        super.onInflate(context, attrs, savedInstanceState);
        if(attrs != null) {
            TypedArray a = context.getTheme().obtainStyledAttributes(
                    attrs,
                    R.styleable.BannerSliderPageFragment,
                    0, 0);
            try {
                mImage = a.getResourceId(R.styleable.BannerSliderPageFragment_image,
                        R.drawable.default_image);
            } finally {
                a.recycle();
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.banner_slider_page_fragment, container, false);
        if(savedInstanceState != null) {
            mImage = savedInstanceState.getInt("image");
        }
        ImageView imageView = (ImageView) rootView.findViewById(R.id.page_image);
        if(imageView != null) {
            imageView.setImageResource(mImage);
            mImageView = imageView;
        } else {
            Log.e(TAG, "Did not found the corr. image view of the page fragment during init");
        }
        Log.i(TAG, "Banner slider fragment created! Image id is " + mImage);
        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt("image", mImage);
    }

    public void setImage(int image) {
        mImage = image;
        if(mImageView != null) {
            Log.i(TAG, "Set image resource to " + mImage);
            mImageView.setImageResource(mImage);
        } else {
            Log.e(TAG, "Ups, no image view found!");
        }
    }

    public int getImage() {
        return mImage;
    }
}

