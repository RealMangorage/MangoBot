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
