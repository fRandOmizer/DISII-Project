package com.distopia.everemote.devices.controls;

import java.util.ArrayList;

/**
 * Interfaces for devices which can switch their channels
 * Created by chris on 05.07.2016.
 */
public interface IChannelSelectable {

    /**
     * Switchs to the previous channel
     * @return the channel which is going to be the current
     */
    public Channel previousChannel();

    /**
     * Switchs to the next channel
     * @return the channel which is going to be the current
     */
    public Channel nextChannel();

    /**
     *
     * @return the current channel
     */
    public Channel getCurrentChannel();


    /**
     *
     * @param newChannel the new channel which should be set
     */
    public void setCurrentChannel(Channel newChannel);

    /**
     *
     * @return a lsit of all available channels. Indices are sorted by channel number.
     */
    public ArrayList<Channel> getChannelList();
}
