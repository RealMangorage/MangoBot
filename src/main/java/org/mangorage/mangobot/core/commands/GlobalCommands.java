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

package org.mangorage.mangobot.core.commands;

import org.mangorage.mangobot.commands.ModMailCommand;
import org.mangorage.mangobot.commands.PrefixCommand;
import org.mangorage.mangobot.commands.ReplyCommand;
import org.mangorage.mangobot.commands.music.PauseCommand;
import org.mangorage.mangobot.commands.music.PlayCommand;
import org.mangorage.mangobot.commands.music.PlayingCommand;
import org.mangorage.mangobot.commands.music.QueueCommand;
import org.mangorage.mangobot.commands.music.StopCommand;
import org.mangorage.mangobot.commands.music.VolumeCommand;
import org.mangorage.mangobot.commands.tricks.TrickCommand;
import org.mangorage.mangobot.core.Bot;
import org.mangorage.mangobot.core.Constants;
import org.mangorage.mangobot.core.Util;
import org.mangorage.mangobotapi.core.commands.AbstractCommand;
import org.mangorage.mangobotapi.core.commands.CommandHolder;
import org.mangorage.mangobotapi.core.commands.CommandResult;
import org.mangorage.mangobotapi.core.registry.CommandRegistry;
import org.mangorage.mangobotapi.core.registry.RegistryObject;
import org.mangorage.mangobotapi.core.util.MessageSettings;

public class GlobalCommands {
    public static final CommandRegistry COMMANDS = CommandRegistry.global();
    public static final RegistryObject<CommandHolder<TrickCommand>> TRICK_COMMAND = COMMANDS.register("tricks", new TrickCommand());
    public static final RegistryObject<CommandHolder<ModMailCommand>> MOD_MAIL_COMMAND = COMMANDS.register("mail", new ModMailCommand());
    public static final RegistryObject<CommandHolder<PrefixCommand>> PREFIX_ADMIN = COMMANDS.register("setPrefix", new PrefixCommand());


    static {
        COMMANDS.register("speak", new ReplyCommand("I have spoken!"));

        COMMANDS.register("terminate", AbstractCommand.create((message, args) -> {
            MessageSettings settings = MessageSettings.create().build();

            if (message.getAuthor().getId().equals("194596094200643584")) {
                message.getChannel().sendMessage("Terminating Bot").queue();
                Util.call(() -> {
                    try {
                        Thread.sleep(1000);
                    } catch (Exception e) {

                    }
                    System.exit(0);
                });
            } else {
                settings.apply(message.getChannel().sendMessage("Unable to Terminate bot. Only MangoRage can do this!")).queue();
            }
            return CommandResult.PASS;
        }, false));


        if (Constants.USE_MUSIC) {
            COMMANDS.register("play", new PlayCommand());
            COMMANDS.register("stop", new StopCommand());
            COMMANDS.register("queue", new QueueCommand());
            COMMANDS.register("pause", new PauseCommand());
            COMMANDS.register("setVolume", new VolumeCommand());
            COMMANDS.register("playing", new PlayingCommand());
        }
    }

    public static void init() {
        COMMANDS.register(Bot.EVENT_BUS);
    }

}
