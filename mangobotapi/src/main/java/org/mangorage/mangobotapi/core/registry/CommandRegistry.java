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

package org.mangorage.mangobotapi.core.registry;

import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.mangorage.mangobotapi.MangoBotAPI;
import org.mangorage.mangobotapi.core.commands.IBasicCommand;
import org.mangorage.mangobotapi.core.commands.ICommand;
import org.mangorage.mangobotapi.core.commands.ISlashCommand;
import org.mangorage.mangobotapi.core.events.BasicCommandEvent;
import org.mangorage.mangobotapi.core.events.SlashCommandEvent;
import org.mangorage.mboteventbus.base.EventHolder;
import org.mangorage.mboteventbus.impl.IEventListener;

import java.util.concurrent.CopyOnWriteArrayList;

public class CommandRegistry {
    private static final EventHolder<BasicCommandEvent> BASIC_COMMAND_EVENT = EventHolder.create(
            BasicCommandEvent.class,
            (i) -> (e) -> {
                for (IEventListener<BasicCommandEvent> listener : i) {
                    listener.invoke(e);
                }
            });

    public static final EventHolder<SlashCommandEvent> SLASH_COMMAND_EVENT = EventHolder.create(
            SlashCommandEvent.class,
            (i) -> (e) -> {
                for (IEventListener<SlashCommandEvent> listener : i) {
                    listener.invoke(e);
                }
            });

    private static final CopyOnWriteArrayList<ICommand<?, ?>> COMMANDS = new CopyOnWriteArrayList<>();

    public static void addBasicCommand(IBasicCommand command) {
        BASIC_COMMAND_EVENT.addListener(command.getListener());
        COMMANDS.add(command);
    }

    public static void addSlashCommand(ISlashCommand command) {
        var updateAction = MangoBotAPI.getInstance().getJDA().updateCommands();
        var commandData = Commands.slash(command.commandId(), command.description());
        command.registerSubCommands(commandData);
        updateAction.addCommands(commandData).queue();
        SLASH_COMMAND_EVENT.addListener(command.getListener());
        COMMANDS.add(command);
    }

    public static void postBasicCommand(BasicCommandEvent event) {
        BASIC_COMMAND_EVENT.post(event);
    }

    public static void postSlashCommand(SlashCommandEvent event) {
        SLASH_COMMAND_EVENT.post(event);
    }

    public static String getUsage(String commandId) {
        for (ICommand<?, ?> command : COMMANDS) {
            if (command.commandId().equals(commandId)) {
                return command.usage();
            }
        }
        return "";
    }
}
