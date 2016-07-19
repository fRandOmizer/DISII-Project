package com.distopia.everemote.devices.controls;

import com.distopia.everemote.network.RaspiClient;

/**
 * Created by chris on 19.07.2016.
 */
public abstract class RaspiControlProxy extends ControlProxy {

    protected RaspiClient raspiClient;

    public RaspiControlProxy(RaspiClient tcpClient) {
        this.raspiClient = tcpClient;
    }
}
