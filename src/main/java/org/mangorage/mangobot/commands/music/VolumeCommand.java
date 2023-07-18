package org.mangorage.mangobot.commands.music;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import org.mangorage.mangobot.commands.AbstractCommand;
import org.mangorage.mangobot.commands.CommandResult;
import org.mangorage.mangobot.core.audio.MusicUtil;

public class VolumeCommand extends AbstractCommand {
    @Override
    public CommandResult execute(Message message, String[] args) {
        String VOLUME = args[0];
        MessageChannelUnion channel = message.getChannel();

        if (Integer.valueOf(VOLUME) != null) {
            int volume = Integer.valueOf(VOLUME);
            if (volume <= 30) {
                MusicUtil.setVolume(volume);
                channel.sendMessage("Volume set to " + volume).queue();
            } else {
                channel.sendMessage("Max Volume allowed is 30").queue();
            }
        }

        return CommandResult.PASS;
    }
}
