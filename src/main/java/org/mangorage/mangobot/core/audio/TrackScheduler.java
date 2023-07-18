package org.mangorage.mangobot.core.audio;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;


@Deprecated
public class TrackScheduler extends AudioEventAdapter {
    private final Queue<AudioTrack> list = new ConcurrentLinkedQueue<>();
    private AudioTrack playing;
    private final AudioPlayer player;
    private Status status = Status.STOPPED;

    public enum Status {
        PLAYING,
        STOPPED,
        PAUSED
    }

    public TrackScheduler(AudioPlayer player) {
        this.player = player;
        player.addListener(this);
    }

    public Status getStatus() {
        return status;
    }

    public void playTrack(AudioTrack track) {
        this.playing = track;
        this.status = Status.PLAYING;
        playTrack();
    }

    public void queueTrack(AudioTrack track) {
        list.add(track);
    }

    public void skipTrack() {
        stopTrack();
    }

    public void stopTrack() {
        player.stopTrack();
    }

    public void playTrack() {
        if (playing != null) {
            player.playTrack(playing);
            this.status = Status.PLAYING;
        }
    }

    @Override
    public void onPlayerPause(AudioPlayer player) {
        // Player was paused
        this.status = Status.PAUSED;
    }

    @Override
    public void onPlayerResume(AudioPlayer player) {
        // Player was resumed
        this.status = Status.PLAYING;
    }

    @Override
    public void onTrackStart(AudioPlayer player, AudioTrack track) {
        // A track started playing
        this.status = Status.PLAYING;
    }

    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
        this.status = Status.STOPPED;
        this.playing = null;

        if (endReason.mayStartNext) {
            if (list.peek() != null)
                playTrack(list.poll());
        }
    }

    @Override
    public void onTrackException(AudioPlayer player, AudioTrack track, FriendlyException exception) {
        // An already playing track threw an exception (track end event will still be received separately)
    }

    @Override
    public void onTrackStuck(AudioPlayer player, AudioTrack track, long thresholdMs) {
        // Audio track has been unable to provide us any audio, might want to just start a new track
        stopTrack();
        this.status = Status.STOPPED;
        this.playing = null;
        if (list.peek() != null)
            playTrack(list.poll());
    }
}
