package org.mangorage.mangobot.core.audio;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.managers.AudioManager;
import org.mangorage.mangobot.commands.music.AudioPlayerSendHandler;

public class MusicUtil {
    public static void connectToAudioChannel(Member member) {
        // Get an audio manager for this guild, this will be created upon first use for each guild
        AudioManager audioManager = member.getGuild().getAudioManager();
        // Create our Send/Receive handler for the audio connection
        AudioPlayerSendHandler handler = AudioPlayerSendHandler.INSTANCE;

        // Set the sending handler to our echo system
        audioManager.setSendingHandler(handler);
        // Connect to the voice channel
        if (member.getVoiceState().inAudioChannel())
            audioManager.openAudioConnection(member.getVoiceState().getChannel());
    }
}
