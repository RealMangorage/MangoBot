package org.mangorage.mangobot.commands.music;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import org.mangorage.mangobot.commands.AbstractCommand;
import org.mangorage.mangobot.commands.CommandResult;
import org.mangorage.mangobot.core.audio.*;

public class PlayCommand extends AbstractCommand {
    @Override
    public CommandResult execute(Message message, String[] args) {

        MusicUtil.connectToAudioChannel(message.getMember());
        String URL = args[0];
        MessageChannelUnion channel = message.getChannel();

        if (URL != null) {
            MusicPlayer player = MusicPlayer.getInstance();
            player.load(URL, e -> {
                switch (e.getReason()) {
                    case SUCCESS -> {
                        if (!player.isPlaying()) {
                            player.add(e.getTrack());
                            player.play();
                            channel.sendMessage("Started playing: " + e.getTrack().getInfo().title).queue();
                        } else {
                            channel.sendMessage("Already playing a Song!").queue();
                        }
                    }
                    case FAILED -> {
                        channel.sendMessage("Failed").queue();
                    }
                    case NO_MATCHES -> {
                        channel.sendMessage("No matches was found!").queue();
                    }
                }
            });
        }

        return CommandResult.PASS;
    }
}
