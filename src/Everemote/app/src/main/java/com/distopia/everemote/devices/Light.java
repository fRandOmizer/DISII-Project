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

    public LightOnOffControlProxy getOnOffControl() {
        return onOffControl;
    }
}
