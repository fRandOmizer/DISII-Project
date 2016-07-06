package com.distopia.everemote.devices;

/**
 * Created by chris on 05.07.2016.
 */
public interface IVolumeSelectable {

    /**
     *
     * @return the current soundLevel between in range of [0.0f, 1.0f]
     */
    public float getVolume();

    /**
     *
     * @param volume the new soundLevel. Should be in range of [0.0f, 1.0f]
     */
    public void setVolume(float volume);
}
