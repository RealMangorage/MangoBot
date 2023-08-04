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

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import org.mangorage.mangobot.core.Bot;
import org.mangorage.mangobot.core.commands.GlobalPermissions;
import org.mangorage.mangobot.core.commands.registry.PermissionRegistry;
import org.mangorage.mangobot.core.commands.util.Arguments;
import org.mangorage.mangobot.core.commands.util.CommandResult;
import org.mangorage.mangobot.core.events.CommandEvent;
import org.mangorage.mangobot.core.events.SubscribeEvent;

import java.util.HashMap;

public class TrickCommand extends AbstractCommand {
    private final HashMap<String, HashMap<String, String>> CONTENT = new HashMap<>(); // guildID Map<ID, Content>

    public TrickCommand() {
        CommandEvent.addListener(Bot.EVENT_BUS, this::onCommandEvent); // Register this listener to bot's event bus!
    }


    @Override
    public CommandResult execute(Message message, Arguments args) {
        Member member = message.getMember();
        String guildID = message.getGuild().getId();
        String type = args.get(0);
        String id = args.get(1);
        String content = args.getFrom(2);

        if (type.equals("-a") && id != null && content != null) {
            if (!PermissionRegistry.hasNeededPermission(member, GlobalPermissions.TRICK_ADMIN))
                return CommandResult.NO_PERMISSION;

            if (CONTENT.containsKey(guildID) && CONTENT.get(guildID).containsKey(id)) {
                message.reply("Trick '%s' already exists!".formatted(id)).mentionRepliedUser(false).setSuppressedNotifications(true).queue();
            } else {
                CONTENT.computeIfAbsent(guildID, (k) -> new HashMap<>()).put(id, content);
                message.reply("Added Trick: '%s'".formatted(id)).mentionRepliedUser(false).setSuppressedNotifications(true).queue();
            }
            return CommandResult.PASS;
        } else if (type.equals("-r") && id != null) {
            if (!PermissionRegistry.hasNeededPermission(member, GlobalPermissions.TRICK_ADMIN))
                return CommandResult.NO_PERMISSION;
        } else if (type.equals("-s") && id != null) {
            MessageChannelUnion channel = message.getChannel();
            if (CONTENT.containsKey(guildID) && CONTENT.get(guildID).containsKey(id)) {
                String response = CONTENT.get(guildID).get(id);
                channel.sendMessage(response).setSuppressedNotifications(true).queue();
            } else {
                message.reply("Trick '%s' does not exist!".formatted(id)).mentionRepliedUser(false).setSuppressedNotifications(true).queue();
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
}
