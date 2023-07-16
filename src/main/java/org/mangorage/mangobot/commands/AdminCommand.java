package org.mangorage.mangobot.commands;

import net.dv8tion.jda.api.entities.Message;
import org.mangorage.mangobot.core.permissions.PermissionNode;

public class AdminCommand extends AbstractCommand {
    private final PermissionNode node;

    public AdminCommand(PermissionNode node) {
        this.node = node;
    }

    @Override
    public CommandResult execute(Message message, String[] args) {
        if (node.hasPermissions(message.getMember())) {
            message.getChannel().sendMessage("Sufficent permissions").queue();
        } else {
            message.getChannel().sendMessage("Insufficent permisisons").queue();
        }
        return CommandResult.PASS;
    }
}
