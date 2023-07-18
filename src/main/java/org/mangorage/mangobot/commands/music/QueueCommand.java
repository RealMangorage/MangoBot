package org.mangorage.mangobot.commands.music;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import org.mangorage.mangobot.commands.AbstractCommand;
import org.mangorage.mangobot.commands.CommandResult;
import org.mangorage.mangobot.core.audio.old.Music;
import org.mangorage.mangobot.core.audio.MusicUtil;

public class QueueCommand extends AbstractCommand {
    @Override
    public CommandResult execute(Message message, String[] args) {
        MusicUtil.connectToAudioChannel(message.getMember());
        String URL = args[0];
        MessageChannelUnion channel = message.getChannel();

        if (URL != null) {
            Music.playOrQueue(URL, true, track -> {
                channel.sendMessage("Queued: " + track.getInfo().title).queue();
            });
        }
        return CommandResult.PASS;
    }
}
