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

/**
 * Represents a slider whose active page is centered in the middle of the the view. An image can be
 * added to this middle view. Additionally, the left and right views are hinted on the screen, for a
 * better visibility.
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

    /**
     * Settings which are later extracted from the XML attributes.
     */
    private float mOffsetBottom   = 0.15f;
    private float mOffsetTop      = 0.0f;
    private float mPaddingToImage = 0.2f;
    private int mImage            = R.drawable.tv_transparent_s;
    private int[] mImages         = {R.drawable.default_image};

    /**
     * Creates a new banner slider for the given context.
     * @param context The context to display the view in.
     */
    public BannerSliderView(Context context) {
        super(context);
        init(context, null);
    }

    /**
     * Creates a new banner slider for the given context.
     * @param context The context to display the view in.
     * @param attrs Possible attributes from the XML file.
     */
    public BannerSliderView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    /**
     * Creates a new banner slider for the given context.
     * @param context The context to display the view in.
     * @param attrs Possible attributes from the XML file.
     * @param defStyle Won't be used here.
     */
    public BannerSliderView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    /**
     * Initializes the widget with the given attributes (offsets, margins, etc.). Initializes the
     * pager and adds all images to the pager.
     * @param context The context to initialize in.
     * @param attrs The XML attributes.
     */
    private void init(Context context, AttributeSet attrs) {
        // Inflates this view.
        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.banner_slider_view, this);

        // Instantiates a ViewPager and a PagerAdapter.
        mPager = (BannerSliderViewPager) findViewById(R.id.pager);
        try {
            FragmentManager fragManager = ((FragmentActivity) context).getSupportFragmentManager();
            mPagerAdapter = new BannerSliderPagerAdapter(fragManager);
            mPager.setAdapter(mPagerAdapter);
            // The lines below allow for a centering of the middle page as well as displaying the
            // left and right pages on the screen.
            mPager.setClipToPadding(false);
            mPager.setOffscreenPageLimit(2);
            mPager.setCurrentItem(1);
        } catch (ClassCastException e) {
            Log.e("BannerSliderView", "Can't get fragment manager");
        }

        // Extracts possible XML attributes.
        if(attrs != null) {
            TypedArray a = context.getTheme().obtainStyledAttributes(
                    attrs,
                    R.styleable.BannerSliderView,
                    0, 0);
            try {
                mOffsetBottom   = a.getFloat(R.styleable.BannerSliderView_offsetBottom, 0.15f);
                mOffsetTop      = a.getFloat(R.styleable.BannerSliderView_offsetTop, 0.0f);
                mPaddingToImage = a.getFloat(R.styleable.BannerSliderView_paddingToImage, 0.2f);
                mImage          = a.getResourceId(R.styleable.BannerSliderView_mainImage,
                                                  R.drawable.tv_transparent_s);
            } finally {
                a.recycle();
            }
        }

        // Sets the center image of the pager.
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

    /**
     * Sets the offset of the image to the bottom of the view.
     * @param offsetBottom The offset as a value between (incl.) 0 and 1.
     */
    public void setOffsetBottom(float offsetBottom) {
        assert(offsetBottom >= 0 && offsetBottom <= 1);
        mOffsetBottom = offsetBottom;
        setMargins();
    }

    /**
     * Returns the offset of the image to the bottom of the view.
     * @return The bottom offset.
     */
    public float getOffsetBottom() {
        return mOffsetBottom;
    }

    /**
     * Sets the offset of the image to the top of the view.
     * @param offsetTop The offset as a value between (incl.) 0 and 1.
     */
    public void setOffsetTop(float offsetTop) {
        assert(offsetTop >= 0 && offsetTop <= 1);
        mOffsetTop = offsetTop;
        setMargins();
    }

    /**
     * Returns the offset of the image to the top of the view.
     * @return The top offset.
     */
    public float getOffsetTop() {
        return mOffsetTop;
    }

    /**
     * Sets the given list of images that are then directly displayed in the slider. The list
     * should contain resource IDs as integers. The order of the slider pages is represented by the
     * order of the resources in this list.
     * @param images The list of images to display.
     */
    public void setImages(int[] images) {
        mImages = images;
        // Displays changes.
        mPagerAdapter.notifyDataSetChanged();
    }

    /**
     * Returns the list of images as resource IDs.
     * @return The image list of the slider.
     */
    public int[] getImages() {
        return mImages;
    }

    /**
     * Sets the margins according to the current value of the setting variables of the slider:
     * The offset of the image to the bottom and top, and the margin of the image to the left and
     * right borders of this view.
     */
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
     * This is the pager adapter that handles the single pages. It builds the
     * BannerSliderPageFragments with the specified image.
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
                Log.e(TAG, "Did not set the image of the BSF!");
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

    /**
     * Just to allow parcels.
     */
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
