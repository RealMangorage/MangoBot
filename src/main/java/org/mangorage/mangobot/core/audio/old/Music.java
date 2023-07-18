/*
 * Copyright (c) 2023. MangoRage
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
 * OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE
 * OR OTHER DEALINGS IN THE SOFTWARE.
 */

package org.mangorage.mangobot.core.audio.old;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
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
