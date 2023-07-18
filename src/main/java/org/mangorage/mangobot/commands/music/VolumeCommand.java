package org.mangorage.mangobot.commands.music;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import org.mangorage.mangobot.commands.AbstractCommand;
import org.mangorage.mangobot.commands.CommandResult;
import org.mangorage.mangobot.core.audio.MusicPlayer;

public class VolumeCommand extends AbstractCommand {
    @Override
    public CommandResult execute(Message message, String[] args) {
        String VOLUME = args[0];
        MessageChannelUnion channel = message.getChannel();

        CommandResult result = CommandResult.FAIL;

        try {
            int volume = Integer.valueOf(VOLUME);
            if (volume <= 30) {
                MusicPlayer.getInstance().setVolume(volume);
                channel.sendMessage("Volume set to " + volume).queue();
            } else
                channel.sendMessage("Max Volume allowed is 30").queue();

            result = CommandResult.PASS;
        } catch (NumberFormatException e) {
            channel.sendMessage("Invalid arguments.").queue();
        }

        return result;
    }
}
