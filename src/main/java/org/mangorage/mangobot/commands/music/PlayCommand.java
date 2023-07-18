package org.mangorage.mangobot.commands.music;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import org.mangorage.mangobot.commands.AbstractCommand;
import org.mangorage.mangobot.commands.CommandResult;
import org.mangorage.mangobot.core.audio.Music;
import org.mangorage.mangobot.core.audio.MusicUtil;
import org.mangorage.mangobot.core.audio.TrackScheduler;

public class PlayCommand extends AbstractCommand {
    @Override
    public CommandResult execute(Message message, String[] args) {

        MusicUtil.connectToAudioChannel(message.getMember());
        String URL = args[0];
        MessageChannelUnion channel = message.getChannel();

        if (URL != null) {
            if (Music.audioPlayer.isPaused()) {
                Music.resume();
                channel.sendMessage("Resumed music").queue();
            } else if (Music.trackScheduler.getStatus() == TrackScheduler.Status.PLAYING) {
                channel.sendMessage("Already Playing!").queue();
            } else {
                boolean success = Music.playOrQueue(URL, false, track -> {
                    if (track != null) {
                        channel.sendMessage("Playing: " + track.getInfo().title).queue();
                    } else {
                        channel.sendMessage("Failed to play Song!").queue();
                    }
                });

            }
        }

        return CommandResult.PASS;
    }
}
