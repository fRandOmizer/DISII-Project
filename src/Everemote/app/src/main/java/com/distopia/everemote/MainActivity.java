package com.distopia.everemote;

import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import com.distopia.everemote.devices.Device;
import com.distopia.everemote.devices.Light;
import com.distopia.everemote.devices.Shutter;
import com.distopia.everemote.devices.Speaker;
import com.distopia.everemote.devices.TV;
import com.distopia.everemote.devices.controls.Channel;
import com.distopia.everewidgets.BannerSliderView;
import com.distopia.everewidgets.CardView;

import java.util.ArrayList;
import java.util.List;

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
    private Light light = new Light(0, 50);

    /**
     * Shutter.
     */
    private Shutter shutter = new Shutter(140, 190);

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
        BannerSliderView slider = (BannerSliderView) findViewById(R.id.tv_channels);
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
                CardView tvChannelsCard = (CardView) findViewById(R.id.tv_channels_card);
                CardView tvVolumeCard = (CardView) findViewById(R.id.tv_volume_card);
                CardView lightOnOffCard = (CardView) findViewById(R.id.lights_onoff_card);
                //CardView shutterCard = (CardView) findViewById(R.id.shutter_card);
                CardView speakerVolumeCard = (CardView) findViewById(R.id.speaker_volume_card);
                if(tvChannelsCard != null) {
                    tvChannelsCard.setVisibility(View.GONE);
                }
                if(tvVolumeCard != null) {
                    tvVolumeCard.setVisibility(View.GONE);
                }
                if(lightOnOffCard != null) {
                    lightOnOffCard.setVisibility(View.GONE);
                }
                /*if(shutterCard != null) {
                    shutterCard.setVisibility(View.GONE);
                }*/
                if(speakerVolumeCard != null) {
                    speakerVolumeCard.setVisibility(View.GONE);
                }
                for(Device deviceInRange : curDevices) {
                    if(deviceInRange instanceof TV && tvChannelsCard != null && tvVolumeCard != null) {
                        tvVolumeCard.setVisibility(View.VISIBLE);
                        tvVolumeCard.getParent().bringChildToFront(tvVolumeCard);
                        tvChannelsCard.setVisibility(View.VISIBLE);
                        tvChannelsCard.getParent().bringChildToFront(tvChannelsCard);
                    } else if(deviceInRange instanceof Light && lightOnOffCard != null) {
                        lightOnOffCard.setVisibility(View.VISIBLE);
                        lightOnOffCard.getParent().bringChildToFront(lightOnOffCard);
                    } /*else if(deviceInRange instanceof Shutter && shutterCard != null) {
                        shutterCard.setVisibility(View.VISIBLE);
                        shutterCard.getParent().bringChildToFront(shutterCard);
                    }*/ else if(deviceInRange instanceof Speaker && speakerVolumeCard != null) {
                        speakerVolumeCard.setVisibility(View.VISIBLE);
                        speakerVolumeCard.getParent().bringChildToFront(speakerVolumeCard);
                    }
                }
            }
        });
    }

    /**
     * Toggles the lights. Is called in case the user pressed on the light button.
     * @param view The view that issued this call.
     */
    public void toggleLights(View view) {
        ImageButton lightButton = ((ImageButton) findViewById(R.id.lights_onoff));
        if(lightButton != null) {
            if (light.isOn()) {
                lightButton.setImageResource(R.drawable.lightbulb_on);
            } else {
                lightButton.setImageResource(R.drawable.lightbulb_off);
            }
        }
        light.toggleLight();
    }
}
