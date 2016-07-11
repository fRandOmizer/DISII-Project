package com.distopia.everemote.devices;

import com.distopia.everemote.devices.controls.ControlProxy;

import java.util.ArrayList;

/**
 * Can contain multiple ControlProxys
 * Created by chris on 05.07.2016.
 */
public class Device {

    private ArrayList<ControlProxy> controls;

    /**
     * creates an object with no controls
     */
    public Device() {
        // creates empty list of proxys
        controls = new ArrayList<ControlProxy>();
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
}
