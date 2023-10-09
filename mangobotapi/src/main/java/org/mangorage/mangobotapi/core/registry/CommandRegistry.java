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

import org.jetbrains.annotations.NotNull;
import org.mangorage.mangobotapi.core.commands.AbstractCommand;
import org.mangorage.mangobotapi.core.commands.CommandHolder;
import org.mangorage.mangobotapi.core.events.CommandEvent;
import org.mangorage.mboteventbus.impl.IEventBus;

import java.util.HashMap;

public class CommandRegistry {
    public static final CommandRegistry GLOBAL = new CommandRegistry("-1");

    public static CommandRegistry create(@NotNull String guildID) {
        return new CommandRegistry(guildID);
    }

    private final String guildID;
    private final HashMap<String, CommandHolder<?>> HOLDERS = new HashMap<>();

    private CommandRegistry(String guildID) {
        this.guildID = guildID;
    }

    public String getGuildId() {
        return guildID;
    }

    public <T extends AbstractCommand> CommandHolder<T> register(String id, T command) {
        var holder = CommandHolder.create(id, command);
        HOLDERS.put(id, holder);
        return holder;
    }

    public void register(IEventBus bus) {
        bus.addListener(CommandEvent.class, this::commandEvent);
    }

    private void commandEvent(CommandEvent event) {
        if (!event.isHandled()) {

            boolean isGuild = event.getMessage().isFromGuild();

            if (isGuild) {
                if (event.getGuildId().equals(guildID) || guildID.contains("-1")) {
                    var holder = HOLDERS.get(event.getCommand());
                    if (holder != null) {
                        var command = holder.getCommand();
                        if (command != null) {
                            event.setHandled(command.execute(event.getMessage(), event.getArguments()));
                        }
                    }
                }
            } else if (guildID.equals("-1")) {
                var holder = HOLDERS.get(event.getCommand());
                if (holder != null) {
                    var command = holder.getCommand();
                    if (command != null) {
                        if (command.isGuildOnly()) return;
                        event.setHandled(command.execute(event.getMessage(), event.getArguments()));
                    }
                }
            }
        }
    }
}
