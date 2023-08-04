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

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import org.mangorage.mangobot.core.commands.util.Arguments;
import org.mangorage.mangobot.core.commands.util.CommandResult;

import java.awt.*;
import java.sql.Time;
import java.time.Instant;

@Deprecated
public class ReplyEmbedCommand extends AbstractCommand {
    @Override
    public CommandResult execute(Message message, Arguments args) {
        message.replyEmbeds(
                new EmbedBuilder()
                        .setTitle("Title")
                        .setAuthor("MangoRage")
                        .setImage("https://preview.redd.it/rcpgibdaqkl61.png?width=1920&format=png&auto=webp&s=c5b0c957843c37c6a7b6cc734c3934465c955415")
                        .setFooter("Footer")
                        .setColor(Color.WHITE)
                        .setDescription("Description")
                        .setThumbnail("https://oyster.ignimgs.com/mediawiki/apis.ign.com/minecraft/b/b4/Herobrine.png")
                        .appendDescription("Additional Description")
                        .addField("test", "value", false)
                        .addField("test", "value inline", true)
                        .setTimestamp(Time.from(Instant.now()).toInstant())
                        .build()).queue();
        return CommandResult.PASS;
    }
}
