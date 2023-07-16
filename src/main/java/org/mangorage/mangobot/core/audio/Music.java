package org.mangorage.mangobot.core.audio;

import com.sedmelluq.discord.lavaplayer.player.*;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import net.dv8tion.jda.api.managers.AudioManager;
import org.mangorage.mangobot.commands.music.AudioPlayerSendHandler;

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

    public static void playOrQueue(String URL, Consumer<AudioTrack> trackConsumer) {
        manager.loadItem(URL, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack track) {
                trackConsumer.accept(track);

                switch (trackScheduler.getStatus()) {
                    case STOPPED -> {
                        trackScheduler.playTrack(track);
                    }
                    default -> {
                        trackScheduler.queueTrack(track);
                    }
                }
            }

            @Override
            public void playlistLoaded(AudioPlaylist playlist) {

            }

            @Override
            public void noMatches() {

            }

            @Override
            public void loadFailed(FriendlyException exception) {

            }
        });
    }
}
