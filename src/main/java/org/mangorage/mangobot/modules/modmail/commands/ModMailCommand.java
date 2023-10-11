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

package org.mangorage.mangobot.modules.modmail.commands;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.Category;
import org.mangorage.mangobot.core.Bot;
import org.mangorage.mangobot.guilds.global.GlobalPermissions;
import org.mangorage.mangobot.modules.modmail.ModMailHandler;
import org.mangorage.mangobotapi.core.commands.AbstractCommand;
import org.mangorage.mangobotapi.core.commands.Arguments;
import org.mangorage.mangobotapi.core.commands.CommandResult;
import org.mangorage.mangobotapi.core.registry.PermissionRegistry;
import org.mangorage.mangobotapi.core.util.MessageSettings;

public class ModMailCommand extends AbstractCommand {
    @Override
    public CommandResult execute(Message message, Arguments args) {
        MessageSettings messageSettings = Bot.DEFAULT_SETTINGS;

        if (message.isFromGuild()) {
            Member member = message.getMember();
            if (member == null)
                return CommandResult.FAIL;

            Guild guild = member.getGuild();
            String guildID = guild.getId();

            if (args.hasArg("close")) {
                ModMailHandler.close(message, message.getGuildChannel().getId());
                return CommandResult.PASS;
            }

            if (!PermissionRegistry.hasNeededPermission(member, GlobalPermissions.MOD_MAIL))
                return CommandResult.NO_PERMISSION;
            if (!args.hasArg("categoryID") || args.findArgOrDefault("categoryID", "null").equals("null"))
                return CommandResult.FAIL;

            String categoryID = args.findArg("categoryID");
            try {
                Category category = guild.getCategoryById(categoryID);
                if (category == null) return CommandResult.FAIL;
                ModMailHandler.configure(guildID, categoryID, guild.getName());
                messageSettings.apply(message.reply("ModMailCommand for this server has been configured")).queue();
            } catch (Exception e) {
                return CommandResult.FAIL;
            }
        } else if (args.hasArg("join")) {
            if (message.isFromGuild()) return CommandResult.NO_PERMISSION;
            ModMailHandler.showChoices(message, message.getAuthor());
        } else if (args.hasArg("leave")) {
            if (message.isFromGuild()) return CommandResult.NO_PERMISSION;
            boolean left = ModMailHandler.leave(message);
            if (!left)
                messageSettings.apply(message.reply("You are not in a ModMail Ticket")).queue();
        }

        return CommandResult.PASS;
    }

    /**
     * @param command
     * @return
     */
    @Override
    public boolean isValidCommand(String command) {
        return command.equalsIgnoreCase("mail");
    }

    @Override
    public boolean isGuildOnly() {
        return false;
    }
}
