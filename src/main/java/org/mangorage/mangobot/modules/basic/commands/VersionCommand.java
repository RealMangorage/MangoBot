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

package org.mangorage.mangobot.modules.basic.commands;

import net.dv8tion.jda.api.entities.Message;
import org.jetbrains.annotations.NotNull;
import org.mangorage.mangobot.core.Bot;
import org.mangorage.mangobotapi.core.commands.Arguments;
import org.mangorage.mangobotapi.core.commands.CommandResult;
import org.mangorage.mangobotapi.core.commands.IBasicCommand;
import org.mangorage.mangobotapi.core.data.DataHandler;

import java.util.concurrent.atomic.AtomicReference;

public class VersionCommand implements IBasicCommand {
    public record Version(String version) {
    }

    private static final AtomicReference<Version> VERSION = new AtomicReference<>();
    private static final DataHandler<Version> VERSION_DATA_HANDLER = DataHandler.create(
            VERSION::set,
            Version.class,
            "botdata/",
            DataHandler.Properties.create()
                    .setFileName("version.json")
                    .useDefaultFileNamePredicate()
    );

    static {
        VERSION_DATA_HANDLER.loadAll();
    }


    @NotNull
    @Override
    public CommandResult execute(Message message, Arguments args) {
        var settings = Bot.DEFAULT_SETTINGS;
        settings.apply(message.reply("Bot is running on Version: " + VERSION.get().version())).queue();
        return CommandResult.PASS;
    }

    /**
     * @return
     */
    @Override
    public String commandId() {
        return "version";
    }

    /**
     * @return
     */
    @Override
    public String description() {
        return "Tells you what version the bot is running on.";
    }
}
