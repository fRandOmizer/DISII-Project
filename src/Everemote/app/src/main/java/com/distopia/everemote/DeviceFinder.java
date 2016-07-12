package com.distopia.everemote;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

import com.distopia.everemote.devices.Device;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 *  The DeviceFinder is able to register objects that want to be informed when the list of devices
 *  that the device points to changes. Internally, it does so by means of a thread running in the
 *  background.
 */
public class DeviceFinder implements SensorEventListener {
    public static final String TAG = "DeviceFinder";

    /**
     * Is thrown in case the device does not have the compass sensors available.
     */
    class InsufficientHardwareException extends Exception {
        InsufficientHardwareException(String message) {
            super(message);
        }
    }

    /// The list of subscribers that want to be informed.
    private List<DevicesChangeNotifyable> subscribers = new ArrayList<>();
    /// The list of devices to look for.
    private List<Device> devices = new ArrayList<>();
    /// The current device angle.
    private int deviceAngle = 0;
    /// The previous device angle (To be more efficient in the updating process).
    private int prevDeviceAngle = 1;

    /// Stores the current rotation matrix from which the orientation data is calculated.
    private float[] rMat        = new float[9];
    /// Stores current orientation data.
    private float[] orientation = new float[3];

    /// The previous and current list of devices that the phone points at. If they are not the same,
    /// the subscribers will be informed.
    List<Device> prevDevices = new ArrayList<>();
    List<Device> newDevices  = new ArrayList<>();

    /// The sensor manager which should be given by the calling activity.
    SensorManager sensorManager;
    /// The sensor that is used for locating devices.
    Sensor rotationSensor;

    /**
     * Creates a new device finder for the given subscriber which will be directly informed about
     * the current list of devices, which is always a subset of the given device list.
     * @param subscriber The initial subscriber.
     * @param devices The list of devices to look for.
     */
    DeviceFinder(DevicesChangeNotifyable subscriber, List<Device> devices, SensorManager sensorManager) throws InsufficientHardwareException {
        this.sensorManager = sensorManager;
        setDevices(devices);
        addSubscriber(subscriber);

        this.rotationSensor = this.sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
        boolean haveRotationSensor = this.sensorManager.registerListener(this, this.rotationSensor, SensorManager.SENSOR_DELAY_GAME);

        if (!haveRotationSensor) {
            throw new InsufficientHardwareException("No rotation sensor present.");
        }
    }

    /**
     * After this call, the given subscriber will be informed about all device changes using its
     * callback method.
     * @param subscriber The subscriber that wants to be informed.
     */
    public void addSubscriber(DevicesChangeNotifyable subscriber) {
        subscribers.add(subscriber);
    }

    /**
     * The given subscriber will not be informed about device list changes after this call.
     * @param subscriber The subscriber to remove from subscription.
     * @return true iff the subscriber has previously subscribed.
     */
    public boolean removeSubscriber(DevicesChangeNotifyable subscriber) {
        return subscribers.remove(subscriber);
    }

    /**
     * Sets the list of devices to look for.
     * @param devices The new list of devices to look for.
     */
    public void setDevices(List<Device> devices) {
        // Sorts the device list according to their start angles.
        Collections.sort(devices, new Comparator<Device>() {
            @Override
            public int compare(Device d1, Device d2) {
                return d1.getAngleBeginning() - d2.getAngleBeginning();
            }
        });
        this.devices = devices;
    }

    /**
     * Adds the device to the list of devices to look for.
     * @param device The device which is to add.
     */
    public void addDevice(Device device) {
        this.devices.add(device);
        // Sorts the device list (Should be O(n)).
        Collections.sort(devices, new Comparator<Device>() {
            @Override
            public int compare(Device d1, Device d2) {
                return d1.getAngleBeginning() - d2.getAngleBeginning();
            }
        });
    }

    /**
     * Returns the list of devices, sorted according to their angle (starting with the lowest
     * angle).
     * @return The sorted list of devices which is currently looked for.
     */
    public List<Device> getDevices() {
        return this.devices;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR) {
            // calculate th rotation matrix
            SensorManager.getRotationMatrixFromVector(rMat, event.values);
            // get the azimuth value (orientation[0]) in degree
            deviceAngle = (int) (Math.toDegrees(SensorManager.getOrientation(rMat, orientation )[0]) + 360) % 360;
            // Checks if we even need to consider an update.
            if(prevDeviceAngle != deviceAngle) {
                // Gathers the new device list.
                newDevices.clear();
                for (Device device : this.devices) {
                    if (device.getAngleBeginning() <= deviceAngle && deviceAngle <= device.getAngleEnd()) {
                        newDevices.add(device);
                    }
                }
                // Publishes the new device list in case any changes happened.
                if (!prevDevices.equals(newDevices)) {
                    Log.i(TAG, "Found new device list: " + newDevices.toString());
                    // Informs all subscribers about the new device list.
                    for (DevicesChangeNotifyable subscriber : this.subscribers) {
                        Log.i(TAG, "Informing subscriber " + subscriber);
                        subscriber.setDevices(newDevices);
                    }
                    prevDevices = new ArrayList<Device>(newDevices);
                }
                prevDeviceAngle = deviceAngle;
            }
        }
    }
}
