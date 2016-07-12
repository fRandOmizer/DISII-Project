package com.distopia.everewidgets;


import android.graphics.Bitmap;
import android.graphics.Color;
import android.view.View;

public class CardDefinition {

    private Bitmap icon;
    private int backgroundColor;
    private String header;
    private View content;

    public CardDefinition(Bitmap icon, int backgroundColor, String header, View content)
    {
        this.icon = icon;
        this.backgroundColor = backgroundColor;
        this.header = header;
        this.content = content;
    }

    public Bitmap getIcon()
    {
        return this.icon;
    }

    public int getBackgroundColor()
    {
        return this.backgroundColor;
    }

    public String getHeader()
    {
        return this.header;
    }

    public View getContent()
    {
        return this.content;
    }
}
