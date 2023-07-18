package org.mangorage.mangobot.commands.music;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import org.mangorage.mangobot.commands.AbstractCommand;
import org.mangorage.mangobot.commands.CommandResult;
import org.mangorage.mangobot.core.audio.Music;

public class PauseCommand extends AbstractCommand {
    @Override
    public CommandResult execute(Message message, String[] args) {
        if (!Music.audioPlayer.isPaused()) {
            MessageChannelUnion channel = message.getChannel();
            channel.sendMessage("Paused Music").queue();
            Music.pause();
        }
        return CommandResult.PASS;
    }
}
