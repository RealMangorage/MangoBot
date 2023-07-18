package org.mangorage.mangobot.commands.music;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import org.mangorage.mangobot.commands.AbstractCommand;
import org.mangorage.mangobot.commands.CommandResult;
import org.mangorage.mangobot.core.audio.MusicPlayer;
import org.mangorage.mangobot.core.audio.MusicUtil;

public class StopCommand extends AbstractCommand {
    @Override
    public CommandResult execute(Message message, String[] args) {
        MessageChannelUnion channel = message.getChannel();
        if (MusicPlayer.getInstance().isPlaying()) {
            MusicPlayer.getInstance().stop();
            channel.sendMessage("Stopped track!");
        }

        MusicUtil.leaveVoiceChannel(message.getMember());

        return CommandResult.PASS;
    }
}
