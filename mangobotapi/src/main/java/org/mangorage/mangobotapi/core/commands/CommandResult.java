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
import org.mangorage.mangobotapi.MangoBotAPI;
import org.mangorage.mangobotapi.core.util.MessageSettings;


public record CommandResult(String string) {
    public static final MessageSettings DEFAULT_SETTINGS = MangoBotAPI.getInstance().getDefaultMessageSettings();

    public static CommandResult of(String string) {
        return new CommandResult(string);
    }

    public static final CommandResult PASS = of(null);
    public static final CommandResult FAIL = of("An error occured while executing this command");
    public static final CommandResult NO_PERMISSION = of("You dont have permission to use this command!");
    public static final CommandResult UNDER_MAINTENANCE = of("This is currently under maintenance! Please try again later!");
    public static final CommandResult GUILD_ONLY = of("This is a Guild Only Command!");
    public static final CommandResult DEVELOPERS_ONLY = of("This command is only for the Developers to use!");

    public void accept(Message message) {
        if (string() != null)
            DEFAULT_SETTINGS.apply(message.reply(string)).queue();
    }
}
