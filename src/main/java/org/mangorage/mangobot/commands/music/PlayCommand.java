package org.mangorage.mangobot.commands.music;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import org.mangorage.mangobot.commands.AbstractCommand;
import org.mangorage.mangobot.commands.CommandResult;
import org.mangorage.mangobot.core.audio.Music;
import org.mangorage.mangobot.core.audio.MusicUtil;

public class PlayCommand extends AbstractCommand {
    @Override
    public CommandResult execute(Message message, String[] args) {

        MusicUtil.connectToAudioChannel(message.getMember());
        String URL = args[0];
        MessageChannelUnion channel = message.getChannel();

        switch (Music.trackScheduler.getStatus()) {
            case STOPPED -> {
                Music.playOrQueue(URL, track -> {
                    channel.sendMessage("Playing: " + track.getInfo().title).queue();
                });
            }
            default -> {
                channel.sendMessage("Already playing!").queue();
            }
        }

        return CommandResult.PASS;
    }
}
