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

package org.mangorage.mangobot.modules.developer;

import net.dv8tion.jda.api.entities.Message;
import org.jetbrains.annotations.NotNull;
import org.mangorage.mangobot.core.Bot;
import org.mangorage.mangobotapi.core.commands.Arguments;
import org.mangorage.mangobotapi.core.commands.CommandResult;
import org.mangorage.mangobotapi.core.commands.IBasicCommand;

public class KickBotCommand implements IBasicCommand {
    /**
     * @param message
     * @param args
     * @return
     */
    @NotNull
    @Override
    public CommandResult execute(Message message, Arguments args) {
        if (message.getAuthor().getId().equals("194596094200643584")) {
            var guildID = args.get(0);
            if (guildID.isBlank()) {
                message.reply("Usage: !kickBot <guildID>").queue();
                return CommandResult.PASS;
            }

            var guild = Bot.getJDAInstance().getGuildById(guildID);
            if (guild == null) {
                message.reply("Guild not found").queue();
                return CommandResult.PASS;
            }
            message.reply("Kicked myself from guild: " + guild.getName()).queue();
            guild.leave().queue();
        } else {
            return CommandResult.DEVELOPERS_ONLY;
        }
        return CommandResult.PASS;
    }

    /**
     * @return
     */
    @Override
    public String commandId() {
        return "kickBot";
    }
}
