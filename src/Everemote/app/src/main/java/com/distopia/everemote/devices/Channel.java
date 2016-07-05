package com.distopia.everemote.devices;

/**
 * Represents a channel for example of a tv or a radio.
 * Created by chris on 05.07.2016.
 */
public class Channel {

    private String channelName;
    private int channelNumber;

    /**
     * Public constructor creates a new channel object
     * @param channelName the channel name
     * @param channelNumber number of the channel
     */
    public Channel(String channelName, int channelNumber) {
        this.channelName = channelName;
        this.channelNumber = channelNumber;
    }

    /**
     *
     * @return the name of the channel
     */
    public String getChannelName() {
        return channelName;
    }

    /**
     *
     * @return the channel number
     */
    public int getChannelNumber() {
        return channelNumber;
    }
}
