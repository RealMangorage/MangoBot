package org.mangorage.mangobot.core.audio;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import net.dv8tion.jda.api.entities.channel.Channel;
import org.apache.commons.collections4.QueueUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

public class TrackScheduler extends AudioEventAdapter {
    private final Queue<AudioTrack> list = QueueUtils.emptyQueue();
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
        playing = list.poll();
    }

    public void stopTrack() {
        player.stopTrack();
    }

    public void playTrack() {
        if (playing != null)
            player.playTrack(playing);
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
    }

    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
        if (endReason.mayStartNext) {
            playing = list.poll();
            playTrack();
        } else {
            playing = null;
        }
    }

    @Override
    public void onTrackException(AudioPlayer player, AudioTrack track, FriendlyException exception) {
        // An already playing track threw an exception (track end event will still be received separately)
    }

    @Override
    public void onTrackStuck(AudioPlayer player, AudioTrack track, long thresholdMs) {
        // Audio track has been unable to provide us any audio, might want to just start a new track
    }
}
