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

package org.mangorage.mangobottest.core.commands;

import org.mangorage.mangobot.commands.ReplyCommand;
import org.mangorage.mangobotapi.core.registry.CommandRegistry;
import org.mangorage.mangobotapi.core.registry.RegistryObject;
import org.mangorage.mangobotapi.core.registry.commands.CommandHolder;
import org.mangorage.mangobottest.core.Bot;

public class GlobalCommands {
    public static final CommandRegistry COMMANDS = CommandRegistry.global();

    public static final RegistryObject<CommandHolder<ReplyCommand>> REPLY = COMMANDS.register("testSpeak", new ReplyCommand("Howdy!"));

    public static void init() {
        COMMANDS.register(Bot.EVENT_BUS);
    }
}
