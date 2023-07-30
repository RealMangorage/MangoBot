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

import net.dv8tion.jda.api.entities.Message;
import org.mangorage.mangobot.core.commands.util.Arguments;
import org.mangorage.mangobot.core.commands.util.CommandResult;

public abstract class AbstractCommand {
    public static AbstractCommand create(ICommand command, boolean isGuild) {
        if (command == null)
            throw new IllegalStateException("Command cannot be null");
        return new AbstractCommand() {
            @Override
            public CommandResult execute(Message message, Arguments args) {
                return command.execute(message, args);
            }

            /**
             * @return
             */
            @Override
            public boolean isGuildOnly() {
                return isGuild;
            }
        };
    }

    public abstract CommandResult execute(Message message, Arguments args);

    public boolean isGuildOnly() {
        return true;
    }

    @FunctionalInterface
    public interface ICommand {
        CommandResult execute(Message message, Arguments args);
    }
}
