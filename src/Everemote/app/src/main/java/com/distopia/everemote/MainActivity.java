package com.distopia.everemote;

import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AnalogClock;
import android.widget.TextView;

import com.distopia.everemote.devices.Device;
import com.distopia.everemote.devices.Light;
import com.distopia.everemote.devices.Shutter;
import com.distopia.everemote.devices.Speaker;
import com.distopia.everemote.devices.TV;
import com.distopia.everemote.devices.controls.Channel;
import com.distopia.everewidgets.BannerSliderView;
import com.distopia.everewidgets.CircleView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.RunnableFuture;

public class MainActivity extends AppCompatActivity implements DevicesChangeNotifyable {
    private static final String TAG = "MainActivity";

    DeviceFinder deviceFinder;

    private List<Device> allDevices = new ArrayList<>();
    private List<Device> curDevices = new ArrayList<>();

    /**
     * TV.
     */
    private TV tv = new TV(20, 120);
    private int[] channelImages = {R.drawable.das_erste_s, R.drawable.zdf_s, R.drawable.rtl_s};
    private Channel[] channels = {new Channel("Das Erste", 1),
                                  new Channel("ZDF", 2),
                                  new Channel("RTL", 3)};

    /**
     * Light.
     */
    private Light light = new Light(40, 50);

    /**
     * Shutter.
     */
    private Shutter shutter = new Shutter(300, 15);

    /**
     * Speaker.
     */
    private Speaker speaker = new Speaker(200, 250);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Sets up all devices (hardcoded for now).
        this.allDevices.add(light);
        this.allDevices.add(tv);
        this.allDevices.add(shutter);
        this.allDevices.add(speaker);

        // Creates a new device finder (if possible) and registers itself as a subscriber.
        try {
            this.deviceFinder = new DeviceFinder(this,
                                                 allDevices,
                                                 (SensorManager) getSystemService(SENSOR_SERVICE));
        } catch (DeviceFinder.InsufficientHardwareException e) {
            Log.e(TAG, "Insufficient hardware: " + e.toString());
            finish();
        }

        // Sets three images to the TV slider.
        BannerSliderView slider = (BannerSliderView) findViewById(R.id.tv);
        if(slider != null) {
            slider.setImages(channelImages);
            slider.setOnSlideListener(new BannerSliderView.OnSlideListener() {
                @Override
                public void onSlide(int position) {
                    if(0 <= position && position < channels.length) {
                        tv.getCannelControl().setCurrentChannel(channels[position]);
                    }
                }
            });
        }
    }

    @Override
    public void setDevices(final List<Device> devices) {
        curDevices = devices;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                BannerSliderView tv = (BannerSliderView) findViewById(R.id.tv);
                if(devices.isEmpty() && tv != null) {
                    tv.setVisibility(View.GONE);
                }
                for(Device device : devices) {
                    if(device instanceof TV && tv != null) {
                        tv.setVisibility(View.VISIBLE);
                    } else if(tv != null) {
                        tv.setVisibility(View.GONE);
                    }
                }
            }
        });
    }

    @Override
    public void setAngle(final int angle) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                TextView text = (TextView) findViewById(R.id.text);
                if(text != null) {
                    text.setText("Angle: " + angle);
                }
            }
        });
    }
}
