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
public class DeviceFinder extends Thread {
    public static final String TAG = "DeviceFinder";

    class InsufficientHardwareException extends Exception {
        InsufficientHardwareException(String message) {
            super(message);
        }
    }

    private SensorEventListener sensorEventListener = new SensorEventListener() {
        float[] aData       = new float[3]; // Accelerometer
        float[] mData       = new float[3]; // Magnetometer
        float[] rMat        = new float[9];
        float[] iMat        = new float[9];
        float[] orientation = new float[3];

        public void onAccuracyChanged(Sensor sensor, int accuracy) {}

        @Override
        public void onSensorChanged(SensorEvent event) {
            float[] data;
            switch (event.sensor.getType()) {
                case Sensor.TYPE_ACCELEROMETER:
                    aData = event.values.clone();
                    break;
                case Sensor.TYPE_MAGNETIC_FIELD:
                    mData = event.values.clone();
                    break;
                default: return;
            }

            if (SensorManager.getRotationMatrix( rMat, iMat, aData, mData)) {
                deviceAngle = (int) (Math.toDegrees(SensorManager.getOrientation(rMat, orientation)[0]) + 360) % 360;
                Log.i(TAG, "Updated device angle to " + deviceAngle);
            }
        }
    };

    /// The list of subscribers that want to be informed.
    private List<DevicesChangeNotifyable> subscribers = new ArrayList<>();
    /// The list of devices to look for.
    private List<Device> devices = new ArrayList<>();
    /// The current device angle.
    private int deviceAngle;

    /// The sensors that are used for locating devices.
    SensorManager sensorManager;
    Sensor accelerometer;
    Sensor magnetometer;

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

        this.accelerometer = this.sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        boolean haveAccelerometer = this.sensorManager.registerListener(sensorEventListener, this.accelerometer, SensorManager.SENSOR_DELAY_GAME );

        this.magnetometer = this.sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        boolean haveMagnetometer = this.sensorManager.registerListener(sensorEventListener, this.magnetometer, SensorManager.SENSOR_DELAY_GAME );

        if (!haveAccelerometer && !haveMagnetometer) {
            throw new InsufficientHardwareException("No accelerometer and magnetometer present.");
        } else if (!haveAccelerometer) {
            throw new InsufficientHardwareException("No accelerometer present.");
        } else if (!haveMagnetometer) {
            throw new InsufficientHardwareException("No magnetometer present.");
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

    /**
     * Runs the device finding method. If any change is detected, the method will inform all the
     * subscribers about that event.
     */
    public void run() {
        List<Device> prevDevices = new ArrayList<>();
        List<Device> newDevices  = new ArrayList<>();
        while(!interrupted()) {
            // Gathers the new device list.
            for(Device device : this.devices) {
                if(device.getAngleBeginning() <= deviceAngle && deviceAngle <= device.getAngleEnd()) {
                    newDevices.add(device);
                }
            }
            // Publishes the new device list in case any changes happened.
            if(!prevDevices.equals(newDevices)) {
                Log.i(TAG, "Found new device list: " + newDevices.toString());
                // Informs all subscribers about the new device list.
                for (DevicesChangeNotifyable subscriber : this.subscribers) {
                    Log.i(TAG, "Informing subscriber " + subscriber);
                    subscriber.setDevices(newDevices);
                }
            }
            prevDevices = newDevices;
            newDevices.clear();
        }

    }
}
