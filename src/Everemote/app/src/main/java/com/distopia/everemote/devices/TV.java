package com.distopia.everemote.devices;

import com.distopia.everemote.devices.controls.TVChannelControlProxy;
import com.distopia.everemote.devices.controls.TVOnOffControlProxy;
import com.distopia.everemote.devices.controls.TVSpeakerControlProxy;
import com.distopia.everemote.network.RaspiClient;

/**
 * Created by chris on 11.07.2016.
 */
public class TV extends RaspiDevice {

    // controls
    private TVChannelControlProxy cannelControl;
    private TVSpeakerControlProxy volumeControl;
    private TVOnOffControlProxy onOffControl;

    /**
     * Creates new tv object
     */
    public TV(RaspiClient raspiClient) {
        super(raspiClient);
        // create controls
        cannelControl = new TVChannelControlProxy(raspiClient);
        volumeControl = new TVSpeakerControlProxy(raspiClient);
        onOffControl = new TVOnOffControlProxy(raspiClient);
        // add controls
        addControl(cannelControl);
        addControl(volumeControl);
        addControl(onOffControl);
    }

    /**
     * Creates a new TV within the specified angle range.
     * @param angleBeginning The angle in which the beginning of the TV is located.
     * @param angleEnd The angle in which the end of the TV is located.
     */
    public TV(int angleBeginning, int angleEnd, RaspiClient raspiClient) {
        super(angleBeginning, angleEnd, raspiClient);
        // create controls
        cannelControl = new TVChannelControlProxy(raspiClient);
        volumeControl = new TVSpeakerControlProxy(raspiClient);
        onOffControl = new TVOnOffControlProxy(raspiClient);
        // add controls
        addControl(cannelControl);
        addControl(volumeControl);
        addControl(onOffControl);
    }

    // getter

    public TVChannelControlProxy getCannelControl() {
        return cannelControl;
    }

    public TVSpeakerControlProxy getVolumeControl() {
        return volumeControl;
    }

    public TVOnOffControlProxy getOnOffControl() {
        return onOffControl;
    }
}
