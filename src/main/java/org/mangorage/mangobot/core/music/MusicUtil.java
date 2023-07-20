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

package org.mangorage.mangobot.core.music;

import net.dv8tion.jda.api.audio.SpeakingMode;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.managers.AudioManager;
import org.mangorage.mangobot.core.music.recorder.VoiceChatRecorder;

public class MusicUtil {
    public static void connectToAudioChannel(VoiceChannel channel) {
        Guild guild = channel.getGuild();
        AudioManager audioManager = guild.getAudioManager();

        audioManager.setSendingHandler(MusicPlayer.getInstance(guild.getId()));
        audioManager.setReceivingHandler(new VoiceChatRecorder());
        audioManager.setSelfDeafened(true);
        audioManager.setSelfMuted(false);
        audioManager.setAutoReconnect(true);
        audioManager.setSpeakingMode(SpeakingMode.SOUNDSHARE);
        audioManager.setConnectTimeout(30_000);

        MusicPlayer.getInstance(guild.getId()).setVolume(5); // Default volume so nobody gets there ears torn out by sound.
        audioManager.openAudioConnection(channel);
    }

    public static void leaveVoiceChannel(Guild guild) {
        guild.getAudioManager().closeAudioConnection();
    }
}
