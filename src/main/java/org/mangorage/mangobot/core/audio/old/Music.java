package org.mangorage.mangobot.core.audio.old;

import com.sedmelluq.discord.lavaplayer.player.*;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import java.util.function.Consumer;

@Deprecated
public class Music {
    public static final AudioPlayerManager manager = new DefaultAudioPlayerManager();
    public static final AudioPlayer audioPlayer = manager.createPlayer();
    public static final TrackScheduler trackScheduler = new TrackScheduler(audioPlayer);

    public static void init() {

    }

    public static void pause() {
        audioPlayer.setPaused(true);
    }

    public static void resume() {
        audioPlayer.setPaused(false);
    }

    public static void playOrQueue(String URL, boolean queue, Consumer<AudioTrack> trackConsumer) {
        if (audioPlayer.isPaused()) {
            resume();
        }

        manager.loadItem(URL, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack track) {
                trackConsumer.accept(track);

                if (track != null) {
                    if (queue) {
                        trackScheduler.queueTrack(track);
                    } else {
                        trackScheduler.playTrack(track);
                    }
                }
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
    }
}
