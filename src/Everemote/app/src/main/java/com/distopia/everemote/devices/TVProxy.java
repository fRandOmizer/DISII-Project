package com.distopia.everemote.devices;

import java.util.ArrayList;

/**
 * Proxy representing a tv
 * Created by chris on 05.07.2016.
 */
public class TVProxy extends DeviceProxy implements IChannelSelectable {

    public TVProxy() {
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

    }

    @Override
    public ArrayList<Channel> getChannelList() {
        return null;
    }
}
