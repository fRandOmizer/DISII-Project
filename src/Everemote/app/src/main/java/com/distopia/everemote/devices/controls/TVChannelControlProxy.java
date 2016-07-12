package com.distopia.everemote.devices.controls;

import android.util.Log;

import java.util.ArrayList;

/**
 * Proxy representing a tv
 * Created by chris on 05.07.2016.
 */
public class TVChannelControlProxy extends ControlProxy implements IChannelSelectable {
    private static final String TAG = "TVChannelControlProxy";

    public TVChannelControlProxy() {
    }

    @Override
    public Channel previousChannel() {
        return null;
    }

    @Override
    public Channel nextChannel() {
        return null;
    }

    @Override
    public Channel getCurrentChannel() {
        return null;
    }

    @Override
    public void setCurrentChannel(Channel newChannel) {
        Log.i(TAG, "Changed channel to " + newChannel);
    }

    @Override
    public ArrayList<Channel> getChannelList() {
        return null;
    }
}
