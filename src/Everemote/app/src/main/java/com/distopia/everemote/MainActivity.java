package com.distopia.everemote;

import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.distopia.everemote.devices.Device;
import com.distopia.everemote.devices.Light;
import com.distopia.everemote.devices.Shutter;
import com.distopia.everemote.devices.Speaker;
import com.distopia.everemote.devices.TV;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements DevicesChangeNotifyable {
    private static final String TAG = "MainActivity";

    DeviceFinder deviceFinder;

    private List<Device> allDevices = new ArrayList<>();
    private List<Device> curDevices = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Sets up all devices (hardcoded for now).
        this.allDevices.add(new Light(40, 50));
        this.allDevices.add(new TV(20, 120));
        this.allDevices.add(new Speaker(280, 10));
        this.allDevices.add(new Shutter(60, 75));

        // Creates a new device finder (if possible) and registers itself as a subscriber.
        try {
            this.deviceFinder = new DeviceFinder(this, allDevices, (SensorManager)getSystemService(SENSOR_SERVICE));
        } catch (DeviceFinder.InsufficientHardwareException e) {
            Log.e(TAG, "Insufficient hardware: " + e.toString());
            finish();
        }
    }

    @Override
    public void setDevices(List<Device> devices) {
        curDevices = devices;
    }

}
