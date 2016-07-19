package com.distopia.everemote.devices.controls;

import com.distopia.everemote.network.RaspiClient;

/**
 * Created by chris on 11.07.2016.
 */
public class TVOnOffControlProxy extends RaspiControlProxy implements IOnOffTurnable{

    public TVOnOffControlProxy(RaspiClient tcpClient) {
        super(tcpClient);
    }

    @Override
    public void turnOn() {
        if (raspiClient != null) {
            raspiClient.sendMessage("TVOn");
        }
    }

    @Override
    public void turnOff() {
        if (raspiClient != null) {
            raspiClient.sendMessage("TVOff");
        }
    }
}
