package com.distopia.everemote.devices;

import com.distopia.everemote.devices.controls.SpeakerControlProxy;

/**
 *
 * Created by chris on 11.07.2016.
 */
public class Speaker extends Device {

    // controls
    private SpeakerControlProxy speakerControl;

    /**
     * Creates a new speaker object.
     */
    public Speaker() {
        speakerControl = new SpeakerControlProxy();
        addControl(speakerControl);
    }

    /**
     * Creates a new speaker within the specified angle range.
     * @param angleBeginning The angle in which the beginning of the speaker is located.
     * @param angleEnd The angle in which the end of the speaker is located.
     */
    public Speaker(int angleBeginning, int angleEnd) {
        super(angleBeginning, angleEnd);
        speakerControl = new SpeakerControlProxy();
        addControl(speakerControl);
    }

    public SpeakerControlProxy getSpeakerControl() {
        return speakerControl;
    }
}
