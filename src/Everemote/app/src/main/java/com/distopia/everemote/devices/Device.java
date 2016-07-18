package com.distopia.everemote.devices;

import android.util.Log;

import com.distopia.everemote.devices.controls.ControlProxy;

import java.util.ArrayList;

/**
 * Can contain multiple ControlProxys
 * Created by chris on 05.07.2016.
 */
public class Device {

    private ArrayList<ControlProxy> controls;
    /// The angle in which the device begins at.
    private int angleBeginning = 0;
    /// The angle in which the device ends at.
    private int angleEnd = 0;

    /**
     * creates an object with no controls
     */
    public Device() {
        // creates empty list of proxys
        controls = new ArrayList<ControlProxy>();
    }

    /**
     * creates an object with no controls and the specified angle range.
     * @param angleBeginning The angle in which the beginning of device is located.
     * @param angleEnd The angle in which the end of the device is located.
     */
    public Device(int angleBeginning, int angleEnd) {
        // creates empty list of proxys
        controls = new ArrayList<ControlProxy>();
        setAngleBeginning(angleBeginning);
        setAngleEnd(angleEnd);
    }

    public void addControl(ControlProxy cp) {
        controls.add(cp);
    }

    /**
     *
     * @return the number of contained controlProxys
     */
    public int getControlsSize() {
        return controls.size();
    }

    /**
     * Returns the controlProxy at position i
     * @param index index of the control
     * @return the control at index
     */
    public ControlProxy getControls(int index) {
        return controls.get(index);
    }

    /**
     * Sets the beginning angle of the device.
     * @param angleBeginning The beginning angle.
     */
    public void setAngleBeginning(int angleBeginning) {
        assert(0 <= angleBeginning && angleBeginning < 360);
        this.angleBeginning = angleBeginning;
    }

    /**
     * Sets the end angle of the device.
     * @param angleEnd The end angle.
     */
    public void setAngleEnd(int angleEnd) {
        assert(0 <= angleEnd && angleEnd < 360);
        this.angleEnd = angleEnd;
    }

    /**
     * Returns the beginning angle of the device.
     * @return The beginning angle.
     */
    public int getAngleBeginning() {
        return this.angleBeginning;
    }

    /**
     * Returns the end angle of the device.
     * @return The end angle.
     */
    public int getAngleEnd() {
        return this.angleEnd;
    }

    @Override
    public String toString() {
        return "Device [" + getAngleBeginning() + ", " + getAngleEnd() + "]";
    }
}
