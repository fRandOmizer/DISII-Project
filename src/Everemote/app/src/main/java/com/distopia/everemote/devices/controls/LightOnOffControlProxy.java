package com.distopia.everemote.devices.controls;

import com.distopia.everemote.network.RaspiClient;

/**
 * Created by chris on 08.07.2016.
 */
public class LightOnOffControlProxy extends ControlProxy implements IOnOffTurnable {

    RaspiClient tcpClient;

    public LightOnOffControlProxy(RaspiClient tcpClient) {
        this.tcpClient = tcpClient;
    }

    @Override
    public void turnOn() {
        if (tcpClient != null) {
            tcpClient.sendMessage("LightsOn");
        }
    }

    @Override
    public void turnOff() {
        if (tcpClient != null) {
            tcpClient.sendMessage("LightsOff");
        }
    }
}
