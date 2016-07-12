package com.distopia.everemote.devices;

import com.distopia.everemote.devices.controls.LightOnOffControlProxy;

/**
 * Created by chris on 11.07.2016.
 */
public class Light extends Device {

    private LightOnOffControlProxy onOffControl;

    public Light() {
        onOffControl = new LightOnOffControlProxy();
        addControl(onOffControl);
    }

    /**
     * Creates a new light within the specified angle range.
     * @param angleBeginning The angle in which the beginning of the light is located.
     * @param angleEnd The angle in which the end of the light is located.
     */
    public Light(int angleBeginning, int angleEnd) {
        super(angleBeginning, angleEnd);
        onOffControl = new LightOnOffControlProxy();
        addControl(onOffControl);
    }

    public LightOnOffControlProxy getOnOffControl() {
        return onOffControl;
    }
}
