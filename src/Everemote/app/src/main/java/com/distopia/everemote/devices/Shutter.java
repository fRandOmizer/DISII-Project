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

    /**
     * Creates a new shutter within the specified angle range.
     * @param angleBeginning The angle in which the beginning of the shutter is located.
     * @param angleEnd The angle in which the end of the shutter is located.
     */
    public Shutter(int angleBeginning, int angleEnd) {
        super(angleBeginning, angleEnd);
        shutterControl = new ShutterControlProxy();
    }

    public ShutterControlProxy getShutterControl() {
        return shutterControl;
    }
}
