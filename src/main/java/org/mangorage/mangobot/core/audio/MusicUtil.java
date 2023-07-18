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

package org.mangorage.mangobot.core.audio;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.managers.AudioManager;

public class MusicUtil {
    public static void connectToAudioChannel(Member member) {
        // Get an audio manager for this guild, this will be created upon first use for each guild
        AudioManager audioManager = member.getGuild().getAudioManager();


        // Set the sending handler to our echo system
        audioManager.setSendingHandler(MusicPlayer.getInstance());
        audioManager.setSelfDeafened(true);
        audioManager.setSelfMuted(false);

        MusicPlayer.getInstance().setVolume(5); // Default volume so nobody gets there ears torn out by sound.

        // Connect to the voice channel
        if (member.getVoiceState().inAudioChannel())
            audioManager.openAudioConnection(member.getVoiceState().getChannel());
    }

    public static void leaveVoiceChannel(Member member) {
        AudioManager audioManager = member.getGuild().getAudioManager();
        audioManager.closeAudioConnection();
    }
}
