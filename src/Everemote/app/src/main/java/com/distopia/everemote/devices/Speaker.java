package com.distopia.everemote.devices;

import com.distopia.everemote.devices.controls.SpeakerControlProxy;
import com.distopia.everemote.network.RaspiClient;

/**
 *
 * Created by chris on 11.07.2016.
 */
public class Speaker extends RaspiDevice {

    // controls
    private SpeakerControlProxy speakerControl;

    /**
     * Creates a new speaker object.
     */
    public Speaker(RaspiClient raspi) {
        super(raspi);
        speakerControl = new SpeakerControlProxy(raspi);
        addControl(speakerControl);
    }

    /**
     * Creates a new speaker within the specified angle range.
     * @param angleBeginning The angle in which the beginning of the speaker is located.
     * @param angleEnd The angle in which the end of the speaker is located.
     */
    public Speaker(int angleBeginning, int angleEnd, RaspiClient raspi) {
        super(angleBeginning, angleEnd, raspi);
        speakerControl = new SpeakerControlProxy(raspi);
        addControl(speakerControl);
    }

    public SpeakerControlProxy getSpeakerControl() {
        return speakerControl;
    }
}
