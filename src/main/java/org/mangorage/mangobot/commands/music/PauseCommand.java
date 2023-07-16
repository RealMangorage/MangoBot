package org.mangorage.mangobot.commands.music;

import net.dv8tion.jda.api.entities.Message;
import org.mangorage.mangobot.commands.AbstractCommand;
import org.mangorage.mangobot.commands.CommandResult;

public class PauseCommand extends AbstractCommand {
    @Override
    public CommandResult execute(Message message, String[] args) {
        return CommandResult.PASS;
    }
}
