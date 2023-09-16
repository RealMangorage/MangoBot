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

package org.mangorage.mangobot.commands;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import org.mangorage.mangobotapi.core.AbstractCommand;
import org.mangorage.mangobotapi.core.registry.PermissionRegistry;
import org.mangorage.mangobotapi.core.registry.commands.Arguments;
import org.mangorage.mangobotapi.core.registry.commands.CommandPrefix;
import org.mangorage.mangobotapi.core.registry.commands.CommandResult;

import static org.mangorage.mangobot.core.Bot.DEFAULT_SETTINGS;
import static org.mangorage.mangobot.core.commands.permissions.GlobalPermissions.PREFIX_ADMIN;

public class PrefixCommand extends AbstractCommand {
    @Override
    public CommandResult execute(Message message, Arguments args) {
        String prefix = args.getOrDefault(0, "");
        Member member = message.getMember();
        Guild guild = message.getGuild();

        if (!PermissionRegistry.hasNeededPermission(member, PREFIX_ADMIN))
            return CommandResult.of("You dont have permission to use this command! Lacking Permission Node '%s'".formatted(PREFIX_ADMIN.id()));

        if (prefix.equals(CommandPrefix.getPrefix(guild.getId()))) {
            DEFAULT_SETTINGS.apply(message.reply("Already have command prefix set to '%s'".formatted(prefix))).queue();
            return CommandResult.PASS;
        }

        if (prefix.length() > 0) {
            CommandPrefix.configure(guild.getId(), prefix);
            DEFAULT_SETTINGS.apply(message.reply("Changed command prefix to '%s'".formatted(prefix))).queue();
            return CommandResult.PASS;
        }

        return CommandResult.FAIL;
    }

    @Override
    public boolean isGuildOnly() {
        return true;
    }
}
