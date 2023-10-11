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

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import org.mangorage.mangobotapi.core.commands.Arguments;
import org.mangorage.mangobotapi.core.commands.CommandResult;
import org.mangorage.mangobotapi.core.commands.ICommand;

import java.awt.*;

public class PingCommand implements ICommand {
    @Override
    public CommandResult execute(Message message, Arguments args) {
        message.getChannel().sendMessageEmbeds(
                new EmbedBuilder()
                        .setTitle("Please disable pings when replying to others")
                        .setImage("https://images-ext-2.discordapp.net/external/nUYuix4co3xyLw0ZFtBh5r2uxogGjmgr-4OTPW4cl8I/https/i.imgur.com/7YCb3AN.png")
                        .setColor(Color.WHITE)
                        .setDescription("""
                                So you don't accidentally break a rule or annoy others, make sure to turn off pings when replying to others. Discord currently turns this on every time you start a reply, so please be vigilant. Thank you.
                                                                
                                Go vote on [Discord Feedback](https://support.discord.com/hc/en-us/community/posts/360052518273-Replies-remember-setting) so they make changes.
                                """)
                        .build()).queue();

        return CommandResult.PASS;
    }


    @Override
    public String commandId() {
        return "ping";
    }
}
