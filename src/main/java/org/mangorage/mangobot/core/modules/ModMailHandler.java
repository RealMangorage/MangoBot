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

package org.mangorage.mangobot.core.modules;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.concrete.Category;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import org.jetbrains.annotations.Nullable;
import org.mangorage.mangobot.core.Bot;
import org.mangorage.mangobotapi.core.eventbus.annotations.SubscribeEvent;
import org.mangorage.mangobotapi.core.eventbus.impl.IEventBus;
import org.mangorage.mangobotapi.core.events.discord.DMessageRecievedEvent;

import java.util.HashMap;

public class ModMailHandler {
    private static final String SAVEDIR = "data/guilddata/modmail/%s";
    private static final HashMap<String, String> GUILD_DATA = new HashMap<>(); // GuildID -> CategoryID
    private static final HashMap<String, ModMailInstance> MOD_MAIL_INSTANCE = new HashMap<>(); // UserID -> Inst


    @SuppressWarnings("all")
    @Nullable
    public static ModMailInstance findByChannelID(String channelID) {
        if (MOD_MAIL_INSTANCE.size() == 0) return null;
        return MOD_MAIL_INSTANCE.values().stream().filter(inst -> inst.channelID.equals(channelID)).findAny().get();
    }


    public static void register(IEventBus bus) {
        bus.addListener(DMessageRecievedEvent.class, ModMailHandler::onMessage);
    }

    public static void configure(String guildID, String categoryID) {
        if (!GUILD_DATA.containsKey(guildID))
            GUILD_DATA.put(guildID, categoryID);
    }

    public static void join(User user, String guildID) {
        if (!MOD_MAIL_INSTANCE.containsKey(user.getId())) {
            Guild guild = Bot.getJDAInstance().getGuildById(guildID);
            String categoryID = GUILD_DATA.get(guildID);
            if (guild == null) return;
            Category category = guild.getCategoryById(categoryID);
            guild.createTextChannel(user.getEffectiveName(), category).queue(e -> {
                MOD_MAIL_INSTANCE.put(user.getId(), new ModMailInstance(guildID, e.getId(), user.getId()));
            });
        }
    }

    public static void close(String channelID) {
        var inst = findByChannelID(channelID);
        if (inst == null) return;
        MOD_MAIL_INSTANCE.remove(inst.userID());
        var channel = Bot.getJDAInstance().getTextChannelById(inst.channelID);
        if (channel == null) return;
        channel.delete().queue();
        inst.closeModMail();
    }

    @SubscribeEvent
    public static void onMessage(DMessageRecievedEvent event) {
        var dEvent = event.get();

        if (dEvent.isFromType(ChannelType.PRIVATE) && !dEvent.getAuthor().isBot()) {
            Message content = dEvent.getMessage();
            User user = dEvent.getAuthor();
            String userID = user.getId();


            if (!MOD_MAIL_INSTANCE.containsKey(userID))
                return;

            ModMailInstance instance = MOD_MAIL_INSTANCE.get(userID);
            instance.sendToServer(content, user);
        } else if (!dEvent.getAuthor().isBot() && dEvent.isFromGuild()) {
            Message content = dEvent.getMessage();
            User user = dEvent.getAuthor();
            MessageChannelUnion channel = dEvent.getChannel();
            var inst = findByChannelID(channel.getId());
            if (inst == null) return;
            inst.sendToModMail(content, user);
        }
    }


    public record ModMailInstance(String guildID, String channelID, String userID) {

        public void closeModMail() {
            // Tell User this ModMail has been closed...
            Bot.getJDAInstance().retrieveUserById(userID).queue(DM -> {
                if (DM == null) return;
                DM.openPrivateChannel().queue(pc -> {
                    pc.sendMessage("ModMail for guildID: %s has been closed".formatted(guildID)).queue();
                });
            });
        }

        public void sendToModMail(Message message, User user) {
            // user being the Moderator in this case
            Bot.getJDAInstance().retrieveUserById(userID).queue(DM -> {
                if (DM == null) return;
                DM.openPrivateChannel().queue(pc -> {
                    pc.sendMessage(message.getContentRaw()).queue();
                });
            });
        }

        @SuppressWarnings("unused")
        public void sendToServer(Message message, User user) {
            // User being the person sending messages to Server
            Guild guild = Bot.getJDAInstance().getGuildById(guildID);
            if (guild == null) return;
            TextChannel channel = guild.getTextChannelById(channelID);
            if (channel == null) return;
            channel.sendMessage(
                    message.getContentRaw()
            ).queue();
        }
    }
}
