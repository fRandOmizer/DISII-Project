package com.distopia.everemote.devices;

import com.distopia.everemote.devices.controls.ShutterControlProxy;

/**
 * Created by chris on 11.07.2016.
 */
public class Shutter extends Device {

    private ShutterControlProxy shutterControl;

    public Shutter() {
        shutterControl = new ShutterControlProxy();
    }

    public ShutterControlProxy getShutterControl() {
        return shutterControl;
    }
}
