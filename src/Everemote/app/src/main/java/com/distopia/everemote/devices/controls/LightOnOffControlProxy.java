package com.distopia.everemote.devices.controls;

import com.distopia.everemote.network.RaspiClient;

/**
 * Created by chris on 08.07.2016.
 */
public class LightOnOffControlProxy extends RaspiControlProxy implements IOnOffTurnable {

    public LightOnOffControlProxy(RaspiClient tcpClient) {
        super(tcpClient);
    }

    @Override
    public void turnOn() {
        if (raspiClient != null) {
            raspiClient.sendMessage("LightOn");
        }
    }

    @Override
    public void turnOff() {
        if (raspiClient != null) {
            raspiClient.sendMessage("LightOff");
        }
    }
}
