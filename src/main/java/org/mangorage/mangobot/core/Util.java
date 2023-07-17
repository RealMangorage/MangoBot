package org.mangorage.mangobot.core;


import net.dv8tion.jda.api.entities.Message;
import org.mangorage.mangobot.core.commands.CommandManager;

import static org.mangorage.mangobot.core.Constants.COMMAND_PREFIX;

public class Util {
    public static boolean handleMessage(Message message) {
        String messageContent = message.getContentDisplay();
        String commandFull = messageContent.split(" ")[0];

        if (messageContent.startsWith(COMMAND_PREFIX)) {
            String command = commandFull.substring(1);
            if (CommandManager.getInstance().isValid(command)) {
                CommandManager.getInstance().handleCommand(command, message);
                return true;
            }
        }

        return false;
    }
}
