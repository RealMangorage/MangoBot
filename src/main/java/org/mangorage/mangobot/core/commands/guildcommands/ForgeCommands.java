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

package org.mangorage.mangobot.core.commands.guildcommands;

import org.mangorage.mangobot.commands.PingCommand;
import org.mangorage.mangobot.commands.ReplyCommand;
import org.mangorage.mangobot.core.commands.CommandAlias;
import org.mangorage.mangobot.core.commands.CommandHolder;
import org.mangorage.mangobot.core.commands.CommandRegistry;
import org.mangorage.mangobot.core.commands.GlobalCommands;
import org.mangorage.mangobot.core.commands.RegistryObject;

public class ForgeCommands {
    public static final CommandRegistry FORGE = CommandRegistry.guild("1129059589325852724");

    public static final RegistryObject<CommandHolder<PingCommand>> PING = FORGE.registerOld("pings", new PingCommand());

    static {
        FORGE.registerOld("paste", new ReplyCommand(
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
        FORGE.registerOld("sl", new ReplyCommand(
                """
                        # Forge Only Supports 1.19.x and 1.20.x
                        The Forge Discord only supports 1.19.x and 1.20.x
                        The Forge Project including everyone in this discord, and the forums, only supports the latest and LTS versions. Currently 1.19.x and 1.20.x.
                        This is due to the Forge team having limited manpower as we are all people working in our free time. We can't possibly support all Minecraft versions that had a Forge build.
                        Some say a version of Minecraft is the best but others say that about a different version.
                        All of the older versions work and there are some other communities that offer support for older versions.
                        """
        ));
        FORGE.registerOld("neoforge", new ReplyCommand(
                """
                        You mentioned NeoForge. NeoForge is a fork of MinecraftForge by many former MC Forge staff and the old discord. We are not affiliated with NeoForge and to get support with NeoForge you should consider joining their discord if you are not already banned. https://discord.neoforged.net/
                                                """
        ));
        FORGE.registerOld("notforge", new ReplyCommand(
                """
                        We only support Minecraft Forge on this discord server, the attached info uses other modloaders. We do not support other modloaders such as Fabric, Quilt, FeatureCreep, LiteLoader, Rift, NeoForge, or any other modloaders mods out of the box. You should contact the developers or the modloader or abstraction layer of your issue.
                        """
        ));
        FORGE.registerOld("java", new ReplyCommand(
                """
                        You can download Java from the Adoptium project: https://adoptium.net/temurin/releases/
                        Select the Version dropdown option for the Java version you wish to download.
                        1.18 and later need Java 17
                        1.17 needs Java 16
                        1.16.5 and older need Java 8
                        """
        ));
        FORGE.registerOld("log", new ReplyCommand(
                """
                        To diagnose your issue we need the game log; please provide the logs/debug.log file, in the minecraft directory, and put it in one of the following sites (preferably the first one):
                                                
                        Here's a list of some paste sites and their size limits:
                            https://gist.github.com/:      [Free] [SignUp] 100MB
                            https://paste.gemwire.uk/:  [Free] 10MB
                            https://paste.ee/:                   [Free] 1MB, [SignUp] 6MB
                            https://pastebin.com/:          [Free] 512KB, [SignUp] [Paid] 10MB
                            https://hastebin.com/:          [Free] 400KB
                            https://gist.github.com/:      [Free] [SignUp] 100MB
                        """
        ));
        FORGE.registerOld("tryitandsee", new ReplyCommand("https://tryitands.ee", false),
                CommandAlias.of("tias")
        );

        //CommandAlias alias = FORGE.registerOld(PING, CommandAlias.of("hello"));
        RegistryObject<CommandAlias> ALIAS_TRICK = FORGE.registerAlias(() -> GlobalCommands.TRICK.get(), CommandAlias.of("trickForge"));
    }

    public static void init() {
    }
}
