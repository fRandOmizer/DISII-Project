package com.distopia.everemote.devices;

import com.distopia.everemote.devices.controls.LightOnOffControlProxy;
import com.distopia.everemote.network.RaspiClient;

/**
 * Created by chris on 19.07.2016.
 */
public abstract class RaspiDevice extends Device {

    public RaspiDevice(RaspiClient raspiClient) {
        // extend constructor in subclass
    }

    /**
     * Creates a new light within the specified angle range.
     * @param angleBeginning The angle in which the beginning of the light is located.
     * @param angleEnd The angle in which the end of the light is located.
     */
    public RaspiDevice(int angleBeginning, int angleEnd, RaspiClient raspiClient) {
        super(angleBeginning, angleEnd);
        // extend constructor in subclass
    }


}
