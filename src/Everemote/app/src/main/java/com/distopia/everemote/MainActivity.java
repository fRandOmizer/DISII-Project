package com.distopia.everemote;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;

import com.distopia.everemote.devices.Device;
import com.distopia.everemote.devices.Light;
import com.distopia.everemote.devices.Shutter;
import com.distopia.everemote.devices.Speaker;
import com.distopia.everemote.devices.TV;
import com.distopia.everemote.devices.controls.Channel;
import com.distopia.everemote.network.RaspiClient;
import com.distopia.everewidgets.AbsoluteRegulatorView;
import com.distopia.everewidgets.BannerSliderView;
import com.distopia.everewidgets.CardView;
import com.distopia.everewidgets.FlowerData;
import com.distopia.everewidgets.FlowerView;
import com.distopia.everewidgets.VolumeView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity implements DevicesChangeNotifyable, FlowerView.OnUpdateListener {
    private static final String TAG               = "MainActivity";
    private static final String TV_MIN_RANGE      = "0";
    private static final String TV_MAX_RANGE      = "80";
    private static final String LIGHT_MIN_RANGE   = "100";
    private static final String LIGHT_MAX_RANGE   = "180";
    private static final String SHUTTER_MIN_RANGE = "140";
    private static final String SHUTTER_MAX_RANGE = "190";
    private static final String SPEAKER_MIN_RANGE = "240";
    private static final String SPEAKER_MAX_RANGE = "320";

    SharedPreferences sharedPreferences;
    OnSharedPreferenceChangeListener prefListener = new OnSharedPreferenceChangeListener() {
        public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
            if(sharedPreferences != null) {
                if ("tv_min_range".equals(key)) {
                    tv.setAngleBeginning(
                            Integer.valueOf(sharedPreferences.getString(key, TV_MIN_RANGE))
                    );
                } else if ("tv_max_range".equals(key)) {
                    tv.setAngleEnd(
                            Integer.valueOf(sharedPreferences.getString(key, TV_MAX_RANGE))
                    );
                } else if ("light_min_range".equals(key)) {
                    light.setAngleBeginning(
                            Integer.valueOf(sharedPreferences.getString(key, LIGHT_MIN_RANGE))
                    );
                } else if ("light_max_range".equals(key)) {
                    light.setAngleEnd(
                            Integer.valueOf(sharedPreferences.getString(key, LIGHT_MAX_RANGE))
                    );
                } else if ("shutter_min_range".equals(key)) {
                    shutter.setAngleBeginning(
                            Integer.valueOf(sharedPreferences.getString(key, SHUTTER_MIN_RANGE))
                    );
                } else if ("shutter_max_range".equals(key)) {
                    shutter.setAngleEnd(
                            Integer.valueOf(sharedPreferences.getString(key, SHUTTER_MAX_RANGE))
                    );
                } else if ("speaker_min_range".equals(key)) {
                    speaker.setAngleBeginning(
                            Integer.valueOf(sharedPreferences.getString(key, SPEAKER_MIN_RANGE))
                    );
                } else if ("speaker_max_range".equals(key)) {
                    speaker.setAngleEnd(
                            Integer.valueOf(sharedPreferences.getString(key, SPEAKER_MAX_RANGE))
                    );
                }
            }

            updateMap();
        }
    };

    /**
     * TCP client connection.
     */
    private RaspiClient tcpClient;

    DeviceFinder deviceFinder;

    private List<Device> allDevices = new ArrayList<>();
    private List<Device> curDevices = new ArrayList<>();

    /// Whether the widget screen is currently locked or not.
    private boolean widgetsLocked = false;
    private MenuItem lockItem = null;

    // manual map stuff
    final private int[] mapIds = {
            R.drawable.ic_tv_black_24dp,
            R.drawable.ic_lightbulb_outline_black_24dp,
            R.drawable.ic_speaker_black_24dp
    };

    private HashMap<Integer, int[]> idToCard = new HashMap<>();

    /**
     * TV.
     */
    private TV tv = new TV(Integer.valueOf(TV_MIN_RANGE), Integer.valueOf(TV_MAX_RANGE));
    private int[] channelImages = {R.drawable.das_erste_s, R.drawable.zdf_s, R.drawable.rtl_s};
    private Channel[] channels = {new Channel("Das Erste", 1),
                                  new Channel("ZDF", 2),
                                  new Channel("RTL", 3)};

    /**
     * Light.
     */
    private Light light = new Light(Integer.valueOf(LIGHT_MIN_RANGE),
                                    Integer.valueOf(LIGHT_MAX_RANGE),
                                    tcpClient);

    /**
     * Shutter.
     */
    private Shutter shutter = new Shutter(Integer.valueOf(SHUTTER_MIN_RANGE),
                                          Integer.valueOf(SHUTTER_MAX_RANGE));

    /**
     * Speaker.
     */
    private Speaker speaker = new Speaker(Integer.valueOf(SPEAKER_MIN_RANGE),
                                          Integer.valueOf(SPEAKER_MAX_RANGE));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPreferences.registerOnSharedPreferenceChangeListener(prefListener);

        // Sets up all devices (hardcoded for now).
        this.allDevices.add(tv);
        this.allDevices.add(light);
        this.allDevices.add(speaker);
        this.allDevices.add(shutter);

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
        if (slider != null) {
            slider.setImages(channelImages);
            slider.setOnSlideListener(new BannerSliderView.OnSlideListener() {
                @Override
                public void onSlide(int position) {
                    if (0 <= position && position < channels.length) {
                        tv.getCannelControl().setCurrentChannel(channels[position]);
                    }
                }
            });
        }

        // Sets the volume controls.
        VolumeView volumeView = (VolumeView) findViewById(R.id.volume);
        if (volumeView != null) {
            volumeView.setOnUpdateListener(new VolumeView.OnUpdateListener() {
                @Override
                public void onUpdate(float value) {
                    speaker.getSpeakerControl().setVolume(value);
                }
            });
        }

        // Sets the shutter controls.
        AbsoluteRegulatorView shutterView = null;//(AbsoluteRegulatorView) findViewById(R.id.shutter);
        if (shutterView != null) {
            shutterView.setOnUpdateListener(new AbsoluteRegulatorView.OnUpdateListener() {
                @Override
                public void onUpdate(float value) {
                    shutter.getShutterControl().setTargetValue(value);
                }
            });
        }

        // creates manual map
        idToCard.put(R.drawable.ic_tv_black_24dp, new int[]{R.id.tv_channels_card, R.id.tv_volume_card});
        idToCard.put(R.drawable.ic_lightbulb_outline_black_24dp, new int[]{R.id.lights_onoff_card});
        idToCard.put(R.drawable.ic_speaker_black_24dp, new int[]{R.id.speaker_volume_card});

        FlowerView flower = (FlowerView) findViewById(R.id.flower);
        updateMap();
        flower.setOnUpdateListener(this);
    }

    void updateMap() {
        FlowerData[] flowerData = new FlowerData[mapIds.length];
        for (int i = 0; i < flowerData.length; i++) {
            FlowerData data = new FlowerData();
            data.iconId = mapIds[i];
            switch (i)
            {
                case 0:
                    data.startAngle = tv.getAngleBeginning();
                    data.endAngle = tv.getAngleEnd();
                    break;
                case 1:
                    data.startAngle = light.getAngleBeginning();
                    data.endAngle = light.getAngleEnd();
                    break;
                case 2:
                    data.startAngle = speaker.getAngleBeginning();
                    data.endAngle = speaker.getAngleEnd();
                    break;
            }
            flowerData[i] = data;
        }

        FlowerView flower = (FlowerView) findViewById(R.id.flower);
        flower.setIconData(flowerData);
    }

    @Override
    public void onResume() {
        super.onResume();

        // Connects to the server.
        new ConnectTask().execute("");
        // Sends the message to the server.
        if (tcpClient != null) {
            tcpClient.sendMessage("Connection Ok");
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        // Stops the network connection.
        if (tcpClient != null) {
            tcpClient.stopClient();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        lockItem = menu.findItem(R.id.action_lock);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        CardView mapCard = (CardView) findViewById(R.id.flower_card);

        switch (item.getItemId()) {
            case R.id.action_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                return true;

            case R.id.action_lock:
                // Toggles the locked icon and the locked flag.
                if (widgetsLocked) {
                    item.setIcon(R.drawable.ic_lock_open_white_24dp);
                } else {
                    item.setIcon(R.drawable.ic_lock_outline_white_24dp);
                }
                widgetsLocked = !widgetsLocked;

                // hide manual selection
                mapCard.setVisibility(View.GONE);

                updateControlWidgets();
                return true;

            case R.id.action_select:
                // show manual selection
                mapCard.setVisibility(View.VISIBLE);
                mapCard.getParent().bringChildToFront(mapCard);
                // lock card view
                widgetsLocked = true;
                lockItem.setIcon(R.drawable.ic_lock_outline_white_24dp);
                // remove anything else from card view
                CardView tvChannelsCard = (CardView) findViewById(R.id.tv_channels_card);
                tvChannelsCard.setVisibility(View.GONE);
                CardView tvVolumeCard = (CardView) findViewById(R.id.tv_volume_card);
                tvVolumeCard.setVisibility(View.GONE);
                CardView lightOnOffCard = (CardView) findViewById(R.id.lights_onoff_card);
                lightOnOffCard.setVisibility(View.GONE);
                //CardView shutterCard = (CardView) findViewById(R.id.shutter_card);
                //shutterCard.setVisibility(View.GONE);
                CardView speakerVolumeCard = (CardView) findViewById(R.id.speaker_volume_card);
                speakerVolumeCard.setVisibility(View.GONE);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onUpdate(int value) {
        CardView mapCard = (CardView) findViewById(R.id.flower_card);
        mapCard.setVisibility(View.GONE);

        int[] cardIds = idToCard.get(mapIds[value]);

        for (int i = 0; i < cardIds.length; i++) {
            CardView cardToShow = (CardView) findViewById(cardIds[i]);
            cardToShow.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void setDevices(final List<Device> devices) {
        curDevices = devices;
        updateControlWidgets();
    }

    @Override
    public void setAngle(int angle) {
        FlowerView flower = (FlowerView) findViewById(R.id.flower);
        flower.setAngleOffset(360 -angle - 90);
    }

    /**
     * Renders all control widgets according to the current device list. This function is run in the
     * UI thread.
     */
    private void updateControlWidgets() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (!widgetsLocked) {
                    CardView tvChannelsCard = (CardView) findViewById(R.id.tv_channels_card);
                    CardView tvVolumeCard = (CardView) findViewById(R.id.tv_volume_card);
                    CardView lightOnOffCard = (CardView) findViewById(R.id.lights_onoff_card);
                    //CardView shutterCard = (CardView) findViewById(R.id.shutter_card);
                    CardView speakerVolumeCard = (CardView) findViewById(R.id.speaker_volume_card);
                    if (tvChannelsCard != null) {
                        tvChannelsCard.setVisibility(View.GONE);
                    }
                    if (tvVolumeCard != null) {
                        tvVolumeCard.setVisibility(View.GONE);
                    }
                    if (lightOnOffCard != null) {
                        lightOnOffCard.setVisibility(View.GONE);
                    }
                    /*if (shutterCard != null) {
                        shutterCard.setVisibility(View.GONE);
                    }*/
                    if (speakerVolumeCard != null) {
                        speakerVolumeCard.setVisibility(View.GONE);
                    }
                    for (Device deviceInRange : curDevices) {
                        if (deviceInRange instanceof TV && tvChannelsCard != null
                                && tvVolumeCard != null) {
                            tvVolumeCard.setVisibility(View.VISIBLE);
                            tvVolumeCard.getParent().bringChildToFront(tvVolumeCard);
                            tvChannelsCard.setVisibility(View.VISIBLE);
                            tvChannelsCard.getParent().bringChildToFront(tvChannelsCard);
                        } else if (deviceInRange instanceof Light && lightOnOffCard != null) {
                            lightOnOffCard.setVisibility(View.VISIBLE);
                            lightOnOffCard.getParent().bringChildToFront(lightOnOffCard);
                        } /*else if (deviceInRange instanceof Shutter && shutterCard != null) {
                            shutterCard.setVisibility(View.VISIBLE);
                            shutterCard.getParent().bringChildToFront(shutterCard);
                        }*/ else if (deviceInRange instanceof Speaker && speakerVolumeCard != null) {
                            speakerVolumeCard.setVisibility(View.VISIBLE);
                            speakerVolumeCard.getParent().bringChildToFront(speakerVolumeCard);
                        }
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
        if (lightButton != null) {
            if (light.isOn()) {
                lightButton.setImageResource(R.drawable.lightbulb_on);
                if (tcpClient != null) {
                    tcpClient.sendMessage("LightOn");
                }
            } else {
                lightButton.setImageResource(R.drawable.lightbulb_off);
                if (tcpClient != null) {
                    tcpClient.sendMessage("LightOff");
                }
            }
        }
        light.toggleLight();
    }

    public class ConnectTask extends AsyncTask<String,String,RaspiClient> {

        @Override
        protected RaspiClient doInBackground(String... message) {

            // We create a TCPClient object and...
            tcpClient = new RaspiClient(new RaspiClient.OnMessageReceived() {
                @Override
                //here the messageReceived method is implemented
                public void messageReceived(String message) {
                    // ...this method calls the onProgressUpdate.
                    publishProgress(message);
                }
            });
            tcpClient.run();

            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
        }
    }

}

