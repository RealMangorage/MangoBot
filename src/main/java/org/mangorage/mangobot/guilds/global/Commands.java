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

package org.mangorage.mangobot.guilds.global;

import org.mangorage.mangobot.core.Constants;
import org.mangorage.mangobot.modules.basic.commands.PingCommand;
import org.mangorage.mangobot.modules.basic.commands.PingSlashCommand;
import org.mangorage.mangobot.modules.basic.commands.PrefixCommand;
import org.mangorage.mangobot.modules.developer.KickBotCommand;
import org.mangorage.mangobot.modules.developer.SpeakCommand;
import org.mangorage.mangobot.modules.developer.TerminateCommand;
import org.mangorage.mangobot.modules.modmail.commands.ModMailCommand;
import org.mangorage.mangobot.modules.music.commands.PauseCommand;
import org.mangorage.mangobot.modules.music.commands.PlayCommand;
import org.mangorage.mangobot.modules.music.commands.PlayingCommand;
import org.mangorage.mangobot.modules.music.commands.QueueCommand;
import org.mangorage.mangobot.modules.music.commands.StopCommand;
import org.mangorage.mangobot.modules.music.commands.VolumeCommand;
import org.mangorage.mangobot.modules.tricks.TrickCommand;
import org.mangorage.mangobotapi.core.registry.CommandRegistry;

public class Commands {

    static {
        CommandRegistry.addBasicCommand(new TrickCommand());
        CommandRegistry.addBasicCommand(new ModMailCommand());
        CommandRegistry.addBasicCommand(new PrefixCommand());
        CommandRegistry.addBasicCommand(new PingCommand());
        CommandRegistry.addBasicCommand(new SpeakCommand());
        CommandRegistry.addBasicCommand(new KickBotCommand());
        CommandRegistry.addBasicCommand(new TerminateCommand());

        CommandRegistry.addSlashCommand(new PingSlashCommand());

        if (Constants.USE_MUSIC) {
            CommandRegistry.addBasicCommand(new PlayCommand());
            CommandRegistry.addBasicCommand(new StopCommand());
            CommandRegistry.addBasicCommand(new QueueCommand());
            CommandRegistry.addBasicCommand(new PauseCommand());
            CommandRegistry.addBasicCommand(new VolumeCommand());
            CommandRegistry.addBasicCommand(new PlayingCommand());
        }
    }

    public static void init() {
    }

}
