package com.distopia.everemote.devices.controls;

/**
 * Created by chris on 09.07.2016.
 */
public class ShutterControlProxy extends ControlProxy implements IRegulatabble {

    public ShutterControlProxy() {
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
