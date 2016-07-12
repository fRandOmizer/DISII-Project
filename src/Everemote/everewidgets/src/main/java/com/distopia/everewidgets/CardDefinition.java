package com.distopia.everewidgets;


import android.graphics.Bitmap;
import android.graphics.Color;
import android.view.View;

/**
 * Definition of a card to be displayed in a card layout
 */
public class CardDefinition {

    private Bitmap mIcon;
    private int mBackgroundColor;
    private String mHeader;
    private View mContent;

    /**
     * Constructs a card definition with all the needed information
     * @param icon
     * @param backgroundColor
     * @param header
     * @param content
     */
    public CardDefinition(Bitmap icon, int backgroundColor, String header, View content)
    {
        this.mIcon = icon;
        this.mBackgroundColor = backgroundColor;
        this.mHeader = header;
        this.mContent = content;
    }

    /**
     * Returns the icon to be displayed in card header
     * @return
     */
    public Bitmap getIcon() { return this.mIcon; }

    /**
     * Returns the background color for the card
     * @return
     */
    public int getBackgroundColor()
    {
        return this.mBackgroundColor;
    }

    /**
     * Returns the header text for the card
     * @return
     */
    public String getHeader()
    {
        return this.mHeader;
    }

    /**
     * Returns the view to be displayed in the card
     * @return
     */
    public View getContent()
    {
        return this.mContent;
    }
}
