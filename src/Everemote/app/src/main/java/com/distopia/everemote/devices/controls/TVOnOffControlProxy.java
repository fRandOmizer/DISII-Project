package com.distopia.everemote.devices.controls;

import com.distopia.everemote.network.RaspiClient;

/**
 * Created by chris on 11.07.2016.
 */
public class TVOnOffControlProxy extends RaspiControlProxy implements IOnOffTurnable{

    private boolean on = false;

    public TVOnOffControlProxy(RaspiClient tcpClient) {
        super(tcpClient);
    }

    @Override
    public void turnOn() {
        on = true;
        if (raspiClient != null) {
            raspiClient.sendMessage("TVOn");
        }
    }

    @Override
    public void turnOff() {
        on = false;
        if (raspiClient != null) {
            raspiClient.sendMessage("TVOff");
        }
    }

    public boolean isOn() {
        return on;
    }
}
