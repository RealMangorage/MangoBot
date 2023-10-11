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

package org.mangorage.mangobot.core;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.dv8tion.jda.api.entities.ISnowflake;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.utils.TimeUtil;
import org.mangorage.mangobotapi.MangoBotAPI;
import org.mangorage.mangobotapi.core.commands.Arguments;
import org.mangorage.mangobotapi.core.commands.CommandPrefix;
import org.mangorage.mangobotapi.core.events.CommandEvent;
import org.mangorage.mangobotapi.core.registry.CommandRegistry;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.temporal.TemporalAccessor;

public class Util {

    private static final Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();

    public static TemporalAccessor getTimestamp(ISnowflake iSnowflake) {
        return TimeUtil.getTimeCreated(iSnowflake);
    }


    public static boolean handleMessage(MessageReceivedEvent event) {
        // Handle Message and prefix
        String Prefix = event.isFromGuild() ? CommandPrefix.getPrefix(event.getGuild().getId()) : CommandPrefix.DEFAULT;

        Message message = event.getMessage();
        String rawMessage = message.getContentRaw();

        if (rawMessage.length() > 1 && rawMessage.startsWith(Prefix)) {
            if (event.getAuthor().isBot()) return true;
            String[] command_pre = rawMessage.split(" ");
            String command = command_pre[0].replaceFirst(Prefix, "");
            Arguments arguments = Arguments.of(Arguments.of(command_pre).getFrom(1).split(" "));

            var commandEvent = new CommandEvent(event.getMessage(), command, arguments);
            CommandRegistry.postCommand(commandEvent);

            // Now post to anything using our EventBus...
            if (!commandEvent.isHandled())
                MangoBotAPI.getInstance().getEventBus().post(commandEvent);

            if (commandEvent.getException() != null) {
                Bot.DEFAULT_SETTINGS.apply(message.reply("""
                        An Exception occurred while executing the command.
                        %s
                        """.formatted(commandEvent.getException().getMessage()))).queue();
            } else if (commandEvent.isHandled()) {
                commandEvent.getCommandResult().accept(message);
            } else {
                Bot.DEFAULT_SETTINGS.apply(message.reply("Invalid Command")).queue();
            }

            return true;
        }

        return false;
    }

    public static Integer parseStringIntoInteger(String s) {
        Integer res = null;
        try {
            res = Integer.parseInt(s);
        } catch (NumberFormatException ignored) {
        }
        return res;
    }

    public static boolean copy(Path from, BasicFileAttributes a, Path target) {
        System.out.println("Copy " + (a.isDirectory() ? "DIR " : "FILE") + " => " + target);
        try {
            if (a.isDirectory())
                Files.createDirectories(target);
            else if (a.isRegularFile())
                Files.copy(from, target, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        return true;
    }
}
