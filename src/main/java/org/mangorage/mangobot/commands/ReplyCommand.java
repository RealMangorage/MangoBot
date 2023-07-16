package org.mangorage.mangobot.commands;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
public class ReplyCommand extends AbstractCommand {
    private final String MESSAGE_RESPONSE;

    public ReplyCommand(String message) {
        this.MESSAGE_RESPONSE = message;
    }

    @Override
    public CommandResult execute(Message message, String... args) {
        message.reply(MESSAGE_RESPONSE).queue();
        return CommandResult.PASS;
    }
}
