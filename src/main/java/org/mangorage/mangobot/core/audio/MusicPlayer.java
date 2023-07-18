package org.mangorage.mangobot.core.audio;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEvent;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventListener;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.function.Consumer;

public class MusicPlayer extends AudioEventAdapter {

    private static final MusicPlayer MUSIC_PLAYER = new MusicPlayer();

    public static MusicPlayer getInstance() {
        return MUSIC_PLAYER;
    }

    private final AudioPlayerManager manager = new DefaultAudioPlayerManager();
    private final AudioPlayer audioPlayer = manager.createPlayer();
    private final Deque<AudioTrack> TRACKS_QUEUE = new ArrayDeque<>();
    private AudioStatus status = AudioStatus.STOPPED;

    private MusicPlayer() {
        AudioSourceManagers.registerLocalSource(manager);
        AudioSourceManagers.registerRemoteSources(manager);
        audioPlayer.addListener(this);
    }

    public AudioPlayer getAudioPlayer() {
        return audioPlayer;
    }


    public boolean isPlaying() {
        return audioPlayer.getPlayingTrack() != null;
    }

    public void load(String URL, Consumer<AudioTrackEvent> eventConsumer) {
        manager.loadItem(URL, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack track) {
                eventConsumer.accept(new AudioTrackEvent(track, AudioTrackEvent.Info.SUCCESS));
            }

            @Override
            public void playlistLoaded(AudioPlaylist playlist) {
                // Allow playlists maybe?
            }

            @Override
            public void noMatches() {
                eventConsumer.accept(new AudioTrackEvent(null, AudioTrackEvent.Info.NO_MATCHES));
            }

            @Override
            public void loadFailed(FriendlyException exception) {
                eventConsumer.accept(new AudioTrackEvent(null, AudioTrackEvent.Info.FAILED));
            }
        });
    }

    public AudioStatus getStatus() {
        return this.status;
    }

    public void play() {
        AudioTrack track = TRACKS_QUEUE.poll();
        if (track != null)
            audioPlayer.playTrack(track);
    }

    public void playNext() {

    }

    public void add(AudioTrack track) {
        TRACKS_QUEUE.add(track);
    }

    public void pause() {
        audioPlayer.setPaused(true);
    }

    public void resume() {

    }


    public void onPlayerPause(AudioPlayer player) {
        this.status = AudioStatus.PAUSED;
    }

    public void onPlayerResume(AudioPlayer player) {
        this.status = AudioStatus.PLAYING;
    }

    public void onTrackStart(AudioPlayer player, AudioTrack track) {
        this.status = AudioStatus.PLAYING;
    }

    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
        this.status = AudioStatus.STOPPED;
        playNext();
    }

    public void onTrackException(AudioPlayer player, AudioTrack track, FriendlyException exception) {

    }


}
