package com.distopia.everemote.devices;

/**
 * Created by chris on 09.07.2016.
 */
public class ShutterProxy extends DeviceProxy implements IRegulatabble {

    public ShutterProxy() {
    }

    @Override
    public float getCurrentValue() {
        return 0;
    }

    @Override
    public float getTargetValue() {
        return 0;
    }

    @Override
    public void setTargetValue(float value) {

    }
}
