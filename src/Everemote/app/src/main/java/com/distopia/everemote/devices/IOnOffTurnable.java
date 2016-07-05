package com.distopia.everemote.devices;

/**
 * Interface for devices which can be turned on or off.
 * Created by chris on 05.07.2016.
 */
public interface IOnOffTurnable {

    /**
     * Turns the device on
     */
    public void turnOn();

    /**
     * Turns the device off
     */
    public void turnOff();
}
