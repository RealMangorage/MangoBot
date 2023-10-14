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

package org.mangorage.mangobot.modules.developer;

import net.dv8tion.jda.api.entities.Message;
import org.jetbrains.annotations.NotNull;
import org.mangorage.mangobotapi.core.commands.Arguments;
import org.mangorage.mangobotapi.core.commands.CommandResult;
import org.mangorage.mangobotapi.core.commands.IBasicCommand;
import org.mangorage.mangobotapi.core.util.TaskScheduler;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class TerminateCommand implements IBasicCommand {
    private static final List<String> USERS = List.of("194596094200643584");

    @NotNull
    @Override
    public CommandResult execute(Message message, Arguments args) {
        if (message.getAuthor().getId().equals("194596094200643584")) {
            message.getChannel().sendMessage("Terminating Bot in 5 seconds...").queue();
            TaskScheduler.getExecutor().schedule(() -> {
                System.exit(0);
            }, 5, TimeUnit.SECONDS);
            return CommandResult.PASS;
        } else {
            return CommandResult.DEVELOPERS_ONLY;
        }
    }


    @Override
    public String commandId() {
        return "terminate";
    }


    @Override
    public List<String> allowedUsers() {
        return USERS;
    }
}
