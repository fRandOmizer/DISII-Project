package com.distopia.everemote;

import com.distopia.everemote.devices.Device;

import java.util.List;

/**
 * This interface can be implemented by any class that wants to be notified in case the list of
 * devices changes the smartphone points at. The implementing class needs to register itself at the
 * DeviceFinder.
 */
public interface DevicesChangeNotifyable {
    /**
     * Is called by the DeviceFinder when the device list has changed.
     * Note that this method is called from outside of the UI thread!
     * @param devices The new device list the smartphone points at. The first element is the nearest
     *                device.
     */
    void setDevices(List<Device> devices);
    void setAngle(int angle);
}
