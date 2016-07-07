package com.distopia.everewidgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.distopia.everewidgets.R;

import java.util.Arrays;

/**
 * TODO: document your custom view class.
 */
public class BannerSliderView extends LinearLayout {
    private static final String TAG = "BannerSliderView";

    /**
     * The pager widget, which handles animation and allows swiping horizontally to access previous
     * and next wizard steps.
     */
    private ViewPager mPager;

    /**
     * The pager adapter, which provides the pages to the view pager widget.
     */
    private PagerAdapter mPagerAdapter;

    private float mOffsetBottom   = 0.15f;
    private float mOffsetTop      = 0.0f;
    private float mPaddingToImage = 0.2f;
    private int mImage            = R.drawable.tv_transparent_s;
    private int[] mImages         = {R.drawable.default_image};

    public BannerSliderView(Context context) {
        super(context);
        init(context, null);
    }

    public BannerSliderView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public BannerSliderView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.banner_slider_view, this);

        // Instantiate a ViewPager and a PagerAdapter.
        mPager = (BannerSliderViewPager) findViewById(R.id.pager);
        try {
            FragmentManager fragManager = ((FragmentActivity) context).getSupportFragmentManager();
            mPagerAdapter = new BannerSliderPagerAdapter(fragManager);
            mPager.setAdapter(mPagerAdapter);
            mPager.setClipToPadding(false);
            mPager.setOffscreenPageLimit(2);
            mPager.setCurrentItem(1);
        } catch (ClassCastException e) {
            Log.e("BannerSliderView", "Can't get fragment manager");
        }

        if(attrs != null) {
            TypedArray a = context.getTheme().obtainStyledAttributes(
                    attrs,
                    R.styleable.BannerSliderView,
                    0, 0);
            try {
                mOffsetBottom   = a.getFloat(R.styleable.BannerSliderView_offsetBottom, 0.15f);
                mOffsetTop      = a.getFloat(R.styleable.BannerSliderView_offsetTop, 0.0f);
                mPaddingToImage = a.getFloat(R.styleable.BannerSliderView_paddingToImage, 0.2f);
                mImage          = a.getResourceId(R.styleable.BannerSliderView_main_image,
                                                  R.drawable.tv_transparent_s);
            } finally {
                a.recycle();
            }
        }

        ((ImageView) findViewById(R.id.image)).setImageResource(mImage);

        setMargins();
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        SavedState ss = new SavedState(superState);
        ss.mImagesState = mImages;
        return ss;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        if(!(state instanceof SavedState)) {
            super.onRestoreInstanceState(state);
            return;
        }
        SavedState ss = (SavedState) state;
        super.onRestoreInstanceState(ss.getSuperState());
        setImages(ss.mImagesState);
    }

    public void setOffsetBottom(float offsetBottom) {
        mOffsetBottom = offsetBottom;
        setMargins();
    }

    public float getOffsetBottom() {
        return mOffsetBottom;
    }

    public void setOffsetTop(float offsetTop) {
        mOffsetTop = offsetTop;
        setMargins();
    }

    public float getOffsetTop() {
        return mOffsetTop;
    }

    public void setImages(int[] images) {
        mImages = images;
        Log.i(TAG, "Set image list to " + Arrays.toString(images));
        mPagerAdapter.notifyDataSetChanged();
    }

    public int[] getImages() {
        return mImages;
    }

    private void setMargins() {
        findViewById(R.id.marginleft).getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        mPager.setPadding(
                                findViewById(R.id.marginleft).getWidth(),
                                (int) (mOffsetTop * findViewById(R.id.image).getHeight()),
                                findViewById(R.id.marginleft).getWidth(),
                                (int) (mOffsetBottom * findViewById(R.id.image).getHeight())
                        );
                    }
                }
        );

        findViewById(R.id.image).getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        mPager.setPadding(
                                findViewById(R.id.marginleft).getWidth(),
                                (int) (mOffsetTop * findViewById(R.id.image).getHeight()),
                                findViewById(R.id.marginleft).getWidth(),
                                (int) (mOffsetBottom * findViewById(R.id.image).getHeight())
                        );
                    }
                }
        );

                        View left = findViewById(R.id.marginleft);
                        left.setLayoutParams(new LinearLayout.LayoutParams(0,
                                                                           LayoutParams.MATCH_PARENT,
                                                                           mPaddingToImage));

                        View right = findViewById(R.id.marginright);
                        right.setLayoutParams(new LinearLayout.LayoutParams(0,
                                                                            LayoutParams.MATCH_PARENT,
                                                                            mPaddingToImage));

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
            BannerSliderPageFragment bsf = new BannerSliderPageFragment();
            if(getImages() != null && position < getImages().length) {
                bsf.setImage(getImages()[position]);
            } else {
                Log.e(TAG, "Did not set the image of the BSF!!");
            }
            return bsf;
        }

        @Override
        public int getCount() {
            if(getImages() != null) {
                return getImages().length;
            } else {
                return 0;
            }
        }
    }

    static class SavedState extends BaseSavedState {
        int[] mImagesState;

        SavedState(Parcelable superState) {
            super(superState);
        }

        private SavedState(Parcel in) {
            super(in);
            in.readIntArray(mImagesState);
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeIntArray(mImagesState);
        }

        public static final Parcelable.Creator<SavedState> CREATOR =
                new Parcelable.Creator<SavedState>() {
                    public SavedState createFromParcel(Parcel in) {
                        return new SavedState(in);
                    }
                    public SavedState[] newArray(int size) {
                        return new SavedState[size];
                    }
                };
    }
}
