package org.mangorage.mangobot.commands.music;

import net.dv8tion.jda.api.entities.Message;
import org.mangorage.mangobot.commands.AbstractCommand;
import org.mangorage.mangobot.commands.CommandResult;
import org.mangorage.mangobot.core.audio.MusicPlayer;

public class PauseCommand extends AbstractCommand {
    @Override
    public CommandResult execute(Message message, String[] args) {
        if (MusicPlayer.getInstance().isPlaying())
            MusicPlayer.getInstance().pause();
        return CommandResult.PASS;
    }
}
