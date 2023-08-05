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

import com.google.gson.Gson;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import org.mangorage.mangobot.core.events.LoadEvent;
import org.mangorage.mangobot.core.events.SaveEvent;
import org.mangorage.mangobot.core.permissions.GlobalPermissions;
import org.mangorage.mangobotapi.core.AbstractCommand;
import org.mangorage.mangobotapi.core.eventbus.SubscribeEvent;
import org.mangorage.mangobotapi.core.events.CommandEvent;
import org.mangorage.mangobotapi.core.registry.PermissionRegistry;
import org.mangorage.mangobotapi.core.util.Arguments;
import org.mangorage.mangobotapi.core.util.CommandResult;
import org.mangorage.mangobotapi.core.util.MessageSettings;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;

import static org.mangorage.mangobot.core.Bot.EVENT_BUS;

public class TrickCommand extends AbstractCommand {
    private final HashMap<String, HashMap<String, String>> CONTENT = new HashMap<>(); // guildID Map<ID, Content>

    @SubscribeEvent
    public void onSaveEvent(SaveEvent event) {
        System.out.println("Saving Data!");
        String savePath = "botresources/guilddata/tricksdata/%s/";

        File saveDir = new File("botresources/guilddata/tricksdata/");
        if (saveDir.exists()) {
            for (File dir : saveDir.listFiles())
                if (dir.isDirectory())
                    for (File file : dir.listFiles())
                        if (!file.isDirectory())
                            file.delete();
        } else {
            saveDir.mkdirs();
        }

        CONTENT.forEach((guildID, data) -> {
            data.forEach((trickid, content) -> {
                File dir = new File(savePath.formatted(guildID));
                if (!dir.exists())
                    dir.mkdirs();

                Gson gson = new Gson();
                try {
                    String json = gson.toJson(new Data(guildID, trickid, content));
                    Files.writeString(Path.of((savePath + "%s.json").formatted(guildID, trickid)), json);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        });


    }

    @SubscribeEvent
    public void onLoadEvent(LoadEvent event) {
        System.out.println("Loading Data!");
        File saveDir = new File("botresources/guilddata/tricksdata/");
        if (saveDir.exists()) {
            for (File dir : saveDir.listFiles())
                if (dir.isDirectory())
                    for (File file : dir.listFiles())
                        if (!file.isDirectory())
                            load(file);
        }
    }

    private void load(File file) {
        Gson gson = new Gson();
        try {
            Data data = gson.fromJson(Files.readString(file.toPath()), Data.class);
            if (data.content != null && data.trickid != null && data.guildid != null)
                CONTENT.computeIfAbsent(data.guildid, (k) -> new HashMap<>()).put(data.trickid, data.content);
        } catch (Exception e) {

        }
    }

    public TrickCommand() {
        // Register listeners to Bot.EVENT_BUS
        LoadEvent.addListener(EVENT_BUS, this::onLoadEvent);
        SaveEvent.addListener(EVENT_BUS, this::onSaveEvent);
        CommandEvent.addListener(EVENT_BUS, this::onCommandEvent);
    }


    @Override
    public CommandResult execute(Message message, Arguments args) {
        MessageSettings dMessage = MessageSettings.create().build();
        Member member = message.getMember();
        String guildID = message.getGuild().getId();
        String type = args.get(0);
        String id = args.get(1);
        String content = args.getFrom(2);

        if (type.equals("-a") && id != null && content != null) {
            if (!PermissionRegistry.hasNeededPermission(member, GlobalPermissions.TRICK_ADMIN))
                return CommandResult.NO_PERMISSION;

            if (CONTENT.containsKey(guildID) && CONTENT.get(guildID).containsKey(id)) {
                dMessage.apply(message.reply("Trick '%s' already exists!".formatted(id))).queue();
            } else {
                CONTENT.computeIfAbsent(guildID, (k) -> new HashMap<>()).put(id, content);
                dMessage.apply(message.reply("Added Trick: '%s'".formatted(id))).queue();
            }
            return CommandResult.PASS;
        } else if (type.equals("-r") && id != null) {
            if (!PermissionRegistry.hasNeededPermission(member, GlobalPermissions.TRICK_ADMIN))
                return CommandResult.NO_PERMISSION;

            if (CONTENT.containsKey(guildID) && CONTENT.get(guildID).containsKey(id)) {
                CONTENT.get(guildID).remove(id);
                dMessage.apply(message.reply("Removed trick '%s'".formatted(id))).queue();
            } else {
                dMessage.apply(message.reply("Trick '%s' does not exist.")).queue();
            }
            return CommandResult.PASS;
        } else if (type.equals("-s") && id != null) {
            MessageChannelUnion channel = message.getChannel();
            if (CONTENT.containsKey(guildID) && CONTENT.get(guildID).containsKey(id)) {
                String response = CONTENT.get(guildID).get(id);
                channel.sendMessage(response).setSuppressedNotifications(true).queue();
            } else {
                dMessage.apply(message.reply("Trick '%s' does not exist!".formatted(id))).queue();
            }
            return CommandResult.PASS;
        }

        return CommandResult.FAIL;
    }

    @Override
    public boolean isGuildOnly() {
        return true;
    }

    @SubscribeEvent
    public void onCommandEvent(CommandEvent event) {
        if (!event.isHandled()) {
            Message message = event.getMessage();
            String guildID = message.getGuild().getId();
            String command = event.getCommand();

            // We have found something that works, make sure we do this so that "Invalid Command" doesn't occur
            // event.setHandled() insures that the command has been handled!
            if (CONTENT.containsKey(guildID) && CONTENT.get(guildID).containsKey(command))
                event.setHandled(execute(message, Arguments.of("-s", command)));
        }
    }

    public class Data {
        private String guildid;
        private String trickid;
        private String content;

        public Data() {
        }

        public Data(String guildid, String trickid, String content) {
            this.guildid = guildid;
            this.trickid = trickid;
            this.content = content;
        }

        public String getGuildID() {
            return guildid;
        }

        public String getTrickID() {
            return trickid;
        }

        public String getContent() {
            return content;
        }
    }
}
