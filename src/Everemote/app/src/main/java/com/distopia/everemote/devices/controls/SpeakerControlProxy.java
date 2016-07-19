package com.distopia.everemote.devices.controls;

import com.distopia.everemote.network.RaspiClient;

/**
 * Created by chris on 05.07.2016.
 */
public class SpeakerControlProxy extends RaspiControlProxy implements IVolumeSelectable {

    public SpeakerControlProxy(RaspiClient tcpClient) {
        super(tcpClient);
    }

    @Override
    public float getVolume() {
        return 0;
    }

    @Override
    public void setVolume(float volume) {
        if (raspiClient != null) {
            raspiClient.sendMessage("SpeakerSetVolume:" + Math.round(volume));
        }
    }
}
