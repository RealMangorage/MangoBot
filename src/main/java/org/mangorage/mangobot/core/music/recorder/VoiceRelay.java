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

import net.dv8tion.jda.api.audio.AudioReceiveHandler;
import net.dv8tion.jda.api.audio.AudioSendHandler;
import net.dv8tion.jda.api.audio.CombinedAudio;
import net.dv8tion.jda.api.audio.SpeakingMode;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.managers.AudioManager;

import java.nio.ByteBuffer;
import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Queue;

// TODO: Figure out a better system!
public class VoiceRelay implements AudioReceiveHandler, AudioSendHandler {
    private static final HashMap<String, VoiceRelay> VOICE_RELAYS = new HashMap<>();

    public static VoiceRelay getInstance(String guildID) {
        return VOICE_RELAYS.computeIfAbsent(guildID, VoiceRelay::new);
    }

    private final Queue<byte[]> DATA = new ArrayDeque<>();
    private final String ID;

    private VoiceRelay(String id) {
        this.ID = id;
    }

    public void join(VoiceChannel channel) {
        Guild guild = channel.getGuild();
        AudioManager audioManager = guild.getAudioManager();

        audioManager.setReceivingHandler(this);
        audioManager.setSendingHandler(this);

        audioManager.setSelfDeafened(true);
        audioManager.setSelfMuted(false);
        audioManager.setAutoReconnect(true);
        audioManager.setSpeakingMode(SpeakingMode.SOUNDSHARE);
        audioManager.setConnectTimeout(30_000);

        audioManager.openAudioConnection(channel);
    }


    /**
     * @return
     */
    @Override
    public boolean canReceiveCombined() {
        return DATA.size() < 10;
    }

    /**
     * @param combinedAudio The combined audio data.
     */
    @Override
    public void handleCombinedAudio(CombinedAudio combinedAudio) {
        if (combinedAudio.getUsers().isEmpty())
            return;

        VOICE_RELAYS.forEach((key, value) -> {
            if (!key.equals(ID)) {
                value.DATA.add(combinedAudio.getAudioData(1));
            }
        });
    }

    /**
     * @return
     */
    @Override
    public boolean canProvide() {
        return !DATA.isEmpty();
    }

    /**
     * @return
     */
    @Override
    public ByteBuffer provide20MsAudio() {
        byte[] data = DATA.poll();
        return data == null ? null : ByteBuffer.wrap(data);
    }

    @Override
    public boolean isOpus() {
        return false;
    }
}
