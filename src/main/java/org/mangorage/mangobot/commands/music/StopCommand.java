package org.mangorage.mangobot.commands.music;

import net.dv8tion.jda.api.entities.Message;
import org.mangorage.mangobot.commands.AbstractCommand;
import org.mangorage.mangobot.commands.CommandResult;
import org.mangorage.mangobot.core.audio.Music;

public class StopCommand extends AbstractCommand {
    @Override
    public CommandResult execute(Message message, String[] args) {
        Music.audioPlayer.stopTrack();
        return CommandResult.PASS;
    }
}
