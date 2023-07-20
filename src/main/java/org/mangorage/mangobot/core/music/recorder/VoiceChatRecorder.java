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

package org.mangorage.mangobot.core.music.recorder;

import com.sedmelluq.discord.lavaplayer.container.ogg.opus.OggOpusCodecHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.track.playback.MutableAudioFrame;
import net.dv8tion.jda.api.audio.AudioReceiveHandler;
import net.dv8tion.jda.api.audio.CombinedAudio;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class VoiceChatRecorder implements AudioReceiveHandler {
    private final List<byte[]> RECORDED_AUDIO = new ArrayList<>();
    private final DefaultAudioPlayerManager manager = new DefaultAudioPlayerManager();
    private final AudioPlayer player = new DefaultAudioPlayer(manager);
    private final MutableAudioFrame frame;
    private final ByteBuffer buffer;


    public VoiceChatRecorder() {
        this.buffer = ByteBuffer.allocate(1024);
        this.frame = new MutableAudioFrame();
        this.frame.setBuffer(buffer);
        AudioSourceManagers.registerLocalSource(manager);
    }

    @Override
    public boolean canReceiveCombined() {
        return true;
    }

    @Override
    public void handleCombinedAudio(CombinedAudio combinedAudio) {
        byte[] data = combinedAudio.getAudioData(1);
        RECORDED_AUDIO.add(combinedAudio.getAudioData(1.0F));
        buffer.put(data);
        player.provide(frame);

        OggOpusCodecHandler
    }

    private static void playAudioUsingByteArray(byte[] byteArr, AudioFormat format) {
        try (Clip clip = AudioSystem.getClip()) {
            clip.open(format, byteArr, 0, byteArr.length);
            clip.start();
            clip.drain();
            Thread.sleep(clip.getMicrosecondLength());
        } catch (LineUnavailableException | InterruptedException e) {
            e.printStackTrace();
        }
    }

}
