package com.distopia.everemote.devices.controls;

import java.util.ArrayList;

/**
 * Created by chris on 09.07.2016.
 */
public class RadioControlProxy extends ControlProxy implements IChannelSelectable {

    public RadioControlProxy() {
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
