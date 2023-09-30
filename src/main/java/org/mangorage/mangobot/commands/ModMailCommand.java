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
import net.dv8tion.jda.api.entities.channel.concrete.Category;
import org.mangorage.mangobot.core.Bot;
import org.mangorage.mangobot.core.commands.permissions.GlobalPermissions;
import org.mangorage.mangobot.core.modules.ModMailHandler;
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

            if (args.hasArg("close")) {
                ModMailHandler.close(message.getGuildChannel().getId());
                return CommandResult.PASS;
            }

            Guild guild = member.getGuild();
            String guildID = guild.getId();
            if (!PermissionRegistry.hasNeededPermission(member, GlobalPermissions.MOD_MAIL))
                return CommandResult.NO_PERMISSION;
            if (!args.hasArg("categoryID") || args.findArgOrDefault("categoryID", "null").equals("null"))
                return CommandResult.FAIL;

            String categoryID = args.findArg("categoryID");
            try {
                Category category = guild.getCategoryById(categoryID);
                if (category == null) return CommandResult.FAIL;
                ModMailHandler.configure(guildID, categoryID);
                Bot.DEFAULT_SETTINGS.apply(message.reply("ModMailCommand for this server has been configured")).queue();
            } catch (Exception e) {
                return CommandResult.FAIL;
            }
        } else if (args.hasArg("join")) {
            if (message.isFromGuild()) return CommandResult.NO_PERMISSION;
            String guildJoinID = args.findArgOrDefault("join", "null");
            if (guildJoinID.equals("null")) return CommandResult.FAIL;
            var result = ModMailHandler.join(message, message.getAuthor(), guildJoinID);
            switch (result) {
                case 1 -> messageSettings.apply(message.reply("GuildID is incorrect / guild doesn't exist")).queue();
                case 2 ->
                        messageSettings.apply(message.reply("Guild Exists. ModMail for this server hasnt been configured")).queue();
                case 3 ->
                        messageSettings.apply(message.reply("Cannot be in more then 1 ModMail ticket at a time !mail leave to leave current one")).queue();
            }
        } else if (args.hasArg("leave")) {
            if (message.isFromGuild()) return CommandResult.NO_PERMISSION;
            ModMailHandler.leave(message.getChannel().asPrivateChannel(), message.getAuthor());
        }

        return CommandResult.PASS;
    }

    @Override
    public boolean isGuildOnly() {
        return false;
    }
}
