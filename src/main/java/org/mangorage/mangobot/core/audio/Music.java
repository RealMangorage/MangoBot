package org.mangorage.mangobot.core.audio;

import com.sedmelluq.discord.lavaplayer.player.*;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class Music {
    public static final AudioPlayerManager manager = new DefaultAudioPlayerManager();
    public static final AudioPlayer audioPlayer = manager.createPlayer();
    public static final TrackScheduler trackScheduler = new TrackScheduler(audioPlayer);

    public static void init() {
        AudioSourceManagers.registerLocalSource(manager);
        AudioSourceManagers.registerRemoteSources(manager);
        audioPlayer.addListener(trackScheduler);
    }

    public static void pause() {
        audioPlayer.setPaused(true);
    }

    public static void resume() {
        audioPlayer.setPaused(false);
    }

    public static boolean playOrQueue(String URL, boolean queue, Consumer<AudioTrack> trackConsumer) {
        if (audioPlayer.isPaused()) {
            resume();
            return true;
        }

        AtomicBoolean loaded = new AtomicBoolean(false);

        manager.loadItem(URL, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack track) {
                trackConsumer.accept(track);
                if (queue) {
                    trackScheduler.queueTrack(track);
                } else {
                    trackScheduler.playTrack(track);
                }
                loaded.set(true);
            }

            @Override
            public void playlistLoaded(AudioPlaylist playlist) {

            }

            @Override
            public void noMatches() {
                trackConsumer.accept(null);
            }

            @Override
            public void loadFailed(FriendlyException exception) {
                trackConsumer.accept(null);
            }

        });

        return loaded.get();
    }
}
