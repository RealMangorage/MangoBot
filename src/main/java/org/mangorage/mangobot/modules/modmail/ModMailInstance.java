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

package org.mangorage.mangobot.modules.modmail;

import com.google.gson.annotations.Expose;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.PrivateChannel;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import org.mangorage.mangobot.core.Bot;

import java.awt.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class ModMailInstance {

    @Expose(deserialize = false)
    private PrivateChannel PRIVATE_CHANNEL = null;

    @Expose
    private final String guildID;
    @Expose
    private final String channelID;
    @Expose
    private final String userID;
    @Expose
    private final String guildName;
    @Expose
    private final String guildIconUrl;
    @Expose
    private final AtomicBoolean active;


    public ModMailInstance(String guildID, String channelID, String userID, String guildName, String guildIconUrl, AtomicBoolean active) {
        this.guildID = guildID;
        this.channelID = channelID;
        this.userID = userID;
        this.guildName = guildName;
        this.guildIconUrl = guildIconUrl;
        this.active = active;
    }

    public String getGuildID() {
        return guildID;
    }

    public String getGuildName() {
        return guildName;
    }

    public AtomicBoolean getActive() {
        return active;
    }

    public String getChannelID() {
        return channelID;
    }

    public String getGuildIconUrl() {
        return guildIconUrl;
    }

    public String getUserID() {
        return userID;
    }

    public boolean isActive() {
        return active.get();
    }

    public void joined(TextChannel channel, User user) {
        EmbedBuilder builder = new EmbedBuilder();

        builder.setAuthor(user.getEffectiveName(), null, user.getEffectiveAvatarUrl());
        builder.setTitle("User has opened a ModMail Ticket");
        builder.setDescription("%s has opened a ModMail ticket".formatted(user.getEffectiveName()));
        builder.setTimestamp(channel.getTimeCreated());
        builder.setFooter("ID: %s".formatted(userID), user.getEffectiveAvatarUrl());
        builder.setColor(Color.GREEN);

        channel.sendMessageEmbeds(builder.build()).queue();
    }

    public void closeModMail(Message message) {
        // Tell User this ModMailCommand has been closed...
        // User being the person who sent the message to the DM
        User user = message.getAuthor();
        Bot.getJDAInstance().retrieveUserById(userID).queue(DM -> {
            if (DM == null) return;
            EmbedBuilder builder = new EmbedBuilder();

            builder.setAuthor(user.getEffectiveName(), null, user.getEffectiveAvatarUrl());
            builder.setTitle("ModMail Ticket Closed");
            builder.setDescription("%s has closed your ModMail ticket".formatted(guildName));
            builder.setTimestamp(message.getTimeCreated());

            builder.setFooter("ModMail | %s | %s".formatted(guildName, guildID), guildIconUrl);
            builder.setColor(Color.GREEN);


            if (PRIVATE_CHANNEL == null)
                DM.openPrivateChannel().queue(pc -> {
                    PRIVATE_CHANNEL = pc;
                    pc.sendMessageEmbeds(builder.build()).queue();
                });
            else
                PRIVATE_CHANNEL.sendMessageEmbeds(builder.build()).queue();
        });
    }

    public void rejoin(Message message, Guild guild) {
        active.set(true);
        TextChannel channel = guild.getTextChannelById(getChannelID());
        if (channel == null) return;
        User user = message.getAuthor();

        EmbedBuilder builder = new EmbedBuilder();

        builder.setAuthor(user.getEffectiveName(), null, user.getEffectiveAvatarUrl());
        builder.setTitle("User has rejoined the ModMail Ticket.");
        builder.setFooter("ID: %s".formatted(user.getId()));
        builder.setTimestamp(message.getTimeCreated());
        builder.setColor(Color.BLUE);

        channel.sendMessageEmbeds(builder.build()).queue();
    }

    public void leave(Message message, PrivateChannel channel, User user) {
        active.set(false);
        Guild guild = Bot.getJDAInstance().getGuildById(guildID);
        if (guild == null) return;
        TextChannel guildChannel = guild.getTextChannelById(getChannelID());
        if (guildChannel == null) return;


        EmbedBuilder builder = new EmbedBuilder();

        builder.setAuthor(user.getEffectiveName(), null, user.getEffectiveAvatarUrl());
        builder.setTitle("User has left the ModMail Ticket.");
        builder.setDescription("Do !mail close to fully close. User can always rejoin.");
        builder.setFooter("ID: %s".formatted(user.getId()));
        builder.setTimestamp(message.getTimeCreated());
        builder.setColor(Color.BLUE);

        var messageData = guildChannel.sendMessageEmbeds(
                builder.build()
        );

        messageData.queue();

        channel.sendMessage("You have left %s's ModMail chat".formatted(guildName)).queue();
    }

    public void openModMail(PrivateChannel channel, Guild guild, User user, String joinMessage) {
        // Send a message to the userID telling them the join message.
        // join Message should have one %s

        // TODO: Make it into embed
        PRIVATE_CHANNEL = channel; // Cache the PrivateChannel
        channel.sendMessage(joinMessage.formatted(guild.getName())).queue();
    }

    public void sendToModMail(Message message, User user) { // Sending from Guild Channel -> Private DM
        // user being the Moderator in this case
        Bot.getJDAInstance().retrieveUserById(userID).queue(DM -> {
            if (DM == null) return;
            EmbedBuilder builder = new EmbedBuilder();

            builder.setAuthor(user.getEffectiveName(), null, user.getEffectiveAvatarUrl());
            builder.setDescription(message.getContentRaw());
            builder.setFooter("ModMail | %s | %s".formatted(guildName, guildID), guildIconUrl);
            builder.setTimestamp(message.getTimeCreated());
            builder.setColor(Color.GREEN);

            // Figure out a better way of doing this. Caching?

            if (PRIVATE_CHANNEL == null)
                DM.openPrivateChannel().queue(pc -> {
                    PRIVATE_CHANNEL = pc;
                    pc.sendMessageEmbeds(builder.build()).queue(after -> {
                        message.addReaction(Emoji.fromUnicode("U+2705")).queue();
                    });
                });
            else
                PRIVATE_CHANNEL.sendMessageEmbeds(builder.build()).queue(after -> {
                    message.addReaction(Emoji.fromUnicode("U+2705")).queue();
                });

        });
    }

    public void sendToServer(Message message, User user) { // Sending from Private DM -> Guild Channel
        // User being the person sending messages to Server
        Guild guild = Bot.getJDAInstance().getGuildById(guildID);
        if (guild == null) return;
        TextChannel channel = guild.getTextChannelById(channelID);
        if (channel == null) return;

        EmbedBuilder builder = new EmbedBuilder();

        builder.setAuthor(user.getEffectiveName(), null, user.getEffectiveAvatarUrl());
        builder.setDescription(message.getContentRaw());
        builder.setFooter("ID: %s".formatted(user.getId()));
        builder.setTimestamp(message.getTimeCreated());
        builder.setColor(Color.BLUE);

        var messageData = channel.sendMessageEmbeds(
                builder.build()
        );

        messageData.queue(after -> {
            message.addReaction(Emoji.fromUnicode("U+2705")).queue();
        });
    }

    public void save() {
        ModMailHandler.MODMAIl_INSTANCE_HANDLER.save(this, getUserID());
    }

    // Keep for now!
    public void delete() {
        ModMailHandler.MODMAIl_INSTANCE_HANDLER.delete(getUserID());
    }
}
