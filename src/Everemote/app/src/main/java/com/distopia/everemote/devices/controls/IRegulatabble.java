package com.distopia.everemote.devices.controls;

/**
 * Things that can be regulated by a target value.
 * Created by chris on 09.07.2016.
 */
public interface IRegulatabble {

    /**
     *
     * @return the current value
     */
    public float getCurrentValue();

    /**
     *
     * @return the target value
     */
    public float getTargetValue();

    /**
     *
     * @param value the target value which will be set
     */
    public void setTargetValue(float value);
}
