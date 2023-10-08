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

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.concrete.Category;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.interactions.components.selections.SelectOption;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;
import org.mangorage.mangobot.core.Bot;
import org.mangorage.mangobotapi.core.data.DataHandler;
import org.mangorage.mangobotapi.core.events.LoadEvent;
import org.mangorage.mangobotapi.core.events.discord.DMessageRecievedEvent;
import org.mangorage.mangobotapi.core.events.discord.DMessageUpdateEvent;
import org.mangorage.mangobotapi.core.events.discord.DStringSelectInteractionEvent;
import org.mangorage.mboteventbus.annotations.SubscribeEvent;
import org.mangorage.mboteventbus.impl.IEventBus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class ModMailHandler {
    protected static final DataHandler<ModMailSettings> GUILD_SETTINGS_HANDLER = DataHandler.create(
            (settings) -> {
                ModMailHandler.GUILD_SETTINGS.put(settings.getGuildID(), settings);
            },
            ModMailSettings.class,
            "data/modmail/guilds",
            DataHandler.Properties.create()
                    .useExposeAnnotation()
                    .useDefaultFileNamePredicate()
    );

    protected static final DataHandler<ModMailInstance> MODMAIl_INSTANCE_HANDLER = DataHandler.create(
            (inst) -> {
                ModMailHandler.MOD_MAIL_INSTANCE_ID.add(inst);
                ModMailHandler.MOD_MAIL_INSTANCE_CHANNEL.put(inst.getChannelID(), inst);
                if (inst.isActive())
                    ModMailHandler.MOD_MAIL_INSTANCE.put(inst.getUserID(), inst);
            },
            ModMailInstance.class,
            "data/modmail/users",
            DataHandler.Properties.create()
                    .useExposeAnnotation()
                    .setFileName("settings.json")
                    .useDefaultFileNamePredicate()
    );

    private static final HashMap<String, ModMailSettings> GUILD_SETTINGS = new HashMap<>(); // GuildID -> CategoryID

    private static final HashMap<String, ModMailInstance> MOD_MAIL_INSTANCE = new HashMap<>(); // UserID -> Inst
    private static final HashMap<String, ModMailInstance> MOD_MAIL_INSTANCE_CHANNEL = new HashMap<>(); // ChannelID -> Inst
    private static final HashSet<ModMailInstance> MOD_MAIL_INSTANCE_ID = new HashSet<>();

    private static final HashMap<String, List<String>> CHOICES = new HashMap<>(); // GuildID's



    public static ModMailInstance findByGuildAndUserID(String guildID, String userID) {
        if (MOD_MAIL_INSTANCE_ID.isEmpty()) return null;
        var result = MOD_MAIL_INSTANCE_ID.stream().filter(inst -> inst.getGuildID().equals(guildID) && inst.getUserID().equals(userID)).findAny();
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
            var settings = new ModMailSettings(guildID, categoryID, guildName);
            GUILD_SETTINGS.put(guildID, settings);
            settings.save();
        }
    }

    public static void showChoices(Message message, User user) {
        List<SelectOption> OPTIONS = new ArrayList<>();
        GUILD_SETTINGS.forEach((key, setting) -> {
            var isMember = user.getJDA().getGuilds().stream()
                    .filter(guild -> guild.getId().equals(setting.getGuildID()))
                    .anyMatch(guild -> guild.isMember(user));

            if (isMember) {
                OPTIONS.add(SelectOption.of(setting.getGuildName(), setting.getGuildID()));
            }
        });

        if (!OPTIONS.isEmpty()) {
            StringSelectMenu.Builder menu = StringSelectMenu.create("mangobot:modmail");
            menu.addOptions(OPTIONS);

            message.reply("Select a ModMail to join").setActionRow(menu.build()).queue();
        } else {
            message.reply("You are not in any guilds that have ModMail configured!").queue();
        }
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
            ModMailSettings settings = GUILD_SETTINGS.get(guildID);

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
                current.rejoin(message, guild);
                current.openModMail(message.getChannel().asPrivateChannel(), guild, user, settings.getJoinMessage());
            } else {
                guild.createTextChannel(user.getEffectiveName(), category).queue(e -> {
                    var inst = new ModMailInstance(guildID, e.getId(), user.getId(), guild.getName(), guild.getIconUrl(), new AtomicBoolean(true));

                    MOD_MAIL_INSTANCE_ID.add(inst);
                    MOD_MAIL_INSTANCE_CHANNEL.put(inst.getChannelID(), inst);
                    MOD_MAIL_INSTANCE.put(user.getId(), inst);

                    inst.openModMail(message.getChannel().asPrivateChannel(), guild, user, settings.getJoinMessage());
                    inst.joined(e, user);
                    inst.save();
                });
            }
            return 0; // All is fine...
        } else {
            return 3;
        }
    }

    public static void close(Message message, String channelID) {
        var inst = MOD_MAIL_INSTANCE_CHANNEL.get(channelID);
        if (inst == null) return;

        MOD_MAIL_INSTANCE.remove(inst.getUserID());
        MOD_MAIL_INSTANCE_CHANNEL.remove(inst.getChannelID());

        var channel = Bot.getJDAInstance().getTextChannelById(inst.getChannelID());
        if (channel == null) return;

        channel.delete().queue();
        inst.closeModMail(message);
        inst.delete();
    }

    public static boolean leave(Message message) {
        var user = message.getAuthor();
        var channel = message.getChannel().asPrivateChannel();
        var inst = MOD_MAIL_INSTANCE.get(user.getId());
        if (inst == null) return false;
        inst.leave(message, channel, user);
        inst.save();
        MOD_MAIL_INSTANCE.remove(user.getId());
        return true;
    }

    public static void onLoad(LoadEvent event) {
        System.out.println("Loading ModMailHandler Data Stage 1...");

        GUILD_SETTINGS_HANDLER.loadAll(); // Loads everything...

        System.out.println("Loading ModMailHandler Data Stage 1 -> Completed");


        System.out.println("Loading ModMailHandler Data Stage 2...");

        MODMAIl_INSTANCE_HANDLER.loadAll();

        System.out.println("Loading ModMailHandler Data Stage 2 -> Completed");
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
            if (inst == null || !inst.isActive()) return;
            inst.sendToModMail(content, user);
        }
    }

    public static void onMessageEdit(DMessageUpdateEvent event) {
    }

}
