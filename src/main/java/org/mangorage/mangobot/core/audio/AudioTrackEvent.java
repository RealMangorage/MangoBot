package org.mangorage.mangobot.core.audio;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

public class AudioTrackEvent {
    private final AudioTrack track;
    private final Info reason;

    public AudioTrackEvent(AudioTrack track, Info info) {
        this.track = track;
        this.reason = info;
    }

    public AudioTrack getTrack() {
        return track;
    }

    public Info getReason() {
        return reason;
    }

    public enum Info {
        SUCCESS,
        FAILED,
        NO_MATCHES
    }
}
