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

import net.dv8tion.jda.api.entities.Message;
import org.mangorage.mangobot.commands.AbstractCommand;
import org.mangorage.mangobot.commands.CommandResult;
import org.mangorage.mangobot.commands.PingCommand;
import org.mangorage.mangobot.commands.ReplyCommand;
import org.mangorage.mangobot.commands.music.*;
import org.mangorage.mangobot.core.Constants;

import java.util.HashMap;

public class CommandManager {
    private static final CommandManager INSTANCE = new CommandManager();

    public static CommandManager getInstance() {
        return INSTANCE;
    }

    private final HashMap<String, CommandHolder<?>> COMMANDS = new HashMap<>();

    public void register() {
        add("speak", new ReplyCommand("I have spoken!"));
        add("paste", new ReplyCommand(
                """
                        Please use a paste site for large blocks of code/logs, instead of dumping it in chat or taking a screenshot.
                                        
                        Here's a list of some paste sites and their size limits:
                            https://gist.github.com/:          [Free] [SignUp] 100MB
                            https://paste.gemwire.uk/:      [Free] 10MB
                            https://paste.ee/:                       [Free] 1MB, [SignUp] 6MB
                            https://pastebin.com/:             [Free] 512KB, [SignUp] [Paid] 10MB
                            https://hastebin.com/:             [Free] 400KB
                            https://paste.centos.org/:       [Free] 1023KB
                            https://mclo.gs/:                        [Free] 15MB
                        """
        ));
        add("sl", new ReplyCommand(
                """
                        # Forge Only Supports 1.19.x and 1.20.x
                        The Forge Discord only supports 1.19.x and 1.20.x
                        The Forge Project including everyone in this discord, and the forums, only supports the latest and LTS versions. Currently 1.19.x and 1.20.x.
                        This is due to the Forge team having limited manpower as we are all people working in our free time. We can't possibly support all Minecraft versions that had a Forge build.
                        Some say a version of Minecraft is the best but others say that about a different version.
                        All of the older versions work and there are some other communities that offer support for older versions.
                        """
        ));
        add("neoforge", new ReplyCommand(
                """
You mentioned NeoForge. NeoForge is a fork of MinecraftForge by many former MC Forge staff and the old discord. We are not affiliated with NeoForge and to get support with NeoForge you should consider joining their discord if you are not already banned. https://discord.neoforged.net/
                        """
        ));
        add("notforge", new ReplyCommand(
                """
                        We only support Minecraft Forge on this discord server, the attached info uses other modloaders. We do not support other modloaders such as Fabric, Quilt, FeatureCreep, LiteLoader, Rift, NeoForge, or any other modloaders mods out of the box. You should contact the developers or the modloader or abstraction layer of your issue.
                        """
        ));
        CommandHolder<PingCommand> PING = add("ping", new PingCommand());
        // Alias's
        add("pingReplys", PING);

        if (Constants.USE_MUSIC) {
            add("play", new PlayCommand());
            add("stop", new StopCommand());
            add("queue", new QueueCommand());
            add("pause", new PauseCommand());
            add("setVolume", new VolumeCommand());
            add("playing", new PlayingCommand());
        }
    }

    private <T extends AbstractCommand> CommandHolder<T> add(String command, T value) {
        CommandHolder<T> HOLDER = CommandHolder.create(value);
        COMMANDS.put(command, HOLDER);
        return HOLDER;
    }

    private <T extends AbstractCommand> void add(String command, CommandHolder<T> holder) {
        COMMANDS.put(command, holder);
    }

    public String[] handleCommand(String command, String content) {
        String params = content.replaceFirst("!" + command, " ").trim();
        return params.split(" ");
    }

    public void handleCommand(String command, Message message) {
        AbstractCommand abstractCommand = COMMANDS.get(command).get();
        if (abstractCommand.isGuildOnly() && message.isFromGuild()) {
            CommandResult result = COMMANDS.get(command).get().execute(message, handleCommand(command, message.getContentDisplay()));
            if (result == CommandResult.FAIL)
                message.getChannel().sendMessage("Command failed");
        }
    }

    public boolean isValid(String command) {
        return COMMANDS.containsKey(command);
    }
}
