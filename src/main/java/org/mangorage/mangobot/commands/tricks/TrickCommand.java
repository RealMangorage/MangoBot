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

package org.mangorage.mangobot.commands.tricks;

import com.google.gson.Gson;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.mangorage.mangobot.core.Bot;
import org.mangorage.mangobot.core.commands.permissions.GlobalPermissions;
import org.mangorage.mangobotapi.core.commands.AbstractCommand;
import org.mangorage.mangobotapi.core.commands.Arguments;
import org.mangorage.mangobotapi.core.commands.CommandResult;
import org.mangorage.mangobotapi.core.events.CommandEvent;
import org.mangorage.mangobotapi.core.events.LoadEvent;
import org.mangorage.mangobotapi.core.events.SaveEvent;
import org.mangorage.mangobotapi.core.events.discord.DButtonInteractionEvent;
import org.mangorage.mangobotapi.core.registry.GuildCache;
import org.mangorage.mangobotapi.core.registry.PermissionRegistry;
import org.mangorage.mangobotapi.core.script.ScriptParser;
import org.mangorage.mangobotapi.core.util.MessageSettings;
import org.mangorage.mangobotapi.core.util.TaskScheduler;
import org.mangorage.mangobotapi.core.util.misc.PagedList;
import org.mangorage.mangobotapi.core.util.misc.RunnableTask;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.StringJoiner;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import static org.mangorage.mangobot.core.Bot.EVENT_BUS;

// TODO: Redo Execute command entirely, make more use of Arguments.class
@SuppressWarnings("all")
public class TrickCommand extends AbstractCommand {
    public enum Type {
        DEFAULT,
        CODE,
        ALIAS
    }

    private static final String TRICKS_DIR = "data/guilddata/tricks/";
    private static final String SAVE_PATH = "data/guilddata/tricks/%s/";


    private final HashMap<String, HashMap<String, Data>> CONTENT = new HashMap<>(); // guildID Map<ID, Content>
    private final ConcurrentHashMap<String, PagedList<String>> PAGES = new ConcurrentHashMap<>();

    public void onSaveEvent(SaveEvent event) {
        System.out.println("Saving Tricks Data!");

        CONTENT.forEach((guildID, data) -> {
            data.forEach((trickid, trick) -> {
                trick.save();
            });
        });
    }

    public void onLoadEvent(LoadEvent event) {
        System.out.println("Loading Tricks Data!");
        File saveDir = new File(TRICKS_DIR);
        if (saveDir.exists()) {
            for (File dir : saveDir.listFiles())
                if (dir.isDirectory())
                    for (File file : dir.listFiles())
                        if (!file.isDirectory())
                            load(file);
        }
        System.out.println("Finished loading Tricks Data!");
    }

