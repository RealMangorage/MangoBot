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

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.concrete.Category;
import net.dv8tion.jda.api.entities.channel.concrete.PrivateChannel;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.interactions.components.selections.SelectOption;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;
import org.mangorage.mangobot.core.Bot;
import org.mangorage.mangobot.core.Util;
import org.mangorage.mangobotapi.core.eventbus.annotations.SubscribeEvent;
import org.mangorage.mangobotapi.core.eventbus.impl.IEventBus;
import org.mangorage.mangobotapi.core.events.LoadEvent;
import org.mangorage.mangobotapi.core.events.discord.DMessageRecievedEvent;
import org.mangorage.mangobotapi.core.events.discord.DMessageUpdateEvent;
import org.mangorage.mangobotapi.core.events.discord.DStringSelectInteractionEvent;
import org.mangorage.mangobotapi.core.util.BotUtil;

import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class ModMailHandler {
    private static final String SAVEDIR_GUILDS = "data/guilddata/modmail/guilds/%s/";
    private static final String SAVEDIR_GUILDS_DIR = "data/guilddata/modmail/guilds";
    private static final String SAVEDIR_USERS = "data/guilddata/modmail/users/%s/";
    private static final String SAVEDIR_USERS_DIR = "data/guilddata/modmail/users";

    private static final HashMap<String, Settings> GUILD_SETTINGS = new HashMap<>(); // GuildID -> CategoryID
    private static final HashMap<String, ModMailInstance> MOD_MAIL_INSTANCE = new HashMap<>(); // UserID -> Inst
    private static final HashMap<String, ModMailInstance> MOD_MAIL_INSTANCE_CHANNEL = new HashMap<>(); // ChannelID -> Inst
    private static final HashSet<ModMailInstance> MOD_MAIL_INSTANCE_ID = new HashSet<>();

    private static final HashMap<String, List<String>> CHOICES = new HashMap<>(); // GuildID's



    public static ModMailInstance findByGuildAndUserID(String guildID, String userID) {
        if (MOD_MAIL_INSTANCE_ID.isEmpty()) return null;
        var result = MOD_MAIL_INSTANCE_ID.stream().filter(inst -> inst.guildID().equals(guildID) && inst.userID().equals(userID)).findAny();
        return result.orElse(null);
    }


    public static void register(IEventBus bus) {
        bus.addListener(LoadEvent.class, ModMailHandler::onLoad);
        bus.addListener(DMessageRecievedEvent.class, ModMailHandler::onMessage);
        bus.addListener(DMessageUpdateEvent.class, ModMailHandler::onMessageEdit);
        bus.addListener(DStringSelectInteractionEvent.class, ModMailHandler::onSelectMenu);
    }

    public static void configure(String guildID, String categoryID, String guildName) {
        if (!GUILD_SETTINGS.containsKey(guildID)) {
            var settings = new Settings(guildID, categoryID, guildName);
            GUILD_SETTINGS.put(guildID, settings);
            settings.save();
        }
    }

    public static void showChoices(Message message, User user) {
        List<SelectOption> OPTIONS = new ArrayList<>();
        GUILD_SETTINGS.forEach((key, setting) -> {
            OPTIONS.add(SelectOption.of(setting.getGuildName(), setting.getGuildID()));
        });

        StringSelectMenu.Builder menu = StringSelectMenu.create("mangobot:modmail");
        menu.addOptions(OPTIONS);

        message.reply("Select a ModMail to join").setActionRow(menu.build()).queue();
    }

    @SubscribeEvent
    public static void onSelectMenu(DStringSelectInteractionEvent event) {
        var dEvent = event.get();
        if (!dEvent.isFromGuild()) {
            var interaction = dEvent.getInteraction();
            if (!interaction.getComponentId().equals("mangobot:modmail")) return;

            var guildID = interaction.getValues().get(0);
            var result = join(interaction.getMessage(), interaction.getUser(), guildID);

            switch (result) {
                case 0 -> interaction.reply("Opening Ticket Soon.").setEphemeral(true).queue();
                case 1 -> interaction.reply("GuildID is incorrect / guild doesn't exist").setEphemeral(true).queue();
                case 2 ->
                        interaction.reply("Guild Exists. ModMail for this server hasnt been configured").setEphemeral(true).queue();
                case 3 ->
                        interaction.reply("Cannot be in more then 1 ModMail ticket at a time !mail leave to leave current one").setEphemeral(true).queue();
            }
        }
    }

    public static int join(Message message, User user, String guildID) {
        if (!MOD_MAIL_INSTANCE.containsKey(user.getId())) {
            Guild guild = Bot.getJDAInstance().getGuildById(guildID);
            Settings settings = GUILD_SETTINGS.get(guildID);

            // If guild or settings are null, don't bother any further
            if (guild == null) return 1; // guild doesnt exist
            if (settings == null) return 2; // Guild exists, but isnt configured

            String categoryID = settings.getCategoryID();
            Category category = guild.getCategoryById(categoryID);

            // If we cant find category, don't bother.
            if (category == null) return 2; // Guild isnt configured. Same message
            Bot.DEFAULT_SETTINGS.apply(message.reply("Opening ticket for %s".formatted(guild.getName()))).queue();
            // Find a channel that this user is in already if any and then Attempt to create a channel if none exist
            var current = findByGuildAndUserID(guildID, user.getId());
            if (current != null) {
                MOD_MAIL_INSTANCE.put(user.getId(), current);
                current.rejoin(user, guild);
                current.openModMail(message.getChannel().asPrivateChannel(), guild, user, settings.getJoinMessage());
            } else {
                guild.createTextChannel(user.getEffectiveName(), category).queue(e -> {
                    var inst = new ModMailInstance(guildID, e.getId(), user.getId(), guild.getName(), guild.getIconUrl(), new AtomicBoolean(true));

                    MOD_MAIL_INSTANCE_ID.add(inst);
                    MOD_MAIL_INSTANCE_CHANNEL.put(inst.channelID(), inst);
                    MOD_MAIL_INSTANCE.put(user.getId(), inst);

                    inst.openModMail(message.getChannel().asPrivateChannel(), guild, user, settings.getJoinMessage());
                    inst.save();
                });
            }
            return 0; // All is fine...
        } else {
            return 3;
        }
    }

    public static void close(String channelID) {
        var inst = MOD_MAIL_INSTANCE_CHANNEL.get(channelID);
        if (inst == null) return;

        MOD_MAIL_INSTANCE.remove(inst.userID());
        MOD_MAIL_INSTANCE_CHANNEL.remove(inst.channelID());

        var channel = Bot.getJDAInstance().getTextChannelById(inst.channelID);
        if (channel == null) return;

        channel.delete().queue();
        inst.closeModMail();
        inst.delete();
    }

    public static void leave(PrivateChannel channel, User user) {
        var inst = MOD_MAIL_INSTANCE.get(user.getId());
        if (inst == null) return;
        inst.leave(channel, user);
        inst.save();
        MOD_MAIL_INSTANCE.remove(user.getId());
    }

    public static void onLoad(LoadEvent event) {
        List<File> DATA_DIR = BotUtil.getFilesInDir(SAVEDIR_GUILDS_DIR);
        DATA_DIR.forEach(file -> {
            if (file.isDirectory()) {
                File settings = new File("%s/settings.json".formatted(file.getAbsolutePath()));
                if (settings.exists()) {
                    // Load
                    Settings settingsObj = Util.loadJsonToObject(settings.getAbsolutePath(), Settings.class);
                    GUILD_SETTINGS.put(settingsObj.guildID, settingsObj);
                    if (settingsObj.guildName == null) {
                        var guild = Bot.getJDAInstance().getGuildById(settingsObj.guildID);
                        if (guild == null) return;
                        settingsObj.guildName = guild.getName();
                    }
                }
            }
        });

        List<File> USERS_DIR = BotUtil.getFilesInDir(SAVEDIR_USERS_DIR);
        USERS_DIR.forEach(file -> {
            if (file.isDirectory()) {
                File settings = new File("%s/settings.json".formatted(file.getAbsolutePath()));
                if (settings.exists()) {
                    // Load
                    ModMailInstance inst = Util.loadJsonToObject(settings.getAbsolutePath(), ModMailInstance.class);

                    MOD_MAIL_INSTANCE_ID.add(inst);
                    MOD_MAIL_INSTANCE_CHANNEL.put(inst.channelID(), inst);
                    if (inst.active().get())
                        MOD_MAIL_INSTANCE.put(inst.userID(), inst);
                }
            }
        });
    }

    @SubscribeEvent
    public static void onMessage(DMessageRecievedEvent event) {
        if (event.isCommand()) return;
        var dEvent = event.get();

        if (dEvent.isFromType(ChannelType.PRIVATE) && !dEvent.getAuthor().isBot()) {
            Message content = dEvent.getMessage();
            User user = dEvent.getAuthor();
            String userID = user.getId();

            if (!MOD_MAIL_INSTANCE.containsKey(userID)) return;

            ModMailInstance instance = MOD_MAIL_INSTANCE.get(userID);
            instance.sendToServer(content, user);
        } else if (!dEvent.getAuthor().isBot() && dEvent.isFromGuild()) {
            Message content = dEvent.getMessage();
            User user = dEvent.getAuthor();
            MessageChannelUnion channel = dEvent.getChannel();
            var inst = MOD_MAIL_INSTANCE_CHANNEL.get(channel.getId());
            if (inst == null || !inst.active().get()) return;
            inst.sendToModMail(content, user);
        }
    }

    public static void onMessageEdit(DMessageUpdateEvent event) {
    }

    public static class Settings {
        private final String guildID;
        private String categoryID = "none";
        private final String transcriptChannelID = "none";
        private final String joinMessage = """
                The %s ModMail Team will be with you shortly!
                """;
        private String guildName;

        public Settings(String guildID, String categoryID, String guildName) {
            this.guildID = guildID;
            this.categoryID = categoryID;
            this.guildName = guildName;
        }

        public String getGuildID() {
            return guildID;
        }

        public String getCategoryID() {
            return categoryID;
        }

        public String getJoinMessage() {
            return joinMessage;
        }

        public String getGuildName() {
            return guildName;
        }

        public void save() {
            Util.saveObjectToFile(this, SAVEDIR_GUILDS.formatted(guildID), "settings.json");
        }
    }

    public record ModMailInstance(String guildID, String channelID, String userID, String guildName,
                                  String guildIconUrl, AtomicBoolean active) {

        public void closeModMail() {
            // Tell User this ModMailCommand has been closed...
            Bot.getJDAInstance().retrieveUserById(userID).queue(DM -> {
                if (DM == null) return;
                DM.openPrivateChannel().queue(pc -> {
                    pc.sendMessage("Your ModMail ticket for %s has been closed".formatted(guildName)).queue();
                });
            });
        }

        public void rejoin(User user, Guild guild) {
            active.set(true);
            TextChannel channel = guild.getTextChannelById(channelID());
            if (channel == null) return;
            EmbedBuilder builder = new EmbedBuilder();

            builder.setAuthor(user.getEffectiveName(), null, user.getEffectiveAvatarUrl());
            builder.setTitle("User has rejoined the ModMail Ticket.");
            builder.setFooter("UserID: %s".formatted(user.getId()));
            builder.setColor(Color.BLUE);

            channel.sendMessageEmbeds(builder.build()).queue();
        }

        public void leave(PrivateChannel channel, User user) {
            active.set(false);
            Guild guild = Bot.getJDAInstance().getGuildById(guildID);
            if (guild == null) return;
            TextChannel guildChannel = guild.getTextChannelById(channelID());
            if (guildChannel == null) return;


            EmbedBuilder builder = new EmbedBuilder();

            builder.setAuthor(user.getEffectiveName(), null, user.getEffectiveAvatarUrl());
            builder.setTitle("User has left the ModMail Ticket.");
            builder.setDescription("Do !mail close to fully close. User can always rejoin.");
            builder.setFooter("UserID: %s".formatted(user.getId()));
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
            channel.sendMessage(joinMessage.formatted(guild.getName())).queue();
        }

        public void sendToModMail(Message message, User user) { // Sending from Guild Channel -> Private DM
            // user being the Moderator in this case
            Bot.getJDAInstance().retrieveUserById(userID).queue(DM -> {
                if (DM == null) return;
                EmbedBuilder builder = new EmbedBuilder();

                builder.setAuthor(user.getEffectiveName(), null, user.getEffectiveAvatarUrl());
                builder.setDescription(message.getContentRaw());
                builder.setFooter("%s | %s".formatted(guildName, guildID), guildIconUrl);
                builder.setTimestamp(message.getTimeCreated());
                builder.setColor(Color.GREEN);

                DM.openPrivateChannel().queue(pc -> {
                    pc.sendMessageEmbeds(builder.build()).queue(after -> {
                        message.addReaction(Emoji.fromUnicode("U+2705")).queue();
                    });
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
            builder.setFooter("UserID: %s".formatted(user.getId()));
            builder.setColor(Color.BLUE);

            var messageData = channel.sendMessageEmbeds(
                    builder.build()
            );

            messageData.queue(after -> {
                message.addReaction(Emoji.fromUnicode("U+2705")).queue();
            });
        }

        public void save() {
            Util.saveObjectToFile(this, SAVEDIR_USERS.formatted(userID), "settings.json");
        }

        public void delete() {
            Util.deleteFile(SAVEDIR_USERS.formatted(userID), "settings.json");
        }
    }
}
