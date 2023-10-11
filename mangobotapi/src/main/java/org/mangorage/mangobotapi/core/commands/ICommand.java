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

package org.mangorage.mangobotapi.core.commands;

import net.dv8tion.jda.api.entities.Message;
import org.mangorage.mangobotapi.core.events.CommandEvent;
import org.mangorage.mboteventbus.impl.IEvent;

import java.util.List;

public interface ICommand {
    CommandResult execute(Message message, Arguments args);

    default IEvent<CommandEvent> getListener() {
        return (e) -> {
            if (isValidCommand(e.getCommand())) {
                var fromGuild = e.getMessage().isFromGuild();

                if (!fromGuild && isGuildOnly()) return;
                if (fromGuild && !allowedGuilds().isEmpty() && !allowedGuilds().contains(e.getMessage().getGuild().getId()))
                    return;
                if (!allowedUsers().isEmpty() && !allowedUsers().contains(e.getMessage().getAuthor().getId())) return;

                try {
                    e.setHandled(execute(e.getMessage(), e.getArguments()));
                } catch (Exception ex) {
                    e.setHandled(CommandResult.PASS);
                    e.setException(ex);
                }
            }
        };
    }

    String commandId();

    default boolean isValidCommand(String command) {
        boolean result = ignoreCase() ? command.equalsIgnoreCase(commandId()) : command.equals(commandId());
        return ignoreCase() ? result : commandAliases().contains(command) || result;
    }

    default List<String> commandAliases() {
        return List.of();
    }

    default boolean ignoreCase() {
        return false;
    }

    default boolean isGuildOnly() {
        return true;
    }

    default List<String> allowedGuilds() {
        return List.of();
    }

    default List<String> allowedUsers() {
        return List.of();
    }

}
