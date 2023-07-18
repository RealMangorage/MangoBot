package org.mangorage.mangobot.commands.music;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import org.mangorage.mangobot.commands.AbstractCommand;
import org.mangorage.mangobot.commands.CommandResult;
import org.mangorage.mangobot.core.audio.*;
import org.mangorage.mangobot.core.audio.MusicUtil;

public class PlayCommand extends AbstractCommand {
    @Override
    public CommandResult execute(Message message, String[] args) {
        String URL = args[0];
        MessageChannelUnion channel = message.getChannel();
        MusicPlayer player = MusicPlayer.getInstance();

        if (URL.length() > 0) {
            if (!player.isPlaying()) {
                player.load(URL, e -> {
                    switch (e.getReason()) {
                        case SUCCESS -> {
                            MusicUtil.connectToAudioChannel(message.getMember());
                            player.add(e.getTrack());
                            player.play();
                            channel.sendMessage("Started playing: " + e.getTrack().getInfo().title).queue();
                        }
                        case FAILED -> {
                            channel.sendMessage("Failed").queue();
                        }
                        case NO_MATCHES -> {
                            channel.sendMessage("No matches was found!").queue();
                        }
                    }
                });
            } else
                channel.sendMessage("Already playing!").queue();
        } else {
            if (player.isPlaying()) {
                MusicPlayer.getInstance().resume();
                channel.sendMessage("Resumed playing: " + MusicPlayer.getInstance().getPlaying().getInfo().title);
            } else {
                channel.sendMessage("Nothing is currently playing.").queue();
            }
        }

        return CommandResult.PASS;
    }
}
