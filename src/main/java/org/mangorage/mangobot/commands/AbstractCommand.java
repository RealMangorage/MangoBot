package org.mangorage.mangobot.commands;

import net.dv8tion.jda.api.entities.Message;

public abstract class AbstractCommand {
    public abstract CommandResult execute(Message message, String[] args);

}
