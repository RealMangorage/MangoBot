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
import net.dv8tion.jda.api.audio.OpusPacket;
import net.dv8tion.jda.api.audio.SpeakingMode;
import net.dv8tion.jda.api.audio.UserAudio;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.managers.AudioManager;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.HashMap;

public class VoiceRelay implements AudioReceiveHandler, AudioSendHandler {
    private static final HashMap<String, VoiceRelay> VOICE_RELAYS = new HashMap<>();

    public static VoiceRelay getInstance(String guildID) {
        return VOICE_RELAYS.computeIfAbsent(guildID, VoiceRelay::new);
    }

    private final ByteBuffer buffer = ByteBuffer.allocate(10_000_000);
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
    public boolean canReceiveUser() {
        return true;
    }

    /**
     * @return
     */
    @Override
    public boolean canReceiveCombined() {
        return false;
    }


    /**
     * @param packet The {@link net.dv8tion.jda.api.audio.OpusPacket}
     */
    @Override
    public void handleEncodedAudio(OpusPacket packet) {
        VOICE_RELAYS.forEach((key, value) -> {
            if (!key.equals(ID)) {
                value.buffer.put(packet.getAudioData(1));
            }
        });
    }

    /**
     * @param userAudio The user audio data
     */
    @Override
    public void handleUserAudio(UserAudio userAudio) {
        VOICE_RELAYS.forEach((key, value) -> {
            if (!key.equals(ID)) {
                value.buffer.put(userAudio.getAudioData(1));
            }
        });
    }

    /**
     * @return
     */
    @Override
    public boolean canProvide() {
        return buffer.position() > 0;
    }

    /**
     * @return
     */
    @Override
    public ByteBuffer provide20MsAudio() {
        byte[] bytes = buffer.array();
        short[] shorts = new short[bytes.length / 2];

        ByteBuffer.wrap(bytes).order(ByteOrder.BIG_ENDIAN).asShortBuffer().get(shorts);

        buffer.clear();

        return ByteBuffer.wrap(OpusPacket.getAudioData(shorts, 1));
    }

    /**
     * @return
     */
    @Override
    public boolean canReceiveEncoded() {
        return true;
    }

    /**
     * @return
     */
    @Override
    public boolean isOpus() {
        return false;
    }
}
