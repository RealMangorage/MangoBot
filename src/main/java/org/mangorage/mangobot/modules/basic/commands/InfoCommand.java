/*
 * Copyright (c) 2023. MangoRage
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
 * OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE
 * OR OTHER DEALINGS IN THE SOFTWARE.
 */

package org.mangorage.mangobot.modules.basic.commands;

import net.dv8tion.jda.api.entities.Message;
import org.jetbrains.annotations.NotNull;
import org.mangorage.mangobot.core.Bot;
import org.mangorage.mangobotapi.core.commands.Arguments;
import org.mangorage.mangobotapi.core.commands.CommandResult;
import org.mangorage.mangobotapi.core.commands.IBasicCommand;
import org.mangorage.mangobotapi.core.registry.CommandRegistry;

import java.util.List;

public class InfoCommand implements IBasicCommand {

    @NotNull
    @SuppressWarnings("unchecked")
    @Override
    public CommandResult execute(Message message, Arguments args) {
        var settings = Bot.DEFAULT_SETTINGS;
        String cmd = args.get(0);
        if (cmd == null) {
            settings.apply(message.reply(usage())).queue();
            return CommandResult.PASS;
        }

        var command = CommandRegistry.getCommand(cmd);
        if (command == null) {
            settings.apply(message.reply("Command not found!")).queue();
            return CommandResult.PASS;
        }

        StringBuilder result = new StringBuilder();

        List<String> aliases = command.commandAliases();
        var description = command.description();
        var usage = command.usage();
        var isGuildOnly = command.isGuildOnly();
        var ignoreCase = command.ignoreCase();
        List<String> allowedGuilds = command.allowedGuilds();
        List<String> allowedUsers = command.allowedUsers();

        if (!aliases.isEmpty()) {
            result.append("Aliases: ").append("\n");
            for (String alias : aliases) {
                result.append(" -> ").append(alias).append("\n");
            }
        }

        if (!description.isEmpty()) {
            result.append("Description: ").append("\n").append(" -> ").append(description).append("\n");
        }

        if (!usage.isEmpty()) {
            result.append("Usage: ").append("\n").append(" -> ").append(usage).append("\n");
        }

        if (!allowedGuilds.isEmpty()) {
            result.append("Allowed Guilds: ").append("\n");
            for (String guild : allowedGuilds) {
                result.append(" -> ").append(guild).append("\n");
            }
        }

        if (!allowedUsers.isEmpty()) {
            result.append("Allowed Users: ").append("\n");
            for (String user : allowedUsers) {
                result.append(" -> ").append(user).append("\n");
            }
        }

        result.append("Guild Only: ").append(" -> ").append(isGuildOnly).append("\n");
        result.append("Ignore Case: ").append(" -> ").append(ignoreCase).append("\n");

        settings.apply(message.reply(result.toString())).queue();

        return CommandResult.PASS;
    }

    /**
     * @return
     */
    @Override
    public String commandId() {
        return "info";
    }

    /**
     * @return
     */
    @Override
    public String usage() {
        return "!info <commandId>";
    }

    /**
     * @return
     */
    @Override
    public String description() {
        return "Gets info about a command";
    }
}
