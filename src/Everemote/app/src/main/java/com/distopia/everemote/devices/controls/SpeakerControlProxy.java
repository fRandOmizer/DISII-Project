package com.distopia.everemote.devices.controls;

/**
 * Created by chris on 05.07.2016.
 */
public class SpeakerControlProxy extends ControlProxy implements IVolumeSelectable {

    public SpeakerControlProxy() {
    }

    @Override
    public float getVolume() {
        return 0;
    }

    @Override
    public void setVolume(float volume) {

    }
}
