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

package org.mangorage.mangobot.core.events;

import net.dv8tion.jda.api.entities.emoji.Emoji;
import org.mangorage.mangobot.core.Bot;
import org.mangorage.mangobotapi.core.eventbus.annotations.SubscribeEvent;
import org.mangorage.mangobotapi.core.events.discord.DMessageRecievedEvent;
import org.mangorage.mangobotapi.core.events.discord.DReactionEvent;

public class Listeners {
    @SubscribeEvent
    public static void onMessage(DMessageRecievedEvent e) {
        var event = e.get();
        var message = event.getMessage();
        var user = event.getAuthor();
        var userID = user.getId();
        if (Bot.getJDAInstance().getSelfUser().getId().equals(userID))
            message.addReaction(Emoji.fromCustom("trash", 1150898672897359983L, false)).queue();
    }

    @SubscribeEvent
    public static void onReaction(DReactionEvent e) {
        var event = e.get();
        var messageid = event.getMessageId();
        var emoji = event.getReaction().getEmoji();

        if (event.getUserId().equals(Bot.getJDAInstance().getSelfUser().getId()))
            return;

        if (emoji.getAsReactionCode().equals("trash:1150898672897359983")) {
            event.getGuildChannel().retrieveMessageById(messageid).queue(m -> {
                if (Bot.getJDAInstance().getSelfUser().getId().equals(m.getAuthor().getId()))
                    m.delete().queue();
            });
        }
    }
}
