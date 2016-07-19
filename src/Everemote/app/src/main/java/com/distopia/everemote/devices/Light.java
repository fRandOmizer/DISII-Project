package com.distopia.everemote.devices;

import com.distopia.everemote.devices.controls.LightOnOffControlProxy;
import com.distopia.everemote.network.RaspiClient;

/**
 * Created by chris on 11.07.2016.
 */
public class Light extends RaspiDevice {

    private boolean on = false;

    private LightOnOffControlProxy onOffControl;

    public Light(RaspiClient raspiClient) {
        super(raspiClient);
        onOffControl = new LightOnOffControlProxy(raspiClient);
        addControl(onOffControl);

    }

    /**
     * Creates a new light within the specified angle range.
     * @param angleBeginning The angle in which the beginning of the light is located.
     * @param angleEnd The angle in which the end of the light is located.
     */
    public Light(int angleBeginning, int angleEnd, RaspiClient raspiClient) {
        super(angleBeginning, angleEnd, raspiClient);
        onOffControl = new LightOnOffControlProxy(raspiClient);
        addControl(onOffControl);
    }

    public LightOnOffControlProxy getOnOffControl() {
        return onOffControl;
    }

    /**
     * Returns the current light state of the light.
     * @return If true, then the light is on. False otherwise.
     */
    public boolean isOn() {
        return on;
    }

    /**
     * Toggles the state of the light.
     */
    public void toggleLight() {
        if(this.on) {
            this.onOffControl.turnOff();
        }
        else
        {
            this.onOffControl.turnOn();
        }
        this.on = !this.on;
    }
}
