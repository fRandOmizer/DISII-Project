package com.distopia.everemote.devices.controls;

import android.util.Log;

import com.distopia.everemote.network.RaspiClient;

import java.util.ArrayList;

/**
 * Proxy representing a tv
 * Created by chris on 05.07.2016.
 */
public class TVChannelControlProxy extends RaspiControlProxy implements IChannelSelectable {
    private static final String TAG = "TVChannelControlProxy";

    public TVChannelControlProxy(RaspiClient tcpClient) {
        super(tcpClient);
    }

    @Override
    public Channel previousChannel() {
        if (raspiClient != null) {
            raspiClient.sendMessage("TVChannelDown");
        }
        return null;
    }

    @Override
    public Channel nextChannel() {
        if (raspiClient != null) {
            raspiClient.sendMessage("TVChannelUp");
        }
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
