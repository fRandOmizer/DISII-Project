package com.distopia.everemote.devices.controls;

import com.distopia.everemote.network.RaspiClient;

/**
 * Created by chris on 11.07.2016.
 */
public class TVSpeakerControlProxy extends RaspiControlProxy implements IVolumeSelectable {

    public TVSpeakerControlProxy(RaspiClient raspiClient) {
        super(raspiClient);
    }

    @Override
    public float getVolume() {
        return 0;
    }

    @Override
    public void setVolume(float volume) {
        if (raspiClient != null) {
            raspiClient.sendMessage("TVSetVolume:" + Math.round(volume));
        }
    }
}
