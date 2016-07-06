package com.distopia.everemote.devices;

/**
 * Created by chris on 05.07.2016.
 */
public class SpeakerProxy extends DeviceProxy implements IVolumeSelectable {

    public SpeakerProxy() {
    }

    @Override
    public float getVolume() {
        return 0;
    }

    @Override
    public void setVolume(float volume) {

    }
}