    private void load(File file) {
        Gson gson = new Gson();
        try {
            Data data = gson.fromJson(Files.readString(file.toPath()), Data.class);
            if (data.settings == null)
                data = data.withSettings(new TrickConfig(true));
            System.out.println("Loaded Trick: '%s' for guild '%s'".formatted(data.trickID(), GuildCache.getName(data.guildID)));
            CONTENT.computeIfAbsent(data.guildID, (k) -> new HashMap<>()).put(data.trickID(), data);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public TrickCommand() {
        // Register listeners to Bot.EVENT_BUS
        EVENT_BUS.addListener(LoadEvent.class, this::onLoadEvent);
        EVENT_BUS.addListener(SaveEvent.class, this::onSaveEvent);
        EVENT_BUS.addListener(CommandEvent.class, this::onCommandEvent);
        EVENT_BUS.addListener(DButtonInteractionEvent.class, this::onButton);
    }


    @Override
    public CommandResult execute(Message message, Arguments args) {
        MessageSettings dMessage = Bot.DEFAULT_SETTINGS;
        Member member = message.getMember();
        String guildID = message.getGuild().getId();
        String type = args.get(0);
        String id = args.get(1);
        // !tricks -a wow -supress false -content Wow is amazing!
        // !tricks -a wow -content wooop!

        if ((type.equals("-a") || type.equals("-add")) && id != null) {
            if (!PermissionRegistry.hasNeededPermission(member, GlobalPermissions.TRICK_ADMIN))
                return CommandResult.NO_PERMISSION;

            if (CONTENT.containsKey(guildID) && CONTENT.get(guildID).containsKey(id)) {
                dMessage.apply(message.reply("Trick '%s' already exists!".formatted(id))).queue();
            } else {
                boolean hasSupressArg = args.hasArg("-supress");
                boolean hasContent = args.hasArg("-content");
                Type trickType = getTrickType(args);

                if (!hasContent)
                    return CommandResult.FAIL;

                int contentIndex = args.getArgIndex("-content");
                String content = args.getFrom(contentIndex + 1);

                Data data = new Data(guildID, id, member.getId(), trickType, content, new TrickConfig(hasSupressArg));
                CONTENT.computeIfAbsent(guildID, (k) -> new HashMap<>()).put(id, data);

                data.save();
                dMessage.apply(message.reply("Added Trick: '%s'".formatted(id))).queue();
            }
            return CommandResult.PASS;
        } else if ((type.equals("-e") || type.equals("-edit")) & id != null) {
            if (!PermissionRegistry.hasNeededPermission(member, GlobalPermissions.TRICK_ADMIN))
                return CommandResult.NO_PERMISSION;

            Data oldData;
            if (CONTENT.containsKey(guildID) && CONTENT.get(guildID).containsKey(id)) {
                oldData = CONTENT.get(guildID).remove(id);
            } else {
                dMessage.apply(message.reply("Trick '%s' does not exist!".formatted(id))).queue();
                return CommandResult.FAIL;
            }

            boolean hasSupressArg = args.hasArg("-supress");
            boolean hasContent = args.hasArg("-content");
            Type trickType = getTrickType(args);
            if (!hasContent)
                return CommandResult.FAIL;

            int contentIndex = args.getArgIndex("-content");
            String content = args.getFrom(contentIndex + 1);

            Data data = new Data(guildID, oldData.trickID(), oldData.ownerID(), trickType, content, new TrickConfig(hasSupressArg));
            CONTENT.computeIfAbsent(guildID, (k) -> new HashMap<>()).put(id, data);

            data.save();
            dMessage.apply(message.reply("Modfied Trick: '%s'".formatted(id))).queue();

            return CommandResult.PASS;
        } else if ((type.equals("-r") || type.equals("-remove")) && id != null) {
            if (!PermissionRegistry.hasNeededPermission(member, GlobalPermissions.TRICK_ADMIN))
                return CommandResult.NO_PERMISSION;

            if (CONTENT.containsKey(guildID) && CONTENT.get(guildID).containsKey(id)) {
                CONTENT.get(guildID).get(id).delete();
                CONTENT.get(guildID).remove(id);
                dMessage.apply(message.reply("Removed trick '%s'".formatted(id))).queue();
            } else {
                dMessage.apply(message.reply("Trick '%s' does not exist.")).queue();
            }
            return CommandResult.PASS;
        } else if ((type.equals("-s") || type.equals("show")) && id != null) {
            MessageChannelUnion channel = message.getChannel();
            if (CONTENT.containsKey(guildID) && CONTENT.get(guildID).containsKey(id)) {
                Data data = CONTENT.get(guildID).get(id);
                String response = data.content();
                return executeTrick(data, channel, message, args);
            } else {
                dMessage.apply(message.reply("Trick '%s' does not exist!".formatted(id))).queue();
            }
            return CommandResult.PASS;
        } else if (type.equals("-l") || type.equals("-list")) {
            MessageChannelUnion channel = message.getChannel();
            if (CONTENT.containsKey(guildID) && !CONTENT.get(guildID).isEmpty()) {

                PagedList<String> tricks = createTricks(guildID, args.findArgOrDefault("-l", (result) -> {
                    return Integer.valueOf(result);
                }, 5));

                channel.sendMessage("""
                        Getting Tricks List... 
                        """).queue((m -> {
                    PAGES.put(m.getId(), tricks);
                    TaskScheduler.executor.schedule(new RunnableTask<>(m, (d) -> removeTricksList(d.get())), 10, TimeUnit.MINUTES);
                    updateTrickListMessage(tricks, m, true);
                        })
                );
            }
            return CommandResult.PASS;
        } else if ((type.equals("-i") || type.equals("-info")) && id != null) {
            MessageChannelUnion channel = message.getChannel();
            if (CONTENT.containsKey(guildID) && CONTENT.get(guildID).containsKey(id)) {
                Data data = CONTENT.get(guildID).get(id);
                StringJoiner builder = new StringJoiner("\n");
                builder.add("Trick Info:");
                builder.add("-----------");
                builder.add("Owner: <@%s>".formatted(data.ownerID()));
                builder.add("TrickType: %s".formatted(data.trickType()));
                builder.add("ID: %s".formatted(data.trickID()));
                builder.add("Content:".formatted());
                builder.add(data.content());

                message.reply(builder.toString()).setSuppressEmbeds(true).setSuppressedNotifications(true).queue();
            } else {
                dMessage.apply(message.reply("Trick '%s' does not exist!".formatted(id))).queue();
            }
            return CommandResult.PASS;
        }
        return CommandResult.FAIL;
    }

    private Type getTrickType(Arguments args) {
        if (args.hasArg("-code"))
            return Type.CODE;
        if (args.hasArg("-alias"))
            return Type.ALIAS;
        return Type.DEFAULT;
    }

    private CommandResult executeTrick(Data data, MessageChannelUnion channel, Message message, Arguments args) {
        MessageSettings dMessage = Bot.DEFAULT_SETTINGS;
        String response = data.content();
        String guildID = data.guildID;

        switch (data.trickType()) {
            case DEFAULT -> {
                boolean supressEmbeds = data.settings().supressEmbeds();
                dMessage.apply(channel.sendMessage(response)).setSuppressEmbeds(supressEmbeds).queue();
            }
            case CODE -> {
                executeScript(message, args.getFrom(2).split(" "), response);
            }
            case ALIAS -> {
                String defer = data.content();
                if (CONTENT.get(guildID).containsKey(defer)) {
                    var tData = CONTENT.get(guildID).get(defer);
                    if (tData.trickType() == Type.ALIAS) {
                        // Cant do it because its an alias
                        return CommandResult.FAIL;
                    } else {
                        executeTrick(tData, channel, message, args);
                        return CommandResult.PASS;
                    }
                } else {
                    // Cant find the trick?
                    return CommandResult.FAIL;
                }
            }
        }

        return CommandResult.PASS;
    }

    private void executeScript(Message message, String[] args, String script) {
        System.out.println(script);
        String[] strArr = script.split("\\n");

        if (strArr[0].startsWith("```")) {
            strArr[0] = "";
            strArr[strArr.length - 1] = "";
        }

        StringJoiner scriptHandled = new StringJoiner("\n");
        for (int i = 0; i < strArr.length; i++) {
            scriptHandled.add(strArr[i]);
        }

        System.out.println("cool -> %s".formatted(scriptHandled.toString()));

        ScriptEngine engine = ScriptParser.get();

        try {
            engine.eval(scriptHandled.toString());
            Invocable inv = (Invocable) engine;
            inv.invokeFunction("main", message, args);
        } catch (ScriptException | NoSuchMethodException e) {
            message.reply(e.getMessage()).setSuppressedNotifications(true).queue();
        }
    }

    @Override
    public boolean isGuildOnly() {
        return true;
    }

    private void removeTricksList(Message message) {
        if (PAGES.containsKey(message.getId())) {
            message.editMessage(createTricksString(PAGES.get(message.getId()))).setComponents().queue();
            PAGES.remove(message.getId());
        }
    }

    private void updateTrickListMessage(PagedList<String> tricks, Message message, boolean addButtons, String buttonID) {
        switch (buttonID) {
            case "next" -> tricks.next();
            case "prev" -> tricks.previous();
        }

        String result = createTricksString(tricks);

        if (addButtons) {
            // Add buttons!
            Button prev = Button.primary("prev".formatted(message.getId()), "previous");
            Button next = Button.primary("next".formatted(message.getId()), "next");

            message.editMessage(result).setActionRow(prev, next).queue();
        } else {
            message.editMessage(result).queue();
        }
    }

    private void updateTrickListMessage(PagedList<String> tricks, Message message, boolean addButtons) {
        updateTrickListMessage(tricks, message, addButtons, "");
    }

    private String createTricksString(PagedList<String> tricks) {
        String result = "List of Tricks (%s / %s) \r".formatted(tricks.getPage(), tricks.totalPages());

        PagedList.Page<String> entries = tricks.current();

        int i = 0;
        for (String entry : entries.getEntries()) {
            i++;
            result = result + "%s: %s \r".formatted(i, entry);
        }

        return result;
    }

    private PagedList<String> createTricks(String guildID, int entries) {
        PagedList<String> tricks = new PagedList<>();

        Object[] LIST = CONTENT.get(guildID).keySet().toArray();
        tricks.rebuild(Arrays.copyOf(LIST, LIST.length, String[].class), entries);

        return tricks;
    }


    public void onCommandEvent(CommandEvent event) {
        if (!event.isHandled()) {
            Message message = event.getMessage();
            if (!message.isFromGuild()) return;
            String guildID = message.getGuild().getId();
            String command = event.getCommand();
            String args = event.getArguments().getFrom(0);

            // We have found something that works, make sure we do this so that "Invalid Command" doesn't occur
            // event.setHandled() insures that the command has been handled!
            if (CONTENT.containsKey(guildID) && CONTENT.get(guildID).containsKey(command))
                event.setHandled(execute(message, Arguments.of("-s", command, args)));
        }
    }

    public void onButton(DButtonInteractionEvent event) {
        var interaction = event.get();

        Message message = interaction.getMessage();
        String ID = message.getId();

        if (PAGES.containsKey(ID)) {
            updateTrickListMessage(PAGES.get(ID), message, false, interaction.getButton().getId());
            interaction.getInteraction().deferEdit().queue();
        }
    }

    public record Data(String guildID, String trickID, String ownerID, Type trickType, String content,
                       TrickConfig settings) {
        public void save() {
            File dir = new File(SAVE_PATH.formatted(guildID));
            if (!dir.exists())
                dir.mkdirs();

            Gson gson = new Gson();
            try {
                String json = gson.toJson(new Data(guildID, trickID, ownerID, trickType, content, new TrickConfig(settings.supressEmbeds())));
                Files.writeString(Path.of((SAVE_PATH + "%s.json").formatted(guildID, trickID)), json);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        public void delete() {
            File file = Path.of((SAVE_PATH + "%s.json").formatted(guildID, trickID)).toFile();
            if (file.exists())
                file.delete();
        }

        public Data withSettings(TrickConfig settings) {
            return new Data(guildID, trickID, ownerID, trickType, content, settings);
        }
    }
}
